package org.firstinspires.ftc.teamcode.R2045;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventLoop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.MecanumDrive;

@Autonomous(name="MecanumAutonomous", group="StarterCode")
public class MecanumStarterAutonomous extends LinearOpMode {
    
    @Override
    public void runOpMode() throws InterruptedException {

        // Declaration variables
        Pose2d beginPose = new Pose2d(new Vector2d(0, 0), Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap);

        waitForStart();

        // Enter Path Implementation here
        Action path = drive.actionBuilder(beginPose)
                .build();

        Actions.runBlocking(new SequentialAction(path));
    }
}
