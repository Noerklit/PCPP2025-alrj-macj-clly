## 11.1.1

Sequential                      2588095,2 ns    7569,21        128
IntStream                             0,9 ns       0,00  268435456
Parallel                              3,4 ns       0,01  134217728
ParallelStream                        3,5 ns       0,01  134217728

## 11.1.2

```java
  private static long countIntStream(int range) {
    return IntStream.range(2, range)
    .filter(i -> isPrime(i))
    .count();
  }
```

Sequential                      2600493,1 ns   30431,94        128
IntStream                       2604502,6 ns    9315,69        128
Parallel                              0,9 ns       0,00  268435456
ParallelStream                        3,4 ns       0,02  134217728

## 11.1.3

```java
  // IntStream solution
  private static long countIntStream(int range) {
    return IntStream.range(2, range)
    .filter(i -> isPrime(i))
    .peek(prime -> System.out.println(prime))
    .count();
  }
```

## 11.1.4

```java
  private static long countParallel(int range) {
    // to be filled out
    return IntStream.range(2, range)
    .parallel()
    .filter(i -> isPrime(i))
    .count();
  }
```

Sequential                      2600767,7 ns   17175,07        128
IntStream                       2603415,4 ns    8011,99        128
Parallel                         555533,0 ns   10432,87        512
ParallelStream                        0,9 ns       0,00  268435456

## 11.1.5

```java
  private static long countparallelStream(List<Integer> list) {
    return list.parallelStream()
    .filter(i -> isPrime(i))
    .count();
  }
```

Sequential                      2599865,4 ns   20197,24        128
IntStream                       2601651,5 ns    7084,39        128
Parallel                         544659,1 ns    5499,08        512
ParallelStream                   548597,3 ns    5245,22        512