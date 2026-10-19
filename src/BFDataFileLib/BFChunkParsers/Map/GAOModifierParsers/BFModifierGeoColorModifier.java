/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.Convert;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierGeoColorModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    private Integer unk2Arr[];
    private Integer colorIDListNum;
    private byte colorIDs[];
    private Byte b1, type, b3, b4;
    private Integer unk1;

    public BFModifierGeoColorModifier() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Geo Color Modifier (Type 23)");
        }

        if (!insideBinFile) {
            if (!buildFromParsed) {
                unk1 = ru.readInt(false);
            } else {
                wu.writeInt(unk1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1 (!insidebinfile): %d (%x)", unk1, unk1);
            }
        }

        int i;

        if (!buildFromParsed) {
            unk2Arr = new Integer[64];
        }

        for (i = 0; i < 64; i++) {
            if (!buildFromParsed) {
                unk2Arr[i] = ru.readInt(false);
            } else {
                wu.writeInt(unk2Arr[i]);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk2Arr[%d/64]: %d (%x)", (i + 1), unk2Arr[i], unk2Arr[i]);
            }
        }

        if (!buildFromParsed) {
            b1 = ru.readByte(false);
            type = ru.readByte(false);
            b3 = ru.readByte(false);
            b4 = ru.readByte(false);
        } else {
            wu.writeByte(b1);
            wu.writeByte(type);
            wu.writeByte(b3);
            wu.writeByte(b4);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("b1: %d (%x), type: %d (%x), b1: %d (%x), b1: %d (%x)", b1, b1, type, type, b3, b3, b4, b4);
        }

        if (!buildFromParsed) {
            colorIDListNum = ru.readInt(false);
        } else {
            wu.writeInt(colorIDListNum);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Color ID List length: %d", colorIDListNum);
        }

        if (colorIDListNum != 0) {
            if (!buildFromParsed) {
                colorIDs = ru.readByteArray(colorIDListNum, false);
            } else {
                wu.writeByteArray(colorIDs);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("ID list: %s", Convert.bytesToHex(colorIDs));
            }
        }

        return true;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("GeoColorModifier: unk1=%x", unk1)).append(System.lineSeparator());
            sb.append("unk2Arr: ");
            for (int i = 0; i < 64; i++) {
                if ((i % 32) == 0 && i != 0) {
                    sb.append(System.lineSeparator());
                }
                sb.append(String.format("%d (%x),", unk2Arr[i], unk2Arr[i]));
            }
            sb.append(System.lineSeparator());
            sb.append(String.format("b1: %d (%x), type: %d (%x), b1: %d (%x), b1: %d (%x)", b1, b1, type, type, b3, b3, b4, b4)).append(System.lineSeparator());
            sb.append(String.format("Color ID List length: %d", colorIDListNum)).append(System.lineSeparator());
            sb.append("ID list: ");
            for (int i = 0; i < colorIDListNum; i++) {
                if ((i % 30) == 0 && i != 0) {
                    sb.append(System.lineSeparator());
                }
                sb.append(String.format("%d (%x),", colorIDs[i], colorIDs[i]));
            }
            return sb.toString();
        } else {
            return "GeoColorModifier";
        }
    }

}
