// GameController.java
package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.model.*;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private AnchorPane container;

    @FXML
    private Pane pane;
    @FXML
    private SubScene gameSubscene;

    private Point2D cameraOffsetPoint;
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

    private MapNode root;
    private final Map<Player, Circle> playerCircles = new HashMap<>();
    private final Map<Pellet, Circle> pelletCircles = new HashMap<>();
    private final Map<Ennemy, Circle> ennemyCircles = new HashMap<>();
    private final NoEffectPelletFactory pelletFactory = new NoEffectPelletFactory();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameSubscene.widthProperty().bind(container.widthProperty());
        gameSubscene.heightProperty().bind(container.heightProperty());

    }

    public void initializeGame(String nickname, ParallelCamera camera) {

        if (pane == null) {
            throw new IllegalStateException("Pane is not initialized. Ensure the FXML file is correctly configured.");
        }

        cameraOffsetPoint = new Point2D(container.getWidth() / 2., container.getHeight() / 2.);
        this.camera = camera;
        camera.setLayoutX(cameraOffsetPoint.getX());
        camera.setLayoutY(cameraOffsetPoint.getY());

        //System.out.println(pane.getScene().equals(gameSubscene));
//       gameSubscene.widthProperty().bind(pane.widthProperty());
//       gameSubscene.heightProperty().bind(pane.heightProperty()); //BUGGE
        //pane.scene

        gameSubscene.setCamera(camera);
        //update de la caméra si le pane change de taille
        ChangeListener<? super Number> sizeChange = (obs, oldWidth, newWidth) -> {
            cameraOffsetPoint = new Point2D((container.getWidth() / 2) * camera.getScaleX(),
                    (container.getHeight() / 2) * camera.getScaleX());
        };

        pane.widthProperty().addListener(sizeChange);
        pane.heightProperty().addListener(sizeChange);


        root = new MapNode(4, new Point2D(0, 0), new Point2D(X_MAX, Y_MAX));
        root.drawBorders(pane);

        Player player = new Player(nickname, new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS);

        player.add(new PlayerLeaf(nickname, new Point2D(PLAYER_SPAWNPOINT_Y, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS, INITIAL_PLAYER_SPEED));

        NoEffectLocalEnnemyFactory f = new NoEffectLocalEnnemyFactory(root);

        List<Ennemy> list = f.generate(3);
        for (int i = 0; i < list.size(); i++) {
            addEnnemy(list.get(i)); //TODO render selon distance
            root.addEntity(list.get(i));
        }

        root.addEntity(player);

        addPlayer(player);
        createPellets(INITIAL_PELLET_NB);

        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                final Point2D[] mousePosition = {new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y)}; //TODO retirer

                final SimpleObjectProperty<Point2D> mouseVector = new SimpleObjectProperty<>(Point2D.ZERO); // représente un vecteur, pas une position
                // property parce que j'ai besoin que ça soit final, peut etre des legers coûts en perf

                newScene.setOnMouseMoved(event -> {
                    double xPosition = event.getX();
                    double yPosition = event.getY();




                    double xVect = xPosition - Math.abs(player.getPosition().getX() - camera.getLayoutX());
                    double yVect = yPosition - Math.abs(player.getPosition().getY() - camera.getLayoutY());

                    System.out.printf("Souris : [%.0f, %.0f], Joueur : [%.0f, %.0f], Vecteur : [%.0f, %.0f], Camera: [%.0f, %.0f]\n", event.getX(), event.getY(), player.getPosition().getX(),
                            player.getPosition().getY(), xVect, yVect, camera.getLayoutX(), camera.getLayoutY());

                    if (Math.abs(xVect) < NO_MOVE_DISTANCE && Math.abs(yVect) < NO_MOVE_DISTANCE) {
                        //zone morte : reset du vecteur
                        mouseVector.setValue(Point2D.ZERO);
                    } else {
                        // mouvement
                        mousePosition[0] = new Point2D(xPosition, yPosition); //TODO retirer
                        mouseVector.setValue(new Point2D(xVect, yVect).normalize()); //TODO pas forcément normaliser : selon l'emplacement de la souris la vitesse change
                    }
                });

                new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        double speed = player.calculateSpeed(mousePosition[0].getX(), mousePosition[0].getY(), X_MAX, Y_MAX); //TODO changer
                        player.setSpeed(speed);

                        for (int i = 0; i < list.size(); i++) {
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

                        for (int i = 0; i < list.size(); i++) {
                            updateEnnemyPosition(list.get(i));

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

        player.currentMapNodeProperty().addListener((obs, oldChunk, newChunk) -> {
            if (newChunk != null) {
                updateLoadedChunks(newChunk);
            }
        });

        // change la position de la camera en fonction de la position du joueur
        player.positionProperty().addListener((obs, oldPoint, newPoint) -> {
            onPlayerPositionChanged(player, newPoint);
        });

        player.massProperty().addListener((obs, oldMass, newMass) -> {
            playerCircle.setRadius(player.calculateRadius()); // update du radius du joueur
            setZoomFromMass(newMass.doubleValue() - oldMass.doubleValue()); // update du zoom de la camera
        });
    }

    private void onPlayerPositionChanged(Player player, Point2D newPos) {
        double x = newPos.getX() - cameraOffsetPoint.getX();
        double y = newPos.getY() - cameraOffsetPoint.getY();

        camera.setLayoutX(x);
        camera.setLayoutY(y);


        //mets à jour le chunk du joueur
        if (!player.getCurrentMapNode().positionInNode(newPos.getX(), newPos.getY())) {

            player.removeFromCurrentNode();
            root.addEntity(player);
        }
    }

    private void setZoomFromMass(double deltaMass) {

        // formule de calcul de la taille de la camera
        // peut être ajustee
        double newScale = camera.getScaleX() + 1. / (deltaMass * 100.);

        camera.setScaleX(newScale);
        camera.setScaleY(newScale);

        // le zoom change : on doit recalculer le centre de la caméra
        cameraOffsetPoint = new Point2D(
                (container.getWidth() / 2) * camera.getScaleX(),
                (container.getHeight() / 2) * camera.getScaleY()
        );
    }

    public void addEnnemy(Ennemy e) {
        Circle ennemyCircle = new Circle(e.getPosition().getX(), e.getPosition().getY(), 25);//Attention Valeur en DUR
        ennemyCircle.setFill(Color.RED);
        ennemyCircles.put(e, ennemyCircle);
        pane.getChildren().add(ennemyCircle);
        //System.out.println(ennemyCircle);

        e.positionProperty().addListener((obs, oldPoint, newPoint) -> {
            ennemyCircle.setCenterX(newPoint.getX());
            ennemyCircle.setCenterY(newPoint.getY());
        });

        e.massProperty().addListener((obs, oldMass, newMass) -> {
            ennemyCircle.setRadius(e.calculateRadius()); // update du radius de l'ennemi
        });
    }

    public void updatePlayerPosition(Player player) {
        Circle playerCircle = playerCircles.get(player);
        if (playerCircle != null) {
            playerCircle.setCenterX(player.getPosition().getX());
            playerCircle.setCenterY(player.getPosition().getY());
        }
    }

    public void updateEnnemyPosition(Ennemy ennemy) {
        Circle ennemyCircle = ennemyCircles.get(ennemy);
        if (ennemyCircle != null) {
            ennemyCircle.setCenterX(ennemy.getPosition().getX());
            ennemyCircle.setCenterY(ennemy.getPosition().getY());
        }
    }


    public void createPellets(int count) {
        List<Pellet> pellets = pelletFactory.generatePellets(count);
        for (Pellet pellet : pellets) {
            root.addEntity(pellet);
            addPellet(pellet);  //TODO render selon distance
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

            pelletCircles.entrySet().removeIf(entry -> { //retire les pellets qui sont trop proches du joueur

                Pellet pellet = entry.getKey();
                Circle pelletCircle = entry.getValue();
                double distance = player.getPosition().distance(pellet.getPosition());

                if (distance <= eventHorizon) {
                    // pellet mangé

                    player.setMass(player.getMass() + pellet.getMass());
                    pane.getChildren().remove(pelletCircle);
                    pellet.removeFromCurrentNode();

                    return true;
                }

                return false;
            });
        }
    }

    public void checkCollisions(Ennemy ennemy) {
        Circle ennemyCircle = ennemyCircles.get(ennemy);
        if (ennemyCircle != null) {
            double enemyRadius = ennemyCircle.getRadius();
            double eventHorizon = enemyRadius + 100;

            pelletCircles.entrySet().removeIf(entry -> {
                Pellet pellet = entry.getKey();
                Circle pelletCircle = entry.getValue();
                double distance = ennemy.getPosition().distance(pellet.getPosition());

                if (distance <= eventHorizon) {
                    ennemy.setMass(ennemy.getMass() + pellet.getMass());
                    //pane.getChildren().remove(pelletCircle);
                    unrenderEntity(pellet);
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

    /**
     * permet de générer le rendu d'une entité à l'écran
     */
    public void renderEntity(Entity entity) {
        //TODO délèguer la méthode à l'entité ? (pas sûr que ca respecte le MVC)

        if (entity instanceof Ennemy) {
            addEnnemy((Ennemy) entity);
        } else if (entity instanceof Player) {
            addPlayer((Player) entity);
        } else {
            //pellet
            addPellet((Pellet) entity);
        }
    }

    public void unrenderEntity(Entity entity) {
        Circle entityCircle;

        if (entity instanceof Ennemy) {
            entityCircle = ennemyCircles.get(entity);
            ennemyCircles.remove(entity, entityCircle);
        } else if (entity instanceof Player) {
            entityCircle = playerCircles.get(entity);
            playerCircles.remove(entity, entityCircle);
        } else {
            //Pellet
            entityCircle = pelletCircles.get(entity);
            pelletCircles.remove(entity, entityCircle);
        }

        pane.getChildren().remove(entityCircle);
    }

    public void updateLoadedChunks(MapNode currentChunk) {
        //System.out.println("update du chunk");
    }


}