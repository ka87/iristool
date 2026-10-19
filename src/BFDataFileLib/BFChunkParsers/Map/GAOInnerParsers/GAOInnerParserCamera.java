/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class GAOInnerParserCamera extends BFChunkParseClass implements BFChunkParseInterface {

    private Integer unk1;
    private Vector3 pos;

    public GAOInnerParserCamera() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner Camera Chunk.");
        }

        if (!buildFromParsed) {
            unk1 = ru.readInt(false);
            pos = new Vector3();

            if (!pos.readValues(ru)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Vector3 read failed.");
                }
                return false;
            }
        } else {
            wu.writeInt(unk1);
            pos.writeValues(wu);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk1: %d (%x)", unk1, unk1);

            log(pos);
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Camera: unk1: %d (%x) %s", unk1, unk1, pos.toString());
    }

}
