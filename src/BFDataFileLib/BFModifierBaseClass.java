/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;

/**
 *
 * @author andra
 */
public abstract class BFModifierBaseClass extends BFChunkParseClass {

    private int handleFlags;
    private BFGRPChunk modifierGroup;
    private boolean inited = false;

    public boolean isInited() {
        return inited;
    }

    public void setInited(boolean inited) {
        this.inited = inited;
    }

    public int getHandleFlags() {
        return handleFlags;
    }

    public void setHandleFlags(int handleFlags) {
        this.handleFlags = handleFlags;
    }

    public BFGRPChunk getModifierGroup() {
        return modifierGroup;
    }

    public void setModifierGroup(BFGRPChunk modifierGroup) {
        this.modifierGroup = modifierGroup;
    }
}
