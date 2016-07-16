package main;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Display {

	private static Stage window;
	private static Scene scene;
	private static Label text;
	private static Button ok;
	private static Pane layout;
	public static boolean clicked = false;

	public static void show(String title, String message) {
		window = new Stage();

		ok = new Button("Ok");
		ok.setFont(new Font("Consolas", 14));

		text = new Label(message);
		text.setFont(new Font("Consolas", 15));

		ok.setOnAction(e -> {
			clicked = true;
			window.close();
		});

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

	public static void show(String title, String message, int width, int height) {
		window = new Stage();

		ok = new Button("Ok");
		ok.setFont(new Font("Consolas", 14));

		text = new Label(message);
		text.setFont(new Font("Consolas", 15));

		ok.setOnAction(e -> {
			clicked = true;
			window.close();
		});

		layout = new Pane();
		text.setLayoutX(10);
		text.setLayoutY(10);
		ok.setLayoutX(width);
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
