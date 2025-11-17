package week12exercises;

// This class encodes/decodes banktransfers to/from strings
// Please use JSON for professional applications !!!

public class Convert{

  public String encode(int a1, int a2, int amount) {
    return "" ;
  }

  public int getA1(String mes){
    return Integer.parseInt(mes.substring(1, mes.indexOf(",")));
  }

  public int getA2(String mes){
    return 0 ;
  }

  public int getAmount(String mes){
    return 0 ;
  }
}