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
    private double camRotateSpeed = 0.05;

    public Main() {
        init();
    }

    public static void main(String[] args) {
        Main main = new Main();
        Timer timer = new Timer();
        //Schedule Main to run at a fixed rate
        timer.scheduleAtFixedRate(main, 0, 20);
    }

    //Initializes the 3D renderer
    public void init() {
        //Create the camera
        this.camera = new Camera(Renderer.WIDTH, Renderer.HEIGHT, 60);

        //Set the camera to the renderer
        Renderer.getInstance().setCamera(camera);
        //Add the key listener to the renderer to get keyboard inputs
        Renderer.getInstance().addKeyListener(this);

        //Poisition and rotate the camera
        camera.setCamPosition(0, 0, 0);
        camera.setCamRotationDegrees(0, 0, 0);


        //File from https://people.sc.fsu.edu/~jburkardt/data/obj/cube.obj
        String cubeFile = "cube.obj";

        //File from http://web.mit.edu/djwendel/www/weblogo/shapes/basic-shapes/sphere/sphere.obj
        String sphereFile = "sphere.obj";

        //File from https://github.com/kivy/kivy/blob/master/examples/3Drendering/monkey.obj (blender monkey object)
        String monkeyFile = "monkey.obj";

        //File from https://sketchfab.com/3d-models/alduin-c1d54a5145004198ac9c45da418239c8
        String dragonFile = "alduin.obj";

        //File from https://www.cgtrader.com/items/838998/download-page
        String carFile = "Ford_GT.obj";

        //Create a series of test objects
        Object3D triObj1 =
                new Object3D(new Tri3D[]{new Tri3D(new Point3D(0, 0, 0), new Point3D(5, 5, 0), new Point3D(0, 5, 0))},
                        "Test OBJ", Color.red);
        Object3D triObj2 =
                new Object3D(new Tri3D[]{new Tri3D(new Point3D(0, 0, 0), new Point3D(5, 5, 0), new Point3D(0, 5, 0))},
                        "Test OBJ1", Color.blue);
        Object3D cubeObject = Object3D.fromOBJFile(cubeFile, "cube", Color.GREEN);
        Object3D sphereObject = Object3D.fromOBJFile(sphereFile, "sphere", Color.BLUE);
        Object3D monkeyObject = Object3D.fromOBJFile(monkeyFile, "monkey", Color.RED);
        Object3D dragonObject = Object3D.fromOBJFile(dragonFile, "dragon", Color.RED);
        Object3D carObject = Object3D.fromOBJFile(carFile, "car", Color.RED);

        //Adjust positions and rotations of test objects
        triObj1.setPosition(new Point3D(0, 0, 10));
        triObj1.setRotationDegrees(new Point3D(45, 0, 0));

        triObj2.setPosition(new Point3D(0, 0, 6));
        triObj2.setRotationDegrees(new Point3D(-45, 0, 0));

        cubeObject.setPosition(new Point3D(5, 0, 10));
        cubeObject.setScale(new Point3D(5, 5, 5));

        sphereObject.setPosition(new Point3D(0, 0, 20));

        monkeyObject.setPosition(new Point3D(0, 0, 20));
        monkeyObject.setRotationDegrees(new Point3D(180, 180, 0));

        dragonObject.setPosition(new Point3D(0, 0, 1000));
        dragonObject.setScale(new Point3D(.01, .01, .01));
        dragonObject.setRotationDegrees(new Point3D(180, 0, 0));

        carObject.setPosition(new Point3D(0, 0, 500));
        carObject.setScale(new Point3D(0.01, 0.01, 0.01));
        carObject.setRotationDegrees(new Point3D(180, 180, 0));

        //Add objects to the renderer
//        Renderer.getInstance().addObject(triObj1);
//        Renderer.getInstance().addObject(triObj2);

        Renderer.getInstance().addObject(dragonObject);

        //Initial object rendering
        Renderer.getInstance().renderObjects();
    }

    /**
     * Handles camera movement and updates the renderer.
     */
    @Override
    public void run() {
        //Move camera from key presses
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

        //Update rendereer
        Renderer.getInstance().renderObjects();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handles keyboard presses
     */
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

    /**
     * Handles keyboard releases
     */
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
