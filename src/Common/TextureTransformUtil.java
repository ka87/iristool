/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import Common.DataType.MaterialMultiTextureSubElem;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import com.jogamp.opengl.math.FloatUtil;
import iristool.Engine.Camera;

public class TextureTransformUtil extends Logger {

    private class TransformData {

        public float pos1;
        public float pos2;
        public float pos3;
        public float pos4;
        public float pos5;
        public float pos6;
    }

    private MaterialMultiTextureSubElem mmtse;
    private float timeDiff;
    private float mtSinTable[] = {
        0.0f, 0.3826834f, 0.70710671f, 0.9238795f,
        1.0f, 0.9238795f, 0.70710671f, 0.3826834f,
        0.0f, -0.3826834f, -0.70710671f, -0.9238795f,
        -1.0f, -0.9238795f, -0.70710671f, -0.3826834f
    };

    public TextureTransformUtil(MaterialMultiTextureSubElem mmtse, float timeDiff) {
        this.mmtse = mmtse;
        this.timeDiff = timeDiff;
    }

    private float getScale(int transform) {
        if (transform != 0 && transform != 0x8001000) {
            return Float.intBitsToFloat(transform << 16);
        } else {
            return 0.0f;
        }
    }

    private int selectSinElemFromTable(int transform1, int transform2) {
        return transform2 & 1 | 4 * (transform1 & 1) | ((transform1 & 0x10000 | (transform2 >> 2) & 0x4000) >> 13);
    }

    private void decompressSpeed(MaterialMultiTextureSubElem mmtse, float timeDiff, TransformData td) {
        if ((mmtse.getSubTextureFlags() & 0xc0000000) != 0) {
            if ((mmtse.getSubTextureFlags() & 0x40000000) != 0) {
                td.pos5 *= timeDiff;
                td.pos5 += 12288.0f; // 0x46400000
                td.pos5 = Float.intBitsToFloat(Float.floatToIntBits(12288.0f) | Float.floatToIntBits(td.pos5) & 0x3FF);
                td.pos5 -= 12288.0f;
            }
            if ((mmtse.getSubTextureFlags() & 0x80000000) != 0) {
                td.pos6 *= timeDiff;
                td.pos6 += 12288.0f; // 0x46400000
                td.pos6 = Float.intBitsToFloat(Float.floatToIntBits(12288.0f) | Float.floatToIntBits(td.pos6) & 0x3FF);
                td.pos6 -= 12288.0f;
            }
        }
    }

    private void calculateRotation(MaterialMultiTextureSubElem mmtse, TransformData td) {
        int tableElem = selectSinElemFromTable(mmtse.getTransform1(), mmtse.getTransform2());
        float sin1 = mtSinTable[tableElem & 0xf];
        float sin2 = mtSinTable[(tableElem + 4) & 0xf];

        float pos1 = td.pos1;
        float pos2 = td.pos2;
        float pos3 = td.pos3;
        float pos4 = td.pos4;

        td.pos1 = td.pos1 * sin2 - pos3 * sin1; // this is DirectX, inverted in OpenGL
        td.pos2 = pos2 * sin2 - pos4 * sin1; // this is DirectX, inverted in OpenGL
        td.pos3 = pos3 * sin2 + pos1 * sin1;
        td.pos4 = pos4 * sin2 + pos2 * sin1;
    }

    private TransformData decompressUV(MaterialMultiTextureSubElem mmtse, float timeDiff) {
        TransformData td = new TransformData();

        td.pos1 = Float.intBitsToFloat(mmtse.getTransform1() & 0xfffe0000);
        td.pos2 = 0;
        td.pos3 = 0;
        td.pos4 = Float.intBitsToFloat(mmtse.getTransform2() & 0xfffe0000);
        td.pos5 = getScale(mmtse.getTransform1()) * td.pos1;
        td.pos6 = getScale(mmtse.getTransform2()) * td.pos4;
        if ((mmtse.getSubTextureFlags() & 0xc0000000) != 0) {
            decompressSpeed(mmtse, timeDiff, td);
        }
        calculateRotation(mmtse, td);

        return td;
    }

    Matrix4x4 makeMatrix(Matrix4x4 mtx, Vector3 firstcol, Vector3 secondcol, Vector3 thirdcol, Vector3 position, Vector3 scale, int mtxflag) {

        if (mtx == null) {
            mtx = new Matrix4x4();
        }

        mtx.setFlags(mtxflag);
        mtx.setM11(firstcol.getX());
        mtx.setM12(firstcol.getY());
        mtx.setM13(firstcol.getZ());
        mtx.setM21(secondcol.getX());
        mtx.setM22(secondcol.getY());
        mtx.setM23(secondcol.getZ());
        mtx.setM31(thirdcol.getX());
        mtx.setM32(thirdcol.getY());
        mtx.setM33(thirdcol.getZ());
        mtx.setM14(scale.getX());
        mtx.setM24(scale.getY());
        mtx.setM34(scale.getZ());
        mtx.setPos(new Vector3(position.getX(), position.getY(), position.getZ()));
        mtx.setW(1.0f);

        return mtx;
    }

