/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFDummyChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFDummyChunk() {
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
            log("Parsing Dummy. TODO!");
            log("Length is " + len + " bytes.");

            logF("PARSING STOPPED AT POSITION %x (%d)\n", ru.getCurrentPos(), ru.getCurrentPos());
        }
        return false;
    }
}
