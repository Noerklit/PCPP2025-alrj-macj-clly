package exercises02;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


public class Monitor {
    private int readers = 0;
    private boolean writer = false;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void readLock() {
        synchronized(lock) {
            while (writer) {
                condition.await();
            }
            readers++;
            
        }
    }

    public void readUnlock() {
        synchronized(lock) {
            readers--;
            if (readers == 0) {
                condition.signalAll();
            }
        }
    }

    public void writeLock() {
        synchronized(lock) {
             while (writer) {
                condition.await();
            }
            writer = true;
            while (readers > 0) {
                condition.await();
            }
        }
    }

    public void writeUnlock() {
        synchronized(lock) {
            writer = false;
            condition.signalAll();
        }
    }
}
