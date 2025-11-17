package week12exercises;

// This class encodes/decodes banktransfers to/from strings
// Please use JSON for professional applications !!!

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