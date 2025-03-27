package iut.gon.agarioclient.server.ObjectSerializable;

public class ChatMessage extends Message {
    private String username;
    private String message;

    public ChatMessage(String username, String message) {
        super("Chat");
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void process() {
        // Traiter le message de chat
        System.out.println(username + ": " + message);
    }
}
