import org.ejml.simple.SimpleMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Optional;

public class Renderer extends JFrame {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
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

    /**
     * Calculates the zbuffer and renders the pixel colors given correct depth.
     *
     * @param tris
     * @param triColors
     * @return
     */
    public Color[][] calculateZBuffer(RenderTri[] tris, Color[] triColors) {
        //Initialize pixels array
        Color[][] pixels = new Color[getWidth()][getHeight()];
        //Initialize array as all black pixels
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                pixels[x][y] = new Color(0, 0, 0);
            }
        }
        //Initialize distances array, used for zbuffer calculation.
        double[][] distances = new double[getWidth()][getHeight()];

        int i = 0;
        //Loop through all render tris
        for (RenderTri renderTri : tris) {
            //Get the bound box of the render tri
            TriBoundBox box = renderTri.getBoundBox();

            //Get the camera-relative 3D triangle
            Tri3D cameraRelativeTri = new Tri3D(
                    camera.calculateCameraRelativePoint(renderTri.getTri3D().getV0()),
                    camera.calculateCameraRelativePoint(renderTri.getTri3D().getV1()),
                    camera.calculateCameraRelativePoint(renderTri.getTri3D().getV2())
            );

            //Compute the shade value given the camera-relative tri normal vector skew angles. This is a very rough way
            //of computing shading, but doesn't look too bad.
            Point3D normal = cameraRelativeTri.getPlaneNormalVector();
            double xSkew = 1 - Math.abs(Math.atan(normal.getX()));
            double ySkew = 1 - Math.abs(Math.atan(normal.getY()));
            double shadeValue = (xSkew + ySkew) / 2;

            //Caps the shade value between 0 and 1
            shadeValue = Math.min(1, Math.max(0, shadeValue));

            //Loops through all pixels
            for (int x = box.getX(); x < box.getX() + box.getWidth(); x++) {
                for (int y = box.getY(); y < box.getY() + box.getHeight(); y++) {
                    //Checks to see if pixel is within triangle and therefore should be rendered
                    if (box.getPixels()[x - box.getX()][y - box.getY()]) {
                        //Checks to make sure pixel is actually on the screen. Otherwise, don't render it.
                        if (x > 0 && x < WIDTH && y > 0 && y < HEIGHT) {
                            //Get the distance to the triangle
                            double distance =
                                    camera.getScreenDistanceToPlane(screenToCameraCoordinate(new Point2D.Double(x, y)),
                                            cameraRelativeTri);

                            //Debug color calculations. This will return a black and white color shaded based on it's
                            //depth to the camera. Used to debug zbuffer calculations. The darker the color, the
                            //further from the camera.
                            double debugMaxDistance = 100;
                            int v = (int) Math.min(Math.max(255 - (distance / debugMaxDistance * 255.0), 0), 255);
                            Color debugColor = new Color(v, v, v);

                            Color color = new Color((int) (triColors[i].getRed() * shadeValue),
                                    (int) (triColors[i].getBlue() * shadeValue),
                                    (int) (triColors[i].getGreen() * shadeValue));

                            //If no other pixel has been drawn here yet, draw the pixel. If a pixel has been drawn
                            // already, only draw the pixel if it is closer to the camera than the previously drawn
                            // pixel, therefore drawing it on top.
                            if (pixels[x][y].equals(new Color(0, 0, 0)) || distance < distances[x][y]) {
                                //Draw the pixel color
                                pixels[x][y] = color;
                                //Update the distance
                                distances[x][y] = distance;
                            }
                        }
                    }
                }
            }
            //Iterate loop counter
            i++;
        }

        //Return rendered pixels
        return pixels;
    }

    /**
     * Projects a 3D triangle onto a 2D camera plane.
     * <p>
     * This loops through the points of the 3D triangle and draws them onto a 2D plane.
     *
     * @param tri3D the input {@link Tri3D}.
     * @return the projected {@link Tri2D}.
     */
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
        //Loop through all points
        for (Point2D p : points) {
            //If x value is less than current bound x, update bound x to the current x value.
            if (p.getX() < boundX) {
                boundX = (int) p.getX();
            }
            //If y value is less than the current bound y, update bound y to the current y value.
            if (p.getY() < boundY) {
                boundY = (int) p.getY();
            }
            //If x value is greater than the current boundXBottom, update boundXBottom to the current x value.
            if (p.getX() > boundXBottom) {
                boundXBottom = (int) p.getX();
            }
            //If y value is greater than the current boundYBottom, update boundYBottom to the current y value.
            if (p.getY() > boundYBottom) {
                boundYBottom = (int) p.getY();
            }
        }

        //Initialize tri pixels array.
        boolean[][] triPixels = new boolean[0][0];

        //Calculate the width and height of the bound box.
        int boundWidth = Math.abs(boundXBottom - boundX);
        int boundHeight = Math.abs(boundYBottom - boundY);

        //Determine whether the triangle should be drawn. This checks that the bound box is on the screen and also
        // that it is within a certain size threshold.
        if (!(boundX > WIDTH || boundY > HEIGHT) && !(boundX + boundWidth < 0 || boundY + boundHeight < 0) &&
                boundWidth < 5000 && boundHeight < 5000) {

            //Initialzie new tri pixels array given the new bound width and height
            triPixels = new boolean[boundWidth][boundHeight];

            //Loop through all x and y values of the pixels
            for (int x = 0; x < triPixels.length; x++) {
                for (int y = 0; y < triPixels[0].length; y++) {
                    //Set the pixel to either true or false depending on whether the pixel is within the triangle.
                    triPixels[x][y] = pointInTriangle(new Point2D.Double(x + boundX, y + boundY),
                            new Tri2D(points[0], points[1], points[2]));
                }
            }
        }

        //Return the triangle bound box.
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

    /**
     * Gets the camera coordinates with (0,0) in the middle from a camera coordinate with (0,0) at the top left
     * of the screen.
     *
     * @param point
     * @return
     */
    public Point2D screenToCameraCoordinate(Point2D point) {
        return new Point2D.Double(point.getX() - getWidth() / 2, -(point.getY() - getHeight() / 2));
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
        //Apply y, x, and z rotation matrix respectively
        outputPoint = Point3D.applyMatrix(point, rotYMatrix);
        outputPoint = Point3D.applyMatrix(outputPoint, rotXMatrix);
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

    class RenderPanel extends JPanel {

        private Color[][] pixels = new Color[][]{};

        public void setPixels(Color[][] pixels) {
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
