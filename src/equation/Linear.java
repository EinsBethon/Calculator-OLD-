package equation;

import utils.Utils;

import java.io.File;

public class Linear extends Equation {

    public Linear(String data, File file) {
        this.file = file;
        if (valid(removeSpaces(data))) solve(removeSpaces(data));
        else {
            Utils.popUp("Invalid equation", "Equation: " + data + "\nis invalid. Please revise it to continue.", 200, 125);
            solved = "error";
        }
    }

    protected void solve(String data) {
        solved = data;
    }

}
