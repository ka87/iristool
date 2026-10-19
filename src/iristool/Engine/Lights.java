/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.BFWOWChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserLight;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import static iristool.Engine.RenderUtils.getGeometryICTFromLODGAO;
import java.util.ArrayList;

/**
 *
 * @author andra
 */
public class Lights {

    private static ArrayList<BFGaoChunk> lgtArray = new ArrayList<>();
    private static int calculatedSpecularColors[];
    private static int calculatedNormalColors[];
    private static int vertexPaintColors[] = null;
    private static int calculatedFinalColors[];
    private static Matrix4x4 currMtx = new Matrix4x4();
    private static Matrix4x4 tmpMtx = new Matrix4x4();
    private static boolean realTimeLightingEnabled = true;

    public static int[] getCalculatedSpecularColors() {
        return calculatedSpecularColors;
    }

    public static int[] getCalculatedNormalColors() {
        return calculatedNormalColors;
    }

    public static int[] getVertexPaintColors() {
        return vertexPaintColors;
    }

    public static int[] getCalculatedFinalColors() {
        return calculatedFinalColors;
    }

    public static boolean isRealTimeLightingEnabled() {
        return realTimeLightingEnabled;
    }

    public static void setRealTimeLightingEnabled(boolean realTimeLightingEnabled) {
        Lights.realTimeLightingEnabled = realTimeLightingEnabled;
    }

