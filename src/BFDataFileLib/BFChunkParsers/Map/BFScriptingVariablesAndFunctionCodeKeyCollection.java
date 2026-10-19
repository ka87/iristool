/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import Common.IrisToolDefines;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class BFScriptingVariablesAndFunctionCodeKeyCollection extends BFChunkParseClass implements BFChunkParseInterface {

    private ArrayList<Integer> scriptingKeys = null;
    private ArrayList<BFKeyListElem> scriptingKLEs = null;

    // RUNTIME only
    private boolean modelResolved = false;

    public boolean isModelResolved() {
        return modelResolved;
    }

    public void setModelResolved(boolean modelResolved) {
        this.modelResolved = modelResolved;
    }

    public ArrayList<BFKeyListElem> getScriptingKLEs() {
        return scriptingKLEs;
    }
        
    public BFScriptingVariablesAndFunctionCodeKeyCollection() {
        super();
    }

    public ArrayList<Integer> getScriptingKeys() {
        return scriptingKeys;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Variables and Function code key collection.");
        }

        Long len = initParse();

        if (insideBinFile && (len % 8 != 0)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Invalid scripting key collection length: " + len);
            }
            return false;
        }

        int i;
        int num = (int) (len / 8);

        long startPos = ru.getCurrentPos();

        scriptingKeys = new ArrayList<>();
        scriptingKLEs = new ArrayList<>();                

        String type;
        Integer key;
        BFKeyListElem kle;

        for (i = 0; i < num; i++) {
            key = ru.readInt(false);
            type = ru.readNString(4, false, true);
            if (type == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Read key collection type fail.");
                }
                return false;
            }
            scriptingKeys.add(key);
            kle = null;
            if (type.equals(".ova")) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Found scripting variables key: %x", key);
                }
                bfdfc.addToKeyList(kle = new BFKeyListElem(key, BFScriptingVariablesChunk.class));
            } else if (type.equals(".ofc")) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Found scripting function code key: %x", key);
                }
                bfdfc.addToKeyList(kle = new BFKeyListElem(key, BFScriptingFunctionCodeChunk.class));
            } else if (insideBinFile) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Unknown key in scripting: %x, ignoring.", key);
                }
            }
            scriptingKLEs.add(kle);
        }

        if (insideBinFile && (ru.getCurrentPos() - startPos) != len) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != length");
            }
            return false;
        }

        return true;
    }

}
