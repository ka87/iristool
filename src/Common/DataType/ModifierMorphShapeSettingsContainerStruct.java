/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import java.util.ArrayList;

/**
 *
 * @author andra
 */
public class ModifierMorphShapeSettingsContainerStruct {
    private Integer morphShapeIDNum;
    private Float unkInner2F1;
    private Float unkInner2F2;
    private String unkInner2S1;
    private ArrayList<Integer> morphShapeFormIDs = new ArrayList<>();

    public Integer getMorphShapeIDNum() {
        return morphShapeIDNum;
    }

    public void setMorphShapeIDNum(Integer morphShapeIDNum) {
        this.morphShapeIDNum = morphShapeIDNum;
    }

    public Float getUnkInner2F1() {
        return unkInner2F1;
    }

    public void setUnkInner2F1(Float unkInner2F1) {
        this.unkInner2F1 = unkInner2F1;
    }

    public Float getUnkInner2F2() {
        return unkInner2F2;
    }

    public void setUnkInner2F2(Float unkInner2F2) {
        this.unkInner2F2 = unkInner2F2;
    }

    public String getUnkInner2S1() {
        return unkInner2S1;
    }

    public void setUnkInner2S1(String unkInner2S1) {
        this.unkInner2S1 = unkInner2S1;
    }

    public ArrayList<Integer> getMorphShapeFormIDs() {
        return morphShapeFormIDs;
    }

    public void setMorphShapeFormIDs(ArrayList<Integer> morphShapeFormIDs) {
        this.morphShapeFormIDs = morphShapeFormIDs;
    }
    
    public ModifierMorphShapeSettingsContainerStruct duplicate() {
        ModifierMorphShapeSettingsContainerStruct tmp = new ModifierMorphShapeSettingsContainerStruct();
        tmp.setUnkInner2F1(this.getUnkInner2F1());
        tmp.setUnkInner2F2(this.getUnkInner2F2());
        tmp.setUnkInner2S1(this.getUnkInner2S1());
        if (this.getMorphShapeFormIDs() != null) {
            ArrayList<Integer> tmpMorphShapeFormIDs = new ArrayList<>();
            this.getMorphShapeFormIDs().forEach(tmpMorphShapeFormIDs::add);
            tmp.setMorphShapeFormIDs(tmpMorphShapeFormIDs);
        }
        tmp.setMorphShapeIDNum(this.getMorphShapeIDNum());
        return tmp;
    }
    
}
