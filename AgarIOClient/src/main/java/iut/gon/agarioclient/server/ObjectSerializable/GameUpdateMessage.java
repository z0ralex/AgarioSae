package iut.gon.agarioclient.server.ObjectSerializable;

public class GameUpdateMessage extends Message {
    private String gameState;

    public GameUpdateMessage(String gameState) {
        super("GameUpdate");
        this.gameState = gameState;
    }

    public String getGameState() {
        return gameState;
    }

    @Override
    public void process() {
        // Traiter la mise à jour du jeu
        System.out.println("Mise à jour du jeu: " + gameState);
    }
}
