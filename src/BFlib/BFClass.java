/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFlib;

import BFDataFileLib.BFDataFileClass;
import Common.Convert;
import Common.DataType.BFFilenameElem;
import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;
import java.io.File;
import java.nio.ByteBuffer;

/**
 *
 * @author andras
 */
public final class BFClass extends Logger {

    private ReaderUtil ru[];
    private File mainFile;
    private File bfFileParts[];
    private boolean opened = false;
    private Integer allKeysNum = 0;
    private Integer allFATTableNum = 0;
    private Integer startKey = -1;
    private Integer bfUnkBFID = 0;
    private Integer bfUnknown2 = 0;
    private Integer bfUnknown3 = 0;
    private Integer bfUnknown4 = 0;
    private Integer bfUnknown5 = 0;
    private Integer bfUnknown6 = 0;
    private Integer FATMaxElemNum = 0;

    private BFFatTable bft = null;

    /**
     * Normal BF file handling (PC, GC, PS2)
     *
     * @param bfFile
     */
    public BFClass(File bfFile) {
        File fa[] = {bfFile};
        openBF(fa);
    }

    /**
     * Splitted BF file handling (bf0, bf1, bf2) (Xbox)
     *
     * @param bfFileParts
     */
    public BFClass(File bfFileParts[]) {
        openBF(bfFileParts);
    }

    public Integer getStartKey() {
        return startKey;
    }

    // TODO: SubFAT > 1 read broken, see KingKong
    private boolean readFAT() {
        int i, j;
        BFSubFatElem bsfe;
        BFFatTableElem bffte;

        if (bft != null) {
            bft.clearFatTable();
            bft = null;
        }
        bft = new BFFatTable();

        if (allFATTableNum <= 0) {
            return false;
        }

        for (i = 0; i < allFATTableNum; i++) {
            if (i > 0 && bft.getSubFatByIndex(i - 1).getNextFATPos() == -1
                    || (i > 0 && !ru[0].seek(bft.getSubFatByIndex(i - 1).getNextFATPos()))) {
                log("BF file FAT table inconsistency.");
                return false;
            }
            bsfe = new BFSubFatElem(ru[0].readInt(false), ru[0].readInt(false),
                    ru[0].readInt(false), ru[0].readInt(false), ru[0].readInt(false), ru[0].readInt(false));
            log(bsfe);

            if (bsfe.getFATPos() == null || bsfe.getFileNumber() == null || bsfe.getNextFATPos() == null
                    || bsfe.getUnknown1() == null || bsfe.getUnknown2() == null || bsfe.getUnknown3() == null) {
                log("BF file FAT table read error.");
                return false;
            }
            bft.addSubFat(bsfe);
        }

        for (i = 0; i < allFATTableNum; i++) {
            ru[0].seek(bft.getSubFatByIndex(i).getFATPos());
            for (j = 0; j < bft.getSubFatByIndex(i).getFileNumber(); j++) {
                bffte = new BFFatTableElem(ru[0].readInt(false), ru[0].readInt(false));
                if (bffte.getPosition() == null || bffte.getKey() == null) {
                    log("BF key to file offset list read error.");
                    return false;
                }
                bft.addElem(bffte);
            }
            if (IrisToolDefines.isReadFileNameListAndDirectoryList()) {
                int fileNameListStartOffset = FATMaxElemNum * 2 * 4 + bft.getSubFatByIndex(i).getFATPos();
                ru[0].seek(fileNameListStartOffset);
                BFFilenameElem fne;
                for (j = 0; j < bft.getSubFatByIndex(i).getFileNumber(); j++) {
                    fne = new BFFilenameElem();
                    if (!fne.readValues(ru[0])) {
                        log("BFFileNameElem read failed.");
                    }
                    log(fne);
                    bft.addFileNameElem(fne);
                }
                ru[0].seek(fileNameListStartOffset + (FATMaxElemNum * (BFFilenameElem.getHeaderSize())));
                Integer unk1;
                while (true) {
                    unk1 = ru[0].readInt(false);
                    if (unk1.equals(0)) {
                        break;
                    }
                    logF("ProjectDirectories: %8x, %8x, %8x, %8x, %8x, %s", unk1, ru[0].readInt(false), ru[0].readInt(false), ru[0].readInt(false),
                            ru[0].readInt(false), ru[0].readNString(64, false, true));
                }
            }
        }

        return true;
    }

    public BFFatTable getFatTable() {
        return bft;
    }

    public boolean isOpened() {
        return opened;
    }

    public File getFile() {
        return mainFile;
    }

    public Integer getBfUnkBFID() {
        return bfUnkBFID;
    }

    public boolean openBF(File bfFileParts[]) {
        if (bfFileParts == null || bfFileParts.length == 0) {
            return false;
        }

        ru = new ReaderUtil[bfFileParts.length];

        if (isOpened()) {
            close();
        }

        this.mainFile = bfFileParts[0];
        this.bfFileParts = bfFileParts;

        ru[0] = new ReaderUtil(bfFileParts[0]);
        if (!ru[0].isOpened()) {
            return false;
        }

        String bfStartID = ru[0].readNString(4, false, true);

        if (!"BIG".equals(bfStartID)) {
            log("invalid file.");
            close();
            return false;
        }

        bfUnkBFID = ru[0].readInt(false);
        allKeysNum = ru[0].readInt(false);
        bfUnknown2 = ru[0].readInt(false);
        bfUnknown3 = ru[0].readInt(false);
        bfUnknown4 = ru[0].readInt(false);
        bfUnknown5 = ru[0].readInt(false);
        bfUnknown6 = ru[0].readInt(false);
        FATMaxElemNum = ru[0].readInt(false);
        allFATTableNum = ru[0].readInt(false);
        startKey = ru[0].readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("StartKey: %x", startKey);
            logF("FAT table number: %d", allFATTableNum);
            logF("BF contains %d keys", allKeysNum);
            logF("BfUnkBFID : %x (%d)", bfUnkBFID, bfUnkBFID);
            logF("BfUnknown2: %x (%d)", bfUnknown2, bfUnknown2);
            logF("BfUnknown3: %x (%d)", bfUnknown3, bfUnknown3);
            logF("BfUnknown4: %x (%d)", bfUnknown4, bfUnknown4);
            logF("BfUnknown5: %x (%d)", bfUnknown5, bfUnknown5);
            logF("BfUnknown6: %x (%d)", bfUnknown6, bfUnknown6);
            logF("FAT max key number: %d", FATMaxElemNum, FATMaxElemNum);
        }

