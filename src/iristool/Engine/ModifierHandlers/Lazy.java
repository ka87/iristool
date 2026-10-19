/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierLazyModifier;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.Quaternion;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import iristool.Engine.RenderUtils;
import iristool.Engine.Renderer;
import java.util.ArrayList;

/**
 *
 * @author andra
 */
public class Lazy {

    private static class AfterRenderingCallElem {

        private BFGaoChunk gao;
        private BFModifierLazyModifier lazy;

        public BFGaoChunk getGao() {
            return gao;
        }

        public void setGao(BFGaoChunk gao) {
            this.gao = gao;
        }

        public BFModifierLazyModifier getLazy() {
            return lazy;
        }

        public void setLazy(BFModifierLazyModifier lazy) {
            this.lazy = lazy;
        }
    }

    private static float elapsedTime = 0.0f;
    private static float zAxisMultiplierBak, positionFactorMultiplierBak, lazynessBak;
    private static int flagsBak;
    private static Vector3 limiterVecMinBak = new Vector3();
    private static Vector3 limiterVecMaxBak = new Vector3();
    private static ArrayList<AfterRenderingCallElem> variableSetListAfterRendering = new ArrayList();
    
    private static void computeForFlag1000(BFModifierLazyModifier lazy, Matrix4x4 mainGlobalMtx, Matrix4x4 refGlobalMtx) {
        float pos1;
        float pos2;
        float pos3;
        float tmpX;
        float tmpY;
        float tmpZ;
        float normX;
        float normY;
        float normZ;
        float tmp4;
        float tmp5;
        float tmp6;
        float tmp7;
        float tmp8;
        float tmp9;
        float firstMtxColumnMult;
        float secondMtxColumnMult;
        float thirdMtxColumnMult;
        float mtx1And2DiffX;
        float mtx1And2DiffY;
        float mtx1And2DiffZ;
        float lazyAndMtx2Distance;
        float distNormalized;
        float loopInvTime;
        float twoMatricesDistance;
        Vector3 mtxPosDiff = new Vector3();
        Quaternion quat = new Quaternion();
        Matrix4x4 refGaoMtxAndMtx2Mult = new Matrix4x4();
        Matrix4x4 mmtx1Copied = new Matrix4x4();
        Matrix4x4 mtxFromQuaternion = new Matrix4x4();

        if (refGlobalMtx != null) {
            pos1 = lazy.getMainGaoMtxCache().getPos().getX();
            pos2 = lazy.getMainGaoMtxCache().getPos().getY();
            pos3 = lazy.getMainGaoMtxCache().getPos().getZ();
            Matrix4x4.matrixMultiplyWithNormalized(refGaoMtxAndMtx2Mult, lazy.getMainGaoMtx(), refGlobalMtx);
            tmpX = (refGaoMtxAndMtx2Mult.getPos().getX() - lazy.getMainGaoMtxCache().getPos().getX()) * lazy.getLazyness();
            tmpY = (refGaoMtxAndMtx2Mult.getPos().getY() - lazy.getMainGaoMtxCache().getPos().getY()) * lazy.getLazyness();
            tmpZ = (refGaoMtxAndMtx2Mult.getPos().getZ() - lazy.getMainGaoMtxCache().getPos().getZ()) * lazy.getLazyness();
            lazy.setPositionFactorX(lazy.getPositionFactorX() + tmpX);
            lazy.setPositionFactorY(lazy.getPositionFactorY() + tmpY);
            lazy.setPositionFactorZ(lazy.getPositionFactorZ() + tmpZ);
            lazy.setPositionFactorZ(lazy.getZAxisMultiplier() + lazy.getPositionFactorZ());
            lazy.setPositionFactorX(lazy.getPositionFactorMultiplier() * lazy.getPositionFactorX());
            lazy.setPositionFactorY(lazy.getPositionFactorMultiplier() * lazy.getPositionFactorY());
            lazy.setPositionFactorZ(lazy.getPositionFactorMultiplier() * lazy.getPositionFactorZ());
            lazy.getMainGaoMtxCache().getPos().setX(lazy.getMainGaoMtxCache().getPos().getX() + (lazy.getPositionFactorX() * elapsedTime));
            lazy.getMainGaoMtxCache().getPos().setY(lazy.getMainGaoMtxCache().getPos().getY() + (lazy.getPositionFactorY() * elapsedTime));
            lazy.getMainGaoMtxCache().getPos().setZ(lazy.getMainGaoMtxCache().getPos().getZ() + (lazy.getPositionFactorZ() * elapsedTime));
            if ((lazy.getFlags() & 0x400) != 0) {
                tmp4 = lazy.getMainGaoMtxCache().getPos().getX() - refGaoMtxAndMtx2Mult.getPos().getX();
                tmp5 = lazy.getMainGaoMtxCache().getPos().getY() - refGaoMtxAndMtx2Mult.getPos().getY();
                tmp6 = lazy.getMainGaoMtxCache().getPos().getZ() - refGaoMtxAndMtx2Mult.getPos().getZ();
                firstMtxColumnMult = refGaoMtxAndMtx2Mult.getM13() * tmp6 + refGaoMtxAndMtx2Mult.getM12() * tmp5 + refGaoMtxAndMtx2Mult.getM11() * tmp4;
                secondMtxColumnMult = refGaoMtxAndMtx2Mult.getM23() * tmp6 + refGaoMtxAndMtx2Mult.getM22() * tmp5 + refGaoMtxAndMtx2Mult.getM21() * tmp4;
                thirdMtxColumnMult = tmp4 * refGaoMtxAndMtx2Mult.getM31() + refGaoMtxAndMtx2Mult.getM33() * tmp6 + refGaoMtxAndMtx2Mult.getM32() * tmp5;
                if (firstMtxColumnMult < lazy.getLimiterVecMin().getX()) {
                    firstMtxColumnMult = lazy.getLimiterVecMin().getX();
                }
                if (secondMtxColumnMult < lazy.getLimiterVecMin().getY()) {
                    secondMtxColumnMult = lazy.getLimiterVecMin().getY();
                }
                if (thirdMtxColumnMult < lazy.getLimiterVecMin().getZ()) {
                    thirdMtxColumnMult = lazy.getLimiterVecMin().getZ();
                }
                if (firstMtxColumnMult > lazy.getLimiterVecMax().getX()) {
                    firstMtxColumnMult = lazy.getLimiterVecMax().getX();
                }
                if (secondMtxColumnMult > lazy.getLimiterVecMax().getY()) {
                    secondMtxColumnMult = lazy.getLimiterVecMax().getY();
                }
                if (thirdMtxColumnMult > lazy.getLimiterVecMax().getZ()) {
                    thirdMtxColumnMult = lazy.getLimiterVecMax().getZ();
                }
                tmp7 = (refGaoMtxAndMtx2Mult.getM21() * secondMtxColumnMult) + (refGaoMtxAndMtx2Mult.getM11() * firstMtxColumnMult);
                tmp8 = (refGaoMtxAndMtx2Mult.getM22() * secondMtxColumnMult) + (refGaoMtxAndMtx2Mult.getM12() * firstMtxColumnMult);
                tmp9 = (refGaoMtxAndMtx2Mult.getM23() * secondMtxColumnMult) + (refGaoMtxAndMtx2Mult.getM13() * firstMtxColumnMult);
                lazy.getMainGaoMtxCache().getPos().setX((refGaoMtxAndMtx2Mult.getM31() * thirdMtxColumnMult) + tmp7 + refGaoMtxAndMtx2Mult.getPos().getX());
                lazy.getMainGaoMtxCache().getPos().setY((refGaoMtxAndMtx2Mult.getM32() * thirdMtxColumnMult) + tmp8 + refGaoMtxAndMtx2Mult.getPos().getY());
                lazy.getMainGaoMtxCache().getPos().setZ((thirdMtxColumnMult * refGaoMtxAndMtx2Mult.getM33()) + tmp9 + refGaoMtxAndMtx2Mult.getPos().getZ());
            }
            mtx1And2DiffX = mainGlobalMtx.getPos().getX() - refGlobalMtx.getPos().getX();
            mtx1And2DiffY = mainGlobalMtx.getPos().getY() - refGlobalMtx.getPos().getY();
            mtx1And2DiffZ = mainGlobalMtx.getPos().getZ() - refGlobalMtx.getPos().getZ();
            mtxPosDiff.setX(lazy.getMainGaoMtxCache().getPos().getX() - refGlobalMtx.getPos().getX());
            mtxPosDiff.setY(lazy.getMainGaoMtxCache().getPos().getY() - refGlobalMtx.getPos().getY());
            mtxPosDiff.setZ(lazy.getMainGaoMtxCache().getPos().getZ() - refGlobalMtx.getPos().getZ());
            twoMatricesDistance = (float) Math.sqrt(mtx1And2DiffY * mtx1And2DiffY + mtx1And2DiffX * mtx1And2DiffX + mtx1And2DiffZ * mtx1And2DiffZ);
            lazyAndMtx2Distance = (float) Math.sqrt(mtxPosDiff.getZ() * mtxPosDiff.getZ() + mtxPosDiff.getY() * mtxPosDiff.getY() + mtxPosDiff.getX() * mtxPosDiff.getX());
            if (lazyAndMtx2Distance != 0.0) {
                distNormalized = twoMatricesDistance * (1.0f / lazyAndMtx2Distance);
                lazy.getMainGaoMtxCache().getPos().setX(refGlobalMtx.getPos().getX() + (mtxPosDiff.getX() * distNormalized));
                lazy.getMainGaoMtxCache().getPos().setY(refGlobalMtx.getPos().getY() + (mtxPosDiff.getY() * distNormalized));
                lazy.getMainGaoMtxCache().getPos().setZ(refGlobalMtx.getPos().getZ() + (mtxPosDiff.getZ() * distNormalized));
                if (twoMatricesDistance != 0.0f) {
                    normX = mtx1And2DiffX * (1.0f / twoMatricesDistance);
                    normY = mtx1And2DiffY * (1.0f / twoMatricesDistance);
                    normZ = mtx1And2DiffZ * (1.0f / twoMatricesDistance);
                    mtxPosDiff.setX(normX + mtxPosDiff.getX() * (1.0f / lazyAndMtx2Distance));
                    mtxPosDiff.setY(normY + mtxPosDiff.getY() * (1.0f / lazyAndMtx2Distance));
                    mtxPosDiff.setZ(normZ + mtxPosDiff.getZ() * (1.0f / lazyAndMtx2Distance));
                    Vector3.getVectorNormalizedInPlace(mtxPosDiff);
                    quat.setF1(normY * mtxPosDiff.getZ() - normZ * mtxPosDiff.getY());
                    quat.setF2(normZ * mtxPosDiff.getX() - mtxPosDiff.getZ() * normX);
                    quat.setF3(mtxPosDiff.getY() * normX - normY * mtxPosDiff.getX());
                    quat.setF4(normZ * mtxPosDiff.getZ() + normY * mtxPosDiff.getY() + normX * mtxPosDiff.getX());
                    Quaternion.convertQuaternionToMatrix(mtxFromQuaternion, quat);
                    mmtx1Copied.set(mainGlobalMtx);
                    Matrix4x4.mult33Matrix(mainGlobalMtx, mmtx1Copied, mtxFromQuaternion, true);
                }
            }
            lazy.setPositionFactorX(lazy.getMainGaoMtxCache().getPos().getX() - pos1);
            lazy.setPositionFactorY(lazy.getMainGaoMtxCache().getPos().getY() - pos2);
            lazy.setPositionFactorZ(lazy.getMainGaoMtxCache().getPos().getZ() - pos3);
            loopInvTime = 1.0f / elapsedTime;
            lazy.setPositionFactorX(loopInvTime * lazy.getPositionFactorX());
            lazy.setPositionFactorY(loopInvTime * lazy.getPositionFactorY());
            lazy.setPositionFactorZ(loopInvTime * lazy.getPositionFactorZ());
            mainGlobalMtx.getPos().setX(lazy.getMainGaoMtxCache().getPos().getX());
            mainGlobalMtx.getPos().setY(lazy.getMainGaoMtxCache().getPos().getY());
            mainGlobalMtx.getPos().setZ(lazy.getMainGaoMtxCache().getPos().getZ());
        }
    }

