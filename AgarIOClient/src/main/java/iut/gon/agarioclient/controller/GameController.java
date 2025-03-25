package iut.gon.agarioclient.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class GameController {

    @FXML
    private Pane pane;

    @FXML
    private Circle playerCircle;

    public void initialize() {
        playerCircle.setFill(javafx.scene.paint.Color.RED);
    }
}
