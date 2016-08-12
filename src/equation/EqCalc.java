package equation;

import algebraic.Algebraic;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import standard.Standard;
import utils.Utils;

import java.io.File;

public class EqCalc {

    private Stage window;
    private TextField first, second;
    private TextArea answer;

    public EqCalc() {
        init();
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

        first = new TextField();
        first.setFont(strings);
        first.setMinWidth(window.getWidth() - 6);
        first.setLayoutY(25);

        second = new TextField();
        second.setFont(strings);
        second.setMinWidth(window.getWidth() - 6);
        second.setLayoutY(85);

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
            if (!first.getText().isEmpty() && second.getText().isEmpty()) {
                if (Utils.variableCount(first.getText()) == 1) {
                    Equation one = new SingleVariable(first.getText(), new File(System.getProperty("user.home") + "/Desktop/calculator/equation/Single.txt"));
                    answer.setText(one.getSolved());
                } else if (Utils.variableCount(first.getText()) == 2) {
                    Equation one = new Linear(first.getText(), new File(System.getProperty("user.home") + "/Desktop/calculator/equation/Linear.txt"));
                    answer.setText(one.getSolved());
                } else if (Utils.variableCount(first.getText()) == 0)
                    Utils.popUp("Error!", "You have entered an algebraic expression.\nPlease use the algebraic calculator for this.");
                else Utils.popUp("Error!", "This calculator does not support\nthree-variable equations yet.");
            } else if (first.getText().isEmpty() && !second.getText().isEmpty()) {
                if (Utils.variableCount(second.getText()) == 1) {
                    Equation one = new SingleVariable(second.getText(), new File(System.getProperty("user.home") + "/Desktop/calculator/equation/Single.txt"));
                    answer.setText(one.getSolved());
                } else if (Utils.variableCount(second.getText()) == 2) {
                    Equation one = new Linear(second.getText(), new File(System.getProperty("user.home") + "/Desktop/calculator/equation/Linear.txt"));
                    answer.setText(one.getSolved());
                } else if (Utils.variableCount(second.getText()) == 0)
                    Utils.popUp("Error!", "You have entered an algebraic expression.\nPlease use the algebraic calculator for this.");
                else Utils.popUp("Error!", "This calculator does not support\nthree-variable equations yet.");
            } else if (!first.getText().isEmpty() && !second.getText().isEmpty()) {
                if (Utils.variableCount(first.getText() + " " + second.getText()) == 2) {
                    EquationSystem sys = new EquationSystem(first.getText(), second.getText());
                    answer.setText(sys.getSolved());
                } else if (Utils.variableCount(first.getText() + " " + second.getText()) == 0)
                    Utils.popUp("Error!", "You have entered an algebraic expression.\nPlease use the algebraic calculator for this.");
                else Utils.popUp("Error!", "This calculator does not support\nthree-variable equations yet.");
            }
        });

        answer = new TextArea();
        answer.setEditable(false);
        answer.setFont(new Font("Consolas", 20));
        answer.setLayoutY(115);

        layout.getChildren().addAll(bar, first, second, solve, answer);

        window.setScene(main);
        window.show();
    }

}
