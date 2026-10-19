/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;

/**
 *
 * @author andra
 */
public class ZListNodeElem {

    private BFGaoChunk gao;
    private Matrix4x4 mtx;

    public BFGaoChunk getGao() {
        return gao;
    }

    public void setGao(BFGaoChunk gao) {
        this.gao = gao;
    }

    public Matrix4x4 getMtx() {
        return mtx;
    }

    public void setMtx(Matrix4x4 mtx) {
        this.mtx = new Matrix4x4();
        if (mtx == null) {
            return;
        }
        this.mtx.set(mtx);
    }
}
