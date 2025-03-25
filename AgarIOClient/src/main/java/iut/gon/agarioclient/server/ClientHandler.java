package iut.gon.agarioclient.server;

import iut.gon.agarioclient.server.Server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Erreur de connexion avec le client : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Générer un ID unique pour le client
            clientId = Server.generateClientId();
            System.out.println("ID du client généré : " + clientId);

            // Envoyer l'ID du client au client
            out.println("Votre ID est : " + clientId + ". Vous pouvez commencer à discuter.");

            // Ajouter le PrintWriter à la liste des clients
            Server.addClientWriter(out, clientId);

            // Lire les messages envoyés par le client (seulement des messages de chat)
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message reçu de " + clientId + ": " + message);

                // Filtrer et ne diffuser que les messages de chat
                if (message.startsWith("CHAT: ")) {
                    // Diffuser uniquement les messages de chat
                    Server.broadcast(clientId + ": " + message.substring(6)); // Enlever le préfixe "CHAT: "
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur du client " + clientId + ": " + e.getMessage());
        } finally {
            // Nettoyer à la déconnexion du client
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClientWriter(out, clientId);
        }
    }
}


