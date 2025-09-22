package exercises05;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

public class SemaphoreImpTest {

    private ISemaphore sImp;

    @RepeatedTest(10)
    @DisplayName("Aquire more than c permits")
    public void aquireMoreThanCPermits() {
        final int c = 10;
        final AtomicInteger threadsInCriticalSection = new AtomicInteger(0);
        sImp = new SemaphoreImp(c);
        //sImp = new SemaphoreWrapper(c);

        sImp.release();

        for (int i = 0; i <= c + 1; i++) {
            new Thread(() -> {
                try {
                    sImp.acquire();
                    threadsInCriticalSection.getAndIncrement();
                } catch (InterruptedException e) {
                }
            }).start();
        }
        assertEquals(c, threadsInCriticalSection.get());
    }
}
