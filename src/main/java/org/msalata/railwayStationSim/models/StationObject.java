package org.msalata.railwayStationSim.models;

import java.util.UUID;

public abstract class StationObject {

    private boolean reversed = false;
    private final double x;
    private final double y;
    private final String id;
    private final double height;
    private final double width;

    protected StationObject(Double x, Double y, boolean reversed, UUID id, double height, double width) {
        this.x = x;
        this.y = y;
        this.reversed = reversed;
        this.id = id.toString();
        this.height = height;
        this.width = width;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public boolean isReversed() {
        return reversed;
    }
    public String getId() {
        return id;
    }
    public double getHeight() {
        return height;
    }
    public double getWidth() {
        return width;
    }
}
