/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFAnimationShapeChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Long animationLen = 0L;
    private Byte elemNum = 0;
    private Byte elemNumOrig = 0;
    private Byte[] boneIDArray, IDForOFCArray, groupElemIDArray;

    public BFAnimationShapeChunk() {
        super();
        boneIDArray = new Byte[64]; // elem id in action/event gao group (bone)
        IDForOFCArray = new Byte[64]; // OFC functions uses this id to change shapes
        groupElemIDArray = new Byte[64]; // elem id in group, (special meanings (eg -3)?)
    }

    @Override
    public BFAnimationShapeChunk duplicate() {
        BFAnimationShapeChunk asc = new BFAnimationShapeChunk();
        asc.setSelfKey(this.getSelfKey());
        Byte[] tmpBoneIDArray = new Byte[64];
        Byte[] tmpB2Array = new Byte[64];
        Byte[] tmpGroupElemIDArray = new Byte[64];
        System.arraycopy(getBoneIDArray(), 0, tmpBoneIDArray, 0, getBoneIDArray().length);
        System.arraycopy(getIDForOFCArray(), 0, tmpB2Array, 0, getIDForOFCArray().length);
        System.arraycopy(getGroupElemIDArray(), 0, tmpGroupElemIDArray, 0, getGroupElemIDArray().length);
        asc.setBoneIDArray(tmpBoneIDArray);
        asc.setIDForOFCArray(tmpB2Array);
        asc.setGroupElemIDArray(tmpGroupElemIDArray);
        asc.setElemNum(this.getElemNum());
        return asc;
    }

    public Byte getElemNum() {
        return elemNum;
    }

    public Byte[] getBoneIDArray() {
        return boneIDArray;
    }

    public Byte[] getIDForOFCArray() {
        return IDForOFCArray;
    }

    public Byte[] getGroupElemIDArray() {
        return groupElemIDArray;
    }

    public void setElemNum(Byte elemNum) {
        this.elemNum = elemNum;
    }

    public void setBoneIDArray(Byte[] boneIDArray) {
        this.boneIDArray = boneIDArray;
    }

    public void setIDForOFCArray(Byte[] IDForOFCArray) {
        this.IDForOFCArray = IDForOFCArray;
    }

    public void setGroupElemIDArray(Byte[] groupElemIDArray) {
        this.groupElemIDArray = groupElemIDArray;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        long startpos = 0;

        if (!buildFromParsed) {
            boneIDArray = new Byte[64];
            IDForOFCArray = new Byte[64];
            groupElemIDArray = new Byte[64];
            animationLen = initParse();

            if (animationLen == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Length read failed.");
                }
                return false;
            }

            startpos = ru.getCurrentPos();

        } else {
            wu.startBlockWithLength();
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Animation Shape Chunk.");
            log("Length is " + animationLen + " bytes.");
        }

        if (!buildFromParsed) {
            elemNum = ru.readByte(false);
            elemNumOrig = elemNum;
        } else {
            wu.writeByte(elemNumOrig);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Animation Shape elem number: %d (%x)", elemNum, elemNum);
        }

        if (elemNum > 64) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Limiting elemNum to 64.");
            }
            elemNum = 64;
        }

        int i;

        for (i = 0; i < elemNum; i++) {
            if (!buildFromParsed) {
                boneIDArray[i] = ru.readByte(false);
                IDForOFCArray[i] = ru.readByte(false);
                groupElemIDArray[i] = ru.readByte(false);
            } else {
                wu.writeByte(boneIDArray[i]);
                wu.writeByte(IDForOFCArray[i]);
                wu.writeByte(groupElemIDArray[i]);
            }
            // groupElemID == -3, gao must be hidden
            // groupElemID == -2, gao must be show
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("boneID: %d (%x), ID: %d (%x), groupElemID: %d (%x)", boneIDArray[i], boneIDArray[i], IDForOFCArray[i], IDForOFCArray[i], groupElemIDArray[i], groupElemIDArray[i]);
            }
        }

        if (!buildFromParsed) {
            if (insideBinFile && (ru.getCurrentPos() - startpos) != animationLen) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Parsed length != length");
                }
                return false;
            }
        } else {
            wu.finishBlockWithLength();
        }
        return true;
    }

}
