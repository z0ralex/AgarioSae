package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Player;
import javafx.geometry.Point2D;

/**
 * Represents a partial invisibility pellet in the game.
 * Extends the EffectPellet class and provides a specific effect to make the player partially invisible.
 */
public class PartialInvisibilityPellet extends EffectPellet {
    /**
     * Constructs a new PartialInvisibilityPellet with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the partial invisibility pellet
     * @param position the position of the partial invisibility pellet in the game space
     * @param mass     the mass of the partial invisibility pellet
     */
    public PartialInvisibilityPellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }
}