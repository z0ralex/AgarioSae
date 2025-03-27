package iut.gon.agarioclient.model;

import iut.gon.agarioclient.model.entity.moveable.*;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.List;

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


    public Game(){
        root = new MapNode(4, new Point2D(0, 0), new Point2D(X_MAX, Y_MAX));
        if (root == null) {
            throw new IllegalStateException("Root MapNode is not initialized.");
        }

        NoEffectLocalEnnemyFactory f = new NoEffectLocalEnnemyFactory(root);
        List<Ennemy> list = f.generate(10);


    }

    public MapNode getRoot() {
        return root;
    }

    /**
     * mets à jour le jeu
     */
    public void nextTick(){

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
}
