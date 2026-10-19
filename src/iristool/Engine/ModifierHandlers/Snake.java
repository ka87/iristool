/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSnake;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import iristool.Engine.Modifier;
import iristool.Engine.RenderUtils;
import iristool.Engine.Renderer;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author andra
 */
public class Snake {

    private static void init(BFModifierSnake snake, BFGaoChunk gao) {
        int i;
        if (snake == null || snake.getUsedBoneNum() < 2) {
            return;
        }
        BFGRPChunk grp = Modifier.getGroupForModifierAndGao(gao, snake);
        // if somebody wants to display a multipart gao part without viewing multipart container firstly (no skeleton applyshape run),
        // do not crash
        if (grp == null) {
            return;
        }
                
        ArrayList<TreeMap<Integer, BFKeyListElem>> grpa = grp.getGrpGaoList();
        BFKeyListElem kle;

        if (grpa == null) {
            return;
        }
        for (i = 0; i < snake.getUsedBoneNum(); i++) {
            if (grpa.size() > snake.getBoneIDs()[i]) {
                kle = grpa.get(snake.getBoneIDs()[i]).firstEntry().getValue();
                if (kle != null && kle.getParseHandler() != null) {
                    snake.getBoneGaos()[i] = (BFGaoChunk) (kle.getParseHandler());
                    BFGaoChunk.restoreGaoInitialPosition(snake.getBoneGaos()[i]);
                    RenderUtils.calculateGlobalMatrixFromGAO(snake.getBoneGaos()[i], snake.getBoneGaos()[i].getGlobalMatrix());
                }
            } else {
                System.out.println("BUG: BFModifierSnake: boneid (" + snake.getBoneIDs()[i] + ") > multipart gao group list (" + grpa.size() + ").");
            }
        }

        Matrix4x4 currGlobalMtx;
        Matrix4x4 prevGlobalMtx;
        float pos1Diff, pos2Diff, pos3Diff;

        for (i = 1; i < snake.getUsedBoneNum(); i++) {
            currGlobalMtx = snake.getBoneGaos()[i - 1].getGlobalMatrix();
            prevGlobalMtx = snake.getBoneGaos()[i].getGlobalMatrix();
            pos1Diff = currGlobalMtx.getPos().getX() - prevGlobalMtx.getPos().getX();
            pos2Diff = currGlobalMtx.getPos().getY() - prevGlobalMtx.getPos().getY();
            pos3Diff = currGlobalMtx.getPos().getZ() - prevGlobalMtx.getPos().getZ();
            snake.getDistances()[i - 1] = (float) Math.sqrt(pos1Diff * pos1Diff + pos2Diff * pos2Diff + pos3Diff * pos3Diff);
        }
    }

    private static void computeLocalMatrixFromMainAndRefgaoGlobalMatrices(BFGaoChunk gao) {
        Matrix4x4 refInvGlobal = new Matrix4x4();
        BFGaoChunk refGao;

        if ((gao.getFlags() & 0x400000) != 0) {
            if (gao.getReferencePointGAOChunkKLE() != null && (refGao = (BFGaoChunk) (gao.getReferencePointGAOChunkKLE().getParseHandler())) != null) {
                Matrix4x4.invertMatrix(refInvGlobal, refGao.getGlobalMatrix());
                Matrix4x4.matrixMultiplyWithNormalized(gao.getLocalMatrix(), gao.getGlobalMatrix(), refInvGlobal);
                Matrix4x4.orthoNormalize(gao.getLocalMatrix());
            }
        }
    }

