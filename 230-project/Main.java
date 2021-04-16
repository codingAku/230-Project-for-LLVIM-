
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        String ece = "4+(4*3+5)*7-(5/2)";
        // here I handle parantheses

        ece = removeParan(ece);
        ece = aticine(ece);
        System.out.println(ece);

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
            System.out.println("load falan filan &t bişe");
            int a = Integer.parseInt(islem.get(i));
            System.out.println("load falan filan &t bişe");
            int b = Integer.parseInt(islem.get(i + 2));
            String exp = islem.get(i + 1);
            switch (exp) {

            case "*":
                System.out.println("çarpma falan filan &t bişe");
                String e = Integer.toString(a * b);
                islem.set(0, e);
                islem.remove(i + 1);
                islem.remove(i + 1);
                break;
            case "/":
                System.out.println("bölme falan filan &t bişe");
                String e1 = Integer.toString(a / b);
                islem.set(0, e1);
                islem.remove(i + 1);
                islem.remove(i + 1);
                break;
            case "+":
                System.out.println("toplama falan filan &t bişe");
                String e3 = Integer.toString(a + b);
                islem.set(0, e3);
                islem.remove(i + 1);
                islem.remove(i + 1);
                break;
            case "-":
                System.out.println("çıkarma falan filan &t bişe");
                String e4 = Integer.toString(a - b);
                islem.set(0, e4);
                islem.remove(i + 1);
                islem.remove(i + 1);
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
        // System.out.println(ali);
    }
}
