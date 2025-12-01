# Exercise 12.1
## 12.1.1
Done

## 12.1.2
```java
public class NewSender {

  private MessageSender ms;
  private int port= 8080;
  private String host= "localhost";

  public static void main(String[] args) {
      new NewSender();
  }

  public NewSender(){
    ms= new MessageSender(host);
    ms.sendMessage("HELLO", port);
    ms.sendMessage("WORLD", port);
    ms.sendMessage("!!!!!!!", port);
    ms.sendMessage(".", port);
    //ms.sendMessage("stop", port);
  }
}
```
hello
world
!
.
HELLO
WORLD
!!!!!!!
.

## 12.1.3
HELLO
WORLD
!!!!!!!
.
hello
world
!
.
hello
world
!
.
HELLO
WORLD
!!!!!!!
.
hello
world
!
.
HELLO
WORLD
!!!!!!!
.
HELLO
WORLD
!!!!!!!
.
hello
world
!
.

The result is not same cause it depends on which of the sender programs compiles and runs first. In theory we could get interleaving messages if we got both sender programs to run in parallel, as UDP has no way to discern one sender from another.


# 12.2
## 12.2.1
```java
public class Convert{

  public String encode(int a1, int a2, int amount) {
    return String.format("%d,%d,%d", a1,a2,amount);
  }

  public int getA1(String mes){
    
    return Integer.parseInt(mes.split("[,]")[0]);
  }

  public int getA2(String mes){
    return Integer.parseInt(mes.split("[,]")[1]);
  }

  public int getAmount(String mes){
    return Integer.parseInt(mes.split("[,]")[2]);
  }
}
```

## 12.2.2
```java
public class Account2{
  private MessageReceiver mr;
  private final int port= 8082;
  private int balance= 0;
  int amount= 0;
  private Convert convert;
  public static void main(String[] args) {
    new Account2();
  }

  public Account2(){
    mr= new MessageReceiver(port);
    convert= new Convert();
    String m= "started";
    do {
      m= mr.receiveMessage();
      amount= Integer.parseInt(m);
      System.out.println("Mes: "+m+" Amount; "+amount);
      balance= balance + amount;
      System.out.println("Account2; "+balance);
    } while (amount != 0); // sending a 0 amount means stop
  }
}
```

## 12.2.3
Bank:
8081,8082,100
8081,8082,-50
8082,8081,17
8081,8082,0

Account1:
Mes: -100 Amount; -100
Account1; -100
Mes: 50 Amount; 50
Account1; -50
Mes: 17 Amount; 17
Account1; -33
Mes: 0 Amount; 0
Account1; -33

Account2:
Mes: 100 Amount; 100
Account2; 100
Mes: -50 Amount; -50
Account2; 50
Mes: -17 Amount; -17
Account2; 33
Mes: 0 Amount; 0
Account2; 33

Based on the transactions made, the math adds up, so it is what we would expect.

# 12.3
## 12.3.1

```erlang
-module(mobile_app).
-include("records.hrl").

-export([start/1, init/1]).

-record(transaction, {from, to, amount}).
-record(mobile_app_state, {bank}).

start(BANK) ->
    InitialState = #mobile_app_state{bank = BANK},
    spawn(?MODULE, init, [InitialState]).

init(InitialState) ->
    loop(InitialState).

loop(State) ->
    receive
        {payment_request, Request} -> 
            From = Request#payment_request.from,
            To = Request#payment_request.to,
            Amount = Request#payment_request.amount,
            State#mobile_app_state.bank ! {transaction, #transaction{from = From, to = To, amount = Amount}};
        _ -> loop(State)
    end.
```
The mobile app contains a state that does not change, which is just the bank that it should use. 
It receives a message which has to have the format `{payment_request, #payment_request{from, to, amount}}`. If other messages are received then it just drops them.
It essentially acts as a proxy for the bank, i.e. once it receives a transfer message it forwards it to the bank, in the correct format.

## 12.3.2
```erlang
-module(bank).

-export([start/0, init/0]).
-record(transaction, {from, to, amount}).

start() ->
    spawn(?MODULE, init, []).

init() ->
    loop().

transfer(FROM, TO, Amount) ->
    FROM ! {deposit, -Amount},
    TO ! {deposit, Amount}.

loop() ->
    receive
        {transaction, Trans} -> 
            Account1 = Trans#transaction.from,
            Account2 = Trans#transaction.to,
            Amount = Trans#transaction.amount,
            transfer(Account1, Account2, Amount),
            loop();
        _ -> loop()
    end.
```
The bank is stateless as the accounts themselves hold the current amount state. The bank just handles subtracting or adding deposits from one account to the other.
It receives a message which has to have the format `{transaction, #transaction{from, to, amount}}`. If other messages are received then it just drops them.

## 12.3.3
```erlang
-module(account).
-export([start/0, init/1]).
-record(account, {balance}).

start() ->
    spawn(?MODULE, init, [0]).

init(InitialValue) ->
    InitialState = #account{balance = InitialValue},
    loop(InitialState).

loop(State) ->
    receive
        {deposit, Amount} -> 
            CurrentBalance = State#account.balance,
            NewState = State#account{balance = CurrentBalance + Amount},
            io:format("Hit with balance: ~p~n", [NewState#account.balance]),
            loop(NewState);
        _ -> loop(State)
    end.
```
The account holds the current balance which is initialized to 0. Once a deposit hits the account the amount is added to that balance. 
It expects messages in the format `{deposit, amount}`, if any other messages hits the account they're dropped. Better error handling could be made, but we could not be bothered.

## 12.3.4

```erlang
-module(system_starter).
-include("records.hrl").
-export([system_start2/0, make_request/0]).


system_start2() ->
    A1 = account:start(),
    A2 = account:start(),
    B1 = bank:start(),
    B2 = bank:start(),
    MA1 = mobile_app:start(B1),
    MA2 = mobile_app:start(B2).

make_request(A1, A2, MA1, MA2) ->
  Payment1 = #payment_request{from = A1, to = A2, amount = 100},
  Payment2 = #payment_request{from = A1, to = A2, amount = -100},
  MA1 ! {payment_request, Payment1},
  MA2 ! {payment_request, Payment2}.

```
## 12.3.5
```erlang
send_N_requests(N, BANK, From, To) -> 
    case N of
        _ when N =< 0 -> 0;
        _ -> 
            BANK ! {transaction, #transaction{from = From, to = To, amount = 1}},
            send_N_requests(N-1, BANK, From, To)
    end.
```
## 12.3.6
```erlang
loop(State) ->
    receive
        {deposit, Amount} -> 
            CurrentBalance = State#account.balance,
            NewState = State#account{balance = CurrentBalance + Amount},
            loop(NewState);
        print_balance -> 
            io:format("Account with id ~p has balance ~p~n", [State#account.accountId, State#account.balance]),
            loop(State);
        _ -> loop(State)
    end.
```

(a) 0
(b) No since each process sends the signals asynchronously there are many different balances that could be printed. It depends whether the deposit or the print_balance hits the message queue first. Since the message queue is FIFO it will just handle the messages in the order they're received. So if 3 deposits come before a print, then it will print the balance 3 or -3 respectivly.
(c) There are no guarantees of the possible balances that could be printed only that for each account it is in the range [-100, 0] or [0, 100]. This is because there is a race condition between when the deposit messages reach the account and the print message reaches the account.

## 12.3.7
No data races cannot occur since the account actor is running sequentially inside its own process. This means that there is no shared memory.

