/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class GAOInnerParserMaterialGroup extends BFChunkParseClass implements BFChunkParseInterface {

    private Integer numOrig = 0;
    private Integer materialGroupKeys[] = null;
    private BFKeyListElem[] materialGroupChunkArrayKLE;

    public GAOInnerParserMaterialGroup() {
        super();
    }

    public Integer[] getMaterialGroupKeys() {
        return materialGroupKeys;
    }

    public BFKeyListElem[] getMaterialGroupChunkArrayKLE() {
        return materialGroupChunkArrayKLE;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner Material Group Chunk.");
        }

        Integer num;

        if (!buildFromParsed) {
            num = ru.readInt(false);
            numOrig = num;
        } else {
            wu.writeInt(numOrig);
            num = numOrig;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Num (max: 255): %d (%x)", num, num);
        }

        if (num > 255) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Num greather than 255, setting to 0.");
            }
            num = 0;
        }

        int i;

        Integer key;

        if (!buildFromParsed && num > 0) {
            materialGroupKeys = new Integer[num];
            materialGroupChunkArrayKLE = new BFKeyListElem[num];
        }

        for (i = 0; i < num; i++) {
            if (!buildFromParsed) {
                key = ru.readInt(false);
                materialGroupKeys[i] = key;
            } else {
                key = materialGroupKeys[i];
                wu.writeInt(key);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Material Key found: %x", key);
            }
            BFKeyListElem materialGroupChunkKLE;
            if (!buildFromParsed) {
                materialGroupChunkKLE = new BFKeyListElem(key, BFGaoInnerChunkParser.class);
                materialGroupChunkArrayKLE[i] = materialGroupChunkKLE;
            } else {
                materialGroupChunkKLE = materialGroupChunkArrayKLE[i];
            }
            bfdfc.addToKeyList(materialGroupChunkKLE);
        }

        return true;
    }

}
