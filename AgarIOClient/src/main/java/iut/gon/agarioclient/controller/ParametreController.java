package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.model.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class ParametreController {

    @FXML
    private TextField ipField;  // Zone de saisie pour l'adresse IP
    @FXML
    private TextField portField;  // Zone de saisie pour le port
    private String nickname;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.ipField.setText("10.42.17.86"); //remplissage automatique du champ pour faciliter le developpement
        this.portField.setText("12345"); //Pour le developpement
    }

    @FXML
    private void onValiderButtonClick(ActionEvent event) {
        String ip = ipField.getText().trim();
        String port = portField.getText().trim();
        // Validation simple
        if (ip.isEmpty() || port.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
        } else {
            try {
                // Connexion au serveur avec l'IP et le port
                boolean connectionSuccessful = connectToServer(ip, Integer.parseInt(port));

                if (connectionSuccessful) {
                    ParallelCamera camera = new ParallelCamera();

                    FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/iut/gon/agarioclient/game-view.fxml"));
                    Parent gameView = gameLoader.load();

                    FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/iut/gon/agarioclient/chat-view.fxml"));
                    Parent chatView = chatLoader.load();

                    AnchorPane anchorPane = new AnchorPane();
                    anchorPane.getChildren().addAll(gameView,chatView);
                    AnchorPane.setTopAnchor(chatView,10.);
                    AnchorPane.setRightAnchor(chatView,10.);
                    AnchorPane.setTopAnchor(gameView, 0.0);
                    AnchorPane.setBottomAnchor(gameView, 0.0);
                    AnchorPane.setLeftAnchor(gameView, 0.0);
                    AnchorPane.setRightAnchor(gameView, 0.0);

                    GameController gameController = gameLoader.getController();
                    gameController.initializeGame(nickname, camera, new Game());

                    ChatController chatController = chatLoader.getController();
                    chatController.initialize(nickname,ip, Integer.valueOf(port));

                    Scene gameScene = new Scene(anchorPane);
                    stage.setScene(gameScene);
                    stage.setTitle("Jeu en ligne");
                } else {
                    showAlert("Erreur de connexion", "Impossible de se connecter au serveur.");
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Le port doit être un nombre.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onAnnulerButtonClick(ActionEvent event) {
        try {
            FXMLLoader welcomeLoader = new FXMLLoader(getClass().getResource("/iut/gon/agarioclient/welcome-view.fxml"));
            Parent welcomeView = welcomeLoader.load();

            WelcomeController welcomeController = welcomeLoader.getController();
            welcomeController.setNickname(nickname);
            welcomeController.setStage(stage);

            Scene welcomeScene = new Scene(welcomeView);
            stage.setScene(welcomeScene);
            stage.setTitle("Welcome to AgarIO");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean connectToServer(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
