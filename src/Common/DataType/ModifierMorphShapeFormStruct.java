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
public class ModifierMorphShapeFormStruct {
    private Integer morphVerticesNum = 0;
    private ArrayList<Integer> indices = new ArrayList<>();
    private ArrayList<Vector3> vecs = new ArrayList<>();

    public Integer getMorphVerticesNum() {
        return morphVerticesNum;
    }

    public void setMorphVecticesNum(Integer morphVerticesNum) {
        this.morphVerticesNum = morphVerticesNum;
    }

    public ArrayList<Integer> getIndices() {
        return indices;
    }

    public void setIndices(ArrayList<Integer> indices) {
        this.indices = indices;
    }

    public ArrayList<Vector3> getVecs() {
        return vecs;
    }

    public void setVecs(ArrayList<Vector3> vecs) {
        this.vecs = vecs;
    }
    
    public ModifierMorphShapeFormStruct duplicate() {
        ModifierMorphShapeFormStruct tmp = new ModifierMorphShapeFormStruct();
        
        if (this.getIndices() != null) {
            ArrayList<Integer> tmpIndices = new ArrayList<>();
            this.getIndices().forEach(tmpIndices::add);
            tmp.setIndices(indices);
        }
        if (this.getVecs() != null) {
            ArrayList<Vector3> tmpVecs = new ArrayList<>();
            this.getVecs().forEach((currE) -> {
                tmpVecs.add(new Vector3(currE.getX(), currE.getY(), currE.getZ()));
            });
            tmp.setVecs(tmpVecs);
        }
        tmp.setMorphVecticesNum(this.getMorphVerticesNum());
        return tmp;
    }
    
}
