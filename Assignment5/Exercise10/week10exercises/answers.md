# Exercise 10.1
## 10.1.1
With 1 transactions:
\- Test transaction               53402876,0 ns  605912,39
With 10 transactions:
\- Test transaction              534648427,1 ns 3095075,64
With 100 transactions:
\- Test transaction                  5345517945,9 ns 13261961,57

We see that the time of the execution gets 10 times higher every time we add a 0 to the nr. of transactions.
The order of time follows the order of execution.

## 10.1.2
Trying to run the program with the second version of the code we run into a deadlock.

When trying to transfer money from account 1 to 2, then one thread will lock account 1 first.
If another thread wants to transer from account 2 to 1 then it would lock 2 first. Now we have a deadlock where the first thread wants to synchronise account 2 and the other wants to synchronise account 1, but both accounts are already used.

This is prevented with the min and max function, making sure that the account with the lowest id is always aquired first. So that the order of account synchronisation is always the same.

## 10.1.3
```java
  public ThreadsAccountExperimentsMany() {
    final ExecutorService pool = new ForkJoinPool(1);

    for (int i = 0; i < N; i++) {
      accounts[i] = new Account(i);
    }
    for (int i = 0; i < NO_TRANSACTION; i++) {
      long amount = rnd.nextInt(5000) + 100;
      int source = rnd.nextInt(N);
      int target = (source + rnd.nextInt(N - 2) + 1) % N; // make sure target <> source
      pool.submit(() -> doTransaction(new Transaction(amount, accounts[source], accounts[target])));
    }

    private static void doTransaction(Transaction t) {
    System.out.println(t);
    System.out.println("YO");

    t.transfer();
  }
```

