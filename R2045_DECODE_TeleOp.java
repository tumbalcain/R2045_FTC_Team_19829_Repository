package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

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

    }

    @Override
    public void loop() {
        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drivePower.driveFieldRelative(forward, strafe, rotate);
    }
}
