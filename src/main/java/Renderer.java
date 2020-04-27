import org.ejml.simple.SimpleMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Optional;

public class Renderer extends JFrame {
    private static Renderer instance = new Renderer();

    public static Renderer getInstance() {
        return instance;
    }

    private ArrayList<Object3D> objects = new ArrayList<>();

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private Color[][] pixelColors;

    private Renderer() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setVisible(true);
    }

    public void renderObjects() {
        for(Object o : objects){

        }
    }

    public Point2D cameraToScreenCoordinate(Point2D point) {
        return new Point2D.Double(point.getX() + getWidth() / 2, point.getY() + getHeight() / 2);
    }

    public Point3D getPointRelativeToPosition(Point3D point, Point3D position) {
        return new Point3D(
                point.getX() - position.getX(),
                point.getY() - position.getY(),
                point.getZ() - position.getZ());
    }

    /**
     * Applies a 3D rotation matrix to the input {@link Point3D}.
     * <p>
     * Matrix equation from https://en.wikipedia.org/wiki/Rotation_matrix
     *
     * @param point
     * @param rotation
     * @return
     */
    public Point3D apply3DRotationMatrix(Point3D point, Point3D rotation) {
        double thetaX = rotation.getX();
        double thetaY = rotation.getY();
        double thetaZ = rotation.getZ();

        //Create rotation matrices
        SimpleMatrix rotXMatrix = new SimpleMatrix(new double[][]{
                {1, 0, 0},
                {0, Math.cos(thetaX), -Math.sin(thetaX)},
                {0, Math.sin(thetaX), Math.cos(thetaX)}
        });
        SimpleMatrix rotYMatrix = new SimpleMatrix(new double[][]{
                {Math.cos(thetaY), 0, Math.sin(thetaY)},
                {0, 1, 0},
                {-Math.sin(thetaY), 0, Math.cos(thetaY)}
        });
        SimpleMatrix rotZMatrix = new SimpleMatrix(new double[][]{
                {Math.cos(thetaZ), -Math.sin(thetaZ), 0},
                {Math.sin(thetaZ), Math.cos(thetaZ), 0},
                {0, 0, 1}
        });

        Point3D outputPoint;
        //Apply x, y, and z rotation matrix respectively
        outputPoint = Point3D.applyMatrix(point, rotXMatrix);
        outputPoint = Point3D.applyMatrix(outputPoint, rotYMatrix);
        outputPoint = Point3D.applyMatrix(outputPoint, rotZMatrix);

        //Return rotated points
        return outputPoint;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for (int c = 0; c < pixelColors.length; c++) {
            for (int r = 0; r < pixelColors[0].length; r++) {
                if (pixelColors[r][c] == null) {
                    g.setColor(new Color(0, 0, 0));
                } else {
                    g.setColor(pixelColors[r][c]);
                }
                g.drawLine(r, c, r, c);
            }
        }
    }

    public void addObject(Object3D object) {
        boolean alreadyExists = false;
        for (Object3D o : objects) {
            if (o.getName() == object.getName()) {
                alreadyExists = true;
            }
        }
        if (alreadyExists) {
            System.out.println("WARNING: Object of name: " + object.getName() + " already exists!");
        } else {
            objects.add(object);
        }
    }

    public Optional<Object3D> getObjectFromName(String name) {
        for (Object3D o : objects) {
            if (o.getName() == name) {
                return Optional.of(o);
            }
        }
        System.out.println("Object of name: " + name + " does not exist!");
        return Optional.empty();
    }

    public ArrayList<Object3D> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Object3D> objects) {
        this.objects = objects;
    }

    public void setPixelColors(Color[][] pixelColors) {
        this.pixelColors = pixelColors;
        repaint();
    }
}
