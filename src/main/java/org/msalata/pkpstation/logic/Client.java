package org.msalata.pkpstation.logic;

import org.msalata.pkpstation.Constants;
import org.msalata.pkpstation.models.StationClientNode;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import static org.msalata.pkpstation.logic.Client.CLIENT_STATE.*;

public class Client extends Thread {

    enum CLIENT_STATE {
        APPEARED,
        WAITING_IN_QUEUE,
        WAITING_IN_INFO_QUEUE,
        BUYING_TICKET,
        TICKET_BOUGHT,
        GETTING_INFORMATION,
        INFO_GIVEN,
        ESCAPED
    }

    private final StationQueue<Client> queue;
    private final StationQueue<Client> infoQueue;
    private volatile int queuePosition = -1;
    private volatile CLIENT_STATE state = CLIENT_STATE.APPEARED;
    private final StationClientNode stationClientNode;
    private String ticketOfficeIdentifier;
    private String infoOfficeIdentifier;
    private final boolean goForInfo;
    private volatile boolean processFinished = false;
    private final Semaphore creationSemaphore;
    private final Queue<CLIENT_STATE> stateQueue = new ConcurrentLinkedQueue<>();

    public Client(StationQueue<Client> queue, String id,
                  StationQueue<Client> infoQueue, StationClientNode stationClientNode,
                  Semaphore creationSemaphore, boolean goForInfo) {
        this.queue = queue;
        this.infoQueue = infoQueue;
        this.setName("Client " + id);
        this.stationClientNode = stationClientNode;
        this.creationSemaphore = creationSemaphore;
        this.goForInfo = goForInfo;
    }

    @Override
    public void run() {
        stationClientNode.waitForNodeAppear();
        while (!processFinished) {
            CLIENT_STATE newState = stateQueue.poll();
            if (newState != null) {
                state = newState;
            }
            switch (state) {
                case APPEARED -> {
                    stationClientNode.moveToQueuePoint(queue.getIdentifier(), Constants.OUT_OF_QUEUE_POSITION);
                    stateQueue.offer(WAITING_IN_QUEUE);
                    queue.offer(this);
                }
                case WAITING_IN_QUEUE -> stationClientNode.moveToQueuePoint(queue.getIdentifier(), queuePosition);
                case BUYING_TICKET -> stationClientNode.moveToPoint(ticketOfficeIdentifier);
                case TICKET_BOUGHT -> {
                    if (goForInfo) {
                        stateQueue.offer(WAITING_IN_INFO_QUEUE);
                        stationClientNode.moveToQueuePoint(infoQueue.getIdentifier(), Constants.OUT_OF_QUEUE_POSITION);
                        infoQueue.offer(this);
                    } else {
                        stationClientNode.moveToEscapePoint();
                        stateQueue.offer(ESCAPED);
                    }
                }
                case WAITING_IN_INFO_QUEUE ->
                        stationClientNode.moveToQueuePoint(infoQueue.getIdentifier(), queuePosition);
                case GETTING_INFORMATION -> stationClientNode.moveToPoint(infoOfficeIdentifier);
                case INFO_GIVEN -> {
                    stationClientNode.moveToEscapePoint();
                    stateQueue.offer(ESCAPED);

                }
                case ESCAPED -> {
                    stationClientNode.kill();
                    creationSemaphore.release();
                    processFinished = true;
                }
            }
        }
    }

    public synchronized void ticketBought() {
        stateQueue.offer(CLIENT_STATE.TICKET_BOUGHT);
    }

    public synchronized void buyingTicket(String ticketOfficeIdentifier) {
        this.ticketOfficeIdentifier = ticketOfficeIdentifier;
        stateQueue.offer(CLIENT_STATE.BUYING_TICKET);
    }

    public synchronized void gettingInformation(String infoOfficeIdentifier) {
        this.infoOfficeIdentifier = infoOfficeIdentifier;
        stateQueue.offer(CLIENT_STATE.GETTING_INFORMATION);
    }

    public synchronized void infoGiven() {
        stateQueue.offer(CLIENT_STATE.INFO_GIVEN);
    }

    public synchronized void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    @Override
    public String toString() {
        return getName();
    }
}
