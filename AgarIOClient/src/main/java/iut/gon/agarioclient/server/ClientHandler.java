package iut.gon.agarioclient.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
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
            // Envoyer un message d'accueil au client
            out.println("Bienvenue sur le serveur! En attente d'un ID de joueur...");

            // Réception de l'ID du client
            clientId = in.readLine();
            if (clientId == null || clientId.trim().isEmpty()) {
                out.println("Erreur : ID de joueur invalide.");
                return;
            }

            System.out.println("ID du client: " + clientId);

            // Ajouter le PrintWriter à la liste des clients
            Server.addClientWriter(out, clientId);

            // Lire les messages envoyés par le client
            String message;
            while ((message = in.readLine()) != null) {
                // On filtre les messages qui ne sont pas des messages de chat
                if (message.startsWith("CHAT: ")) {
                    // Diffuser le message de chat à tous les clients
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
