// Player.java
package iut.gon.agarioclient.model.entity.moveable;

import iut.gon.agarioclient.controller.AnimationManager;
import iut.gon.agarioclient.model.entity.pellet.PartialInvisibilityPellet;
import iut.gon.agarioclient.model.entity.pellet.SpeedBoostPellet;
import iut.gon.agarioclient.model.entity.pellet.SpeedReductionPellet;
import iut.gon.agarioclient.model.entity.pellet.Pellet;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.io.Serializable;
import java.util.*;

public class Player extends Entity implements PlayerComponent, Serializable {
    protected List<PlayerComponent> components = new ArrayList<>();
    private Point2D position;
    private double mass;

    private boolean alive = true;
    private boolean markedForRemoval = false;

    private boolean isVisible = true;

    private long affectedUntil = -1;
    private double specialEffect = 1;
    private long gotEffectedAt;

    private long gotInvisbileAt = -1;

    private long InvisbileUntil = -1;


    public Player(String id, Point2D position, double mass) {
        super(id, position, mass);
        this.position = position;
        this.mass = mass;
    }

    public void add(PlayerComponent component) {
        components.add(component);
    }

    public void remove(PlayerComponent component) {
        components.remove(component);
    }

    public List<PlayerComponent> getComponents() {
        return components;
    }

    private Point2D lastPosition = getPosition();


    @Override
    public double getMass() {
        return components.stream().mapToDouble(PlayerComponent::getMass).sum();
    }

    @Override
    public void setMass(double mass) {
        double totalMass = getMass();
        for (PlayerComponent component : components) {
            double proportion = component.getMass() / totalMass;
            component.setMass(mass * proportion);
        }
        this.mass=mass;
    }

    public Point2D getDirection() {
        Point2D currentPosition = getPosition();
        Point2D direction = currentPosition.subtract(lastPosition);

        if (direction.magnitude() == 0) {
            return Point2D.ZERO;
        }
        return direction.normalize();
    }


    @Override
    public Point2D getPosition() {
        double x = components.stream().mapToDouble(c -> c.getPosition().getX()).average().orElse(0);
        double y = components.stream().mapToDouble(c -> c.getPosition().getY()).average().orElse(0);
        return new Point2D(x, y);
    }

    @Override
    public void setPosition(Point2D position) {
        this.lastPosition = getPosition();
        this.position = position;
        for (PlayerComponent component : components) {
            component.setPosition(position);
        }
    }

    @Override
    public double getSpeed() {
        return components.stream().mapToDouble(PlayerComponent::getSpeed).average().orElse(0);
    }

    @Override
    public void setSpeed(double speed) {
        for (PlayerComponent component : components) {
            component.setSpeed(speed);
        }
    }

    @Override
    public double calculateRadius() {
        return components.stream().mapToDouble(PlayerComponent::calculateRadius).average().orElse(0);
    }

    public void setSpecialEffect(double specialEffect){
        this.specialEffect = specialEffect;
    }

    @Override
    public double calculateSpeed(double cursorX, double cursorY, double mapWidth, double mapHeight) {
        double mass = getMass();
        return ((mass / Math.pow(mass, 1.1)) * 10) * specialEffect;
    }

    @Override
    public boolean isAlive() {
        return alive && !components.isEmpty();
    }

    @Override
    public void setAlive(boolean alive) {
        this.alive = alive;
        if (!alive) {
            markForRemoval();
        }
    }

    public void setGotEffectedAt(long gotEffectedAt) {
        this.gotEffectedAt = gotEffectedAt;
    }

    public long getGotEffectedAt() {
        return gotEffectedAt;
    }



    public double massProperty() {
        return mass;
    }

    public Point2D calculateNewPosition(Point2D targetPosition, double mapWidth, double mapHeight) {
        double speed = calculateSpeed(targetPosition.getX(), targetPosition.getY(), mapWidth, mapHeight);
        setSpeed(speed);

        Point2D direction = targetPosition.subtract(getPosition()).normalize();
        Point2D newPosition = getPosition().add(direction.multiply(getSpeed()));

        // Check for collisions with the map boundaries
        double newX = Math.max(0, Math.min(newPosition.getX(), mapWidth));
        double newY = Math.max(0, Math.min(newPosition.getY(), mapHeight));
        return new Point2D(newX, newY);
    }

    public Set<Pellet> checkCollisionsWithPellet(Collection<Pellet> pellets) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius + 10;
        Set<Pellet> eatenPellets = new HashSet<>();

