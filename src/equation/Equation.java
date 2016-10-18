package equation;

import utils.Utils;

import java.io.File;
import java.util.ArrayList;

public abstract class Equation {

    protected String solved = "";
    protected static final File file = new File(System.getProperty("user.home") + "/Desktop/calculator/equation.txt");

    protected String removeSpaces(String s) {
        StringBuilder string = new StringBuilder(s);
        for (byte i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' ') string.deleteCharAt(i);
        }
        return string.toString();
    }

    protected boolean validEquation(String s) {
        boolean valid = false;
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
        else if (s.contains("=") && !s.substring(s.indexOf("=") + 1, s.length()).contains("=")) valid = true;
        else valid = false;

        return valid;
    }

    public String getSolved() {
        return solved;
    }

    public static String simplifyExpression(String expression) {
        if (expIsSimplified(correctSyntax(expression))) {
            Utils.writeToFile(file, correctSyntax(expression) + " is already simplified.", true);
            return expression;
        }
        StringBuilder data = new StringBuilder(correctSyntax(expression));
        Utils.writeToFile(file, "Simplify in: " + data.toString(), true);

        // Find and solve parenthetical expressions
        if (data.toString().contains("(") || data.toString().contains(")")) {
            ArrayList<String> parentheses = getParentheses(data.toString());
            if (parentheses.get(0).equals("error")) return "Error";

            // Start i at 1 because parentheses will always start with an opening parenthesis since getParentheses checks for errors
            for (byte i = 1, o = 1; i < parentheses.size(); i++) {
                if (i != 0) {
                    if (parentheses.get(i).contains("(")) o++;
                    else
                        o--; // If it doesn't contain an opening parenthesis, checking if it contains a closing one is redundant
                    if (o == 0) {
                        byte one = Byte.parseByte(parentheses.get(0).substring(0, parentheses.get(0).indexOf("("))); // Index of the first parenthesis in the expression
                        byte two = Byte.parseByte(parentheses.get(i).substring(0, parentheses.get(i).indexOf(")"))); // Index of the second parenthesis in the expression

                        // If there is a number outside of a parenthetical expression on the left, distribute that number into the expression
                        if (one > 0 && (Character.isDigit(data.charAt(one - 1)) || Character.isLetter(data.charAt(one - 1)))) {
                            for (byte b = (byte) (one - 1), n = 0; b >= 0; b--) {
                                if (data.charAt(b) == '-') n++;
                                if (n <= 1) {
                                    if (b == 0) {
                                        // Solve any other parenthetical expressions before distributing
                                        if (data.substring(one + 1, two).contains("(")) {
                                            data.replace(one + 1, two, simplifyExpression(data.substring(one + 1, two)));
                                            data.replace(0, data.length(), correctSyntax(data.toString()));
                                            break;
                                        }

                                        data.replace(0, two + 1, distribute(data.substring(one + 1, two), data.substring(0, one), file));
                                        data.replace(0, data.length(), correctSyntax(data.toString()));
                                        break;
                                    } else if (!Character.isLetter(data.charAt(b)) && !Character.isDigit(data.charAt(b)) && data.charAt(b) != '.') {
                                        if (data.substring(one + 1, two).contains("(")) {
                                            data.replace(one + 1, two, simplifyExpression(data.substring(one + 1, two)));
                                            data.replace(0, data.length(), correctSyntax(data.toString()));
                                            break;
                                        }

                                        StringBuilder dist = new StringBuilder(distribute(data.substring(one + 1, two), data.substring(b, one), file));
                                        if (!dist.toString().startsWith("-")) dist.insert(0, '+');
                                        data.replace(b, two + 1, dist.toString());
                                        data.replace(0, data.length(), correctSyntax(data.toString()));
                                        break;
                                    }
                                }
                            }
                        } else if(two < data.length() - 1 && (Character.isLetter(data.charAt(two + 1)) || Character.isDigit(data.charAt(two + 1))) && !Utils.getNextNumber(data.substring(two + 1, data.length())).isEmpty()) { // If there is a number outside of a parenthetical expression on the right, distribute that number into the expression
                            String num = Utils.getNextNumber(data.substring(two + 1, data.length()));
                            data.replace(0, two + 1 /*Add one to two so it is outside the second parenthesis*/ + num.length(), distribute(data.substring(one + 1, two), num, file));
                            data.replace(0, data.length(), correctSyntax(data.toString()));
                            break;
                        } else if(one > 0 && (data.charAt(one - 1) == '-')) { // If there is a negative symbol on the outside of a parenthetical expression, distribute it as -1
                            if (data.substring(one + 1, two).contains("(")) {
                                data.replace(one + 1, two, simplifyExpression(data.substring(one + 1, two)));
                                data.replace(0, data.length(), correctSyntax(data.toString()));
                                break;
                            }

                            data.replace(one - 1, two + 1, distribute(data.substring(one + 1, two), "-1", file));
                            data.replace(0, data.length(), correctSyntax(data.toString()));
                        } else if(two < data.length() - 1 && data.charAt(two + 1) == '/') { // If there is a division operation following the parenthetical expression, divide the expression by the number
                            String num = simplifyExpression("1/" + Utils.getNextNumber(data.substring(two + 2, data.length())));
                            data.replace(0, two + 2 /*Add two to two so it is to the right of the division symbol*/ + num.length(), distribute(data.substring(one + 1, two), num, file));
                            data.replace(0, data.length(), correctSyntax(data.toString()));
                            break;
                        } else {
                            data.replace(one, two + 1, simplifyExpression(data.substring(one + 1, two)));
                            data.replace(0, data.length(), correctSyntax(data.toString()));
                        }

                        if (!expIsSimplified(data.toString()))
                            data.replace(0, data.length(), simplifyExpression(data.toString()));
                        break;
                    }
                }
            }
        }

        // Exponent
        if (opCanBeSimplified(data.toString(), '^')) {
            String[] operands = getOperands(data.toString(), '^');
            Utils.writeToFile(file, "Exponent in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
            double power;
            String variable = "";
            if (Utils.containsLetters(operands[1])) {
                variable = "" + operands[1].charAt(operands[1].length() - 1);
                power = getNumber(operands[1]);
            } else power = Double.parseDouble(operands[1]);

            if (!variable.equals("")) {
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), Math.pow(Double.parseDouble(operands[0]), power) + variable);
                Utils.writeToFile(file, "Exponent out: " + Math.pow(Double.parseDouble(operands[0]), power) + variable, true);
            } else {
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + Math.pow(Double.parseDouble(operands[0]), power));
                Utils.writeToFile(file, "Exponent out: " + Math.pow(Double.parseDouble(operands[0]), power), true);
            }
        }

        // Multiplication and division

        // If there is a multiplication sign before a division sign OR there is a multiplication sign and no division sign
        // Vice-versa for the else if statement
        if (opCanBeSimplified(data.toString(), '*') && (!data.toString().contains("/") || (data.toString().contains("/") && data.indexOf("*") < data.indexOf("/")))) {
            String[] operands = getOperands(data.toString(), '*');
            Utils.writeToFile(file, "Multiplication in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
            double[] numbers = new double[]{getNumber(operands[0]), getNumber(operands[1])};

            String variable = "";

            if (Utils.containsLetters(operands[0]))
                variable = "" + operands[0].charAt(operands[0].length() - 1);
            else if (Utils.containsLetters(operands[1]))
                variable = "" + operands[1].charAt(operands[1].length() - 1);

            // If both operands contain the variable
            if (Utils.containsLetters(operands[0]) && Utils.containsLetters(operands[1])) {
                char one = operands[0].charAt(operands[0].length() - 1);
                char two = operands[1].charAt(operands[1].length() - 1);

                if(one == two) variable += "^2";
                else variable = one + "*" + two;
            }

            // Replace the expression with its simplified form
            data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), numbers[0] * numbers[1] + variable);
            Utils.writeToFile(file, "Multiplication out: " + numbers[0] * numbers[1] + variable, true);
        } else if (opCanBeSimplified(data.toString(), '/') && (!data.toString().contains("*") || (data.toString().contains("*") && data.indexOf("/") < data.indexOf("*")))) {
            String[] operands = getOperands(data.toString(), '/');
            Utils.writeToFile(file, "Division in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
            double[] numbers = new double[]{getNumber(operands[0]), getNumber(operands[1])};
            String variable = "";

            byte b = 0;
            if(Utils.containsLetters(operands[0])) b++;
            if(Utils.containsLetters(operands[1])) b++;

            if(b == 1) {
                if (Utils.containsLetters(operands[0]))
                    variable = "" + operands[0].charAt(operands[0].length() - 1);
                else if (Utils.containsLetters(operands[1]))
                    variable = "" + operands[1].charAt(operands[1].length() - 1);
            }

            // Replace the expression with its simplified form
            data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), (numbers[0] / numbers[1]) + variable);
            Utils.writeToFile(file, "Division out: " + (numbers[0] / numbers[1]) + variable, true);
        }

        // If there is an addition sign before a subtraction sign OR there is an addition sign and no subtraction sign
        // Vice-versa for the else if statement
        if (opCanBeSimplified(data.toString(), '+') && (!opCanBeSimplified(data.toString(), '-') || (opCanBeSimplified(data.toString(), '-') && data.indexOf("-") > data.indexOf("+")))) {
            String[] operands = getOperands(data.toString(), '+');
            if ((Utils.containsLetters(operands[0]) && Utils.containsLetters(operands[1])) || (!Utils.containsLetters(operands[0]) && !Utils.containsLetters(operands[1]))) {
                Utils.writeToFile(file, "Addition in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
                double[] numbers = new double[]{getNumber(operands[0]), getNumber(operands[1])};

                char variable = ' ';
                if (Utils.containsLetters(operands[0]))
                    variable = operands[0].charAt(operands[0].length() - 1);

                StringBuilder outcome = new StringBuilder("");

                if(numbers[0] + numbers[1] != 0) {
                    if (numbers[0] + numbers[1] >= 0) {
                        if (variable != ' ') outcome.append(numbers[0] + numbers[1]).append(variable);
                        else outcome.append(numbers[0] + numbers[1]);
                        if (Integer.parseInt(operands[2]) != 0) outcome.insert(0, '+');
                    } else {
                        if (variable != ' ') outcome.append(numbers[0] + numbers[1]).append(variable);
                        else outcome.append(numbers[0] + numbers[1]);
                    }
                }

                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), outcome.toString());
                Utils.writeToFile(file, "Addition out: " + outcome.toString(), true);
            }
        } else if (opCanBeSimplified(data.toString(), '-') && (!opCanBeSimplified(data.toString(), '+') || (opCanBeSimplified(data.toString(), '+') && data.indexOf("+") > data.indexOf("-")))) {
            String[] operands = getOperands(data.toString(), '-');
            if ((Utils.containsLetters(operands[0]) && Utils.containsLetters(operands[1])) || (!Utils.containsLetters(operands[0]) && !Utils.containsLetters(operands[1]))) {
                Utils.writeToFile(file, "Subtraction in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
                double[] numbers = new double[]{getNumber(operands[0]), getNumber(operands[1])};

                char variable = ' ';
                if (Utils.containsLetters(operands[0]))
                    variable = operands[0].charAt(operands[0].length() - 1);


                String outcome;
                if(numbers[0] - numbers[1] == 0) outcome = "";
                else if(variable != ' ') outcome = "" + (numbers[0] - numbers[1]) + variable;
                else outcome = "" + (numbers[0] - numbers[1]);

                if(numbers[0] - numbers[1] == 0) data.delete(Integer.parseInt(operands[0]), Integer.parseInt(operands[1]));
                else data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), outcome);
                Utils.writeToFile(file, "Subtraction out: " + outcome, true);
            }
        }

        if (!expIsSimplified(data.toString())) data.replace(0, data.length(), simplifyExpression(data.toString()));

        Utils.writeToFile(file, "Simplify out: " + data.toString(), true);
        return correctSyntax(data.toString());
    }

    private static String distribute(String expression, String distribute, File file) {
        StringBuilder data = new StringBuilder(expression.substring(0, expression.indexOf("(") + 1) + simplifyExpression(expression.substring(expression.indexOf("(") + 1, expression.length())));
        Utils.writeToFile(file, "Distribute in: " + data.toString(), true);

        ArrayList<String> numbers = new ArrayList<>(); // Numbers/variables to distribute to
        ArrayList<Character> operators = new ArrayList<>(); // Operators to put in between

        while (!data.toString().isEmpty()) {
            numbers.add(Utils.getNextNumber(data.toString()));
            data.delete(0, Utils.getNextNumber(data.toString()).length());

            if (!data.toString().isEmpty()) {
                operators.add(data.charAt(0));
                data.deleteCharAt(0);
            }
        }

        Utils.writeToFile(file, "Distribute " + distribute + " to: " + numbers, true);

        // A list of the numbers inside the parentheses multiplied by the distributing number
        ArrayList<String> expressions = new ArrayList<>();

        // Multiply the numbers by the distributing number
        for(byte i = 0; i < numbers.size(); i++) {
            expressions.add(simplifyExpression(distribute + "*" + numbers.get(i)));
        }

        // Add the expressions and the operators to the end of data, then return it
        for (byte i = 0; i < expressions.size(); i++) {
            if (i == expressions.size() - 1) data.append(expressions.get(i));
            else
                data.append(expressions.get(i)).append(operators.get(i));
            }

        Utils.writeToFile(file, "Distribute out: " + correctSyntax(data.toString()), true);
        return correctSyntax(data.toString());
    }

    protected static String[] addVariables(String left, String right, char variable) {
        StringBuilder l = new StringBuilder(left);
        StringBuilder r = new StringBuilder(right);

        ArrayList<String> variables = new ArrayList<>();

        for (byte i = 0; i < r.length(); ) {
            String s = Utils.getNextNumber(r.substring(i, r.toString().length()));
            // If the number has the variable in it and is in an addition or subtraction operation OR is alone
            if (s.contains(variable + "") && (i == 0 || (i > 0 && ((r.charAt(i - 1) == '-' || s.startsWith("-")) || r.charAt(i - 1) == '+'))) && (i + s.length() >= r.length() || (i + s.length() < r.length() && (r.charAt(i + s.length()) == '-' || r.charAt(i + s.length()) == '+')))) {
                if (s.startsWith("-")) {
                    variables.add(s.substring(1, s.length()));
                    r.delete(i, i + s.length());
                } else {
                    variables.add("-" + s);
                    if (i > 0) r.delete(i - 1, i + s.length());
                    else r.delete(i, i + s.length());
                }
            } else {
                if (i + s.length() + 1 < r.length()) {
                    if (r.charAt(i + s.length()) == '-') i += s.length();
                    else i += s.length() + 1;
                } else break;
            }
        }

        for (String s : variables) {
            if(s.startsWith("-")) l.append(s);
            else l.append("+").append(s);
        }

        if(l.toString().startsWith("+")) l.deleteCharAt(0);
        if(r.toString().startsWith("+")) r.deleteCharAt(0);

        return new String[]{l.toString(), r.toString()};
    }

    protected static String[] addConstants(String left, String right, char variable) {
        StringBuilder l = new StringBuilder(left);
        StringBuilder r = new StringBuilder(right);

        ArrayList<String> constants = new ArrayList<>();

        for (byte i = 0; i < l.length(); ) {
            String s = Utils.getNextNumber(l.substring(i, l.toString().length()));
            // If the number doesn't have the variable in it and is in an addition or subtraction operation OR is alone
            if (!s.contains(variable + "") && (i == 0 || (i > 0 && ((l.charAt(i - 1) == '-' || s.startsWith("-")) || l.charAt(i - 1) == '+'))) && (i + s.length() >= l.length() || (i + s.length() < l.length() && (l.charAt(i + s.length()) == '-' || l.charAt(i + s.length()) == '+')))) {
                if (s.startsWith("-")) {
                    constants.add(s.substring(1, s.length()));
                    l.delete(i, i + s.length());
                } else {
                    constants.add("-" + s);
                    if (i > 0) l.delete(i - 1, i + s.length());
                    else l.delete(i, i + s.length());
                }
            } else {
                if (i + s.length() + 1 < l.length()) {
                    if (l.charAt(i + s.length()) == '-') i += s.length();
                    else i += s.length() + 1;
                } else break;
            }
        }

        double d = 0;
        for (String s : constants) {
            if(!Utils.containsLetters(s)) d += Double.parseDouble(s);
        }

        if(d != 0) {
            if (d > 0) r.append("+").append(d);
            else r.append(d);
        }

        for (String s : constants) {
            if (Utils.containsLetters(s)) {
                if (s.startsWith("-")) r.append(s);
                else r.append("+").append(s);
            }
        }

        if(l.toString().startsWith("+")) l.deleteCharAt(0);
        if(r.toString().startsWith("+")) r.deleteCharAt(0);

        return new String[]{l.toString(), r.toString()};
    }

    private static boolean expIsSimplified(String data) {
        if (!opCanBeSimplified(data, '^') && !opCanBeSimplified(data, '*') && !opCanBeSimplified(data, '/') && !opCanBeSimplified(data, '+') && !opCanBeSimplified(data, '-') && !data.contains("(")) {
            if (Utils.getNextNumber(data).length() == data.length()) return true;
            String s = data.substring(Utils.getNextNumber(data).length() + 1, data.length());
            if (s.contains("^") || s.contains("*") || s.contains("/") || s.contains("+") || s.indexOf("-") > 0) {
                if (Utils.getNextNumber(s).length() != s.length() && expIsSimplified(s.substring(Utils.getNextNumber(s).length() + 1, s.length())))
                    return true;
            } else return true;
        }
        return false;
    }

    private static boolean opCanBeSimplified(String expression, char operator) {
        if (expression.contains(operator + "")) {
            String[] operands = getOperands(expression, operator);
            if (operands[0] != null && operands[1] != null && operands[2] != null && operands[3] != null) {
                if (operands[0].isEmpty() || operands[1].isEmpty()) return false;

                if((operator == '+' || operator == '-') && !Utils.containsLetters(operands[0]) && !Utils.containsLetters(operands[1])) return true;

                if (operator == '^' && (Utils.containsLetters(operands[0]) || Utils.containsLetters(operands[1])))
                    return false;

                if(Utils.containsLetters(operands[0]) && Utils.containsLetters(operands[1])) {
                    char one = operands[0].charAt(operands[0].length() - 1);
                    char two = operands[1].charAt(operands[1].length() - 1);
                    if (operator == '*' || operator == '/') {
                        if (one == two) return true;
                        else if(getNumber(operands[1]) != 1) return true;
                        else if(getNumber(operands[1]) == 1) return false;
                        return false;
                    } else return one == two;
                }

                if(operator == '+' || operator == '-') return false;
                return true;
            }
        }
        return false;
    }

    private static String correctSyntax(String expression) {
        StringBuilder data = new StringBuilder(expression);

        while (data.toString().contains("--")) {
            data.replace(data.indexOf("--"), data.indexOf("--") + 2, "+");
        }

        while (data.toString().contains("+-")) {
            data.replace(data.indexOf("+-"), data.indexOf("+-") + 2, "-");
        }

        for(byte i = 0; i < data.length(); i++) {
            if(i < data.length() - 1 && Character.isLetter(data.charAt(i)) && (Character.isLetter(data.charAt(i + 1)) || Character.isDigit(data.charAt(i + 1)))) {
                data.insert(i + 1, '*');
            }
        }

        return data.toString();
    }

    public static Double getNumber(String exp) {
        if (!exp.isEmpty()) {
            if (Utils.containsLetters(exp)) {
                if (exp.length() == 1) return 1.0;
                else if (exp.length() == 2 && exp.startsWith("-"))
                    return -1.0;
                return Double.parseDouble(exp.substring(0, exp.length() - 1));
            } else return Double.parseDouble(exp);
        }
        return 0.0;
    }

    private static ArrayList<String> getParentheses(String expression) {
        ArrayList<String> parenthesis = new ArrayList<>();
        for (byte i = 0, o = 0, c = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') o++;
            else if (expression.charAt(i) == ')') c++;
            if (i == expression.length() - 1 && o != c) { // If the loop is on its last iteration and the opening and closing parenthesis counts are not equal
                Utils.popUp("Error!", "You are missing a closing or opening parenthesis.");
                parenthesis.add("error");
                return parenthesis;
            }
            if (c > o) { // If a closing parenthesis is ever used before an opening parenthesis, the expression is invalid
                Utils.popUp("Error!", "You cannot use a closing parenthesis before an opening parenthesis.");
                parenthesis.add("error");
                return parenthesis;
            }
        }

        for (byte i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(' || expression.charAt(i) == ')')
                parenthesis.add(i + "" + expression.charAt(i));
        }
        return parenthesis;
    }

    private static String[] getOperands(String expression, char operator) {
        String[] operands = new String[4];
        /*
        * 0 = the first operand
        * 1 = the second operand
        * 2 = the first index
        * 3 = the second index
        */
        byte index;
        if (expression.startsWith(operator + ""))
            index = (byte) (expression.substring(1, expression.length()).indexOf(operator) + 1);
            // If a negative number starts the expression, find the next occurrence of a subtraction symbol and add one to compensate for the subtraction symbol omitted from the beginning
        else index = (byte) expression.indexOf(operator);

        // Find first operand (Going to the left of the operator in the string)
        for (byte i = (byte) (index - 1); i >= 0; i--) {
            if (i == 0 || (expression.charAt(i) == '-' && (Character.isDigit(expression.charAt(i - 1)) || Character.isLetter(expression.charAt(i - 1))))) {
                operands[0] = expression.substring(i, index);
                operands[2] = i + "";
                break;
            } else if (expression.charAt(i) != '.' && !Character.isDigit(expression.charAt(i)) && !Character.isLetter(expression.charAt(i))) {
                operands[0] = expression.substring(i + 1, index);
                operands[2] = i + 1 + "";
                break;
            }
        }


        // Find second operand (Going to the right of the operator in the string)
        operands[1] = Utils.getNextNumber(expression.substring(index + 1, expression.length()));
        operands[3] = index + 1 + operands[1].length() + "";
        return operands;
    }
}