    private static void init(BFModifierLazyModifier lazy, BFGaoChunk gao) {
        if (gao != null) {
            if ((gao.getFlags() & 0x400000) != 0) {
                if (gao.getLocalMatrix() != null) {
                    RenderUtils.calculateGlobalMatrixFromGAO(gao, gao.getGlobalMatrix());
                    lazy.getMainGaoMtxCache().set(gao.getGlobalMatrix());
                }
            } else {
                lazy.getMainGaoMtxCache().set(gao.getGlobalMatrix());
            }
        }
    }

    private static void compute(BFModifierLazyModifier lazy, Renderer r, Matrix4x4 mainGlobalMtx, Matrix4x4 refGlobalMtx) {
        int flags;
        float multSum;
        float lazynessInv;
        float timeCounter;
        float xDiff;
        float yDiff;
        float yDiffMultiplied;
        float zDiffMultiplied;
        float firstMtxColumnMult;
        float secondMtxColumnMult;
        float thirdMtxColumnMult;
        float len;
        float len2;
        Vector3 tmpVec2 = new Vector3();
        float multiplier;
        Vector3 pos2 = new Vector3();
        Vector3 tmpVec = new Vector3();
        Matrix4x4 mtxTemp2 = new Matrix4x4();
        Matrix4x4 mtxTemp = new Matrix4x4();
        Quaternion quat = new Quaternion();
        float multForATimeSlice;
        float zDiff;
        float xDiffMultiplied;

        flags = lazy.getFlags();
        if ((flags & 0x1000) != 0) {
            computeForFlag1000(lazy, mainGlobalMtx, refGlobalMtx);
        } else {
            multSum = 1.0f;
            lazynessInv = 1.0f - lazy.getLazyness();
            if (lazynessInv < 0.0f) {
                lazynessInv = 0.0f;
            }
            multForATimeSlice = 1.0f - lazynessInv * lazynessInv;
            timeCounter = 0.0f;
            if (refGlobalMtx == null) {
                flags &= ~0x8;
            }
            if ((flags & 8) != 0) {
                flags &= ~0x2;
            }
            if (elapsedTime > 0.0f) {
                do {
                    multSum = multSum * multForATimeSlice;
                    timeCounter = timeCounter + 0.0033333334f;
                } while (timeCounter < elapsedTime);
            }

            multiplier = 1.0f - multSum;

            if ((flags & 0x9) != 0) {
                xDiff = mainGlobalMtx.getPos().getX() - lazy.getMainGaoMtxCache().getPos().getX();
                yDiff = mainGlobalMtx.getPos().getY() - lazy.getMainGaoMtxCache().getPos().getY();
                zDiff = mainGlobalMtx.getPos().getZ() - lazy.getMainGaoMtxCache().getPos().getZ();
                xDiffMultiplied = xDiff * multiplier;
                yDiffMultiplied = yDiff * multiplier;
                zDiffMultiplied = zDiff * multiplier;
                lazy.getMainGaoMtxCache().getPos().setX(lazy.getMainGaoMtxCache().getPos().getX() + xDiffMultiplied);
                lazy.getMainGaoMtxCache().getPos().setY(lazy.getMainGaoMtxCache().getPos().getY() + yDiffMultiplied);
                lazy.getMainGaoMtxCache().getPos().setZ(lazy.getMainGaoMtxCache().getPos().getZ() + zDiffMultiplied);
                if ((flags & 0x8) != 0) {
                    lazy.getMainGaoMtxCache().getPos().setZ(multiplier * lazy.getZAxisMultiplier() + lazy.getMainGaoMtxCache().getPos().getZ());
                    if ((flags & 0x400) != 0) {
                        Matrix4x4.matrixMultiplyWithNormalized(mtxTemp2, lazy.getMainGaoMtx(), refGlobalMtx);
                        tmpVec2.setX(lazy.getMainGaoMtxCache().getPos().getX() - mtxTemp2.getPos().getX());
                        tmpVec2.setY(lazy.getMainGaoMtxCache().getPos().getY() - mtxTemp2.getPos().getY());
                        tmpVec2.setZ(lazy.getMainGaoMtxCache().getPos().getZ() - mtxTemp2.getPos().getZ());
                        firstMtxColumnMult = mtxTemp2.getM13() * tmpVec2.getZ() + mtxTemp2.getM12() * tmpVec2.getY() + mtxTemp2.getM11() * tmpVec2.getX();
                        secondMtxColumnMult = mtxTemp2.getM23() * tmpVec2.getZ() + mtxTemp2.getM22() * tmpVec2.getY() + mtxTemp2.getM21() * tmpVec2.getX();
                        thirdMtxColumnMult = mtxTemp2.getM33() * tmpVec2.getZ() + mtxTemp2.getM32() * tmpVec2.getY() + mtxTemp2.getM31() * tmpVec2.getX();
                        if (firstMtxColumnMult < lazy.getLimiterVecMin().getX()) {
                            firstMtxColumnMult = lazy.getLimiterVecMin().getX();
                        }
                        if (secondMtxColumnMult < lazy.getLimiterVecMin().getY()) {
                            secondMtxColumnMult = lazy.getLimiterVecMin().getY();
                        }
                        if (thirdMtxColumnMult < lazy.getLimiterVecMin().getZ()) {
                            thirdMtxColumnMult = lazy.getLimiterVecMin().getZ();
                        }
                        if (firstMtxColumnMult > lazy.getLimiterVecMax().getX()) {
                            firstMtxColumnMult = lazy.getLimiterVecMax().getX();
                        }
                        if (secondMtxColumnMult > lazy.getLimiterVecMax().getY()) {
                            secondMtxColumnMult = lazy.getLimiterVecMax().getY();
                        }
                        if (thirdMtxColumnMult > lazy.getLimiterVecMax().getZ()) {
                            thirdMtxColumnMult = lazy.getLimiterVecMax().getZ();
                        }
                        tmpVec2.setX(mtxTemp2.getM11() * firstMtxColumnMult);
                        tmpVec2.setY(mtxTemp2.getM12() * firstMtxColumnMult);
                        tmpVec2.setZ(mtxTemp2.getM13() * firstMtxColumnMult);
                        Vector3.addAnotherMultipliedVec(tmpVec2, tmpVec2, new Vector3(mtxTemp2.getM21(), mtxTemp2.getM22(), mtxTemp2.getM23()), secondMtxColumnMult);
                        Vector3.addAnotherMultipliedVec(tmpVec2, tmpVec2, new Vector3(mtxTemp2.getM31(), mtxTemp2.getM32(), mtxTemp2.getM33()), thirdMtxColumnMult);

                        lazy.getMainGaoMtxCache().getPos().setX(tmpVec2.getX() + mtxTemp2.getPos().getX());
                        lazy.getMainGaoMtxCache().getPos().setY(tmpVec2.getY() + mtxTemp2.getPos().getY());
                        lazy.getMainGaoMtxCache().getPos().setZ(tmpVec2.getZ() + mtxTemp2.getPos().getZ());
                    }
                    tmpVec2.setX(mainGlobalMtx.getPos().getX() - refGlobalMtx.getPos().getX());
                    tmpVec2.setY(mainGlobalMtx.getPos().getY() - refGlobalMtx.getPos().getY());
                    tmpVec2.setZ(mainGlobalMtx.getPos().getZ() - refGlobalMtx.getPos().getZ());

                    pos2.setX(lazy.getMainGaoMtxCache().getPos().getX() - refGlobalMtx.getPos().getX());
                    pos2.setY(lazy.getMainGaoMtxCache().getPos().getY() - refGlobalMtx.getPos().getY());
                    pos2.setZ(lazy.getMainGaoMtxCache().getPos().getZ() - refGlobalMtx.getPos().getZ());

                    len = (float) Math.sqrt(tmpVec2.getX() * tmpVec2.getX() + tmpVec2.getZ() * tmpVec2.getZ() + tmpVec2.getY() * tmpVec2.getY());
                    len2 = (float) Math.sqrt(pos2.getZ() * pos2.getZ() + pos2.getY() * pos2.getY() + pos2.getX() * pos2.getX());
                    if (len2 != 0.0f) {
                        Vector3.addAnotherMultipliedVec(lazy.getMainGaoMtxCache().getPos(), refGlobalMtx.getPos(), pos2, ((1.0f / len2) * len));
                        if (len != 0.0f) {
                            Vector3.multiplyVectorWithScalar(tmpVec2, tmpVec2, (1.0f / len));
                            Vector3.multiplyVectorWithScalar(pos2, pos2, (1.0f / len2));
                            pos2.setX(tmpVec2.getX() + pos2.getX());
                            pos2.setY(tmpVec2.getY() + pos2.getY());
                            pos2.setZ(tmpVec2.getZ() + pos2.getZ());
                            Vector3.getVectorNormalizedInPlace(pos2);
                            Vector3.crossProduct(tmpVec, tmpVec2, pos2);
                            quat.setF1(tmpVec.getX());
                            quat.setF2(tmpVec.getY());
                            quat.setF3(tmpVec.getZ());
                            quat.setF4(tmpVec2.getZ() * pos2.getZ() + tmpVec2.getY() * pos2.getY() + tmpVec2.getX() * pos2.getX());

                            Quaternion.convertQuaternionToMatrix(mtxTemp2, quat);
                            mtxTemp.set(mainGlobalMtx);
                            Matrix4x4.mult33Matrix(mainGlobalMtx, mtxTemp, mtxTemp2, true);
                        }
                    }
                }
                mainGlobalMtx.getPos().setX(lazy.getMainGaoMtxCache().getPos().getX());
                mainGlobalMtx.getPos().setY(lazy.getMainGaoMtxCache().getPos().getY());
                mainGlobalMtx.getPos().setZ(lazy.getMainGaoMtxCache().getPos().getZ());
            }
            if ((flags & 0x2) != 0) {
                lazy.getMainGaoMtxCache().setM11(((mainGlobalMtx.getM11() - lazy.getMainGaoMtxCache().getM11()) * multiplier) + lazy.getMainGaoMtxCache().getM11());
                lazy.getMainGaoMtxCache().setM12(((mainGlobalMtx.getM12() - lazy.getMainGaoMtxCache().getM12()) * multiplier) + lazy.getMainGaoMtxCache().getM12());
                lazy.getMainGaoMtxCache().setM13(((mainGlobalMtx.getM13() - lazy.getMainGaoMtxCache().getM13()) * multiplier) + lazy.getMainGaoMtxCache().getM13());
                lazy.getMainGaoMtxCache().setM21(lazy.getMainGaoMtxCache().getM21() + ((mainGlobalMtx.getM21() - lazy.getMainGaoMtxCache().getM21()) * multiplier));
                lazy.getMainGaoMtxCache().setM22(lazy.getMainGaoMtxCache().getM22() + ((mainGlobalMtx.getM22() - lazy.getMainGaoMtxCache().getM22()) * multiplier));
                lazy.getMainGaoMtxCache().setM23(lazy.getMainGaoMtxCache().getM23() + ((mainGlobalMtx.getM23() - lazy.getMainGaoMtxCache().getM23()) * multiplier));
                lazy.getMainGaoMtxCache().setM31(lazy.getMainGaoMtxCache().getM31() + ((mainGlobalMtx.getM31() - lazy.getMainGaoMtxCache().getM31()) * multiplier));
                lazy.getMainGaoMtxCache().setM32(lazy.getMainGaoMtxCache().getM32() + ((mainGlobalMtx.getM32() - lazy.getMainGaoMtxCache().getM32()) * multiplier));
                lazy.getMainGaoMtxCache().setM33(lazy.getMainGaoMtxCache().getM33() + ((mainGlobalMtx.getM33() - lazy.getMainGaoMtxCache().getM33()) * multiplier));
                Matrix4x4.orthoNormalize(lazy.getMainGaoMtxCache());
                mainGlobalMtx.setM11(lazy.getMainGaoMtxCache().getM11());
                mainGlobalMtx.setM12(lazy.getMainGaoMtxCache().getM12());
                mainGlobalMtx.setM13(lazy.getMainGaoMtxCache().getM13());
                mainGlobalMtx.setM21(lazy.getMainGaoMtxCache().getM21());
                mainGlobalMtx.setM22(lazy.getMainGaoMtxCache().getM22());
                mainGlobalMtx.setM23(lazy.getMainGaoMtxCache().getM23());
                mainGlobalMtx.setM31(lazy.getMainGaoMtxCache().getM31());
                mainGlobalMtx.setM32(lazy.getMainGaoMtxCache().getM32());
                mainGlobalMtx.setM33(lazy.getMainGaoMtxCache().getM33());
            }
            if ((flags & 0x4) != 0) {
                tmpVec2.set(1.0f, 1.0f, 1.0f);
                if ((mainGlobalMtx.getFlags() & 0x8) != 0) {
                    if ((lazy.getMainGaoMtxCache().getFlags() & 0x8) != 0) {
                        tmpVec2.setX(lazy.getMainGaoMtxCache().getM14());
                        tmpVec2.setY(lazy.getMainGaoMtxCache().getM24());
                        tmpVec2.setZ(lazy.getMainGaoMtxCache().getM34());
                    } else {
                        lazy.getMainGaoMtxCache().setFlags(lazy.getMainGaoMtxCache().getFlags() | 0x8);
                    }
                    pos2.setX(mainGlobalMtx.getM14() - tmpVec2.getX());
                    pos2.setY(mainGlobalMtx.getM24() - tmpVec2.getY());
                    pos2.setZ(mainGlobalMtx.getM34() - tmpVec2.getZ());
                    Vector3.addAnotherMultipliedVec(tmpVec2, tmpVec2, pos2, multiplier);
                    mainGlobalMtx.setM14(tmpVec2.getX());
                    lazy.getMainGaoMtxCache().setM14(tmpVec2.getX());
                    mainGlobalMtx.setM24(tmpVec2.getY());
                    lazy.getMainGaoMtxCache().setM24(tmpVec2.getY());
                    mainGlobalMtx.setM34(tmpVec2.getZ());
                    lazy.getMainGaoMtxCache().setM34(tmpVec2.getZ());
                }
            }
        }
    }

