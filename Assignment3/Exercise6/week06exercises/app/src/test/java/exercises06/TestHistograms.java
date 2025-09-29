package exercises06;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
// JUnit testing imports

public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports

    // TODO: 5.1.3
    @RepeatedTest(10)
    @DisplayName("Test cas histogram supports parrallel operations")
    public void TestCasHistogramParallelism_ShouldCountFactorsCorrectly() throws InterruptedException {
        int span = 5000;
        FactorCalc expected = new FactorCalc(new CasHistogram(span), 0, span);
        expected.start();
        expected.join();

        Histogram sut = new Histogram1(span);
        //Histogram sut = new CasHistogram(span);

        List<FactorCalc> cList = new LinkedList<>();

        int nrThreads = 10;
        int sectionSize = span / nrThreads;
        for (int i = 0; i < nrThreads; i++) {
            FactorCalc tmp = new FactorCalc(sut, i * sectionSize, sectionSize * (i + 1));
            cList.add(tmp);
            tmp.start();
        }

        cList.forEach((calc) -> {
            try {
                calc.join();
            } catch (InterruptedException ex) {
                fail(ex.getMessage());
            }
        });

        cList.forEach((calc) -> expected.assertEqualOther(calc));
    }

    // Function to count the numbe of prime factors of a number `p`
    private static int countFactors(int p) {
        if (p < 2) {
            return 0;
        }
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p = p / k;
            } else {
                k = k + 1;
            }
        }
        return factorCount;
    }

    private class FactorCalc extends Thread {

        private final Histogram his;
        private final int start, end;

        public FactorCalc(Histogram his, int start, int end) {
            this.his = his;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                int bin = countFactors(i);
                his.increment(bin);
            }
        }

        public void assertEqualOther(FactorCalc other) {
            for (int i = start; i < end; i++) {
                assertEquals(his.getCount(i), other.his.getCount(i));
            }
        }
    }

}
