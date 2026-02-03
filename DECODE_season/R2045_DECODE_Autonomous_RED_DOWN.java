
package org.firstinspires.ftc.teamcode.R2045.DECODE_season;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.R2045.pipelines.TabbyTag;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Config
@Autonomous(name="R2045_DECODE_Autonomous_RED_DOWN", group="R2045_DECODE")
public class R2045_DECODE_Autonomous_RED_DOWN extends LinearOpMode {

    TabbyTag tabbyTag = new TabbyTag();

    // Mechanism Instantiation

    // Turret Class
    public class Turret {
        private final CRServo turret;

        static final double KP = 0.1;
        static final double DEADBAND = 1.5;

        public Turret(HardwareMap hardwareMap) {
            turret = hardwareMap.get(CRServo.class, "turret");
        } // end of hardwareMap

        // Align To Tag Function

        public class AlignToTag implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                double bearing = tabbyTag.getBearingToTag(24);

                if (Math.abs(bearing) < DEADBAND) {
                    turret.setPower(0);
                    return true;
                } // end of conditional

                double power = Math.max(-0.3, Math.min(0.3, bearing * KP));
                turret.setPower(power);
                return false;
            } // end of boolean run
        } // end of AlignToTag

        public Action alignToTag() {
            return new AlignToTag();
        } // end of alignToTag function
    } // end of class

    // Hood Class

    public static class Hood {
        private final Servo hood;

        // Tune this
        static final double HOOD_SHOT = 0.42;

        public Hood(HardwareMap hardwareMap) {
            hood = hardwareMap.get(Servo.class, "hood");
        } // end of hardwareMap

        // Angle Configuration Function

        public class AngleConfig implements Action {
            private final double position;
            private boolean done = false;

            public AngleConfig(double position) {
                this.position = position;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!done) {
                    hood.setPosition(position);
                    done = true;
                } // end of conditional
                return true;
            } // end of boolean run
        } // end of AngleConfig action

        public Action angleConfig() {
            return new AngleConfig(HOOD_SHOT);
        } // end of shootBall function
    } // end of class

    // Shooter Class
    public static class Shooter {
        private final DcMotorEx shooter;

        public Shooter(HardwareMap hardwareMap) {
            shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        } // end of hardwareMap

        // Shooting Function

        public class ShootBall implements Action {

            private final ElapsedTime timer = new ElapsedTime();
            private boolean started = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!started) {
                    timer.reset();
                    shooter.setPower(1.0);
                    started = true;
                } // end of conditional

                if (timer.milliseconds() >= 5000) {
                    shooter.setPower(0.0);
                    return true;
                } // end of conditional
                return false;
            } // end of boolean
        } // end of ShootBall

        public Action shootBall() {
            return new ShootBall();
        } // end of shootBall function
    } // end of class

    // Intake Class
    public static class Intake {
        private final DcMotorEx intake;

        public Intake(HardwareMap hardwareMap) {
            intake = hardwareMap.get(DcMotorEx.class, "intake");
        } // end of hardwareMap

        // Intake Action

        public Action intakeOn() {
            return packet -> {
                intake.setPower(1.0);
                return true;
            };
        }
    } // end of class

    // Stopper Class
    public static class Stopper {
        private final Servo stopper;

        static final double CLOSED = 0.7;
        static final double OPEN = 0.2;

        public Stopper(HardwareMap hardwareMap) {
            stopper = hardwareMap.get(Servo.class, "stopper");
        } // end of hardwareMap

        // Close Stopper Action

        public Action close() {
            return packet -> {
                stopper.setPosition(CLOSED);
                return true;
            }; // end of return
        } // end of Action

        public Action open() {
            return packet -> {
                stopper.setPosition(OPEN);
                return true;
            }; // end of return
        } // end of Action
    } // end of class

    // Autonomous OpMode

    @Override
    public void runOpMode() throws InterruptedException {

        tabbyTag.init(hardwareMap, telemetry);
        Shooter shooter = new Shooter(hardwareMap);
        Turret turret = new Turret(hardwareMap);
        Intake intake = new Intake(hardwareMap);
        Stopper stopper = new Stopper(hardwareMap);
        Hood hood = new Hood(hardwareMap);

        // Declaration variables
        Pose2d beginPose = new Pose2d(11.57, -62.11, Math.toRadians(90.00));
        Pose2d lz = new Pose2d(-0.26, 10.26, Math.toRadians(90.00));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        waitForStart();

        // LZ = Launch Zone
        // AIS = Artifact Intake Sweep

        // Enter Path Implementation here
        Action pathSPOne = drive.actionBuilder(beginPose)
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Spawn to LZ
                .build();
        // end of pathSPOne

        Action pathSPTwo = drive.actionBuilder(lz)
                .splineTo(new Vector2d(31.54, 12.37), Math.toRadians(0.00)) // LZ to Spike Mark 3
                .splineTo(new Vector2d(50.08, 12.37), Math.toRadians(-0.20)) // Spike Mark 3 AIS
                .splineTo(new Vector2d(37.10, 0.96), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .build();
        // end of pathSPTwo

        Action pathSPThree = drive.actionBuilder(lz)
                .splineTo(new Vector2d(31.54, -12.63), Math.toRadians(0.00)) // LZ to Spike Mark 2
                .splineTo(new Vector2d(50.08, -12.63), Math.toRadians(0.00)) // Spike Mark 2 AIS
                .splineTo(new Vector2d(37.10, -20.08), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .build();
        // end of pathSPThree

        Action pathSPFour = drive.actionBuilder(lz)
                .splineTo(new Vector2d(31.54, -36.31), Math.toRadians(0.00)) // LZ to Spike Mark 1
                .splineTo(new Vector2d(49.21, -36.31), Math.toRadians(-0.51)) // Spike Mark 1 AIS
                .splineTo(new Vector2d(37.10, -48.15), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .build();
        // end of pathSPFour

        // Shooter Parallel Action

        Action shoot = new SequentialAction(
                stopper.open(),
                shooter.shootBall(),
                stopper.close()
        );

        Actions.runBlocking(new ParallelAction(
                intake.intakeOn(),
                new SequentialAction(
                        stopper.close(),
                        hood.angleConfig(),
                        pathSPOne,
                        turret.alignToTag(),
                        shoot,

                        pathSPTwo,
                        turret.alignToTag(),
                        shoot,

                        pathSPThree,
                        turret.alignToTag(),
                        shoot,

                        pathSPFour,
                        turret.alignToTag(),
                        shoot
                ) // end of SequentialAction
        ));
        // end of runBlocking() function
    } // end of runOpMode() function
} // end of loop() function
