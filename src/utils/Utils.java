package utils;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {

    private static Stage window;
    private static Scene scene;
    private static Label text;
    private static Button ok;
    private static Pane layout;

    public static String[] getOperands(String data, char c) {
        String[] nums = new String[4];
        /*
        * 0 = the first operand
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
            if ((i != 0 && data.charAt(i) == '-' && !Character.isDigit(data.charAt(i - 1)) && !Character.isLetter(data.charAt(i - 1))) || i == 0) {
                nums[0] = data.substring(i, index);
                if (i == 0) nums[2] = i + "";
                else nums[2] = i + 1 + "";
                break;
            } else if (data.charAt(i) != '.' && !Character.isDigit(data.charAt(i)) && !Character.isLetter(data.charAt(i))) {
                nums[0] = data.substring(i + 1, index);
                nums[2] = i + 1 + "";
                break;
            }
        }


        // Find second operand (Going to the right of the operator in the string)
        nums[1] = getNextNumber(data.substring(index + 1, data.length()));
        nums[3] = index + 1 + nums[1].length() + "";
        return nums;
    }

    public static String getNextNumber(String s) {
        if (s.length() == 1) return s;
        for (byte i = 1; i < s.length(); i++) {
            if (i == s.length() - 1) return s;
            if (s.charAt(i) != '.' && !Character.isDigit(s.charAt(i)) && !Character.isLetter(s.charAt(i)))
                return s.substring(0, i);
        }
        return "";
    }

    public static boolean containsLetters(String s) {
        for (byte i = 0; i < s.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (s.charAt(i) == c) return true;
            }
        }
        return false;
    }

    public static double nthRoot(Double number, int root) {
        return Math.pow(number, (double) 1 / (double) root);
    }

    public static byte variableCount(String s) {
        ArrayList<Character> variables = new ArrayList<>();
        for (byte i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i)) && !variables.contains(s.charAt(i))) variables.add(s.charAt(i));
        }
        return (byte) variables.size();
    }

    public static void writeToFile(File file, String s, boolean append) {
        BufferedWriter writer;
        if (file.exists()) {
            try {
                writer = new BufferedWriter(new FileWriter(file, append));
                writer.write(s + System.lineSeparator());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void popUp(String title, String message) {
        window = new Stage();

        ok = new Button("Ok");
        ok.setFont(new Font("Consolas", 14));

        text = new Label(message);
        text.setFont(new Font("Consolas", 15));

        ok.setOnAction(e -> window.close());

        layout = new Pane();
        text.setLayoutX(10);
        text.setLayoutY(10);
        ok.setLayoutX(130);
        ok.setLayoutY(130);

        layout.getChildren().addAll(ok, text);

        scene = new Scene(layout);

        window.setScene(scene);
        window.setTitle(title);
        window.setMinWidth(300);
        window.setMinHeight(175);
        window.setResizable(false);
        window.showAndWait();
    }

    public static void popUp(String title, String message, int width, int height) {
        window = new Stage();

        ok = new Button("Ok");
        ok.setFont(new Font("Consolas", 14));

        text = new Label(message);
        text.setFont(new Font("Consolas", 15));

        ok.setOnAction(e -> window.close());

        layout = new Pane();
        text.setLayoutX(10);
        text.setLayoutY(10);
        ok.setLayoutX(width - 50);
        ok.setLayoutY(height - 50);

        layout.getChildren().addAll(ok, text);

        scene = new Scene(layout);

        window.setScene(scene);
        window.setTitle(title);
        window.setMinWidth(width);
        window.setMinHeight(height);
        window.setResizable(false);
        window.show();
    }
}
