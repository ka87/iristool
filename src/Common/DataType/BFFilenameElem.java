/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.IrisToolDefines;
import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public class BFFilenameElem {

    private Integer unk1, unk2, unk3, unk4, unk5;
    private String fileName; // in King Kong, file name string is 68 bytes long

    public BFFilenameElem() {
    }

    public BFFilenameElem(Integer unk1, Integer unk2, Integer unk3, Integer unk4, Integer unk5, String fileName) {
        this.unk1 = unk1;
        this.unk2 = unk2;
        this.unk3 = unk3;
        this.unk4 = unk4;
        this.unk5 = unk5;
        this.fileName = fileName;
    }

    public static int getHeaderSize() {
        if (IrisToolDefines.isHdVersionQuirk()) {
            return 4 + 4 + 4 + 4 + 4 + 68; // see readValues() read sum
        } else {
            return 4 + 4 + 4 + 4 + 4 + 64; // see readValues() read sum
        }
    }

    public Integer getUnk1() {
        return unk1;
    }

    public void setUnk1(Integer unk1) {
        this.unk1 = unk1;
    }

    public Integer getUnk2() {
        return unk2;
    }

    public void setUnk2(Integer unk2) {
        this.unk2 = unk2;
    }

    public Integer getUnk3() {
        return unk3;
    }

    public void setUnk3(Integer unk3) {
        this.unk3 = unk3;
    }

    public Integer getUnk4() {
        return unk4;
    }

    public void setUnk4(Integer unk4) {
        this.unk4 = unk4;
    }

    public Integer getUnk5() {
        return unk5;
    }

    public void setUnk5(Integer unk5) {
        this.unk5 = unk5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean readValues(ReaderUtil ru) {
        unk1 = ru.readInt(false);
        unk2 = ru.readInt(false);
        unk3 = ru.readInt(false);
        unk4 = ru.readInt(false);
        unk5 = ru.readInt(false);
        if (IrisToolDefines.isHdVersionQuirk()) {
            fileName = ru.readNString(68, false, true);
        } else {
            fileName = ru.readNString(64, false, true);
        }

        return (unk1 != null && unk2 != null && unk3 != null
                && unk4 != null && unk5 != null && fileName != null);
    }

    public void writeValues(WriterUtil wu) {
        wu.writeInt(unk1);
        wu.writeInt(unk2);
        wu.writeInt(unk3);
        wu.writeInt(unk4);
        wu.writeInt(unk5);
        if (IrisToolDefines.isHdVersionQuirk()) {
            wu.writeNString(fileName, 68);
        } else {
            wu.writeNString(fileName, 64);
        }
    }

    @Override
    public String toString() {
        return String.format("BFFileNameElem: %x, %x, %x, %x, %x, %s", unk1, unk2, unk3, unk4, unk5, fileName);
    }
}
