// GameController.java
package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.App;
import iut.gon.agarioclient.model.entity.ia.IAStratEatPlayers;
import iut.gon.agarioclient.model.entity.ia.IAStratRandomMoving;
import iut.gon.agarioclient.model.entity.moveable.*;
import iut.gon.agarioclient.model.entity.pellet.*;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleDoubleProperty;
import iut.gon.agarioclient.server.TestVecteur;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import javafx.scene.shape.Line;

import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private AnchorPane container;

    @FXML
    private Pane pane;
    @FXML
    private SubScene gameSubscene;

    @FXML
    private StackPane chat;

    private Point2D cameraOffsetPoint;
    private Stage stage;

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
    private AnimationManager animationManager;

    private String nickname;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    private final SimpleDoubleProperty scale = new SimpleDoubleProperty(1.0);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameSubscene.widthProperty().bind(container.widthProperty());
        gameSubscene.heightProperty().bind(container.heightProperty());

        gameSubscene.setFocusTraversable(true);
        gameSubscene.setOnMouseClicked(event -> gameSubscene.requestFocus());

        // Ajout du gestionnaire d'événements pour la touche espace
        gameSubscene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                System.out.println("Space pressed");
                Player player = playerCircles.keySet().iterator().next();
                player.divide();
                updatePlayerVisual(player);
            }
        });
    }

    private void updatePlayerVisual(Player player) {
        Circle mainCircle = playerCircles.get(player);
        if (mainCircle != null) {
            pane.getChildren().remove(mainCircle);
            playerCircles.remove(player);
        }

        // Crée un nouveau cercle pour le joueur
        Circle playerCircle = new Circle(player.getPosition().getX(), player.getPosition().getY(), player.calculateRadius());
        playerCircle.setFill(Color.BLUE);
        playerCircles.put(player, playerCircle);
        pane.getChildren().add(playerCircle);
        playerCircle.toFront();
    }


    public void initializeGame(String nickname, ParallelCamera camera) {

        if (pane == null) {
            throw new IllegalStateException("Pane is not initialized. Ensure the FXML file is correctly configured.");
        }

        this.nickname = nickname;
        animationManager = new AnimationManager(pane);

        cameraCenterPoint = new Point2D(pane.getWidth() / 2., pane.getHeight() / 2.);

        cameraOffsetPoint = new Point2D(container.getWidth() / 2., container.getHeight() / 2.);
        this.camera = camera;
        camera.setLayoutX(cameraOffsetPoint.getX());
        camera.setLayoutY(cameraOffsetPoint.getY());

        scale.bind(camera.scaleXProperty());
        drawGrid();

        gameSubscene.setCamera(camera);
        //update de la caméra si le pane change de taille
        ChangeListener<? super Number> sizeChange = (obs, oldWidth, newWidth) -> {
            cameraOffsetPoint = new Point2D((container.getWidth() / 2) * camera.getScaleX(),
                    (container.getHeight() / 2) * camera.getScaleX());
        };

        pane.widthProperty().addListener(sizeChange);
        pane.heightProperty().addListener(sizeChange);


        root = new MapNode(4, new Point2D(0, 0), new Point2D(X_MAX, Y_MAX));
        if (root == null) {
            throw new IllegalStateException("Root MapNode is not initialized.");
        }

        Player player = new Player(nickname, new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS);

        player.add(new PlayerLeaf(nickname, new Point2D(PLAYER_SPAWNPOINT_Y, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS, INITIAL_PLAYER_SPEED));

        NoEffectLocalEnnemyFactory f = new NoEffectLocalEnnemyFactory(root);
        List<Ennemy> list = f.generate(10);
        for(int i = 0; i < list.size(); i++){
            addEnnemy(list.get(i));
        }

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


                    double xVect = xPosition - (player.getPosition().getX() - camera.getLayoutX()) / scale.doubleValue();
                    double yVect = yPosition - (player.getPosition().getY() - camera.getLayoutY()) / scale.doubleValue();


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
                        playerCircles.keySet().forEach(Player::update);

                        if (!player.isAlive()) {
                            Platform.runLater(() -> handlePlayerDeath());
                            stop();
                            return;
                        }

                        if (player.getCurrentMapNode() != null &&
                                !player.getCurrentMapNode().positionInNode(player.getPosition().getX(), player.getPosition().getY())) {
                            player.removeFromCurrentNode();
                            root.addEntity(player);
                        }

                        player.getComponents().forEach(component -> {
                            if(component instanceof PlayerLeaf) {
                                PlayerLeaf leaf = (PlayerLeaf) component;
                                Circle circle = playerCircles.get(leaf);
                                if(circle != null) {
                                    circle.setCenterX(leaf.getPosition().getX());
                                    circle.setCenterY(leaf.getPosition().getY());
                                    circle.setRadius(leaf.calculateRadius());
                                }
                            }
                        });

                        double speed = player.calculateSpeed(mousePosition[0].getX(), mousePosition[0].getY(), X_MAX, Y_MAX); //TODO changer
                        player.setSpeed(speed);

                        for(int i = 0; i < list.size(); i++){
                            list.get(i).executeStrat();
                            double speedE = list.get(i).calculateSpeed(list.get(i).getPosition().getX(), list.get(i).getPosition().getY(), X_MAX, Y_MAX);
                            list.get(i).setSpeed(speedE);
                        }

                        Point2D newPosition = player.getPosition().add(mouseVector.get().multiply(player.getSpeed())/*.multiply(scale.doubleValue())*/); //TODO scaling

                        // Check for collisions with the map boundaries
                        double newX = Math.max(0, Math.min(newPosition.getX(), X_MAX));
                        double newY = Math.max(0, Math.min(newPosition.getY(), Y_MAX));
                        newPosition = new Point2D(newX, newY);

                        player.setPosition(newPosition);

                        updatePlayerPosition(player);
                        player.checkCollisionsWithPellet(pelletCircles, pane, animationManager);
                        if(!(player.isVisible())){
                            playerCircles.get(player).setOpacity(0.02);
                        } else{
                            playerCircles.get(player).setOpacity(1);
                        }
                        player.checkCollisionsWithEnemies(ennemyCircles, pane, animationManager);
                        spawnPellets();

                        for (int i = 0; i < list.size(); i++) {
                            updateEnnemyPosition(list.get(i));
                            list.get(i).checkCollisions(pelletCircles, ennemyCircles, pane);
                            list.get(i).checkCollisionsWithEnemies(ennemyCircles, pane, animationManager);
                            list.get(i).checkCollisionsWithPlayers(playerCircles, pane, animationManager);

                        }
                    }
                }.start();
            }
        });
    }

    private void handlePlayerDeath() {
        try {
            // Clear all game elements
            pane.getChildren().clear();
            playerCircles.clear();
            pelletCircles.clear();
            ennemyCircles.clear();

            // Load welcome view
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/iut/gon/agarioclient/welcome-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 600);

            WelcomeController welcomeController = fxmlLoader.getController();
            welcomeController.setStage(stage);
            welcomeController.setNickname(nickname);

            stage.setTitle("Welcome to AgarIO");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateFromServer(TestVecteur t) {
        System.out.println(t.toString());
    }

    private void drawGrid() {
        pane.getChildren().clear();

        for (int x = 0; x <= X_MAX; x += 200) {
            Line verticalLine = new Line(x, 0, x, Y_MAX);
            verticalLine.setStroke(Color.LIGHTGRAY);
            verticalLine.setOpacity(0.5);
            pane.getChildren().add(verticalLine);
        }

        for (int y = 0; y <= Y_MAX; y += 200) {
            Line horizontalLine = new Line(0, y, X_MAX, y);
            horizontalLine.setStroke(Color.LIGHTGRAY);
            horizontalLine.setOpacity(0.5);
            pane.getChildren().add(horizontalLine);
        }
    }

    public void addPlayer(Player player) {
        Circle playerCircle = new Circle(player.getPosition().getX(), player.getPosition().getY(), player.calculateRadius());

        root.addEntity(player);
        playerCircle.setFill(Color.BLUE);
        playerCircles.put(player, playerCircle);
        pane.getChildren().add(playerCircle);
        playerCircle.toFront();
        player.currentMapNodeProperty().addListener((obs, oldChunk, newChunk)-> {
            if (newChunk != null){
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
        double x = (newPos.getX() - cameraOffsetPoint.getX()) ;
        double y = (newPos.getY() - cameraOffsetPoint.getY()) ;


        camera.setLayoutX(x);
        camera.setLayoutY(y);

        //mets à jour le chunk du joueur
        if (!player.getCurrentMapNode().positionInNode(newPos.getX(), newPos.getY())) {

            player.removeFromCurrentNode();
            root.addEntity(player);
        }
    }

    private void setZoomFromMass(double deltaMass) {
        //System.out.println("scale : " + scale.doubleValue());
        // formule de calcul de la taille de la camera
        // peut être ajustee
        double newScale = camera.getScaleX() + 1. / (deltaMass * 200.);

        camera.setScaleX(newScale);
        camera.setScaleY(newScale);

        // le zoom change : on doit recalculer le centre de la caméra
        cameraOffsetPoint = new Point2D(
                (container.getWidth() / 2) * camera.getScaleX(),
                (container.getHeight() / 2) * camera.getScaleY()

        );
    }

    public void addEnnemy(Ennemy e) {
        Circle ennemyCircle = new Circle(e.getPosition().getX(), e.getPosition().getY(), e.calculateRadius());

        root.addEntity(e);
        if (e.getStrat() instanceof IAStratEatPlayers) {
            ennemyCircle.setFill(Color.RED);
        } else if (e.getStrat() instanceof IAStratRandomMoving){
            ennemyCircle.setFill(Color.GREEN);
        } else {
            ennemyCircle.setFill(Color.BLUE);
        }
        ennemyCircles.put(e, ennemyCircle);
        pane.getChildren().add(ennemyCircle);

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
            root.addEntity(pellet);
            addPellet(pellet);  //TODO render selon distance
        }
    }

    public void addPellet(Pellet pellet) {
        Circle pelletCircle = new Circle(pellet.getPosition().getX(), pellet.getPosition().getY(), pellet.calculateRadius());
        root.addEntity(pellet);

        Random couleur = new Random();

        if(pellet instanceof PartialInvisibilityPellet){
            pelletCircle.setFill(Color.CYAN);
            pelletCircle.setRadius(15.0);
        } else if(pellet instanceof SpeedReductionPellet){
            pelletCircle.setFill(Color.YELLOW);
            pelletCircle.setRadius(15.0);
        }else if(pellet instanceof SpeedBoostPellet){
            pelletCircle.setFill(Color.MAGENTA);
            pelletCircle.setRadius(15.0);
        }else {
            int selector = couleur.nextInt(6);
            switch (selector){
                case 0: pelletCircle.setFill(Color.DARKVIOLET); break;
                case 1: pelletCircle.setFill(Color.BLUE); break;
                case 2: pelletCircle.setFill(Color.GREEN); break;
                case 3: pelletCircle.setFill(Color.RED); break;
                case 4: pelletCircle.setFill(Color.BROWN); break;
                default: pelletCircle.setFill(Color.GREY);

            }

        }

        pelletCircles.put(pellet, pelletCircle);
        pane.getChildren().add(pelletCircle);
        pelletCircle.toBack();
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
        if (pelletCircles.size() < MAX_PELLET) {
            createPellets(100);
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