/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

/**
 *
 * @author andra
 */
public final class CollisionMapBoxTriangleChunkElem {
    private Short triangleChunkID;
    private Short triangleIDElemNum;
    private Short[] triangleElemIDs;

    public CollisionMapBoxTriangleChunkElem(Short triangleChunkID, Short triangleIDElemNum) {
        this.triangleChunkID = triangleChunkID;
        setTriangleIDElemNum(triangleIDElemNum);
    }

    public Short getTriangleChunkID() {
        return triangleChunkID;
    }

    public void setTriangleChunkID(Short triangleChunkID) {
        this.triangleChunkID = triangleChunkID;
    }

    public Short getTriangleIDElemNum() {
        return triangleIDElemNum;
    }

    public void setTriangleIDElemNum(Short triangleIDElemNum) {
        this.triangleIDElemNum = triangleIDElemNum;
        if (triangleIDElemNum > 0) {
            this.triangleElemIDs = new Short[triangleIDElemNum];
        } else {
            this.triangleElemIDs = null;
        }
    }

    public Short[] getTriangleElemIDs() {
        return triangleElemIDs;
    }        
}
