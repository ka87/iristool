/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

import BFDataFileLib.BFChunkParsers.Map.BFWOLParser;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFlib.SPEClass;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFWOWChunk;
import BFDataFileLib.BFChunkParsers.Texture.BFTextureParser;
import BFDataFileLib.BFChunkParsers.SoundBank.BFSoundBankHeaderCollectionParser;
import BFDataFileLib.BFChunkParsers.SoundBank.BFSoundBankPackParser;
import BFDataFileLib.BFChunkParsers.Texts.BFTextParser;
import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import Common.lzo.MInt;
import Common.lzo.MiniLZO;
import BFlib.BFClass;
import BFlib.BFFatTableElem;
import BFlib.LoadedSoundsClass;
import BFlib.ParsedObjectDatabaseClass;
import BFlib.TextureDatabase;
import Common.WriterUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 *
 * @author andras
 */
public class BFDataFileClass extends Logger {

    private byte[] data;
    private int key = 0;
    private int dataType = 0;
    private BFClass bfc = null;
    private SPEClass spe = null;
    private LoadedSoundsClass lsc = null;
    private TextureDatabase td = null;
    private ReaderUtil ru = null;
    private WriterUtil wu = null;
    private LinkedList<BFKeyListElem> keyList = null;
    private ParsedObjectDatabaseClass objdb = null;
    private LinkedHashMap<Integer, String> scriptNames = null;
    private Boolean insideBinFile = false;
    private ArrayList<TreeMap<Integer, BFGaoChunk>> al;
    private String textureSaveDir = null;
    private Object wolParser = null;
    private Object sbpParser = null;
    private Object sbhcParser = null;
    //private Integer startKey = -1;
    public final static int BF_D_UNKNOWN = -1;
    public final static int BF_D_NOT_BINARIZED = 0;
    public final static int BF_D_MAP_MODEL_SCRIPT_RELATED = 1;
    public final static int BF_D_TEXTURES = 2;
    public final static int BF_D_SOUNDBANK_PACK = 3;
    public final static int BF_D_SOUNDBANK_HEADER_COLLECTION = 4;
    public final static int BF_D_TEXTS = 5;

    public static boolean isDataCompressedByType(int dkt) {
        switch (dkt) {
            case BF_D_MAP_MODEL_SCRIPT_RELATED:
            case BF_D_TEXTURES:
            case BF_D_TEXTS:
            case BF_D_SOUNDBANK_HEADER_COLLECTION:
                return true;
        }
        return false;
    }

    public byte[] getDataFromBFbyKey(int key) {
        BFFatTableElem bfe = bfc.getFatTable().getElemByKey(key);
        return BFClass.extractFatTableElem(bfc, bfc.getFatTable().getElemIndex(bfe), true);
    }

    public static boolean isDataCompressedByKey(int key) {
        int dkt = getBinarizedDataKeyType(key);
        return isDataCompressedByType(dkt);
    }

    public static int getBinarizedDataKeyType(int key) {
        if (!isBinarizedDataKey(key)) {
            return BF_D_NOT_BINARIZED;
        }

        int keysubtype = (key >> 20) & 0xf;
        key = (key >> 24) & 0xff;

        if (key == 0xff) {
            switch (keysubtype) {
                case 0:
                    return BF_D_MAP_MODEL_SCRIPT_RELATED;
                case 4:
                    return BF_D_SOUNDBANK_PACK;
                case 8:
                    return BF_D_TEXTURES;
                default:
                    return BF_D_UNKNOWN;
            }
        }
        if (key == 0xfe) {
            return BF_D_SOUNDBANK_HEADER_COLLECTION;
        }
        if (key == 0xfd) {
            return BF_D_TEXTS;
        }

        return BF_D_UNKNOWN;
    }

