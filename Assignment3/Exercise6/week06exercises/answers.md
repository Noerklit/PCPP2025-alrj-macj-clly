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
        AtomicInteger currBin = bins[bin];
        do {
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