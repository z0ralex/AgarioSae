package iut.gon.agarioclient.model.entity.moveable;

import java.util.List;

/**
 * Interface defining a factory for creating enemy entities locally.
 * Implementations of this interface are responsible for generating enemy instances.
 */
public interface LocalEnnemyFactory {

    /**
     * Generates a specified number of enemy entities.
     *
     * @param quantity The number of enemies to generate (preferably divisible by 3 for balanced distribution)
     * @return A list of generated Enemy instances
     */
    List<Ennemy> generate(int quantity);
}