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

/**
 *
 * @author andras
 */
public class BFWAYGroupChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFWAYGroupChunk() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        Long len = initParse();

        long startpos = ru.getCurrentPos();

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing WAY GRP Chunk.");
            log("Length is " + len + " bytes.");
        }

        Integer num = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("WAYs num: " + num);
        }
        Integer key;

        int i;
        for (i = 0; i < num; i++) {
            key = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("WAY key: %x", key);
            }
            bfdfc.addToKeyList(new BFKeyListElem(key, BFWAYLinkToGAOChunk.class));
        }

        if (!insideBinFile && len != (ru.getCurrentPos() - startpos)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != length");
            }
            return false;
        }

        return true;
    }

}
