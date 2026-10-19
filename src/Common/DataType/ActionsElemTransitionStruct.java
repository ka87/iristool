/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.ReaderUtil;
import Common.WriterUtil;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class ActionsElemTransitionStruct {

    private Integer count, unk2, unk3nb, unk4nb;
    private ArrayList<ActionsElemTransitionStructInside1> atsi1Array = new ArrayList<>();
    private ArrayList<ActionsElemTransitionStructInside2> atsi2Array = new ArrayList<>();

    public ActionsElemTransitionStruct() {
    }

    public ActionsElemTransitionStruct(Integer count, Integer unk2, Integer unk3nb, Integer unk4nb) {
        this.count = count;
        this.unk2 = unk2;
        this.unk3nb = unk3nb;
        this.unk4nb = unk4nb;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getUnk2() {
        return unk2;
    }

    public void setUnk2(Integer unk2) {
        this.unk2 = unk2;
    }

    public Integer getUnk3nb() {
        return unk3nb;
    }

    public void setUnk3nb(Integer unk3nb) {
        this.unk3nb = unk3nb;
    }

    public Integer getUnk4nb() {
        return unk4nb;
    }

    public void setUnk4nb(Integer unk4nb) {
        this.unk4nb = unk4nb;
    }

    public ArrayList<ActionsElemTransitionStructInside1> getATSi1Array() {
        return atsi1Array;
    }

    public ArrayList<ActionsElemTransitionStructInside2> getATSi2Array() {
        return atsi2Array;
    }

    public boolean readValues(ReaderUtil ru, Boolean insideBinFile) {
        if (!insideBinFile) {
            unk3nb = ru.readInt(false);
        }
        count = ru.readInt(false);
        if (!insideBinFile) {
            unk4nb = ru.readInt(false);
        }
        unk2 = ru.readInt(false);
        int i;
        for (i = 0; i < count; i++) {
            ActionsElemTransitionStructInside1 atsi1 = new ActionsElemTransitionStructInside1();
            if (!atsi1.readValues(ru, insideBinFile)) {
                return false;
            }
            atsi1Array.add(atsi1);
        }

        for (i = 0; i < count; i++) {
            ActionsElemTransitionStructInside2 atsi2 = new ActionsElemTransitionStructInside2();
            if (!atsi2.readValues(ru, insideBinFile)) {
                return false;
            }
            atsi2Array.add(atsi2);
        }

        return (count != null && unk2 != null);
    }

    public Boolean writeValues(WriterUtil wu, Boolean insideBinFile) {
        if (!insideBinFile) {
            wu.writeInt(unk3nb);
        }
        wu.writeInt(count);
        if (!insideBinFile) {
            wu.writeInt(unk4nb);
        }
        wu.writeInt(unk2);
        int i;
        for (i = 0; i < count; i++) {
            ActionsElemTransitionStructInside1 atsi1 = atsi1Array.get(i);
            if (atsi1 == null) {
                return false;
            }
            atsi1.writeValues(wu, insideBinFile);
        }

        for (i = 0; i < count; i++) {
            ActionsElemTransitionStructInside2 atsi2 = atsi2Array.get(i);
            if (atsi2 == null) {
                return false;
            }
            atsi2.writeValues(wu, insideBinFile);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ActionElemTransition: count=%d, unk2=%d(%x)\n", count, unk2, unk2));
        int i;
        for (i = 0; i < count; i++) {
            sb.append(" ");
            sb.append(atsi1Array.get(i));
            sb.append("\n");
        }
        for (i = 0; i < count; i++) {
            sb.append(" ");
            sb.append(atsi2Array.get(i));
            sb.append("\n");
        }
        return sb.toString();
    }
}
