package iut.gon.agarioclient.model;

import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.Set;

public class IAStratEatPlayers implements IA{

    private MapNode root;

    public IAStratEatPlayers(MapNode root){ this.root = root;}

    @Override
    public void execute(Ennemy e) {
        Player nearestPlayer = findNearestPlayer(e);
        if (nearestPlayer != null) {
            moveToPlayer(e, nearestPlayer);
            checkCollisionAndConsume(e, nearestPlayer);
        }
    }

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

    private void moveToPlayer(Ennemy ennemy, Player target) {
        Point2D direction = target.getPosition().subtract(ennemy.getPosition()).normalize();
        Point2D newPosition = ennemy.getPosition().add(direction.multiply(10));
        ennemy.setPosition(newPosition);
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
                    if(target instanceof Ennemy) {
                        //System.out.println("Ennemy " + ennemy.getId() + " consumed " + target.getId());
                    }
                }
            }
        }
    }

}
