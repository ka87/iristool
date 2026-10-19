/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.DataType.CollisionInstanceElem;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFCollisionInstanceCollisionSetChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private CollisionInstanceElem cie[];
    private Byte[] collisionInstanceOrderingIDsUsedForActivationCheck;
    Byte num;
    Byte unkB1;
    Short snb1;

    public BFCollisionInstanceCollisionSetChunk() {
        super();
    }

    public CollisionInstanceElem[] getCie() {
        return cie;
    }

    public Byte[] getCollisionInstanceOrderingIDsUsedForActivationCheck() {
        return collisionInstanceOrderingIDsUsedForActivationCheck;
    }

    public Byte getNum() {
        return num;
    }

    public Byte getUnkB1() {
        return unkB1;
    }

    public Short getSnb1() {
        return snb1;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Collision Instance Collision Set Chunk.");
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
            log("Collision Instance Collision Set Chunk length: " + len + " bytes.");
        }

        int i;

        num = ru.readByte(false);
        unkB1 = ru.readByte(false);

        if (!insideBinFile) {
            snb1 = ru.readShort(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("s1: %d (%x)", snb1, snb1);
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("num: %d, unkB1: %d (%x)", num, unkB1, unkB1);
        }

        collisionInstanceOrderingIDsUsedForActivationCheck = new Byte[16];

        for (i = 0; i < 16; i++) {
            collisionInstanceOrderingIDsUsedForActivationCheck[i] = ru.readByte(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("collisionInstanceOrderingIDsUsedForActivationCheck[%d]: %d (%x)", i, collisionInstanceOrderingIDsUsedForActivationCheck[i], collisionInstanceOrderingIDsUsedForActivationCheck[i]);
            }
        }

        cie = new CollisionInstanceElem[num];

        for (i = 0; i < num; i++) {
            cie[i] = new CollisionInstanceElem();
            cie[i].readValues(ru);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF(" %d/%d: %s", i + 1, num, cie[i]);
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
                cie[i].setUbnb1(ru.readByte(false));
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("ubnb1[%d](!insidebeinfile): %d (%x)", i, cie[i].getUbnb1(), cie[i].getUbnb1());
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
            return String.format("CollisionInstanceCollisionSetChunk: num: %d (%x), unkB1: %d (%x), snb1: %d (%x)", num, num, unkB1, unkB1, snb1, snb1);
        } else {
            return "CollisionInstanceCollisionSetChunk";
        }
    }
}
