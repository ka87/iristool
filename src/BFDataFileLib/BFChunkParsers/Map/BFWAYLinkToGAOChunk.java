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
public class BFWAYLinkToGAOChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFWAYLinkToGAOChunk() {
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
            log("Parsing WAY Link TO GAO Chunk.");
            log("Length is " + len + " bytes.");
        }

        Integer key = ru.readInt(false);
        Integer unk1 = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("GAO Key: %x, unk1: %d (%x)", key, unk1, unk1);
        }

        if (key != 0 && key != -1) {
            bfdfc.addToKeyList(new BFKeyListElem(key, BFGaoChunk.class));
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
