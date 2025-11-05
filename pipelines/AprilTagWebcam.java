package org.firstinspires.ftc.teamcode.R2045.pipelines;

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

public class AprilTagWebcam {

    // List, VisionPortal, AprilTag, and Telemetry Declaration

    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;

    private List<AprilTagDetection> detectedTags = new ArrayList<>();

    private Telemetry telemetry;

    // AprilTag and VisionPortal Initilization

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

    // Specific AprilTag Id Fetch Function

    public AprilTagDetection getTagBySpecificId(int id) {
        for (AprilTagDetection detection : detectedTags) {
            if (detection.id == id) {
                return detection;
            } // end of conditional
        } // end of for loop

        return null;
    } // end of getTagBySpecificId() function

    // Telemetry Display Function

    public void displayDetectionTelemetry(AprilTagDetection detectedId) {
        if (detectedId == null) {
            return;
        } // end of conditional

        if (detectedId.metadata != null) {
            telemetry.addLine(String.format("\n==== (ID %d) %s", detectedId.id, detectedId.metadata.name));
            telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detectedId.ftcPose.x, detectedId.ftcPose.y, detectedId.ftcPose.z));
            telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detectedId.ftcPose.pitch, detectedId.ftcPose.roll, detectedId.ftcPose.yaw));
            telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detectedId.ftcPose.range, detectedId.ftcPose.bearing, detectedId.ftcPose.elevation));
        } else {
            telemetry.addLine(String.format("\n==== (ID %d) Unknown", detectedId.id));
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
