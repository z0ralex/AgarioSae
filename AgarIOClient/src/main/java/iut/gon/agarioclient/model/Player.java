// Player.java
package iut.gon.agarioclient.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

// He is the PlayerComposite class from the Composite design pattern
public class Player extends Entity implements PlayerComponent {
    protected List<PlayerComponent> components = new ArrayList<>();
    private ObjectProperty<Point2D> position;
    private DoubleProperty mass;

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

    @Override
    public Point2D getPosition() {
        double x = components.stream().mapToDouble(c -> c.getPosition().getX()).average().orElse(0);
        double y = components.stream().mapToDouble(c -> c.getPosition().getY()).average().orElse(0);
        return new Point2D(x, y);
    }

    @Override
    public void setPosition(Point2D position) {
        for (PlayerComponent component : components) {
            component.setPosition(position);
        }
        this.position.set(position);
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

    @Override
    public double calculateSpeed(double cursorX, double cursorY, double mapWidth, double mapHeight) {
        return components.stream()
                .mapToDouble(c -> c.calculateSpeed(cursorX, cursorY, mapWidth, mapHeight))
                .average()
                .orElse(0);
    }

    @Override
    public boolean isAlive() {
        return components.stream().allMatch(PlayerComponent::isAlive);
    }

    @Override
    public void setAlive(boolean alive) {
        for (PlayerComponent component : components) {
            component.setAlive(alive);
        }
    }

    public SimpleObjectProperty<Point2D> positionProperty() {
        return (SimpleObjectProperty<Point2D>) position;
    }

    public DoubleProperty massProperty() {
        return mass;
    }

    public void setInvisible(boolean b) {
        // TODO: Implement setInvisible logic
    }

    public List<PlayerComponent> divide() {
        // TODO: Implement divide logic
        return null;
    }

    public void merge() {
        // TODO: Implement merge logic
        // Implement merging logic based on the formula t = C + m/100
    }
}