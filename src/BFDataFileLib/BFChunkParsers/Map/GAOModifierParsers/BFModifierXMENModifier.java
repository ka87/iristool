/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierXMENModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    private Integer unk1;
    private Integer unk2Type;
    private Integer unk2TypeI1, unk2TypeI2;
    private Float unk2TypeF1;
    private Integer flags, flagsOrig;
    private Integer unkNum;
    private Integer unkNumUnk1[];
    private Float unkNumUnkF1[];
    private Integer materialKey;
    private BFKeyListElem materialKLE;

    public BFModifierXMENModifier() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing XMEN");
        }

        long startPos = ru.getCurrentPos();

        if (!insideBinFile) {
            if (!buildFromParsed) {
                unk1 = ru.readInt(false);
            } else {
                wu.writeInt(unk1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1 (not in binfile): %x", unk1);
            }
        }

        if (!buildFromParsed) {
            unk2Type = ru.readInt(false);
        } else {
            wu.writeInt(unk2Type);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Unk2Type: %d (%x)", unk2Type, unk2Type);
        }

        if (!buildFromParsed) {
            flags = ru.readInt(false);
        } else {
            wu.writeInt(flags);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Flags: %d (%x)", flags, flags);
        }

        flagsOrig = flags;
        flags = flags & 0xFFFFFFF7;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Flags (& 0xFFFFFFF7): %d (%x)", flags, flags);
        }

        if (!buildFromParsed) {
            unkNum = ru.readInt(false);
        } else {
            wu.writeInt(unkNum);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("UnkNum: %d (%x)", unkNum, unkNum);
        }

        if (unkNum > 0 && !buildFromParsed) {
            unkNumUnk1 = new Integer[unkNum];
            unkNumUnkF1 = new Float[unkNum];
        }

        int i;

        for (i = 0; i < unkNum; i++) {
            if (!buildFromParsed) {
                unkNumUnk1[i] = ru.readInt(false);
                unkNumUnkF1[i] = ru.readFloat(false);
            } else {
                wu.writeInt(unkNumUnk1[i]);
                wu.writeFloat(unkNumUnkF1[i]);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("-->(%d.) unkNumUnk1: %d (%x), unkNumUnkF1: %f", i, unkNumUnk1[i], unkNumUnk1[i], unkNumUnkF1[i]);
            }
        }

        if (!buildFromParsed) {
            materialKey = ru.readInt(false);
        } else {
            wu.writeInt(materialKey);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Material Key: %d (%x)", materialKey, materialKey);
        }

        if (unk2Type > 0) {
            if (!buildFromParsed) {
                unk2TypeI1 = ru.readInt(false);
            } else {
                wu.writeInt(unk2TypeI1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk2TypeI1: %d (%x)", unk2TypeI1, unk2TypeI1);
            }
        }
        if (unk2Type > 1) {
            if (!buildFromParsed) {
                unk2TypeF1 = ru.readFloat(false);
            } else {
                wu.writeFloat(unk2TypeF1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk2TypeF1: %f", unk2TypeF1);
            }
        }
        if (unk2Type > 2) {
            if (!buildFromParsed) {
                unk2TypeI2 = ru.readInt(false);
            } else {
                wu.writeInt(unk2TypeI2);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk2TypeI2: %d (%x)", unk2TypeI2, unk2TypeI2);
            }
        }

        if ((flags & 0x8) == 0 && materialKey != 0 && materialKey != -1) {
            bfdfc.addToKeyList(materialKLE = new BFKeyListElem(materialKey, BFGaoInnerChunkParser.class));
        }

        return true;
    }

    @Override
    public BFModifierXMENModifier duplicate() {
        int i;
        BFModifierXMENModifier ret = new BFModifierXMENModifier();

        ret.flags = flags;
        ret.flagsOrig = flagsOrig;
        ret.materialKey = materialKey;
        ret.materialKLE = materialKLE; // ?
        ret.unk1 = unk1;
        ret.unk2Type = unk2Type;
        ret.unk2TypeF1 = unk2TypeF1;
        ret.unk2TypeI1 = unk2TypeI1;
        ret.unk2TypeI2 = unk2TypeI2;
        ret.unkNum = unkNum;
        if (unkNumUnk1 != null) {
            ret.unkNumUnk1 = new Integer[unkNumUnk1.length];
            for (i = 0; i < unkNumUnk1.length; i++) {
                ret.unkNumUnk1[i] = unkNumUnk1[i];
            }
        }
        if (unkNumUnkF1 != null) {
            ret.unkNumUnkF1 = new Float[unkNumUnkF1.length];
            for (i = 0; i < unkNumUnkF1.length; i++) {
                ret.unkNumUnkF1[i] = unkNumUnkF1[i];
            }
        }
        return ret;
    }

}
