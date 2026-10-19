/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 *
 * @author andras
 */
public class ReaderUtil extends Logger {

    private ExtDataInputStream dis = null;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;
    private int opened;
    private File f = null;
    private byte[] bdata = null;
    private long pos = 0;
    private int endianness;
    private boolean xored = false;
    private int xorVal = 0;

    final int RU_CLOSED = 0;
    final int RU_FILE = 1;
    final int RU_DATA = 2;

    final int RU_ENDIAN_LITTLE = 0;
    final int RU_ENDIAN_BIG = 1;

    public boolean isXored() {
        return xored;
    }

    public void setXored(boolean xored) {
        this.xored = xored;
        this.xorVal = 0;
    }

    public void setXored(boolean xored, int xorVal) {
        this.xored = xored;
        this.xorVal = xorVal;
    }

    private byte[] unXor(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            b[i] ^= (byte) (xorVal << ((b.length - i) & 3));
        }
        return b;
    }

    public boolean setEndianness(int endian) {
        if (endian != RU_ENDIAN_BIG && endian != RU_ENDIAN_LITTLE) {
            return false;
        }

        endianness = endian;

        return true;
    }

    public int getEndianness() {
        return endianness;
    }

    public ReaderUtil(byte[] data) {
        opened = RU_CLOSED;
        endianness = RU_ENDIAN_LITTLE;
        openData(data);
    }

    public ReaderUtil(File f) {
        opened = RU_CLOSED;
        endianness = RU_ENDIAN_LITTLE;
        openFile(f);
    }

    public boolean reset() {
        if (!isOpened()) {
            return false;
        }

        File fBackup = f;
        byte[] bdataBackup = bdata;

        if (getOpenDataType() == RU_FILE) {
            opened = RU_CLOSED;
            close();
            if (!openFile(fBackup)) {
                return false;
            }
            pos = 0;
            return true;
        }

        if (getOpenDataType() == RU_DATA) {
            opened = RU_CLOSED;
            close();
            if (!openData(bdataBackup)) {
                return false;
            }
            pos = 0;
            return true;
        }
        return false;
    }

    public boolean seek(long toPos) {
        if (!isOpened()) {
            return false;
        }
        try {

            if (getOpenDataType() != RU_FILE && getOpenDataType() != RU_DATA) {
                return false;
            }

            if (getOpenDataType() == RU_FILE) {
                if (toPos >= f.length()) {
                    return false;
                }
            }

            if (getOpenDataType() == RU_DATA) {
                if (toPos >= bdata.length) {
                    return false;
                }
            }

            if (toPos > pos) {
                dis.skipBytesL(toPos - pos);
            } else {
                if (!reset()) {
                    return false;
                }
                dis.skipBytesL(toPos);
            }
            pos = toPos;

            return true;

        } catch (IOException e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("IO Exception:" + e.getMessage());
            }
            return false;
        }
    }

    public boolean openData(byte[] data) {

        if (data == null) {
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Data size: " + data.length + " bytes");
        }

        if (isOpened()) {
            this.close();
        }

        try {
            InputStream is = new ByteArrayInputStream(data);
            bis = new BufferedInputStream(is);
            dis = new ExtDataInputStream(bis);
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            return false;
        }
        opened = RU_DATA;
        bdata = data;
        pos = 0;
        return true;
    }

    public boolean openFile(File f) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Opening file: " + f.getAbsolutePath());
            log("File size: " + f.length() + " bytes");
        }
        if (isOpened()) {
            this.close();
        }

        try {
            fis = new FileInputStream(f);
            bis = new BufferedInputStream(fis);
            dis = new ExtDataInputStream(bis);
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            return false;
        }
        opened = RU_FILE;
        this.f = f;
        pos = 0;
        return true;
    }

    public boolean isOpened() {
        return opened != RU_CLOSED;
    }

    public int getOpenDataType() {
        return opened;
    }

    public boolean close() {
        boolean ret = true;
        f = null;
        bdata = null;
        if (!isOpened()) {
            return ret;
        }

        try {
            dis.close();
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            ret = false;
        }

        try {
            bis.close();
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            ret = false;
        }

        try {
            if (fis != null) {
                fis.close();
            }
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            ret = false;
        }
        return ret;
    }

    public byte[] readByteArray(int size) {
        return readByteArray(size, false);
    }

    public byte[] readByteArray(int size, boolean noIncrementPos) {
        long prevpos = pos;
        if (!isOpened()) {
            return null;
        }

        byte[] data = new byte[size];

        if (noIncrementPos) {
            dis.mark(size);
        }
        try {
            pos += dis.read(data);
            if (isXored()) {
                unXor(data);
            }
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("readByteArray Exception: " + e.getMessage());
            }
            try {
                if (noIncrementPos) {
                    dis.reset();
                    pos = prevpos;
                }
            } catch (Exception ee) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("readByteArray Exception: " + e.getMessage());
                }
            }
            return null;
        }
        try {
            if (noIncrementPos) {
                dis.reset();
                pos = prevpos;
            }
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("readByteArray Exception: " + e.getMessage());
            }
            return null;
        }
        return data;
    }

    public String readStringToEnd(boolean noIncrementPos) {
        if (!isOpened()) {
            return null;
        }

        long currPos = getCurrentPos();

        Byte b = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        do {
            b = readByte(false);
            if (isXored()) {
                byte[] xb = unXor(new byte[]{b});
                b = xb[0];
            }
            if (b == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("read failed.");
                }
                return null;
            }
            bos.write(b);
        } while (b != 0x00);

        if (noIncrementPos) {
            if (!seek(currPos)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Seek back failed.");
                }
                return null;
            }
        }

        byte[] ret = bos.toByteArray();

        return new String(ret, 0, ret.length, Charset.forName("ISO-8859-1"));
    }

    public String readNString(int num, boolean noIncrementPos) {
        return readNString(num, noIncrementPos, false);
    }

    public String readNString(int num, boolean noIncrementPos, boolean cutAtFirstNull) {
        byte[] data = readByteArray(num, noIncrementPos);

        if (data == null) {
            return null;
        }

        int i = data.length;

        if (cutAtFirstNull) {
            for (i = 0; i < data.length; i++) {
                if (data[i] == 0) {
                    break;
                }
            }

        }

        return new String(data, 0, i, Charset.forName("ISO-8859-1"));
    }

    public Integer readInt(boolean noIncrementPos) {
        long prevpos = pos;

        if (!isOpened()) {
            return null;
        }

        int ret;

        if (noIncrementPos) {
            dis.mark(4);
        }
        try {
            ret = dis.readInt();
            if (isXored()) {
                byte[] xb = unXor(Convert.intToHexArray(ret));
                ret = Convert.hexArrayToInt(xb);
            }
            if (endianness == RU_ENDIAN_LITTLE) {
                ret = Convert.intBigEndianToLittleEndian(ret);
            }
            pos += 4;
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            try {
                if (noIncrementPos) {
                    dis.reset();
                    pos = prevpos;
                }
            } catch (Exception ee) {
            }
            return null;
        }
        try {
            if (noIncrementPos) {
                dis.reset();
                pos = prevpos;
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }

    public Float readFloat(boolean noIncrementPos) {
        long prevpos = pos;

        if (!isOpened()) {
            return null;
        }

        float ret;

        if (noIncrementPos) {
            dis.mark(4);
        }
        try {
            Integer tmp = dis.readInt();
            if (isXored()) {
                byte[] xb = unXor(Convert.intToHexArray(tmp));
                tmp = Convert.hexArrayToInt(xb);
            }
            if (endianness == RU_ENDIAN_LITTLE) {
                ret = Float.intBitsToFloat(Convert.intBigEndianToLittleEndian(tmp));
            } else {
                ret = Float.intBitsToFloat(tmp);
            }
            pos += 4;
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            try {
                if (noIncrementPos) {
                    dis.reset();
                    pos = prevpos;
                }
            } catch (Exception ee) {
            }
            return null;
        }
        try {
            if (noIncrementPos) {
                dis.reset();
                pos = prevpos;
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }

    public Long readLong(boolean noIncrementPos) {
        long prevpos = pos;

        if (!isOpened()) {
            return null;
        }

        long ret;

        if (noIncrementPos) {
            dis.mark(8);
        }
        try {
            ret = dis.readLong();
            if (isXored()) {
                byte[] xb = unXor(Convert.longToHexArray(ret));
                ret = Convert.hexArrayToLong(xb);
            }
            if (endianness == RU_ENDIAN_LITTLE) {
                ret = Convert.longBigEndianToLittleEndian(ret);
            }
            pos += 8;
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            try {
                if (noIncrementPos) {
                    dis.reset();
                    pos = prevpos;
                }
            } catch (Exception ee) {
            }
            return null;
        }
        try {
            if (noIncrementPos) {
                dis.reset();
                pos = prevpos;
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }

    public Short readShort(boolean noIncrementPos) {
        long prevpos = pos;

        if (!isOpened()) {
            return null;
        }

        short ret;

        if (noIncrementPos) {
            dis.mark(2);
        }
        try {
            ret = dis.readShort();
            if (isXored()) {
                byte[] xb = unXor(Convert.shortToHexArray(ret));
                ret = Convert.hexArrayToShort(xb);
            }
            if (endianness == RU_ENDIAN_LITTLE) {
                ret = Convert.shortBigEndianToLittleEndian(ret);
            }
            pos += 2;
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            try {
                if (noIncrementPos) {
                    dis.reset();
                }
            } catch (Exception ee) {
            }
            return null;
        }
        try {
            if (noIncrementPos) {
                dis.reset();
                pos = prevpos;
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }

    public Byte readByte(boolean noIncrementPos) {
        long prevpos = pos;

        if (!isOpened()) {
            return null;
        }

        byte ret;

        if (noIncrementPos) {
            dis.mark(1);
        }
        try {
            ret = dis.readByte();
            if (isXored()) {
                byte[] xb = unXor(new byte[]{ret});
                ret = xb[0];
            }
            pos++;
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Exception: " + e.getMessage());
            }
            try {
                if (noIncrementPos) {
                    dis.reset();
                    pos = prevpos;
                }
            } catch (Exception ee) {
            }
            return null;
        }
        try {
            if (noIncrementPos) {
                dis.reset();
                pos = prevpos;
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }

    public File getFile() {
        if (isOpened()) {
            return f;
        }
        return null;
    }

    public long getCurrentPos() {
        return pos;
    }

    public long getDataLength() {
        if (!isOpened()) {
            return 0;
        }
        if (getOpenDataType() == RU_DATA) {
            return bdata.length;
        }
        if (getOpenDataType() == RU_FILE) {
            return f.length();
        }

        return 0;
    }

    public boolean isEOF() {
        if ((getDataLength() - 1) <= getCurrentPos()) {
            return true;
        }
        return false;
    }
}
