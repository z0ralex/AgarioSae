package iut.gon.agarioclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class WelcomeController {

    @FXML
    private TextField nicknameField;

    @FXML
    private Button onlineButton;

    @FXML
    private Button localButton;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onOnlineButtonClick(ActionEvent event) {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un surnom !");
        } else {
            try {
                // Tentative de connexion au serveur
                boolean connectionSuccessful = connectToServer();

                if (connectionSuccessful) {
                    FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/iut/gon/agarioclient/game-view.fxml"));
                    Parent gameView = gameLoader.load();

                    FXMLLoader rightPanelLoader = new FXMLLoader(getClass().getResource("/iut/gon/agarioclient/chat-view.fxml"));
                    Parent rightPanelView = rightPanelLoader.load();

                    HBox hbox = new HBox();
                    hbox.getChildren().addAll(gameView, rightPanelView);
                    hbox.setSpacing(10);

                    GameController gameController = gameLoader.getController();
                    gameController.initializeGame(nickname);

                    ChatController chatController = rightPanelLoader.getController();
                    chatController.initialize(nickname);

                    Scene gameScene = new Scene(hbox);
                    stage.setScene(gameScene);
                    stage.setTitle("Jeu en ligne");

                } else {
                    showAlert("Erreur de connexion", "Impossible de se connecter au serveur.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean connectToServer() {
        try {
            Socket socket = new Socket("10.42.17.86", 12345);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    @FXML
    private void onLocalButtonClick(ActionEvent event) {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un surnom !");
        } else {
            try {
                ParallelCamera camera = new ParallelCamera();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/iut/gon/agarioclient/game-view.fxml"));
                Parent newView = loader.load();

                GameController gameController = loader.getController();
                gameController.initializeGame(nickname, camera);

                Scene gameScene = new Scene(newView);
                stage.setScene(gameScene);
                stage.setTitle("Jeu en local");
                gameScene.setCamera(camera);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}