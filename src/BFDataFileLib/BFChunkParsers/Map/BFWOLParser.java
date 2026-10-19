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
import Common.DataType.BoneShapeElem;
import Common.IrisToolDefines;
import iristool.Engine.AnimationPlayer;
import iristool.Engine.Modifier;
import static iristool.Engine.Skeleton.fillBoneShapeMatricesToSize;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author andras
 */
public class BFWOLParser extends BFChunkParseClass implements BFChunkParseInterface {

    private LinkedList<Integer> wowKeys, wowKeysOrig;
    private ArrayList<BFKeyListElem> wows;
    private BFWOWChunk mergedWOW;
    private Long wolLen = 0L;

    /*
     A betöltés menete:
     A játék betölti a gaokat. A kulcslista kiürül, mivel végigmegy az olvasás (parseAllFromKeyFIFO()). 
     Miután minden betöltődöt, a második körben az animációkon, és az eventeken
     van a sor. Ez úgy történik, hogy a kezdeti gaolist-en végigmegy (amit a WOW kezdetben tartalmaz), és
     ezeknek nézi meg, hogy tartalmaz-e animationkeyt, groupkeyt és eventkeyt. [*] Ha tartalmaz animationt, hozzáadja
     majd megnézi a groupkeyt is. Ha tartalmaz azt is azt is hozzáadja. FONTOS: hogy a wow gaolistbe már ekkor
     bekerül az épp feldolgozott gao (aminek az animationját, csoportját, etc nézzük). Ha ezeket felvettük a
     feldolgozandó kulcslistába, akkor parseAllFromKeyFIFO(). A csoport feldolgozásakor megnézzük, hogy a
     csoportban lévő gaonak van-e 0x2-es falgje, ha igen, akkor rekurzió és kezdjük a csillagtól (*) a folyamatot újra.
     Ha ezzel is végzett, akkor jön a gaolistek mergelése, majd a felesleges pozíciós gaok kitakarítása.
     A KITAKARÍTÁS: a teljes mergelt wow gaolisten megyünk végig, és nézzük hogy, van-e 0x400000 flagje. Ha van, akkor
     megnézzük, hogy a referenciapont/pozíciós gaoja (twogaokey) létezik-e és benne van-e a mergelt wow gaolistjébe. Ha nincs,
     akkor kitöröljük a beolvasott kulcslista adatbázisból (ParsedObjectDatabase), ha benne van, akkor pedig békénhagyjuk.
     Ugyanez van a 0x800000 flag-et tartalmazó gaoknál. Csak itt lekérjük a csoportot (GRP) és a csoportban lévő gao-kat
     vizsgáljuk a 0x400000-nál említett szempontok szerint.
     Enélkül egy fájlban elhasal a parser.
     */
    public BFWOLParser() {
        super();
        wowKeysOrig = new LinkedList<>();
        wows = new ArrayList<>();
    }

    public BFWOWChunk getMergedWOW() {
        return mergedWOW;
    }

    public ArrayList<BFKeyListElem> getWOWsKLE() {
        return wows;
    }

