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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.control.Label;
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
import java.util.stream.Collectors;

public class GameController implements Initializable {

    @FXML
    private AnchorPane container;

    @FXML
    private Pane pane;
    @FXML
    private SubScene gameSubscene;

    @FXML
    private  Pane minimap;

    @FXML
    private Pane classement ;

    private Point2D cameraOffsetPoint;
    private Stage stage;

    private ParallelCamera camera;

    private int debug_cmpt = 0;

    //TODO SUPPRIMER APRES SEPARATION CONTROLEUR/MODELE
    public static final int X_MAX = 8000;
    public static final int Y_MAX = 6000;
    private static final double PLAYER_SPAWNPOINT_X = 400;
    private static final double PLAYER_SPAWNPOINT_Y = 300;
//FIN VARIABLES COTE MODELE

    private Game game;
    private Player ourPlayer;
    private AnimationTimer timer;


    private static final double NO_MOVE_DISTANCE = 10;


    private final Map<Player, Circle> playerCircles = new HashMap<>();
    private final Map<Pellet, Circle> pelletCircles = new HashMap<>();
    private final Map<Ennemy, Circle> ennemyCircles = new HashMap<>();

    private final Map<Player, Circle> minimapPlayerCircles = new HashMap<>();
    private final Map<Ennemy, Circle> minimapEnnemyCircles = new HashMap<>();
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


    }

    public void initializeGame(String nickname, ParallelCamera camera, Game game) {
        this.game = game;

        if (pane == null) {
            throw new IllegalStateException("Pane is not initialized. Ensure the FXML file is correctly configured.");
        }
        this.nickname = nickname;
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

        ourPlayer = game.addPlayer(nickname); //TODO serv
        addPlayer(ourPlayer);

        addPlayerToMinimap(ourPlayer);

        afficherClassement();

        //partie controller
        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                final Point2D[] mousePosition = {new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y)};

                final SimpleObjectProperty<Point2D> mouseVector = new SimpleObjectProperty<>(Point2D.ZERO); // représente un vecteur, pas une position
                // property parce que j'ai besoin que ça soit final, peut etre des legers coûts en perf

                newScene.setOnMouseMoved(event -> {
                    double xPosition = event.getX();
                    double yPosition = event.getY();


                    double xVect = xPosition - (ourPlayer.getPosition().getX() - camera.getLayoutX()) / scale.doubleValue();
                    double yVect = yPosition - (ourPlayer.getPosition().getY() - camera.getLayoutY()) / scale.doubleValue();


                    if (Math.abs(xVect) < NO_MOVE_DISTANCE && Math.abs(yVect) < NO_MOVE_DISTANCE) {
                        //zone morte : reset du vecteur
                        mouseVector.setValue(Point2D.ZERO);
                    } else {
                        // mouvement
                        mousePosition[0] = new Point2D(xPosition, yPosition);
                        mouseVector.setValue(new Point2D(xVect, yVect).normalize()); //TODO pas forcément normaliser : selon l'emplacement de la souris la vitesse change
                    }
                });

                timer = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        HashMap<Entity, Set<Entity>> eatenMap = game.nextTick();

                        if (!ourPlayer.isAlive()) {
                            Platform.runLater(() -> handlePlayerDeath()); //purement client
                            stop();
                            return;
                        }


                        double speed = ourPlayer.calculateSpeed(mousePosition[0].getX(), mousePosition[0].getY(), X_MAX, Y_MAX);
                        ourPlayer.setSpeed(speed);

                        game.moveEntity(ourPlayer, mouseVector.getValue());

                        //graphique
                        //redrawPlayer(ourPlayer);


                        afficherClassement();

                        if(!(ourPlayer.isVisible())){
                            playerCircles.get(ourPlayer).setOpacity(0.02);
                        } else{
                            playerCircles.get(ourPlayer).setOpacity(1);
                        }
                        /*player.checkCollisionsWithEnemies(ennemyCircles, minimapEnnemyCircles, pane, minimap, animationManager);
                        spawnPellets();*/

                        Set<Entity> unrendered = new HashSet<Entity>();

                        //updateEnnemyPosition
                        //pour toutes les entités existantes actuellement
                        for (Entity eatingEntity : eatenMap.keySet()){
                            if(!unrendered.contains(eatingEntity)){
                                renderEntity(eatingEntity);
                            }


                            if(eatenMap.get(eatingEntity) != null){
                                for (Entity eatenEntity : eatenMap.get(eatingEntity)) {
                                    animationManager.playPelletAbsorption(getEntityCircle(eatenEntity),
                                            eatingEntity.getPosition());
                                        unrendered.add(eatenEntity);
                                        unrenderEntity(eatenEntity);//TODO


                                }
                            }
                        }
                    }
                };

                timer.start();
            }
        });
    }

    private Circle getEntityCircle(Entity e){
        if(e instanceof Ennemy){
            return ennemyCircles.get(e);
        } else if (e instanceof Player) {
            return playerCircles.get(e);
        }

        return pelletCircles.get(e);
    }



    public void afficherClassement() {
        // Créer une liste de paires (entité, masse) pour les joueurs et ennemis
        ObservableList<Map.Entry<String, Double>> classement = FXCollections.observableArrayList();

        // Ajouter les joueurs
        for (Map.Entry<Player, Circle> entry : playerCircles.entrySet()) {
            Player player = entry.getKey();
            classement.add(new AbstractMap.SimpleEntry<>(player.getId(), player.getMass()));
        }

        // Ajouter les ennemis
        for (Map.Entry<Ennemy, Circle> entry : ennemyCircles.entrySet()) {
            Ennemy ennemy = entry.getKey();
            classement.add(new AbstractMap.SimpleEntry<>(ennemy.getId(), ennemy.getMass()));
        }

        // Trier par masse, du plus grand au plus petit
        classement.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        // Limiter aux 10 premiers
        List<Map.Entry<String, Double>> top10 = classement.stream().limit(10).collect(Collectors.toList());

        // Afficher dans le Pane de classement
        afficherTop10(top10);
    }

    public void afficherTop10(List<Map.Entry<String, Double>> top10) {
        classement.getChildren().clear(); // Vider le Pane avant de remplir

        int position = 1;
        String str="Classement : \n";
        for (Map.Entry<String, Double> entry : top10) {
            String nom = entry.getKey();
            Double masse = entry.getValue();

            str+=(position + ". Score : " + nom + " : " + String.format("%.2f", masse) + "\n");
            position++;
        }
        Label label = new Label(str);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: black; ");
        label.setPadding(new Insets(10, 10, 10, 10));
        classement.getChildren().add(label);
    }


    private void handlePlayerDeath() {
        try {
            // Clear all game elements
            pane.getChildren().clear();
            playerCircles.clear();
            minimapPlayerCircles.clear();
            pelletCircles.clear();
            ennemyCircles.clear();
            minimapEnnemyCircles.clear();

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
        /*player.positionProperty().addListener((obs, oldPoint, newPoint) -> {
            onPlayerPositionChanged(player, newPoint);
        });*/

        /*player.massProperty().addListener((obs, oldMass, newMass) -> {
            playerCircle.setRadius(player.calculateRadius()); // update du radius du joueur
            setZoomFromMass(newMass.doubleValue() - oldMass.doubleValue()); // update du zoom de la camera
        });*/
    }

    private void addPlayerToMinimap(Player player) {
        double minimapScaleX = minimap.getWidth() / X_MAX;
        double minimapScaleY = minimap.getHeight() / Y_MAX;

        double minimapPlayerRadius = player.calculateRadius() * 0.1;

        Circle miniPlayer = new Circle(
                player.getPosition().getX() * minimapScaleX,
                player.getPosition().getY() * minimapScaleY,
                player.calculateRadius() * 0.1,  // Réduction de la taille du joueur
                Color.BLUE
        );

        minimapPlayerCircles.put(player, miniPlayer);
        minimap.getChildren().add(miniPlayer);

    }


    private void onPlayerPositionChanged(Player player, Point2D newPos) {


        //mets à jour le chunk du joueur

    }

    private void setZoomFromMass(double deltaMass) {
        //System.out.println("scale : " );
        // formule de calcul de la taille de la camera
        // peut être ajustee
        double newScale = 1 + 2. / (deltaMass);

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

        /*e.massProperty().addListener((obs, oldMass, newMass) -> {
            ennemyCircle.setRadius(e.calculateRadius()); // update du radius du joueur
        });*/
        addEnnemyToMinimap(e);
    }

    private void addEnnemyToMinimap(Ennemy ennemy) {
        double minimapScaleX = minimap.getWidth() / X_MAX;
        double minimapScaleY = minimap.getHeight() / Y_MAX;

        double minimapEnnemyRadius = ennemy.calculateRadius() * 0.1;

        Color color = (ennemy.getStrat() instanceof IAStratEatPlayers) ? Color.RED : Color.GREEN;

        Circle miniEnnemy = new Circle(
                ennemy.getPosition().getX() * minimapScaleX,
                ennemy.getPosition().getY() * minimapScaleY,
                ennemy.calculateRadius() * 0.1,  // Taille réduite
                color
        );

        minimapEnnemyCircles.put(ennemy, miniEnnemy);
        minimap.getChildren().add(miniEnnemy);

        // Suivi des mouvements de l'ennemi
        ennemy.positionProperty().addListener((obs, oldPos, newPos) -> {
            miniEnnemy.setCenterX(newPos.getX() * minimapScaleX);
            miniEnnemy.setCenterY(newPos.getY() * minimapScaleY);
        });
    }


    public void redrawPlayer(Player player) {
        if(player.equals(ourPlayer)){
            double x = (player.getPosition().getX() - cameraOffsetPoint.getX());
            double y = (player.getPosition().getY() - cameraOffsetPoint.getY());


            camera.setLayoutX(x);
            camera.setLayoutY(y);
        }

        Circle playerCircle = playerCircles.get(player);
        if (playerCircle != null) {
            playerCircle.setCenterX(player.getPosition().getX());
            playerCircle.setCenterY(player.getPosition().getY());
            playerCircle.setRadius(player.calculateRadius());
        }


        setZoomFromMass(player.getMass());//TODO div cellule

        Circle miniPlayerCircle = minimapPlayerCircles.get(player);
        if(miniPlayerCircle != null){
            double minimapScaleX = minimap.getWidth() / X_MAX;
            double minimapScaleY = minimap.getHeight() / Y_MAX;
            miniPlayerCircle.setCenterX(player.getPosition().getX() * minimapScaleX);
            miniPlayerCircle.setCenterY(player.getPosition().getY() * minimapScaleY);
            double minimapPlayerRadius = Math.min(Math.max(3,player.calculateRadius()*0.03),20);
            miniPlayerCircle.setRadius(minimapPlayerRadius);
        }
    }


    public void redrawEnemy(Ennemy e) {
        Circle ennemyCircle = ennemyCircles.get(e);
        if (ennemyCircle != null) {
            ennemyCircle.setCenterX(e.getPosition().getX());
            ennemyCircle.setCenterY(e.getPosition().getY());
            ennemyCircle.setRadius(e.calculateRadius());
        }

        Circle miniEnnemyCircle = minimapEnnemyCircles.get(e);
        if(miniEnnemyCircle!=null){
            double minimapScaleX = minimap.getWidth() / X_MAX;
            double minimapScaleY = minimap.getHeight() / Y_MAX;
            miniEnnemyCircle.setCenterX(e.getPosition().getX() * minimapScaleX);
            miniEnnemyCircle.setCenterY(e.getPosition().getY() * minimapScaleY);

            double minimapEnnemyRadius = Math.min(Math.max(3,e.calculateRadius()*0.03),20);
            miniEnnemyCircle.setRadius(minimapEnnemyRadius);
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
        Circle miniEntityCircle = null;
        if (entity instanceof Ennemy) {
            entityCircle = ennemyCircles.get(entity);
            miniEntityCircle = minimapEnnemyCircles.get(entity);
            ennemyCircles.remove(entity, entityCircle);
            minimapEnnemyCircles.remove(entity,miniEntityCircle);


        } else if (entity instanceof Player) {
            entityCircle = playerCircles.get(entity);
            playerCircles.remove(entity, entityCircle);
            miniEntityCircle = minimapPlayerCircles.get(entity);
            minimapPlayerCircles.remove(entity,miniEntityCircle);

        } else {
            //Pellet
            entityCircle = pelletCircles.get(entity);
            pelletCircles.remove(entity, entityCircle);
        }

        pane.getChildren().remove(entityCircle);
        minimap.getChildren().remove(miniEntityCircle);
    }

    public void updateLoadedChunks(MapNode currentChunk) {
        //System.out.println("update du chunk");
    }


}