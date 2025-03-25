package iut.gon.agarioclient;

import iut.gon.agarioclient.controller.WelcomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 280);
        WelcomeController controller = fxmlLoader.getController();
        controller.setStage(stage);
        stage.setTitle("Agar.IO");
        stage.setMinHeight(280);
        stage.setMinWidth(400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}