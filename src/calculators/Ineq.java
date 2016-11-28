package calculators;

import inequality.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

import static inequality.Inequality.*;

public class Ineq {

    public Ineq() {
        init();
    }

    private void init() {
        Stage window = new Stage();
        Pane layout = new Pane();
        Scene main = new Scene(layout);

        window.setTitle("Ineq Calculator");
        window.setWidth(400);
        window.setHeight(200);

        // Menu stuff
        MenuBar bar;
        Menu view;
        MenuItem standard, algebraic, equation, graphing;

        standard = new MenuItem("_Standard");
        algebraic = new MenuItem("_Algebraic");
        equation = new MenuItem("_Equation");
        graphing = new MenuItem("_Graphing");
        view = new Menu("Vie_w");
        view.getItems().addAll(standard, algebraic, equation, graphing);
        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        bar.getMenus().add(view);

        algebraic.setAccelerator(KeyCombination.keyCombination("ALT+A"));
        standard.setAccelerator(KeyCombination.keyCombination("ALT+S"));
        equation.setAccelerator(KeyCombination.keyCombination("ALT+E"));
        graphing.setAccelerator(KeyCombination.keyCombination("ALT+G"));

        standard.setOnAction(e -> {
            new Standard();
            window.close();
        });

        algebraic.setOnAction(e -> {
            new Algebraic();
            window.close();
        });

        equation.setOnAction(e -> {
            new Eq();
            window.close();
        });

        graphing.setOnAction(e -> {
            new Graphing();
            window.close();
        });

        Font font = new Font("Consolas", 16);

        TextField first = new TextField();
        first.setMinWidth(window.getWidth());
        first.setFont(font);
        first.setLayoutY(29);

        TextField answer = new TextField();
        answer.setLayoutY(89);
        answer.setMinWidth(window.getWidth());
        answer.setFont(font);
        answer.setEditable(false);

        Button solve = new Button("Solve");
        solve.setFont(font);
        solve.setMinWidth(200);
        solve.setLayoutX(window.getWidth() / 2 - solve.getMinWidth() / 2);
        solve.setLayoutY(60);

        solve.setOnAction(e -> {
            boolean empty = first.getText().isEmpty();
            int signNum = getSigns(first.getText()).size();
            String fText = first.getText();

            Inequality ineq;

            if(!empty && signNum == 1) ineq = new SingleSign(fText);
            else if(!empty && signNum <= 2) ineq = new Compund(fText);
            else ineq = null;

            if(ineq != null) answer.setText(ineq.solved());
        });

        Button lte, gte;

        lte = new Button(LTE + "");
        lte.setFont(font);
        lte.setLayoutX(solve.getLayoutX() - 35);
        lte.setLayoutY(solve.getLayoutY());
        lte.setOnAction(e -> first.setText(first.getText() + LTE));

        gte = new Button(GTE + "");
        gte.setFont(font);
        gte.setLayoutX(solve.getLayoutX() + solve.getMinWidth());
        gte.setLayoutY(solve.getLayoutY());
        gte.setOnAction(e -> first.setText(first.getText() + GTE));


        layout.getChildren().addAll(bar, first, solve, lte, gte, answer);

        window.setScene(main);
        window.show();

        window.setWidth(400);
        window.setHeight(200);
    }

    private ArrayList<String> getSigns(String inequality) {
        ArrayList<String> signs = new ArrayList<>();
        for(int i = 0; i < inequality.length(); i++) {
            char c = inequality.charAt(i);
            if(c == '>' || c == '<' || c == LTE || c == GTE) signs.add(i + "" + c);
        }
        return signs;
    }

}
