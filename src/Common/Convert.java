/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.nio.ByteBuffer;

/**
 *
 * @author andras
 */
public class Convert {

    public static int ShortToIntUnsigned(short s) {
        return s & 0xffff;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, 0, bytes.length);
    }

    public static String bytesToHex(byte[] bytes, int num) {
        return bytesToHex(bytes, 0, num);
    }

    public static String bytesToHex(byte[] bytes, int start, int num) {
        char[] hexChars = new char[num * 2];
        for (int j = 0; j < num; j++) {
            if (bytes.length <= j) {
                break;
            }

            int v = bytes[j + start] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static short shortBigEndianToLittleEndian(short s) {
        return (short) (((s >> 8) & 0xff) | ((s << 8) & 0xff00));
    }

    public static int intBigEndianToLittleEndian(int i) {
        return (i & 0xff) << 24 | (i & 0xff00) << 8 | (i & 0xff0000) >> 8 | (i >> 24) & 0xff;
    }

    public static long longBigEndianToLittleEndian(long l) {
        long ret;
        ret = (l >> 56)
                | ((l << 40) & 0x00FF000000000000L)
                | ((l << 24) & 0x0000FF0000000000L)
                | ((l << 8) & 0x000000FF00000000L)
                | ((l >> 8) & 0x00000000FF000000L)
                | ((l >> 24) & 0x0000000000FF0000L)
                | ((l >> 40) & 0x000000000000FF00L)
                | (l << 56);
        return ret;
    }

    public static byte[] intToHexArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static byte[] longToHexArray(long l) {
        return ByteBuffer.allocate(8).putLong(l).array();
    }

    public static byte[] floatToHexArray(float f) {
        return ByteBuffer.allocate(4).putFloat(f).array();
    }

    public static byte[] shortToHexArray(short s) {
        return ByteBuffer.allocate(2).putShort(s).array();
    }

    public static long intToLongUnsigned(int i) {
        return (i & 0xffffffffL);
    }

    public static int BGRToRGB(int bgr) {
        int red = (bgr >> 16) & 0xFF;
        int green = (bgr >> 8) & 0xFF;
        int blue = (bgr) & 0xFF;
        return (blue << 16) | (green << 8) | (red);
    }

    public static short hexArrayToShort(byte[] b) {
        return ByteBuffer.wrap(b).getShort();
    }

    public static int hexArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).getInt();
    }

    public static long hexArrayToLong(byte[] b) {
        return ByteBuffer.wrap(b).getLong();
    }

    public static short hexArrayToShort(byte[] b, int i1, int i2) {
        return ByteBuffer.wrap(b, i1, i2).getShort();
    }

    public static int hexArrayToInt(byte[] b, int i1, int i2) {
        return ByteBuffer.wrap(b, i1, i2).getInt();
    }

    public static long hexArrayToLong(byte[] b, int i1, int i2) {
        return ByteBuffer.wrap(b, i1, i2).getLong();
    }
}
