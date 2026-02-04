package org.firstinspires.ftc.teamcode.code.pipelines;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * PID Controller for Shooter Motor
 * Maintains constant RPM even under load
 *
 * REQUIREMENTS:
 * - Your shooter motor MUST have an encoder connected
 * - Use DcMotorEx instead of DcMotor for velocity control
 *
 * USAGE:
 * ShooterPID shooterPID = new ShooterPID();
 * shooterPID.init(hardwareMap.get(DcMotorEx.class, "Shooter"));
 *
 * In loop:
 * shooterPID.setTargetRPM(3000); // Set desired RPM
 * shooterPID.update();
 *
 * To stop:
 * shooterPID.stop();
 */
public class ShooterPID {

    // PID Constants - TUNE THESE VALUES
    private static final double KP = 0.0001;  // Proportional gain
    private static final double KI = 0.00001; // Integral gain
    private static final double KD = 0.00001; // Derivative gain
    private static final double KF = 0.00015; // Feedforward gain (very important for motors!)

    // Motor limits
    private static final double MAX_POWER = 1.0;
    private static final double MIN_POWER = 0.0;

    // Preset RPM values - Set to high values for maximum power
    // HIGH_SPEED_RPM is set near free speed = ~1.0 power
    public static final double HIGH_SPEED_RPM = 6000;  // Full power / free speed
    public static final double MED_SPEED_RPM = 4500;   // ~0.75 power
    public static final double LOW_SPEED_RPM = 3200;   // ~0.5 power

    private DcMotorEx shooterMotor;
    private ElapsedTime timer;

    // PID variables
    private double targetRPM;
    private double lastError;
    private double integralSum;
    private double lastTime;

    // Status
    private boolean isRunning;
    private double currentRPM;
    private double currentPower;

    public ShooterPID() {
        this.targetRPM = 0;
        this.lastError = 0;
        this.integralSum = 0;
        this.isRunning = false;
        this.timer = new ElapsedTime();
        this.currentRPM = 0;
        this.currentPower = 0;
    }

    /**
     * Initialize the shooter motor
     * IMPORTANT: Motor MUST have encoder!
     */
    public void init(DcMotorEx motor) {
        this.shooterMotor = motor;

        // Set to use encoder for velocity measurements
        shooterMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set zero power behavior
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        timer.reset();
        lastTime = timer.seconds();
    }

    /**
     * Set target RPM for the shooter
     */
    public void setTargetRPM(double rpm) {
        this.targetRPM = rpm;
        this.isRunning = (rpm > 0);

        // Reset integral when changing target significantly
        if (Math.abs(rpm - targetRPM) > 500) {
            integralSum = 0;
        }
    }

    /**
     * Preset: Set to high speed
     */
    public void setHighSpeed() {
        setTargetRPM(HIGH_SPEED_RPM);
    }

    /**
     * Preset: Set to medium speed
     */
    public void setMediumSpeed() {
        setTargetRPM(MED_SPEED_RPM);
    }

    /**
     * Preset: Set to low speed
     */
    public void setLowSpeed() {
        setTargetRPM(LOW_SPEED_RPM);
    }

    /**
     * Main update loop - CALL THIS EVERY CYCLE
     */
    public void update() {
        if (!isRunning) {
            shooterMotor.setPower(0);
            currentPower = 0;
            integralSum = 0;
            lastError = 0;
            return;
        }

        // Get current velocity in ticks per second, convert to RPM
        double velocity = shooterMotor.getVelocity(); // ticks per second
        currentRPM = velocityToRPM(velocity);

        // Calculate time delta
        double currentTime = timer.seconds();
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;

        // PID calculations
        double error = targetRPM - currentRPM;

        // Proportional
        double P = KP * error;

        // Integral (with anti-windup)
        integralSum += error * deltaTime;
        integralSum = clamp(integralSum, -1000, 1000); // Prevent integral windup
        double I = KI * integralSum;

        // Derivative
        double derivative = (error - lastError) / deltaTime;
        double D = KD * derivative;

        // Feedforward (very important for motor control!)
        double F = KF * targetRPM;

        // Calculate total power
        double power = P + I + D + F;
        power = clamp(power, MIN_POWER, MAX_POWER);

        // Set motor power
        shooterMotor.setPower(power);
        currentPower = power;

        // Update for next cycle
        lastError = error;
    }

    /**
     * Stop the shooter
     */
    public void stop() {
        setTargetRPM(0);
        shooterMotor.setPower(0);
        currentPower = 0;
        integralSum = 0;
        lastError = 0;
    }

    /**
     * Check if shooter is at target speed (within tolerance)
     */
    public boolean isAtSpeed() {
        return isAtSpeed(50); // Default tolerance: 50 RPM
    }

    /**
     * Check if shooter is at target speed with custom tolerance
     */
    public boolean isAtSpeed(double toleranceRPM) {
        if (!isRunning) return false;
        return Math.abs(currentRPM - targetRPM) < toleranceRPM;
    }

    /**
     * Get current RPM
     */
    public double getCurrentRPM() {
        return currentRPM;
    }

    /**
     * Get target RPM
     */
    public double getTargetRPM() {
        return targetRPM;
    }

    /**
     * Get current motor power (0.0 to 1.0)
     */
    public double getCurrentPower() {
        return currentPower;
    }

    /**
     * Get error (target - current)
     */
    public double getError() {
        return targetRPM - currentRPM;
    }

    /**
     * Check if shooter is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Convert motor velocity (ticks/sec) to RPM
     * You may need to adjust the TICKS_PER_REVOLUTION based on your motor
     */
    private double velocityToRPM(double ticksPerSecond) {
        // Most common FTC motors use 28 ticks per revolution (or multiples)
        // Adjust this value based on your motor specs:
        // - NeveRest 20: 537.6 ticks/rev
        // - NeveRest 40: 1120 ticks/rev
        // - NeveRest 60: 1680 ticks/rev
        // - REV HD Hex: 28 ticks/rev
        final double TICKS_PER_REVOLUTION = 28.0; // CHANGE THIS FOR YOUR MOTOR

        double revolutionsPerSecond = ticksPerSecond / TICKS_PER_REVOLUTION;
        return revolutionsPerSecond * 60.0; // Convert to RPM
    }

    /**
     * Utility: Clamp value between min and max
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
