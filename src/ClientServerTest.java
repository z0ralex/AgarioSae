import java.io.*;
import java.net.*;

public class ClientServerTest {

    private static final String SERVER_ADDRESS = "127.0.0.1";  // Adresse du serveur (localhost pour tester en local)
    private static final int SERVER_PORT = 12345;              // Port utilisé pour la connexion

    public static void main(String[] args) {
        try {
            // Connexion au serveur
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connexion établie avec le serveur...");

            // Création des flux d'entrée et de sortie
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Lire le message d'accueil du serveur
            String serverMessage = in.readLine();
            System.out.println("Message du serveur : " + serverMessage);

            // Envoi de l'ID du joueur au serveur
            String playerId = "Player2";  // ID du joueur (tu peux en choisir un autre)
            out.println(playerId);
            System.out.println("Message envoyé au serveur : " + playerId);

            // Lire la réponse du serveur
            serverMessage = in.readLine();
            System.out.println("Message du serveur : " + serverMessage);

            // Envoi du message "prêt" pour indiquer que le joueur est prêt
            out.println("prêt");
            out.flush(); // Assurer l'envoi immédiat
            System.out.println("Message 'prêt' envoyé au serveur");

            // Attendre un moment pour voir les interactions
            Thread.sleep(1000);

            // Envoi de quelques messages supplémentaires pour simuler l'interaction
            out.println("Je me déplace vers la droite");
            Thread.sleep(1000);
            out.println("Je me déplace vers la gauche");

            // Fermer la connexion
            out.close();
            in.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}