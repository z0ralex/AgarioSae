package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.model.EntityFactory;
import iut.gon.agarioclient.model.Pellet;
import iut.gon.agarioclient.model.Player;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameController {

    @FXML
    private Pane pane;

    private Map<Player, Circle> playerCircles = new HashMap<>();
    private Map<Pellet, Circle> pelletCircles = new HashMap<>();
    private Random random = new Random();

    public void initializeGame(String nickname) {
        if (pane == null) {
            throw new IllegalStateException("Pane is not initialized. Ensure the FXML file is correctly configured.");
        }

        Player player = new Player(nickname, new Point2D(400, 300), 10, 5);
        addPlayer(player);

        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                final Point2D[] mousePosition = {new Point2D(400, 300)};

                newScene.setOnMouseMoved(event -> {
                    mousePosition[0] = new Point2D(event.getX(), event.getY());
                });

                new javafx.animation.AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        double speed = player.calculateSpeed(mousePosition[0].getX(), mousePosition[0].getY(), pane.getWidth(), pane.getHeight());
                        player.setSpeed(speed);

                        Point2D direction = mousePosition[0].subtract(player.getPosition()).normalize();
                        Point2D newPosition = player.getPosition().add(direction.multiply(player.getSpeed()));
                        player.setPosition(newPosition);

                        updatePlayerPosition(player);
                        checkCollisions(player);
                        spawnPellets();
                    }
                }.start();
            }
        });
    }

    public void addPlayer(Player player) {
        Circle playerCircle = new Circle(player.getPosition().getX(), player.getPosition().getY(), player.calculateRadius());
        playerCircle.setFill(Color.BLUE);
        playerCircles.put(player, playerCircle);
        pane.getChildren().add(playerCircle);
    }

    public void updatePlayerPosition(Player player) {
        Circle playerCircle = playerCircles.get(player);
        if (playerCircle != null) {
            playerCircle.setCenterX(player.getPosition().getX());
            playerCircle.setCenterY(player.getPosition().getY());
        }
    }

    public void addPellet(Pellet pellet) {
        Circle pelletCircle = new Circle(pellet.getPosition().getX(), pellet.getPosition().getY(), pellet.calculateRadius());
        pelletCircle.setFill(Color.GREEN);
        pelletCircles.put(pellet, pelletCircle);
        pane.getChildren().add(pelletCircle);
    }

    public void checkCollisions(Player player) {
        Circle playerCircle = playerCircles.get(player);
        if (playerCircle != null) {
            double playerRadius = playerCircle.getRadius();
            double eventHorizon = playerRadius + 100;

            pelletCircles.entrySet().removeIf(entry -> {
                Pellet pellet = entry.getKey();
                Circle pelletCircle = entry.getValue();
                double distance = player.getPosition().distance(pellet.getPosition());

                if (distance <= eventHorizon) {
                    player.setMass(player.getMass() + pellet.getMass());
                    playerCircle.setRadius(player.calculateRadius());
                    pane.getChildren().remove(pelletCircle);
                    return true;
                }
                return false;
            });
        }
    }

    public void spawnPellets() {
        if (pelletCircles.size() < 60) { // Maintain at least ?? pellets on the map
            double x = random.nextDouble() * pane.getWidth();
            double y = random.nextDouble() * pane.getHeight();
            Pellet pellet = EntityFactory.createPellet(new Point2D(x, y), 1);
            addPellet(pellet);
        }
    }
}