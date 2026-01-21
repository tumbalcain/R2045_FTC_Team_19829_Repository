package org.firstinspires.ftc.teamcode.R2045.DECODE_season;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.R2045.pipelines.AprilTagWebcam;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Config
@Autonomous(name="R2045_DECODE_Autonomous_RED_DOWN", group="R2045_DECODE")
public class R2045_DECODE_Autonomous_RED_DOWN extends LinearOpMode {

    AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();

    // Mechanism Instantiation

    // Turret Class
    public class Turret {
        private final Servo turret;
        private final Servo hood;

        public Turret(HardwareMap hardwareMap) {
            turret = hardwareMap.get(Servo.class, "turret");
            hood = hardwareMap.get(Servo.class, "hood");
        } // end of hardwareMap

        // AutoAim Action

        public class AutoAim implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                Double bearing = aprilTagWebcam.getBearingToTag(24);
                Double distance = aprilTagWebcam.getDistanceToTag(24);

                if (bearing != null) {
                    double pos = 0.5 + bearing * 0.01;
                    turret.setPosition(Math.max(0.0, Math.min(1.0, pos)));
                } // end of conditional

                if (distance != null) {
                    double[] shot = getShotConfig(distance);
                    hood.setPosition(shot[0]);
                } // end of conditional
                return false;
            } // end of boolean run
        } // end of AutoAim Action

        public Action autoAim() {
            return new AutoAim();
        } // end of autoAim() function
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

                if (timer.milliseconds() >= 7000) {
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

        public class IntakeConveyor implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                intake.setPower(1.0);
                return false;
            } // end of boolean run
        } // end of IntakeConveyor action

        public Action intakeRun() {
            return new IntakeConveyor();
        } // end of intakeRun function
    } // end of class

    // Stopper Class
    public class Stopper {
        private Servo stopper;

        public Stopper(HardwareMap hardwareMap) {
            stopper = hardwareMap.get(Servo.class, "stopper");
        } // end of hardwareMap

        // Close Stopper Action

        public class StopperClose implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                stopper.setPosition(0.0);
                return true;
            } // end of boolean run
        } // end of StopperClose action

        public Action stopperClose() {
            return new StopperClose();
        }

        // Shooting Sequence Stopper Action

        public class ShootingSequence implements Action {
            private final ElapsedTime timer = new ElapsedTime();
            private boolean started = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!started) {
                    timer.reset();
                    stopper.setPosition(0.0);
                    started = true;
                } // end of conditional

                if (timer.milliseconds() > 2000) {
                    stopper.setPosition(1.0);
                }

                if (timer.milliseconds() >= 7000) {
                    stopper.setPosition(0.0);
                    return true;
                } // end of conditional
                return false;
            } // end of boolean run
        } // end of ShootingSequence action

        public Action shootingSequence() {
            return new ShootingSequence();
        }

    } // end of class

    // Shooting Calibration Table

    double[][] shotTable = {
            // distance(cm), hoodPos, shooterPower
            // NOTE TO PROGRAMMERS & MECHANIC:
            // AS OF 19/01/26, THIS DATA IS TEMPORARY AND SERVE AS A DUMMY.
            // WE NEED SHOOTER & HOOD TESTING AND CALIBRATION.
            {10,  0.15, 0.65},
            {20,  0.22, 0.72},
            {30, 0.30, 0.80},
            {40, 0.38, 0.90}
    }; // end of shotTable

    // Interpolation Function
    // a.k.a gay math

    private double lerp (double a, double b, double t) {
        return a + (b - a) * t;
    } // end of lerp

    private double[] getShotConfig(double distance) {
        for (int i = 0; i < shotTable.length - 1; i++) {
            double[] A = shotTable[i];
            double[] B = shotTable[i + 1];

            if (distance >= A[0] && distance < B[0]) {
                double t = (distance - A[0]) / (B[0] - A[0]);
                return new double[] {
                        lerp(A[1], B[1], t),
                        lerp(A[2], B[2], t)
                }; // end of return
            } // end of conditional
        } // end of for loop
        return new double[] {shotTable[shotTable.length - 1][1], shotTable[shotTable.length - 1][2]};
    } // end of double

    // Autonomous OpMode

    @Override
    public void runOpMode() throws InterruptedException {

        aprilTagWebcam.init(hardwareMap, telemetry);
        Shooter shooter = new Shooter(hardwareMap);
        Turret turret = new Turret(hardwareMap);
        Intake intake = new Intake(hardwareMap);
        Stopper stopper = new Stopper(hardwareMap);

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

        Action shoot = new ParallelAction(
                shooter.shootBall(),
                stopper.shootingSequence()
        );

        Actions.runBlocking(new ParallelAction(
                turret.autoAim(),
                intake.intakeRun(),
                new SequentialAction(
                    pathSPOne,
                    shoot,

                    pathSPTwo,
                    shoot,

                    pathSPThree,
                    shoot,

                    pathSPFour,
                    shoot
                ) // end of SequentialAction
        ));
        // end of runBlocking() function
    } // end of runOpMode() function
} // end of loop() function