    public static Matrix4x4 planarGetMatrixFromGao(BFGaoChunk ownerGao, int data) {
        int boneID = (((byte) (data >> 8)) & 0x7);

        Matrix4x4 global = new Matrix4x4();

        if ((ownerGao.getFlags() & 0x1000000) != 0) {
            if (ownerGao.getBoneShapes() != null && ownerGao.getBoneShapes().size() > boneID && ownerGao.getBoneShapes().get(boneID).getAnimationShapeMtx() != null) {
                global.set(ownerGao.getBoneShapes().get(boneID).getAnimationShapeMtx());
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("BUG: boneID: " + boneID + " not found. [0x1000000] Gao: " + ownerGao.getGaoName());
                }
                global.set(Matrix4x4.getIdentityMatrix());
            }
        } else if ((ownerGao.getFlags() & 0x200000) != 0) {
            if (ownerGao.getBoneShapeMatrices() != null && ownerGao.getBoneShapeMatrices().size() > boneID && ownerGao.getBoneShapeMatrices().get(boneID) != null) {
                global.set(ownerGao.getBoneShapeMatrices().get(boneID));
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("BUG: boneID: " + boneID + " not found. [0x200000] Gao: " + ownerGao.getGaoName());
                }
                global.set(Matrix4x4.getIdentityMatrix());
            }
        } else {
            global.set(Matrix4x4.getIdentityMatrix());
        }
        Matrix4x4 ret = new Matrix4x4();
        Matrix4x4.invertMatrix(ret, global);
        return ret;
    }

    private Matrix4x4 calculateUV(MaterialMultiTextureSubElem mmtse, float timeDiff) {
        Matrix4x4 uvMatrix;

        if ((!mmtse.getTransform1().equals(0x3F800000) || !mmtse.getTransform2().equals(0x3F800000))
                && !mmtse.getTransform1().equals(0) && !mmtse.getTransform2().equals(0)) {
            TransformData td = decompressUV(mmtse, timeDiff);

            uvMatrix = makeMatrix(null,
                    new Vector3(td.pos1, td.pos2, 0.0f),
                    new Vector3(td.pos3, td.pos4, 0.0f),
                    new Vector3(0.0f, 0.0f, 0.0f),
                    new Vector3(td.pos5, td.pos6, 0.0f),
                    new Vector3(0.0f, 0.0f, 0.0f), 6);
        } else {
            uvMatrix = new Matrix4x4();
            uvMatrix.set(Matrix4x4.getIdentityMatrix());
        }
        return uvMatrix;
    }

    public Matrix4x4 getMatrixAsObject() {
        return calculateUV(mmtse, timeDiff);
    }

    public float[] getMatrixAsFloatArray() {
        return calculateUV(mmtse, timeDiff).getAsFloatArray(true, true);
    }

    public static float[] selectPlanarMatrix(BFGaoChunk gao, MaterialMultiTextureSubElem mmtse, float viewMatrix[], float[] planarMatrix) {
        if (planarMatrix == null || planarMatrix.length != 16) {
            planarMatrix = new float[16];
        }
        if (mmtse.getUVFuncNum() == 6 && ((mmtse.getAlphaMaskType400() & 0xff) >> 6) == 3) {
            FloatUtil.transposeMatrix(TextureTransformUtil.planarGetMatrixFromGao(gao, mmtse.getAlphaMaskType400()).getAsFloatArray(true, true), planarMatrix);
        } else if (mmtse.getUVFuncNum() == 6 && ((mmtse.getAlphaMaskType400() & 0xff) >> 6) == 1) {
            FloatUtil.transposeMatrix(gao.getGlobalMatrix().getAsFloatArray(true, true), planarMatrix);
        } else if (mmtse.getUVFuncNum() == 6 && ((mmtse.getAlphaMaskType400() & 0xff) >> 6) == 2) {
            FloatUtil.transposeMatrix(viewMatrix, planarMatrix);
        } else {
            FloatUtil.transposeMatrix(Matrix4x4.getIdentityMatrix().getAsFloatArray(true, true), planarMatrix);
        }
        return planarMatrix;
    }

    public static float getDistanceForPlanar(int width) {
        return Camera.getHalfWidthDivWithTangentOfHalfFOV(width) / (width / 2.0f);
    }
}
