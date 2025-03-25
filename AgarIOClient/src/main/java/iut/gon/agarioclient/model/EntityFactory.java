package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.UUID;

/**
 * The EntityFactory class is responsible for creating instances of game entities such as players and pellets.
 * It uses the Factory design pattern to encapsulate the creation logic.
 */
public class EntityFactory {

    /**
     * Creates a new Player instance with a unique identifier, specified position, mass, and speed.
     *
     * @param position the position of the player in the game space
     * @param mass     the mass of the player
     * @param speed    the speed of the player
     * @return a new Player instance
     */
    public static Player createPlayer(Point2D position, double mass, double speed) {
        return new Player(UUID.randomUUID().toString(), position, mass, speed);
    }

    /**
     * Creates a new Pellet instance with a unique identifier, specified position, and mass.
     *
     * @param position the position of the pellet in the game space
     * @param mass     the mass of the pellet
     * @return a new Pellet instance
     */
    public static Pellet createPellet(Point2D position, double mass) {
        return new Pellet(UUID.randomUUID().toString(), position, mass);
    }
}