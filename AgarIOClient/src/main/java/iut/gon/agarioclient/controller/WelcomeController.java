package iut.gon.agarioclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
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

    private String nickname;

    public void setNickname(String nickname){
        this.nickname=nickname;
        nicknameField.setText(nickname);
    }

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
                // Afficher la fenêtre de paramètres
                FXMLLoader paramLoader = new FXMLLoader(getClass().getResource("/iut/gon/agarioclient/parametre-view.fxml"));
                Parent paramView = paramLoader.load();
                // Passer le pseudo au contrôleur de la page Parametre
                ParametreController paramController = paramLoader.getController();
                paramController.setNickname(nickname);
                paramController.setStage(stage);
                // Créer une nouvelle scène avec la page Parametre
                Scene paramScene = new Scene(paramView);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(paramScene);
                stage.setTitle("Paramètres");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                gameController.setStage(stage);
                gameController.initializeGame(nickname, camera);

                Scene gameScene = new Scene(newView);
                stage.setScene(gameScene);
                stage.setTitle("Jeu en local");

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