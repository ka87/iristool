/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.BFWOWChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSpriteGenModifier;
import Common.DataType.GeometrySprite;
import Common.DataType.GeometryTriangleChunk;
import Common.DataType.GeometryTriangleData;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import com.jogamp.opengl.GL4;
import iristool.Engine.OpenGLFunctions;
import iristool.Engine.RenderUtils;
import iristool.Engine.Renderer;

/**
 *
 * @author andra
 */
public class SpriteGen {

    private static GeometrySprite[] gs = new GeometrySprite[4];

    private static int occupiedSpritesNumFromBuffer = 0;
    private static int spgColorsWithFlagsEncoded = 0;
    private static float obj_dist_m33 = 0.0f;
    private static float sprite_z_PosCalcd = 0.0f;
    private static float obj_dist_m23 = 0.0f;
    private static float obj_dist_m13 = 0.0f;
    private static Vector3 spgVertMultiplied1 = new Vector3();
    private static Vector3 spgVertMultiplied2 = new Vector3();
    private static float spgRandomVals[] = new float[257]; // TODO: perhaps out of bounds in game here (256)
    private static Vector3 spriteDataGeoSprite[] = new Vector3[256];
    private static int spriteDataGeoSpriteColors[] = new int[256];
    private static boolean isMovingSprite = false;
    private static int spgVertexIndexOffset = 0;
    private static float[] spgDefaultVector = new float[3];
    private static Matrix4x4 spgSpriteViewMatrixTemp = new Matrix4x4();
    private static Matrix4x4 spgSpriteViewMatrixTemp2 = new Matrix4x4();

    static class SPGElem {

        public float pos1;
        public float pos2;
        public float pos3;
        public float pos4;
        public short pos51;
        public short pos52;
        public short pos61;
        public short pos62;

        public void set(SPGElem e) {
            pos1 = e.pos1;
            pos2 = e.pos2;
            pos3 = e.pos3;
            pos4 = e.pos4;
            pos51 = e.pos51;
            pos52 = e.pos52;
            pos61 = e.pos61;
            pos62 = e.pos62;
        }
    }

    static class SPGParams {

        float dividerVectorLimit;
        float unknown_spg_struct_multiplier;
        int dividerMaxDecrease;
        int vertexIdxCheckFlags;
    }

    public static void spriteColorCalc(Vector3 vecs[], int srcAndDestColor[], int num, boolean isClamped) {
        float factor;
        int j = 0;

        if (num != 0) {
            do {
                // A játékban a szorzatösszegből vonódik ki a sprite_z_PosCalcd (D3D<->OpenGL Coord Z ?)
                factor = sprite_z_PosCalcd - (vecs[j].getZ() * obj_dist_m33
                        + vecs[j].getY() * obj_dist_m23 + vecs[j].getX() * obj_dist_m13);
                if (!isClamped || (factor > 0.0f && factor < 1.0f)) {
                    srcAndDestColor[j] = RenderUtils.interpolateTwoColors(srcAndDestColor[j],
                            spgColorsWithFlagsEncoded, factor);
                } else if (isClamped && factor >= 1.0) {
                    vecs[j].setX(spgDefaultVector[0]);
                    vecs[j].setY(spgDefaultVector[1]);
                    vecs[j].setZ(spgDefaultVector[2]);
                }
                ++j;
                --num;
            } while (num != 0);
        }
    }

    public static void init(Renderer r) {
        for (int i = 255; i >= 0; i--) {
            spgRandomVals[i] = ((RenderUtils.getElapsedTimeBasedRandom(r.getElapsedTimeInMillis()) & 0xff) * (1 / 256.0f)) - 0.5f;
            spriteDataGeoSprite[i] = new Vector3();
        }
        spgRandomVals[256] = 0; // TODO
    }

