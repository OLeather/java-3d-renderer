public class Main {
    public static void main(String[] args) {
        Camera camera = new Camera(Renderer.getInstance().getWidth(), Renderer.getInstance().getHeight(), 60);
        Renderer.getInstance().setCamera(camera);
        camera.setCamPosition(0, 0, 0);
        camera.setCamRotationDegrees(0, 0, 0);

        Object3D object3D = new Object3D(new Tri3D[]{new Tri3D(new Point3D(0, 0, 0),new Point3D(5, 5, 0),new Point3D(0, 5, 0))}, "Test OBJ");

        object3D.setPosition(new Point3D(0, 0, 50));
        object3D.setRotationDegrees(new Point3D(45, 45, 0));

        Renderer.getInstance().addObject(object3D);

        Renderer.getInstance().renderObjects();
    }
}
