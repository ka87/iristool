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
import Common.DataType.CollisionInstanceElem;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFCollisionInstanceChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Long len;
    private CollisionInstanceElem cie[];
    private Byte sumNum;
    private Byte num1;
    private Byte num2;
    private Byte collPrio;
    private Byte[] collisionInstanceOrderingIDs;
    private Short activationBitmask, collElemsOrCollSetElemsBitmask;
    private Short unksnb1;
    private Short crossable, unks4;
    private Byte ub4, ub5;
    private BFKeyListElem collisionSetChunkKLE;
    private Integer key;

    public BFCollisionInstanceChunk() {
        super();
    }

    public CollisionInstanceElem[] getCie() {
        return cie;
    }

    public Byte getSumNum() {
        return sumNum;
    }

    public Byte getNum1() {
        return num1;
    }

    public Byte getNum2() {
        return num2;
    }

    public Byte getCollPrio() {
        return collPrio;
    }

    public Byte[] getCollisionInstanceOrderingIDs() {
        return collisionInstanceOrderingIDs;
    }

    public Short getActivationBitmask() {
        return activationBitmask;
    }

    public Short getCollElemsOrCollSetElemsBitmask() {
        return collElemsOrCollSetElemsBitmask;
    }

    public Short getUnksnb1() {
        return unksnb1;
    }

    public Short getCrossable() {
        return crossable;
    }

    public Short getUnks4() {
        return unks4;
    }

    public Byte getUb4() {
        return ub4;
    }

    public Byte getUb5() {
        return ub5;
    }

    public BFKeyListElem getCollisionSetChunkKLE() {
        return collisionSetChunkKLE;
    }

    public Integer getKey() {
        return key;
    }

    public Long getLen() {
        return len;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Collision Instance Chunk.");
        }

        len = initParse();

        long startPos = ru.getCurrentPos();

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Collision Instance length: " + len + " bytes.");
        }

        key = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Collision Instance Collision Set Key: %x", key);
        }
        if (key != -1) {
            bfdfc.addToKeyList(collisionSetChunkKLE = new BFKeyListElem(key, BFCollisionInstanceCollisionSetChunk.class));
        }

        if (len != 4) {
            sumNum = ru.readByte(false);
            num1 = ru.readByte(false);
            num2 = ru.readByte(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("num1: %d (%x), num2: %d (%x), sumNum: %d (%x)", num1, num1, num2, num2, sumNum, sumNum);
            }
            if ((num1 + num2) != sumNum) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("sumNum != num1 + num2, returning.");
                }
                return true;
            }

            int i;

            collPrio = ru.readByte(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Collision Priority: %d (%x)", collPrio, collPrio);
            }

            if (sumNum > 0) {
                collisionInstanceOrderingIDs = new Byte[sumNum];
            }

            /*
            collisionInstanceOrderingIDs: game resolves this to Collision Instance Elem by ID from 
            "embedded collision elems" and "collision set elems" by collElemsOrCollSetElemsBitmask:
            collElemsOrCollSetElemsBitmask[i]:
                0: collision set elems
                1: embedded collision elems
            Game uses collisionInstanceOrderingIDsUsedForActivationCheck from "BFCollisionInstanceCollisionSetChunk" 
            for activationBitmask check. (BUG?)
             */
            for (i = 0; i < sumNum; i++) {
                collisionInstanceOrderingIDs[i] = ru.readByte(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("collisionInstanceOrderingIDs[%d]: %d (%x)", i, collisionInstanceOrderingIDs[i], collisionInstanceOrderingIDs[i]);
                }
            }

            cie = new CollisionInstanceElem[num2];

            for (i = 0; i < num2; i++) {
                cie[i] = new CollisionInstanceElem();
                cie[i].readValues(ru);

                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF(" %d/%d: %s", i + 1, num2, cie[i]);
                }

                if (!insideBinFile) {
                    cie[i].setUbnb1(ru.readByte(false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("ubnb1[%d](!insidebeinfile): %d (%x)", i, cie[i].getUbnb1(), cie[i].getUbnb1());
                    }
                }

                cie[i].setuLen(ru.readInt(false));
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("uLen[%d]: %d (%x)", i, cie[i].getuLen(), cie[i].getuLen());
                }

                if (!insideBinFile) {
                    cie[i].setUs1(ru.readNString(cie[i].getuLen(), false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("us1[%d](!insidebeinfile): %s", i, cie[i].getUs1());
                    }
                    cie[i].setUbnb2(ru.readByte(false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("ubnb2[%d](!insidebeinfile): %d (%x)", i, cie[i].getUbnb2(), cie[i].getUbnb2());
                    }
                }

                switch (cie[i].getType()) {
                    case 1:
                        cie[i].setPos1(new Vector3());
                        if (!cie[i].getPos1().readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("Vector3 read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(cie[i].getPos1());
                        }
                        cie[i].setPos2(new Vector3());
                        if (!cie[i].getPos2().readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("Vector3 read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(cie[i].getPos2());
                        }
                        break;
                    case 2:
                        cie[i].setPos1(new Vector3());
                        if (!cie[i].getPos1().readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("Vector3 read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(cie[i].getPos1());
                        }
                        cie[i].setF1(ru.readFloat(false));
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("f1: %f", cie[i].getF1());
                        }
                        break;
                    case 3:
                        cie[i].setPos1(new Vector3());
                        if (!cie[i].getPos1().readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("Vector3 read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(cie[i].getPos1());
                        }
                        cie[i].setF1(ru.readFloat(false));
                        cie[i].setF2(ru.readFloat(false));
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("f1: %f, f2: %f", cie[i].getF1(), cie[i].getF2());
                        }
                        break;
                }
            }

            activationBitmask = ru.readShort(false);
            collElemsOrCollSetElemsBitmask = ru.readShort(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("activationBitmask: %d (%x), collElemsOrCollSetElemsBitmask: %d (%x)", activationBitmask, activationBitmask, collElemsOrCollSetElemsBitmask, collElemsOrCollSetElemsBitmask);
            }
            if (!insideBinFile) {
                unksnb1 = ru.readShort(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("unksnb1(!insidebinfile): %d (%x)", unksnb1, unksnb1);
                }
            }
            crossable = ru.readShort(false);
            unks4 = ru.readShort(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("crossable (game ignores it, and defaults to 0x589a): %d (%x), unks4: %d (%x)", crossable, crossable, unks4, unks4);
            }
            ub4 = ru.readByte(false);
            ub5 = ru.readByte(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("ub4: %d (%x), ub5: %d (%x)", ub4, ub4, ub5, ub5);
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

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("CollisionInstanceChunk: Collision Instance Collision Set Key: %x", key));
            if (len != 4) {
                sb.append(String.format(", num1: %d (%x), num2: %d (%x), sumNum: %d (%x), collPrio: %d (%x), activationBitmask: %d (%x), collElemsOrCollSetElemsBitmask: %d (%x), crossable: %d (%x), unks4: %d (%x), ub4: %d (%x), ub5: %d (%x)",
                        num1, num1, num2, num2, sumNum, sumNum, collPrio, collPrio, activationBitmask, activationBitmask, collElemsOrCollSetElemsBitmask,
                        collElemsOrCollSetElemsBitmask, crossable, crossable, unks4, unks4, ub4, ub4, ub5, ub5));
            }
            return sb.toString();
        } else {
            return "CollisionInstanceChunk";
        }
    }
}
