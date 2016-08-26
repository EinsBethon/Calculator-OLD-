package equation;

import utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class SingleVariable extends Equation {

    public SingleVariable(String data, File file) {
        this.file = file;
        if (validEquation(removeSpaces(data))) solve(removeSpaces(data));
        else
            Utils.popUp("Invalid equation", "Equation: " + data + "\nis invalid. Please revise it to continue.", 200, 125);
    }

    protected void solve(String input) {
        StringBuilder left = new StringBuilder(Equation.simplifySideSV(input.substring(0, input.indexOf("="))));
        Utils.writeToFile(file, "", true);
        StringBuilder right = new StringBuilder(Equation.simplifySideSV(input.substring(input.indexOf("=") + 1, input.length())));

        //Add all variables to the left
        if (Utils.containsLetters(right.toString())) {
            ArrayList<String> variables = new ArrayList<>();

            for (byte i = 0; i < right.length(); ) {
                String s = Utils.getNextNumber(right.substring(i, right.toString().length()));
                // If the number is in an addition or subtraction operation OR is alone
                if (Utils.containsLetters(s) && (i == 0 || (i > 0 && (right.charAt(i - 1) == '-' || right.charAt(i - 1) == '+'))) && (i + s.length() >= right.length() || (i + s.length() < right.length() && (right.charAt(i + s.length()) == '-' || right.charAt(i + s.length()) == '+')))) {
                    if (s.startsWith("-")) {
                        variables.add(s);
                        right.delete(i, i + s.length());
                    } else {
                        variables.add("-" + s);
                        if (i > 0) right.delete(i - 1, i + s.length());
                        else right.delete(i, i + s.length());
                    }
                }

                if (i + s.length() + 1 < right.length()) {
                    if (right.charAt(i + s.length() + 1) == '-') i += s.length();
                    else i += s.length() + 1;
                } else break;
            }

            for (String s : variables) {
                left.append(s);
            }
        }

        Utils.writeToFile(file, "Isolate the variable: " + left.toString() + " = " + right.toString(), true);

        //Add all constants to the right
        ArrayList<Double> constants = new ArrayList<>();

        for (byte i = 0; i < left.length(); ) {
            String s = Utils.getNextNumber(left.substring(i, left.toString().length()));
            if (!Utils.containsLetters(s) && (i == 0 || (i > 0 && (left.charAt(i - 1) == '-' || left.charAt(i - 1) == '+'))) && (i + s.length() >= left.length() || (i + s.length() < left.length() && (left.charAt(i + s.length()) == '-' || left.charAt(i + s.length()) == '+')))) {
                if (s.startsWith("-")) {
                    constants.add(Double.parseDouble(s));
                    left.delete(i, i + s.length());
                } else {
                    constants.add(-Double.parseDouble(s));
                    if (i > 0) left.delete(i - 1, i + s.length());
                    else left.delete(i, i + s.length());
                }
            }

            if (i + s.length() + 1 < left.length()) {
                if (left.charAt(i + s.length() + 1) == '-') i += s.length();
                else i += s.length() + 1;
            } else break;
        }

        for (double d : constants) {
            double n = 0;
            n += d;
            right.append(n);
        }

        Utils.writeToFile(file, "Add all constants to one side: " + left.toString() + " = " + right.toString(), true);

        left.replace(0, left.length(), Equation.simplifySideSV(left.toString()));
        right.replace(0, right.length(), Equation.simplifySideSV(right.toString()));
        Utils.writeToFile(file, "Simplify both sides: " + left.toString() + " = " + right.toString(), true);

        //If the equation contains the variable and the variable raised to a power ex. 2x(x + 3) = 12
        if (Utils.containsLetters(left.toString())) {
            StringBuilder s = new StringBuilder(left.toString());
            char var = ' ';
            for (byte i = 0; i < left.length(); i++) {
                if (Character.isLetter(left.charAt(i))) {
                    var = left.charAt(i);
                    break;
                }
            }
            s.deleteCharAt(s.indexOf(var + ""));

            if (Utils.containsLetters(s.toString()) && s.toString().contains("^"))
                solved = left.toString() + " = " + right.toString();
        }

        if (solved.equals("")) {
            // Simplify exponents
            if (left.toString().contains("^")) {
                double exponent = Double.parseDouble(left.substring(left.indexOf("^") + 1, left.length()));
                double number = Double.parseDouble(right.toString());
                left.delete(left.indexOf("^"), left.length());
                right.replace(0, right.length(), "" + Utils.nthRoot(number, exponent));
            }

            // Divide both sides by the variable
            if (Utils.containsLetters(left.toString())) {
                double l;
                double r;
                if (!right.toString().isEmpty()) r = Double.parseDouble(right.toString());
                else r = 0;
                char variable = left.charAt(left.length() - 1);

                if (left.length() == 1) l = 1;
                else if (left.length() == 1 && left.toString().startsWith("-")) l = -1;
                else l = Double.parseDouble(left.substring(0, left.length() - 1));

                r /= l;

                solved = variable + " = " + r;
            } else solved = left.toString() + "" + right.toString();
        }

        Utils.writeToFile(file, "Solution: " + solved, true);
        Utils.writeToFile(file, "", true);
    }

}