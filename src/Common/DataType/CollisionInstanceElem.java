/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.IrisToolDefines;
import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public class CollisionInstanceElem {

    private Byte flags;
    private Byte type;
    private Byte ub2;
    private Byte ub3;
    private Byte ubnb1;
    private Byte ubnb2;
    private Integer uLen;
    private String us1;
    private Vector3 pos1;
    private Vector3 pos2;
    private Float f1, f2;

    public CollisionInstanceElem() {
    }

    public Byte getUbnb1() {
        return ubnb1;
    }

    public void setUbnb1(Byte ubnb1) {
        this.ubnb1 = ubnb1;
    }

    public Integer getuLen() {
        return uLen;
    }

    public void setuLen(Integer uLen) {
        this.uLen = uLen;
    }

    public String getUs1() {
        return us1;
    }

    public void setUs1(String us1) {
        this.us1 = us1;
    }

    public Vector3 getPos1() {
        return pos1;
    }

    public void setPos1(Vector3 pos1) {
        this.pos1 = pos1;
    }

    public Vector3 getPos2() {
        return pos2;
    }

    public void setPos2(Vector3 pos2) {
        this.pos2 = pos2;
    }

    public Float getF1() {
        return f1;
    }

    public void setF1(Float f1) {
        this.f1 = f1;
    }

    public Float getF2() {
        return f2;
    }

    public void setF2(Float f2) {
        this.f2 = f2;
    }

    public Byte getFlags() {
        return flags;
    }

    public Byte getType() {
        return type;
    }

    public Byte getUb2() {
        return ub2;
    }

    public Byte getUb3() {
        return ub3;
    }

    public Byte getUbnb2() {
        return ubnb2;
    }

    public void setUbnb2(Byte ubnb2) {
        this.ubnb2 = ubnb2;
    }

    public boolean readValues(ReaderUtil ru) {

        flags = ru.readByte(false);
        type = ru.readByte(false);
        ub2 = ru.readByte(false);
        ub3 = ru.readByte(false);

        return (flags != null && type != null && ub2 != null && ub3 != null);
    }

    public void writeValues(WriterUtil wu) {
        wu.writeByte(flags);
        wu.writeByte(type);
        wu.writeByte(ub2);
        wu.writeByte(ub3);
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            return String.format("CollisionInstanceElem: flags: %d (%x), type: %d (%x, %s), ub2: %d (%x), ub3: %d (%x)",
                    flags, flags, type, type, CollisionInstanceElem.getTypeDescription(type), ub2, ub2, ub3, ub3);
        } else {
            return "CollisionInstanceElem";
        }
    }

    public static String getTypeDescription(int t) {
        switch (t) {
            case 1:
                return "box";
            case 2:
                return "sphere";
            case 3:
                return "cylinder";
            default:
                return "unknown";
        }
    }

}
