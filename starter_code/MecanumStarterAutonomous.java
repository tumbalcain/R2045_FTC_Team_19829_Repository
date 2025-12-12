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

                // Execute InstantFunction
                .stopAndAdd(new OpenClaw(0))

                .build();

        Actions.runBlocking(new SequentialAction(path));
    }
}
