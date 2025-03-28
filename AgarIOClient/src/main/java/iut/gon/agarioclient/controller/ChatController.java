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

/**
 * Controller class for managing the chat functionality in the game.
 * Handles sending and receiving messages between the client and the server.
 */
public class ChatController {

    @FXML
    private TextArea chatArea; // Text area for displaying chat messages

    @FXML
    private Button sendButton; // Button for sending chat messages

    @FXML
    private TextField messageField; // Text field for entering chat messages

    private ObjectOutputStream out; // Output stream for sending objects to the server
    private ObjectInputStream in; // Input stream for receiving objects from the server
    private Socket socket; // Socket for connecting to the server
    private String clientId; // Unique identifier for the client
    private String nickname; // Nickname of the client
    private String host; // Host address of the server
    private Integer port; // Port number of the server

    /**
     * Initializes the chat controller with the specified nickname, host, and port.
     * Establishes a connection to the server and starts a thread to receive messages.
     *
     * @param nickname the nickname of the client
     * @param host     the host address of the server
     * @param port     the port number of the server
     */
    public void initialize(String nickname, String host, Integer port) {
        this.nickname = nickname;
        this.host = host;
        this.port = port;

        try {
            socket = new Socket(this.host, this.port); // Connect to the server
            in = new ObjectInputStream(socket.getInputStream()); // For receiving objects
            out = new ObjectOutputStream(socket.getOutputStream()); // For sending objects

            // Thread for receiving messages from the server (text or objects)
            Thread receiveMessagesThread = new Thread(() -> {
                Object message = null;
                try {
                    while (true) {
                        message = Serializer.receiveObject(in);
                        if (message instanceof String) {
                            final String msg = (String) message;

                            // If the welcome message is received, extract the client ID
                            if (msg.startsWith("Bienvenue! Votre ID est : ")) {
                                System.out.println(msg);
                                clientId = msg.substring("Bienvenue! Votre ID est : ".length());
                                // Send "ready" message to the server to indicate the client is ready
                                out.writeObject("pret");
                                System.out.println("pret");
                                out.flush();
                            }
                            if (msg.startsWith("CHAT: ")) {
                                // Display the chat message in the chat area
                                String messageToDisplay = msg.replaceFirst("^CHAT: ", "");
                                Platform.runLater(() -> chatArea.appendText(messageToDisplay + "\n"));
                            }
                        } else if (message instanceof ArrayList<?>) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of sending a chat message.
     * Sends the message to the server with a "CHAT: " prefix.
     */
    @FXML
    public void handleSendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty() && clientId != null) {
            try {
                out.writeObject("CHAT: " + nickname + " : " + message); // Send the message as a String
                out.flush();
                messageField.clear(); // Clear the text field after sending
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the socket connection to the server.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}