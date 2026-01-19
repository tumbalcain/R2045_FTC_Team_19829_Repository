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
        Pose2d beginPose = new Pose2d(11.57, -62.11, Math.toRadians(90.00));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        waitForStart();

        // LZ = Launch Zone
        // AIS = Artifact Intake Sweep

        // Enter Path Implementation here
        Action path = drive.actionBuilder(beginPose)
                .splineTo(new Vector2d(28.50, -36.92), Math.toRadians(0.00)) // Spawn to Spike Mark 1
                .splineTo(new Vector2d(53.06, -36.92), Math.toRadians(0.00)) // Spike Mark 1 AIS
                .splineTo(new Vector2d(47.97, -23.42), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .splineTo(new Vector2d(28.33, -12.19), Math.toRadians(0.00)) // LZ to Spike Mark 2
                .splineTo(new Vector2d(53.23, -12.19), Math.toRadians(0.00)) // Spike Mark 2 AIS
                .splineTo(new Vector2d(40.43, -20.08), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .splineTo(new Vector2d(29.03, 12.37), Math.toRadians(0.00)) // LZ to Spike Mark 3
                .splineTo(new Vector2d(50.08, 12.37), Math.toRadians(0.00)) // Spike Mark 3 AIS
                .splineTo(new Vector2d(37.10, 0.96), Math.toRadians(180.00)) // Turn to LZ
                .splineTo(new Vector2d(-0.26, 10.26), Math.toRadians(90.00)) // Enter LZ
                .build();

        Actions.runBlocking(new SequentialAction(path));
    }

} // end of loop() function
