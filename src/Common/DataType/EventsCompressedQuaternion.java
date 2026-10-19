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
public class EventsCompressedQuaternion {

    private Short cq1;
    private Short cq2;
    private Short cq3;

    public Short getCQ1() {
        return cq1;
    }

    public void setCQ1(Short cq1) {
        this.cq1 = cq1;
    }

    public Short getCQ2() {
        return cq2;
    }

    public void setCQ2(Short cq2) {
        this.cq2 = cq2;
    }

    public Short getCQ3() {
        return cq3;
    }

    public void setCQ3(Short cq3) {
        this.cq3 = cq3;
    }

    public EventsCompressedQuaternion(Short cq1, Short cq2, Short cq3) {
        this.cq1 = cq1;
        this.cq2 = cq2;
        this.cq3 = cq3;
    }

    public EventsCompressedQuaternion() {
    }

    public boolean readValues(ReaderUtil ru) {
        cq1 = ru.readShort(false);
        cq2 = ru.readShort(false);
        cq3 = ru.readShort(false);

        return (cq1 != null && cq2 != null && cq3 != null);
    }
    
    @Override
    public String toString() {
        return String.format("EventsCompressedQuaternion: cq1=%d (%x), cq2=%d(%x), cq3=%d(%x)",
                cq1, cq1, cq2, cq2, cq3, cq3);
    }
}
