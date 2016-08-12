package equation;

import java.io.File;

public abstract class Equation {

    protected String solved;
    protected boolean valid;
    protected File file;

    protected String removeSpaces(String s) {
        StringBuilder string = new StringBuilder(s);
        for (byte i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' ') string.deleteCharAt(i);
        }
        return string.toString();
    }

    protected void solve(String data) {

    }

    protected boolean valid(String s) {
        valid = false;
        for (byte i = 0, num = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '=' || c == 'c' || c == '(' || c == ')' || c == '.')
                num++;
            if (num == s.length() - 1) {
                valid = true;
                break;
            }
        }
        if (!s.contains("=")) valid = false;
        if (s.contains("=") && !s.substring(s.indexOf("=") + 1, s.length()).contains("=")) valid = true;
        else valid = false;

        return valid;
    }

    public String getSolved() {
        return solved;
    }

}
