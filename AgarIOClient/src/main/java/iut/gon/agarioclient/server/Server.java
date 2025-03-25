package iut.gon.agarioclient.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    private static final int PORT = 12345; // Port d'écoute
    private static ServerSocket serverSocket;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, Boolean> clientReadyStatus = new HashMap<>(); // Suivi de l'état de préparation des clients
    private static ExecutorService threadPool = Executors.newCachedThreadPool(); // Thread pool pour gérer les connexions

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Serveur lancé, en attente de connexions...");

            // Attente infinie de nouvelles connexions
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                // Création d'un thread pour gérer cette connexion
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                threadPool.execute(clientHandler); // Démarrage du thread
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }

    // Méthode pour ajouter un PrintWriter thread-safe
    public static synchronized void addClientWriter(PrintWriter out, String clientId) {
        clientWriters.add(out);
        clientReadyStatus.put(clientId, false); // Par défaut, le client n'est pas prêt
    }

    // Méthode pour supprimer un PrintWriter lors de la déconnexion
    public static synchronized void removeClientWriter(PrintWriter out, String clientId) {
        clientWriters.remove(out);
        clientReadyStatus.remove(clientId);
    }

    // Méthode pour envoyer un message à tous les clients
    public static synchronized void broadcast(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);  // Envoi du message à tous les clients connectés
        }
    }
}
