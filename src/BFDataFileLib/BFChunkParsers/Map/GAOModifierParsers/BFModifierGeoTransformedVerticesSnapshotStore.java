/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierGeoTransformedVerticesSnapshotStore extends BFModifierBaseClass implements BFChunkParseInterface {

    private Integer unk1;
    private Integer verticesNum;

    public BFModifierGeoTransformedVerticesSnapshotStore() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GEO Modifier Transformed Vertices Snapshot Store (Type 21)");
        }

        if (!insideBinFile) {
            if (!buildFromParsed) {
                unk1 = ru.readInt(false);
            } else {
                wu.writeInt(unk1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1(!insidebinfile): %d (%x)", unk1, unk1);
            }
        }

        if (!buildFromParsed) {
            verticesNum = ru.readInt(false);
        } else {
            wu.writeInt(verticesNum);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Vertices num to save: %d (%x)", verticesNum, verticesNum);
        }

        return true;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            return String.format("GeoTransformedVerticesSnapshotStore: unk1(!insidebinfile): %x (%d), verticesNumToStore: %d (%x)", unk1, unk1, verticesNum, verticesNum);
        } else {
            return "GeoTransformedVerticesSnapshotStore";
        }
    }

    @Override
    public BFModifierGeoTransformedVerticesSnapshotStore duplicate() {
        BFModifierGeoTransformedVerticesSnapshotStore ret = new BFModifierGeoTransformedVerticesSnapshotStore();

        ret.unk1 = unk1;
        ret.verticesNum = verticesNum;

        return ret;
    }

}
