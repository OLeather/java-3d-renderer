import com.mokiat.data.front.parser.IOBJParser;
import com.mokiat.data.front.parser.OBJFace;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJParser;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Object3D {
    private Tri3D[] tris;
    private String name;
    private Color color;
    private Point3D position = new Point3D(0, 0, 0);
    private Point3D rotation = new Point3D(0, 0, 0);
    private Point3D scale = new Point3D(1, 1, 1);

    public Object3D(Tri3D[] tris, String name, Color color) {
        this.tris = tris;
        this.name = name;
        this.color = color;
    }

    /**
     * Generates an {@link Object3D} from an OBJ file. Uses the library "java-data-front" to parse OBJ data.
     * <p>
     * https://github.com/mokiat/java-data-front
     *
     * @param filePath
     * @param name
     * @param color
     * @return
     */
    public static Object3D fromOBJFile(String filePath, String name, Color color) {
        ArrayList<Tri3D> tris = new ArrayList<>();

        //Create OBJParser and OBJModel
        IOBJParser parser = new OBJParser();
        OBJModel model = null;
        //Try parsing the OBJ file with the given path
        try {
            model = parser.parse(new FileInputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //For each face (triangle), get the 3 vertices and add them to the triangle list
        for (OBJFace face : model.getObjects().get(0).getMeshes().get(0).getFaces()) {
            Point3D v0 = new Point3D(model.getVertex(face.getReferences().get(0)).x,
                    model.getVertex(face.getReferences().get(0)).y, model.getVertex(face.getReferences().get(0)).z);
            Point3D v1 = new Point3D(model.getVertex(face.getReferences().get(1)).x,
                    model.getVertex(face.getReferences().get(1)).y, model.getVertex(face.getReferences().get(1)).z);
            Point3D v2 = new Point3D(model.getVertex(face.getReferences().get(2)).x,
                    model.getVertex(face.getReferences().get(2)).y, model.getVertex(face.getReferences().get(2)).z);
            tris.add(new Tri3D(v0, v1, v2));
        }

        return new Object3D(tris.toArray(new Tri3D[]{}), name, color);
    }

    /**
     * Returns the triangles with the object position, rotation, and scale applied to it
     *
     * @return the triangles with the object position, rotation, and scale applied to it
     */
    public Tri3D[] getPositionedTris() {
        Tri3D[] positionedTris = new Tri3D[tris.length];
        //Loop through all the tris
        for (int i = 0; i < positionedTris.length; i++) {
            //Create temporary tri point array to make iterating them easier
            Point3D[] triPoints = new Point3D[]{tris[i].getV0(), tris[i].getV1(), tris[i].getV2()};
            //Iterate through points of tri
            for (int j = 0; j < triPoints.length; j++) {
                //Apply rotation. Rotation is applied first because it needs to be rotated around it's local origin
                //before positioned in world space.
                triPoints[j] = Renderer.getInstance().apply3DRotationMatrix(triPoints[j], rotation);
                //Apply position
                triPoints[j] = Renderer.getInstance().getPointRelativeToPosition(triPoints[j], position);
                //Apply scale
                triPoints[j] = triPoints[j].times(scale);
            }
            positionedTris[i] = new Tri3D(triPoints[0], triPoints[1], triPoints[2]);
        }
        return positionedTris;
    }

    public Tri3D[] getTris() {
        return tris;
    }

    public void setTris(Tri3D[] tris) {
        this.tris = tris;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public Point3D getRotationRads() {
        return rotation;
    }

    public void setRotationRads(Point3D rotation) {
        this.rotation = rotation;
    }

    public void setScale(Point3D scale) {
        this.scale = scale;
    }

    public void setRotationDegrees(Point3D rotation) {
        this.rotation = new Point3D(Math.toRadians(rotation.getX()), Math.toRadians(rotation.getY()),
                Math.toRadians(rotation.getZ()));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
