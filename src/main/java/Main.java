import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends TimerTask implements KeyListener {
    private Camera camera;
    private boolean forwardPressed = false;
    private boolean backwardPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftRotPressed = false;
    private boolean rightRotPressed = false;
    private boolean upRotPressed = false;
    private boolean downRotPressed = false;
    private double camMoveSpeed = 0.5;
    private double camRotateSpeed = 0.02;

    public static void main(String[] args) {
        Main main = new Main();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(main, 0, 20);
    }

    public Main() {
        init();
    }

    public void init() {
        this.camera = new Camera(Renderer.WIDTH, Renderer.HEIGHT, 60);

        Renderer.getInstance().setCamera(camera);
        Renderer.getInstance().addKeyListener(this);

        camera.setCamPosition(0, 0, 0);
        camera.setCamRotationDegrees(0, 0, 0);

        Object3D object3D =
                new Object3D(new Tri3D[]{new Tri3D(new Point3D(0, 0, 0), new Point3D(5, 5, 0), new Point3D(0, 5, 0))},
                        "Test OBJ", Color.red);

        Object3D object3D1 =
                new Object3D(new Tri3D[]{new Tri3D(new Point3D(0, 0, 0), new Point3D(5, 5, 0), new Point3D(0, 5, 0))},
                        "Test OBJ1", Color.blue);

        Object3D cubeObject = Object3D.fromOBJFile("cube.obj", "cube", Color.RED);


        object3D.setPosition(new Point3D(0, 0, 10));
        object3D.setRotationDegrees(new Point3D(45, 0, 0));

        object3D1.setPosition(new Point3D(0, 2, 20));

        cubeObject.setPosition(new Point3D(0, 0, 5));

        cubeObject.setScale(new Point3D(2, 2, 2));

//        Renderer.getInstance().addObject(object3D);
//        Renderer.getInstance().addObject(object3D1);
        Renderer.getInstance().addObject(cubeObject);

        camera.setCamRotationDegrees(new Point3D(0, 15, 0));
        Renderer.getInstance().renderObjects();
    }

    @Override
    public void run() {
        if (forwardPressed) {
            camera.setCamPosition(camera.getPosition().plus(Renderer.getInstance()
                    .apply3DRotationMatrix(new Point3D(0, 0, -camMoveSpeed),
                            new Point3D(0, camera.getRotation().getY(), Math.PI))));
        }
        if (backwardPressed) {
            camera.setCamPosition(camera.getPosition().plus(Renderer.getInstance()
                    .apply3DRotationMatrix(new Point3D(0, 0, camMoveSpeed),
                            new Point3D(0, camera.getRotation().getY(), Math.PI))));
        }
        if (leftPressed) {
            camera.setCamPosition(camera.getPosition().plus(Renderer.getInstance()
                    .apply3DRotationMatrix(new Point3D(-camMoveSpeed, 0, 0),
                            new Point3D(0, camera.getRotation().getY(), Math.PI))));
        }
        if (rightPressed) {
            camera.setCamPosition(camera.getPosition().plus(Renderer.getInstance()
                    .apply3DRotationMatrix(new Point3D(camMoveSpeed, 0, 0),
                            new Point3D(0, camera.getRotation().getY(), Math.PI))));
        }

        if (upPressed) {
            camera.setCamPosition(camera.getPosition().plus(new Point3D(0, -camMoveSpeed, 0)));
        }
        if (downPressed) {
            camera.setCamPosition(camera.getPosition().plus(new Point3D(0, camMoveSpeed, 0)));
        }

        if (leftRotPressed) {
            camera.setCamRotationRads(camera.getRotation().plus(new Point3D(0, camRotateSpeed, 0)));
        }
        if (rightRotPressed) {
            camera.setCamRotationRads(camera.getRotation().plus(new Point3D(0, -camRotateSpeed, 0)));
        }
        if (upRotPressed) {
            camera.setCamRotationRads(camera.getRotation().plus(new Point3D(camRotateSpeed, 0, 0)));
        }
        if (downRotPressed) {
            camera.setCamRotationRads(camera.getRotation().plus(new Point3D(-camRotateSpeed, 0, 0)));
        }


        Renderer.getInstance().renderObjects();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            forwardPressed = true;
        }
        if (e.getKeyChar() == 's') {
            backwardPressed = true;
        }
        if (e.getKeyChar() == 'a') {
            leftPressed = true;
        }
        if (e.getKeyChar() == 'd') {
            rightPressed = true;
        }
        if (e.getKeyCode() == 32) {
            upPressed = true;
        }
        if (e.getKeyCode() == 16) {
            downPressed = true;
        }
        if (e.getKeyCode() == 37) {
            leftRotPressed = true;
        }
        if (e.getKeyCode() == 39) {
            rightRotPressed = true;
        }
        if (e.getKeyCode() == 38) {
            upRotPressed = true;
        }
        if (e.getKeyCode() == 40) {
            downRotPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            forwardPressed = false;
        }
        if (e.getKeyChar() == 's') {
            backwardPressed = false;
        }
        if (e.getKeyChar() == 'a') {
            leftPressed = false;
        }
        if (e.getKeyChar() == 'd') {
            rightPressed = false;
        }
        if (e.getKeyCode() == 32) {
            upPressed = false;
        }
        if (e.getKeyCode() == 16) {
            downPressed = false;
        }
        if (e.getKeyCode() == 37) {
            leftRotPressed = false;
        }
        if (e.getKeyCode() == 39) {
            rightRotPressed = false;
        }
        if (e.getKeyCode() == 38) {
            upRotPressed = false;
        }
        if (e.getKeyCode() == 40) {
            downRotPressed = false;
        }
    }
}
