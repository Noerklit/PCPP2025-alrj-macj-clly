package exercises09;
// jst@itu.dk * 2025-08-20

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class TestTimeSearch {

    public static void main(String[] args) {
        new TestTimeSearch();
    }

    public TestTimeSearch() {
        final String filename = "src/main/resources/long-text-file.txt";
        final String target = "ipsum";

        final PrimeCounter lc = new PrimeCounter();  //name is a bit misleading, it is just a counter
        String[] lineArray = readWords(filename);

        System.out.println("Array Size: " + lineArray.length);
        System.out.println("# Occurences of " + target + " :" + search(target, lineArray, 0, lineArray.length, lc));
        lc.reset();
        System.out.println("# 4 Thread parrallel occurences of " + target + " : " + countParallelN(target, lineArray, lc, lineArray.length, 4));
        lc.reset();
        System.out.println("# 8 Thread parrallel occurences of " + target + " : " + countParallelN(target, lineArray, lc, lineArray.length, 8));
        lc.reset();
        Benchmark.Mark7("Test Search", i -> {
            return search(target, lineArray, 0, lineArray.length, lc);
        });

        lc.reset();
        for (int c = 1; c <= 16; c++) {
            final int threadCount = c;
            Benchmark.Mark7(String.format("countParallelN %7d", threadCount),
                    i -> countParallelN(target, lineArray, lc, lineArray.length, threadCount));

        }
    }

    static long search(String x, String[] lineArray, int from, int to, PrimeCounter lc) {
        //Search each line of file
        for (int i = from; i < to; i++) {
            lc.add(linearSearch(x, lineArray[i]));
        }
        //System.out.println("Found: "+lc.get());
        return lc.get();
    }

    static int linearSearch(String x, String line) {
        //Search for occurences of c in line
        String[] arr = line.split(" ");
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if ((arr[i].equals(x))) {
                count++;
            }
        }
        return count;
    }

    public static String[] readWords(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            return reader.lines().toArray(String[]::new);   //will be explained in Week10;
        } catch (IOException exn) {
            return null;
        }
    }

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
}
