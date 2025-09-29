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

The set is the same, but the outcome is different. The sets are the same as seen in the first part of the assignment.

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

The set is the same for all possible program executions.
$$
HB = {m(start(t1)) \rightarrow  m(start(t2)) \rightarrow m(join(t1))\rightarrow m(join(t2)) }
$$

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
If count is volatile it would be data race free, but not race condition free. Each thread would still be able to read count at the same time. The threads can read count and write to the temp value at the same time. If there has been written to count volatile ensures us we get the right value of count.
$$
HB = t1(1)  \rightarrow t2(1) \rightarrow t1(2)  \rightarrow t2(2)
$$

## 3.2

### 1
```java
    public class StringSet {

        private final List<String> list = new ArrayList<String>(); // (init(list))

        public synchronized int findOrAdd(String s) { 
            int ret = list.indexOf(s); // (init(ret))
            if (ret == -1) {      // 1
                list.add(s); // 2
            }
            return ret; // 3
        }

        public int find(String s) {
            return list.indexOf(s); // 4
        }
    }

    s = new StringSet(); // init(s)

        Thread t1 = new Thread(() -> {
                s.findOrAdd("PCPP"); // 5
        });
        Thread t2 = new Thread(() -> {
                s.find("PCPP"); // 6
        });

        t1.start(); //
        t2.start(); //
```
$$
HB^m_{po} = m(init(s)) \rightarrow m(start(t1)) \rightarrow m(start(t2)) \\

HB^{t1}_{po} = t1(5) \rightarrow t1(init(ret)) \rightarrow t1(1) \rightarrow t1(2) \rightarrow t1(3) \\

HB^{t2}_{po} = t2(6) \rightarrow t2(4)
$$
The above denotes the happens before for the program and its threads. There is a data race between t1(2) and t2(4), since there are no action pairs that relate them, ergo there is not Happens-Before set that contains them both. That means they are conflicting actions.

### 2
```java
    public class StringSet {

        private final List<String> list = new ArrayList<String>(); // (init(list))

        public synchronized int findOrAdd(String s) { 
            int ret = list.indexOf(s); // (init(ret))
            if (ret == -1) {      // 1
                list.add(s); // 2
            }
            return ret; // 3
        }

        public synchronized int find(String s) {
            return list.indexOf(s); // 4
        }
    }

    s = new StringSet(); // init(s)

        Thread t1 = new Thread(() -> {
                s.findOrAdd("PCPP"); // 5
        });
        Thread t2 = new Thread(() -> {
                s.find("PCPP"); // 6
        });

        t1.start(); //
        t2.start(); //
```
$$
HB = m(init(s)) \rightarrow m(start(t1)) \rightarrow m(start(t2)) \rightarrow \\ 
t1(5) \rightarrow t1(init(ret) ) \rightarrow t1(1) \rightarrow t1(2) \rightarrow t1(3) \\ 
\rightarrow t2(6) \rightarrow t2(4) \\
$$

The class is correctly synchronized, becasue to be correctly syncronized means that the program has to be data race free. With both methods being synchronized the program is data race free. Beceause we are using an intrinsic lock which means that both find or findOrAdd cannot run at the same time. Because of the introduction of the synchronized keyword, i.e lock to the find method, the monitor rule applies, results in us not having the conflicting action pair t1(2) and t2(4) no longer occurring at the same time, eliminating the data race. Note that a race condition is still present, in terms of which threads, reaches the lock first. 


## 3.3
### 1
``` Java
    int x; // init(x)

    public TestVisibility() throws InterruptedException {
        x=0; // 1
        Thread t1 = new Thread(() -> {
                while(x==0) // 2
                    {/*Do nothing*/} // 3
                System.out.println("t1: x="+x); // 4
        });
        t1.start(); // start(t1)
        x=42; // 5
        System.out.println("Main: x="+x); // 6
    }
```
Ask TA's about whether it is the first option or the second. Our logic goes like this: We have to reorder the spawning of the thread and the write to x, as we otherwise have a situation, wherein the main thread writes to its local cache, never notifying t1 of the updated value, resulting in t1 reading a stale value of x, thus running forever. So should the reordering be of the pair m(5) and start(t1) or the pair m(5) t1(2)?

The pair of actions that must be ordered by the happens-before is m(5) and t1(2). m(5) (the write to x) must happen before t1(2) (the read to x), in order to prevent the infinite loop.

The pair of actions that must be ordered by the happens-before is m(5) and start(t1). m(5) (the write to x) must happen before start(t1) (Spawning of t1), in order to prevent the infinite loop.

### 2
- Program order rule: As action t1(2) and action m(5) are not executed by the same thread, this program does adhere to the program order rule. 
- Monitor rule: No locks exists in the program, therefore it does not adhere to the monitor rule.
- Volatile rule: The variable x is not marked as volatile, this program does not adhere to the volatile rule.
- Default init rule: Our action a in this case is x=42, but for the init rule to apply this would have to be int x;
- Final field init rule: Same reasoning as for the default init rule. 
- Thread start rule: Action a is not a call to start, therefore the program does not adhere.
- Thread termination rule: action b is not a call to join, such a call does not exist in the program, therefore the program does not adhere to this rule.
- Transitive rule: This does not apply, since our actions are not in the same happens-before. 

Since none of the happens-before rules apply, we can conclude that our pair of actions are not ordered by happens-before in all executions of the program.
