/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Convert;
import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;
import Common.WriterUtil;
import com.jogamp.opengl.math.FloatUtil;
import java.nio.ByteBuffer;

/**
 *
 * @author andras
 */
public class Matrix4x4 extends Logger {

    private Float m11 = 0.0f;
    private Float m12 = 0.0f;
    private Float m13 = 0.0f;
    private Float m14 = 0.0f;
    private Float m21 = 0.0f;
    private Float m22 = 0.0f;
    private Float m23 = 0.0f;
    private Float m24 = 0.0f;
    private Float m31 = 0.0f;
    private Float m32 = 0.0f;
    private Float m33 = 0.0f;
    private Float m34 = 0.0f;
    private Vector3 pos = new Vector3(0.0f, 0.0f, 0.0f);
    private Float w = 0.0f;
    private Integer flags = 0;
    private float floatArrayRet[] = new float[16];
    private static final Matrix4x4 identityMatrix = new Matrix4x4(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            new Vector3(0.0f, 0.0f, 0.0f), 1.0f, 2);

    public static Matrix4x4 copy(Matrix4x4 src) {
        return new Matrix4x4(
                src.getM11(), src.getM12(), src.getM13(), src.getM14(),
                src.getM21(), src.getM22(), src.getM23(), src.getM24(),
                src.getM31(), src.getM32(), src.getM33(), src.getM34(),
                new Vector3(src.getPos().getX(), src.getPos().getY(), src.getPos().getZ()), src.getW(), src.getFlags());
    }

    public Float getM11() {
        return m11;
    }

    public void setM11(Float m11) {
        this.m11 = m11;
    }

    public Float getM12() {
        return m12;
    }

    public void setM12(Float m12) {
        this.m12 = m12;
    }

    public Float getM13() {
        return m13;
    }

    public void setM13(Float m13) {
        this.m13 = m13;
    }

    public Float getM14() {
        return m14;
    }

    public void setM14(Float m14) {
        this.m14 = m14;
    }

    public Float getM21() {
        return m21;
    }

    public void setM21(Float m21) {
        this.m21 = m21;
    }

    public Float getM22() {
        return m22;
    }

    public void setM22(Float m22) {
        this.m22 = m22;
    }

    public Float getM23() {
        return m23;
    }

    public void setM23(Float m23) {
        this.m23 = m23;
    }

    public Float getM24() {
        return m24;
    }

    public void setM24(Float m24) {
        this.m24 = m24;
    }

    public Float getM31() {
        return m31;
    }

    public void setM31(Float m31) {
        this.m31 = m31;
    }

    public Float getM32() {
        return m32;
    }

    public void setM32(Float m32) {
        this.m32 = m32;
    }

    public Float getM33() {
        return m33;
    }

    public void setM33(Float m33) {
        this.m33 = m33;
    }

    public Float getM34() {
        return m34;
    }

