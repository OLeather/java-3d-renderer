import com.mokiat.data.front.error.WFException;
import com.mokiat.data.front.parser.IOBJParser;
import com.mokiat.data.front.parser.OBJFace;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJParser;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;

public class Object3D {
    private Tri3D[] tris;
    private String name;
    private Color color;
    private Point3D position = new Point3D(0, 0, 0);
    private Point3D rotation = new Point3D(0, 0, 0);
    private Point3D scale = new Point3D(0, 0, 0);

    public Object3D(Tri3D[] tris, String name, Color color) {
        this.tris = tris;
        this.name = name;
        this.color = color;
    }

    public static Object3D rectangle(double width, double height, double length, String name, Color color) {
        Tri3D[] tris = new Tri3D[]{};
        return new Object3D(tris, name, color);
    }

    public static Object3D fromOBJFile(String filePath, String name, Color color) {
        ArrayList<Tri3D> tris = new ArrayList<>();
        // Open a stream to your OBJ resource
        try (InputStream in = new FileInputStream(filePath)) {
            // Create an OBJParser and parse the resource
            final IOBJParser parser = new OBJParser();
            final OBJModel model = parser.parse(in);

            // Use the model representation to get some basic info
            System.out.println(MessageFormat.format(
                    "OBJ model has {0} vertices, {1} normals, {2} texture coordinates, and {3} objects.",
                    model.getVertices().size(),
                    model.getNormals().size(),
                    model.getTexCoords().size(),
                    model.getObjects().size()));
            for (OBJFace face : model.getObjects().get(0).getMeshes().get(0).getFaces()) {
//                System.out.println("Face: {");
//                for (OBJDataReference ref : face.getReferences()) {
//                    OBJVertex vert = model.getVertex(ref);
//                    System.out.println(vert.x + " " + vert.y + " " + vert.z);
//                }
//                System.out.println("}");
                Point3D v0 = new Point3D(model.getVertex(face.getReferences().get(0)).x,
                        model.getVertex(face.getReferences().get(0)).y, model.getVertex(face.getReferences().get(0)).z);
                Point3D v1 = new Point3D(model.getVertex(face.getReferences().get(1)).x,
                        model.getVertex(face.getReferences().get(1)).y, model.getVertex(face.getReferences().get(1)).z);
                Point3D v2 = new Point3D(model.getVertex(face.getReferences().get(2)).x,
                        model.getVertex(face.getReferences().get(2)).y, model.getVertex(face.getReferences().get(2)).z);
                tris.add(new Tri3D(v0, v1, v2));
            }
        } catch (WFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Object3D(tris.toArray(new Tri3D[]{}), name, color);
    }

    public Tri3D[] getPositionedTris() {
        Tri3D[] positionedTris = new Tri3D[tris.length];
        for (int i = 0; i < positionedTris.length; i++) {
            Point3D[] triPoints = new Point3D[]{tris[i].getV0(), tris[i].getV1(), tris[i].getV2()};
            for (int j = 0; j < triPoints.length; j++) {
                triPoints[j] = Renderer.getInstance().apply3DRotationMatrix(triPoints[j], rotation);
                triPoints[j] = Renderer.getInstance().getPointRelativeToPosition(triPoints[j], position);
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
