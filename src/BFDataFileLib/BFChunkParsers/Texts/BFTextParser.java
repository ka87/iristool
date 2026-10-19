package BFDataFileLib.BFChunkParsers.Texts;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import Common.IrisToolDefines;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andras
 */
public class BFTextParser extends BFChunkParseClass implements BFChunkParseInterface {

    private boolean parseGroupList(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        long dataLen = ru.getDataLength();
        Long len = initParse();

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        long startPos = ru.getCurrentPos();
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Length: %d bytes.", len);
        }

        if (len <= 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Empty, returning.");
            }
            return true;
        }

        int txgKey;
        String dottxg;
        while (len > 0) {
            txgKey = ru.readInt(false);
            dottxg = ru.readNString(4, false);
            if (txgKey != 0 && txgKey != -1 && ".txg".equals(dottxg)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Text Group key: %x, .txg: %s", txgKey, dottxg);
                }
                bfdfc.addToKeyList(new BFKeyListElem(txgKey, BFTextGroupParser.class));
            }
            len -= 8;
        }

        if (!bfdfc.parseAllFromKeyFIFO(buildFromParsed)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Text Group List.");
        }

        if (!parseGroupList(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        // on bin file case we read again next round.
        if (insideBinFile) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("We are in inside bin file, so read again group list.");
            }
            if (!parseGroupList(bfdfc, insideBinFile, buildFromParsed)) {
                return false;
            }
        }
        return true;
    }
}
