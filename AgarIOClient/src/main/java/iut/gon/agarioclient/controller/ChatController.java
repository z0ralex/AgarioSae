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

    // Méthode d'initialisation de la connexion avec le serveur
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
                        // Afficher le message dans l'interface graphique
                        final String msg = message;
                        javafx.application.Platform.runLater(() -> chatArea.appendText(msg + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveMessagesThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            // Envoyer le message au serveur
            out.println(message);
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
