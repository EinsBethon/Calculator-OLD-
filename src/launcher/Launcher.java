package launcher;

import calculators.Standard;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage window) {
        new Standard();
    }

}
