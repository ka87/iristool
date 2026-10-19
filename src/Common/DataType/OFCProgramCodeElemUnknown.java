/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;
import Common.ReaderUtil;

/**
 *
 * @author andras
 */
public class OFCProgramCodeElemUnknown extends Logger {

    private Integer unk1;
    private Short unks1;
    private Byte unkb1, unkb2;

    public Integer getUnk1() {
        return unk1;
    }

    public void setUnk1(Integer unk1) {
        this.unk1 = unk1;
    }

    public Short getUnks1() {
        return unks1;
    }

    public void setUnks1(Short unks1) {
        this.unks1 = unks1;
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

    public OFCProgramCodeElemUnknown(Integer unk1, Short unks1, Byte unkb1, Byte unkb2) {
        this.unk1 = unk1;
        this.unks1 = unks1;
        this.unkb1 = unkb1;
        this.unkb2 = unkb2;
    }

    public OFCProgramCodeElemUnknown() {
    }

    public boolean readValues(ReaderUtil ru) {
    
        this.unk1 = ru.readInt(false);
        this.unks1 = ru.readShort(false);
        this.unkb1 = ru.readByte(false);
        this.unkb2 = ru.readByte(false);

        return (this.unk1 != null && this.unks1 != null && this.unkb1 != null && this.unkb2 != null);
    }

    @Override
    public String toString() {
        return String.format("OFCProgramCodeElemUnknown: unk1=%d (%x), unks1=%d (%x), unkb1=%d (%x), unkb2=%d (%x)",
                unk1, unk1, unks1, unks1, unkb1, unkb1, unkb2, unkb2);
    }
}
