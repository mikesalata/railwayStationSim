package org.msalata.railwayStationSim.logic;

import org.msalata.railwayStationSim.models.StaticStationObjectNode;

import java.util.Random;
import java.util.UUID;

public class TicketOffice extends Thread implements ObjectWithIdentity {

    private final StationQueue<Client> queue;
    private final String id;
    private final StaticStationObjectNode staticStationNode;

    public TicketOffice(StationQueue<Client> queue, String name, UUID id, StaticStationObjectNode staticStationNode) {
        this.queue = queue;
        this.setName(name);
        this.id = id.toString();
        this.staticStationNode = staticStationNode;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            Client client = queue.poll();
            client.buyingTicket(id);
            try {
                // Ticket sell duration
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            client.ticketBought();

            if (0 == random.nextLong(9)) {
                staticStationNode.startBreak();
                try {
                    // Ticket office break duration
                    Thread.sleep(10000L);
                    staticStationNode.finishBreak();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String getIdentifier() {
        return id;
    }
}
