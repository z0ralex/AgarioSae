package iut.gon.agarioclient.model.entity.ia;

import iut.gon.agarioclient.model.entity.moveable.Ennemy;

public interface IA {
    /**
     * Executes the AI strategy for the given enemy.
     * This method defines the behavior that the enemy will follow.
     *
     * @param e The enemy entity to which the AI strategy is applied.
     */
    void execute(Ennemy e);
}