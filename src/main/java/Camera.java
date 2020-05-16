import java.awt.geom.Point2D;

public class Camera {
    private Point3D position = new Point3D(0, 0, 0);
    private Point3D rotation = new Point3D(0, 0, 0);

    private double pxWidth, pxHeight;
    private double hFov;
    private double vFov;
    private double focalLength;

    public Camera(double pxWidth, double pxHeight, double fov) {
        this.pxWidth = pxWidth;
        this.pxHeight = pxHeight;
        setFov(fov);
    }

    public void setFov(double fov) {
        this.hFov = fov;
        this.focalLength = pxWidth / Math.tan(Math.toRadians(hFov));
        this.vFov = Math.toDegrees(Math.atan(pxHeight / 2 / focalLength));
    }

    public void setCamPosition(Point3D position) {
        this.position = position;
    }

    public void setCamPosition(double x, double y, double z) {
        this.position = new Point3D(x, y, z);
    }

    public void setCamRotationRads(double rotX, double rotY, double rotZ) {
        this.rotation = new Point3D(rotX, rotY, rotZ);
    }

    public void setCamRotationDegrees(Point3D rotation) {
        this.rotation = new Point3D(Math.toRadians(rotation.getX()), Math.toRadians(rotation.getY()),
                Math.toRadians(rotation.getZ()));
    }

    public void setCamRotationRads(Point3D rotation) {
        this.rotation = rotation;
    }

    public void setCamRotationDegrees(double rotX, double rotY, double rotZ) {
        this.rotation = new Point3D(Math.toRadians(rotX), Math.toRadians(rotY), Math.toRadians(rotZ));
    }

    /**
     * Calculates the {@link Point3D} relative to the camera position and rotation. In other words, it gets the point
     * coordinates if the camera were at position (0,0,0) and rotation (0,0,0).
     *
     * @param point input {@link Point3D}.
     * @return camera relative {@link Point3D}.
     */
    public Point3D calculateCameraRelativePoint(Point3D point) {
        Point3D output;
        output = new Point3D(point.getX() - position.getX(), point.getY() - position.getY(),
                point.getZ() - position.getZ());
        output = Renderer.getInstance().apply3DRotationMatrix(output, rotation);
        return output;
    }

    /**
     * Projects a 3D point onto the camera plane.
     *
     * @param point3D {@link Point3D} to project.
     * @return the projected {@link Point2D}.
     */
    public Point2D project3dPointToCameraPlane(Point3D point3D) {
        Point3D cameraRelativePoint = calculateCameraRelativePoint(point3D);
        double thetaX = Math.atan2(cameraRelativePoint.getX(), cameraRelativePoint.getZ());
        double thetaY = Math.atan2(cameraRelativePoint.getY(), cameraRelativePoint.getZ());
        //Calculate X and Y pixels. Because cameras reflect light opposite, the values are negative.
        double px = focalLength * Math.tan(thetaX);
        double py = focalLength * Math.tan(thetaY);
        px = checkForIncorrectNumbers(px);
        py = checkForIncorrectNumbers(py);

        return new Point2D.Double(px, py);
    }

    /**
     * Calculates the 3D ray vector from the camera to a screen point.
     *
     * @param screenPoint the screen {@link Point2D} to get the vector to.
     * @return the 3D ray vector from the camera to a screen point in the form of a {@link Point3D}.
     */
    public Point3D getRayVectorFromScreenPoint(Point2D screenPoint) {
        double px = screenPoint.getX();
        double py = screenPoint.getY();

        double thetaX = Math.atan2(px, focalLength);
        double thetaY = Math.atan2(py, focalLength);

        return new Point3D(Math.tan(thetaX), Math.tan(thetaY), 1);
    }

    /**
     * Returns the distance from a screen point to a 3D triangle.
     * <p>
     * The 3D triangle must be in camera-relative coordinates. The screen point must be in screen-relative coordinates.
     *
     * @param screenPoint screen-relative {@link Point2D}.
     * @param tri         camera-realtive {@link Tri3D}.
     * @return the distance from the screen point to the triangle.
     */
    public double getScreenDistanceToPlane(Point2D screenPoint, Tri3D tri) {
        Point3D rayVector = getRayVectorFromScreenPoint(screenPoint);
        Point3D planeNormal = tri.getPlaneNormalVector();
        Point3D planePoint = getPlaneRayIntersection(rayVector, planeNormal, tri.getV0());
        return planePoint.distance(new Point3D());
    }

    /**
     * Returns the 3D point of intersection of a ray vector from the camera and a plane given the plane normal and a
     * point on the plane.
     * <p>
     * Algorithm from https://rosettacode.org/wiki/Find_the_intersection_of_a_line_with_a_plane
     *
     * @param rayVector   the ray vector from the camera to the desired intersection point.
     * @param planeNormal the normal vector of the plane.
     * @param planePoint  a point on the plane.
     * @return {@link Point3D} of intersection.
     */
    public Point3D getPlaneRayIntersection(Point3D rayVector, Point3D planeNormal, Point3D planePoint) {
        Point3D diff = new Point3D().minus(planePoint);
        double prod1 = diff.dot(planeNormal);
        double prod2 = rayVector.dot(planeNormal);
        double prod3 = prod1 / prod2;
        return new Point3D().minus(rayVector.times(prod3));
    }

    private double checkForIncorrectNumbers(double x) {
        return Double.isNaN(x) || Double.isInfinite(x) ? 0 : x;
    }

    public Point3D getPosition() {
        return position;
    }

    public Point3D getRotation() {
        return rotation;
    }

    public double getPxWidth() {
        return pxWidth;
    }

    public double getPxHeight() {
        return pxHeight;
    }

    public double gethFov() {
        return hFov;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
        this.hFov = Math.toDegrees(Math.atan(pxWidth / 2 / focalLength));
        this.vFov = Math.toDegrees(Math.atan(pxHeight / 2 / focalLength));
    }

    public double getvFov() {
        return vFov;
    }
}
