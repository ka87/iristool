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
public class BFModifierProtexModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierProtexModifier() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Protex Modifier.");
        }

        Integer unk1, unk2;

        unk1 = ru.readInt(false);
        unk2 = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk1: %d (%x)", unk1, unk1);
            logF("unk2: %d (%x)", unk2, unk2);
        }

        if (unk2 != 0) {
            Integer len1, len2;
            String s1, s2;

            len1 = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Length 1: %d", len1);
            }
            s1 = ru.readNString(len1, false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("s1: %s", s1);
            }
            len2 = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Length 2: %d", len2);
            }
            s2 = ru.readNString(len2, false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("s2: %s", s2);
            }
        }

        return true;
    }

}
