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
import Common.IrisToolDefines;
import Common.MaterialInterface;

/**
 *
 * @author andras
 */
public class GAOInnerParserMaterialSingleTexture extends BFChunkParseClass implements BFChunkParseInterface, MaterialInterface {

    private Integer unk1, unk2, vertexColor, unk4, textureKey, flags;
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

    public Integer getTextureKey() {
        return textureKey;
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

    public GAOInnerParserMaterialSingleTexture() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner Material SingleTexture Chunk.");
        }

        if (!buildFromParsed) {
            unk1 = ru.readInt(false);
            unk2 = ru.readInt(false);
            vertexColor = ru.readInt(false);
            unkf1 = ru.readFloat(false);
            unkf2 = ru.readFloat(false);
            unk4 = ru.readInt(false);
            textureKey = ru.readInt(false);
            flags = ru.readInt(false);
        } else {
            wu.writeInt(unk1);
            wu.writeInt(unk2);
            wu.writeInt(vertexColor);
            wu.writeFloat(unkf1);
            wu.writeFloat(unkf2);
            wu.writeInt(unk4);
            wu.writeInt(textureKey);
            wu.writeInt(flags);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk1: %d (%x)", unk1, unk1);
            logF("unk2: %d (%x)", unk2, unk2);
            logF("VertexColor: %x", vertexColor);

            logF("unkf1: %f", unkf1);
            logF("unkf2: %f", unkf2);

            logF("unk4: %d (%x)", unk4, unk4);
            logF("Texture Key: %x", textureKey);
            logF("Flags: %d (%x) [%s]", flags, flags, MaterialInterface.getFlagsMeaning(flags));
        }

        if (!textureKey.equals(0xffffffff)
                && bfdfc.getTextureDatabase() != null
                && !bfdfc.getTextureDatabase().hasKey(textureKey)) {
            bfdfc.getTextureDatabase().addElem(new BFTextureElem(textureKey));
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("-- Singletexture --\n");
        sb.append(String.format("unk1: %d (%x)\n", unk1, unk1));
        sb.append(String.format("unk2: %d (%x)\n", unk2, unk2));
        sb.append(String.format("Vertexcolor: %d (%x)\n", vertexColor, vertexColor));

        sb.append(String.format("unkf1: %f\n", unkf1));
        sb.append(String.format("unkf2: %f\n", unkf2));

        sb.append(String.format("unk4: %d (%x)\n", unk4, unk4));
        sb.append(String.format("Texture key: %x", textureKey, textureKey));
        sb.append(String.format("Flags: %d (%x) [%s]\n", flags, flags, MaterialInterface.getFlagsMeaning(flags)));

        return sb.toString();
    }
}
