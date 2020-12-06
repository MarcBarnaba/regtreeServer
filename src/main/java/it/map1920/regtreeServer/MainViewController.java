package it.map1920.regtreeServer;

import it.map1920.regtreeServer.server.MultiServer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainViewController {

	// Declare the controls used in the FXML file
	@FXML
	private Button avvia;
	@FXML
	private Label avviato_label;
	@FXML
	private AnchorPane root;

	@FXML
	public void initialize() {
		avviato_label.setVisible(false);
		avvia.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				new Thread(() -> {
					new MultiServer(8080);
				}).start();
				
				avvia.setVisible(false);
				avviato_label.setVisible(true);
			}
		});

	}

}