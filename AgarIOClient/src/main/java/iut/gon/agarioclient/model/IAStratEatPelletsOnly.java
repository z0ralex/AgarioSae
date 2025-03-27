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
        //System.out.println(ennemy.getSpeed());
        Point2D newPosition = ennemy.getPosition().add(direction.multiply(10));
        //System.out.println("Current Position: " + ennemy.getPosition());
        //System.out.println("Direction: " + direction);
        //System.out.println("New Position: " + newPosition);
        ennemy.setPosition(newPosition);
        //System.out.println("Updated Position: " + ennemy.getPosition());
    }

    private void checkCollisionAndConsume(Ennemy ennemy, Entity target) {
        double distance = ennemy.getPosition().distance(target.getPosition());
        if (distance <= ennemy.calculateRadius()) {
            ennemy.setMass(ennemy.getMass() + target.getMass());
            target.removeFromCurrentNode();
        }
    }

    private void checkCollisionAndConsume(Ennemy ennemy, Player target) {
        // Don't consume self
        if (ennemy.getId().equals(target.getId())) return;

        double distance = ennemy.getPosition().distance(target.getPosition());
        if (distance <= ennemy.calculateRadius()) {
            // Only consume if the enemy is larger
            if (ennemy.getMass() > target.getMass() * 1.1) { // 10% size advantage
                ennemy.setMass(ennemy.getMass() + target.getMass());
                target.removeFromCurrentNode();

                // If target is an enemy, notify controller
                if (target instanceof Ennemy) {
                    ((Ennemy)target).markForRemoval();
                        System.out.println("Ennemy " + ennemy.getId() + " consumed " + target.getId());
                }
            }
        }
    }
}