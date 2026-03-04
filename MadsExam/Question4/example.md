## Exercise 3.1

### 1
```java
public class CountingThreads {
    int count;

    public CountingThreads() throws InterruptedException {
        count = 0; //(init(count))
        
        CountingThread t1 = new CountingThread(); 
        CountingThread t2 = new CountingThread();
         
        t1.start(); // (start(t1)) sync
        t2.start(); // (start(t2)) sync

        t1.join(); // (join(t1)) sync
        t2.join(); // (join(t2)) sync
        
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
#### Define the happens-before order set containing action pairs obtained from the program order rule.
$$
HB^m_{po} = \{m(init(count)) \rightarrow m(start(t1)) \\
\rightarrow m(start(t2)) \rightarrow m(join(t1)) \\
\rightarrow m(join(t2)) \rightarrow m(3)\}
$$

$$
HB^{t1}_{po} = \{t1(1)  \rightarrow t1(2)\}
$$

$$
HB^{t2}_{po} = \{t2(1)  \rightarrow t2(2)\}
$$

$$
HB_{po}= HB^m_{po}\cup HB^{t1}_{po}\cup HB^{t2}_{po}
$$
#### Is this set the same for all possible program executions?
Yes the set is the same as all actions pairs have been obtained using the program order rule.

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

The set is the same, but the outcome is different. The sets are the same as seen in the first part of the assignment.

### 3
#### Define the happens-before order set containing action pairs obtained from the thread start rule and the thread termination rule.
$$
HB^{t1}_{sync}=\{
m(start(t1)) \rightarrow t1(1),
t1(2) \rightarrow m(join(t1))
\}
$$
$$
HB^{t2}_{sync}=\{
m(start(t2)) \rightarrow t2(1),
t2(2) \rightarrow m(join(t2))
\}
$$
$$
HB_{sync}= HB^{t1}_{sync}\cup HB^{t2}_{sync}
$$
#### Is this set the same for all possible program executions?
Yes the set derived from the thread start and termination rule remains the same for all program executions.

The set is the same for all possible program executions.
$$
HB = {m(start(t1)) \rightarrow  m(start(t2)) \rightarrow m(join(t1))\rightarrow m(join(t2)) }
$$

### 4 - Define a set of all possible synchronization orders for this program

$$
S=\{\\
    (
        m(start(t1)),
        m(start(t2)),
        t1(1),
        t1(2),
        t2(1),
        t2(2),
        m(join(t1)),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        m(start(t2)),
        t2(1),
        t2(2),
        t1(1),
        t1(2),
        m(join(t1)),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        m(start(t2)),
        t2(1),
        t1(1),
        t2(2),
        t1(2),
        m(join(t1)),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        m(start(t2)),
        t1(1),
        t2(1),
        t1(2),
        t2(2),
        m(join(t1)),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        m(start(t2)),
        t1(1),
        t2(1),
        t1(2),
        m(join(t1)),
        t2(2),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        m(start(t2)),
        t1(1),
        t1(2),
        t2(1),
        m(join(t1)),
        t2(2),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        m(start(t2)),
        t1(1),
        t1(2),
        m(join(t1)),
        t2(1),
        t2(2),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        t1(1),
        m(start(t2)),
        t1(2),
        m(join(t1)),
        t2(1),
        t2(2),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        t1(1),
        t1(2),
        m(start(t2)),
        m(join(t1)),
        t2(1),
        t2(2),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        t1(1),
        t1(2),
        m(start(t2)),
        t2(1),
        m(join(t1)),
        t2(2),
        m(join(t2))
    ),\\
    (
        m(start(t1)),
        t1(1),
        t1(2),
        m(start(t2)),
        t2(1),
        t2(2),
        m(join(t1)),
        m(join(t2))
    ),\\
\}
$$

### 5 - Show, using the Java memory model, that this program contains data races.
There is a data race between t1(1) and t2(2), and t2(1) and t1(2) as they both use count and there is no action pairs relating t1 and t2. As can be seen from the HB relations in the above exercieses.

### 6 - Use a lock to eliminate the data races in the program
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

Since the threads race to aquire the lock there are a couple of synchronization orders, but no race conditions nor data races, as
1. Concurrent access to count is no longer allowed.
2. The output of the program does not depend on the interleavings.

### 7 - Show, using the Java memory model, that your updated program (3.1.6) does not contain data races
$$
HB^1 = t1(2) \xrightarrow{(po)}  t1(3) \xrightarrow{(po)}  t1(4) \\
\xrightarrow{(sync)}  t2(1) \xrightarrow{(po)}  t2(2) \xrightarrow{(po)}  t2(3)
$$
$$
HB^2 = t2(2) \xrightarrow{(po)}  t2(3) \xrightarrow{(po)}  t2(4) \\
\xrightarrow{(sync)}  t1(1) \xrightarrow{(po)}  t1(2) \xrightarrow{(po)}  t1(3)
$$
Due to the monitor rule, no data races are present as either t1 or t2 has to wait for the other thread to unlock.

### 8 - Consider again the program without the lock, but defining x as volatile
If count is volatile it would be data race free, but not race condition free. Each thread would still be able to read count at the same time. The threads can read count and write to the temp value at the same time. If there has been written to count volatile ensures us we get the right value of count.
$$
HB = t1(1)  \rightarrow t2(1) \rightarrow t1(2)  \rightarrow t2(2)
$$

More formally, the volatile rule states that any writes to a volatile variable happends before a subsequent read. This means that any execution of this program is ordered by happens before, this would not be the case if volatile was not present.
