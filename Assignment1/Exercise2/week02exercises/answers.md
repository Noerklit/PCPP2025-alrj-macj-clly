## Exercise 2.1

### 1
```java
private int readers = 0;
    private boolean writer = false;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void readLock() {
        synchronized(lock) {
            while (writer) {
                condition.await();
            }
            readers++;
            
        }
    }

    public void readUnlock() {
        synchronized(lock) {
            readers--;
            if (readers == 0) {
                condition.signalAll();
            }
        }
    }

    public void writeLock() {
        synchronized(lock) {
            while (writer) {
                condition.await();
            }
            writer = true;
            while (readers > 0) {
                condition.await();
            }
        }
    }

    public void writeUnlock() {
        synchronized(lock) {
            writer = false;
            condition.signalAll();
        }
    }
```

### 2
Our solution is fair to writer threads.  The writer can indicate that they are waiting to enter the critical section. They do this by setting the writer flag to true, which they can do unless someone is already writing.

### 3
Yes our implementation contain one condition variable. It is used to ensure that writers and readers are not blocking the lock if it is already in use. Our imlementation uses the methods await() and signalAll()

## Exercise 2.2

### 1
