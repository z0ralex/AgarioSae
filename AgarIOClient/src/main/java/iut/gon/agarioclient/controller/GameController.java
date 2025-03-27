// GameController.java
package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.App;
import iut.gon.agarioclient.model.Game;
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

    private ParallelCamera camera;




    //TODO SUPPRIMER APRES SEPARATION CONTROLEUR/MODELE
    public static final int X_MAX = 8000;
    public static final int Y_MAX = 6000;
    private static final int INITIAL_PLAYER_MASS = 10;
    private static final int INITIAL_PELLET_NB = 20;
    private static final int MAX_PELLET = 1500;
    private static final int INITIAL_PLAYER_SPEED = 5;
    private static final double PLAYER_SPAWNPOINT_X = 400;
    private static final double PLAYER_SPAWNPOINT_Y = 300;


    private Game game;

    //FIN VARIABLES COTE MODELE

    private static final double NO_MOVE_DISTANCE = 10;


    private final Map<Player, Circle> playerCircles = new HashMap<>();
    private final Map<Pellet, Circle> pelletCircles = new HashMap<>();
    private final Map<Ennemy, Circle> ennemyCircles = new HashMap<>();
    private final NoEffectPelletFactory pelletFactory = new NoEffectPelletFactory();
    private AnimationManager animationManager;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    private final SimpleDoubleProperty scale = new SimpleDoubleProperty(1.0);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameSubscene.widthProperty().bind(container.widthProperty());
        gameSubscene.heightProperty().bind(container.heightProperty());


    }

    public void initializeGame(String nickname, ParallelCamera camera, Game game) {
        this.game = game;

        if (pane == null) {
            throw new IllegalStateException("Pane is not initialized. Ensure the FXML file is correctly configured.");
        }

        animationManager = new AnimationManager(pane);

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

        Player player = game.addPlayer(nickname);

        //TODO retirer


        addPlayer(player);

        //partie controller
        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                final Point2D[] mousePosition = {new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y)};

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
                        mousePosition[0] = new Point2D(xPosition, yPosition);
                        mouseVector.setValue(new Point2D(xVect, yVect).normalize()); //TODO pas forcément normaliser : selon l'emplacement de la souris la vitesse change
                    }
                });

                new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        if (!player.isAlive()) {
                            Platform.runLater(() -> handlePlayerDeath()); //purement client
                            stop();
                            return;
                        }

                        game.checkEntityChunck(player); //va falloir changer ça

                        double speed = player.calculateSpeed(mousePosition[0].getX(), mousePosition[0].getY(), X_MAX, Y_MAX);
                        player.setSpeed(speed);


                        game.moveEntity(player, mouseVector.getValue());

                        //graphique
                        redrawPlayer(player);

                        //TODO serveur PUTAIN C'EST CHAUD CA
                        Set<Pellet> eatenPellets = player.checkCollisionsWithPellet(pelletCircles.keySet());

                        for (Pellet p: eatenPellets) {
                            animationManager.playPelletAbsorption(pelletCircles.get(p), player.getPosition());
                            unrenderEntity(p);
                        }

                        //client
                        if(!(player.isVisible())){
                            playerCircles.get(player).setOpacity(0.02);
                        } else{
                            playerCircles.get(player).setOpacity(1);
                        }

                        //TODO la meme qu'en haut
                        Set<Ennemy> eatenEnemies = player.checkCollisionsWithEnemies(ennemyCircles.keySet());

                        for (Ennemy e: eatenEnemies) {
                            animationManager.playPelletAbsorption(ennemyCircles.get(e), player.getPosition());
                            unrenderEntity(e);
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
        game.checkEntityChunck(player); //TODO modele
    }

    private void setZoomFromMass(double deltaMass) {

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

        game.getRoot().addEntity(e); //TODO modele

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

    public void redrawPlayer(Player player) {
        Circle playerCircle = playerCircles.get(player);
        if (playerCircle != null) {
            playerCircle.setCenterX(player.getPosition().getX());
            playerCircle.setCenterY(player.getPosition().getY());
        }
    }

    public void redrawEnemy(Ennemy e) {
        Circle ennemyCircle = ennemyCircles.get(e);
        if (ennemyCircle != null) {
            ennemyCircle.setCenterX(e.getPosition().getX());
            ennemyCircle.setCenterY(e.getPosition().getY());
            ennemyCircle.setRadius(e.calculateRadius());
        }
    }

    public void addPellet(Pellet pellet) {
        Circle pelletCircle = new Circle(pellet.getPosition().getX(), pellet.getPosition().getY(), pellet.calculateRadius());

        Random color = new Random();

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
            int selector = color.nextInt(6);
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



    /**
     * permet de générer le rendu d'une entité à l'écran
     */
    public void renderEntity(Entity entity) {

        if (entity instanceof Ennemy) {
            Ennemy e = (Ennemy) entity;
            if(ennemyCircles.containsKey(e)) redrawEnemy(e);
            else addEnnemy(e);

        } else if (entity instanceof Player) {
            Player p = (Player) entity;
            if(playerCircles.containsKey(p)) redrawPlayer(p);
            else addPlayer(p);
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
            pane.getChildren().remove(entityCircle);

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