    private static void getMatrixPartsByFlags(BFModifierLazyModifier lazy, BFGaoChunk srcgao, BFGaoChunk dest) {
        if ((srcgao.getFlags() & 0x400000) != 0) {
            if ((lazy.getFlags() & 0x10) != 0) {
                dest.getLocalMatrix().getPos().set(srcgao.getLocalMatrix().getPos());
            }
            if ((lazy.getFlags() & 0x20) != 0) {
                dest.getLocalMatrix().setM11(srcgao.getLocalMatrix().getM11());
                dest.getLocalMatrix().setM12(srcgao.getLocalMatrix().getM12());
                dest.getLocalMatrix().setM13(srcgao.getLocalMatrix().getM13());
                dest.getLocalMatrix().setM21(srcgao.getLocalMatrix().getM21());
                dest.getLocalMatrix().setM22(srcgao.getLocalMatrix().getM22());
                dest.getLocalMatrix().setM23(srcgao.getLocalMatrix().getM23());
                dest.getLocalMatrix().setM31(srcgao.getLocalMatrix().getM31());
                dest.getLocalMatrix().setM32(srcgao.getLocalMatrix().getM32());
                dest.getLocalMatrix().setM33(srcgao.getLocalMatrix().getM33());
            }
            if ((lazy.getFlags() & 0x40) != 0) {
                if ((srcgao.getLocalMatrix().getFlags() & 0x8) != 0) {
                    dest.getLocalMatrix().setFlags(dest.getLocalMatrix().getFlags() | 0x8);
                    dest.getLocalMatrix().setM14(srcgao.getLocalMatrix().getM14());
                    dest.getLocalMatrix().setM24(srcgao.getLocalMatrix().getM24());
                    dest.getLocalMatrix().setM34(srcgao.getLocalMatrix().getM34());
                } else {
                    dest.getLocalMatrix().setFlags(dest.getLocalMatrix().getFlags() & ~0x8);
                }
            }
        } else {
            if ((lazy.getFlags() & 0x20) != 0) {

                dest.getLocalMatrix().setM11(srcgao.getGlobalMatrix().getM11());
                dest.getLocalMatrix().setM12(srcgao.getGlobalMatrix().getM12());
                dest.getLocalMatrix().setM13(srcgao.getGlobalMatrix().getM13());
                dest.getLocalMatrix().setM21(srcgao.getGlobalMatrix().getM21());
                dest.getLocalMatrix().setM22(srcgao.getGlobalMatrix().getM22());
                dest.getLocalMatrix().setM23(srcgao.getGlobalMatrix().getM23());
                dest.getLocalMatrix().setM31(srcgao.getGlobalMatrix().getM31());
                dest.getLocalMatrix().setM32(srcgao.getGlobalMatrix().getM32());
                dest.getLocalMatrix().setM33(srcgao.getGlobalMatrix().getM33());
            }
            if ((lazy.getFlags() & 0x40) != 0) {
                if ((srcgao.getGlobalMatrix().getFlags() & 0x8) != 0) {
                    dest.getLocalMatrix().setFlags(dest.getLocalMatrix().getFlags() | 0x8);
                    dest.getLocalMatrix().setM14(srcgao.getGlobalMatrix().getM14());
                    dest.getLocalMatrix().setM24(srcgao.getGlobalMatrix().getM24());
                    dest.getLocalMatrix().setM34(srcgao.getGlobalMatrix().getM34());
                } else {
                    dest.getLocalMatrix().setFlags(dest.getLocalMatrix().getFlags() & ~0x8);
                }
            }
        }
    }

