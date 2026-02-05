package org.firstinspires.ftc.teamcode.code;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.code.pipelines.ReusableMecanum;
import org.firstinspires.ftc.teamcode.code.pipelines.ShooterPID;

@TeleOp(name = "R2045_DECODE_TeleOp", group = "Competition")
public class R2045_DECODE_TeleOp extends LinearOpMode {

    // Pipelines
    private ReusableMecanum mecanumDrive = new ReusableMecanum();
    private ShooterPID shooterPID = new ShooterPID();

    // Other subsystems
    private DcMotorEx Shooter = null;
    private DcMotor Intake = null;
    private Servo Hood = null;
    private CRServo Turret = null;
    private Servo Stopper = null;

    private DcMotor Lifter1, Lifter2 = null;

    @Override
    public void runOpMode() {

        // Initialize mecanum drive pipeline
        mecanumDrive.init(hardwareMap);

        // Initialize other subsystems
        Shooter = hardwareMap.get(DcMotorEx.class, "Shooter");
        Hood = hardwareMap.get(Servo.class, "Hood");
        Turret = hardwareMap.get(CRServo.class, "Turret");
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        Stopper = hardwareMap.get(Servo.class, "Stopper");

        Lifter1 = hardwareMap.get(DcMotor.class, "Lifter1");
        Lifter2 = hardwareMap.get(DcMotor.class, "Lifter2");

        // Initialize shooter PID
        shooterPID.init(Shooter);

        telemetry.addData("Status", "✓ Initialized!");
        telemetry.addData("Mode", "Robot-Centric Drive");
        telemetry.addData("Shooter PID", "✓ Ready");
        telemetry.addLine();
        telemetry.addLine("=== CONTROLS ===");
        telemetry.addData("Drive", "Left stick: Move | Right stick: Rotate");
        telemetry.addData("Boost", "Right Trigger");
        telemetry.addData("Intake", "RB: In | LB: Out");
        telemetry.addData("Shooter", "Y: High | X: Med | A: Low | B: Stop");
        telemetry.addData("Turret", "D-pad Left/Right");
        telemetry.addData("Hood", "D-pad Up/Down");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // === MECANUM DRIVE (Using ReusableMecanum Pipeline) ===
            mecanumDrive.setBoost(gamepad1.right_trigger > 0.5);  // Enable boost with right trigger

            double y = -gamepad1.left_stick_y;  // Forward/backward
            double x = gamepad1.left_stick_x;   // Strafe left/right
            double rx = gamepad1.right_stick_x; // Rotate

            mecanumDrive.drivePower(y, x, rx);

            Lifter1.setDirection(DcMotor.Direction.FORWARD);
            Lifter2.setDirection(DcMotor.Direction.REVERSE);

            // === INTAKE CONTROL ===
            if (gamepad1.right_bumper) {
                Intake.setPower(-1);
            } else if (gamepad1.left_bumper) {
                Intake.setPower(1);
            } else {
                Intake.setPower(0);
            }

            if (gamepad2.right_trigger > 0) {
                Lifter1.setPower(-1);
                Lifter2.setPower(-1);
            } else if (gamepad2.left_trigger > 0) {
                Lifter1.setPower(1);
                Lifter2.setPower(1);
            } else {
                Lifter1.setPower(0);
                Lifter2.setPower(0);
            }

            // === TURRET CONTROL ===
            // === TURRET CONTROL (GAMEPAD2 LEFT STICK X) ===
            double turretInput = gamepad2.left_stick_x;
            double deadzone = 0.05;

            if (Math.abs(turretInput) > deadzone) {
                Turret.setPower(turretInput);
            } else {
                Turret.setPower(0);
            }

            // === SHOOTER CONTROL (PID) ===
            if (gamepad2.y) {
                shooterPID.setHighSpeed();
            } else if (gamepad2.x) {
                shooterPID.setMediumSpeed();
            } else if (gamepad2.a) {
                shooterPID.setLowSpeed();
            } else if (gamepad2.b) {
                shooterPID.stop();
            }

            // Update PID controller every cycle
            shooterPID.update();

            // === HOOD CONTROL ===
            if (gamepad2.dpad_up) {
                Hood.setPosition(-0.7);
            } else if (gamepad2.dpad_down) {
                Hood.setPosition(0.7);
            }

            // === STOPPER CONTROL ===
            if (gamepad2.right_bumper) {
                Stopper.setPosition(1);
            } else
                Stopper.setPosition(-1);

            // === TELEMETRY ===
            telemetry.addData("Drive Mode", "Robot-Centric");
            telemetry.addData("Speed", mecanumDrive.isBoostActive() ? "🚀 BOOST!" : "Normal (0.5)");
            telemetry.addData("Input", "Y: %.2f | X: %.2f | RX: %.2f", y, x, rx);
            telemetry.addLine();

            // Shooter PID Status
            telemetry.addLine("=== SHOOTER ===");
            telemetry.addData("Status", shooterPID.isRunning() ? "🔥 RUNNING" : "⏸ STOPPED");
            telemetry.addData("Target RPM", "%.0f", shooterPID.getTargetRPM());
            telemetry.addData("Current RPM", "%.0f", shooterPID.getCurrentRPM());
            telemetry.addData("Power", "%.2f", shooterPID.getCurrentPower());
            telemetry.addData("Error", "%.0f RPM", shooterPID.getError());

            if (shooterPID.isRunning()) {
                if (shooterPID.isAtSpeed()) {
                    telemetry.addData("Speed Status", "✓ AT SPEED!");
                } else {
                    telemetry.addData("Speed Status", "⟳ Ramping up...");
                }
            }
            telemetry.addLine();

            // Other systems
            telemetry.addData("Ii ntake", Intake.getPower() > 0 ? "⬆ OUT" : Intake.getPower() < 0 ? "⬇ IN" : "⏸ OFF");
            telemetry.addData("Turret", Turret.getPower() != 0 ? "↔ MOVING" : "⏸ STOPPED");
            telemetry.addData("Hood", "%.2f", Hood.getPosition());
            telemetry.addData("Stopper", "%.2f", Stopper.getPosition());
            telemetry.addLine();

            // === MOTOR ENCODERS ===
            telemetry.addLine("=== DRIVEBASE ENCODERS ===");
            telemetry.addData("LF", "%d", mecanumDrive.getLeftFrontEncoder());
            telemetry.addData("LB", "%d", mecanumDrive.getLeftBackEncoder());
            telemetry.addData("RF", "%d", mecanumDrive.getRightFrontEncoder());
            telemetry.addData("RB", "%d", mecanumDrive.getRightBackEncoder());
            telemetry.addLine();

            telemetry.addLine("=== OTHER ENCODERS ===");
            telemetry.addData("Shooter", "%d", Shooter.getCurrentPosition());
            telemetry.addData("Intake", "%d", Intake.getCurrentPosition());

            telemetry.update();
        }

        // Cleanup
        mecanumDrive.stop();
        shooterPID.stop();
    }
}