    public static void handleSpriteGen(Renderer r, GL4 gl4, BFWOWChunk wow, BFGaoChunk gao, Vector3 vertIN[], Matrix4x4 globalMatrix, float viewMatrix[], float rotation[], float elapsedTime,
            BFModifierSpriteGenModifier spgm, int dist, boolean fromZListSort) {
        int i, j;
        float distCalcd;
        float distCalcdNormalized;
        float distalcdNormalizedSQ2;
        int maxDecreaseLimit;
        int spriteElemNum;
        float spg_pos1;
        float spg_pos2;
        float spg_posdiff;
        int vertexIndex1;
        int vertexIndex2;
        int vertexIndex3;
        float posMult;
        int vertexcolorsParam[] = new int[3];
        SPGParams paramsParam = new SPGParams();
        Vector3 verticesParam1Param = new Vector3();
        Vector3 verticesParam2Param = new Vector3();
        Vector3 verticesParam3Param = new Vector3();
        spgVertexIndexOffset = 0;
        isMovingSprite = false;

        // compute real view matrix, TODO: REALLY NASTY IMPLEMENTATION, should be simplified
        Matrix4x4 view = new Matrix4x4(viewMatrix);
        Matrix4x4.fixMatrix(view);
        Matrix4x4 rot = new Matrix4x4(rotation);
        Matrix4x4.fixMatrix(rot);
        Matrix4x4.matrixMultiplyWithNormalized(spgSpriteViewMatrixTemp, globalMatrix, rot);
        Matrix4x4.matrixMultiplyWithNormalized(spgSpriteViewMatrixTemp2, spgSpriteViewMatrixTemp, view);
        spgSpriteViewMatrixTemp.set(spgSpriteViewMatrixTemp2);

        BFGaoInnerChunkParser geoICToRender = RenderUtils.getGeometryICTFromLODGAO(gao);
        GAOInnerParserGeometry geo = ((GAOInnerParserGeometry) geoICToRender.getGaoInner());

        if (vertIN == null) {
            vertIN = geo.getVertices();
        }

        distCalcd = dist * spgm.getDistMultiplicator() * 0.015625f;
        if (distCalcd > 1.0) {
            distCalcdNormalized = 1.0f;
        } else {
            distCalcdNormalized = distCalcd;
            if (distCalcd <= 0.0f) {
                distCalcdNormalized = 0.0f;
            }
        }
        distalcdNormalizedSQ2 = distCalcdNormalized * distCalcdNormalized;

        if (distalcdNormalizedSQ2 >= 0.5f) {
            if ((spgm.getAnotherFlags() & 0x1) != 0) {
                if (!fromZListSort && r.iszListSortEnabled()) {
                    r.addGaoToZList(gao, globalMatrix);
                    return;
                }
            } else if (fromZListSort) {
                return;
            }

            gs[0] = new GeometrySprite((spgm.getSpgsbpd()[0].getDimensionSize1Multiplier() * spgm.getDimensionSize1() * distalcdNormalizedSQ2),
                    (spgm.getSpgsbpd()[0].getDimensionSize2Multiplier() * spgm.getDimensionSize2()),
                    spgm.getSpgsbpd()[0].getMatID() & 0xff, 64);
            gs[1] = new GeometrySprite((spgm.getSpgsbpd()[1].getDimensionSize1Multiplier() * spgm.getDimensionSize1() * distalcdNormalizedSQ2),
                    (spgm.getSpgsbpd()[1].getDimensionSize2Multiplier() * spgm.getDimensionSize2()),
                    spgm.getSpgsbpd()[1].getMatID() & 0xff, 64);
            gs[2] = new GeometrySprite((spgm.getSpgsbpd()[2].getDimensionSize1Multiplier() * spgm.getDimensionSize1() * distalcdNormalizedSQ2),
                    (spgm.getSpgsbpd()[2].getDimensionSize2Multiplier() * spgm.getDimensionSize2()),
                    spgm.getSpgsbpd()[2].getMatID() & 0xff, 64);
            gs[3] = new GeometrySprite((spgm.getSpgsbpd()[3].getDimensionSize1Multiplier() * spgm.getDimensionSize1() * distalcdNormalizedSQ2),
                    (spgm.getSpgsbpd()[3].getDimensionSize2Multiplier() * spgm.getDimensionSize2()),
                    spgm.getSpgsbpd()[3].getMatID() & 0xff, 64);

            maxDecreaseLimit = spgm.getDividerMaxDecreaseLimit();
            if (maxDecreaseLimit > 8) {
                maxDecreaseLimit = 8;
            }
            spgm.setDividerMaxDecreaseLimit(maxDecreaseLimit);
            spgm.setDividerMaxDecreaseLimit((maxDecreaseLimit < 0 ? 0 : maxDecreaseLimit));
            spriteElemNum = 64;

            for (i = 0; i < spriteElemNum; i++) {
                gs[0].getGeoSpriteElem()[i].setMultiplier(1.0f);
                gs[1].getGeoSpriteElem()[i].setMultiplier(1.0f);
                gs[2].getGeoSpriteElem()[i].setMultiplier(1.0f);
                gs[3].getGeoSpriteElem()[i].setMultiplier(1.0f);
            }

            do {
                --spriteElemNum;
                gs[0].getGeoSpriteElem()[spriteElemNum].setMultiplier(gs[0].getGeoSpriteElem()[spriteElemNum].getMultiplier() - spgRandomVals[spriteElemNum] * spgm.getSpgsbpd()[0].getRandomFactorMultiplier());
                gs[1].getGeoSpriteElem()[spriteElemNum].setMultiplier(gs[1].getGeoSpriteElem()[spriteElemNum].getMultiplier() - spgRandomVals[spriteElemNum] * spgm.getSpgsbpd()[1].getRandomFactorMultiplier());
                gs[2].getGeoSpriteElem()[spriteElemNum].setMultiplier(gs[2].getGeoSpriteElem()[spriteElemNum].getMultiplier() - spgRandomVals[spriteElemNum] * spgm.getSpgsbpd()[2].getRandomFactorMultiplier());
                gs[3].getGeoSpriteElem()[spriteElemNum].setMultiplier(gs[3].getGeoSpriteElem()[spriteElemNum].getMultiplier() - spgRandomVals[spriteElemNum] * spgm.getSpgsbpd()[3].getRandomFactorMultiplier());
                gs[0].getGeoSpriteElem()[spriteElemNum].setVerticesID1((short) ((4 * spriteElemNum) + (short) (vertIN.length)));
                gs[1].getGeoSpriteElem()[spriteElemNum].setVerticesID1((short) ((4 * spriteElemNum) + (short) (vertIN.length + 1)));
                gs[2].getGeoSpriteElem()[spriteElemNum].setVerticesID1((short) ((4 * spriteElemNum) + (short) (vertIN.length + 2)));
                gs[3].getGeoSpriteElem()[spriteElemNum].setVerticesID1((short) ((4 * spriteElemNum) + (short) (vertIN.length + 3)));
            } while (spriteElemNum > 0);

            spgVertexIndexOffset = -vertIN.length;
            if ((spgm.getAnotherFlags() & 0x10) != 0) {
                isMovingSprite = true;
            }
            if ((spgm.getAnotherFlags() & 0x4) != 0) // enable advanced color calc
            {
                spgColorsWithFlagsEncoded = spgm.getColor() & 0xFFFFFEFF | 1;// enable advanced color calc
                if ((spgm.getAnotherFlags() & 8) != 0) {
                    spgColorsWithFlagsEncoded = spgColorsWithFlagsEncoded | 0x100;// factor should clamped between 0.0-1.0
                }
                spg_pos1 = spgm.getPos1();
                spg_pos2 = spgm.getPos2();
                if (spg_pos2 == spg_pos1) {
                    spg_pos2 = spg_pos1 + 0.1f;
                }
                spg_posdiff = 1.0f / (float) (spg_pos2 - spg_pos1);

                obj_dist_m13 = spg_posdiff * spgSpriteViewMatrixTemp.getM13();
                obj_dist_m23 = spg_posdiff * spgSpriteViewMatrixTemp.getM23();
                obj_dist_m33 = spg_posdiff * spgSpriteViewMatrixTemp.getM33();
                spgDefaultVector[0] = 100000.0f;
                spgDefaultVector[1] = 100000.0f;
                spgDefaultVector[2] = 100000.0f;
                sprite_z_PosCalcd = (spg_pos1 - spgSpriteViewMatrixTemp.getPos().getZ()) * spg_posdiff;
            } else {
                spgColorsWithFlagsEncoded = 0;
            }

            if (spgm.getDividerMaxDecreaseLimit() > 0) {
                paramsParam.dividerVectorLimit = spgm.getDividerVectorLimit() * spgm.getDividerVectorLimit();
                occupiedSpritesNumFromBuffer = 0;
                paramsParam.dividerMaxDecrease = spgm.getDividerMaxDecreaseLimit();  // range [0,8]
                paramsParam.unknown_spg_struct_multiplier = spgm.getTf3();
                GeometryTriangleChunk geots[] = geo.getGeometryTriangleChunks();
                GeometryTriangleData geotd;
                Vector3 normals[] = OpenGLFunctions.getVertexNormals(geo.getVertices(), geo.getNormalsTemp(), geo.getNormalsNumTemp(), geots);
                Vector3[] vertices = vertIN;
                int vertexColors[];

                // Lighting
                if (gao.getPrimaryVertexColorArr() != null) {
                    vertexColors = gao.getPrimaryVertexColorArr();
                } else {
                    vertexColors = ((GAOInnerParserGeometry) geoICToRender.getGaoInner()).getVertexColors();
                }
                for (i = 0; i < geots.length; i++) {
                    if (((1 << geots[i].getMaterialNum()) & spgm.getMaterialEnabledBitmask()) != 0) {
                        for (j = 0; j < geots[i].getGeoTriangleDataCount(); j++) {
                            geotd = geots[i].getGeoTriangleData()[j];
                            vertexIndex1 = geotd.getVertex1ID();
                            vertexIndex2 = geotd.getVertex2ID();
                            vertexIndex3 = geotd.getVertex3ID();

                            vertexcolorsParam[0] = RenderUtils.ambientLightAdd(wow, gao, (vertexColors == null ? 0 : vertexColors[vertexIndex1]));
                            vertexcolorsParam[1] = RenderUtils.ambientLightAdd(wow, gao, (vertexColors == null ? 0 : vertexColors[vertexIndex2]));
                            vertexcolorsParam[2] = RenderUtils.ambientLightAdd(wow, gao, (vertexColors == null ? 0 : vertexColors[vertexIndex3]));

                            paramsParam.vertexIdxCheckFlags = 0;
                            if (vertexIndex1 > vertexIndex2) {
                                paramsParam.vertexIdxCheckFlags = 1;
                            }
                            if (vertexIndex2 > vertexIndex3) {
                                paramsParam.vertexIdxCheckFlags |= 2;
                            }
                            if (vertexIndex3 > vertexIndex1) {
                                paramsParam.vertexIdxCheckFlags |= 4;
                            }

                            posMult = spgm.getPosMultiplicator();

                            verticesParam1Param.setX((float) ((normals[vertexIndex1].getX() * posMult)
                                    + vertices[vertexIndex1].getX()));
                            verticesParam1Param.setY((float) ((normals[vertexIndex1].getY() * posMult)
                                    + vertices[vertexIndex1].getY()));
                            verticesParam1Param.setZ((float) ((normals[vertexIndex1].getZ() * posMult)
                                    + vertices[vertexIndex1].getZ()));
                            verticesParam2Param.setX((float) ((normals[vertexIndex2].getX() * posMult)
                                    + vertices[vertexIndex2].getX()));
                            verticesParam2Param.setY((float) ((normals[vertexIndex2].getY() * posMult)
                                    + vertices[vertexIndex2].getY()));
                            verticesParam2Param.setZ((float) ((normals[vertexIndex2].getZ() * posMult)
                                    + vertices[vertexIndex2].getZ()));
                            verticesParam3Param.setX((float) ((normals[vertexIndex3].getX() * posMult)
                                    + vertices[vertexIndex3].getX()));
                            verticesParam3Param.setY((float) ((normals[vertexIndex3].getY() * posMult)
                                    + vertices[vertexIndex3].getY()));
                            verticesParam3Param.setZ((float) ((normals[vertexIndex3].getZ() * posMult)
                                    + vertices[vertexIndex3].getZ()));
                            spriteCalcSpritePositions(r, gl4, gao, globalMatrix, view, elapsedTime, verticesParam1Param, verticesParam2Param, verticesParam3Param,
                                    vertexcolorsParam, paramsParam);
                        }
                    }
                }
                if (occupiedSpritesNumFromBuffer > 0) {
                    gs[0].setSpElemNum(occupiedSpritesNumFromBuffer >> 2);
                    gs[1].setSpElemNum(occupiedSpritesNumFromBuffer >> 2);
                    gs[2].setSpElemNum(occupiedSpritesNumFromBuffer >> 2);
                    gs[3].setSpElemNum(occupiedSpritesNumFromBuffer >> 2);
                    int origCounter3 = occupiedSpritesNumFromBuffer & 3;
                    i = origCounter3;
                    // ha nem osztható 4-gyel a sprite darabszám, 
                    // módosítja a 4 tár elemszámát, hogy kijöljön a generált sprite darabszám
                    // pl: ha 3 sprite a plusz, akkor a 0,1,2 tár kap plusz 1 elemet
                    if ((occupiedSpritesNumFromBuffer & 3) != 0) {
                        int num;
                        do {
                            --i;
                            num = gs[i].getSpElemNum();
                            gs[i].setSpElemNum(num + 1);

                        } while (i > 0);
                    }
                    occupiedSpritesNumFromBuffer = origCounter3 - 1;

                    int vertexIDElemNumSUM = gs[0].getSpElemNum() + gs[1].getSpElemNum() + gs[2].getSpElemNum() + gs[3].getSpElemNum();

                    if (spgColorsWithFlagsEncoded > 0) {
                        if ((spgColorsWithFlagsEncoded & 0x100) != 0) {
                            spriteColorCalc(spriteDataGeoSprite, spriteDataGeoSpriteColors, vertexIDElemNumSUM, true);
                        } else {
                            spriteColorCalc(spriteDataGeoSprite, spriteDataGeoSpriteColors, vertexIDElemNumSUM, false);
                        }
                    }
                    spritesSendToDraw(r, gl4, gao, globalMatrix, view, elapsedTime, true);
                }
            }
            isMovingSprite = false;
        }
    }

