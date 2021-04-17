
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Main {
    static HashSet<String> declaredVariables = new HashSet<String>();
    public static int number = 1;
    public static boolean flag = false; // we must reset flag to false at each expression line, beware

    public static void main(String[] args) {
        // String ece = "x = b+(4*c+5)*7";
        // String ece = "print(f1)";
        // String ece = "a=b";
        String ece = "while(n)";
        // System.out.println(ece);
        conditioner(ece);

    }

    public static void expression(String ece) {

        if (ece.contains("=")) {
            StringTokenizer dec = new StringTokenizer(ece, "=", false);
            String varName = dec.nextToken();
            String value = dec.nextToken();
            value = removeParan(value);
            boolean x = false;
            aticine(value, x);
            if (!x) {
                System.out.println("store i32 %t" + --number + ", i32* %" + varName);
            } else {
                System.out.println("store i32 " + value + ", i32* %" + varName);

            }
            // }
            declaredVariables.add(varName);
        }
    }

    public static void print(String ece) {
        StringTokenizer cond = new StringTokenizer(ece, "()", false);
        cond.nextToken();
        String printStatement = cond.nextToken();
        printStatement = aticine(printStatement, true);
        System.out.println("call i32 (i8*, ...)* @printf(i8* getelementptr ([4 x i8]* @print.str, i32 0, i32 0), i32 "
                + "%t" + (number - 1) + " )");
    }

    public static int conditioner(String ece) {
        StringTokenizer cond = new StringTokenizer(ece, "()", false);
        if (cond.nextToken().equals("while")) {
            System.out.println("br label %whcond");
            System.out.println();
            // StringTokenizer paran = new StringTokenizer(cond.nextToken(), "()", false);
            System.out.println("whcond:");
            String test = cond.nextToken();
            aticine(test, true);
            System.out.println("%t" + number++ + " = icmp ne i32 %t" + (number - 2) + ", 0");
            System.out.println("br i1 %t" + (number - 1) + ", label %whbody, label %whend");
            System.out.println();
            System.out.println("whbody:");
            // dont forget whend statement, check
            return 0;

        } else if (cond.nextToken().equals("if")) {
            System.out.println("ifcond:");
            String test = cond.nextToken();
            aticine(test, true);
            // is this always true or false? where do we set it false or true?
            System.out.println("%t" + number++ + " = icmp ne i32 %t" + (number - 2) + ", 0");
            System.out.println("br i1 %t" + number++ + ", label %ifbody");
            System.out.println();
            System.out.println("ifbody:");
            return 1;

        }
        return 2;

    }

    // returns 0 if int, 1 if variable name, 2 if erroneous name
    public static int checkTypeValidity(String s) {
        boolean integer = true;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch <= 57 || ch >= 48)
                continue;
            else if (ch <= 122 || ch >= 97)
                integer = false;
            else
                return 2;
        }
        if (integer)
            return 0;
        else
            return 1;
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
                    System.out.println("Load %t" + number + " = load i32* %" + islem.get(i));
                    islem.set(i, "%t" + number++);
                }
            }

            // boolean integerB = true;
            try {
                int a = Integer.parseInt(islem.get(i + 2));
            } catch (NumberFormatException e) {
                if (islem.get(i + 2).charAt(0) != '%') {
                    System.out.println("Load %t" + number + " = load i32* %" + islem.get(i + 2));
                    islem.set(i + 2, "%t" + number++);
                }
                // integerB = false;
            }

            String exp = islem.get(i + 1);

            switch (exp) {

            case "*":
                // %t6 = add i32 %t4, %t5
                System.out.println("%t" + number++ + "= mul i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, "%t" + (number - 1));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "/":
                System.out.println("%t" + number++ + " = sdiv i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, islem.set(0, "%t" + (number - 1)));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "+":
                System.out.println("%t" + number++ + " = add i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, "%t" + (number - 1));
                islem.remove(i + 1);
                islem.remove(i + 1);
                flag = true;

                break;
            case "-":
                System.out.println("%t" + number++ + " = sub i32 " + islem.get(i) + ", " + islem.get(i + 2));
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
                return ali;
            } catch (NumberFormatException e) {
                // integerA = false;
                if (ali.charAt(0) != '%') { // here means the variable is neither an intger nor a %t variable
                    System.out.println("%t" + number++ + " = load i32* %" + ali);
                    declaredVariables.add(ali);
                    x = true;
                    return ali;
                    // islem.set(i, "%t"+number++);
                }
            }
        }

        calculation(son);
        return son.get(0);
    }
}
