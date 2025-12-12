package org.firstinspires.ftc.teamcode.R2045.starter_code;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

// GUIDE TO NEW PROGRAMMERS ON HOW TO USE THIS PIPELINE IN OPMODES

// STEP 1: IMPORT THIS PIPELINE USING THE CODE BELOW
// import org.firstinspires.ftc.teamcode.R2045.pipelines.ReusableMecanum;

// STEP 2: INITIALIZE THE PIPELINE IN THE OPMODE
// DO NOT ADD THE CODE INSIDE THE INIT() FUNCTION
// ReusableMecanum drivePower = new ReusableMecanum();
// double forward, strafe, rotate;

// STEP 3: INITIALIZE THE DRIVEBASE CONTROL IN THE LOOP() FUNCTION
// EXAMPLE CODE:
// forward = gamepad1.left_stick_y;
// strafe = gamepad1.left_stick_x;
// rotate = gamepad1.right_stick_x;

// STEP 4: IMPLEMENT THE driveFieldRelative() FUNCTION
// drivePower.driveFieldRelative(forward, strafe, rotate);

public final class ReusableMecanum {

    private DcMotorEx front_right_drive, front_left_drive, back_right_drive, back_left_drive;

    private IMU imu;

    public void init(HardwareMap hardwareMap) {

        // Actuator Configuration and Hardware Mapping

        // Drivebase Hardware Mapping
        // Drivebase Actuator
        // REV UltraPlanetary Motors
        front_right_drive = BlocksOpModeCompanion.hardwareMap.get(DcMotorEx.class, "front_right_drive");
        front_left_drive = BlocksOpModeCompanion.hardwareMap.get(DcMotorEx.class, "front_left_drive");
        back_right_drive = BlocksOpModeCompanion.hardwareMap.get(DcMotorEx.class, "back_right_drive");
        back_left_drive = BlocksOpModeCompanion.hardwareMap.get(DcMotorEx.class, "back_left_drive");

        // Direction Configuration

        // NOTE TO PROGRAMMERS: WE USE MECANUM WHEEL DRIVE.
        // SECOND NOTE TO PROGRAMMERS: PLEASE READ THE TEXT ABOVE.

        front_right_drive.setDirection(DcMotorEx.Direction.FORWARD);
        front_left_drive.setDirection(DcMotorEx.Direction.REVERSE);
        back_right_drive.setDirection(DcMotorEx.Direction.FORWARD);
        back_left_drive.setDirection(DcMotorEx.Direction.REVERSE);

        // Encoder Configuration

        front_right_drive.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        front_left_drive.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        back_right_drive.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        back_left_drive.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        // IMU Configuration

        imu = BlocksOpModeCompanion.hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        );

        imu.initialize(new IMU.Parameters(RevOrientation));
    }

    // Drive Power Function

    public void drivePower(double forward, double strafe, double rotate) {
        double front_left_power = forward + strafe + rotate;
        double front_right_power = forward - strafe - rotate;
        double back_right_power = forward + strafe - rotate;
        double back_left_power = forward - strafe + rotate;

        double maxPower = 1.0;

        // NOTE TO PROGRAMMERS: MAXSPEED IS OPTIONAL.
        // DO NOT TOUCH WITHOUT LEAD PERMISSION.
        double maxSpeed = 1.0;

        maxPower = Math.max(maxPower, Math.abs(front_left_power));
        maxPower = Math.max(maxPower, Math.abs(front_right_power));
        maxPower = Math.max(maxPower, Math.abs(back_left_power));
        maxPower = Math.max(maxPower, Math.abs(back_right_power));

        front_right_drive.setPower(maxSpeed * (front_right_power / maxPower));
        front_left_drive.setPower(maxSpeed * (front_left_power / maxPower));
        back_right_drive.setPower(maxSpeed * (back_right_power / maxPower));
        back_left_drive.setPower(maxSpeed * (back_left_power / maxPower));
    }

    // Field Relative Drivebase System

    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double rotation = Math.atan2(forward, strafe);
        double distance = Math.hypot(strafe, forward);

        rotation = AngleUnit.normalizeRadians(
                rotation - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS)
        );

        double newForward = distance * Math.sin(rotation);
        double newStrafe = distance * Math.cos(rotation);

        this.drivePower(newForward, newStrafe, rotate);
    }
}
