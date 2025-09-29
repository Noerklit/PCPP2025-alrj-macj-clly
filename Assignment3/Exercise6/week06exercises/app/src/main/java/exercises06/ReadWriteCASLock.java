// For week 6
// raup@itu.dk * 2024-09-22
package exercises06;

// Very likely you will need some imports here
import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {

    private final AtomicReference<Holders> holders = new AtomicReference<>();

    // TODO: Add necessary field(s) for the class
    public boolean readerTryLock() {
        // TODO 5.2.3
        return true;
    }

    public void readerUnlock() {
        // TODO 5.2.4
    }

    public boolean writerTryLock() {
        Holders writer = new Writer();
        do {
            if (holders.get() != null) {
                return false;
            }
        } while (!holders.compareAndSet(null, writer));
        return true;
    }

    public void writerUnlock() {
        Holders holder;
        do {
            holder = holders.get();
            if (holder.isReader()) {
                throw new RuntimeException();
            }
        } while (!holders.compareAndSet(holder,));
    }

    // Challenging 5.2.7: You may add new methods
    private static abstract class Holders {

        public abstract boolean isReader();

        public abstract boolean isWriter();
    }

    private static class ReaderList extends Holders {

        private final Thread thread;
        private final ReaderList next;

        // TODO: Constructor
        public ReaderList(ReaderList next) {
            thread = Thread.currentThread();
            this.next = next;
        }

        // TODO: contains
        // TODO: remove
        @Override
        public boolean isReader() {
            Thread curr = Thread.currentThread();
            if (curr == thread) {
                return true;
            }
            return next.isReader();
        }

        @Override
        public boolean isWriter() {
            return !isReader();
        }
    }

    private static class Writer extends Holders {

        public final Thread thread;

        public Writer() {
            thread = Thread.currentThread();
        }

        @Override
        public boolean isReader() {
            return !isWriter();
        }

        @Override
        public boolean isWriter() {
            return thread == Thread.currentThread();
        }
    }
}
