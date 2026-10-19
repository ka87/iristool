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
public class OFCLocalElem extends Logger {

    private Integer unk1, unk2;
    private String unkst1;
    private Short unks1;

    public Integer getUnk1() {
        return unk1;
    }

    public void setUnk1(Integer unk1) {
        this.unk1 = unk1;
    }

    public Integer getUnk2() {
        return unk2;
    }

    public void setUnk2(Integer unk2) {
        this.unk2 = unk2;
    }

    public String getUnkst1() {
        return unkst1;
    }

    public void setUnkst1(String unkst1) {
        this.unkst1 = unkst1;
    }

    public Short getUnks1() {
        return unks1;
    }

    public void setUnks1(Short unks1) {
        this.unks1 = unks1;
    }

    public OFCLocalElem(Integer unk1, Integer unk2, String unkst1, Short unks1) {
        this.unk1 = unk1;
        this.unk2 = unk2;
        this.unkst1 = unkst1;
        this.unks1 = unks1;
    }
 
    public OFCLocalElem() {
    }

    public boolean readValues(ReaderUtil ru) {
    
        this.unk1 = ru.readInt(false);
        this.unk2 = ru.readInt(false);
        this.unkst1 = ru.readNString(30, false, true);
        this.unks1 = ru.readShort(false);

        return (this.unk1 != null && this.unk2 != null && this.unkst1 != null && this.unks1 != null);
    }

    @Override
    public String toString() {
        return String.format("OFCLocalElem: unk1=%d (%x), unk2=%d (%x), unkst1=%s, unks1=%d (%x)",
                unk1, unk1, unk2, unk2, unkst1, unks1, unks1);
    }
}
