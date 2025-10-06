// raup@itu.dk * 2023-10-20
package exercises07;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;


class TestLockFreeStack {

    // The imports above are just for convenience, feel free add or remove imports

    // TODO: 7.2.2 - Test push
    @Test
    public void TestPushLockFreeStack_ExpectedPushSumToBeActualSum() {
        LockFreeStack sut = new LockFreeStack<>();
        int n = 500;
        List<Thread> lt = new List<Thread>();
        CyclicBarrier barrier = new CyclicBarrier(n+1);
        int expected = 0;
        Integer actual = 0;

        for(Integer i = 0; i < n; i++) {
            Thread thread = new Thread(() -> {
                barrier.await();
                sut.push(i);
                barrier.await();
            });
            lt.add(thread);
            thread.start();
            expected += i;
        }

        barrier.await();
        barrier.await();

        for(Integer curr = 0; curr != null; curr = sut.pop()) {
            actual += curr;
        }

        assertEquals(expected, actual);

    }

    // TODO: 7.2.3 - Test pop
}
