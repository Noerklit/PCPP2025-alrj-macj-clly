package exercises10;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram {

    private final AtomicInteger[] bins;

    public CasHistogram(int span) {
        this.bins = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            bins[i] = new AtomicInteger(0);
        }
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
        return bins.length;
    }

    public int getAndClear(int bin) {
        int res;
        AtomicInteger currBin = bins[bin];
        do {
            res = currBin.get();
        } while (!currBin.compareAndSet(res, 0));
        return res;
    }
}
