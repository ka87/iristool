/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.GUI;

import Common.Convert;
import java.awt.Color;

/**
 *
 * @author andra
 */
public class GUIDefines {

    public static Color COLOR_GAO_HAS_GEOMETRY = Color.RED;
    public static Color COLOR_GAO_HAS_ANIMATION = Color.BLUE;
    public static Color COLOR_GAO_HAS_EVENTS = Color.ORANGE;
    public static Color COLOR_GAO_HAS_PAG = new Color(0x663399); // purple    

    public static int COLOR_COLMAP_RENDER = 0xffff0000;      // blue
    public static int COLOR_COLINSTANCE_RENDER = 0xff00ff00; // green
    public static int COLOR_LIGHTSOURCE_RENDER = 0xffffffff; // white
    public static int COLOR_CAMERA_RENDER = 0xff0000ff;      // red
    public static int COLOR_BOUNDINGBOX_RENDER = Convert.BGRToRGB(Color.ORANGE.getRGB()); // orange

    public static final float RENDERER_X_AXIS_PREROTATION = (float) Math.toRadians(90.0f);
    public static final float RENDERER_Y_AXIS_PREROTATION = (float) Math.toRadians(180.0f);
    public static final float RENDERER_Z_AXIS_PREROTATION = 0.0f;
}
