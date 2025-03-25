package iut.gon.agarioclient.server;

public  class GameUpdater implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {


                // Mettre à jour l'état du jeu ici (positions, scores, etc.)
                String gameState = "État du jeu mis à jour : ..."; // Exemple d'état
                Server.broadcast(gameState);

                // Attendre 33ms pour maintenir une mise à jour à 30 FPS
                Thread.sleep(33);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}