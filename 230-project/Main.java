
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    static ArrayList<String> declaredVariables = new ArrayList<String>();
    static HashMap<String, Integer> temporaryVariables = new HashMap<String, Integer>();
    public static int number = 0;
    public static boolean flag = false; // we must reset flag to false at each expression line, beware
    public static void main(String[] args) {
        String ece = "x = b+(4*c+5)*7";
        String declaration2 = "a = 5";
        String declaration = "a=4+b";
       // System.out.println(ece);
       // ece = aticine(ece);
    
        
        if(ece.contains("=")){
            StringTokenizer dec = new StringTokenizer(ece, "=", false);
            String varName = dec.nextToken();
            String value = dec.nextToken();
            if(value.contains("+")||value.contains("-")||value.contains("/")||value.contains("*")){
                //check here for invalid expressions
                value = removeParan(value);
                System.out.println(value);
                aticine(value);
                System.out.println("store t" + --number + " " +varName +  " bişe bişe"  );
            }
            declaredVariables.add(varName);
        }


    }

    //returns 0 if int, 1 if variable name, 2 if erroneous name
    public static int checkTypeValidity(String s){
        boolean integer = true;
        for(int i = 0; i<s.length(); i++){
            char ch = s.charAt(i);
            if( ch <= 57 || ch >=48) continue;
            else if (ch <= 122 || ch >= 97) integer = false;
            else return 2;
        }
        if(integer) return 0;
        else return 1;
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
            
            boolean integerA = true;
            try{
                int a = Integer.parseInt(islem.get(i));
            }catch(NumberFormatException e){
                integerA = false;
            }
            if(!integerA && islem.get(i).charAt(0) != '%'){ //here means the variable is neither an integer nor a %t variable
                    System.out.println("Load t"+ number +" the value of " + islem.get(i));
                    islem.set(i, "%t"+number++);
            }


            boolean integerB = true;
            try{
                int a = Integer.parseInt(islem.get(i+2));
            }catch(NumberFormatException e){
                integerB = false;
            }
            if(!integerB && islem.get(i+2).charAt(0) != '%'){
                System.out.println("Load t"+ number +" the value of " + islem.get(i+2));
                islem.set(i+2, "%t"+ number++);
            }

            String exp = islem.get(i + 1);

            switch (exp) {

            case "*":
                System.out.println("çarpma falan filan " + islem.get(i)  + "  " + islem.get(i+2) + "şuraya ata" + " t"+ number++ + "bişe");
                islem.set(0, "%t"+ (number-1));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "/":
            System.out.println("bölme falan filan &t" + islem.get(i)  + "  " + islem.get(i+2) + "şuraya ata" + " t"+ number++ + "bişe");
                islem.set(0, islem.set(0, "%t"+ (number-1)));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;
                
                break;
            case "+":
                System.out.println("toplama falan filan &t" + islem.get(i)  + "  " + islem.get(i+2) + "şuraya ata" + " t"+ number++ + "bişe");
                islem.set(0, "%t"+ (number-1));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "-":
            System.out.println("çıkarma falan filan &t" + islem.get(i)  + "  " + islem.get(i+2) + "şuraya ata" + " t"+ number++ + "bişe");
                islem.set(0, "%t"+ (number-1));
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
