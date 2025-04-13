package org.acme;

public record Position(double latDegrees, double lonDegrees, double headingDegrees,double speedKnots) {
    public Position {
        if (latDegrees < -90.0 || latDegrees > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (lonDegrees < -180.0 || lonDegrees > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
        if (headingDegrees < 0.0 || headingDegrees >= 360.0) {
            throw new IllegalArgumentException("Heading must be between 0 and 360 degrees");
        }
   
    }

}
