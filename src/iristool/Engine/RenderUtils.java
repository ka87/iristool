/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.BFWOWChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialGroup;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialMultiTexture;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserStaticLOD;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.GeometryTriangleChunk;
import Common.DataType.MaterialMultiTextureSubElem;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import Common.MaterialInterface;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeMap;

/**
 *
 * @author andra
 */
public class RenderUtils {

    private static Stack<Matrix4x4> mtxStack = new Stack<>();
    private static float[] decalTable = null;
    private static int[] invRootTable = null;
    private static int[] rootTable = null;
    private static int[] invLightTable = null;
    private static int[] lightNormalTable = null;
    private static int[] lightExponentTable = null;
    private static double unknownMagic = (Math.pow(2, 51) * 3.0);

    public static float[] getDecalTable() {
        int i;
        if (decalTable == null) {
            decalTable = new float[1024];
            for (i = 0; i < 1024; i++) {
                decalTable[i] = (float) Math.sin(i * 0.006135923322290182); // 1/2Pi
            }
        }
        return decalTable;
    }

    public static double getUnknownMagic() {
        return unknownMagic;
    }

    public static int[] getInverseRootTable() {
        if (invRootTable == null) {
            invRootTable = new int[2048];

            double sqRoot2 = Math.sqrt(2.0);
            double root;
            float nr;
            for (int i = 0; i < 1024; i++) {
                root = Math.sqrt((double) i * 0.0009765625 + 1.0); // 1/2048
                invRootTable[i + 1024] = Float.floatToIntBits((float) (2.0f / root)) & 0x7fffff;
                nr = (float) (sqRoot2 / root);
                invRootTable[i] = (Float.floatToIntBits(nr) & 0x7fffff) + 0x800000;
            }
        }
        return invRootTable;
    }

    public static float getInverseRootForFloat(Float f) {
        int floatInts = Float.floatToIntBits(f);
        return Float.intBitsToFloat(getInverseRootTable()[(floatInts >> 13) & 0x7ff] + (((0xbd800000 - (floatInts & 0x7f800000)) >> 1) & 0x7f800000));
    }

    public static int[] getRootTable() {
        if (rootTable == null) {
            rootTable = new int[2048];

            double sqRoot2 = Math.sqrt(2.0);
            double root;
            float nr;
            for (int i = 0; i < 1024; i++) {
                root = Math.sqrt((double) i * 0.0009765625 + 1.0); // 1/1024
                nr = (float) (sqRoot2 * root);
                rootTable[i + 1024] = Float.floatToIntBits((float) root) & 0x7fffff;
                rootTable[i] = (Float.floatToIntBits(nr) & 0x7fffff);
            }
        }
        return rootTable;
    }

    public static float getRootForFloat(Float f) {
        int floatInts = Float.floatToIntBits(f);
        return Float.intBitsToFloat((getRootTable()[(floatInts >> 13) & 0x7ff]) + (((0x7f800000 + (floatInts & 0x3f800000)) >> 1) & 0x7f800000));
    }

    public static int[] getInverseLightTable() {
        float inv;

        if (invLightTable == null) {
            invLightTable = new int[1024];
            invLightTable[0] = 0x800000;
            for (int i = 1; i < 1024; i++) {
                inv = (float) (1.0 / ((double) i * 0.0009765625 + 1.0));
                invLightTable[i] = Float.floatToIntBits(inv) & 0x7FFFFF;
            }
        }
        return invLightTable;
    }

    public static float getInverseLightForFloat(Float f) {
        int floatInts = Float.floatToIntBits(f);
        return Float.intBitsToFloat(Float.floatToIntBits(getInverseLightTable()[(floatInts >> 13) & 0x3ff]) - (floatInts & 0xff800000) + 0x7e800000);
    }

    public static int[] getLightNormalTable() {

        if (lightNormalTable == null) {
            lightNormalTable = new int[256];
            for (int i = 0; i < 256; i++) {
                lightNormalTable[i] = i;
            }
        }
        return lightNormalTable;
    }

    public static int[] getLightExponentTable() {
        int tmp;
        if (lightExponentTable == null) {
            lightExponentTable = new int[256];
            for (int i = 0; i < 256; i++) {
                tmp = (i * i >> 8) * (i * i >> 8) >> 8;
                lightExponentTable[i] = tmp * tmp >> 8;
            }
        }
        return lightExponentTable;
    }

