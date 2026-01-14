package org.firstinspires.ftc.teamcode.R2045.DECODE_season;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name="R2045_DECODE_Autonomous", group="R2045_DECODE")
public class R2045_DECODE_Autonomous_RED_DOWN extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // Declaration variables
        Pose2d beginPose = new Pose2d(12.08, -60.02, Math.toRadians(90.00));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        waitForStart();

        // Enter Path Implementation here
        Action path = drive.actionBuilder(beginPose)
                .splineTo(new Vector2d(53.92, -36.25), Math.toRadians(0.00)) // Spike Mark 1
                .splineTo(new Vector2d(47.63, -27.32), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.10, 7.82), Math.toRadians(90.00)) // Enter LZ
                .splineTo(new Vector2d(53.72, -12.29), Math.toRadians(0.00)) // Spike Mark 2
                .splineTo(new Vector2d(48.24, -6.80), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.10, 7.82), Math.toRadians(90.00)) // Enter LZ
                .splineTo(new Vector2d(53.31, 11.68), Math.toRadians(0.00)) // Spike Mark 3
                .splineTo(new Vector2d(-0.10, 7.82), Math.toRadians(90.00)) // Enter LZ
                .build();

        Actions.runBlocking(new SequentialAction(path));
    }

} // end of loop() function
