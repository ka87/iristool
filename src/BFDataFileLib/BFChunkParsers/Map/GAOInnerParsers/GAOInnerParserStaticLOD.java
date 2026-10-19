/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class GAOInnerParserStaticLOD extends BFChunkParseClass implements BFChunkParseInterface {

    private Byte numOrig;
    private Integer[] LODKeys = null;
    private BFKeyListElem[] LODChunkKLE = null;
    private Byte b2[];
    private Byte b1;

    public GAOInnerParserStaticLOD() {
        super();
    }

    public Integer[] getLODKeys() {
        return LODKeys;
    }

    public BFKeyListElem[] getLODChunkKLE() {
        return LODChunkKLE;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner Static LOD Chunk.");
        }

        Byte num;

        if (!buildFromParsed) {
            b2 = new Byte[6];
            num = ru.readByte(false);
            numOrig = num;
        } else {
            num = numOrig;
            wu.writeByte(num);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Num (max: 6): %d (%x)", num, num);
        }

        if (num > 6) {
            num = 6;
        }

        int i;

        if (!insideBinFile) {
            if (!buildFromParsed) {
                b1 = ru.readByte(false);
            } else {
                wu.writeByte(b1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("b1 (!insidebinfile): %d (%x)", b1, b1);
            }
        }

        for (i = 0; i < 6; i++) {
            if (!buildFromParsed) {
                b2[i] = ru.readByte(false);
            } else {
                wu.writeByte(b2[i]);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("b2 (%d): %d (%x)", i, b2[i], b2[i]);
            }
        }

        if (!buildFromParsed && num > 0) {
            LODKeys = new Integer[num];
            LODChunkKLE = new BFKeyListElem[num];
        }

        Integer key;
        BFKeyListElem lodkle;

        for (i = 0; i < num; i++) {
            if (!buildFromParsed) {
                key = ru.readInt(false);
                LODKeys[i] = key;
                lodkle = new BFKeyListElem(key, BFGaoInnerChunkParser.class);
                LODChunkKLE[i] = lodkle;
            } else {
                key = LODKeys[i];
                wu.writeInt(key);
                lodkle = LODChunkKLE[i];
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Static LOD Key found: %x", key);
            }
            bfdfc.addToKeyList(lodkle);
        }

        return true;
    }

}
