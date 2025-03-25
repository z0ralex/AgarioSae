package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * The EntityFactory class is responsible for creating instances of game entities such as players and pellets.
 * It uses the Factory design pattern to encapsulate the creation logic.
 */
public interface PelletFactory {
    List<Pellet> generatePellets(int quantity);
}