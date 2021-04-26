import java.util.*;
public class hello{
    public static boolean error = false;
    public static void main(String[] args){
        String s = "choose(a,b,c,d)";


    }



    public String checkChoose(String ece){
        while(ece.contains("choose")){
             int a = ece.indexOf("choose");
             int b = findChoose(ece);
             if(ece.substring( a + 6, ece.indexOf( "(" , a+6 )).replaceAll(" ", "").length() != 0){
                 error = true;
                 return "";
             }
 
 
             int choclose = b + 1;
             if(choclose == 0){ //if no closing phar exists exists and we still open a bracket
                 error = true;
                 return "";
             }
 
             String cho = ece.substring(a + 7, b); //inside choose phar
             insideCheck(cho);
             if(error == true) return "";
 
             ece = ece.substring(0,a+6) + "1" + ece.substring(b+1);
         } 
         
         
     }
     
     public void insideCheck(String ece){
         StringTokenizer tok = new StringTokenizer(ece , "," , false);
         int i = 0;
         while(tok.hasNextToken() && i != 4){
             String k = tok.nextToken();
             if(k.contains("choose")){
                 String temp = k.substring(k.indexOf("choose"), findChoose(k)+1);
                 checkChoose(temp);
                 if(error == true) return;
                 ece = ece.substring(0, ece.indexOf("choose")) + "1" + ece.substring(findChoose(ece)+1);
                 tok = new StringTokenizer(ece, ",", false);
                for(int j = 0; j<= i ; j++){
                     tok.nextToken();
                }
 
             }else{
                 if(removeParan(k) == "2") return;
             }
             i++;
         }
 
         if(tok.hasnextToken()){
             error = true;
             return;
         }
 
 
     }
 
 
     public int findChoose(String ece){
         int i=0;
         boolean first = true;
         int index=0;
         for(int k=ece.indexOf("choose"); k<ece.length(); k++){
             if(ece.charAt(k) == ')'){
                 first = false;
                 if(i < 0){
                     error = true;
                     return 0;
                 }
                 i--;
             }else if(ece.charAt(k)=='('){
                 first = false;
                 i++;
             }
             if(i == 0 && !first){
                 index = k;
                 break;
             }
         }
         //index += ece.indexOf("choose");
         return index;
 
     }


     private String removePar(String ece) {

        if (!ece.contains("(")) {
            return parser(ece);
        }

        while (ece.contains("(")) {
            int a = ece.lastIndexOf("(");
            if (a == -1) {
                error = true;
                return "2";
            }
            int b = ece.indexOf(")", ece.lastIndexOf("("));
            ece = ece.substring(0, a) + removePar(ece.substring(a + 1, b))
                    + (b == ece.length() - 1 ? "" : ece.substring(b + 1, ece.length()));
            System.out.println(ece);
        }
        return ece;
    }

    public String parser(String ali) {
        // first tokning to + -
        if (ali.contains("+") || ali.contains("-")) {
            ali = ali.replaceAll("\\+", " + ");
            ali = ali.replaceAll("\\-", " - ");
            StringTokenizer aticine = new StringTokenizer(ali, "+-", false);
            while (aticine.hasMoreTokens()) {
                String ayse = aticine.nextToken();
                if (ayse.contains("*") || ayse.contains("/")) {
                    ali = ali.replaceAll("\\*", " * ");
                    ali = ali.replaceAll("\\/", " / ");

                    StringTokenizer aticine2 = new StringTokenizer(ayse, "*/", false);
                    while (aticine2.hasMoreTokens()) {
                        String a = aticine2.nextToken();
                        StringTokenizer b = new StringTokenizer(a, " ", false); // this and below if checks a + b c + d
                                                                                // also operators sequential without num
                                                                                // between
                        if (b.countTokens() != 1) {
                            error = true;
                            return "";
                        } else {
                            a = a.replaceAll(" ", "");
                            if (checkTypeValidity(a) == 2) {
                                error = true;
                                return "";
                            }
                        }
                    }
                } else {
                    StringTokenizer b = new StringTokenizer(ayse, " ", false);
                    if (b.countTokens() != 1) {
                        error = true;
                        return "";
                    } else {
                        String a = b.nextToken();
                        a = a.replaceAll(" ", "");
                        if (checkTypeValidity(a) == 2) {
                            error = true;
                            return "";
                        }
                    }
                }
            }
        } else if (ali.contains("*") || ali.contains("/")) {
            ali = ali.replaceAll("\\*", " * ");
            ali = ali.replaceAll("\\/", " / ");
            StringTokenizer aticine2 = new StringTokenizer(ali, "*/", false);
            while (aticine2.hasMoreTokens()) {
                String a = aticine2.nextToken();
                StringTokenizer b = new StringTokenizer(a, " ", false); // this and below if checks a + b c + d also
                                                                        // operators sequential without num between
                if (b.countTokens() != 1) {
                    error = true;
                    return "";
                } else {
                    String c = b.nextToken();
                    c = c.replaceAll(" ", "");
                    if (checkTypeValidity(a) == 2) {
                        error = true;
                        return "";
                    }
                }
            }
        } else { // for not expreessions like "n" in while in example
            StringTokenizer b = new StringTokenizer(ali, " ", false);
            if (b.countTokens() != 1) {
                error = true;
                return "";
            } else {
                String a = b.nextToken();
                a = a.replaceAll(" ", "");
                if (checkTypeValidity(a) == 2) {
                    error = true;
                    return "";
                }
            }
        }
        return "1";
    }

}