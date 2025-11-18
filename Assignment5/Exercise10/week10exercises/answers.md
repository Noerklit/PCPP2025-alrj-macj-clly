# Exercise 10.1
## 10.1.1
With 1 transactions:
  - Test transaction               53402876,0 ns  605912,39 \
With 10 transactions:
  - Test transaction              534648427,1 ns 3095075,64 \
With 100 transactions:
  - Test transaction                  5345517945,9 ns 13261961,57 

We see that the time of the execution gets 10 times higher every time we add a 0 to the nr. of transactions.
The order of time follows the order of execution.

## 10.1.2
Trying to run the program with the second version of the code we run into a deadlock.

When trying to transfer money from account 1 to 2, then one thread will lock account 1 first.
If another thread wants to transer from account 2 to 1 then it would lock 2 first. Now we have a deadlock where the first thread wants to synchronise account 2 and the other wants to synchronise account 1, but both accounts are already used.

This is prevented with the min and max function, making sure that the account with the lowest id is always aquired first. So that the order of account synchronisation is always the same.

## 10.1.3
```java
  public ThreadsAccountExperimentsMany() {
    final ExecutorService pool = new ForkJoinPool(1);

    for (int i = 0; i < N; i++) {
      accounts[i] = new Account(i);
    }
    for (int i = 0; i < NO_TRANSACTION; i++) {
      long amount = rnd.nextInt(5000) + 100;
      int source = rnd.nextInt(N);
      int target = (source + rnd.nextInt(N - 2) + 1) % N; // make sure target <> source
      pool.submit(() -> doTransaction(new Transaction(amount, accounts[source], accounts[target])));
    }

    private static void doTransaction(Transaction t) {
    System.out.println(t);
    System.out.println("YO");

    t.transfer();
  }
```
## 10.1.4
```java
  public ThreadsAccountExperimentsMany() {
    final ExecutorService pool = new ForkJoinPool(1);

    for (int i = 0; i < N; i++) {
      accounts[i] = new Account(i);
    }
    for (int i = 0; i < NO_TRANSACTION; i++) {
      long amount = rnd.nextInt(5000) + 100;
      int source = rnd.nextInt(N);
      int target = (source + rnd.nextInt(N - 2) + 1) % N; // make sure target <> source
      pool.submit(() -> doTransaction(new Transaction(amount, accounts[source], accounts[target])));
    }
    pool.shutdown();
    try {
      if (pool.awaitTermination(20, TimeUnit.SECONDS)) {
        System.out.println("All tasks completed");
      } else {
        System.out.println("Timeout elapsed before termination");
      }
    } catch (InterruptedException e) { e.printStackTrace();}
```

# Exercise 10.2
## 10.2.1

$maxSpeed16Cores = \frac{n log_2(n)}{n + \frac{n}{2} + \frac{n}{4}  + \frac{n}{8}  + (\frac{n}{16} )log_2(\frac{n}{16} )}$

$maxSpeed32Cores = \frac{n log_2(n)}{n + \frac{n}{2} + \frac{n}{4}  + \frac{n}{8}  + \frac{n}{16} + (\frac{n}{32} )log_2(\frac{n}{32})}$

## 10.2.2
$maxSpeed16Cores(1.000.000) = \frac{1.000.000 log_2(1.000.000)}{1.000.000 + \frac{1.000.000}{2} + \frac{1.000.000}{4}  + \frac{1.000.000}{8}  + (\frac{1.000.000}{16} )log_2(\frac{1.000.000}{16} )}\\ ≈6.94$

$maxSpeed32Cores(1.000.000) = \frac{1.000.000 log_2(1.000.000)}{1.000.000 + \frac{1.000.000}{2} + \frac{1.000.000}{4}  + \frac{1.000.000}{8} + \frac{1.000.000}{16} + (\frac{1.000.000}{32} )log_2(\frac{1.000.000}{32} )}\\ ≈ 8.29$

