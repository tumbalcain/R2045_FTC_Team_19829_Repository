package org.firstinspires.ftc.teamcode.R2045.DECODE_season;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.R2045.pipelines.AprilTagWebcam;

@TeleOp(name="R2045_DECODE_TeleOp", group="R2045_DECODE")
public class R2045_DECODE_TeleOp_RED extends OpMode {

    private DcMotorEx intake, shooter, right_lifter, left_lifter;

    private Servo hood, turret, stopper;
    private IMU imu;

    ReusableMecanum drivePower = new ReusableMecanum();
    AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    double forward, strafe, rotate;

    // Shooting Tuning Table

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

    @Override
    public void init() {

        aprilTagWebcam.init(hardwareMap, telemetry);
        drivePower.init(hardwareMap);

        // Actuator Configuration and Hardware Mapping

        // Shooter Hardware Mapping
        // Shooter Actuator
        // REV UltraPlanetary Motors
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");

        // Turret Hardware Mapping
        // Turret Servo
        // REV CRServo
        turret = hardwareMap.get(Servo.class, "turret");

        // Hood Hardware Mapping
        // Hood Servo
        // REV CRServo
        hood = hardwareMap.get(Servo.class, "hood");

        // Stopper Hardware Mapping
        // Stopper Servo
        // REV CRServo
        stopper = hardwareMap.get(Servo.class, "stopper");

        // Intake Hardware Mapping
        // Intake Actuator
        // REV Core Hex Motor
        intake = hardwareMap.get(DcMotorEx.class, "intake");

        // Lifter Hardware Mapping
        // Lifter Actuator
        // REV UltraPlanetary Motors
        right_lifter = hardwareMap.get(DcMotorEx.class, "right_lifter");
        left_lifter = hardwareMap.get(DcMotorEx.class, "left_lifter");
    } // end of init() function

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

    @Override
    public void loop() {
        // Control Variable Declaration

        aprilTagWebcam.update();

        double right_trigger = -gamepad1.right_trigger;
        double left_trigger = gamepad1.left_trigger;

        // Tag ID 20: Blue Alliance
        // Tag ID 24: Red Alliance
        Double bearing = aprilTagWebcam.getBearingToTag(24);
        Double distance = aprilTagWebcam.getDistanceToTag(24);

        // Turret Auto-Aim

        if (bearing != null) {
            double pos = 0.5 + bearing * 0.01;
            turret.setPosition(Math.max(0.0, Math.min(1.0, pos)));
        } // end of conditional

        // Intake Control

        if (right_trigger > left_trigger) {
            intake.setPower(right_trigger);
        } else if (left_trigger > right_trigger) {
            intake.setPower(left_trigger);
        } else {
            intake.setPower(0);
        } // end of conditional

        // Lifter Control

        if (gamepad1.dpad_up) {
            right_lifter.setPower(1);
            left_lifter.setPower(1);
        } else if (gamepad1.dpad_down) {
            right_lifter.setPower(-1);
            left_lifter.setPower(-1);
        } else {
            right_lifter.setPower(0);
            left_lifter.setPower(0);
        } // end of conditional

        // Shooter Control

        if (gamepad1.y && distance != null) {
            double[] shot = getShotConfig(distance);
            hood.setPosition(shot[0]);
            shooter.setPower(shot[1]);
        } else {
            shooter.setPower(0);
        } // end of conditional

        // Drivebase Control

        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drivePower.driveFieldRelative(forward, strafe, rotate);
    } // end of loop() function
} // end of opMode()
