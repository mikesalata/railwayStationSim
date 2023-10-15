package org.msalata.pkpstation.logic;

import org.msalata.pkpstation.models.StaticStationObjectNode;

import java.util.UUID;

public class InfoOffice extends Thread implements ObjectWithIdentity {

    private StationQueue<Client> queue;
    private OfficeWorkstation officeWorkstation;
    private volatile StaticStationObjectNode staticStationNode;
    private final String id;

    public InfoOffice(StationQueue<Client> queue, String name, UUID id, OfficeWorkstation officeWorkstation, StaticStationObjectNode staticStationNode) {
        this.queue = queue;
        this.setName(name);
        this.officeWorkstation = officeWorkstation;
        this.id = id.toString();
        this.staticStationNode = staticStationNode;
    }

    @Override
    public void run() {
        while (true) {
            Client client = queue.poll();
            client.gettingInformation(id);
            officeWorkstation.retrieveInfo(staticStationNode);
            try {
                // Info retrieval duration
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            client.infoGiven();
        }
    }

    @Override
    public String getIdentifier() {
        return id;
    }
}
