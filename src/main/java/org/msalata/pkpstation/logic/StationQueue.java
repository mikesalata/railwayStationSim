package org.msalata.pkpstation.logic;

import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StationQueue<T> implements ObjectWithIdentity {

    private QueueNode<T> head;
    private QueueNode<T> tail;
    private final Lock lock = new ReentrantLock();
    private final Semaphore producerSemaphore;
    private final Semaphore consumerSemaphore;
    private final String id;

    public StationQueue(int capacity, UUID id) {
        this.producerSemaphore = new Semaphore(capacity);
        this.consumerSemaphore = new Semaphore(0);
        this.id = id.toString();
    }

    public void offer(T element) {
        QueueNode<T> toOffer = new QueueNode<>(element);
        try {
            producerSemaphore.acquire();
            lock.lock();
            if (head == null) {
                head = toOffer;
            } else {
                tail.next = toOffer;
                toOffer.prev = tail;
            }
            tail = toOffer;
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        } finally {
            if (head != null && head.value instanceof Client) {
                updatePositions();
            }
            lock.unlock();
            consumerSemaphore.release();
        }
    }

    public T poll() {
        try {
            consumerSemaphore.acquire();
            lock.lock();
            if (head == null) {
                return null;
            }
            QueueNode<T> toPoll = head;
            head = head.next;
            if (head == null) {
                tail = null;
            }
            Client client = (Client) toPoll.value;
            client.setQueuePosition(-1);
            return toPoll.value;
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        } finally {
            if (head != null && head.value instanceof Client) {
                updatePositions();
            }
            lock.unlock();
            producerSemaphore.release();
        }
    }

    private void updatePositions() {
        QueueNode<T> iterator = head;
        int position = 0;
        while (iterator != null) {
            Client client = (Client) iterator.value;
            client.setQueuePosition(position);
            iterator = iterator.next;
            position++;
        }
    }

    @Override
    public String toString() {
        try {
            lock.lock();
            QueueNode<T> iterator = head;
            StringBuilder toString = new StringBuilder();
            while (iterator != null) {
                toString.append(iterator.value.toString()).append(" ");
                iterator = iterator.next;
            }
            return toString.toString();
        } finally {
            lock.unlock();
        }
    }

    public String getIdentifier() {
        return id;
    }

    private static class QueueNode<T> {
        private final T value;
        private QueueNode<T> next;
        private QueueNode<T> prev;

        public QueueNode(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

}