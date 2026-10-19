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
public class ActionsElemTransitionStructInside1 {

    private Integer actionID, unk4nb;

    public ActionsElemTransitionStructInside1() {
    }

    public ActionsElemTransitionStructInside1(Integer actionID, Integer unk4nb) {
        this.actionID = actionID;
        this.unk4nb = unk4nb;
    }

    public Integer getActionID() {
        return actionID;
    }

    public void setActionID(Integer actionID) {
        this.actionID = actionID;
    }

    public Integer getUnk4nb() {
        return unk4nb;
    }

    public void setUnk4nb(Integer unk4nb) {
        this.unk4nb = unk4nb;
    }

    public boolean readValues(ReaderUtil ru, Boolean insideBinFile) {
        actionID = ru.readInt(false);
        if (!insideBinFile) {
            unk4nb = ru.readInt(false);
        }

        return (actionID != null);
    }

    public void writeValues(WriterUtil wu, Boolean insideBinFile) {
        wu.writeInt(actionID);
        if (!insideBinFile) {
            wu.writeInt(unk4nb);
        }
    }

    @Override
    public String toString() {
        return String.format("count1 actionid=%d(%x)", actionID, actionID);
    }
}
