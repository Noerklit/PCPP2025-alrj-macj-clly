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