    public static boolean checkSubMatsAlphaBlendingTypeHas3Or4(GAOInnerParserMaterialMultiTexture bfgicpMT) {
        Iterator mtsi = bfgicpMT.getMtSubArr().iterator();
        MaterialMultiTextureSubElem mmtse;
        while (mtsi.hasNext()) {
            mmtse = (MaterialMultiTextureSubElem) mtsi.next();
            if ((mmtse.getSubTextureFlags() & 0x800) == 0) {
                int x = (mmtse.getSubTextureFlags() >> 16) & 0xF;
                if (x == 3) {
                    return true;
                }
                if (x == 4) {
                    return true;
                }
            }
        }
        return false;
    }

    public static BFGaoInnerChunkParser getGeometryICTFromLODGAO(BFGaoChunk gao) {
        BFKeyListElem geoKLE = gao.getGeoChunkKLE();
        if (geoKLE == null || geoKLE.getParseHandler() == null) {
            return null;
        }
        BFGaoInnerChunkParser geoIC = (BFGaoInnerChunkParser) geoKLE.getParseHandler();
        BFGaoInnerChunkParser geoICToRender;

        geoICToRender = geoIC;
        if (geoIC.getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_STATICLOD)) {
            BFKeyListElem lcs[] = ((GAOInnerParserStaticLOD) geoIC.getGaoInner()).getLODChunkKLE();
            if (lcs != null && lcs[0] != null && lcs[0].getParseHandler() != null) {
                Object tmp = lcs[0].getParseHandler();
                if (tmp instanceof BFGaoInnerChunkParser) {
                    //System.out.println("Selecting lod array 0th elem.");
                    geoICToRender = (BFGaoInnerChunkParser) tmp;
                }
            }
        }
        return geoICToRender;
    }

    public static BFGaoInnerChunkParser getMaterialICFromGAO(BFGaoChunk gao) {
        BFKeyListElem materialGroupKLE = gao.getMaterialGroupChunkKLE();
        if (materialGroupKLE == null || materialGroupKLE.getParseHandler() == null || !(materialGroupKLE.getParseHandler() instanceof BFGaoInnerChunkParser)) {
            return null;
        }
        return (BFGaoInnerChunkParser) materialGroupKLE.getParseHandler();
    }

    public static void _dumpmtx(float[] f) {
        System.out.format("%08f %08f %08f %08f\n", f[0], f[1], f[2], f[3]);
        System.out.format("%08f %08f %08f %08f\n", f[4], f[5], f[6], f[7]);
        System.out.format("%08f %08f %08f %08f\n", f[8], f[9], f[10], f[11]);
        System.out.format("%08f %08f %08f %08f\n", f[12], f[13], f[14], f[15]);
    }

    public static void _dumpmtx3(float[] f) {
        System.out.format("%08f %08f %08f\n", f[0], f[1], f[2]);
        System.out.format("%08f %08f %08f\n", f[3], f[4], f[5]);
        System.out.format("%08f %08f %08f\n", f[6], f[7], f[8]);
    }

    public void setProcessFlagsForAGAO(BFGaoChunk gao, Boolean isInverted, byte flag) {
        byte flags = gao.getProcessFlagsRuntime();
        if (!isInverted) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
        gao.setProcessFlagsRuntime(flags);
    }

    public boolean hasGaoFlagsFromProcessFlagsRuntime(BFGaoChunk gao, Boolean isInverted, byte flag) {
        byte flags = gao.getProcessFlagsRuntime();
        if (!isInverted) {
            return (byte) (flags & flag) != 0;
        } else {
            return (byte) (flags & flag) == 0;
        }
    }

    public static ArrayList<TreeMap<Integer, BFKeyListElem>> getGRPLForGao(BFGaoChunk g) {
        BFKeyListElem groupKLE = g.getBonesGroupChunkInstanceKLE();
        if (groupKLE == null || groupKLE.getParseHandler() == null
                || !(groupKLE.getParseHandler() instanceof BFGRPContainerChunk)) {
            return null;
        }

        BFGRPContainerChunk grpc = (BFGRPContainerChunk) groupKLE.getParseHandler();
        if (grpc == null) {
            return null;
        }
        BFGRPChunk grp = grpc.getGRP();
        if (grp == null) {
            return null;
        }

        ArrayList<TreeMap<Integer, BFKeyListElem>> grpl = grp.getGrpGaoList();
        if (grpl == null) {
            return null;
        }
        return grpl;
    }

    public static BFGaoChunk getSelectedBoneGao(BFGaoChunk gao, int selectedElemIdx) {
        if (gao == null || (gao.getFlags() & 0x2) == 0) {
            return null;
        }
        int i = -1;
        if (selectedElemIdx == -1) {
            return null;
        }
        Iterator it = getGRPLForGao(gao).iterator();
        while (it.hasNext()) {
            i++;
            TreeMap<Integer, BFGaoChunk> tm = (TreeMap<Integer, BFGaoChunk>) it.next();
            BFGaoChunk boneGao = tm.firstEntry().getValue();
            if (i == selectedElemIdx) {
                return boneGao;
            }
        }
        return null;
    }

    public static Integer getBoneGaoIndexFromMultipartGao(BFGaoChunk gao, BFGaoChunk boneGaoRequest) {
        if (gao == null || (gao.getFlags() & 0x2) == 0) {
            return null;
        }
        int i = -1;
        if (boneGaoRequest == null) {
            return null;
        }
        Iterator it = getGRPLForGao(gao).iterator();
        while (it.hasNext()) {
            i++;
            TreeMap<Integer, BFKeyListElem> tm = (TreeMap<Integer, BFKeyListElem>) it.next();
            BFGaoChunk boneGao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            if (boneGao.equals(boneGaoRequest)) {
                return i;
            }
        }
        return null;
    }

    public static Matrix4x4 calculateLocalMatrixFromGAOIfHasPivotReference(BFGaoChunk gao) {
        Matrix4x4 localMtx;
        BFGaoChunk refGao;
        Vector3 scale = new Vector3();
        Matrix4x4 globalRefInvert = new Matrix4x4();

        if ((gao.getFlags() & 0x400000) != 0) {
            localMtx = gao.getLocalMatrix();
            if (gao.getReferencePointGAOChunkKLE() == null || gao.getReferencePointGAOChunkKLE().getParseHandler() == null) {
                return null;
            }
            refGao = (BFGaoChunk) (gao.getReferencePointGAOChunkKLE().getParseHandler());

            if ((refGao.getFlags() & 0x400000) != 0) {
                Matrix4x4 tmp = new Matrix4x4();
                calculateGlobalMatrixFromGAO(refGao, tmp);
                refGao.getGlobalMatrix().set(tmp);
                Matrix4x4.invertMatrix(globalRefInvert, tmp);
            } else {
                Matrix4x4.invertMatrix(globalRefInvert, refGao.getGlobalMatrix());
            }

            Matrix4x4.matrixMultiplyWithNormalized(localMtx, gao.getGlobalMatrix(), globalRefInvert);

            if ((localMtx.getFlags() & 0x8) != 0) {
                Matrix4x4.getScale(scale, localMtx);
                if (Vector3.isVectorsDifferenceWithinLimit(Vector3.getIVector(), scale, 0.001f)
                        || Vector3.isVectorsDifferenceWithinLimit(Vector3.getNullVector(), scale, 0.001f)) {
                    localMtx.setM14(0.0f);
                    localMtx.setM24(0.0f);
                    localMtx.setM34(0.0f);
                    localMtx.setFlags(localMtx.getFlags() & ~0x8);
                }
            }
            Matrix4x4.orthoNormalize(localMtx);
            return localMtx;
        }
        return null;
    }

    public static Matrix4x4 calculateGlobalMatrixFromGAO(BFGaoChunk gao, Matrix4x4 dest) {
        // This is not exactly same as how game works. Game uses GAO process flag 0x80 to detect global matrix has been computed.
        // We compute here all the whole reference chain over and over again.
        //BFGaoChunk gaoBak = gao;
        if (gao == null) {
            return null;
        }
        Matrix4x4 mtx;
        Matrix4x4 ret = null;
        Matrix4x4 mtxTemp = new Matrix4x4();
        //float[] firstGaoGlobalMatrix = gao.getGlobalMatrix().getAsFloatArray(true, true);
        mtxStack.clear();

        while (true) {
            /* TODO: fix this!
            if (processedGaos.get(gaos.indexOf(gao))) {
                System.out.println("calculateGlobalMatrixFromGAO: BUG: loop detected (Gao: " + gaoBak.getGaoName() + ", refgao: " + gao.getGaoName() + ")!");
                return firstGaoGlobalMatrix;
            }
            processedGaos.set(gaos.indexOf(gao), true);
             */
            if (!gao.getReferencePointGAOKey().equals(-1) && gao.getReferencePointGAOChunkKLE() != null
                    && gao.getReferencePointGAOChunkKLE().getParseHandler() != null
                    && gao.getReferencePointGAOChunkKLE().getParseHandler() instanceof BFGaoChunk
                    && gao.getLocalMatrix() != null) {
                mtx = gao.getLocalMatrix();
                gao = (BFGaoChunk) gao.getReferencePointGAOChunkKLE().getParseHandler();
                mtxStack.push(mtx);
            } else {
                mtx = gao.getGlobalMatrix();
                mtxStack.push(mtx);
                break;
            }
        }

        while (!mtxStack.empty()) {
            if (ret == null) {
                ret = new Matrix4x4();
                ret.set(mtxStack.pop());
            } else {
                mtxTemp.set(ret);
                Matrix4x4.matrixMultiplyWithNormalized(ret, mtxStack.pop(), mtxTemp);
            }
        }
        if (dest != null && ret != null) {
            dest.set(ret);
        }
        return ret;
    }

    public static void computeObjectGlobalMatrixWithReferencesGlobalMatrix(BFGaoChunk a1) {
        if ((a1.getFlags() & 0x400000) != 0) {
            if (a1.getReferencePointGAOChunkKLE() != null && a1.getReferencePointGAOChunkKLE().getParseHandler() != null) {
                Matrix4x4.matrixMultiplyWithNormalized(a1.getGlobalMatrix(), a1.getLocalMatrix(),
                        ((BFGaoChunk) a1.getReferencePointGAOChunkKLE().getParseHandler()).getGlobalMatrix());
            }
        }
    }

    public static Matrix4x4 getLookAtCameraMatrixForGlobalMatrix(Matrix4x4 globalMtx, Matrix4x4 view, Matrix4x4 rot) {
        Vector3 tmpVec = new Vector3();
        Vector3 tmpVec2 = new Vector3();
        Matrix4x4 dest = new Matrix4x4();
        Matrix4x4 cameraWorldSpace = new Matrix4x4();
        float tmp1, tmp2, tmp3;

        // compute real view matrix, TODO: REALLY NASTY IMPLEMENTATION, should be simplified
        Matrix4x4.matrixMultiplyWithNormalized(cameraWorldSpace, rot, view);
        cameraWorldSpace = Camera.camInvert(null, cameraWorldSpace);

        tmpVec2.setX(globalMtx.getPos().getX() - cameraWorldSpace.getPos().getX());
        tmpVec2.setY(globalMtx.getPos().getY() - cameraWorldSpace.getPos().getY());
        tmpVec2.setZ(globalMtx.getPos().getZ() - cameraWorldSpace.getPos().getZ());
        // disquisting ubisoft camera related hack
        tmpVec.setX(cameraWorldSpace.getM21() * -1.0f);
        tmpVec.setY(cameraWorldSpace.getM22() * -1.0f);
        tmpVec.setZ(cameraWorldSpace.getM23() * -1.0f);
        // --
        Matrix4x4.makeRotationMatrixFromSight(dest, tmpVec2, tmpVec, true);
        // disquisting ubisoft camera related y<->z flip
        tmp1 = dest.getM21();
        tmp2 = dest.getM22();
        tmp3 = dest.getM23();
        dest.setM21(dest.getM31());
        dest.setM22(dest.getM32());
        dest.setM23(dest.getM33());
        dest.setM31(tmp1 * -1.0f);
        dest.setM32(tmp2 * -1.0f);
        dest.setM33(tmp3 * -1.0f);
        // --
        dest.getPos().setX(globalMtx.getPos().getX());
        dest.getPos().setY(globalMtx.getPos().getY());
        dest.getPos().setZ(globalMtx.getPos().getZ());
        dest.setFlags(dest.getFlags() | 0x2);
        if ((globalMtx.getFlags() & 0x8) != 0) {
            Matrix4x4.getScale(tmpVec, globalMtx);
            Matrix4x4.setScale(dest, tmpVec);
        }

        return dest;
    }

    public static int getGAOUsedMaterialsFlagsUnion(GeometryTriangleChunk geotcArr[], BFGaoInnerChunkParser materialGroupIC) {
        int ret = 0x11;
        if (geotcArr == null || materialGroupIC == null) {
            return 0x11;
        }
        int i;
        GeometryTriangleChunk geotc;
        MaterialInterface mi;

        switch (materialGroupIC.getGaoInnerType()) {
            case BFGaoInnerChunkParser.BF_GAO_TYPE_MATERIAL_GROUP:
                byte flagSumTmp = 0;
                byte leastCommonFlag = (byte) 0xff;
                GAOInnerParserMaterialGroup mmg = (GAOInnerParserMaterialGroup) materialGroupIC.getGaoInner();
                BFKeyListElem mmgKLE[] = mmg.getMaterialGroupChunkArrayKLE();
                if (mmgKLE != null) {
                    for (i = 0; i < geotcArr.length; i++) {
                        geotc = geotcArr[i];
                        if (geotc.getMaterialNum() <= (mmgKLE.length - 1)) {
                            mi = (MaterialInterface) (((BFGaoInnerChunkParser) (mmgKLE[geotc.getMaterialNum()]).getParseHandler()).getGaoInner());
                            if (mi != null) {
                                flagSumTmp |= mi.getFlags();
                                leastCommonFlag &= mi.getFlags();
                            }
                        } else {
                            if (mmgKLE[mmgKLE.length - 1] != null && mmgKLE[mmgKLE.length - 1].getParseHandler() != null
                                    && ((BFGaoInnerChunkParser) (mmgKLE[mmgKLE.length - 1].getParseHandler())).getGaoInnerType() != null) {
                                mi = (MaterialInterface) (((BFGaoInnerChunkParser) (mmgKLE[mmgKLE.length - 1]).getParseHandler()).getGaoInner());
                                if (mi != null) {
                                    flagSumTmp |= mi.getFlags();
                                    leastCommonFlag &= mi.getFlags();
                                }
                            }
                        }
                    }
                }
                ret = (byte) (flagSumTmp ^ (byte) ((byte) (flagSumTmp ^ leastCommonFlag) & 0x40));
                break;
            case BFGaoInnerChunkParser.BF_GAO_TYPE_MATERIAL_MULTITEXTURE:
            case BFGaoInnerChunkParser.BF_GAO_TYPE_MATERIAL_SINGLETEXTURE:
                mi = (MaterialInterface) materialGroupIC.getGaoInner();
                if (mi != null) {
                    ret = mi.getFlags();
                }
                break;
        }
        return ret;
    }

    public static int interpolateTwoColors(int src1, int src2, float factor) {
        int result;
        int tmp;
        int tmp2;
        int factorInt;
        byte factorByte;

        if (factor < 0.98000002f) {
            if (factor > 0.02f) {
                factor = factor + 49152.0f;
                factorInt = Float.floatToRawIntBits(factor);
                factorByte = (byte) (factorInt & 0xff);
                tmp = src2;
                if ((factorByte & 0x80) == 0) {
                    tmp = src1;
                }
                tmp2 = (tmp >> 1) & 0x7F7F7F7F;
                tmp = src2;
                if ((factorByte & 0x40) == 0) {
                    tmp = src1;
                }
                tmp2 = ((tmp >> 2) & 0x3F3F3F3F) + tmp2;
                tmp = src2;
                if ((factorByte & 0x20) == 0) {
                    tmp = src1;
                }
                tmp2 = ((tmp >> 3) & 0x1F1F1F1F) + tmp2;
                tmp = src2;
                if ((factorByte & 0x10) == 0) {
                    tmp = src1;
                }
                tmp2 = ((tmp >> 4) & 0xF0F0F0F) + tmp2;
                tmp = src2;
                if ((factorByte & 0x8) == 0) {
                    tmp = src1;
                }
                tmp2 = ((tmp >> 5) & 0x7070707) + tmp2;
                tmp = src2;
                if ((factorByte & 0x4) == 0) {
                    tmp = src1;
                }
                tmp2 = ((tmp >> 6) & 0x3030303) + tmp2;
                if ((factorByte & 0x2) == 0) {
                    result = ((src2 >> 7) & 0x1010101) + tmp2;
                } else {
                    result = ((src1 >> 7) & 0x1010101) + tmp2;
                }
            } else {
                result = src1;
            }
        } else {
            result = src2;
        }
        return result;
    }

    public static int multiplyColor(int color, float mult) {
        int result;
        int multInt;

        if (mult < 0.98000002) {
            if (mult > 0.02) {
                result = 0;
                multInt = Float.floatToIntBits(mult + 49152.0f);
                if (((multInt & 0xff) & 0x80) != 0) {
                    result = (color >> 1) & 0x7F7F7F7F;
                }
                if (((multInt & 0xff) & 0x40) != 0) {
                    result += (color >> 2) & 0x3F3F3F3F;
                }
                if (((multInt & 0xff) & 0x20) != 0) {
                    result += (color >> 3) & 0x1F1F1F1F;
                }
                if (((multInt & 0xff) & 0x10) != 0) {
                    result += (color >> 4) & 0xF0F0F0F;
                }
                if (((multInt & 0xff) & 0x8) != 0) {
                    result += (color >> 5) & 0x7070707;
                }
            } else {
                return 0;
            }
        } else {
            return color;
        }
        return result;
    }

    public static int multiplyColorForLighting(int color, float mult, int lightTypeTable[]) {
        int result;
        int multInt, multIntTmp;

        if (mult < 0.98000002f) {
            if (mult > 0.02f) {
                result = 0;
                multIntTmp = Float.floatToIntBits(mult + 49152.0f);
                multInt = lightTypeTable[multIntTmp & 0xff];
                if (((multInt & 0xff) & 0x80) != 0) {
                    result = (color >> 1) & 0x7F7F7F7F;
                }
                if (((multInt & 0xff) & 0x40) != 0) {
                    result += (color >> 2) & 0x3F3F3F3F;
                }
                if (((multInt & 0xff) & 0x20) != 0) {
                    result += (color >> 3) & 0x1F1F1F1F;
                }
                if (((multInt & 0xff) & 0x10) != 0) {
                    result += (color >> 4) & 0xF0F0F0F;
                }
                if (((multInt & 0xff) & 0x8) != 0) {
                    result += (color >> 5) & 0x7070707;
                }
            } else {
                return 0;
            }
        } else {
            return color;
        }
        return result;
    }

    public static int addColorForLighting(int color1, int color2) {
        int result;

        result = (((((((color2 >> 1) & 0x7F7F7F7F) + ((color1 >> 1) & 0x7F7F7F7F)) & 0x80808080) >> 1)
                | (((color2 >> 1) & 0x7F7F7F7F) + ((color1 >> 1) & 0x7F7F7F7F)) & 0x80808080) >> 2)
                | (((((color2 >> 1) & 0x7F7F7F7F) + ((color1 >> 1) & 0x7F7F7F7F)) & 0x80808080) >> 1)
                | (((color2 >> 1) & 0x7F7F7F7F) + ((color1 >> 1) & 0x7F7F7F7F)) & 0x80808080;
        return result | 2 * (((color2 >> 1) & 0x7F7F7F7F) + ((color1 >> 1) & 0x7F7F7F7F)) | (result >> 4);
    }

    public static int substractColorForLighting(int color1, int color2) {

        color1 = ~color1;
        color1 = addColorForLighting(color1, color2);
        color1 = ~color1;
        return color1;
    }

    public static void invertForLighting(Vector3 vecs[], boolean pos1orpos2andpos3, boolean all) {
        int i;
        if (pos1orpos2andpos3) {
            if (all) {
                for (i = 0; i < vecs.length; i++) {
                    vecs[i].setY(-vecs[i].getY());
                    vecs[i].setZ(-vecs[i].getZ());
                }

            } else {
                for (i = 0; i < vecs.length; i++) {
                    vecs[i].setX(-vecs[i].getX());
                }
            }
        } else if (all) {
            for (i = 0; i < vecs.length; i++) {
                vecs[i].setX(-vecs[i].getX());
                vecs[i].setY(-vecs[i].getY());
                vecs[i].setZ(-vecs[i].getZ());
            }
        }
    }

    public static StringBuilder getMatricesDescription(BFGaoChunk gao, StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        if (gao.getGlobalMatrix() != null) {
            sb.append("Global Matrix - ").append(gao.getGlobalMatrix()).append("\n");
        }
        if (gao.getLocalMatrix() != null) {
            sb.append("Local matrix - ").append(gao.getLocalMatrix()).append("\n");
        }
        if (gao.getReferencePointGAOChunkKLE() != null) {
            BFGaoChunk refPosGao = ((BFGaoChunk) (gao.getReferencePointGAOChunkKLE().getParseHandler()));
            if (refPosGao != null) {
                sb.append("Reference GAO: Key: ").append(String.format("%x", gao.getReferencePointGAOKey())).append(", ");
                sb.append(refPosGao.getGaoName());
            }
        }
        return sb;
    }

    // used in spriteGen modifier, and lighting calc
    public static int ambientLightAdd(BFWOWChunk wow, BFGaoChunk gao, int color) {
        int b1, b2, b3, b4;
        int color2;
        b1 = (color & 0xff);
        b2 = ((color >> 8) & 0xff);
        b3 = ((color >> 16) & 0xff);
        b4 = ((color >> 24) & 0xff);
        if (gao != null && (gao.getDesignFlags1() & 0x100) != 0) {
            if ((gao.getDesignFlags1() & 0x400000) == 0) {
                if (wow.getAmbientColor2() == -1) {
                    color2 = wow.getAmbientColor1() & 0xffffff;
                } else {
                    color2 = wow.getAmbientColor2() & 0xffffff;
                }
            } else {
                color2 = wow.getAmbientColor1() & 0xffffff;
            }
            b1 = Math.min(((color2) & 0xff) + b1, 255);
            b2 = Math.min(((color2 >> 8) & 0xff) + b2, 255);
            b3 = Math.min(((color2 >> 16) & 0xff) + b3, 255);
            b4 = Math.min(((color2 >> 24) & 0xff) + b4, 255);
            return (b1 & 0xff) | ((b2 & 0xff) << 8) | ((b3 & 0xff) << 16) | ((b4 & 0xff) << 24);
        } else {
            return color;
        }
    }

    public static float getDecalByFloat(float f) {
        double d = 162.975 * f + RenderUtils.getUnknownMagic();
        return RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(d) & 0x3ff)];
    }

    public static float getDecalByFloatOffsettedBy256(float f) {
        double d = 162.975 * f + RenderUtils.getUnknownMagic();
        return RenderUtils.getDecalTable()[(int) ((Double.doubleToLongBits(d) + 256) & 0x3ff)];
    }

    // Randoms
    public static int randShort() {
        return (int) (Math.random() * Short.MAX_VALUE);
    }

    public static int getElapsedTimeBasedRandom(long elapsedTimeMillis) {
        int random = (int) randShort();
        short timeS = (short) (elapsedTimeMillis & 0xffff);
        double d = timeS * (1 / 65535.0) * 32767.0 + RenderUtils.getUnknownMagic();
        long tr = Double.doubleToLongBits(d);
        return (((int) (tr & 0xffffffff)) ^ random);
    }

    public static BFGaoChunk getBaseGaoOfGao(BFGaoChunk gao) {
        if (gao == null) {
            return null;
        }

        while (true) {
            if (!gao.getReferencePointGAOKey().equals(-1) && gao.getReferencePointGAOChunkKLE() != null
                    && gao.getReferencePointGAOChunkKLE().getParseHandler() != null
                    && gao.getReferencePointGAOChunkKLE().getParseHandler() instanceof BFGaoChunk
                    && gao.getLocalMatrix() != null) {
                gao = (BFGaoChunk) gao.getReferencePointGAOChunkKLE().getParseHandler();
            } else {
                break;
            }
        }

        return gao;
    }

    public static Vector3[] convertTriangleStripToTringleList(Vector3 src[]) {
        int trianglesNum = src.length - 2;
        int newVerticesNum = trianglesNum * 3;
        Vector3 poss[] = new Vector3[newVerticesNum];

        int idx = 0;
        for (int i = 2; i < src.length; i++) {
            poss[idx++] = new Vector3(src[i - 2].getX(), src[i - 2].getY(), src[i - 2].getZ());
            poss[idx++] = new Vector3(src[i - 1].getX(), src[i - 1].getY(), src[i - 1].getZ());
            poss[idx++] = new Vector3(src[i].getX(), src[i].getY(), src[i].getZ());
        }
        return poss;
    }
}
