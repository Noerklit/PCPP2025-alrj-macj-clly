## Exercise 4.1
### 1
```java
package exercises04;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<T> implements BoundedBufferInteface<T> {

    private final Queue<T> q = new LinkedList<>();
    private final Semaphore size, items, lock;

    public BoundedBuffer(int size) {
        this.size = new Semaphore(size);
        this.items = new Semaphore(0);
        this.lock = new Semaphore(1);
    }

    private <K> K doLocked(Callable<K> callable) throws Exception {
        lock.acquire();
        try {
            return callable.call();
        } finally {
            lock.release();
        }
    }

    public boolean isEmpty() throws Exception {
        return doLocked(() -> items.availablePermits() == 0);
    }

    @Override
    public void insert(T elem) throws Exception {
        size.acquire();
        doLocked(()-> q.add(elem));
        items.release();
    }

    @Override
    public T take() throws Exception {
        items.acquire();
        var ret = doLocked(() -> q.remove());
        size.release();
        return ret;
    }
}
```

### 2
#### Class state
We have 4 fields containing the class state.
- q: The queue
- size: The amount of space left in the BoundedBuffer
- items: The amount of items in the BoundedBuffer
- lock: A Semaphore to be used as a lock (limited to 1 permit)

#### Escaping
All fields are private and final, thus ensuring that they can only be accessed through public methods. Furthermore no methods return references to objects in the class state. 

#### (Safe) publication
We utilize the final keyword to ensure that there exists a happen-before relation when initializing the BoundedBuffer. 

#### Immutability
Whilst the objects themselves do change state after initialization, the use of final ensures that the reference to the objects does not change after initialization. 

#### Mutual exclusion
To ensure Mutual Exclusion, we have used a single permit Semaphore, that acts as a lock for performing operations on the queue. This is done by wrapping any operations on the queue, in the doLocked method, which simply aquires the 1 permit from the Semaphore, then performs the action, and finally releases the permit back to Semaphore. 


### 3
It is not possible, since barrier fundamentally aims to do something differently. A barrier's purpose is to wait for all threads to reach a certain state, and then releasing them all to perform a certain action. Therefore we cannot use barrier to implement a lock, as the barrier would block all threads from the critical section, until they are all ready to enter it, and then the barrier would allow all threads to enter the critical section at the same time, which is the opposite of what the lock should do. 
Barrier does not allow us to control the amount of threads gaining access, so we cannot use it to implement either a lock or a Semaphore. 

## Exercise 4.2
### 1
