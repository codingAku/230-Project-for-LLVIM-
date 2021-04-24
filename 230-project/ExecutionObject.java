import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;

public class ExecutionObject{
    
    public  HashSet<String> declaredVariables = new HashSet<String>();
    public  int number = 1;
    public  ArrayList<String> outputs = new ArrayList<String>();
    public boolean choose = true;

  

    public  int typeChecker(String ece) {
        if (ece.contains("=")) {
            return 1;
        } else if (ece.contains("{")) {
            return 2;
        } else if (ece.contains("}")) {
            return 3;
        } else if (ece.contains("print")) {
            return 4;
        } else {
            return 0;
        }
    }

    public boolean choose(String ece, String name){
        //true ise br yap çık
        boolean flag = true;
        boolean[] ne = new boolean[1];
        outputs.add("br label %choose");
        outputs.add("choose:");
        String[] variables = new String[4];
        String exp = ece.substring(ece.indexOf("(")+1, ece.lastIndexOf(")"));
        StringTokenizer a = new StringTokenizer(exp, ",",false);
        int i=0;
        while(a.hasMoreTokens()){
            String tempE = a.nextToken();
            if(tempE.contains("choose")){
              
            }else{
                tempE= tempE.replaceAll(" ", "");
                variables[i]=removeParan(tempE, "", ne);
                if(variables[i].contains("~")){
                    variables[i] =variables[i].substring(1);


                }else if(!variables[i].contains("%")){
                    variables[i] ="%t" + (number-1);
                }
                i++;

            }
        }
        //outputs.add("\ndefine i32 @choose"+"(i32 "+variables[0]+", i32 "+variables[1] +", i32 "+variables[3] + ", i32 "+variables[4] + ") {");
        //outputs.add("\nentry:");
        outputs.add("%t" + number++ + " = icmp sgt i32 "+ variables[0] + ", 0");
        outputs.add("br i1 %t" + (number-1) + ", label %greaterEnd, label %equalCheck");
        outputs.add("greaterEnd:");
        outputs.add("store i32 " +  variables[2] + ", i32* %" + name);
        outputs.add("br label %end");
        outputs.add("equalCheck:");
        outputs.add("%t" + number++ + " = icmp eq i32 " +variables[0]+ ", 0");
        outputs.add("br i1 %t" + (number-1) + ", label %equalEnd, label %negativeEnd");
        outputs.add("equalEnd:");
        outputs.add("store i32 " +  variables[1] + ", i32* %" + name);
        outputs.add("br label %end");
        outputs.add("negativeEnd:");
        outputs.add("store i32 " +  variables[3] + ", i32* %" + name);
        outputs.add("br label %end");
        choose = false;
        return false;
        
        
        

    }
    public int findChoose(String ece){
        int i=0;
        boolean first = true;
        int index=0;
        for(int k=ece.indexOf("choose"); k<ece.length(); k++){
            if(ece.charAt(k) == ')'){
                first = false;
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

        boolean[] t = new boolean[1];
        t[0] = true;

        if (ece.contains("=")) {
            StringTokenizer dec = new StringTokenizer(ece, "=", false);
            String varName = dec.nextToken();
            String value = dec.nextToken(); 
            String temporary = "";
            temporary = removeParan(value, varName, t);
           // temporary = aticine(value);
            if(temporary.contains("%") && t[0]){
                outputs.add("store i32 " + temporary + ", i32* %" + varName); 
            }
            else if (temporary.charAt(0) != '~' && t[0]) {
                outputs.add("store i32 %t" + (number - 1) + ", i32* %" + varName);// burda bi bokluk var
            } else if(t[0]) {
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
            aticine(test);
            outputs.add("%t" + number++ + " = icmp ne i32 %t" + (number - 2) + ", 0");
            outputs.add("br i1 %t" + (number - 1) + ", label %whbody, label %whend");
            outputs.add("\n");
            outputs.add("whbody:");
            // dont forget whend statement, check
            return 1;

        } else if (cond.nextToken().equals("if")) {
            outputs.add("ifcond:");
            String test = cond.nextToken();
            aticine(test);
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
    // should check if the first letter of varname is num!!
    public int checkTypeValidity(String s) {
        StringTokenizer tok = new StringTokenizer(s, " ", false);
        if (tok.countTokens() != 1)
            return 2;
        s = s.replaceAll(" ", "");
        boolean integer = true;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((int) ch <= 57 && (int) ch >= 48)
                continue; // if integer
            else if (((int) ch <= 122 && (int) ch >= 97) || ((int) ch <= 132 && (int) ch >= 101))
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
    public  String removeParan(String ece, String name, boolean[] check) {
        if(ece.contains("choose")){
            int index = findChoose(ece);
            String temp = ece.substring(ece.indexOf("choose"), index+1);
            check[0] = choose(temp, name);
            ece = ece.substring(0,ece.indexOf("choose")) + "%t"+(number-1) + ece.substring(index+1);
        }
        if(!ece.contains("(")){
            return aticine(ece);
        }

        while(ece.contains("(")){
            System.out.println(ece);
            int a = ece.lastIndexOf("(");
            int b = ece.indexOf(")", ece.lastIndexOf("("));
            String tmp = ece.substring(a+1, b);
            tmp = removeParan(tmp, "", check);
            ece = ece.substring(0,a) + tmp + ( b == ece.length()-1 ? "" : ece.substring(b+1, ece.length()));
            System.out.println(ece);
        }
        return aticine(ece);
    }


    // this method takes an array and does the calculations inside it
    public  void calculation(ArrayList<String> islem) {
        while (islem.size() != 1) {
            int i = 0;

            // boolean integerA = true;
            try {
                int a = Integer.parseInt(islem.get(i));
            } catch (NumberFormatException e) {
                // integerA = false;
                if (islem.get(i).charAt(0) != '%') { // here means the variable is neither an integer nor a %t variable
                    outputs.add("%t" + number + " = load i32* %" + islem.get(i));
                    declaredVariables.add(islem.get(i));
                    islem.set(i, "%t" + number++);
                }
            }
            
            // boolean integerB = true;
            try {
                int a = Integer.parseInt(islem.get(i + 2));
            } catch (NumberFormatException e) {
                if (islem.get(i + 2).charAt(0) != '%') {
                    outputs.add("%t" + number + " = load i32* %" + islem.get(i + 2));
                    declaredVariables.add(islem.get(i+2));
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

                break;
            case "/":
                outputs.add("%t" + number++ + " = sdiv i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0,"%t" + (number - 1));
                islem.remove(i + 1);
                islem.remove(i + 1);

                break;
            case "+":
                outputs.add("%t" + number++ + " = add i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, "%t" + (number - 1));
                islem.remove(i + 1);
                islem.remove(i + 1);

                break;
            case "-":
                outputs.add("%t" + number++ + " = sub i32 " + islem.get(i) + ", " + islem.get(i + 2));
                islem.set(0, "%t" + (number - 1));
                islem.remove(i + 1);
                islem.remove(i + 1);

                break;
            }

        }
    }

    // this method handles calculations while replacing the results with
    // calculations like 4*3 is replaced with 12
    // works fine if there is no pharanthesis in the expression
    public  String aticine(String ali) {
        ArrayList<String> son = new ArrayList<String>();

        // first tokning to + -
        if (ali.contains("+") || ali.contains("-")) { 
            // if(ali.contains("choose")){
            //     int index = findChoose(ali);
            //     String temp = ali.substring(ali.indexOf("choose"), index+1);
            //     choose(temp);
            //     ali = ali.substring(0,ali.indexOf("choose")) + "%t"+(number-1) + ali.substring(index+1);
            // }
            StringTokenizer aticine = new StringTokenizer(ali, "+-", true);
            while (aticine.hasMoreTokens()) {
                ArrayList<String> islem = new ArrayList<String>();
                String ayse = aticine.nextToken();
                if (ayse.contains("*") || ayse.contains("/")) {
                    // if(ayse.contains("choose")){
                    //     int index = findChoose(ayse);
                    //     String temp = ayse.substring(ayse.indexOf("choose"), index+1);
                    //     choose(temp);
                    //     ayse = ayse.substring(0,ali.indexOf("choose")) + "%t"+(number-1) + ayse.substring(index+1);
                    // }
                    // below 5 lines can be done with split, will reduce space and time (wpnt
                    // require islem arraylist)
                    StringTokenizer aticine2 = new StringTokenizer(ayse, "*/", true);
                    while (aticine2.hasMoreTokens()) {
                        String exp = aticine2.nextToken();
                        islem.add(exp);
                        
                    }
                    calculation(islem);
                    // I cant replace the calculation with string here
                    son.add(islem.get(0));
                } else {
                    son.add(ayse);
                }
            }
        } else if (ali.contains("*") || ali.contains("/")) {
            // if(ali.contains("choose")){
            //     int index = findChoose(ali);
            //     String temp = ali.substring(ali.indexOf("choose"), index+1);
            //     choose(temp);
            //     ali = ali.substring(0,ali.indexOf("choose")) + "%t"+(number-1) + ali.substring(index+1);
            // }
            StringTokenizer aticine = new StringTokenizer(ali, "*/", true);
            ArrayList<String> islem = new ArrayList<String>();
            while (aticine.hasMoreTokens()) {
                String exp = aticine.nextToken();
               
                islem.add(exp);
                
            }
            calculation(islem);
            son.add(islem.get(0));

        }else { // for not expreessions like "n" in while in example
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
                }else{
                    return ali;
                }
            }
        }

        calculation(son);
        return son.get(0);
    }
}