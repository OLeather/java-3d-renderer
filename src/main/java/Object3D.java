public class Object3D {
    private Tri3D[] tris;
    private String name;
    private Point3D position = new Point3D(0, 0, 0);
    private Point3D rotation = new Point3D(0, 0, 0);

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
        for (int i = 0; i < positionedTris.length; i++) {
            Point3D[] triPoints = new Point3D[]{tris[i].getV0(),tris[i].getV1(),tris[i].getV2()};
            for (int j = 0; j < triPoints.length; j++) {
                triPoints[j] = Renderer.getInstance().apply3DRotationMatrix(triPoints[j], rotation);
                triPoints[j] = Renderer.getInstance().getPointRelativeToPosition(triPoints[j], position);
            }
            positionedTris[i] = new Tri3D(triPoints[0],triPoints[1],triPoints[2]);
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

    public void setRotationDegrees(Point3D rotation) {
        this.rotation = new Point3D(Math.toRadians(rotation.getX()),Math.toRadians(rotation.getY()),Math.toRadians(rotation.getZ()));
    }
}