        pellets.forEach(pellet -> {
            //Circle pelletCircle = entry.getValue();
            double distance = getPosition().distance(pellet.getPosition());

            if (distance <= eventHorizon) {
                //animationManager.playPelletAbsorption(pelletCircle, getPosition());
                eatenPellets.add(pellet);

                if(pellet instanceof SpeedReductionPellet){
                    this.gotEffectedAt = System.currentTimeMillis();
                    this.affectedUntil = System.currentTimeMillis() + 4000;
                    this.setSpecialEffect(0.5);
                }

                if(pellet instanceof SpeedBoostPellet){
                    this.gotEffectedAt = System.currentTimeMillis();
                    this.affectedUntil = System.currentTimeMillis() + 4000;
                    this.setSpecialEffect(2);
                }

                if(pellet instanceof PartialInvisibilityPellet){
                    this.gotInvisbileAt = System.currentTimeMillis();
                    this.InvisbileUntil = System.currentTimeMillis() + 4000;
                    this.isVisible = false;
                }
                setMass(getMass() + pellet.getMass());
                //pane.getChildren().remove(pelletCircle); //TODO: Remove pane
                pellet.removeFromCurrentNode();

            } else {
                    //Reset Speed
                if (System.currentTimeMillis() > affectedUntil) {
                    this.setSpecialEffect(1.0);
                }
                //Reset Invisibility
                if (System.currentTimeMillis() > InvisbileUntil) {
                    this.isVisible = true;
                }
            }
        });

        return eatenPellets;
    }


    public boolean isVisible() {
        return isVisible;
    }

    //TODO merge minimap
    public Set<Ennemy> checkCollisionsWithEnemies(Collection<Ennemy> enemies) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius * 0.33;
        Set<Ennemy> eaten = new HashSet<>();

        enemies.forEach(enemy -> {
            double distance = getPosition().distance(enemy.getPosition());
            double overlap = Math.max(0, playerRadius + enemy.calculateRadius() - distance);
//        List<Ennemy> toRemove = new ArrayList<>();
//
//        for (Map.Entry<Ennemy, Circle> entry : ennemyCircles.entrySet()) {
//            Ennemy ennemy = entry.getKey();
//            Circle ennemyCircle = entry.getValue();
//            Circle miniEnnemyCircle = minimapEnnemyCircles.get(ennemy);
//            double distance = getPosition().distance(ennemy.getPosition());
//            double overlap = Math.max(0, playerRadius + ennemy.calculateRadius() - distance);

            if (getMass() >= enemy.getMass() * 1.33 && overlap >= playerRadius * 0.33) {
                eaten.add(enemy);
                //animationManager.playPelletAbsorption(enemyCircle, getPosition());
                setMass(getMass() + enemy.getMass());
                //pane.getChildren().remove(ennemyCircle); //TODO: Remove pane
//                minimap.getChildren().remove(miniEnnemyCircle);
                enemy.markForRemoval();
                //toRemove.add(enemy);

            }
        });
        return eaten;


//        for (Ennemy e : toRemove) {
//            ennemyCircles.remove(e);
//            minimapEnnemyCircles.remove(e);
//        }
    }

    public Set<Player> checkCollisionsWithPlayers(Collection<Player> playerCircles) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius * 0.33;
        Set<Player> eaten = new HashSet<>();

        playerCircles.forEach(otherPlayer -> {

            double distance = getPosition().distance(otherPlayer.getPosition());
            double overlap = Math.max(0, playerRadius + otherPlayer.calculateRadius() - distance);


            if (this != otherPlayer && getMass() >= otherPlayer.getMass() * 1.33 && overlap >= playerRadius * 0.33) {

                //animationManager.playPelletAbsorption(otherPlayerCircle, getPosition());
                eaten.add(otherPlayer);
                setMass(getMass() + otherPlayer.getMass());
                //pane.getChildren().remove(otherPlayerCircle); //TODO: Remove pane
                otherPlayer.markForRemoval();
            } /*else if  (this == otherPlayer && overlap >= playerRadius * 0.33){
                System.out.println("ah bah qui est le plus con dans l'histoire ?");
                //animationManager.playPelletAbsorption(otherPlayerCircle, getPosition());

                setMass(getMass() + otherPlayer.getMass());
                //pane.getChildren().remove(otherPlayerCircle); //TODO: Remove pane
                otherPlayer.markForRemoval();
            }*/
        });
        return eaten;
    }


    public void markForRemoval() {
        if(!markedForRemoval){
            this.markedForRemoval = true;
            removeFromCurrentNode();
            components.clear();
        }
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public List<PlayerComponent> divide() {
        // TODO: Implement divide logic
        return null;
    }

    public void merge() {
        // TODO: Implement merge logic
        // Implement merging logic based on the formula t = C + m/100
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Player player = (Player) o;
        return Double.compare(player.mass, mass) == 0 && alive == player.alive && markedForRemoval == player.markedForRemoval && isVisible == player.isVisible && affectedUntil == player.affectedUntil && Double.compare(player.specialEffect, specialEffect) == 0 && gotEffectedAt == player.gotEffectedAt && gotInvisbileAt == player.gotInvisbileAt && InvisbileUntil == player.InvisbileUntil && Objects.equals(components, player.components) && position.equals(player.position) && lastPosition.equals(player.lastPosition);
    }

}