// GameController.java
package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.model.*;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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

    private Point2D cameraCenterPoint;
    private ParallelCamera camera;

    public static final int X_MAX = 8000;
    public static final int Y_MAX = 6000;
    private static final int INITIAL_PELLET_NB = 20;
    private static final int MAX_PELLET = 1500;

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
    private Map<Ennemy, Circle> ennemyCircles = new HashMap<>();
    private NoEffectPelletFactory pelletFactory = new NoEffectPelletFactory();

    public void initializeGame(String nickname, ParallelCamera camera) {
        if (pane == null) {
            throw new IllegalStateException("Pane is not initialized. Ensure the FXML file is correctly configured.");
        }

        cameraCenterPoint = new Point2D(pane.getWidth() / 2., pane.getHeight() / 2.);
        this.camera = camera;
        camera.setLayoutX(cameraCenterPoint.getX());
        camera.setLayoutY(cameraCenterPoint.getY());

        ChangeListener sizeChange = (obs, oldWidth, newWidth) -> {
            cameraCenterPoint = new Point2D(pane.getWidth() / 2, pane.getHeight() / 2);
        };

        pane.widthProperty().addListener(sizeChange);
        pane.heightProperty().addListener(sizeChange);

        root = new MapNode(4, new Point2D(0, 0), new Point2D(X_MAX, Y_MAX));
        root.drawBorders(pane);

        Player player = new Player(nickname, new Point2D(400, 300), 10);
        player.add(new PlayerLeaf(nickname, new Point2D(400, 300), 10, 5));

        NoEffectLocalEnnemyFactory f = new NoEffectLocalEnnemyFactory(root);
        List<Ennemy> list = f.generate(3);
        for(int i = 0; i < list.size(); i++){
            addEnnemy(list.get(i));
        }

        addPlayer(player);
        createPellets(INITIAL_PELLET_NB);

        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                final Point2D[] mousePosition = {new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y)};
                final SimpleObjectProperty<Point2D> mouseVector = new SimpleObjectProperty<>(Point2D.ZERO);

                newScene.setOnMouseMoved(event -> {
                    double xPosition = event.getX();
                    double yPosition = event.getY();

                    double xVect = (xPosition - player.getPosition().getX());
                    double yVect = (yPosition - player.getPosition().getY());

                    if (Math.abs(xVect) < NO_MOVE_DISTANCE && Math.abs(yVect) < NO_MOVE_DISTANCE) {
                        mouseVector.setValue(Point2D.ZERO);
                    } else {
                        mousePosition[0] = new Point2D(xPosition, yPosition);
                        mouseVector.setValue(new Point2D(xVect, yVect).normalize());
                    }
                });

                new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        double speed = player.calculateSpeed(mousePosition[0].getX(), mousePosition[0].getY(), X_MAX, Y_MAX);
                        player.setSpeed(speed);

                        for(int i = 0; i < list.size(); i++){
                            list.get(i).executeStrat();
                            double speedE = list.get(i).calculateSpeed(list.get(i).getPosition().getX(), list.get(i).getPosition().getY(), X_MAX, Y_MAX);
                            list.get(i).setSpeed(speedE);
                        }


                        Point2D newPosition = player.getPosition().add(mouseVector.get().multiply(player.getSpeed()));

                        // Check for collisions with the map boundaries
                        double newX = Math.max(0, Math.min(newPosition.getX(), X_MAX));
                        double newY = Math.max(0, Math.min(newPosition.getY(), Y_MAX));
                        newPosition = new Point2D(newX, newY);


                        player.setPosition(newPosition);

                        updatePlayerPosition(player);
                        player.checkCollisions(pelletCircles, pane);
                        spawnPellets();

                        for(int i = 0; i < list.size(); i++){
                            updateEnnemyPosition(list.get(i));
                            list.get(i).checkCollisions(pelletCircles, pane);

                        }
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
            double x = newPoint.getX() - cameraCenterPoint.getX();
            double y = newPoint.getY() - cameraCenterPoint.getY();
            camera.setLayoutX(x);
            camera.setLayoutY(y);
        });

        player.massProperty().addListener((obs, oldMass, newMass) -> {
            playerCircle.setRadius(player.calculateRadius());
            setZoomFromMass(newMass.doubleValue() - oldMass.doubleValue());
        });
    }

    public void addEnnemy(Ennemy e) {
        Circle ennemyCircle = new Circle(e.getPosition().getX(), e.getPosition().getY(), 25);//Attention Valeur en DUR
        if (e.getStrat() instanceof IAStratEatPelletsOnly) {
            ennemyCircle.setFill(Color.YELLOW);
        } else {
            ennemyCircle.setFill(Color.RED);
        }
        ennemyCircles.put(e, ennemyCircle);
        pane.getChildren().add(ennemyCircle);
        System.out.println(ennemyCircle);

        e.positionProperty().addListener((obs, oldPoint, newPoint) -> {
            double x = newPoint.getX() - ((pane.getWidth() / 2) * camera.getScaleX());
            double y = newPoint.getY() - ((pane.getHeight() / 2) * camera.getScaleY());
            ennemyCircle.setCenterX( e.getPosition().getX());
            ennemyCircle.setCenterY( e.getPosition().getY());
        });

        e.massProperty().addListener((obs, oldMass, newMass) -> {
            ennemyCircle.setRadius(e.calculateRadius()); // update du radius du joueur
        });
    }

    private void setZoomFromMass(double deltaMass) {
        double newScale = camera.getScaleX() + 1. / (deltaMass * 100.);
        camera.setScaleX(newScale);
        camera.setScaleY(newScale);

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


    public void updateEnnemyPosition(Ennemy e) {
        Circle ennemyCircle = ennemyCircles.get(e);
        if (ennemyCircle != null) {
            ennemyCircle.setCenterX(e.getPosition().getX());
            ennemyCircle.setCenterY(e.getPosition().getY());
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

    public void spawnPellets() {
        if (pelletCircles.size() < MAX_PELLET) { // Maintain at least 100 pellets on the map
            createPellets(1);
        }
    }
}