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
public class BFGridInsideChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFGridInsideChunk() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing GRID Inside Chunk.");
        }

        Long len = initParse();

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        long startPos = ru.getCurrentPos();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("GRID length: " + len + " bytes.");
        }

        Integer chunkLen, chunkLenMinusSize;
        Long chunkReadStart;
        Byte type;
        Byte unkBTypeC0;
        Byte unkB1Type80, unkB2Type80;
        Short unkS1Type40;
        Byte unkB1Type40;
        if (len != 0) {
            while (true) {
                chunkLen = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Chunk length: %d (%x)", chunkLen, chunkLen);
                }
                chunkLenMinusSize = chunkLen - 4;
                chunkReadStart = ru.getCurrentPos();
                while (true) {
                    type = ru.readByte(false);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("->Type: %x", type);
                    }
                    if ((type & 0xc0) == 0x0ffffffc0) {
                        unkBTypeC0 = ru.readByte(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("->Type: 0xc0 - unkBTypeC0: %d (%x)", unkBTypeC0, unkBTypeC0);
                        }
                    }
                    if (type == (byte) 0x80) { // cast it to byte because of silly java
                        unkB1Type80 = ru.readByte(false);
                        unkB2Type80 = ru.readByte(false); // FIXME: nem biztos: tipust olvassa be, a jatek vmi logikai kapcsolat miatt olvassa 2x.
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("->Type: 0x80 - unkB1Type80: %d (%x), unkB2Type80: %d (%x)", unkB1Type80, unkB1Type80, unkB2Type80, unkB2Type80);
                        }
                    } else if (type == (byte) 0x40) {
                        unkS1Type40 = ru.readShort(false);
                        unkB1Type40 = ru.readByte(false); // FIXME: nem biztos: tipust olvassa be, a jatek vmi logikai kapcsolat miatt olvassa 2x.
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("->Type: 0x40 - unkS1Type40: %d (%x), unkB1Type40: %d (%x)", unkS1Type40, unkS1Type40, unkB1Type40, unkB1Type40);
                        }
                    }
                    if ((ru.getCurrentPos() - chunkReadStart) >= chunkLenMinusSize) {
                        break;
                    }
                }

                if ((ru.getCurrentPos() - startPos) >= len) {
                    break;
                }
            }
        }
        if ((ru.getCurrentPos() - startPos) != len) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != length");
            }
            return false;
        }

        return true;
    }

}
