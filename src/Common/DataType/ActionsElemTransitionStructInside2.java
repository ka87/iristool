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
public class ActionsElemTransitionStructInside2 {

    private Short actionID;
    private Byte flags, length;

    public ActionsElemTransitionStructInside2() {
    }

    public Short getActionID() {
        return actionID;
    }

    public Byte getFlags() {
        return flags;
    }

    public Byte getLength() {
        return length;
    }

    public boolean readValues(ReaderUtil ru, Boolean insidebinfile) {
        actionID = ru.readShort(false); // actionid
        flags = ru.readByte(false);     // flags
        length = ru.readByte(false);    // length

        return (actionID != null && flags != null && length != null);
    }

    public void writeValues(WriterUtil wu, Boolean insideBinFile) {
        wu.writeShort(actionID);
        wu.writeByte(flags);
        wu.writeByte(length);
    }
    
    @Override
    public String toString() {
        return String.format("count2 actionID=%d(%x), flags=%d(%x), length=%d(%x)", actionID, actionID, flags, flags, length, length);
    }
}
