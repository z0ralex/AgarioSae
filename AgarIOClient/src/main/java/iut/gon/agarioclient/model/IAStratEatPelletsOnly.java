// IAStratEatPelletsOnly.java
package iut.gon.agarioclient.model;

import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.Random;
import java.util.Set;

public class IAStratEatPelletsOnly implements IA {

    private MapNode root;

    public IAStratEatPelletsOnly(MapNode root) {
        this.root = root;
    }

    @Override
    public void execute(Ennemy ennemy) {

        Pellet nearestPellet = findNearestPellet(ennemy);
        if (nearestPellet != null) {
            moveToPellet(ennemy, nearestPellet);
            checkCollisionAndConsume(ennemy, nearestPellet);
        }
    }

    private Pellet findNearestPellet(Ennemy ennemy) {
        Set<Entity> entities = root.getEntitySet();

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

    private void moveToPellet(Ennemy ennemy, Pellet pellet) {
        Point2D direction = pellet.getPosition().subtract(ennemy.getPosition()).normalize();
        Point2D newPosition = ennemy.getPosition().add(direction.multiply(ennemy.getSpeed()));
        ennemy.setPosition(newPosition);
    }

    private void checkCollisionAndConsume(Ennemy ennemy, Entity target) {
        double distance = ennemy.getPosition().distance(target.getPosition());
        if (distance <= ennemy.calculateRadius()) {
            ennemy.setMass(ennemy.getMass() + target.getMass());
            target.removeFromCurrentNode();
        }
    }
}