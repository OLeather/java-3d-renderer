import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class Renderer extends JFrame {
    private static Renderer instance = new Renderer();

    public static Renderer getInstance() {
        return instance;
    }

    private ArrayList<Object3D> objects = new ArrayList<>();

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private Color[][] pixelColors;

    private Renderer() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setVisible(true);
    }

    public void renderObjects() {

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for (int c = 0; c < pixelColors.length; c++) {
            for (int r = 0; r < pixelColors[0].length; r++) {
                if (pixelColors[r][c] == null) {
                    g.setColor(new Color(0, 0, 0));
                } else {
                    g.setColor(pixelColors[r][c]);
                }
                g.drawLine(r, c, r, c);
            }
        }
    }

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

    public Optional<Object3D> getObjectFromName(String name) {
        for (Object3D o : objects) {
            if (o.getName() == name) {
                return Optional.of(o);
            }
        }
        System.out.println("Object of name: " + name + " does not exist!");
        return Optional.empty();
    }

    public ArrayList<Object3D> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Object3D> objects) {
        this.objects = objects;
    }

    public void setPixelColors(Color[][] pixelColors) {
        this.pixelColors = pixelColors;
        repaint();
    }
}
