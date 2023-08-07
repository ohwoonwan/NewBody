package com.example.newbody;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class LandmarkTriple {
    private final PoseLandmark firstLandmark;
    private final PoseLandmark middleLandmark;
    private final PoseLandmark lastLandmark;

    public LandmarkTriple(PoseLandmark firstLandmark, PoseLandmark middleLandmark, PoseLandmark lastLandmark) {
        this.firstLandmark = firstLandmark;
        this.middleLandmark = middleLandmark;
        this.lastLandmark = lastLandmark;
    }

    public PoseLandmark getFirstLandmark() {
        return firstLandmark;
    }

    public PoseLandmark getMiddleLandmark() {
        return middleLandmark;
    }

    public PoseLandmark getLastLandmark() {
        return lastLandmark;
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
}
