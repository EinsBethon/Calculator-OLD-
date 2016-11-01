package calculators;

import expression.Expression;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

import static utils.Utils.*;

public class Algebraic {

    private Stage window;

    private TextArea result;

    private ArrayList<Button> buttons;
    private StringBuilder one, two;
    private boolean displayAnswer;
    private boolean nav;
    private File file;

    public Algebraic() {
        init();
    }

    private void newButton(String text, int x, int y, int minW, int minH, Font font, EventHandler<ActionEvent> e) {
        Button b = new Button(text);
        b.setLayoutX(x);
        b.setLayoutY(y);
        b.setOnAction(e);
        b.setFont(font);
        b.setMinWidth(minW);
        b.setMinHeight(minH);
        buttons.add(b);
    }

    private void setText() {
        result.setText(two.toString() + "\n" + one.toString());
    }

    private String replaceFunctions(String expression) {
        StringBuilder s = new StringBuilder(expression);

        if(s.toString().contains("sqrt(")) {
            String exp = getExpression(s.substring(s.indexOf("sqrt"), s.length()), "sqrt");
            double d = Math.sqrt(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("sqrt("), s.indexOf("sqrt(") + 5 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("cbrt(")) {
            String exp = getExpression(s.substring(s.indexOf("cbrt"), s.length()), "cbrt");
            double d = Math.cbrt(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("cbrt("), s.indexOf("cbrt(") + 5 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("ln(")) {
            String exp = getExpression(s.substring(s.indexOf("ln"), s.length()), "ln");
            double d = Math.log(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("ln("), s.indexOf("ln(") + 3 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("log(")) {
            String exp = getExpression(s.substring(s.indexOf("log"), s.length()), "log");
            double d = Math.log10(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("log("), s.indexOf("log(") + 4 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("sin-1(")) {
            String exp = getExpression(s.substring(s.indexOf("sin-1"), s.length()), "sin-1");
            double d = Math.asin(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("sin-1("), s.indexOf("sin-1(") + 6 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("tan-1(")) {
            String exp = getExpression(s.substring(s.indexOf("tan-1"), s.length()), "tan-1");
            double d = Math.atan(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("tan-1("), s.indexOf("tan-1(") + 6 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("cos-1(")) {
            String exp = getExpression(s.substring(s.indexOf("cos-1"), s.length()), "cos-1");
            double d = Math.acos(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("cos-1("), s.indexOf("cos-1(") + 6 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("sinh(")) {
            String exp = getExpression(s.substring(s.indexOf("sinh"), s.length()), "sinh");
            double d = Math.sinh(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("sinh"), s.indexOf("sinh(") + 5 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("tanh(")) {
            String exp = getExpression(s.substring(s.indexOf("tanh"), s.length()), "tanh");
            double d = Math.tanh(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("tanh"), s.indexOf("tanh(") + 5 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("cosh(")) {
            String exp = getExpression(s.substring(s.indexOf("cosh"), s.length()), "cosh");
            double d = Math.cosh(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("cosh"), s.indexOf("cosh(") + 5 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("sin(")) {
            String exp = getExpression(s.substring(s.indexOf("sin"), s.length()), "sin");
            double d = Math.sin(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("sin("), s.indexOf("sin(") + 4 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("tan(")) {
            String exp = getExpression(s.substring(s.indexOf("tan"), s.length()), "tan");
            double d = Math.tan(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("tan("), s.indexOf("tan(") + 4 + exp.length() + 1, d + "");
        }

        if(s.toString().contains("cos(")) {
            String exp = getExpression(s.substring(s.indexOf("cos"), s.length()), "cos");
            double d = Math.cos(Double.parseDouble(new Expression(exp, file).solved()));
            if(d >= 0) s.replace(s.indexOf("cos("), s.indexOf("cos(") + 4 + exp.length() + 1, d + "");
        }

        return s.toString();
    }

    private String getExpression(String exp, String function) {
        ArrayList<String> parentheses = getParentheses(exp);

        String s = "";

        if(!parentheses.contains("error")) {
            for(int i = 1, o = 1; i < parentheses.size(); i++) {
                if(parentheses.get(i).contains("(")) o++;
                else o--;
                if(o == 0) {
                    s = exp.substring(exp.indexOf(function) + function.length() + 1, Integer.parseInt(parentheses.get(i).substring(0, parentheses.get(i).length() - 1)));
                    break;
                }
            }
        }

        return s;
    }

    private ArrayList<String> getParentheses(String expression) {
        ArrayList<String> parentheses = new ArrayList<>();
        for (int i = 0, o = 0, c = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') o++;
            else if (expression.charAt(i) == ')') c++;
            if (i == expression.length() - 1 && o != c) { // If the loop is on its last iteration and the opening and closing parenthesis counts are not equal
                popUp("Error!", "You are missing a closing or opening parenthesis.");
                parentheses.add("error");
                return parentheses;
            }
            if (c > o) { // If a closing parenthesis is ever used before an opening parenthesis, the expression is invalid
                popUp("Error!", "You cannot use a closing parenthesis before an opening parenthesis.");
                parentheses.add("error");
                return parentheses;
            }
        }

        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(' || expression.charAt(i) == ')')
                parentheses.add(i + "" + expression.charAt(i));
        }
        return parentheses;
    }

    private void init() {
        file = new File(System.getProperty("user.home") + "/Desktop/calculator/algebraic.txt");
        Scene main;
        Pane layout;

        MenuBar bar;
        Menu view, help;
        MenuItem equation, standard, shortcuts, formulas, inequality;

        Font buttonsFont, tArea;

        window = new Stage();
        layout = new Pane();
        main = new Scene(layout);
        window.setTitle("Algebraic Calculator");
        window.setWidth(410);
        window.setHeight(325);

        buttons = new ArrayList<>();

        one = new StringBuilder("0");
        two = new StringBuilder("");

        displayAnswer = false;
        nav = false;

        buttonsFont = new Font("Consolas", 18);
        tArea = new Font("Consolas", 18);

        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        view = new Menu("Vie_w");
        help = new Menu("H_elp");
        shortcuts = new MenuItem("_Shortcuts");
        equation = new MenuItem("_Equation");
        standard = new MenuItem("_Standard");
        inequality = new MenuItem("Ine_quality");
        formulas = new MenuItem("_Formulas");

        standard.setAccelerator(KeyCombination.keyCombination("ALT+S"));
        inequality.setAccelerator(KeyCombination.keyCombination("ALT+Q"));
        equation.setAccelerator(KeyCombination.keyCombination("ALT+E"));
        shortcuts.setAccelerator(KeyCombination.keyCombination("ALT+C"));

        help.getItems().addAll(shortcuts, formulas);
        view.getItems().addAll(standard, equation, inequality);
        bar.getMenus().addAll(view, help);

        // Zero
        newButton("0", 205, (int) window.getHeight() - 70, 74, 35, buttonsFont, e -> {
            if (!one.toString().equals("0")) one.append("0");
            setText();
        });

        // Digits 1-9
        for (int y = 0, z = 0; y < 9; y += 3, z++) {
            for (int x = 0; x < 3; x++) {
                int x1 = x;
                int y1 = y;
                newButton(x + y + 1 + "", 205 + (x * 40), (int) (buttons.get(0).getLayoutY() - 40) - (z * 40), 35, 35, buttonsFont, e -> {
                    if (nav) {
                        two.replace(two.indexOf("<"), two.indexOf("<") + 1, "");
                        nav = false;
                    } else if (displayAnswer) {
                        one.replace(0, one.length(), x1 + y1 + 1 + "");
                        two.replace(0, two.length(), "");
                        setText();
                        displayAnswer = false;
                        return;
                    }
                    if (one.toString().equals("0")) {
                        one.replace(0, one.length(), x1 + y1 + 1 + "");
                        displayAnswer = false;
                    } else one.append((x1 + y1 + 1));
                    setText();
                });
            }
        }

        // Decimal
        newButton(".", (int) buttons.get(0).getLayoutX() + 80, (int) buttons.get(1).getLayoutY() + 40, 35, 35, buttonsFont, e -> {
            if (nav) {
                two.replace(two.indexOf("<"), two.indexOf("<") + 1, "");
                nav = false;
            }
            if (!one.toString().contains(".")) one.append(".");
            setText();
        });

        // Operators
        for (int i = 0; i < 4; i++) {
            char c = ' ';
            if (i == 0) c = '+';
            else if (i == 1) c = '-';
            else if (i == 2) c = '*';
            else if (i == 3) c = '/';
            char c1 = c;
            newButton(c + "", (int) buttons.get(10).getLayoutX() + 40, (int) buttons.get(10).getLayoutY() - (i * 40), 35, 35, buttonsFont, e -> {
                if (nav) {
                    two.replace(two.indexOf("<"), two.indexOf("<") + 1, "");
                    nav = false;
                } else if (displayAnswer) {
                    two.replace(0, two.length(), one.toString() + c1);
                    displayAnswer = false;
                } else if (one.toString().equals("0")) two.append(c1);
                else {
                    one.append(c1);
                    two.append(one.toString());
                }
                one.replace(0, one.length(), "0");
                setText();
            });
        }
        buttons.get(11).setFont(new Font("Consolas", 16));

        // Equals
        newButton("=", (int) buttons.get(11).getLayoutX() + 40, (int) buttons.get(11).getLayoutY() - 40, 35, 74, new Font("Consolas", 17), e -> {
            if (nav) {
                two.replace(two.indexOf("<"), two.indexOf("<") + 1, "");
                nav = false;
            }
            if (!one.toString().equals("0") && !displayAnswer) {
                two.append(one.toString());
            }
            one.replace(0, one.length(), "0");
            setText();
            one.replace(0, one.length(), new Expression(replaceFunctions(two.toString()), file).solved());
            setText();
        });

        // Modulus
        newButton("%", (int) buttons.get(14).getLayoutX() + 40, (int) buttons.get(14).getLayoutY(), 36, 35, new Font("Consolas", 16), e -> {
            if (nav) {
                two.replace(two.indexOf("<"), two.indexOf("<") + 1, "");
                nav = false;
            } else if (displayAnswer) {
                two.replace(0, two.length(), one.toString() + "%");
                displayAnswer = false;
            } else if (one.toString().equals("0")) two.append("%");
            else {
                one.append('%');
                two.append(one.toString());
            }
            one.replace(0, one.length(), "0");
            setText();
        });

        // Clear
        newButton("C", (int) buttons.get(9).getLayoutX(), (int) buttons.get(9).getLayoutY() - 40, 35, 35, new Font("Consolas", 17), e -> {
            one.replace(0, one.length(), "0");
            if (!two.toString().equals("")) two.replace(0, two.length(), "");
            setText();
        });

        // Clear Entry
        newButton("CE", (int) buttons.get(17).getLayoutX() - 40, (int) buttons.get(17).getLayoutY(), 35, 35, new Font("Consolas", 12), e -> {
            one.replace(0, one.toString().length(), "0");
            setText();
        });

        // Backspace
        newButton("<", (int) buttons.get(18).getLayoutX() - 40, (int) buttons.get(18).getLayoutY(), 35, 35, new Font("Consolas", 16), e -> {
            if (nav && !two.toString().startsWith("<")) {
                two.replace(two.indexOf("<") - 1, two.indexOf("<"), "");
                setText();
                return;
            }
            if (!one.toString().equals("0")) one.delete(one.length() - 1, one.length());
            if (one.length() == 0) one.append("0");
            setText();
        });

        // Reciprocal
        newButton("1/x", (int) buttons.get(13).getLayoutX() + 40, (int) buttons.get(13).getLayoutY(), 38, 35, new Font("Consolas", 10), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + 1 / Double.parseDouble(one.toString()));
            setText();
        });

        // Square Root
        newButton("\u221A", (int) buttons.get(16).getLayoutX(), (int) buttons.get(16).getLayoutY() - 40, 37, 35, buttonsFont, e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "sqrt(" + two.toString() + ")");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "sqrt(");
            setText();
        });

        // Sign
        newButton("\u00B1", (int) buttons.get(14).getLayoutX(), (int) buttons.get(14).getLayoutY() - 40, 35, 35, new Font("Consolas", 16), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + -Double.parseDouble(one.toString()));
            setText();
        });

        // Power 10
        newButton("10^x", (int) buttons.get(0).getLayoutX() - 40, (int) buttons.get(0).getLayoutY(), 35, 35, new Font("Consolas", 9), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.pow(10, Double.parseDouble(one.toString())));
            setText();
        });

        // Cube root
        newButton("3\u221Ax", (int) buttons.get(23).getLayoutX(), (int) buttons.get(23).getLayoutY() - 40, 35, 35, new Font("Consolas", 10), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "cbrt(");
            setText();
        });

