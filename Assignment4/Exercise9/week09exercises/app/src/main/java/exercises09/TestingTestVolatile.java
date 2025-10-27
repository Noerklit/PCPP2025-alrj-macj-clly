package exercises09;

import benchmarking.Benchmark;

public class TestingTestVolatile {

    public static void main(String[] args) {
        TestingVolatile();
    }

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
}
