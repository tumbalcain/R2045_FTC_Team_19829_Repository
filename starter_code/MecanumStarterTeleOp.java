package org.firstinspires.ftc.teamcode.R2045;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.teamcode.R2045.pipelines.ReusableMecanum;

@TeleOp(name="MecanumTeleOp", group="StarterCode")
public class MecanumStarterTeleOp extends OpMode {

    // Class Initialization

    ReusableMecanum drivePower = new ReusableMecanum();

    double forward, strafe, rotate;

    private IMU imu;

    // Loop Implementation

    @Override
    public void loop() {
        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drivePower.driveFieldRelative(forward, strafe, rotate);
    }
}