    private static int spriteCalcDivider(int maxCsokkentesiLimit, float vectorlimit, SPGElem struct1EzAKisebbHosszju, SPGElem struct2_) {
        int decreaserCnt = 0;
        float lesserLenght;
        float structlen1;

        structlen1 = struct2_.pos1 * struct2_.pos1 + struct2_.pos2 * struct2_.pos2 + struct2_.pos3 * struct2_.pos3;
        lesserLenght = struct1EzAKisebbHosszju.pos1 * struct1EzAKisebbHosszju.pos1 + struct1EzAKisebbHosszju.pos2 * struct1EzAKisebbHosszju.pos2 + struct1EzAKisebbHosszju.pos3 * struct1EzAKisebbHosszju.pos3;
        if (lesserLenght > structlen1) {
            lesserLenght = structlen1;
        }
        for (; lesserLenght > vectorlimit; ++decreaserCnt) {
            if (decreaserCnt >= maxCsokkentesiLimit) {
                break;
            }
            lesserLenght = lesserLenght * 0.25f;
        }
        return decreaserCnt;
    }

    private static void spriteSubstractElem(SPGElem dest, SPGElem src1, SPGElem src2) {
        dest.pos62 = (short) (src1.pos62 - src2.pos62);
        dest.pos61 = (short) (src1.pos61 - src2.pos61);
        dest.pos52 = (short) (src1.pos52 - src2.pos52);
        dest.pos51 = (short) (src1.pos51 - src2.pos51);
        dest.pos1 = src1.pos1 - src2.pos1;
        dest.pos2 = src1.pos2 - src2.pos2;
        dest.pos3 = src1.pos3 - src2.pos3;
    }