    private void addGaoToWOWGaoList(BFWOWChunk w, BFKeyListElem kle) {
        BFGaoListChunk bfglc;
        bfglc = w.getGaoList();
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Adding GAO %x to WOW Gao List.", kle.getKey());
        }
        bfglc.addToGaoList(kle);
    }

    private Boolean hasGaoInWOWGaoList(BFWOWChunk w, int key) {
        ArrayList<TreeMap<Integer, BFKeyListElem>> bfgca;
        BFGaoListChunk bfglc;
        Map<Integer, BFGaoChunk> m;
        Map.Entry elem;
        Integer currKey;

        bfglc = w.getGaoList();
        bfgca = bfglc.getGaos();
        Iterator i = bfgca.iterator();
        int j = 0;
        while (i.hasNext()) {
            j++;
            m = (Map<Integer, BFGaoChunk>) (i.next());
            elem = (Map.Entry) (m.entrySet().iterator().next());
            currKey = (Integer) (elem.getKey());
            if ((new Integer(key).equals(currKey))) {
                return true;
            }
        }
        return false;
    }

    private Boolean parseGRPRecursive(BFWOWChunk w, BFKeyListElem grpKLE, Boolean buildFromParsed) {
        if (grpKLE == null || grpKLE.getParseHandler() == null) {
            return false;
        }

        BFGRPContainerChunk grpcc = (BFGRPContainerChunk) (grpKLE.getParseHandler());

        BFGRPChunk grpc = grpcc.getGRP();

        ArrayList<TreeMap<Integer, BFKeyListElem>> grpGaoList = grpc.getGrpGaoList();
        Iterator i = grpGaoList.iterator();
        Iterator ii;
        TreeMap<Integer, BFKeyListElem> hm, hm2;
        BFGaoChunk gao, gao2;
        BFKeyListElem kle, kle2;
        Object oo;
        BFGRPContainerChunk grpcc2;
        BFGRPChunk grpc2;
        ArrayList<TreeMap<Integer, BFKeyListElem>> grpGaoList2;

        while (i.hasNext()) {
            hm = (TreeMap<Integer, BFKeyListElem>) i.next();
            kle = hm.firstEntry().getValue();
            gao = (BFGaoChunk) (kle.getParseHandler());
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("GAO in GRP List: %x, flags: %x", hm.firstEntry().getKey(), gao.getFlags());
            }
            if (!hasGaoInWOWGaoList(w, hm.firstEntry().getKey())) {
                gao.setFlags(gao.getFlags() | 1);

                if (!parseEventsAnimationAndGRPsForAGao(w, kle, gao, buildFromParsed)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("parseEventsAnimationAndGRPsInner failed.");
                    }
                    return false;
                }
                if ((gao.getFlags() & 0x800000) != 0) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("GAO has flag 0x800000");
                    }
                    if (!bfdfc.getParsedObjectDatabaseClass().hasKey(gao.getGroupKey())) {
                        return false;
                    }
                    if (gao.getGroupChunkKLE() == null || (oo = gao.getGroupChunkKLE().getParseHandler()) == null) {
                        return false;
                    }
                    grpcc2 = (BFGRPContainerChunk) oo;
                    grpc2 = grpcc2.getGRP();
                    grpGaoList2 = grpc2.getGrpGaoList();
                    ii = grpGaoList2.iterator();
                    while (ii.hasNext()) {
                        hm2 = (TreeMap<Integer, BFKeyListElem>) ii.next();
                        kle2 = hm2.firstEntry().getValue();
                        gao2 = (BFGaoChunk) (kle2.getParseHandler());
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("GAO in GRP List (SUBGAO): %x, flags: %x", hm2.firstEntry().getKey(), gao2.getFlags());
                        }
                        gao2.setFlags(gao2.getFlags() | 1); // this will disable GAO's (gao2) direct rendering (in visibility rechecker)
                        if (!parseEventsAnimationAndGRPsForAGao(w, kle2, gao2, buildFromParsed)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("parseEventsAnimationAndGRPsForAGao (SUBGAO) failed.");
                            }
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private Boolean parseEventsAnimationAndGRPsForAGao(BFWOWChunk w, BFKeyListElem kle, BFGaoChunk bfgc, Boolean buildFromParsed) {
        if ((bfgc.getFlags() & 2) != 0) {
            if (bfgc.getAnimationKey() != -1) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Adding animation key: %x", bfgc.getAnimationKey());
                }
                BFKeyListElem bfklea;
                if (!buildFromParsed) {
                    bfklea = new BFKeyListElem(bfgc.getAnimationKey(), BFAnimationShapeChunk.class);
                    bfgc.setAnimationShapeChunkKLE(bfklea);
                } else {
                    bfklea = bfgc.getAnimationShapeChunkKLE();
                }
                bfdfc.addToKeyList(bfklea);
            }
            if (bfgc.getBonesGroupKey() != -1) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Adding bones group key: %x", bfgc.getBonesGroupKey());
                }
                BFKeyListElem bfkleg;
                if (!buildFromParsed) {
                    bfkleg = new BFKeyListElem(bfgc.getBonesGroupKey(), BFGRPContainerChunk.class);
                    bfgc.setBonesGroupChunkKLE(bfkleg);
                } else {
                    bfkleg = bfgc.getBonesGroupChunkKLE();
                }
                bfdfc.addToKeyList(bfkleg);
            }
            if (bfdfc.hasKeyInKeyList() && !bfdfc.parseAllFromKeyFIFO(buildFromParsed)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Animation and GRP parsing failed.");
                }
                return false;
            }

            // This is only needed for BonePositioner (runtime)
            bfgc.backupAnimationShapeChunkKLE();

            if (bfgc.getBonesGroupKey() != -1) {
                if (!parseGRPRecursive(w, bfgc.getBonesGroupChunkKLE(), buildFromParsed)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("parseGRPRecursive failed.");
                    }
                    return false;
                }
                BFGRPContainerChunk grp = ((BFGRPContainerChunk) (bfgc.getBonesGroupChunkKLE().getParseHandler())).duplicate();
                BFKeyListElem grpKLE = new BFKeyListElem(grp.getSelfKey(), BFGRPContainerChunk.class);
                grpKLE.setStoreIntoDatabase(false);
                grpKLE.setParseHandler(grp);
                bfgc.setBonesGroupChunkInstanceKLE(grpKLE);
                updateBonesGroupReferencesAfterDuplicatingMultiPartGAO(bfgc.getBonesGroupChunkInstanceKLE(), bfgc, w);
                Modifier.updateModifiersForMultipartGaos(bfgc, grp);
            }
            if (bfgc.getEventsKey() != -1 && bfgc.getActionsKey() == 0) { // look at BFGaoChunk::getActionsKey()'s comment
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Adding events key: %x", bfgc.getEventsKey());
                }
                BFKeyListElem bfklee;
                if (!buildFromParsed) {
                    bfklee = new BFKeyListElem(bfgc.getEventsKey(), BFEventsChunk.class);
                    bfgc.setEventsChunkKLE(bfklee);
                } else {
                    bfklee = bfgc.getEventsChunkKLE();
                }
                bfdfc.addToKeyList(bfklee);
            }

            if (bfdfc.hasKeyInKeyList() && !bfdfc.parseAllFromKeyFIFO(buildFromParsed)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Events parsing failed.");
                }
                return false;
            }
            BFGaoChunk.setFlashMatrix(bfgc, bfgc.getGlobalMatrix());
            if (bfgc.getActionsKey() == 0 && bfgc.getEventsChunkKLE() != null && bfgc.getEventsChunkKLE().getParseHandler() != null) {
                AnimationPlayer.resolveEventTracks(bfdfc, (BFEventsChunk) (bfgc.getEventsChunkKLE().getParseHandler()));
            }
        }
        if (!hasGaoInWOWGaoList(w, kle.getKey())) {
            addGaoToWOWGaoList(w, kle); // itt hozzaadjuk a tablahoz a feldolgozott wow-okat
        }
        return true;
    }

    private void updateBonesGroupReferencesAfterDuplicatingMultiPartGAO(BFKeyListElem duplicatedGrp, BFGaoChunk mainGao, BFWOWChunk w) {
        BFGRPContainerChunk duplicatedGrpCont = (BFGRPContainerChunk) (duplicatedGrp.getParseHandler());
        BFGRPChunk duplicatedGrpContGrp = duplicatedGrpCont.getGRP();
        ArrayList<TreeMap<Integer, BFKeyListElem>> algrpi = duplicatedGrpContGrp.getGrpGaoList();
        int i, j;
        for (i = 0; i < algrpi.size(); i++) {
            BFKeyListElem kle = algrpi.get(i).firstEntry().getValue();
            if (kle != null) {
                BFGaoChunk gaoinst = (BFGaoChunk) (kle.getParseHandler());
                if (w != null) {
                    addGaoToWOWGaoList(w, kle);
                }
                // Pivot references update (between bones)
                if (gaoinst.getReferencePointGAOChunkKLE() != null && gaoinst.getReferencePointGAOChunkKLE().getParseHandler() != null) {
                    if (mainGao.getSelfKey() != null && mainGao.getSelfKey().equals(gaoinst.getReferencePointGAOKey())) {
                        BFKeyListElem newkle = new BFKeyListElem(mainGao.getSelfKey(), BFGaoChunk.class);
                        newkle.setParseHandler(mainGao);
                        newkle.setStoreIntoDatabase(false);
                        gaoinst.setReferencePointGAOChunkKLE(newkle);
                    } else {
                        for (j = 0; j < algrpi.size(); j++) {
                            BFKeyListElem kle2 = algrpi.get(j).firstEntry().getValue();
                            if (kle2.getKey() == gaoinst.getReferencePointGAOChunkKLE().getKey()) {
                                ((BFGaoChunk) (kle.getParseHandler())).setReferencePointGAOChunkKLE(kle2);
                            }
                        }
                    }
                }

                // has skinning, need to set boneGaoMatrices and boneGaoChunk here (if it has group, this is done in applyShapeForGao)
                if ((gaoinst.getFlags() & 0x200000) != 0) {
                    BoneShapeElem bse;
                    BFKeyListElem boneKLE;
                    for (j = 0; j < gaoinst.getBoneShapes().size(); j++) {
                        bse = gaoinst.getBoneShapes().get(j);
                        if (bse.getBoneID() != -1) {
                            if (mainGao.getBoneShapeMatrices().size() <= bse.getBoneID()) {
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    System.out.format("BUG: %s: shape matrices num (%d) < than bone ID (%d), adjusting size.\n",
                                            mainGao.getGaoName(), mainGao.getBoneShapeMatrices().size(), bse.getBoneID());
                                }
                                fillBoneShapeMatricesToSize(mainGao.getBoneShapeMatrices(), bse.getBoneID() + 1);
                            }
                            bse.setAnimationShapeMtx(mainGao.getBoneShapeMatrices().get(bse.getBoneID()));
                        }
                        if (bse.getBoneGaoKey() != null && !bse.getBoneGaoKey().equals(-1)) {
                            boneKLE = new BFKeyListElem(mainGao.getSelfKey(), BFGaoChunk.class);
                            boneKLE.setStoreIntoDatabase(false);
                            boneKLE.setParseHandler(mainGao);
                            bse.setBoneGaoChunkKLE(boneKLE);
                        }
                    }
                }
            }
        }
    }

    private Boolean parseEventsAnimationAndGRPs(BFWOWChunk w, Boolean buildFromParsed) {
        BFGaoListChunk bfglc;
        ArrayList<TreeMap<Integer, BFKeyListElem>> bfgca;
        ArrayList<TreeMap<Integer, BFKeyListElem>> bfgcaCopy;
        BFKeyListElem kle;
        TreeMap<Integer, BFGaoChunk> m;
        Map.Entry elem;
        BFGaoChunk bfgc;
        Iterator i;
        Integer flags;
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("starting...");
        }
        bfglc = w.getGaoList(); // Ennek a key-e nincs bent a ParsedObjectDatabase-ben, mert a játék se teszi bele

        bfgca = bfglc.getGaos();
        bfgcaCopy = new ArrayList<>(bfgca); // hogy ne legyen ConcurrentModificationException
        i = bfgcaCopy.iterator();
        while (i.hasNext()) {
            m = (TreeMap<Integer, BFGaoChunk>) (i.next());
            elem = (Map.Entry) (m.entrySet().iterator().next());
            kle = (BFKeyListElem) (elem.getValue());
            bfgc = (BFGaoChunk) (kle.getParseHandler());
            flags = bfgc.getFlags();

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Animation key: %x, Group Key: %x, Events Key: %x, Actions Key: %x, flags: %x",
                        bfgc.getAnimationKey(), bfgc.getBonesGroupKey(), bfgc.getEventsKey(), bfgc.getActionsKey(), flags);
            }

            if ((flags & 3) != 1) {
                if ((flags & 2) != 0) {
                    if (!parseEventsAnimationAndGRPsForAGao(w, kle, bfgc, buildFromParsed)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("parseEventsAnimationAndGRPsForAGao failed.");
                        }
                        return false;
                    }
                }
            }
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("...finished.");
        }
        return true;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (buildFromParsed) {
            if (insideBinFile) {
                wu.startBlockWithLength();
            }
        } else {
            wolLen = initParse();
            wows.clear();
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Length: " + wolLen);
        }

        int num = (int) (wolLen / 8);

        if ((num * 8) != wolLen) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("invalid length (" + wolLen + ")");
            }
            return false;
        }

        Integer key;
        String wowstring;

        while (num-- > 0) {

            if (buildFromParsed) {
                key = wowKeysOrig.get(wowKeysOrig.size() - num + 1);
                wowstring = ".wow";
                wu.writeInt(key);
                wu.writeNString(wowstring, 4);
            } else {
                key = ru.readInt(false);
                wowstring = ru.readNString(4, false);
            }

            if (key == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("read key failed.");
                }
                return false;
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("WOW Key found: %x", key);
            }

            if (key != 0 && !".wow".equals(wowstring)) { // when key is 0, then no .wow string
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(".WOW string read failed: " + wowstring);
                }
                return false;
            }

            wowKeysOrig.offer(key);
        }

        if (buildFromParsed) {
            if (insideBinFile) {
                wu.finishBlockWithLength();
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("We need to parse " + wowKeysOrig.size() + " WOWs.");
        }

        // save wowkeys for write
        wowKeys = (LinkedList<Integer>) (wowKeysOrig.clone());

        mergedWOW = null;
        int WOWKey;

        bfdfc.clearParsedObjectDatabase();

        ArrayList<TreeMap<Integer, BFKeyListElem>> al;
        Iterator it;
        TreeMap<Integer, BFKeyListElem> tm;
        int i = 0;

        while (!wowKeys.isEmpty()) {
            WOWKey = wowKeys.poll();
            if (WOWKey == -1 || WOWKey == 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Key equals to " + WOWKey + ", ignoring.");
                }
                continue;
            }

            BFKeyListElem wowKLE;

            if (!buildFromParsed) {
                wowKLE = new BFKeyListElem(WOWKey, BFWOWChunk.class);
                wows.add(wowKLE);
            } else {
                wowKLE = wows.get(i);
            }
            i++;

            bfdfc.addToKeyList(wowKLE);
            if (!bfdfc.parseAllFromKeyFIFO(buildFromParsed)) {
                return false;
            }

            BFWOWChunk wow = (BFWOWChunk) (wowKLE.getParseHandler());

            if (mergedWOW == null) {
                mergedWOW = wow;
            }

            // minden gaonak 0x10-et adunk a preven
            if (!mergedWOW.equals(wow)) {
                al = mergedWOW.getGaoList().getGaos();
                it = al.iterator();
                BFGaoChunk tmp;
                while (it.hasNext()) {
                    tm = (TreeMap<Integer, BFKeyListElem>) it.next();
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("Adding processRuntimeFlags 0x10 on GAO %x to prevwow GAO list.", tm.firstEntry().getKey());
                    }
                    tmp = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
                    tmp.setProcessFlagsRuntime((byte) (tmp.getProcessFlagsRuntime() | 0x10));
                }
            }

            if (!parseEventsAnimationAndGRPs(wow, buildFromParsed)) { // TODO: 0x10 flag check!!!
                return false;
            }

            // MERGE
            if (!mergedWOW.equals(wow)) {
                al = wow.getGaoList().getGaos();

                if (al == null) {
                    return false;
                }

                it = al.iterator();
                while (it.hasNext()) {
                    tm = (TreeMap<Integer, BFKeyListElem>) it.next();
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("MERGING: Adding gao %x to prevwow gao list.", tm.firstEntry().getKey());
                    }
                    addGaoToWOWGaoList(mergedWOW, tm.firstEntry().getValue());
                }
                mergedWOW.setAmbientColor1(wow.getAmbientColor1());
                mergedWOW.setAmbientColor2(wow.getAmbientColor2());
                mergedWOW.setClearColor(wow.getClearColor());
                mergedWOW.setWowName(wow.getWowName());
            }

            // Clear unreferenced position gaos from refpoints (in wow gao list)
            if (!clearPositionGaos(mergedWOW)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("clearPositionGaos() failed.");
                }
                return false;
            }

            if (!mergedWOW.equals(wow)) {
                // A mergelés miatt kiszedjük az aktuális wow-ot a ParsedObjectDatabase-ből,
                // mivel ez problémát okoz a PC-s verzió ff00abb4 key-ű binben.
                bfdfc.removeKeyFromParsedObjectDatabase(WOWKey);
            }

        }

        if (mergedWOW != null) {
            dumpGaoListToLog(mergedWOW);
        }

        if (bfdfc.getTextureDatabase() != null && !IrisToolDefines.PUBLIC_RELEASE) {
            simpleLog("-----------------------------");
            simpleLog("Texture summary after parsing");
            simpleLog("-----------------------------");
            bfdfc.getTextureDatabase().dumpListIntoLog();
        }

        setHasBeenParsed(true);

        return true;
    }

    private void dumpGaoListToLog(BFWOWChunk prevWOW) {
        Iterator i = prevWOW.getGaoList().getGaos().iterator();
        TreeMap<Integer, BFKeyListElem> tm;
        BFGaoChunk gao;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            simpleLog("--------------------------");
            simpleLog("GAOs summary after parsing");
            simpleLog("--------------------------");
        }

        while (i.hasNext()) {
            tm = (TreeMap<Integer, BFKeyListElem>) i.next();
            gao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                simpleLogF("GAO key: %x, name: %s, flags: %x", tm.firstEntry().getKey(), gao.getGaoName(), gao.getFlags());
            }
        }
    }

    private void clearPivotPointFromGaoListByGAOKey(BFWOWChunk w, Integer pivotGaoKey) {
        ArrayList<TreeMap<Integer, BFKeyListElem>> al = w.getGaoList().getGaos();
        TreeMap<Integer, BFKeyListElem> tm;
        BFGaoChunk gao;
        Iterator i = al.iterator();
        while (i.hasNext()) {
            tm = (TreeMap<Integer, BFKeyListElem>) i.next();
            gao = (BFGaoChunk) ((tm.firstEntry().getValue()).getParseHandler());
            if ((gao.getFlags() & 0x400000) != 0) {
                if (gao.getReferencePointGAOKey() != null && gao.getReferencePointGAOKey().equals(pivotGaoKey)) {
                    gao.setReferencePointGAOKey(-1);
                    gao.setReferencePointGAOChunkKLE(null);
                    gao.setFlags(gao.getFlags() & ~0x400000);
                    gao.setLocalMatrix(null);
                }
            }
        }
    }

    private Boolean clearPositionGaos(BFWOWChunk w) {
        ArrayList<TreeMap<Integer, BFKeyListElem>> al;
        Iterator i;
        TreeMap<Integer, BFKeyListElem> tm, tm2;
        BFGaoChunk gao;
        BFGRPContainerChunk grp;
        al = w.getGaoList().getGaos();

        i = al.iterator();
        while (i.hasNext()) {
            tm = (TreeMap<Integer, BFKeyListElem>) i.next();
            gao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("GAO (name: %s): %x, flags: %x (has 0x400000: %x, has 0x800000: %x)", gao.getGaoName(),
                        tm.firstEntry().getKey(), gao.getFlags(),
                        (gao.getFlags() & 0x400000), (gao.getFlags() & 0x800000));
            }
            if ((gao.getFlags() & 0x400000) != 0) {
                if (gao.getReferencePointGAOKey() != -1) {
                    if (bfdfc.getParsedObjectDatabaseClass().hasKey(gao.getReferencePointGAOKey())) {
                        if (!hasGaoInWOWGaoList(w, gao.getReferencePointGAOKey())) {
                            bfdfc.removeKeyFromParsedObjectDatabase(gao.getReferencePointGAOKey());
                            clearPivotPointFromGaoListByGAOKey(w, gao.getReferencePointGAOKey());
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                logF("Found GAO (key: %x), removed.", gao.getReferencePointGAOKey());
                            }
                        } else {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                logF("Found GAO (key: %x), referenced in wow gaolist, not removing.", gao.getReferencePointGAOKey());
                            }
                        }
                    } else {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("Gao key is not valid, can not remove.");
                        }
                    }
                }
            }
            if ((gao.getFlags() & 0x800000) != 0) {
                if (gao.getGroupKey() != 0) {
                    if (gao.getGroupChunkKLE() != null && gao.getGroupChunkKLE().getParseHandler() != null) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("Found GRP (key: %x, name: %s), checking.", gao.getGroupKey(), gao.getGaoName());
                        }
                        grp = (BFGRPContainerChunk) (gao.getGroupChunkKLE().getParseHandler());
                        ArrayList<TreeMap<Integer, BFKeyListElem>> al2 = grp.getGRP().getGrpGaoList();
                        Iterator ii = al2.iterator();
                        while (ii.hasNext()) {
                            tm2 = (TreeMap<Integer, BFKeyListElem>) ii.next();
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                logF("GRP elem: Checking key in ParsedObjectDatabase: %x", tm2.firstEntry().getKey());
                            }
                            if (bfdfc.getParsedObjectDatabaseClass().hasKey(tm2.firstEntry().getKey())) {
                                if (!hasGaoInWOWGaoList(w, tm2.firstEntry().getKey())) {
                                    bfdfc.removeKeyFromParsedObjectDatabase(tm2.firstEntry().getKey());
                                    clearPivotPointFromGaoListByGAOKey(w, tm2.firstEntry().getKey());
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        logF("GRP: Found GAO (key: %x), removed.", tm2.firstEntry().getKey());
                                    }
                                } else {
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        logF("GRP: Found GAO (key: %x), referenced in wow gaolist, not removing.", tm2.firstEntry().getKey());
                                    }
                                }
                            }
                        }
                    } else {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("GRP key is not valid, can not check & remove.");
                        }
                    }
                }
            }
        }
        return true;
    }
}
