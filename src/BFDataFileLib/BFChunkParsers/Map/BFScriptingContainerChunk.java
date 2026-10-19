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
import Common.DataType.ScriptingSpecialPointerElem;
import Common.IrisToolDefines;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class BFScriptingContainerChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Long len = 0L;
    private Integer functionsAndVariablesKey = -1;
    private Integer variableOverridesKey = -1;
    private BFKeyListElem functionsAndVariablesChunkKLE;
    private BFKeyListElem variableOverridesChunkKLE;

    // RUNTIME only
    private byte[] instanceVariableDefaultsDefault = null;
    private ArrayList<ScriptingSpecialPointerElem> instanceVariableDefaultsResolvedDefault = null;
    private byte[] instanceVariableDefaults = null;
    private ArrayList<ScriptingSpecialPointerElem> instanceVariableDefaultsResolved = null;

    public BFScriptingVariablesChunk getVariablesChunk() {
        int i;
        BFScriptingVariablesAndFunctionCodeKeyCollection scc;
        ArrayList<BFKeyListElem> kles;
        BFKeyListElem kle;
        if (functionsAndVariablesChunkKLE == null || functionsAndVariablesChunkKLE.getParseHandler() == null
                || !(functionsAndVariablesChunkKLE.getParseHandler() instanceof BFScriptingVariablesAndFunctionCodeKeyCollection)) {
            return null;
        }
        scc = (BFScriptingVariablesAndFunctionCodeKeyCollection) (functionsAndVariablesChunkKLE.getParseHandler());
        if ((kles = scc.getScriptingKLEs()) == null) {
            return null;
        }
        for (i = 0; i < kles.size(); i++) {
            if ((kle = kles.get(i)) == null || kle.getParseHandler() == null || !(kle.getParseHandler() instanceof BFScriptingVariablesChunk)) {
                continue;
            }
            return (BFScriptingVariablesChunk) (kle.getParseHandler());
        }
        return null;
    }

    public byte[] getInstanceVariableDefaultsDefault() {
        return instanceVariableDefaultsDefault;
    }

    public void setInstanceVariableDefaultsDefault(byte[] instanceVariableDefaultsDefault) {
        this.instanceVariableDefaultsDefault = instanceVariableDefaultsDefault;
    }

    public ArrayList<ScriptingSpecialPointerElem> getInstanceVariableDefaultsResolvedDefault() {
        return instanceVariableDefaultsResolvedDefault;
    }

    public void setInstanceVariableDefaultsResolvedDefault(ArrayList<ScriptingSpecialPointerElem> instanceVariableDefaultsResolvedDefault) {
        this.instanceVariableDefaultsResolvedDefault = instanceVariableDefaultsResolvedDefault;
    }

    public byte[] getInstanceVariableDefaults() {
        return instanceVariableDefaults;
    }

    public void setInstanceVariableDefaults(byte[] instanceVariableDefaults) {
        this.instanceVariableDefaults = instanceVariableDefaults;
    }

    public ArrayList<ScriptingSpecialPointerElem> getInstanceVariableDefaultsResolved() {
        return instanceVariableDefaultsResolved;
    }

    public void setInstanceVariableDefaultsResolved(ArrayList<ScriptingSpecialPointerElem> instanceVariableDefaultsResolved) {
        this.instanceVariableDefaultsResolved = instanceVariableDefaultsResolved;
    }

    public Integer getFunctionsAndVariablesKey() {
        return functionsAndVariablesKey;
    }

    public Integer getVariableOverridesKey() {
        return variableOverridesKey;
    }

    public BFKeyListElem getFunctionsAndVariablesChunkKLE() {
        return functionsAndVariablesChunkKLE;
    }

    public BFKeyListElem getVariableOverridesChunkKLE() {
        return variableOverridesChunkKLE;
    }

    public BFScriptingContainerChunk() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Scripting Container.");
        }

        if (!buildFromParsed) {
            len = initParse();

            if ((insideBinFile && len != 8) || (!insideBinFile && len != 2044)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Invalid scripting container length: " + len);
                }
                return false;
            }
        } else {
            wu.startBlockWithLength();
        }

        if (!buildFromParsed) {
            /*
            Game reads variables first, then read functions. After that it will read other variable chunk, that will be used to override firstly read variable chunk variables.
            It uses variable names from 2nd variable chunk to find variable position in 1st variable chunk. Then it will copy default value from override to main vars
            (Vars are duplicated after read, probably to keep original value [for eg map reset], overriding occurs in duplicate, not in main var storage)
             */
            functionsAndVariablesKey = ru.readInt(false);
            variableOverridesKey = ru.readInt(false);
        } else {
            wu.writeInt(functionsAndVariablesKey);
            wu.writeInt(variableOverridesKey);
            if (len == 2044) {
                wu.fillWithNByte((byte) 0, 2036); // 2044-8 (8 is the 2 keys' size)
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Functions and variables Key : %x", functionsAndVariablesKey);
            logF("Variable Overrides Key : %x", variableOverridesKey);
        }

        if (functionsAndVariablesKey == null || variableOverridesKey == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Keys read failed.");
            }
            return false;
        }

        if (functionsAndVariablesKey != 0 && functionsAndVariablesKey != -1) {
            if (!buildFromParsed) {
                functionsAndVariablesChunkKLE = new BFKeyListElem(functionsAndVariablesKey, BFScriptingVariablesAndFunctionCodeKeyCollection.class);
            }
            bfdfc.addToKeyList(functionsAndVariablesChunkKLE);
        }

        if (variableOverridesKey != 0 && variableOverridesKey != -1) {
            if (!buildFromParsed) {
                variableOverridesChunkKLE = new BFKeyListElem(variableOverridesKey, BFScriptingVariablesChunk.class);
            }
            bfdfc.addToKeyList(variableOverridesChunkKLE);
        }

        if (buildFromParsed) {
            wu.finishBlockWithLength();
        }

        return true;
    }

}
