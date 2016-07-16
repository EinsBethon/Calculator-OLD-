package equation;

import main.Display;
import main.Writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.Pack200;

class Side {

    private String s;

    Side(String side, boolean last) {
        // Test cases
        //2(x+2-6/2) = 10+3x+2-x+4                       2x - 2 = 2x + 16
        // 2(x+(3*5)+4) = 12                             2x + 38
        // 3.125 + x -.125 + 4x = 12                     5x + 3
        // 3+2x-2+x = 12                                 3x + 1
        if (last) {
            Writer.write("Data in: " + side);
            s = simplify(cleanUp(side));
            Writer.write("Data out: " + s);
        } else {
            Writer.write("Data in: " + side);
            s = cleanUp(simplify(side));
            Writer.write("Data out: " + s);
        }
    }

    private String simplify(String input) {
        if (simplified(input)) return input;
        StringBuilder data = new StringBuilder(input);
        Writer.write("Simplify in: " + data.toString());

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
        if (data.indexOf("^") != -1 && validOperation(data.toString(), '^')) {
            Writer.write("Exponent in: " + data.toString());
            String[] nums = getNums(data.toString(), '^');
            if (containsLetters(nums[0]) && containsLetters(nums[1])) {
                double d = 0;
                if (!nums[1].substring(0, nums[1].length() - 1).isEmpty())
                    d = Double.parseDouble(nums[1].substring(0, nums[1].length() - 1));
                else if (nums[1].length() == 1) d = 1;
                else if (nums[1].length() == 2 && nums[1].charAt(0) == '-') d = -1;
                if (nums[0].charAt(nums[0].length() - 1) == nums[1].charAt(nums[1].length() - 1))
                    data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), nums[0] + "^" + d);
            } else if (!containsLetters(nums[0]) && !containsLetters(nums[1])) {
                double d = Math.pow(Double.parseDouble(nums[0]), Double.parseDouble(nums[1]));
                if (d != 1) d++;
                if (Double.toString(d).endsWith(".0"))
                    d = Double.parseDouble(Double.toString(d).substring(0, Double.toString(d).length() - 2));
                data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]) + 1, "" + d);
            }
            if (data.toString().endsWith("^1.0")) data.delete(data.length() - 4, data.length());
            Writer.write("Exponent out: " + data.toString());
        }

        if (validOperation(data.toString(), '*') && ((data.indexOf("*") != -1 && data.indexOf("/") != -1 && data.indexOf("*") < data.indexOf("/")) || (data.indexOf("*") != -1 && data.indexOf("/") == -1))) {
            Writer.write("Multiplication in: " + data.toString());
            String[] nums = getNums(data.toString(), '*');

            if (containsLetters(nums[0]) || containsLetters(nums[1])) {
                double one, two;

                // Get variables
                ArrayList<Character> vOne = getVariables(nums[0]);
                ArrayList<Character> vTwo = getVariables(nums[1]);

                // Get numbers to multiply
                one = getOperands(nums[0]);
                two = getOperands(nums[1]);

                // Multiply variables
                String vars = "";
                byte o;
                if (vOne.size() > vTwo.size()) o = (byte) vTwo.size();
                else o = (byte) vOne.size();
                for (byte i = 0; i < o; i++) {
                    if (vOne.indexOf(vTwo.get(i)) != -1) {
                        vars += vTwo.get(i) + "^2";
                        vOne.remove(vOne.indexOf(vTwo.get(i)));
                        vTwo.remove(i);
                    } else {
                        vars += vTwo.get(i) + "" + vOne.get(i);
                        vOne.remove(i);
                        vTwo.remove(i);
                    }
                }

                //Add remaining variables that aren't squared
                if (vOne.size() > 0) {
                    for (byte i = 0; i < vOne.size(); i++) {
                        vars += vOne.get(i);
                    }
                }

                if (vTwo.size() > 0) {
                    for (byte i = 0; i < vTwo.size(); i++) {
                        vars += vTwo.get(i);
                    }
                }

                data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), (one * two) + vars);
            } else
                data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), "" + (Double.parseDouble(nums[0]) * Double.parseDouble(nums[1])));
            Writer.write("Multiplication out: " + data.toString());
        } else if (validOperation(data.toString(), '/') && ((data.indexOf("/") != -1 && data.indexOf("*") != -1 && data.indexOf("/") < data.indexOf("*")) || (data.indexOf("/") != -1 && data.indexOf("*") == -1) || (data.indexOf("/") > 0 && !validOperation(data.toString(), '*')))) {
            Writer.write("Division in: " + data.toString());
            String[] nums = getNums(data.toString(), '/');

            if (containsLetters(nums[0]) || containsLetters(nums[1])) {
                double one, two;

                // Get variables
                ArrayList<Character> vOne = getVariables(nums[0]);
                ArrayList<Character> vTwo = getVariables(nums[1]);

                // Get numbers to divide
                one = getOperands(nums[0]);
                two = getOperands(nums[1]);

                // Divide variables
                String vars = "";
                byte o;
                if (vOne.size() > vTwo.size()) o = (byte) vTwo.size();
                else o = (byte) vOne.size();
                for (byte i = 0; i < o; i++) {
                    if (vOne.indexOf(vTwo.get(i)) != -1) {
                        vOne.remove(vOne.indexOf(vTwo.get(i)));
                        vTwo.remove(i);
                    } else {
                        vars += vTwo.get(i) + "" + vOne.get(i);
                        vOne.remove(i);
                        vTwo.remove(i);
                    }
                }

                if (vOne.size() > 0) {
                    for (byte i = 0; i < vOne.size(); i++) {
                        vars += vOne.get(i);
                    }
                }

                if (vTwo.size() > 0) {
                    for (byte i = 0; i < vTwo.size(); i++) {
                        vars += vTwo.get(i);
                    }
                }

                data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), (one / two) + vars);
            } else
                data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), "" + (Double.parseDouble(nums[0]) / Double.parseDouble(nums[1])));
            Writer.write("Division out: " + data.toString());
        }

        if (validOperation(data.toString(), '+') && ((data.indexOf("+") != -1 && data.indexOf("-") != -1 && data.indexOf("+") < data.indexOf("-")) || (data.indexOf("+") != -1 && data.indexOf("-") == -1))) {
            Writer.write("Addition in: " + data.toString());
            String[] nums = getNums(data.toString(), '+');

            if (containsLetters(nums[0]) || containsLetters(nums[1])) {
                // Find if variables on both operands are the same
                String vOne, vTwo;
                vOne = "";
                vTwo = "";
                for (byte i = 0; i < nums[0].length(); i++) {
                    if (Character.isLetter(nums[0].charAt(i))) vOne = nums[0].substring(i, nums[0].length());
                }

                for (byte i = 0; i < nums[1].length(); i++) {
                    if (Character.isLetter(nums[1].charAt(i))) vTwo = nums[1].substring(i, nums[1].length());
                }
                if (vOne.equals(vTwo)) {
                    double one, two;

                    // Get numbers to add
                    one = getOperands(nums[0]);
                    two = getOperands(nums[1]);

                    System.out.println(one + ", " + two);

                    data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), (one + two) + vOne);
                }
            } else
                data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), "" + (Double.parseDouble(nums[0]) + Double.parseDouble(nums[1])));
            Writer.write("Addition out: " + data.toString());
        } else if (validOperation(data.toString(), '-') && ((data.indexOf("-") > 0 && data.indexOf("+") != -1 && data.indexOf("-") < data.indexOf("+")) || ((data.indexOf("-") > 0 || data.indexOf("-") != -1) && data.indexOf("+") == -1) || (data.indexOf("-") > 0 && !validOperation(data.toString(), '+')))) {
            Writer.write("Subtraction in: " + data.toString());
            String[] nums = getNums(data.toString(), '-');
            if (containsLetters(nums[0]) || containsLetters(nums[1])) {

                // Find if variables on both operands are the same
                String vOne, vTwo;
                vOne = "";
                vTwo = "";
                for (byte i = 0; i < nums[0].length(); i++) {
                    if (Character.isLetter(nums[0].charAt(i))) vOne = nums[0].substring(i, nums[0].length());
                }

                for (byte i = 0; i < nums[1].length(); i++) {
                    if (Character.isLetter(nums[1].charAt(i))) vTwo = nums[1].substring(i, nums[1].length());
                }
                if (vOne.equals(vTwo)) {
                    double one, two;

                    // Get numbers to subtract
                    one = getOperands(nums[0]);
                    two = getOperands(nums[1]);

                    data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), (one - two) + vOne);
                }
            } else
                data.replace(Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), "" + (Double.parseDouble(nums[0]) - Double.parseDouble(nums[1])));
            Writer.write("Subtraction out: " + data.toString());
        }

        if (!simplified(data.toString()))
            data.replace(0, data.length(), data.substring(0, getNextNum(data.toString()).length() + 1) + simplify(data.substring(getNextNum(data.toString()).length() + 1, data.length())));

        Writer.write("Simplify out: " + data.toString());
        return data.toString();
    }

    private String distribute(String input) {
        StringBuilder data = new StringBuilder(input.substring(0, input.indexOf("(") + 1) + simplify(input.substring(input.indexOf("(") + 1, input.length())));
        Writer.write("Distribute in: " + data.toString());

        String distribute; // Number/variable to distribute to nums
        ArrayList<String> nums = new ArrayList<>(); // Numbers/variables to distribute to
        ArrayList<Character> operators = new ArrayList<>(); // Operators to put in between
        // 2(x + 1) = 2 * x + 1 * x

        distribute = data.substring(0, data.indexOf("("));
        data.delete(0, distribute.length() + 1);

        while (!data.toString().equals("")) {
            nums.add(getNextNum(data.toString()));
            data.delete(0, getNextNum(data.toString()).length());

            if (!data.toString().equals("")) {
                operators.add(data.charAt(0));
                data.deleteCharAt(0);
            }
        }

        for (byte i = 0; i < nums.size(); i++) {
            if (i == nums.size() - 1) data.append(distribute + "*" + nums.get(i));
            else data.append(distribute + "*" + nums.get(i) + operators.get(i));
        }

        Writer.write("Distribute out: " + data.toString());
        return data.toString();
    }

    private boolean simplified(String data) {
        if (!validOperation(data, '^') && !validOperation(data, '*') && !validOperation(data, '/') && !validOperation(data, '+') && !validOperation(data, '-') && !data.contains("(")) {
            if (getNextNum(data).length() == data.length()) return true;
            String s = data.substring(getNextNum(data).length() + 1, data.length());
            if (s.contains("^") || s.contains("*") || s.contains("/") || s.contains("+") || s.indexOf("-") > 0) {
                if (getNextNum(s).length() != s.length() && simplified(s.substring(getNextNum(s).length() + 1, s.length())))
                    return true;
            } else return true;
        }
        return false;
    }

    private boolean validOperation(String s, char operator) {
        String[] nums = getNums(s, operator);
        //System.out.println(operator + nums[0] + ", " + nums[1] + ", " + nums[2] + ", " + nums[3]);
        if (nums[0] != null && nums[1] != null && nums[2] != null && nums[3] != null) {
            if (!nums[0].equals("") && !nums[1].equals("")) return true;
        }
        return false;
    }

    private String[] getNums(String data, char c) {
        String[] nums = new String[4];
        /*
        * 0 = the first operand
        * 1 = the second operand
        * 2 = the first index
        * 3 = the second index
        */
        byte index;
        if (data.startsWith(c + "")) index = (byte) (data.substring(1, data.length()).indexOf(c) + 1);
            // If a negative number starts the expression, find the next occurrence of a subtraction symbol and add one to compensate for the subtraction symbol omitted from the beginning
        else index = (byte) data.indexOf(c);

        // Find first operand (Going to the left of the operator in the string)
        for (byte i = (byte) (index - 1); i >= 0; i--) {
            if ((i != 0 && data.charAt(i) == '-' && !Character.isDigit(data.charAt(i - 1)) && !Character.isLetter(data.charAt(i - 1))) || i == 0) {
                nums[0] = data.substring(i, index);
                if (i == 0) nums[2] = i + "";
                else nums[2] = i + 1 + "";
                break;
            } else if (data.charAt(i) != '.' && !Character.isDigit(data.charAt(i)) && !Character.isLetter(data.charAt(i))) {
                nums[0] = data.substring(i + 1, index);
                nums[2] = i + 1 + "";
                break;
            }
        }

        // Find second operand (Going to the right of the operator in the string)
        for (byte i = (byte) (index + 1); i < data.length(); i++) {
            if (data.charAt(i) == '-' && i == index + 1)
                continue; // If it is the negative symbol starting a negative number, skip the rest of the code this iteration
            else if (data.charAt(i) == '-' && i != index + 1) { // If it is the end of a number and the start of a subtraction operation
                nums[1] = data.substring(index + 1, i);
                nums[3] = i + "";
                break;
            }
            if ((data.charAt(i) != '.' && !Character.isDigit(data.charAt(i)) && !Character.isLetter(data.charAt(i))) || i == data.length() - 1) { // If the character at index i is an operator or it is the end of the string
                if (i == data.length() - 1) {
                    nums[1] = data.substring(index + 1, i + 1);
                    nums[3] = i + 1 + "";
                } else if (i != data.length() - 1) {
                    nums[1] = data.substring(index + 1, i);
                    nums[3] = i + "";
                }
                break;
            }
        }
        return nums;
    }

    private ArrayList<String> getParentheses(String s) {
        ArrayList<String> parenthesis = new ArrayList<>();
        for (byte i = 0, o = 0, c = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') o++;
            else if (s.charAt(i) == ')') c++;
            if (i == s.length() - 1 && o != c) { // If the loop is on its last iteration and the opening and closing parenthesis counts are not equal
                Display.show("Error!", "You are missing a closing or opening parenthesis.");
                parenthesis.add("error");
                return parenthesis;
            }
            if (c > o) { // If a closing parenthesis is ever used before an opening parenthesis, the expression is invalid
                Display.show("Error!", "You cannot use a closing parenthesis before an opening parenthesis.");
                parenthesis.add("error");
                return parenthesis;
            }
        }

        for (byte i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(' || s.charAt(i) == ')') parenthesis.add(s.charAt(i) + "" + i);
        }
        return parenthesis;
    }

    private String getNextNum(String s) {
        if (s.length() == 1) return s;
        for (byte i = 1; i < s.length(); i++) {
            if (i == s.length() - 1) return s;
            if (s.charAt(i) != '.' && !Character.isDigit(s.charAt(i)) && !Character.isLetter(s.charAt(i)))
                return s.substring(0, i);
        }
        return "";
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

    private String cleanUp(String s) {
        Writer.write("Clean up in: " + s);
        StringBuilder data = new StringBuilder(s);

        for (byte i = 0; i < data.length(); i++) {
            if (i < data.length() - 1) {
                if (data.charAt(i) == '+' && data.charAt(i + 1) == '-') data.deleteCharAt(i);
            }
        }

        for (byte i = 0; i < data.length(); i++) {
            if (i < data.length() - 3) {
                if (Character.isDigit(data.charAt(i)) && data.charAt(i + 1) == '*' && Character.isLetter(data.charAt(i + 2)))
                    data.deleteCharAt(i + 1);
            }
        }

        // Get constants
        ArrayList<Double> constants = new ArrayList<>();

        if (getNextNum(data.toString()).length() != data.length()) {
            if (!containsLetters(getNextNum(data.toString()))) {
                if ((data.charAt(getNextNum(data.toString()).length()) == '+' || data.charAt(getNextNum(data.toString()).length()) == '-')) {
                    constants.add(Double.parseDouble(data.substring(0, getNextNum(data.toString()).length())));
                    data.delete(0, getNextNum(data.toString()).length());
                }
            }
        }

        for (byte i = 0; i < data.length(); i++) {
            if (i < data.length() - 1) {
                if (data.charAt(i) == '-') {
                    if (!containsLetters(getNextNum(data.substring(i, data.length())))) {
                        constants.add(Double.parseDouble(getNextNum(data.substring(i, data.length()))));
                        String e = getNextNum(data.substring(i, data.length()));
                        data.delete(i, i + e.length());
                    } else i += getNextNum(data.substring(i, data.length())).length() - 1;
                } else if (data.charAt(i) == '+') {
                    if (!containsLetters(getNextNum(data.substring(i + 1, data.length())))) {
                        constants.add(Double.parseDouble(getNextNum(data.substring(i + 1, data.length()))));
                        String e = getNextNum(data.substring(i, data.length()));
                        data.delete(i, i + e.length());
                    } else i += getNextNum(data.substring(i + 1, data.length())).length() - 1;
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
            if (d >= 0) data.append("+" + d);
            else data.append(d);
        }

        // Delete + signs at the beginning of expressions
        if (data.charAt(0) == '+') data.deleteCharAt(0);

        Writer.write("Clean up out: " + data.toString());
        return simplify(data.toString());
    }

    String getString() {
        return s;
    }

}