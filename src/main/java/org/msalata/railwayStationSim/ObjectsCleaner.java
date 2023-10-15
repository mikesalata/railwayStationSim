package org.msalata.railwayStationSim;

import org.msalata.railwayStationSim.models.StationClientNode;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectsCleaner extends Thread {

    private final Map<String, StationClientNode> clientNodeMap;

    @Override
    public void run() {
        while (true) {
            try {
                // Objects cleaner pause duration
                Thread.sleep(10L * 1000L);
                Set<String> clientsToRemoveSet = clientNodeMap.entrySet().stream()
                        .filter(clientNodeEntry -> clientNodeEntry.getValue().isDead())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
                clientsToRemoveSet.forEach(clientNodeMap::remove);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ObjectsCleaner(Map<String, StationClientNode> clientNodeMap) {
        this.clientNodeMap = clientNodeMap;
    }
}
