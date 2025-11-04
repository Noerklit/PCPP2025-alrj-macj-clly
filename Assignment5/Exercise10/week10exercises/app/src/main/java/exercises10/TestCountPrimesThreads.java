package exercises10;
// jst@itu.dk 08/20/2025

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import benchmarking.Benchmark;

public class TestCountPrimesThreads {

    public static void main(String[] args) {
        new TestCountPrimesThreads();
    }

    public TestCountPrimesThreads() {
        final int range = 100_000;
        Benchmark.Mark7("countSequential", i -> countSequential(range));
        Benchmark.Mark7("countParallelNForkJoining",
                i -> countParallelNForkJoin(range));
        for (int c = 1; c <= 16; c = 2 * c) {
            final int threadCount = c;
            Benchmark.Mark7(String.format("countParallelNFixedThreadPool %2d", threadCount),
                    i -> countParallelNFixedThreadPool(range, threadCount));
            Benchmark.Mark7(String.format("countParallelNManualThreads %2d", threadCount),
                    i -> countParallelNManualThreads(range, threadCount));
            Benchmark.Mark7(String.format("countParallelNForkJoinPool %2d", threadCount),
                    i -> countParallelNLocal(range, threadCount));

        }
    }

    private static boolean isPrime(int n) {
        int k = 2;
        while (k * k <= n && n % k != 0) {
            k++;
        }
        return n >= 2 && k * k > n;
    }

    // Sequential solution
    private static long countSequential(int range) {
        long count = 0;
        final int from = 0, to = range;
        for (int i = from; i < to; i++) {
            if (isPrime(i)) {
                count++;
            }
        }
        return count;
    }

    private static long countParallelNForkJoin(int range) {
        final ForkJoinPool pool = new ForkJoinPool();
        final ForkCountPrimes countPrimes = new ForkCountPrimes(0, range);
        final long res = pool.invoke(countPrimes);
        pool.shutdown();
        return res;
    }

    // General parallel solution, using multiple threads
    private static long countParallelNFixedThreadPool(int range, int threadCount) {
        final ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        final AtomicLong lc = new AtomicLong(0);
        final int perThread = range / threadCount;

        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            pool.submit(
                    () -> {
                        for (int i = from; i < to; i++) {
                            if (isPrime(i)) {
                                lc.incrementAndGet();
                            }
                        }
                    }
            );
        }

        pool.shutdown();
        try {
            if (pool.awaitTermination(20, TimeUnit.SECONDS)) {
                // System.out.println("All tasks completed");
            } else {
                System.out.println("Timeout elapsed before termination");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return lc.get();

        // Thread[] threads = new Thread[threadCount];
        // for (int t=0; t<threadCount; t++) {
        //   final int from = perThread * t, 
        //               to = (t+1==threadCount) ? range : perThread * (t+1); 
        //   threads[t] = new Thread( () -> {
        //        for (int i=from; i<to; i++)
        // 	       if (isPrime(i))
        // 	         lc.incrementAndGet();
        // });
        // }
        // for (int t=0; t<threadCount; t++) 
        //   threads[t].start();
        //   try {
        //     for (int t=0; t<threadCount; t++) 
        //         threads[t].join();
        //       //System.out.println("Primes: "+lc.get());
        //   } catch (InterruptedException exn) { }
    }

    private static long countParallelNManualThreads(int range, int threadCount) {
        final AtomicLong lc = new AtomicLong(0);
        final int perThread = range / threadCount;
        final CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);
        Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            threads[t] = new Thread(() -> {
                for (int i = from; i < to; i++) {
                    if (isPrime(i)) {
                        lc.incrementAndGet();
                    }
                }
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                }
            });
        }
        for (int t = 0; t < threadCount; t++) {
            threads[t].start();
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
        }

        return lc.get();
    }

    // General parallel solution, using multiple threads
    private static long countParallelNLocal(int range, int threadCount) {
        final ExecutorService pool = new ForkJoinPool();
        final long[] results = new long[threadCount];

        final int perThread = range / threadCount;

        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            final int threadNo = t;
            pool.submit(
                    () -> {
                        int count = 0;
                        for (int i = from; i < to; i++) {
                            if (isPrime(i)) {
                                count++;
                            }
                        }
                        results[threadNo] = count;
                    }
            );
        }

        pool.shutdown();
        try {
            if (pool.awaitTermination(20, TimeUnit.SECONDS)) {
                // System.out.println("All tasks completed");
            } else {
                System.out.println("Timeout elapsed before termination");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long result = 0;
        for (int t = 0; t < threadCount; t++) {
            result += results[t];
        }
        return result;

        // final int perThread = range / threadCount;
        // Thread[] threads = new Thread[threadCount];
        // for (int t=0; t<threadCount; t++) {
        //   final int from = perThread * t, 
        //               to = (t+1==threadCount) ? range : perThread * (t+1); 
        //   final int threadNo = t;
        //   threads[t] = new Thread( ()-> {
        //       long count = 0;
        //       for (int i=from; i<to; i++)
        // 	      if (isPrime(i))
        // 	        count++;
        //     results[threadNo] = count;
        //   });
        // }
        // for (int t=0; t<threadCount; t++) 
        //   threads[t].start();
        //   try {
        //     for (int t=0; t<threadCount; t++) 
        //         threads[t].join();
        //   } catch (InterruptedException exn) { }
    }
}
