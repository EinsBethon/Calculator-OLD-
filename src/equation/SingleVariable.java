package equation;

import utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class SingleVariable extends Equation {

    public SingleVariable(String data, File file) {
        this.file = file;
        if (valid(removeSpaces(data))) solve(removeSpaces(data));
        else
            Utils.popUp("Invalid equation", "Equation: " + data + "\nis invalid. Please revise it to continue.", 200, 125);
    }

    protected void solve(String input) {
        Side left, right;
        left = new Side(input.substring(0, input.indexOf("=")), file);
        Utils.writeToFile(file, "\n", true);
        right = new Side(input.substring(input.indexOf("=") + 1), file);
        Utils.writeToFile(file, "\n", true);

        StringBuilder l = new StringBuilder(left.getString());
        StringBuilder r = new StringBuilder(right.getString());

        char variable = '0';
        if (Utils.containsLetters(l.toString())) {
            for (byte i = 0; i < l.toString().length(); i++) {
                if (Character.isLetter(l.toString().charAt(i))) {
                    variable = l.toString().charAt(i);
                    break;
                }
            }
        }

        // Get variables and coefficients
        if (Utils.containsLetters(right.toString())) {
            ArrayList<String> vars = getVariables(r.toString());
            r.replace(0, r.length(), vars.get(vars.size() - 1));
            double v = 0;
            for (byte i = 0; i < vars.size() - 1; i++) {
                v += Double.parseDouble(vars.get(i).substring(0, vars.get(i).length() - 1));
            }
            v *= -1;

            // Get constants
            ArrayList<String> constants = getConstants(l.toString());
            l.replace(0, l.length(), constants.get(constants.size() - 1));
            double c = 0;
            for (byte i = 0; i < constants.size() - 1; i++) {
                c += Double.parseDouble(constants.get(i).substring(0, constants.get(i).length() - 1));
            }
            c *= -1;

            // Add all variables and their coefficients to the left side
            if (v != 0) {
                if (v > 0 && !l.toString().endsWith("+")) l.append("+").append(v).append(variable);
                else l.append(v).append(variable);
            }

            // Add constants to right side
            if (c != 0 && !r.toString().endsWith("+")) {
                if (c > 0) r.append("+").append(c);
                else r.append(c);
            }

            if (l.toString().startsWith("+")) l.deleteCharAt(0);

            Utils.writeToFile(file, "Isolate the variable: " + l.toString() + " = " + r.toString(), true);
            Utils.writeToFile(file, "Simplify each side: ", true);

            left = new Side(l.toString(), file);
            right = new Side(r.toString(), file);
            double divideBy;

            // Find the index of the variable
            byte index = -1;
            for (byte i = 0; i < left.getString().length(); i++) {
                if (Character.isLetter(left.getString().charAt(i))) {
                    index = i;
                    break;
                }
            }

            l.replace(0, l.length(), left.getString());
            r.replace(0, r.length(), right.getString());

            Utils.writeToFile(file, "", true);
            Utils.writeToFile(file, "Before finding solution: " + l.toString() + " = " + r.toString(), true);

            if (left.getString().contains("^") && ((left.getString().contains("+") || left.getString().contains("-") || left.getString().contains("*") || left.getString().contains("/")) || left.getString().charAt(left.getString().indexOf("^") + 1) == variable))
                solved = left.getString() + " = " + right.getString();
            else {
                if (left.getString().substring(0, index).equals("-")) divideBy = -1;
                else if (left.getString().substring(0, index).isEmpty())
                    divideBy = 1;
                else divideBy = Double.parseDouble(left.getString().substring(0, index));

                r.replace(0, r.length(), "" + Double.parseDouble(r.toString()) / divideBy);

                if (left.getString().contains("^"))
                    r.replace(0, r.length(), "" + Utils.nthRoot(Double.parseDouble(r.toString()), Integer.parseInt(left.getString().substring(left.getString().indexOf("^") + 1, left.getString().length()))));

                l.delete(l.indexOf("^"), l.length());

                Utils.writeToFile(file, "", true);
                Utils.writeToFile(file, "After simplifying powers: " + l.toString() + " = " + r.toString(), true);
                Utils.writeToFile(file, "", true);

                if (divideBy != 0)
                    solved = variable + " = " + r.toString();
                else
                    solved = "0 = " + r.toString();
            }
        }
        Utils.writeToFile(file, "Solution: " + solved, true);
        Utils.writeToFile(file, "", true);
    }

    private ArrayList<String> getVariables(String s) {
        ArrayList<String> vars = new ArrayList<>();
        StringBuilder data = new StringBuilder(s);
        for (byte i = 0; i < data.length(); i++) {
            if (Utils.getNextNumber(data.toString()).length() != data.length()) {
                if (Utils.containsLetters(Utils.getNextNumber(data.toString()))) {
                    if ((data.charAt(Utils.getNextNumber(data.toString()).length()) == '+' || data.charAt(Utils.getNextNumber(data.toString()).length()) == '-')) {
                        vars.add(data.substring(0, Utils.getNextNumber(data.toString()).length()));
                        data.delete(0, Utils.getNextNumber(data.toString()).length());
                    }
                }
            }

            if (data.charAt(i) == '-') {
                if (Utils.containsLetters(Utils.getNextNumber(data.substring(i, data.length())))) {
                    vars.add(Utils.getNextNumber(data.substring(i, data.length())));
                    String e = Utils.getNextNumber(data.substring(i, data.length()));
                    data.delete(i, i + e.length());
                } else i += Utils.getNextNumber(data.substring(i, data.length())).length() - 1;
            } else if (data.charAt(i) == '+') {
                if (Utils.containsLetters(Utils.getNextNumber(data.substring(i + 1, data.length())))) {
                    vars.add(Utils.getNextNumber(data.substring(i + 1, data.length())));
                    String e = Utils.getNextNumber(data.substring(i, data.length()));
                    data.delete(i, i + e.length());
                } else i += Utils.getNextNumber(data.substring(i + 1, data.length())).length() - 1;
            }
        }
        vars.add(data.toString());
        return vars;
    }

    private ArrayList<String> getConstants(String s) {
        ArrayList<String> constants = new ArrayList<>();
        StringBuilder data = new StringBuilder(s);
        for (byte i = 0; i < data.length(); i++) {
            if (Utils.getNextNumber(data.toString()).length() != data.length()) {
                if (!Utils.containsLetters(Utils.getNextNumber(data.toString()))) {
                    if ((data.charAt(Utils.getNextNumber(data.toString()).length()) == '+' || data.charAt(Utils.getNextNumber(data.toString()).length()) == '-')) {
                        constants.add(data.substring(0, Utils.getNextNumber(data.toString()).length()));
                        data.delete(0, Utils.getNextNumber(data.toString()).length());
                    }
                }
            }

            if (data.charAt(i) == '-') {
                if (!Utils.containsLetters(Utils.getNextNumber(data.substring(i, data.length())))) {
                    constants.add(Utils.getNextNumber(data.substring(i, data.length())));
                    String e = Utils.getNextNumber(data.substring(i, data.length()));
                    data.delete(i, i + e.length());
                } else i += Utils.getNextNumber(data.substring(i, data.length())).length() - 1;
            } else if (data.charAt(i) == '+') {
                if (!Utils.containsLetters(Utils.getNextNumber(data.substring(i + 1, data.length())))) {
                    constants.add(Utils.getNextNumber(data.substring(i + 1, data.length())));
                    String e = Utils.getNextNumber(data.substring(i, data.length()));
                    data.delete(i, i + e.length());
                } else i += Utils.getNextNumber(data.substring(i + 1, data.length())).length() - 1;
            }
        }
        constants.add(data.toString());
        return constants;
    }

}
