package iut.gon.agarioclient.server;
import java.io.*;
import java.util.Random;
import java.net.Socket;
import java.util.Set;

public  class GameUpdater implements Runnable {
    private Set<PrintWriter> clientWriters;

    public GameUpdater(Set<PrintWriter> clients) {
        this.clientWriters = clients;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Random random = new Random();
                TestVecteur vecteur = new TestVecteur(random.nextDouble(), random.nextDouble(), random.nextDouble());

                // Envoyer l'objet à tous les clients
                for (ClientHandler client : Server.getClientHandlersSet()) {
                    try {
                        client.sendObject(vecteur);
                        System.out.println("Vecteur envoyé au client " + client.getClientId() + " : " + vecteur);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Thread.sleep(33); // Envoi toutes les 33 ms (~30 FPS)
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}