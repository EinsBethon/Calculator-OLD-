package equation;

import java.io.File;
import java.util.ArrayList;

public class EquationSystem {

    private String solved;

    public EquationSystem(String first, String second) {
        //Get all variables
        ArrayList<Character> vars = new ArrayList<>();
        for (byte i = 0; i < first.length(); i++) {
            if (Character.isLetter(first.charAt(i))) vars.add(first.charAt(i));
        }

        for (byte i = 0; i < second.length(); i++) {
            if (!vars.contains(second.charAt(i)) && Character.isLetter(second.charAt(i))) vars.add(second.charAt(i));
        }

        Equation one = new Linear(first, new File(System.getProperty("user.home") + "/Desktop/calculator/equation/Linear.txt"));
        Equation two = new Linear(second, new File(System.getProperty("user.home") + "/Desktop/calculator/equation/Linear.txt"));
        solved = solve(one, two);
    }

    private String solve(Equation eOne, Equation eTwo) {
        StringBuilder one = new StringBuilder(eOne.getSolved());
        StringBuilder two = new StringBuilder(eTwo.getSolved());


        return eOne.getSolved() + "\n" + eTwo.getSolved();
    }

    public String getSolved() {
        return solved;
    }

}
