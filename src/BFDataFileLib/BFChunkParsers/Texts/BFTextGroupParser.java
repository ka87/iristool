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
public class BFTextGroupParser extends BFChunkParseClass implements BFChunkParseInterface {

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Text Groups.");
        }
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

        int keyi;
        String dottxi;
        int keys;
        String dottxs;
        int defLangIDKey = 0;
        int defLangTextKey = 0;
        while (len > 0) {
            keyi = ru.readInt(false);
            dottxi = ru.readNString(4, false);
            keys = ru.readInt(false);
            dottxs = ru.readNString(4, false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("IDs key: %x, .txi: %s, Texts key: %x, .txs = %s", keyi, dottxi, keys, dottxs);
            }
            // TODO: language specific key select, !insidebinfile file positioning handling
            if (defLangIDKey == 0 && keyi != 0 && keyi != -1) {
                defLangIDKey = keyi;
                defLangTextKey = keys;
            }
            len -= 16;
        }

        int unkNum = ru.readInt(false); // osztva ezzel: 68 (ennyi lenne a mérete, ha !insidebinfile)
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("UnkNum: %x (%d)", unkNum, unkNum);
        }
        bfdfc.addToKeyList(new BFKeyListElem(defLangIDKey, BFTextIdentifierParser.class));
        bfdfc.addToKeyList(new BFKeyListElem(defLangTextKey, BFTextStringParser.class));

        return true;
    }
}
