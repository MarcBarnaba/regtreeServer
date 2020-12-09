package it.map1920.regtreeServer;

import it.map1920.regtreeServer.server.MultiServer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class MainViewController {

	// Declare the controls used in the FXML file
	@FXML
	private Button avvia;
	@FXML
	private Label avviato_label;
	@FXML
	private TextField textField;
	@FXML
	private HBox form;

	private int port = 8080;

	@FXML
	public void initialize() {
		avviato_label.setVisible(false);
		avvia.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				new Thread(() -> {

					if (isNumeric(textField.getText())) {
						port = Integer.parseInt(textField.getText());
					}
					new MultiServer(port);

				}).start();

				avvia.setVisible(false);
				form.setVisible(false);
				avviato_label.setText("Server avviato sulla Port " + port + "!");
				avviato_label.setVisible(true);
			}
		});

	}

	private boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}