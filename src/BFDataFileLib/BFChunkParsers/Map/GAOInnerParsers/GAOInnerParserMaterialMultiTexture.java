/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFTextureElem;
import Common.DataType.MaterialMultiTextureSubElem;
import Common.IrisToolDefines;
import Common.MaterialInterface;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class GAOInnerParserMaterialMultiTexture extends BFChunkParseClass implements BFChunkParseInterface, MaterialInterface {

    public GAOInnerParserMaterialMultiTexture() {
        super();
    }

    private ArrayList<MaterialMultiTextureSubElem> mtSubArr;
    private Integer unk1, unk2, vertexColor, unk4, memoryAddr, flags;
    private Float unkf1, unkf2;

    public Integer getUnk1() {
        return unk1;
    }

    public Integer getUnk2() {
        return unk2;
    }

    public Integer getVertexColor() {
        return vertexColor;
    }

    public Integer getUnk4() {
        return unk4;
    }

    public Integer getMemoryAddr() {
        return memoryAddr;
    }

    @Override
    public Integer getFlags() {
        return flags;
    }

    public Float getUnkf1() {
        return unkf1;
    }

    public Float getUnkf2() {
        return unkf2;
    }

    public ArrayList<MaterialMultiTextureSubElem> getMtSubArr() { // TODO: ezt innen ki kene szedni
        return mtSubArr;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner Material MultiTexture Chunk.");
        }

        if (!buildFromParsed) {
            mtSubArr = new ArrayList<>();

            unk1 = ru.readInt(false);
            unk2 = ru.readInt(false);
            vertexColor = ru.readInt(false);
            unkf1 = ru.readFloat(false);
            unkf2 = ru.readFloat(false);
            unk4 = ru.readInt(false);
            memoryAddr = ru.readInt(false);
            flags = ru.readInt(false);
        } else {
            wu.writeInt(unk1);
            wu.writeInt(unk2);
            wu.writeInt(vertexColor);
            wu.writeFloat(unkf1);
            wu.writeFloat(unkf2);
            wu.writeInt(unk4);
            wu.writeInt(memoryAddr);
            wu.writeInt(flags);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk1: %d (%x)", unk1, unk1);
            logF("unk2: %d (%x)", unk2, unk2);
            logF("Vertexcolor: %d (%x)", vertexColor, vertexColor);

            logF("unkf1: %f", unkf1);
            logF("unkf2: %f", unkf2);

            logF("unk4: %d (%x)", unk4, unk4);
            logF("Memory address (to alloc SubTexture): %x", memoryAddr);
            logF("Flags: %d (%x) [%s]", flags, flags, MaterialInterface.getFlagsMeaning(flags));
        }

        int i = 0;

        if (memoryAddr != 0) {
            MaterialMultiTextureSubElem mmtse;
            do {
                if (!buildFromParsed) {
                    mmtse = new MaterialMultiTextureSubElem();
                    mtSubArr.add(mmtse);
                    if (!mmtse.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("MaterialMultitextureSubElem read failed.");
                        }
                        return false;
                    }
                } else {
                    mmtse = mtSubArr.get(i++);
                    mmtse.writeValues(wu);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(mmtse);
                }
                if (!mmtse.getTextureKey().equals(0xffffffff)
                        && bfdfc.getTextureDatabase() != null
                        && !bfdfc.getTextureDatabase().hasKey(mmtse.getTextureKey())) {
                    bfdfc.getTextureDatabase().addElem(new BFTextureElem(mmtse.getTextureKey()));
                }
            } while (mmtse.getHasNext() != 0);
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("-- Multitexture --\n");
        sb.append(String.format("unk1: %d (%x)\n", unk1, unk1));
        sb.append(String.format("unk2: %d (%x)\n", unk2, unk2));
        sb.append(String.format("Vertexcolor: %d (%x)\n", vertexColor, vertexColor));

        sb.append(String.format("unkf1: %f\n", unkf1));
        sb.append(String.format("unkf2: %f\n", unkf2));

        sb.append(String.format("unk4: %d (%x)\n", unk4, unk4));
        sb.append(String.format("Memory address (to alloc SubTexture): %x\n", memoryAddr));
        sb.append(String.format("Flags: %d (%x) [%s]\n", flags, flags, MaterialInterface.getFlagsMeaning(flags)));
        if (mtSubArr != null) {
            int i = 0;
            for (MaterialMultiTextureSubElem mmtse : mtSubArr) {
                sb.append("-- Material elem #").append(++i).append(" --\n");
                sb.append(mmtse.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
