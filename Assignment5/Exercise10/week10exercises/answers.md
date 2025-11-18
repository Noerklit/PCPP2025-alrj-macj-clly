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
We expected that using executors would make it faster, but here it is clearly not faster.
We probably need to change implemtation.

insert code

ask TA/Albert \
countSequential                 2618662,9 ns   22301,92        128\
countParallelN  1               2692522,0 ns   14464,83        128\
countParallelNLocal  1          2702471,4 ns   10655,69        128\
countParallelN  2               1846013,3 ns   19001,39        256\
countParallelNLocal  2          1772262,3 ns    4884,55        256\
countParallelN  4               1371633,1 ns   10373,22        256\
countParallelNLocal  4          1176240,6 ns    8402,95        256\
countParallelN  8               1251309,3 ns   23894,60        256\
countParallelNLocal  8          1011713,8 ns   12482,61        256\
countParallelN 16               1128675,0 ns   17671,69        256\
countParallelNLocal 16           894046,1 ns    9447,17        512

# 10.4
## 10.4.1

Is this how we are supposed to implement it?
Insert code.

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