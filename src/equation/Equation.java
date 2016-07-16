package equation;

import algebraic.Algebraic;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.Writer;
import standard.Standard;

import java.util.ArrayList;

public class Equation {

    private Stage window;

    private TextField input;
    private Label answer;

    public Equation() {
        init();
    }

    private String removeSpaces(String s) {
        StringBuilder string = new StringBuilder(s);
        for (byte i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' ') string.deleteCharAt(i);
        }
        return string.toString();
    }

    private boolean valid(String s) {
        if (!s.contains("=")) return false;
        for (byte i = 0, num = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '=' || c == 'c' || c == '(' || c == ')' || c == '.')
                num++;
            if (num == s.length() - 1) return true;
        }
        return false;
    }

    private void solve(String input) {
        Side left, right;
        left = new Side(input.substring(0, input.indexOf("=")), false);
        System.out.println();
        right = new Side(input.substring(input.indexOf("=") + 1, input.length()), false);
        Writer.write("\n");

        StringBuilder l = new StringBuilder(left.getString());
        StringBuilder r = new StringBuilder(right.getString());

        char variable = 'x';
        if (containsLetters(l.toString())) {
            for (byte i = 0; i < l.toString().length(); i++) {
                if (Character.isLetter(l.toString().charAt(i))) {
                    variable = l.toString().charAt(i);
                    break;
                }
            }
        }

        if (containsLetters(right.toString())) {
            ArrayList<String> vars = getVariables(r.toString());
            r.replace(0, r.length(), vars.get(vars.size() - 1));
            double v = 0;
            for (byte i = 0; i < vars.size() - 1; i++) {
                v += Double.parseDouble(vars.get(i).substring(0, vars.get(i).length() - 1));
            }
            v *= -1;

            ArrayList<String> constants = getConstants(l.toString());
            l.replace(0, l.length(), constants.get(constants.size() - 1));
            double c = 0;
            for (byte i = 0; i < constants.size() - 1; i++) {
                c += Double.parseDouble(constants.get(i).substring(0, constants.get(i).length() - 1));
            }
            c *= -1;

            if (v != 0) {
                if (v > 0 && !l.toString().endsWith("+")) l.append("+" + v + ("" + variable));
                else l.append(v + ("" + variable));
            }

            if (c != 0 && !r.toString().endsWith("+")) {
                if (c > 0) r.append("+" + c);
                else r.append(c);
            }

            // 3x + 2 = 15 - 4x
            left = new Side(l.toString(), true);
            right = new Side(r.toString(), true);
            double divideBy = Double.parseDouble(left.getString().substring(0, left.getString().length() - 1));
            double rightSide = 0;
            if (divideBy != 0) rightSide = Double.parseDouble(right.getString()) / divideBy;
            else rightSide = Double.parseDouble(right.getString());
            if (divideBy != 0) {
                answer.setText(variable + " = " + rightSide);
                Writer.write(variable + " = " + rightSide);
            } else {
                answer.setText("0 = " + rightSide);
                Writer.write("0 = " + rightSide);
            }
        }
    }

    private ArrayList<String> getVariables(String s) {
        ArrayList<String> vars = new ArrayList<>();
        StringBuilder data = new StringBuilder(s);
        for (byte i = 0; i < data.length(); i++) {
            if (getNextNum(data.toString()).length() != data.length()) {
                if (containsLetters(getNextNum(data.toString()))) {
                    if ((data.charAt(getNextNum(data.toString()).length()) == '+' || data.charAt(getNextNum(data.toString()).length()) == '-')) {
                        vars.add(data.substring(0, getNextNum(data.toString()).length()));
                        data.delete(0, getNextNum(data.toString()).length());
                    }
                }
            }

            if (data.charAt(i) == '-') {
                if (containsLetters(getNextNum(data.substring(i, data.length())))) {
                    vars.add(getNextNum(data.substring(i, data.length())));
                    String e = getNextNum(data.substring(i, data.length()));
                    data.delete(i, i + e.length());
                } else i += getNextNum(data.substring(i, data.length())).length() - 1;
            } else if (data.charAt(i) == '+') {
                if (containsLetters(getNextNum(data.substring(i + 1, data.length())))) {
                    vars.add(getNextNum(data.substring(i + 1, data.length())));
                    String e = getNextNum(data.substring(i, data.length()));
                    data.delete(i, i + e.length());
                } else i += getNextNum(data.substring(i + 1, data.length())).length() - 1;
            }
        }
        vars.add(data.toString());
        return vars;
    }

    private ArrayList<String> getConstants(String s) {
        ArrayList<String> constants = new ArrayList<>();
        StringBuilder data = new StringBuilder(s);
        for (byte i = 0; i < data.length(); i++) {
            if (getNextNum(data.toString()).length() != data.length()) {
                if (!containsLetters(getNextNum(data.toString()))) {
                    if ((data.charAt(getNextNum(data.toString()).length()) == '+' || data.charAt(getNextNum(data.toString()).length()) == '-')) {
                        constants.add(data.substring(0, getNextNum(data.toString()).length()));
                        data.delete(0, getNextNum(data.toString()).length());
                    }
                }
            }

            if (data.charAt(i) == '-') {
                if (!containsLetters(getNextNum(data.substring(i, data.length())))) {
                    constants.add(getNextNum(data.substring(i, data.length())));
                    String e = getNextNum(data.substring(i, data.length()));
                    data.delete(i, i + e.length());
                } else i += getNextNum(data.substring(i, data.length())).length() - 1;
            } else if (data.charAt(i) == '+') {
                if (!containsLetters(getNextNum(data.substring(i + 1, data.length())))) {
                    constants.add(getNextNum(data.substring(i + 1, data.length())));
                    String e = getNextNum(data.substring(i, data.length()));
                    data.delete(i, i + e.length());
                } else i += getNextNum(data.substring(i + 1, data.length())).length() - 1;
            }
        }
        constants.add(data.toString());
        return constants;
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

    private boolean containsLetters(String s) {
        for (byte i = 0; i < s.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (s.charAt(i) == c) return true;
            }
        }
        return false;
    }

    private void init() {
        Scene main;
        Pane layout;

        MenuBar bar;
        Menu view;
        MenuItem standard, algebraic;

        Button solve;
        Font buttons, strings;

        window = new Stage();
        layout = new Pane();
        main = new Scene(layout);
        window.setTitle("Equation Calculator");
        window.setWidth(400);
        window.setHeight(300);
        window.setResizable(false);

        standard = new MenuItem("Standard");
        algebraic = new MenuItem("Algebraic");
        view = new Menu("View");
        view.getItems().addAll(standard, algebraic);
        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        bar.getMenus().add(view);

        buttons = new Font("Consolas", 16);
        strings = new Font("Consolas", 16);

        input = new TextField();
        input.setFont(strings);
        input.setMinWidth(window.getWidth() - 6);
        input.setLayoutY(25);

        solve = new Button("Solve");
        solve.setFont(buttons);
        solve.setMinWidth(window.getWidth() - 6);
        solve.setLayoutY(55);

        // Event handlers
        standard.setOnAction(e -> {
            new Standard();
            window.close();
        });

        algebraic.setOnAction(e -> {
            new Algebraic();
            window.close();
        });

        solve.setOnAction(e -> {
            if (input.getText().length() > 0)
                if (valid(removeSpaces(input.getText()))) solve(removeSpaces(input.getText()));
        });

        answer = new Label();
        answer.setFont(new Font("Consolas", 20));
        answer.setLayoutY(85);

        layout.getChildren().addAll(bar, input, solve, answer);

        window.setScene(main);
        window.show();
    }

}
