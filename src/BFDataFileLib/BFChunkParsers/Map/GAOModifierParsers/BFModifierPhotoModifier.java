/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierPhotoModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierPhotoModifier() {
        super();
    }

    private Integer unkI1;
    private Integer unkI2;
    private Integer unkI3;
    private Integer unkI4;
    private Float unkF1;
    private Float unkF2;
    private Float unkF3;
    private Float unkF4;
    private Vector3 pos1;
    private Vector3 pos2;
    private Float unkF5;

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Photo Modifier.");
        }

        if (!buildFromParsed) {
            unkI1 = ru.readInt(false);
            unkI2 = ru.readInt(false);
            unkI3 = ru.readInt(false);
            unkI4 = ru.readInt(false);
        } else {
            wu.writeInt(unkI1);
            wu.writeInt(unkI2);
            wu.writeInt(unkI3);
            wu.writeInt(unkI4);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkI1: %d (%x), unkI2: %d (%x), unkI3: %d (%x), unkI4: %d (%x)",
                    unkI1, unkI1, unkI2, unkI2, unkI3, unkI3, unkI4, unkI4);
        }

        if (!buildFromParsed) {
            unkF1 = ru.readFloat(false);
            unkF2 = ru.readFloat(false);
            unkF3 = ru.readFloat(false);
            unkF4 = ru.readFloat(false);
        } else {
            wu.writeFloat(unkF1);
            wu.writeFloat(unkF2);
            wu.writeFloat(unkF3);
            wu.writeFloat(unkF4);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkF1: %f, unkF2: %f unkF3: %f, unkF4: %f", unkF1, unkF2, unkF3, unkF4);
        }

        Integer floatInt = 0x3f8020c5; // 1.001000

        if (unkF4 == Float.intBitsToFloat(floatInt)) {
            if (!buildFromParsed) {
                pos1 = new Vector3();
                pos2 = new Vector3();
                if (!pos1.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Position3D read failed.");
                    }
                    return false;
                }
                if (!pos2.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Position3D read failed.");
                    }
                    return false;
                }
            } else {
                pos1.writeValues(wu);
                pos2.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(pos1);
                log(pos2);
            }
        }
        if (!buildFromParsed) {
            unkF5 = ru.readFloat(false);
        } else {
            wu.writeFloat(unkF5);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkF5: %f", unkF5);
        }

        return true;
    }

}
