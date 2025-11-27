package org.firstinspires.ftc.teamcode.R2045;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

@Autonomous(name="R2045_DECODE_Autonomous", group="R2045_DECODE")
public class R2045_DECODE_Autonomous extends OpMode {

    private DcMotorEx intake, right_shooter, left_shooter, right_lifter, left_lifter;

    private IMU imu;

    @Override

    public void init() {
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

} // end of loop() function
