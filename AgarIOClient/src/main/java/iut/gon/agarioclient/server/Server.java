package iut.gon.agarioclient.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    private static final int PORT = 12345; // Port d'écoute
    private static ServerSocket serverSocket;

    private static Set<ClientHandler> clientHandlersSet = new HashSet<>();;
    private static Set<ObjectOutputStream> clientOutputStreams = new HashSet<>(); // Remplace PrintWriter par ObjectOutputStream
    private static Map<String, Boolean> clientReadyStatus = new HashMap<>(); // Suivi de l'état de préparation des clients
    private static ExecutorService threadPool = Executors.newCachedThreadPool(); // Thread pool pour gérer les connexions

    // Méthode pour générer un ID aléatoire unique
    public static String generateClientId() {
        return "<Client>-" + UUID.randomUUID().toString().substring(0, 8); // Génère un ID aléatoire basé sur UUID
    }

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Serveur lancé, en attente de connexions...");
            GameUpdater gameUpdater = new GameUpdater(clientHandlersSet);
            threadPool.execute(gameUpdater);
            // Attente infinie de nouvelles connexions
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                // Création d'un thread pour gérer cette connexion
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlersSet.add(clientHandler);
                threadPool.execute(clientHandler); // Démarrage du thread
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }

    // Méthode pour ajouter un ObjectOutputStream thread-safe
    public static synchronized void addClientOutputStream(ObjectOutputStream out, String clientId) {
        clientOutputStreams.add(out);
        clientReadyStatus.put(clientId, false); // Par défaut, le client n'est pas prêt
    }

    // Méthode pour supprimer un ObjectOutputStream lors de la déconnexion
    public static synchronized void removeClientOutputStream(ObjectOutputStream out, String clientId) {
        clientOutputStreams.remove(out);
        clientReadyStatus.remove(clientId);
    }

    // Méthode pour envoyer un message à tous les clients
    public static synchronized void broadcast(Object message) {
        for (ObjectOutputStream out : clientOutputStreams) {
            try {
                out.writeObject(message);  // Envoi de l'objet à tous les clients
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Boolean> getClientReadyStatus() {
        return clientReadyStatus;
    }

    public static Set<ClientHandler> getClientHandlersSet() {
        return clientHandlersSet;
    }
}
