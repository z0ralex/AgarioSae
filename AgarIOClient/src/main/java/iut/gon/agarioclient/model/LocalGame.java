package iut.gon.agarioclient.model;

public class LocalGame extends Game{

    public LocalGame(){
        super();

        Thread tick = new Thread(()->{
            this.nextTick();
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        tick.run();
    }
}
