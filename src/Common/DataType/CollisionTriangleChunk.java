/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public class CollisionTriangleChunk {

    private Short triangleElemNum;
    private Byte unkb1, unkb2;
    private Integer gmatID;

    private CollisionTriangleChunkElem ctce[];

    public CollisionTriangleChunk() {
    }

    public CollisionTriangleChunk(Short triangleElemNum, Byte unkb1, Byte unkb2, Integer gmatID) {
        this.triangleElemNum = triangleElemNum;
        this.unkb1 = unkb1;
        this.unkb2 = unkb2;
        this.gmatID = gmatID;

        if (this.triangleElemNum > 0) {
            this.ctce = new CollisionTriangleChunkElem[triangleElemNum];
        }
    }

    public Short getTriangleElemNum() {
        return triangleElemNum;
    }

    public void setTriangleElemNum(Short triangleElemNum) {
        this.triangleElemNum = triangleElemNum;

        if (this.triangleElemNum > 0) {
            this.ctce = new CollisionTriangleChunkElem[triangleElemNum];
        } else {
            this.ctce = null;
        }
    }

    public CollisionTriangleChunkElem[] getCtce() {
        return ctce;
    }

    public Byte getUnkb1() {
        return unkb1;
    }

    public void setUnkb1(Byte unkb1) {
        this.unkb1 = unkb1;
    }

    public Byte getUnkb2() {
        return unkb2;
    }

    public void setUnkb2(Byte unkb2) {
        this.unkb2 = unkb2;
    }

    public Integer getGmatID() {
        return gmatID;
    }

    public void setGmatID(Integer gmatID) {
        this.gmatID = gmatID;
    }

    public boolean readValues(ReaderUtil ru) {

        triangleElemNum = ru.readShort(false);
        unkb1 = ru.readByte(false);
        unkb2 = ru.readByte(false);
        gmatID = ru.readInt(false);

        boolean b = (triangleElemNum != null && unkb1 != null && unkb2 != null
                && gmatID != null);
        this.ctce = null;
        if (b && this.triangleElemNum > 0) {
            this.ctce = new CollisionTriangleChunkElem[triangleElemNum];
        }
        return b;
    }

    public void writeValues(WriterUtil wu) {
        wu.writeShort(triangleElemNum);
        wu.writeByte(unkb1);
        wu.writeByte(unkb2);
        wu.writeInt(gmatID);
    }

    @Override
    public String toString() {
        return String.format("CollisionTriangleChunk: triangleElemNum: %d (%x), unkb1: %d (%x), unkb2: %d (%x), gmat: %d (%x)",
                triangleElemNum, triangleElemNum, unkb1, unkb1, unkb2, unkb2, gmatID, gmatID);
    }
}
