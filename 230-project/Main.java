
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    static ArrayList<String> declaredVariables = new ArrayList<String>();
    static HashMap<String, Integer> temporaryVariables = new HashMap<String, Integer>();
    public static int number = 0;
    public static boolean flag = false;
    public static void main(String[] args) {
        String ece = "x =4+(4*3+5)*7";
        String declaration2 = "a = 5";
        String declaration = "a=4+b";
       // System.out.println(ece);
       // ece = aticine(ece);
        
        
        if(ece.contains("=")){
            StringTokenizer dec = new StringTokenizer(ece, "=", false);
            String varName = dec.nextToken();
            String value = dec.nextToken();
            if(value.contains("+")||value.contains("-")||value.contains("/")||value.contains("*")){
                value = removeParan(value);
                System.out.println(value);
                aticine(value);
                System.out.println("store t" + --number + " " +varName +  " bişe bişe"  );
            }
            declaredVariables.add(varName);
        }


    }




     // here I handle parantheses
    public static String removeParan(String ece) {
        while (ece.contains("(")) {
            int fi = ece.indexOf("(", 0);
            int fis = ece.indexOf(")", 0);
            String ali = ece.substring(fi + 1, fis);
            // this part is just testing
            ali = aticine(ali);
            ece = ece.substring(0, fi) + ali + ece.substring(fis + 1);

        }
        return ece;
    }

    // this method takes an array and does the calculations inside it
    public static void calculation(ArrayList<String> islem) {
        while (islem.size() != 1) {
            int i = 0;
            int a = Integer.parseInt(islem.get(i));
            if(!flag){
            System.out.println("load falan filan &t"+ number++ + " bişe");
            }   
            System.out.println("load falan filan &t"+ number++ + " bişe");    
            int b = Integer.parseInt(islem.get(i + 2));
            String exp = islem.get(i + 1);
            switch (exp) {

            case "*":
            
                System.out.println("çarpma falan filan &t" + (number-2) +" t"+ (number-1) + " t"+ number++ + "bişe");
                String e = Integer.toString(a * b);
                flag = true;
                islem.set(0, e);
                islem.remove(i + 1);
                islem.remove(i + 1);
                break;
            case "/":
            System.out.println("bölme falan filan &t" + (number-2) +" t"+ (number-1) + " t"+ number++ + "bişe");
            String e1 = Integer.toString(a / b);
                islem.set(0, e1);
                flag = true;
                islem.remove(i + 1);
                islem.remove(i + 1);
                break;
            case "+":
            System.out.println("toplama falan filan &t" + (number-2) +" t"+ (number-1) + " t"+ number++ + "bişe");
            String e3 = Integer.toString(a + b);
                islem.set(0, e3);
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "-":
            System.out.println("çıkarma falan filan &t" + (number-2) +" t"+ (number-1) + " t"+ number + "bişe");
            String e4 = Integer.toString(a - b);
                islem.set(0, e4);
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            }

        }
    }

    // this method handles calculations while replacing the results with
    // calculations like 4*3 is replaced with 12
    // works fine if there is no pharanthesis in the expression
    public static String aticine(String ali) {
        ArrayList<String> son = new ArrayList<String>();

        // first tokning to + -
        if (ali.contains("+") || ali.contains("-")) {
            StringTokenizer parser = new StringTokenizer(ali, "+-", true);
            while (parser.hasMoreTokens()) {
                ArrayList<String> islem = new ArrayList<String>();
                String ayse = parser.nextToken();
                if (ayse.contains("*") || ayse.contains("/")) {
                    // below 5 lines can be done with split, will reduce space and time (wpnt
                    // require islem arraylist)
                    StringTokenizer parser2 = new StringTokenizer(ayse, "*/", true);
                    while (parser2.hasMoreTokens()) {
                        islem.add(parser2.nextToken());
                    }
                    calculation(islem);
                    // I cant replace the calculation with string here
                    son.add(islem.get(0));

                } else {
                    son.add(ayse);
                }
            }
        }else if (ali.contains("*") || ali.contains("/")) {
            StringTokenizer parser = new StringTokenizer(ali, "*/", true);
            ArrayList<String> islem = new ArrayList<String>();
            while (parser.hasMoreTokens()) {
                islem.add(parser.nextToken());
            }
            calculation(islem);
            son.add(islem.get(0));

        }
        // if code is here, this means there is no * or / left

        calculation(son);
        return son.get(0);
    }
}
