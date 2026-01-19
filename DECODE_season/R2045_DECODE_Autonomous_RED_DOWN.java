package org.firstinspires.ftc.teamcode.R2045.DECODE_season;

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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.R2045.pipelines.AprilTagWebcam;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name="R2045_DECODE_Autonomous", group="R2045_DECODE")
public class R2045_DECODE_Autonomous_RED_DOWN extends LinearOpMode {

    AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();

    Servo turret = null;
    Servo hood = null;
    DcMotorEx shooter = null;

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

    Action autoAim = packet -> {
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
    }; // end of Action

    public static class ShootBall implements Action {

        private final DcMotorEx shooter;
        private final ElapsedTime timer = new ElapsedTime();
        private boolean started = false;

        public ShootBall(DcMotorEx shooter) {
            this.shooter = shooter;
        } // end of whatever this thing is

        @Override
        public boolean run(TelemetryPacket packet) {
            if (!started) {
                timer.reset();
                shooter.setPower(1.0);
                started = true;
            } // end of conditional

            if (timer.milliseconds() >= 2000) {
                shooter.setPower(0.0);
                return true;
            } // end of conditional

            return false;
        } // end of boolean
    } // end of ShootBall

    @Override
    public void runOpMode() throws InterruptedException {

        aprilTagWebcam.init(hardwareMap, telemetry);

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        turret = hardwareMap.get(Servo.class, "turret");
        hood = hardwareMap.get(Servo.class, "hood");


        // Declaration variables
        Pose2d beginPose = new Pose2d(11.57, -62.11, Math.toRadians(90.00));
        Pose2d endSPOne = new Pose2d(-0.26, 10.26, Math.toRadians(90.00));
        Pose2d endSPTwo = new Pose2d(-0.26, 10.26, Math.toRadians(90.00));
    @Override
    public void runOpMode() throws InterruptedException {

        // Declaration variables
        Pose2d beginPose = new Pose2d(11.57, -62.11, Math.toRadians(90.00));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        waitForStart();

        // LZ = Launch Zone
        // AIS = Artifact Intake Sweep

        // Enter Path Implementation here
        Action pathSPOne = drive.actionBuilder(beginPose)
                // Turret tracking apriltag from the beginning to the end of trajectory
        Action path = drive.actionBuilder(beginPose)
                .splineTo(new Vector2d(28.50, -36.92), Math.toRadians(0.00)) // Spawn to Spike Mark 1
                .splineTo(new Vector2d(53.06, -36.92), Math.toRadians(0.00)) // Spike Mark 1 AIS
                .splineTo(new Vector2d(47.97, -23.42), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .build();
        // end of pathSPOne

        Action pathSPTwo = drive.actionBuilder(endSPOne)
                .splineTo(new Vector2d(28.33, -12.19), Math.toRadians(0.00)) // LZ to Spike Mark 2
                .splineTo(new Vector2d(53.23, -12.19), Math.toRadians(0.00)) // Spike Mark 2 AIS
                .splineTo(new Vector2d(40.43, -20.08), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .build();
        // end of pathSPTwo

        Action pathSPThree = drive.actionBuilder(endSPTwo)
                .splineTo(new Vector2d(29.03, 12.37), Math.toRadians(0.00)) // LZ to Spike Mark 3
                .splineTo(new Vector2d(50.08, 12.37), Math.toRadians(0.00)) // Spike Mark 3 AIS
                .splineTo(new Vector2d(37.10, 0.96), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .build();
        // end of pathSPThree

        Action ShootBall;
        Actions.runBlocking(new ParallelAction(
                autoAim,
                new SequentialAction(
                    pathSPOne,
                    new ShootBall(shooter),
                    pathSPTwo,
                    new ShootBall(shooter),
                    pathSPThree,
                    new ShootBall(shooter)
                ) // end of SequentialAction
        ));
        // end of runBlocking() function
    } // end of runOpMode() function

        Actions.runBlocking(new SequentialAction(path));
    }

} // end of loop() function
