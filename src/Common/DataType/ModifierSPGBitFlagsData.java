/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;
import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public class ModifierSPGBitFlagsData extends Logger {

    private Byte b1, b2, b3, b4, b5, b6, b7, b8;

    public Byte getB1() {
        return b1;
    }

    public void setB1(Byte b1) {
        this.b1 = b1;
    }

    public Byte getB2() {
        return b2;
    }

    public void setB2(Byte b2) {
        this.b2 = b2;
    }

    public Byte getB3() {
        return b3;
    }

    public void setB3(Byte b3) {
        this.b3 = b3;
    }

    public Byte getB4() {
        return b4;
    }

    public void setB4(Byte b4) {
        this.b4 = b4;
    }

    public Byte getB5() {
        return b5;
    }

    public void setB5(Byte b5) {
        this.b5 = b5;
    }

    public Byte getB6() {
        return b6;
    }

    public void setB6(Byte b6) {
        this.b6 = b6;
    }

    public Byte getB7() {
        return b7;
    }

    public void setB7(Byte b7) {
        this.b7 = b7;
    }

    public Byte getB8() {
        return b8;
    }

    public void setB8(Byte b8) {
        this.b8 = b8;
    }

    public ModifierSPGBitFlagsData(Byte b1, Byte b2, Byte b3, Byte b4, Byte b5, Byte b6, Byte b7, Byte b8) {
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.b4 = b4;
        this.b5 = b5;
        this.b6 = b6;
        this.b7 = b7;
        this.b8 = b8;
    }

    public ModifierSPGBitFlagsData() {
    }

    public boolean readValues(ReaderUtil ru) {
        this.b1 = ru.readByte(false);
        this.b2 = ru.readByte(false);
        this.b3 = ru.readByte(false);
        this.b4 = ru.readByte(false);
        this.b5 = ru.readByte(false);
        this.b6 = ru.readByte(false);
        this.b7 = ru.readByte(false);
        this.b8 = ru.readByte(false);

        return (this.b1 != null && this.b2 != null && this.b3 != null
                && this.b4 != null && this.b5 != null && this.b6 != null
                && this.b7 != null && this.b8 != null);
    }

    public void writeValues(WriterUtil wu) {
        wu.writeByte(b1);
        wu.writeByte(b2);
        wu.writeByte(b3);
        wu.writeByte(b4);
        wu.writeByte(b5);
        wu.writeByte(b6);
        wu.writeByte(b7);
        wu.writeByte(b8);
    }

    public ModifierSPGBitFlagsData duplicate() {
        ModifierSPGBitFlagsData ret = new ModifierSPGBitFlagsData();
        
        ret.b1 = b1;
        ret.b2 = b2;
        ret.b3 = b3;
        ret.b4 = b4;
        ret.b5 = b5;
        ret.b6 = b6;
        ret.b7 = b7;
        ret.b8 = b8;
        
        return ret;
    }

    @Override
    public String toString() {
        return "ModifierSPGBitFlagsData: b1:" + b1 + ", b2:" + b2 + ", b3:" + b3
                + "b4: " + b4 + ", b5: " + b5 + ", b6: " + b6 + ", b7: " + b7 + ", b8: " + b8;
    }
}
