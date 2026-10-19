/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFlib;

import Common.DataType.BFFilenameElem;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author andras
 */
public class BFFatTable {

    private final ArrayList<BFFatTableElem> fatelems;
    private final ArrayList<BFFilenameElem> fileNameElems;
    private final ArrayList<BFSubFatElem> subfat;

    public BFFatTable() {
        fatelems = new ArrayList<>();
        fileNameElems = new ArrayList<>();
        subfat = new ArrayList<>();
    }

    public int getElemCount() {
        return fatelems.size();
    }

    public void addElem(BFFatTableElem fte) {
        fatelems.add(fte);
    }

    public int getElemIndex(BFFatTableElem fte) {
        return fatelems.indexOf(fte);
    }

    public BFFatTableElem getElemByIndex(int idx) {
        if (idx >= fatelems.size()) {
            return null;
        }
        return fatelems.get(idx);
    }

    public BFFatTableElem getElemByKey(int key) {
        Iterator i = fatelems.iterator();
        BFFatTableElem fte;
        while (i.hasNext()) {
            fte = (BFFatTableElem) (i.next());
            if (fte.getKey() == key) {
                return fte;
            }
        }
        return null;
    }

    public void removeElem(BFFatTableElem fte) {
        fatelems.remove(fte);
    }

    public void removeElemByIndex(int idx) {
        if (idx >= fatelems.size()) {
            return;
        }
        fatelems.remove(idx);
    }

    public int getFileNameElemCount() {
        return fileNameElems.size();
    }

    public void addFileNameElem(BFFilenameElem fne) {
        fileNameElems.add(fne);
    }

    public int getFileNameElemIndex(BFFilenameElem fne) {
        return fileNameElems.indexOf(fne);
    }

    public BFFilenameElem getFileNameElemByIndex(int idx) {
        if (idx >= fileNameElems.size()) {
            return null;
        }
        return fileNameElems.get(idx);
    }

    public BFFilenameElem getFileNameElemByName(String name) {
        Iterator i = fileNameElems.iterator();
        BFFilenameElem fne;
        while (i.hasNext()) {
            fne = (BFFilenameElem) (i.next());
            if (fne != null
                    && fne.getFileName() != null
                    && fne.getFileName().equals(name)) {
                return fne;
            }
        }
        return null;
    }

    public void removeFileNameElem(BFFilenameElem fne) {
        fileNameElems.remove(fne);
    }

    public void removeFileNameElemByIndex(int idx) {
        if (idx >= fileNameElems.size()) {
            return;
        }
        fileNameElems.remove(idx);
    }

    public void addSubFat(BFSubFatElem bsfe) {
        subfat.add(bsfe);
    }

    public int getSubFatIndex(BFSubFatElem bsfe) {
        return subfat.indexOf(bsfe);
    }

    public BFSubFatElem getSubFatByIndex(int idx) {
        if (idx >= subfat.size()) {
            return null;
        }
        return subfat.get(idx);
    }

    public void removeSubFat(BFSubFatElem bsfe) {
        subfat.remove(bsfe);
    }

    public void removeSubFatByIndex(int idx) {
        if (idx >= subfat.size()) {
            return;
        }
        subfat.remove(idx);
    }

    public void clearFatTable() {
        fatelems.clear();
        fileNameElems.clear();
        subfat.clear();
    }
}
