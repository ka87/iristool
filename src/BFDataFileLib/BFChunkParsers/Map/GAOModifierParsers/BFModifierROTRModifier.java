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
public class BFModifierROTRModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    private Integer unk1, unk2;
    private Float f1, f2, f3, f4;

    public BFModifierROTRModifier() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing ROTR Modifier.");
        }

        if (!insideBinFile) {
            if (!buildFromParsed) {
                unk1 = ru.readInt(false);
            } else {
                wu.writeInt(unk1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1 (!insidebinfile): %d (%x)", unk1, unk1);
            }
        }

        if (!buildFromParsed) {
            unk2 = ru.readInt(false);
            f1 = ru.readFloat(false);
            f2 = ru.readFloat(false);
            f3 = ru.readFloat(false);
            f4 = ru.readFloat(false);
        } else {
            wu.writeInt(unk2);
            wu.writeFloat(f1);
            wu.writeFloat(f2);
            wu.writeFloat(f3);
            wu.writeFloat(f4);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk2: %d (%x)", unk2, unk2);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("f1: %f, f2: %f, f3: %f, f4: %f", f1, f2, f3, f4);
        }

        return true;
    }

    @Override
    public BFModifierROTRModifier duplicate() {
        BFModifierROTRModifier ret = new BFModifierROTRModifier();

        ret.unk1 = unk1;
        ret.unk2 = unk2;
        ret.f1 = f1;
        ret.f2 = f2;
        ret.f3 = f3;
        ret.f4 = f4;

        return ret;
    }

}
