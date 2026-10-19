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
public class CollisionMapBox {

    private Integer triangleChunkNum;
    private Vector3 aaBoxVec1 = new Vector3(), aaBoxVec2 = new Vector3();
    private CollisionMapBoxTriangleChunkElem[] collisionMapBoxTriangleChunks;

    public Integer getTriangleChunkNum() {
        return triangleChunkNum;
    }

    public void setTriangleChunkNum(Integer triangleChunkNum) {
        this.triangleChunkNum = triangleChunkNum;
        if (triangleChunkNum > 0) {
            this.collisionMapBoxTriangleChunks = new CollisionMapBoxTriangleChunkElem[triangleChunkNum];
        } else {
            this.collisionMapBoxTriangleChunks = null;
        }
    }

    public Vector3 getAaBoxVec1() {
        return aaBoxVec1;
    }

    public Vector3 getAaBoxVec2() {
        return aaBoxVec2;
    }

    public CollisionMapBoxTriangleChunkElem[] getCollisionMapBoxTriangleChunks() {
        return collisionMapBoxTriangleChunks;
    }
}
