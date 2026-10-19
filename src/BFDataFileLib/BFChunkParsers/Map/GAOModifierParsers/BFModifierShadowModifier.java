/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import BFDataFileLib.BFTextureElem;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierShadowModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierShadowModifier() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Shadow Modifier (SDW)");
        }

        // read dword if not efs
        Integer unk1 = ru.readInt(false);
        Integer textureKey = ru.readInt(false);
        Float unkf1 = ru.readFloat(false);
        Float unkf2 = ru.readFloat(false);
        Integer unk3 = ru.readInt(false);
        // read dword if not efs
        // read dword if not efs
        Float unkf3 = ru.readFloat(false);
        Integer unk4 = ru.readInt(false);
        Float unkf4 = ru.readFloat(false);
        Vector3 pos = new Vector3();
        pos.readValues(ru);
        Float unkf5 = ru.readFloat(false);
        Integer unk5 = ru.readInt(false);
        Byte unkb1 = ru.readByte(false);
        // read byte if not efs
        // read byte if not efs
        // read byte if not efs

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Unk1: %d (%x)", unk1, unk1);
            logF("Texture Key: %x", textureKey);
            logF("Unk Float1: %f", unkf1);
            logF("Unk Float2: %f", unkf2);
            logF("Unk3: %d (%x)", unk3, unk3);
            logF("Unk Float3: %f", unkf3);
            logF("Unk4: %d (%x)", unk4, unk4);
            logF("Unk Float4: %f", unkf4);
            log(pos);
            logF("Unk Float5: %f", unkf5);
            logF("Unk5: %d (%x)", unk5, unk5);
            logF("Unkb1: %d (%x)", unkb1, unkb1);
        }

        if (bfdfc.getTextureDatabase() != null
                && !bfdfc.getTextureDatabase().hasKey(textureKey)) {
            bfdfc.getTextureDatabase().addElem(new BFTextureElem(textureKey));
        }

        return true;
    }

}
