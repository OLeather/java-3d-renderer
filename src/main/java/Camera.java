import java.awt.geom.Point2D;

public class Camera {
    private Point3D position = new Point3D(0,0,0);
    private Point3D rotation = new Point3D(0,0,0);

    private double pxWidth, pxHeight;
    private double hFov;
    private double vFov;
    private double focalLength;

    public Camera(double pxWidth, double pxHeight, double fov){
        this.pxWidth = pxWidth;
        this.pxHeight = pxHeight;
        setFov(fov);
    }

    public void setFocalLength(double focalLength){
        this.focalLength = focalLength;
        this.hFov = Math.toDegrees(Math.atan(pxWidth/2/focalLength));
        this.vFov = Math.toDegrees(Math.atan(pxHeight/2/focalLength));
    }

    public void setFov(double fov){
        this.hFov = fov;
        this.focalLength = pxWidth/Math.tan(Math.toRadians(hFov));
        this.vFov = Math.toDegrees(Math.atan(pxHeight/2/focalLength));
    }

    public void setCamPosition(double x, double y, double z){
        this.position = new Point3D(x, y, z);
    }

    public void setCamRotationRads(double rotX, double rotY, double rotZ){
        this.rotation = new Point3D(rotX, rotY, rotZ);
    }

    public void setCamRotationDegrees(double rotX, double rotY, double rotZ){
        this.rotation = new Point3D(Math.toRadians(rotX), Math.toRadians(rotY), Math.toRadians(rotZ));
    }

    public Point3D calculateCameraRelativePoint(Point3D point){
        Point3D output;
        output = new Point3D(point.getX()-position.getX(), point.getY()-position.getY(), point.getZ()-position.getZ());
        System.out.println(rotation);
        output = Renderer.getInstance().apply3DRotationMatrix(output, rotation);
        System.out.println(output);
        return output;
    }

    public Point2D project3dPointTo2dPlane(Point3D point3D){
        Point3D cameraRelativePoint = calculateCameraRelativePoint(point3D);
        double thetaX = Math.atan2(cameraRelativePoint.getX(), cameraRelativePoint.getZ());
        double thetaY = Math.atan2(cameraRelativePoint.getY(), cameraRelativePoint.getZ());
        //Calculate X and Y pixels. Because cameras reflect light opposite, the values are negative.
        double px = focalLength*Math.tan(thetaX);
        double py = focalLength*Math.tan(thetaY);
        px = checkForIncorrectNumbers(px);
        py = checkForIncorrectNumbers(py);

        return new Point2D.Double(px, py);
    }

    private double checkForIncorrectNumbers(double x){
        return Double.isNaN(x) || Double.isInfinite(x) ? 0 : x;
    }

    public Point3D getPosition(){
        return position;
    }

    public Point3D getRotation(){
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

    public double getvFov() {
        return vFov;
    }
}
