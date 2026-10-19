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
public class BFGaoListChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Long len = 0L;
    private ArrayList<Integer> gaoKeys = new ArrayList<>();
    private ArrayList<BFKeyListElem> gaos = new ArrayList<>();
    private ArrayList<Integer> gaoKeysOrig = new ArrayList<>();
    private ArrayList<BFKeyListElem> gaosOrig = new ArrayList<>();

    public BFGaoListChunk() {
        super();
    }

    public boolean addToGaoList(BFKeyListElem kle) {
        if (kle == null) {
            return false;
        }
        gaoKeys.add(kle.getKey());
        gaos.add(kle);
        return true;
    }

    public ArrayList<TreeMap<Integer, BFKeyListElem>> getGaos() {

        ArrayList<TreeMap<Integer, BFKeyListElem>> gaosRet = new ArrayList<>();

        Iterator i = gaos.iterator();
        while (i.hasNext()) {
            BFKeyListElem kle = (BFKeyListElem) (i.next());
            if (kle == null) {
                continue;
            }

            TreeMap tm = new TreeMap();
            tm.put(kle.getKey(), kle);
            gaosRet.add(tm);
        }
        return gaosRet;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing GAO List.");
        }

        gaoKeys.clear();
        if (!buildFromParsed) {
            gaoKeysOrig.clear();
            gaosOrig.clear();
        }

        int num;

        if (!buildFromParsed) {
            len = initParse();

            if (len == null) {
                return false;
            }

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("GAO List length: " + len + " bytes.");
            }

            if (len % 4 != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Invalid length.");
                }
                return false;
            }

            num = (int) (len / 4);

        } else {
            num = gaoKeysOrig.size();
            wu.startBlockWithLength();
        }

        while (num-- > 0) {
            int key;
            if (!buildFromParsed) {
                key = ru.readInt(false);
                gaoKeysOrig.add(key);
            } else {
                key = gaoKeysOrig.get(gaoKeysOrig.size() - num + 1);
                wu.writeInt(key);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Found GAO key: %x", key);
            }
            if (key != 0) {
                BFKeyListElem gaoKLE;
                if (!buildFromParsed) {
                    gaoKLE = new BFKeyListElem(key, BFGaoChunk.class);
                    gaos.add(gaoKLE);
                    gaosOrig.add(gaoKLE);
                } else {
                    gaoKLE = gaosOrig.get(gaosOrig.size() - num + 1);
                }
                bfdfc.addToKeyList(gaoKLE);
                gaoKeys.add(key);
            } else {
                gaos.add(null);
                gaosOrig.add(null);
            }
        }

        return true;
    }

}
