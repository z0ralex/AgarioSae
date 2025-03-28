package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Player;
import javafx.geometry.Point2D;

/**
 * Represents a speed reduction pellet in the game.
 * Extends the EffectPellet class and provides a specific effect to reduce the player's speed.
 */
public class SpeedReductionPellet extends EffectPellet {
    /**
     * Constructs a new SpeedReductionPellet with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the speed reduction pellet
     * @param position the position of the speed reduction pellet in the game space
     * @param mass     the mass of the speed reduction pellet
     */
    public SpeedReductionPellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }
}