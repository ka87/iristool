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

/**
 *
 * @author andras
 */
public class BFCollisionObjectGameMaterialChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Long len = 0L;
    private long startPos;
    private Integer num;
    private Integer gmatIds[];
    private Integer crossableFlags[];
    private Float slide[], rebound[];
    private Byte soundID[], unkB2_2[];
    private Short id[];

    public BFCollisionObjectGameMaterialChunk() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing Collision Object GameMaterial Chunk.");
        }

        if (!buildFromParsed) {
            len = initParse();

            startPos = ru.getCurrentPos();

            if (len == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Length read failed.");
                }
                return false;
            }

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Collision Object GameMaterial length: " + len + " bytes.");
            }
        } else {
            wu.startBlockWithLength();
        }

        if (!buildFromParsed) {
            num = ru.readInt(false);

            gmatIds = new Integer[num];
            crossableFlags = new Integer[num];
            slide = new Float[num];
            rebound = new Float[num];
            soundID = new Byte[num];
            unkB2_2 = new Byte[num];
            id = new Short[num];
        } else {
            wu.writeInt(num);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Num: " + num);
        }

        int i;

        for (i = 0; i < num; i++) {
            if (!buildFromParsed) {
                gmatIds[i] = ru.readInt(false);
            } else {
                wu.writeInt(gmatIds[i]);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("gmatIds[%d]: %d (%x)", i, gmatIds[i], gmatIds[i]);
            }
        }

        for (i = 0; i < num; i++) {
            if (!buildFromParsed) {
                crossableFlags[i] = ru.readInt(false);
                slide[i] = ru.readFloat(false);
                rebound[i] = ru.readFloat(false);
                soundID[i] = ru.readByte(false);
                unkB2_2[i] = ru.readByte(false);
                id[i] = ru.readShort(false);
            } else {
                wu.writeInt(crossableFlags[i]);
                wu.writeFloat(slide[i]);
                wu.writeFloat(rebound[i]);
                wu.writeByte(soundID[i]);
                wu.writeByte(unkB2_2[i]);
                wu.writeShort(id[i]);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("[%d] crossableFlags: %d (%x), slide: %f, rebound: %f, soundID: %d (%x), unkB2_2: %d (%x), id: %d (%x)",
                        i, crossableFlags[i], crossableFlags[i], slide[i], rebound[i], soundID[i], soundID[i], unkB2_2[i], unkB2_2[i], id[i], id[i]);
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
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            return "CollisionObjectGameMaterialChunk: Num: " + num;
        } else {
            return "CollisionObjectGameMaterialChunk";
        }
    }
}