        if (bfUnkBFID.equals(0x25)) {
            log("Detected HD edition, enabling HD specific quirks.");
            IrisToolDefines.setHdVersionQuirk(true);
        } else {
            log("Detected game original edition.");
            IrisToolDefines.setHdVersionQuirk(false);
        }

        if (!readFAT()) {
            log("FAT table read failed.");
            return false;
        }

        if (!openAdditionalFiles(bfFileParts)) {
            log("Opening additional files failed.");
            return false;
        }

        opened = true;

        return true;
    }

    private boolean openAdditionalFiles(File bfFileParts[]) {
        if (bfFileParts.length > 1) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Opening additional BF file parts...");
            }
            for (int i = 1; i < bfFileParts.length; i++) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Opening " + bfFileParts[i].getName() + " ...");
                }
                ru[i] = new ReaderUtil(bfFileParts[i]);
                if (!ru[i].isOpened()) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Open failed.");
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public long getDataLength() {
        long size = 0L;
        for (int i = 0; i < bfFileParts.length; i++) {
            size += ru[i].getDataLength();
        }
        return size;
    }

    public byte[] readDataFromPos(long pos, long len) {
        int i;
        long currPartEndOffset = 0;
        long posTmp = pos;
        long lenTmp = len;

        if (!isOpened() || ru == null) {
            return null;
        }

        byte tmpRet[] = new byte[(int) len];

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Seeking to position: " + pos);
        }
        for (i = 0; i < ru.length; i++) {
            currPartEndOffset += ru[i].getDataLength();
            if (pos < currPartEndOffset) {
                break;
            }
            if (i >= (ru.length - 1)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Seek failed.");
                }
                return null;
            }
            posTmp -= ru[i].getDataLength();
        }

        if (!ru[i].seek(posTmp)) {
            return null;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Seek succeeded.");
        }

        byte tmpData[];
        long sizeToRead;
        long tmpRetCurrOffset = 0;

        for (; i < ru.length; i++) {
            if (posTmp + lenTmp > currPartEndOffset) {
                sizeToRead = Math.min(lenTmp - ((posTmp + lenTmp) - currPartEndOffset), lenTmp);
            } else {
                sizeToRead = lenTmp;
            }
            tmpData = ru[i].readByteArray((int) sizeToRead, false);
            // append data
            System.arraycopy(tmpData, 0, tmpRet, (int) tmpRetCurrOffset, (int) sizeToRead);
            tmpRetCurrOffset += sizeToRead;
            posTmp += sizeToRead;
            if (sizeToRead == lenTmp) {
                lenTmp = 0;
                break;
            }
            if (i < ru.length - 1) {
                currPartEndOffset += ru[i].getDataLength();
            }
            lenTmp -= sizeToRead;
        }

        if (lenTmp != 0) {
            log("Not enough data to read.");
            return null;
        }
        return tmpRet;
    }

    public byte[] readLengthAndDataFromPos(long pos) {
        if (!isOpened() || ru == null) {
            return null;
        }

        byte lenData[] = readDataFromPos(pos, 4);

        if (lenData == null) {
            return null;
        }

        long len = Integer.toUnsignedLong(Convert.intBigEndianToLittleEndian(ByteBuffer.wrap(lenData).getInt()));

        if (len <= 0) {
            log("Length read failed: returned " + len);
            return null;
        }

        if ((pos + len) > getDataLength()) {
            log("Invalid length: " + len);
            return null;
        }

        return readDataFromPos(pos + 4, len);
    }

    static public byte[] extractFatTableElem(BFClass bfc, int elem, boolean decompress) {
        if (elem == -1 || bfc.getFatTable().getElemCount() <= elem) {
            return null;
        }

        byte[] data;
        byte[] extdata;

        if (BFDataFileClass.isDataCompressedByKey(bfc.getFatTable().getElemByIndex(elem).getKey()) && decompress) {
            data = bfc.readLengthAndDataFromPos(bfc.getFatTable().getElemByIndex(elem).getPosition());
            extdata = BFDataFileClass.decompressDataFileLZO(data);
            return extdata;
        } else {
            data = bfc.readLengthAndDataFromPos(bfc.getFatTable().getElemByIndex(elem).getPosition());
            return data;
        }
    }

    public byte[] getDataFromBFbyKey(int key) {
        BFFatTableElem bfe = getFatTable().getElemByKey(key);
        return extractFatTableElem(this, getFatTable().getElemIndex(bfe), true);
    }

    private boolean close() {
        boolean ret = true;
        if (ru != null && ru.length != 0) {
            for (int i = 0; i < ru.length; i++) {
                if (!(ru[i].close())) {
                    ret = false;
                }
            }
        }

        this.mainFile = null;

        return ret;
    }
}
