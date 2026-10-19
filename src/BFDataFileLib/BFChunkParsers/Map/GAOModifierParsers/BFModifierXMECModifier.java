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
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierXMECModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    private Float substractFromLen, zPlusFactor, cntFactor, zMinLimit, vecMultFactor, boneToBoneDiffFactor;
    private Integer flags, boneNum, rank;
    private Integer[] boneIDs = new Integer[50];
    private BFGaoChunk[] boneGaos = new BFGaoChunk[50];
    Integer unk;

    // runtime
    private BFGaoChunk gao1;
    private BFGaoChunk gao2;
    private Matrix4x4[] goGlobalMatrixPtrs = new Matrix4x4[50];
    private Vector3[] positionDiffVecs = new Vector3[50];

    public BFModifierXMECModifier() {
        super();
    }

    public Integer getBoneNum() {
        return boneNum;
    }

    public BFGaoChunk getGao1() {
        return gao1;
    }

    public void setGao1(BFGaoChunk gao1) {
        this.gao1 = gao1;
    }

    public BFGaoChunk getGao2() {
        return gao2;
    }

    public void setGao2(BFGaoChunk gao2) {
        this.gao2 = gao2;
    }

    public Matrix4x4[] getGaoGlobalMatrixPtrs() {
        return goGlobalMatrixPtrs;
    }

    public Vector3[] getPositionDiffVecs() {
        return positionDiffVecs;
    }

    public Float getSubstractFromLen() {
        return substractFromLen;
    }

    public Float getZPlusFactor() {
        return zPlusFactor;
    }

    public Float getCntFactor() {
        return cntFactor;
    }

    public Float getZMinLimit() {
        return zMinLimit;
    }

    public Float getVecMultFactor() {
        return vecMultFactor;
    }

    public Float getBoneToBoneDiffFactor() {
        return boneToBoneDiffFactor;
    }

    public Integer getFlags() {
        return flags;
    }

    public BFGaoChunk[] getBoneGaos() {
        return boneGaos;
    }

    public Integer[] getBoneIDs() {
        return boneIDs;
    }

    public Integer getRank() {
        return rank;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing XMEC Modifier.");
        }

        if (!insideBinFile) {
            Integer unk1 = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1 (!insidebinfile): %d (%x)", unk1, unk1);
            }
        }

        unk = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk: %d (%x)", unk, unk);
        }

        if (unk != 0) {
            substractFromLen = ru.readFloat(false);
            zPlusFactor = ru.readFloat(false);
            cntFactor = ru.readFloat(false);
            zMinLimit = ru.readFloat(false);
            vecMultFactor = ru.readFloat(false);
            boneToBoneDiffFactor = ru.readFloat(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("substractFromLen: %f", substractFromLen);
                logF("zPlusFactor: %f", zPlusFactor);
                logF("cntFactor: %f", cntFactor);
                logF("zMinLimit: %f", zMinLimit);
                logF("vecMultFactor: %f", vecMultFactor);
                logF("boneToBoneDiffFactor: %f", boneToBoneDiffFactor);
            }

            flags = ru.readInt(false);
            boneNum = ru.readInt(false);
            rank = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("flags: %d (%x)", flags, flags);
                logF("boneNum: %d (%x)", boneNum, boneNum);
                logF("rank: %d (%x)", rank, rank);
            }

            int i;

            // runtime
            if (boneNum > 0) {
                for (i = 0; i < boneNum; i++) {
                    positionDiffVecs[i] = new Vector3();
                }
            }

            for (i = 0; i < 50; i++) {
                boneIDs[i] = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Bone[%d]: %d (%x)", i, boneIDs[i], boneIDs[i]);
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            sb.append(String.format("substractFromLen: %f, ", substractFromLen));
            sb.append(String.format("zPlusFactor: %f, ", zPlusFactor));
            sb.append(String.format("cntFactor: %f, ", cntFactor));
            sb.append(String.format("zMinLimit: %f, ", zMinLimit));
            sb.append(String.format("vecMultFactor: %f, ", vecMultFactor));
            sb.append(String.format("boneToBoneDiffFactor: %f, ", boneToBoneDiffFactor));
            sb.append(String.format("flags: %d (%x), ", flags, flags));
            sb.append(String.format("boneNum: %d (%x), ", boneNum, boneNum));
            sb.append(String.format("rank: %d (%x)", rank, rank)).append(System.lineSeparator());

            int i;
            sb.append("Bones: ");
            for (i = 0; i < boneNum; i++) {
                sb.append(String.format("%d (%x), ", boneIDs[i], boneIDs[i]));
            }
            return sb.toString();
        }
        return "XMECModifier";
    }

}
