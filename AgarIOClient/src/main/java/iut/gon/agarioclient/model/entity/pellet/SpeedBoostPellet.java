package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Player;
import iut.gon.agarioclient.model.entity.moveable.Point2DSerial;
import iut.gon.agarioclient.model.entity.pellet.EffectPellet;
import javafx.geometry.Point2D;

/**
 * Represents a speed boost pellet in the game.
 * Extends the EffectPellet class and provides a specific effect to boost the player's speed.
 */
public class SpeedBoostPellet extends EffectPellet {

    /**
     * Constructs a new SpeedBoostPellet with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the speed boost pellet
     * @param position the position of the speed boost pellet in the game space
     * @param mass     the mass of the speed boost pellet
     */
    public SpeedBoostPellet(String id, Point2DSerial position, double mass) {
        super(id, position, mass);
    }
}