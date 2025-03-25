package iut.gon.agarioclient.model;

import java.util.List;

public interface LocalEnnemyFactory {

    List<Ennemy> generate(int quantity);// Mieux si divisible par 3

}
