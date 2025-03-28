package iut.gon.agarioclient.model.map;

import java.io.Serializable;

/**
 * Enum representing the four diagonal directions in the game.
 * Used to specify movement or orientation in the game map.
 */
public enum Direction implements Serializable {
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_EAST,
    SOUTH_WEST
}