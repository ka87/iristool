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
public class SPEClass extends Logger {

    private File f;
    private LinkedList<Integer> keys;
    private Boolean success = false;

    public SPEClass(String f) {
        this(new File(f));
    }

    public SPEClass(File f) {

        keys = new LinkedList<>();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Set PErmanent (SPE) file.");
        }

        if (!f.exists() || !f.isFile()) {
            return;
        }
        this.f = f;

        ReaderUtil ru = new ReaderUtil(f);

        Long len = ru.getDataLength();

        if (len % 4 != 0) {
            log("Invalid spe file size.");
            return;
        }

        int num = (int) (len / 4);

        Integer key;

        while (num-- > 0) {
            key = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("SPE key: %x", key);
            }
            keys.add(key);
        }
        success = true;
    }

    public Boolean isParsed() {
        return success;
    }

    public Boolean hasKey(int key) {
        Boolean b = keys.contains(key);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Has Key: %x in SPE? %s", key, (b ? "yes" : "no"));
        }
        return b;
    }

}
