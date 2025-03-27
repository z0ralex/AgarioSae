// AnimationManager.java
package iut.gon.agarioclient.controller;

import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class AnimationManager {
    private Pane gamePane;

    public AnimationManager(Pane gamePane) {
        this.gamePane = gamePane;
    }

    public void playPelletAbsorption(Node pellet, Point2D targetPosition) {
        if(pellet != null){
            // Create a copy for the animation so the original can be removed immediately
            Circle animatedPellet = new Circle(
                    ((Circle)pellet).getRadius(),
                    ((Circle)pellet).getFill()
            );
            animatedPellet.setCenterX(((Circle)pellet).getCenterX());
            animatedPellet.setCenterY(((Circle)pellet).getCenterY());
            gamePane.getChildren().add(animatedPellet);

            // Create parallel animations
            TranslateTransition move = new TranslateTransition(Duration.millis(300), animatedPellet);
            move.setToX(targetPosition.getX() - animatedPellet.getCenterX());
            move.setToY(targetPosition.getY() - animatedPellet.getCenterY());

            FadeTransition fade = new FadeTransition(Duration.millis(300), animatedPellet);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            ScaleTransition shrink = new ScaleTransition(Duration.millis(300), animatedPellet);
            shrink.setToX(0.1);
            shrink.setToY(0.1);

            ParallelTransition absorption = new ParallelTransition(move, fade, shrink);
            absorption.setOnFinished(e -> gamePane.getChildren().remove(animatedPellet));
            absorption.play();
        }

    }

    public void playCellDivision(Point2D position, double initialRadius) {
        // Create visual effect for division
        for (int i = 0; i < 8; i++) {
            Circle particle = new Circle(initialRadius/4, Color.WHITE);
            particle.setCenterX(position.getX());
            particle.setCenterY(position.getY());
            gamePane.getChildren().add(particle);

            TranslateTransition move = new TranslateTransition(Duration.millis(500), particle);
            move.setByX(Math.cos(i * Math.PI/4) * 50);
            move.setByY(Math.sin(i * Math.PI/4) * 50);

            FadeTransition fade = new FadeTransition(Duration.millis(500), particle);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            new ParallelTransition(move, fade).play();
        }
    }
}