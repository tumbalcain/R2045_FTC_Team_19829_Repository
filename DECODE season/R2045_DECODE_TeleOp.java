package org.firstinspires.ftc.teamcode.R2045;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.teamcode.R2045.pipelines.ReusableMecanum;

//test

@TeleOp(name="R2045_DECODE_TeleOp", group="R2045_DECODE")
public class R2045_DECODE_TeleOp extends OpMode {

    private DcMotorEx intake, right_shooter, left_shooter, right_lifter, left_lifter;

    private IMU imu;

    ReusableMecanum drivePower = new ReusableMecanum();
    double forward, strafe, rotate;

    @Override
    public void init() {
        drivePower.init(hardwareMap);

        // Actuator Configuration and Hardware Mapping

        // Shooter Hardware Mapping
        // Shooter Actuator
        // REV UltraPlanetary Motors
        right_shooter = hardwareMap.get(DcMotorEx.class, "right_shooter");
        left_shooter = hardwareMap.get(DcMotorEx.class, "left_shooter");

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

    @Override
    public void loop() {
        // Control Variable Declaration

        double right_trigger = -gamepad1.right_trigger;
        double left_trigger = gamepad1.left_trigger;

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

        if (gamepad1.y) {
            right_shooter.setPower(1);
            left_shooter.setPower(1);
        } else if (gamepad1.a) {
            right_shooter.setPower(-1);
            left_shooter.setPower(-1);
        } else {
            right_shooter.setPower(0);
            left_shooter.setPower(0);
        }

        // Drivebase Control

        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drivePower.driveFieldRelative(forward, strafe, rotate);
    } // end of loop() function
} // end of opMode()
