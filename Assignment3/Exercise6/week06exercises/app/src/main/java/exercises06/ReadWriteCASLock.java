// For week 6
// raup@itu.dk * 2024-09-22
package exercises06;

// Very likely you will need some imports here
import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {

    private final AtomicReference<Holders> holders = new AtomicReference<>();

    // TODO: Add necessary field(s) for the class
    public boolean readerTryLock() {
        ReaderList rl = null;
        Holders holder;
        do {
            holder = holders.get();
            if (holder != null) {
                if (holder.isWriterLock()) {
                    return false;
                }
                rl = (ReaderList) holder;
            }
            rl = new ReaderList(rl);
        } while (!holders.compareAndSet(holder, rl));
        return true;
    }

    public void readerUnlock() throws RuntimeException {
        ReaderList newRl;
        Holders holder;
        do {
            holder = holders.get();
            if (holder == null || !holder.isReader()) {
                throw new RuntimeException();
            }
            newRl = ((ReaderList) holder).CloneRemoved();
        } while (!holders.compareAndSet(holder, newRl));
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

    @Override
    public void writerUnlock() throws RuntimeException {
        Holders holder;
        do {
            holder = holders.get();
            if (holder == null || !holder.isWriter()) {
                throw new RuntimeException();
            }
        } while (!holders.compareAndSet(holder, null));
    }

    // Challenging 5.2.7: You may add new methods
    private static abstract class Holders {

        public abstract boolean isReader();

        public abstract boolean isWriter();

        public abstract boolean isReaderList();

        public abstract boolean isWriterLock();
    }

    private static class ReaderList extends Holders {

        private final Thread thread;
        private final ReaderList next;

        // TODO: Constructor
        public ReaderList(ReaderList next) {
            thread = Thread.currentThread();
            this.next = next;
        }

        private ReaderList(Thread thread, ReaderList next) {
            this.thread = thread;
            this.next = next;
        }

        public ReaderList CloneRemoved() {
            Thread t = Thread.currentThread();

            if (thread == t && next == null) {
                return null;
            }
            if (thread == t) {
                return next.CloneRemoved();
            }
            if (next == null) {
                return new ReaderList(thread, null);
            }
            return new ReaderList(thread, next.CloneRemoved());
        }

        // TODO: contains
        // TODO: remove
        @Override
        public boolean isReader() {
            Thread curr = Thread.currentThread();
            if (curr == thread) {
                return true;
            }
            if (next == null) {
                return false;
            }
            return next.isReader();
        }

        @Override
        public boolean isWriter() {
            return !isReader();
        }

        @Override
        public boolean isReaderList() {
            return true;
        }

        @Override
        public boolean isWriterLock() {
            return false;
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

        @Override
        public boolean isReaderList() {
            return false;
        }

        @Override
        public boolean isWriterLock() {
            return true;
        }
    }
}
