import java.io.PrintWriter;
import java.util.StringTokenizer;

public class ErrorObject {
    public boolean error = false;
    public String checkChoose(String ece){
       while(ece.contains("choose")){
            int a = ece.indexOf("choose");
            int b = findChoose(ece);
            if(b == 0){
                error = true;
                return "";
            }
            if(ece.substring( a + 6, ece.indexOf( "(" , a+6 )).replaceAll(" ", "").length() != 0){
                error = true;
                return "";
            }


            int choClose = b + 1;
            if(choClose == 1){ //if no closing phar exists exists and we still open a bracket
                error = true;
                return "";
            }

            String cho = ece.substring(a + 7, b); //inside choose phar
            ece = ece.substring(0, ece.indexOf("choose")+7) + insideCheck(cho) + ece.substring(b); 


            if(error == true) return "";

            a = ece.indexOf("choose");

            b = findChoose(ece);

            
            ece = ece.substring(0,a) + "1" + ece.substring(b+1);

        } 
        return ece;
        
    }
    
    public String insideCheck(String ece){
        StringTokenizer tok = new StringTokenizer(ece , "," , false);
        int i = 0;
        while(tok.hasMoreTokens() && i != 4){
            String k = tok.nextToken();
            if(k.contains("choose")){                
                String temp = k.substring(k.indexOf("choose"), findChoose(k)+1);
                checkChoose(temp);
                if(error == true) return "";
                ece = ece.substring(0, ece.indexOf("choose")) + "1" + ece.substring(findChoose(ece)+1);
                tok = new StringTokenizer(ece, ",", false);
                for(int j = 0; j<= i ; j++){
                    tok.nextToken();
                }
            }else{
                expressionCheck(k);
                if( error) return "";
            }

            i++;
        }

        if(tok.hasMoreTokens()){
            error = true;
            return "";
        }

        if(i<4){
            error = true;
            return "";
        }
        return ece;
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

    public void printError(int lineNum, PrintWriter writer){
        writer.println("; ModuleID = 'mylang2ir'");
        writer.println("declare i32 @printf(i8*, ...)");
        writer.println("@print.str = private constant [22 x i8] c\"" + "Line "+ lineNum +": syntax error\\0A\\00\"");
        writer.println("define i32 @main() {");
        writer.println("call i32 (i8*, ...)* @printf(i8* getelementptr ([22 x i8]* @print.str, i32 0, i32 0))");
        writer.println("ret i32 0");
        writer.println("}");
        
    }
   

    public void assignmentCheck(String s) {
        StringTokenizer t = new StringTokenizer(s, "=", false);
        if (t.countTokens() != 2) {
            error = true;
            return;
        }

        String varName = t.nextToken();
        if (checkTypeValidity(varName) != 1) {
            error = true;
            return;
        }
        expressionCheck(t.nextToken());
    }

    public void expressionCheck(String s) {
        StringTokenizer tok = new StringTokenizer(s, "+/*-()", true);
        String test = s;
        test = test.replaceAll(" ", "");
        if (test.length() == 0) { // if no expression exists, just spaces
            error = true;
            return;
        }


        s = checkChoose(s);
        System.out.println(s);
        if(error) return;
        
        removePar(s);
        System.out.println("CH EXPRESSION " + error);

        
    }

    public void conditionCheck(String s) {
        s = s.replaceAll("\\(", " ( ");
        s = s.replaceAll("\\)", " ) ");
        s = s.replaceAll("\\{", " { ");
        StringTokenizer tok1 = new StringTokenizer(s, " ", false);
        String cond = tok1.nextToken();
        if (!(cond.equals("while") || cond.equals("if"))) {
            error = true;
            return;
        } else if (!(tok1.nextToken().equals("("))) {
            error = true;
            return;
        } else if (s.indexOf(")") == -1) {
            error = true;
            return;
        } else {
            String exp = s.substring((s.indexOf("(") + 1), s.lastIndexOf(")"));
            expressionCheck(exp);
            if (error)
                return;
            s = s.replaceAll(" ", "");
            if (s.substring(s.lastIndexOf(")") + 1).length() != 1) {
                error = true;
                return;
            }
        }

    }

    public void closeCheck(String ece) {
        ece = ece.replaceAll(" ", "");
        if (ece.length() != 1) {
            error = true;
            return;
        }
    }

    public void printCheck(String s) {
        s = s.replaceAll("\\(", " ( ");
        s = s.replaceAll("\\)", " ) ");
        StringTokenizer tok1 = new StringTokenizer(s, " ", false);
        String cond = tok1.nextToken();
        if (!(cond.equals("print"))) {
            error = true;
            return;
        } else if (tok1.hasMoreTokens() && !(tok1.nextToken().equals("("))) {
            error = true;
            return;
        } else if (s.indexOf(")") == -1) {
            error = true;
            return;
        } else {
            String exp = s.substring((s.indexOf("(") + 1), s.lastIndexOf(")"));
            expressionCheck(exp);
            if (error)
                return;
            s = s.replaceAll(" ", "");
            if (s.substring(s.lastIndexOf(")") + 1).length() != 0) {
                error = true;
                return;
            }
        }
    }

    /*
     * public static void chooseFunct(String s){
     * System.out.println("define i32 @choose(" + ) }
     */
    
//beware!! this method returns 2 when no error occurs BUT the input 
//(ece) may really be 2 in this case the program will give error
private String removePar(String ece) {

        if (!ece.contains("(")) {
            return parser(ece);
        }

        while (ece.contains("(")) {
            int a = ece.lastIndexOf("(");
            int b = ece.indexOf(")", ece.lastIndexOf("("));
            if (b == -1) {
                error = true;
                return "2";
            }

            ece = ece.substring(0, a) + removePar(ece.substring(a + 1, b))
                    + (b == ece.length() - 1 ? "" : ece.substring(b + 1, ece.length()));
        }
                parser(ece);
                System.out.println("HOO " +ece + error);
                
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
                    ayse = ayse.replaceAll("\\*", " * ");
                    ayse = ayse.replaceAll("\\/", " / ");

                    StringTokenizer aticine2 = new StringTokenizer(ayse, "*/", false);
                    System.out.println("TTT");
                    System.out.println(aticine2.countTokens());
                    System.out.println(ayse);
                    while (aticine2.hasMoreTokens()) {
                        String a = aticine2.nextToken();
                        StringTokenizer b = new StringTokenizer(a, " ", false); // this and below if checks a + b c + d
                                                                                // also operators sequential without num
                        System.out.println("QQQQ" + a);                                 // between
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

    public int checkTypeValidity(String s) {
        StringTokenizer tok = new StringTokenizer(s, " ", false);
        if (tok.countTokens() != 1)
            return 2;
        s = s.replaceAll(" ", "");
        boolean integer = true;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((int) ch <= 57 && (int) ch >= 48) {
                continue; // if integer
            } else if (((int) ch <= 122 && (int) ch >= 97) || ((int) ch <= 90 && (int) ch >= 65) || ch == 95)
                integer = false;
            else
                return 2;
        }
        if (integer)
            return 0;
        else
            return 1;
    }
}