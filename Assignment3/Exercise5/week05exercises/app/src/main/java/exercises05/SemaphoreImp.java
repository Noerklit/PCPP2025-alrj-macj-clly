// For week 4
// raup@itu.dk * 12/09/2023
package exercises05;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreImp implements ISemaphore {

    private final int capacity;
    private int state; // safely-published because it is initialized to zero (see constructor)
    private Lock lock;
    private Condition condition;

    public SemaphoreImp(int c) {
        capacity = c;
        state = 0;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    @Override
    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (state == capacity) {
                condition.await();
            }
            state++;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release() {
        lock.lock();
        try {
            state--;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
