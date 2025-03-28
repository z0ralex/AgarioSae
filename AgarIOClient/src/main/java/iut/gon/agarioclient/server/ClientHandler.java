package iut.gon.agarioclient.server;

import iut.gon.agarioclient.model.Game;
import iut.gon.agarioclient.server.Server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String clientId;
    private Game game;

    public ObjectOutputStream getOut() {
        return out;
    }

    public ClientHandler(Socket socket, Game game) {
        this.socket = socket;
        this.game = game;
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
            String s = "Bienvenue! Votre ID est : " + clientId;
            System.out.println(s);
            out.writeObject(s); // Envoi du message d'accueil
            out.flush();

            Server.addClientOutputStream(out, clientId);

            Object message;
            while ((message = in.readObject()) != null) {
                if (message instanceof String) {
                    String msg = (String) message;
                    // Vérifier si le client signale qu'il est prêt
                    if (msg.equalsIgnoreCase("pret")) {
                        System.out.println("Le client " + clientId + " est prêt.");
                        Server.getClientReadyStatus().put(clientId, true);
                    }
                    // Si c'est un message de chat
                    else if (msg.startsWith("CHAT: ")) {
                        System.out.println("Message du client " + clientId + ": " + msg);
                        Server.broadcast(msg);
                    }
                } else {
                    System.err.println("Type inconnu reçu : " + message.getClass());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur du client " + clientId + ": " + e.getMessage());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                Server.getClientHandlersSet().remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClientOutputStream(out, clientId);
        }
    }


    public String getClientId() {
        return clientId;
    }
}
