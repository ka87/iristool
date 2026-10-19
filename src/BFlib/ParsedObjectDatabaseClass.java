package BFlib;

import BFDataFileLib.BFChunkParseInterface;
import Common.IrisToolDefines;
import Common.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andras
 */
public class ParsedObjectDatabaseClass extends Logger {

    private Map<Integer, BFChunkParseInterface> object;
    private ArrayList<BFChunkParseInterface> allObjects;

    public ParsedObjectDatabaseClass() {
        object = new HashMap<>();
        allObjects = new ArrayList<>();
    }

    public Boolean addElem(Integer key, BFChunkParseInterface obj) {
        if (object.containsKey(key)) {
            return false;
        }

        object.put(key, obj);
        allObjects.add(obj);
        return true;
    }

    public void clear() {
        object.clear();
        allObjects.clear();
    }

    public Boolean hasKey(Integer key) {
        Boolean b = object.containsKey(key);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Has Object with Key: %x in ParsedObjectDatabase? %s", key, (b ? "yes" : "no"));
        }
        return b;
    }

    public BFChunkParseInterface getObject(Integer key) {
        return object.get(key);
    }

    public boolean removeKey(Integer key) {
        if (!object.containsKey(key)) {
            return false;
        }
        BFChunkParseInterface obj = object.get(key);
        allObjects.remove(obj);
        object.remove(key);
        return true;
    }

    public ArrayList<BFChunkParseInterface> getAllObjects() {
        return allObjects;
    }

}
