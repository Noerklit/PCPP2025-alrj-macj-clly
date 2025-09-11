// For week 3 
// raup@itu.dk * 2025-09-03

package exercises03;

public class TestVisibility {
    int x;

    public TestVisibility() throws InterruptedException {
        x=0;
        Thread t1 = new Thread(() -> {
                while(x==0) 
                    { /*System.out.println("Hej"); */ /*Do nothing*/}
                System.out.println("t1: x="+x);
        });
        t1.start();
        // Thread.sleep(10);
        x=42;
        System.out.println("Main: x="+x);
    }

    public static void main(String[] args) throws InterruptedException {
        new TestVisibility();
    }
}
