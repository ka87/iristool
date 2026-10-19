/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierXMECModifier;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import iristool.Engine.Modifier;
import iristool.Engine.RenderUtils;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author andra
 */
public class XMEC {

    private static void xmecPositionCalc(Vector3 mainGaoGlobalPos, Matrix4x4 mtxTmpPtrsForGlobal[], int num, float substractFromLen, float pos3MinLimit) {
        int i;
        Vector3 prevVector;
        Vector3[] tmpPositions = new Vector3[50];
        Vector3 tmpPos = new Vector3();
        int iterationCounter;
        float calcdLen;

        for (iterationCounter = 0; iterationCounter < 10; iterationCounter++) {
            for (i = 0; i < num; i++) {
                tmpPositions[i] = new Vector3();
            }

            prevVector = mainGaoGlobalPos;
            for (i = 0; i < num; i++) {
                Vector3.substractTwoVectors(tmpPos, prevVector, mtxTmpPtrsForGlobal[i].getPos());
                calcdLen = Vector3.getVectorLength(tmpPos) - substractFromLen;
                Vector3.divEqualVectorInPlace(tmpPos);
                if (i > 0) {
                    Vector3.vectorMultiplyWithScalarAndSum(
                            tmpPositions[i - 1],
                            tmpPositions[i - 1],
                            tmpPos,
                            (calcdLen * -0.5f));
                    Vector3.vectorMultiplyWithScalarAndSum(
                            tmpPositions[i],
                            tmpPositions[i],
                            tmpPos,
                            (calcdLen * 0.5f));
                } else {
                    Vector3.vectorMultiplyWithScalarAndSum(tmpPositions[0], tmpPositions[0], tmpPos, calcdLen);
                }
                prevVector = mtxTmpPtrsForGlobal[i].getPos();
            }
            for (i = 0; i < num; i++) {
                Vector3.sumTwoVector(mtxTmpPtrsForGlobal[i].getPos(), mtxTmpPtrsForGlobal[i].getPos(), tmpPositions[i]);
                if (mtxTmpPtrsForGlobal[i].getPos().getZ() < pos3MinLimit) {
                    mtxTmpPtrsForGlobal[i].getPos().setZ(pos3MinLimit);
                }
            }
        }
    }

    public static void init(BFModifierXMECModifier xmec, BFGaoChunk gao) {
        if (xmec == null) {
            return;
        }
        xmec.setGao1(gao);
        xmec.setGao2(gao);
        BFGRPChunk grp = Modifier.getGroupForModifierAndGao(gao, xmec);

        if (grp == null) {
            return;
        }

        ArrayList<TreeMap<Integer, BFKeyListElem>> grpa = grp.getGrpGaoList();
        BFKeyListElem kle;

        if (grpa == null) {
            return;
        }

        for (int i = 0; i < xmec.getBoneNum(); i++) {
            if (grpa.size() > xmec.getBoneIDs()[i]) {
                kle = grpa.get(xmec.getBoneIDs()[i]).firstEntry().getValue();
                if (kle != null && kle.getParseHandler() != null) {
                    xmec.getBoneGaos()[i] = (BFGaoChunk) (kle.getParseHandler());
                    xmec.getGaoGlobalMatrixPtrs()[i] = xmec.getBoneGaos()[i].getGlobalMatrix();
                }
            }
        }
        if (xmec.getRank() != -1) {
            if (grpa.size() > xmec.getRank()) {
                kle = grpa.get(xmec.getRank()).firstEntry().getValue();
                if (kle != null && kle.getParseHandler() != null) {
                    xmec.setGao2(gao);
                }
            }
        }
    }

