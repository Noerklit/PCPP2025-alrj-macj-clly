# Exercise 7.1
## 7.1.1
Yes the execution is sequentially consistent, because we can move the dequeue that happens in thread B to happen after the enqueue of x in thread A. Here the program order is preserved.
A: --------|q.enq(x)|---------|q.enq(y)|->
B: ------------------|q.deq(x)|---------->
We therefore have the sequential execution:
<q.enq(x),q.deq(x),p.enq(y)>

## 7.1.2
No the execution is not linearizable because to be linearizable the real time order of the execution must be preserved. We look if the linearzation points are happening in sequential order. In this execution, no matter where in the execution the linearization is, they will not be in sequential order.
A: ---------------|q.enq(x)|--|q.enq(y)|->
B: ---|q.deq(x)|------------------------->
The linearization is as shown and therefore not linearizable.
<q.deq(x), q.enq(x), p.enq(y)>

## 7.1.3
Yes the execution is linearizable, because we are able to provide linearzation points that would be executed in sequential order.
A: ---|        q.enq(x)        |-->
B: ------|q.deq(x)|--------------->
The linearization is shown below
<q.enq(x), q.deq(x)>

## 7.1.4
Yes the execution is linearizable because we are able to find linearization points that would be in sequential order. 
Below is shown where we set the linearization points P. Here we can see that the linearization points for enqueue x and y happen before the linearization point for dequeue y.
A: ---|P q.enq(x)|-----|P q.enq(y)|-->
B: --|          q.deq(y)         P |->
We have the linearization
<q.enq(x), p.enq(y), q.deq(y)>

# 7.2
## 7.2.1
```java
public void push(T value) {
        Node<T> newHead = new Node<T>(value);
        Node<T> oldHead;
        do {
            oldHead      = top.get(); // A1
            newHead.next = oldHead; // A2
        } while (!top.compareAndSet(oldHead,newHead)); // A3

    }

    public T pop() {
        Node<T> newHead;
        Node<T> oldHead;
        do {
            oldHead = top.get(); // B1
            if(oldHead == null) { return null; } // B2
            newHead = oldHead.next; //B3
        } while (!top.compareAndSet(oldHead,newHead)); //B4

        return oldHead.value;
    }
```
The linearization point for push is A3 because if successfully executed the element has been pushed to the stack.


The linearization points for pop are B1 & B4.
B1 is because if the stack is empty, the head has been determined to be null, and will therefore return null.
B4 is because if successfully executed the top element will be popped from the stack.

## 7.2.2
```java
@Test
public void TestPushLockFreeStack_ExpectedPushSumToBeActualSum() throws InterruptedException, BrokenBarrierException {
    LockFreeStack<Integer> sut = new LockFreeStack<>();
    int n = 500;
    CyclicBarrier barrier = new CyclicBarrier(n + 1);
    int expected = 0;
    Integer actual = 0;

    for (Integer i = 0; i < n; i++) {
        final Integer tmp = i;
        Thread thread = new Thread(() -> {
            try {
                barrier.await();
                sut.push(tmp);
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
            }
        });
        thread.start();
        expected += i;
    }

    barrier.await();
    barrier.await();

    for (Integer curr = 0; curr != null; curr = sut.pop()) {
        actual += curr;
    }

    assertEquals(expected, actual);

}
```
## 7.2.3
```java
 // TODO: 7.2.3 - Test pop
    @Test
    public void TestPopLockFreeStack_ExpectPopSumToEqualActualSum() throws InterruptedException, BrokenBarrierException {
        LockFreeStack<Integer> sut = new LockFreeStack<>();
        int n = 500;
        CyclicBarrier barrier = new CyclicBarrier(n + 1);
        Integer expected = 0;
        AtomicInteger actual = new AtomicInteger(0);

        for (Integer i = 0; i < n; i++) {
            Thread thread = new Thread(() -> {
                try {
                    barrier.await();
                    Integer val = sut.pop();
                    barrier.await();
                    actual.getAndAdd(val);
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                }
            });
            sut.push(i);
            thread.start();
            expected += i;
        }

        barrier.await();
        barrier.await();
        barrier.await();

        assertEquals(expected, actual.get());
    }
```
## 7.2.4
Yes we cover A3 and B4
# 7.3
## 7.3.1
- `writerTryLock`: wait-free, it performs a CAS operation which guarantees completion at contention
- `writerUnlock`: wait-free, only a single writer can unlock the RW lock.
- `readerTryLock`: lock-free, if it fails, it means that another thread has made progress, i.e. aquired the lock.
- `readerUnlock`: lock-free for the same reason.
