package org.msalata.railwayStationSim.logic;

import org.msalata.railwayStationSim.models.StaticStationObjectNode;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OfficeWorkstation extends Thread {


    public enum WORKSTATION_STATUS {
        WORKING,
        CRASHED
    }

    private final Lock crashLock = new ReentrantLock();
    private final Lock repairLock = new ReentrantLock();
    private final Condition waitingForACrashCondition = crashLock.newCondition();
    private final Condition waitingForARepairCondition = repairLock.newCondition();
    private volatile WORKSTATION_STATUS status = WORKSTATION_STATUS.WORKING;

    public WORKSTATION_STATUS getStatus() {
        return status;
    }

    public void repairWorkstation() {

        try {
            repairLock.lock();
            status = WORKSTATION_STATUS.WORKING;
            waitingForARepairCondition.signal();
        }
        finally {
            repairLock.unlock();
        }
    }

    public void retrieveInfo(StaticStationObjectNode staticStationNode) {
        repairLock.lock();
        try {
            while (status == WORKSTATION_STATUS.CRASHED) {
                staticStationNode.startBreak();
                waitingForARepairCondition.await();
            }
            staticStationNode.finishBreak();
        }
        catch (InterruptedException e) {
            throw new RuntimeException();
        }
        finally {
            repairLock.unlock();
        }
    }
    public Lock getCrashLock() {
        return crashLock;
    }

    public Condition getWaitingForACrashCondition() {
        return waitingForACrashCondition;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                // Time between attempt to hang system duration
                Thread.sleep( random.nextLong(15,21) * 1000L);
                tryHang();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void tryHang() {
        if (crashLock.tryLock()) {
            try {
                if (status == WORKSTATION_STATUS.WORKING) {
                    status = WORKSTATION_STATUS.CRASHED;
                    waitingForACrashCondition.signal();
                }
            }
            finally {
                crashLock.unlock();
            }
        }
    }
}
