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
public class BFModifierObjectFakeLookAtCameraModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierObjectFakeLookAtCameraModifier() {
        super();
    }

    private Integer unk1;

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing BFModifierObjectFakeLookAtCameraModifier (Type 5)");
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
        } else {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Not need to read anything...");
            }
        }

        return true;
    }

}
