// AnimationManager.java
package iut.gon.agarioclient.controller;

import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Manages animations for the game, including pellet absorption and cell division effects.
 */
public class AnimationManager {
    private Pane gamePane; // The pane where the animations will be displayed

    /**
     * Constructs an AnimationManager with the specified game pane.
     *
     * @param gamePane the pane where the animations will be displayed
     */
    public AnimationManager(Pane gamePane) {
        this.gamePane = gamePane;
    }

    /**
     * Plays the pellet absorption animation.
     * The pellet moves to the target position, fades out, and shrinks.
     *
     * @param pellet         the pellet node to animate
     * @param targetPosition the target position where the pellet will move
     */
    public void playPelletAbsorption(Node pellet, Point2D targetPosition) {
        if (pellet != null) {
            // Create a copy for the animation so the original can be removed immediately
            Circle animatedPellet = new Circle(
                    ((Circle) pellet).getRadius(),
                    ((Circle) pellet).getFill()
            );
            animatedPellet.setCenterX(((Circle) pellet).getCenterX());
            animatedPellet.setCenterY(((Circle) pellet).getCenterY());
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

    /**
     * Plays the cell division animation.
     * Creates a visual effect where particles move outward from the specified position and fade out.
     *
     * @param position      the position where the cell division occurs
     * @param initialRadius the initial radius of the cell being divided
     */
    public void playCellDivision(Point2D position, double initialRadius) {
        // Create visual effect for division
        for (int i = 0; i < 8; i++) {
            Circle particle = new Circle(initialRadius / 4, Color.WHITE);
            particle.setCenterX(position.getX());
            particle.setCenterY(position.getY());
            gamePane.getChildren().add(particle);

            TranslateTransition move = new TranslateTransition(Duration.millis(500), particle);
            move.setByX(Math.cos(i * Math.PI / 4) * 50);
            move.setByY(Math.sin(i * Math.PI / 4) * 50);

            FadeTransition fade = new FadeTransition(Duration.millis(500), particle);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            new ParallelTransition(move, fade).play();
        }
    }
}