## Exercise 1.1

### 1
The values outputted are not the expected output of 20 million, because of thread interleaving, as both threads access the LongCounter concurrently, resulting in a data race, where threads sometimes get stale data. We get an output: Count is 19752675 and should be 20000000, but the number given is non-deterministic and would be in a range below 20 million.

### 2
The issue explained in the answer above is still present, the likelyhood of it happening is just a lot lower. This is because the higher the count number is, the more accesses and writes to the critical section are required, this results in a higher risk of a data race occurring. So while we will mostly receive the correct value of 200, it is not guaranteed, as there is still a chance for a data race happening. 

### 3
This will not make any differences in the result. This is because the different forms of writing, all result in the same java bytecode. The instructions the machine performs are the same, therefore the same problem will persist. 

### 4
In the program, we've identified a critical section, i.e, the increment method. 
By using the ReentrantLock, we ensure that only one thread can access the critical section at a time, making it mutually exclusive, thus mitigating data races.

```java
class LongCounter {
    private long count = 0;

    public void increment() {
        lock.lock();
        count = count + 1;
        lock.unlock();
    }

    public long get() {
        return count;
    }
}
```

### 5
We've defined our critical section to contain the incrementation of count, i.e the read and write in the increment method, which is the least amount of lines of code that the critical section should contain. We have excludeded the get method from our critical section, as it is not accessed by any of the threads spawned, only by the main thread. 


## Exercise 1.2

### 1
Program is located in Printer.java

### 2
```java
class Printer {
    public void print () {
        System.out.print("-"); // p1
        try {
            Thread.sleep(50); // p2
        } catch (InterruptedException exn) {}
        System.out.print("|"); // p3
    }

    public static void main(String[] args) {
        var printer = new Printer();
        
        var t1 = new Thread(() -> {
            while (true) {
                printer.print();
            }
        });
        var t2 = new Thread(() -> {
            while (true) {
                printer.print();
            }
        });

        t1.start();t2.start();
        try { t1.join(); t2.join(); }
        catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }
    }
}
```

```
t1(p1),t2(p3),t1(p2),t1(p3),t2(p1)
```
In the interleaving provided above we show a situation, wherein a race condition happens. Thread 1 prints a dash, then thread 2 prints a pipe, thread 1 sleeps, but then thread 1 prints a pipe, before thread 2 prints the dash, ruining the order. 

### 3
```java
class Printer {
    Lock lock = new ReentrantLock();

    public void print () {
        lock.lock();
        System.out.print("-");
        try {
            Thread.sleep(50);
        } catch (InterruptedException exn) {}
        System.out.print("|");
        lock.unlock();
    }

    public static void main(String[] args) {
        var printer = new Printer();
        
        var t1 = new Thread(() -> {
            while (true) {
                printer.print();
            }
        });
        var t2 = new Thread(() -> {
            while (true) {
                printer.print();
            }
        });

        t1.start();t2.start();
        try { t1.join(); t2.join(); }
        catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }
    }
}
```
The solution provided above ensures the output is the correct pattern, by using a lock to ensure only one thread accesses the critical section at a time. This mitigates the race condition wherein both of our threads executes prints at the same time. The program above ensures that each thread locks the critical section, then prints a dash and a pipe, before unlocking the critical section, so the next thread can access it, ensuring prints are executed in the correct order (sequentially).


### 4 (not finished)
t1(p1),t2(p1),t1(p2),t2(p2),t1(p3),t1(p1),t2(p3),t1(p2),t2(p1),t2(p2),t1(p3),t2(p)


## Exercise 1.3
### 1
```java
public class CounterThreads2Covid {

    Lock lock = new ReentrantLock();
    long counter = 0;
    final long PEOPLE  = 10_000;
    final long MAX_PEOPLE_COVID = 15_000;

    public CounterThreads2Covid() {
        try {
            Turnstile turnstile1 = new Turnstile();
            Turnstile turnstile2 = new Turnstile();

            turnstile1.start();turnstile2.start();
            turnstile1.join();turnstile2.join();

            System.out.println(counter+" people entered");
        }
        catch (InterruptedException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CounterThreads2Covid();
    }


    public class Turnstile extends Thread {
        public void run() {
            for (int i = 0; i < PEOPLE; i++) {
                lock.lock();
                if (counter < MAX_PEOPLE_COVID) {
                    counter++;
                }
                lock.unlock();
            }
        }
    }
}
```
In the code above, we have identified the critical section, utilizing our lock to ensure no data races happen. Only one thread can access the critical section, i.e, reading and writing, ensuring the counter always reaches 15000 exactly. 


## Exercise 1.4
### 1
There are subtle differences between Resource Utilization and Inherent concurrency. As described in the notes inherent concurrency only concerns allowing the same process to do some other stuff, while resource utlization, as described in Goetz, allows any other program to use the free resources.

So if we allocate a thread to draw to the screen and another to perform backend operations it is considered Inherent concurrency, but, if we utilize the async/await keywords we allow the OS to use our resources for other programs while waiting. E.g. Resource Utilization is a combination of Inherent and Hidden Concurrency.

### 2
- Exploitation
    Metro system, there are multiple doors to enter the metro not just one, they also need to communicate with the doors on the platform.
- Inherent 
    GUIs such as web applications
- Hidden
    Operating systems