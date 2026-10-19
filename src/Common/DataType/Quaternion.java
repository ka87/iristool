/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.ReaderUtil;

/**
 *
 * @author andras
 */
public class Quaternion {

    private Float f1;
    private Float f2;
    private Float f3;
    private Float f4;

    public Float getF1() {
        return f1;
    }

    public void setF1(Float f1) {
        this.f1 = f1;
    }

    public Float getF2() {
        return f2;
    }

    public void setF2(Float f2) {
        this.f2 = f2;
    }

    public Float getF3() {
        return f3;
    }

    public void setF3(Float f3) {
        this.f3 = f3;
    }

    public Float getF4() {
        return f4;
    }

    public void setF4(Float f4) {
        this.f4 = f4;
    }

    public void set(Quaternion eq) {
        this.f1 = eq.getF1();
        this.f2 = eq.getF2();
        this.f3 = eq.getF3();
        this.f4 = eq.getF4();
    }
    
    public Quaternion(Float f1, Float f2, Float f3, Float f4) {
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
    }

    public Quaternion() {
    }

    public boolean readValues(ReaderUtil ru) {
        f1 = ru.readFloat(false);
        f2 = ru.readFloat(false);
        f3 = ru.readFloat(false);
        f4 = ru.readFloat(false);

        return (f1 != null && f2 != null && f3 != null && f4 != null);
    }

    @Override
    public String toString() {
        return String.format("Quaternion: f1: %f, f2: %f, f3: %f, f4: %f",
                f1, f2, f3, f4);
    }

    // Static functions 
    public static Quaternion uncompressQuaternion(EventsCompressedQuaternion ecq, Quaternion eq) {
        Quaternion ret = eq;
        if (eq == null) {
            ret = new Quaternion();
        }
        ret.setF1((float) (ecq.getCQ1() / 32767.0));
        ret.setF2((float) (ecq.getCQ2() / 32767.0));
        ret.setF3((float) (ecq.getCQ3() / 32767.0));
        float sum = (ret.getF1() * ret.getF1()) + (ret.getF2() * ret.getF2()) + (ret.getF3() * ret.getF3());
        if (sum >= 1.0f) {
            ret.setF4(0.0f);
        } else {
            ret.setF4((float) Math.sqrt(1.0 - sum));
        }
        return ret;
    }

    public static EventsCompressedQuaternion compressQuaternion(Quaternion eq, EventsCompressedQuaternion ecq) {
        EventsCompressedQuaternion ret = ecq;
        if (ecq == null) {
            ret = new EventsCompressedQuaternion();
        }
        Quaternion tmpEq = new Quaternion(eq.getF1(), eq.getF2(), eq.getF3(), eq.getF4());
        if (tmpEq.getF4() < 0.0f) {
            tmpEq.setF1(-tmpEq.getF1());
            tmpEq.setF2(-tmpEq.getF2());
            tmpEq.setF3(-tmpEq.getF3());
        }
        ret.setCQ1((short) (tmpEq.getF1() * 32767.0 + 0.5));
        ret.setCQ2((short) (tmpEq.getF2() * 32767.0 + 0.5));
        ret.setCQ3((short) (tmpEq.getF3() * 32767.0 + 0.5));
        return ret;
    }

    public static Matrix4x4 convertQuaternionToMatrix(Matrix4x4 mtx, Quaternion quat) {
        if (quat == null) {
            return null;
        }
        if (mtx == null) {
            mtx = new Matrix4x4();
        }

        float q1sq = quat.getF1() * quat.getF1();
        float q1sqminushalf = 0.5f - (q1sq + q1sq);
        float q2q1 = quat.getF2() * quat.getF1();
        float q1q2x2 = q2q1 + q2q1;
        float q3q1 = quat.getF3() * quat.getF1();
        float q3q1x2 = q3q1 + q3q1;
        float q4q1 = quat.getF4() * quat.getF1();
        float q4q1x2 = q4q1 + q4q1;
        float q2sq = quat.getF2() * quat.getF2();
        float q2sqminushalf = 0.5f - (q2sq + q2sq);
        float q3q2 = quat.getF3() * quat.getF2();
        float q3q2x2 = q3q2 + q3q2;
        float q4q2 = quat.getF4() * quat.getF2();
        float q4q2x2 = q4q2 + q4q2;
        float q3sq = quat.getF3() * quat.getF3();
        float q3sqminushalf = 0.5f - (q3sq + q3sq);
        float q4q3 = quat.getF4() * quat.getF3();
        float q4q3x2 = q4q3 + q4q3;
        mtx.setM14(0.0f);
        mtx.setM24(0.0f);
        mtx.setM34(0.0f);
        mtx.setFlags((mtx.getFlags() & ~0x8) | 4);
        mtx.setM11(q3sqminushalf + q2sqminushalf);
        mtx.setM21(q1q2x2 - q4q3x2);
        mtx.setM31(q4q2x2 + q3q1x2);
        mtx.setM12(q4q3x2 + q1q2x2);
        mtx.setM22(q3sqminushalf + q1sqminushalf);
        mtx.setM32(q3q2x2 - q4q1x2);
        mtx.setM13(q3q1x2 - q4q2x2);
        mtx.setM23(q3q2x2 + q4q1x2);
        mtx.setM33(q2sqminushalf + q1sqminushalf);

        return mtx;
    }

