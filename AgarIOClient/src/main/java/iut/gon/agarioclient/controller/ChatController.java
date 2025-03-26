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

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private ObjectInputStream objectIn;
    private GameController gameController;
    private String clientId;

    private String nickname;


    public void initialize(String nickname, GameController gameController) {
        this.nickname = nickname;
        this.gameController = gameController;
        try {
            socket = new Socket("10.42.17.106", 12345); // Connexion au serveur
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Pour les messages texte
            out = new PrintWriter(socket.getOutputStream(), true); // Pour envoyer des messages texte
            DataInputStream dataIn = new DataInputStream(socket.getInputStream()); // Pour les objets

            // Thread pour recevoir les messages du serveur (texte)
            Thread receiveMessagesThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("CHAT: ")) {
                            final String msg = message.substring(6); // On enlève le préfixe "CHAT: "
                            Platform.runLater(() -> chatArea.appendText(msg + "\n"));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveMessagesThread.start();

            // Thread pour recevoir des objets (données sérialisées)
            Thread receiveObjectsThread = new Thread(() -> {
                try {
                    ObjectInputStream objectIn = new ObjectInputStream(dataIn); // Créer un ObjectInputStream pour lire les objets
                    while (true) {
                        Object receivedObject = objectIn.readObject();
                        if (receivedObject instanceof TestVecteur) {
                            TestVecteur vecteur = (TestVecteur) receivedObject;
                            Platform.runLater(() -> gameController.updateFromServer(vecteur));
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            receiveObjectsThread.start();

            // Recevoir l'ID du client
            String welcomeMessage = in.readLine(); // "Votre ID est : Client-XXXX"
            System.out.println(welcomeMessage);
            if (welcomeMessage != null && welcomeMessage.startsWith("Bienvenue! Votre ID est : ")) {
                clientId = welcomeMessage.split(": ")[1].split(" ")[0]; // Extraire l'ID du message
            } else {
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
            out.println("CHAT: "+nickname + " : " + message);
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
