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
import Common.ReaderUtil;
import java.util.LinkedList;

/**
 *
 * @author andras
 */
public class BFSoundBankChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFSoundBankChunk() {
        super();
        smdKeys = new LinkedList<>();
    }

    private LinkedList<Integer> smdKeys;

    public Boolean parseAudio(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        Long len = initParse();
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Audio Chunk length/num: " + len);
        }

        int num;

        if (insideBinFile) {
            num = ru.readInt(false);
        } else {
            num = (int) (len / 4);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Audio Chunk num: " + num);
        }

        if (num == 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Number is 0, returning.");
            }
            return true;
        }

        Integer key;
        String type;

        int i = 0;
        while (true) {
            if (i >= num) {
                break;
            }
            key = ru.readInt(false);
            type = ru.readNString(4, false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Audio key: %x, type: %s", key, type);
            }
            if (key == 0 || key == -1) {
                i++;
                continue;
            }
            if (type.equals(".smd")) {
                if (!bfdfc.getLoadedSoundsClass().hasKey(key)) {
                    bfdfc.getLoadedSoundsClass().addKey(key);
                    if (!parseSMD(key, bfdfc, insideBinFile, buildFromParsed)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Sound modifier parse failed.");
                        }
                        return false;
                    }
                }
            } else {
                // TODO: check LoadedSoundsClass
            }
            i++;
        }

        return true;
    }

    public Boolean parseSNK(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        Long len = initParse();

        long startPos = ru.getCurrentPos();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("SNK Chunk length: " + len);
        }

        Integer key, unk1;

        Integer defaultLangID = 0, selectedLangID = 0;
        Integer langIDSet = IrisToolDefines.getLanguageID();

        int i = 0;

        while ((ru.getCurrentPos() - startPos) < len) {
            key = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Sound key: %x", key);
            }
            if (key != 0 && key != -1) {
                if (defaultLangID == 0) {
                    defaultLangID = key;
                }
                if (i == langIDSet) {
                    selectedLangID = key;
                }
            }
            if (!insideBinFile) {
                unk1 = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("unk1: %x", unk1);
                }
            }
            i++;
        }

        if (selectedLangID == 0) {
            selectedLangID = defaultLangID;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Lang key set: %x", selectedLangID);
        }

        // TODO: set position if not in binfile
        return parseAudio(bfdfc, insideBinFile, buildFromParsed);
    }

    public Boolean parseSMD(Integer smdKey, BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (smdKeys.contains(smdKey)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Already parsed key, ignoring.");
            }
            return true;
        }
        smdKeys.add(smdKey);
        long len = initParse();
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("SMD (sound modifier) Chunk length: " + len);
        }

        ReaderUtil smdru = new ReaderUtil(ru.readByteArray((int) len, false));

        Integer magic = smdru.readInt(false);
        if (magic != 1) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("SMD magic is not 1: %d", magic);
            }
            return false;
        }

        Integer type, dlen, key, flags;
        Long currpos;
        Integer unk = smdru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk: %x", unk);
        }
        while (true) {
            currpos = smdru.getCurrentPos();
            type = smdru.readInt(false);
            if (type == -1) {
                break;
            }
            dlen = smdru.readInt(false);
            key = smdru.readInt(false);
            flags = smdru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("SMD Modifier type: %d, data len: %d, key: %x, flags: %x, other modifier type specific data omitted.", type, dlen, key, flags);
            }
            smdru.seek((currpos + dlen + 8));
        }
        smdru.close();

        return true;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        // This is different, than other chunks. We are not calling here initParse().
        long startpos = ru.getCurrentPos();

        int len;

        if (!insideBinFile) {
            len = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Chunk length: " + len);
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Sound Bank Chunk.");
        }

        String type = ru.readNString(4, false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Type: " + type);
            log("Language ID (in IrisTool): " + IrisToolDefines.getLanguageID());
        }

        if (type.equals(".snk")) {
            return parseSNK(bfdfc, insideBinFile, buildFromParsed);
        } else {
            return parseAudio(bfdfc, insideBinFile, buildFromParsed);
        }
    }

}
