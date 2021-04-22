
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    static HashSet<String> declaredVariables = new HashSet<String>();
    public static int number = 1;
    public static boolean flag = false; // we must reset flag to false at each expression line, beware
    static ArrayList<String> outputs = new ArrayList<String>();
    static boolean error = false;

    public static void main(String[] args) throws FileNotFoundException {
        // String ece = "x = b+(4*c+5)*7";
        // String ece = "print(f1)";
        // String ece = "a=b";
        // String ece = "while(n)";
        // outputs.add(ece);
        // expression(ece);
        File input = new File(args[0]);
        Scanner x = new Scanner(input);
        
        PrintWriter writer = new PrintWriter(new File(args[1]));
        int while_if = 0;
        int lineNum = 1;

        while (x.hasNextLine()) {
            String ece = x.nextLine();
            //ece = ece.replaceAll(" ", "");
            ece = ece.replaceAll("\t", " ");

            if(ece.contains("#")) ece = ece.substring(0, ece.indexOf("#"));

            int type = typeChecker(ece);
            switch (type) {
            case 1: // expression
                assignmentCheck(ece);
                if(!error) expression(ece);
                else{
                    writer.println("Syntax error at line " + lineNum);
                }
                break;
            case 2: //while/if entrance
                //conditionCheck(ece);
                while_if = conditioner(ece);
                break;
            case 3: //while/if enclosure
                if(while_if == 1){
                    outputs.add("br label %whcond");
                    outputs.add("\n");
                    outputs.add("whend:" + "\n" + "\nret i32 0");
                }else if(while_if == 2){
                    outputs.add("br label %ifend");
                }
                break;
            case 4: //print
                print(ece);
                break;
            default: // handles gibberish lines AND empty lines
                ece.replaceAll(" ", "");
                if(ece.length() != 0) error = true;
                break;
            }
            lineNum++;
        }

        writer.println("; ModuleID = \'mylang2ir\' \n"+"declare i32 @printf(i8*, ...)\n"+"@print.str = constant [4 x i8] c\"%d\\0A\\00\" \n\n"+"define i32 @main() {");
        outputs.add("\n }");

        for(String s: declaredVariables){ writer.println("%" + s + " = alloca i32");}
        writer.println();
        for(String s: declaredVariables) {writer.println("store i32 0, i32* %" + s);}
        writer.println();

        for(String s: outputs) writer.println(s);
        writer.close();
    }


    public static void conditionCheck(String s){
        s.replaceAll("(", " ( ");
        s.replaceAll(")", " ) ");
        s.replaceAll("{", " { ");
        //StringTokenizer tok = new StringTokenizer(s, " ", false);
        if(!s.substring(0, s.indexOf(" ")).equals("while") || !s.substring(0, s.indexOf(" ")).equals("if")){
            error = true;
            return;
        }
    }

    /*
    public static void chooseFunct(String s){
        System.out.println("define i32 @choose(" + )
    }
    */



    private static void removePhar(String ece){
        while (ece.contains("(")) {
            int fi = ece.indexOf("(", 0);
            int fis = ece.indexOf(")", 0);
            if(fis == -1){
                error = true;
                return;
            }
            String ali = ece.substring(fi + 1, fis);
            // this part is just testing
            ali = parser(ali);
            ece = ece.substring(0, fi) + ali + ece.substring(fis + 1);
        }

        if(ece.contains(")")) error = true;
        ece = parser(ece);
    }

