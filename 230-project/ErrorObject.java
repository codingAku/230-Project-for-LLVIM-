import java.util.StringTokenizer;

public class ErrorObject {
    public boolean error = false;

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

    private void removePar(String ece) {
        while (ece.contains("(")) {
            int fi = ece.indexOf("(");
            int fis = ece.indexOf(")");
            if (fis == -1) {
                error = true;
                return;
            }
            String ali = ece.substring(fi + 1, fis);
            // this part is just testing
            ali = parser(ali);
            ece = ece.substring(0, fi) + ali + ece.substring(fis + 1);
        }
        if (ece.contains(")"))
            error = true;
        ece = parser(ece);
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
}