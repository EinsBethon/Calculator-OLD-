package equation;

import utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class Side {

    private String s;
    private File file;

    public Side(String side, File file) {
        this.file = file;
        Utils.writeToFile(file, "Data in: " + side, true);
        s = simplify(side);
        Utils.writeToFile(file, "Data out: " + s, true);
    }

    private String simplify(String input) {
        if (simplified(input)) return input;
        StringBuilder data;
        if (!input.contains("(") && !input.contains(")")) data = new StringBuilder(addConstants(input));
        else data = new StringBuilder(input);
        Utils.writeToFile(file, "Simplify in: " + data.toString(), true);

        // Find and solve parenthetical expressions
        if (data.indexOf("(") != -1 && data.indexOf(")") != -1) {
            ArrayList<String> parentheses = getParentheses(data.toString());
            if (parentheses.get(0).equals("error")) return "Error";
            for (byte i = 1, o = 1; i < parentheses.size(); i++) {
                if (parentheses.get(i).startsWith("(")) o++;
                else o--;
                if (o == 0) {
                    byte one = Byte.parseByte(parentheses.get(0).substring(1, parentheses.get(0).length()));
                    byte two = Byte.parseByte(parentheses.get(i).substring(1, parentheses.get(i).length()));
                    parentheses.remove(0);
                    parentheses.remove(i - 1); // Subtract one to compensate for removing index 0, which offsets every index by 1

                    // If distribution is needed, find it and solve it
                    if (one - 1 >= 0) {
                        // If a variable or a constant needs to be distributed into a parenthetical expression
                        if (Character.isLetter(data.charAt(one - 1)) || (containsLetters(data.substring(one + 1, two)) && (Character.isDigit(data.charAt(one - 1)) || Character.isLetter(data.charAt(one - 1)))))
                            data.replace(0, data.length(), distribute(data.substring(0, two)));

                            // If inside and outside of the parentheses are constants, solve the inside, then remove the parentheses and add a multiplication sign if another sign is not already present
                        else { // If no distribution is needed, replace the parenthetical expression with its simplified form
                            data.replace(one, two + 1, simplify(data.substring(one + 1, two)));
                            if (Character.isDigit(data.charAt(one - 1))) data.insert(one, "*");
                        }
                    } else data.replace(0, data.length(), simplify(data.substring(1, data.length() - 1)));
                }
            }
        }

        //Perform operations following PEMDAS
        if (data.indexOf("^") != -1 && canBeSimplified(data.toString(), '^')) {
            Utils.writeToFile(file, "Exponent in: " + data.toString(), true);
            String[] operands = Utils.getOperands(data.toString(), '^');
            if (!containsLetters(operands[0]) && !containsLetters(operands[1]))
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + Math.pow(Double.parseDouble(operands[0]), Double.parseDouble(operands[1])));
            Utils.writeToFile(file, "Exponent out: " + data.toString(), true);
        }

        if (canBeSimplified(data.toString(), '*') && ((data.indexOf("*") != -1 && data.indexOf("/") != -1 && data.indexOf("*") < data.indexOf("/")) || (data.indexOf("*") != -1 && data.indexOf("/") == -1))) {
            Utils.writeToFile(file, "Multiplication in: " + data.toString(), true);
            String[] operands = Utils.getOperands(data.toString(), '*');

            if (containsLetters(operands[0]) || containsLetters(operands[1])) {
                double one, two;

                // Get variables
                ArrayList<Character> vOne = getVariables(operands[0]);
                ArrayList<Character> vTwo = getVariables(operands[1]);

                // Get numbers to multiply
                one = getOperands(operands[0]);
                two = getOperands(operands[1]);

                // Multiply variables
                String variables = "";
                byte o;
                if (vOne.size() > vTwo.size()) o = (byte) vTwo.size();
                else o = (byte) vOne.size();
                for (byte i = 0; i < o; i++) {
                    if (vOne.indexOf(vTwo.get(i)) != -1) {
                        variables += vTwo.get(i) + "^2";
                        vOne.remove(vOne.indexOf(vTwo.get(i)));
                        vTwo.remove(i);
                    } else {
                        variables += vTwo.get(i) + "" + vOne.get(i);
                        vOne.remove(i);
                        vTwo.remove(i);
                    }
                }

                //Add remaining variables that aren't squared
                if (vOne.size() > 0) {
                    for (byte i = 0; i < vOne.size(); i++) {
                        variables += vOne.get(i);
                    }
                }

                if (vTwo.size() > 0) {
                    for (byte i = 0; i < vTwo.size(); i++) {
                        variables += vTwo.get(i);
                    }
                }

                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), (one * two) + variables);
            } else
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (Double.parseDouble(operands[0]) * Double.parseDouble(operands[1])));
            Utils.writeToFile(file, "Multiplication out: " + data.toString(), true);
        } else if (canBeSimplified(data.toString(), '/') && ((data.indexOf("/") != -1 && data.indexOf("*") != -1 && data.indexOf("/") < data.indexOf("*")) || (data.indexOf("/") != -1 && data.indexOf("*") == -1) || (data.indexOf("/") > 0 && !canBeSimplified(data.toString(), '*')))) {
            Utils.writeToFile(file, "Division in: " + data.toString(), true);
            String[] operands = Utils.getOperands(data.toString(), '/');

            if (containsLetters(operands[0]) || containsLetters(operands[1])) {
                double one, two;

                // Get variables
                ArrayList<Character> vOne = getVariables(operands[0]);
                ArrayList<Character> vTwo = getVariables(operands[1]);

                // Get numbers to divide
                one = getOperands(operands[0]);
                two = getOperands(operands[1]);

                // Divide variables
                String variables = "";
                byte o;
                if (vOne.size() > vTwo.size()) o = (byte) vTwo.size();
                else o = (byte) vOne.size();
                for (byte i = 0; i < o; i++) {
                    if (vOne.indexOf(vTwo.get(i)) != -1) {
                        vOne.remove(vOne.indexOf(vTwo.get(i)));
                        vTwo.remove(i);
                    } else {
                        variables += vTwo.get(i) + "" + vOne.get(i);
                        vOne.remove(i);
                        vTwo.remove(i);
                    }
                }

                if (vOne.size() > 0) {
                    for (byte i = 0; i < vOne.size(); i++) {
                        variables += vOne.get(i);
                    }
                }

                if (vTwo.size() > 0) {
                    for (byte i = 0; i < vTwo.size(); i++) {
                        variables += vTwo.get(i);
                    }
                }

                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), (one / two) + variables);
            } else
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (Double.parseDouble(operands[0]) / Double.parseDouble(operands[1])));
            Utils.writeToFile(file, "Division out: " + data.toString(), true);
        }

        if (canBeSimplified(data.toString(), '+') && ((data.indexOf("+") != -1 && data.indexOf("-") != -1 && data.indexOf("+") < data.indexOf("-")) || (data.indexOf("+") != -1 && data.indexOf("-") == -1))) {
            Utils.writeToFile(file, "Addition in: " + data.toString(), true);
            String[] operands = Utils.getOperands(data.toString(), '+');

            if (containsLetters(operands[0]) || containsLetters(operands[1])) {
                // Find if variables on both operands are the same
                String vOne, vTwo;
                vOne = "";
                vTwo = "";
                for (byte i = 0; i < operands[0].length(); i++) {
                    if (Character.isLetter(operands[0].charAt(i)))
                        vOne = operands[0].substring(i, operands[0].length());
                }

                for (byte i = 0; i < operands[1].length(); i++) {
                    if (Character.isLetter(operands[1].charAt(i)))
                        vTwo = operands[1].substring(i, operands[1].length());
                }
                if (vOne.equals(vTwo)) {
                    double one, two;

                    // Get numbers to add
                    one = getOperands(operands[0]);
                    two = getOperands(operands[1]);
                    data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), (one + two) + vOne);
                }
            } else
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (Double.parseDouble(operands[0]) + Double.parseDouble(operands[1])));
            Utils.writeToFile(file, "Addition out: " + data.toString(), true);
        } else if (canBeSimplified(data.toString(), '-') && ((data.indexOf("-") > 0 && data.indexOf("+") != -1 && data.indexOf("-") < data.indexOf("+")) || ((data.indexOf("-") > 0 || data.indexOf("-") != -1) && data.indexOf("+") == -1) || (data.indexOf("-") > 0 && !canBeSimplified(data.toString(), '+')))) {
            Utils.writeToFile(file, "Subtraction in: " + data.toString(), true);
            String[] operands = Utils.getOperands(data.toString(), '-');
            if (containsLetters(operands[0]) || containsLetters(operands[1])) {

                // Find if variables on both operands are the same
                String vOne, vTwo;
                vOne = "";
                vTwo = "";
                for (byte i = 0; i < operands[0].length(); i++) {
                    if (Character.isLetter(operands[0].charAt(i)))
                        vOne = operands[0].substring(i, operands[0].length());
                }

                for (byte i = 0; i < operands[1].length(); i++) {
                    if (Character.isLetter(operands[1].charAt(i)))
                        vTwo = operands[1].substring(i, operands[1].length());
                }
                if (vOne.equals(vTwo)) {
                    double one, two;

                    // Get numbers to subtract
                    one = getOperands(operands[0]);
                    two = getOperands(operands[1]);

                    data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), (one - two) + vOne);
                }
            } else
                data.replace(Integer.parseInt(operands[2]), Integer.parseInt(operands[3]), "" + (Double.parseDouble(operands[0]) - Double.parseDouble(operands[1])));
            Utils.writeToFile(file, "Subtraction out: " + data.toString(), true);
        }

        if (!simplified(data.toString())) {
            if (data.charAt(Utils.getNextNumber(data.toString()).length()) == '^') {
                int index = Utils.getNextNumber(data.toString()).length() + 1 + Utils.getNextNumber(data.substring(Utils.getNextNumber(data.toString()).length() + 1, data.length())).length() + 1;
                data.replace(0, data.length(), data.substring(0, index) + simplify(data.substring(index, data.length())));
            } else
                data.replace(0, data.length(), data.substring(0, Utils.getNextNumber(data.toString()).length() + 1) + simplify(data.substring(Utils.getNextNumber(data.toString()).length() + 1, data.length())));
        }

        Utils.writeToFile(file, "Simplify out: " + data.toString(), true);
        return data.toString();
    }

    private String distribute(String input) {
        StringBuilder data = new StringBuilder(input.substring(0, input.indexOf("(") + 1) + simplify(input.substring(input.indexOf("(") + 1, input.length())));
        Utils.writeToFile(file, "Distribute in: " + data.toString(), true);

        String distribute; // Number/variable to distribute to numbers
        ArrayList<String> numbers = new ArrayList<>(); // Numbers/variables to distribute to
        ArrayList<Character> operators = new ArrayList<>(); // Operators to put in between

        distribute = data.substring(0, data.indexOf("("));
        data.delete(0, distribute.length() + 1);

        while (!data.toString().isEmpty()) {
            numbers.add(Utils.getNextNumber(data.toString()));
            data.delete(0, Utils.getNextNumber(data.toString()).length());

            if (!data.toString().isEmpty()) {
                operators.add(data.charAt(0));
                data.deleteCharAt(0);
            }
        }

        for (byte i = 0; i < numbers.size(); i++) {
            if (i == numbers.size() - 1) data.append(distribute).append("*").append(numbers.get(i));
            else data.append(distribute).append("*").append(numbers.get(i)).append(operators.get(i));
        }

        Utils.writeToFile(file, "Distribute out: " + data.toString(), true);
        return data.toString();
    }

    private boolean simplified(String data) {
        if (!canBeSimplified(data, '^') && !canBeSimplified(data, '*') && !canBeSimplified(data, '/') && !canBeSimplified(data, '+') && !canBeSimplified(data, '-') && !data.contains("(")) {
            if (Utils.getNextNumber(data).length() == data.length()) return true;
            String s = data.substring(Utils.getNextNumber(data).length() + 1, data.length());
            if (s.contains("^") || s.contains("*") || s.contains("/") || s.contains("+") || s.indexOf("-") > 0) {
                if (Utils.getNextNumber(s).length() != s.length() && simplified(s.substring(Utils.getNextNumber(s).length() + 1, s.length())))
                    return true;
            } else return true;
        }
        return false;
    }

    private boolean canBeSimplified(String s, char operator) {
        String[] operands = Utils.getOperands(s, operator);
        if (operands[0] != null && operands[1] != null && operands[2] != null && operands[3] != null) {
            if (operator == '^' && (containsLetters(operands[1]) || containsLetters(operands[0]))) return false;
            if (!operands[0].isEmpty() && !operands[1].isEmpty()) return true;
        }
        return false;
    }

    private ArrayList<String> getParentheses(String s) {
        ArrayList<String> parenthesis = new ArrayList<>();
        for (byte i = 0, o = 0, c = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') o++;
            else if (s.charAt(i) == ')') c++;
            if (i == s.length() - 1 && o != c) { // If the loop is on its last iteration and the opening and closing parenthesis counts are not equal
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

        for (byte i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(' || s.charAt(i) == ')') parenthesis.add(s.charAt(i) + "" + i);
        }
        return parenthesis;
    }

    private ArrayList<Character> getVariables(String s) {
        ArrayList<Character> variables = new ArrayList<>();
        for (byte i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i)) && variables.indexOf(s.charAt(i)) == -1)
                variables.add(s.charAt(i));
        }
        return variables;
    }

    private Double getOperands(String s) {
        if (containsLetters(s)) {
            if (s.length() == 1) return 1.0;
            else if (s.length() == 2 && s.charAt(0) == '-') return -1.0;
            if (s.length() > 1 && s.charAt(0) != '-') {
                for (byte i = 0; i < s.length(); i++) {
                    if (Character.isLetter(s.charAt(i)))
                        return Double.parseDouble(s.substring(0, i));
                }
            }
        } else return Double.parseDouble(s);
        return 0.0;
    }

    private boolean containsLetters(String s) {
        for (byte i = 0; i < s.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (s.charAt(i) == c) return true;
            }
        }
        return false;
    }

    private String addConstants(String s) {
        StringBuilder data = new StringBuilder(s);
        ArrayList<Double> constants = new ArrayList<>();

        if (Utils.getNextNumber(data.toString()).length() != data.length()) {
            if (!containsLetters(Utils.getNextNumber(data.toString()))) {
                if ((data.charAt(Utils.getNextNumber(data.toString()).length()) == '+' || data.charAt(Utils.getNextNumber(data.toString()).length()) == '-')) {
                    constants.add(Double.parseDouble(data.substring(0, Utils.getNextNumber(data.toString()).length())));
                    data.delete(0, Utils.getNextNumber(data.toString()).length());
                }
            }
        }

        for (byte i = 0; i < data.length(); i++) {
            if (i < data.length() - 1) {
                if (data.charAt(i) == '-') {
                    if (!containsLetters(Utils.getNextNumber(data.substring(i, data.length())))) {
                        constants.add(Double.parseDouble(Utils.getNextNumber(data.substring(i, data.length()))));
                        String e = Utils.getNextNumber(data.substring(i, data.length()));
                        data.delete(i, i + e.length());
                    } else i += Utils.getNextNumber(data.substring(i, data.length())).length() - 1;
                } else if (data.charAt(i) == '+') {
                    if (!containsLetters(Utils.getNextNumber(data.substring(i + 1, data.length())))) {
                        constants.add(Double.parseDouble(Utils.getNextNumber(data.substring(i + 1, data.length()))));
                        String e = Utils.getNextNumber(data.substring(i, data.length()));
                        data.delete(i, i + e.length());
                    } else i += Utils.getNextNumber(data.substring(i + 1, data.length())).length() - 1;
                }
            }
        }

        // Add all constants
        double d = 0;
        for (byte i = 0; i < constants.size(); i++) {
            d += constants.get(i);
        }

        // Add the constants to the end of data
        if (d != 0) {
            if (d >= 0) data.append("+").append(d);
            else data.append(d);
        }

        // Delete + signs at the beginning of expressions
        if (!data.toString().isEmpty()) {
            if (data.charAt(0) == '+') data.deleteCharAt(0);
        }
        return data.toString();
    }

    String getString() {
        return s;
    }

}