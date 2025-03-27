package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class IAStratRandomMoving implements IA{

    private long tookDecisionAt = System.currentTimeMillis();
    private int dirSelector = 4;

    public void execute(Ennemy e) {
        Random r = new Random();

        if (this.tookDecisionAt + 1000 < System.currentTimeMillis()) {
            this.tookDecisionAt = System.currentTimeMillis();
            dirSelector = r.nextInt(1,9);
        }

            switch (dirSelector) {
                case 1://N
                    if (tookDecisionAt + 1000 > System.currentTimeMillis()) {
                        e.setPosition(new Point2D(e.getPosition().getX(), e.getPosition().getY() + 2));
                    }
                    break;
                case 2://S
                    if (tookDecisionAt + 1000 > System.currentTimeMillis()) {
                        e.setPosition(new Point2D(e.getPosition().getX(), e.getPosition().getY() - 2));
                    }
                    break;
                case 3://W
                    if (tookDecisionAt + 1000 > System.currentTimeMillis()) {
                        e.setPosition(new Point2D(e.getPosition().getX() - 2, e.getPosition().getY()));
                    }
                    break;
                case 4://E
                    if (tookDecisionAt + 1000 > System.currentTimeMillis()) {
                        e.setPosition(new Point2D(e.getPosition().getX() + 2, e.getPosition().getY() ));
                    }
                    break;

                case 5://NW
                    if (tookDecisionAt + 1000 > System.currentTimeMillis()) {
                        e.setPosition(new Point2D(e.getPosition().getX() - 2, e.getPosition().getY() + 2));
                    }
                    break;

                case 6://NE
                    if (tookDecisionAt + 1000 > System.currentTimeMillis()) {
                        e.setPosition(new Point2D(e.getPosition().getX() + 2, e.getPosition().getY() + 2 ));
                    }
                    break;

                case 7://SE
                    if (tookDecisionAt + 1000 > System.currentTimeMillis()) {
                        e.setPosition(new Point2D(e.getPosition().getX() + 2, e.getPosition().getY() - 2 ));
                    }
                    break;

                case 8://SW
                    if (tookDecisionAt + 1000 > System.currentTimeMillis()) {
                        e.setPosition(new Point2D(e.getPosition().getX() - 2, e.getPosition().getY() - 2));
                    }
                    break;
            }
        e.calculateRadius();
    }

}
