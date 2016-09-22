package main;

import javafx.application.Application;
import javafx.stage.Stage;
import standard.Standard;

public class Launcher extends Application {

	public void start(Stage window) throws Exception {
		new Standard();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
