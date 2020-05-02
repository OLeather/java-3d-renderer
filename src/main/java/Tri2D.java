import java.awt.geom.Point2D;

public class Tri2D {
    private Point2D v0;
    private Point2D v1;
    private Point2D v2;

    public Tri2D(Point2D v0, Point2D v1, Point2D v2){
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    public Point2D getV0() {
        return v0;
    }

    public void setV0(Point2D v0) {
        this.v0 = v0;
    }

    public Point2D getV1() {
        return v1;
    }

    public void setV1(Point2D v1) {
        this.v1 = v1;
    }

    public Point2D getV2() {
        return v2;
    }

    public void setV2(Point2D v2) {
        this.v2 = v2;
    }

    @Override
    public String toString() {
        return "Tri2D{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                '}';
    }
}
