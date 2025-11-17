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



