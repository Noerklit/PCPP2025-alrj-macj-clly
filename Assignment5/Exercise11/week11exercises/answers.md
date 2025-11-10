# Exercise 11.1

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

# Exercise 11.2

## Exercise 11.2.1

```java
public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      return reader.lines();
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
  }
```

## Exercise 11.2.2

```java
  public static void main(String[] args) {
    String filename = "src/main/resources/english-words.txt";
    // System.out.println(readWords(filename).count());
    System.out.println(
      readWords(filename)
      .limit(100)
      .peek(word -> System.out.println(word))
      .count()
      );

  }
```

## Exercise 11.2.3

```java
  public static void main(String[] args) {
    String filename = "src/main/resources/english-words.txt";

    System.out.println(
      readWords(filename)
      .filter(word -> word.length() >= 22)
      .peek(word -> System.out.println(word))
      .count()
      );
  }
```

number of words with at least 22 letters: 63

## Exercise 11.2.4

```java
  public static void main(String[] args) {
    String filename = "src/main/resources/english-words.txt";

    System.out.println(
      readWords(filename)
      .filter(word -> word.length() >= 22)
      .findAny()
      );
  }
```

Print : Optional[anatomicophysiological]

## Exercise 11.2.5

```java
  public static boolean isPalindrome(String s) {
    String s_reversed = new StringBuilder(s.trim()).reverse().toString();
    return s.trim().equals(s_reversed); 
  }
```

Palindromes : 161

## Exercise 11.2.6

```java
  public static void main(String[] args) {
    String filename = "src/main/resources/english-words.txt";

    System.out.println(
      readWords(filename)
      .parallel()
      .filter(word -> isPalindrome(word))
      .peek(word -> System.out.println(word))
      .count()
      );

  }
```

```java
    Benchmark.Mark7("Sequential", i ->       
    readWords(filename)
      .filter(word -> isPalindrome(word))
      .count());
    
    Benchmark.Mark7("parallel", i ->       
    readWords(filename)
      .parallel()
      .filter(word -> isPalindrome(word))
      .count());
```

Sequential                     13042293,6 ns  104869,04         32
parallel                       11827712,8 ns  151592,29         32

It is possible to observe, here we use Mark7 to observe if parallel is faster than sequential.
It is a bit faster, but not much.
This might be because it is an IO bound function, so there is a limit to how much faster it will go when parallelising.

## Exercise 11.2.7

```java
public static void main(String[] args) {
    String filename = "https://staunstrups.dk/jst/english-words.txt";

    System.out.println(readWordsFromUrl(filename).count());
  }

  public static Stream<String> readWordsFromUrl(String url) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      
      return reader.lines();
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
  }
```

Prints 235883 words.

## Exercise 11.2.8

```java
    DoubleStream ds = 
    readWordsFromUrl(filename)
      .map(word -> word.length())
      .mapToDouble(length -> (double) length);

    DoubleSummaryStatistics stats = ds.summaryStatistics();
    
    System.out.printf("count=%d, min=%g, max=%g, sum=%g, mean=%g%n",
    stats.getCount(), stats.getMin(), stats.getMax(),
    stats.getSum(), stats.getAverage());
```

Prints:
235883
count=235883, min=1,00000, max=24,0000, sum=2,25722e+06, mean=9,56923

# Exercise 11.3

## Exercise 11.3.1

=================================
Using Sequential Stream
=================================
1 main
2 main
3 main
4 main
5 main
6 main
7 main
8 main
9 main
10 main
=================================
Using Parallel Stream
=================================
7 main
6 main
3 ForkJoinPool.commonPool-worker-1
4 ForkJoinPool.commonPool-worker-3
8 ForkJoinPool.commonPool-worker-3
1 ForkJoinPool.commonPool-worker-3
2 ForkJoinPool.commonPool-worker-2
5 ForkJoinPool.commonPool-worker-1
9 main
10 ForkJoinPool.commonPool-worker-6

For a sequential stream the main thread does the work.
In parallel stream we can see it uses 5 threads. The parallel streams uses the Fork an Join pool to handle creating and managing threads.

## Exercise 11.3.2



On Windows computer with 8 cores but 16 logical processors:
=================================
Using Sequential Stream
=================================
1 main
2 main
3 main
4 main
5 main
6 main
7 main
8 main
9 main
10 main
11 main
12 main
13 main
14 main
15 main
16 main
17 main
18 main
19 main
20 main
21 main
22 main
23 main
24 main
25 main
26 main
27 main
28 main
29 main
30 main
=================================
Using Parallel Stream
=================================
20 main
19 main
22 main
21 main
17 main
18 main
16 main
10 ForkJoinPool.commonPool-worker-1
25 ForkJoinPool.commonPool-worker-2
26 ForkJoinPool.commonPool-worker-2
11 ForkJoinPool.commonPool-worker-1
29 ForkJoinPool.commonPool-worker-5
9 ForkJoinPool.commonPool-worker-1
27 ForkJoinPool.commonPool-worker-5
8 ForkJoinPool.commonPool-worker-1
23 ForkJoinPool.commonPool-worker-1
30 ForkJoinPool.commonPool-worker-4
1 ForkJoinPool.commonPool-worker-7
12 ForkJoinPool.commonPool-worker-4
3 ForkJoinPool.commonPool-worker-4
2 ForkJoinPool.commonPool-worker-6
28 main
5 ForkJoinPool.commonPool-worker-3
4 ForkJoinPool.commonPool-worker-6
7 ForkJoinPool.commonPool-worker-13
6 ForkJoinPool.commonPool-worker-3
24 ForkJoinPool.commonPool-worker-2
14 ForkJoinPool.commonPool-worker-5
15 ForkJoinPool.commonPool-worker-10
13 ForkJoinPool.commonPool-worker-1

## Exercise 11.3.3

# Exercise 11.4

## Exericse 11.4.1

## Exericse 11.4.2