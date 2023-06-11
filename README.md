# 3D Renderer

Ever since I started to learn to code, I always had an ambitious dream to program a 3D game from scratch. After 11 years of coding, I finally achieved that goal. This was my first attempt at a 3D renderer I made in java while in high school. 

## Z-buffer
![Z-buffer](https://github.com/OLeather/java-3d-renderer/blob/master/images/Screenshot%202023-06-11%20130716.png)

The core of the renderer is the z-buffer algorithm. The z-buffer calculates the depth of each pixel based on a vector intersection with each object in the scene. It then renders the closest pixel on top, allowing intersecting and overlapping triangles. The image above demonstrates the z-buffer with two overlapping triangles, a classic test in 3D rendering.

[src/main/java/Renderer.java#L87-L144](https://github.com/OLeather/java-3d-renderer/blob/844c8ce4de1ab325c365b6580da7d9269730e149/src/main/java/Renderer.java#L87-L144)
``` java
/**
 * Calculates the z-buffer and renders the pixel colors given correct depth.
 *
 * The z-buffer is a concept in computer graphics that deals with occluding objects. Although it is a widely
 * documented concept, this is my individual solution to the problem and this specific algorithm code was developed
 * individually by myself.
 *
 * @param tris      Array of {@link RenderTri}s to render onto the screen
 * @param triColors Array of colors for each tri
 * @return 2d array of rendered pixel colors
 */
public Color[][] calculateZBuffer(RenderTri[] tris, Color[] triColors) {
    //Initialize pixels array
    Color[][] pixels = new Color[getWidth()][getHeight()];
    //Initialize array as all black pixels
    for (int x = 0; x < pixels.length; x++) {
        for (int y = 0; y < pixels[0].length; y++) {
            pixels[x][y] = new Color(0, 0, 0);
        }
    }
    //Initialize distances array, used for zbuffer calculation.
    double[][] distances = new double[getWidth()][getHeight()];


    //Loop through all render tris
    for (int i = 0; i < tris.length; i++) {
        //Get current render tri
        RenderTri renderTri = tris[i];
        //Get the bound box of the render tri
        TriBoundBox box = renderTri.getBoundBox();


        //Get the camera-relative 3D triangle
        Tri3D cameraRelativeTri = getCameraRelativeTri3D(renderTri.getTri3D());
        //Calculate shade value
        double shadeValue = calculateShadeValue(cameraRelativeTri);


        //Loops through all pixels
        for (int x = box.getX(); x < box.getX() + box.getWidth(); x++) {
            for (int y = box.getY(); y < box.getY() + box.getHeight(); y++) {
                //Checks to see if pixel is within triangle and therefore should be rendered
                if (box.getPixels()[x - box.getX()][y - box.getY()]) {
                    //Checks to make sure pixel is actually on the screen. Otherwise, don't render it.
                    if (x > 0 && x < WIDTH && y > 0 && y < HEIGHT) {
                        //Get the distance to the triangle
                        double distance =
                                camera.getScreenDistanceToPlane(screenToCameraCoordinate(new Point2D.Double(x, y)),
                                        cameraRelativeTri);


                        Color color = new Color((int) (triColors[i].getRed() * shadeValue),
                                (int) (triColors[i].getBlue() * shadeValue),
                                (int) (triColors[i].getGreen() * shadeValue));


                        //If no other pixel has been drawn here yet, draw the pixel. If a pixel has been drawn
                        //already, only draw the pixel if it is closer to the camera than the previously drawn
                        //pixel, therefore drawing it on top.
                        if (pixels[x][y].equals(new Color(0, 0, 0)) || distance < distances[x][y]) {
                            //Draw the pixel color
                            pixels[x][y] = color;
                            //Update the distance
                            distances[x][y] = distance;
                        }
                    }
                }
            }
        }
    }


    //Return rendered pixels
    return pixels;
}
```

## Shading
![Shading](https://github.com/OLeather/java-3d-renderer/blob/master/images/Screenshot%202023-06-11%20130812.png)

A physical lighting and shader system was out of the scope of this project, so I used an estimated shading algorithm which shades each triangle darker based on the relative angle to the camera. It achieves a shading effect that looks quite nice. 

[src/main/java/Renderer.java#L167-L179](https://github.com/OLeather/java-3d-renderer/blob/844c8ce4de1ab325c365b6580da7d9269730e149/src/main/java/Renderer.java#L167-L179)
```java
/**
 * Calculates the shading value of the input camera-relative {@link Tri3D} based on it's normal vector.
 *
 * @param cameraRelativeTri3D camera-relative {@link Tri3D}
 * @return shading value between 0 and 1. 0 means darker and 1 means lighter colors.
 */
private double calculateShadeValue(Tri3D cameraRelativeTri3D) {
    //Compute the shade value given the camera-relative tri normal vector skew angles. This is a very rough way
    //of computing shading, but doesn't look too bad.
    Point3D normal = cameraRelativeTri3D.getPlaneNormalVector();
    double xSkew = 1 - Math.abs(Math.atan(normal.getX()));
    double ySkew = 1 - Math.abs(Math.atan(normal.getY()));
    double shadeValue = (xSkew + ySkew) / 2;


    //Caps the shade value between 0 and 1
    shadeValue = Math.min(1, Math.max(0, shadeValue));


    return shadeValue;
}
```

## Model Rendering
The basis of the renderer renders triangles. Since most 3D object file formats are represented in triangles, the renderer is able to render full complex 3D objects. Below are a few of the complex models (200,000+ tris) in the renderer.

![Dragon](https://github.com/OLeather/java-3d-renderer/blob/master/images/Screenshot%202023-06-11%20130850.png)
![Dragon1](https://github.com/OLeather/java-3d-renderer/blob/master/images/Screenshot%202023-06-11%20130904.png)
![Dragon2](https://github.com/OLeather/java-3d-renderer/blob/master/images/Screenshot%202023-06-11%20130915.png)
![Car](https://github.com/OLeather/java-3d-renderer/blob/master/images/Screenshot%202023-06-11%20131035.png)
![Car1](https://github.com/OLeather/java-3d-renderer/blob/master/images/Screenshot%202023-06-11%20131050.png)
