package equation;

import utils.Utils;

public class SingleVariable extends Equation {

    public SingleVariable(String equation) {
        if (validEquation(removeSpaces(equation))) solve(removeSpaces(equation));
        else
            Utils.popUp("Invalid equation", "Equation: " + equation + "\nis invalid. Please revise it to continue.", 200, 125);
    }

    // Test cases
    // 6n - 3(-3n + 2) = -24 + 6n                   -2
    // -3(4x + 3) + 4(6x + 1) = 43                  4
    // -5(1 - 5x) + 5(-8x - 2) = -4x - 8x           -5
    // 2(4x-3)-8=4+2x                               3
    // 3n-5=-8(6 + 5n)                              -1
    // (16 + (24 / 3)) * 3 = 9x                     8
    // 4(3x - 1) + 13 = 5x + 2                      -1

    protected void solve(String equation) {
        StringBuilder left = new StringBuilder(simplifyExpression(equation.substring(0, equation.indexOf("=")))); // Left side of the equation
        Utils.writeToFile(file, "", true);
        StringBuilder right = new StringBuilder(simplifyExpression(equation.substring(equation.indexOf("=") + 1, equation.length()))); // Right side of the equation

        if(!left.toString().isEmpty() && !right.toString().isEmpty() && !Utils.containsLetters(left.toString()) && !Utils.containsLetters(right.toString())) {
            if(Double.parseDouble(left.toString()) == Double.parseDouble(right.toString())) solved = "All Real Numbers";
            else solved = "No Solution";

            Utils.writeToFile(file, "Equation: " + left.toString() + "=" + right.toString(), true);
            Utils.writeToFile(file, "Solution: " + solved, true);
            Utils.writeToFile(file, "", true);
            return;
        }

        char c = ' ';
        String string = left.toString() + right.toString();
        for(byte i = 0; i < left.length() + right.length(); i++) {
            if(Character.isLetter(string.charAt(i))) {
                c = string.charAt(i);
                break;
            }
        }

        String[] sides = addVariables(left.toString(), right.toString(), c);

        left = new StringBuilder(sides[0]);
        right = new StringBuilder(sides[1]);

        Utils.writeToFile(file, "Isolate the variable: " + left.toString() + " = " + right.toString(), true);

        sides = addConstants(left.toString(), right.toString(), c);
        left = new StringBuilder(sides[0]);
        right = new StringBuilder(sides[1]);

        Utils.writeToFile(file, "Add all constants to one side: " + left.toString() + " = " + right.toString(), true);

        left.replace(0, left.length(), simplifyExpression(left.toString()));
        right.replace(0, right.length(), simplifyExpression(right.toString()));
        Utils.writeToFile(file, "Simplify both sides: " + left.toString() + " = " + right.toString(), true);

        //If the equation contains the variable and the variable raised to a power ex. 2x(x + 3) = 12 == 2x^2 + 6x = 12
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