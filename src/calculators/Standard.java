package calculators;

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
import utils.Utils;

import java.util.ArrayList;

public class Standard {

    private Stage window;

    private TextArea result;
    private String lineOne, lineTwo;
    private double one, two, answer;
    private int lastOp, currOp;
    private boolean showAnswer;
    private ArrayList<Button> buttons;

    public Standard() {
        init();
    }

    private void op(int op) {
        if (lineOne.equals("0")) return;
        if (lineTwo.equals("")) {
            lastOp = op;
            one = Double.parseDouble(lineOne);
            lineTwo = lineOne + buttons.get(op).getText();
            lineOne = "0";
        } else {
            currOp = op;
            two = Double.parseDouble(lineOne);
            answer();
            lineOne = "0";
            lastOp = currOp;
            lineTwo = answer + buttons.get(lastOp).getText();
            one = answer;
        }
        setText();
    }

    private void answer() {
        switch (lastOp) {
            case 11:
                answer = one + two;
                break;
            case 12:
                answer = one - two;
                break;
            case 13:
                answer = one * two;
                break;
            case 14:
                answer = one / two;
                break;
            case 16:
                answer = one % two;
                break;
        }
    }

    private void setText() {
        result.setText(lineTwo + "\n" + lineOne);
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
        Pane layout;
        Scene main;
        window = new Stage();
        layout = new Pane();
        main = new Scene(layout);
        window.setTitle("Calculator");
        window.setWidth(220);
        window.setHeight(325);

        //Buttons list
        buttons = new ArrayList<>();

        // Strings
        lineOne = "0";
        lineTwo = "";
        lastOp = -1;
        currOp = -1;

        // Fonts
        Font button, tArea;
        button = new Font("Consolas", 18);
        tArea = new Font("Consolas", 18);

        // Menu
        MenuBar bar;
        Menu view, help;
        MenuItem equation, inequality, algebraic, shortcuts, graphing;

        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        view = new Menu("Vie_w");
        help = new Menu("H_elp");
        shortcuts = new MenuItem("_Shortcuts");
        equation = new MenuItem("_Equation");
        inequality = new MenuItem("Ine_quality");
        algebraic = new MenuItem("_Algebraic");
        graphing = new MenuItem("_Graphing");

        algebraic.setAccelerator(KeyCombination.keyCombination("ALT+A"));
        inequality.setAccelerator(KeyCombination.keyCombination("ALT+Q"));
        equation.setAccelerator(KeyCombination.keyCombination("ALT+E"));
        shortcuts.setAccelerator(KeyCombination.keyCombination("ALT+C"));
        graphing.setAccelerator(KeyCombination.keyCombination("ALT+G"));

        shortcuts.setOnAction(e -> Utils.popUp("Shortcuts", "Modulus: M\nClear: Delete\nSign: S\nSquare Root: Q\nReciprocal: R"));

        equation.setOnAction(e -> {
            new Eq();
            window.close();
        });

        algebraic.setOnAction(e -> {
            new Algebraic();
            window.close();
        });

        inequality.setOnAction(e -> {
            new Ineq();
            window.close();
        });

        graphing.setOnAction(e -> {
            new Graphing();
            window.close();
        });

        help.getItems().add(shortcuts);
        view.getItems().addAll(algebraic, equation, inequality, graphing);
        bar.getMenus().addAll(view, help);

        // Digits
        newButton("0", 10, (int) window.getHeight() - 70, 75, 35, button, e -> {
            if (showAnswer) {
                lineOne = buttons.get(0).getText();
                showAnswer = false;
            } else if (lineOne.equals("0")) lineOne = buttons.get(0).getText();
            else lineOne += buttons.get(0).getText();
            setText();
        });

        for (int y = 0, z = 0; y < 9; y += 3, z++) {
            for (int x = 0; x < 3; x++) {
                int i = x + y + 1;
                newButton((x + y + 1) + "", 10 + (x * 40), (int) (buttons.get(0).getLayoutY() - 40) - (z * 40), 35, 35, button, e -> {
                    if (showAnswer) {
                        lineOne = buttons.get(i).getText();
                        showAnswer = false;
                    } else if (lineOne.equals("0")) lineOne = buttons.get(i).getText();
                    else lineOne += buttons.get(i).getText();
                    setText();
                });
            }
        }

        //Decimal
        newButton(".", (int) buttons.get(0).getLayoutX() + 80, (int) buttons.get(0).getLayoutY(), 35, 35, button, e -> {
            if (showAnswer) {
                lineOne = "0.";
                showAnswer = false;
            } else if (!lineOne.contains(".")) lineOne += ".";
            setText();
        });

        //Operators
        for (int i = 0; i < 4; i++) {
            char c = ' ';
            if (i == 0) c = '+';
            else if (i == 1) c = '-';
            else if (i == 2) c = '*';
            else if (i == 3) c = '/';
            int i1 = i;
            newButton(c + "", (int) buttons.get(10).getLayoutX() + 40, (int) buttons.get(10).getLayoutY() - (i * 40), 35, 35, button, e -> op(11 + i1));
        }
        buttons.get(11).setFont(new Font("Consolas", 16));

        //Equals
        newButton("=", (int) buttons.get(11).getLayoutX() + 40, (int) buttons.get(10).getLayoutY() - 40, 35, 74, new Font("Consolas", 17), e -> {
            showAnswer = true;
            two = Double.parseDouble(lineOne);
            answer();
            lineOne = answer + "";
            lineTwo = "";
            setText();
        });

        //Modulus
        newButton("%", (int) buttons.get(14).getLayoutY() + 36, (int) buttons.get(14).getLayoutY(), 35, 35, new Font("Consolas", 16), e -> op(16));

        //Clear
        newButton("C", (int) buttons.get(9).getLayoutX(), (int) buttons.get(9).getLayoutY() - 40, 35, 35, new Font("Consolas", 17), e -> {
            lineOne = "0";
            lineTwo = "";
            setText();
        });

        // Clear Entry
        newButton("CE", (int) buttons.get(17).getLayoutX() - 40, (int) buttons.get(17).getLayoutY(), 35, 35, new Font("Consolas", 13), e -> {
            lineOne = "0";
            setText();
        });

        //Backspace
        newButton("<", (int) buttons.get(18).getLayoutX() - 40, (int) buttons.get(18).getLayoutY(), 35, 35, new Font("Consolas", 16), e -> {
            if (!lineOne.equals("0")) {
                lineOne = lineOne.substring(0, lineOne.length() - 1);
                if (lineOne.equals("")) lineOne = "0";
                setText();
            }
        });

        //Reciprocal
        newButton("1/x", (int) buttons.get(16).getLayoutX(), (int) buttons.get(16).getLayoutY() + 40, 37, 35, new Font("Consolas", 11), e -> {
            double d = Double.parseDouble(lineOne);
            lineOne = 1 / d + "";
            setText();
        });

        //Square root
        newButton("\u221A", (int) buttons.get(16).getLayoutX(), (int) buttons.get(16).getLayoutY() - 40, 37, 35, button, e -> {
            double d = Double.parseDouble(lineOne);
            lineOne = Math.sqrt(d) + "";
            setText();
        });

        //Sign
        newButton("\u00B1", (int) buttons.get(14).getLayoutX(), (int) buttons.get(14).getLayoutY() - 40, 35, 35, new Font("Consolas", 16), e -> {
            if (lineOne.startsWith("-")) lineOne = lineOne.substring(1, lineOne.length());
            else lineOne = "-" + lineOne;
            setText();
        });

        result = new TextArea();
        result.setFont(tArea);
        result.setLayoutY(29);
        result.setMinWidth(window.getWidth());
        result.setMaxHeight(60);
        result.setEditable(false);
        result.setFocusTraversable(false);
        setText();

        // Key input
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

        for(Button b: buttons) {
            layout.getChildren().add(b);
        }

        layout.getChildren().addAll(bar, result);

        window.setScene(main);
        window.show();

        window.setWidth(220);
        window.setHeight(325);
    }

}
