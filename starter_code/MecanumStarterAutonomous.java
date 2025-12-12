package org.firstinspires.ftc.teamcode.R2045.starter_code;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name="MecanumAutonomous", group="StarterCode")
public class MecanumStarterAutonomous extends LinearOpMode {

    Servo claw;

    // This is an instant class used to control other actuators.
    // Example:
    public class OpenClaw implements InstantFunction {

        float targetPosition;

        public OpenClaw(float targetPosition) {
            this.targetPosition = targetPosition;
        }

        @Override
        public void run() {
            claw.setPosition(targetPosition);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {

        claw = hardwareMap.get(Servo.class, "claw");

        // Declaration variables
        Pose2d beginPose = new Pose2d(new Vector2d(0, 0), Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        waitForStart();

        // Enter Path Implementation here
        Action path = drive.actionBuilder(beginPose)

                ///////////////////////////////////////////////////////////////////////////////
                ///                         PATH COMMANDS EXAMPLES                          ///
                ///////////////////////////////////////////////////////////////////////////////
        
                // Move forward 10 inches
                .forward(10)

                // Move backward 10 inches
                .back(10)

                // Strafe left 10 inches
                .strafeLeft(10)

                // Strafe right 10 inches
                .strafeRight(10)

                // strafeTo is a shorthand for lineToConstantHeading()
                .strafeTo(new Vector(10, 10))

                // The same.
                .lineTo(new Vector(10, 10))

                // The same as lineTo adn strafeTo
                .lineToConstantHeading(new Pose2d(10, 10, Math.toRadians(90)))

                // Robot moves to the specified coordinates while linearly
                // Interpolating between the start heading and a specified end heading.
                .lineToLinearHeading(new Pose2d(10, 10, Math.toRadians(90)))

                // Robot moves to the specified coordinates while spline
                // interpolating between the start heading and a specified end heading.
                .lineToSplineHeading(new Pose2d(10, 10, Math.toRadians(90)))

                // Robot moves to the specified coordinates in a spline path
                // while following a tangent heading interpolator.
                .splineTo(new Vector2d(10, 10), Math.toRadians(90))

                // Robot moves to the specified coordinates in a spline path
                // while keeping the heading constant.
                // The robot maintains the heading it starts at throughout the trajectory
                .splineToConstantHeading(new Vector2d(10, 10), Math.toRadians(90))

                // Robot moves to the specified coordinates in a spline path
                // while separately linearly interpolating the heading
                //
                // The heading interpolates to the heading specified in `endPose`.
                // Setting `endTangent` affects the shape of the spline path itself.

                // 🚨  Will cause PathContinuityException's!! 🚨
                // Use splineToSplineHeading() if you are chaining these calls
                .splineToLinearHeading(new Pose2d(40, 40, Math.toRadians(90)), Math.toRadians(0))


                // Robot moves to the specified coordinates in a spline path
                // while separately spline interpolating the heading
                //
                // The heading interpolates to the heading specified in `endPose`.
                // Setting `endTangent` affects the shape of the spline path itself.
                .splineToSplineHeading(new Pose2d(40, 40, Math.toRadians(90)), Math.toRadians(0))

                // Execute InstantFunction
                .stopAndAdd(new OpenClaw(0))

                .build();

        Actions.runBlocking(new SequentialAction(path));
    }
}