    private static void spriteDivideElem(SPGElem elem, int divider) {

        elem.pos62 >>>= divider;
        elem.pos61 >>>= divider;
        elem.pos52 >>>= divider;
        elem.pos51 >>>= divider;
        float f = Float.intBitsToFloat(0x3F800000 - (divider << 23));
        elem.pos1 = f * elem.pos1;
        elem.pos2 = f * elem.pos2;
        elem.pos3 = f * elem.pos3;
    }

    private static void spriteMultiplyElem(SPGElem structMult1Param, SPGElem structMult2Param, float factor) {

        spgVertMultiplied1.setX(structMult1Param.pos1 * factor);
        spgVertMultiplied1.setY(structMult1Param.pos2 * factor);
        spgVertMultiplied1.setZ(structMult1Param.pos3 * factor);
        spgVertMultiplied2.setX(structMult2Param.pos1 * factor);
        spgVertMultiplied2.setY(structMult2Param.pos2 * factor);
        spgVertMultiplied2.setZ(structMult2Param.pos3 * factor);
    }

    private static void spriteCalcSpritePositions(Renderer r, GL4 gl4, BFGaoChunk gao, Matrix4x4 globalMatrix, Matrix4x4 viewMatrix, float elapsedTime,
            Vector3 vertices1Param, Vector3 vertices2Param, Vector3 vertices3Param, int colorsParam[], SPGParams paramsParam) {
        int i;
        int divider;
        int endindex;
        SPGElem vert2MinusVert1 = new SPGElem();
        SPGElem destsum = new SPGElem();
        SPGElem vert3MinusVert1 = new SPGElem();
        SPGElem vert1DataCalcd = new SPGElem();
        SPGElem vert3DataCalcd = new SPGElem();
        SPGElem vert2DataCalcd = new SPGElem();
        SPGElem destsumCopied = new SPGElem();

        vert1DataCalcd.pos62 = (short) ((colorsParam[0] >>> 17) & 0x7F80);
        vert1DataCalcd.pos61 = (short) ((colorsParam[0] >>> 9) & 0x7F80);
        vert1DataCalcd.pos52 = (short) ((colorsParam[0] >>> 1) & 0x7F80);
        vert1DataCalcd.pos51 = (short) ((colorsParam[0] & 0xff) << 7);
        vert1DataCalcd.pos1 = vertices1Param.getX();
        vert1DataCalcd.pos2 = vertices1Param.getY();
        vert1DataCalcd.pos3 = vertices1Param.getZ();

        vert2DataCalcd.pos62 = (short) ((colorsParam[1] >>> 17) & 0x7F80);
        vert2DataCalcd.pos61 = (short) ((colorsParam[1] >>> 9) & 0x7F80);
        vert2DataCalcd.pos52 = (short) ((colorsParam[1] >>> 1) & 0x7F80);
        vert2DataCalcd.pos51 = (short) ((colorsParam[1] & 0xff) << 7);
        vert2DataCalcd.pos1 = vertices2Param.getX();
        vert2DataCalcd.pos2 = vertices2Param.getY();
        vert2DataCalcd.pos3 = vertices2Param.getZ();

        vert3DataCalcd.pos62 = (short) ((colorsParam[2] >>> 17) & 0x7F80);
        vert3DataCalcd.pos61 = (short) ((colorsParam[2] >>> 9) & 0x7F80);
        vert3DataCalcd.pos52 = (short) ((colorsParam[2] >>> 1) & 0x7F80);
        vert3DataCalcd.pos51 = (short) ((colorsParam[2] & 0xff) << 7);
        vert3DataCalcd.pos1 = vertices3Param.getX();
        vert3DataCalcd.pos2 = vertices3Param.getY();
        vert3DataCalcd.pos3 = vertices3Param.getZ();

        vert2MinusVert1.pos1 = vert2DataCalcd.pos1 - vert1DataCalcd.pos1;
        vert2MinusVert1.pos2 = vert2DataCalcd.pos2 - vert1DataCalcd.pos2;
        vert2MinusVert1.pos3 = vert2DataCalcd.pos3 - vert1DataCalcd.pos3;
        vert2MinusVert1.pos52 = (short) (vert2DataCalcd.pos52 - vert1DataCalcd.pos52);
        vert2MinusVert1.pos51 = (short) (vert2DataCalcd.pos51 - vert1DataCalcd.pos51);
        vert2MinusVert1.pos62 = (short) (vert2DataCalcd.pos62 - vert1DataCalcd.pos62);
        vert2MinusVert1.pos61 = (short) (vert2DataCalcd.pos61 - vert1DataCalcd.pos61);

        vert3MinusVert1.pos1 = vert3DataCalcd.pos1 - vert1DataCalcd.pos1;
        vert3MinusVert1.pos2 = vert3DataCalcd.pos2 - vert1DataCalcd.pos2;
        vert3MinusVert1.pos3 = vert3DataCalcd.pos3 - vert1DataCalcd.pos3;
        vert3MinusVert1.pos52 = (short) (vert3DataCalcd.pos52 - vert1DataCalcd.pos52);
        vert3MinusVert1.pos51 = (short) (vert3DataCalcd.pos51 - vert1DataCalcd.pos51);
        vert3MinusVert1.pos62 = (short) (vert3DataCalcd.pos62 - vert1DataCalcd.pos62);
        vert3MinusVert1.pos61 = (short) (vert3DataCalcd.pos61 - vert1DataCalcd.pos61);

        if (paramsParam.dividerVectorLimit == 0.0f) {
            divider = paramsParam.dividerMaxDecrease;
        } else {
            divider = spriteCalcDivider(paramsParam.dividerMaxDecrease, paramsParam.dividerVectorLimit,
                    vert2MinusVert1, vert3MinusVert1);
        }
        if (divider != 0) {
            spriteDivideElem(vert2MinusVert1, divider);
            spriteDivideElem(vert3MinusVert1, divider);

            spriteMultiplyElem(vert2MinusVert1, vert3MinusVert1, paramsParam.unknown_spg_struct_multiplier);
            destsum.set(vert1DataCalcd);
            endindex = (1 << divider) - 1;
            if (endindex != 0) {
                i = endindex - 1;
                while (true) {
                    destsum.pos61 += vert2MinusVert1.pos61;
                    destsum.pos52 += vert2MinusVert1.pos52;
                    destsum.pos51 += vert2MinusVert1.pos51;
                    destsum.pos62 += vert2MinusVert1.pos62;
                    destsum.pos1 = destsum.pos1 + vert2MinusVert1.pos1;
                    destsum.pos2 = destsum.pos2 + vert2MinusVert1.pos2;
                    destsum.pos3 = destsum.pos3 + vert2MinusVert1.pos3;
                    destsumCopied.set(destsum);
                    spriteAddSpritesAndDrawIfStorageIsFull(r, gl4, gao, globalMatrix, viewMatrix, elapsedTime, destsumCopied, vert3MinusVert1, i);
                    if (i == 0) {
                        break;
                    }
                    i--;
                }
            }
            if ((paramsParam.vertexIdxCheckFlags & 1) != 0) {
                destsum.set(vert1DataCalcd);
                spriteAddSpritesAndDrawIfStorageIsFull(r, gl4, gao, globalMatrix, viewMatrix, elapsedTime, destsum, vert2MinusVert1, endindex);
            }
            if ((paramsParam.vertexIdxCheckFlags & 2) != 0) {
                destsum.set(vert2DataCalcd);
                spriteSubstractElem(vert2MinusVert1, vert3DataCalcd, vert2DataCalcd);
                spriteDivideElem(vert2MinusVert1, divider);
                spriteAddSpritesAndDrawIfStorageIsFull(r, gl4, gao, globalMatrix, viewMatrix, elapsedTime, destsum, vert2MinusVert1, endindex);
            }
            if ((paramsParam.vertexIdxCheckFlags & 4) != 0) {
                destsum.set(vert1DataCalcd);
                spriteSubstractElem(vert2MinusVert1, vert3DataCalcd, vert1DataCalcd);
                spriteDivideElem(vert2MinusVert1, divider);
                spriteAddSpritesAndDrawIfStorageIsFull(r, gl4, gao, globalMatrix, viewMatrix, elapsedTime, destsum, vert2MinusVert1, endindex);
            }
        }
    }

