/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Texture;

import BFDataFileLib.*;
import BFlib.MaterialSpriteDatabase;
import Common.IrisToolDefines;
import Common.TextureTool;
import java.io.File;

/**
 *
 * @author andras
 *
 * Game console versions of the game parses textures other than PC version.
 * Differences are with parsing in pixeldata loading. There are two major
 * differences: 1, Console versions of the game expect to read all truecolor
 * (type 1) texture data first (in order), while PC version of the game reads
 * them in order. 2, Console versions of the game shares pixel data between
 * paletted images (if selectedtype7keys are same), and read data only once, PC
 * version of the game should share too, but it reads again. (I think it is a
 * bug with PC port)
 *
 * HD version texture bin has a bug, t least one texture data has bytes missing.
 * Almost 1/4 of the texture is missing (block size is smaller than width x
 * height x bitdepth)
 *
 * We can switch between above behaviors by setting
 * IrisToolDefines.setConsoleVersionTextureLoad(...) true or false
 */
public class BFTextureParser extends BFChunkParseClass implements BFChunkParseInterface {

    private MaterialSpriteDatabase msd = new MaterialSpriteDatabase();
    private String saveDir = null;

    public BFTextureParser() {
        super();
    }

    public void setSaveDir(String saveDir) {
        // do not allow to save, if the release is public
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            this.saveDir = saveDir;
        }
    }

    private Boolean parseOnePaletteBlock(BFPaletteElem bpe, Boolean buildFromParsed /*TODO*/) {
        Long len = initParse();
        //TODO: handle non binarized format (seek by key, etc)

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing palette (key: %x)", bpe.getKey());
            log("Block length: " + len);
        }
        if (TextureTool.getChannelNumberFromPaletteSize(len.intValue()) == 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Invalid palette length (%d), returning false.", len);
            }
            return false;
        }
        byte[] ret = ru.readByteArray(len.intValue(), false);
        bpe.setPaletteData(ret);
        return true;
    }

    private Boolean parseOneTextureBlock(BFTextureElem bte, boolean onlyHeader, Boolean buildFromParsed /*TODO*/) {
        Long len = initParse();
        //TODO: handle non binarized format (seek by key, etc)
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing texture: %x", bte.getKey());
            log("Block length: " + len);
        }

        long startPos = ru.getCurrentPos();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Current pos: %d (%x)", startPos, startPos);
        }

        Integer unk1 = ru.readInt(false);
        Short flags = ru.readShort(false);
        Byte textureType = ru.readByte(false);
        Byte textureFormat = ru.readByte(false);
        Short width = ru.readShort(false);
        Short height = ru.readShort(false);
        Integer unk2 = ru.readInt(false);
        Integer fontdescKey = ru.readInt(false);
        Integer unk4 = ru.readInt(false);
        Integer unk5 = ru.readInt(false);
        Integer unk6 = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk1 (ffffffff): %d (%x)", unk1, unk1);
            logF("Flags: %d (%x)", flags, flags);
            logF("Texture type: %d (%x)", textureType, textureType);
            logF("Texture format: %d (%x), bitdepth: %d", textureFormat, textureFormat,
                    TextureTool.getTextureBithDepthFromFormat(textureFormat));
        }
        bte.setTextureType((int) textureType);
        bte.setTextureFormat((int) textureFormat);
        bte.setWidth((int) width);
        bte.setHeight((int) height);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Texture dimension: %dx%d (%xx%x)", width, height, width, height);
            logF("unk2: %d (%x)", unk2, unk2);
            logF("Fontdesc key: %d (%x)", fontdescKey, fontdescKey);
        }
        bte.setFontdescKey(fontdescKey);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk4(CAD01234): %d (%x)", unk4, unk4);
            logF("unk5(00FF00FF): %d (%x)", unk5, unk5);
            logF("unk6(C0DEC0DE): %d (%x)", unk6, unk6);
        }

        if (unk4 == null || unk5 == null || unk6 == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("magic nums read failed.");
            }
            return false;
        }

        if (unk4.equals(0xCAD01234) && unk5.equals(0xFF00FF) && unk6.equals(0xC0DEC0DE)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Texture header has magic nums!");
            }
            if (textureType == 4 || textureType == 5
                    || textureType == 7 || textureType == 9) {
                onlyHeader = false;

                // update dimensions needed
            }
        } else {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Texture header does not have magic nums!");
                logF("Type: %d (%x) => setting textureType to this.", (unk1 >> 28), (unk1 >> 28));
            }
            textureType = (byte) ((unk1 >> 28) & 0xff);
            textureFormat = 0;
            onlyHeader = false;

            // update dimensions not needed
        }

        if (onlyHeader) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Only Header requested, returning.");
            }
            return true;
        }

        Boolean ret = false;

        switch (textureType) {
            case 1:
                ret = parseTextureType1(len, startPos, bte, onlyHeader, buildFromParsed);
                if ((flags.intValue() & 0x40) != 0) {
                    parseFontdesc(bte, buildFromParsed);
                }
                break;
            case 4:
                ret = parseTextureType4(len, bte, buildFromParsed);
                if ((flags.intValue() & 0x40) != 0) {
                    parseFontdesc(bte, buildFromParsed);
                }
                break;
            case 5:
                ret = parseTextureType5(len, bte, buildFromParsed);
                if ((flags.intValue() & 0x40) != 0) {
                    parseFontdesc(bte, buildFromParsed);
                }
                break;
            case 6:
                ret = parseTextureType6(len, bte, buildFromParsed);
                if ((flags.intValue() & 0x40) != 0) {
                    parseFontdesc(bte, buildFromParsed);
                }
                break;
            case 7:
                ret = parseTextureType7(len, bte, buildFromParsed);
                if (!ret) {
                    return false;
                }
                if ((flags.intValue() & 0x40) != 0) {
                    parseFontdesc(bte, buildFromParsed);
                }
                ret = selectTextureType7TextureAndPalette(bte);
                break;
            case 9:
                ret = parseTextureType9(len, bte, buildFromParsed);
                if (!ret) {
                    return false;
                }
                if ((flags.intValue() & 0x40) != 0) {
                    parseFontdesc(bte, buildFromParsed);
                }
                ret = addAnimatedTexturesToTextureDatabase(bte);
                break;
            default:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Texture type %d unhandled.", textureType);
                }
                return true; // CHECK THIS
        }

        return ret;
    }

    private Boolean selectTextureType7TextureAndPalette(BFTextureElem bte) {
        int index = 0;
        int currNum = 0;
        int textureKey = -1;
        int paletteKey = -1;
        int textureWithoutPaletteKey = -1;
        do {
            currNum = IrisToolDefines.getTextureType7ReadOrder()[index];
            if (currNum == -1) {
                return false;
            }
            int currNumPos = currNum * 3;
            textureKey = bte.getType7ElemKey(currNumPos);
            paletteKey = bte.getType7ElemKey(currNumPos + 1);
            textureWithoutPaletteKey = bte.getType7ElemKey(currNumPos + 2);
            if (paletteKey != -1) {
                break;
            }
            if (textureKey != -1) {
                break;
            }
            if (textureWithoutPaletteKey != -1) {
                break;
            }
            index++;
        } while (index < 4);
        if (index == 4) {
            return false;
        }

        if (paletteKey == -1 || (textureWithoutPaletteKey != -1 && IrisToolDefines.getPreferPalettelessTextures())) {
            if (textureWithoutPaletteKey != -1) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Paletteless texture added. Key: %x", textureWithoutPaletteKey);
                }
                bfdfc.getTextureDatabase().addElem(new BFTextureElem(textureWithoutPaletteKey));
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("No usable texture, nothing added.");
                }
            }
        } else {
            BFTextureElem bteSub;
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Paletted texture added. Texture Key: %x, Palette Key: %x", textureKey, paletteKey);
            }
            if (bfdfc.getTextureDatabase().addElem(bteSub = new BFTextureElem(textureKey)) == false
                    && IrisToolDefines.isConsoleVersionTextureLoad()) {
                bte.setConsoleSpecificLinkedBTE(bfdfc.getTextureDatabase().getObjectBySelectedPalettedTextureKey(textureKey, true));
                if (bte.getConsoleSpecificLinkedBTE() == null) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        System.out.println("BUG: Console specific texture parsing: consoleSpecificLinkedBTE is null, expect problems.");
                    }
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Console specific texture parsing: This texture (key: %x) shares pixel data with another texture (key: %x), ignoring pixel data read.",
                            bte.getKey(), bte.getConsoleSpecificLinkedBTE().getKey());
                }
            }
            bfdfc.getTextureDatabase().addElemPalette(new BFPaletteElem(paletteKey));
            bteSub.setSelectedType7DataTextureKey(textureKey);
            bte.setSelectedType7DataTextureKey(textureKey);
            bteSub.setPaletteKey(paletteKey);
            bte.setWidth(bteSub.getWidth());
            bte.setHeight(bteSub.getHeight());
            bte.setTextureFormat(bteSub.getTextureFormat());
            bte.setPaletteKey(bteSub.getPaletteKey());
            bteSub.setIgnoreFromDataParse(true);
        }

        return true;
    }

    private Boolean addAnimatedTexturesToTextureDatabase(BFTextureElem bte) {
        bte.resetAnimatedTextureIterator();
        while (bte.hasNextAnimatedTexture()) {
            BFAnimatedTextureElem bate = bte.nextAnimatedTexture();
            if (!bfdfc.getTextureDatabase().hasKey(bate.getKey())) {
                bfdfc.getTextureDatabase().addElem(new BFTextureElem(bate.getKey()));
            }
        }
        return true;
    }

    private Boolean parseTextureType1(Long len, Long startPos, BFTextureElem bte, Boolean onlyHeader, Boolean buildFromParsed /*TODO*/) {
        Byte unkb1 = ru.readByte(false);
        Byte unkb2 = ru.readByte(false);
        Byte unkb3 = ru.readByte(false);
        Short unks1 = ru.readShort(false);
        Short unks2 = ru.readShort(false);
        Byte unkBitdepth = ru.readByte(false);
        Short unks3 = ru.readShort(false);
        Short unks4 = ru.readShort(false);
        Short width = ru.readShort(false);
        Short height = ru.readShort(false);
        Byte bitdepth = ru.readByte(false);
        Byte unkb5 = ru.readByte(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unkb1: %d (%x)", unkb1, unkb1);
            logF("unkb2: %d (%x)", unkb2, unkb2);
            logF("unkb3: %d (%x)", unkb3, unkb3);
            logF("unks1: %d (%x)", unks1, unks1);
            logF("unks2: %d (%x)", unks2, unks2);
            logF("unkBitdepth: %d (%x)", unkBitdepth, unkBitdepth);
            logF("unks3: %d (%x)", unks3, unks3);
            logF("unks4: %d (%x)", unks4, unks4);
            logF("Dimension: %dx%d (%xx%x)", width, height, width, height);
            logF("Bitdepth: %d (%x)", bitdepth, bitdepth);
            logF("unkb5: %d (%x)", unkb5, unkb5);
        }

        if (!onlyHeader) {
            if (!insideBinFile) {
                ru.readByteArray(unks2 * (unkBitdepth / 8), false);
            }
            int size = width * height * (bitdepth / 8);
            if (unkb3 >= 9) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("unkb3 >= 9, this is unhandled.");
                }
                return false;
            }

            // sanity check (workaround broken texture in HD edition)
            long maxReadSize = (len - (ru.getCurrentPos() - startPos));

            byte data[];
            byte dataTmp[];

            if (maxReadSize < size) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("BUG: expected size (w x h x d) [%d] > block size [%d], limiting read to block end.", size, maxReadSize);
                }
                data = new byte[size];
                size = (int) Math.min(maxReadSize, (long) size);
                dataTmp = ru.readByteArray(size, false);
                System.arraycopy(dataTmp, 0, data, 0, dataTmp.length);
            } else {
                data = ru.readByteArray(size, false);
            }
            // sanity check end

            if (bitdepth.intValue() == 32 || bitdepth.intValue() == 24) {
                bte.setWidth((int) width); // pass dimension
                bte.setHeight((int) height);
                bte.setTrueColorBitDepth((int) bitdepth);
                bte.setPixelData(data);
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Currently only 32 or 24 bit textures are supported, so pixeldata can not passed.");
                }
            }
        }

        return true;
    }

    private Boolean parseTextureType4(Long len, BFTextureElem bte, Boolean buildFromParsed /*TODO*/) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing material sprite texture (type 4), Key: %x.", bte.getKey());
        }
        long startPos = ru.getCurrentPos();

        BFTextureElem newBte;

        if (msd.hasKey(bte.getKey())) {
            Integer refKey = msd.getRefKeyForKey(bte.getKey());
            if (refKey == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("refKey is null.");
                }
                return false;
            }
            if (bfdfc.getTextureDatabase().hasKey(refKey)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key %x already in TextureDatabase, ignoring.", refKey);
                }
                return true;
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Adding Key %x to TextureDatabase.", refKey);
                }
                bfdfc.getTextureDatabase().addElem(newBte = new BFTextureElem(refKey));
            }
            newBte.setIgnoreFromDataParse(true);
            return true;
        } else {
            if ((len - 32) <= 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("len - 32 <= 0 [%d], returning.", len - 32);
                }
                return true;
            }
            Integer c0de1999 = ru.readInt(false);
            Integer c0de2000 = ru.readInt(false);
            Integer texkey = ru.readInt(false);
            Short unks1 = ru.readShort(false);
            Short texid = ru.readShort(false);
            Float unkf1 = ru.readFloat(false);
            Float unkf2 = ru.readFloat(false);
            Integer unk4 = ru.readInt(false);
            Float unkf3 = ru.readFloat(false);
            Float unkf4 = ru.readFloat(false);
            Float unkf5 = ru.readFloat(false);
            Float unkf6 = ru.readFloat(false);
            Integer unk5 = ru.readInt(false);
            Integer unk6 = ru.readInt(false);
            Float unkf7 = ru.readFloat(false);
            Integer unk7 = ru.readInt(false);
            Integer unk8 = ru.readInt(false);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("c0de1999: %d (%x)", c0de1999, c0de1999);
                logF("c0de2000: %d (%x)", c0de2000, c0de2000);
                logF("Texture key: %d (%x)", texkey, texkey);
                logF("unks1: %d (%x)", unks1, unks1);
                logF("texid: %d (%x)", texid, texid);
                logF("unkf1: %f", unkf1);
                logF("unkf2: %f", unkf2);
                logF("unk4: %d (%x)", unk4, unk4);
                logF("unkf3: %f", unkf3);
                logF("unkf4: %f", unkf4);
                logF("unkf5: %f", unkf5);
                logF("unkf6: %f", unkf6);
                logF("unk5: %d (%x)", unk5, unk5);
                logF("unk6: %d (%x)", unk6, unk6);
                logF("unkf7: %f", unkf7);
                logF("unk7: %d (%x)", unk7, unk7);
                logF("unk8: %d (%x)", unk8, unk8);
            }

            msd.addElem(bte.getKey(), texkey);

            if (texkey.equals(-1)) {
                log("TexKey is null.");
                return true;
            }
            // HACK!!
            //bte.setIgnoreFromDataParse(true);
            if (!(bfdfc.getTextureDatabase().hasKey(texkey))) {
                bfdfc.getTextureDatabase().addElem(newBte = new BFTextureElem(texkey));
                newBte.setIgnoreFromDataParse(true);
            }

        }
        return true;
    }

    private Boolean parseTextureType5(Long len, BFTextureElem bte, Boolean buildFromParsed /*TODO*/) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing dynamic texture (type 5), Key: %x.", bte.getKey());
        }
        int dynamicTextureType = 0;
        int partSize = 0;
        int width = 128;
        int height = 128;
        long startPos = ru.getCurrentPos();
        if ((len - 32) > 0) {
            partSize = ru.readInt(false);
            int unks1 = ru.readShort(false);
            width = ru.readShort(false);
            height = ru.readShort(false);
            dynamicTextureType = ru.readShort(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("partSize: %d (%x)", partSize, partSize);
                logF("unks1: %d (%x)", unks1, unks1);
                logF("Width: %d (%x)", width, width);
                logF("Height: %d (%x)", height, height);
                logF("Dynamic texture type: %d (%x)", dynamicTextureType, dynamicTextureType);
            }
            //ru.seek((int) (startPos + (len - 32)));
            bte.setIgnoreFromDataParse(true);
        }

        Boolean ret = false;
        switch (dynamicTextureType) {
            case 3:
                ret = handleDynamicTextureType3((int) (len - 32 - partSize), buildFromParsed);
                break;
            case 4:
                ret = handleDynamicTextureType4((int) (len - 32 - partSize), buildFromParsed);
                break;
            default:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Dynamic texture type: %d unhandled.", dynamicTextureType);
                }
                return false;
        }

        return ret;
    }

    private Boolean parseTextureType6(Long len, BFTextureElem bte, Boolean buildFromParsed /*TODO*/) {
        if ((bte.getHeight() * bte.getWidth()) == 0) {
            return false;
        }

        int pixelWidth = TextureTool.getTextureBithDepthFromFormat(bte.getTextureFormat());

        if (pixelWidth == 0) {
            return false;
        }

        int size = (bte.getHeight() * bte.getWidth() * pixelWidth) / 8;
        if (size != (len - 32)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Size check failed: size=%d != len-32=%d", size, len - 32);
            }
            return false;
        }
        byte data[] = ru.readByteArray(size, false);
        bte.setPixelData(data);

        return true;
    }

    private Boolean parseTextureType7(Long len, BFTextureElem bte, Boolean buildFromParsed /*TODO*/) {
        if (len > 80 || (len & 3) != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Type 7 length check failed.");
            }
            return false;
        }

        long num = (len - 32) / 4; // 32 is the already read bytes in parseOneTextureBlock()

        Integer key;

        while (num-- > 0) {
            key = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Key: %x", key);
            }
            bte.addToType7Data(key);
        }

        return true;
    }

    private Boolean parseTextureType9(Long len, BFTextureElem bte, Boolean buildFromParsed /*TODO*/) {
        if (len - 32 <= 0) {
            return true;
        }

        int unk1 = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk1: %d (%x)", unk1, unk1);
        }
        short unks1 = ru.readShort(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unks1: %d (%x)", unks1, unks1);
        }
        byte num = ru.readByte(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Animated texture count: %d (%x)", num, num);
        }
        if (!insideBinFile) {
            byte unkb1 = ru.readByte(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unkb1 (!insidebinfile): %d (%x)", unkb1, unkb1);
            }
        }
        int i;
        int animTexKey;
        short unks2;
        for (i = 0; i < num; i++) {
            animTexKey = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Texture key [%d]: %x (%d)", i, animTexKey, animTexKey);
            }
            unks2 = ru.readShort(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unks2 [%d]: %d (%x)", i, unks2, unks2);
            }
            if (!insideBinFile) {
                byte unks3 = ru.readByte(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("unks3 (!insidebinfile) [%d]: %d (%x)", i, unks3, unks3);
                }
            }
            bte.addAnimatedTextureElem(new BFAnimatedTextureElem(animTexKey, unks2));
        }

        return true;
    }

    private Boolean parseFontdesc(BFTextureElem bte, Boolean buildFromParsed /*TODO*/) {
        // TODO: handle !insidebinfile.
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing FONTDESC (Key: %x) for Texture Key: %x", bte.getFontdescKey(), bte.getKey());
        }
        Long len = initParse();
        //TODO: handle non binarized format (seek by key, etc)
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Block length: " + len);
        }
        long startPos = ru.getCurrentPos();
        if (len < 12) {
            return false;
        }

        String fontdesc = ru.readNString(8, false);

        if (!"FONTDESC".equals(fontdesc)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("FONTDESC check failed: %s", fontdesc);
            }
            return false;
        }

        long posBak = ru.getCurrentPos();

        if (!ru.seek((startPos + len - 4))) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Seek to last elem pos failed.");
            }
            return false;
        }
        int lastElem = ru.readInt(false);
        if (lastElem != -1) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Last elem is not -1: %d", lastElem);
            }
            return false;
        }

        if (!ru.seek(posBak)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Backseek failed.");
            }
            return false;
        }

        Integer unk1 = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Unk1: %d (%x)", unk1, unk1);
        }

        int glypthID;
        Float pos1, pos2, pos3, pos4;

        while ((ru.getCurrentPos() - 4) < (len + startPos)) {
            glypthID = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Glypth ID: %d (%x)", glypthID, glypthID);
            }
            if (glypthID > 65535 /*255*/ || glypthID < 32) { // the new version has korean and chinese translation, and the range is wider. 
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Glypth ID is not between 32 and 65535, returning.");
                }                
                return true;
            }
            pos1 = ru.readFloat(false);
            pos2 = ru.readFloat(false);
            pos3 = ru.readFloat(false);
            pos4 = ru.readFloat(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Glypth ID: %d [pos1: %f, pos2: %f, pos3: %f, pos4: %f]", glypthID, pos1, pos2, pos3, pos4);
            }
        }

        lastElem = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Last elem read again: %d", lastElem);
        }

        return true;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }

        if (bfdfc.getTextureDatabase() == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("TextureDatabase must be supplied first.");
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("%d textures are about to parse.", bfdfc.getTextureDatabase().getTextureCount());
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("-- Parsing texture headers --");
        }
        bfdfc.getTextureDatabase().resetTextureIterator();
        BFTextureElem bte;
        while (bfdfc.getTextureDatabase().hasNext()) {
            bte = bfdfc.getTextureDatabase().next();
            if (!parseOneTextureBlock(bte, true, buildFromParsed)) {
                return false;
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("%d textures are about to parse (after header read).", bfdfc.getTextureDatabase().getTextureCount());
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("-- Parsing texture palettes --");
        }
        bfdfc.getTextureDatabase().resetPaletteIterator();
        BFPaletteElem bpe;
        while (bfdfc.getTextureDatabase().hasNextPalette()) {
            bpe = bfdfc.getTextureDatabase().nextPalette();
            if (!parseOnePaletteBlock(bpe, buildFromParsed)) {
                return false;
            }
        }

        bfdfc.getTextureDatabase().resetTextureIterator();
        while (bfdfc.getTextureDatabase().hasNext()) {
            bte = bfdfc.getTextureDatabase().next();
            if (bte.getIgnoreFromDataParse() || bte.getTextureType() != 7) {
                continue;
            }
            Integer texKey = bte.getSelectedType7DataTextureKey();
            if (texKey != null && !texKey.equals(-1)) {
                BFTextureElem subBte = bfdfc.getTextureDatabase().getObject(texKey);
                if (subBte != null && !subBte.getIgnoreFromDataParse()) {
                    bte.setIgnoreFromDataParse(true); // Ha nem palettás a kép, akkor a containerjét (type7) dobjuk el
                    // ha palettás, akkor meg a subBte-t.
                }
            }
        }

        bfdfc.getTextureDatabase().resetTextureIterator();

        File f;
        if (saveDir != null) {
            f = new File(saveDir);
            if (!f.exists() || !f.isDirectory()) {
                if (!f.mkdir()) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("Can not create directory " + saveDir + " for texture dumping.");
                    }
                    saveDir = null;
                }
            }
            f = null;
        }

        // Console texture pack load 
        if (IrisToolDefines.isConsoleVersionTextureLoad()) {
            while (bfdfc.getTextureDatabase().hasNext()) {
                bte = bfdfc.getTextureDatabase().next();
                if (bte.getIgnoreFromDataParse()) {
                    continue;
                }
                if (bte.getTextureType().equals(1)) { // truecolor texture
                    if (!parseOneTextureBlock(bte, false, buildFromParsed)) {
                        return false;
                    }
                    if (!doConversion(bte, true)) {
                        return false;
                    }
                }
            }
        }

        bfdfc.getTextureDatabase().resetTextureIterator();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("-- Parsing and converting texture data --");
        }
        while (bfdfc.getTextureDatabase().hasNext()) {
            bte = bfdfc.getTextureDatabase().next();

            // Console texture pack load            
            if (IrisToolDefines.isConsoleVersionTextureLoad() && bte.getTextureType().equals(1)) {
                // in this case type 1 (rgb) textures load handled above
                continue;
            }

            // HACK: Elvileg ez jo ez igy, ugyanis, texturat nem kell olvasni a faljbol. (0x20 flag, lasd: [****] offset 43DB90)
            if (bte.getTextureType().equals(4)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Ignoring texture read for type 4 texture (key: %x). Game does this too.", bte.getKey());
                }
                continue;
            }
            // HACK: Elvileg ez is jo ez igy, 0x20 lesz beallitva ra a jatekban
            if (bte.getTextureType().equals(9)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Ignoring texture read for type 9 texture (key: %x). Game does this too.", bte.getKey());
                }
                continue;
            }
            if (bte.getIgnoreFromDataParse()) {
                continue;
            }

            if (IrisToolDefines.isConsoleVersionTextureLoad() && bte.getConsoleSpecificLinkedBTE() != null) {
                bte.setPixelData(bte.getConsoleSpecificLinkedBTE().getPixelData());
                bte.setWidth(bte.getConsoleSpecificLinkedBTE().getWidth());
                bte.setHeight(bte.getConsoleSpecificLinkedBTE().getHeight());
                bte.setTextureFormat(bte.getConsoleSpecificLinkedBTE().getTextureFormat());
                if (!doConversion(bte, false)) {
                    return false;
                }
                continue;
            }

            if (!parseOneTextureBlock(bte, false, buildFromParsed)) {
                return false;
            }

            if (!doConversion(bte, false)) {
                return false;
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Finished at pos: %x", ru.getCurrentPos());
        }

        return true;
    }

    private Boolean doConversion(BFTextureElem bte, Boolean onlyIfTrueColorTexture) {
        BFPaletteElem bpe;
        byte[] convertedPixelData = null;
        TextureTool tt = new TextureTool();

        if (!onlyIfTrueColorTexture) {
            if (bte.getPaletteKey() != null && bte.getPaletteKey() != -1) {
                bpe = bfdfc.getTextureDatabase().getObjectPalette(bte.getPaletteKey());
                try {
                    if (bpe == null) {
                        throw new Exception("palette not found");
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("Using Palette: Key: %x", bpe.getKey());
                    }
                    convertedPixelData = tt.makePalettedImage(bte.getPixelData(), bpe.getPaletteData(), bte.getWidth(), bte.getHeight(), bte.getTextureFormat(),
                            (saveDir != null ? (saveDir + File.separator + String.format("%08x_P.png", bte.getKey())) : null));
                } catch (Exception e) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("makePalettedImage failed: " + e.getLocalizedMessage());
                    }
                    return false;
                }
            }
        }
        if (bte.getTextureType().equals(1)) { // truecolor texture
            try {
                convertedPixelData = tt.makeTrueColorImage(bte.getPixelData(), bte.getWidth(), bte.getHeight(), bte.getTrueColorBitDepth(),
                        (saveDir != null ? (saveDir + File.separator + String.format("%08x_T.png", bte.getKey())) : null));
            } catch (Exception e) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("makeTrueColorImage failed: " + e.getLocalizedMessage());
                }
                return false;
            }
        }
        bte.setConvertedPixelData(convertedPixelData);
        return true;
    }

    private Boolean handleDynamicTextureType3(Integer size, Boolean buildFromParsed /*TODO*/) {
        if (size == null) {
            size = 0;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Calculated size: %d (%x)", size, size);
        }

        if (!size.equals(0)) {
            int partSize = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1: %d (%x)", partSize, partSize);
            }
            if (!size.equals(partSize)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Size (%d) != partSize (%d), returning.", size, partSize);
                }
                return true;
            } else {
                int key = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key: %x", key);
                }
                switch (size) {
                    case 8: // Nothing to read here.
                        break;
                    case 32:
                        int unk30_20 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk30_20: %d (%x)", unk30_20, unk30_20);
                        }
                        int unk30_24 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk30_24: %d (%x)", unk30_24, unk30_24);
                        }
                        int unk30_36 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk30_36: %d (%x)", unk30_36, unk30_36);
                        }
                        int unk30_40 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk30_40: %d (%x)", unk30_40, unk30_40);
                        }
                        int unk30_32 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk30_32: %d (%x)", unk30_32, unk30_32);
                        }
                        int unk30_28 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk30_28: %d (%x)", unk30_28, unk30_28);
                        }
                        break;
                    case 48:
                        int unk48_20 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_20: %d (%x)", unk48_20, unk48_20);
                        }
                        int unk48_24 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_24: %d (%x)", unk48_24, unk48_24);
                        }
                        int unk48_36 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_36: %d (%x)", unk48_36, unk48_36);
                        }
                        int unk48_40 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_40: %d (%x)", unk48_40, unk48_40);
                        }
                        int unk48_32 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_32: %d (%x)", unk48_32, unk48_32);
                        }
                        int unk48_28 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_28: %d (%x)", unk48_28, unk48_28);
                        }
                        int unk48_92 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_92: %d (%x)", unk48_92, unk48_92);
                        }
                        int unk48_80 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_80: %d (%x)", unk48_80, unk48_80);
                        }
                        int unk48_84 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_84: %d (%x)", unk48_84, unk48_84);
                        }
                        int unk48_88 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unk48_88: %d (%x)", unk48_88, unk48_88);
                        }
                        break;
                    default:
                        int unkdef_20 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_20: %d (%x)", unkdef_20, unkdef_20);
                        }
                        int unkdef_24 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_24: %d (%x)", unkdef_24, unkdef_24);
                        }
                        int unkdef_36 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_36: %d (%x)", unkdef_36, unkdef_36);
                        }
                        int unkdef_40 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_40: %d (%x)", unkdef_40, unkdef_40);
                        }
                        int unkdef_32 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_32: %d (%x)", unkdef_32, unkdef_32);
                        }
                        int unkdef_28 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_28: %d (%x)", unkdef_28, unkdef_28);
                        }
                        int unkdef_92 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_92: %d (%x)", unkdef_92, unkdef_92);
                        }
                        int unkdef_80 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_80: %d (%x)", unkdef_80, unkdef_80);
                        }
                        int unkdef_84 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_84: %d (%x)", unkdef_84, unkdef_84);
                        }
                        int unkdef_88 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_88: %d (%x)", unkdef_88, unkdef_88);
                        }
                        int unkdef_100 = ru.readInt(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdef_100: %d (%x)", unkdef_100, unkdef_100);
                        }
                        break;
                }
                // TODO: handle !insidebinfile key position & load
                int unkSize = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Key: %x => unkSize: %d (%x)", key, unkSize, unkSize);
                }
                // TODO: probably !insidebinfile read here 2048 bytes min!
                return true;
            }
        } else {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Size is 0, returning.");
            }
            return true;
        }
    }

    private Boolean handleDynamicTextureType4(Integer size, Boolean buildFromParsed /*TODO*/) {
        if (size == null) {
            size = 0;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Calculated size: %d (%x)", size, size);
        }

        if (!size.equals(0)) {
            if (!insideBinFile) {
                int unk1 = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("unk1 (!insidebinfile): %d (%x)", unk1, unk1);
                }
            }
            byte unkb = ru.readByte(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unkb: %d (%x)", unkb, unkb);
            }
        }
        return true;
    }
}
