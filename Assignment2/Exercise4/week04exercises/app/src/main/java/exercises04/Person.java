package exercises04;

import java.util.concurrent.atomic.AtomicLong;

public class Person {

    private static volatile AtomicLong next = null;

    public final long id;
    private volatile String name;
    private volatile int zip;
    private volatile String address;

    public Person() {
        if (next == null) {
            next = new AtomicLong(0);
        }
        this.id = next.getAndIncrement();
    }

    public Person(long id) {
        if (next == null) {
            next = new AtomicLong(id);
        }
        this.id = next.getAndIncrement();
    }

    public synchronized void UpdateAddress(int zip, String address) {
        this.address = address;
        this.zip = zip;
    }

    public synchronized void UpdateName(String name) {
        this.name = name;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized String getAddress() {
        return address;
    }

    public synchronized int getZip() {
        return zip;
    }
}
