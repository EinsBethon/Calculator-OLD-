package calculators;

import equation.EqSystem;
import equation.SingleVariable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static utils.Utils.*;

public class Eq {

    public Eq() {
        init();
    }

    private void init() {
        Stage window;
        TextField first, second, answer;

        Scene main;
        Pane layout;

        MenuBar bar;
        Menu view;
        MenuItem standard, algebraic, inequality, graphing;

        Button solve;
        Font font;

        window = new Stage();
        layout = new Pane();
        main = new Scene(layout);
        window.setTitle("Equation Calculator");
        window.setWidth(400);
        window.setHeight(300);

        standard = new MenuItem("_Standard");
        algebraic = new MenuItem("_Algebraic");
        inequality = new MenuItem("Ine_quality");
        graphing = new MenuItem("_Graphing");
        view = new Menu("Vie_w");
        view.getItems().addAll(standard, algebraic, inequality, graphing);
        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        bar.getMenus().add(view);

        algebraic.setAccelerator(KeyCombination.keyCombination("ALT+A"));
        standard.setAccelerator(KeyCombination.keyCombination("ALT+S"));
        inequality.setAccelerator(KeyCombination.keyCombination("ALT+Q"));
        graphing.setAccelerator(KeyCombination.keyCombination("ALT+G"));

        font = new Font("Consolas", 16);

        first = new TextField();
        first.setFont(font);
        first.setMinWidth(window.getWidth());
        first.setLayoutY(29);

        second = new TextField();
        second.setFont(font);
        second.setMinWidth(window.getWidth());
        second.setLayoutY(89);

        solve = new Button("Solve");
        solve.setFont(font);
        solve.setMinWidth(window.getWidth());
        solve.setLayoutY(59);

        // Event handlers
        standard.setOnAction(e -> {
            new Standard();
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

        answer = new TextField();
        answer.setEditable(false);
        answer.setFont(new Font("Consolas", 20));
        answer.setMinWidth(window.getWidth());
        answer.setLayoutY(121);

        solve.setOnAction(e -> {
            boolean fEmpty = first.getText().isEmpty(), sEmpty = second.getText().isEmpty();
            int fVarNum = variableCount(first.getText()), sVarNum = variableCount(second.getText());
            String fText = first.getText(), sText = second.getText();

            equation.Equation equation;

            if(!fEmpty && sEmpty && fVarNum == 1) equation = new SingleVariable(fText);
            else if(fEmpty && !sEmpty && sVarNum == 1) equation = new SingleVariable(sText);
            else if(!fEmpty && !sEmpty && fVarNum <= 2 && sVarNum <= 2) equation = new EqSystem(fText, sText);
            else equation = null;

            if(equation != null) answer.setText(equation.solved());
        });

        layout.getChildren().addAll(bar, first, second, solve, answer);

        window.setScene(main);
        window.show();

        window.setWidth(400);
        window.setHeight(300);
    }

}
