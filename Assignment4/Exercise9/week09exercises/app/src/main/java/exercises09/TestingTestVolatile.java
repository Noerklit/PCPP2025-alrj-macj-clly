package main.java.exercises09;

import benchmarking.Benchmark;

public class TestingTestVolatile {

    public class TestVolatile {
        private volatile int vCtr;
        private int ctr;

        public int vInc() {
            return vCtr++;
        }

        public int inc() {
            return ctr++;
        }
    }

    public static void main(String[] args) {
        TestingVolatile();
    }

    public static void TestingVolatile() {
        final TestVolatile tv = new TestVolatile();
        Benchmark.Mark7("Test volatile increment",
                i -> {
                    return tv.vInc();
                });

        Benchmark.Mark7("Test normal increment",
                i -> {
                    return tv.inc();
                });
    }
}
