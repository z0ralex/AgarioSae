// Player.java
package iut.gon.agarioclient.model.entity.moveable;

import iut.gon.agarioclient.controller.AnimationManager;
import iut.gon.agarioclient.model.entity.pellet.PartialInvisibilityPellet;
import iut.gon.agarioclient.model.entity.pellet.SpeedBoostPellet;
import iut.gon.agarioclient.model.entity.pellet.SpeedReductionPellet;
import iut.gon.agarioclient.model.entity.pellet.Pellet;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player extends Entity implements PlayerComponent {
    protected List<PlayerComponent> components = new ArrayList<>();
    private ObjectProperty<Point2D> position;
    private DoubleProperty mass;

    private boolean alive = true;
    private boolean markedForRemoval = false;

    private boolean isVisible = true;

    private long affectedUntil = -1;
    private double specialEffect = 1;
    private long gotEffectedAt;

    private long gotInvisbileAt = -1;

    private long InvisbileUntil = -1;

    private static final double MERGE_COOLDOWN_BASE = 10.0; // 10 secondes
    private List<Long> divisionTimes = new ArrayList<>();



    public Player(String id, Point2D position, double mass) {
        super(id, position, mass);
        this.position = new SimpleObjectProperty<>(position);
        this.mass = new SimpleDoubleProperty(mass);
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
        this.mass.set(mass);
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
        this.position.set(position);
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

    public SimpleObjectProperty<Point2D> positionProperty() {
        return (SimpleObjectProperty<Point2D>) position;
    }

    public DoubleProperty massProperty() {
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

    public void checkCollisionsWithPellet(Map<Pellet, Circle> pelletCircles, Pane pane, AnimationManager animationManager) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius + 10;

        pelletCircles.entrySet().removeIf(entry -> {
            Pellet pellet = entry.getKey();
            Circle pelletCircle = entry.getValue();
            double distance = getPosition().distance(pellet.getPosition());

            if (distance <= eventHorizon) {
                animationManager.playPelletAbsorption(pelletCircle, getPosition());

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
                pane.getChildren().remove(pelletCircle);
                pellet.removeFromCurrentNode();
                return true;
            }
            //Reset Speed
            if(System.currentTimeMillis() > affectedUntil){
                this.setSpecialEffect(1.0);
            }
            //Reset Invisibility
            if(System.currentTimeMillis() > InvisbileUntil){
                this.isVisible = true;
            }
            return false;
        });
    }


    public boolean isVisible() {
        return isVisible;
    }

    public void checkCollisionsWithEnemies(Map<Ennemy, Circle> ennemyCircles, Pane pane, AnimationManager animationManager) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius * 0.33;

        ennemyCircles.entrySet().removeIf(entry -> {
            Ennemy ennemy = entry.getKey();
            Circle ennemyCircle = entry.getValue();
            double distance = getPosition().distance(ennemy.getPosition());
            double overlap = Math.max(0, playerRadius + ennemy.calculateRadius() - distance);

            if (getMass() >= ennemy.getMass() * 1.33 && overlap >= playerRadius * 0.33) {
                animationManager.playPelletAbsorption(ennemyCircle, getPosition());
                setMass(getMass() + ennemy.getMass());
                pane.getChildren().remove(ennemyCircle);
                ennemy.markForRemoval();
                return true;
            }
            return false;
        });
    }

    public void checkCollisionsWithPlayers(Map<Player, Circle> playerCircles, Pane pane, AnimationManager animationManager) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius * 0.33;

        playerCircles.entrySet().removeIf(entry -> {
            Player otherPlayer = entry.getKey();
            Circle otherPlayerCircle = entry.getValue();
            double distance = getPosition().distance(otherPlayer.getPosition());
            double overlap = Math.max(0, playerRadius + otherPlayer.calculateRadius() - distance);

            if (this != otherPlayer && getMass() >= otherPlayer.getMass() * 1.33 && overlap >= playerRadius * 0.33) {
                animationManager.playPelletAbsorption(otherPlayerCircle, getPosition());

                setMass(getMass() + otherPlayer.getMass());
                pane.getChildren().remove(otherPlayerCircle);
                otherPlayer.markForRemoval();
                return true;
            } else if (this == otherPlayer && overlap >= playerRadius * 0.33) {
                animationManager.playPelletAbsorption(otherPlayerCircle, getPosition());

                setMass(getMass() + otherPlayer.getMass());
                pane.getChildren().remove(otherPlayerCircle);
                otherPlayer.markForRemoval();
                return true;
            }
            return false;
        });
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
        removeFromCurrentNode();
        components.clear();
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    // In Player.java
    public void divide() {
        if(components.isEmpty()) return;

        List<PlayerComponent> newComponents = new ArrayList<>();
        for(PlayerComponent component : components) {
            double halfMass = component.getMass() / 2;

            // Create two new cells with offset positions
            Point2D originalPos = component.getPosition();
            PlayerLeaf cell1 = new PlayerLeaf(
                    getId() + "-" + System.nanoTime(), // Unique ID
                    originalPos.add(new Point2D(20, 0)), // Offset position
                    halfMass,
                    component.getSpeed() * 3
            );

            PlayerLeaf cell2 = new PlayerLeaf(
                    getId() + "-" + System.nanoTime(),
                    originalPos.add(new Point2D(-20, 0)), // Offset position
                    halfMass,
                    component.getSpeed()
            );

            newComponents.add(cell1);
            newComponents.add(cell2);
        }

        components.clear();
        components.addAll(newComponents);
        // Reset division times for all new components
        divisionTimes.clear();
        components.forEach(c -> divisionTimes.add(System.currentTimeMillis()));
    }

    public void merge() {
        if(canMerge()) {
            double totalMass = components.stream().mapToDouble(PlayerComponent::getMass).sum();
            PlayerLeaf mergedCell = new PlayerLeaf(
                    getId(),
                    getPosition(),
                    totalMass,
                    components.get(0).getSpeed() // Use existing component's speed
            );

            components.clear();
            components.add(mergedCell);
            divisionTimes.clear();
            divisionTimes.add(System.currentTimeMillis());
        }
    }

    private boolean canMerge() {
        if(components.size() <= 1) return false;
        if(divisionTimes.size() != components.size()) return false;

        long currentTime = System.currentTimeMillis();
        for(int i = 0; i < components.size(); i++) {
            double mass = components.get(i).getMass();
            double requiredTime = (MERGE_COOLDOWN_BASE + mass/100) * 1000;
            if(currentTime - divisionTimes.get(i) < requiredTime) return false;
        }
        return true;
    }

    // Ajouter dans handle() ou une méthode similaire
    public void update() {
        if(canMerge()) merge();

        // Ralentissement progressif des cellules boostées
        components.forEach(component -> {
            if(component.getSpeed() > component.calculateSpeed(0, 0, 0, 0)) {
                component.setSpeed(component.getSpeed() * 0.99);
            }
        });
    }

}