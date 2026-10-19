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
import Common.IrisToolDefines;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class BFActionListChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private ArrayList<Integer> actionKeys;
    private ArrayList<BFKeyListElem> actionsKLE;
    private ArrayList<Integer> unk1s, unk2s;
    private Long actionListLen = 0L;

    public BFActionListChunk() {
        super();
        actionKeys = new ArrayList<>();
        actionsKLE = new ArrayList<>();
        unk1s = new ArrayList<>();
        unk2s = new ArrayList<>();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing GAO Action List Chunk.");
        }

        if (!buildFromParsed) {
            actionKeys.clear();
            actionsKLE.clear();
            unk1s.clear();
            unk2s.clear();
            actionListLen = initParse();
        } else {
            wu.startBlockWithLength();
        }

        if (actionListLen == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Action List length: " + actionListLen + " bytes.");
        }

        if (actionListLen % 4 != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Invalid length.");
            }
            return false;
        }

        int num;
        if (!buildFromParsed) {
            num = (int) (actionListLen / 4);
        } else {
            num = actionKeys.size();
        }

        int cnt = 0;
        int i = 0;

        while (num-- > 0) {
            int key;
            if (!buildFromParsed) {
                key = ru.readInt(false);
                actionKeys.add(key);
            } else {
                key = actionKeys.get(actionKeys.size() - num + 1);
            }
            if (!insideBinFile) {
                Integer unk1;
                if (!buildFromParsed) {
                    unk1 = ru.readInt(false);
                    unk1s.add(unk1);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("unk1(!insidebinfile): %x", unk1);
                    }
                } else {
                    wu.writeInt(unk1s.get(unk1s.size() - num + 1));
                }
            }
            if (key == 1) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Found Actions key 1, decreasing num.", key);
                }
                if (!buildFromParsed) {
                    unk2s.add(null);
                    actionsKLE.add(null);
                }
                num--;
                continue;
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Found Actions key: %x", key);
            }
            Integer unk2;
            if (!insideBinFile) {
                if (!buildFromParsed) {
                    unk2 = ru.readInt(false);
                    unk2s.add(unk2);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("unk2(!insidebinfile): %x", unk2);
                    }
                } else {
                    wu.writeInt(unk2s.get(unk2s.size() - num + 1));
                }
            }
            if (key != 0 && key != -1) {
                BFKeyListElem bfklea;
                if (!buildFromParsed) {
                    bfklea = new BFKeyListElem(key, BFActionChunk.class);
                    actionsKLE.add(bfklea);
                } else {
                    bfklea = actionsKLE.get(actionsKLE.size() - num + 1);
                }
                bfdfc.addToKeyList(bfklea);
                cnt++;
            } else {
                if (!buildFromParsed) {
                    actionsKLE.add(null);
                }
            }
        }

        if (buildFromParsed) {
            wu.finishBlockWithLength();
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Added " + cnt + " actions to key list.");
        }

        return true;
    }

    public ArrayList<Integer> getActionKeys() {
        return actionKeys;
    }

    public ArrayList<BFKeyListElem> getActionsKLE() {
        return actionsKLE;
    }
}
