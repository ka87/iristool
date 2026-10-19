/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

import Common.Logger;

/**
 *
 * @author andras
 */
public class BFKeyListElem extends Logger {

    private int key;
    private BFChunkParseInterface parseHandler;
    private Class parserClass;
    private Boolean storeIntoDatabase;
    private Boolean hasInSPE = false;

    public BFKeyListElem(int key, Class parserClass) {
        this(key, parserClass, true);
    }

    public BFKeyListElem(int key, Class parserClass, Boolean storeIntoDatabase) {
        this.key = key;
        this.parserClass = parserClass;
        this.storeIntoDatabase = storeIntoDatabase;
    }

    public Boolean createParserObject() {
        if (parserClass == null) {
            return false;
        }
        try {
            this.parseHandler = (BFChunkParseInterface) Class.forName(parserClass.getName()).newInstance();
        } catch (ClassNotFoundException e) {
            log("Exception (ClassNotFoundException): " + e.getMessage());
            return false;
        } catch (InstantiationException e) {
            log("Exception (InstantiationException): " + e.getMessage());
            return false;
        } catch (IllegalAccessException e) {
            log("Exception (IllegalAccessException): " + e.getMessage());
            return false;
        }
        return true;
    }

    public Boolean getHasInSPE() {
        return hasInSPE;
    }

    public void setHasInSPE(Boolean hasInSPE) {
        this.hasInSPE = hasInSPE;
    }

    public void setParserClass(Class parserClass) {
        this.parserClass = parserClass;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Object getParseHandler() {
        return parseHandler;
    }

    public void setParseHandler(BFChunkParseInterface parseHandler) {
        this.parseHandler = parseHandler;
    }

    public Boolean getStoreIntoDatabase() {
        return storeIntoDatabase;
    }

    public void setStoreIntoDatabase(Boolean storeIntoDatabase) {
        this.storeIntoDatabase = storeIntoDatabase;
    }

    public BFKeyListElem duplicate() {
        BFKeyListElem ret = new BFKeyListElem(key, parserClass);
        ret.hasInSPE = hasInSPE;
        ret.parseHandler = parseHandler;
        ret.storeIntoDatabase = false;
        return ret;
    }
    
}
