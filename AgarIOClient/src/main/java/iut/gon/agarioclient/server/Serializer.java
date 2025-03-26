package iut.gon.agarioclient.server;
import java.io.*;

public class Serializer {

    // Envoi d'un objet sur un flux de sortie
    public static <T extends Serializable> void sendObject(T object, PrintWriter oos) throws IOException {
        oos.print(object);
        oos.flush();
    }

    // Réception d'un objet depuis un flux d'entrée
    public static <T extends Serializable> T receiveObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return (T) ois.readObject();
    }
}