$maxSpeed16Cores(10.000.000) = \frac{10.000.000 log_2(10.000.000)}{10.000.000 + \frac{10.000.000}{2} + \frac{10.000.000}{4}  + \frac{10.000.000}{8}  + (\frac{10.000.000}{16} )log_2(\frac{10.000.000}{16} )}\\ ≈7.55$

$maxSpeed32Cores(10.000.000) = \frac{10.000.000 log_2(10.000.000)}{10.000.000 + \frac{10.000.000}{2} + \frac{10.000.000}{4}  + \frac{10.000.000}{8} + \frac{10.000.000}{16} + (\frac{10.000.000}{32} )log_2(\frac{10.000.000}{32} )}\\ ≈ 9.27$

# 10.3
## 10.3.1
countSequential                 2595369,6 ns    8281,73        128\
countParallelN  1               2696924,0 ns   12523,07        128\
countParallelNLocal  1          2710373,1 ns   25312,25        128\
countParallelN  2               1768428,1 ns   29382,57        256\
countParallelNLocal  2          1805103,7 ns  144621,09        128\
countParallelN  4               1249831,5 ns   11042,38        256\
countParallelNLocal  4          1047731,0 ns   43987,30        256\
countParallelN  8               1057103,7 ns   27318,41        256\
countParallelNLocal  8           876976,3 ns   48823,95        512\
countParallelN 16               1078416,6 ns   32143,98        256\
countParallelNLocal 16           951648,6 ns   38902,43        512\

We can observe that using more threads makes it faster but with diminishing returns. We see that the optimal threads count here is eight threads.
Furthermore we observe that countParallelNLocal is faster than countParallelN when spawning four threads or more.

This is probably becasue countParallelN uses an AtomicLong to keep track of prime numbers, whereas uses a local array. 
In countParallelN the threads have to wait to be able to increment the result, therefore there we believe that the difference in time comes from this waiting.

## 10.3.2
```java
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
    }
}
```

countSequential                         2581502.6 ns    4213.09       128\
countParallelNForkJoining               842467.5 ns     5980.17       512\
countParallelNFixedThreadPool  1        2723337.7 ns    4177.04       128\
countParallelNManualThreads  1          2683605.8 ns    4918.77       128\
countParallelNForkJoinPool  1           2694634.5 ns    5462.66       128\
countParallelNFixedThreadPool  2        1730468.2 ns    10380.73      256\
countParallelNManualThreads  2          1753697.2 ns    4735.94       256\
countParallelNForkJoinPool  2           1783155.5 ns    10551.51      256\
countParallelNFixedThreadPool  4        1117592.0 ns    10461.73      256\
countParallelNManualThreads  4          1294041.3 ns    5003.93       256\
countParallelNForkJoinPool  4           1130500.3 ns    3658.62       256\
countParallelNFixedThreadPool  8        903272.5 ns     7640.69       512\
countParallelNManualThreads  8          985640.8 ns     15157.68      256\
countParallelNForkJoinPool  8           913773.7 ns     4052.37       512\
countParallelNFixedThreadPool 16        952547.5 ns     6028.67       512\
countParallelNManualThreads 16          1071193.3 ns    5098.17       256\
countParallelNForkJoinPool 16           818046.7 ns     7355.97       512

