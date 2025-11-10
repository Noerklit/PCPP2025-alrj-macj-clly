//Exercise 10.?
//JSt vers Aug 20, 2025

package exercises11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import benchmarking.Benchmark;

public class TestWordStream {
  public static void main(String[] args) {
    // String filename = "src/main/resources/english-words.txt";
    String filename = "https://staunstrups.dk/jst/english-words.txt";

    System.out.println(readWordsFromUrl(filename).count());

    // System.out.println(
    //   readWords(filename)
    //   .limit(100)
    //   .peek(word -> System.out.println(word))
    //   .count()
    //   );

    // System.out.println(
    //   readWords(filename)
    //   .filter(word -> word.length() >= 22)
    //   .peek(word -> System.out.println(word))
    //   .count()
    //   );

    // System.out.println(
    //   readWords(filename)
    //   .filter(word -> word.length() >= 22)
    //   .findAny()
    //   );

    // System.out.println(
    //   readWords(filename)
    //   .filter(word -> isPalindrome(word))
    //   .peek(word -> System.out.println(word))
    //   .count()
    //   );

    // System.out.println(
    //   readWords(filename)
    //   .parallel()
    //   .filter(word -> isPalindrome(word))
    //   .peek(word -> System.out.println(word))
    //   .count()
    //   );

    // Benchmark.Mark7("Sequential", i ->       
    // readWords(filename)
    //   .filter(word -> isPalindrome(word))
    //   .count());
    
    // Benchmark.Mark7("parallel", i ->       
    // readWords(filename)
    //   .parallel()
    //   .filter(word -> isPalindrome(word))
    //   .count());

    DoubleStream ds = 
    readWordsFromUrl(filename)
      .map(word -> word.length())
      .mapToDouble(length -> (double) length);

    DoubleSummaryStatistics stats = ds.summaryStatistics();
    
    System.out.printf("count=%d, min=%g, max=%g, sum=%g, mean=%g%n",
    stats.getCount(), stats.getMin(), stats.getMax(),
    stats.getSum(), stats.getAverage());

  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      // TO DO: Implement properly
      
      return reader.lines();
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
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

  public static boolean isPalindrome(String s) {
    // TO DO: Implement properly
    String s_reversed = new StringBuilder(s.trim()).reverse().toString();
    return s.trim().equals(s_reversed); 
  }

  public static Map<Character,Integer> letters(String s) {
    Map<Character,Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    return res;
  }
}
