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
public class BFModifierSoundFXModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierSoundFXModifier() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Sound FX Modifier");
        }

        Integer unkSize = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("UnkSize: " + unkSize);
        }

        if (unkSize == 256) {
            Integer unk1, unk2;

            unk1 = ru.readInt(false);
            unk2 = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1: %d (%x), unk2: %d (%x)", unk1, unk1, unk2, unk2);
            }

            Float f1, f2;

            f1 = ru.readFloat(false);
            f2 = ru.readFloat(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("f1: %f, f2: %f", f1, f2);
            }

            Integer unk3, unk4, unk5;

            unk3 = ru.readInt(false);
            unk4 = ru.readInt(false);
            unk5 = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk3: %d (%x), unk4: %d (%x), unk5: %d (%x)", unk3, unk3, unk4, unk4, unk5, unk5);
            }

            Float f3 = ru.readFloat(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("f3: %f", f3);
            }

            Integer unk6 = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk6: %d (%x)", unk6, unk6);
            }

            if (!insideBinFile) {
                String s = ru.readNString(256, false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("String(!insidebinfile) - or byte array: %s", s);
                }
            }
        }

        // TODO: add to LoadedSoundsClass
        return true;
    }

}
