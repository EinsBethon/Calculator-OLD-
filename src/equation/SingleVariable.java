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
        StringBuilder left = new StringBuilder(Equation.simplifySideSV(input.substring(0, input.indexOf("=")))); // Left side of the equation
        Utils.writeToFile(file, "", true);
        StringBuilder right = new StringBuilder(Equation.simplifySideSV(input.substring(input.indexOf("=") + 1, input.length()))); // Right side of the equation

        //Add all variables to the left
        if (Utils.containsLetters(right.toString())) {
            ArrayList<String> variables = new ArrayList<>();

            for (byte i = 0; i < right.length(); ) {
                String s = Utils.getNextNumber(right.substring(i, right.toString().length()));
                // If the number has the variable in it and is in an addition or subtraction operation OR is alone
                if (Utils.containsLetters(s) && (i == 0 || (i > 0 && ((right.charAt(i - 1) == '-' || s.startsWith("-")) || right.charAt(i - 1) == '+'))) && (i + s.length() >= right.length() || (i + s.length() < right.length() && (right.charAt(i + s.length()) == '-' || right.charAt(i + s.length()) == '+')))) {
                    if (s.startsWith("-")) {
                        variables.add(s.substring(1, s.length()));
                        right.delete(i, i + s.length());
                    } else {
                        variables.add("-" + s);
                        if (i > 0) right.delete(i - 1, i + s.length());
                        else right.delete(i, i + s.length());
                    }
                } else {
                    if (i + s.length() + 1 < right.length()) {
                        if (right.charAt(i + s.length()) == '-') i += s.length();
                        else i += s.length() + 1;
                    } else break;
                }
            }

            // Test cases
            // 6n - 3(-3n + 2) = -24 + 6n                   -2
            // -3(4x + 3) + 4(6x + 1) = 43                  4
            // -5(1 - 5x) + 5(-8x - 2) = -4x - 8x           -5
            // 2(4x-3)-8=4+2x
            // 3n-5=-8(6 + 5n)                              -1
            // (16 + (24 / 3)) * 3 = 9x                     8                   DOESN'T WORK

            for (String s : variables) {
                if(s.startsWith("-")) left.append(s);
                else left.append("+").append(s);
            }
        }

        if(left.toString().startsWith("+")) left.deleteCharAt(0);
        if(right.toString().startsWith("+")) right.deleteCharAt(0);

        Utils.writeToFile(file, "Isolate the variable: " + left.toString() + " = " + right.toString(), true);

        //Add all constants to the right
        ArrayList<Double> constants = new ArrayList<>();

        for (byte i = 0; i < left.length(); ) {
            String s = Utils.getNextNumber(left.substring(i, left.toString().length()));
            // If the number doesn't have the variable in it and is in an addition or subtraction operation OR is alone
            if (!Utils.containsLetters(s) && (i == 0 || (i > 0 && ((left.charAt(i - 1) == '-' || s.startsWith("-")) || left.charAt(i - 1) == '+'))) && (i + s.length() >= left.length() || (i + s.length() < left.length() && (left.charAt(i + s.length()) == '-' || left.charAt(i + s.length()) == '+')))) {
                if (s.startsWith("-")) {
                    constants.add(Double.parseDouble(s.substring(1, s.length())));
                    left.delete(i, i + s.length());
                } else {
                    constants.add(-Double.parseDouble(s));
                    if (i > 0) left.delete(i - 1, i + s.length());
                    else left.delete(i, i + s.length());
                }
            } else {
                if (i + s.length() + 1 < left.length()) {
                    if (left.charAt(i + s.length()) == '-') i += s.length();
                    else i += s.length() + 1;
                } else break;
            }
        }

        for (double d : constants) {
            double n = 0;
            n += d;
            if(n < 0) right.append(n);
            else right.append("+").append(n);
        }

        if(left.toString().startsWith("+")) left.deleteCharAt(0);
        if(right.toString().startsWith("+")) right.deleteCharAt(0);

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
            // Divide both sides by the variable
            if (Utils.containsLetters(left.toString())) {
                double l; // Coefficient of the variable
                double r; // Right side

                if (!right.toString().isEmpty()) r = Double.parseDouble(right.toString());
                else r = 0;

                char variable = ' ';

                // Find the variable
                for (byte i = 0; i < left.length(); i++) {
                    if (Character.isLetter(left.charAt(i))) {
                        variable = left.charAt(i);
                        break;
                    }
                }

                //Find the coefficient of the variable
                if (left.length() == 1) l = 1;
                else if (left.length() == 1 && left.toString().startsWith("-")) l = -1;
                else {
                    if (left.toString().contains("^"))
                        l = Double.parseDouble(left.substring(0, left.indexOf(variable + "")));
                    else l = Double.parseDouble(left.substring(0, left.length() - 1));
                }

                // Divide the right side by the coefficient of the variable
                r /= l;

                right.replace(0, right.length(), r + "");
                // Since both sides were divided by the coefficient of the variable, the coefficient can just be deleted, leaving the variable
                left.delete(0, left.indexOf(variable + ""));
            }

            // Simplify exponents
            if (left.toString().contains("^")) {
                double exponent = Double.parseDouble(left.substring(left.indexOf("^") + 1, left.length()));
                double number = Double.parseDouble(right.toString());
                left.delete(left.indexOf("^"), left.length());
                right.replace(0, right.length(), "" + Utils.nthRoot(number, exponent));
            }

            Utils.writeToFile(file, "Simplify exponents: " + left.toString() + " = " + right.toString(), true);
        }

        solved = left.toString() + " = " + right.toString();
        Utils.writeToFile(file, "Solution: " + solved, true);
        Utils.writeToFile(file, "", true);
    }

}