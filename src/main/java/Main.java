import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Renderer.getInstance();
        Color[][] pixels = new Color[200][200];
        for(int i = 0; i < pixels.length; i++){
            for(int j = 0; j < pixels[0].length; j++){
                pixels[i][j] = new Color(255,0,0);
            }
        }
        Renderer.getInstance().setPixelColors(pixels);
        Renderer.getInstance().repaint();
    }
}
