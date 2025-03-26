package iut.gon.agarioclient.server.ObjectSerializable;

public class InfoMessage extends Message {
    private String info;

    public InfoMessage(String info) {
        super("Info");
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public void process() {
        // Traiter l'info de base
        System.out.println("Traitement du message d'info: " + info);
    }
}