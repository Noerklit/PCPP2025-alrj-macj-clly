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
