package calculators;

import equation.MultiVariable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

import static utils.Utils.*;

public class Graphing {

    private Stage window;
    private Pane layout;

    private TextField eqInput;

    private ArrayList<Node> nodes;
    private boolean showingSolution;

    private int xOrigin, yOrigin;

    private final int CELL_SIZE = 33;

    public Graphing() {
        init();
    }

    /**
     * Plots points and graphs a line according to the equation given
     *
     * @param equation = the equation to be graphed
     */
    private void graph(MultiVariable equation) {
        showingSolution = true;

        // Find slope and y-intercept
        String s = equation.solved().substring(equation.solved().indexOf("=") + 1, equation.solved().length());
        double rise = 0; // Run will always be 1 because of the way the equation is solved
        double yInt = 0; // Can act as xInt in the case that the equation is solved for x
        double startX = 0, startY = 75, endX = 0, endY = window.getHeight();

        if (equation.solved().startsWith("x")) {
            yInt = xOrigin + (Double.parseDouble(s) * CELL_SIZE);
            startX = yInt;
            startY = window.getHeight();
            endX = yInt;
            endY = 75;
        } else if (equation.solved().startsWith("y") && !containsLetters(s)) {
            yInt = yOrigin - (Double.parseDouble(s) * CELL_SIZE);
            startX = 0;
            startY = yInt;
            endX = window.getWidth();
            endY = yInt;
        } else {
            for (int i = 0; i < s.length(); ) {
                if (!removeSpaces(getNextNumber(s.substring(i, s.length()))).isEmpty()) {
                    if (getNextNumber(s.substring(i, s.length())).contains("x"))
                        rise = getNumber(getNextNumber(s.substring(i, s.length())));
                    else yInt = getNumber(getNextNumber(s.substring(i, s.length())));
                }
                i += getNextNumber(s.substring(i, s.length())).length();
            }

            //Adds points on the left half of the grid (quadrants 1 and 2)
            for (double y = yInt, x = 0; y >= -12 && y <= 12 && x >= -12; y += -rise, x--) {
                addPoint(x, y);

                double x2, y2;
                if (rise < 0 && (y - rise >= 12 || x - 1 <= -12)) {
                    y2 = 12 - y;
                    x2 = y2 / rise;

                    //System.out.println("LEFT XY: (" + x + ", " + y + ") X2Y2: (" + (x + x2) + ", " + 12 + ")");

                    startX = xOrigin + ((x + x2) * CELL_SIZE);
                    startY = yOrigin - (12 * CELL_SIZE);
                } else if (rise >= 0 && (y - rise <= -12 || x - 1 <= -12)) {
                    y2 = y + 12;
                    x2 = y2 / rise;

                    //System.out.println("LEFT XY: (" + x + ", " + y + ") X2Y2: (" + (x - x2) + ", " + -12 + ")");

                    startX = xOrigin + ((x - x2) * CELL_SIZE);
                    startY = yOrigin + (12 * CELL_SIZE); // Subtract y from the yOrigin because the y-axis is on the top, not the bottom
                }
            }

            //Adds points on the right half of the grid (quadrants 3 and 4)
            for (double y = yInt, x = 0; y <= 12 && y >= -12 && x <= 12; y += rise, x++) {
                addPoint(x, y);

                double x2, y2;
                if (rise < 0 && (y + rise <= -12 || x + 1 >= 12)) {
                    y2 = y + 12;
                    x2 = y2 / rise;

                    //System.out.println("RIGHT XY: (" + x + ", " + y + ") X2Y2: (" + (x - x2) + ", " + -12 + ")");

                    endX = xOrigin + ((x - x2) * CELL_SIZE);
                    endY = yOrigin + (12 * CELL_SIZE);
                } else if (rise >= 0 && (y + rise >= 12 || x + 1 >= 12)) {
                    y2 = 12 - y;
                    x2 = y2 / rise;

                    //System.out.println("RIGHT XY: (" + x + ", " + y + ") X2Y2: (" + (x + x2) + ", " + 12 + ")");

                    endX = xOrigin + ((x + x2) * CELL_SIZE);
                    endY = yOrigin - (12 * CELL_SIZE); // Subtract y from the yOrigin because the y-axis is on the top, not the bottom
                }
            }
        }

        //System.out.println("START: (" + startX + ", " + startY + ") END: (" + endX + ", " + endY + ")");

        Line line = new Line(startX, startY, endX, endY);
        line.setStrokeWidth(3);
        line.setStroke(Color.FIREBRICK);
        nodes.add(line);
        layout.getChildren().add(line);
    }

