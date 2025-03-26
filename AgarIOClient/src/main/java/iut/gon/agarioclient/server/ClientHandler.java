package iut.gon.agarioclient.server;

import iut.gon.agarioclient.server.Server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String clientId;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream()); // Remplace PrintWriter par ObjectOutputStream
            this.in = new ObjectInputStream(socket.getInputStream()); // Utilise ObjectInputStream pour les objets
        } catch (IOException e) {
            System.err.println("Erreur de connexion avec le client : " + e.getMessage());
        }
    }

    public void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
    }

    @Override
    public void run() {
        try {
            clientId = Server.generateClientId();
            System.out.println("ID du client généré : " + clientId);
            out.writeObject("Bienvenue! Votre ID est : " + clientId); // Message d'accueil avec un objet

            Server.addClientOutputStream(out, clientId);

            Object message;
            while ((message = in.readObject()) != null) {  // Lecture d'objets
                if (message instanceof String && ((String) message).startsWith("CHAT: ")) {
                    Server.broadcast(message); // Diffusion du message texte en tant qu'objet
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur du client " + clientId + ": " + e.getMessage());
        } finally {
            // Nettoyage après déconnexion
            try {
                if (socket != null) {
                    socket.close();
                }
                Server.getClientHandlersSet().remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClientOutputStream(out, clientId); // Retirer proprement le client
        }
    }

    public String getClientId() {
        return clientId;
    }
}
