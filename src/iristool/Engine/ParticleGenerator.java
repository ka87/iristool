package iristool.Engine;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserParticleGenerator;
import Common.DataType.GeometryTriangleChunk;
import Common.DataType.GeometryTriangleData;
import Common.DataType.Matrix4x4;
import Common.DataType.ParticleElem;
import Common.DataType.UV;
import Common.DataType.Vector3;
import com.jogamp.opengl.GL4;
import static iristool.Engine.OpenGLFunctions.getVertexNormals;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andra
 */
public class ParticleGenerator {

    private static final UV[] UVData = new UV[]{new UV(0.0f, 1.0f - 0.0f), new UV(1.0f, 1.0f - 0.0f), new UV(1.0f, 1.0f - 1.0f), new UV(0.0f, 1.0f - 1.0f)};
    private static float mvMTmp[] = new float[16];
    private static Matrix4x4 cameraWorldSpace = new Matrix4x4();
    private static Matrix4x4 pagViewMatrixTemp = new Matrix4x4();
    private static boolean isDebug = false;

    private static void manageParticlesLifecycle(BFGaoChunk gao, GAOInnerParserParticleGenerator pag, float elapsedTime) {
        float elapsedTimeTmp;
        ParticleElem currParticle;
        int currVertId;
        int elapsedTimeInt;
        float nextRand;
        int vertNumTmp;
        int timeIntCnt;
        float v26, v27;
        Matrix4x4 mtx;
        float case400And800Mult;
        double v37;
        double v38;
        float v39;
        float v40;
        float v41;
        float elapsedTimeInMin;
        int i = 0;
        boolean shouldContinue = false;

        elapsedTimeTmp = elapsedTime + pag.getElapsedtimeRelated();
        currVertId = pag.getActPAGVertNum();
        elapsedTimeInt = (int) (elapsedTimeTmp * 60.0f);
        elapsedTimeInMin = elapsedTimeInt * 0.016666999f;// 1/60
        if (isDebug) {
            System.out.format("elapsedTime: %f, elapsedTimeRelated: %f, elapsedTimeTmp: %f,  elapsedTimeInt: %d, elapsedTimeInMin: %f\n",
                    elapsedTime, pag.getElapsedtimeRelated(), elapsedTimeTmp, elapsedTimeInt, elapsedTimeInMin);
        }
        pag.setElapsedtimeRelated(elapsedTimeTmp - elapsedTimeInMin);
        if (currVertId == 0) {
            return;
        }
        do {
            --currVertId;
            currParticle = pag.getPAGElemData()[i];
            if (isDebug) {
                System.out.format("vertexRelatedCalcByTime Particle: i: %d, unkFlagsCanBe1Or2: %x (elapsedTimeMinDownCount: %f, elapsedTimeInMin: %f)\n",
                        i, currParticle.getParticleState(), currParticle.getElapsedTimeMinDownCount(), elapsedTimeInMin);
            }
            for (; currParticle.getElapsedTimeMinDownCount() < elapsedTimeInMin;
                    currParticle.setInvElapsedTimeDownCount(1.0f / nextRand)) {
                shouldContinue = false;
                if ((currParticle.getParticleState() & 1) != 0) {
                    currParticle.setParticleState(0);
                    nextRand = RenderUtils.randShort() * 0.000030518509f * (pag.getRandMaxVal1() - pag.getRandMinVal1()) + pag.getRandMinVal1();
                } else {
                    if ((currParticle.getParticleState() & 2) != 0) {
                        vertNumTmp = pag.getActPAGVertNum() - 1;
                        pag.setActPAGVertNum(vertNumTmp);
                        if (currVertId != 0) {
                            // remove current particle elem (curr particle is set to last particle elem, and elem num is decreased)
                            if (isDebug) {
                                System.out.println("Removing particle: (" + i + ")");
                            }
                            currParticle.set(pag.getPAGElemData()[vertNumTmp]);
                            shouldContinue = true;
                            break;
                        }
                        return;
                    }
                    currParticle.setParticleState(2);
                    nextRand = RenderUtils.randShort() * 0.000030518509f * (pag.getRandMaxVal2() - pag.getRandMinVal2()) + pag.getRandMinVal2();
                }
                currParticle.setElapsedTimeMinDownCount(nextRand);
            }
            if (shouldContinue) {
                shouldContinue = false;
                continue;
            }
            currParticle.setElapsedTimeMinDownCount(currParticle.getElapsedTimeMinDownCount() - elapsedTimeInMin);
            currParticle.setElapsedTimeMinUpCount(elapsedTimeInMin + currParticle.getElapsedTimeMinUpCount());
            if ((pag.getFlags() & 0x1000) != 0) {
                if ((currParticle.getUnkCounter() & 0x8000) == 0) {
                    currParticle.setUnkCounter(65537 * currParticle.getUnkCounter());
                } else {
                    int hi = (currParticle.getUnkCounter() >>> 16);
                    currParticle.setUnkCounter(currParticle.getUnkCounter() & 0xffff);
                    currParticle.setUnkCounter((((hi - (currParticle.getUnkCounter() & 0x7fff)) << 16) | currParticle.getUnkCounter()));
                }
            }
            if (elapsedTimeInt != 0) {
                timeIntCnt = elapsedTimeInt;
                do {
                    v39 = pag.getVec().getX() * 0.016666999f;
                    v40 = pag.getVec().getY() * 0.016666999f;
                    v41 = pag.getVec().getZ() * 0.016666999f;
                    currParticle.setPosX(currParticle.getPosX() + v39 * 0.0083330004f);
                    currParticle.setPosY(currParticle.getPosY() + v40 * 0.0083330004f);
                    currParticle.setPosZ(currParticle.getPosZ() + v41 * 0.0083330004f);
                    currParticle.setPosX(currParticle.getPosX() + currParticle.getVertPlusOffsetX() * 0.016666999f);
                    currParticle.setPosY(currParticle.getPosY() + currParticle.getVertPlusOffsetY() * 0.016666999f);
                    currParticle.setPosZ(currParticle.getPosZ() + currParticle.getVertPlusOffsetZ() * 0.016666999f);
                    if ((pag.getFlags() & 0x8000) != 0) {
                        currParticle.setVertPlusOffsetX(v39 + currParticle.getVertPlusOffsetX());
                        currParticle.setVertPlusOffsetY(v40 + currParticle.getVertPlusOffsetY());
                        currParticle.setVertPlusOffsetZ(v41 + currParticle.getVertPlusOffsetZ());
                    }
                    if ((pag.getFlags() & 0x400) != 0
                            && (currParticle.getPosZ() - currParticle.getParticleSize2()) < pag.getSize2Min()) {
                        mtx = gao.getGlobalMatrix();
                        v26 = currParticle.getPosX() - mtx.getPos().getX();
                        v27 = currParticle.getPosY() - mtx.getPos().getY();
                        if (pag.getDisableUnkFlagsCase400IfThisSet() == 0.0f
                                || ((pag.getDisableUnkFlagsCase400IfThisSet() * pag.getDisableUnkFlagsCase400IfThisSet() * v27) + v27) > v26) { // TODO: check assembly
                            currParticle.setPosZ(currParticle.getParticleSize2() + pag.getSize2Min());
                            case400And800Mult = -(pag.getNegateOffsetMultiplierForUnkflags400() * currParticle.getVertPlusOffsetZ());

                            currParticle.setVertPlusOffsetZ(case400And800Mult);
                        } else {
                            // this is same                            
                            if ((pag.getFlags() & 0x2000) != 0) {
                                currParticle.setVertPlusOffsetX(pag.getUnkFlagsHas2000Multiplier() * currParticle.getVertPlusOffsetX());
                                currParticle.setVertPlusOffsetY(pag.getUnkFlagsHas2000Multiplier() * currParticle.getVertPlusOffsetY());
                                currParticle.setVertPlusOffsetZ(pag.getUnkFlagsHas2000Multiplier() * currParticle.getVertPlusOffsetZ());
                            }
                            --timeIntCnt;
                            // <--
                            continue;
                        }
                    } else if ((pag.getFlags() & 0x800) != 0
                            && (currParticle.getParticleSize2() + currParticle.getPosZ()) > pag.getSize2Max()) {
                        currParticle.setPosZ(pag.getSize2Max() - currParticle.getParticleSize2());
                        currParticle.setVertPlusOffsetZ(-(pag.getNegateOffsetMultiplierForUnkflags800() * currParticle.getVertPlusOffsetZ()));
                    } else {
                        currParticle.setVertPlusOffsetX(v39 + currParticle.getVertPlusOffsetX());
                        currParticle.setVertPlusOffsetY(v40 + currParticle.getVertPlusOffsetY());
                        currParticle.setVertPlusOffsetZ(v41 + currParticle.getVertPlusOffsetZ());
                    }

                    // same as this
                    if ((pag.getFlags() & 0x2000) != 0) {
                        currParticle.setVertPlusOffsetX(pag.getUnkFlagsHas2000Multiplier() * currParticle.getVertPlusOffsetX());
                        currParticle.setVertPlusOffsetY(pag.getUnkFlagsHas2000Multiplier() * currParticle.getVertPlusOffsetY());
                        currParticle.setVertPlusOffsetZ(pag.getUnkFlagsHas2000Multiplier() * currParticle.getVertPlusOffsetZ());
                    }
                    --timeIntCnt;
                    // <--
                } while (timeIntCnt != 0);
            }
            if (pag.getDecalXMultipilier() != 0.0f) {
                v37 = pag.getDecalXMultipilier() * currParticle.getElapsedTimeMinUpCount() * 162.975 + RenderUtils.getUnknownMagic();
                currParticle.setPosX(RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v37) & 0x3ff)] * elapsedTimeInMin
                        + currParticle.getPosX());
            }
            if (pag.getDecalYMultipilier() != 0.0f) {
                v38 = pag.getDecalYMultipilier() * currParticle.getElapsedTimeMinUpCount() * 162.975 + RenderUtils.getUnknownMagic();
                currParticle.setPosY(RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v38) & 0x3ff)] * elapsedTimeInMin
                        + currParticle.getPosY());
            }
            ++i;
        } while (currVertId != 0);
    }

    private static void addParticlesIfNeeded(BFGaoChunk gao, GAOInnerParserParticleGenerator pagIN, float elapsedTime) {
        Matrix4x4 globalMtx;
        double elapsedTimePlusAnotherFloat;
        float floatToIntConverted;
        int vertNum;
        int floatToIntLimiter;
        ParticleElem particle;
        float v24;
        float particleSizeTmp;
        float r;
        float v34;
        float v38;
        float v42;
        float v47;
        float v48;
        float v52;
        float v65;
        float v72;
        float v76;
        float v80;
        float v100;
        float v104;
        float v105;
        float v107;
        float v112;
        float v115;
        float v116;
        float v121;
        float v123;
        float v127;
        float v130;
        float v136;
        float v139;
        float v141;
        double v153;
        float floatToIntConvertedTmp;
        float floatToIntConverted_2a;
        float floatToIntConverted_2b;
        float v159;
        float r2;
        float v162;
        float v163;
        double v167;
        double v169;
        double v170;
        double v171;
        double v172;
        double v173;
        double factor;
        double rand;
        double factor1;
        double factor2;
        float floatToIntConvertedCurr;
        GAOInnerParserGeometry geo = null;
        Vector3 vertices[] = null;
        int vertId = 0;

        if (pagIN.getGaoForVertices() != null && pagIN.getGaoForVertices().getGeoChunkKLE() != null && pagIN.getGaoForVertices().getGeoChunkKLE().getParseHandler() != null) {
            BFGaoInnerChunkParser icp = (BFGaoInnerChunkParser) (pagIN.getGaoForVertices().getGeoChunkKLE().getParseHandler());
            if (icp.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY) {
                geo = (GAOInnerParserGeometry) (icp.getGaoInner());
                if (pagIN.getParticleType() == 6 && (geo == null || geo.getVertices() == null)) {
                    System.out.println("PAG: " + gao.getGaoName() + ": no vertices for particle type 6, returning.");
                    return;
                }
                vertices = geo.getVertices();
            }
        }
        if (pagIN.getParticleType() == 6) {
            // TODO: TransformedVerticesSnapshotModifier support for vertices source
            if (pagIN.getGaoForGlobalMatrix() != null) {
                globalMtx = pagIN.getGaoForGlobalMatrix().getGlobalMatrix();
            } else if (pagIN.getGaoForVertices() != null) {
                globalMtx = pagIN.getGaoForVertices().getGlobalMatrix();
            } else {
                System.out.println("PAG: " + gao.getGaoName() + ": no globalMatrix source, returnning.");
                return;
            }
        } else {
            globalMtx = gao.getGlobalMatrix();
        }

        if (pagIN.getTimeDividerMain() != 0.0f) {
            elapsedTimePlusAnotherFloat = elapsedTime + pagIN.getElapsedTimeRemained();
            //v11 = elapsedTimePlusAnotherFloat * pagIN.getTimeDividerMain();
            floatToIntConverted = ((int) (elapsedTimePlusAnotherFloat));
            floatToIntConvertedTmp = floatToIntConverted;
            pagIN.setElapsedTimeRemained((float) (elapsedTimePlusAnotherFloat - floatToIntConverted / pagIN.getTimeDividerMain()));
            vertNum = pagIN.getActPAGVertNum();
            floatToIntLimiter = pagIN.getMaxPAGVertNum();
            if (vertNum + floatToIntConverted >= floatToIntLimiter) {
                floatToIntConvertedTmp = floatToIntLimiter - vertNum - 1;
            }
            if (floatToIntConvertedTmp > 0) {
                floatToIntConvertedCurr = floatToIntConvertedTmp;
                while (true) {
                    particle = pagIN.getPAGElemData()[pagIN.getActPAGVertNum()];
                    pagIN.setActPAGVertNum(pagIN.getActPAGVertNum() + 1);
                    particle.setElapsedTimeMinDownCount(0);
                    particle.setElapsedTimeMinUpCount(0);
                    if (pagIN.getGlobalMaxMultiplier() != 0.0f) {
                        particle.setParticleState(1);
                        particle.setElapsedTimeMinDownCount(RenderUtils.randShort() * 0.000030518509f * (pagIN.getGlobalMaxMultiplier() - pagIN.getGlobalMinMultiplier()) + pagIN.getGlobalMinMultiplier());
                    }
                    if (particle.getElapsedTimeMinDownCount() == 0.0f) {
                        particle.setParticleState(0);
                        v24 = RenderUtils.randShort() * 0.000030518509f * (pagIN.getRandMaxVal1() - pagIN.getRandMinVal1()) + pagIN.getRandMinVal1();
                        particle.setElapsedTimeMinDownCount(v24);
                        if (v24 == 0.0 && pagIN.getRandMaxVal2() != 0.0f) {
                            particle.setParticleState(2);
                            particle.setElapsedTimeMinDownCount(RenderUtils.randShort() * 0.000030518509f * (pagIN.getRandMaxVal2() - pagIN.getRandMinVal2()) + pagIN.getRandMinVal2());
                        }
                    }
                    particle.setInvElapsedTimeDownCount(1.0f / particle.getElapsedTimeMinDownCount());
                    particleSizeTmp = RenderUtils.randShort() * 0.000030518509f * (pagIN.getVecMultMax() - pagIN.getVecMultMin()) + pagIN.getVecMultMin();
                    particle.setParticleSize1(particleSizeTmp);
                    if ((pagIN.getFlags() & 4) == 0) {
                        particleSizeTmp = RenderUtils.randShort() * 0.000030518509f * (pagIN.getVecMult2WOunkFlags4Max() - pagIN.getVecMult2WOunkFlags4Min()) + pagIN.getVecMult2WOunkFlags4Min();
                    }
                    particle.setDecalOffsetForCheck(particleSizeTmp);
                    switch (pagIN.getParticleType()) {
                        case 0:
                            particle.setPosX(globalMtx.getPos().getX());
                            particle.setPosY(globalMtx.getPos().getY());
                            particle.setPosZ(globalMtx.getPos().getZ());
                            break;
                        case 1:
                            r = (float) RenderUtils.randShort();
                            v34 = (r * 0.000030518509f + r * 0.000030518509f - 1.0f)
                                    * pagIN.getParticleType1RandMultiplier();
                            particle.setPosX(v34 * globalMtx.getM11() + globalMtx.getPos().getX());
                            particle.setPosY(globalMtx.getPos().getY() + globalMtx.getM12() * v34);
                            particle.setPosZ(globalMtx.getPos().getZ() + globalMtx.getM13() * v34);
                            r = (float) RenderUtils.randShort();
                            v38 = (r * 0.000030518509f + r * 0.000030518509f - 1.0f) * pagIN.getGlobalMtxMultiplier();
                            particle.setPosX(v38 * globalMtx.getM31() + particle.getPosX());
                            particle.setPosY(particle.getPosY() + globalMtx.getM32() * v38);
                            particle.setPosZ(particle.getPosZ() + globalMtx.getM33() * v38);
                            break;
                        case 2:
                            v47 = (float) RenderUtils.randShort() * 0.000030518509f * 12.566371f;
                            v159 = (float) RenderUtils.randShort() * 0.000030518509f * pagIN.getParticleType1RandMultiplier();
                            v48 = RenderUtils.getDecalByFloatOffsettedBy256(v47) * v159;
                            particle.setPosX(v48 * globalMtx.getM11() + globalMtx.getPos().getX());
                            particle.setPosY(globalMtx.getPos().getY() + globalMtx.getM12() * v48);
                            particle.setPosZ(globalMtx.getPos().getZ() + globalMtx.getM13() * v48);
                            v167 = 162.975 * v47 + RenderUtils.getUnknownMagic();
                            v52 = RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v167) & 0x3ff)] * v159;
                            particle.setPosX(v52 * globalMtx.getM31() + particle.getPosX());
                            particle.setPosY(particle.getPosY() + globalMtx.getM32() * v52);
                            particle.setPosZ(particle.getPosZ() + globalMtx.getM33() * v52);
                            break;
                        case 3:
                            v47 = (float) RenderUtils.randShort() * 0.000030518509f * 12.566371f;
                            v159 = (float) RenderUtils.randShort() * 0.000030518509f * pagIN.getParticleType1RandMultiplier();
                            v48 = RenderUtils.getDecalByFloatOffsettedBy256(v47) * v159;
                            particle.setPosX(v48 * globalMtx.getM11() + globalMtx.getPos().getX());
                            particle.setPosY(globalMtx.getPos().getY() + globalMtx.getM12() * v48);
                            particle.setPosZ(globalMtx.getPos().getZ() + globalMtx.getM13() * v48);
                            v167 = 162.975 * v47 + RenderUtils.getUnknownMagic();
                            v52 = RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v167) & 0x3ff)] * v159;
                            particle.setPosX(v52 * globalMtx.getM31() + particle.getPosX());
                            particle.setPosY(particle.getPosY() + globalMtx.getM23() * v52);
                            particle.setPosZ(particle.getPosZ() + globalMtx.getM33() * v52);
                            v65 = -((float) RenderUtils.randShort() * 0.000030518509f * pagIN.getGlobalMtxMultiplier());
                            particle.setPosX(v65 * globalMtx.getM21() + particle.getPosX());
                            particle.setPosY(particle.getPosY() + globalMtx.getM22() * v65);
                            particle.setPosZ(particle.getPosZ() + globalMtx.getM23() * v65);
                            break;
                        case 4:
                            r = (float) RenderUtils.randShort() * 0.000030518509f * 6.2831855f;
                            floatToIntConverted_2a = RenderUtils.randShort() * 0.000030518509f * 3.1415927f - 1.5707964f;
                            r2 = RenderUtils.randShort() * 0.000030518509f * pagIN.getParticleType1RandMultiplier();
                            v72 = RenderUtils.getDecalByFloatOffsettedBy256(r) * RenderUtils.getDecalByFloatOffsettedBy256(floatToIntConverted_2a) * r2;
                            particle.setPosX(v72 * globalMtx.getM11() + globalMtx.getPos().getX());
                            particle.setPosY(globalMtx.getPos().getY() + globalMtx.getM12() * v72);
                            particle.setPosZ(globalMtx.getPos().getZ() + globalMtx.getM13() * v72);
                            v169 = 162.975 * r + RenderUtils.getUnknownMagic();
                            v76 = RenderUtils.getDecalByFloatOffsettedBy256(floatToIntConverted_2a)
                                    * RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v169) & 0x3ff)]
                                    * r2;
                            particle.setPosX(v76 * globalMtx.getM21() + particle.getPosX());
                            particle.setPosY(particle.getPosY() + globalMtx.getM22() * v76);
                            particle.setPosZ(particle.getPosZ() + globalMtx.getM23() * v76);
                            v170 = 162.975 * floatToIntConverted_2a + RenderUtils.getUnknownMagic();
                            v80 = RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v170) & 0x3ff)] * r2;
                            particle.setPosX(v80 * globalMtx.getM31() + particle.getPosX());
                            particle.setPosY(particle.getPosY() + globalMtx.getM32() * v80);
                            particle.setPosZ(particle.getPosZ() + globalMtx.getM33() * v80);
                            break;
                        case 5:
                            rand = RenderUtils.randShort();
                            factor1 = ((rand * 0.000030518509) + (rand * 0.000030518509) - 1.0) * pagIN.getParticleType1RandMultiplier();
                            particle.setPosX(globalMtx.getPos().getX() + (float) (globalMtx.getM11() * factor1));
                            particle.setPosY(globalMtx.getPos().getY() + (float) (globalMtx.getM12() * factor1));
                            particle.setPosZ(globalMtx.getPos().getZ() + (float) (globalMtx.getM13() * factor1));
                            rand = RenderUtils.randShort();
                            factor2 = ((rand * 0.000030518509) + (rand * 0.000030518509) - 1.0) * pagIN.getGlobalMtxMultiplier();
                            particle.setPosX(particle.getPosX() + (float) (globalMtx.getM31() * factor2));
                            particle.setPosY(particle.getPosY() + (float) (globalMtx.getM32() * factor2));
                            particle.setPosZ(particle.getPosZ() + (float) (globalMtx.getM33() * factor2));
                            rand = -(RenderUtils.randShort() * 0.000030518509 * pagIN.getPartType5GlobalMtxRndMultiplier());
                            particle.setPosX(particle.getPosX() + (float) (globalMtx.getM21() * rand));
                            particle.setPosY(particle.getPosY() + (float) (globalMtx.getM22() * rand));
                            particle.setPosZ(particle.getPosZ() + (float) (globalMtx.getM23() * rand));
                            break;
                        case 6:
                            vertId = vertices.length * RenderUtils.getElapsedTimeBasedRandom(Float.floatToIntBits(elapsedTime)) / 0x7fff;
                            Vector3 vert = vertices[vertId];
                            if (vert.getZ() < pagIN.getSize2Min() || vert.getZ() > pagIN.getSize2Max()) {
                                pagIN.setActPAGVertNum(pagIN.getActPAGVertNum() - 1);
                                return;
                            } else {
                                Vector3 destVert = new Vector3();
                                Vector3.transformVertex(destVert, globalMtx, vert);
                                particle.setPosX(destVert.getX());
                                particle.setPosY(destVert.getY());
                                particle.setPosZ(destVert.getZ());
                            }
                            break;
                        default:
                            break;
                    }

                    v42 = -pagIN.getParticleType2Case6GlobalMtxMultiplier();
                    particle.setPosX(particle.getPosX() + globalMtx.getM21() * v42);
                    particle.setPosY(particle.getPosY() + globalMtx.getM22() * v42);
                    particle.setPosZ(particle.getPosZ() + globalMtx.getM23() * v42);
                    switch (pagIN.getParticleType2()) {
                        case 0:
                            factor = -(RenderUtils.randShort() * 0.000030518509 * (pagIN.getParticleTypeCase2SecMaxOffset() - pagIN.getParticleTypeCase2SecMinOffset()) + pagIN.getParticleTypeCase2SecMinOffset());
                            particle.setVertPlusOffsetX((float) (globalMtx.getM21() * factor));
                            particle.setVertPlusOffsetY((float) (globalMtx.getM22() * factor));
                            particle.setVertPlusOffsetZ((float) (globalMtx.getM23() * factor));
                            break;
                        case 1:
                            v100 = -pagIN.getParticleTypeCase2FirstCentralizedOffsetMin();
                            v104 = RenderUtils.randShort() * 0.000030518509f * (pagIN.getParticleTypeCase2FirstCentralizedOffsetMin() - v100) + v100;
                            v162 = RenderUtils.randShort() * 0.000030518509f * (pagIN.getParticleTypeCase2SecMaxOffset() - pagIN.getParticleTypeCase2SecMinOffset()) + pagIN.getParticleTypeCase2SecMinOffset();
                            v105 = -(RenderUtils.getDecalByFloatOffsettedBy256(v104) * v162);
                            particle.setVertPlusOffsetX(globalMtx.getM21() * v105);
                            particle.setVertPlusOffsetY(globalMtx.getM22() * v105);
                            particle.setVertPlusOffsetZ(globalMtx.getM23() * v105);
                            v171 = 162.975 * v104 + RenderUtils.getUnknownMagic();
                            v107 = RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v171) & 0x3ff)] * v162;
                            particle.setVertPlusOffsetX(v107 * globalMtx.getM11() + particle.getVertPlusOffsetX());
                            particle.setVertPlusOffsetY(particle.getVertPlusOffsetY() + globalMtx.getM12() * v107);
                            particle.setVertPlusOffsetZ(particle.getVertPlusOffsetZ() + globalMtx.getM13() * v107);
                            break;
                        case 2:
                            v112 = -pagIN.getParticleTypeCase2FirstCentralizedOffsetMin();
                            v115 = RenderUtils.randShort() * 0.000030518509f * (pagIN.getParticleTypeCase2FirstCentralizedOffsetMin() - v112) + v112;
                            v116 = -pagIN.getParticleTypeCase2FirstCentralizedOffsetMin();
                            floatToIntConverted_2b = RenderUtils.randShort() * 0.000030518509f * (pagIN.getParticleTypeCase2FirstCentralizedOffsetMax() - v116) + v116;
                            v163 = RenderUtils.randShort() * 0.000030518509f * (pagIN.getParticleTypeCase2SecMaxOffset() - pagIN.getParticleTypeCase2SecMinOffset()) + pagIN.getParticleTypeCase2SecMinOffset();
                            v121 = -(RenderUtils.getDecalByFloatOffsettedBy256(v115) * RenderUtils.getDecalByFloatOffsettedBy256(floatToIntConverted_2b) * v163);
                            particle.setVertPlusOffsetX(globalMtx.getM21() * v121);
                            particle.setVertPlusOffsetY(globalMtx.getM22() * v121);
                            particle.setVertPlusOffsetZ(globalMtx.getM23() * v121);
                            v172 = 162.975 * v115 + RenderUtils.getUnknownMagic();

                            v123 = RenderUtils.getDecalByFloatOffsettedBy256(floatToIntConverted_2b)
                                    * RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v172) & 0x3ff)]
                                    * v163;
                            particle.setVertPlusOffsetX(v123 * globalMtx.getM11() + particle.getVertPlusOffsetX());
                            particle.setVertPlusOffsetY(particle.getVertPlusOffsetY() + globalMtx.getM12() * v123);
                            particle.setVertPlusOffsetZ(particle.getVertPlusOffsetZ() + globalMtx.getM13() * v123);
                            v173 = 162.975 * floatToIntConverted_2b + RenderUtils.getUnknownMagic();
                            v127 = RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v173) & 0x3ff)] * v163;
                            particle.setVertPlusOffsetX(v127 * globalMtx.getM31() + particle.getVertPlusOffsetX());
                            particle.setVertPlusOffsetY(particle.getVertPlusOffsetY() + globalMtx.getM32() * v127);
                            particle.setVertPlusOffsetZ(particle.getVertPlusOffsetZ() + globalMtx.getM33() * v127);
                            break;
                        case 3:
                            particle.setVertPlusOffsetX(particle.getPosX() - globalMtx.getPos().getX());
                            particle.setVertPlusOffsetY(particle.getPosY() - globalMtx.getPos().getY());
                            particle.setVertPlusOffsetZ(particle.getPosZ() - globalMtx.getPos().getZ());
                            v130 = (float) Math.sqrt(particle.getVertPlusOffsetX() * particle.getVertPlusOffsetX()
                                    + particle.getVertPlusOffsetY() * particle.getVertPlusOffsetY() + particle.getVertPlusOffsetZ() * particle.getVertPlusOffsetZ());
                            if (v130 > 0) {
                                particle.setVertPlusOffsetX(v130 * particle.getVertPlusOffsetX());
                                particle.setVertPlusOffsetY(v130 * particle.getVertPlusOffsetY());
                                particle.setVertPlusOffsetZ(v130 * particle.getVertPlusOffsetZ());
                            } else {
                                v136 = RenderUtils.randShort() * 0.000030518509f * (pagIN.getParticleTypeCase2SecMaxOffset() - pagIN.getParticleTypeCase2SecMinOffset()) + pagIN.getParticleTypeCase2SecMinOffset();
                                v130 = v136 / v130;
                                particle.setVertPlusOffsetX(v130 * particle.getVertPlusOffsetX());
                                particle.setVertPlusOffsetY(v130 * particle.getVertPlusOffsetY());
                                particle.setVertPlusOffsetZ(v130 * particle.getVertPlusOffsetZ());
                            }
                            break;
                        case 4:
                            Vector3 normals[] = getVertexNormals(vertices, geo.getNormalsTemp(), geo.getNormalsNumTemp(), geo.getGeometryTriangleChunks());
                            v139 = RenderUtils.randShort() * 0.000030518509f;
                            v141 = -(v139 * (pagIN.getParticleTypeCase2SecMaxOffset() - pagIN.getParticleTypeCase2SecMinOffset()) + pagIN.getParticleTypeCase2SecMinOffset());
                            particle.setVertPlusOffsetX(normals[vertId].getX() * v141);
                            particle.setVertPlusOffsetY(normals[vertId].getY() * v141);
                            particle.setVertPlusOffsetZ(normals[vertId].getZ() * v141);
                            break;
                        case 5:
                            particle.setVertPlusOffsetX(pagIN.getRand1MaxMultiplier() - particle.getPosX());
                            particle.setVertPlusOffsetY(pagIN.getRand1MinMultiplier() - particle.getPosY());
                            particle.setVertPlusOffsetZ(pagIN.getRandAdditionalMinMultiplier() - particle.getPosZ());
                            v130 = (float) Math.sqrt(particle.getVertPlusOffsetX() * particle.getVertPlusOffsetX()
                                    + particle.getVertPlusOffsetY() * particle.getVertPlusOffsetY() + particle.getVertPlusOffsetZ() * particle.getVertPlusOffsetZ());
                            if (v130 > 0) {
                                v136 = RenderUtils.randShort() * 0.000030518509f * (pagIN.getParticleTypeCase2SecMaxOffset() - pagIN.getParticleTypeCase2SecMinOffset()) + pagIN.getParticleTypeCase2SecMinOffset();
                                v130 = v136 / v130;
                            }
                            particle.setVertPlusOffsetX(v130 * particle.getVertPlusOffsetX());
                            particle.setVertPlusOffsetY(v130 * particle.getVertPlusOffsetY());
                            particle.setVertPlusOffsetZ(v130 * particle.getVertPlusOffsetZ());
                            break;
                        default:
                            break;
                    }

                    if ((pagIN.getFlags() & 0x1000) != 0) {
                        particle.setUnkCounter((int) ((RenderUtils.randShort() * 0.000030518509f * (pagIN.getRand1MinMultiplier() - pagIN.getRand1MaxMultiplier()) + pagIN.getRand1MaxMultiplier()) * 162.97466) << 22);
                        v153 = (RenderUtils.randShort() * 0.000030518509f * (pagIN.getRandAdditionalMaxMultiplier() - pagIN.getRandAdditionalMinMultiplier()) + pagIN.getRandAdditionalMinMultiplier()) * 10430.378;
                        if (v153 < 0.0f) {
                            v153 = -v153;
                            particle.setUnkCounter(particle.getUnkCounter() | 0x8000);
                        }
                        particle.setUnkCounter(particle.getUnkCounter() | (((int) (v153)) & 0x7fff));
                    } else {
                        particle.setUnkCounter(0);
                    }

                    --floatToIntConvertedCurr;
                    if (isDebug) {
                        System.out.format("particle added: %d %s\n", (pagIN.getActPAGVertNum() - 1), particle);
                    }
                    if (floatToIntConvertedCurr != 0.0f) {
                        continue;
                    }
                    break;
                }
            }
        } else {
            System.out.println("PAG: " + gao.getGaoName() + ": TimeDividerMain is 0.0, calculation disabled.");
        }
    }

    public static void handlePAG(GL4 gl4, BFGaoChunk gao, GAOInnerParserParticleGenerator pagIN, float viewMatrix[], float rotation[], boolean fromZListSort, Renderer r) {
        int flags;
        int zListFlag;
        ParticleElem currParticle;
        Vector3 vertices[];
        Vector3 vertexTmp;
        int particleState;
        int multipliedColor;
        int color;
        float v23;
        int unkCounter;
        int unkCounterBS;
        float decal1;
        float invSignDecal1;
        float decal2;
        float vm;
        float mult;
        float vec1MultFactor;
        float vec2MultFactor;
        int vertexColor;
        boolean isScaledDownCamVec;
        Vector3 camVec1 = new Vector3();
        Vector3 camVec2 = new Vector3();
        int vertexColors[];
        Vector3 camVec1Calcd = new Vector3();
        Vector3 camVec2Calcd = new Vector3();
        Vector3 dest1 = new Vector3();

        int i;

        // TODO: Hack to test
        if (gao.getGaoName().equals("Part_Arbre.gao")) {
            pagIN.setTimeDividerMain(50.0f);
        }

        // compute real view matrix, TODO: REALLY NASTY IMPLEMENTATION, should be simplified
        Matrix4x4 view = new Matrix4x4(viewMatrix);
        Matrix4x4.fixMatrix(view);
        Matrix4x4 rot = new Matrix4x4(rotation);
        Matrix4x4.fixMatrix(rot);
        Matrix4x4.matrixMultiplyWithNormalized(cameraWorldSpace, rot, view);
        pagViewMatrixTemp.set(cameraWorldSpace);
        cameraWorldSpace = Camera.camInvert(null, cameraWorldSpace);

        flags = pagIN.getFlags();
        zListFlag = ~((pagIN.getFlags() >> 16) & 0xff) & 1;
        if ((flags & 0x40) != 0 /* TODO: + cam distance != 0*/) {
            if (zListFlag == 0 && (flags & 1) != 0) {
                if (!fromZListSort && r.iszListSortEnabled()) {
                    r.addGaoToZList(gao, view);
                    return;
                }
            }
            if (pagIN.getRenderingCounter() != r.getRenderingCounter()) {
                if ((flags & 0x80) == 0) {
                    manageParticlesLifecycle(gao, pagIN, r.getElapsedTimeInSecsPerLoop());
                    addParticlesIfNeeded(gao, pagIN, r.getElapsedTimeInSecsPerLoop());
                } else {
                    System.out.println("PAG: " + gao.getGaoName() + ": Particle management disabled.");
                }
                pagIN.setRenderingCounter(r.getRenderingCounter());
            }
            if (pagIN.getActPAGVertNum() != 0) {
                if (pagIN.getVecMultMax() != pagIN.getVecMultMin()
                        || pagIN.getVecMult2WOunkFlags4Max() != pagIN.getVecMult2WOunkFlags4Min()
                        || ((pagIN.getFlags() & 0x30) != 0)) {
                    camVec1.set(cameraWorldSpace.getM11(), cameraWorldSpace.getM12(), cameraWorldSpace.getM13());
                    camVec2.set(cameraWorldSpace.getM21(), cameraWorldSpace.getM22(), cameraWorldSpace.getM23());
                    isScaledDownCamVec = false;
                } else {
                    Vector3.multiplyVectorWithScalar(camVec1, new Vector3(cameraWorldSpace.getM11(), cameraWorldSpace.getM12(), cameraWorldSpace.getM13()), pagIN.getVecMultMin());
                    if ((pagIN.getFlags() & 4) != 0) {
                        vm = pagIN.getVecMultMin();
                    } else {
                        vm = pagIN.getVecMult2WOunkFlags4Min();
                    }
                    Vector3.multiplyVectorWithScalar(camVec2, new Vector3(cameraWorldSpace.getM21(), cameraWorldSpace.getM22(), cameraWorldSpace.getM23()), vm);
                    camVec1Calcd.set(camVec1);
                    camVec2Calcd.set(camVec2);
                    isScaledDownCamVec = true;
                }

                vertexColors = new int[pagIN.getActPAGVertNum() * 4];

                vertices = new Vector3[pagIN.getActPAGVertNum() * 4];
                Vector3 normalsTmp[] = new Vector3[pagIN.getActPAGVertNum() * 4];
                int normalsNumTmp[] = new int[pagIN.getActPAGVertNum() * 4];

                for (i = 0; i < pagIN.getActPAGVertNum() * 4; i++) {
                    vertices[i] = new Vector3();
                    normalsTmp[i] = new Vector3();
                    normalsNumTmp[i] = 0;
                }

                GeometryTriangleChunk geotc[] = new GeometryTriangleChunk[1];
                geotc[0] = new GeometryTriangleChunk(pagIN.getActPAGVertNum() * 2, 0, 0, 0); //1st param: one particle consists of 2 tiangles, 2nd param: first material (0) from gao          
                if (pagIN.getActPAGVertNum() > 0) {
                    i = 0;
                    while (true) {
                        currParticle = pagIN.getPAGElemData()[i];
                        if (isDebug) {
                            System.out.format("%d: %s\n", i, currParticle);
                        }
                        particleState = currParticle.getParticleState();
                        vertexColor = pagIN.getColor();
                        vec1MultFactor = currParticle.getParticleSize1();
                        vec2MultFactor = currParticle.getParticleSize2();
                        if ((particleState & 1) == 0) {
                            if ((particleState & 2) != 0) {
                                mult = currParticle.getElapsedTimeMinDownCount() * currParticle.getInvElapsedTimeDownCount();
                                if ((flags & 8) != 0) {
                                    color = RenderUtils.multiplyColor(vertexColor, mult);
                                    vertexColor = color;
                                    if ((flags & 2) == 0) {
                                        vertexColor = (color ^ pagIN.getColor()) & 0xffffff ^ color;
                                    }
                                }
                                if ((flags & 0x10) != 0) {
                                    vec1MultFactor = mult * vec1MultFactor;
                                    vec2MultFactor = mult * vec2MultFactor;
                                } else {
                                    if ((flags & 0x20) != 0) {
                                        v23 = (1.0f - mult) * pagIN.getTwoShortsMultiplierIfUnkFlagsHas20() + 1.0f;
                                        vec1MultFactor = vec1MultFactor * v23;
                                        vec2MultFactor = vec2MultFactor * v23;
                                    }
                                }
                            }
                        } else {
                            mult = 1.0f - (currParticle.getElapsedTimeMinDownCount() * currParticle.getInvElapsedTimeDownCount());
                            if ((flags & 0x100) != 0) {
                                multipliedColor = RenderUtils.multiplyColor(vertexColor, mult);
                                vertexColor = multipliedColor;
                                if ((flags & 2) == 0) {
                                    vertexColor = (multipliedColor ^ pagIN.getColor()) & 0xffffff ^ multipliedColor;
                                }
                            }
                            if ((flags & 0x200) != 0) {
                                vec1MultFactor = mult * vec1MultFactor;
                                vec2MultFactor = mult * vec2MultFactor;
                            }
                        }
                        if (!isScaledDownCamVec) {
                            Vector3.multiplyVectorWithScalar(camVec1Calcd, camVec1, vec1MultFactor);
                            Vector3.multiplyVectorWithScalar(camVec2Calcd, camVec2, vec2MultFactor);
                        }
                        unkCounter = currParticle.getUnkCounter();
                        if (unkCounter != 0) {
                            if (isScaledDownCamVec) {
                                camVec1Calcd.set(camVec1);
                                camVec2Calcd.set(camVec2);
                            }
                            unkCounterBS = unkCounter >>> 22;
                            decal1 = RenderUtils.getDecalTable()[unkCounterBS];
                            decal2 = RenderUtils.getDecalTable()[(unkCounterBS + 256) % 0x3FF];
                            dest1.setX(camVec1Calcd.getX() * decal2);
                            dest1.setY(camVec1Calcd.getY() * decal2);
                            dest1.setZ(camVec1Calcd.getZ() * decal2);
                            Vector3.addAnotherMultipliedVec(dest1, dest1, camVec2Calcd, decal1);
                            Vector3.multiplyVectorWithScalar(camVec2Calcd, camVec2Calcd, decal2);
                            invSignDecal1 = -decal1;
                            Vector3.addAnotherMultipliedVec(camVec2Calcd, camVec2Calcd, camVec1Calcd, invSignDecal1);
                            camVec1Calcd.set(dest1);
                        }

                        vertexColors[i * 4] = vertexColor;
                        vertexColors[i * 4 + 1] = vertexColor;
                        vertexColors[i * 4 + 2] = vertexColor;
                        vertexColors[i * 4 + 3] = vertexColor;

                        // change triangle strip winding to render particles (opposite in the game)
                        //geotc[0].addGeometryTriangleData(new GeometryTriangleData((short) (i * 4), (short) (i * 4 + 1), (short) (i * 4 + 2), (short) 0, (short) 1, (short) 2, 0, 0));
                        //geotc[0].addGeometryTriangleData(new GeometryTriangleData((short) (i * 4 + 2), (short) (i * 4 + 3), (short) (i * 4), (short) 2, (short) 3, (short) 0, 0, 0));
                        geotc[0].addGeometryTriangleData(new GeometryTriangleData((short) (i * 4 + 2), (short) (i * 4 + 1), (short) (i * 4), (short) 0, (short) 1, (short) 2, 0, 0));
                        geotc[0].addGeometryTriangleData(new GeometryTriangleData((short) (i * 4), (short) (i * 4 + 3), (short) (i * 4 + 2), (short) 2, (short) 3, (short) 0, 0, 0));

                        vertexTmp = vertices[i * 4];
                        vertexTmp.setX(currParticle.getPosX() - camVec1Calcd.getX());
                        vertexTmp.setY(currParticle.getPosY() - camVec1Calcd.getY());
                        vertexTmp.setZ(currParticle.getPosZ() - camVec1Calcd.getZ());
                        vertexTmp.setX(vertexTmp.getX() - camVec2Calcd.getX());
                        vertexTmp.setY(vertexTmp.getY() - camVec2Calcd.getY());
                        vertexTmp.setZ(vertexTmp.getZ() - camVec2Calcd.getZ());
                        vertexTmp = vertices[i * 4 + 1];
                        vertexTmp.setX(currParticle.getPosX() - camVec1Calcd.getX());
                        vertexTmp.setY(currParticle.getPosY() - camVec1Calcd.getY());
                        vertexTmp.setZ(currParticle.getPosZ() - camVec1Calcd.getZ());
                        vertexTmp.setX(vertexTmp.getX() + camVec2Calcd.getX());
                        vertexTmp.setY(vertexTmp.getY() + camVec2Calcd.getY());
                        vertexTmp.setZ(vertexTmp.getZ() + camVec2Calcd.getZ());
                        vertexTmp = vertices[i * 4 + 2];
                        vertexTmp.setX(currParticle.getPosX() + camVec1Calcd.getX());
                        vertexTmp.setY(currParticle.getPosY() + camVec1Calcd.getY());
                        vertexTmp.setZ(currParticle.getPosZ() + camVec1Calcd.getZ());
                        vertexTmp.setX(vertexTmp.getX() + camVec2Calcd.getX());
                        vertexTmp.setY(vertexTmp.getY() + camVec2Calcd.getY());
                        vertexTmp.setZ(vertexTmp.getZ() + camVec2Calcd.getZ());
                        vertexTmp = vertices[i * 4 + 3];
                        vertexTmp.setX(currParticle.getPosX() + camVec1Calcd.getX());
                        vertexTmp.setY(currParticle.getPosY() + camVec1Calcd.getY());
                        vertexTmp.setZ(currParticle.getPosZ() + camVec1Calcd.getZ());
                        vertexTmp.setX(vertexTmp.getX() - camVec2Calcd.getX());
                        vertexTmp.setY(vertexTmp.getY() - camVec2Calcd.getY());
                        vertexTmp.setZ(vertexTmp.getZ() - camVec2Calcd.getZ());

                        if (isDebug) {
                            System.out.println(vertices[i * 4]);
                            System.out.println(vertices[i * 4 + 1]);
                            System.out.println(vertices[i * 4 + 2]);
                            System.out.println(vertices[i * 4 + 3]);
                            System.out.println("--");
                        }
                        if (++i >= pagIN.getActPAGVertNum()) {
                            break;
                        }
                    }
                    if (isDebug) {
                        System.out.println("ActPagNum: " + pagIN.getActPAGVertNum());
                    }
                    BFGaoInnerChunkParser materialGroupIC = RenderUtils.getMaterialICFromGAO(gao);
                    int materialFlagsUnion = RenderUtils.getGAOUsedMaterialsFlagsUnion(geotc, materialGroupIC);
                    if ((materialFlagsUnion & 0xc0) != 0 && !fromZListSort && r.iszListSortEnabled()) {
                        r.addGaoToZList(gao, view);
                        if ((materialFlagsUnion & 0x40) != 0) {
                            return;
                        }
                    }

                    // we do not use precalculated ModelView matrix, because particle's positions already multiplied with globalmatrix, see addParticlesIfNeeded(...))
                    System.arraycopy(r.getOglf().getModelViewMtx(), 0, mvMTmp, 0, 16);
                    System.arraycopy(pagViewMatrixTemp.getAsFloatArray(true, true), 0, r.getOglf().getModelViewMtx(), 0, 16);
                    Lights.setMatrices(gao.getGlobalMatrix(), view, rot);
                    r.getOglf().drawMultiMaterialTriangles(gl4, true, geotc, vertices, UVData, normalsTmp, normalsNumTmp, vertexColors,
                            materialGroupIC, materialFlagsUnion, Matrix4x4.getIdentityMatrix().getAsFloatArray(true, true), fromZListSort, gao, r.getElapsedTimeInMillis());
                    // restore modelView matrix
                    System.arraycopy(mvMTmp, 0, r.getOglf().getModelViewMtx(), 0, 16);
                }
            }
        } else {
            System.out.println("PAG: " + gao.getGaoName() + ": ParticleGenerator disabled for this gao.");
        }
    }
}
