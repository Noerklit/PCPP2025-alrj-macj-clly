## Exercise 3.1

### 1
```java
public class CountingThreads {
    int count;

    public CountingThreads() throws InterruptedException {
        count = 0; //(init(count))
        
        CountingThread t1 = new CountingThread(); 
        CountingThread t2 = new CountingThread();
         
        t1.start(); // (start(t1)) po
        t2.start(); // (start(t2)) po

        t1.join(); // (join(t1)) po
        t2.join(); // (join(t2)) po
        
        System.out.println("count="+count); // (3) Variable access/read
    }

    public class CountingThread extends Thread {
        public void run() {
            int temp = count; //(1) Variable access/read
            count = temp + 1; //(2) Variable access/write    
        }
    }


    public static void main(String[] args) throws InterruptedException {
        new CountingThreads();
    }
}
```

### 2
$$
HB^m_{po} = m(init(count)) \rightarrow m(start(t1)) \\
\rightarrow m(start(t2)) \rightarrow m(join(t1)) \\
\rightarrow m(join(t2)) \rightarrow m(3)
$$

$$
HB^{t1}_{po} = t1(1)  \rightarrow t1(2)
$$

$$
HB^{t2}_{po} = t2(1)  \rightarrow t2(2)
$$

HB1 will result in count = 2
$$
HB^{1} = t1(1)  \rightarrow t1(2) \rightarrow t2(1)  \rightarrow t2(2) 
$$
HB2 will result in count = 2
$$
HB^{2} = t2(1)  \rightarrow t2(2) \rightarrow t1(1)  \rightarrow t1(2)
$$
HB3 will result in count = 1
$$
HB^{3} = t1(1)  \rightarrow t2(1) \rightarrow t1(2)  \rightarrow t2(2)
$$
HB4 will result in count = 1
$$
HB^{4} = t2(1)  \rightarrow t1(1) \rightarrow t2(2)  \rightarrow t1(2)
$$
HB5 will result in count = 1
$$
HB^{5} = t1(1)  \rightarrow t2(1) \rightarrow t2(2)  \rightarrow t1(2)
$$
HB6 will result in count = 1
$$
HB^{6} = t2(1)  \rightarrow t1(1) \rightarrow t1(2)  \rightarrow t2(2)
$$
This set is not the same for all possible program execusions. As can be seen from the example above.

### 3
$$
HB^1_{init} = \\
m(start(t1)) \rightarrow t1(1) \rightarrow t1(2) \rightarrow\\
m(start(t2)) \rightarrow t2(1) \rightarrow t2(2) \\
$$
$$
HB^2_{init} = \\
m(start(t1)) \rightarrow  m(start(t2)) \rightarrow t1(1) \rightarrow t1(2) \rightarrow t2(1) \rightarrow t2(2) \\
$$
$$
HB^1_{ter} = \\
t1(2) \rightarrow m(join(t1)) \rightarrow t2(2) 
\rightarrow m(join(t2))  \\
$$
No, as can be seen in the example above.

### 4
$$
m(start(t1)) \rightarrow  m(start(t2)) \rightarrow m(join(t1))\rightarrow m(join(t2))
$$

### 5
There is a race between t1(1) and t2(2), and t2(1) and t1(2) as they both use count and there is no action pairs relating t1 and t2. As can be seen from the HB relations in the above exercieses.

### 6
```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountingThreads {
    int count;
    Lock lock = new ReentrantLock();
     

    public CountingThreads() throws InterruptedException {
        count = 0; //(init(count))
        
        CountingThread t1 = new CountingThread();
        CountingThread t2 = new CountingThread();
        
        t1.start(); // (start(t1))
        t2.start(); // (start(t2))

        t1.join(); // (join(t1))
        t2.join(); // (join(t2))
        
        System.out.println("count="+count); // (5)
    }

    public class CountingThread extends Thread {
        public void run() {
            lock.lock(); // (1) sync
            int temp = count; // (2)
            count = temp + 1; // (3)
            lock.unlock(); // (4) sync      
        }
    }


    public static void main(String[] args) throws InterruptedException {
        new CountingThreads();
    }
}
```
$$
HB = m(init(count)) \rightarrow m(start(t1)) \\
\rightarrow t1(1) \rightarrow t1(2) \rightarrow t1(3) \rightarrow t1(4) \\
\rightarrow m(start(t2))
\rightarrow t2(1) \rightarrow t2(2) \rightarrow t2(3) \rightarrow t2(4)\\
\rightarrow m(join(t1))
\rightarrow m(join(t2)) \rightarrow m(5)
$$
There is another execution order, that is if t2 aquires the lock before t1.

### 7
$$
HB^1 = t1(2) \xrightarrow{(po)}  t1(3) \xrightarrow{(po)}  t1(4) \\
\xrightarrow{(sync)}  t2(1) \xrightarrow{(po)}  t2(2) \xrightarrow{(po)}  t2(3)
$$
$$
HB^2 = t2(2) \xrightarrow{(po)}  t2(3) \xrightarrow{(po)}  t2(4) \\
\xrightarrow{(sync)}  t1(1) \xrightarrow{(po)}  t1(2) \xrightarrow{(po)}  t1(3)
$$
Due to the monitor rule, no data reces are present as either t1 or t2 has to wait for the other thread to unlock.

### 8
If count is volatile it would not be data race free. Each thread would still be able to read count at the same time.
