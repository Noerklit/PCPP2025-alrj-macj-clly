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
