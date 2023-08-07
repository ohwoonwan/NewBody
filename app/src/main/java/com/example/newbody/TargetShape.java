package com.example.newbody;

import java.util.List;
import java.util.Objects;

public class TargetShape {
    private final int firstLandmarkType;
    private final int middleLandmarkType;
    private final int lastLandmarkType;
    private final double angle;

    public TargetShape(int firstLandmarkType, int middleLandmarkType, int lastLandmarkType, double angle) {
        this.firstLandmarkType = firstLandmarkType;
        this.middleLandmarkType = middleLandmarkType;
        this.lastLandmarkType = lastLandmarkType;
        this.angle = angle;
    }

    public int getFirstLandmarkType() {
        return firstLandmarkType;
    }

    public int getMiddleLandmarkType() {
        return middleLandmarkType;
    }

    public int getLastLandmarkType() {
        return lastLandmarkType;
    }

    public double getAngle() {
        return angle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetShape that = (TargetShape) o;
        return firstLandmarkType == that.firstLandmarkType &&
                middleLandmarkType == that.middleLandmarkType &&
                lastLandmarkType == that.lastLandmarkType &&
                Double.compare(that.angle, angle) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstLandmarkType, middleLandmarkType, lastLandmarkType, angle);
    }

    @Override
    public String toString() {
        return "TargetShape{" +
                "firstLandmarkType=" + firstLandmarkType +
                ", middleLandmarkType=" + middleLandmarkType +
                ", lastLandmarkType=" + lastLandmarkType +
                ", angle=" + angle +
                '}';
    }
}