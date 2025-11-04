package exercises10;

import java.util.concurrent.RecursiveTask;

class ForkCountPrimes extends RecursiveTask<Long> {

    private final int from, to;
    private final static int THRESHOLD = 10000;

    public ForkCountPrimes(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    protected Long compute() {
        if (to - from <= THRESHOLD) {
            return computeDirectly();
        }
        int mid = from + (to - from) / 2;

        ForkCountPrimes t1 = new ForkCountPrimes(from, mid);
        t1.fork();
        ForkCountPrimes t2 = new ForkCountPrimes(mid, to);
        long c2 = t2.compute();
        long c1 = t1.join();
        return c1 + c2;
    }

    protected Long computeDirectly() {
        long acc = 0;
        for (int i = from; i < to; i++) {
            if (isPrime(i)) {
                acc++;
            }
        }
        return acc;
    }

    private static boolean isPrime(int n) {
        int k = 2;
        while (k * k <= n && n % k != 0) {
            k++;
        }
        return n >= 2 && k * k > n;
    }

}
