/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFlib;

/**
 *
 * @author andras
 */
public class BFSubFatElem {

    private Integer fileNumber;
    private Integer unknown1;
    private Integer FATPos;
    private Integer nextFATPos;
    private Integer unknown2;
    private Integer unknown3;

    public BFSubFatElem(Integer filenumber, Integer unknown1, Integer fatpos,
            Integer nextFATPos, Integer unknown2, Integer unknown3) {
        this.fileNumber = filenumber;
        this.unknown1 = unknown1;
        this.FATPos = fatpos;
        this.nextFATPos = nextFATPos;
        this.unknown2 = unknown2;
        this.unknown3 = unknown3;
    }
    
    public BFSubFatElem() {
    
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(Integer filenumber) {
        this.fileNumber = filenumber;
    }

    public Integer getUnknown1() {
        return unknown1;
    }

    public void setUnknown1(Integer unknown1) {
        this.unknown1 = unknown1;
    }

    public Integer getFATPos() {
        return FATPos;
    }

    public void setFATPos(Integer fatpos) {
        this.FATPos = fatpos;
    }

    public Integer getNextFATPos() {
        return nextFATPos;
    }

    public void setNextFATPos(Integer nextfatpos) {
        this.nextFATPos = nextfatpos;
    }

    public Integer getUnknown2() {
        return unknown2;
    }

    public void setUnknown2(Integer unknown2) {
        this.unknown2 = unknown2;
    }

    public Integer getUnknown3() {
        return unknown3;
    }

    public void setUnknown3(Integer unknown3) {
        this.unknown3 = unknown3;
    }    
    
    @Override
    public String toString() {
        return String.format("SubFAT elem: File number: %d, unknown1: %x (%d), FATPos: %x, nextFATPos: %x, unknown2: %x (%d), unknown3: %x (%d)", fileNumber, 
                unknown1, unknown1, FATPos, nextFATPos, unknown2, unknown2, unknown3, unknown3);
    }
    
}
