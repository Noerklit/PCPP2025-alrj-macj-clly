// For week 1
// alrj@itu.dk * 2025-25-08
// macj@itu.dk * 2025-25-08
// clly@itu.dk * 2025-25-08

package exercises01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Printer {
    Lock lock = new ReentrantLock();

    public void print () {
        lock.lock();
        System.out.print("-");
        try {
            Thread.sleep(50);
        } catch (InterruptedException exn) {}
        System.out.print("|");
        lock.unlock();
    }

    public static void main(String[] args) {
        var printer = new Printer();
        
        var t1 = new Thread(() -> {
            while (true) {
                printer.print();
            }
        });
        var t2 = new Thread(() -> {
            while (true) {
                printer.print();
            }
        });

        t1.start();t2.start();
        try { t1.join(); t2.join(); }
        catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }
    }
}
