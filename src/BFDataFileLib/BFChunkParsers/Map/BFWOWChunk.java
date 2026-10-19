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
import Common.DataType.Matrix4x4;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFWOWChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFWOWChunk() {
        super();
    }

    private Integer gaoListKey;
    private BFKeyListElem gaoListChunkKLE;
    private BFKeyListElem grid1ChunkKLE;
    private BFKeyListElem grid2ChunkKLE;
    private BFKeyListElem wayGroupChunkKLE;
    private String wowName;
    private String wowNameOriginal;
    private Long wowLen = 0L;
    private String signature = "";
    private int version = 0;
    private int unknownNum = 0;
    private int ambientColor1 = 0;
    private Integer unknbi1 = 0;
    private Matrix4x4 mtx = new Matrix4x4();
    private float unknownFloat = 0.0f;
    private int clearColor = 0;
    private int ambientColor2 = 0;
    private int unknownNum3 = 0;
    private String unknbs1 = "";
    private int grid1Key = 0;
    private int grid2Key = 0;
    private int wayKey = 0;
    private int textsKey = 0;

    public Matrix4x4 getMtx() {
        return mtx;
    }

    public BFGaoListChunk getGaoList() {
        return (BFGaoListChunk) gaoListChunkKLE.getParseHandler(); // Ennek a key-e nincs bent a ParsedObjectDatabase-ben, mert a játék se teszi bele, így nem kell meggyőződni, 
        // hogy több ilyen key miatt érvényes gaolistet használjuk-e.
    }

    public String getWowName() {
        if (wowName == null) {
            return "<name not read>";
        }
        return wowName;
    }

    public String getWowNameOriginal() {
        if (wowNameOriginal == null) {
            return "<name not read>";
        }
        return wowNameOriginal;
    }

    public void setWowName(String wowName) {
        this.wowName = wowName;
    }

    public int getAmbientColor1() {
        return ambientColor1;
    }

    public void setAmbientColor1(int ambientColor1) {
        this.ambientColor1 = ambientColor1;
    }

    public int getClearColor() {
        return clearColor;
    }

    public void setClearColor(int clearColor) {
        this.clearColor = clearColor;
    }

    public int getAmbientColor2() {
        return ambientColor2;
    }

    public void setAmbientColor2(int ambientColor2) {
        this.ambientColor2 = ambientColor2;
    }
       
    /*public Integer getGaoListKey() {
     return gaoListKey;
     }*/
    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing WOW.");
        }

        if (buildFromParsed) {
            if (insideBinFile) {
                wu.startBlockWithLength();
            }
        } else {
            wowLen = initParse();
        }

        if (wowLen == null) {
            return false;
        }

        long startPos = 0;

        if (!buildFromParsed) {
            startPos = ru.getCurrentPos();

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("WOW length: " + wowLen + " bytes.");
            }
        }

        if (!buildFromParsed) {
            signature = ru.readNString(4, false);
            version = ru.readInt(false);
        } else {
            wu.writeNString(signature, 4);
            wu.writeInt(version);
        }

        if (!".wow".equals(signature)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("signature is not .wow: " + signature);
            }
            return false;
        }

        if (version != 3) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("version expected to be 3. It is : " + version);
            }
            return false;
        }

        if (!buildFromParsed) {
            unknownNum = ru.readInt(false);
            ambientColor1 = ru.readInt(false);
        } else {
            wu.writeInt(unknownNum);
            wu.writeInt(ambientColor1);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Unknown num: %d (%x)", unknownNum, unknownNum);
            logF("Ambient Color 1: %x", ambientColor1);
        }

        if (!buildFromParsed) {
            wowName = ru.readNString(60, false, true);
            wowNameOriginal = wowName;
            if (wowName != null) {
                wowName = wowName.trim();
            }
        } else {
            wu.writeNString(wowNameOriginal, 60);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("WOW name: " + wowName);
        }

        if (!insideBinFile) {
            if (!buildFromParsed) {
                unknbi1 = ru.readInt(false);
            } else {
                wu.writeInt(unknbi1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Unknown integer (!insidebinfile): %d (%x)", unknbi1, unknbi1);
            }
        }

        if (!buildFromParsed && !mtx.readValues(ru)) {
            return false;
        } else if (buildFromParsed) {
            mtx.writeValues(wu);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log(mtx);
        }

        if (!buildFromParsed) {
            unknownFloat = ru.readFloat(false); // maybe FOV
        } else {
            wu.writeFloat(unknownFloat);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Unknown float: " + unknownFloat);
        }

        if (!buildFromParsed) {
            clearColor = ru.readInt(false);
            ambientColor2 = ru.readInt(false);
            unknownNum3 = ru.readInt(false);
        } else {
            wu.writeInt(clearColor);
            wu.writeInt(ambientColor2);
            wu.writeInt(unknownNum3);
        }
        if (!insideBinFile) {
            if (!buildFromParsed) {
                unknbs1 = ru.readNString(44, false);
            } else {
                wu.writeNString(unknbs1, 44);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Unknown string (!insidebinfile): %s", unknbs1);
            }
        }
        if (!buildFromParsed) {
            grid1Key = ru.readInt(false);
            grid2Key = ru.readInt(false);
            gaoListKey = ru.readInt(false);
            wayKey = ru.readInt(false);
            textsKey = ru.readInt(false);
        } else {
            wu.writeInt(grid1Key);
            wu.writeInt(grid2Key);
            wu.writeInt(gaoListKey);
            wu.writeInt(wayKey);
            wu.writeInt(textsKey);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Clear Color: %x", clearColor);
            logF("Ambient Color 2: %x", ambientColor2);
            logF("Unknown num3: %d (%x)", unknownNum3, unknownNum3);
            logF("Grid1 Key: %x", grid1Key);
        }
        if (grid1Key != 0 && grid1Key != -1) {
            if (!buildFromParsed) {
                grid1ChunkKLE = new BFKeyListElem(grid1Key, BFGridChunk.class);
            }
            bfdfc.addToKeyList(grid1ChunkKLE);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Grid2 Key: %x", grid2Key);
        }
        if (grid2Key != 0 && grid2Key != -1) {
            if (!buildFromParsed) {
                grid2ChunkKLE = new BFKeyListElem(grid2Key, BFGridChunk.class);
            }
            bfdfc.addToKeyList(grid2ChunkKLE);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("GAO List Key: %x", gaoListKey);
        }
        if (!buildFromParsed) {
            gaoListChunkKLE = new BFKeyListElem(gaoListKey, BFGaoListChunk.class, false);
        }
        bfdfc.addToKeyList(gaoListChunkKLE);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("WAY Key: %x", wayKey);
        }
        if (wayKey != 0 && wayKey != -1) {
            if (!buildFromParsed) {
                wayGroupChunkKLE = new BFKeyListElem(wayKey, BFWAYGroupChunk.class);
            }
            bfdfc.addToKeyList(wayGroupChunkKLE);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Texts Key: %x", textsKey);
        }

        if (!buildFromParsed) {
            long parsedLen = (ru.getCurrentPos() - startPos);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length: " + parsedLen);
            }

            if (parsedLen != wowLen) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Parsed length != length");
                }
                return false;
            }
        }

        if (buildFromParsed) {
            if (insideBinFile) {
                wu.finishBlockWithLength();
            }
        }
        return true;
    }
}
