package exercises05;

import java.util.concurrent.Semaphore;

public class SemaphoreWrapper implements ISemaphore {

    private final Semaphore semp;

    public SemaphoreWrapper(int c) {
        this.semp = new Semaphore(c);
    }

    @Override
    public void acquire() throws InterruptedException {
        semp.acquire();
    }

    @Override
    public void release() {
        semp.release();
    }
}
