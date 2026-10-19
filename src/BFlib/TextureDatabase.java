package BFlib;

import BFDataFileLib.BFPaletteElem;
import BFDataFileLib.BFTextureElem;
import Common.IrisToolDefines;
import Common.Logger;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andras
 */
public class TextureDatabase extends Logger {

    private ArrayList<BFTextureElem> ar;
    private ArrayList<BFPaletteElem> arPalette;
    private int arIteratorIdx = -1;
    private int arPaletteIteratorIdx = -1;

    public TextureDatabase() {
        ar = new ArrayList<>();
        arPalette = new ArrayList<>();
    }

    public Boolean addElem(BFTextureElem obj) {
        if (obj == null) {
            return false;
        }
        if (hasKey(obj.getKey())) {
            return false;
        }

        ar.add(obj);
        return true;
    }

    public Boolean addElemPalette(BFPaletteElem obj) {
        if (obj == null) {
            return false;
        }
        if (hasKeyPalette(obj.getKey())) {
            return false;
        }

        arPalette.add(obj);
        return true;
    }

    public void clear() {
        ar.clear();
    }

    public void clearPalette() {
        arPalette.clear();
    }

    public int getTextureCount() {
        return ar.size();
    }

    public int getPaletteCount() {
        return arPalette.size();
    }

    public Boolean hasKey(int key) {
        Integer idx = getElemIndex(key);
        Boolean b = (idx != null);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Has Texture with Key: %x in TextureDatabase? %s", key, (b ? "yes" : "no"));
        }
        return b;
    }

    public Boolean hasKeyPalette(int key) {
        Integer idx = getElemIndexPalette(key);
        Boolean b = (idx != null);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Has Palette with Key: %x in TextureDatabase? %s", key, (b ? "yes" : "no"));
        }
        return b;
    }

    public BFTextureElem getObject(Integer key) {
        Integer idx = getElemIndex(key);
        if (idx == null) {
            return null;
        }

        return ar.get(idx);
    }

    public BFTextureElem getObjectBySelectedPalettedTextureKey(Integer key, boolean noIgnored) {
        Integer idx = getElemIndexBySelectedPalettedTextureKey(key, noIgnored);
        if (idx == null) {
            return null;
        }

        return ar.get(idx);
    }

    public BFPaletteElem getObjectPalette(Integer key) {
        Integer idx = getElemIndexPalette(key);
        if (idx == null) {
            return null;
        }

        return arPalette.get(idx);
    }

    public Integer getElemIndex(int key) {
        int i = 0;
        Integer idx = null;
        for (BFTextureElem bft : ar) {
            if (bft.getKey() == key) {
                idx = i;
                break;
            }
            i++;
        }
        return idx;
    }

    public Integer getElemIndexBySelectedPalettedTextureKey(int key, boolean noIgnored) {
        int i = 0;
        Integer idx = null;
        for (BFTextureElem bft : ar) {
            if (bft.getSelectedType7DataTextureKey() != null
                    && bft.getSelectedType7DataTextureKey().equals(key)) {
                if (noIgnored && bft.getIgnoreFromDataParse().equals(true)) {
                    continue;
                }
                idx = i;
                break;
            }
            i++;
        }
        return idx;
    }

    public Integer getElemIndexPalette(int key) {
        int i = 0;
        Integer idx = null;
        for (BFPaletteElem e : arPalette) {
            if (e != null && e.getKey() == key) {
                idx = i;
                break;
            }
            i++;
        }
        return idx;
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

    public boolean removeKeyPalette(int key) {
        Integer idx = getElemIndexPalette(key);
        if (idx != null) {
            arPalette.remove((int) idx);
            if (arPaletteIteratorIdx != -1) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("TODO: Palette iterator restarted!");
                }
                arPaletteIteratorIdx = -1;
            }
        }
        return true;
    }

    public void dumpListIntoLog() {
        for (BFTextureElem bft : ar) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                simpleLogF("Texture Key: %x", bft.getKey());
            }
        }
    }

    public void dumpListIntoLogPalette() {
        for (BFPaletteElem e : arPalette) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                simpleLogF("Palette Key: %x", e.getKey());
            }
        }
    }

    public Boolean hasNext() {
        if ((arIteratorIdx + 1) > (ar.size() - 1)) {
            return false;
        }
        return true;
    }

    public BFTextureElem next() {
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

    public Boolean hasNextPalette() {
        if ((arPaletteIteratorIdx + 1) > (arPalette.size() - 1)) {
            return false;
        }
        return true;
    }

    public BFPaletteElem nextPalette() {
        if (!hasNextPalette()) {
            return null;
        } else {
            arPaletteIteratorIdx++;
            return arPalette.get(arPaletteIteratorIdx);
        }
    }

    public void resetPaletteIterator() {
        arPaletteIteratorIdx = -1;
    }
}
