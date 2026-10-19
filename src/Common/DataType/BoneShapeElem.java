/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import BFDataFileLib.BFKeyListElem;

/**
 *
 * @author andra
 */
public class BoneShapeElem {
    private Integer boneID = null;
    private Integer boneGaoKey = null;
    private BFKeyListElem boneGaoChunkKLE = null;
    private Matrix4x4 animationShapeMtx = null;

    public Integer getBoneID() {
        return boneID;
    }

    public void setBoneID(Integer boneID) {
        this.boneID = boneID;
    }

    public Integer getBoneGaoKey() {
        return boneGaoKey;
    }

    public void setBoneGaoKey(Integer boneGaoKey) {
        this.boneGaoKey = boneGaoKey;
    }

    public BFKeyListElem getBoneGaoChunkKLE() {
        return boneGaoChunkKLE;
    }

    public void setBoneGaoChunkKLE(BFKeyListElem boneGaoChunkKLE) {
        this.boneGaoChunkKLE = boneGaoChunkKLE;
    }

    public Matrix4x4 getAnimationShapeMtx() {
        return animationShapeMtx;
    }

    public void setAnimationShapeMtx(Matrix4x4 animationShapeMtx) {
        this.animationShapeMtx = animationShapeMtx;
    }
    
    public BoneShapeElem duplicate() {
        BoneShapeElem bse = new BoneShapeElem();
        bse.boneID = boneID;
        bse.boneGaoChunkKLE = boneGaoChunkKLE;
        bse.boneGaoKey = boneGaoKey;
        if (animationShapeMtx != null) {
            bse.animationShapeMtx = new Matrix4x4();
            bse.animationShapeMtx.set(animationShapeMtx);
        }
        return bse;
    }
}
