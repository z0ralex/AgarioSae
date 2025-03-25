package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NoEffectPelletFactory implements PelletFactory{

    @Override
    public List<Pellet> generatePellets(int quantity){
        List<Pellet> list = new ArrayList<>();
        for(int i = 0; i < quantity; i++){
            Random r = new Random();
            Point2D p = new Point2D(r.nextInt(80), r.nextInt(80));//Changer avec les coordonées max du pane
            list.add(new Pellet(UUID.randomUUID().toString(),p,5));//A revoir
        }
        return list;
    }


}
