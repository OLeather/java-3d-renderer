public class Main {
    public static void main(String[] args) {
        Camera camera = new Camera(Renderer.getInstance().getWidth(), Renderer.getInstance().getHeight(), 60);
        Renderer.getInstance().setCamera(camera);
        camera.setCamPosition(0, 0, 0);
        camera.setCamRotationDegrees(0, 0, 0);
        Tri3D tri3D = new Tri3D(new Point3D(0, 0, 50),new Point3D(5, 5, 50),new Point3D(0, 5, 50));
        Tri2D tri2D = Renderer.getInstance().projectTri3DTo2D(tri3D);
        Renderer.getInstance().testRenderTri(tri2D);
    }
}
