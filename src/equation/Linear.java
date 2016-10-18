package equation;

import utils.Utils;

public class Linear extends Equation {

    public Linear(String equation, char variable) {
        if (validEquation(removeSpaces(equation))) solve(removeSpaces(equation), variable);
        else {
            Utils.popUp("Invalid equation", "Equation: " + equation + "\nis invalid. Please revise it to continue.", 200, 125);
            solved = "error";
        }
    }

    protected void solve(String equation, char varToSolveFor) {
        // If an equation in a system of equations is a single variable equation, solve it and skip the rest of the code
        if(Utils.variableCount(equation) == 1) {
            solved = new SingleVariable(equation).getSolved();
            Utils.writeToFile(file, "Solved equation: " + solved, true);
            return;
        }

        StringBuilder left = new StringBuilder(simplifyExpression(equation.substring(0, equation.indexOf("="))));
        StringBuilder right = new StringBuilder(simplifyExpression(equation.substring(equation.indexOf("=") + 1, equation.length())));

        String[] sides = addVariables(left.toString(), right.toString(), varToSolveFor);

        left = new StringBuilder(sides[0]);
        right = new StringBuilder(sides[1]);

        Utils.writeToFile(file, "Isolate the variable: " + left.toString() + " = " + right.toString(), true);

        sides = addConstants(left.toString(), right.toString(), varToSolveFor);
        left = new StringBuilder(sides[0]);
        right = new StringBuilder(sides[1]);

        Utils.writeToFile(file, "Add all constants to one side: " + left.toString() + " = " + right.toString(), true);

        left.replace(0, left.length(), simplifyExpression(left.toString()));
        right.replace(0, right.length(), simplifyExpression(right.toString()));
        Utils.writeToFile(file, "Simplify both sides: " + left.toString() + " = " + right.toString(), true);

        // Divide both sides by the variable's coefficient
        double l = getNumber(left.toString());

        right.replace(0, right.length(), simplifyExpression("(" + right.toString() + ")/" + l));
        left.replace(0, left.length(), left.charAt(left.length() - 1) + "");

        solved = left.toString() + " = " + right.toString();

        Utils.writeToFile(file, "Solution: " + solved, true);
    }
}
