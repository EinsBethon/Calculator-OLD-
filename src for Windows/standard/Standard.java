package standard;

import algebraic.Algebraic;
import equation.EqCalc;
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
import utils.Utils;

public class Standard {

    private Stage window;
    private Pane layout;

    private Button[] digits;
    private Button[] operators;
    private Button clear, clearE, backspace, decimal, sign, sqrt, reciprocal, equals;
    private TextArea result;
    private MenuItem equation, algebraic, shortcuts;
    private String lineOne, lineTwo;
    private double one, two, answer;
    private byte lastOp, currOp;
    private boolean showAnswer;

    public Standard() {
        init();
        handle();
    }

    private void handle() {
        layout.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                for (byte i = 0; i < digits.length; i++) {
                    if (e.getText().equals(i + "")) digits[i].fire();
                }
                for (byte i = 0; i < 4; i++) {
                    if (e.getText().equals(operators[i].getText())) operators[i].fire();
                }

                if (e.getText().equals("m")) operators[4].fire();
                if (e.getCode() == KeyCode.DELETE) clear.fire();
                if (e.getCode() == KeyCode.DECIMAL) decimal.fire();
                if (e.getText().equals("s")) sign.fire();
                if (e.getText().equals("q")) sqrt.fire();
                if (e.getText().equals("r")) reciprocal.fire();
                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.EQUALS) equals.fire();
                if (e.getCode() == KeyCode.BACK_SPACE) backspace.fire();
            }
        });

        shortcuts.setOnAction(e -> Utils.popUp("Shortcuts", "Modulus: M\nClear: Delete\nSign: S\nSquare Root: Q\nReciprocal: R"));

        equation.setOnAction(e -> {
            new EqCalc();
            window.close();
        });

        algebraic.setOnAction(e -> {
            new Algebraic();
            window.close();
        });

        for (byte i = 0; i < digits.length; i++) {
            byte o = i;
            digits[i].setOnAction(e -> {
                if (showAnswer) {
                    lineOne = digits[o].getText();
                    showAnswer = false;
                } else if (lineOne.equals("0")) lineOne = digits[o].getText();
                else lineOne += digits[o].getText();
                setText();
            });
        }

        for (byte i = 0; i < operators.length; i++) {
            byte o = i;
            operators[o].setOnAction(e -> op(o));
        }

        backspace.setOnAction(e -> {
            if (!lineOne.equals("0")) {
                lineOne = lineOne.substring(0, lineOne.length() - 1);
                if (lineOne.equals("")) lineOne = "0";
                setText();
            }
        });

        decimal.setOnAction(e -> {
            if (showAnswer) {
                lineOne = "0.";
                showAnswer = false;
            } else if (!lineOne.contains(".")) lineOne += decimal.getText();
            setText();
        });

        equals.setOnAction(e -> {
            showAnswer = true;
            two = Double.parseDouble(lineOne);
            answer();
            lineOne = answer + "";
            lineTwo = "";
            setText();
        });

        clearE.setOnAction(e -> {
            lineOne = "0";
            setText();
        });

        clear.setOnAction(e -> {
            lineOne = "0";
            lineTwo = "";
            setText();
        });

        sign.setOnAction(e -> {
            if (lineOne.startsWith("-")) lineOne = lineOne.substring(1, lineOne.length());
            else lineOne = "-" + lineOne;
            setText();
        });

        sqrt.setOnAction(e -> {
            double d = Double.parseDouble(lineOne);
            lineOne = Math.sqrt(d) + "";
            setText();
        });

        reciprocal.setOnAction(e -> {
            double d = Double.parseDouble(lineOne);
            lineOne = 1 / d + "";
            setText();
        });
    }

    private void op(byte op) {
        if (lineOne.equals("0")) return;
        if (lineTwo.equals("")) {
            lastOp = op;
            one = Double.parseDouble(lineOne);
            lineTwo = lineOne + operators[op].getText();
            lineOne = "0";
        } else {
            currOp = op;
            two = Double.parseDouble(lineOne);
            answer();
            lineOne = "0";
            lastOp = currOp;
            lineTwo = answer + operators[lastOp].getText();
            one = answer;
        }
        setText();
    }

    private void answer() {
        switch (lastOp) {
            case 0:
                answer = one + two;
                break;
            case 1:
                answer = one - two;
                break;
            case 2:
                answer = one * two;
                break;
            case 3:
                answer = one / two;
                break;
            case 4:
                answer = one % two;
                break;
        }
    }

    private void setText() {
        result.setText(lineTwo + "\n" + lineOne);
    }

    private void init() {
        Scene main;
        window = new Stage();
        layout = new Pane();
        main = new Scene(layout);
        window.setTitle("Calculator");
        window.setWidth(220);
        window.setHeight(325);
        window.setResizable(false);

        lineOne = "0";
        lineTwo = "";
        lastOp = -1;
        currOp = -1;

        digits = new Button[10];
        operators = new Button[5];

        Font buttons, tArea;
        buttons = new Font("Consolas", 18);
        tArea = new Font("Consolas", 18);

        MenuBar bar;
        Menu view, help;

        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        view = new Menu("View");
        help = new Menu("Help");
        shortcuts = new MenuItem("Shortcuts");
        equation = new MenuItem("Equation");
        algebraic = new MenuItem("Algebraic");

        help.getItems().add(shortcuts);
        view.getItems().addAll(algebraic, equation);
        bar.getMenus().addAll(view, help);

        for (byte i = 0; i < digits.length; i++) {
            digits[i] = new Button(i + "");
            digits[i].setFont(buttons);
            layout.getChildren().add(digits[i]);
        }

        digits[0].setMinWidth(74);
        digits[0].setLayoutX(10);
        digits[0].setLayoutY(window.getHeight() - 70);

        for (byte y = 0, z = 0; y < 9; y += 3, z++) {
            for (byte x = 0; x < 3; x++) {
                digits[x + y + 1].setLayoutX(10 + (x * 40));
                digits[x + y + 1].setLayoutY((digits[0].getLayoutY() - 40) - (z * 40));
            }
        }

        clear = new Button("C");
        clear.setFont(new Font("Consolas", 17));
        clear.setMinWidth(35);
        clear.setLayoutX(digits[9].getLayoutX());
        clear.setLayoutY(digits[9].getLayoutY() - 40);

        clearE = new Button("CE");
        clearE.setFont(new Font("Consolas", 13));
        clearE.setLayoutX(clear.getLayoutX() - 40);
        clearE.setLayoutY(clear.getLayoutY());
        clearE.setMinWidth(35);
        clearE.setMinHeight(35);

        backspace = new Button("<");
        backspace.setFont(new Font("Consolas", 16));
        backspace.setMinWidth(35);
        backspace.setLayoutX(clearE.getLayoutX() - 40);
        backspace.setLayoutY(clearE.getLayoutY());

        decimal = new Button(".");
        decimal.setFont(buttons);
        decimal.setLayoutX(digits[0].getLayoutX() + 80);
        decimal.setLayoutY(digits[0].getLayoutY());
        decimal.setMinWidth(35);

        operators[0] = new Button("+");
        operators[1] = new Button("-");
        operators[2] = new Button("*");
        operators[3] = new Button("/");
        operators[4] = new Button("%");

        for (byte i = 0; i < 4; i++) {
            operators[i].setFont(buttons);
            operators[i].setLayoutX(decimal.getLayoutX() + 40);
            operators[i].setLayoutY(decimal.getLayoutY() - (i * 40));
            operators[i].setMinWidth(35);
            operators[i].setMinHeight(34);
            layout.getChildren().add(operators[i]);
        }

        operators[0].setFont(new Font("Consolas", 16));

        operators[4].setFont(new Font("Consolas", 16));
        operators[4].setLayoutX(operators[3].getLayoutX() + 40);
        operators[4].setLayoutY(operators[3].getLayoutY());
        layout.getChildren().add(operators[4]);

        sign = new Button("\u00B1");
        sign.setFont(new Font("Consolas", 16));
        sign.setLayoutX(operators[3].getLayoutX());
        sign.setLayoutY(operators[3].getLayoutY() - 40);

        sqrt = new Button("\u221A");
        sqrt.setFont(buttons);
        sqrt.setLayoutX(operators[4].getLayoutX());
        sqrt.setLayoutY(operators[4].getLayoutY() - 40);

        reciprocal = new Button("1/x");
        reciprocal.setFont(new Font("Consolas", 11));
        reciprocal.setLayoutX(operators[4].getLayoutX());
        reciprocal.setLayoutY(operators[4].getLayoutY() + 40);
        reciprocal.setMinWidth(37);
        reciprocal.setMinHeight(35);

        equals = new Button("=");
        equals.setFont(buttons);
        equals.setLayoutX(operators[1].getLayoutX() + 40);
        equals.setLayoutY(operators[1].getLayoutY());
        equals.setMinWidth(35);
        equals.setMinHeight(74);

        result = new TextArea();
        result.setFont(tArea);
        result.setLayoutY(25);
        result.setMinWidth(window.getWidth());
        result.setMaxHeight(60);
        result.setEditable(false);
        result.setFocusTraversable(false);
        setText();

        layout.getChildren().addAll(result, clear, clearE, backspace, decimal, sign, sqrt, reciprocal, equals, bar);

        window.setScene(main);
        window.show();
    }

}
