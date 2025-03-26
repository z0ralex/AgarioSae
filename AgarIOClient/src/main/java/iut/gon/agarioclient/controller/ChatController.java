package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.server.TestVecteur;
import javafx.application.Platform;
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

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    private String clientId;
    private String nickname;

    public void initialize(String nickname) {
        this.nickname = nickname;
        try {
            socket = new Socket("10.42.17.106", 12345); // Connexion au serveur
            in = new ObjectInputStream(socket.getInputStream()); // Pour recevoir des objets
            out = new ObjectOutputStream(socket.getOutputStream()); // Pour envoyer des objets

            // Thread pour recevoir les messages du serveur (texte)
            Thread receiveMessagesThread = new Thread(() -> {
                try {
                    Object message;
                    while ((message = in.readObject()) != null) {
                        if (message instanceof String) {
                            final String msg = (String) message;
                            Platform.runLater(() -> chatArea.appendText(msg + "\n"));
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            receiveMessagesThread.start();

            // Recevoir l'ID du client
            String welcomeMessage = (String) in.readObject(); // Lecture de l'ID
            System.out.println(welcomeMessage);
            if (welcomeMessage != null && welcomeMessage.startsWith("Bienvenue! Votre ID est : ")) {
                clientId = welcomeMessage.split(": ")[1].split(" ")[0]; // Extraire l'ID du message
            } else {
                System.err.println("Erreur : message d'accueil invalide");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty() && clientId != null) {
            // Envoi du message au serveur avec un préfixe "CHAT: "
            try {
                out.writeObject("CHAT: "+nickname + " : " + message);
                out.flush();
                messageField.clear(); // Effacer le champ de texte après envoi
            } catch (IOException e) {
                e.printStackTrace();
            }
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
