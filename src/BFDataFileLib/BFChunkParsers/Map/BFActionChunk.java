/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.ActionsElemStruct;
import Common.DataType.ActionsElemTransitionStruct;
import Common.IrisToolDefines;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class BFActionChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private ArrayList<ActionsElemStruct> aesList;
    private ArrayList<BFKeyListElem> animationChunksKLE;
    private ArrayList<BFKeyListElem> eventsChunksKLE;
    private Byte num = 0;
    private Byte loopStartElem = 0;
    private Short unkshort1nb = 0;
    private Long len = 0L;

    public BFActionChunk() {
        super();
        aesList = new ArrayList<>();
        animationChunksKLE = new ArrayList<>();
        eventsChunksKLE = new ArrayList<>();
    }

    public ArrayList<ActionsElemStruct> getAesList() {
        return aesList;
    }

    public ArrayList<BFKeyListElem> getAnimationChunksKLE() {
        return animationChunksKLE;
    }

    public ArrayList<BFKeyListElem> getEventsChunksKLE() {
        return eventsChunksKLE;
    }

    public Byte getLoopStartElem() {
        return loopStartElem;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Action Chunk.");
        }

        long startpos = 0;

        if (!buildFromParsed) {
            aesList.clear();
            animationChunksKLE.clear();
            eventsChunksKLE.clear();
            len = initParse();

            startpos = ru.getCurrentPos();
            if (len == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Length read failed.");
                }
                return false;
            }

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length is " + len + " bytes.");
            }

        } else {
            wu.startBlockWithLength();
        }

        if (len == 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Empty action, finished parsing.");
            }
            return true;
        }

        if (!buildFromParsed) {
            num = ru.readByte(false);
        } else {
            wu.writeByte(num);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Action num: " + num);
        }

        int i;

        if (num > 0) {
            if (!buildFromParsed) {
                loopStartElem = ru.readByte(false);
            } else {
                wu.writeByte(loopStartElem);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("loopStartElem: %d (%x)", loopStartElem, loopStartElem);
            }
            if (!insideBinFile) {
                if (!buildFromParsed) {
                    unkshort1nb = ru.readShort(false);
                } else {
                    wu.writeShort(unkshort1nb);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Unkshort1nb: %d (%x)", unkshort1nb, unkshort1nb);
                }
            }
            for (i = 0; i < num; i++) {
                ActionsElemStruct aes = new ActionsElemStruct();
                if (!buildFromParsed && !aes.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("ActionsElemStruct read failed.");
                    }
                    return false;
                } else if (buildFromParsed) {
                    aes.writeValues(wu);
                }

                aesList.add(aes);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(aes);
                }

                if (aes.getEventsKey() != -1) {
                    BFKeyListElem bfklee;
                    if (!buildFromParsed) {
                        bfklee = new BFKeyListElem(aes.getEventsKey(), BFEventsChunk.class);
                        eventsChunksKLE.add(bfklee);
                    } else {
                        bfklee = eventsChunksKLE.get(i);
                    }
                    bfdfc.addToKeyList(bfklee);
                } else {
                    if (!buildFromParsed) {
                        eventsChunksKLE.add(null);
                    }
                }
                if (aes.getAnimationShapeKey() != -1) {
                    BFKeyListElem bfklea;
                    if (!buildFromParsed) {
                        bfklea = new BFKeyListElem(aes.getAnimationShapeKey(), BFAnimationShapeChunk.class);
                        animationChunksKLE.add(bfklea);
                    } else {
                        bfklea = animationChunksKLE.get(i);
                    }
                    bfdfc.addToKeyList(bfklea);
                }
            }

            for (i = 0; i < num; i++) {
                if (!aesList.get(i).hasTransition()) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Unk1 is 0 for Action " + i + ", ignoring read action transition data.");
                    }
                    aesList.get(i).setATS(null);
                    continue;
                }
                ActionsElemTransitionStruct ats;
                if (!buildFromParsed) {
                    ats = new ActionsElemTransitionStruct();
                    if (!ats.readValues(ru, insideBinFile)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("ActionsElemTransitionStruct read failed.");
                        }
                        return false;
                    }
                    aesList.get(i).setATS(ats);
                } else {
                    ats = aesList.get(i).getATS();
                    ats.writeValues(wu, insideBinFile);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(ats);
                }
            }
        }

        if (!buildFromParsed && ((ru.getCurrentPos() - startpos) != len)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Parsed length != length: currrentpos: %x", ru.getCurrentPos());
            }
            return false;
        }

        if (buildFromParsed) {
            wu.finishBlockWithLength();
        }

        return true;
    }
}