    public static void handleXMEC(BFModifierXMECModifier xmec, BFGaoChunk gao) {
        int boneNum;
        int i;
        Vector3 col;
        Vector3 gao2GlobalPos;
        Matrix4x4 currMatrixPtr;
        Matrix4x4 currGaoGlobal;
        Matrix4x4 gao2Global;
        Matrix4x4 tmpMatricesForGlobal[] = new Matrix4x4[50];
        Matrix4x4 mtxTmpPtrsForGlobal[] = new Matrix4x4[50];
        Vector3 tmpVecStoreForPos[] = new Vector3[50];
        float calculatedFactor;
        Vector3 temp = new Vector3();
        Vector3 temp2 = new Vector3();

        if (!xmec.isInited()) {
            init(xmec, gao);
            xmec.setInited(true);
        }

        for (i = 0; i < 50; i++) {
            tmpMatricesForGlobal[i] = new Matrix4x4();
            tmpVecStoreForPos[i] = new Vector3();
        }

        RenderUtils.calculateGlobalMatrixFromGAO(xmec.getGao1(), xmec.getGao1().getGlobalMatrix());
        if (xmec.getGao1() != xmec.getGao2()) {
            RenderUtils.calculateGlobalMatrixFromGAO(xmec.getGao2(), xmec.getGao2().getGlobalMatrix());
        }
        boneNum = xmec.getBoneNum();

        for (i = 0; i < boneNum; i++) {
            mtxTmpPtrsForGlobal[i] = tmpMatricesForGlobal[i];
            tmpMatricesForGlobal[i].set(Matrix4x4.getIdentityMatrix());
            if (xmec.getGaoGlobalMatrixPtrs()[i] != null) {
                tmpMatricesForGlobal[i].set(xmec.getGaoGlobalMatrixPtrs()[i]);
            } else {
                boneNum = i;
            }
        }

        if (boneNum != 0) {
            for (i = 0; i < boneNum; i++) {
                xmec.getPositionDiffVecs()[i].setZ(xmec.getZPlusFactor() * 0.050000001f + xmec.getPositionDiffVecs()[i].getZ());
                tmpVecStoreForPos[i].setX(mtxTmpPtrsForGlobal[i].getPos().getX());
                tmpVecStoreForPos[i].setY(mtxTmpPtrsForGlobal[i].getPos().getY());
                tmpVecStoreForPos[i].setZ(mtxTmpPtrsForGlobal[i].getPos().getZ());
            }

            calculatedFactor = xmec.getBoneToBoneDiffFactor() * 0.050000001f;
            float calculatedFactorPerBone = calculatedFactor * xmec.getCntFactor() / boneNum;
            if (boneNum > 0) {
                i = boneNum;
                do {
                    gao2Global = xmec.getGao2().getGlobalMatrix();
                    if ((xmec.getFlags() & 1) != 0) {
                        col = new Vector3(gao2Global.getM21(), gao2Global.getM22(), gao2Global.getM23());
                    } else {
                        col = new Vector3(gao2Global.getM31(), gao2Global.getM32(), gao2Global.getM33());
                    }
                    Vector3.vectorMultiplyWithScalarAndSum(xmec.getPositionDiffVecs()[i - 1], xmec.getPositionDiffVecs()[i - 1], col, calculatedFactor);
                    calculatedFactor = calculatedFactor - calculatedFactorPerBone;
                } while (!(i-- == 1));
            }

            for (i = 0; i < boneNum; i++) {
                if (mtxTmpPtrsForGlobal[i].getPos().getZ() <= xmec.getZMinLimit()) {
                    mtxTmpPtrsForGlobal[i].getPos().setZ(xmec.getZMinLimit());
                    xmec.getPositionDiffVecs()[i].setX(xmec.getPositionDiffVecs()[i].getX() * xmec.getVecMultFactor());
                    xmec.getPositionDiffVecs()[i].setY(xmec.getPositionDiffVecs()[i].getY() * xmec.getVecMultFactor());
                }
                Vector3.vectorMultiplyWithScalarAndSum(mtxTmpPtrsForGlobal[i].getPos(), mtxTmpPtrsForGlobal[i].getPos(), xmec.getPositionDiffVecs()[i], 0.050000001f);
            }

            xmecPositionCalc(xmec.getGao2().getGlobalMatrix().getPos(), mtxTmpPtrsForGlobal, boneNum, xmec.getSubstractFromLen(), xmec.getZMinLimit());

            for (i = 0; i < boneNum; i++) {
                Vector3.substractTwoVectors(xmec.getPositionDiffVecs()[i], mtxTmpPtrsForGlobal[i].getPos(), tmpVecStoreForPos[i]);
                Vector3.multiplyVectorWithScalar(xmec.getPositionDiffVecs()[i], xmec.getPositionDiffVecs()[i], 20.0f);
            }

            gao2GlobalPos = xmec.getGao2().getGlobalMatrix().getPos();
            currGaoGlobal = xmec.getGao2().getGlobalMatrix();
            i = 0;
            if (boneNum > 0) {
                while (true) {
                    currMatrixPtr = mtxTmpPtrsForGlobal[i];
                    if ((xmec.getFlags() & 1) != 0) {
                        Vector3.substractTwoVectors(temp, gao2GlobalPos, currMatrixPtr.getPos());
                        Vector3.divEqualVectorInPlace(temp);
                        currMatrixPtr.setM21(temp.getX());
                        currMatrixPtr.setM22(temp.getY());
                        currMatrixPtr.setM23(temp.getZ());
                        Vector3.crossProduct(temp2, new Vector3(currGaoGlobal.getM11(), currGaoGlobal.getM12(), currGaoGlobal.getM13()), temp);
                        Vector3.divEqualVectorInPlace(temp2);
                        currMatrixPtr.setM31(temp2.getX());
                        currMatrixPtr.setM32(temp2.getY());
                        currMatrixPtr.setM33(temp2.getZ());
                        Vector3.crossProduct(temp, new Vector3(currMatrixPtr.getM21(), currMatrixPtr.getM22(), currMatrixPtr.getM23()), new Vector3(currMatrixPtr.getM31(), currMatrixPtr.getM32(), currMatrixPtr.getM33()));
                        currMatrixPtr.setM11(temp.getX());
                        currMatrixPtr.setM12(temp.getY());
                        currMatrixPtr.setM13(temp.getZ());
                    } else {
                        Vector3.substractTwoVectors(temp, gao2GlobalPos, currMatrixPtr.getPos());
                        Vector3.divEqualVectorInPlace(temp);
                        currMatrixPtr.setM31(temp.getX());
                        currMatrixPtr.setM32(temp.getY());
                        currMatrixPtr.setM33(temp.getZ());
                        Vector3.crossProduct(temp2, temp, new Vector3(currGaoGlobal.getM11(), currGaoGlobal.getM12(), currGaoGlobal.getM13()));
                        Vector3.divEqualVectorInPlace(temp2);
                        currMatrixPtr.setM21(temp2.getX());
                        currMatrixPtr.setM22(temp2.getY());
                        currMatrixPtr.setM23(temp2.getZ());
                        Vector3.crossProduct(temp, new Vector3(currMatrixPtr.getM21(), currMatrixPtr.getM22(), currMatrixPtr.getM23()), new Vector3(currMatrixPtr.getM31(), currMatrixPtr.getM32(), currMatrixPtr.getM33()));
                        currMatrixPtr.setM11(temp.getX());
                        currMatrixPtr.setM12(temp.getY());
                        currMatrixPtr.setM13(temp.getZ());
                    }
                    ++i;
                    currGaoGlobal = currMatrixPtr;
                    gao2GlobalPos = currMatrixPtr.getPos();
                    if (i >= boneNum) {
                        break;
                    }
                }
            }

            for (i = 0; i < boneNum; i++) {
                //System.out.println(xmec.getBoneGaos()[i].getGaoName());
                xmec.getGaoGlobalMatrixPtrs()[i].set(mtxTmpPtrsForGlobal[i]);
                //System.out.println(xmec.getGaoGlobalMatrixPtrs()[i]);
                RenderUtils.calculateLocalMatrixFromGAOIfHasPivotReference(xmec.getBoneGaos()[i]);
            }
        }
    }
}