    public static void handleLazy(BFModifierLazyModifier lazy, BFGaoChunk mainGao, Renderer r) {
        BFGaoChunk refGao;
        Matrix4x4 refGaoGlobalInv = new Matrix4x4();
        int i;

        if (!lazy.isInited()) {
            init(lazy, mainGao);
            lazy.setInited(true);
        }
        
        elapsedTime = r.getElapsedTimeInSecsPerLoop();

        zAxisMultiplierBak = lazy.getZAxisMultiplier();
        positionFactorMultiplierBak = lazy.getPositionFactorMultiplier();
        lazynessBak = lazy.getLazyness();
        flagsBak = lazy.getFlags();
        limiterVecMinBak.set(lazy.getLimiterVecMin());
        limiterVecMaxBak.set(lazy.getLimiterVecMax());
        lazy.setZAxisMultiplierBakLocal(zAxisMultiplierBak);
        lazy.setPositionFactorMultiplierBakLocal(positionFactorMultiplierBak);
        lazy.setLazynessBakLocal(lazynessBak);
        lazy.setFlagsBakLocal(flagsBak);
        lazy.getLimiterVecMinBakLocal().set(limiterVecMinBak);
        lazy.getLimiterVecMaxBakLocal().set(limiterVecMaxBak);

        if ((lazy.getFlags() & 0x10000000) == 0) {// wasn't handled in this loop yet
            if ((mainGao.getFlags() & 0x400000) != 0) {
                BFKeyListElem kle = mainGao.getReferencePointGAOChunkKLE();
                if (kle == null || kle.getParseHandler() == null) {
                    System.out.println("BUG: refgao is null for: " + mainGao.getGaoName());
                    return;
                }
                refGao = (BFGaoChunk) (kle.getParseHandler());
                if (refGao != null) {
                    if ((refGao.getFlags() & 0x2000) != 0 && refGao.getModifiersArray() != null) {
                        BFChunkParseInterface cpi;
                        for (i = 0; i < refGao.getModifiersArray().size(); i++) {
                            if ((cpi = refGao.getModifiersArray().get(i)) instanceof BFModifierLazyModifier
                                    && (((BFModifierLazyModifier) cpi).getHandleFlags() & 0xa000000) == 0 && (((BFModifierLazyModifier) cpi).getHandleFlags() & 0x1000000) != 0) {
                                handleLazy((BFModifierLazyModifier) cpi, refGao, r);
                            }
                        }
                    }
                    if ((lazy.getFlags() & 0x80) != 0) {
                        lazy.setFlags(flagsBak);
                    }
                    if ((lazy.getFlags() & 0x100) != 0) {
                        lazy.setLazyness(lazynessBak);
                        lazy.setZAxisMultiplier(zAxisMultiplierBak);
                        lazy.setPositionFactorMultiplier(positionFactorMultiplierBak);
                    }
                    if ((lazy.getFlags() & 0x800) != 0) {
                        lazy.getLimiterVecMin().set(limiterVecMinBak);
                        lazy.getLimiterVecMax().set(limiterVecMaxBak);
                    }
                    if ((lazy.getFlags() & 0x70) != 0) {
                        getMatrixPartsByFlags(lazy, refGao, mainGao);
                    }
                    RenderUtils.calculateGlobalMatrixFromGAO(mainGao, mainGao.getGlobalMatrix());
                    lazy.getMainGaoMtx().set(mainGao.getLocalMatrix());
                    compute(lazy, r, mainGao.getGlobalMatrix(), refGao.getGlobalMatrix());
                    Matrix4x4.invertMatrix(refGaoGlobalInv, refGao.getGlobalMatrix());
                    Matrix4x4.matrixMultiplyWithNormalized(mainGao.getLocalMatrix(), mainGao.getGlobalMatrix(), refGaoGlobalInv);
                }
            } else {
                lazy.getMainGaoMtx().set(mainGao.getGlobalMatrix());
                compute(lazy, r, mainGao.getGlobalMatrix(), null);
            }

            AfterRenderingCallElem arce = new AfterRenderingCallElem();
            arce.setGao(mainGao);
            lazy.setFlags(lazy.getFlags() | 0x10000000);
            arce.setLazy(lazy);
            variableSetListAfterRendering.add(arce);
        }
    }

