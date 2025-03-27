package iut.gon.agarioclient.controller;

import iut.gon.agarioclient.server.Serializer;
import iut.gon.agarioclient.server.TestVecteur;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
    private String host;
    private Integer port;


    public void initialize(String nickname, String host, Integer port) {

        System.out.println("ici");

        if(host==null && port == null){
            chatArea.setStyle("-fx-background-color: transparent;");
            messageField.setStyle("-fx-background-color: transparent;");
            sendButton.setStyle("-fx-background-color: transparent;");
        }

        this.nickname = nickname;
        this.host=host;
        this.port=port;


        try {
            socket = new Socket(this.host, this.port); // Connexion au serveur
            in = new ObjectInputStream(socket.getInputStream()); // Pour recevoir des objets
            out = new ObjectOutputStream(socket.getOutputStream()); // Pour envoyer des objets

            // Thread pour recevoir les messages du serveur (texte ou objets)
            Thread receiveMessagesThread = new Thread(() -> {
                Object message = null;
                try {
                    while (true) {
                        //Object message = in.readObject();
                        message = Serializer.receiveObject(in);
                        if (message instanceof String) {
                            final String msg = (String) message;

                            // Si le message de bienvenue est reçu, on peut l'interpréter pour extraire l'ID si besoin
                            if (msg.startsWith("Bienvenue! Votre ID est : ")) {
                                System.out.println(msg);
                                clientId = msg.substring("Bienvenue! Votre ID est : ".length());
                                // On envoie le message "pret" au serveur pour indiquer que le client est prêt
                                out.writeObject("pret");
                                System.out.println("pret");
                                out.flush();
                            }
                            if (msg.startsWith("CHAT: ")) {
                                // Affichage dans la zone de chat (en enlevant le préfixe "CHAT: " s'il y en a un)
                                String messageToDisplay = msg.replaceFirst("^CHAT: ", "");
                                Platform.runLater(() -> chatArea.appendText(messageToDisplay + "\n"));
                            }
                        }else if(message instanceof ArrayList<?>) {
                            ArrayList<?> ar = (ArrayList<?>) message;
                        } else if (message instanceof TestVecteur) {
                            final TestVecteur vecteur = (TestVecteur) message;
                            Platform.runLater(() -> {
                                System.out.println("Vecteur reçu : " + vecteur);
                            });
                        } else {
                            System.err.println("Type inconnu reçu : " + message.getClass());
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(message);
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
