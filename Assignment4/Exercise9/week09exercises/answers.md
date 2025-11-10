# Exercise 9.1
## 9.1.1
- In Threads work, our standard deviation seems to fluctuate, but does tend towards becoming smaller overall. We have no idea why this is the case (potentially just the operating system doing something, like allocating ressources towards other processes in the system). 
- In thread

## 9.1.2
- OS:   Windows 11; 10.0; amd64
- JVM:  Microsoft; 17.0.12
- CPU:  AMD64 Family 25 Model 80 Stepping 0,  AuthenticAMD; 16 "cores"
- Date: 2025-10-27T09:19:35+0100

### Mark 7 measurements
- Point creation 17.5 ns 0.20 16777216
- Thread's work 1818.8 ns 22.87 262144
- Thread create 666.4 ns 5.47 524288    
- Thread create start 95919.4 ns 766.45 4096
- Thread create start join 195074.4 ns 1358.87       2048
- ai value = 1070732704
- Uncontended lock 5.3 ns 0.03 67108864

The values are plausible because in threads work it uses 1818 ns to perform 1000 "operations", which if divided with 1000, closely resembles the single operation of point creation (noted that point creation is more expensive as we here create a new object as opposed to just incrementing a value).

The data also shows that creating a thread object is more expensive in comparison to creating a point object, which is to be expected.

In the measurements of starting a thread, the value is also around the expected value, albeit on the higher end, as we would expect the average to be more around 75000 thousand, as we saw with the measurements done with Mark 6. But there is a constant overhead in the thread start, which explains why we end up with a value of around 70000-90000 ish. 


## 9.1.3
666.4 ns

Creation of a thread takes 666.4 ns whilst creating and starting takes 95919.4 ns, ergo starting the thread is very expensive compared to creating it. Therefore creating threads for small computations make no sense. 


# Exercise 9.2
## 9.2.1
No surprises here, volatile has to flush to main memory for each write so it is slower.
```java
public static void TestingVolatile() {
    TestVolatile tv = new TestVolatile();
    Benchmark.Mark7("Test volatile increment",
            i -> {
                return tv.vInc();
            });

    Benchmark.Mark7("Test normal increment",
            i -> {
                return tv.inc();
            });
}
```
```
Test volatile increment               5.9 ns       0.01   67108864
Test normal increment                 3.4 ns       0.02  134217728
```

# Exercise 9.3
## 9.3.1
```
# OS:   Mac OS X; 15.6.1; aarch64
# JVM:  Azul Systems, Inc.; 21.0.7
# CPU:  null; 10 "cores"
# Date: 2025-10-27T11:22:54+0100
countSequential                 2582528.7 ns    3993.65        128
countParallelN       1          2657813.1 ns   16737.19        128
countParallelN       2          1797083.4 ns   21912.04        256
countParallelN       3          1430897.5 ns   14329.01        256
countParallelN       4          1318325.3 ns   12574.14        256
countParallelN       5          1514214.3 ns    9697.30        256
countParallelN       6          1564787.8 ns   11221.68        256
countParallelN       7          1608481.6 ns   12566.11        256
countParallelN       8          1633475.5 ns   16532.84        256
countParallelN       9          1646898.9 ns   10118.77        256
countParallelN      10          1671105.9 ns   16471.38        256
countParallelN      11          1680380.2 ns   10835.84        256
countParallelN      12          1695787.3 ns   11787.24        256
countParallelN      13          1709329.3 ns    9926.70        256
countParallelN      14          1725997.0 ns    7189.14        256
countParallelN      15          1742027.9 ns   14878.24        256
countParallelN      16          1752796.9 ns   24439.30        256
countParallelN      17          1760175.4 ns   10506.89        256
countParallelN      18          1771205.5 ns    5806.94        256
countParallelN      19          1791830.1 ns    8451.00        256
countParallelN      20          1800074.8 ns    8756.06        256
countParallelN      21          1817978.3 ns    8593.57        256
countParallelN      22          1828565.3 ns    9645.18        256
countParallelN      23          1845542.0 ns    8158.19        256
countParallelN      24          1861536.1 ns   10402.49        256
countParallelN      25          1874699.6 ns    8546.80        256
countParallelN      26          1888445.7 ns   14037.06        256
countParallelN      27          1899898.1 ns   10715.02        256
countParallelN      28          1915198.9 ns   13392.14        256
countParallelN      29          1927305.7 ns   12158.02        256
countParallelN      30          1937230.5 ns   10867.04        256
countParallelN      31          1947668.0 ns   11485.14        256
countParallelN      32          1959469.3 ns    9275.67        256
```
## 9.3.2
There is no real correlation between the number of threads on the computer and the threads used in the test. We expected there to be a strong correlation i.e. the more threads the better, but this does not seem to be the case.

