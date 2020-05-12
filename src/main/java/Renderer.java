import org.ejml.simple.SimpleMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Optional;

public class Renderer extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static Renderer instance = new Renderer();
    private ArrayList<Object3D> objects = new ArrayList<>();
    private Color[][] pixelColors = new Color[WIDTH][HEIGHT];
    private Camera camera;

    private RenderPanel renderPanel;

    private Renderer() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        this.renderPanel = new RenderPanel();
        add(renderPanel);
        setVisible(true);
    }

    public static Renderer getInstance() {
        return instance;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Renders all the objects added to the {@link Renderer}.
     */
    public void renderObjects() {
        //Keep track of a list of all tris and their colors.
        ArrayList<Tri3D> tris = new ArrayList();
        ArrayList<Color> triColors = new ArrayList();
        //Loop through all tris in all objects
        for (Object3D obj : objects) {
            for (Tri3D tri : obj.getPositionedTris()) {
                tris.add(tri);
                triColors.add(obj.getColor());
            }
        }

        //Calculate all render tris
        RenderTri[] renderTris = getRenderTris(tris.toArray(new Tri3D[]{}));

        //Calculate the zbuffer and render the tris to pixels
        Color[][] renderedPixels = calculateZBuffer(renderTris, triColors.toArray(new Color[]{}));

        //Draw pixels on the screen
        setPixelColors(renderedPixels);
    }

    /**
     * Calculates all of the render tris given an array of {@link Tri3D}'s and returns it in the form of an array.
     *
     * @param tris
     * @return
     */
    public RenderTri[] getRenderTris(Tri3D[] tris) {
        RenderTri[] renderTris = new RenderTri[tris.length];
        int i = 0;
        for (Tri3D tri3D : tris) {
            renderTris[i] = calculateRenderTri(tri3D);
            i++;
        }
        return renderTris;
    }

    public Color[][] calculateZBuffer(RenderTri[] tris, Color[] triColors) {
        Color[][] pixels = new Color[getWidth()][getHeight()];
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                pixels[x][y] = new Color(0, 0, 0);
            }
        }
        double[][] distances = new double[getWidth()][getHeight()];
        int i = 0;
        for (RenderTri renderTri : tris) {
            TriBoundBox box = renderTri.getBoundBox();
            for (int x = box.getX(); x < box.getX() + box.getWidth(); x++) {
                for (int y = box.getY(); y < box.getY() + box.getHeight(); y++) {
                    if (box.getPixels()[x - box.getX()][y - box.getY()]) {
                        double distance =
                                camera.getScreenDistanceToPlane(new Point2D.Double(x, y), renderTri.getTri3D());
//                        double debugMaxDistance = 50;
//                        Color debugColor = new Color(255, 255, 255, Math.min(Math.max(255-(int)(distance/debugMaxDistance*255),0),255));
                        if (pixels[x][y].equals(new Color(0, 0, 0))) {
                            pixels[x][y] = triColors[i];
                            distances[x][y] = distance;
                        } else {
                            if (distance < distances[x][y]) {
                                pixels[x][y] = triColors[i];
                                distances[x][y] = distance;
                            }
                        }
                    }
                }
            }
            i++;
        }
        return pixels;
    }

    public Tri2D projectTri3DTo2D(Tri3D tri3D) {
        return new Tri2D(camera.project3dPointToCameraPlane(tri3D.getV0()),
                camera.project3dPointToCameraPlane(tri3D.getV1()),
                camera.project3dPointToCameraPlane(tri3D.getV2()));
    }

    /**
     * Test function used to render a single Tri3D without zbuffer calculation.
     *
     * @param tri3D
     */
    public void testRenderTri(Tri3D tri3D) {
        TriBoundBox box = calculateRenderTri(tri3D).getBoundBox();
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

    /**
     * Creates the render tri given a {@link Tri3D}.
     *
     * @param tri3D
     * @return
     */
    public RenderTri calculateRenderTri(Tri3D tri3D) {
        Tri2D tri2D = projectTri3DTo2D(tri3D);
        TriBoundBox boundBox = calculateTriBoundBox(tri2D);
        return new RenderTri(boundBox, tri3D);
    }

    /**
     * Calculates the {@link TriBoundBox} given A {@link Tri2D}.
     *
     * @param tri2D
     * @return
     */
    public TriBoundBox calculateTriBoundBox(Tri2D tri2D) {
        //Here we are trying to find the bounding box of the tri2D. The bounding box consists of an X, Y, width, and
        //height value. The x and y values are the top left corner point of the tri2D bounding box, and the width and
        //height are the width and height of the tri2D bounding box on the screen.
        int boundX = 999999;
        int boundY = 999999;
        int boundXBottom = 0;
        int boundYBottom = 0;

        Point2D[] points =
                new Point2D[]{cameraToScreenCoordinate(tri2D.getV0()), cameraToScreenCoordinate(tri2D.getV1()),
                        cameraToScreenCoordinate(tri2D.getV2())};
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
                triPixels[x][y] = pointInTriangle(new Point2D.Double(x + boundX, y + boundY),
                        new Tri2D(points[0], points[1], points[2]));
            }
        }

        return new TriBoundBox(triPixels, boundX, boundY);
    }

    /**
     * Returns whether or not a point is within a triangle.
     * <p>
     * https://www.geeksforgeeks.org/check-whether-a-given-point-lies-inside-a-triangle-or-not/
     *
     * @param pt
     * @param triangle
     * @return
     */
    public boolean pointInTriangle(Point2D pt, Tri2D triangle) {
        double area = triangle.area();
        double area1 = new Tri2D(pt, triangle.getV1(), triangle.getV2()).area();
        double area2 = new Tri2D(triangle.getV0(), pt, triangle.getV2()).area();
        double area3 = new Tri2D(triangle.getV0(), triangle.getV1(), pt).area();
        return Math.abs(area - (area1 + area2 + area3)) < 1;
    }

    /**
     * Gets the screen coordinates with (0,0) at the top left corner from a camera coordinate with (0,0) in the middle
     * of the screen.
     *
     * @param point
     * @return
     */
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

    /**
     * Adds an {@link Object3D} to the renderer.
     *
     * @param object
     */
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

    /**
     * Returns an object from it's name
     *
     * @param name
     * @return
     */
    public Optional<Object3D> getObjectFromName(String name) {
        for (Object3D o : objects) {
            if (o.getName() == name) {
                return Optional.of(o);
            }
        }
        System.out.println("Object of name: " + name + " does not exist!");
        return Optional.empty();
    }

    /**
     * Returns all objects rendered
     *
     * @return
     */
    public ArrayList<Object3D> getObjects() {
        return objects;
    }

    /**
     * Sets all objects rendered
     *
     * @param objects
     */
    public void setObjects(ArrayList<Object3D> objects) {
        this.objects = objects;
    }

    /**
     * Sets the 2d array of colors rendered onto the screen.
     *
     * @param pixelColors
     */
    public void setPixelColors(Color[][] pixelColors) {
        this.pixelColors = pixelColors;
        renderPanel.setPixels(pixelColors);
        SwingUtilities.invokeLater(() -> renderPanel.repaint());
    }

    class RenderTri {
        TriBoundBox boundBox;
        private Tri3D tri3D;

        /**
         * Constructs a {@link RenderTri}, containing information about how to render the tri including its 2D bound
         * box and its 3D tri used for zbuffer calculation.
         *
         * @param boundBox
         * @param tri3D
         */
        public RenderTri(TriBoundBox boundBox, Tri3D tri3D) {
            this.boundBox = boundBox;
            this.tri3D = tri3D;
        }

        public TriBoundBox getBoundBox() {
            return boundBox;
        }

        public void setBoundBox(TriBoundBox boundBox) {
            this.boundBox = boundBox;
        }

        public Tri3D getTri3D() {
            return tri3D;
        }

        public void setTri3D(Tri3D tri3D) {
            this.tri3D = tri3D;
        }
    }

    class TriBoundBox {
        private boolean[][] pixels;
        private int x;
        private int y;

        /**
         * Constructs a {@link TriBoundBox}, which contains the bound box and pixel coordinates to render a {@link Tri2D}.
         *
         * @param pixels
         * @param x
         * @param y
         */
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

    class RenderPanel extends JPanel{

        private Color[][] pixels = new Color[][]{};

        public void setPixels(Color[][] pixels){
            this.pixels = pixels;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            for (int c = 0; c < pixels.length; c++) {
                for (int r = 0; r < pixels[0].length; r++) {
                    if (pixels[r][c] == null) {
                        g.setColor(new Color(0, 0, 0));
                    } else {
                        g.setColor(pixels[r][c]);
                    }
                    g.drawLine(r, c, r, c);
                }
            }
        }
    }
}
