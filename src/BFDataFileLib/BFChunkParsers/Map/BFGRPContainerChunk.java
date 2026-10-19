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

/**
 *
 * @author andras
 */
public class BFGRPContainerChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFGRPContainerChunk() {
        super();
    }

    private Long grpContlen = 0L;
    private Integer grpKey;
    private Integer unk1;
    private Integer unk2, unk3;
    private BFKeyListElem grpChunkKLE;

    public BFGRPChunk getGRP() {
        if (grpKey == null || grpChunkKLE == null || grpChunkKLE.getParseHandler() == null) {
            return null;
        }
        return (BFGRPChunk) (grpChunkKLE.getParseHandler());
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        if (!buildFromParsed) {
            grpContlen = initParse();
        } else {
            wu.startBlockWithLength();
        }

        long startpos = ru.getCurrentPos();

        if (grpContlen == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing GRP Container.");
        }

        if (!buildFromParsed) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length is " + grpContlen + " bytes.");
            }
            grpKey = ru.readInt(false);
            unk1 = ru.readInt(false);
        } else {
            wu.writeInt(grpKey);
            wu.writeInt(unk1);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Key: %x", grpKey);
            logF("unk1: (%d) %x", unk1, unk1);
        }

        if (!buildFromParsed) {
            grpChunkKLE = new BFKeyListElem(grpKey, BFGRPChunk.class);
        }
        bfdfc.addToKeyList(grpChunkKLE
        );
        if (!insideBinFile) {
            if (!buildFromParsed) {
                unk2 = ru.readInt(false);
                unk3 = ru.readInt(false);
            } else {
                wu.writeInt(unk2);
                wu.writeInt(unk3);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk2: %d (%x, unk3: %d (%x)", unk2, unk2, unk3, unk3);
            }
        }

        if (!buildFromParsed && (!insideBinFile && grpContlen != (ru.getCurrentPos() - startpos))) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != length");
            }
            return false;
        }
        if (buildFromParsed) {
            wu.finishBlockWithLength();
        }

        return true;
    }

    @Override
    public BFGRPContainerChunk duplicate() {
        BFGRPContainerChunk ret = new BFGRPContainerChunk();
        ret.grpContlen = grpContlen;
        ret.grpKey = grpKey;
        ret.setSelfKey(selfKey);
        ret.unk1 = unk1;
        ret.unk2 = unk2;
        ret.unk3 = unk3;

        BFKeyListElem kle = new BFKeyListElem(grpKey, BFGRPChunk.class);
        kle.setStoreIntoDatabase(false);
        if (grpChunkKLE.getParseHandler() != null) {
            BFGRPChunk grpc = (BFGRPChunk) (grpChunkKLE.getParseHandler());
            BFGRPChunk newgrpc = grpc.duplicate();
            kle.setParseHandler(newgrpc);
        }
        ret.grpChunkKLE = kle;

        return ret;
    }

}
