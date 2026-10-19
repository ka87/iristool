/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.Convert;
import Common.DataType.OFCProgramCodeElem;
import Common.DataType.OFCProgramCodeElemUnknown;
import Common.DataType.OFCLocalElem;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFScriptingFunctionCodeChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Integer programCodeElemNum = 0;
    private OFCProgramCodeElem ofcpes[];
    private byte[] desc;

    public Integer getProgramCodeElemNum() {
        return programCodeElemNum;
    }

    public OFCProgramCodeElem[] getOfcpes() {
        return ofcpes;
    }

    public byte[] getDesc() {
        return desc;
    }
       
    public BFScriptingFunctionCodeChunk() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Scripting Function Code Chunk (OFC).");
        }

        Long len = initParse();

        long startPos = ru.getCurrentPos();
        int i;

        if (len > 0) {
            /*
            This variable gets used, when a scripting function calls another function from itself (ai execute call)
            It probably contains function maximal used variable size sum to offset global random access memory pool to
            avoid memory collision with parent (caller) func. This avoids to overwrite caller function stack.
             */
            Integer randomAccessPoolOffsetter = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("randomAccessPoolOffsetter: %x (%d)", randomAccessPoolOffsetter, randomAccessPoolOffsetter);
            }
            Integer programCodeLen = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Programcode length: " + programCodeLen + " bytes.");
            }
            if (programCodeLen == null || programCodeLen % 8 != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Invalid programcode length.");
                }
                return false;
            }
            programCodeElemNum = programCodeLen / 8;

            if (!insideBinFile) {
                if (programCodeLen > 0) {
                    ofcpes = new OFCProgramCodeElem[programCodeLen];
                }
                for (i = 0; i < programCodeElemNum; i++) {
                    ofcpes[i] = new OFCProgramCodeElem();
                    if (!ofcpes[i].readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("OFCProgramCodeElem read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(ofcpes[i]);
                        log("  Details: " + ofcpes[i].getActualElemDescription());
                        log("  Extra details: " + ofcpes[i].getActualElemExtraDescription());
                    }
                }
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Programcode read ignored, because we are in bin file.");
                }
            }

            if (!insideBinFile) {
                Integer anotherProgramCodeLen = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Another programcode length: " + anotherProgramCodeLen + " bytes.");
                }
                if (anotherProgramCodeLen == null || anotherProgramCodeLen % 8 != 0) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Invalid another programcode length.");
                    }
                    return false;
                }
                Integer anotherProgramCodeElemNum = anotherProgramCodeLen / 8;
                for (i = 0; i < anotherProgramCodeElemNum; i++) {
                    OFCProgramCodeElemUnknown aofcpeu = new OFCProgramCodeElemUnknown();
                    if (!aofcpeu.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Another OFCProgramCodeElemUnknown read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(aofcpeu);
                    }
                }
            }

            Integer descLen = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Desc Length: " + descLen + " bytes.");
            }
            if (descLen != null && descLen > 0 && descLen < 5000) { // itt lehetnek olvasási gondok az ubisoft hülyesége miatt, ezért limitáljuk max 5000-re
                try {
                    desc = ru.readByteArray(descLen, false);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Desc: " + Convert.bytesToHex(desc));
                    }
                } catch (Exception e) {
                }
            }

            if ((ru.getCurrentPos() - startPos) != len) {
                if (!insideBinFile) {
                    Integer OFCLocalPartSize = ru.readInt(false);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("OFC Local Part size: " + OFCLocalPartSize + " bytes.");
                    }
                    if (OFCLocalPartSize != null && OFCLocalPartSize != 0) {
                        Integer OFCLocalPartNum = OFCLocalPartSize / 40;
                        for (i = 0; i < OFCLocalPartNum; i++) {
                            OFCLocalElem ofcle = new OFCLocalElem();
                            if (!ofcle.readValues(ru)) {
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    log("OFCLocalElem read failed.");
                                }
                                return false;
                            }
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log(ofcle);
                            }
                        }
                    }
                }
            }
        }

        if (insideBinFile && (ru.getCurrentPos() - startPos) != len) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != length, but it can be because of stupid Ubisoft (when inside bin and contains programcode)");
                log("aligning read position...");
            }
            ru.seek((int) (startPos + len));
        }

        return true;
    }

    @Override
    public String toString() {
        String name = bfdfc.getScriptNameByKey(selfKey);
        return "Function Code: Key: " + String.format("%X", selfKey) + (name != null ? (", " + name) : "");
    }

}
