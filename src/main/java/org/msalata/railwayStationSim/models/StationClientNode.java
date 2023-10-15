package org.msalata.railwayStationSim.models;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.msalata.railwayStationSim.Constants;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.msalata.railwayStationSim.Constants.CLIENT_BLOCK_SIZE;
import static org.msalata.railwayStationSim.Constants.OBJECT_MOVEMENT_DELTA;

public class StationClientNode {


    private final Node node;
    private volatile boolean dead = false;
    volatile double targetX;
    volatile double targetY;
    private final String id;
    private final StationObjects stationObjects;
    private boolean appeared = false;
    private final Queue<TargetPoint> targetRoute = new ConcurrentLinkedQueue<>();
    private final Lock movingSyncLock = new ReentrantLock();
    private final Condition movingSyncLockCondition = movingSyncLock.newCondition();
    private final Lock appearSyncLock = new ReentrantLock();
    private final Condition appearSyncLockCondition = appearSyncLock.newCondition();

    public StationClientNode(double clientStartPointX, double clientStartPointY, String id, StationObjects stationObjects) {
        Random random = new Random();
        this.targetX = clientStartPointX;
        this.targetY = clientStartPointY;
        this.id = id;
        this.stationObjects = stationObjects;
        Rectangle client = new Rectangle(CLIENT_BLOCK_SIZE, CLIENT_BLOCK_SIZE);
        double colorMin = 0.2;
        double colorMax = 1.0;
        client.setFill(Color.color(random.nextDouble(colorMin, colorMax), random.nextDouble(colorMin, colorMax), random.nextDouble(colorMin, colorMax)));
        node = client;
        node.setTranslateX(clientStartPointX);
        node.setTranslateY(clientStartPointY);
    }

    public void moveToQueuePoint(String queueObjectId, int queuePosition) {
        if (queueObjectId == null) {
            return;
        }
        double targetXCoordinate;
        double targetYCoordinate;

        StationObject queueObject = stationObjects.getDynamicStationNodeMap().get(queueObjectId);
        targetXCoordinate = queueObject.getX();
        if (queuePosition == -1) {
            targetYCoordinate = queueObject.getY();
        } else {
            if (queueObject.isReversed()) {
                targetYCoordinate = queueObject.getY() - (queuePosition + 1) * CLIENT_BLOCK_SIZE * 1.5;
            } else {
                targetYCoordinate = queueObject.getY() + (queuePosition + 1) * CLIENT_BLOCK_SIZE * 1.5;
            }
        }
        moveAndWaitForMovement(targetXCoordinate, targetYCoordinate);
    }

    public void moveToPoint(String stationObjectId) {
        double targetXCoordinate;
        double targetYCoordinate;
        if (stationObjectId == null) {
            return;
        }
        StationObject stationObject = stationObjects.getAnyTypeStationObject(stationObjectId);
        if (!stationObject.isReversed()) {
            targetXCoordinate = stationObject.getX() + stationObject.getWidth() / 2.0 - CLIENT_BLOCK_SIZE / 4.0;
            targetYCoordinate = stationObject.getY() + stationObject.getHeight() * 1.05;
        } else {
            targetXCoordinate = stationObject.getX() + stationObject.getWidth() / 2.0 - CLIENT_BLOCK_SIZE / 4.0;
            targetYCoordinate = stationObject.getY() - stationObject.getHeight() * 0.1;
        }
        moveAndWaitForMovement(targetXCoordinate, targetYCoordinate);
    }

    public void performClientMovement() {
        try {
            movingSyncLock.lock();
            boolean isStaying = isStaying();
            if (isStaying && targetRoute.isEmpty()) {
                movingSyncLockCondition.signal();
            } else if (isStaying) {
                TargetPoint target = targetRoute.poll();
                if (target != null) {
                    targetX = target.x();
                    targetY = target.y();
                }
            }
            move(getNode());
        } finally {
            movingSyncLock.unlock();
        }
    }

    public boolean isStaying() {
        return Math.abs(node.getTranslateX() - targetX) < OBJECT_MOVEMENT_DELTA
                && Math.abs(node.getTranslateY() - targetY) < OBJECT_MOVEMENT_DELTA;
    }

    public void moveToEscapePoint() {
        double targetXCoordinate;
        double targetYCoordinate;
        targetXCoordinate = Constants.ESCAPE_X;
        targetYCoordinate = Constants.ESCAPE_Y;
        moveAndWaitForMovement(targetXCoordinate, targetYCoordinate);
    }

    public void appear() {
        try {
            appearSyncLock.lock();
            this.appeared = true;
            appearSyncLockCondition.signal();
        } finally {
            appearSyncLock.unlock();
        }
    }

    public boolean appeared() {
        return appeared;
    }

    public Node getNode() {
        return node;
    }

    public boolean isDead() {
        return dead;
    }

    public void kill() {
        dead = true;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getSpeed() {
        return OBJECT_MOVEMENT_DELTA;
    }

    public String getId() {
        return id;
    }

    private void moveAndWaitForMovement(double targetX, double targetY) {
        try {
            movingSyncLock.lock();
            this.targetRoute.offer(new TargetPoint(targetX, targetY));
            movingSyncLockCondition.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            movingSyncLock.unlock();
        }
    }

    public void waitForNodeAppear() {
        try {
            appearSyncLock.lock();
            while (!appeared) {
                appearSyncLockCondition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            appearSyncLock.unlock();
        }
    }

    private void move(Node clientNode) {
        if (Math.abs(clientNode.getTranslateX() - getTargetX()) >= getSpeed()) {
            clientNode.setTranslateX(clientNode.getTranslateX() < getTargetX() ?
                    clientNode.getTranslateX() + getSpeed() : clientNode.getTranslateX() - getSpeed());
        } else if (Math.abs(clientNode.getTranslateX() - getTargetX()) < getSpeed()
                && Math.abs(clientNode.getTranslateX() - getTargetX()) > 0.0) {
            clientNode.setTranslateX(getTargetX());
        } else if (Math.abs(clientNode.getTranslateY() - getTargetY()) >= getSpeed()) {
            clientNode.setTranslateY(clientNode.getTranslateY() < getTargetY() ?
                    clientNode.getTranslateY() + getSpeed() : clientNode.getTranslateY() - getSpeed());
        } else if (Math.abs(clientNode.getTranslateY() - getTargetY()) < getSpeed()
                && Math.abs(clientNode.getTranslateY() - getTargetY()) > 0.0) {
            clientNode.setTranslateY(getTargetY());
        }
    }
}
