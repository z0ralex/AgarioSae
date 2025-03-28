package iut.gon.agarioclient.model.entity.moveable;

import javafx.geometry.Point2D;

import java.io.Serializable;

/**
 * Code pris de Point2D
 */
public class Point2DSerial implements Serializable {
    public static final Point2DSerial ZERO = new Point2DSerial(0.0, 0.0);
    private final double x;
    private final double y;

    public final double getX() {
        return this.x;
    }

    public final double getY() {
        return this.y;
    }

    public Point2DSerial(double v, double v1) {
        this.x = v;
        this.y = v1;
    }

    public Point2DSerial(Point2D point){
        this.x = point.getX();
        this.y = point.getY();
    }

    public Point2DSerial subtract(Point2DSerial var1) {
        return this.subtract(var1.getX(), var1.getY());
    }

    public Point2DSerial subtract(double var1, double var3) {
        return new Point2DSerial(this.getX() - var1, this.getY() - var3);
    }

    public double distance(double var1, double var3) {
        double var5 = this.getX() - var1;
        double var7 = this.getY() - var3;
        return Math.sqrt(var5 * var5 + var7 * var7);
    }

    public double distance(Point2DSerial var1) {
        return this.distance(var1.getX(), var1.getY());
    }

    public Point2DSerial add(double var1, double var3) {
        return new Point2DSerial(this.getX() + var1, this.getY() + var3);
    }

    public Point2DSerial add(Point2DSerial var1) {
        return this.add(var1.getX(), var1.getY());
    }



    public Point2DSerial multiply(double var1) {
        return new Point2DSerial(this.getX() * var1, this.getY() * var1);
    }



    public Point2DSerial normalize() {
        double var1 = this.magnitude();
        return var1 == 0.0 ? new Point2DSerial(0.0, 0.0) : new Point2DSerial(this.getX() / var1, this.getY() / var1);
    }

    public Point2DSerial midpoint(double var1, double var3) {
        return new Point2DSerial(var1 + (this.getX() - var1) / 2.0, var3 + (this.getY() - var3) / 2.0);
    }

    public Point2DSerial midpoint(Point2DSerial var1) {
        return this.midpoint(var1.getX(), var1.getY());
    }


    public double magnitude() {
        double var1 = this.getX();
        double var3 = this.getY();
        return Math.sqrt(var1 * var1 + var3 * var3);
    }

    public boolean equals(Object var1) {
        if (var1 == this) {
            return true;
        } else if (!(var1 instanceof Point2D)) {
            return false;
        } else {
            Point2DSerial var2 = (Point2DSerial) var1;
            return this.getX() == var2.getX() && this.getY() == var2.getY();
        }
    }



    //TODO multiply, add, normalize, magnitude

}
