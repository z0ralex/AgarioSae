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
            clientId = Server.generateClientId();
            System.out.println("ID du client généré : " + clientId);
            out.println("Bienvenue! Votre ID est : " + clientId); // Message explicite

            Server.addClientWriter(out, clientId);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message reçu de " + clientId + ": " + message);

                if (message.startsWith("CHAT: ")) {
                    Server.broadcast("CHAT: " + message.substring(6));
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur du client " + clientId + ": " + e.getMessage());
        } finally {
            // Nettoyage après déconnexion
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClientWriter(out, clientId); // Retirer proprement le client
        }
    }

}


