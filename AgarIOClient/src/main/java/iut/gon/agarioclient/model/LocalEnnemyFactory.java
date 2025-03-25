package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.List;
import java.util.UUID;

public interface LocalEnnemyFactory {

    List<Ennemy> generate(int quantity);// Mieux si divisible par 3

}
