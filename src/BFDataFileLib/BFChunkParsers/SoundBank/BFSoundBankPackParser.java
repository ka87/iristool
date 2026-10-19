package BFDataFileLib.BFChunkParsers.SoundBank;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.DataType.WaveFormatInterface;
import Common.DataType.WaveFormatType16;
import Common.DataType.WaveFormatType18;
import Common.DataType.WaveFormatType52;
import Common.IrisToolDefines;
import Common.ReaderUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andras
 */
public class BFSoundBankPackParser extends BFChunkParseClass implements BFChunkParseInterface {

    private int keys[];
    private int flags[];
    private WaveFormatInterface wfi[];

    static public WaveFormatInterface readWaveInfo(ReaderUtil ru, boolean pakReadHD) {

        int riff;
        int chunkSize;
        int wave;
        int fmt;
        int subChunkSize;
        // HD edition extra fields
        int offset1 = 0;
        int len2 = 0;
        int offset2 = 0;

        if (!pakReadHD) {
            riff = ru.readInt(false);
            chunkSize = ru.readInt(false);
            wave = ru.readInt(false);
            fmt = ru.readInt(false);
            subChunkSize = ru.readInt(false);
        } else {
            chunkSize = ru.readInt(false);
            riff = ru.readInt(false);
            offset1 = ru.readInt(false);
            len2 = ru.readInt(false);
            fmt = ru.readInt(false);
            offset2 = ru.readInt(false);
            subChunkSize = ru.readInt(false);
            wave = ByteBuffer.wrap(new byte[]{'W', 'A', 'V', 'E'}).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }
        WaveFormatInterface wfil;

        switch (subChunkSize) {
            case 16: // found in hd edition (ps3)
                wfil = new WaveFormatType16();
                break;
            case 18:
                wfil = new WaveFormatType18();
                break;
            case 52: // found in hd edition (x360)
                wfil = new WaveFormatType52();
                break;
            default:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.format("unhandled: type: %d (%x)\n", subChunkSize, subChunkSize);
                }
                return null;
        }
        wfil.setParseModeHDPak(pakReadHD);
        wfil.readValues(ru, false);

        wfil.setHeaderFirstPart(riff, chunkSize, wave, fmt, subChunkSize, offset1, len2, offset2);

        return wfil;
    }

    public int[] getKeys() {
        return keys;
    }

    public int[] getFlags() {
        return flags;
    }

    public WaveFormatInterface[] getWfis() {
        return wfi;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Sound Bank Pack (FF4).");
        }

        // TODO: suppport !insideBinFile
        int num = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Found " + num + " sounds.");
        }
        if (num > 0) {
            keys = new int[num];
            flags = new int[num];
            wfi = new WaveFormatInterface[num];
            for (int i = 0; i < num; i++) {
                keys[i] = ru.readInt(false);
                flags[i] = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Sound key: %x, Flags: %x", keys[i], flags[i]);
                }
            }
            for (int i = 0; i < num; i++) {
                wfi[i] = readWaveInfo(ru, IrisToolDefines.isHdVersionQuirk());
                if (wfi[i] == null) {
                    return false;
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(wfi[i]);
                }
            }
            for (int i = 0; i < num; i++) {
                int size = wfi[i].getSize();
                if (size > 0 && flags[i] == 0) {
                    wfi[i].readUnkData(ru);
                    wfi[i].setData(ru.readByteArray(size, false));
                }
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing finished: datalen: " + ru.getDataLength() + ", pos: " + ru.getCurrentPos());
        }
        return true;
    }
}
