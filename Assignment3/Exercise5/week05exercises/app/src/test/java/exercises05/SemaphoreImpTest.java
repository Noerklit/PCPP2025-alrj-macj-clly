package exercises05;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

public class SemaphoreImpTest {

    private ISemaphore sImp;

    @RepeatedTest(10)
    @DisplayName("Aquire more than c permits")
    public void aquireMoreThanCPermits() throws InterruptedException, BrokenBarrierException {
        final int c = 10;
        final int nThreads = c + 1;
        final AtomicInteger threadsInCriticalSection = new AtomicInteger(0);
        final CyclicBarrier barrier = new CyclicBarrier(nThreads + 1);
        sImp = new SemaphoreImp(c);

        sImp.release();
        for (int i = 0; i < nThreads; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    sImp.acquire();
                    threadsInCriticalSection.getAndIncrement();
                } catch (InterruptedException | BrokenBarrierException e) {
                }
            }).start();
        }
        barrier.await();
        assertEquals(c, threadsInCriticalSection.get());
    }
}
