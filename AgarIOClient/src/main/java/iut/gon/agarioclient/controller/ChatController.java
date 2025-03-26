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

            // Thread pour recevoir les messages du serveur (texte ou objets)
            Thread receiveMessagesThread = new Thread(() -> {
                try {
                    while (true) {
                        // Lire un objet qui peut être soit une String soit un TestVecteur
                        Object message = in.readObject();
                        //System.out.println("Message reçu : " + message);

                        if (message instanceof String) {
                            // Si c'est une String (message de chat)
                            final String msg = (String) message;
                            String messageToDisplay = msg.replaceFirst("^CHAT: ", "");
                            Platform.runLater(() -> chatArea.appendText(messageToDisplay + "\n"));
                        } else if (message instanceof TestVecteur) {
                            // Si c'est un TestVecteur
                            final TestVecteur vecteur = (TestVecteur) message;
                            Platform.runLater(() -> {
                                // Afficher le vecteur reçu
                                System.out.println("Vecteur reçu : " + vecteur);
                            });
                        } else {
                            System.err.println("Type inconnu reçu : " + message.getClass());
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            receiveMessagesThread.start();


        } catch (IOException  e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleSendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty() && clientId != null) {
            // Envoi du message au serveur avec un préfixe "CHAT: "
            try {
                out.writeObject("CHAT: " + nickname + " : " + message);  // Envoi du message sous forme de String
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
