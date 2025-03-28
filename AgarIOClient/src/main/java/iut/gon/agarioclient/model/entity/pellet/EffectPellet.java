// EffectPellet.java
package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Player;
import iut.gon.agarioclient.model.entity.moveable.Point2DSerial;
import javafx.geometry.Point2D;

/**
 * Abstract class representing an effect pellet in the game.
 * Extends the Pellet class and provides a base for specific effect pellets.
 */
public abstract class EffectPellet extends Pellet {
    /**
     * Constructs a new EffectPellet with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the effect pellet
     * @param position the position of the effect pellet in the game space
     * @param mass     the mass of the effect pellet
     */
    public EffectPellet(String id, Point2DSerial position, double mass) {
        super(id, position, mass);
    }



}