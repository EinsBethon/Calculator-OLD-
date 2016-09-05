package algebraic;

import java.io.File;
import java.util.ArrayList;

import equation.EqCalc;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import standard.Standard;
import utils.Utils;

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

    private String solve(String input) {
        StringBuilder data = new StringBuilder(input);
        Utils.writeToFile(file, "In: " + data.toString(), true);
        if (data.toString().contains("\u03C0") || data.toString().contains("T") || data.toString().contains("e^"))
            data.replace(0, data.length(), replaceVars(data.toString()));

        // Find parenthesis
        if (data.toString().contains("(") || data.toString().contains(")")) {
            ArrayList<String> parentheses = getParentheses(input);
            if (parentheses.size() != 0) {
                // Check for missing or misplaced parenthesis
                for (byte i = 0, o = 0, c = 0; i < parentheses.size(); i++) {
                    if (parentheses.get(i).startsWith("(")) o++;
                    else if (parentheses.get(i).startsWith(")")) c++;
                    if (i == parentheses.size() - 1 && o != c) { // If opening and parentheses are not equally frequent, "throw" an error and return
                        Utils.popUp("Error!", "You are either missing an opening\nor closing parenthesis.");
                        return "Error!";
                    } else if (c > o) { // If a closing parenthesis is used before an opening parenthesis, "throw" an error and return
                        Utils.popUp("Error!", "You cannot use a closing parenthesis\nwithout an opening parenthesis first.");
                        return "Error!";
                    }
                }

                // Find matching parenthesis and solve the inside
                for (byte i = 1, o = 0; i < parentheses.size(); i++) {
                    if (o == 0 && parentheses.get(i).startsWith(")")) {
                        byte s = Byte.parseByte(parentheses.get(0).substring(1, parentheses.get(0).length()));
                        byte e = Byte.parseByte(parentheses.get(i).substring(1, parentheses.get(i).length()));
                        parentheses.remove(0);
                        parentheses.remove(i - 1); // Need to subtract one from i because everything moves one spot to the left when removing index 0
                        if (e < data.length()) {
                            if (s - 1 >= 0 && Character.isDigit(data.charAt(s - 1)))
                                data.replace(s, e + 1, "*" + solve(data.substring(s + 1, e))); // Replace parenthetical expression with its simplified form
                            else data.replace(s, e + 1, solve(data.substring(s + 1, e)));
                        }
                        // Need if statement above to keep it from crashing since the final execution of the method causes e to be outside of the bounds of data
                        if (data.toString().contains("("))
                            data.replace(0, data.length(), solve(data.toString())); // If other parentheses exist, simplify them
                        else break;
                    } else if (parentheses.get(i).startsWith("(")) o++;
                    else o--;
                }
            }
        }

        if (data.indexOf("^") != -1) {
            Utils.writeToFile(file, "Exponent in: " + data.toString(), true);
            double[] nums = getNums(data.toString(), '^');
            data.replace((int) nums[2], (int) nums[3], "" + Math.pow(nums[0], nums[1]));
            Utils.writeToFile(file, "Exponent out: " + data.toString(), true);
        }

        if ((data.indexOf("*") != -1 && data.indexOf("/") != -1 && data.indexOf("*") < data.indexOf("/")) || (data.indexOf("*") != -1 && data.indexOf("/") == -1)) {
            Utils.writeToFile(file, "Multiplication in: " + data.toString(), true);
            double[] nums = getNums(data.toString(), '*');
            data.replace((int) nums[2], (int) nums[3], "" + (nums[0] * nums[1]));
            Utils.writeToFile(file, "Multiplication out: " + data.toString(), true);
        } else if ((data.indexOf("/") != -1 && data.indexOf("*") != -1 && data.indexOf("/") < data.indexOf("*")) || (data.indexOf("/") != -1 && data.indexOf("*") == -1)) {
            Utils.writeToFile(file, "Division in: " + data.toString(), true);
            double[] nums = getNums(data.toString(), '/');
            data.replace((int) nums[2], (int) nums[3], "" + (nums[0] / nums[1]));
            Utils.writeToFile(file, "Division out: " + data.toString(), true);
        }

        if ((data.indexOf("+") != -1 && data.indexOf("-") != -1 && data.indexOf("+") < data.indexOf("-")) || (data.indexOf("+") != -1 && data.indexOf("-") == -1)) {
            Utils.writeToFile(file, "Addition in: " + data.toString(), true);
            double[] nums = getNums(data.toString(), '+');
            data.replace((int) nums[2], (int) nums[3], "" + (nums[0] + nums[1]));
            Utils.writeToFile(file, "Addition out: " + data.toString(), true);
        } else if ((data.indexOf("-") > 0 && data.indexOf("+") != -1 && data.indexOf("-") < data.indexOf("+")) || (data.indexOf("-") != -1 && data.indexOf("+") == -1)) {
            // If there is a subtraction symbol for an operation (not a negative number) before an addition symbol OR no addition symbol exists but a subtraction symbol does
            Utils.writeToFile(file, "Subtraction in: " + data.toString(), true);
            double[] nums = getNums(data.toString(), '-');
            data.replace((int) nums[2], (int) nums[3], "" + (nums[0] - nums[1]));
            Utils.writeToFile(file, "Subtraction out: " + data.toString(), true);
        }

        String s = data.toString();
        if (s.contains("^") || s.contains("*") || s.contains("/") || s.contains("+") || (s.contains("-") && s.indexOf("-") != 0))
            data.replace(0, data.length(), solve(s));
        displayAnswer = true;

        Utils.writeToFile(file, "Out: " + data.toString() + System.lineSeparator(), true);
        return data.toString();
    }

    private String replaceVars(String data) {
        StringBuilder s = new StringBuilder(data);
        if (s.toString().contains("\u03C0")) {
            s.replace(s.indexOf("\u03C0"), s.indexOf("\u03C0") + 1, "" + Math.PI);
            if (s.indexOf("\u03C0") != -1) replaceVars(s.toString());
        }

        if (s.toString().contains("T")) {
            s.replace(s.indexOf("T"), s.indexOf("T") + 1, "" + Math.PI * 2);
            if (s.indexOf("T") != -1) replaceVars(s.toString());
        }

        if (s.toString().contains("e^")) {
            s.replace(s.indexOf("e^"), s.indexOf("e") + 1, "" + Math.E);
            if (s.indexOf("e") != -1) replaceVars(s.toString());
        }
        return s.toString();
    }

    private double[] getNums(String data, char c) {
        double[] nums = new double[4];
        /* 0 = the first operand
        * 1 = the second operand
        * 2 = the first index
        * 3 = the second index
        */
        byte index;
        if (data.startsWith(c + "")) index = (byte) (data.substring(1, data.length()).indexOf(c) + 1);
            // If a negative number starts the expression, find the next occurrence of a subtraction symbol and add one to compensate for the subtraction symbol omitted from the beginning
        else index = (byte) data.indexOf(c);

        // Find first operand (Going to the left of the operator in the string)
        for (byte i = (byte) (index - 1); i >= 0; i--) {
            if ((i != 0 && data.charAt(i) == '-' && !Character.isDigit(data.charAt(i - 1))) || i == 0) { // If the number is negative OR it is the beginning of the string
                nums[0] = Double.parseDouble(data.substring(i, index));
                if (i == 0) nums[2] = i;
                else nums[2] = i + 1;
                break;
            } else if (data.charAt(i) != '.' && !Character.isDigit(data.charAt(i))) { // If the character at index i is an operator, but the number is not negative
                nums[0] = Double.parseDouble(data.substring(i + 1, index));
                nums[2] = i + 1;
                break;
            }
        }

        // Find second operand (Going to the right of the operator in the string)
        for (byte i = (byte) (index + 1); i < data.length(); i++) {
            if (data.charAt(i) == '-' && i == index + 1)
                continue; // If it is the negative symbol starting a negative number, skip the rest of the code this iteration
            else if (data.charAt(i) == '-' && i != index + 1) { // If it is the end of a number and the start of a subtraction operation
                nums[1] = Double.parseDouble(data.substring(index + 1, i));
                nums[3] = i;
                break;
            }
            if ((data.charAt(i) != '.' && !Character.isDigit(data.charAt(i))) || i == data.length() - 1) { // If the character at index i is an operator or it is the end of the string
                if (i == data.length() - 1) {
                    nums[1] = Double.parseDouble(data.substring(index + 1, i + 1));
                    nums[3] = i + 1;
                } else {
                    nums[1] = Double.parseDouble(data.substring(index + 1, i));
                    nums[3] = i;
                }
                break;
            }
        }
        return nums;
    }

    private ArrayList<String> getParentheses(String s) {
        ArrayList<String> parenthesis = new ArrayList<>();
        for (byte i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') parenthesis.add("(" + i);
            else if (s.charAt(i) == ')') parenthesis.add(")" + i);
        }
        return parenthesis;
    }

    private void setText() {
        result.setText(two.toString() + "\n" + one.toString());
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

    private void init() {
        file = new File(System.getProperty("user.home") + "/Desktop/calculator/algebraic.txt");
        Scene main;
        Pane layout;

        MenuBar bar;
        Menu view, help;
        MenuItem equation, standard, shortcuts, formulas;

        Font buttonsFont, tArea;

        window = new Stage();
        layout = new Pane();
        main = new Scene(layout);
        window.setTitle("Algebraic Calculator");
        window.setWidth(415);
        window.setHeight(325);
        window.setResizable(false);

        buttons = new ArrayList<>();

        one = new StringBuilder("0");
        two = new StringBuilder("");

        displayAnswer = false;
        nav = false;

        buttonsFont = new Font("Consolas", 18);
        tArea = new Font("Consolas", 18);

        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        view = new Menu("View");
        help = new Menu("Help");
        shortcuts = new MenuItem("Shortcuts");
        equation = new MenuItem("Equation");
        standard = new MenuItem("Standard");
        formulas = new MenuItem("Formulas");

        help.getItems().addAll(shortcuts, formulas);
        view.getItems().addAll(standard, equation);
        bar.getMenus().addAll(view, help);

        // Zero
        newButton("0", 205, (int) window.getHeight() - 70, 74, 0, buttonsFont, e -> {
            if (!one.toString().equals("0")) one.append("0");
            setText();
        });

        // Digits 1-9
        for (byte y = 0, z = 0; y < 9; y += 3, z++) {
            for (byte x = 0; x < 3; x++) {
                byte x1 = x;
                byte y1 = y;
                newButton(x + y + 1 + "", 205 + (x * 40), (int) (buttons.get(0).getLayoutY() - 40) - (z * 40), 0, 0, buttonsFont, e -> {
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
                    } else one.append(x1 + y1 + 1 + "");
                    setText();
                });
            }
        }

        // Decimal
        newButton(".", (int) buttons.get(0).getLayoutX() + 80, (int) buttons.get(1).getLayoutY() + 40, 0, 0, buttonsFont, e -> {
            if (nav) {
                two.replace(two.indexOf("<"), two.indexOf("<") + 1, "");
                nav = false;
            }
            if (!one.toString().contains(".")) one.append(".");
            setText();
        });

        // Operators
        for (byte i = 0; i < 4; i++) {
            char c = ' ';
            if (i == 0) c = '+';
            else if (i == 1) c = '-';
            else if (i == 2) c = '*';
            else if (i == 3) c = '/';
            char c1 = c;
            newButton(c + "", (int) buttons.get(10).getLayoutX() + 40, (int) buttons.get(10).getLayoutY() - (i * 40), 0, 0, buttonsFont, e -> {
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

        // Equals
        newButton("=", (int) buttons.get(11).getLayoutX() + 40, (int) buttons.get(11).getLayoutY() - 40, 34, 74, buttonsFont, e -> {
            if (nav) {
                two.replace(two.indexOf("<"), two.indexOf("<") + 1, "");
                nav = false;
            }
            if (!one.toString().equals("0") && !displayAnswer) {
                two.append(one.toString());
            }
            one.replace(0, one.length(), "0");
            setText();
            one.replace(0, one.length(), solve(two.toString()));
            setText();
        });

        // Modulus
        newButton("%", (int) buttons.get(14).getLayoutX() + 40, (int) buttons.get(14).getLayoutY(), 0, 0, buttonsFont, e -> {
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
        newButton("C", (int) buttons.get(9).getLayoutX(), (int) buttons.get(9).getLayoutY() - 40, 0, 0, buttonsFont, e -> {
            one.replace(0, one.length(), "0");
            if (!two.toString().equals("")) two.replace(0, two.length(), "");
            setText();
        });

        // Clear Entry
        newButton("CE", (int) buttons.get(17).getLayoutX() - 40, (int) buttons.get(17).getLayoutY(), 34, 34, new Font("Consolas", 12), e -> {
            one.replace(0, one.toString().length(), "0");
            setText();
        });

        // Backspace
        newButton("<", (int) buttons.get(18).getLayoutX() - 40, (int) buttons.get(18).getLayoutY(), 0, 0, buttonsFont, e -> {
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
        newButton("1/x", (int) buttons.get(13).getLayoutX() + 40, (int) buttons.get(13).getLayoutY(), 34, 34, new Font("Consolas", 10), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + 1 / Double.parseDouble(one.toString()));
            setText();
        });

        // Square Root
        newButton("\u221A", (int) buttons.get(16).getLayoutX(), (int) buttons.get(16).getLayoutY() - 40, 0, 0, buttonsFont, e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.sqrt(Double.parseDouble(one.toString())));
            setText();
        });

        // Sign
        newButton("\u00B1", (int) buttons.get(14).getLayoutX(), (int) buttons.get(14).getLayoutY() - 40, 0, 0, buttonsFont, e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + -Double.parseDouble(one.toString()));
            setText();
        });

        // Power 10
        newButton("10x", (int) buttons.get(0).getLayoutX() - 40, (int) buttons.get(0).getLayoutY(), 34, 34, new Font("Consolas", 10), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.pow(10, Double.parseDouble(one.toString())));
            setText();
        });

        // Cube root
        newButton("3\u221Ax", (int) buttons.get(23).getLayoutX(), (int) buttons.get(23).getLayoutY() - 40, 34, 34, new Font("Consolas", 10), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.cbrt(Double.parseDouble(one.toString())));
            setText();
        });

        // Factorial
        newButton("n!", (int) buttons.get(24).getLayoutX(), (int) buttons.get(24).getLayoutY() - 40, 34, 34, new Font("Consolas", 12), e -> {
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
        newButton("log", (int) buttons.get(25).getLayoutX(), (int) buttons.get(25).getLayoutY() - 40, 34, 34, new Font("Consolas", 10), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.log10(Double.parseDouble(one.toString())));
            setText();
        });

        // Close parenthesis
        newButton(")", (int) buttons.get(26).getLayoutX(), (int) buttons.get(26).getLayoutY() - 40, 0, 0, buttonsFont, e -> {
            if (!one.toString().equals("0")) two.append(one.toString() + ")");
            else two.append(")");
            one.replace(0, one.length(), "0");
            setText();
        });

        // Open parenthesis
        newButton("(", (int) buttons.get(27).getLayoutX() - 40, (int) buttons.get(27).getLayoutY(), 0, 0, buttonsFont, e -> {
            if (!one.toString().equals("0")) two.append(one.toString() + "(");
            else two.append("(");
            one.replace(0, one.length(), "0");
            setText();
        });

        // Square
        newButton("^2", (int) buttons.get(23).getLayoutX() - 40, (int) buttons.get(23).getLayoutY(), 34, 34, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.pow(Double.parseDouble(one.toString()), 2));
            setText();
        });

        // Cube
        newButton("^3", (int) buttons.get(29).getLayoutX(), (int) buttons.get(29).getLayoutY() - 40, 34, 34, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.pow(Double.parseDouble(one.toString()), 3));
            setText();
        });

        // Raise to power
        newButton("^y", (int) buttons.get(30).getLayoutX(), (int) buttons.get(30).getLayoutY() - 40, 34, 34, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            if (!one.toString().equals("0")) two.append(one.toString() + "^");
            one.replace(0, one.length(), "0");
            setText();
        });

        // Pi
        newButton("\u03C0", (int) buttons.get(31).getLayoutX(), (int) buttons.get(31).getLayoutY() - 40, 34, 0, buttonsFont, e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            if (one.toString().equals("0")) one.replace(0, one.length(), "\u03C0");
            else one.append("*\u03C0");
            setText();
        });

        // Sine
        newButton("sin", (int) buttons.get(29).getLayoutX() - 40, (int) buttons.get(29).getLayoutY(), 34, 34, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.sin(Double.parseDouble(one.toString())));
            setText();
        });

        // Cosine
        newButton("cos", (int) buttons.get(33).getLayoutX(), (int) buttons.get(33).getLayoutY() - 40, 34, 34, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.cos(Double.parseDouble(one.toString())));
            setText();
        });

        // Tangent
        newButton("tan", (int) buttons.get(34).getLayoutX(), (int) buttons.get(34).getLayoutY() - 40, 34, 34, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.tan(Double.parseDouble(one.toString())));
            setText();
        });

        // Hyperbolic sine
        newButton("sinh", (int) buttons.get(33).getLayoutX() - 40, (int) buttons.get(33).getLayoutY(), 34, 34, new Font("Consolas", 9), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.sinh(Double.parseDouble(one.toString())));
            setText();
        });

        // Hyperbolic cosine
        newButton("cosh", (int) buttons.get(36).getLayoutX(), (int) buttons.get(36).getLayoutY() - 40, 34, 34, new Font("Consolas", 9), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.cosh(Double.parseDouble(one.toString())));
            setText();
        });

        // Hyperbolic tangent
        newButton("tanh", (int) buttons.get(37).getLayoutX(), (int) buttons.get(37).getLayoutY() - 40, 34, 34, new Font("Consolas", 9), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.tanh(Double.parseDouble(one.toString())));
            setText();
        });

        // Inverse sine
        newButton("sin-1", (int) buttons.get(36).getLayoutX() - 40, (int) buttons.get(36).getLayoutY(), 34, 34, new Font("Consolas", 8), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.asin(Double.parseDouble(one.toString())));
            setText();
        });

        // Inverse cosine
        newButton("cos-1", (int) buttons.get(39).getLayoutX(), (int) buttons.get(39).getLayoutY() - 40, 34, 34, new Font("Consolas", 8), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.acos(Double.parseDouble(one.toString())));
            setText();
        });

        // Inverse tangent
        newButton("tan-1", (int) buttons.get(40).getLayoutX(), (int) buttons.get(40).getLayoutY() - 40, 34, 34, new Font("Consolas", 8), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.atan(Double.parseDouble(one.toString())));
            setText();
        });

        // Natural logarithm
        newButton("ln", (int) buttons.get(35).getLayoutX(), (int) buttons.get(35).getLayoutY() - 40, 34, 34, new Font("Consolas", 12), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.log(Double.parseDouble(one.toString())));
            setText();
        });

        // e^x
        newButton("e^x", (int) buttons.get(42).getLayoutX() - 40, (int) buttons.get(42).getLayoutY(), 34, 34, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "e^");
            setText();
        });

        // Tau
        newButton("T", (int) buttons.get(43).getLayoutX() - 40, (int) buttons.get(43).getLayoutY(), 34, 0, buttonsFont, e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            if (!one.toString().equals("0")) one.append("*T");
            else one.replace(0, one.length(), "T");
            setText();
        });

        // Absolute value
        newButton("|x|", (int) buttons.get(43).getLayoutX() + 40, (int) buttons.get(42).getLayoutY() - 40, 34, 35, new Font("Consolas", 11), e -> {
            if (displayAnswer) {
                two.replace(0, two.length(), "");
                displayAnswer = false;
            }
            one.replace(0, one.length(), "" + Math.abs(Double.parseDouble(one.toString())));
            setText();
        });

        // Forward
        newButton("->", (int) buttons.get(43).getLayoutX(), (int) buttons.get(43).getLayoutY() - 40, 34, 35, new Font("Consolas", 12), e -> {
            if (!two.toString().equals("")) {
                if (nav) {
                    byte index = (byte) two.indexOf("<");
                    two.replace(index, index + 1, "");
                    if (index + 1 < two.length()) two.insert(index + 1, "<");
                    else nav = false;
                }
                setText();
            }
        });

        // Backward
        newButton("<-", (int) buttons.get(43).getLayoutX() - 40, (int) buttons.get(43).getLayoutY() - 40, 34, 35, new Font("Consolas", 12), e -> {
            if (!two.toString().equals("")) {
                if (nav) {
                    byte index = (byte) two.indexOf("<");
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
                for (byte i = 0; i < 10; i++) {
                    if (k.getText().equals(i + "")) buttons.get(i).fire();
                }
                for (byte i = 0; i < 5; i++) {
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
            new EqCalc();
            window.close();
        });

        standard.setOnAction(e -> {
            new Standard();
            window.close();
        });

        shortcuts.setOnAction(e -> Utils.popUp("Shortcuts", "Modulus: M\nClear: Delete\nSign: S\nSquare Root: Q\nReciprocal: R"));

        formulas.setOnAction(e -> Utils.popUp("Formulas", "Area of a rectangle: w * h\nPerimeter of a rectangle: 2w + 2l\nArea of a circle: \u03C0r^2\nCircumference of a circle: 2\u03C0r\nSine: Opposite/Hypotenuse\nCosine: Adjacent/Hypotenuse\nTangent: Opposite/Adjacent", 130, 225));

        result = new TextArea();
        result.setFont(tArea);
        result.setLayoutY(25);
        result.setMinWidth(window.getWidth());
        result.setMaxHeight(60);
        result.setEditable(false);
        result.setFocusTraversable(false);
        setText();

        layout.getChildren().addAll(result, bar);

        for (byte i = 0; i < buttons.size(); i++) {
            layout.getChildren().add(buttons.get(i));
        }

        window.setScene(main);
        window.show();
    }

}
