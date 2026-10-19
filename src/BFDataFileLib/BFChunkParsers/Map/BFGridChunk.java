/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFGridChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFGridChunk() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing GRID.");
        }

        Long len = initParse();

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        long startPos = ru.getCurrentPos();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("GRID length: " + len + " bytes.");
        }

        if (!insideBinFile) {
            Integer unkNB1, unkNB2;
            unkNB1 = ru.readInt(false);
            unkNB2 = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("(!insidebinfile) unkNB1: %d (%x), unkNB2: %d (%x)", unkNB1, unkNB1, unkNB2, unkNB2);
            }
        }

        Integer gridInsideKey = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("GRID Inside Key: %x", gridInsideKey);
        }

        if (!insideBinFile) {
            Integer unkNB3, unkNB4, unkNB5;
            unkNB3 = ru.readInt(false);
            unkNB4 = ru.readInt(false);
            unkNB5 = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("(!insidebinfile) unkNB3: %d (%x), unkNB4: %d (%x), unkNB5: %d (%x)", unkNB3, unkNB3, unkNB4, unkNB4, unkNB5, unkNB5);
            }
        }

        Float f1, f2, f3, f4;
        Short num1, num2;

        f1 = ru.readFloat(false);
        f2 = ru.readFloat(false);
        f3 = ru.readFloat(false);
        f4 = ru.readFloat(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("f1: %f, f2: %f, f3: %f, f4: %f", f1, f2, f3, f4);
        }

        num1 = ru.readShort(false);
        num2 = ru.readShort(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("num1: %d, num2: %d", num1, num2);
        }

        if (!insideBinFile) {
            Short unkNBS1, unkNBS2;
            unkNBS1 = ru.readShort(false);
            unkNBS2 = ru.readShort(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unkNBS1: %d (%x), unkNBS2: %d (%x)", unkNBS1, unkNBS1, unkNBS2, unkNBS2);
            }
        }

        Short unks1, unks2;
        unks1 = ru.readShort(false);
        unks2 = ru.readShort(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unks1: %d (%x), unks2: %d (%x)", unks1, unks1, unks2, unks2);
        }

        Byte unkb1 = ru.readByte(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkb1: %d (%x)", unkb1, unkb1);
        }

        if (!insideBinFile) {
            Integer unkNB6 = ru.readInt(false);
            Byte unkNBB1, unkNBB2, unkNBB3;
            unkNBB1 = ru.readByte(false);
            unkNBB2 = ru.readByte(false);
            unkNBB3 = ru.readByte(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unkNB6: %d (%x), unkNBB1: %d (%x), unkNBB2: %d (%x), unkNBB3: %d (%x)",
                        unkNB6, unkNB6, unkNBB1, unkNBB1, unkNBB2, unkNBB2, unkNBB3, unkNBB3);
            }
        }

        int i;

        Integer unk1;

        for (i = 0; i < (num1 * num2); i++) {
            unk1 = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1[%d]: %d (%x)", i, unk1, unk1);
            }
        }

        bfdfc.addToKeyList(new BFKeyListElem(gridInsideKey, BFGridInsideChunk.class));

        if ((ru.getCurrentPos() - startPos) != len) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != length");
            }
            return false;
        }

        return true;
    }

}
