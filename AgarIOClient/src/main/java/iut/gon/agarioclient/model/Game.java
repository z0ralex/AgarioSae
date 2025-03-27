package iut.gon.agarioclient.model;

import iut.gon.agarioclient.model.entity.moveable.*;
import iut.gon.agarioclient.model.entity.pellet.NoEffectPelletFactory;
import iut.gon.agarioclient.model.entity.pellet.Pellet;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {
    //Constantes
    public static final int INITIAL_PLAYER_MASS = 10;
    public static final int INITIAL_PELLET_NB = 20;
    public static final int MAX_PELLET = 1500;
    public static final int INITIAL_PLAYER_SPEED = 5;
    public static final double PLAYER_SPAWNPOINT_X = 400;
    public static final double PLAYER_SPAWNPOINT_Y = 300;
    public static final int X_MAX = 8000;
    public static final int Y_MAX = 6000;

    private MapNode root;
    private final NoEffectPelletFactory pelletFactory = new NoEffectPelletFactory();

    //TODO changer ça
    private List<Ennemy> enemyList;
    private Set<Pellet> pellets;
    private Set<Player> players;


    public Game(){
        root = new MapNode(4, new Point2D(0, 0), new Point2D(X_MAX, Y_MAX));
        if (root == null) {
            throw new IllegalStateException("Root MapNode is not initialized.");
        }

        NoEffectLocalEnnemyFactory f = new NoEffectLocalEnnemyFactory(root);
        enemyList = f.generate(10);
        pellets = new HashSet<>();

    }

    public MapNode getRoot() {
        return root;
    }

    /**
     * mets à jour le jeu
     */
    public void nextTick(){
        for (Ennemy ennemy : enemyList) {
            ennemy.executeStrat();
            double speedE = ennemy.calculateSpeed(ennemy.getPosition().getX(), ennemy.getPosition().getY(), X_MAX, Y_MAX);
            ennemy.setSpeed(speedE);
        }

        spawnPellets();

        for (Ennemy ennemy : enemyList) { //TODO refactor si on fait l'opti
            Set<Entity> eaten = new HashSet<>();

            //redrawEnemy(ennemy);
            eaten.addAll(ennemy.checkCollisions(pellets));
            eaten.addAll(ennemy.checkCollisionsWithEnemies(enemyList));
            eaten.addAll(ennemy.checkCollisionsWithPlayers(players));

        }
    }

    /**
     *
     * @param entity Player ou Ennemy
     * @param vector
     */
    public void moveEntity(Player entity, Point2D vector){
        Point2D newPosition = entity.getPosition().add(vector.multiply(entity.getSpeed()));

        // Check for collisions with the map boundaries
        double newX = Math.max(0, Math.min(newPosition.getX(), X_MAX));
        double newY = Math.max(0, Math.min(newPosition.getY(), Y_MAX));
        newPosition = new Point2D(newX, newY);

        entity.setPosition(newPosition);
    }

    public void addEnemy(Ennemy e){
        root.addEntity(e);
    }

    public void spawnPellets() {
        if (pellets.size() < MAX_PELLET) {
            createPellets(100);
        }
    }

    public void createPellets(int count) {
        //TODO separer vue/modele
        List<Pellet> pellets = pelletFactory.generatePellets(count);
        for (Pellet pellet : pellets) {
            getRoot().addEntity(pellet);
            root.addEntity(pellet);
        }
    }
    public Player addPlayer(String nickname){
        Player player = new Player(nickname, new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS);
        player.add(new PlayerLeaf(nickname, new Point2D(PLAYER_SPAWNPOINT_Y, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS, INITIAL_PLAYER_SPEED));

        return player;
    }


    /**
     * vérifie si l'entité s'est déplacée hors de son chunck, si oui on la déplace sur la map aussi
     * @param entity
     */
    public void checkEntityChunck(Entity entity){
        if (entity.getCurrentMapNode() != null &&
                !entity.getCurrentMapNode().positionInNode(entity.getPosition().getX(), entity.getPosition().getY())) {

            entity.removeFromCurrentNode();
            root.addEntity(entity);
        }
    }

    public HashMap<Entity, Set<Entity>> getEatenEntities(){

    }
}