    public static void testCompressUncompress() {
        // values from new demo first non 0 read in uncompressQuaternion (3rd)
        Quaternion eq = Quaternion.uncompressQuaternion(new EventsCompressedQuaternion((short) 0x6ff, (short) 0xfbba, (short) 0xfdb7), null);
        System.out.println(eq);
        EventsCompressedQuaternion ecq = Quaternion.compressQuaternion(eq, null);
        System.out.println(ecq);
    }

    public static Quaternion invNormalQuaternion(Quaternion dest, Quaternion src) {
        if (dest == null) {
            dest = new Quaternion();
        }

        float len = (float) Math.sqrt(
                src.getF4() * src.getF4() + src.getF3() * src.getF3()
                + src.getF2() * src.getF2() + src.getF1() * src.getF1());

        dest.setF1(1.0f / len * src.getF1());
        dest.setF2(1.0f / len * src.getF2());
        dest.setF3(1.0f / len * src.getF3());
        dest.setF4(1.0f / len * src.getF4());

        return dest;
    }

    public static Quaternion Slerp(Quaternion dest, float interpFactor, Quaternion src1, Quaternion src2, float theta, float invSinTheta) {
        float sin1;
        float sin2;
        float q1sin1;
        float q2sin1;
        float q3sin1;
        float q4sin1;

        if (dest == null) {
            dest = new Quaternion();
        }

        if (theta == 0.0f) {
            dest.setF1(src1.getF1());
            dest.setF2(src1.getF2());
            dest.setF3(src1.getF3());
            dest.setF4(src1.getF4());
        } else {
            sin1 = (float) Math.sin((1.0f - interpFactor) * theta) * invSinTheta;
            sin2 = (float) Math.sin(interpFactor * theta) * invSinTheta;
            q1sin1 = src1.getF1() * sin1;
            q2sin1 = src1.getF2() * sin1;
            q3sin1 = src1.getF3() * sin1;
            q4sin1 = src1.getF4() * sin1;
            dest.setF1(src2.getF1() * sin2);
            dest.setF2(src2.getF2() * sin2);
            dest.setF3(src2.getF3() * sin2);
            dest.setF4(src2.getF4() * sin2);

            dest.setF1(dest.getF1() + q1sin1);
            dest.setF2(dest.getF2() + q2sin1);
            dest.setF3(dest.getF3() + q3sin1);
            dest.setF4(dest.getF4() + q4sin1);
            invNormalQuaternion(dest, dest);
        }
        return dest;
    }

    public static Matrix4x4 quaternionBlendAndConvertToMatrix(Matrix4x4 destMtx, Quaternion qsrc1, Quaternion qsrc2, float interpFactor, boolean b) {
        float prod;
        float invSinTheta = 1.0f;
        float theta = 1.5700001f;
        float pprod;

        if (destMtx == null) {
            destMtx = new Matrix4x4();
        }

        if (b) {
            prod = qsrc1.getF3() * qsrc2.getF3() + qsrc1.getF2() * qsrc2.getF2()
                    + qsrc1.getF4() * qsrc2.getF4() + qsrc1.getF1() * qsrc2.getF1();
            pprod = prod;
            if (prod <= 1.0f) {
                if (pprod < 0.0f) {
                    pprod = -pprod;
                    qsrc2.setF1(-qsrc2.getF1());
                    qsrc2.setF2(-qsrc2.getF2());
                    qsrc2.setF3(-qsrc2.getF3());
                    qsrc2.setF4(-qsrc2.getF4());
                }
            } else {
                pprod = 1.0f;
            }
            if (Math.abs(pprod - 1.0f) <= 0.0000001f) {
                theta = 0.0f;
                invSinTheta = 0.0f;
            } else {
                theta = (float) Math.acos(pprod);
                invSinTheta = (float) (1.0f / Math.sqrt(1.0f - pprod * pprod));
            }
        }

        destMtx.setFlags(destMtx.getFlags() | 0x4);
        Quaternion destQuat = Slerp(null, interpFactor, qsrc1, qsrc2, theta, invSinTheta);
        return convertQuaternionToMatrix(destMtx, destQuat);
    }

    public Quaternion duplicate() {
        return new Quaternion(f1, f2, f3, f4);
    }
}
