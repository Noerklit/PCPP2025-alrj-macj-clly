package exercises09;

public class TestVolatile {

    private volatile int vCtr;
    private int ctr;

    public int vInc() {
        return vCtr++;
    }

    public int inc() {
        return ctr++;
    }
}