    private void graph(int x0, int x1, int y0, int y1) {
        showingSolution = true;

    }

    /**
     * Plots points on the grid at the specified coordinates
     *
     * @param x = The x-coordinate at which to plot the point
     * @param y = The y-coordinate at which to plot the point
     */
    private void addPoint(double x, double y) {
        Circle c = new Circle(xOrigin + (x * CELL_SIZE), yOrigin - (y * CELL_SIZE), 8);
        c.setRadius(4);
        c.setFill(Color.FIREBRICK);
        nodes.add(c);
        layout.getChildren().add(c);
    }

    private void init() {
        window = new Stage();
        layout = new Pane();
        Scene main = new Scene(layout);

        window.setTitle("Graphing Calculator");
        window.setWidth(792);
        window.setHeight(895);
        window.setScene(main);

        xOrigin = (int) (window.getWidth() / 2);
        yOrigin = (int) ((window.getHeight() - 103) / 2) + 75;

        nodes = new ArrayList<>();

        // Menu stuff
        MenuBar bar;
        Menu view;
        MenuItem standard, algebraic, equation, ineq;

        standard = new MenuItem("_Standard");
        algebraic = new MenuItem("_Algebraic");
        equation = new MenuItem("_Equation");
        ineq = new MenuItem("Ine_quality");
        view = new Menu("Vie_w");
        view.getItems().addAll(standard, algebraic, equation, ineq);
        bar = new MenuBar();
        bar.setMinWidth(window.getWidth());
        bar.getMenus().add(view);

        algebraic.setAccelerator(KeyCombination.keyCombination("ALT+A"));
        standard.setAccelerator(KeyCombination.keyCombination("ALT+S"));
        equation.setAccelerator(KeyCombination.keyCombination("ALT+E"));
        ineq.setAccelerator(KeyCombination.keyCombination("ALT+Q"));

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

        ineq.setOnAction(e -> {
            new Ineq();
            window.close();
        });

        eqInput = new TextField();
        eqInput.setFont(new Font("Consolas", 23));
        eqInput.setLayoutY(29);

        Button graph = new Button("graph");
        graph.setFont(new Font("Consolas", 24));
        graph.setLayoutX(300);
        graph.setLayoutY(28);
        graph.setOnAction(e -> {
            if (showingSolution) {
                for (Node n : nodes) {
                    layout.getChildren().remove(n);
                }
            }

            String s = eqInput.getText();
            for (int i = 0, o = 0; i < s.length(); i++) {
                if (s.charAt(i) == ',') {
                    if (s.substring(o, i).contains("y")) graph(new MultiVariable(s.substring(o, i), 'y'));
                    else graph(new MultiVariable(s.substring(o, i), 'x'));
                    o = i + 1; // Add one so that the comma is not included in the next equation
                } else if (i == s.length() - 1) {
                    if (s.substring(o, i).contains("y")) graph(new MultiVariable(s.substring(o, i + 1), 'y'));
                    else graph(new MultiVariable(s.substring(o, i + 1), 'x'));
                }
            }
        });


        // Create grid
        for (int i = 0; i <= 24; i++) {
            Line vertical, horizontal;
            if (i == 12) {
                vertical = new Line(i * CELL_SIZE - 1, 867, i * CELL_SIZE - 1, 75);
                vertical.setStrokeWidth(3);
                horizontal = new Line(0, (i * CELL_SIZE) + 74, 792, (i * CELL_SIZE) + 74);
                horizontal.setStrokeWidth(3);
            } else {
                vertical = new Line(i * CELL_SIZE, 867, i * CELL_SIZE, 75);
                horizontal = new Line(0, (i * CELL_SIZE) + 75, 792, (i * CELL_SIZE) + 75);
            }

            layout.getChildren().addAll(vertical, horizontal);
        }

        layout.getChildren().addAll(bar, eqInput, graph);

        window.show();
        window.setWidth(792);
        window.setHeight(895);
    }

}
