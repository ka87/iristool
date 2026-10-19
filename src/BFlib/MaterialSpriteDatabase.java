package BFlib;

import BFDataFileLib.MaterialSpriteElem;
import Common.IrisToolDefines;
import Common.Logger;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andras
 */
public class MaterialSpriteDatabase extends Logger {

    private ArrayList<MaterialSpriteElem> ar;
    private int arIteratorIdx = -1;

    public MaterialSpriteDatabase() {
        ar = new ArrayList<>();
    }

    public Boolean addElem(Integer key, Integer refKey) {
        Integer idx = getElemIndex(key);

        if (idx != null) {
            return false;
        }

        ar.add(new MaterialSpriteElem(key, refKey));
        return true;
    }

    public void clear() {
        ar.clear();
    }

    public int getMaterialSpriteCount() {
        return ar.size();
    }

    public Boolean hasKey(int key) {
        Integer idx = getElemIndex(key);
        Boolean b = (idx != null);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Has MaterialSprite with Key: %x in MaterialSpriteDatabase? %s", key, (b ? "yes" : "no"));
        }
        return b;
    }

    public Integer getElemIndex(int key) {
        int i = 0;
        Integer idx = null;
        for (MaterialSpriteElem me : ar) {
            if (me.getKey() == key) {
                idx = i;
                break;
            }
            i++;
        }
        return idx;
    }

    public Integer getRefKeyForKey(Integer key) {
        Integer idx = getElemIndex(key);
        if (idx == null) {
            return null;
        }

        MaterialSpriteElem mse = ar.get(idx);

        if (mse == null) {
            return null;
        }

        return mse.getRefkey();
    }

    public boolean removeKey(int key) {
        Integer idx = getElemIndex(key);
        if (idx != null) {
            ar.remove((int) idx);
            if (arIteratorIdx != -1) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("TODO: Texture iterator restarted!");
                }
                arIteratorIdx = -1;
            }
        }
        return true;
    }

    public void dumpListIntoLog() {
        for (MaterialSpriteElem me : ar) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                simpleLogF("MaterialSprite Key: %x => %x", me.getKey(), me.getRefkey());
            }
        }
    }

    public Boolean hasNext() {
        if ((arIteratorIdx + 1) > (ar.size() - 1)) {
            return false;
        }
        return true;
    }

    public MaterialSpriteElem next() {
        if (!hasNext()) {
            return null;
        } else {
            arIteratorIdx++;
            return ar.get(arIteratorIdx);
        }
    }

    public void resetTextureIterator() {
        arIteratorIdx = -1;
    }
}