    private static void computeSpotLight(int dest[], GAOInnerParserLight gaoLight, Vector3 vertices[],
            Matrix4x4 lightSourceMatrix, int lightColor, int lightTypeTable[], Vector3 normals[]) {
        float sqDist;
        int flags;
        float vertFromPosDotProduct;
        float vertTransformedX;
        float vertTransformedY;
        float vertTransformedZ;
        int multipliedColor;
        float tmp;
        float invVertFromDp;
        float densityCalculated;
        float vertFromPosDistX;
        float vertFromPosDistY;
        float vertFromPosDistZ;
        double dMin;
        double dMax;
        float densityRangeMin;
        float endSq;
        float density;
        float densityRangeMax;
        float startSq;
        float tmpFactor;
        float dpMultInv;
        float finalFactor;
        float tmpInvDp;
        float multiplyfactor;
        int i;

        startSq = gaoLight.getStart() * gaoLight.getStart();
        endSq = gaoLight.getEnd() * gaoLight.getEnd();
        sqDist = endSq - startSq;
        if (sqDist != 0.0f) {
            sqDist = RenderUtils.getInverseLightForFloat(sqDist);
        }

        dMin = 162.975 * gaoLight.getDensityMin() + RenderUtils.getUnknownMagic();
        densityRangeMin = RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(dMin) & 0x3ff)];
        dMax = 162.975 * gaoLight.getDensityMax() + RenderUtils.getUnknownMagic();
        densityRangeMax = RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(dMax) & 0x3ff)];
        density = densityRangeMax - densityRangeMin;
        if (density != 0.0) {
            density = RenderUtils.getInverseLightForFloat(density);
        }
        flags = gaoLight.getFlags();
        if ((flags & 0x80) == 0) {
            if ((flags & 0x10) != 0) {
                for (i = 0; i < vertices.length; i++) {
                    vertFromPosDistX = vertices[i].getX() - lightSourceMatrix.getPos().getX();
                    vertFromPosDistY = vertices[i].getY() - lightSourceMatrix.getPos().getY();
                    vertFromPosDistZ = vertices[i].getZ() - lightSourceMatrix.getPos().getZ();
                    if (vertFromPosDistY * lightSourceMatrix.getM22() + vertFromPosDistZ * lightSourceMatrix.getM23() + vertFromPosDistX * lightSourceMatrix.getM21() <= 0.0f) {
                        vertFromPosDotProduct = vertFromPosDistX * vertFromPosDistX + vertFromPosDistZ * vertFromPosDistZ + vertFromPosDistY * vertFromPosDistY;
                        if (vertFromPosDotProduct <= endSq) {
                            multiplyfactor = 1.0f;
                            if (vertFromPosDotProduct > startSq) {
                                multiplyfactor = 1.0f - (vertFromPosDotProduct - startSq) * sqDist;
                            }
                            vertTransformedX = vertFromPosDistZ * lightSourceMatrix.getM22() - vertFromPosDistY * lightSourceMatrix.getM23();
                            vertTransformedY = vertFromPosDistX * lightSourceMatrix.getM23() - vertFromPosDistZ * lightSourceMatrix.getM21();
                            vertTransformedZ = vertFromPosDistY * lightSourceMatrix.getM21() - vertFromPosDistX * lightSourceMatrix.getM22();
                            densityCalculated = (float) Math.sqrt(vertTransformedX * vertTransformedX + vertTransformedZ * vertTransformedZ + vertTransformedY * vertTransformedY) / RenderUtils.getRootForFloat(vertFromPosDotProduct);
                            if (densityCalculated <= densityRangeMax) {
                                if (densityCalculated > densityRangeMin) {
                                    multiplyfactor = (densityRangeMax - densityCalculated) * multiplyfactor * density;
                                }
                                multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, multiplyfactor, lightTypeTable);
                                dest[i] = RenderUtils.addColorForLighting(dest[i], multipliedColor);
                            }
                        }
                    }
                }
            } else {
                for (i = 0; i < vertices.length; i++) {
                    vertFromPosDistX = vertices[i].getX() - lightSourceMatrix.getPos().getX();
                    vertFromPosDistY = vertices[i].getY() - lightSourceMatrix.getPos().getY();
                    vertFromPosDistZ = vertices[i].getZ() - lightSourceMatrix.getPos().getZ();
                    if (vertFromPosDistY * lightSourceMatrix.getM22()
                            + vertFromPosDistZ * lightSourceMatrix.getM23()
                            + vertFromPosDistX * lightSourceMatrix.getM21() <= 0.0) {
                        vertFromPosDotProduct = vertFromPosDistX * vertFromPosDistX + vertFromPosDistZ * vertFromPosDistZ + vertFromPosDistY * vertFromPosDistY;
                        if (vertFromPosDotProduct <= endSq) {
                            tmp = -(normals[i].getZ() * vertFromPosDistZ + normals[i].getY() * vertFromPosDistY + vertFromPosDistX * normals[i].getX());
                            if (tmp > 0.0f) {
                                invVertFromDp = 1.0f / RenderUtils.getRootForFloat(vertFromPosDotProduct);
                                tmpInvDp = (float) (tmp * invVertFromDp);
                                if (vertFromPosDotProduct > (double) startSq) {
                                    tmpInvDp = (1.0f - (vertFromPosDotProduct - startSq) * sqDist) * tmpInvDp;
                                }
                                vertTransformedX = vertFromPosDistZ * lightSourceMatrix.getM22() - vertFromPosDistY * lightSourceMatrix.getM23();
                                vertTransformedY = vertFromPosDistX * lightSourceMatrix.getM23() - vertFromPosDistZ * lightSourceMatrix.getM21();
                                vertTransformedZ = vertFromPosDistY * lightSourceMatrix.getM21() - vertFromPosDistX * lightSourceMatrix.getM22();
                                dpMultInv = (float) (Math.sqrt(vertTransformedX * vertTransformedX + vertTransformedZ * vertTransformedZ + vertTransformedY * vertTransformedY) * invVertFromDp);
                                if (dpMultInv <= densityRangeMax) {
                                    if (dpMultInv > densityRangeMin) {
                                        tmpInvDp = (densityRangeMax - dpMultInv) * tmpInvDp * density;
                                    }
                                    multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, tmpInvDp, lightTypeTable);
                                    dest[i] = RenderUtils.addColorForLighting(dest[i], multipliedColor);
                                }
                            }
                        }
                    }
                }
            }
        } else if ((flags & 0x10) != 0) {
            for (i = 0; i < vertices.length; i++) {
                vertFromPosDistX = vertices[i].getX() - lightSourceMatrix.getPos().getX();
                vertFromPosDistY = vertices[i].getY() - lightSourceMatrix.getPos().getY();
                vertFromPosDistZ = vertices[i].getZ() - lightSourceMatrix.getPos().getZ();
                if (vertFromPosDistY * lightSourceMatrix.getM22()
                        + vertFromPosDistZ * lightSourceMatrix.getM23()
                        + vertFromPosDistX * lightSourceMatrix.getM21() <= 0.0) {
                    vertFromPosDotProduct = vertFromPosDistX * vertFromPosDistX + vertFromPosDistZ * vertFromPosDistZ + vertFromPosDistY * vertFromPosDistY;
                    if (vertFromPosDotProduct <= endSq) {
                        finalFactor = 1.0f;
                        if (vertFromPosDotProduct > startSq) {
                            finalFactor = (float) (1.0f - (vertFromPosDotProduct - startSq) * sqDist);
                        }
                        vertTransformedX = vertFromPosDistZ * lightSourceMatrix.getM22() - vertFromPosDistY * lightSourceMatrix.getM23();
                        vertTransformedY = vertFromPosDistX * lightSourceMatrix.getM23() - vertFromPosDistZ * lightSourceMatrix.getM21();
                        vertTransformedZ = vertFromPosDistY * lightSourceMatrix.getM21() - vertFromPosDistX * lightSourceMatrix.getM22();
                        tmpFactor = (float) (Math.sqrt(vertTransformedX * vertTransformedX + vertTransformedZ * vertTransformedZ + vertTransformedY * vertTransformedY) / RenderUtils.getRootForFloat(vertFromPosDotProduct));
                        if (tmpFactor <= densityRangeMax) {
                            if (tmpFactor > densityRangeMin) {
                                finalFactor = (densityRangeMax - tmpFactor) * finalFactor * density;
                            }
                            multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, finalFactor, lightTypeTable);
                            dest[i] = RenderUtils.substractColorForLighting(dest[i], multipliedColor);
                        }
                    }
                }
            }
        } else {
            for (i = 0; i < vertices.length; i++) {
                vertFromPosDistX = vertices[i].getX() - lightSourceMatrix.getPos().getX();
                vertFromPosDistY = vertices[i].getY() - lightSourceMatrix.getPos().getY();
                vertFromPosDistZ = vertices[i].getZ() - lightSourceMatrix.getPos().getZ();
                if (vertFromPosDistY * lightSourceMatrix.getM22() + vertFromPosDistZ * lightSourceMatrix.getM23() + vertFromPosDistX * lightSourceMatrix.getM21() <= 0.0f) {
                    vertFromPosDotProduct = vertFromPosDistX * vertFromPosDistX + vertFromPosDistZ * vertFromPosDistZ + vertFromPosDistY * vertFromPosDistY;
                    if (vertFromPosDotProduct <= endSq) {
                        tmp = -(normals[i].getZ() * vertFromPosDistZ + normals[i].getY() * vertFromPosDistY + vertFromPosDistX * normals[i].getX());
                        if (tmp > 0.0f) {
                            invVertFromDp = 1.0f / RenderUtils.getRootForFloat(vertFromPosDotProduct);
                            tmpInvDp = (tmp * invVertFromDp);
                            if (vertFromPosDotProduct > startSq) {
                                tmpInvDp = (float) ((1.0f - (vertFromPosDotProduct - startSq) * sqDist) * tmpInvDp);
                            }
                            vertTransformedX = vertFromPosDistZ * lightSourceMatrix.getM22() - vertFromPosDistY * lightSourceMatrix.getM23();
                            vertTransformedY = vertFromPosDistX * lightSourceMatrix.getM23() - vertFromPosDistZ * lightSourceMatrix.getM21();
                            vertTransformedZ = vertFromPosDistY * lightSourceMatrix.getM21() - vertFromPosDistX * lightSourceMatrix.getM22();
                            dpMultInv = (float) (Math.sqrt(vertTransformedX * vertTransformedX + vertTransformedZ * vertTransformedZ + vertTransformedY * vertTransformedY) * invVertFromDp);
                            if (dpMultInv <= densityRangeMax) {
                                if (dpMultInv > densityRangeMin) {
                                    tmpInvDp = (densityRangeMax - dpMultInv) * tmpInvDp * density;
                                }
                                multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, tmpInvDp, lightTypeTable);
                                dest[i] = RenderUtils.substractColorForLighting(dest[i], multipliedColor);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void computeDirectionalLight(int dest[], GAOInnerParserLight gaoLight, Vector3 vertices[], Matrix4x4 lightSourceMatrix, int lightColor, int lightTypeTable[]) {
        int flags;
        float factor;
        int color;
        float factorFixed;
        int i;

        lightSourceMatrix.setM21(-lightSourceMatrix.getM21());
        lightSourceMatrix.setM22(-lightSourceMatrix.getM22());
        lightSourceMatrix.setM23(-lightSourceMatrix.getM23());
        flags = gaoLight.getFlags();

        if ((flags & 0x80) == 0) {
            if ((flags & 0x10) != 0) {
                for (i = 0; i < vertices.length; i++) {
                    dest[i] = RenderUtils.addColorForLighting(dest[i], lightColor);
                }
            } else {
                for (i = 0; i < vertices.length; i++) {
                    factor = -(vertices[i].getY() * lightSourceMatrix.getM22()
                            + vertices[i].getZ() * lightSourceMatrix.getM23()
                            + lightSourceMatrix.getM21() * vertices[i].getX());
                    if (factor < 0.0f) {
                        factorFixed = 0.0f;
                    } else {
                        factorFixed = factor;
                    }
                    color = RenderUtils.multiplyColorForLighting(lightColor, factorFixed, lightTypeTable);
                    dest[i] = RenderUtils.addColorForLighting(dest[i], color);
                }
            }
        } else if ((flags & 0x10) != 0) {
            for (i = 0; i < vertices.length; i++) {
                dest[i] = RenderUtils.substractColorForLighting(dest[i], lightColor);
            }
        } else {
            for (i = 0; i < vertices.length; i++) {
                factor = -(vertices[i].getY() * lightSourceMatrix.getM22()
                        + vertices[i].getZ() * lightSourceMatrix.getM23()
                        + lightSourceMatrix.getM21() * vertices[i].getX());
                if (factor < 0.0f) {
                    factorFixed = 0.0f;
                } else {
                    factorFixed = factor;
                }
                color = RenderUtils.multiplyColorForLighting(lightColor, factorFixed, lightTypeTable);
                dest[i] = RenderUtils.substractColorForLighting(dest[i], color);
            }
        }

        lightSourceMatrix.setM21(-lightSourceMatrix.getM21());
        lightSourceMatrix.setM22(-lightSourceMatrix.getM22());
        lightSourceMatrix.setM23(-lightSourceMatrix.getM23());
    }

    private static void computeOmniLight(int dest[], GAOInnerParserLight gaoLight, Vector3 vertices[],
            Matrix4x4 lightSourceMatrix, Matrix4x4 gaoGlobalMatrixInverted, int lightColor, int lightTypeTable[], Vector3 normals[]) {
        float avgScale;
        double fs_and_fe_diff;
        double fsSQ_and_feSQDiff;
        int flags;
        float yDist;
        float zDist;
        float xDist;
        int tmpInvR;
        double lightpropMtxPosAndvVertexPosDiffSQSum;
        int multipliedColor;
        float invVecLen;
        double minusDp;
        float v36;
        double xDistSq;
        float allVecSum;
        double invRootAllVec;
        float minusX;
        float minusY;
        float minusZ;
        float llightpropMtxPosAndvVertexPosDiffSQSum;
        float ffsSQ_and_feSQDiff;
        float fogend_and_possum_productSQ;
        float Inv_fs_and_fe_dif;
        float fogstart_and_possum_product;
        float fogend_and_possum_product;
        float fogstart_and_possum_productSQ;
        float calcdVecLen;
        float factor;
        Vector3 scale = new Vector3();
        int i;

        Matrix4x4.getScale(scale, gaoGlobalMatrixInverted);
        avgScale = (scale.getZ() + scale.getY() + scale.getX()) * 0.333f;
        fogstart_and_possum_product = avgScale * gaoLight.getEnd();
        fogend_and_possum_product = avgScale * gaoLight.getStart();
        fs_and_fe_diff = fogstart_and_possum_product - fogend_and_possum_product;
        Inv_fs_and_fe_dif = (float) fs_and_fe_diff;
        if (fs_and_fe_diff != 0.0f) {
            ffsSQ_and_feSQDiff = Inv_fs_and_fe_dif;
            Inv_fs_and_fe_dif = RenderUtils.getInverseLightForFloat(Inv_fs_and_fe_dif);
        }
        fogstart_and_possum_productSQ = fogstart_and_possum_product * fogstart_and_possum_product;
        fogend_and_possum_productSQ = fogend_and_possum_product * fogend_and_possum_product;
        fsSQ_and_feSQDiff = fogstart_and_possum_productSQ - fogend_and_possum_productSQ;
        ffsSQ_and_feSQDiff = (float) fsSQ_and_feSQDiff;
        if (fsSQ_and_feSQDiff != 0.0f) {
            //InvffsSQ_and_feSQDiff = RenderUtils.getInverseLightTable()[(Float.floatToIntBits(ffsSQ_and_feSQDiff) >> 13) & 0x3ff];
            calcdVecLen = ffsSQ_and_feSQDiff;
            ffsSQ_and_feSQDiff = RenderUtils.getInverseLightForFloat(ffsSQ_and_feSQDiff);
        }
        flags = gaoLight.getFlags();
        if ((flags & 0x80) == 0) {
            if ((flags & 0x10) != 0) {
                for (i = 0; i < vertices.length; i++) {
                    yDist = vertices[i].getY() - lightSourceMatrix.getPos().getY();
                    zDist = vertices[i].getZ() - lightSourceMatrix.getPos().getZ();
                    lightpropMtxPosAndvVertexPosDiffSQSum = zDist * zDist + yDist * yDist + (vertices[i].getX() - lightSourceMatrix.getPos().getX()) * (vertices[i].getX() - lightSourceMatrix.getPos().getX());
                    if (lightpropMtxPosAndvVertexPosDiffSQSum <= fogstart_and_possum_productSQ) {
                        llightpropMtxPosAndvVertexPosDiffSQSum = (float) lightpropMtxPosAndvVertexPosDiffSQSum;
                        calcdVecLen = RenderUtils.getRootForFloat(llightpropMtxPosAndvVertexPosDiffSQSum);
                        if (lightpropMtxPosAndvVertexPosDiffSQSum <= fogend_and_possum_productSQ) {
                            factor = 1.0f;
                        } else {
                            factor = (fogstart_and_possum_product - calcdVecLen) * Inv_fs_and_fe_dif;
                        }
                        multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, factor, lightTypeTable);
                        dest[i] = RenderUtils.addColorForLighting(dest[i], multipliedColor);
                    }
                }
            } else if ((flags & 0x100000) != 0) {
                minusZ = -lightSourceMatrix.getPos().getZ();
                minusY = -lightSourceMatrix.getPos().getY();
                minusX = -lightSourceMatrix.getPos().getX();
                calcdVecLen = (float) (Math.sqrt((-lightSourceMatrix.getPos().getZ()) * (-lightSourceMatrix.getPos().getZ())
                        + (-lightSourceMatrix.getPos().getY()) * (-lightSourceMatrix.getPos().getY())
                        + (-lightSourceMatrix.getPos().getX()) * (-lightSourceMatrix.getPos().getX())));
                if (calcdVecLen <= fogstart_and_possum_product) {
                    invVecLen = 1.0f / calcdVecLen;
                    minusX = minusX * invVecLen;
                    minusY = minusY * invVecLen;
                    minusZ = invVecLen * minusZ;
                    ffsSQ_and_feSQDiff = (calcdVecLen <= fogend_and_possum_product ? 1.0f : ((fogstart_and_possum_productSQ - calcdVecLen * calcdVecLen) * ffsSQ_and_feSQDiff));
                    for (i = 0; i < vertices.length; i++) {
                        if (vertices[i].getX() != 0.0 || vertices[i].getY() != 0.0 || vertices[i].getZ() != 0.0) {
                            minusDp = -(vertices[i].getZ() * minusZ + vertices[i].getY() * minusY + minusX * vertices[i].getX());
                            if (minusDp > 0.0) {
                                v36 = (float) (ffsSQ_and_feSQDiff * minusDp);
                                multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, v36, lightTypeTable);
                                dest[i] = RenderUtils.addColorForLighting(dest[i], multipliedColor);
                            }
                        }
                    }
                }
            } else {
                for (i = 0; i < vertices.length; i++) {
                    if (vertices[i].getX() != 0.0 || vertices[i].getY() != 0.0 || vertices[i].getZ() != 0.0) {
                        xDist = vertices[i].getX() - lightSourceMatrix.getPos().getX();
                        xDistSq = xDist * xDist;
                        if (xDistSq <= fogstart_and_possum_productSQ) {
                            yDist = vertices[i].getY() - lightSourceMatrix.getPos().getY();
                            fogend_and_possum_product = (float) (xDistSq + yDist * yDist);
                            if (fogend_and_possum_product <= fogstart_and_possum_productSQ) {
                                zDist = vertices[i].getZ() - lightSourceMatrix.getPos().getZ();
                                allVecSum = zDist * zDist + fogend_and_possum_product;
                                if (allVecSum <= fogstart_and_possum_productSQ) {
                                    factor = (float) (-(vertices[i].getZ() * zDist + vertices[i].getY() * yDist + xDist * vertices[i].getX()));
                                    if (factor > 0.0) {
                                        if (allVecSum > (double) fogend_and_possum_productSQ) {
                                            factor = (fogstart_and_possum_productSQ - allVecSum) * ffsSQ_and_feSQDiff * factor;
                                        }
                                        invRootAllVec = RenderUtils.getInverseRootForFloat(allVecSum);
                                        factor = (float) (invRootAllVec * factor);
                                        multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, factor, lightTypeTable);
                                        dest[i] = RenderUtils.addColorForLighting(dest[i], multipliedColor);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if ((flags & 0x10) != 0) {
            for (i = 0; i < vertices.length; i++) {
                yDist = vertices[i].getY() - lightSourceMatrix.getPos().getY();
                zDist = vertices[i].getZ() - lightSourceMatrix.getPos().getZ();
                ffsSQ_and_feSQDiff = zDist * zDist + yDist * yDist
                        + (vertices[i].getX() - lightSourceMatrix.getPos().getX())
                        * (vertices[i].getX() - lightSourceMatrix.getPos().getX());
                if (ffsSQ_and_feSQDiff <= fogstart_and_possum_productSQ) {
                    calcdVecLen = RenderUtils.getInverseRootForFloat(ffsSQ_and_feSQDiff);
                    if (ffsSQ_and_feSQDiff <= fogend_and_possum_productSQ) {
                        factor = 1.0f;
                    } else {
                        factor = (fogstart_and_possum_product - calcdVecLen) * Inv_fs_and_fe_dif;
                    }
                    multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, factor, lightTypeTable);
                    dest[i] = RenderUtils.substractColorForLighting(dest[i], multipliedColor);
                }
            }
        } else {
            for (i = 0; i < vertices.length; i++) {
                xDist = vertices[i].getX() - lightSourceMatrix.getPos().getX();
                yDist = vertices[i].getY() - lightSourceMatrix.getPos().getY();
                zDist = vertices[i].getZ() - lightSourceMatrix.getPos().getZ();
                fogend_and_possum_product = zDist * zDist + yDist * yDist + xDist * xDist;
                if (fogend_and_possum_product <= fogstart_and_possum_productSQ) {
                    factor = (float) (-(vertices[i].getZ() * zDist + vertices[i].getY() * yDist + xDist * vertices[i].getX()));
                    if (factor >= 0.0) {
                        //ffsSQ_and_feSQDiff = fogend_and_possum_product;
                        tmpInvR = RenderUtils.getInverseRootTable()[((RenderUtils.getRootTable()[(Float.floatToIntBits(fogend_and_possum_product) >> 13) & 0x7FF]
                                + ((((Float.floatToIntBits(fogend_and_possum_product) & 0x7F800000) + 0x3F800000) >> 1) & 0x7F800000)) >> 13) & 0x3FF];
                        calcdVecLen = Float.intBitsToFloat(RenderUtils.getRootTable()[(Float.floatToIntBits(fogend_and_possum_product) >> 13) & 0x7FF]
                                + ((((Float.floatToIntBits(fogend_and_possum_product) & 0x7F800000) + 0x3F800000) >> 1) & 0x7F800000));
                        //llightpropMtxPosAndvVertexPosDiffSQSum = calcdVecLen;
                        ffsSQ_and_feSQDiff = Float.intBitsToFloat(tmpInvR - (Float.floatToIntBits(calcdVecLen) & 0xFF800000) + 0x7E800000);
                        factor = ffsSQ_and_feSQDiff * factor;
                        if (fogend_and_possum_product > (double) fogend_and_possum_productSQ) {
                            factor = (fogstart_and_possum_product - calcdVecLen) * Inv_fs_and_fe_dif * factor;
                        }
                        multipliedColor = RenderUtils.multiplyColorForLighting(lightColor, factor, lightTypeTable);
                        dest[i] = RenderUtils.substractColorForLighting(dest[i], multipliedColor);
                    }
                }
            }
        }
    }

    public static Vector3[] calculateSpecularColorForGao(BFGaoChunk gao, Matrix4x4 currMtx, Vector3 normals[]) {
        int i;
        float vertMinusInvPosX;
        float vertMinusInvPosY;
        float vertMinusInvPosZ;
        float f;
        float fr;
        float vertMinusInvPosAndNormalSumPlusMagic;
        Vector3 invPos = new Vector3(0.0f, 0.0f, 0.0f);;
        Matrix4x4 invCurrMtx = new Matrix4x4();

        BFGaoInnerChunkParser icp = getGeometryICTFromLODGAO(gao);
        if (icp == null || icp.getGaoInnerType() != BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY || icp.getGaoInner() == null) {
            System.out.println("BUG: Gao: " + gao.getGaoName() + " has no geometry.");
            return null;
        }

        GAOInnerParserGeometry geo = (GAOInnerParserGeometry) (icp.getGaoInner());

        Vector3 vertices[] = geo.getVertices();
        if (vertices.length <= 0) {
            return null;
        }
        Vector3 ret[] = new Vector3[vertices.length];

        Matrix4x4.invertMatrix(invCurrMtx, currMtx);
        Vector3.transformVertex(invPos, invCurrMtx, invPos);

        if ((gao.getDesignFlags1() & 0x8000) != 0) {
            for (i = 0; i < vertices.length; i++) {
                vertMinusInvPosX = vertices[i].getX() - invPos.getX();
                vertMinusInvPosY = vertices[i].getY() - invPos.getY();
                vertMinusInvPosZ = vertices[i].getZ() - invPos.getZ();
                f = vertMinusInvPosZ * vertMinusInvPosZ + vertMinusInvPosY * vertMinusInvPosY + vertMinusInvPosX * vertMinusInvPosX;
                fr = RenderUtils.getInverseRootForFloat(f);

                vertMinusInvPosAndNormalSumPlusMagic = (vertMinusInvPosZ * normals[i].getZ() + vertMinusInvPosY * normals[i].getY() + vertMinusInvPosX * normals[i].getX()) * -1.9f;

                ret[i] = new Vector3();
                ret[i].setX((vertMinusInvPosAndNormalSumPlusMagic * normals[i].getX() + vertMinusInvPosX) * fr);
                ret[i].setY((vertMinusInvPosAndNormalSumPlusMagic * normals[i].getY() + vertMinusInvPosY) * fr);
                ret[i].setZ((vertMinusInvPosAndNormalSumPlusMagic * normals[i].getZ() + vertMinusInvPosZ) * fr);
            }
        } else {
            for (i = 0; i < vertices.length; i++) {
                vertMinusInvPosX = -vertices[i].getX() - invPos.getX();
                vertMinusInvPosY = vertices[i].getY() - invPos.getY();
                vertMinusInvPosZ = vertices[i].getZ() - invPos.getZ();
                f = vertMinusInvPosZ * vertMinusInvPosZ + vertMinusInvPosY * vertMinusInvPosY + vertMinusInvPosX * vertMinusInvPosX;
                fr = RenderUtils.getInverseRootForFloat(f);

                vertMinusInvPosAndNormalSumPlusMagic = (vertMinusInvPosZ * normals[i].getZ() - vertMinusInvPosX * normals[i].getX() + vertMinusInvPosY * normals[i].getY()) * -1.9f;

                ret[i] = new Vector3();
                ret[i].setX(-((vertMinusInvPosX - vertMinusInvPosAndNormalSumPlusMagic * normals[i].getX()) * fr));
                ret[i].setY((vertMinusInvPosAndNormalSumPlusMagic * normals[i].getY() + vertMinusInvPosY) * fr);
                ret[i].setZ((vertMinusInvPosAndNormalSumPlusMagic * normals[i].getZ() + vertMinusInvPosZ) * fr);
            }
        }
        return ret;
    }

    private static boolean gaoShouldBeLit(GAOInnerParserLight lgt, BFGaoChunk gaoToLight, BFGaoChunk lgtGao, boolean checkBoundingBox) {
        BFGaoChunk refGao;
        Vector3 tmpVec = new Vector3();
        if ((gaoToLight.getVisualFlags() & 8) != 0) {
            if ((lgt.getFlags() & 0x100) == 0) {
                return false;
            }
        } else if ((lgt.getFlags() & 0x200) == 0) {
            return false;
        }
        if (lgt.getGaoChunkKLE() != null && lgt.getGaoChunkKLE().getParseHandler() != null && lgt.getGaoChunkKLE().getParseHandler() != RenderUtils.getBaseGaoOfGao(gaoToLight)) {
            return false;
        }
        if (checkBoundingBox && gaoToLight.getBoundingBox1() != null && gaoToLight.getBoundingBox2() != null
                && ((lgt.getFlags() & 7) == 0 || (lgt.getFlags() & 7) == 2)) {
            if ((gaoToLight.getFlags() & 0x600001) == 0x600001
                    && (gaoToLight.getBoneShapes() != null && gaoToLight.getBoneShapes().size() > 1)
                    && (gaoToLight.getReferencePointGAOChunkKLE() != null)
                    && (((refGao = (BFGaoChunk) (gaoToLight.getReferencePointGAOChunkKLE().getParseHandler())) != null))) {
                Vector3.transformVertex(tmpVec, refGao.getGlobalMatrix(), gaoToLight.getLocalMatrix().getPos());
            } else {
                tmpVec.set(gaoToLight.getGlobalMatrix().getPos());
            }
            tmpVec.setX(lgtGao.getGlobalMatrix().getPos().getX() - tmpVec.getX());
            tmpVec.setY(lgtGao.getGlobalMatrix().getPos().getY() - tmpVec.getY());
            tmpVec.setZ(lgtGao.getGlobalMatrix().getPos().getZ() - tmpVec.getZ());
            float radius = lgt.getEnd();
            if (!BV.checkIntersectionBetweenSphereAndAABB(tmpVec, radius, gaoToLight.getBoundingBox1(), gaoToLight.getBoundingBox2())) {
                return false;
            }

        }
        return true;
    }

    private static void computeLighting(BFGaoChunk gaoToLight, Matrix4x4 currMtx, boolean lightingEnabled) {
        int tmp;
        boolean noFlag0x40000000;
        Matrix4x4 objectGlobalPos;
        int flagsForCase;
        Vector3 centerOfObjectPosGlobal = new Vector3();
        Vector3 BVCenter = new Vector3();
        float factor = 0.0f;
        boolean pos1OrPos2AndPos3;
        int all;
        boolean initNeeded;
        int currColorModified;
        Matrix4x4 objectGlobalPosInverted = new Matrix4x4();
        Matrix4x4 currLightSourceMtxLocal = new Matrix4x4();
        int i;

        calculatedSpecularColors = null;

        if (lightingEnabled) {
            if (lgtArray.size() > 0) {
                BFGaoInnerChunkParser icp = getGeometryICTFromLODGAO(gaoToLight);
                if (icp == null || icp.getGaoInnerType() != BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY || icp.getGaoInner() == null) {
                    System.out.println("BUG: Gao: " + gaoToLight.getGaoName() + " has no geometry.");
                    return;
                }
                GAOInnerParserGeometry geo = ((GAOInnerParserGeometry) icp.getGaoInner());
                Vector3 normals[] = OpenGLFunctions.getVertexNormals(geo.getVertices(), geo.getNormalsTemp(), geo.getNormalsNumTemp(), geo.getGeometryTriangleChunks());

                if (normals == null) {
                    System.out.println("BUG: Gao: " + gaoToLight.getGaoName() + " normals calc failed.");
                    return;
                }

                if (geo.getVertices() != null && geo.getVertices().length > 0) {
                    calculatedSpecularColors = new int[geo.getVertices().length];
                }

                tmp = ~(gaoToLight.getDesignFlags1() >> 13) & 1;
                all = (~(gaoToLight.getDesignFlags1() >> 13) & 1);
                pos1OrPos2AndPos3 = false;
                if ((gaoToLight.getDesignFlags1() & 0x8000) == 0) {
                    pos1OrPos2AndPos3 = true;
                    all = tmp ^ 1;
                }
                initNeeded = true;

                boolean BVCheck = (gaoToLight.getDesignFlags1() & 0x200000) != 0;
                noFlag0x40000000 = ((~(gaoToLight.getDesignFlags1() >> 30)) & 1) != 0;
                if (noFlag0x40000000) {
                    System.out.println("FIXME: no_flag_40000000 should be checked. Gao: " + gaoToLight.getGaoName());
                    factor = (127.0f - ((gaoToLight.getDesignFlags2() >> 8) & 0xff)) * 0.0078740157f * (1.0f /*- [lightfactor UP DN] TODO*/);
                }

                for (i = 0; i < lgtArray.size(); i++) {
                    BFGaoChunk lgtGao = lgtArray.get(i);
                    BFGaoInnerChunkParser lgtIC = (BFGaoInnerChunkParser) lgtGao.getLightGAOChunkKLE().getParseHandler();
                    if (lgtIC.getGaoInnerType() != BFGaoInnerChunkParser.BF_GAO_TYPE_LIGHT) {
                        System.out.println("BUG: Gao: " + lgtGao.getGaoName() + " is not a light gao.");
                        continue;
                    }
                    GAOInnerParserLight lgt = (GAOInnerParserLight) lgtIC.getGaoInner();
                    if (!gaoShouldBeLit(lgt, gaoToLight, lgtGao, BVCheck)) { // TODO: BVs should be recalculated, not just used
                        continue;
                    }
                    if ((lgt.getFlags() & 7) == 4) {
                        System.out.println("BUG: material light type (4) unsupported.");
                        continue;
                    }
                    if (initNeeded) {
                        objectGlobalPos = gaoToLight.getGlobalMatrix();
                        initNeeded = false;
                        Matrix4x4.invertMatrix(objectGlobalPosInverted, objectGlobalPos);
                        if (gaoToLight.getBoundingBox1() != null && gaoToLight.getBoundingBox2() != null) {
                            Vector3 minBV = gaoToLight.getBoundingBox1();
                            Vector3 maxBV = gaoToLight.getBoundingBox2();

                            BVCenter.setX((maxBV.getX() + minBV.getX()) * 0.5f);
                            BVCenter.setY((maxBV.getY() + minBV.getY()) * 0.5f);
                            BVCenter.setZ((maxBV.getZ() + minBV.getZ()) * 0.5f);
                            Vector3.transformVector(centerOfObjectPosGlobal, objectGlobalPosInverted, BVCenter);
                        } else {
                            BVCenter.set(0.0f, 0.0f, 0.0f);
                        }
                        RenderUtils.invertForLighting(normals, pos1OrPos2AndPos3, all != 0);
                    }
                    Matrix4x4.multMatrix(currLightSourceMtxLocal, lgtGao.getGlobalMatrix(), objectGlobalPosInverted);
                    if ((lgt.getFlags() & 0x100007) == 0x100000) {
                        currLightSourceMtxLocal.getPos().setX(currLightSourceMtxLocal.getPos().getX() - centerOfObjectPosGlobal.getX());
                        currLightSourceMtxLocal.getPos().setY(currLightSourceMtxLocal.getPos().getY() - centerOfObjectPosGlobal.getY());
                        currLightSourceMtxLocal.getPos().setZ(currLightSourceMtxLocal.getPos().getZ() - centerOfObjectPosGlobal.getZ());
                    }
                    currColorModified = lgt.getColor();
                    if ((lgt.getFlags() & 0x20) == 0) {
                        currColorModified &= 0xFF000000;
                    }
                    if ((lgt.getFlags() & 0x40) == 0) {
                        currColorModified &= ~0xff000000;
                    }
                    if (noFlag0x40000000 && (lgt.getFlags() & 0x200000) == 0) {
                        currColorModified = RenderUtils.multiplyColorForLighting(currColorModified, factor, RenderUtils.getLightNormalTable());
                    }

                    flagsForCase = lgt.getFlags() & 0x1007;
                    Vector3 specVecs[];
                    switch (flagsForCase) {
                        case 0x0:
                            computeOmniLight(calculatedNormalColors, lgt, normals, currLightSourceMtxLocal, objectGlobalPosInverted, currColorModified, RenderUtils.getLightNormalTable(), normals);
                            break;
                        case 0x1:
                            computeDirectionalLight(calculatedNormalColors, lgt, normals, currLightSourceMtxLocal, currColorModified, RenderUtils.getLightNormalTable());
                            break;
                        case 0x2:
                            computeSpotLight(calculatedNormalColors, lgt, normals, currLightSourceMtxLocal, currColorModified, RenderUtils.getLightNormalTable(), normals);
                            break;
                        case 0x1000:
                            lgt.setEnd(lgt.getEnd() + lgt.getEnd());
                            lgt.setStart(lgt.getStart() * 0.25f);
                            specVecs = calculateSpecularColorForGao(gaoToLight, currMtx, normals);
                            computeOmniLight(calculatedSpecularColors, lgt, specVecs, currLightSourceMtxLocal, objectGlobalPosInverted, currColorModified, RenderUtils.getLightExponentTable(), normals);
                            lgt.setEnd(lgt.getEnd() * 0.5f);
                            lgt.setStart(lgt.getStart() * 4.00f);
                            computeOmniLight(calculatedNormalColors, lgt, normals, currLightSourceMtxLocal, objectGlobalPosInverted, currColorModified, RenderUtils.getLightNormalTable(), normals);
                            break;
                        case 0x1001:
                            specVecs = calculateSpecularColorForGao(gaoToLight, currMtx, normals);
                            computeDirectionalLight(calculatedSpecularColors, lgt, specVecs, currLightSourceMtxLocal, currColorModified, RenderUtils.getLightExponentTable());
                            computeDirectionalLight(calculatedNormalColors, lgt, normals, currLightSourceMtxLocal, currColorModified, RenderUtils.getLightNormalTable());
                            break;
                        case 0x1002:
                            lgt.setEnd(lgt.getEnd() + lgt.getEnd());
                            specVecs = calculateSpecularColorForGao(gaoToLight, currMtx, normals);
                            computeSpotLight(calculatedSpecularColors, lgt, specVecs, currLightSourceMtxLocal, currColorModified, RenderUtils.getLightExponentTable(), normals);
                            lgt.setEnd(lgt.getEnd() * 0.5f);
                            computeSpotLight(calculatedNormalColors, lgt, normals, currLightSourceMtxLocal, currColorModified, RenderUtils.getLightNormalTable(), normals);
                            break;
                    }
                }
                if (!initNeeded) {
                    RenderUtils.invertForLighting(normals, pos1OrPos2AndPos3, all != 0);
                }
            }
        }
    }

    public static void addLightsFromGaoList(ArrayList<BFGaoChunk> gaoList, OpenGLFunctions oglf) {
        int i, flags, cnt = 0;
        BFGaoChunk tmpGao;
        BFGaoInnerChunkParser lgtIC;
        lgtArray.clear();
        oglf.setFogParamsToRender(0, 0, 0, 0.0f, 1.0f);
        for (i = 0; i < gaoList.size(); i++) {
            tmpGao = gaoList.get(i);
            // gao is disabled
            if ((tmpGao.getVisualFlags() & 0x200000) != 0) {
                continue;
            }
            if (tmpGao == null || tmpGao.getLightGAOChunkKLE() == null || tmpGao.getLightGAOChunkKLE().getParseHandler() == null) {
                continue;
            }
            lgtIC = (BFGaoInnerChunkParser) tmpGao.getLightGAOChunkKLE().getParseHandler();
            if (lgtIC.getGaoInnerType() != BFGaoInnerChunkParser.BF_GAO_TYPE_LIGHT) {
                continue;
            }
            GAOInnerParserLight lgt = (GAOInnerParserLight) lgtIC.getGaoInner();
            flags = lgt.getFlags();
            if ((flags & 8) != 0) {
                if ((flags & 7) == 3) {
                    if (++cnt > 1) {
                        System.out.println("BUG: fog settings overwrite (by " + tmpGao.getGaoName() + ")");
                    }
                    oglf.setFogParamsToRender(1, lgt.getColor(), 0, lgt.getStart(), lgt.getEnd());
                } else if ((flags & 0x40000000) == 0) {
                    lgtArray.add(tmpGao);
                }
            }
        }
    }

    public static void setMatrices(Matrix4x4 globalMatrix, Matrix4x4 viewMatrix, Matrix4x4 rotation) {
        // compute real view matrix, TODO: REALLY NASTY IMPLEMENTATION, should be simplified
        Matrix4x4.matrixMultiplyWithNormalized(currMtx, globalMatrix, rotation);
        Matrix4x4.matrixMultiplyWithNormalized(tmpMtx, currMtx, viewMatrix);
        currMtx.set(tmpMtx);
    }

    public static void handleLights(BFWOWChunk wow, BFGaoChunk gao, int materialFlags) {
        int i;
        if (gao == null) {
            return;
        }

        if ((materialFlags & 0x13) != 0) {

            BFGaoInnerChunkParser geoICToRender = RenderUtils.getGeometryICTFromLODGAO(gao);
            if (geoICToRender.getGaoInnerType() != BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY || geoICToRender.getGaoInner() == null) {
                return;
            }
            GAOInnerParserGeometry geo = (GAOInnerParserGeometry) (geoICToRender.getGaoInner());

            if (geo.getVertices() == null || geo.getVertices().length == 0) {
                return;
            }

            vertexPaintColors = null;

            if (gao.getPrimaryVertexColorArr() != null) {
                vertexPaintColors = gao.getPrimaryVertexColorArr();
            } else {
                vertexPaintColors = ((GAOInnerParserGeometry) geoICToRender.getGaoInner()).getVertexColors();
            }

            calculatedNormalColors = new int[geo.getVertices().length];
            if (vertexPaintColors != null) {
                System.arraycopy(vertexPaintColors, 0, calculatedNormalColors, 0, Math.min(vertexPaintColors.length, calculatedNormalColors.length)); // TODO: unknown cvc stuff
            }

            if (realTimeLightingEnabled && (materialFlags & 3) != 0 && (gao.getDesignFlags1() & 0x8000000) != 0) {
                computeLighting(gao, currMtx, true);
            }

            // ambient
            calculatedFinalColors = new int[geo.getVertices().length];
            for (i = 0; i < calculatedNormalColors.length; i++) {
                calculatedFinalColors[i] = RenderUtils.ambientLightAdd(wow, gao, calculatedNormalColors[i]);
            }

        }
    }

}