    public static void handleSnake(BFModifierSnake snake, BFGaoChunk gao, Renderer r) {
        int i;
        float blendFactor;
        float factor;
        float elapsedTimeAndFactorMult;
        float distanceLinearInterpolated;
        float elapsedTime;
        float bonePos1AndPos2Distance;
        Vector3 bonePosDiffVec = new Vector3();
        Vector3 tmp = new Vector3();
        Vector3 tmp2 = new Vector3();
        Vector3 tmp1 = new Vector3();
        Vector3 sight = new Vector3();
        Vector3 scale = new Vector3();
        float boneToBoneCalculatedFactor;

        if (!snake.isInited()) {
            init(snake, gao);
            snake.setInited(true);
        }

        elapsedTime = r.getElapsedTimeInSecsPerLoop();
        if (elapsedTime == 0.0f) {
            elapsedTime = 0.02f;
        }
        
        if (snake.getBoneGaos() != null && snake.getBoneGaos().length > 0 && snake.getBoneGaos()[0] != null) {
            RenderUtils.calculateGlobalMatrixFromGAO(snake.getBoneGaos()[0], snake.getBoneGaos()[0].getGlobalMatrix());
            boneToBoneCalculatedFactor = 0.0f;
            if (snake.getUsedBoneNum() > 1) {
                for (i = 1; i < snake.getUsedBoneNum(); i++) {
                    if (snake.getBoneGaos()[i - 1] == null || snake.getBoneGaos()[i] == null) {
                        break;
                    }
                    if ((snake.getHandleFlags() & 1) != 0) {
                        tmp1.setX(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM31());
                        tmp1.setY(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM32());
                        tmp1.setZ(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM33());
                        tmp2.setX(snake.getBoneGaos()[i].getGlobalMatrix().getM31());
                        tmp2.setY(snake.getBoneGaos()[i].getGlobalMatrix().getM32());
                        tmp2.setZ(snake.getBoneGaos()[i].getGlobalMatrix().getM33());
                    } else {
                        tmp1.setX(-snake.getBoneGaos()[i - 1].getGlobalMatrix().getM21());
                        tmp1.setY(-snake.getBoneGaos()[i - 1].getGlobalMatrix().getM22());
                        tmp1.setZ(-snake.getBoneGaos()[i - 1].getGlobalMatrix().getM23());
                        tmp2.setX(-snake.getBoneGaos()[i].getGlobalMatrix().getM21());
                        tmp2.setY(-snake.getBoneGaos()[i].getGlobalMatrix().getM22());
                        tmp2.setZ(-snake.getBoneGaos()[i].getGlobalMatrix().getM23());
                    }
                    blendFactor = elapsedTime * snake.getBlendOffsetter();
                    Vector3.vectorBlendRotate(tmp2, tmp1, blendFactor, tmp);
                    tmp1.set(tmp);

                    bonePosDiffVec.setX(snake.getBoneGaos()[i].getGlobalMatrix().getPos().getX() - snake.getBoneGaos()[i - 1].getGlobalMatrix().getPos().getX());
                    bonePosDiffVec.setY(snake.getBoneGaos()[i].getGlobalMatrix().getPos().getY() - snake.getBoneGaos()[i - 1].getGlobalMatrix().getPos().getY());
                    bonePosDiffVec.setZ(snake.getBoneGaos()[i].getGlobalMatrix().getPos().getZ() - snake.getBoneGaos()[i - 1].getGlobalMatrix().getPos().getZ());
                    bonePos1AndPos2Distance = (float) Math.sqrt(bonePosDiffVec.getX() * bonePosDiffVec.getX()
                            + bonePosDiffVec.getY() * bonePosDiffVec.getY() + bonePosDiffVec.getZ() * bonePosDiffVec.getZ());

                    if (i < 2) {
                        if ((snake.getHandleFlags() & 1) != 0) {
                            tmp2.setX(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM21());
                            tmp2.setY(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM22());
                            tmp2.setZ(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM23());
                        } else {
                            tmp2.setX(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM31());
                            tmp2.setY(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM32());
                            tmp2.setZ(snake.getBoneGaos()[i - 1].getGlobalMatrix().getM33());
                        }
                    } else {
                        tmp2.setX(snake.getBoneGaos()[i - 1].getGlobalMatrix().getPos().getX() - snake.getBoneGaos()[i - 2].getGlobalMatrix().getPos().getX());
                        tmp2.setY(snake.getBoneGaos()[i - 1].getGlobalMatrix().getPos().getY() - snake.getBoneGaos()[i - 2].getGlobalMatrix().getPos().getY());
                        tmp2.setZ(snake.getBoneGaos()[i - 1].getGlobalMatrix().getPos().getZ() - snake.getBoneGaos()[i - 2].getGlobalMatrix().getPos().getZ());
                    }
                    Vector3.getNormalVectorMultipliedWithScalar(tmp, tmp2, snake.getDistances()[i - 1]);
                    tmp2.set(tmp);
                    factor = (boneToBoneCalculatedFactor + snake.getBlendOffsetter()) * elapsedTime;
                    Vector3.vectorBlendRotate(bonePosDiffVec, tmp2, factor, tmp);
                    bonePosDiffVec.set(tmp);
                    boneToBoneCalculatedFactor = boneToBoneCalculatedFactor + snake.getBoneToBoneIncrementer();
                    if (boneToBoneCalculatedFactor + snake.getBlendOffsetter() < 0.0f) {
                        boneToBoneCalculatedFactor = -snake.getBlendOffsetter();
                    }
                    elapsedTimeAndFactorMult = elapsedTime * snake.getTimeFactor();
                    if (elapsedTimeAndFactorMult >= 0.0f) {
                        if (elapsedTimeAndFactorMult > 1.0f) {
                            elapsedTimeAndFactorMult = 1.0f;
                        }
                    } else {
                        elapsedTimeAndFactorMult = 0.0f;
                    }
                    distanceLinearInterpolated = (1.0f - elapsedTimeAndFactorMult) * snake.getDistances()[i - 1] + elapsedTimeAndFactorMult * bonePos1AndPos2Distance;
                    Vector3.getNormalVectorMultipliedWithScalar(tmp, bonePosDiffVec, distanceLinearInterpolated);
                    bonePosDiffVec.set(tmp);
                    Vector3.getVectorNormalizedInPlace(tmp);
                    Vector3.getVectorNormalized(sight, tmp1);

                    if ((snake.getHandleFlags() & 1) != 0) {
                        tmp.setX(-tmp.getX());
                        tmp.setY(-tmp.getY());
                        tmp.setZ(-tmp.getZ());
                        Matrix4x4.makeRotationMatrixFromSight(snake.getBoneGaos()[i].getGlobalMatrix(), tmp, sight, false);
                    } else {
                        Matrix4x4.makeRotationMatrixFromBanking(snake.getBoneGaos()[i].getGlobalMatrix(), sight, tmp, false);
                    }

                    Vector3.sumTwoVector(bonePosDiffVec, bonePosDiffVec, snake.getBoneGaos()[i - 1].getGlobalMatrix().getPos());
                    snake.getBoneGaos()[i].getGlobalMatrix().getPos().set(bonePosDiffVec);
                    Matrix4x4.getScale(scale, snake.getBoneGaos()[i].getLocalMatrix());
                    computeLocalMatrixFromMainAndRefgaoGlobalMatrices(snake.getBoneGaos()[i]);
                    Matrix4x4.setScale(snake.getBoneGaos()[i].getLocalMatrix(), scale);
                }
            }
        }
    }
}
