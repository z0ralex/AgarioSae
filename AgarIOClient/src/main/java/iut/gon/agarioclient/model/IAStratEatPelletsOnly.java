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
        System.out.println(ennemy.getSpeed());
        Point2D newPosition = ennemy.getPosition().add(direction.multiply(10));
        ennemy.setPosition(newPosition);
    }

    private void checkCollisionAndConsume(Ennemy ennemy, Pellet pellet) {
        double distance = ennemy.getPosition().distance(pellet.getPosition());
        if (distance <= ennemy.calculateRadius()) {
            ennemy.setMass(ennemy.getMass() + pellet.getMass());
            pellet.removeFromCurrentNode();
        }
    }
}