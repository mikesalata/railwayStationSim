package org.msalata.railwayStationSim.models;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.msalata.railwayStationSim.Constants;

import java.util.UUID;

public class StaticStationObjectNode extends StationObject {

    public enum STATIC_OBJECT_TYPE {
        CITY,
        INTERCITY,
        INFO
    }
    private final Node node;
    private final STATIC_OBJECT_TYPE stationObjectType;

    public StaticStationObjectNode(Node node, UUID id, boolean reversed, double height, double width, STATIC_OBJECT_TYPE stationObjectType) {
        super(node.getTranslateX(), node.getTranslateY(), reversed, id, height, width);
        this.node = node;
        this.stationObjectType = stationObjectType;
    }

    public void startBreak() {
        switch (stationObjectType) {
            case CITY -> ((ImageView) this.node).setImage(Constants.CITY_OFFICES_CLOSED_IMAGE);
            case INTERCITY -> ((ImageView) this.node).setImage(Constants.INTERCITY_OFFICES_CLOSED_IMAGE);
            case INFO -> ((ImageView) this.node).setImage(Constants.INFO_OFFICES_CLOSED_IMAGE);
        }
    }
    public void finishBreak() {
        switch (stationObjectType) {
            case CITY -> ((ImageView) this.node).setImage(Constants.CITY_OFFICES_IMAGE);
            case INTERCITY -> ((ImageView) this.node).setImage(Constants.INTERCITY_OFFICES_IMAGE);
            case INFO -> ((ImageView) this.node).setImage(Constants.INFO_OFFICES_IMAGE);
        }
    }
}
