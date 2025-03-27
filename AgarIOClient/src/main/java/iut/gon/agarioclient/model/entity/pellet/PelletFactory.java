package iut.gon.agarioclient.model.entity.pellet;

import java.util.List;

/**
 * The EntityFactory class is responsible for creating instances of game entities such as players and pellets.
 * It uses the Factory design pattern to encapsulate the creation logic.
 */
public interface PelletFactory {
    List<Pellet> generatePellets(int quantity);
}