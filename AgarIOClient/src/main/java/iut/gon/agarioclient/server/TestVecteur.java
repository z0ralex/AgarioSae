package iut.gon.agarioclient.server;
import java.io.Serializable;
public class TestVecteur implements Serializable {
    private static final long serialVersionUID = 1L;
    private double x, y, z;

    public TestVecteur(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double norme() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public String toString() {
        return "Vecteura(" + x + ", " + y + ", " + z + ")";
    }
}
