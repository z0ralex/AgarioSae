package iut.gon.agarioclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private TextField nicknameField;

    @FXML
    private Button onlineButton;

    @FXML
    private Button localButton;

    private Stage stage; // Ajouter un champ pour stocker le stage

    // Méthode pour définir le stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onOnlineButtonClick(ActionEvent event) {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un surnom !");
        } else {
            System.out.println("Lancer le jeu en ligne avec le surnom : " + nickname);
        }
    }

    @FXML
    private void onLocalButtonClick(ActionEvent event) {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un surnom !");
        } else {
            try {
                // Charger la vue du jeu local
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/iut/gon/agarioclient/game-view.fxml"));
                Parent newView = loader.load();

                // Créer une nouvelle scène avec la vue de jeu
                Scene gameScene = new Scene(newView);

                // Changer la scène du stage actuel
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

