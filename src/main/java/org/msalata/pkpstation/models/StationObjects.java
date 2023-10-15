package org.msalata.pkpstation.models;


import java.util.Map;

public class StationObjects {
    private final Map<String, DynamicStationObject> dynamicStationNodeMap;
    private final Map<String, StaticStationObjectNode> staticStationNodeMap;

    public StationObjects(Map<String, DynamicStationObject> dynamicStationNodeMap,
                          Map<String, StaticStationObjectNode> staticStationNodeMap) {
        this.dynamicStationNodeMap = dynamicStationNodeMap;
        this.staticStationNodeMap = staticStationNodeMap;
    }

    public Map<String, DynamicStationObject> getDynamicStationNodeMap() {
        return dynamicStationNodeMap;
    }

    public StationObject getAnyTypeStationObject(String stationObjectId) {
        DynamicStationObject dynamicStationNode = dynamicStationNodeMap.get(stationObjectId);
        StaticStationObjectNode staticStationNode = staticStationNodeMap.get(stationObjectId);
        return dynamicStationNode != null ? dynamicStationNode : staticStationNode;
    }

    public void addStaticStationObject(String id, StaticStationObjectNode staticStationNode) {
        staticStationNodeMap.put(id, staticStationNode);
    }

    public void addDynamicStationObject(String id, DynamicStationObject dynamicStationNode) {
        dynamicStationNodeMap.put(id, dynamicStationNode);
    }
}
