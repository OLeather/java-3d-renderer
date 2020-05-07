import org.ejml.simple.SimpleMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Renderer extends JFrame {
    private static Renderer instance = new Renderer();

    public static Renderer getInstance() {
        return instance;
    }

    private ArrayList<Object3D> objects = new ArrayList<>();

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private Color[][] pixelColors = new Color[WIDTH][HEIGHT];

    private Camera camera;

    private Renderer() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setVisible(true);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void renderObjects() {
        for (Object3D o : objects) {
            for(Tri3D tri : o.getPositionedTris()){
                Tri2D projectedTri = projectTri3DTo2D(tri);
                testRenderTri(projectedTri);
            }
        }
    }

    public Color[][] calculateZBuffer(TriBoundBox[] tris){
        
        return new Color[][]{};
    }

    public Tri2D projectTri3DTo2D(Tri3D tri3D) {
        return new Tri2D(camera.project3dPointTo2dPlane(tri3D.getV0()), camera.project3dPointTo2dPlane(tri3D.getV1()),
                camera.project3dPointTo2dPlane(tri3D.getV2()));
    }

    public void testRenderTri(Tri2D tri) {
        TriBoundBox box = calculateTriBoundBox(tri);
        Color[][] pixels = new Color[getWidth()][getHeight()];
        for (int x = box.getX(); x < box.getX() + box.getWidth(); x++) {
            for (int y = box.getY(); y < box.getY() + box.getHeight(); y++) {
                if (box.getPixels()[x - box.getX()][y - box.getY()]) {
                    pixels[x][y] = Color.RED;
                }
            }
        }
        setPixelColors(pixels);
    }

    public TriBoundBox calculateTriBoundBox(Tri2D tri) {
        //Here we are trying to find the bounding box of the tri. The bounding box consists of an X, Y, width, and
        //height value. The x and y values are the top left corner point of the tri bounding box, and the width and
        //height are the width and height of the tri bounding box on the screen.
        int boundX = 999999;
        int boundY = 999999;
        int boundXBottom = 0;
        int boundYBottom = 0;

        Point2D[] points = new Point2D[]{cameraToScreenCoordinate(tri.getV0()), cameraToScreenCoordinate(tri.getV1()),
                cameraToScreenCoordinate(tri.getV2())};
        //Loop through all points once to get the bound X
        for (Point2D p : points) {
            if (p.getX() < boundX) {
                boundX = (int) p.getX();
            }
            if (p.getY() < boundY) {
                boundY = (int) p.getY();
            }
            if (p.getX() > boundXBottom) {
                boundXBottom = (int) p.getX();
            }
            if (p.getY() > boundYBottom) {
                boundYBottom = (int) p.getY();
            }
        }

        //Calculate width and height for bound box given bound coordinates and bound bottom coordinates
        int boundWidth = Math.abs(boundXBottom - boundX);
        int boundHeight = Math.abs(boundYBottom - boundY);

        boolean[][] triPixels = new boolean[boundWidth][boundHeight];

        for (int x = 0; x < triPixels.length; x++) {
            for (int y = 0; y < triPixels[0].length; y++) {
                triPixels[x][y] = pointInTriangle(new Point2D.Double(x + boundX, y + boundY), new Tri2D(points[0], points[1], points[2]));
            }
        }

        return new TriBoundBox(triPixels, boundX, boundY);
    }

    double triangleArea(Tri2D triangle) {
        return Math.abs((triangle.getV0().getX() * (triangle.getV1().getY() - triangle.getV2().getY()) +
                triangle.getV1().getX() * (triangle.getV2().getY() - triangle.getV0().getY()) +
                triangle.getV2().getX() * (triangle.getV0().getY() - triangle.getV1().getY())) / 2.0);
    }

    public boolean pointInTriangle(Point2D pt, Tri2D triangle) {
        double area = triangleArea(triangle);
        double area1 = triangleArea(new Tri2D(pt, triangle.getV1(), triangle.getV2()));
        double area2 = triangleArea(new Tri2D(triangle.getV0(), pt, triangle.getV2()));
        double area3 = triangleArea(new Tri2D(triangle.getV0(), triangle.getV1(), pt));
        return Math.abs(area - (area1 + area2 + area3)) < 1;
    }

    public Point2D cameraToScreenCoordinate(Point2D point) {
        return new Point2D.Double(point.getX() + getWidth() / 2, -point.getY() + getHeight() / 2);
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

    class TriBoundBox {
        private boolean[][] pixels;
        private int x;
        private int y;

        public TriBoundBox(boolean[][] pixels, int x, int y) {
            this.pixels = pixels;
            this.x = x;
            this.y = y;
        }

        public boolean[][] getPixels() {
            return pixels;
        }

        public void setPixels(boolean[][] pixels) {
            this.pixels = pixels;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return this.getPixels().length;
        }

        public int getHeight() {
            return this.getPixels()[0].length;
        }
    }
}
