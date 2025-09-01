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
It can run forever because the main thread sets the mi value in its core register or one of its own caches, which the t thread cannot access. Therefore to the t thread the mi value still seems to be zero.

### 2
```java
class MutableInteger {
    private int value = 0;
    public synchronized void set(int value) throws InterruptedException {
        this.value = value;
    }
    public synchronized int get() throws InterruptedException {
        return value;
    }
}
```
Our solution prevents thread t from running forever, because we use intrisic locks accessed by the synchronized keyword. 
Intrisic locks ensure that our variables are synchronized across our threads.

### 3
If get () is not synchronized thread t will not always terminate. This is because when thread t tries to read from value, it reads from its own cache. 


### 4
```java
class MutableInteger {
    // WARNING: Not ready for usage by concurrent programs
    private volatile int value = 0;
    public  void set(int value) throws InterruptedException {
        this.value = value;
    }
    public  int get() throws InterruptedException {
        return value;
    }
}
```
With the code shown above, thread t will always terminate, as the volatile keyword, forces a flush of the caches to main memory. This does not mean that the program is thread safe, but updates to the volatile value, will always be seen by all threads. 


## Exercise 2.3

### 1
- Sum is 1593584.000000 and should be 2000000.000000
- Sum is 1481195.000000 and should be 2000000.000000
- Sum is 1518468.000000 and should be 2000000.000000
Yes there is a race condition in the code, this is because they are not using the same lock.

### 2
The race condition appears, because t1 uses a non static method, and t2 uses a static method. This means they are not using the same class lock.

### 3 
```java
class NewMystery {
    private static double sum = 0;
    private static Object o = new Object();

    public static void addStatic(double x) {
        synchronized (o) {
            sum += x;
        }
    }

    public void addInstance(double x) {
        synchronized (o) {
            sum += x;
        }
    }

    public static double sum() {
        synchronized(o) {
            return sum;
        }
    }
}
```
Our new implementation does not have any race conditions, because we are utilizing the same object when locking, therefore all methods now use the same lock.


### 4
This intrisic lock is not necessary, because the threads are joined before we invoke the sum method. 
