// For week 2
// raup@itu.dk * 01/09/2021
package exercises02;

public class ReadersWriters {

    public ReadersWriters() {
        ReadWriteMonitor m = new ReadWriteMonitor();

        final int numReadersWriters = 10;

        for (int i = 0; i < numReadersWriters; i++) {

            // start a reader
            new Thread(() -> {

                try {
                    m.readLock();
                    System.out.println(" Reader " + Thread.currentThread().getId() + " started reading");
                    // read
                    System.out.println(" Reader " + Thread.currentThread().getId() + " stopped reading");
                    m.readUnlock();
                } catch (InterruptedException ex) {
                }

            }).start();

            // start a writer
            new Thread(() -> {
                try {
                    m.writeLock();
                    System.out.println(" Writer " + Thread.currentThread().getId() + " started writing");
                    // write
                    System.out.println(" Writer " + Thread.currentThread().getId() + " stopped writing");
                    m.writeUnlock();
                } catch (InterruptedException ex) {
                }
            }).start();

        }
    }

    public static void main(String[] args) {
        new ReadersWriters();
    }

}