public static String parser(String ali) {
        ArrayList<String> son = new ArrayList<String>();
        // first tokning to + -
        if (ali.contains("+") || ali.contains("-")) {
            ali.replaceAll("+", " + ");
            ali.replaceAll("-", " - ");
            StringTokenizer aticine = new StringTokenizer(ali, "+-", false);
            while (aticine.hasMoreTokens()) {
                ArrayList<String> islem = new ArrayList<String>();
                String ayse = aticine.nextToken();
                if (ayse.contains("*") || ayse.contains("/")) {
                    ali.replaceAll("*", " * ");
                    ali.replaceAll("/", " / ");
        
                    StringTokenizer aticine2 = new StringTokenizer(ayse, "*/", false);
                    while (aticine2.hasMoreTokens()) {
                        String a = aticine2.nextToken();
                        StringTokenizer b = new StringTokenizer(a, " ", false); // this and below if checks a + b  c + d also operators sequential without num between
                        if(b.countTokens() != 1){
                            error = true;
                            return "";
                        }else{
                            a = a.replaceAll(" ", "");
                            if(checkTypeValidity(a) == 2){
                                error = true;
                                return "";
                            }
                        }
                    }
                } else {
                    StringTokenizer b = new StringTokenizer(ayse, " ", false);
                        if(b.countTokens() != 1){
                            error = true;
                            return "";
                        }else{
                            String a = b.nextToken();
                            a = a.replaceAll(" ", "");
                            if(checkTypeValidity(a) == 2){
                                error = true;
                                return "";
                            }
                        }
                }
            }
        } else if (ali.contains("*") || ali.contains("/")) {
            ali.replaceAll("*", " * ");
            ali.replaceAll("/", " / ");
            StringTokenizer aticine2 = new StringTokenizer(ali, "*/", false);
            while (aticine2.hasMoreTokens()) {
                String a = aticine2.nextToken();
                StringTokenizer b = new StringTokenizer(a, " ", false); // this and below if checks a + b  c + d also operators sequential without num between
                if(b.countTokens() != 1){
                    error = true;
                    return "";
                }else{
                    String c = b.nextToken();
                    c = c.replaceAll(" ", "");
                    if(checkTypeValidity(a) == 2){
                        error = true;
                        return "";
                    }
                }
            }
        } else { // for not expreessions like "n" in while in example
            StringTokenizer b = new StringTokenizer(ali, " ", false);
            if(b.countTokens() != 1){
                error = true;
                return "";
            }else{
                String a = b.nextToken();
                a = a.replaceAll(" ", "");
                if(checkTypeValidity(a) == 2){
                    error = true;
                    return "";
                }
            }
        }
        return "1";
    }


    public static void expressionCheck(String s){
        StringTokenizer tok = new StringTokenizer(s, "+/*-()", true);
        String test = s;
        test.replaceAll(" ", "");
        if(test.length() == 0){ //if no expression exists, just spaces
             error = true;
             return;
        }
        removePhar(s);
    }

    public static void assignmentCheck(String s){
        StringTokenizer t = new StringTokenizer(s, "=", false);
        if(t.countTokens() != 2){
            error = true;
            System.out.println("A");
            return;
        }

        String varName = t.nextToken();
        if(checkTypeValidity(varName) != 1){
            System.out.println(checkTypeValidity(varName));
            System.out.println("B");
            System.out.println(varName);
            error = true;
            return;
        }

        expressionCheck(t.nextToken());
    }


    public static int typeChecker(String ece) {
        if (ece.contains("=")) {
            return 1;
        } else if (ece.contains("{")) {
            return 2;
        } else if (ece.contains("}")) {
            return 3;
        } else if(ece.contains("print")) {
            return 4;
        }else{
            return 0;
        }
    }

    public static void expression(String ece) {

        if (ece.contains("=")) {
            StringTokenizer dec = new StringTokenizer(ece, "=", false);
            String varName = dec.nextToken();
            String value = dec.nextToken();
            String temporary = "";
            value = removeParan(value);
            boolean x = false;
            temporary = aticine(value, x);
            if (temporary.charAt(0)!='~') {
                outputs.add("store i32 %t" + (number-1) + ", i32* %" + varName);//burda bi bokluk var
            } else {
                outputs.add("store i32 " + value + ", i32* %" + varName);
            }
            declaredVariables.add(varName);
        }
    }

    public static void print(String ece) {
        StringTokenizer cond = new StringTokenizer(ece, "()", false);
        cond.nextToken();
        String printStatement = cond.nextToken();
        printStatement = aticine(printStatement, true);
        outputs.add("call i32 (i8*, ...)* @printf(i8* getelementptr ([4 x i8]* @print.str, i32 0, i32 0), i32 "
                + "%t" + (number - 1) + " )");
    }

    public static int conditioner(String ece) {
        StringTokenizer cond = new StringTokenizer(ece, "()", false);
        if (cond.nextToken().equals("while")) {
            outputs.add("br label %whcond");
            outputs.add("\n");
            // StringTokenizer paran = new StringTokenizer(cond.nextToken(), "()", false);
            outputs.add("whcond:");
            String test = cond.nextToken();
            aticine(test, true);
            outputs.add("%t" + number++ + " = icmp ne i32 %t" + (number - 2) + ", 0");
            outputs.add("br i1 %t" + (number - 1) + ", label %whbody, label %whend");
            outputs.add("\n");
            outputs.add("whbody:");
            // dont forget whend statement, check
            return 1;

        } else if (cond.nextToken().equals("if")) {
            outputs.add("ifcond:");
            String test = cond.nextToken();
            aticine(test, true);
            // is this always true or false? where do we set it false or true?
            outputs.add("%t" + number++ + " = icmp ne i32 %t" + (number - 2) + ", 0");
            outputs.add("br i1 %t" + number++ + ", label %ifbody");
            outputs.add("\n");
            outputs.add("ifbody:");
            return 2;

        }
        return 0;

    }

    // returns 0 if int, 1 if variable name, 2 if erroneous name
    //should check if the first letter of varname is num!!
    public static int checkTypeValidity(String s) {
        System.out.println("varname is \"" + s + "\"");
        StringTokenizer tok = new StringTokenizer(s, " ", false);
        if(tok.countTokens() != 1) return 2;
        s.replaceAll(" ", "");
        boolean integer = true;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            //System.out.println();
            System.out.println((int)ch);
            if ((int)ch <= 57 && (int)ch >= 48) continue;  //if integer
            else if (((int)ch <= 122 && (int)ch >= 97 ) || ((int)ch <= 132 && (int)ch >= 101)) integer = false;
            else return 2;
        }
        if (integer) return 0;
        else return 1;
    }

    // here I handle parantheses
    public static String removeParan(String ece) {
        while (ece.contains("(")) {
            int fi = ece.indexOf("(", 0);
            int fis = ece.indexOf(")", 0);
            String ali = ece.substring(fi + 1, fis);
            // this part is just testing
            ali = aticine(ali, true);
            ece = ece.substring(0, fi) + ali + ece.substring(fis + 1);
        }
        return ece;
    }

    // this method takes an array and does the calculations inside it
    public static void calculation(ArrayList<String> islem) {
        while (islem.size() != 1) {
            int i = 0;

            // boolean integerA = true;
            try {
                int a = Integer.parseInt(islem.get(i));
            } catch (NumberFormatException e) {
                // integerA = false;
                if (islem.get(i).charAt(0) != '%') { // here means the variable is neither an integer nor a %t variable
                    outputs.add("%t" + number + " = load i32* %" + islem.get(i));
                    islem.set(i, "%t" + number++);
                }
            }

            // boolean integerB = true;
            try {
                int a = Integer.parseInt(islem.get(i + 2));
            } catch (NumberFormatException e) {
                if (islem.get(i + 2).charAt(0) != '%') {
                    outputs.add("%t" + number + " = load i32* %" + islem.get(i + 2));
                    islem.set(i + 2, "%t" + number++);
                }
                // integerB = false;
            }

            String exp = islem.get(i + 1);

            switch (exp) {

            case "*":
                // %t6 = add i32 %t4, %t5
                outputs.add("%t" + number++ + "= mul i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, "%t" + (number - 1));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "/":
                outputs.add("%t" + number++ + " = sdiv i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, islem.set(0, "%t" + (number - 1)));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "+":
                outputs.add("%t" + number++ + " = add i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, "%t" + (number - 1));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "-":
                outputs.add("%t" + number++ + " = sub i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, "%t" + (number - 1));
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
    public static String aticine(String ali, boolean x) {
        ArrayList<String> son = new ArrayList<String>();

        // first tokning to + -
        if (ali.contains("+") || ali.contains("-")) {
            StringTokenizer aticine = new StringTokenizer(ali, "+-", true);
            while (aticine.hasMoreTokens()) {
                ArrayList<String> islem = new ArrayList<String>();
                String ayse = aticine.nextToken();
                if (ayse.contains("*") || ayse.contains("/")) {
                    // below 5 lines can be done with split, will reduce space and time (wpnt
                    // require islem arraylist)
                    StringTokenizer aticine2 = new StringTokenizer(ayse, "*/", true);
                    while (aticine2.hasMoreTokens()) {
                        islem.add(aticine2.nextToken());
                    }
                    calculation(islem);
                    // I cant replace the calculation with string here
                    son.add(islem.get(0));
                } else {
                    son.add(ayse);
                }
            }
        } else if (ali.contains("*") || ali.contains("/")) {
            StringTokenizer aticine = new StringTokenizer(ali, "*/", true);
            ArrayList<String> islem = new ArrayList<String>();
            while (aticine.hasMoreTokens()) {
                islem.add(aticine.nextToken());
            }
            calculation(islem);
            son.add(islem.get(0));

        } else { // for not expreessions like "n" in while in example
            try {
                int a = Integer.parseInt(ali);
                return "~" + ali;
            } catch (NumberFormatException e) {
                // integerA = false;
                if (ali.charAt(0) != '%') { // here means the variable is neither an intger nor a %t variable
                    outputs.add("%t" + number++ + " = load i32* %" + ali);
                    declaredVariables.add(ali);
                   // x = true;
                    return ali;
                    // islem.set(i, "%t"+number++);
                }
            }
        }

        calculation(son);
        return son.get(0);
    }
}