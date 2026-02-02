package org.firstinspires.ftc.teamcode.R2045.DECODE_season;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="R2045_DECODE_TeleOp", group="R2045_DECODE")
public class R2045_DECODE_TeleOp extends OpMode {

    private DcMotorEx intake, shooter, right_lifter, left_lifter;

    private CRServo turret;

    private Servo hood, stopper;

    ReusableMecanum drivePower = new ReusableMecanum();
    double forward, strafe, rotate;

    @Override
    public void init() {

        drivePower.init(hardwareMap);

        // Actuator Configuration and Hardware Mapping

        // Shooter Hardware Mapping
        // Shooter Actuator
        // REV UltraPlanetary Motors
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");

        // Turret Hardware Mapping
        // Turret Servo
        // REV CRServo
        turret = hardwareMap.get(CRServo.class, "turret");

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

    double hoodPosition = 0.42;

    @Override
    public void loop() {
        // Control Variable Declaration

        double right_trigger = -gamepad1.right_trigger;
        double left_trigger = gamepad1.left_trigger;
        double turretPower = gamepad2.left_stick_x;

        // Turret Control

        if (Math.abs(turretPower) > 0.5) {
            turret.setPower(turretPower * 0.4);
        } else {
            turret.setPower(0.0);
        }

        // Hood Control

        if (Math.abs(gamepad2.right_stick_y) > 0.05) {
            hoodPosition += gamepad2.right_stick_y * 0.01;
            hoodPosition = Math.max(0.0, Math.min(1.0, hoodPosition));
        }
        hood.setPosition(hoodPosition);

        // Intake Control

        if (right_trigger > left_trigger) {
            intake.setPower(right_trigger);
        } else if (left_trigger > right_trigger) {
            intake.setPower(left_trigger);
        } else {
            intake.setPower(0.0);
        } // end of conditional

        // Lifter Control

        if (gamepad1.dpad_up) {
            right_lifter.setPower(1.0);
            left_lifter.setPower(1.0);
        } else if (gamepad1.dpad_down) {
            right_lifter.setPower(-1.0);
            left_lifter.setPower(-1.0);
        } else {
            right_lifter.setPower(0.0);
            left_lifter.setPower(0.0);
        } // end of conditional

        // Shooter Control

        if (gamepad2.y) {
            shooter.setPower(1.0);
        } else {
            shooter.setPower(0.0);
        } // end of conditional

        // Stopper Control

        if (gamepad2.a) {
            stopper.setPosition(0.2); // open
        } else if (gamepad2.b) {
            stopper.setPosition(0.7); // closed
        } // end of conditional

        // Drivebase Control

        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drivePower.driveFieldRelative(forward, strafe, rotate);
    } // end of loop() function
} // end of opMode()