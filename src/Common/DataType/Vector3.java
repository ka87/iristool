/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;
import Common.ReaderUtil;
import Common.WriterUtil;
import iristool.Engine.RenderUtils;

/**
 *
 * @author andras
 */
public class Vector3 extends Logger {

    private Float x = 0.0f;
    private Float y = 0.0f;
    private Float z = 0.0f;

    private static final Vector3 I_VECTOR = new Vector3(1.0f, 1.0f, 1.0f);
    private static final Vector3 NULL_VECTOR = new Vector3(0.0f, 0.0f, 0.0f);
    private static final Vector3 DEFAULT_SCALE_VECTOR = new Vector3(1.0f, 1.0f, 1.0f);
    private static final Vector3 I_VECTOR_X = new Vector3(1.0f, 0.0f, 0.0f);
    private static final Vector3 I_VECTOR_Y = new Vector3(0.0f, 1.0f, 0.0f);
    private static final Vector3 I_VECTOR_Z = new Vector3(0.0f, 0.0f, 1.0f);

    public void set(Vector3 src) {
        if (src == null) {
            return;
        }
        this.x = src.getX();
        this.y = src.getY();
        this.z = src.getZ();
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }

    public Vector3(Float x, Float y, Float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3() {
    }

    public boolean readValues(ReaderUtil ru) {
        this.x = ru.readFloat(false);
        this.y = ru.readFloat(false);
        this.z = ru.readFloat(false);

        return (this.x != null && this.y != null && this.z != null);
    }

    public void writeValues(WriterUtil wu) {
        wu.writeFloat(x);
        wu.writeFloat(y);
        wu.writeFloat(z);
    }

    @Override
    public String toString() {
        return "Vector3: X:" + x + ", Y:" + y + ", Z:" + z;
    }

    // Static functions
    public static boolean isVectorsDifferenceWithinLimit(Vector3 a1, Vector3 a2, float limit) {
        if (Math.abs(a1.getX() - a2.getX()) > limit
                || Math.abs(a1.getY() - a2.getY()) > limit) {
            return false;
        } else {
            return Math.abs(a1.getZ() - a2.getZ()) <= limit;
        }
    }

    public static Vector3 getIVector() {
        return I_VECTOR;
    }

    public static Vector3 getIVectorX() {
        return I_VECTOR_X;
    }

    public static Vector3 getIVectorY() {
        return I_VECTOR_Y;
    }

    public static Vector3 getIVectorZ() {
        return I_VECTOR_Z;
    }

    public static Vector3 getNullVector() {
        return NULL_VECTOR;
    }

    public static Vector3 getDefaultScaleVector() {
        return DEFAULT_SCALE_VECTOR;
    }

    public static Vector3 transformVector(Vector3 dest, Matrix4x4 mtx, Vector3 src) {
        Vector3 res;
        float srcPos1;
        float srcPos2;

        res = src;
        if (dest == src) {
            srcPos1 = src.getX();
            srcPos2 = src.getY();
            if ((mtx.getFlags() & 8) != 0) {
                res.setX(mtx.getM31() * mtx.getM34() * res.getZ()
                        + mtx.getM21() * mtx.getM24() * res.getY()
                        + mtx.getM14() * mtx.getM11() * res.getX());
                res.setY(mtx.getM12() * mtx.getM14() * srcPos1
                        + mtx.getM32() * mtx.getM34() * res.getZ()
                        + mtx.getM22() * mtx.getM24() * res.getY());
                res.setZ(mtx.getM23() * mtx.getM24() * srcPos2
                        + mtx.getM13() * mtx.getM14() * srcPos1
                        + mtx.getM33() * mtx.getM34() * res.getZ());
            } else {
                res.setX(mtx.getM21() * res.getY()
                        + mtx.getM31() * res.getZ()
                        + mtx.getM11() * res.getX());
                res.setY(mtx.getM22() * res.getY()
                        + mtx.getM12() * srcPos1
                        + mtx.getM32() * res.getZ());
                res.setZ(mtx.getM23() * srcPos2
                        + mtx.getM13() * srcPos1
                        + mtx.getM33() * res.getZ());
            }
        } else if ((mtx.getFlags() & 8) != 0) {
            dest.setX(mtx.getM31() * mtx.getM34() * src.getZ()
                    + mtx.getM21() * mtx.getM24() * src.getY()
                    + mtx.getM14() * mtx.getM11() * src.getX());
            dest.setY(mtx.getM12() * mtx.getM14() * src.getX()
                    + mtx.getM32() * mtx.getM34() * src.getZ()
                    + mtx.getM22() * mtx.getM24() * src.getY());
            dest.setZ(mtx.getM13() * mtx.getM14() * src.getX()
                    + mtx.getM33() * mtx.getM34() * src.getZ()
                    + mtx.getM23() * mtx.getM24() * src.getY());
        } else {
            dest.setX(mtx.getM21() * src.getY() + mtx.getM31() * src.getZ() + mtx.getM11() * src.getX());
            dest.setY(mtx.getM22() * src.getY() + mtx.getM12() * src.getX() + mtx.getM32() * src.getZ());
            dest.setZ(mtx.getM23() * src.getY() + mtx.getM13() * src.getX() + mtx.getM33() * src.getZ());
        }
        return res;
    }

    public static Vector3 transformVertex(Vector3 dest, Matrix4x4 src, Vector3 multiply) {
        Vector3 ret = transformVector(dest, src, multiply);
        dest.setX(src.getPos().getX() + dest.getX());
        dest.setY(src.getPos().getY() + dest.getY());
        dest.setZ(src.getPos().getZ() + dest.getZ());

        return ret;
    }

    public static Vector3 uncompressVectorYXZ(EventsCompressedVector ecv, Vector3 p) {
        Vector3 ret = p;
        if (p == null) {
            ret = new Vector3();
        }
        ret.setX((ecv.getUnkb1() / 1024.0f) + ret.getX());
        ret.setY((ecv.getUnks1() / 1024.0f) + ret.getY());
        ret.setZ((ecv.getUnkb2() / 1024.0f) + ret.getZ());
        return ret;
    }

    public static Vector3 uncompressVectorYZX(EventsCompressedVector ecv, Vector3 p) {
        Vector3 ret = p;
        if (p == null) {
            ret = new Vector3();
        }
        ret.setX((ecv.getUnkb1() / 1024.0f) + ret.getX());
        ret.setY((ecv.getUnkb2() / 1024.0f) + ret.getY());
        ret.setZ((ecv.getUnks1() / 1024.0f) + ret.getZ());
        return ret;
    }

    public static Vector3 uncompressVectorXYZ(EventsCompressedVector ecv, Vector3 p) {
        Vector3 ret = p;
        if (p == null) {
            ret = new Vector3();
        }
        ret.setX((ecv.getUnks1() / 1024.0f) + ret.getX());
        ret.setY((ecv.getUnkb1() / 1024.0f) + ret.getY());
        ret.setZ((ecv.getUnkb2() / 1024.0f) + ret.getZ());
        return ret;
    }

    public static Vector3 uncompressVectorXYZFromCompressedQuaternion(EventsCompressedQuaternion ecq, Vector3 p) {
        Vector3 ret = p;
        if (p == null) {
            ret = new Vector3();
        }
        ret.setX((ecq.getCQ1() / 1024.0f) + ret.getX());
        ret.setY((ecq.getCQ2() / 1024.0f) + ret.getY());
        ret.setZ((ecq.getCQ3() / 1024.0f) + ret.getZ());
        return ret;
    }

    public static Vector3 uncompressVectorXYZFromCompressedQuaternionNoAddToPrevValue(EventsCompressedQuaternion ecq, Vector3 p) {
        Vector3 ret = p;
        if (p == null) {
            ret = new Vector3();
        }
        ret.setX((ecq.getCQ1() / 1024.0f));
        ret.setY((ecq.getCQ2() / 1024.0f));
        ret.setZ((ecq.getCQ3() / 1024.0f));
        return ret;
    }

    public static void sumTwoVector(Vector3 dest, Vector3 src1, Vector3 src2) {
        dest.setX(src1.getX() + src2.getX());
        dest.setY(src1.getY() + src2.getY());
        dest.setZ(src1.getZ() + src2.getZ());
    }

    public static void sumTwoVectorInPlace(Vector3 src1, Vector3 src2) {
        src1.setX(src1.getX() + src2.getX());
        src1.setY(src1.getY() + src2.getY());
        src1.setZ(src1.getZ() + src2.getZ());
    }

    public static void substractTwoVectors(Vector3 dest, Vector3 src1, Vector3 src2) {
        dest.setX(src1.getX() - src2.getX());
        dest.setY(src1.getY() - src2.getY());
        dest.setZ(src1.getZ() - src2.getZ());
    }

    public static void divTwoVector(Vector3 dest, Vector3 src1, Vector3 src2) {
        dest.setX(src1.getX() / src2.getX());
        dest.setY(src1.getY() / src2.getY());
        dest.setZ(src1.getZ() / src2.getZ());
    }

    public static Vector3 divEqualVectorInPlace(Vector3 vec) {
        float len;

        len = (float) Math.sqrt(vec.getX() * vec.getX() + vec.getY() * vec.getY() + vec.getZ() * vec.getZ());
        vec.setX(1.0f / len * vec.getX());
        vec.setY(1.0f / len * vec.getY());
        vec.setZ(1.0f / len * vec.getZ());

        return vec;
    }

    public static void blendVector(Vector3 dest, Vector3 vec1, Vector3 vec2, float factor) {
        if (factor < 0.0f) {
            factor = 0.0f;
        }
        if (factor > 1.0f) {
            factor = 1.0f;
        }
        dest.setX((1.0f - factor) * vec1.getX() + factor * vec2.getX());
        dest.setY((1.0f - factor) * vec1.getY() + factor * vec2.getY());
        dest.setZ((1.0f - factor) * vec1.getZ() + factor * vec2.getZ());
    }

    public static void multiplyVectorWithScalar(Vector3 dest, Vector3 vec, float scalar) {
        dest.setX(vec.getX() * scalar);
        dest.setY(vec.getY() * scalar);
        dest.setZ(vec.getZ() * scalar);
    }

    public static void addAnotherMultipliedVec(Vector3 dest1, Vector3 pos1, Vector3 pos2, float szorzo) {
        dest1.setX(szorzo * pos2.getX() + pos1.getX());
        dest1.setY(szorzo * pos2.getY() + pos1.getY());
        dest1.setZ(szorzo * pos2.getZ() + pos1.getZ());
    }

    public static void getVectorNormalized(Vector3 dest, Vector3 vec) {
        float len = (float) Math.sqrt(vec.getX() * vec.getX() + vec.getY() * vec.getY() + vec.getZ() * vec.getZ());
        dest.setX(1.0f / len * vec.getX());
        dest.setY(1.0f / len * vec.getY());
        dest.setZ(1.0f / len * vec.getZ());
    }

    public static void getVectorNormalizedInPlace(Vector3 vec) {
        float len = (float) Math.sqrt(vec.getX() * vec.getX() + vec.getY() * vec.getY() + vec.getZ() * vec.getZ());
        vec.setX(1.0f / len * vec.getX());
        vec.setY(1.0f / len * vec.getY());
        vec.setZ(1.0f / len * vec.getZ());
    }

    public static void getNormalVectorMultipliedWithScalar(Vector3 dest, Vector3 src, float factor) {
        if (dest == null) {
            dest = new Vector3();
        }

        getVectorNormalized(dest, src);
        multiplyVectorWithScalar(dest, dest, factor);
    }

    public static float getDistanceBetweenTwoVectors(Vector3 vec1, Vector3 vec2) {
        float dx = vec1.getX() - vec2.getX();
        float dy = vec1.getY() - vec2.getY();
        float dz = vec1.getZ() - vec2.getZ();
        return (float) Math.sqrt(dz * dz + dx * dx + dy * dy);
    }

    public static float getVectorLength(Vector3 v) {
        return (float) Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
    }

    public static Vector3 crossProduct(Vector3 dest, Vector3 vec1, Vector3 vec2) {

        if (dest == null) {
            dest = new Vector3();
        }

        dest.setX(vec1.getY() * vec2.getZ() - vec1.getZ() * vec2.getY());
        dest.setY(vec1.getZ() * vec2.getX() - vec1.getX() * vec2.getZ());
        dest.setZ(vec2.getY() * vec1.getX() - vec2.getX() * vec1.getY());

        return dest;
    }

    public static float invNormalVectors(Vector3 vec1, Vector3 vec2) {
        float vec1Len;
        float vec2Len;

        vec1Len = (float) Math.sqrt(vec1.getZ() * vec1.getZ() + vec1.getY() * vec1.getY() + vec1.getX() * vec1.getX());
        vec2Len = (float) Math.sqrt(vec2.getZ() * vec2.getZ() + vec2.getY() * vec2.getY() + vec2.getX() * vec2.getX());
        return (1.0f / vec1Len * vec1.getZ()) * (1.0f / vec2Len * vec2.getZ())
                + (1.0f / vec2Len * vec2.getY()) * (1.0f / vec1Len * vec1.getY())
                + (1.0f / vec2Len * vec2.getX()) * (1.0f / vec1Len * vec1.getX());
    }

    public static float vectorsSin(Vector3 vec1, Vector3 vec2, Vector3 vecToDPTest) {
        float vec1Len;
        float vec2Len;
        float retLen;
        float invVec1X;
        float invVec1Y;
        float invVec1Z;
        float invVec2X;
        float invVec2Y;
        float invVec2Z;
        float cpX, cpY, cpZ;

        vec1Len = (float) Math.sqrt(vec1.getZ() * vec1.getZ() + vec1.getY() * vec1.getY() + vec1.getX() * vec1.getX());
        invVec1X = 1.0f / vec1Len * vec1.getX();
        invVec1Y = 1.0f / vec1Len * vec1.getY();
        invVec1Z = 1.0f / vec1Len * vec1.getZ();
        vec2Len = (float) Math.sqrt(vec2.getZ() * vec2.getZ() + vec2.getY() * vec2.getY() + vec2.getX() * vec2.getX());
        invVec2X = 1.0f / vec2Len * vec2.getX();
        invVec2Y = 1.0f / vec2Len * vec2.getY();
        invVec2Z = 1.0f / vec2Len * vec2.getZ();
        cpX = invVec2Z * invVec1Y - invVec2Y * invVec1Z;
        cpY = invVec1Z * invVec2X - invVec2Z * invVec1X;
        cpZ = invVec2Y * invVec1X - invVec1Y * invVec2X;
        retLen = (float) Math.sqrt(cpZ * cpZ + cpY * cpY + cpX * cpX);
        if (vecToDPTest != null && vecToDPTest.getZ() * cpZ + vecToDPTest.getY() * cpY + cpX * vecToDPTest.getX() < 0.0f) {
            return -retLen;
        } else {
            return retLen;
        }
    }

    public static float vectorsAngle(Vector3 a1, Vector3 a2, Vector3 a3) {
        float ret;

        ret = invNormalVectors(a1, a2);
        if (ret < 1.0f) {
            if (ret > -1.0f) {
                ret = (float) Math.acos(ret);
            } else {
                ret = (float) Math.PI;
            }
            if (vectorsSin(a1, a2, a3) < 0.0f) {
                return -ret;
            }
            return ret;
        } else {
            return 0.0f;
        }
    }

    public static float blendFloat(float f1, float f2, float factor) {
        if (factor >= 0.0f) {
            if (factor > 1.0f) {
                factor = 1.0f;
            }
        } else {
            factor = 0.0f;
        }
        return (1.0f - factor) * f1 + f2 * factor;
    }

    public static void vectorBlendRotate(Vector3 vec1, Vector3 vec2, float factor, Vector3 dest) {
        float vec2Len;
        float vec1Len;
        float angle;
        Matrix4x4 mtx = new Matrix4x4();
        Vector3 tmpVec = new Vector3();

        boolean doNotCalc = false;

        if (factor < 0.0f) {
            factor = 0.0f;
        }
        if (factor > 1.0f) {
            factor = 1.0f;
        }

        if (Math.abs(vec1.getX()) <= 0.0000001f && Math.abs(vec1.getY()) <= 0.0000001f && Math.abs(vec1.getZ()) <= 0.0000001f) {
            doNotCalc = true;
            dest.set(vec2);
        }

        if (Math.abs(vec2.getX()) <= 0.0000001f && Math.abs(vec2.getY()) <= 0.0000001f && Math.abs(vec2.getZ()) <= 0.0000001f) {
            doNotCalc = true;
            dest.set(vec1);
        }

        if (!doNotCalc) {
            crossProduct(tmpVec, vec1, vec2);
            if (Math.abs(tmpVec.getX()) <= 0.00001f && Math.abs(tmpVec.getY()) <= 0.00001f && Math.abs(tmpVec.getZ()) <= 0.00001f) {
                if ((vec2.getZ() * vec1.getZ() + vec2.getY() * vec1.getY() + vec1.getX() * vec2.getX()) <= 0.0f) {
                    crossProduct(tmpVec, vec1, I_VECTOR_X);
                    if (Math.abs(tmpVec.getX()) <= 0.001f && Math.abs(tmpVec.getY()) <= 0.001f && Math.abs(tmpVec.getZ()) <= 0.001f) {
                        crossProduct(tmpVec, vec1, I_VECTOR_Y);
                    }
                } else {
                    dest.set(vec1);
                    doNotCalc = true;
                }
            }
            if (!doNotCalc) {
                divEqualVectorInPlace(tmpVec);
                angle = vectorsAngle(vec1, vec2, tmpVec) * factor;
                Matrix4x4.makeRotationMatrixFromAxisAngle(mtx, tmpVec, angle, true);
                transformVector(dest, mtx, vec1);
            }
        }

        vec1Len = (float) Math.sqrt(vec1.getX() * vec1.getX() + vec1.getY() * vec1.getY() + vec1.getZ() * vec1.getZ());
        vec2Len = (float) Math.sqrt(vec2.getX() * vec2.getX() + vec2.getY() * vec2.getY() + vec2.getZ() * vec2.getZ());

        if (Math.abs(dest.getX()) <= 0.0000001f && Math.abs(dest.getY()) <= 0.0000001f && Math.abs(dest.getZ()) <= 0.0000001f) {
            dest.set(0.0f, 0.0f, 0.0f);
        } else {
            getVectorNormalizedInPlace(dest);
            multiplyVectorWithScalar(dest, dest, blendFloat(vec1Len, vec2Len, factor));
        }
    }

    public static Vector3 roundVector(Vector3 vec) {
        double v2;
        double v3;
        double v4;

        v2 = vec.getX() * 10000.0 + RenderUtils.getUnknownMagic();
        v3 = vec.getY() * 10000.0 + RenderUtils.getUnknownMagic();
        v4 = vec.getZ() * 10000.0 + RenderUtils.getUnknownMagic();
        vec.setX((float) (v2 * 0.000099999997));
        vec.setY((float) (v3 * 0.000099999997));
        vec.setZ((float) (v4 * 0.000099999997));
        return vec;
    }

    public static Vector3 vectorMultiplyWithScalarAndSum(Vector3 dest, Vector3 vec1, Vector3 vec2, float scalar) {
        Vector3 tmp = new Vector3();
        multiplyVectorWithScalar(tmp, vec2, scalar);
        sumTwoVector(dest, vec1, tmp);
        return dest;
    }
    
    public Vector3 duplicate() {
        Vector3 ret = new Vector3();
        
        ret.x = x;
        ret.y = y;
        ret.z = z;
        
        return ret;
    }

    //Vector3 v = new Vector3();
    //Vector3.vectorBlendRotate(new Vector3(-0.071029663f, -0.019584656f, 4.4261932f), new Vector3(0.09104564f, 0.07667581f, 0.21949005f), 0.17393816f, v);
    //System.out.println(v);
    //Matrix4x4 m = new Matrix4x4();
    //Matrix4x4.makeRotationMatrixFromBanking(m, new Vector3(0.33079433f, 0.8956216f, 0.29738376f), new Vector3(0.36462751f, 0.30708614f, 0.87905908f), false);
    //System.out.println(m);
}