# 10.4
## 10.4.1
```java
package exercises10;
class TestCASLockHistogram {
  public static void main(String[] args) {
    int span = 30;

    // Create an object `histogramCAS` with your Histogram CAS implementation from week05
    Histogram casHis = new CasHistogram(span);

    // Create an object `histogramLock` using the class in the file HistogramLocks
    Histogram lockHis = new HistogramLocks(span);
    
    int noThreads= 16;
    int range= 100_000;	

    for (int i= 1; i < noThreads; i++) {
      int threadCount= i;
      // Benchmark.Mark7( ... using a monitor
        Benchmark.Mark7(String.format("countParallel Lock %2d", threadCount), 
		      p -> countParallel(range, threadCount, lockHis));
    }

    for (int i= 1; i < noThreads; i++) {
      int threadCount= i;
      // Benchmark.Mark7( ... using CAS
      Benchmark.Mark7(String.format("countParallel CAS %2d", threadCount), 
		      p -> countParallel(range, threadCount, casHis));
    }
  }
  
  // Function to count the prime factors of a number `p`
  private static int countFactors(int p) {
    if (p < 2) return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
        factorCount++;
        p= p/k;
      } else 
        k= k+1;
    }
    return factorCount;
  }

  // Parallel execution of counting the number of primes for numbers in `range`
    private static int countParallel(int range, int threadCount, Histogram h) {
    final int perThread= range / threadCount;
    Thread[] threads= new Thread[threadCount];
    for (int t=0; t<threadCount; t= t+1) {
      final int from= perThread * t, 
        to= (t+1==threadCount) ? range : perThread * (t+1); 
      threads[t]= new Thread( () -> {
          for (int i= from; i<to; i++) h.increment(countFactors(i));
                
      });
    } 
    for (int t= 0; t<threadCount; t= t+1) 
      threads[t].start();
    try {
      for (int t= 0; t<threadCount; t= t+1) 
        threads[t].join();
    } catch (InterruptedException exn) { }
    return 0;
  }
    
  // Auxiliary method to print the histogram data
  public static void dump(Histogram histogram) {
    for (int bin= 0; bin < histogram.getSpan(); bin= bin+1) {
      System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
    }
  }
}
```

We would expect CAS to be faster than using locks.

Locks are implemented using CAS. 
There is a high memory overhead in CAS operations, so with more threads the CAS operations get slower because there is higher thread contention.
When using locks we see that the system slows down but quite quickly begins outputting the same time, even if the amount of threads are increased. This is because the lock causes the threads to wait by sleeping, meaning they don't consume CPU, so this is better for high thread contention.


countParallel Lock  1           7608792,3 ns   31113,13         64\
countParallel Lock  2           8342632,6 ns  309878,21         32\
countParallel Lock  3           9212179,8 ns  171533,14         32\
countParallel Lock  4           9958024,5 ns  161250,67         32\
countParallel Lock  5          10098408,2 ns  115647,67         32\
countParallel Lock  6          10204797,8 ns   65267,84         32\
countParallel Lock  7          10266605,7 ns   98214,08         32\
countParallel Lock  8          10298604,3 ns   81994,32         32\
countParallel Lock  9          10315809,4 ns   70381,55         32\
countParallel Lock 10          10459399,6 ns  492089,90         32\
countParallel Lock 11          10611759,9 ns  272155,41         32\
countParallel Lock 12          10586852,1 ns  477390,19         32\
countParallel Lock 13          10431128,9 ns   86528,70         32\
countParallel Lock 14          10433329,3 ns   83498,59         32\
countParallel Lock 15          10453427,0 ns   82588,17         32\
countParallel CAS  1            7436796,8 ns   19977,84         64\
countParallel CAS  2            5195860,6 ns  212481,23         64\
countParallel CAS  3            5736041,6 ns  165116,86         64\
countParallel CAS  4            5401006,8 ns   28979,39         64\
countParallel CAS  5            5634466,1 ns   34706,90         64\
countParallel CAS  6            6524665,9 ns   20169,05         64\
countParallel CAS  7            7270772,5 ns   41922,35         64\
countParallel CAS  8            7680055,5 ns  230526,34         64\
countParallel CAS  9            7896167,8 ns  482498,98         32\
countParallel CAS 10            7320639,1 ns  264800,17         64\
countParallel CAS 11            7479709,4 ns  270381,92         64\
countParallel CAS 12            8618662,0 ns  321213,89         32\
countParallel CAS 13            9021164,6 ns  562892,70         32\
countParallel CAS 14            9664515,1 ns  166319,78         32\
countParallel CAS 15            9808951,8 ns  280468,83         32