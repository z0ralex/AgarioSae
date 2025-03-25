// Player.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
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
}