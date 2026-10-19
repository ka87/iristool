package BFDataFileLib.BFChunkParsers.SoundBank;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.DataType.WaveFormatInterface;
import Common.IrisToolDefines;
import Common.ReaderUtil;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andras
 */
public class BFSoundBankHeaderCollectionParser extends BFChunkParseClass implements BFChunkParseInterface {

    private int[] defaultSoundKeys;
    private WaveFormatInterface wfi[];
    private int[] activeLanguageSpecificSoundKeys;

    public int[] getDefaultSoundKeys() {
        return defaultSoundKeys;
    }

    public WaveFormatInterface[] getWfis() {
        return wfi;
    }

    public int[] getActiveLanguageSpecificSoundKeys() {
        return activeLanguageSpecificSoundKeys;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        long len = initParse();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Sound Bank Header Collection (Language specific).");

            log("Data length: " + len + " bytes.");
        }
        int num;
        if (!IrisToolDefines.isHdVersionQuirk()) {
            num = (int) (len / 54); // (int[4] + wave header[64] + int[4])
        } else {
            num = (int) (len / 8); // (int[4] + int[4])
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Found " + num + " sounds.");
        }

        if (num > 0) {
            defaultSoundKeys = new int[num];
            wfi = new WaveFormatInterface[num];
            activeLanguageSpecificSoundKeys = new int[num];
            byte[] waveData;
            ReaderUtil ruWave;
            for (int i = 0; i < num; i++) {
                defaultSoundKeys[i] = ru.readInt(false);
                if (!IrisToolDefines.isHdVersionQuirk()) {
                    wfi[i] = BFSoundBankPackParser.readWaveInfo(ru, false);
                }
                activeLanguageSpecificSoundKeys[i] = ru.readInt(false);
                if (IrisToolDefines.isHdVersionQuirk()) {
                    waveData = bfdfc.getDataFromBFbyKey(activeLanguageSpecificSoundKeys[i]);
                    ruWave = new ReaderUtil(waveData);
                    wfi[i] = BFSoundBankPackParser.readWaveInfo(ruWave, false);
                    ruWave.close();
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("%d. Default sound key: %x, active language specific sound key: %x, %s", (i + 1), defaultSoundKeys[i], activeLanguageSpecificSoundKeys[i], wfi[i]);
                }
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing finished: datalen: " + ru.getDataLength() + ", pos: " + ru.getCurrentPos());
        }

        if (ru.getDataLength() != ru.getCurrentPos()) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("data length != current pos");
            }
            return false;
        }

        return true;
    }
}
