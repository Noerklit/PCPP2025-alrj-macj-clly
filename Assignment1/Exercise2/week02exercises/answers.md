## Exercise 2.1

### 1

```java
public class ReadWriteMonitor {

    private int readers = 0;
    private boolean writer = false;

    public synchronized void readLock() throws InterruptedException {
        while (writer) {
            this.wait();
        }
        readers++;
    }

    public synchronized void readUnlock() throws InterruptedException {
        readers--;
        if (readers == 0) {
            this.notifyAll();
        }
    }

    public synchronized void writeLock() throws InterruptedException {
        while (writer) {
            this.wait();
        }
        writer = true;
        while (readers > 0) {
            this.wait();
        }
    }

    public synchronized void writeUnlock() {
        writer = false;
        this.notifyAll();
    }

}
```

### 2

Our solution is fair to writer threads. The writer can indicate that they are
waiting to enter the critical section. They do this by setting the writer flag
to true, which they can do unless someone is already writing.

### 3

Yes our implementation uses a condition variable, but it is the one implicitly
attached to all java objects. It is used to ensure that writers and readers are
not blocking the lock if it is already in use. Our imlementation uses the
methods wait() and notifyAll()

## Exercise 2.2

### 1
We do observe that mi.value is invisible to the t thread, so it keeps looping even though the mi value is not zero.
It can run forever because the main thread sets the mi value in its core register which the t thread cannot access. Therefore to the t thread the mi value still seems to be zero.

### 2