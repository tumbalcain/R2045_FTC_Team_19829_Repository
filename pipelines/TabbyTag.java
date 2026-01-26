package org.firstinspires.ftc.teamcode.R2045.pipelines;

import android.annotation.SuppressLint;
import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

// GUIDE TO NEW PROGRAMMERS ON HOW TO USE THIS PIPELINE IN OPMODES

// STEP 1: IMPORT THIS PIPELINE USING THE CODE BELOW
// import org.firstinspires.ftc.teamcode.R2045.pipelines.TabbyTag;
// or
// import org.firstinspires.ftc.teamcode.TabbyTag;

// STEP 2: INITIALIZE THE PIPELINE IN THE OPMODE
// DO NOT ADD THE CODE INSIDE THE INIT() FUNCTION
// TabbyTag tabbyTag = new TabbyTag();

// STEP 3: ADD THE CODE BELOW INSIDE THE INIT() FUNCTION
// tabbyTag.init(hardwareMap, telemetry);

// STEP 4: ADD THE CODE BELOW INSIDE THE LOOP() FUNCTION
// tabbyTag.update();

// STEP 5: ADD THE CODE BELOW INSIDE THE STOP() FUNCTION
// OR BELOW THE while(opModeIsActive()) LOOP
// tabbyTag.stop();


public final class TabbyTag {

    // List, VisionPortal, AprilTag, and Telemetry Declaration

    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;

    private List<AprilTagDetection> detectedTags = new ArrayList<>();

    private Telemetry telemetry;

    // AprilTag and VisionPortal Initialization

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        aprilTagProcessor = new AprilTagProcessor.Builder()
            .setDrawTagID(true)
            .setDrawTagOutline(true)
            .setDrawAxes(true)
            .setDrawCubeProjection(true)
            .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
            .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();

        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.setCameraResolution(new Size(640, 480));
        builder.addProcessor(aprilTagProcessor);

        visionPortal = builder.build();
    } // end of init() function

    // Update Function

    public void update() {
        detectedTags = aprilTagProcessor.getDetections();
    } // end of update() function

    // List of Detected AprilTags

    public List<AprilTagDetection> getDetectedTags() {
        return detectedTags;
    } // end of getDetectedTag() List

    // AprilTag Id Fetch Function

    public AprilTagDetection getTagById(int id) {
        for (AprilTagDetection detection : detectedTags) {
            if (detection.id == id) {
                return detection;
            } // end of conditional
        } // end of for loop

        return null;
    } // end of getTagById() function

    // AprilTag Id Distance Fetch Function

    public double getDistanceToTag(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.z : -1;
    } // end of getDistanceToTag() function

    // AprilTag Id Bearing Fetch Function

    public double getBearingToTag(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.bearing : -1;
    } // end of getBearingToTag() function

    // AprilTag Id Offset Fetch Function

    public double getOffsetToTag(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.x : -1;
    } // end of getOffsetToTag() function

    // AprilTag Id Yaw Fetch Function

    public double getYawToTag(int id) {
        AprilTagDetection tag = getTagById(id);
        return (tag != null) ? tag.ftcPose.yaw : -1;
    } // end of getYawToTag() function

    // Check if an AprilTag Id exists

    public boolean hasTag(int id) {
        return getTagById(id) != null;
    } // end of boolean

    // Telemetry Display Function

    @SuppressLint("DefaultLocale")
    public void displayDetectionTelemetry(AprilTagDetection detectedId) {
        if (detectedId == null) {
            return;
        } // end of conditional

        if (detectedId.metadata != null) {
            telemetry.addLine(String.format("\n==== (APRILTAG ID %d) x  ", detectedId.id, detectedId.metadata.name));
            telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (cm)", detectedId.ftcPose.x, detectedId.ftcPose.y, detectedId.ftcPose.z));
            telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detectedId.ftcPose.pitch, detectedId.ftcPose.roll, detectedId.ftcPose.yaw));
            telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (cm, deg, deg)", detectedId.ftcPose.range, detectedId.ftcPose.bearing, detectedId.ftcPose.elevation));
        } else {
            telemetry.addLine(String.format("\n==== (APRILTAG ID %d) Unknown", detectedId.id));
            telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detectedId.center.x, detectedId.center.y));
        } // end of conditional
    } // end of displayDetectionTelemetry() function

    // VisionPortal Stop Function

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        } // end of conditional
    } // end of stop() function
}
