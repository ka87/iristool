/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFDummyChunk;
import BFDataFileLib.BFChunkParsers.Map.BFSoundBankChunk;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierSoundModifierGEN extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierSoundModifierGEN() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Sound Modifier (GEN)");
        }

        Integer type = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Type: %d (%x)", type, type);
        }

        Boolean type10a = false, type10b = false, type10c = false,
                type10d = false, type10e = false, type10fand110 = false;

        switch (type) {
            case 0x10A:
                type10a = true;
            case 0x10B:
                type10b = true;
            case 0x10C:
                type10c = true;
            case 0x10D:
                type10d = true;
            case 0x10E:
                type10e = true;
            case 0x10F:
            case 0x110:
                type10fand110 = true;
            case 0x111:
                break;
            default:
                break;
        }

        Integer unkFlags, unk2, unkKey, unk4, unk5, unk6, unk7, unk8, unkFlags2, unk10, unk11;
        Float unkf1, unkf2, unkf3, unkf4, unkf5;

        unkFlags = ru.readInt(false);
        unk2 = ru.readInt(false);
        unkKey = ru.readInt(false);
        unkf1 = ru.readFloat(false);
        unkf2 = ru.readFloat(false);
        unkf3 = ru.readFloat(false);
        unkf4 = ru.readFloat(false);
        unkf5 = ru.readFloat(false);
        unk4 = ru.readInt(false);
        unk5 = ru.readInt(false);
        unk6 = ru.readInt(false);
        unk7 = ru.readInt(false);
        unk8 = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkFlags: %d (%x)", unkFlags, unkFlags);
            logF("unk2: %d (%x)", unk2, unk2);
            logF("unkKey: %d (%x)", unkKey, unkKey);
            logF("unkf1: %f", unkf1);
            logF("unkf2: %f", unkf2);
            logF("unkf3: %f", unkf3);
            logF("unkf4: %f", unkf4);
            logF("unkf5: %f", unkf5);
            logF("unk4: %d (%x)", unk4, unk4);
            logF("unk5: %d (%x)", unk5, unk5);
            logF("unk6: %d (%x)", unk6, unk6);
            logF("unk7: %d (%x)", unk7, unk7);
            logF("unk8: %d (%x)", unk8, unk8);
        }

        unkFlags2 = ru.readInt(false);
        unk10 = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkFlags2: %d (%x)", unkFlags2, unkFlags2);
            logF("unk10: %d (%x)", unk10, unk10);
        }

        if (!insideBinFile) {
            unk11 = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk11 (!insidebinfile): %d (%x)", unk11, unk11);
            }
        }

        if (type10c || (unkFlags & 8) == 0) {
            if ((unkFlags2 & 0x4000) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key %x is a wad file.", unkKey);
                }
            } else if ((unkFlags2 & 0x2000) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key %x is a wam file.", unkKey);
                }
            } else if ((unkFlags2 & 0x80000) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key %x is a waa file.", unkKey);
                }
            } else if ((unkFlags2 & 0x10000) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key %x is a smd file.", unkKey);
                    log("SMD PARSE TODO.");
                }
                return false;
            } else if ((unkFlags2 & 0x8) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key %x is a wac file.", unkKey);
                }
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key %x is a wav file.", unkKey);
                }
            }
        }
        if ((unkFlags & 0x10) != 0 && (unkFlags & 8) == 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("language specific probably");
            }
        }

        if (!insideBinFile) {
            Integer unk12 = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk12 (!insidebinfile): %d (%x)", unk12, unk12);
            }
        }

        Float unkf6, unkf7, unkf8, unkf9, unkf10, unkf11, unkf12;
        Integer unk13;

        unkf6 = ru.readFloat(false);
        unkf7 = ru.readFloat(false);
        unkf8 = ru.readFloat(false);
        unkf9 = ru.readFloat(false);
        unkf10 = ru.readFloat(false);
        unkf11 = ru.readFloat(false);
        unkf12 = ru.readFloat(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkf6: %f", unkf6);
            logF("unkf7: %f", unkf7);
            logF("unkf8: %f", unkf8);
            logF("unkf9: %f", unkf9);
            logF("unkf10: %f", unkf10);
            logF("unkf11: %f", unkf11);
            logF("unkf12: %f", unkf12);
        }

        unk13 = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk13: %d (%x)", unk13, unk13);
        }

        Float unkf13, unkf14, unkf15, unkf16, unkf17, unkf18;
        Integer unk14, unk15;
        Float unkf19;

        unkf13 = ru.readFloat(false);
        unkf14 = ru.readFloat(false);
        unkf15 = ru.readFloat(false);
        unkf16 = ru.readFloat(false);
        unkf17 = ru.readFloat(false);
        unkf18 = ru.readFloat(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkf13: %f", unkf13);
            logF("unkf14: %f", unkf14);
            logF("unkf15: %f", unkf15);
            logF("unkf16: %f", unkf16);
            logF("unkf17: %f", unkf17);
            logF("unkf18: %f", unkf18);
        }

        unk14 = ru.readInt(false);
        unk15 = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk14: %d (%x)", unk14, unk14);
            logF("unk15: %d (%x)", unk15, unk15);
        }

        unkf19 = ru.readFloat(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkf19: %f", unkf19);
        }

        if (!insideBinFile) {
            Integer unku1, unku2, unku3;
            String unks1;

            unku1 = ru.readInt(false);
            unku2 = ru.readInt(false);
            unku3 = ru.readInt(false);
            unks1 = ru.readNString(40, false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unku1: %d (%x)", unku1, unku1);
                logF("unku2: %d (%x)", unku2, unku2);
                logF("unku3: %d (%x)", unku3, unku3);
                logF("unks1: %s", unks1);
            }
        }

        if (!type10c && (unkFlags & 0x800) != 0 && unkKey != -1 && unkKey != 0) {
            bfdfc.addToKeyList(new BFKeyListElem(unkKey, BFSoundBankChunk.class));
        }

        return true;
    }

}
