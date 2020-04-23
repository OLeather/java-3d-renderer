import java.awt.*;
import java.awt.geom.Point2D;

public class Main {
    public static void main(String[] args) {
        Renderer.getInstance();
        Camera camera = new Camera(Renderer.getInstance().getWidth(), Renderer.getInstance().getHeight(), 60);
        Color[][] pixels = new Color[Renderer.getInstance().getWidth()][Renderer.getInstance().getHeight()];
        Point3D p3d = new Point3D(0,0,5);
        Point2D point2D = camera.project3dPointTo2dPlane(p3d);
        point2D = Renderer.getInstance().cameraToScreenCoordinate(point2D);
        pixels[(int)point2D.getX()][(int)point2D.getY()] = new Color(255,0,0);
        Renderer.getInstance().setPixelColors(pixels);
        Renderer.getInstance().repaint();
    }
}
