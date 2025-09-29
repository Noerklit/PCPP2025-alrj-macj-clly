package exercises06;

// JUnit testing imports
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class TestLocks {
    // The imports above are just for convenience, feel free add or remove imports

    // TODO: 5.2.5
    @Test
    public void ReadWriteLock_ReaderLockUnlock() {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        assertTrue(sut.readerTryLock());
        boolean success = true;
        try {
            sut.readerUnlock();
        } catch (Exception e) {
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void ReadWriteLock_WriterLockUnlock() {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        assertTrue(sut.writerTryLock());
        boolean success = true;
        try {
            sut.writerUnlock();
        } catch (Exception e) {
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void ReadWriteLock_WriterLockReadUnlock_DoesNotUnlock() {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        assertTrue(sut.writerTryLock());
        boolean success = true;
        try {
            sut.readerUnlock();
        } catch (Exception e) {
            success = false;
        }
        assertFalse(success);
    }

    @Test
    public void ReadWriteLock_ReaderLockWriteUnlock_DoesNotUnlock() {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        assertTrue(sut.readerTryLock());
        boolean success = true;
        try {
            sut.writerUnlock();
        } catch (Exception e) {
            success = false;
        }
        assertFalse(success);
    }

    @Test
    public void ReadWriteLock_ReaderLockWriteLock_LockNotAquired() {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        assertTrue(sut.readerTryLock());
        assertFalse(sut.writerTryLock());
    }

    @Test
    public void ReadWriteLock_WriteLockReaderLock_LockNotAquired() {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        assertTrue(sut.writerTryLock());
        assertFalse(sut.readerTryLock());
    }

    @Test
    public void ReadWriteLock_ReaderLockUnlock_WriteLockUnlock() {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        assertTrue(sut.readerTryLock());
        boolean success = true;
        try {
            sut.readerUnlock();
        } catch (Exception e) {
            success = false;
        }
        assertTrue(success);
        assertTrue(sut.writerTryLock());
        try {
            sut.writerUnlock();
        } catch (Exception e) {
            success = false;
        }
        assertTrue(success);
    }

    private void assertTrue(Object actual) {
        assertEquals(true, actual);
    }

    private void assertFalse(Object actual) {
        assertEquals(false, actual);
    }

    @RepeatedTest(10)
    public void ReadWriteLocks_ReadersBeatsWriter() throws InterruptedException, BrokenBarrierException {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        int numReaders = 5;
        int numWriters = 1;
        ConcurrentHashMap<Thread, Boolean> readerSuccess = new ConcurrentHashMap<>();
        ConcurrentHashMap<Thread, Boolean> writerSuccess = new ConcurrentHashMap<>();
        CyclicBarrier barrier = new CyclicBarrier(numWriters + 1);
        CyclicBarrier readerBarrier = new CyclicBarrier(numReaders + 1);

        for (int i = 0; i < numReaders; i++) {
            new RWTestThread(sut, readerBarrier, readerSuccess, true).start();
        }
        readerBarrier.await();
        readerBarrier.await();
        for (int i = 0; i < numWriters; i++) {
            new RWTestThread(sut, barrier, writerSuccess, false).start();
        }
        barrier.await();
        barrier.await();

        assertEquals(true, readerSuccess.values().stream().allMatch((a) -> a));
        assertEquals(true, writerSuccess.values().stream().allMatch((a) -> !a));
    }

    @RepeatedTest(10)
    public void ReadWriteLocks_WriterBeatsReaders() throws InterruptedException, BrokenBarrierException {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        int numReaders = 5;
        int numWriters = 1;
        ConcurrentHashMap<Thread, Boolean> readerSuccess = new ConcurrentHashMap<>();
        ConcurrentHashMap<Thread, Boolean> writerSuccess = new ConcurrentHashMap<>();
        CyclicBarrier barrier = new CyclicBarrier(numWriters + 1);
        CyclicBarrier readerBarrier = new CyclicBarrier(numReaders + 1);

        for (int i = 0; i < numWriters; i++) {
            new RWTestThread(sut, barrier, writerSuccess, false).start();
        }
        barrier.await();
        barrier.await();

        for (int i = 0; i < numReaders; i++) {
            new RWTestThread(sut, readerBarrier, readerSuccess, true).start();
        }
        readerBarrier.await();
        readerBarrier.await();

        assertEquals(true, readerSuccess.values().stream().allMatch((a) -> !a));
        assertEquals(true, writerSuccess.values().stream().allMatch((a) -> a));
    }

    @RepeatedTest(10)
    public void ReadWriteLocks_WriterContention_OnlyOneAquires() throws InterruptedException, BrokenBarrierException {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        int numWriters = 10;
        ConcurrentHashMap<Thread, Boolean> writerSuccess = new ConcurrentHashMap<>();
        CyclicBarrier barrier = new CyclicBarrier(numWriters + 1);

        for (int i = 0; i < numWriters; i++) {
            new RWTestThread(sut, barrier, writerSuccess, false).start();
        }
        barrier.await();
        barrier.await();

        boolean oneSuccess = false;

        for (boolean elem : writerSuccess.values()) {
            if (elem && oneSuccess) {
                fail("More than one writer holds the lock");
            }
            if (elem) {
                oneSuccess = true;
            }
        }
        assertEquals(true, oneSuccess);
    }

    @RepeatedTest(10)
    public void ReadWriteLocks_ReadersBeatsWriter_WriterAquiresAfterRelease() throws InterruptedException, BrokenBarrierException {
        SimpleRWTryLockInterface sut = new ReadWriteCASLock();
        int numReaders = 5;
        int numWriters = 1;
        ConcurrentHashMap<Thread, Boolean> readerSuccess = new ConcurrentHashMap<>();
        ConcurrentHashMap<Thread, Boolean> writerSuccess = new ConcurrentHashMap<>();
        CyclicBarrier barrier = new CyclicBarrier(numWriters + numReaders + 1);
        CyclicBarrier readerBarrier = new CyclicBarrier(numReaders + 1);

        for (int i = 0; i < numReaders; i++) {
            new Thread(() -> {
                try {
                    readerBarrier.await();
                    boolean success = sut.readerTryLock();
                    readerBarrier.await();
                    readerSuccess.put(Thread.currentThread(), success);
                    sut.readerUnlock();
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                }
            }).start();
        }
        readerBarrier.await();
        readerBarrier.await();
        for (int i = 0; i < numWriters; i++) {
            new Thread(() -> {
                try {
                    while (!sut.writerTryLock()) {

                    }
                    boolean success = true;
                    try {
                        sut.writerUnlock();
                    } catch (Exception e) {
                        success = false;
                    }
                    writerSuccess.put(Thread.currentThread(), success);
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                }
            }).start();
        }
        barrier.await();

        assertEquals(true, readerSuccess.values().stream().allMatch((a) -> a));
        assertEquals(true, writerSuccess.values().stream().allMatch((a) -> a));
    }

    public class RWTestThread extends Thread {

        private final SimpleRWTryLockInterface sut;
        private final CyclicBarrier barrier;
        private final ConcurrentHashMap<Thread, Boolean> readerSuccess;
        private final boolean isReader;

        public RWTestThread(
                SimpleRWTryLockInterface lock,
                CyclicBarrier barrier,
                ConcurrentHashMap<Thread, Boolean> readerSuccess,
                boolean isReader) {
            sut = lock;
            this.barrier = barrier;
            this.readerSuccess = readerSuccess;
            this.isReader = isReader;
        }

        @Override
        public void run() {
            try {
                barrier.await();

                boolean success;
                if (isReader) {
                    success = sut.readerTryLock();
                } else {
                    success = sut.writerTryLock();
                }
                readerSuccess.put(Thread.currentThread(), success);
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                fail("More than one writer holds the lock");
            }
        }
    }
}
