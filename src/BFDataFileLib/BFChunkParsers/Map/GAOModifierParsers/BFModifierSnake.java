/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierSnake extends BFModifierBaseClass implements BFChunkParseInterface {

    public static final int MAXBONENUM = 20;
    
    Integer unk1;
    Integer usedBoneNum;
    Float blendOffsetter, timeFactor, boneToBoneIncrementer;
    Integer[] boneIDs;
    BFGaoChunk boneGaos[];
    float distances[];

    public BFModifierSnake() {
        super();
        boneGaos = new BFGaoChunk[MAXBONENUM];
        distances = new float[MAXBONENUM];
    }

    public BFGaoChunk[] getBoneGaos() {
        return boneGaos;
    }

    public float[] getDistances() {
        return distances;
    }

    public Integer getUnk1() {
        return unk1;
    }

    public Integer getUsedBoneNum() {
        return usedBoneNum;
    }

    public Float getBlendOffsetter() {
        return blendOffsetter;
    }

    public Float getTimeFactor() {
        return timeFactor;
    }

    public Float getBoneToBoneIncrementer() {
        return boneToBoneIncrementer;
    }

    public Integer[] getBoneIDs() {
        return boneIDs;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Snake Modifier (Type 14)");
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
        }

        if (!buildFromParsed) {
            usedBoneNum = ru.readInt(false);
            blendOffsetter = ru.readFloat(false);
            timeFactor = ru.readFloat(false);
            boneToBoneIncrementer = ru.readFloat(false);
        } else {
            wu.writeInt(usedBoneNum);
            wu.writeFloat(blendOffsetter);
            wu.writeFloat(timeFactor);
            wu.writeFloat(boneToBoneIncrementer);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("usedBoneNum: %d", usedBoneNum);
            logF("blendOffsetter: %f, timeFactor: %f, boneToBoneIncrementer: %f", blendOffsetter, timeFactor, boneToBoneIncrementer);
        }

        if (!buildFromParsed) {
            boneIDs = new Integer[20];
        }

        for (int i = 0; i < 20; i++) {
            if (!buildFromParsed) {
                boneIDs[i] = ru.readInt(false);
            } else {
                wu.writeInt(boneIDs[i]);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("BoneID[%d]: %d (%x)", i, boneIDs[i], boneIDs[i]);
            }
        }

        return true;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("ModifierSnake: unk1(!insidebinfile): %x (%d), usedBoneNum: %d, blendOffsetter: %f, timeFactor: %f, boneToBoneIncrementer: %f",
                    unk1, unk1, usedBoneNum, blendOffsetter, timeFactor, boneToBoneIncrementer)).append(System.lineSeparator());
            sb.append("Bones: ");
            for (int i = 0; i < MAXBONENUM; i++) {
                sb.append(String.format("%d (%x), ", boneIDs[i], boneIDs[i]));
            }
            sb.append(System.lineSeparator());
            return sb.toString();
        } else {
            return "ModifierSnake";
        }
    }

    @Override
    public BFModifierSnake duplicate() {
        BFModifierSnake tmp = new BFModifierSnake();
        tmp.setSelfKey(this.getSelfKey());
        tmp.setHandleFlags(this.getHandleFlags());
        tmp.unk1 = this.unk1;
        tmp.usedBoneNum = this.usedBoneNum;
        tmp.blendOffsetter = this.blendOffsetter;
        tmp.timeFactor = this.timeFactor;
        tmp.boneToBoneIncrementer = this.boneToBoneIncrementer;
        if (this.boneIDs != null) {
            tmp.boneIDs = new Integer[this.boneIDs.length];
            System.arraycopy(this.boneIDs, 0, tmp.boneIDs, 0, this.boneIDs.length);

        }
        return tmp;
    }
}
