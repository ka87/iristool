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
public class BFWAYGroupInGAOChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFWAYGroupInGAOChunk() {
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
            log("Parsing WAY Group in GAO Chunk.");
            log("Length is " + len + " bytes.");
        }

        Integer num = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Group element num: " + num);
        }

        int i, j;

        Short uS1, uS2;
        Byte uB1;
        Integer gaoKey;

        for (i = 0; i < num; i++) {

            Integer key = ru.readInt(false);
            Integer unkElemNum1 = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("[%d] GAO Key: %x, unkElemNum1: %d (%x)", i, key, unkElemNum1, unkElemNum1);
            }
            bfdfc.addToKeyList(new BFKeyListElem(key, BFWAYLinkToGAOChunk.class));

            for (j = 0; j < unkElemNum1; j++) {
                uS1 = ru.readShort(false);
                gaoKey = ru.readInt(false);
                bfdfc.addToKeyList(new BFKeyListElem(gaoKey, BFGaoChunk.class));
                uB1 = ru.readByte(false);
                uS2 = ru.readShort(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF(" [%d] uS1: %d (%x), GAO Key: %x, uB1: %d (%x), uS2: %d (%x)", j, uS1, uS1, gaoKey, uB1, uB1, uS2, uS2);
                }
            }

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
