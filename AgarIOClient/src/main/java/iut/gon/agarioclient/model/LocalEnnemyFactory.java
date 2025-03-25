package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.UUID;

public class LocalEnnemyFactory extends  EntityFactory{
    /**
     * Creates a new Pellet instance with a unique identifier, specified position, and mass.
     *
     * @param position the position of the pellet in the game space
     * @param mass     the mass of the pellet
     * @return a new Pellet instance
     */
    public static Ennemy createEnnemy(Point2D position, double mass) {
        return new Ennemy(UUID.randomUUID().toString(), position, mass);
    }
}
