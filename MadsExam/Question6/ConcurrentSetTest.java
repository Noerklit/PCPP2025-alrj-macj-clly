package exercises05;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Repeatable;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.*;
// TODO: Very likely you need to expand the list of imports


public class ConcurrentSetTest {

    // Variable with set under test
    private ConcurrentIntegerSet set;
    private CyclicBarrier barrier;

    // TODO: Very likely you should add more variables here

    // Uncomment the appropriate line below to choose the class to test
    // Remember that @BeforeEach is executed before each test
    @BeforeEach
    public void initialize() {
        // init set
        // set = new ConcurrentIntegerSetBuggy();
        // set = new ConcurrentIntegerSetSync();
        set = new ConcurrentIntegerSetLibrary();
    }

    @RepeatedTest(10)
    @DisplayName("Add with unique numbers")
    public void testingAddWithUniqueNumbers() {
        for (int i = 0; i < 10000; i++) {
            set.add(i);
        }
        assertEquals(10000, set.size());
        ;
    }

    @RepeatedTest(10)
    @DisplayName("Add with threads")
    public void testingAddWithThreads() throws Exception {
        final int nrThreads = 2;
        final int N = 10000;

        barrier = new CyclicBarrier(nrThreads + 1);

        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int i = 0; i < N; i++) {
                        set.add(i);
                    }
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        barrier.await();
        barrier.await();
        assertEquals(N, set.size());
    }

    @RepeatedTest(10)
    @DisplayName("Remove with threads")
    public void testingRemoveWithThreads() throws Exception {
        final int nrThreads = 2;
        final int N = 10000;

        barrier = new CyclicBarrier(nrThreads + 1);

        for (int i = 0; i < N; i++) {
            set.add(i);
        }

        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int i = 0; i < N; i++) {
                        set.remove(i);
                    }
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        barrier.await();
        barrier.await();
        assertEquals(0, set.size());
    }
}

