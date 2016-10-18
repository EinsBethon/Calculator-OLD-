package inequality;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;

public class IneqCalc {

    private static final File file = new File(System.getProperty("user.home") + "/Desktop/calculator/inequality.txt");

    public IneqCalc() {
        init();
    }

    private void init() {
        Stage window = new Stage();
        Pane layout = new Pane();
        Scene main = new Scene(layout);

        window.setTitle("Inequality Calculator");
        window.setWidth(300);
        window.setHeight(300);
        window.setResizable(false);



        window.setScene(main);
        window.show();
    }

}
