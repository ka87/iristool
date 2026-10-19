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
public class BFModifierSoundLoadingModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierSoundLoadingModifier() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Sound Loading Modifier");
        }

        Integer unkSize = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("UnkSize: " + unkSize);
        }

        if (unkSize == 256) {
            Integer unk1, unk2;
            Integer sndKey;

            unk1 = ru.readInt(false);
            unk2 = ru.readInt(false);
            sndKey = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1: %d (%x), unk2: %d (%x), Sound key: %x", unk1, unk1, unk2, unk2, sndKey);
            }

            Float f1, f2, f3, f4;

            f1 = ru.readFloat(false);
            f2 = ru.readFloat(false);
            f3 = ru.readFloat(false);
            f4 = ru.readFloat(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("f1: %f, f2: %f, f3: %f, f4: %f", f1, f2, f3, f4);
            }

            Integer unk3, unk4, unk5, unk6, unk7, unk8, unk9;

            unk3 = ru.readInt(false);
            unk4 = ru.readInt(false);
            unk5 = ru.readInt(false);
            unk6 = ru.readInt(false);
            unk7 = ru.readInt(false);
            unk8 = ru.readInt(false);
            unk9 = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk3: %d (%x), unk4: %d (%x), unk5: %d (%x), unk6: %d (%x), unk7: %d (%x), unk8: %d (%x), unk9: %d (%x)",
                        unk3, unk3, unk4, unk4, unk5, unk5, unk6, unk6, unk7, unk7, unk8, unk8, unk9, unk9);
            }

            Float f5, f6, f7, f8, f9, f10, f11, f12;

            f5 = ru.readFloat(false);
            f6 = ru.readFloat(false);
            f7 = ru.readFloat(false);
            f8 = ru.readFloat(false);
            f9 = ru.readFloat(false);
            f10 = ru.readFloat(false);
            f11 = ru.readFloat(false);
            f12 = ru.readFloat(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("f5: %f, f6: %f, f7: %f, f8: %f, f9: %f, f10: %f, f11: %f, f12: %f", f5, f6, f7, f8, f9, f10, f11, f12);
            }

            Integer unk10 = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk10: %d (%x)", unk10, unk10);
            }

            Float f13, f14, f15, f16, f17, f18;

            f13 = ru.readFloat(false);
            f14 = ru.readFloat(false);
            f15 = ru.readFloat(false);
            f16 = ru.readFloat(false);
            f17 = ru.readFloat(false);
            f18 = ru.readFloat(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("f13: %f, f14: %f, f15: %f, f16: %f, f17: %f, f18: %f", f13, f14, f15, f16, f17, f18);
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
