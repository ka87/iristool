/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;
import Common.ReaderUtil;

/**
 *
 * @author andras
 */
public class OVATypeElem extends Logger {

    private Integer memoryOffset, typeNum;
    private Short type, flags; // flags : 0x10 -> save to savegame, 0x20 -> restore val in universe on reinit
    private Short typeOrig;

    public boolean isArray() {
        return ((typeNum >> 30) & 0x3) != 0;
    }

    public int getArrayDimension() {
        return ((typeNum >> 30) & 0x3);
    }

    public int getRawDataElemNumWithoutDimensionSizes() {
        return typeNum & 0x3fffffff;
    }

    public Integer getMemoryOffset() {
        return memoryOffset;
    }

    public void setMemoryOffset(Integer memoryOffset) {
        this.memoryOffset = memoryOffset;
    }

    public Integer getTypeNum() {
        return typeNum;
    }

    public void setTypeNum(Integer typeNum) {
        this.typeNum = typeNum;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Short getTypeOrig() {
        return typeOrig;
    }

    public Short getFlags() {
        return flags;
    }

    public void setFlags(Short flags) {
        this.flags = flags;
    }

    public OVATypeElem(Integer memoryOffset, Integer typeNum, Short type, Short flags) {
        this.memoryOffset = memoryOffset;
        this.typeNum = typeNum;
        this.type = type;
        this.flags = flags;
        this.typeOrig = type;
    }

    public OVATypeElem() {
    }

    public boolean readValues(ReaderUtil ru) {
        this.memoryOffset = ru.readInt(false);
        this.typeNum = ru.readInt(false);
        this.type = ru.readShort(false);
        this.flags = ru.readShort(false);
        this.typeOrig = this.type;

        return (this.memoryOffset != null && this.typeNum != null && this.type != null && this.flags != null);
    }

    @Override
    public String toString() {
        return String.format("Variables: memoryOffset=%d (%x), typeNum=%d (%x), type=%d (%x), flags=%x (%d)",
                memoryOffset, memoryOffset, typeNum, typeNum, type, type, flags, flags);
    }
}
