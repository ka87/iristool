/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

/**
 *
 * @author andras
 */
public class Semantic {

    public static class Attr {

        public static final int POSITION = 0;
        public static final int NORMAL = 1;
        public static final int COLORVERTEXPAINT = 3;
        public static final int COLORREALTIMENORMAL = 4;
        public static final int COLORREALTIMESPECULAR = 5;
        public static final int COLORFINAL = 6;
        public static final int TEXCOORD = 7;
        public static final int DRAW_ID = 8;
    }

    public static class Frag {

        public static final int COLOR = 0;
        public static final int RED = 0;
        public static final int GREEN = 1;
        public static final int BLUE = 2;
        public static final int ALPHA = 0;
    }

    public static class Uniform {

        public static final int MATERIAL = 0;
        public static final int TRANSFORM0 = 1;
        public static final int TRANSFORM1 = 2;
        public static final int INDIRECTION = 3;
        public static final int CONSTANT = 0;
        public static final int PER_FRAME = 1;
        public static final int PER_PASS = 2;
        public static final int LIGHT = 3;
        public static final int TEXTURE0 = 0;
    }

    public static class Object {

        public static final int VAO1 = 0;
        public static final int VAO2 = 1;
        public static final int VBO1 = 2;
        public static final int VBO2 = 3;
        public static final int IBO1 = 4;
        public static final int IBO2 = 5;
        public static final int SAMPLER = 6;
        public static final int SIZE = 7;
    }

    public static class Image {

        public static final int DIFFUSE = 0;
        public static final int PICKING = 1;
    }
    
    public static class Sampler {

        public static final int DIFFUSE = 0;
        public static final int POSITION = 4;
        public static final int TEXCOORD = 5;
        public static final int COLOR = 6;
    }
}