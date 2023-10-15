package org.msalata.railwayStationSim.logic;

import org.msalata.railwayStationSim.Constants;
import org.msalata.railwayStationSim.models.StationClientNode;
import org.msalata.railwayStationSim.models.StationObjects;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ClientGenerator extends Thread {

    private final StationQueue<Client> cityTrainsQueue;
    private final StationQueue<Client> interCityTrainsQueue;
    private final StationQueue<Client> infoQueue;
    private final Map<String, StationClientNode> clientNodeMap;
    private final StationObjects stationObjects;
    private final Semaphore creationSemaphore = new Semaphore(Constants.MAX_CLIENT_THREADS);

    public ClientGenerator(StationQueue<Client> cityTrainsQueue, StationQueue<Client> interCityTrainsQueue,
                           StationQueue<Client> infoQueue, Map<String, StationClientNode> clientNodeMap,
                           StationObjects stationObjects) {
        this.cityTrainsQueue = cityTrainsQueue;
        this.interCityTrainsQueue = interCityTrainsQueue;
        this.infoQueue = infoQueue;
        this.clientNodeMap = clientNodeMap;
        this.stationObjects = stationObjects;
    }

    @Override
    public void run() {
        Random random = new Random();
        try (ExecutorService executorService = Executors.newFixedThreadPool(Constants.MAX_CLIENT_THREADS)) {
            while (true) {
                try {
                    String clientId = UUID.randomUUID().toString();
                    StationClientNode stationClientNode = new StationClientNode(Constants.MOVING_STARTING_POINT_X, Constants.MOVING_STARTING_POINT_Y, clientId, stationObjects);
                    creationSemaphore.acquire();
                    Client client = new Client(random.nextBoolean() ? cityTrainsQueue : interCityTrainsQueue,
                            clientId, infoQueue, stationClientNode, creationSemaphore, random.nextBoolean());
                    clientNodeMap.put(stationClientNode.getId(), stationClientNode);
                    executorService.submit(client);
                    // Time between client generation duration
                    Thread.sleep(random.nextLong(1L, 2L) * 600L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
