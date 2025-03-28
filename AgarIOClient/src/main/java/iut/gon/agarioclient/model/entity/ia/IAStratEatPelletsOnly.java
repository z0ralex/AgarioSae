package iut.gon.agarioclient.model.entity.ia;

import iut.gon.agarioclient.model.entity.moveable.Ennemy;
import iut.gon.agarioclient.model.entity.moveable.Entity;
import iut.gon.agarioclient.model.entity.pellet.Pellet;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.Set;

public class IAStratEatPelletsOnly implements IA {

    private MapNode root; // The root node of the map containing all entities.

    /**
     * Constructor for the AI strategy that makes enemies target pellets.
     *
     * @param root The root MapNode of the game map.
     */
    public IAStratEatPelletsOnly(MapNode root) {
        this.root = root;
    }

    /**
     * Executes the strategy: Moves the enemy towards the nearest pellet.
     *
     * @param ennemy The enemy entity to control.
     */
    @Override
    public void execute(Ennemy ennemy) {
        if (root == null) {
            System.err.println("Root MapNode is null.");
            return;
        }

        Pellet nearestPellet = findNearestPellet(ennemy);
        if (nearestPellet != null) {
            moveToPellet(ennemy, nearestPellet);
        }
    }

    /**
     * Finds the nearest pellet to the enemy.
     *
     * @param ennemy The enemy entity searching for pellets.
     * @return The nearest Pellet object, or null if no pellets are found.
     */
    private Pellet findNearestPellet(Ennemy ennemy) {
        Set<Entity> entities = root.getEntitySet();
        if (entities == null) {
            System.err.println("Entity set is null.");
            return null;
        }

        Pellet nearestPellet = null;
        double minDistance = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (entity instanceof Pellet) {
                double distance = ennemy.getPosition().distance(entity.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPellet = (Pellet) entity;
                }
            }
        }
        return nearestPellet;
    }

    /**
     * Moves the enemy towards the specified pellet.
     *
     * @param ennemy The enemy entity to move.
     * @param pellet The target pellet to move towards.
     */
    private void moveToPellet(Ennemy ennemy, Pellet pellet) {
        Point2D direction = pellet.getPosition().subtract(ennemy.getPosition()).normalize();
        Point2D newPosition = ennemy.getPosition().add(direction.multiply(ennemy.getSpeed()));
        ennemy.setPosition(newPosition);
    }
}