/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;

/**
 *
 * @author andra
 */
public class GeometrySpriteElem extends Logger {

    private Short verticesID1;
    private Short verticesID2;

    public Short getVerticesID1() {
        return verticesID1;
    }

    public void setVerticesID1(Short verticesID1) {
        this.verticesID1 = verticesID1;
    }

    public Short getVerticesID2() {
        return verticesID2;
    }

    public float getMultiplier() {
        if (verticesID1.equals(0xC0DE)) {
            System.out.println("getMultiplier: BUG: There is no multiplier with 0xC0DE foarmat.");
            return 0;
        }
        int f = ((verticesID2 << 16) & 0xffff0000) | (verticesID1 & 0xffff);
        return Float.intBitsToFloat(f);
    }

    public void setMultiplier(float m) {
        int ff = Float.floatToRawIntBits(m);

        verticesID1 = (short) (ff & 0xffff);
        verticesID2 = (short) ((ff >> 16) & 0xffff);
    }

    public void setVerticesID2(Short verticesID2) {
        this.verticesID2 = verticesID2;
    }

    public GeometrySpriteElem(Short verticesID1, Short verticesID2) {
        this.verticesID1 = verticesID1;
        this.verticesID2 = verticesID2;
    }

    public static void test() {
        GeometrySpriteElem gse = new GeometrySpriteElem((short) 0, (short) 0);
        gse.setMultiplier(1.0f);
        System.out.format("1:%x\n", gse.getVerticesID1());
        System.out.format("2:%x\n", gse.getVerticesID2());
        gse.setVerticesID1((short) 5);
        System.out.format("1s:%x\n", gse.getVerticesID1());
        System.out.format("2s:%x\n", gse.getVerticesID2());
        System.out.println("mult:" + gse.getMultiplier());
    }

    @Override
    public String toString() {
        if (verticesID1.equals(0xC0DE)) { // vertices input: Vector3 + multiplier (4 x float)
            return String.format("Sprite Elem: type: 0xC0DE: (0x%x) verticesId=%d (0x%x)", verticesID1, verticesID2, verticesID2);
        } else { // vertices input: Vector3 (3 x float)
            return String.format("Sprite Elem: verticesId1=%d (0x%x) verticesId1=%d (0x%x)", verticesID1, verticesID1, verticesID2, verticesID2);
        }
    }
}