    private static void spritesSendToDraw(Renderer r, GL4 gl4, BFGaoChunk gao, Matrix4x4 globalMatrix, Matrix4x4 viewMatrix, float elapsedTime, boolean check) {

        if (spgColorsWithFlagsEncoded != 0) {
            if ((spgColorsWithFlagsEncoded & 0x100) != 0) {
                spriteColorCalc(spriteDataGeoSprite, spriteDataGeoSpriteColors, 256, true);
            } else {
                spriteColorCalc(spriteDataGeoSprite, spriteDataGeoSpriteColors, 256, false);
            }
        }
        if (!check || (check && gs[0].getSpElemNum() > 0)) {
            r.drawSprite(gl4, gao, spriteDataGeoSprite, spriteDataGeoSpriteColors, globalMatrix, viewMatrix, gs[0], isMovingSprite, elapsedTime, spgVertexIndexOffset);
        }
        if (!check || (check && gs[1].getSpElemNum() > 0)) {
            r.drawSprite(gl4, gao, spriteDataGeoSprite, spriteDataGeoSpriteColors, globalMatrix, viewMatrix, gs[1], isMovingSprite, elapsedTime, spgVertexIndexOffset);
        }
        if (!check || (check && gs[2].getSpElemNum() > 0)) {
            r.drawSprite(gl4, gao, spriteDataGeoSprite, spriteDataGeoSpriteColors, globalMatrix, viewMatrix, gs[2], isMovingSprite, elapsedTime, spgVertexIndexOffset);
        }
        if (!check || (check && gs[3].getSpElemNum() > 0)) {
            r.drawSprite(gl4, gao, spriteDataGeoSprite, spriteDataGeoSpriteColors, globalMatrix, viewMatrix, gs[3], isMovingSprite, elapsedTime, spgVertexIndexOffset);
        }

        occupiedSpritesNumFromBuffer = 0;
    }

