# 6.1
## 6.1.1
```java
public class CasHistogram implements Histogram {

    private final int span;
    private final AtomicInteger[] bins;

    public CasHistogram(int span) {
        bins = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            bins[i] = new AtomicInteger(0);
        }
        this.span = span;
    }

    @Override
    public void increment(int bin) {
        int val;
        do {
            AtomicInteger currBin = bins[bin]; // move it inside the loop
            val = currBin.get();
        } while (!currBin.compareAndSet(val, val + 1));
    }

    @Override
    public int getCount(int bin) {
        return bins[bin].get();
    }

    @Override
    public int getSpan() {
        return span;
    }

    @Override
    public int getAndClear(int bin) {
        int res;
        AtomicInteger currBin = bins[bin];
        do {
            res = currBin.get();
        } while (!currBin.compareAndSet(res, 0));
        return res;
    }
}
```
- The state does not escape in our implementation because all fields are private.
- There is safe publication because every field is final, i.e. imutable. Also no atomic integers escape method calls, ensuring no external code can directly modify the atomic values.

## 6.1.2
Yes we ensure that the operation is executed atomically as we do not clear the bin until we are sure that the bin has not been tampered with.

## 6.1.3
```java
public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports

    // TODO: 5.1.3
    @RepeatedTest(10)
    @DisplayName("Test cas histogram supports parrallel operations")
    public void TestCasHistogramParallelism_ShouldCountFactorsCorrectly() throws InterruptedException {
        int span = 5000;
        FactorCalc expected = new FactorCalc(new CasHistogram(span), 0, span);
        expected.start();
        expected.join();

        //Histogram sut = new Histogram1(span);
        Histogram sut = new CasHistogram(span);

        List<FactorCalc> cList = new LinkedList<>();

        int nrThreads = 10;
        int sectionSize = span / nrThreads;
        for (int i = 0; i < nrThreads; i++) {
            FactorCalc tmp = new FactorCalc(sut, i * sectionSize, sectionSize * (i + 1));
            cList.add(tmp);
            tmp.start();
        }

        cList.forEach((calc) -> {
            try {
                calc.join();
            } catch (InterruptedException ex) {
                fail(ex.getMessage());
            }
        });

        cList.forEach((calc) -> expected.assertEqualOther(calc));
    }

    // Function to count the numbe of prime factors of a number `p`
    private static int countFactors(int p) {
        if (p < 2) {
            return 0;
        }
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p = p / k;
            } else {
                k = k + 1;
            }
        }
        return factorCount;
    }

    private class FactorCalc extends Thread {

        private final Histogram his;
        private final int start, end;

        public FactorCalc(Histogram his, int start, int end) {
            this.his = his;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                int bin = countFactors(i);
                his.increment(bin);
            }
        }

        public void assertEqualOther(FactorCalc other) {
            for (int i = start; i < end; i++) {
                assertEquals(his.getCount(i), other.his.getCount(i));
            }
        }
    }

}
```
# 6.2
## 6.2.1
```java
private static abstract class Holders {

    public abstract boolean isReader();

    public abstract boolean isWriter();

    public abstract boolean isReaderList();

    public abstract boolean isWriterLock();
}

private static class ReaderList extends Holders {

    private final Thread thread;
    private final ReaderList next;

    // TODO: Constructor
    public ReaderList(ReaderList next) {
        thread = Thread.currentThread();
        this.next = next;
    }

    private ReaderList(Thread thread, ReaderList next) {
        this.thread = thread;
        this.next = next;
    }

    public ReaderList CloneRemoved() {
        Thread t = Thread.currentThread();

        if (thread == t && next == null) {
            return null;
        }
        if (thread == t) {
            return next.CloneRemoved();
        }
        if (next == null) {
            return new ReaderList(thread, null);
        }
        return new ReaderList(thread, next.CloneRemoved());
    }

    @Override
    public boolean isReader() {
        Thread curr = Thread.currentThread();
        if (curr == thread) {
            return true;
        }
        if (next == null) {
            return false;
        }
        return next.isReader();
    }

    @Override
    public boolean isWriter() {
        return !isReader();
    }

    @Override
    public boolean isReaderList() {
        return true;
    }

    @Override
    public boolean isWriterLock() {
        return false;
    }
}

private static class Writer extends Holders {

    public final Thread thread;

    public Writer() {
        thread = Thread.currentThread();
    }

    @Override
    public boolean isReader() {
        return !isWriter();
    }

    @Override
    public boolean isWriter() {
        return thread == Thread.currentThread();
    }

    @Override
    public boolean isReaderList() {
        return false;
    }

    @Override
    public boolean isWriterLock() {
        return true;
    }
}
public boolean writerTryLock() {
    Holders writer = new Writer();
    do {
        if (holders.get() != null) {
            return false;
        }
    } while (!holders.compareAndSet(null, writer));
    return true;
}
```

## 6.2.2
```java
public void writerUnlock() throws RuntimeException {
    Holders holder;
    do {
        holder = holders.get();
        if (holder == null || !holder.isWriter()) {
            throw new RuntimeException();
        }
    } while (!holders.compareAndSet(holder, null));
}
```
## 6.2.3
```java
public boolean readerTryLock() {
    ReaderList rl = null;
    Holders holder;
    do {
        holder = holders.get();
        if (holder != null) {
            if (holder.isWriterLock()) {
                return false;
            }
            rl = (ReaderList) holder;
        }
        rl = new ReaderList(rl);
    } while (!holders.compareAndSet(holder, rl));
    return true;
}
```

## 6.2.4
```java
public void readerUnlock() throws RuntimeException {
    ReaderList newRl;
    Holders holder;
    do {
        holder = holders.get();
        if (holder == null || !holder.isReader()) {
            throw new RuntimeException();
        }
        newRl = ((ReaderList) holder).CloneRemoved();
    } while (!holders.compareAndSet(holder, newRl));
}
```
## 6.2.5
```java
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
```
## 6.2.6
```java
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
```