    public static String describeBinarizedDataKeyType(int key) {
        int dkt = getBinarizedDataKeyType(key);

        if (dkt == BF_D_NOT_BINARIZED) {
            return "Not binarized";
        }

        int keysubtype = (key >> 20) & 0xf;

        if (dkt == BF_D_UNKNOWN) {
            return "Unknown";
        }

        switch (dkt) {
            case BF_D_MAP_MODEL_SCRIPT_RELATED:
                return "Map, model and script related";
            case BF_D_SOUNDBANK_PACK:
                return "Soundbank Pack";
            case BF_D_SOUNDBANK_HEADER_COLLECTION:
                return "Soundbank (language: " + keysubtype + ")";
            case BF_D_TEXTURES:
                return "Textures";
            case BF_D_TEXTS:
                return "Texts (language: " + keysubtype + ")";
        }

        return "Unknown";
    }

    public static boolean isBinarizedDataKey(int key) {
        key = (key >> 24) & 0xff;

        if (key == 0xfd || key == 0xfe || key == 0xff) {
            return true;
        }
        return false;
    }

    public static byte[] decompressDataFileLZO(byte[] data) {
        ReaderUtil ru = new ReaderUtil(data);
        Integer unpacked;
        Integer compressed;
        MInt mi = new MInt();
        int ret;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while (!ru.isEOF()) {
            unpacked = ru.readInt(false);
            compressed = ru.readInt(false);
            if (unpacked == 0) {
                break;
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println("decompressDataFileLZO: unpacked size: " + unpacked + " bytes, compressed size: " + compressed + " bytes.");
            }

            byte[] compressedDataChunk = ru.readByteArray(compressed);

            if (compressedDataChunk == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("decompressDataFileLZO: chunk read failed.");
                }
                return null;
            }

            byte[] uncompressedDataChunk;

            // check for requirement of uncompress (needed for HD edition)
            if (compressed >= unpacked) {
                uncompressedDataChunk = compressedDataChunk;
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.format("decompressDataFileLZO: compressed (%d bytes) >= uncompressed (%d bytes), unneeded to decompress.\n", compressed, unpacked);
                }
            } else {
                uncompressedDataChunk = new byte[unpacked];
                mi.v = 0;
                if ((ret = MiniLZO.lzo1x_decompress(compressedDataChunk, compressed, uncompressedDataChunk, mi)) < 0) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        System.out.println("decompressDataFileLZO: decompress failed. (" + ret + ")");
                    }
                    return null;
                }
            }
            try {
                bos.write(uncompressedDataChunk);
            } catch (IOException e) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("decompressDataFileLZO: IO Exception: " + e.getMessage());
                }
            }
        }

        return bos.toByteArray();
    }

    ///////
    public boolean isInsideBinFile() {
        return insideBinFile;
    }

    public File getBFFile() {
        return bfc.getFile();
    }

    public int getKey() {
        return key;
    }

    public void setTextureSaveDir(String textureSaveDir) {
        this.textureSaveDir = textureSaveDir;
    }

    private boolean openKey(int key) {
        dataType = getBinarizedDataKeyType(key);

        BFFatTableElem bfte = bfc.getFatTable().getElemByKey(key);
        if (bfte == null) {
            return false;
        }
        long pos = bfte.getPosition();

        data = bfc.readLengthAndDataFromPos(pos);
        if (data == null) {
            return false;
        }

        if (BFDataFileClass.isDataCompressedByType(dataType)) {
            this.data = BFDataFileClass.decompressDataFileLZO(data);
        }

        if (data != null) {
            ru = new ReaderUtil(this.data);
        }

        return (data != null);
    }

    public BFDataFileClass(Integer key, BFClass bfc, SPEClass spe, LoadedSoundsClass lsc, TextureDatabase td) {

        if (bfc == null || !bfc.isOpened()) {
            return;
        }

        this.bfc = bfc;
        this.spe = spe;
        this.lsc = lsc;
        this.td = td;

        scriptNames = new LinkedHashMap<Integer, String>();

        //remove!!! -->
        
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "d:\\bge_scriptnames.csv"));
            String line = reader.readLine().trim();
            String[] parts;
            while (line != null) {
                if (line.startsWith("0x")) {
                    line = line.substring(2);
                }
                parts = line.split(";");
                if (parts == null || parts.length != 2) {
                    continue;
                }

                scriptNames.put((int)Long.parseLong(parts[0], 16), parts[1]);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // -- remove!!!

        keyList = new LinkedList<>();

        dataType = getBinarizedDataKeyType(key);

        insideBinFile = !(dataType == BF_D_NOT_BINARIZED);

        if (!openKey(key)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Key (%x) open failed.", key);
            }
            this.bfc = null;
            this.lsc = null;
            this.td = null;
            return;
        }

        this.key = key;
        this.wu = new WriterUtil();
    }

    public String getScriptNameByKey(int key) {
        if (scriptNames == null) {
            return null;
        }
        return scriptNames.get(key);
    }

    public boolean isDataFileOpened() {
        return (this.data != null);
    }

    public BFWOWChunk getParsedWOWMerged() {
        if (!isDataFileOpened() || dataType != BF_D_MAP_MODEL_SCRIPT_RELATED) {
            return null;
        }
        return ((BFWOLParser) wolParser).getMergedWOW();
    }

    public ArrayList<BFKeyListElem> getParsedWOWsKLE() {
        if (!isDataFileOpened() || dataType != BF_D_MAP_MODEL_SCRIPT_RELATED) {
            return null;
        }
        return ((BFWOLParser) wolParser).getWOWsKLE();
    }

    public BFSoundBankPackParser getParsedSoundBankPack() {
        if (!isDataFileOpened() || dataType != BF_D_SOUNDBANK_PACK) {
            return null;
        }
        return ((BFSoundBankPackParser) sbpParser);
    }

    public BFSoundBankHeaderCollectionParser getParsedSoundBankHeaderCollection() {
        if (!isDataFileOpened() || dataType != BF_D_SOUNDBANK_HEADER_COLLECTION) {
            return null;
        }
        return ((BFSoundBankHeaderCollectionParser) sbhcParser);
    }

    public boolean parse() throws Exception {
        if (!isDataFileOpened()) {
            return false;
        }

        boolean ret = false;

        if (!ru.isOpened()) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("readerUtil failed.");
            }
            return false;
        }

        switch (dataType) {
            case BF_D_MAP_MODEL_SCRIPT_RELATED:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Calling WOLParser.");
                }
                wolParser = new BFWOLParser();
                ret = ((BFChunkParseInterface) wolParser).parse(this, insideBinFile, false);
                break;
            case BF_D_TEXTURES:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Calling TextureParser.");
                }
                Object tParser = new BFTextureParser();
                ((BFTextureParser) tParser).setSaveDir(textureSaveDir);
                ret = ((BFChunkParseInterface) tParser).parse(this, insideBinFile, false);
                break;
            case BF_D_TEXTS:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Calling TextParser.");
                }
                Object ttParser = new BFTextParser();
                ret = ((BFChunkParseInterface) ttParser).parse(this, insideBinFile, false);
                break;
            case BF_D_SOUNDBANK_PACK:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Calling SoundBankPackParser.");
                }
                sbpParser = new BFSoundBankPackParser();
                ret = ((BFChunkParseInterface) sbpParser).parse(this, insideBinFile, false);
                break;
            case BF_D_SOUNDBANK_HEADER_COLLECTION:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Calling SoundBankHeaderCollectionParser.");
                }
                sbhcParser = new BFSoundBankHeaderCollectionParser();
                ret = ((BFChunkParseInterface) sbhcParser).parse(this, insideBinFile, false);
                break;
            default:
                if (bfc != null && bfc.isOpened() && bfc.getStartKey() != -1 && key == bfc.getStartKey()) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("StartKey selected, invoking BFScriptingContainerChunk()");
                    }
                    clearKeyStack();
                    addToKeyList(new BFKeyListElem(key, BFScriptingContainerChunk.class));
                    ret = parseAllFromKeyFIFO(false);
                } else {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Unknown dataType: " + dataType);
                    }
                }
        }

        return ret;
    }

    public ParsedObjectDatabaseClass getParsedObjectDatabaseClass() {
        return objdb;
    }

    public void clearKeyStack() {
        if (keyList != null) {
            keyList.clear();
        }
    }

    public Boolean clearParsedObjectDatabase() {
        if (objdb != null) {
            objdb.clear();
        }
        return true;
    }

    public boolean removeKeyFromParsedObjectDatabase(Integer key) {
        if (objdb != null) { // TODO: van sajat metodusunk erre.
            return objdb.removeKey(key);
        }
        return false;
    }

    public boolean parseAllFromKeyFIFO(Boolean buildFromParsed) {

        // KESZ: 62001aa2-t úgy ki kéne hagyni, hogy meg nem is parseolta (jade.spe)
        boolean speCheck = false;

        if (key != IrisToolDefines.getMainBINFileID() && spe != null && spe.isParsed()) {
            speCheck = true;
        }

        boolean ret = false;
        Object parser;

        if (al == null) {
            al = new ArrayList<>();
        }

        if (objdb == null) {
            objdb = new ParsedObjectDatabaseClass();
        }

        while (hasKeyInKeyList()) {
            BFKeyListElem kle = getFromKeyList();
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Parsing key: %X", kle.getKey());
            }
            if (speCheck && spe.hasKey(kle.getKey())) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Key is in SPE, ignoring.");
                }
                kle.setHasInSPE(true);
                ret = true;
                continue;
            }

            if (getParsedObjectDatabaseClass().hasKey(kle.getKey())) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Already processed key, ignoring.");
                }
                kle.setParseHandler(getParsedObjectDatabaseClass().getObject(kle.getKey()));
                ret = true;
                continue;
            }

            if (!insideBinFile) {
                if (!openKey(kle.getKey())) {
                    return false;
                }
            }

            TreeMap<Integer, BFGaoChunk> tm;

            parser = kle.getParseHandler();
            if (parser == null) {
                kle.createParserObject();
                parser = kle.getParseHandler();
            }
            if (parser != null && parser instanceof BFChunkParseInterface) {
                ((BFChunkParseInterface) parser).setSelfKey(kle.getKey());
                ret = ((BFChunkParseInterface) parser).parse(this, insideBinFile, buildFromParsed);
                if (!ret) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("Parser failed for key: %X (@ pos: %d (%x))", kle.getKey(), ru.getCurrentPos(), ru.getCurrentPos());
                    }
                    break;
                }

                if (kle.getStoreIntoDatabase()) { // Add successfully parsed GAO to ParsedObjectDatabaseClass
                    getParsedObjectDatabaseClass().addElem(kle.getKey(), (BFChunkParseInterface) parser);
                }

                if (parser instanceof BFGaoChunk) {
                    tm = new TreeMap<>();
                    tm.put(kle.getKey(), (BFGaoChunk) parser);
                    al.add(tm);
                }

            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Parser handler is null or does not implement interface: %X", kle.getKey());
                }
            }
        }
        return ret;
    }

    public ArrayList<TreeMap<Integer, BFGaoChunk>> getGaos() {
        return al;
    }

    public void clearGaos() {
        if (al == null) {
            return;
        }
        al.clear();
    }

    public ReaderUtil getReaderUtil() {
        return ru;
    }

    public WriterUtil getWriterUtil() {
        return wu;
    }

    public LoadedSoundsClass getLoadedSoundsClass() {
        return lsc;
    }

    public TextureDatabase getTextureDatabase() {
        return td;
    }

    public void addToKeyList(BFKeyListElem kle) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("addToKeyList() adding key: %X", kle.getKey());
        }
        keyList.offer(kle);
    }

    public BFKeyListElem getFromKeyList() {
        return keyList.poll();
    }

    public boolean hasKeyInKeyList() {
        return !keyList.isEmpty();
    }

}
