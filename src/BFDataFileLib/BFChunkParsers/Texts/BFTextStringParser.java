package BFDataFileLib.BFChunkParsers.Texts;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
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
public class BFTextStringParser extends BFChunkParseClass implements BFChunkParseInterface {

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Text String.");
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

        int id;
        String text = ru.readNString(Math.toIntExact(len), false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Text: %s", text);
        }

        return true;
    }
}
