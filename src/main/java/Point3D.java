import org.ejml.simple.SimpleMatrix;

public class Point3D {
    private double x;
    private double y;
    private double z;

    public Point3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns a {@link Point3D} from the input <code>point</code> multiplied by the input <code>matrix</code>.
     *
     * @param point
     * @param matrix
     * @return
     */
    public static Point3D applyMatrix(Point3D point, SimpleMatrix matrix){
        SimpleMatrix pointMatrix = new SimpleMatrix(new double[][]{
                {point.getX()},
                {point.getY()},
                {point.getZ()}
        });
        SimpleMatrix appliedMatrix = pointMatrix.mult(matrix);
        return new Point3D(appliedMatrix.get(0), appliedMatrix.get(1), appliedMatrix.get(2));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
