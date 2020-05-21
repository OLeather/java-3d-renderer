import org.ejml.simple.SimpleMatrix;

public class Point3D {
    private double x;
    private double y;
    private double z;

    public Point3D() {
        this(0, 0, 0);
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns a {@link Point3D} from the input <code>point</code> multiplied by the input <code>matrix</code>.
     * <p>
     * SimpleMatrix class from EJML library: http://ejml.org/wiki/index.php?title=Main_Page
     *
     * @param point
     * @param matrix
     * @return
     */
    public static Point3D applyMatrix(Point3D point, SimpleMatrix matrix) {
        SimpleMatrix pointMatrix = new SimpleMatrix(new double[][]{
                {point.getX()},
                {point.getY()},
                {point.getZ()}
        });
        SimpleMatrix appliedMatrix = matrix.mult(pointMatrix);
        return new Point3D(appliedMatrix.get(0), appliedMatrix.get(1), appliedMatrix.get(2));
    }

    /**
     * Returns the distance of this point to another point.
     *
     * @param other
     * @return
     */
    public double distance(Point3D other) {
        return Math.sqrt(Math.pow(other.getX() - getX(), 2) + Math.pow(other.getY() - getY(), 2) +
                Math.pow(other.getZ() - getZ(), 2));
    }

    /**
     * Returns this point added to the other point.
     *
     * @param other
     * @return
     */
    public Point3D plus(Point3D other) {
        return new Point3D(other.getX() + getX(), other.getY() + getY(), other.getZ() + getZ());
    }

    /**
     * Returns this point subtracted by the other point
     *
     * @param other
     * @return
     */
    public Point3D minus(Point3D other) {
        return new Point3D(getX() - other.getX(), getY() - other.getY(), getZ() - other.getZ());
    }

    /**
     * Returns this point times the other point
     *
     * @param other
     * @return
     */
    public Point3D times(Point3D other) {
        return new Point3D(other.getX() * getX(), other.getY() * getY(), other.getZ() * getZ());
    }

    /**
     * Returns this point times a scalar value
     *
     * @param scalar
     * @return
     */
    public Point3D times(double scalar) {
        return new Point3D(scalar * getX(), scalar * getY(), scalar * getZ());
    }

    /**
     * Returns the dot product of this point and another point
     *
     * @param other
     * @return
     */
    public double dot(Point3D other) {
        return other.getX() * getX() + other.getY() * getY() + other.getZ() * getZ();
    }

    /**
     * Returns the cross product of this point and another point
     *
     * @param other
     * @return
     */
    public Point3D cross(Point3D other) {
        double newX = y * other.getZ() - z * other.getY();
        double newY = z * other.getX() - x * other.getZ();
        double newZ = x * other.getY() - y * other.getX();
        return new Point3D(newX, newY, newZ);
    }

    /**
     * Returns the normalized vector.
     * <p>
     * Normalized vector calculation from http://www.fundza.com/vectors/normalize/
     *
     * @return
     */
    public Point3D normalize() {
        double length = distance(new Point3D());
        return new Point3D(x / length, y / length, z / length);
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

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