    public void setM34(Float m34) {
        this.m34 = m34;
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public Float getW() {
        return w;
    }

    public void setW(Float w) {
        this.w = w;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    /* Debug
    public void setFlags(Integer flags) {
        setFlags(flags, true);
    }
    
    public void setFlags(Integer flags, boolean debug) {
                if (m14 == 0.7f && (this.flags & 0x8) != 0 && (flags & 0x8) == 0 && debug) {
                    Thread.dumpStack();
        }
        this.flags = flags;
    }
     */
    public void set(Matrix4x4 src) {
        if (src == null) {
            return;
        }
        this.m11 = src.getM11();
        this.m12 = src.getM12();
        this.m13 = src.getM13();
        this.m14 = src.getM14();
        this.m21 = src.getM21();
        this.m22 = src.getM22();
        this.m23 = src.getM23();
        this.m24 = src.getM24();
        this.m31 = src.getM31();
        this.m32 = src.getM32();
        this.m33 = src.getM33();
        this.m34 = src.getM34();
        if (pos == null) {
            pos = new Vector3();
        }
        this.pos.setX(src.getPos().getX());
        this.pos.setY(src.getPos().getY());
        this.pos.setZ(src.getPos().getZ());
        this.w = src.getW();
        this.flags = src.getFlags();
    }

    public void set(float mtx[], boolean transpose) {
        if (mtx != null && mtx.length == 16) {
            float[] tmp;
            if (transpose) {
                tmp = new float[16];
                FloatUtil.transposeMatrix(mtx, tmp);
            } else {
                tmp = mtx;
            }
            m11 = tmp[0];
            m12 = tmp[1];
            m13 = tmp[2];
            m14 = tmp[3];
            m21 = tmp[4];
            m22 = tmp[5];
            m23 = tmp[6];
            m24 = tmp[7];
            m31 = tmp[8];
            m32 = tmp[9];
            m33 = tmp[10];
            m34 = tmp[11];
            if (pos != null) {
                pos.setX(tmp[12]);
                pos.setY(tmp[13]);
                pos.setZ(tmp[14]);
            } else {
                pos = new Vector3(tmp[12], tmp[13], tmp[14]);
            }
            w = tmp[15];
        }
    }

    public void set33(Matrix4x4 src) {

        this.setM11(src.getM11());
        this.setM12(src.getM12());
        this.setM13(src.getM13());
        this.setM21(src.getM21());
        this.setM22(src.getM22());
        this.setM23(src.getM23());
        this.setM31(src.getM31());
        this.setM32(src.getM32());
        this.setM33(src.getM33());

        if ((src.getFlags() & 0x4) != 0) {
            this.setFlags(this.getFlags() | 0x4);
        }
    }

    public Matrix4x4(Float m11, Float m12, Float m13, Float m14, Float m21,
            Float m22, Float m23, Float m24, Float m31, Float m32, Float m33,
            Float m34, Vector3 pos, Float w, Integer flags) {
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m14 = m14;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m24 = m24;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        this.m34 = m34;
        this.pos = pos;
        this.w = w;
        this.flags = flags;
    }

    public Matrix4x4(Float m11, Float m12, Float m13, Float m14, Float m21,
            Float m22, Float m23, Float m24, Float m31, Float m32, Float m33,
            Float m34, Float posX, Float posY, Float posZ, Float w, Integer flags) {
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m14 = m14;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m24 = m24;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        this.m34 = m34;
        this.pos = new Vector3(posX, posY, posZ);
        this.w = w;
        this.flags = flags;
    }

    public Matrix4x4(float mtx[]) {
        this(mtx, false);
    }

    public Matrix4x4(float mtx[], boolean transpose) {
        set(mtx, transpose);
    }

    public Matrix4x4() {
    }

    public float[] getAsFloatArray(Boolean ubisoftFix, Boolean removeNormalization) {
        int i = 0;
        floatArrayRet[i++] = m11;
        floatArrayRet[i++] = m12;
        floatArrayRet[i++] = m13;
        floatArrayRet[i++] = m14;
        floatArrayRet[i++] = m21;
        floatArrayRet[i++] = m22;
        floatArrayRet[i++] = m23;
        floatArrayRet[i++] = m24;
        floatArrayRet[i++] = m31;
        floatArrayRet[i++] = m32;
        floatArrayRet[i++] = m33;
        floatArrayRet[i++] = m34;
        floatArrayRet[i++] = pos.getX();
        floatArrayRet[i++] = pos.getY();
        floatArrayRet[i++] = pos.getZ();
        floatArrayRet[i++] = w;

        if (removeNormalization && (flags & 8) != 0) {
            floatArrayRet[0] *= floatArrayRet[3];
            floatArrayRet[1] *= floatArrayRet[3];
            floatArrayRet[2] *= floatArrayRet[3];

            floatArrayRet[4] *= floatArrayRet[7];
            floatArrayRet[5] *= floatArrayRet[7];
            floatArrayRet[6] *= floatArrayRet[7];

            floatArrayRet[8] *= floatArrayRet[11];
            floatArrayRet[9] *= floatArrayRet[11];
            floatArrayRet[10] *= floatArrayRet[11];

            floatArrayRet[3] = 0.0f;
            floatArrayRet[7] = 0.0f;
            floatArrayRet[11] = 0.0f;
        }
        if (ubisoftFix) {
            floatArrayRet[15] = 1.0f;
        }

        return floatArrayRet;
    }

    public boolean readValues(ReaderUtil ru) {
        this.m11 = ru.readFloat(false);
        this.m12 = ru.readFloat(false);
        this.m13 = ru.readFloat(false);
        this.m14 = ru.readFloat(false);
        this.m21 = ru.readFloat(false);
        this.m22 = ru.readFloat(false);
        this.m23 = ru.readFloat(false);
        this.m24 = ru.readFloat(false);
        this.m31 = ru.readFloat(false);
        this.m32 = ru.readFloat(false);
        this.m33 = ru.readFloat(false);
        this.m34 = ru.readFloat(false);
        Vector3 pos = new Vector3();
        if (!pos.readValues(ru)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Vector3 values read failed.");
            }
            return false;
        }
        this.w = ru.readFloat(false);
        this.flags = ru.readInt(false);
        this.pos = pos;

        return (this.m11 != null && this.m12 != null && this.m13 != null && this.m14 != null
                && this.m21 != null && this.m22 != null && this.m23 != null && this.m24 != null
                && this.m31 != null && this.m32 != null && this.m33 != null && this.m34 != null
                && this.w != null);
    }

    public void writeValues(WriterUtil wu) {
        wu.writeFloat(m11);
        wu.writeFloat(m12);
        wu.writeFloat(m13);
        wu.writeFloat(m14);
        wu.writeFloat(m21);
        wu.writeFloat(m22);
        wu.writeFloat(m23);
        wu.writeFloat(m24);
        wu.writeFloat(m31);
        wu.writeFloat(m32);
        wu.writeFloat(m33);
        wu.writeFloat(m34);
        pos.writeValues(wu);
        wu.writeFloat(w);
        wu.writeInt(flags);
    }

    public String getFlagMeaning(int flag) {
        if ((flag & 0x2) != 0) {
            return "has position";
        }
        if ((flag & 0x4) != 0) {
            return "has rotation";
        }
        if ((flag & 0x8) != 0) {
            return "has scale";
        }
        return "unknown";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            int i = 0, flagsTmp = flags;
            while (flagsTmp != 0 && i < 32) {
                if ((flagsTmp & (1 << i)) != 0) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(String.format("%s (0x%x)", getFlagMeaning(1 << i), (1 << i)));
                    flagsTmp -= (1 << i);
                }
                i++;
            }
        }
        return "Matrix 4x4:\n [" + m11 + ", " + m12 + ", " + m13 + ", " + m14 + "]\n"
                + " [" + m21 + ", " + m22 + ", " + m23 + ", " + m24 + "]\n"
                + " [" + m31 + ", " + m32 + ", " + m33 + ", " + m34 + "]\n"
                + " [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ", " + w + "]\n Matrix flags: " + String.format("%x", flags) + (sb.length() > 0 ? " (" + sb.toString() + ")" : "");
    }

    // Static functions
    public static Vector3 getScale(Vector3 scale, Matrix4x4 mtx) {
        if (scale == null) {
            scale = new Vector3();
        }

        if ((mtx.getFlags() & 8) != 0) {
            scale.setX(mtx.getM14());
            scale.setY(mtx.getM24());
            scale.setZ(mtx.getM34());
        } else {
            scale.setX(1.0f);
            scale.setY(1.0f);
            scale.setZ(1.0f);
        }

        return scale;
    }

    public static Matrix4x4 setScale(Matrix4x4 mtx, Vector3 scale) {
        if (Vector3.isVectorsDifferenceWithinLimit(scale, Vector3.getIVector(), 0.000099999997f)
                || Vector3.isVectorsDifferenceWithinLimit(scale, Vector3.getNullVector(), 0.000099999997f)) {
            mtx.setM14(0.0f);
            mtx.setM24(0.0f);
            mtx.setM34(0.0f);
            mtx.setFlags(mtx.getFlags() & ~0x8);
        } else {
            mtx.setM14(scale.getX());
            mtx.setM24(scale.getY());
            mtx.setM34(scale.getZ());
            mtx.setFlags(mtx.getFlags() | 0x8);
        }
        return mtx;
    }

    public static Matrix4x4 orthoNormalize(Matrix4x4 mtx) {
        float len1 = (float) Math.sqrt(mtx.getM11() * mtx.getM11() + mtx.getM12() * mtx.getM12() + mtx.getM13() * mtx.getM13());
        mtx.setM11(1.0f / len1 * mtx.getM11());
        mtx.setM12(1.0f / len1 * mtx.getM12());
        mtx.setM13(1.0f / len1 * mtx.getM13());
        mtx.setM21(mtx.getM13() * mtx.getM32() - mtx.getM12() * mtx.getM33());
        mtx.setM22(mtx.getM11() * mtx.getM33() - mtx.getM13() * mtx.getM31());
        mtx.setM23(mtx.getM31() * mtx.getM12() - mtx.getM32() * mtx.getM11());
        float len2 = (float) Math.sqrt(mtx.getM23() * mtx.getM23() + mtx.getM21() * mtx.getM21() + mtx.getM22() * mtx.getM22());
        mtx.setM21(1.0f / len2 * mtx.getM21());
        mtx.setM22(1.0f / len2 * mtx.getM22());
        mtx.setM23(1.0f / len2 * mtx.getM23());
        mtx.setM31(mtx.getM23() * mtx.getM12() - mtx.getM22() * mtx.getM13());
        mtx.setM32(mtx.getM13() * mtx.getM21() - mtx.getM23() * mtx.getM11());
        mtx.setM33(mtx.getM11() * mtx.getM22() - mtx.getM21() * mtx.getM12());
        float len3 = (float) Math.sqrt(mtx.getM33() * mtx.getM33() + mtx.getM31() * mtx.getM31() + mtx.getM32() * mtx.getM32());
        mtx.setM31(1.0f / len3 * mtx.getM31());
        mtx.setM32(1.0f / len3 * mtx.getM32());
        mtx.setM33(1.0f / len3 * mtx.getM33());
        return mtx;
    }

    public static void transposeM33(Matrix4x4 dest, Matrix4x4 src) {
        dest.setM11(src.getM11());
        dest.setM21(src.getM12());
        dest.setM31(src.getM13());
        dest.setM12(src.getM21());
        dest.setM22(src.getM22());
        dest.setM32(src.getM23());
        dest.setM33(src.getM33());
        dest.setM13(src.getM31());
        dest.setM23(src.getM32());
    }

    public static void transposeM44(Matrix4x4 dest, Matrix4x4 src) {
        dest.setM11(src.getM11());
        dest.setM21(src.getM12());
        dest.setM31(src.getM13());
        dest.getPos().setX(src.getM14());
        dest.setM12(src.getM21());
        dest.setM22(src.getM22());
        dest.setM32(src.getM23());
        dest.getPos().setY(src.getM24());
        dest.setM13(src.getM31());
        dest.setM23(src.getM32());
        dest.setM33(src.getM33());
        dest.getPos().setZ(src.getM34());
        dest.setM14(src.getPos().getX());
        dest.setM24(src.getPos().getY());
        dest.setM34(src.getPos().getZ());
        dest.setW(src.getW());
    }

    public static Vector3 invertMatrix(Matrix4x4 dest, Matrix4x4 src) {
        Vector3 destPos;

        Matrix4x4.transposeM33(dest, src);
        dest.setFlags(src.getFlags());
        if ((src.getFlags() & 8) != 0) {
            dest.setM14(1.0f / src.getM14());
            dest.setM24(1.0f / src.getM24());
            dest.setM34(1.0f / src.getM34());
        }
        destPos = dest.getPos();
        destPos.setX(-src.getPos().getX());
        destPos.setY(-src.getPos().getY());
        destPos.setZ(-src.getPos().getZ());
        return Vector3.transformVector(dest.getPos(), dest, dest.getPos());
    }

    public static Matrix4x4 removeUbiSpecificNormalization(Matrix4x4 destMtx, Matrix4x4 srcMtx) {
        Matrix4x4 ret = destMtx;
        if ((srcMtx.getFlags() & 0x8) != 0) {
            destMtx.setM11(srcMtx.getM11() * srcMtx.getM14());
            destMtx.setM12(srcMtx.getM12() * srcMtx.getM14());
            destMtx.setM13(srcMtx.getM13() * srcMtx.getM14());
            destMtx.setM21(srcMtx.getM21() * srcMtx.getM24());
            destMtx.setM22(srcMtx.getM22() * srcMtx.getM24());
            destMtx.setM23(srcMtx.getM23() * srcMtx.getM24());
            destMtx.setM31(srcMtx.getM31() * srcMtx.getM34());
            destMtx.setM32(srcMtx.getM32() * srcMtx.getM34());
            destMtx.setM33(srcMtx.getM33() * srcMtx.getM34());
            destMtx.getPos().setX(srcMtx.getPos().getX());
            destMtx.getPos().setY(srcMtx.getPos().getY());
            destMtx.getPos().setZ(srcMtx.getPos().getZ());
            destMtx.setM14(0.0f);
            destMtx.setM24(0.0f);
            destMtx.setM34(0.0f);
            destMtx.setW(1.0f);
            destMtx.setFlags(srcMtx.getFlags() & ~0x8);
        } else {
            destMtx.set(srcMtx);
            destMtx.setW(1.0f); // ubisoft fix
        }
        return ret;
    }

    public static Matrix4x4 multiplyM33WithScale(Matrix4x4 ret, Matrix4x4 input, Vector3 scale) {

        ret.setM11(input.getM11() * scale.getX());
        ret.setM12(input.getM12() * scale.getX());
        ret.setM13(input.getM13() * scale.getX());
        ret.setM21(input.getM21() * scale.getY());
        ret.setM22(input.getM22() * scale.getY());
        ret.setM23(input.getM23() * scale.getY());
        ret.setM31(input.getM31() * scale.getZ());
        ret.setM32(input.getM32() * scale.getZ());
        ret.setM33(input.getM33() * scale.getZ());

        return input;
    }

    public static Vector3 getScaleFromMatrix33(Vector3 dest, Matrix4x4 src) {
        dest.setX((float) Math.sqrt(src.getM11() * src.getM11() + src.getM12() * src.getM12() + src.getM13() * src.getM13()));
        dest.setY((float) Math.sqrt(src.getM21() * src.getM21() + src.getM22() * src.getM22() + src.getM23() * src.getM23()));
        dest.setZ((float) Math.sqrt(src.getM31() * src.getM31() + src.getM32() * src.getM32() + src.getM33() * src.getM33()));

        return dest; //Vector3.roundVector(dest);
    }

    public static Matrix4x4 multMatrix(Matrix4x4 res, Matrix4x4 mtx2, Matrix4x4 mtx1) {
        final float b00 = mtx2.getM11();
        final float b10 = mtx2.getM12();
        final float b20 = mtx2.getM13();
        final float b30 = mtx2.getM14();
        final float b01 = mtx2.getM21();
        final float b11 = mtx2.getM22();
        final float b21 = mtx2.getM23();
        final float b31 = mtx2.getM24();
        final float b02 = mtx2.getM31();
        final float b12 = mtx2.getM32();
        final float b22 = mtx2.getM33();
        final float b32 = mtx2.getM34();
        final float b03 = mtx2.getPos().getX();
        final float b13 = mtx2.getPos().getY();
        final float b23 = mtx2.getPos().getZ();
        final float b33 = mtx2.getW();

        float ai0 = mtx1.getM11(); // row-0 of a
        float ai1 = mtx1.getM21();
        float ai2 = mtx1.getM31();
        float ai3 = mtx1.getPos().getX();
        res.setM11(ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30);
        res.setM21(ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31);
        res.setM31(ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32);
        res.getPos().setX(ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33);

        ai0 = mtx1.getM12(); // row-1 of a
        ai1 = mtx1.getM22();
        ai2 = mtx1.getM32();
        ai3 = mtx1.getPos().getY();
        res.setM12(ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30);
        res.setM22(ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31);
        res.setM32(ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32);
        res.getPos().setY(ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33);

        ai0 = mtx1.getM13(); // row-2 of a
        ai1 = mtx1.getM23();
        ai2 = mtx1.getM33();
        ai3 = mtx1.getPos().getZ();
        res.setM13(ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30);
        res.setM23(ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31);
        res.setM33(ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32);
        res.getPos().setZ(ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33);

        ai0 = mtx1.getM14(); // row-3 of a
        ai1 = mtx1.getM24();
        ai2 = mtx1.getM34();
        ai3 = mtx1.getW();
        res.setM14(ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30);
        res.setM24(ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31);
        res.setM34(ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32);
        res.setW(ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33);

        return res;
    }

    public static float[] multMatrix(final float[] a, final float[] b, final float[] d) {
        final float b00 = b[0 + 0 * 4];
        final float b10 = b[1 + 0 * 4];
        final float b20 = b[2 + 0 * 4];
        final float b30 = b[3 + 0 * 4];
        final float b01 = b[0 + 1 * 4];
        final float b11 = b[1 + 1 * 4];
        final float b21 = b[2 + 1 * 4];
        final float b31 = b[3 + 1 * 4];
        final float b02 = b[0 + 2 * 4];
        final float b12 = b[1 + 2 * 4];
        final float b22 = b[2 + 2 * 4];
        final float b32 = b[3 + 2 * 4];
        final float b03 = b[0 + 3 * 4];
        final float b13 = b[1 + 3 * 4];
        final float b23 = b[2 + 3 * 4];
        final float b33 = b[3 + 3 * 4];

        float ai0 = a[0 * 4]; // row-0 of a
        float ai1 = a[1 * 4];
        float ai2 = a[2 * 4];
        float ai3 = a[3 * 4];
        d[0 * 4] = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30;
        d[1 * 4] = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31;
        d[2 * 4] = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32;
        d[3 * 4] = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33;

        ai0 = a[1 + 0 * 4]; // row-1 of a
        ai1 = a[1 + 1 * 4];
        ai2 = a[1 + 2 * 4];
        ai3 = a[1 + 3 * 4];
        d[1 + 0 * 4] = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30;
        d[1 + 1 * 4] = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31;
        d[1 + 2 * 4] = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32;
        d[1 + 3 * 4] = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33;

        ai0 = a[2 + 0 * 4]; // row-2 of a
        ai1 = a[2 + 1 * 4];
        ai2 = a[2 + 2 * 4];
        ai3 = a[2 + 3 * 4];
        d[2 + 0 * 4] = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30;
        d[2 + 1 * 4] = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31;
        d[2 + 2 * 4] = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32;
        d[2 + 3 * 4] = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33;

        ai0 = a[3 + 0 * 4]; // row-3 of a
        ai1 = a[3 + 1 * 4];
        ai2 = a[3 + 2 * 4];
        ai3 = a[3 + 3 * 4];
        d[3 + 0 * 4] = ai0 * b00 + ai1 * b10 + ai2 * b20 + ai3 * b30;
        d[3 + 1 * 4] = ai0 * b01 + ai1 * b11 + ai2 * b21 + ai3 * b31;
        d[3 + 2 * 4] = ai0 * b02 + ai1 * b12 + ai2 * b22 + ai3 * b32;
        d[3 + 3 * 4] = ai0 * b03 + ai1 * b13 + ai2 * b23 + ai3 * b33;

        return d;
    }

    /**
     * Multiply matrix with scale handling (DO NOT USE dest matrix as a source
     * of multiplication)
     *
     * @param dest
     * @param mtx1
     * @param mtx2
     */
    public static void matrixMultiplyWithNormalized(Matrix4x4 dest, Matrix4x4 mtx1, Matrix4x4 mtx2) {

        dest.setFlags(0);
        if ((mtx1.getFlags() & 0x8) != 0 || (mtx2.getFlags() & 0x8) != 0) {
            Matrix4x4 mtx1Tmp = new Matrix4x4();
            Matrix4x4 mtx2Tmp = new Matrix4x4();
            Matrix4x4.removeUbiSpecificNormalization(mtx1Tmp, mtx1);
            Matrix4x4.removeUbiSpecificNormalization(mtx2Tmp, mtx2);
            Matrix4x4.multMatrix(dest, mtx1Tmp, mtx2Tmp);
            Vector3 vectorLens = new Vector3();
            Matrix4x4.getScaleFromMatrix33(vectorLens, dest);
            Matrix4x4.setScale(dest, vectorLens);
            if ((dest.getFlags() & 0x8) != 0) {
                vectorLens.setX(1.0f / vectorLens.getX());
                vectorLens.setY(1.0f / vectorLens.getY());
                vectorLens.setZ(1.0f / vectorLens.getZ());
                Matrix4x4.multiplyM33WithScale(dest, dest, vectorLens);
            }
        } else {
            Matrix4x4.multMatrix(dest, mtx1, mtx2);
            dest.setM14(0.0f);
            dest.setM24(0.0f);
            dest.setM34(0.0f);
            dest.setW(1.0f);
        }
        if ((mtx1.getFlags() & 0x4) != 0 || (mtx2.getFlags() & 0x4) != 0) {
            dest.setFlags(dest.getFlags() | 0x4);
        }
        if (Math.abs(dest.getPos().getX()) != 0
                || Math.abs(dest.getPos().getY()) != 0
                || Math.abs(dest.getPos().getZ()) != 0) {
            dest.setFlags(dest.getFlags() | 0x2);
        }
    }

    public static void testMultiply() {
        // first bonematrix calc with multiply [bonematrix second case] (mammago garage save demo)
        Matrix4x4 mtx1 = new Matrix4x4(
                ByteBuffer.wrap(Convert.intToHexArray(0x3F7F9EF4)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3C951098)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BD51FD8C)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BC694DAD)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3F7F393A)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3D9CB284)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3D570E61)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BD9AF85E)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3F7EE975)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                new Vector3((float) -0.025065698,
                        (float) -0.050402109,
                        (float) 0.83112383),
                ByteBuffer.wrap(Convert.intToHexArray(0x3F800000)).getFloat(),
                0x6);
        Matrix4x4 mtx2 = new Matrix4x4(
                ByteBuffer.wrap(Convert.intToHexArray(0x0BF78951B)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BE748706)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BC188207)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BE649D0F)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3F6B9BE8)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BEA469A8)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3DAE96F3)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BE9E95BA)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BF726E28)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                new Vector3((float) 0.11168079,
                        (float) 0.0016624212,
                        (float) 0.011489241),
                ByteBuffer.wrap(Convert.intToHexArray(0x3F800000)).getFloat(),
                0x6);

        Matrix4x4 dest = new Matrix4x4(
                ByteBuffer.wrap(Convert.intToHexArray(0x0BF77780D)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BE829430)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3CB63504)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BE817AD5)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3F70132C)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BE739977)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x3D231233)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BE713DD0)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0x0BF7895EA)).getFloat(),
                ByteBuffer.wrap(Convert.intToHexArray(0)).getFloat(),
                new Vector3((float) 0.087029271,
                        (float) -0.047581922,
                        (float) 0.83696586),
                ByteBuffer.wrap(Convert.intToHexArray(0x3F800000)).getFloat(),
                0x6);

        Matrix4x4 dest2 = new Matrix4x4();
        //float fdest[] = new float[16];
        Matrix4x4.matrixMultiplyWithNormalized(dest2, mtx1, mtx2);
        //FloatUtil.multMatrix(mtx1.getAsFloatArray(false, false), mtx2.getAsFloatArray(false, false), fdest);               
        System.out.println(dest);
        System.out.println(dest2);
        System.exit(0);
        //for (int x=0; x < 16;x++) {
        //    System.out.println(fdest[x]);
        //}
    }

    public static Quaternion convertMatrixToQuaternion(Quaternion dest, Matrix4x4 src) {
        float v2;
        float v3;
        float v4;
        float v8;
        float v9;
        float v10;
        float v11;
        float v12;

        v2 = src.getM11() + src.getM33() + src.getM22();
        if (v2 <= 0.0) {
            if (src.getM11() < src.getM22()) {
                if (src.getM22() >= src.getM33()) {
                    v11 = (float) Math.sqrt(1.0f - src.getM11() + src.getM22() - src.getM33());
                    dest.setF2(0.5f * v11);
                    v9 = 0.5f / v11;
                    dest.setF3((src.getM32() + src.getM23()) * v9);
                    dest.setF1((src.getM21() + src.getM12()) * v9);
                    v10 = src.getM31() - src.getM13();
                } else {
                    v8 = (float) Math.sqrt(1.0f - src.getM11() - src.getM22() + src.getM33()); // same
                    dest.setF3(0.5f * v8);
                    v9 = 0.5f / v8;
                    dest.setF1((src.getM31() + src.getM13()) * v9);
                    dest.setF2((src.getM32() + src.getM23()) * v9);
                    v10 = src.getM12() - src.getM21();
                }
            } else if (src.getM11() >= src.getM33()) {
                v12 = (float) Math.sqrt(src.getM11() + 1.0f - src.getM22() - src.getM33());
                dest.setF1(0.5f * v12);
                v9 = 0.5f / v12;
                dest.setF2((src.getM21() + src.getM12()) * v9);
                dest.setF3((src.getM31() + src.getM13()) * v9);
                v10 = src.getM23() - src.getM32();
            } else {
                v8 = (float) Math.sqrt(1.0f - src.getM11() - src.getM22() + src.getM33()); // same
                dest.setF3(0.5f * v8);
                v9 = 0.5f / v8;
                dest.setF1((src.getM31() + src.getM13()) * v9);
                dest.setF2((src.getM32() + src.getM23()) * v9);
                v10 = src.getM12() - src.getM21();
            }

            dest.setF4(v10 * v9);
            return Quaternion.invNormalQuaternion(dest, dest);
        }
        v3 = (float) Math.sqrt(v2 + 1.0f);
        dest.setF4(0.5f * v3);
        v4 = 0.5f / v3;
        dest.setF1((src.getM23() - src.getM32()) * v4);
        dest.setF2((src.getM31() - src.getM13()) * v4);
        dest.setF3((src.getM12() - src.getM21()) * v4);

        return Quaternion.invNormalQuaternion(dest, dest);
    }

    public static int getMatrixFlagsByInspectionAndFixMatrix(Matrix4x4 mtx) {
        int flagBackup = mtx.getFlags();
        Vector3 scale = new Vector3();

        mtx.setFlags(0x8);

        getScale(scale, mtx);
        if (Vector3.isVectorsDifferenceWithinLimit(scale, Vector3.getDefaultScaleVector(), 0.000099999997f)) {
            mtx.setFlags(mtx.getFlags() & ~0x8);
        }
        if (Vector3.isVectorsDifferenceWithinLimit(scale, Vector3.getNullVector(), 0.000099999997f)) {
            mtx.setFlags(mtx.getFlags() & ~0x8);
        }
        if (Vector3.isVectorsDifferenceWithinLimit(new Vector3(mtx.getM11(), mtx.getM12(), mtx.getM13()),
                Vector3.getIVectorX(), 0.000099999997f)
                && Vector3.isVectorsDifferenceWithinLimit(new Vector3(mtx.getM21(), mtx.getM22(), mtx.getM23()),
                        Vector3.getIVectorY(), 0.000099999997f)
                && Vector3.isVectorsDifferenceWithinLimit(new Vector3(mtx.getM31(), mtx.getM32(), mtx.getM33()),
                        Vector3.getIVectorZ(), 0.000099999997f)) {
            mtx.setM11(1.0f);
            mtx.setM12(0.0f);
            mtx.setM13(0.0f);
            mtx.setM21(0.0f);
            mtx.setM22(1.0f);
            mtx.setM23(0.0f);
            mtx.setM31(0.0f);
            mtx.setM32(0.0f);
            mtx.setM33(1.0f);

            mtx.setFlags(mtx.getFlags() & ~0x4);
        } else {
            orthoNormalize(mtx);
            mtx.setFlags(mtx.getFlags() | 0x4);
        }
        if (!Vector3.isVectorsDifferenceWithinLimit(mtx.getPos(), Vector3.getNullVector(), 0.000000001f)) {
            mtx.setFlags(mtx.getFlags() | 0x2);
        }

        int ret = mtx.getFlags();
        mtx.setFlags(flagBackup);
        return ret;
    }

    public static void fixMatrix(Matrix4x4 mtx) {
        mtx.setFlags(getMatrixFlagsByInspectionAndFixMatrix(mtx));
        if ((mtx.getFlags() & 0x8) == 0) {
            mtx.setM14(0.0f);
            mtx.setM24(0.0f);
            mtx.setM34(0.0f);
        }
    }

    public static Matrix4x4 mult33Matrix(Matrix4x4 destMtx, Matrix4x4 src1, Matrix4x4 src2, boolean setTransformationFlag) {
        if ((src1.getFlags() & 0x3C) != 0) {
            if ((src2.getFlags() & 0x3C) != 0) {
                destMtx.setM11(src1.getM11() * src2.getM11() + src1.getM12() * src2.getM21() + src2.getM31() * src1.getM13());
                destMtx.setM12(src2.getM32() * src1.getM13() + src2.getM12() * src1.getM11() + src2.getM22() * src1.getM12());
                destMtx.setM13(src1.getM11() * src2.getM13() + src1.getM12() * src2.getM23() + src1.getM13() * src2.getM33());
                destMtx.setM21(src1.getM23() * src2.getM31() + src1.getM22() * src2.getM21() + src1.getM21() * src2.getM11());
                destMtx.setM22(src1.getM22() * src2.getM22() + src1.getM21() * src2.getM12() + src1.getM23() * src2.getM32());
                destMtx.setM23(src1.getM22() * src2.getM23() + src1.getM21() * src2.getM13() + src1.getM23() * src2.getM33());
                destMtx.setM31(src2.getM21() * src1.getM32() + src2.getM11() * src1.getM31() + src2.getM31() * src1.getM33());
                destMtx.setM32(src2.getM22() * src1.getM32() + src2.getM12() * src1.getM31() + src2.getM32() * src1.getM33());
                destMtx.setM33(src1.getM33() * src2.getM33() + src1.getM32() * src2.getM23() + src1.getM31() * src2.getM13());
                if (setTransformationFlag) {
                    if (((src1.getFlags() | src2.getFlags()) & 0x4) != 0) {
                        destMtx.setFlags(destMtx.getFlags() | 0x4);
                    }
                }
            } else {
                destMtx.set33(src1);
            }
        } else {
            destMtx.set33(src2);
        }
        return destMtx;
    }

    public static Matrix4x4 getIdentityMatrix() {
        return identityMatrix;
    }

    public static Matrix4x4 makeRotationMatrixFromSight(Matrix4x4 mtx, Vector3 pos, Vector3 sight, boolean removeNormAndPos) {

        float len;
        float sightZNorm;
        float sightXNorm;
        float sightYNorm;

        if (mtx == null) {
            mtx = new Matrix4x4();
        }

        len = (float) Math.sqrt(pos.getZ() * pos.getZ() + pos.getY() * pos.getY() + pos.getX() * pos.getX());
        mtx.setM21(1.0f / len * pos.getX());
        mtx.setM22(1.0f / len * pos.getY());
        mtx.setM23(1.0f / len * pos.getZ());

        mtx.setM21(-mtx.getM21());
        mtx.setM22(-mtx.getM22());
        mtx.setM23(-mtx.getM23());

        if (sight != null) {
            len = (float) Math.sqrt(sight.getZ() * sight.getZ() + sight.getY() * sight.getY() + sight.getX() * sight.getX());
            sightXNorm = 1.0f / len * sight.getX();
            sightYNorm = 1.0f / len * sight.getY();
            sightZNorm = 1.0f / len * sight.getZ();
            // cross product
            mtx.setM11(sightZNorm * pos.getY() - sightYNorm * pos.getZ());
            mtx.setM12(sightXNorm * pos.getZ() - sightZNorm * pos.getX());
            mtx.setM13(sightYNorm * pos.getX() - sightXNorm * pos.getY());
            if (Math.abs(mtx.getM11()) <= 0.000099999997
                    && Math.abs(mtx.getM12()) <= 0.000099999997
                    && Math.abs(mtx.getM13()) <= 0.000099999997) {
                sightXNorm = sightXNorm + 0.000099999997f;
                // cross product
                mtx.setM11(sightZNorm * pos.getY() - sightYNorm * pos.getZ());
                mtx.setM12(sightXNorm * pos.getZ() - sightZNorm * pos.getX());
                mtx.setM13(sightYNorm * pos.getX() - sightXNorm * pos.getY());
            }
            mtx.setM31(sightXNorm);
            mtx.setM32(sightYNorm);
            mtx.setM33(sightZNorm);
            mtx.setM11(-mtx.getM11());
            mtx.setM12(-mtx.getM12());
            mtx.setM13(-mtx.getM13());
        } else {
            mtx.setM11(mtx.getM22() * mtx.getM33() - mtx.getM23() * mtx.getM32());
            mtx.setM12(mtx.getM31() * mtx.getM23() - mtx.getM33() * mtx.getM21());
            mtx.setM13(mtx.getM21() * mtx.getM32() - mtx.getM22() * mtx.getM31());
        }
        len = (float) Math.sqrt(mtx.getM11() * mtx.getM11() + mtx.getM12() * mtx.getM12() + mtx.getM13() * mtx.getM13());
        mtx.setM11(1.0f / len * mtx.getM11());
        mtx.setM12(1.0f / len * mtx.getM12());
        mtx.setM13(1.0f / len * mtx.getM13());
        mtx.setM31(mtx.getM12() * mtx.getM23() - mtx.getM13() * mtx.getM22());
        mtx.setM32(mtx.getM21() * mtx.getM13() - mtx.getM23() * mtx.getM11());
        mtx.setM33(mtx.getM22() * mtx.getM11() - mtx.getM12() * mtx.getM21());
        if (removeNormAndPos) {
            mtx.setM14(0.0f);
            mtx.setM24(0.0f);
            mtx.setM34(0.0f);
            mtx.setFlags(mtx.getFlags() & ~0x8);
            mtx.getPos().set(0.0f, 0.0f, 0.0f);
            mtx.setFlags(mtx.getFlags() & ~0x2);
        }
        mtx.setFlags(mtx.getFlags() | 0x4);

        return mtx;
    }

    public static Matrix4x4 makeRotationMatrixFromAxisAngle(Matrix4x4 mtx, Vector3 vec, float angle, boolean removeNormAndPos) {
        float halfAngle;
        float halfAngleSin;
        float halfAngleCos;
        float vhasXmult2;
        float vhasXhac;
        float vhasYhac;
        float vhasXmult2AndX;
        float vhasYmult2AndX;
        float vhasZmult2AndX;
        float vhasYmult2AndY;
        float vhasZmult2AndZ;
        float vhasZhac;
        float vhasZmult2;
        float vhasYmult2;
        float vhasZmult2AndY;
        Vector3 vecHalfAngleSin = new Vector3();

        halfAngle = angle * 0.5f;
        halfAngleSin = (float) Math.sin(halfAngle);

        vecHalfAngleSin.setX(vec.getX() * halfAngleSin);
        vecHalfAngleSin.setY(vec.getY() * halfAngleSin);
        vecHalfAngleSin.setZ(vec.getZ() * halfAngleSin);

        halfAngleCos = (float) Math.cos(halfAngle);
        vhasXmult2 = vecHalfAngleSin.getX() + vecHalfAngleSin.getX();
        vhasYmult2 = vecHalfAngleSin.getY() + vecHalfAngleSin.getY();
        vhasZmult2 = vecHalfAngleSin.getZ() + vecHalfAngleSin.getZ();
        vhasXhac = halfAngleCos * vhasXmult2;
        vhasYhac = halfAngleCos * vhasYmult2;
        vhasZhac = halfAngleCos * vhasZmult2;
        vhasXmult2AndX = vhasXmult2 * vecHalfAngleSin.getX();
        vhasYmult2AndX = vhasYmult2 * vecHalfAngleSin.getX();
        vhasZmult2AndX = vhasZmult2 * vecHalfAngleSin.getX();
        vhasYmult2AndY = vhasYmult2 * vecHalfAngleSin.getY();
        vhasZmult2AndY = vhasZmult2 * vecHalfAngleSin.getY();
        vhasZmult2AndZ = vhasZmult2 * vecHalfAngleSin.getZ();
        mtx.setM11(1.0f - (vhasYmult2AndY + vhasZmult2AndZ));
        mtx.setM12(vhasYmult2AndX + vhasZhac);
        mtx.setM13(vhasZmult2AndX - vhasYhac);
        mtx.setM21(vhasYmult2AndX - vhasZhac);
        mtx.setM22(1.0f - (vhasZmult2AndZ + vhasXmult2AndX));
        mtx.setM23(vhasZmult2AndY + vhasXhac);
        mtx.setM31(vhasZmult2AndX + vhasYhac);
        mtx.setM32(vhasZmult2AndY - vhasXhac);
        mtx.setM33(1.0f - (vhasYmult2AndY + vhasXmult2AndX));
        if (removeNormAndPos) {
            mtx.setM14(0.0f);
            mtx.setM24(0.0f);
            mtx.setM34(0.0f);
            mtx.setFlags(mtx.getFlags() & ~0x8);
            mtx.getPos().set(0.0f, 0.0f, 0.0f);
            mtx.setFlags(mtx.getFlags() & ~0x2);
        }
        mtx.setFlags(mtx.getFlags() | 0x4);
        return mtx;
    }

    public static Matrix4x4 makeRotationMatrixFromBanking(Matrix4x4 mtx, Vector3 vec1, Vector3 vec2, boolean removeNormAndPos) {
        float vec2Len;
        float mtx2Len;
        float mtx1Len;

        vec2Len = (float) Math.sqrt(vec2.getZ() * vec2.getZ() + vec2.getY() * vec2.getY() + vec2.getX() * vec2.getX());
        mtx.setM31(1.0f / vec2Len * vec2.getX());
        mtx.setM32(1.0f / vec2Len * vec2.getY());
        mtx.setM33(1.0f / vec2Len * vec2.getZ());

        if (vec1 != null) {
            mtx.setM21(-vec1.getX());
            mtx.setM22(-vec1.getY());
            mtx.setM23(-vec1.getZ());

            mtx2Len = (float) Math.sqrt(mtx.getM21() * mtx.getM21() + mtx.getM22() * mtx.getM22() + mtx.getM23() * mtx.getM23());
            mtx.setM21(1.0f / mtx2Len * mtx.getM21());
            mtx.setM22(1.0f / mtx2Len * mtx.getM22());
            mtx.setM23(1.0f / mtx2Len * mtx.getM23());
        }
        mtx.setM11(mtx.getM22() * mtx.getM33() - mtx.getM23() * mtx.getM32());
        mtx.setM12(mtx.getM23() * mtx.getM31() - mtx.getM33() * mtx.getM21());
        mtx.setM13(mtx.getM32() * mtx.getM21() - mtx.getM22() * mtx.getM31());
        if (Math.abs(mtx.getM11()) <= 0.0000001f && Math.abs(mtx.getM12()) <= 0.0000001f && Math.abs(mtx.getM13()) <= 0.0000001f) {
            mtx.setM21(Vector3.getIVectorX().getX() + mtx.getM21());
            mtx.setM22(Vector3.getIVectorX().getY() + mtx.getM22());
            mtx.setM23(Vector3.getIVectorX().getZ() + mtx.getM23());
            mtx.setM11(mtx.getM22() * mtx.getM33() - mtx.getM23() * mtx.getM32());
            mtx.setM12(mtx.getM23() * mtx.getM31() - mtx.getM33() * mtx.getM21());
            mtx.setM13(mtx.getM32() * mtx.getM21() - mtx.getM22() * mtx.getM31());
        }
        mtx1Len = (float) Math.sqrt(mtx.getM11() * mtx.getM11() + mtx.getM12() * mtx.getM12() + mtx.getM13() * mtx.getM13());
        mtx.setM11(1.0f / mtx1Len * mtx.getM11());
        mtx.setM12(1.0f / mtx1Len * mtx.getM12());
        mtx.setM13(1.0f / mtx1Len * mtx.getM13());
        mtx.setM21(mtx.getM13() * mtx.getM32() - mtx.getM12() * mtx.getM33());
        mtx.setM22(mtx.getM33() * mtx.getM11() - mtx.getM13() * mtx.getM31());
        mtx.setM23(mtx.getM31() * mtx.getM12() - mtx.getM11() * mtx.getM32());
        if (removeNormAndPos) {
            mtx.setM14(0.0f);
            mtx.setM24(0.0f);
            mtx.setM34(0.0f);
            mtx.setFlags(mtx.getFlags() & ~0x8);
            mtx.getPos().set(0.0f, 0.0f, 0.0f);
            mtx.setFlags(mtx.getFlags() & ~0x2);
        }
        mtx.setFlags(mtx.getFlags() | 0x4);
        return mtx;
    }
}
