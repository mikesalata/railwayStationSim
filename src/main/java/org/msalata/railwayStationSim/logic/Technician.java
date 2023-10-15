package org.msalata.railwayStationSim.logic;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Technician extends Thread {

    private final OfficeWorkstation officeWorkstation;


    public Technician(OfficeWorkstation officeWorkstation) {
        this.officeWorkstation = officeWorkstation;
    }

    @Override
    public void run() {
        Lock crashLock = officeWorkstation.getCrashLock();
        Condition waitingForACrashCondition = officeWorkstation.getWaitingForACrashCondition();
        while (true) {
            try {
                crashLock.lock();
                if (officeWorkstation.getStatus() != OfficeWorkstation.WORKSTATION_STATUS.CRASHED) {
                    waitingForACrashCondition.await();
                }
                if (officeWorkstation.getStatus() == OfficeWorkstation.WORKSTATION_STATUS.CRASHED) {
                    // Workstation reparation duration
                    Thread.sleep(5L * 1000L);
                    officeWorkstation.repairWorkstation();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                crashLock.unlock();
            }
        }
    }
}