    private static void setVariablesAfterRenderingForAnElem(AfterRenderingCallElem arce) {
        arce.getLazy().setZAxisMultiplier(arce.getLazy().getZAxisMultiplierBakLocal());
        arce.getLazy().setPositionFactorMultiplier(arce.getLazy().getPositionFactorMultiplierBakLocal());
        arce.getLazy().setLazyness(arce.getLazy().getLazynessBakLocal());
        arce.getLazy().getLimiterVecMin().set(arce.getLazy().getLimiterVecMinBakLocal());
        arce.getLazy().getLimiterVecMax().set(arce.getLazy().getLimiterVecMaxBakLocal());
        arce.getLazy().setFlags(arce.getLazy().getFlagsBakLocal() & ~0x10000000);

        if ((arce.getGao().getFlags() & 0x400000) != 0) {
            arce.getGao().getLocalMatrix().set(arce.getLazy().getMainGaoMtx());
        } else {
            arce.getGao().getGlobalMatrix().set(arce.getLazy().getMainGaoMtx());
        }
    }

    public static void variablesSetAfterRenderingCall() {
        if (variableSetListAfterRendering == null) {
            return;
        }
        for (int i = 0; i < variableSetListAfterRendering.size(); i++) {
            setVariablesAfterRenderingForAnElem(variableSetListAfterRendering.get(i));
        }
        variableSetListAfterRendering.clear();
    }

}
