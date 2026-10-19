/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class GAOInnerParserStringStruct extends BFChunkParseClass implements BFChunkParseInterface {

    private Integer num;

    public GAOInnerParserStringStruct() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner String Struct Chunk.");
        }

        if (!buildFromParsed) {
            num = ru.readInt(false);
        } else {
            wu.writeInt(num);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Num: %d (%x)", num, num);
        }

        return true;
    }

}
