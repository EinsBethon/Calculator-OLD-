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

        standard = new MenuItem("_Standard");
        algebraic = new MenuItem("_Algebraic");
        view = new Menu("Vie_w");
        view.getItems().addAll(standard, algebraic);
        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        bar.getMenus().add(view);

        buttons = new Font("Consolas", 16);
        strings = new Font("Consolas", 16);

        first = new TextField();
        first.setFont(strings);
        first.setMinWidth(window.getWidth());
        first.setLayoutY(29);

        second = new TextField();
        second.setFont(strings);
        second.setMinWidth(window.getWidth());
        second.setLayoutY(89);

        solve = new Button("Solve");
        solve.setFont(buttons);
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

        solve.setOnAction(e -> {
            boolean fEmpty = first.getText().isEmpty(), sEmpty = second.getText().isEmpty();
            byte fVarNum = Utils.variableCount(first.getText()), sVarNum = Utils.variableCount(second.getText());
            String fText = first.getText(), sText = second.getText();

            Equation equation;

            if(!fEmpty && sEmpty && fVarNum == 1) equation = new SingleVariable(fText);
            else if(fEmpty && !sEmpty && sVarNum == 1) equation = new SingleVariable(sText);
            else if(!fEmpty && !sEmpty && fVarNum <= 2 && sVarNum <= 2) equation = new SystemOfEquations(fText, sText);
            else equation = null;

            if(equation != null) answer.setText(equation.getSolved());
        });

        answer = new TextArea();
        answer.setEditable(false);
        answer.setFont(new Font("Consolas", 20));
        answer.setLayoutY(119);

        layout.getChildren().addAll(bar, first, second, solve, answer);

        window.setScene(main);
        window.show();
    }

}
