import java.util.StringTokenizer;

public class ErrorObject {
    public boolean error = false;

    public String checkChoose(String ece){
       while(ece.contains("choose")){
            int a = ece.indexOf("choose");
            int b = ece.findChoose();
            if(ece.substring( a + 6, ece.indexOf( "(" , a+6 )).replaceAll(" ", "").length() != 0){
                error = true;
                return "";
            }


            int choclose = b + 1;
            if(choClose == 0){
                error = 0;
                return "";
            }

            String cho = ece.substring(a + 7, b); //inside choose phar
            insideCheck(cho);
            if(error == true) return "";

            ece = ece.substring(0,a+6) + "1" + ece.substring()
        } 
        
        
    }
    
    public void insideCheck(String s){
        StringTokenizer tok = new StringTokenizer(cho , "," , false);
        int i = 0;
        while(tok.hasNextToken() && i != 4){
            String k = tok.nextToken();
            if(k.contains("choose")){
                String temp = k.substring(k.indexOf("choose"), findChoose(k)+1);
                checkChoose(temp);
                if(error == true) return;
                ece = ece.substring(0, ece.indexOf("choose")) + "1" + ece.substring(findChoose(ece)+1);
            }
            tok = new StringTokenizer(ece, ",", false);
            for(int j = 0; j<= i ; j++){
                tok.nextToken();
            }

            }else{
                if(removePhar(k) == "2") return;
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

    public  void expression(String ece) {

        String temporary = "";
        
        if (ece.contains("=")) {
            StringTokenizer dec = new StringTokenizer(ece, "=", false);
            String varName = dec.nextToken();
            String value = dec.nextToken(); 
            if(value.contains("choose")){
            value = choose(value);
            chooseNum += depth;
            depth = 0;
            }
            temporary = removeParan(value);
           // temporary = aticine(value);
            if(temporary.contains("%")){
                outputs.add("store i32 " + temporary + ", i32* %" + varName); 
            }
            else if (temporary.charAt(0) != '~') {
                outputs.add("store i32 %t" + (number - 1) + ", i32* %" + varName);// burda bi bokluk var
            } else  {
                outputs.add("store i32 " + value + ", i32* %" + varName);
            }
            declaredVariables.add(varName);
            //if(!t[0]){
           // outputs.add("end:");
            //}
        }
    }

    public  void print(String ece) {
        StringTokenizer cond = new StringTokenizer(ece, "()", false);
        cond.nextToken();
        String printStatement = cond.nextToken();
        printStatement = aticine(printStatement);
    
        outputs.add("call i32 (i8*, ...)* @printf(i8* getelementptr ([4 x i8]* @print.str, i32 0, i32 0), i32 " + "%t"
                + (number - 1) + " )");
    }

    public  int conditioner(String ece) {
        StringTokenizer cond = new StringTokenizer(ece, "()", false);
        if (cond.nextToken().equals("while")) {
            outputs.add("br label %whcond");
            outputs.add("\n");
            // StringTokenizer paran = new StringTokenizer(cond.nextToken(), "()", false);
            outputs.add("whcond:");
            String test = cond.nextToken();
            int a = ece.indexOf("(")+1;
            int b = ece.lastIndexOf(")");
            String temporary = removeParan(ece.substring(a, b));
            // temporary = aticine(value);
            if(temporary.contains("%"))
                outputs
                .add("%t" + number++ + " = icmp ne i32 " + temporary + ", 0");
            else if (temporary.charAt(0) != '~') {
                outputs.add("%t" + number++ + " = load i32* %" + temporary);
                outputs.add("%t" + number++ + " = icmp ne i32 %t" + (number-1) + ", 0");
            } else  {
                outputs.add("%t" + number++ + " = icmp ne i32 " + temporary.substring(1) + ", 0");
            }
            outputs.add("br i1 %t" + (number - 1) + ", label %whbody, label %whend");
            outputs.add("\n");
            outputs.add("whbody:");
            // dont forget whend statement, check
            return 1;

        } else if (cond.nextToken().equals("if")) {
            outputs.add("ifcond:");
            String test = cond.nextToken();
            int a = ece.indexOf("(");
            int b = ece.lastIndexOf(")")+1;
            String temporary = removeParan(ece.substring(a, b));
            // temporary = aticine(value);
            if(temporary.contains("%"))
                outputs.add("%t" + number++ + " = icmp ne i32 " + temporary + ", 0");
            else if (temporary.charAt(0) != '~') {
                outputs.add("%t" + number++ + " = load i32* %" + temporary);
                outputs.add("%t" + number++ + " = icmp ne i32 %t" + (number-1) + ", 0");
            }else 
                outputs.add("%t" + number++ + " = icmp ne i32 " + temporary.substring(1) + ", 0");

            // is this always true or false? where do we set it false or true?
            outputs.add("%t" + number++ + " = icmp ne i32 %t" + (number - 2) + ", 0");
            outputs.add("br i1 %t" + number++ + ", label %ifbody");
            outputs.add("\n");
            outputs.add("ifbody:");
            return 2;

        }
        return 0;

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
        removePar(s);
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