# Exercise 9.4
## 9.4.1
```java
class PrimeCounter {

    private int count = 0;

    public synchronized void increment() {
        count = count + 1;
    }

    public synchronized int get() {
        return count;
    }

    public synchronized void add(int c) {
        count += c;
    }

    public synchronized void reset() {
        count = 0;
    }
}
```
## 9.4.2
```
Array Size: 5697
# Occurences of ipsum :1430
```
## 9.4.3
```
Test Search                     6259268.2 ns   34940.97         64
```
## 9.4.4
```java
private static int countParallelN(
        String target,
        String[] lineArr,
        PrimeCounter lc,
        int range,
        int threadCount) {
    final int perThread = range / threadCount;
    Thread[] threads = new Thread[threadCount];
    for (int t = 0; t < threadCount; t++) {
        final int from = perThread * t,
                to = (t + 1 == threadCount) ? range : perThread * (t + 1);
        threads[t] = new Thread(() -> {
            search(target, lineArr, from, to, lc);
        });
    }
    for (int t = 0; t < threadCount; t++) {
        threads[t].start();
    }
    try {
        for (int t = 0; t < threadCount; t++) {
            threads[t].join();
        }
    } catch (InterruptedException exn) {
    }
    return lc.get();
}
```
We can verify that it works by running it beside the sequential search.
```
Array Size: 5697
# Occurences of ipsum :1430
# 4 Thread parrallel occurences of ipsum : 1430
# 8 Thread parrallel occurences of ipsum : 1430
```
## 9.4.5
```
# OS:   Mac OS X; 15.6.1; aarch64
# JVM:  Azul Systems, Inc.; 21.0.7
# CPU:  null; 10 "cores"
# Date: 2025-10-27T12:72:224+0100
Array Size: 5697
# Occurences of ipsum :1430
Test Search                     6282171.6 ns   25164.53         64
countParallelN       1          6387533.7 ns   15606.48         64
countParallelN       2          3453727.4 ns   18704.50        128
countParallelN       3          2475697.2 ns   15200.80        128
countParallelN       4          1978866.0 ns   23461.33        128
countParallelN       5          1915664.6 ns   10826.97        256
countParallelN       6          2051472.9 ns   11325.02        128
countParallelN       7          2186433.1 ns   11764.21        128
countParallelN       8          2189692.0 ns    8268.58        128
countParallelN       9          2262122.2 ns   38385.37        128
countParallelN      10          2298177.0 ns    7544.53        128
countParallelN      11          2290836.9 ns    9871.14        128
countParallelN      12          2289186.8 ns   15158.45        128
countParallelN      13          2309551.9 ns   13892.28        128
countParallelN      14          2315018.2 ns   14462.09        128
countParallelN      15          2321421.1 ns    6900.24        128
countParallelN      16          2322801.9 ns    5659.12        128
```

These results match the results that we saw earlier, the lowest time is achieved at 4-5 cores. The computation when run sequentially takes around 6000000 ns, so with the overhead of around 70000 ns when starting a thread, the overhead quickly becomes negliable. Since the computation is so large, it is very beneficial to parallelize the work, as can be seen in the results (time is halved with just two threads). If the computation was much smaller, parallelizing the work on several threads would end up being inefficient, since the cost of the threads would negate any performance gains. 
Furthermore it can be seen that there is an optimal amount of threads spawned, as we can see that from 1 thread to 4 time is almost halved, but afterwards time slowly starts increasing again. 
