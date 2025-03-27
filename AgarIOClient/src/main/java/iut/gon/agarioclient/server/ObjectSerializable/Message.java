package iut.gon.agarioclient.server.ObjectSerializable;

public abstract class Message {
    private String type; // Type de message pour identification

    public Message(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public abstract void process(); // Méthode à implémenter dans chaque sous-classe
}
