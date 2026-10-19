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
import Common.DataType.CollisionMapBox;
import Common.DataType.CollisionMapBoxTriangleChunkElem;
import Common.DataType.CollisionTriangleChunk;
import Common.DataType.CollisionTriangleChunkElem;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFCollisionObjectChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Integer gameMaterialKey;
    private BFKeyListElem gameMaterialKLE;
    private Byte type;
    private Byte flags;
    private Vector3 pos1;
    private Vector3 pos2;
    private Float f1, f2;
    private Integer defaultGameMaterialID;
    private CollisionTriangleChunk ctc[];
    private Integer collisionTringleChunkNum;
    private Integer collisionVerticesNum;
    Integer proximityVerticesNum;
    private Integer triangleChunkNum;
    private Short triangleIDElemNum, triangleChunkID, triangleElemID;
    private Integer boxesNum;
    private CollisionMapBox[] collsisionBoxes;
    private Vector3[] collisionVertices;
    private Vector3[] proximityArr;

    public BFCollisionObjectChunk() {
        super();
    }

    public CollisionMapBox[] getCollsisionBoxes() {
        return collsisionBoxes;
    }

    public Byte getType() {
        return type;
    }

    public Vector3 getPos1() {
        return pos1;
    }

    public Vector3 getPos2() {
        return pos2;
    }

    public Float getF1() {
        return f1;
    }

    public Float getF2() {
        return f2;
    }

    public CollisionTriangleChunk[] getCtc() {
        return ctc;
    }

    public Byte getFlags() {
        return flags;
    }

    public Integer getDefaultGameMaterialID() {
        return defaultGameMaterialID;
    }

    public Integer getCollisionTringleChunkNum() {
        return collisionTringleChunkNum;
    }

    public Integer getCollisionVerticesNum() {
        return collisionVerticesNum;
    }

    public Integer getProximityVerticesNum() {
        return proximityVerticesNum;
    }

    public Integer getBoxesNum() {
        return boxesNum;
    }

    public Integer getGameMaterialKey() {
        return gameMaterialKey;
    }

    public BFKeyListElem getGameMaterialKLE() {
        return gameMaterialKLE;
    }

    public Vector3[] getCollisionVertices() {
        return collisionVertices;
    }

    public Vector3[] getProximityArr() {
        return proximityArr;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Collision Object Chunk.");
        }

        Long len = initParse();

        long startPos = ru.getCurrentPos();

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Collision Object length: " + len + " bytes.");
        }

        gameMaterialKey = ru.readInt(false);
        type = ru.readByte(false);
        flags = ru.readByte(false); // 0x1, collision object has game material
        Byte flagsCheck = (byte) (flags & 0xfe);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Collision Object Game Material Key: %x", gameMaterialKey);
            logF("type: %d (%x, %s), flags: %d (%x), flags & 0xFE: %d (%x)", type, type,
                    getTypeDescription(type), flags, flags, flagsCheck, flagsCheck);
        }

        int i, j, k;

        if (type == 1 || type == 2 || type == 3 || type == 5) {
            if (gameMaterialKey != -1) {
                bfdfc.addToKeyList(gameMaterialKLE = new BFKeyListElem(gameMaterialKey, BFCollisionObjectGameMaterialChunk.class));
                flagsCheck = (byte) (flagsCheck | 0x1);
            }
            if (type <= 3) {
                defaultGameMaterialID = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Default GameMaterial ID [for type 1-3]: %x (%d)", defaultGameMaterialID, defaultGameMaterialID);
                }
            }
            switch (type) {
                case 1:
                    pos1 = new Vector3();
                    if (!pos1.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Vector3 read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(pos1);
                    }
                    pos2 = new Vector3();
                    if (!pos2.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Vector3 read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(pos2);
                    }
                    break;
                case 2:
                    pos1 = new Vector3();
                    if (!pos1.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Vector3 read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(pos1);
                    }
                    f1 = ru.readFloat(false);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("f1: %f", f1);
                    }
                    break;
                case 3:
                    pos1 = new Vector3();
                    if (!pos1.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Vector3 read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(pos1);
                    }
                    f1 = ru.readFloat(false);
                    f2 = ru.readFloat(false);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("f1: %f, f2: %f", f1, f2);
                    }
                    break;
                case 5:
                    collisionVerticesNum = ru.readInt(false);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("Collision vertices num: %d", collisionVerticesNum);
                    }
                    if (collisionVerticesNum > 0) {
                        collisionVertices = new Vector3[collisionVerticesNum];
                    }
                    for (i = 0; i < collisionVerticesNum; i++) {
                        collisionVertices[i] = new Vector3();
                        if (!collisionVertices[i].readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("Vector3 read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(collisionVertices[i]);
                        }
                    }
                    proximityVerticesNum = ru.readInt(false);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("Proximity vertices num: %d", proximityVerticesNum);
                    }
                    if (proximityVerticesNum > 0) {
                        proximityArr = new Vector3[proximityVerticesNum];
                    }
                    for (i = 0; i < proximityVerticesNum; i++) {
                        proximityArr[i] = new Vector3();
                        if (!proximityArr[i].readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("Vector3 read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(proximityArr[i]);
                        }
                    }
                    collisionTringleChunkNum = ru.readInt(false);
                    ctc = new CollisionTriangleChunk[collisionTringleChunkNum];

                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("Collision triangle chunk num: %d", collisionTringleChunkNum);
                    }
                    for (i = 0; i < collisionTringleChunkNum; i++) {
                        ctc[i] = new CollisionTriangleChunk();
                        if (!ctc[i].readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("CollisionTriangleChunk read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(ctc[i]);
                            logF("Collision triangle chunk elems[%d]: %d", i, ctc[i].getTriangleElemNum());
                        }
                        if (ctc[i].getTriangleElemNum() != 0) {
                            for (j = 0; j < ctc[i].getTriangleElemNum(); j++) {
                                ctc[i].getCtce()[j] = new CollisionTriangleChunkElem();
                                ctc[i].getCtce()[j].readVertexIDValues(ru);
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    logF(" %d/%d %s", j + 1, ctc[i].getTriangleElemNum(), ctc[i].getCtce()[j].toString(true, false));
                                }
                            }
                        }
                    }
                    if (insideBinFile && (flagsCheck & 0x80) != 0) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Flags has 0x80 and insidebinfile, doing proximity indices read.");
                        }
                        for (i = 0; i < collisionTringleChunkNum; i++) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                logF("Collision proximity chunk elems(insidebinfile)[%d]: %d", i, ctc[i].getTriangleElemNum());
                            }
                            if (ctc[i].getTriangleElemNum() != 0) {
                                for (j = 0; j < ctc[i].getTriangleElemNum(); j++) {
                                    // ctce allocation has been done in prev for loop
                                    ctc[i].getCtce()[j].readProximityIDValues(ru);
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        logF(" %d/%d %s", j + 1, ctc[i].getTriangleElemNum(), ctc[i].getCtce()[j].toString(false, true));
                                    }
                                }
                            }
                        }
                    }
                    /* Collision vertices are grouped together in an AABox
                       if game is in bin mode (binarized read), and collision
                       has this type of grouping, it will make precedence of
                       standard collision map information (read just earlier)
                       on calc
                     */

                    if (insideBinFile && (flagsCheck & 0x88) == 0x88) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Flags has 0x88 and insidebinfile, doing box-grouped Collision information read.");
                        }
                        boxesNum = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("BoxesNum: " + boxesNum);
                        }

                        if (boxesNum > 0) {
                            collsisionBoxes = new CollisionMapBox[boxesNum];
                        }

                        for (i = 0; i < boxesNum; i++) {
                            triangleChunkNum = ru.readInt(false);
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                logF(" triangleChunkNum[%d]: %d", i, triangleChunkNum);
                            }
                            pos1 = new Vector3();
                            if (!pos1.readValues(ru)) {
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    log("Vector3 read failed.");
                                }
                                return false;
                            }
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("AABoxVec1: " + pos1);
                            }
                            pos2 = new Vector3();
                            if (!pos2.readValues(ru)) {
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    log("Vector3 read failed.");
                                }
                                return false;
                            }
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("AABoxVec2: " + pos2);
                            }
                            collsisionBoxes[i] = new CollisionMapBox();
                            collsisionBoxes[i].getAaBoxVec1().set(pos1);
                            collsisionBoxes[i].getAaBoxVec2().set(pos2);
                            collsisionBoxes[i].setTriangleChunkNum(triangleChunkNum);

                            /*
                            To get collision geometry for CollisionMapBox:
                            ----
                            Walk through trianglechunks, select triangleChunkID from CollisionTriangleChunks (ctc),
                            then walk through triangleElemIDs (collsisionBoxes[i].getCollisionMapBoxTriangleChunks()[j].getTriangleElemIDs()),
                            and select actual triangleElemID'th CollisionTriangleChunkElem from ctc[i].getCtce().                                                       
                             */
                            for (j = 0; j < triangleChunkNum; j++) {
                                triangleChunkID = ru.readShort(false);
                                triangleIDElemNum = ru.readShort(false);
                                collsisionBoxes[i].getCollisionMapBoxTriangleChunks()[j] = new CollisionMapBoxTriangleChunkElem(triangleChunkID, triangleIDElemNum);
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    logF("  reference triangleChunkID: %d (%x), triangleIDElemNum[%d]: %d",
                                            j, triangleChunkID, triangleChunkID, triangleIDElemNum);
                                }
                                for (k = 0; k < triangleIDElemNum; k++) {
                                    triangleElemID = ru.readShort(false);
                                    collsisionBoxes[i].getCollisionMapBoxTriangleChunks()[j].getTriangleElemIDs()[k] = triangleElemID;
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        logF("   triangleElemID: %d (%x)", k, triangleElemID, triangleElemID);
                                    }
                                }
                            }
                        }
                    }
                    break;
            }

        }

        if (insideBinFile && (startPos + len) != ru.getCurrentPos()) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != real length");
            }
            return false;
        }

        return true;
    }

    private String getTypeDescription(int t) {
        switch (t) {
            case 1:
                return "box";
            case 2:
                return "sphere";
            case 3:
                return "cylinder";
            case 5:
                return "geometry";
            default:
                return "unknown";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("CollisionObjectChunk: GameMaterial Key: %x", gameMaterialKey));
        sb.append(String.format(", type: %d (%x, %s), flags: %d (%x)", type, type, getTypeDescription(type), flags, flags));
        if (type > 0 && type <= 3) {
            sb.append(String.format(", Default GameMaterial ID: %x (%d)", defaultGameMaterialID, defaultGameMaterialID));
        }
        return sb.toString();
    }

}
