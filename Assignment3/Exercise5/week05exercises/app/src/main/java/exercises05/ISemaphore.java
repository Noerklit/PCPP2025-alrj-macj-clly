package exercises05;

public interface ISemaphore {

    public void acquire() throws InterruptedException;

    public void release();
}
