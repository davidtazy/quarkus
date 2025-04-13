package org.acme;

public record PolarPoint(
        double radiusMeter,
        double angleDegree
) {
    public PolarPoint {
        if (radiusMeter < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
    }
}