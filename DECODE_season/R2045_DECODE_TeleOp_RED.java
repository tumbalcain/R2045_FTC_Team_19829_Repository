package org.firstinspires.ftc.teamcode.R2045.DECODE_season;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.R2045.pipelines.TabbyTag;

@TeleOp(name="R2045_DECODE_TeleOp", group="R2045_DECODE")
public class R2045_DECODE_TeleOp_RED extends OpMode {

    private DcMotorEx intake, shooter, right_lifter, left_lifter;

    private Servo hood, turret, stopper;

    ReusableMecanum drivePower = new ReusableMecanum();
    TabbyTag tabbyTag = new TabbyTag();
    double forward, strafe, rotate;

    // Shooting Tuning Table

    double[][] shotTable = {
            // distance(cm), hoodPos
            // NOTE TO PROGRAMMERS & MECHANIC:
            // AS OF 19/01/26, THIS DATA IS TEMPORARY AND SERVE AS A DUMMY.
            // WE NEED SHOOTER & HOOD TESTING AND CALIBRATION.
            {10,  0.15},
            {20,  0.22},
            {30, 0.30},
            {40, 0.38}
    }; // end of shotTable

    @Override
    public void init() {

        tabbyTag.init(hardwareMap, telemetry);
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
    // stinky math

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
                        lerp(A[1], B[1], t)
                }; // end of return
            } // end of conditional
        } // end of for loop
        return new double[] {shotTable[shotTable.length - 1][1], shotTable[shotTable.length - 1][2]};
    } // end of double


    // Turret Control Constants

    final double TURRET_CENTER = 0.5;
    final double TURRET_KP = 0.008; // adjust this
    final double TURRET_DEADBAND = 1.5;

    boolean useAutoAim = true;
    boolean lastRB = false;
    double turretPos = TURRET_CENTER;

    @Override
    public void loop() {
        // Control Variable Declaration

        boolean rb = gamepad2.right_bumper;

        tabbyTag.update();

        // Auto Aim Toggle
        if (rb && !lastRB) {
            useAutoAim = !useAutoAim;
        } // end of conditional
        lastRB = rb;

        double right_trigger = -gamepad1.right_trigger;
        double left_trigger = gamepad1.left_trigger;
        // Tag ID 20: Blue Alliance
        // Tag ID 24: Red Alliance
        Double bearing = tabbyTag.getBearingToTag(24);
        Double distance = tabbyTag.getDistanceToTag(24);

        // Turret Auto-Aim

        if (bearing != null) {
            if (useAutoAim && Math.abs(bearing) > TURRET_DEADBAND) {
                turretPos += bearing * TURRET_KP;
            } // end of conditional
            if (!useAutoAim && Math.abs(gamepad2.left_stick_x) > 0.1) {
                turretPos += gamepad2.left_stick_x * 0.01;
            } // end of conditional
            turretPos = Math.max(0.0, Math.min(1.0, turretPos));
            turret.setPosition(turretPos);
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
            right_lifter.setPower(1.0);
            left_lifter.setPower(1.0);
        } else if (gamepad1.dpad_down) {
            right_lifter.setPower(-1.0);
            left_lifter.setPower(-1.0);
        } else {
            right_lifter.setPower(0);
            left_lifter.setPower(0);
        } // end of conditional

        // Shooter Control

        if (gamepad2.y && distance != null) {
            double hoodPos = getShotConfig(distance)[0];
            hood.setPosition(hoodPos);
            shooter.setPower(1.0);
        } else {
            shooter.setPower(0.0);
        } // end of conditional

        // Stopper Control

        if (gamepad2.a) {
            stopper.setPosition(1.0);
        } else if (gamepad2.b) {
            stopper.setPosition(0.0);
        } // end of conditional

        // Drivebase Control

        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drivePower.driveFieldRelative(forward, strafe, rotate);
    } // end of loop() function
} // end of opMode()