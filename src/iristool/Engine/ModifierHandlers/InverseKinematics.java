/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGAOInverseKinematics;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import iristool.Engine.Modifier;
import iristool.Engine.RenderUtils;

/**
 *
 * @author andra
 */
public class InverseKinematics {

    private static void calculateInverseKinematics(BFModifierGAOInverseKinematics ik, BFGaoChunk currGao) {
        BFGaoChunk gao1Or3;
        Vector3 gao1Pos;
        Vector3 currentGaoPos;
        Vector3 gao2Pos;
        int flags;
        Vector3 vec;
        double len;
        float firstMiddleLenSQ;
        Vector3 v2;
        Vector3 v1;
        float sqr;
        float factor;
        Matrix4x4 gao1Or3GlobalMtx;
        Vector3 dest2 = new Vector3();
        Vector3 tmp = new Vector3();
        Vector3 tmp2 = new Vector3();
        Vector3 dest = new Vector3();

        if (ik != null && ik.getGao1KLE() != null && ik.getGao2KLE() != null && currGao != null) {
            BFGaoChunk gao1 = (BFGaoChunk) (ik.getGao1KLE().getParseHandler());
            BFGaoChunk gao2 = (BFGaoChunk) (ik.getGao2KLE().getParseHandler());
            RenderUtils.calculateGlobalMatrixFromGAO(gao1, gao1.getGlobalMatrix());
            RenderUtils.calculateGlobalMatrixFromGAO(currGao, currGao.getGlobalMatrix());
            RenderUtils.calculateGlobalMatrixFromGAO(gao2, gao2.getGlobalMatrix());
            gao1Or3 = gao1;
            gao1Pos = gao1.getGlobalMatrix().getPos();
            currentGaoPos = currGao.getGlobalMatrix().getPos();
            gao2Pos = gao2.getGlobalMatrix().getPos();
            if (ik.getGao3KLE() != null) {
                gao1Or3 = (BFGaoChunk) (ik.getGao3KLE().getParseHandler());
            }
            gao1Or3GlobalMtx = gao1Or3.getGlobalMatrix();
            flags = ik.getFlags();

            if ((flags & 1) != 0) {
                vec = new Vector3(gao1Or3GlobalMtx.getM11(), gao1Or3GlobalMtx.getM12(), gao1Or3GlobalMtx.getM13());
            } else if ((flags & 2) != 0) {
                vec = new Vector3(gao1Or3GlobalMtx.getM21(), gao1Or3GlobalMtx.getM22(), gao1Or3GlobalMtx.getM23());
            } else {
                vec = new Vector3(gao1Or3GlobalMtx.getM31(), gao1Or3GlobalMtx.getM32(), gao1Or3GlobalMtx.getM33());
            }

            len = Vector3.getDistanceBetweenTwoVectors(gao1Pos, gao2Pos);
            firstMiddleLenSQ = ik.getFirstAndMiddleLenFromInitPos() * ik.getFirstAndMiddleLenFromInitPos();
            factor = (float) ((firstMiddleLenSQ - ik.getSecondAndMiddleLenFromInitPos() * ik.getSecondAndMiddleLenFromInitPos() + len * len)
                    / (len + len));
            /*            
            System.out.println("pos1:" + gao1Pos + ", " + gao1.getGaoName());
            System.out.println("pos2:" + gao2Pos + ", " + gao2.getGaoName());
            System.out.println("len:" + len);            
            System.out.println("fmsq:" + firstMiddleLenSQ);
            System.out.println("factor:" + factor);
            System.out.println("diff:"+ (firstMiddleLenSQ - factor * factor));
             */
            
            if ((firstMiddleLenSQ - factor * factor) <= 0) {
                System.out.println("BUG: InverseKinematicsModifier (firstMiddleLen^2 - factor^2) is negative or zero ["
                        + (firstMiddleLenSQ - factor * factor) + "], disabling calculation.");
                return;
            }

            sqr = RenderUtils.getRootForFloat((firstMiddleLenSQ - factor * factor));
            tmp2.setX(gao2Pos.getX() - gao1Pos.getX());
            tmp2.setY(gao2Pos.getY() - gao1Pos.getY());
            tmp2.setZ(gao2Pos.getZ() - gao1Pos.getZ());
            Vector3.getVectorNormalizedInPlace(tmp2);
            tmp2.setX(tmp2.getX() * factor);
            tmp2.setY(tmp2.getY() * factor);
            tmp2.setZ(tmp2.getZ() * factor);

            if ((ik.getFlags() & 8) == 0) {
                v1 = vec;
                v2 = tmp2;
            } else {
                v2 = vec;
                v1 = tmp2;
            }
            Vector3.crossProduct(dest, v1, v2);
            Vector3.crossProduct(dest2, tmp2, dest);
            //System.out.println("dest: " + dest);
            //System.out.println("dest2: " + dest2);
            Vector3.getVectorNormalizedInPlace(dest2);
            currentGaoPos.setX(tmp2.getX() + gao1Pos.getX());
            currentGaoPos.setY(tmp2.getY() + gao1Pos.getY());
            currentGaoPos.setZ(tmp2.getZ() + gao1Pos.getZ());
            currentGaoPos.setX(dest2.getX() * sqr + currentGaoPos.getX());
            currentGaoPos.setY(dest2.getY() * sqr + currentGaoPos.getY());
            currentGaoPos.setZ(dest2.getZ() * sqr + currentGaoPos.getZ());
            dest2.setX(currentGaoPos.getX() - gao1Pos.getX());
            dest2.setY(currentGaoPos.getY() - gao1Pos.getY());
            dest2.setZ(currentGaoPos.getZ() - gao1Pos.getZ());
            Vector3.crossProduct(tmp2, new Vector3(gao1.getGlobalMatrix().getM21(), gao1.getGlobalMatrix().getM22(), gao1.getGlobalMatrix().getM23()), dest2);
            Vector3.getVectorNormalized(tmp, tmp2);
            gao1.getGlobalMatrix().setM11(tmp.getX());
            gao1.getGlobalMatrix().setM12(tmp.getY());
            gao1.getGlobalMatrix().setM13(tmp.getZ());
            Vector3.getVectorNormalized(tmp, dest2);
            gao1.getGlobalMatrix().setM31(tmp.getX());
            gao1.getGlobalMatrix().setM32(tmp.getY());
            gao1.getGlobalMatrix().setM33(tmp.getZ());
            Vector3.crossProduct(tmp, new Vector3(gao1.getGlobalMatrix().getM31(), gao1.getGlobalMatrix().getM32(), gao1.getGlobalMatrix().getM33()),
                    new Vector3(gao1.getGlobalMatrix().getM11(), gao1.getGlobalMatrix().getM12(), gao1.getGlobalMatrix().getM13()));
            gao1.getGlobalMatrix().setM21(tmp.getX());
            gao1.getGlobalMatrix().setM22(tmp.getY());
            gao1.getGlobalMatrix().setM23(tmp.getZ());
            RenderUtils.calculateLocalMatrixFromGAOIfHasPivotReference(gao1);
            dest2.setX(gao2Pos.getX() - currentGaoPos.getX());
            dest2.setY(gao2Pos.getY() - currentGaoPos.getY());
            dest2.setZ(gao2Pos.getZ() - currentGaoPos.getZ());
            Vector3.getVectorNormalized(tmp, dest2);
            currGao.getGlobalMatrix().setM31(tmp.getX());
            currGao.getGlobalMatrix().setM32(tmp.getY());
            currGao.getGlobalMatrix().setM33(tmp.getZ());
            Vector3.crossProduct(tmp2, new Vector3(currGao.getGlobalMatrix().getM21(), currGao.getGlobalMatrix().getM22(), currGao.getGlobalMatrix().getM23()), dest2);
            Vector3.getVectorNormalized(tmp, tmp2);
            currGao.getGlobalMatrix().setM11(tmp.getX());
            currGao.getGlobalMatrix().setM12(tmp.getY());
            currGao.getGlobalMatrix().setM13(tmp.getZ());
            Vector3.crossProduct(tmp, new Vector3(currGao.getGlobalMatrix().getM31(), currGao.getGlobalMatrix().getM32(), currGao.getGlobalMatrix().getM33()),
                    new Vector3(currGao.getGlobalMatrix().getM11(), currGao.getGlobalMatrix().getM12(), currGao.getGlobalMatrix().getM13()));
            currGao.getGlobalMatrix().setM21(tmp.getX());
            currGao.getGlobalMatrix().setM22(tmp.getY());
            currGao.getGlobalMatrix().setM23(tmp.getZ());
            RenderUtils.calculateLocalMatrixFromGAOIfHasPivotReference(currGao);
            RenderUtils.calculateLocalMatrixFromGAOIfHasPivotReference(gao2);
        }
    }