    private static void spriteTwoElemMergeAndAdd(Vector3 vertexDestArr[], int colorDestArr[], int idx, SPGElem dest, SPGElem src, float multiplier, float multiplier2) {

        dest.pos62 += src.pos62;
        dest.pos61 += src.pos61;
        dest.pos52 += src.pos52;
        dest.pos51 += src.pos51;
        dest.pos1 = src.pos1 + dest.pos1;
        dest.pos2 = src.pos2 + dest.pos2;
        dest.pos3 = src.pos3 + dest.pos3;

        vertexDestArr[idx].setX(spgVertMultiplied1.getX() * multiplier + dest.pos1);
        vertexDestArr[idx].setY(spgVertMultiplied1.getY() * multiplier + dest.pos2);
        vertexDestArr[idx].setZ(spgVertMultiplied1.getZ() * multiplier + dest.pos3);
        vertexDestArr[idx].setX(spgVertMultiplied2.getX() * multiplier2 + vertexDestArr[idx].getX());
        vertexDestArr[idx].setY(spgVertMultiplied2.getY() * multiplier2 + vertexDestArr[idx].getY());
        vertexDestArr[idx].setZ(spgVertMultiplied2.getZ() * multiplier2 + vertexDestArr[idx].getZ());
        int color = 2 * (((dest.pos62 & 0xFFFFFF80) << 16) | (dest.pos52 | ((dest.pos61 & 0xFFFFFF80) << 8)) & 0xFFFFFF80);
        colorDestArr[idx] = (((short) (dest.pos51 >>> 7)) & 0x1ff) | color;
    }

