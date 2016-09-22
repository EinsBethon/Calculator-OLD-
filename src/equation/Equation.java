package equation;

import utils.Utils;

import java.io.File;
import java.util.ArrayList;

public abstract class Equation {

    protected String solved = "";
    private boolean valid;
    protected File file;
    private static File single = new File(System.getProperty("user.home") + "/Desktop/calculator/equation/Single.txt");
    private static File linear = new File(System.getProperty("user.home") + "/Desktop/calculator/equation/Linear.txt");

    protected String removeSpaces(String s) {
        StringBuilder string = new StringBuilder(s);
        for (byte i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' ') string.deleteCharAt(i);
        }
        return string.toString();
    }

    protected boolean validEquation(String s) {
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
        else if (s.contains("=") && !s.substring(s.indexOf("=") + 1, s.length()).contains("=")) valid = true;
        else valid = false;

        return valid;
    }

    public String getSolved() {
        return solved;
    }

    //Single Variable
    public static String simplifySideSV(String input) {
        if (expIsSimplified(input)) return input;
        StringBuilder data = new StringBuilder(correctOperators(input));
        Utils.writeToFile(single, "Simplify in: " + data.toString(), true);

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
                        byte one = Byte.parseByte(parentheses.get(0).substring(0, parentheses.get(0).indexOf("(")));
                        byte two = Byte.parseByte(parentheses.get(i).substring(0, parentheses.get(i).indexOf(")")));

                        // If there is a number outside of a parenthetical expression, distribute that number into the expression
                        if (one > 0 && (Character.isDigit(data.charAt(one - 1)) || Character.isLetter(data.charAt(one - 1)))) {
                            for (byte b = (byte) (one - 1), n = 0; b >= 0; b--) {
                                if (data.charAt(b) == '-') n++;
                                if (n <= 1) {
                                    if (b == 0) {
                                        // Solve any other parenthetical expressions before distributing
                                        if (data.substring(one + 1, two).contains("(")) {
                                            data.replace(one + 1, two, simplifySideSV(data.substring(one + 1, two)));
                                            data.replace(0, data.length(), correctOperators(data.toString()));
                                            break;
                                        }

                                        data.replace(0, two + 1, distribute(data.substring(one + 1, two), data.substring(0, one), single));
                                        data.replace(0, data.length(), correctOperators(data.toString()));
                                        break;
                                    } else if (!Character.isLetter(data.charAt(b)) && !Character.isDigit(data.charAt(b)) && data.charAt(b) != '.') {
                                        if (data.substring(one + 1, two).contains("(")) {
                                            data.replace(one + 1, two, simplifySideSV(data.substring(one + 1, two)));
                                            data.replace(0, data.length(), correctOperators(data.toString()));
                                            break;
                                        }

                                        StringBuilder dist = new StringBuilder(distribute(data.substring(one + 1, two), data.substring(b, one), single));
                                        if (!dist.toString().startsWith("-")) dist.insert(0, '+');
                                        data.replace(b, two + 1, dist.toString());
                                        data.replace(0, data.length(), correctOperators(data.toString()));
                                        break;
                                    }
                                }
                            }
                            // If there is a negative symbol on the outside of a parenthetical expression, distribute it as -1
                        } else if(one > 0 && (data.charAt(one - 1) == '-')) {
                            if (data.substring(one + 1, two).contains("(")) {
                                data.replace(one + 1, two, simplifySideSV(data.substring(one + 1, two)));
                                data.replace(0, data.length(), correctOperators(data.toString()));
                                break;
                            }

                            data.replace(one - 1, two + 1, distribute(data.substring(one + 1, two), "-1", single));
                            data.replace(0, data.length(), correctOperators(data.toString()));
                        } else {
                            data.replace(one, two + 1, simplifySideSV(data.substring(one + 1, two)));
                            data.replace(0, data.length(), correctOperators(data.toString()));
                        }

                        if (!expIsSimplified(data.toString()))
                            data.replace(0, data.length(), simplifySideSV(data.toString()));
                        break;
                    }
                }
            }
        }

        // Exponent
        if (opCanBeSimplified(data.toString(), '^')) {
            String[] operands = getOperands(data.toString(), '^');
            Utils.writeToFile(single, "Exponent in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
            double power;
            String variable = "";
            if (Utils.containsLetters(operands[1])) {
                variable = operands[1].substring(getVariableIndex(operands[1]), operands[1].length());
                power = getNumber(operands[1]);
            } else power = Double.parseDouble(operands[1]);

            if (!variable.equals("")) {
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), Math.pow(Double.parseDouble(operands[0]), power) + variable);
                Utils.writeToFile(single, "Exponent out: " + Math.pow(Double.parseDouble(operands[0]), power) + variable, true);
            } else {
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + Math.pow(Double.parseDouble(operands[0]), power));
                Utils.writeToFile(single, "Exponent out: " + Math.pow(Double.parseDouble(operands[0]), power), true);
            }
        }

        // Multiplication and division

        // If there is a multiplication sign before a division sign OR there is a multiplication sign and no division sign
        // Vice-versa for the else if statement
        if (opCanBeSimplified(data.toString(), '*') && (!data.toString().contains("/") || (data.toString().contains("/") && data.indexOf("*") < data.indexOf("/")))) {
            String[] operands = getOperands(data.toString(), '*');
            Utils.writeToFile(single, "Multiplication in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
            double[] numbers = new double[]{getNumber(operands[0]), getNumber(operands[1])};
            String variable = "";

            if (Utils.containsLetters(operands[0]))
                variable = operands[0].substring(getVariableIndex(operands[0]), operands[0].length());
            else if (Utils.containsLetters(operands[1]))
                variable = operands[1].substring(getVariableIndex(operands[1]), operands[1].length());

            // If both operands contain the variable
            if (Utils.containsLetters(operands[0]) && Utils.containsLetters(operands[1]))
                variable += "^" + 2; // Append the variable with 2 if both operands contain the variable

            // Replace the expression with its simplified form
            if (!variable.equals("")) {
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), (numbers[0] * numbers[1]) + variable);
                Utils.writeToFile(single, "Multiplication out: " + (numbers[0] * numbers[1]) + variable, true);
            } else {
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (numbers[0] * numbers[1]));
                Utils.writeToFile(single, "Multiplication out: " + (numbers[0] * numbers[1]), true);
            }
        } else if (opCanBeSimplified(data.toString(), '/') && (!data.toString().contains("*") || (data.toString().contains("*") && data.indexOf("/") < data.indexOf("*")))) {
            String[] operands = getOperands(data.toString(), '/');
            Utils.writeToFile(single, "Division in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
            double[] numbers = new double[]{getNumber(operands[0]), getNumber(operands[1])};
            String variable = "";

            if (Utils.containsLetters(operands[0]))
                variable = operands[0].substring(getVariableIndex(operands[0]), operands[0].length());
            else if (Utils.containsLetters(operands[1]))
                variable = operands[1].substring(getVariableIndex(operands[1]), operands[1].length());

            // If both operands contain the variable
            if (Utils.containsLetters(operands[0]) && Utils.containsLetters(operands[1]))
                variable = ""; // Delete the variable because x / x == 1, not a variable

            // Replace the expression with its simplified form
            if (!variable.equals("")) {
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), (numbers[0] / numbers[1]) + variable);
                Utils.writeToFile(single, "Division out: " + (numbers[0] / numbers[1]) + variable, true);
            } else {
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (numbers[0] / numbers[1]));
                Utils.writeToFile(single, "Division out: " + (numbers[0] / numbers[1]), true);
            }
        }

        // If there is an addition sign before a subtraction sign OR there is an addition sign and no subtraction sign
        // Vice-versa for the else if statement
        if (opCanBeSimplified(data.toString(), '+') && (!opCanBeSimplified(data.toString(), '-') || (opCanBeSimplified(data.toString(), '-') && data.indexOf("-") > data.indexOf("+")))) {
            String[] operands = getOperands(data.toString(), '+');
            if ((Utils.containsLetters(operands[0]) && Utils.containsLetters(operands[1])) || (!Utils.containsLetters(operands[0]) && !Utils.containsLetters(operands[1]))) {
                Utils.writeToFile(single, "Addition in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
                double[] numbers = new double[]{getNumber(operands[0]), getNumber(operands[1])};

                char variable = '0';
                if (Utils.containsLetters(operands[0]))
                    variable = operands[0].charAt(operands[0].length() - 1);

                if (variable != '0') {
                    data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (numbers[0] + numbers[1]) + variable);
                    Utils.writeToFile(single, "Addition out: " + (numbers[0] + numbers[1]) + variable, true);
                } else {
                    data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (numbers[0] + numbers[1]));
                    Utils.writeToFile(single, "Addition out: " + (numbers[0] + numbers[1]), true);
                }
            }
        } else if (opCanBeSimplified(data.toString(), '-') && (!opCanBeSimplified(data.toString(), '+') || (opCanBeSimplified(data.toString(), '+') && data.indexOf("+") > data.indexOf("-")))) {
            String[] operands = getOperands(data.toString(), '-');
            if ((Utils.containsLetters(operands[0]) && Utils.containsLetters(operands[1])) || (!Utils.containsLetters(operands[0]) && !Utils.containsLetters(operands[1]))) {
                Utils.writeToFile(single, "Subtraction in: " + data.substring(Integer.parseInt(operands[2]), Integer.parseInt(operands[3])), true);
                double[] numbers = new double[]{getNumber(operands[0]), getNumber(operands[1])};

                char variable = '0';
                if (Utils.containsLetters(operands[0]))
                    variable = operands[0].charAt(operands[0].length() - 1);

                if (variable != '0') {
                    data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (numbers[0] - numbers[1]) + variable);
                    Utils.writeToFile(single, "Subtraction out: " + (numbers[0] - numbers[1]) + variable, true);
                } else {
                    data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (numbers[0] - numbers[1]));
                    Utils.writeToFile(single, "Subtraction out: " + (numbers[0] - numbers[1]), true);
                }
            }
        }

        if (!expIsSimplified(data.toString())) data.replace(0, data.length(), simplifySideSV(data.toString()));

        Utils.writeToFile(single, "Simplify out: " + data.toString(), true);
        return correctOperators(data.toString());
    }

    private static String distribute(String expression, String distribute, File file) {
        StringBuilder data = new StringBuilder(expression.substring(0, expression.indexOf("(") + 1) + simplifySideSV(expression.substring(expression.indexOf("(") + 1, expression.length())));
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
            expressions.add(simplifySideSV(distribute + "*" + numbers.get(i)));
        }

        // Add the expressions and the operators to the end of data, then return it
        for (byte i = 0; i < expressions.size(); i++) {
            if (i == expressions.size() - 1) data.append(expressions.get(i));
            else
                data.append(expressions.get(i)).append(operators.get(i));
            }

        Utils.writeToFile(file, "Distribute out: " + correctOperators(data.toString()), true);
        return correctOperators(data.toString());
    }

    private static String addConstants(String exp) {
        StringBuilder data = new StringBuilder(exp);
        ArrayList<Double> constants = new ArrayList<>();

        for (byte i = 0; i < data.length(); ) {
            String s = Utils.getNextNumber(data.substring(i, data.toString().length()));
            // If the number is in an addition or subtraction operation OR is alone
            if (!Utils.containsLetters(Utils.getNextNumber(s)) && (i == 0 || (i > 0 && (data.charAt(i - 1) == '-' || data.charAt(i - 1) == '+'))) && (i + s.length() >= data.length() || (i + s.length() < data.length() && (data.charAt(i + s.length()) == '-' || data.charAt(i + s.length()) == '+')))) {
                if (s.startsWith("-")) {
                    constants.add(Double.parseDouble(s));
                    data.delete(i, i + s.length());
                } else {
                    constants.add(-Double.parseDouble(s));
                    if (i > 0) data.delete(i - 1, i + s.length());
                    else data.delete(i, i + s.length());
                }
            }

            if (i + s.length() + 1 < data.length()) {
                if (data.charAt(i + s.length() + 1) == '-') i += s.length();
                else i += s.length() + 1;
            } else break;
        }

        double n = 0;
        for (double d : constants) {
            n += d;
        }

        if (n != 0) {
            if (n < 0) data.append(n);
            else data.append("+").append(n);
        }

        return data.toString();
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
                if ((operator == '+' || operator == '-') && ((Utils.containsLetters(operands[0]) && !Utils.containsLetters(operands[1])) || (Utils.containsLetters(operands[1]) && !Utils.containsLetters(operands[0]))))
                    return false;
                if (operator == '^' && (Utils.containsLetters(operands[0]) || Utils.containsLetters(operands[1])))
                    return false;
                if (!operands[0].isEmpty() && !operands[1].isEmpty()) return true;
            }
        }
        return false;
    }

    private static String correctOperators(String expression) {
        StringBuilder data = new StringBuilder(expression);

        while (data.toString().contains("--")) {
            data.replace(data.indexOf("--"), data.indexOf("--") + 2, "+");
        }

        while (data.toString().contains("+-")) {
            data.replace(data.indexOf("+-"), data.indexOf("+-") + 2, "-");
        }

        return data.toString();
    }

    private static Double getNumber(String exp) {
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

    private static int getVariableIndex(String number) {
        for (byte i = 0; i < number.length(); i++) {
            if (Character.isLetter(number.charAt(i))) return i;
        }
        return -1;
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
            if ((i != 0 && expression.charAt(i) == '-' && !Character.isDigit(expression.charAt(i - 1)) && !Character.isLetter(expression.charAt(i - 1))) || i == 0) {
                operands[0] = expression.substring(i, index);
                if (i == 0) operands[2] = i + "";
                else operands[2] = i + 1 + "";
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
