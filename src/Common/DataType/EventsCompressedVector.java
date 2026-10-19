/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.ReaderUtil;

/**
 *
 * @author andras
 */
public class EventsCompressedVector {

    private Short unks1;
    private Byte unkb1;
    private Byte unkb2;

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

    public EventsCompressedVector(Short unks1, Byte unkb1, Byte unkb2) {
        this.unks1 = unks1;
        this.unkb1 = unkb1;
        this.unkb2 = unkb2;
    }

    public EventsCompressedVector() {
    }
   
    public boolean readValues(ReaderUtil ru) {        
        unks1 = ru.readShort(false);
        unkb1 = ru.readByte(false);
        unkb2 = ru.readByte(false);
        
        return (unks1 != null && unkb1 != null && unkb2 != null);
    }

    @Override
    public String toString() {
        return String.format("EventsCompressedVector: unks1=%d (%x), unkb1=%d(%x), unkb2=%d(%x)",
                unks1, unks1, unkb1, unkb1, unkb2, unkb2);
    }
}
