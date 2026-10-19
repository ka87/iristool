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
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author andras
 */
public class BFGRPChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFGRPChunk() {
        super();
        grpGaoList = new ArrayList<>();
        unk1Arr = new ArrayList<>();
        grpGaoArrayKLE = new ArrayList<>();
    }

    private ArrayList<Integer> grpGaoList;
    private ArrayList<Integer> unk1Arr;
    private ArrayList<BFKeyListElem> grpGaoArrayKLE;
    private Long grpLen = 0L;

    public ArrayList<TreeMap<Integer, BFKeyListElem>> getGrpGaoList() {

        BFGaoChunk gao;
        Object obj;

        ArrayList<TreeMap<Integer, BFKeyListElem>> retGRPGaoList = new ArrayList<>();
        Iterator i = grpGaoArrayKLE.iterator();
        while (i.hasNext()) {
            BFKeyListElem kle = (BFKeyListElem) (i.next());
            if (kle == null || kle.getParseHandler() == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Key (KLE) is null, ignoring.");
                }
                continue;
            }
            obj = kle.getParseHandler();

            if (!(obj instanceof BFGaoChunk)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key %x is not a GAO (parse error!), ignoring.", kle.getKey());
                }
                continue;
            }

            gao = (BFGaoChunk) obj;
            TreeMap tm = new TreeMap();
            tm.put(kle.getKey(), kle);
            retGRPGaoList.add(tm);
        }
        return retGRPGaoList;
    }

    public Integer getElemOffsetByKey(int key) {
        Iterator it = grpGaoList.iterator();
        int i = 0;
        Integer retKey;
        while (it.hasNext()) {
            retKey = (Integer) (it.next());
            if (retKey != null && retKey.equals(key)) {
                return i;
            }
            i++;
        }
        return null;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        if (!buildFromParsed) {
            grpLen = initParse();
            grpGaoList.clear();
            unk1Arr.clear();
            grpGaoArrayKLE.clear();
        } else {
            wu.startBlockWithLength();
        }

        long startpos = ru.getCurrentPos();

        if (grpLen == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing GRP Chunk.");
            log("Length is " + grpLen + " bytes.");
        }

        Integer key, unk1;

        if (!buildFromParsed) {
            while (ru.getCurrentPos() < (startpos + grpLen)) {
                key = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("GRP Gao key: %x", key);
                }
                BFKeyListElem gaoKLE = new BFKeyListElem(key, BFGaoChunk.class);
                bfdfc.addToKeyList(gaoKLE);

                grpGaoList.add(key);
                grpGaoArrayKLE.add(gaoKLE);

                if (!insideBinFile) {
                    unk1 = ru.readInt(false);
                    unk1Arr.add(unk1);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("unk1: %d (%x)", unk1, unk1);
                    }
                }
            }
            if (!insideBinFile && grpLen != (ru.getCurrentPos() - startpos)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Parsed length != length");
                }
                return false;
            }
        } else {
            int i;
            for (i = 0; i < grpGaoList.size(); i++) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Writing GRP Gao key: %x", grpGaoList.get(i));
                }
                wu.writeInt(grpGaoList.get(i));
                bfdfc.addToKeyList(grpGaoArrayKLE.get(i));
                if (!insideBinFile) {
                    wu.writeInt(unk1Arr.get(i));
                }
            }
            wu.finishBlockWithLength();
        }

        return true;
    }

    @Override
    public BFGRPChunk duplicate() {
        BFGRPChunk ret = new BFGRPChunk();
        ret.setSelfKey(selfKey);
        ret.grpLen = grpLen;

        int i;
        BFGaoChunk tmp;
        BFKeyListElem tmpKLE;
        for (i = 0; i < grpGaoList.size(); i++) {
            if (grpGaoArrayKLE.get(i) != null) {
                tmpKLE = new BFKeyListElem(grpGaoArrayKLE.get(i).getKey(), BFGaoChunk.class);
                tmpKLE.setStoreIntoDatabase(false);
                if (grpGaoArrayKLE.get(i).getParseHandler() != null) {
                    tmp = ((BFGaoChunk) (grpGaoArrayKLE.get(i).getParseHandler())).duplicate();
                    tmpKLE.setParseHandler(tmp);
                }
                ret.grpGaoArrayKLE.add(tmpKLE);
            } else {
                ret.grpGaoArrayKLE.add(null);
            }

            ret.grpGaoList.add(grpGaoList.get(i));
        }
        return ret;
    }
}
