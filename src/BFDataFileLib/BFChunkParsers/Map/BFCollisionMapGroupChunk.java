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
public class BFCollisionMapGroupChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Long len = 0L;
    private Byte collKeyNum, activationBitmask, b2, b3;
    private ArrayList<Integer> collKeys;
    private ArrayList<BFKeyListElem> collisionObjectChunksKLE;

    public BFCollisionMapGroupChunk() {
        super();
        collKeys = new ArrayList<>();
        collisionObjectChunksKLE = new ArrayList<>();
    }

    public Byte getCollKeyNum() {
        return collKeyNum;
    }

    public Byte getActivationBitmask() {
        return activationBitmask;
    }

    public ArrayList<Integer> getCollKeys() {
        return collKeys;
    }

    public ArrayList<BFKeyListElem> getCollisionObjectChunksKLE() {
        return collisionObjectChunksKLE;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Collision Map Group Chunk.");
        }

        long startPos = 0L;

        if (!buildFromParsed) {
            collKeys.clear();
            collisionObjectChunksKLE.clear();
            len = initParse();
            startPos = ru.getCurrentPos();
            if (len == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Length read failed.");
                }
                return false;
            }
        } else {
            wu.startBlockWithLength();
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Collision Map Group length: " + len + " bytes.");
        }

        Integer key;
        BFKeyListElem collisionObjectKLE;

        if (len == 4) {
            if (!buildFromParsed) {
                key = ru.readInt(false);
                activationBitmask = (byte) 0xff;
                collKeys.add(key);
                collisionObjectKLE = new BFKeyListElem(key, BFCollisionObjectChunk.class);
                collisionObjectChunksKLE.add(collisionObjectKLE);
            } else {
                key = collKeys.get(0);
                wu.writeInt(key);
                collisionObjectKLE = collisionObjectChunksKLE.get(0);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Collision Map Collision Object Key: %x", key);
            }
            bfdfc.addToKeyList(collisionObjectKLE);
        } else {
            if (!buildFromParsed) {
                collKeyNum = ru.readByte(false);
                activationBitmask = ru.readByte(false);
                b2 = ru.readByte(false);
                b3 = ru.readByte(false);
            } else {
                wu.writeByte(collKeyNum);
                wu.writeByte(activationBitmask);
                wu.writeByte(b2);
                wu.writeByte(b3);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("collKeyNum: %d, activationBitmask: %d (%x), b2: %d (%x), b3: %d (%x)", collKeyNum, activationBitmask, activationBitmask, b2, b2, b3, b3);
            }
            int i;
            for (i = 0; i < collKeyNum; i++) {
                if (!buildFromParsed) {
                    key = ru.readInt(false);
                    collKeys.add(key);
                } else {
                    key = collKeys.get(i);
                    wu.writeInt(key);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Collision Map Collision Object Key [%d]: %x", i, key);
                }
                if (key != -1) {
                    if (!buildFromParsed) {
                        collisionObjectKLE = new BFKeyListElem(key, BFCollisionObjectChunk.class);
                        collisionObjectChunksKLE.add(collisionObjectKLE);
                    } else {
                        collisionObjectKLE = collisionObjectChunksKLE.get(i);
                    }
                    bfdfc.addToKeyList(collisionObjectKLE);
                } else {
                    if (!buildFromParsed) {
                        collisionObjectChunksKLE.add(null);
                    }
                }
            }
        }

        if (!buildFromParsed && insideBinFile && (startPos + len) != ru.getCurrentPos()) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != real length");
            }
            return false;
        }

        if (buildFromParsed) {
            wu.finishBlockWithLength();
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("collKeyNum: %d, activationBitmask: %d (%x), b2: %d (%x), b3: %d (%x)", collKeyNum, activationBitmask, activationBitmask, b2, b2, b3, b3);
    }

}
