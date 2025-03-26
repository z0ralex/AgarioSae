package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.model.NoEffectPelletFactory;
import iut.gon.agarioclient.model.Pellet;
import iut.gon.agarioclient.model.Player;
import iut.gon.agarioclient.model.PlayerLeaf;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.ParallelCamera;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GameController {

    @FXML
    private Pane pane;

    private Point2D cameraCenterPoint;
    private ParallelCamera camera;

    public static final int X_MAX = 8000;
    public static final int Y_MAX = 6000;
    private static final int INITIAL_PELLET_NB = 20;
    private static final int MAX_PELLET = 500;

    private static final int INITIAL_PLAYER_MASS = 10;

    private static final int INITIAL_PLAYER_SPEED = 5;

    private static final double PLAYER_SPAWNPOINT_X = 400;
    private static final double PLAYER_SPAWNPOINT_Y = 300;
    private static final double NO_MOVE_DISTANCE = 10;


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

        //System.out.println(pane.ge);
        cameraCenterPoint = new Point2D(pane.getWidth() / 2., pane.getHeight() / 2.); //BUGGE

        this.camera = camera;

        camera.setLayoutX(cameraCenterPoint.getX());
        camera.setLayoutY(cameraCenterPoint.getY());



        //update de la caméra si le pane change de taille

        ChangeListener sizeChange = (obs, oldWidth, newWidth)->{
            cameraCenterPoint = new Point2D(pane.getWidth() / 2,
                    pane.getHeight() / 2);
        };

        pane.widthProperty().addListener(sizeChange);
        pane.heightProperty().addListener(sizeChange);


        root = new MapNode(4, new Point2D(0, 0), new Point2D(X_MAX, Y_MAX));
        root.drawBorders(pane);

        Player player = new Player(nickname, new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS);
        player.add(new PlayerLeaf(nickname, new Point2D(PLAYER_SPAWNPOINT_Y, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS, INITIAL_PLAYER_SPEED));

        addPlayer(player);
        createPellets(INITIAL_PELLET_NB); // Create 20 pellets initially

        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {

                final Point2D[] mousePosition = {new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y)}; //TODO retirer

                final SimpleObjectProperty<Point2D> mouseVector = new SimpleObjectProperty<>(Point2D.ZERO); // représente un vecteur, pas une position
                // property parce que j'ai besoin que ça soit final, peut etre des legers coûts en perf

                newScene.setOnMouseMoved(event -> {
                    double xPosition = event.getX();
                    double yPosition = event.getY();

                    double xVect = xPosition - player.getPosition().getX();
                    double yVect = yPosition - player.getPosition().getY();

                    // on vérifie si la souris est assez loin du joueur
                    //if(Math.abs(xVect) < NO_MOVE_DISTANCE || Math.abs(yVect) < NO_MOVE_DISTANCE){
                        mousePosition[0] = new Point2D(xPosition, yPosition); //TODO retirer
                        mouseVector.setValue(new Point2D(xVect, yVect).normalize()); //TODO pas forcément normaliser : selon l'emplacement de la souris la vitesse change
                    //}
                });

                new javafx.animation.AnimationTimer() {
                    @Override
                    public void handle(long now) {

                        double speed = player.calculateSpeed(mousePosition[0].getX(), mousePosition[0].getY(), X_MAX, Y_MAX); //TODO changer
                        player.setSpeed(speed);

                        //Point2D direction = mousePosition[0].subtract(player.getPosition()).normalize();

                        Point2D newPosition = player.getPosition().add(mouseVector.get().multiply(player.getSpeed()));

                        // Check for collisions with the map boundaries
                        double newX = Math.max(0, Math.min(newPosition.getX(), X_MAX));
                        double newY = Math.max(0, Math.min(newPosition.getY(), Y_MAX));
                        newPosition = new Point2D(newX, newY);


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

        playerCircle.toFront();

        // change la position de la camera en fonction de la position du joueur
        player.positionProperty().addListener((obs, oldPoint, newPoint) -> {

            double x = newPoint.getX() - cameraCenterPoint.getX();
            double y = newPoint.getY() - cameraCenterPoint.getY();

            camera.setLayoutX(x);
            camera.setLayoutY(y);
        });

        player.massProperty().addListener((obs, oldMass, newMass) -> {
            playerCircle.setRadius(player.calculateRadius()); // update du radius du joueur
            setZoomFromMass(newMass.doubleValue() - oldMass.doubleValue()); // update du zoom de la camera
        });
    }

    private void setZoomFromMass(double deltaMass) {

        // formule de calcul de la taille de la camera
        // peut être ajustee
        double newScale = camera.getScaleX() + 1. / (deltaMass * 100.);

        camera.setScaleX(newScale);
        camera.setScaleY(newScale);

        // le zoom change : on doit recalculer le centre de la caméra
        cameraCenterPoint = new Point2D(
                (pane.getWidth() / 2) * camera.getScaleX(),
                (pane.getHeight() / 2) * camera.getScaleY()
        );
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

            pelletCircles.entrySet().removeIf(entry -> { //retirer les pellets qui sont trop proches du joueur

                Pellet pellet = entry.getKey();
                Circle pelletCircle = entry.getValue();
                double distance = player.getPosition().distance(pellet.getPosition());

                if (distance <= eventHorizon) {

                    Point2D direction = player.getDirection();
                    double speed = player.getSpeed();

                    double transitionDuration = Math.max(100, distance / speed);

                    Point2D predictedPosition = player.getPosition().add(direction.multiply(speed * (transitionDuration / 1000.0)));

                    double toX = predictedPosition.getX() - pellet.getPosition().getX();
                    double toY = predictedPosition.getY() - pellet.getPosition().getY();

                    TranslateTransition transition = new TranslateTransition(Duration.millis(transitionDuration), pelletCircle);
                    transition.setToX(toX);
                    transition.setToY(toY);

                    transition.setOnFinished(event -> {
                        player.setMass(player.getMass() + pellet.getMass());
                        pane.getChildren().remove(pelletCircle);
                        pellet.removeFromCurrentNode();
                        playerCircles.get(player).toFront();
                    });

                    transition.play();

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