        // Factorial
        newButton("n!", (int) buttons.get(24).getLayoutX(), (int) buttons.get(24).getLayoutY() - 40, 35, 35, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            int num = (int) Double.parseDouble(one.toString());
            for (int i = num - 1; i > 0; i--) {
                num *= i;
            }
            one.replace(0, one.length(), "" + num);
            setText();
        });

        // Logarithm
        newButton("log", (int) buttons.get(25).getLayoutX(), (int) buttons.get(25).getLayoutY() - 40, 35, 35, new Font("Consolas", 10), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "log(");
            setText();
        });

        // Close parenthesis
        newButton(")", (int) buttons.get(26).getLayoutX(), (int) buttons.get(26).getLayoutY() - 40, 35, 35, buttonsFont, e -> {
            if (!one.toString().equals("0")) two.append(one.toString()).append(")");
            else two.append(")");
            one.replace(0, one.length(), "0");
            setText();
        });

        // Open parenthesis
        newButton("(", (int) buttons.get(27).getLayoutX() - 40, (int) buttons.get(27).getLayoutY(), 35, 35, buttonsFont, e -> {
            if (!one.toString().equals("0")) two.append(one.toString()).append("(");
            else two.append("(");
            one.replace(0, one.length(), "0");
            setText();
        });

        // Square
        newButton("^2", (int) buttons.get(23).getLayoutX() - 40, (int) buttons.get(23).getLayoutY(), 35, 35, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.pow(Double.parseDouble(one.toString()), 2));
            setText();
        });

        // Cube
        newButton("^3", (int) buttons.get(29).getLayoutX(), (int) buttons.get(29).getLayoutY() - 40, 35, 35, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.pow(Double.parseDouble(one.toString()), 3));
            setText();
        });

        // Raise to power
        newButton("^y", (int) buttons.get(30).getLayoutX(), (int) buttons.get(30).getLayoutY() - 40, 35, 35, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            if (!one.toString().equals("0")) two.append(one.toString()).append("^");
            one.replace(0, one.length(), "0");
            setText();
        });

        // Pi
        newButton("\u03C0", (int) buttons.get(31).getLayoutX(), (int) buttons.get(31).getLayoutY() - 40, 35, 35, buttonsFont, e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            if (one.toString().equals("0")) one.replace(0, one.length(), "\u03C0");
            else one.append("*\u03C0");
            setText();
        });

        // Sine
        newButton("sin", (int) buttons.get(29).getLayoutX() - 40, (int) buttons.get(29).getLayoutY(), 35, 35, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "sin(");
            setText();
        });

        // Cosine
        newButton("cos", (int) buttons.get(33).getLayoutX(), (int) buttons.get(33).getLayoutY() - 40, 35, 35, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "cos(");
            setText();
        });

        // Tangent
        newButton("tan", (int) buttons.get(34).getLayoutX(), (int) buttons.get(34).getLayoutY() - 40, 35, 35, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "tan(");
            setText();
        });

        // Hyperbolic sine
        newButton("sinh", (int) buttons.get(33).getLayoutX() - 40, (int) buttons.get(33).getLayoutY(), 35, 35, new Font("Consolas", 9), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "sinh(");
            setText();
        });

        // Hyperbolic cosine
        newButton("cosh", (int) buttons.get(36).getLayoutX(), (int) buttons.get(36).getLayoutY() - 40, 35, 35, new Font("Consolas", 9), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "cosh(");
            setText();
        });

        // Hyperbolic tangent
        newButton("tanh", (int) buttons.get(37).getLayoutX(), (int) buttons.get(37).getLayoutY() - 40, 35, 35, new Font("Consolas", 9), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "tanh(");
            setText();
        });

        // Inverse sine
        newButton("sin-1", (int) buttons.get(36).getLayoutX() - 40, (int) buttons.get(36).getLayoutY(), 35, 35, new Font("Consolas", 8), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "sin-1(");
            setText();
        });

        // Inverse cosine
        newButton("cos-1", (int) buttons.get(39).getLayoutX(), (int) buttons.get(39).getLayoutY() - 40, 35, 35, new Font("Consolas", 8), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "cos-1(");
            setText();
        });

        // Inverse tangent
        newButton("tan-1", (int) buttons.get(40).getLayoutX(), (int) buttons.get(40).getLayoutY() - 40, 35, 35, new Font("Consolas", 8), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "tan-1(");
            setText();
        });

        // Natural logarithm
        newButton("ln", (int) buttons.get(35).getLayoutX(), (int) buttons.get(35).getLayoutY() - 40, 35, 35, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "ln(");
            setText();
        });

        // e^x
        newButton("e^x", (int) buttons.get(42).getLayoutX() - 40, (int) buttons.get(42).getLayoutY(), 35, 35, new Font("Consolas", 10), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "e^");
            setText();
        });

        // Tau
        newButton("T", (int) buttons.get(43).getLayoutX() - 40, (int) buttons.get(43).getLayoutY(), 35, 35, buttonsFont, e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            if (!one.toString().equals("0")) one.append("*T");
            else one.replace(0, one.length(), "T");
            setText();
        });

        // Absolute value
        newButton("|x|", (int) buttons.get(43).getLayoutX() + 40, (int) buttons.get(42).getLayoutY() - 40, 35, 35, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.abs(Double.parseDouble(one.toString())));
            setText();
        });

        // Forward
        newButton("->", (int) buttons.get(43).getLayoutX(), (int) buttons.get(43).getLayoutY() - 40, 35, 35, new Font("Consolas", 12), e -> {
            if (!two.toString().equals("")) {
                if (nav) {
                    int index = two.indexOf("<");
                    two.replace(index, index + 1, "");
                    if (index + 1 < two.length()) two.insert(index + 1, "<");
                    else nav = false;
                }
                setText();
            }
        });

        // Backward
        newButton("<-", (int) buttons.get(43).getLayoutX() - 40, (int) buttons.get(43).getLayoutY() - 40, 35, 35, new Font("Consolas", 12), e -> {
            if (!two.toString().equals("")) {
                if (nav) {
                    int index = two.indexOf("<");
                    two.replace(index, index + 1, "");
                    if (index - 1 > 0) two.insert(index - 1, "<");
                    else nav = false;
                } else {
                    two.insert(two.length(), "<");
                    nav = true;
                }
                setText();
            }
        });

        // Key inputs
        layout.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent k) {
                for (int i = 0; i < 10; i++) {
                    if (k.getText().equals(i + "")) buttons.get(i).fire();
                }
                for (int i = 0; i < 5; i++) {
                    if (k.getText().equals(buttons.get(i + 10).getText())) buttons.get(i + 10).fire();
                }

                if (k.getText().equals("m")) buttons.get(16).fire();
                if (k.getCode() == KeyCode.DELETE) buttons.get(17).fire();
                if (k.getCode() == KeyCode.DECIMAL) buttons.get(10).fire();
                if (k.getCode() == KeyCode.ENTER || k.getCode() == KeyCode.EQUALS) buttons.get(15).fire();
                if (k.getCode() == KeyCode.BACK_SPACE) buttons.get(19).fire();
            }
        });

        equation.setOnAction(e -> {
            new Equation();
            window.close();
        });

        standard.setOnAction(e -> {
            new Standard();
            window.close();
        });

        inequality.setOnAction(e -> {
            new Inequality();
            window.close();
        });

        shortcuts.setOnAction(e -> popUp("Shortcuts", "Modulus: M\nClear: Delete\nSign: S\nSquare Root: Q\nReciprocal: R"));

        formulas.setOnAction(e -> popUp("Formulas", "Area of a rectangle: w * h\nPerimeter of a rectangle: 2w + 2l\nArea of a circle: \u03C0r^2\nCircumference of a circle: 2\u03C0r\nSine: Opposite/Hypotenuse\nCosine: Adjacent/Hypotenuse\nTangent: Opposite/Adjacent", 130, 225));

        result = new TextArea();
        result.setFont(tArea);
        result.setLayoutY(25);
        result.setMinWidth(window.getWidth());
        result.setMaxHeight(60);
        result.setEditable(false);
        result.setFocusTraversable(false);

        setText();

        layout.getChildren().addAll(result, bar);

        for (Button b: buttons) {
            layout.getChildren().add(b);
        }

        window.setScene(main);
        window.show();

        window.setWidth(410);
        window.setHeight(325);
    }

}