    public static void handleInverseKinematics(BFModifierGAOInverseKinematics ikMod, BFGaoChunk currGao) {
        BFGRPChunk group = null;
        int flags;
        BFGaoChunk gao1;
        BFGaoChunk gao2;
        Vector3 middle;
        Vector3 first;
        Vector3 second;
        BFKeyListElem gao1KLEBak;
        BFKeyListElem gao2KLEBak;
        BFKeyListElem gao3KLEBak;
        Vector3 substracted = new Vector3();

        gao1KLEBak = ikMod.getGao1KLE();
        gao2KLEBak = ikMod.getGao2KLE();
        gao3KLEBak = ikMod.getGao3KLE();

        flags = ikMod.getFlags();

        // for multipart gaos
        if ((flags & 0x10) != 0) {
            group = Modifier.getGroupForModifierAndGao(currGao, ikMod);
            if (group != null) {
                BFKeyListElem kleTmp;
                if (group.getGrpGaoList().size() > ikMod.getGaoKey1()) {
                    kleTmp = group.getGrpGaoList().get(ikMod.getGaoKey1()).firstEntry().getValue();
                    ikMod.setGao1KLE(kleTmp);
                } else {
                    System.out.format("BUG: id1: %x out of bounds group elem list.\n", ikMod.getGaoKey1());
                }
                if (group.getGrpGaoList().size() > ikMod.getGaoKey2()) {
                    kleTmp = group.getGrpGaoList().get(ikMod.getGaoKey2()).firstEntry().getValue();
                    ikMod.setGao2KLE(kleTmp);
                } else {
                    System.out.format("BUG: id2: %x out of bounds group elem list.\n", ikMod.getGaoKey2());
                }
                if (group.getGrpGaoList().size() > ikMod.getGaoKey3()) {
                    kleTmp = group.getGrpGaoList().get(ikMod.getGaoKey3()).firstEntry().getValue();
                    ikMod.setGao3KLE(kleTmp);
                } else {
                    System.out.format("BUG: id3: %x out of bounds group elem list.\n", ikMod.getGaoKey3());
                }
            }
        }

        //flags |=0x20;
//        System.out.println("ik gao: " + currGao.getGaoName());
        if ((flags & 0x10) == 0 || group != null) {
            if ((flags & 0x20) != 0) {
                if ((flags & 0x40) == 0) {
                    gao1 = ikMod.getGao1KLE() != null ? (BFGaoChunk) (ikMod.getGao1KLE().getParseHandler()) : null;
                    ikMod.setFlags(flags | 0x40);
                    if (gao1 != null) {
                        gao2 = ikMod.getGao2KLE() != null ? (BFGaoChunk) (ikMod.getGao2KLE().getParseHandler()) : null;
                        if (gao2 != null) {
                            if ((gao1.getFlags() & 0x10000) != 0 && (currGao.getFlags() & 0x10000) != 0 && (gao2.getFlags() & 0x10000) != 0) {
                                middle = currGao.getGlobalMatrixInitial().getPos();
                                first = gao1.getGlobalMatrixInitial().getPos();
                                Vector3.substractTwoVectors(substracted, first, middle);
                                ikMod.setFirstAndMiddleLenFromInitPos(Vector3.getVectorLength(substracted));
                                second = gao2.getGlobalMatrixInitial().getPos();
                                Vector3.substractTwoVectors(substracted, second, middle);
                                ikMod.setSecondAndMiddleLenFromInitPos(Vector3.getVectorLength(substracted));
                            }
                        }
                    }
                }
            }
        }
        if ((ikMod.getFlags() & 0x10) == 0 || group != null) {
            calculateInverseKinematics(ikMod, currGao);
        }

        ikMod.setGao1KLE(gao1KLEBak);
        ikMod.setGao2KLE(gao2KLEBak);
        ikMod.setGao3KLE(gao3KLEBak);
    }
}
