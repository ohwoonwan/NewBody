package com.example.newbody;

import java.util.List;
import java.util.Objects;

public class TargetPose {
    private final List<TargetShape> targets;

    public TargetPose(List<TargetShape> targets) {
        this.targets = targets;
    }

    public List<TargetShape> getTargets() {
        return targets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetPose that = (TargetPose) o;
        return Objects.equals(targets, that.targets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targets);
    }

    @Override
    public String toString() {
        return "TargetPose{" +
                "targets=" + targets +
                '}';
    }
}
