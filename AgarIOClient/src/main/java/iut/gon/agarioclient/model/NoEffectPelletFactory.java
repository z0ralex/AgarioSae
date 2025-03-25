package iut.gon.agarioclient.model;

import iut.gon.agarioclient.controller.GameController;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NoEffectPelletFactory implements PelletFactory {
    @Override
    public List<Pellet> generatePellets(int quantity) {
        List<Pellet> list = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < quantity; i++) {
            Point2D p = new Point2D(r.nextDouble() * GameController.X_MAX, r.nextDouble() * GameController.Y_MAX);
            list.add(new Pellet(UUID.randomUUID().toString(), p, 1));
        }
        return list;
    }
}