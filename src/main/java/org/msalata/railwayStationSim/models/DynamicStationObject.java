package org.msalata.railwayStationSim.models;

import java.util.UUID;

public class DynamicStationObject extends StationObject {
    public DynamicStationObject(Double x, Double y, boolean reversed, UUID id, double height, double width) {
        super(x, y, reversed, id, height, width);
    }
}
