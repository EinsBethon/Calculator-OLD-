package equation;

public class SystemOfEquations extends Equation {

    private String solved;

    public SystemOfEquations(String first, String second) {
        // Find the variables
        char fChar = ' ';
        for(byte i = 0; i < first.length(); i++) {
            if(Character.isLetter(first.charAt(i))) {
                fChar = first.charAt(i);
                break;
            }
        }

        // Search both equations to find the second variable
        char sChar = ' ';
        for(byte i = 0; i < first.length(); i++) {
            if(Character.isLetter(first.charAt(i)) && first.charAt(i) != fChar) {
                sChar = first.charAt(i);
                break;
            }
        }

        if(sChar == ' ') {
            for(byte i = 0; i < second.length(); i++) {
                if(Character.isLetter(second.charAt(i)) && second.charAt(i) != fChar) {
                    sChar = second.charAt(i);
                    break;
                }
            }
        }

        // Simplify the first equation so that the first variable is on the left
        Equation e1 = new Linear(first, fChar);

        // Replace fChar in the second equation with the first equation's solution
        StringBuilder two = new StringBuilder(second);
        while(two.toString().contains(fChar + "")) {
            two.replace(0, two.length(), replaceVariable(two.toString(), e1.getSolved().substring(e1.getSolved().indexOf("=") + 1, e1.getSolved().length()), fChar));
        }

        // Solve the second equation
        // Only needs to be a SingleVariable equation because after replacing the other variable, there is only one remaining
        Equation e2 = new SingleVariable(two.toString());

        if(e2.getSolved().equals("No Solution") || e2.getSolved().equals("All Real Numbers")){
            solved = "No Solution";
            return;
        }

        // Replace the second variable in the first equation with its value
        StringBuilder one = new StringBuilder(e1.getSolved());
        while(one.toString().contains(sChar + "")) {
            one.replace(0, one.length(), replaceVariable(one.toString(), e2.getSolved().substring(e2.getSolved().indexOf("=") + 1, e2.getSolved().length()), sChar));
        }

        // Solve the first equation
        e1 = new SingleVariable(one.toString());

        solved = e1.getSolved() + ", " + e2.getSolved();
    }

    public String getSolved() {
        return solved;
    }

    private String replaceVariable(String equation, String expression, char variable) {
        StringBuilder s = new StringBuilder(equation);
        byte i = (byte) equation.indexOf(variable + "");
        if(i != -1) {
            s.deleteCharAt(i);
            s.insert(i, "(" + expression + ")");
        }

        return s.toString();
    }

}
