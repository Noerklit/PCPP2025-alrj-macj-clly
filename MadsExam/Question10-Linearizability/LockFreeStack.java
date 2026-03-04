// For week 7
// raup@itu.dk * 2023-10-20
package exercises07;

import java.util.concurrent.atomic.AtomicReference;

// Treiber's LockFree Stack (Goetz 15.4 & Herlihy 11.2)
class LockFreeStack<T> {
    AtomicReference<Node<T>> top = new AtomicReference<Node<T>>(); // Initializes to null

    public void push(T value) {
        Node<T> newHead = new Node<T>(value);
        Node<T> oldHead;
        do {
            oldHead      = top.get(); // A1
            newHead.next = oldHead; // A2
        } while (!top.compareAndSet(oldHead,newHead)); // A3 - Linearization point, if it succeeds, element pushed to stack

    }

    public T pop() {
        Node<T> newHead;
        Node<T> oldHead;
        do {
            oldHead = top.get(); // B1 - Linearization point if stack is empty
            if(oldHead == null) { return null; } // B2
            newHead = oldHead.next; //B3
        } while (!top.compareAndSet(oldHead,newHead)); //B4 - Linearization point, if it succeeds, top element popped from stack

        return oldHead.value;
    }

    // class for nodes
    private static class Node<T> {
        public final T value;
        public Node<T> next;

        public Node(T value) {
            this.value = value;
            this.next  = null;
        }
    }
}
