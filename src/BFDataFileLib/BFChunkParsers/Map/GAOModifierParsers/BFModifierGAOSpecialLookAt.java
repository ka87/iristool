/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierGAOSpecialLookAt extends BFModifierBaseClass implements BFChunkParseInterface {

    private Integer readType;
    private Byte type, b2, b3, b4;
    private Float zCoord;
    private Integer gaoID;
    private Integer gaoKey;
    private BFKeyListElem gaoKLE = null;
    private Float uF2, uF3;

    public BFModifierGAOSpecialLookAt() {
        super();
    }

    public Byte getType() {
        return type;
    }

    public Integer getGaoID() {
        return gaoID;
    }

    public Integer getGaoKey() {
        return gaoKey;
    }

    public Float getuF2() {
        return uF2;
    }

    public Float getuF3() {
        return uF3;
    }

    public BFKeyListElem getGaoKLE() {
        return gaoKLE;
    }

    public Float getzCoord() {
        return zCoord;
    }
        
    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Special Look at (Type 7) Modifier.");
        }

        if (!buildFromParsed) {
            readType = ru.readInt(false);

            type = ru.readByte(false);
            b2 = ru.readByte(false);
            b3 = ru.readByte(false);
            b4 = ru.readByte(false);
        } else {
            wu.writeInt(readType);
            wu.writeByte(type);
            wu.writeByte(b2);
            wu.writeByte(b3);
            wu.writeByte(b4);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("type: %d (%x), b2: %d (%x), b3: %d (%x), b4: %d (%x)", type, type, b2, b2, b3, b3, b4, b4);
        }

        if (!buildFromParsed) {
            zCoord = ru.readFloat(false);
        } else {
            wu.writeFloat(zCoord);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("zCoord: %f", zCoord);
        }

        gaoID = -1;

        if (readType == 28) {
            if (!buildFromParsed) {
                gaoID = ru.readInt(false);
            } else {
                wu.writeInt(gaoID);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("gaoID: %d (%x)", gaoID, gaoID);
            }
        }

        if (!buildFromParsed) {
            gaoKey = ru.readInt(false);
        } else {
            wu.writeInt(gaoKey);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("GAO Key: %x", gaoKey);
        }

        if (gaoID == -1 && gaoKey != 0 && gaoKey != -1) {
            bfdfc.addToKeyList(gaoKLE = new BFKeyListElem(gaoKey, BFGaoChunk.class));
        }

        if (readType != 4) {
            if (!buildFromParsed) {
                uF2 = ru.readFloat(false);
                uF3 = ru.readFloat(false);
            } else {
                wu.writeFloat(uF2);
                wu.writeFloat(uF3);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("uF2: %f, uF3: %f", uF2, uF3);
            }
        } else {
            uF2 = 1000.0f;
            uF3 = 1000.0f;
        }

        return true;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("SpecialLookAtModifier: readType=%d (%x), type: %d (%x), b2: %d (%x), b3: %d (%x), b4: %d (%x)",
                    readType, readType, type, type, b2, b2, b3, b3, b4, b4)).append(System.lineSeparator());
            sb.append(String.format("zCoord: %f", zCoord)).append(System.lineSeparator());
            sb.append(String.format("gaoID: %d (%x)", gaoID, gaoID)).append(System.lineSeparator());
            sb.append(String.format("GAO Key: %x", gaoKey)).append(System.lineSeparator());
            if (readType != 4) {
                sb.append(String.format("uF2: %f, uF3: %f", uF2, uF3)).append(System.lineSeparator());
            }
            return sb.toString();
        } else {
            return "SpecialLookAtModifier";
        }
    }

}
