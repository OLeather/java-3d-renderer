public class Object3D {
    private Tri3D[] tris;
    private String name;
    private Point3D position;
    private Point3D rotation;

    public static Object3D rectangle(double width, double height, double length, String name) {
        Tri3D[] tris = new Tri3D[]{};
        return new Object3D(tris, name);
    }

    public Object3D(Tri3D[] tris, String name) {
        this.tris = tris;
        this.name = name;
    }

    public Tri3D[] getPositionedTris() {
        Tri3D[] positionedTris = new Tri3D[tris.length];
        for (int i = 0; i < tris.length; i++) {
            Point3D[] triPoints = new Point3D[3];
            for (int j = 0; j < triPoints.length; j++) {
                triPoints[i] = Renderer.getInstance().getPointRelativeToPosition(triPoints[i], rotation);
                triPoints[i] = Renderer.getInstance().apply3DRotationMatrix(triPoints[i], rotation);
            }
            positionedTris[i].setV0(triPoints[0]);
            positionedTris[i].setV1(triPoints[1]);
            positionedTris[i].setV2(triPoints[2]);
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

    public Point3D getRotation() {
        return rotation;
    }

    public void setRotation(Point3D rotation) {
        this.rotation = rotation;
    }
}
