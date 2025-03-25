// Player.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;

import java.util.List;

public class Player extends PlayerComposite {

    public Player(String id, Point2D position, double mass, double speed) {
        PlayerLeaf initialCell = new PlayerLeaf(id, position, mass, speed);
        add(initialCell);
    }

    public List<PlayerComponent> divide() {
        // TODO: Implement divide logic
        return null;
    }

    public void merge() {
        // TODO: Implement merge logic
        // Implement merging logic based on the formula t = C + m/100
    }

    public void setInvisible(boolean b) {
        // TODO: Implement setInvisible logic
    }

    public ObjectProperty<Point2D> positionProperty() {
        return ((PlayerLeaf) components.get(0)).positionProperty();
    }

    public DoubleProperty massProperty() {
        return ((PlayerLeaf) components.get(0)).massProperty();
    }
}