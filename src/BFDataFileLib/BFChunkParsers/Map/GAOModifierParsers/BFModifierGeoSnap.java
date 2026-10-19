/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierGeoSnap extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierGeoSnap() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Geo Snap Modifier (Type 0)");

            logF("pos=%x", ru.getCurrentPos());
        }

        if (!insideBinFile) {
            ru.readInt(false);
        }

        Integer key = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("GAO Key: %x", key);
        }
        if (key != -1) {
            bfdfc.addToKeyList(new BFKeyListElem(key, BFGaoChunk.class));
        }
        Integer num = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Index num: %d (%x)", num, num);
        }
        int i;

        if (true) {
            return false;
        }

        Integer vec1Idx, vec2Idx;

        for (i = 0; i < num; i++) {
            vec1Idx = ru.readInt(false);
            vec2Idx = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("vec1Idx: %d (%x)", vec1Idx, vec1Idx);
                logF("vec2Idx: %d (%x)", vec2Idx, vec2Idx);
            }
        }

        return true;
    }

}
