package org.firstinspires.ftc.teamcode.code;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.code.pipelines.AprilTagWebcam;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "ClankerTesting", group = "Example")
public class ClankerTesting extends LinearOpMode {

    // Pipelines
    private AprilTagWebcam aprilTagVision = new AprilTagWebcam();

    // Other subsystems
    private DcMotor Shooter = null;

    private DcMotor Intake = null;
    private CRServo Hood = null;
    private CRServo Turret = null;

    private CRServo Stopper = null;

    private DcMotorEx front_right_drive, front_left_drive, back_right_drive, back_left_drive;

    // Drive control variables
    private double forward, strafe, rotate;

    @Override
    public void runOpMode() {


        // Initialize AprilTag vision
        aprilTagVision.init(hardwareMap, telemetry);

        // Initialize other subsystems
        Shooter = hardwareMap.get(DcMotor.class, "Shooter");
        Hood = hardwareMap.get(CRServo.class, "Hood");
        Turret = hardwareMap.get(CRServo.class, "Turret");
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        Stopper = hardwareMap.get(CRServo.class, "Stopper");

        front_right_drive = hardwareMap.get(DcMotorEx.class, "front_right_drive");
        front_left_drive = hardwareMap.get(DcMotorEx.class, "front_left_drive");
        back_right_drive = hardwareMap.get(DcMotorEx.class, "back_right_drive");
        back_left_drive = hardwareMap.get(DcMotorEx.class, "back_left_drive");

        front_left_drive.setDirection(DcMotor.Direction.FORWARD);
        back_left_drive.setDirection(DcMotor.Direction.FORWARD);
        front_right_drive.setDirection(DcMotor.Direction.REVERSE);
        back_right_drive.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "✓ Initialized!");
        telemetry.addData("Mode", "Field-Centric Drive");
        telemetry.addData("Vision", "✓ Camera Always Active");
        telemetry.addData("Camera Stream", "Tap ⋮ > Camera Stream");
        telemetry.addLine();
        telemetry.addData("Reset Heading", "gamepad1.start");
        telemetry.update();

        waitForStart();

        // Reset IMU heading when starting

        while (opModeIsActive()) {

            // === UPDATE VISION (Always Active) ===
            aprilTagVision.update();

            // === DRIVER 1: MECANUM DRIVE (Field-Centric) ===
            double y = -gamepad1.left_stick_y; // Arah maju/mundur
            double x = gamepad1.left_stick_x; // Arah kiri/kanan
            double rx = gamepad1.right_stick_x; // Rotasi

            double frontLeftPower = y + x + rx;
            double frontRightPower = y - x - rx;
            double backLeftPower = y - x + rx;
            double backRightPower = y + x - rx;

            double maxPower = Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
            );

            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            // Atur daya motor
            front_left_drive.setPower(frontLeftPower);
            front_right_drive.setPower(frontRightPower);
            back_left_drive.setPower(backLeftPower);
            back_right_drive.setPower(backRightPower);


            // === TURRET CONTROL ===
            if (gamepad2.x) {
                Turret.setPower(1);
            } else if (gamepad2.b) {
                Turret.setPower(-1);
            } else {
                Turret.setPower(0);
            }

            // === SHOOTER CONTROL ===
            if (gamepad2.y) {
                Shooter.setPower(1);
            } else if (gamepad2.a) {
                Shooter.setPower(-1);
            } else {
                Shooter.setPower(0);
            }

            // === HOOD CONTROL ===
            if (gamepad2.dpad_up) {
                Hood.setPower(1);
            } else if (gamepad2.dpad_down) {
                Hood.setPower(-1);
            } else {
                Hood.setPower(0);
            }

            if (gamepad1.right_bumper) {
                Intake.setPower(1);
            } else if (gamepad1.left_bumper) {
                Intake.setPower(-1);
            } else {
                Intake.setPower(0);
            }

            if (gamepad2.right_bumper ) {
                Stopper.setPower(1);
            } else if (gamepad2.left_bumper) {
                Stopper.setPower(-1);
            } else {
                Stopper.setPower(0);
            }

            // === TELEMETRY ===
            telemetry.addData("Drive Mode", "Field-Centric");
            telemetry.addData("Forward/Strafe/Rotate", "%.2f / %.2f / %.2f", forward, strafe, rotate);
            telemetry.addLine();

            telemetry.addData("Shooter", Shooter.getPower() != 0 ? "ON" : "OFF");
            telemetry.addData("Turret", Turret.getPower() != 0 ? "MOVING" : "STOPPED");
            telemetry.addData("Hood", Hood.getPower() != 0 ? "MOVING" : "STOPPED");
            telemetry.addLine();

            // === VISION TELEMETRY (Always Active) ===
            telemetry.addData("Vision Status", "✓ ACTIVE");
            telemetry.addData("Tags Detected", aprilTagVision.getDetectedTags().size());

            // Show specific tag if detected (example: tag ID 20)
            AprilTagDetection tag20 = aprilTagVision.getTagBySpecificId(20);
            if (tag20 != null) {
                telemetry.addData("Target Tag 20", "✓ FOUND!");
                telemetry.addData("  Distance", "%.1f cm", tag20.ftcPose.range);
                telemetry.addData("  Bearing", "%.1f°", tag20.ftcPose.bearing);
            } else {
                telemetry.addData("Target Tag 20", "Not found");
            }

            // List all detected tags
            if (aprilTagVision.getDetectedTags().size() > 0) {
                telemetry.addLine();
                StringBuilder tagList = new StringBuilder("Visible Tags: ");
                for (AprilTagDetection detection : aprilTagVision.getDetectedTags()) {
                    tagList.append(detection.id).append(" ");
                }
                telemetry.addData("All Tags", tagList.toString());
            }

            telemetry.update();
        }

        // Cleanup
        aprilTagVision.stop();
    }
}
