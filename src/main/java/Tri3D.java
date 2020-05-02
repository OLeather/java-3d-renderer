public class Tri3D {
    private Point3D v0;
    private Point3D v1;
    private Point3D v2;

    public Tri3D(Point3D v0, Point3D v1, Point3D v2){
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    public Point3D getV0() {
        return v0;
    }

    public void setV0(Point3D v0) {
        this.v0 = v0;
    }

    public Point3D getV1() {
        return v1;
    }

    public void setV1(Point3D v1) {
        this.v1 = v1;
    }

    public Point3D getV2() {
        return v2;
    }

    public void setV2(Point3D v2) {
        this.v2 = v2;
    }

    @Override
    public String toString() {
        return "Tri3D{" +
                "v0=" + v0 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                '}';
    }
}
