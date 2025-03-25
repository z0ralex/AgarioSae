// PlayerComposite.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class PlayerComposite implements PlayerComponent {
    protected List<PlayerComponent> components = new ArrayList<>(); // Change to protected

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
    public double calculateSpeed(double cursorX, double cursorY, double panelWidth, double panelHeight) {
        return components.stream().mapToDouble(c -> c.calculateSpeed(cursorX, cursorY, panelWidth, panelHeight)).average().orElse(0);
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

    @Override
    public String getId() {
        return null; // Composite does not have an ID
    }
}