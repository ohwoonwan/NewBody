package com.example.newbody;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class PoseMatcher {

    private static final double OFFSET = 15.0;

    public boolean match(Pose pose, TargetPose targetPose) {
        return extractAndMatch(pose, targetPose);
    }

    public boolean extractAndMatch(Pose pose, TargetPose targetPose) {
        for (TargetShape target : targetPose.getTargets()) {
            LandmarkTriple triple = extractLandmark(pose, target);
            PoseLandmark firstLandmark = triple.getFirstLandmark();
            PoseLandmark middleLandmark = triple.getMiddleLandmark();
            PoseLandmark lastLandmark = triple.getLastLandmark();

            if (landmarkNotFound(firstLandmark, middleLandmark, lastLandmark)) {
                return false;
            }

            double angle = calculateAngle(firstLandmark, middleLandmark, lastLandmark);
            double targetAngle = target.getAngle();

            if (abs(angle - targetAngle) > OFFSET) {
                return false;
            }
        }
        return true;
    }

    public LandmarkTriple extractLandmark(Pose pose, TargetShape target) {
        return new LandmarkTriple(
                extractLandmarkFromType(pose, target.getFirstLandmarkType()),
                extractLandmarkFromType(pose, target.getMiddleLandmarkType()),
                extractLandmarkFromType(pose, target.getLastLandmarkType())
        );
    }

    public PoseLandmark extractLandmarkFromType(Pose pose, int landmarkType) {
        return pose.getPoseLandmark(landmarkType);
    }

    public boolean landmarkNotFound(PoseLandmark firstLandmark, PoseLandmark middleLandmark, PoseLandmark lastLandmark) {
        return firstLandmark == null || middleLandmark == null || lastLandmark == null;
    }

    public double calculateAngle(PoseLandmark firstLandmark, PoseLandmark middleLandmark, PoseLandmark lastLandmark) {
        double angle = toDegrees(
                atan2(
                        lastLandmark.getPosition().y - middleLandmark.getPosition().y,
                        lastLandmark.getPosition().x - middleLandmark.getPosition().x
                ) - atan2(
                        firstLandmark.getPosition().y - middleLandmark.getPosition().y,
                        firstLandmark.getPosition().x - middleLandmark.getPosition().x
                )
        );

        double absoluteAngle = abs(angle);
        if (absoluteAngle > 180) {
            absoluteAngle = 360 - absoluteAngle;
        }
        return absoluteAngle;
    }

    public boolean anglesMatch(double angle, double targetAngle) {
        return angle < targetAngle + OFFSET && angle > targetAngle - OFFSET;
    }
}

