/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author andras
 */
public class WriterUtil extends Logger {

    private ByteArrayOutputStream baos, activeBaos;
    private DataOutputStream dos, activeDos;
    private ArrayList<ByteArrayOutputStream> baosBlockArrayWithLength;
    private ArrayList<DataOutputStream> dosBlockArrayWithLength;
    private int openedBlockWithLengthNum = 0;
    private int endianness;

    final int WU_ENDIAN_LITTLE = 0;
    final int WU_ENDIAN_BIG = 1;

    public boolean setEndianness(int endian) {
        if (endian != WU_ENDIAN_BIG && endian != WU_ENDIAN_LITTLE) {
            return false;
        }

        endianness = endian;

        return true;
    }

    public int getEndianness() {
        return endianness;
    }

    public void reset() {
        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            activeBaos = baos;
            activeDos = dos;
            baosBlockArrayWithLength.clear();
            dosBlockArrayWithLength.clear();
            openedBlockWithLengthNum = 0;
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
    }

    public void startBlockWithLength() {
        openedBlockWithLengthNum++;
        ByteArrayOutputStream tmpBaos = new ByteArrayOutputStream();
        DataOutputStream tmpDos = new DataOutputStream(tmpBaos);
        baosBlockArrayWithLength.add(tmpBaos);
        dosBlockArrayWithLength.add(tmpDos);
        activeBaos = tmpBaos;
        activeDos = tmpDos;
    }

    public void finishBlockWithLength() {
        if (openedBlockWithLengthNum <= 0) {
            return;
        }
        try {
            activeBaos.flush();
        } catch (IOException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
        int length = activeBaos.size();
        byte[] data = activeBaos.toByteArray();
        baosBlockArrayWithLength.remove(openedBlockWithLengthNum - 1);
        dosBlockArrayWithLength.remove(openedBlockWithLengthNum - 1);
        if (openedBlockWithLengthNum == 1) {
            activeBaos = baos;
            activeDos = dos;
        } else {
            activeBaos = baosBlockArrayWithLength.get(openedBlockWithLengthNum - 2);
            activeDos = dosBlockArrayWithLength.get(openedBlockWithLengthNum - 2);
        }
        openedBlockWithLengthNum--;
        writeInt(length);
        writeByteArray(data);
    }

    public void cancelBlockWithLength() {
        if (openedBlockWithLengthNum <= 0) {
            return;
        }
        baosBlockArrayWithLength.remove(openedBlockWithLengthNum - 1);
        dosBlockArrayWithLength.remove(openedBlockWithLengthNum - 1);
        if (openedBlockWithLengthNum == 1) {
            activeBaos = baos;
            activeDos = dos;
        } else {
            activeBaos = baosBlockArrayWithLength.get(openedBlockWithLengthNum - 2);
            activeDos = dosBlockArrayWithLength.get(openedBlockWithLengthNum - 2);
        }
        openedBlockWithLengthNum--;
    }

    public void cancelAllBlocksWithLength() {
        activeBaos = baos;
        activeDos = dos;
        baosBlockArrayWithLength.clear();
        dosBlockArrayWithLength.clear();
        openedBlockWithLengthNum = 0;
    }

    public WriterUtil() {
        baosBlockArrayWithLength = new ArrayList<>();
        dosBlockArrayWithLength = new ArrayList<>();
        endianness = WU_ENDIAN_LITTLE;
        reset();
    }

    public void writeByte(byte b) {
        try {
            activeDos.writeByte((int) b);
        } catch (IOException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
    }

    public void writeByteArray(byte[] b) {
        try {
            activeDos.write(b);
        } catch (IOException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
    }

    public void writeInt(Integer i) {
        if (endianness == WU_ENDIAN_LITTLE) {
            i = Convert.intBigEndianToLittleEndian(i);
        }
        try {
            activeDos.writeInt(i);
        } catch (IOException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
    }

    public void writeFloat(Float f) {
        int tmp = Float.floatToIntBits(f);
        if (endianness == WU_ENDIAN_LITTLE) {
            tmp = Convert.intBigEndianToLittleEndian(tmp);
        }
        writeInt(tmp);
    }

    public void writeLong(Long l) {
        if (endianness == WU_ENDIAN_LITTLE) {
            l = Convert.longBigEndianToLittleEndian(l);
        }
        try {
            activeDos.writeLong(l);
        } catch (IOException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
    }

    public void writeShort(Short s) {
        if (endianness == WU_ENDIAN_LITTLE) {
            s = Convert.shortBigEndianToLittleEndian(s);
        }
        try {
            activeDos.writeShort(s);
        } catch (IOException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
    }

    public void writeNString(String s, int len) {
        if (len <= 0) {
            return;
        }
        try {
            byte[] ret;
            byte[] b = s.getBytes("ISO-8859-1");
            if (b.length == len) {
                ret = b;
            } else {
                ret = Arrays.copyOf(b, len);
            }
            writeByteArray(ret);
        } catch (UnsupportedEncodingException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
    }

    public void fillWithNByte(Byte b, int len) {
        int i;
        for (i = 0; i < len; i++) {
            writeByte(b);
        }
    }

    public byte[] getBytes() {
        try {
            baos.flush();
        } catch (IOException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
        }
        return activeBaos.toByteArray();
    }

    public static void test() {
        WriterUtil wu = new WriterUtil();

        wu.writeInt(0xaa);
        wu.writeShort((short) 0xbb);
        wu.startBlockWithLength();
        wu.writeInt(0xcc);
        wu.writeInt(0xdd);
        wu.startBlockWithLength();
        wu.writeInt(0xee);
        wu.writeInt(0xff);
        wu.finishBlockWithLength();
        wu.finishBlockWithLength();
        wu.writeInt(0xb1);
        System.out.println("a:" + Convert.bytesToHex(wu.getBytes()));
    }

}
