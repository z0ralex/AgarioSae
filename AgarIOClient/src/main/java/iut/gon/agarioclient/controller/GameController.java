package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.model.NoEffectPelletFactory;
import iut.gon.agarioclient.model.Pellet;
import iut.gon.agarioclient.model.Player;
import iut.gon.agarioclient.model.PlayerLeaf;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.ParallelCamera;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {

    @FXML
    private Pane pane;

    private ParallelCamera camera;

    public static final int X_MAX = 8000;
    public static final int Y_MAX = 6000;
    private static final int INITIAL_PELLET_NB = 20;
    private static final int MAX_PELLET = 500;

    private double xScale;
    private double yScale;
    private MapNode root;
    private Map<Player, Circle> playerCircles = new HashMap<>();
    private Map<Pellet, Circle> pelletCircles = new HashMap<>();
    private NoEffectPelletFactory pelletFactory = new NoEffectPelletFactory();

    public void initializeGame(String nickname, ParallelCamera camera) {
        if (pane == null) {
            throw new IllegalStateException("Pane is not initialized. Ensure the FXML file is correctly configured.");
        }

        this.camera = camera;

        root = new MapNode(4, new Point2D(0, 0), new Point2D(X_MAX, Y_MAX));

        Player player = new Player(nickname, new Point2D(400, 300), 10);
        player.add(new PlayerLeaf(nickname, new Point2D(400, 300), 10, 5));

        addPlayer(player);
        createPellets(INITIAL_PELLET_NB); // Create 20 pellets initially

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

        player.positionProperty().addListener((obs, oldPoint, newPoint) -> {
            double x = newPoint.getX() - ((pane.getWidth() / 2) * camera.getScaleX());
            double y = newPoint.getY() - ((pane.getHeight() / 2) * camera.getScaleY());
            camera.setLayoutX(x);
            camera.setLayoutY(y);
        });

        player.massProperty().addListener((obs, oldMass, newMass) -> {
            playerCircle.setRadius(player.calculateRadius()); // update du radius du joueur
            setZoomFromMass(newMass.doubleValue() - oldMass.doubleValue()); // update du zoom de la camera
        });
    }

    private void setZoomFromMass(double deltaMass) {
        System.out.println(camera.scaleXProperty().doubleValue());
        camera.setScaleX(camera.getScaleX() + 1. / (deltaMass * 100.));
        camera.setScaleY(camera.getScaleY() + 1. / (deltaMass * 100.));
    }

    public void updatePlayerPosition(Player player) {
        Circle playerCircle = playerCircles.get(player);
        if (playerCircle != null) {
            playerCircle.setCenterX(player.getPosition().getX());
            playerCircle.setCenterY(player.getPosition().getY());
        }
    }

    public void createPellets(int count) {
        List<Pellet> pellets = pelletFactory.generatePellets(count);
        for (Pellet pellet : pellets) {
            addPellet(pellet);
        }
    }

    public void addPellet(Pellet pellet) {
        Circle pelletCircle = new Circle(pellet.getPosition().getX(), pellet.getPosition().getY(), pellet.calculateRadius());
        root.addEntity(pellet);
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
                    pane.getChildren().remove(pelletCircle);
                    pellet.removeFromCurrentNode();
                    return true;
                }
                return false;
            });
        }
    }

    public void spawnPellets() {
        if (pelletCircles.size() < MAX_PELLET) { // Maintain at least 100 pellets on the map
            createPellets(1);
        }
    }
}