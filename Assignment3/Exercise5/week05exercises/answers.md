## Exercise 5.1
### 1
```java
@RepeatedTest(100)
    @DisplayName("Add with threads")
    public void testingAddWithThreads() throws Exception {
        final int nrThreads = 2;
        final int N = 10000;

        barrier = new CyclicBarrier(nrThreads + 1);

        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int i = 0; i < N; i++) {
                        set.add(i);
                    }
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        barrier.await();
        barrier.await();
        assertEquals(N, set.size());
    }
```
This test fails, since we cannot be sure that the set size is always the same. 
There is an interleaving where two threads can end up adding the same element, and therefore the size will be incremented by two, where it should only be incremented by one. 

### 2
```java
@RepeatedTest(10)
    @DisplayName("Remove with threads")
    public void testingRemoveWithThreads() throws Exception {
        final int nrThreads = 2;
        final int N = 10000;

        barrier = new CyclicBarrier(nrThreads + 1);

        for (int i = 0; i < N; i++) {
            set.add(i);
        }

        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int i = 0; i < N; i++) {
                        set.remove(i);
                    }
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        barrier.await();
        barrier.await();
        assertEquals(0, set.size());
    }
```
This test finds the interleaving wherin both threads try to remove the same element at the same time. Therefore both threads decrease size by one, meaning that the size is decremented by two for one element, resulting in size being negative in some test cases. We see data race issues. If one thread is slower than the other it will read a stale size, and end up overriding with a larger size than the actual size, meaning that even though all elements are removed, the size could still be larger than zero.

### 3
```java
class ConcurrentIntegerSetSync implements ConcurrentIntegerSet {
    final private Set<Integer> set;

    public ConcurrentIntegerSetSync() {
        this.set = new HashSet<Integer>();
    }

    public synchronized boolean add(Integer element) {
        return set.add(element);
    }

    public synchronized boolean remove(Integer element) {
        return set.remove(element);
    }

    public synchronized int size() {
        return set.size();
    }
}
```
We have added the synchronized keyword to the methods, to ensure mutual exclusion for each of the methods. This is the same as the syncronized wrappers Java provides. Meaning that instead of using synchronized keyword we could also use the SynchronizedSet instead of HashSet.

### 4
All tests pass when running them on the ConcurrentIntegerSetSync. This is expected, because ConcurrentIntegerSetSync implements ConcurrentSkipListSet, which is a threadsafe collection. 

### 5 
Yes. As we know the intented functionality of sets, we can safely assume errors in the tests, are caused by the concurrent usage of the set, via the add and remove methods.

### 6
No. First and foremost a passing test is not equal to all interleavings explored. Furthermore since we are not testing all methods the set has, we can not say anything about its thread safety with certainty. Unit testing does not provide proof of correctness (but can be used to prove incorrectness, so what is done in the question above).

## Exercise 5.2
### 1
If release is called more times than aquire, which could happen if for example one thread calls release before any other thread has called aquire, then state is decremented to be below 0, and we can then call aquire more than once, resulting in more threads being given access to the critical section, than whatever the capacity is set to. 

### 2


