// raup@itu.dk * 2023-10-20
package exercises07;

// JUnit testing imports
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TestLockFreeStack {

    // The imports above are just for convenience, feel free add or remove imports
    // TODO: 7.2.2 - Test push
    @Test
    public void TestPushLockFreeStack_ExpectedPushSumToBeActualSum() throws InterruptedException, BrokenBarrierException {
        LockFreeStack<Integer> sut = new LockFreeStack<>();
        int n = 500;
        CyclicBarrier barrier = new CyclicBarrier(n + 1);
        int expected = 0;
        Integer actual = 0;

        for (Integer i = 0; i < n; i++) {
            final Integer tmp = i;
            Thread thread = new Thread(() -> {
                try {
                    barrier.await();
                    sut.push(tmp);
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                }
            });
            thread.start();
            expected += i;
        }

        barrier.await();
        barrier.await();

        for (Integer curr = 0; curr != null; curr = sut.pop()) {
            actual += curr;
        }

        assertEquals(expected, actual);

    }

    // TODO: 7.2.3 - Test pop
}