    private static void spriteAddSpritesAndDrawIfStorageIsFull(Renderer r, GL4 gl4, BFGaoChunk gao, Matrix4x4 globalMatrix, Matrix4x4 viewMatrix, float elapsedTime,
            SPGElem destsum, SPGElem src, int spriteNumToAdd) {
        int i = 0, j = occupiedSpritesNumFromBuffer;
        int rIdx = occupiedSpritesNumFromBuffer;
        if ((occupiedSpritesNumFromBuffer + spriteNumToAdd) >= 256) {
            int elemsSurplus = (((occupiedSpritesNumFromBuffer + spriteNumToAdd) - 256) / 256) + 1;
            do {
                int gt = spriteNumToAdd - ((occupiedSpritesNumFromBuffer + spriteNumToAdd) - 256);
                for (i = 0; i < gt; i++, j++) {
                    spriteTwoElemMergeAndAdd(spriteDataGeoSprite, spriteDataGeoSpriteColors, j, destsum, src, spgRandomVals[rIdx], spgRandomVals[++rIdx]);
                }
                spritesSendToDraw(r, gl4, gao, globalMatrix, viewMatrix, elapsedTime, false);
                occupiedSpritesNumFromBuffer -= 256;
                rIdx -= 256;
                j = 0;
            } while (--elemsSurplus > 0);
        }

        for (; i < spriteNumToAdd; i++, j++) {
            spriteTwoElemMergeAndAdd(spriteDataGeoSprite, spriteDataGeoSpriteColors, j, destsum, src, spgRandomVals[rIdx], spgRandomVals[++rIdx]);
        }
        occupiedSpritesNumFromBuffer = j;
    }

}
