package iut.gon.agarioclient.model.entity.ia;

import iut.gon.agarioclient.model.entity.moveable.Ennemy;
import iut.gon.agarioclient.model.entity.moveable.Entity;
import iut.gon.agarioclient.model.entity.moveable.Player;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.Set;

public class IAStratEatPlayers implements IA {

    private MapNode root; // The root node of the map containing all entities.

    /**
     * Constructor for the AI strategy that makes enemies target players.
     *
     * @param root The root MapNode of the game map.
     */
    public IAStratEatPlayers(MapNode root) {
        this.root = root;
    }

    /**
     * Executes the strategy: Moves the enemy towards the nearest player.
     *
     * @param e The enemy entity to control.
     */
    @Override
    public void execute(Ennemy e) {
        Player nearestPlayer = findNearestPlayer(e);
        if (nearestPlayer != null) {
            moveToPlayer(e, nearestPlayer);
        }
    }

    /**
     * Finds the nearest player to the enemy (excluding itself).
     *
     * @param ennemy The enemy entity searching for players.
     * @return The nearest Player object, or null if no players are found.
     */
    private Player findNearestPlayer(Ennemy ennemy) {
        Set<Entity> entities = root.getEntitySet();
        Player nearestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (entity instanceof Player && !entity.getId().equals(ennemy.getId())) {
                double distance = ennemy.getPosition().distance(entity.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPlayer = (Player) entity;
                }
            }
        }
        return nearestPlayer;
    }

    /**
     * Moves the enemy towards the specified player.
     *
     * @param ennemy The enemy entity to move.
     * @param target The target player to move towards.
     */
    private void moveToPlayer(Ennemy ennemy, Player target) {
        Point2D direction = target.getPosition().subtract(ennemy.getPosition()).normalize();
        Point2D newPosition = ennemy.getPosition().add(direction.multiply(ennemy.getSpeed()));
        ennemy.setPosition(newPosition);
    }
}