package BFlib;

import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;
import java.io.File;
import java.util.LinkedList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andras
 */
public class LoadedSoundsClass extends Logger {

    private LinkedList<Integer> keys;

    public LoadedSoundsClass() {
        keys = new LinkedList<>();
    }

    public void addKey(Integer key) {
        keys.add(key);
    }

    public void clear() {
        keys.clear();
    }

    public Boolean hasKey(int key) {
        Boolean b = keys.contains(key);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Has Sound Key: %x already been loaded ? %s", key, (b ? "yes" : "no"));
        }
        return b;
    }

}
