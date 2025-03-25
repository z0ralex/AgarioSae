package iut.gon.agarioclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.*;

public class ChatController {

    @FXML
    private TextArea chatArea;

    @FXML
    private Button sendButton;

    @FXML
    private TextField messageField; // Champ de texte pour saisir le message

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    private String clientId;

    public void initialize() {
        try {
            socket = new Socket("10.42.17.106", 12345); // Connexion au serveur
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Thread pour recevoir les messages du serveur
            Thread receiveMessagesThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        // On ne veut afficher que les messages de chat (et non ceux comme "Bienvenue...")
                        if (message.startsWith("CHAT: ")) {
                            final String msg = message.substring(6); // On enlève le préfixe "CHAT: "
                            javafx.application.Platform.runLater(() -> chatArea.appendText(msg + "\n"));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveMessagesThread.start();

            // Recevoir l'ID du client
            String welcomeMessage = in.readLine(); // "Votre ID est : Client-XXXX"
            System.out.println(welcomeMessage);
            if (welcomeMessage != null && welcomeMessage.startsWith("Bienvenue! Votre ID est : ")) {
                clientId = welcomeMessage.split(": ")[1].split(" ")[0]; // Extraire l'ID du message
            } else {
                // Si le message ne correspond pas, afficher un message d'erreur ou gérer autrement
                System.err.println("Erreur : message d'accueil invalide");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleSendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty() && clientId != null) {
            // Envoi du message au serveur avec un préfixe "CHAT: "
            out.println("CHAT: " + message);
            messageField.clear(); // Effacer le champ de texte après envoi
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
