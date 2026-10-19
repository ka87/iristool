/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierGAOInverseKinematics extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierGAOInverseKinematics() {
        super();
    }

    private Integer unkI1;
    private Integer gaoKey1;
    private Integer gaoKey2;
    private float firstAndMiddleLenFromInitPos = 0.0f;
    private float secondAndMiddleLenFromInitPos = 0.0f;
    private Integer gaoKey3;
    private Integer flags;
    private int flagsCleaned;

    private BFKeyListElem gao1KLE;
    private BFKeyListElem gao2KLE;
    private BFKeyListElem gao3KLE;

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inverse Kinematics Modifier.");
        }

        if (!buildFromParsed) {
            unkI1 = ru.readInt(false);
        } else {
            wu.writeInt(unkI1);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkI1: %d (%x)", unkI1, unkI1);
        }

        if (!buildFromParsed) {
            gaoKey1 = ru.readInt(false);
            gaoKey2 = ru.readInt(false);
            firstAndMiddleLenFromInitPos = ru.readFloat(false);
            secondAndMiddleLenFromInitPos = ru.readFloat(false);
            gaoKey3 = ru.readInt(false);
            flags = ru.readInt(false);
        } else {
            wu.writeInt(gaoKey1);
            wu.writeInt(gaoKey2);
            wu.writeFloat(firstAndMiddleLenFromInitPos);
            wu.writeFloat(secondAndMiddleLenFromInitPos);
            wu.writeInt(gaoKey3);
            wu.writeInt(flags);
        }

        flagsCleaned = flags & ~0x40;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("GAO Key1: %x, GAO Key2: %x, firstAndMiddleLenFromInitPos: %f, secondAndMiddleLenFromInitPos: %f, GAO Key3: %x, Flags: %d (%x), Flags(cleaned): %d (%x)",
                    gaoKey1, gaoKey2, firstAndMiddleLenFromInitPos, secondAndMiddleLenFromInitPos, gaoKey3, flags, flags, flagsCleaned, flagsCleaned);
        }

        if ((flagsCleaned & 0x10) == 0) {
            if (gaoKey1 != 0 && gaoKey1 != -1) {
                bfdfc.addToKeyList(gao1KLE = new BFKeyListElem(gaoKey1, BFGaoChunk.class));
            }
            if (gaoKey2 != 0 && gaoKey2 != -1) {
                bfdfc.addToKeyList(gao2KLE = new BFKeyListElem(gaoKey2, BFGaoChunk.class));
            }
            if (gaoKey3 != 0 && gaoKey3 != -1) {
                bfdfc.addToKeyList(gao3KLE = new BFKeyListElem(gaoKey3, BFGaoChunk.class));
            }
        }

        return true;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            return String.format("ModifierGAOInverseKinematics: GAO Key1: %x, GAO Key2: %x, firstAndMiddleLenFromInitPos: %f, secondAndMiddleLenFromInitPos: %f, GAO Key3: %x, Flags: %d (%x), Flags(cleaned): %d (%x)",
                    gaoKey1, gaoKey2, firstAndMiddleLenFromInitPos, secondAndMiddleLenFromInitPos, gaoKey3, flags, flags, flagsCleaned, flagsCleaned);
        } else {
            return "ModifierGAOInverseKinematics";
        }
    }

    public Integer getGaoKey1() {
        return gaoKey1;
    }

    public Integer getGaoKey2() {
        return gaoKey2;
    }

    public Integer getGaoKey3() {
        return gaoKey3;
    }

    public Integer getFlags() {
        return flagsCleaned;
    }

    public BFKeyListElem getGao1KLE() {
        return gao1KLE;
    }

    public void setGao1KLE(BFKeyListElem gao1KLE) {
        this.gao1KLE = gao1KLE;
    }

    public BFKeyListElem getGao2KLE() {
        return gao2KLE;
    }

    public void setGao2KLE(BFKeyListElem gao2KLE) {
        this.gao2KLE = gao2KLE;
    }

    public BFKeyListElem getGao3KLE() {
        return gao3KLE;
    }

    public void setGao3KLE(BFKeyListElem gao3KLE) {
        this.gao3KLE = gao3KLE;
    }

    public float getFirstAndMiddleLenFromInitPos() {
        return firstAndMiddleLenFromInitPos;
    }

    public void setFirstAndMiddleLenFromInitPos(float firstAndMiddleLenFromInitPos) {
        this.firstAndMiddleLenFromInitPos = firstAndMiddleLenFromInitPos;
    }

    public float getSecondAndMiddleLenFromInitPos() {
        return secondAndMiddleLenFromInitPos;
    }

    public void setSecondAndMiddleLenFromInitPos(float secondAndMiddleLenFromInitPos) {
        this.secondAndMiddleLenFromInitPos = secondAndMiddleLenFromInitPos;
    }

    public void setFlags(Integer flags) {
        this.flagsCleaned = flags;
    }

    @Override
    public BFModifierGAOInverseKinematics duplicate() {
        BFModifierGAOInverseKinematics ret = new BFModifierGAOInverseKinematics();

        ret.unkI1 = unkI1;
        ret.flags = flags;
        ret.flagsCleaned = flagsCleaned;
        ret.firstAndMiddleLenFromInitPos = firstAndMiddleLenFromInitPos;
        ret.secondAndMiddleLenFromInitPos = secondAndMiddleLenFromInitPos;
        ret.gaoKey1 = gaoKey1;
        ret.gaoKey2 = gaoKey2;
        ret.gaoKey3 = gaoKey3;
        // multipart gao duplication reperculate handled in handleInverseKinematics() function
        ret.gao1KLE = gao1KLE;
        ret.gao2KLE = gao2KLE;
        ret.gao3KLE = gao3KLE;

        return ret;
    }
}
