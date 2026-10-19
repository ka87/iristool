/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import Common.Convert;
import Common.DataType.GeometrySprite;
import Common.DataType.GeometrySpriteElem;
import Common.DataType.GeometryTriangleChunk;
import Common.DataType.GeometryTriangleStripChunk;
import Common.DataType.GeometryWholeTriangleStripBlock;
import Common.DataType.GeometryTriangleStripElement;
import Common.DataType.GeometryTriangleData;
import Common.DataType.GeometryType1Data;
import Common.DataType.GeometryType1InnerData;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import Common.DataType.UV;
import Common.IrisToolDefines;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class GAOInnerParserGeometry extends BFChunkParseClass implements BFChunkParseInterface {

    private Vector3 vertices[];
    private GeometryTriangleChunk geotc[];
    private UV uvs[];
    private int vertexColors[];
    private ArrayList<Integer> sknWeightArrayPacked[] = null;
    private Matrix4x4 skinMatrices[] = null;
    private Short[] skinBoneIDs;
    private Short unk1;
    private Short sknPartNum;
    private GeometryType1Data type1DataArr[];
    private Integer type1Num;
    private Integer mrmUnk2;
    private float mrmF1Arr[] = null;
    private Short mrmUnk1Arr[] = null;
    private Short mrmC0de0008Arr[] = null;
    private Integer vertexNum;
    private Integer vertexColorNum;
    private Integer uvNum;
    private Integer triangleChunkCount;
    private Integer mrm;
    private Integer flags;
    private Integer unkflags2;
    private Integer spriteCount;
    private GeometrySprite sprites[];
    // RUNTIME
    private Vector3 verticesTempForCalc[];
    private Vector3 verticesTempForCalc2[];
    private Vector3 normalsTemp[];
    private int normalsNumTemp[];

    public GAOInnerParserGeometry() {
        super();
    }

    public Short getUnk1() {
        return unk1;
    }

    public ArrayList<Integer>[] getSknWeightArrayPacked() {
        return sknWeightArrayPacked;
    }

    public Matrix4x4[] getSkinMatrices() {
        return skinMatrices;
    }

    public Short[] getSkinBoneIDs() {
        return skinBoneIDs;
    }

    public Vector3[] getVertices() {
        return vertices;
    }

    public Vector3[] getVerticesTempForCalc() {
        return verticesTempForCalc;
    }

    public Vector3[] getVerticesTempForCalc2() {
        return verticesTempForCalc2;
    }
        
    public Vector3[] getNormalsTemp() {
        return normalsTemp;
    }

    public int[] getNormalsNumTemp() {
        return normalsNumTemp;
    }
        
    public GeometryTriangleChunk[] getGeometryTriangleChunks() {
        return geotc;
    }

    public UV[] getUVs() {
        return uvs;
    }

    public int[] getVertexColors() {
        return vertexColors;
    }

    public GeometrySprite[] getGeometrySprites() {
        return sprites;
    }

    private Boolean sknParse(Boolean buildFromParsed) {
        if (!buildFromParsed) {
            unk1 = ru.readShort(false);
            sknPartNum = ru.readShort(false);
            if (sknPartNum > 0) {
                sknWeightArrayPacked = new ArrayList[sknPartNum];
                skinBoneIDs = new Short[sknPartNum];
                skinMatrices = new Matrix4x4[sknPartNum];
            } else {
                sknWeightArrayPacked = null;
                skinBoneIDs = null;
                skinMatrices = null;
            }
        } else {
            wu.writeShort(unk1);
            wu.writeShort(sknPartNum);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("SKN Unk1: %d", unk1);
            logF("SKN num: %d", sknPartNum);
        }

        Short boneID;
        Short weightNum;
        Integer wieghtPacked;
        Float weightFloat;

        Short i = sknPartNum;
        Short j;

        while (i > 0) {
            i--;
            if (!buildFromParsed) {
                boneID = ru.readShort(false);
                skinBoneIDs[i] = boneID;
                weightNum = ru.readShort(false);
                sknWeightArrayPacked[i] = new ArrayList<>();
            } else {
                boneID = skinBoneIDs[i];
                weightNum = new Integer(sknWeightArrayPacked[i].size()).shortValue();
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF(" -->SKN MultiPart gao group elem id (skinBoneID): %d Weight num: %d", boneID, weightNum);
            }
            if (!buildFromParsed) {
                skinMatrices[i] = new Matrix4x4();

                if (!skinMatrices[i].readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Matrix4x4 read failed.");
                    }
                    return false;
                }
                if ((skinMatrices[i].getFlags() & 0x8) == 0) { // ubisoft fix, not work the way as gíame does, I don't know where it clears these values. I clear them here.
                    skinMatrices[i].setM14(0.0f);
                    skinMatrices[i].setM24(0.0f);
                    skinMatrices[i].setM34(0.0f);
                    skinMatrices[i].setW(1.0f);
                }
            } else {
                skinMatrices[i].writeValues(wu);
            }

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(skinMatrices[i]);
            }

            j = 0;

            while (j < weightNum) {
                if (!buildFromParsed) {
                    wieghtPacked = ru.readInt(false);
                    sknWeightArrayPacked[i].add(wieghtPacked);
                } else {
                    wieghtPacked = sknWeightArrayPacked[i].get(j);
                    wu.writeInt(wieghtPacked);
                }
                weightFloat = ByteBuffer.wrap(Convert.intToHexArray(wieghtPacked)).getFloat();
                short id = (short) (wieghtPacked & 0xffff);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("  --> SKN Weight 4byte 0x%x (%d) (%f) (id=%d)", wieghtPacked, wieghtPacked, weightFloat, id);
                }
                j++;
            }
        }

        return true;
    }

    private Boolean type1Parse(Boolean buildFromParsed) {
        int i, j, k;
        if (!buildFromParsed) {
            type1Num = ru.readInt(false);
            if (type1Num > 0) {
                type1DataArr = new GeometryType1Data[type1Num];
            } else {
                type1DataArr = null;
            }
        } else {
            wu.writeInt(type1Num);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Type1 num: %d", type1Num);
        }

        Integer type1UnknownNum;
        Short type1Unk2;
        Short type1Unk3Num;
        Short type1Unk4;

        Vector3 pos1;
        Vector3 pos2;

        for (i = 0; i < type1Num; i++) {
            if (!buildFromParsed) {
                type1UnknownNum = ru.readInt(false);
                pos1 = new Vector3();
                if (!pos1.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Position3D read failed.");
                    }
                    return false;
                }
                pos2 = new Vector3();
                if (!pos2.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Position3D read failed.");
                    }
                    return false;
                }
                type1DataArr[i] = new GeometryType1Data(type1UnknownNum, pos1, pos2);
            } else {
                type1UnknownNum = type1DataArr[i].getType1UnknownNum();
                wu.writeInt(type1UnknownNum);
                pos1 = type1DataArr[i].getPos1();
                pos1.writeValues(wu);
                pos2 = type1DataArr[i].getPos2();
                pos2.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF(" -->Type1 unknown num: %d", type1UnknownNum);
                log(pos1);
                log(pos2);
            }

            for (j = 0; j < type1UnknownNum; j++) {
                if (!buildFromParsed) {
                    type1Unk2 = ru.readShort(false);
                    type1Unk3Num = ru.readShort(false);
                    type1DataArr[i].getInnerArr()[j] = new GeometryType1InnerData(type1Unk2, type1Unk3Num);
                } else {
                    type1Unk2 = type1DataArr[i].getInnerArr()[j].getType1Unk2();
                    wu.writeShort(type1Unk2);
                    type1Unk3Num = type1DataArr[i].getInnerArr()[j].getType1Unk3Num();
                    wu.writeShort(type1Unk3Num);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF(" -->Type1 unk2: %d unk3_num: %d", type1Unk2, type1Unk3Num);
                }
                for (k = 0; k < type1Unk3Num; k++) {
                    if (!buildFromParsed) {
                        type1Unk4 = ru.readShort(false);
                        type1DataArr[i].getInnerArr()[j].getType1Unk4Arr()[k] = type1Unk4;
                    } else {
                        type1Unk4 = type1DataArr[i].getInnerArr()[j].getType1Unk4Arr()[k];
                        wu.writeShort(type1Unk4);
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("  --> Type1 inner 2byte 0x%x (%d)", type1Unk4, type1Unk4);
                    }
                }
            }
        }

        return true;
    }

    // Not used in BGE (afaik)
    private Boolean mrmParse(Integer vertexNum, Integer mrm, Boolean buildFromParsed) {
        int i;

        if (!buildFromParsed) {
            if (vertexNum > 0) {
                mrmUnk1Arr = new Short[vertexNum];
                mrmC0de0008Arr = new Short[vertexNum];
                mrmF1Arr = new float[8];
            }
        }

        for (i = 0; i < vertexNum; i++) {
            if (!buildFromParsed) {
                mrmUnk1Arr[i] = ru.readShort(false);
            } else {
                wu.writeShort(mrmUnk1Arr[i]);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF(" -->MRM Unk1: %d (%x)", mrmUnk1Arr[i], mrmUnk1Arr[i]);
            }
        }

        if (mrm == 0xc0de0008) { // overwrite previously read values ?
            for (i = 0; i < vertexNum; i++) {
                if (!buildFromParsed) {
                    mrmC0de0008Arr[i] = ru.readShort(false);
                } else {
                    wu.writeShort(mrmC0de0008Arr[i]);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF(" -->MRM c0de0008 Unk1: %d (%x)", mrmC0de0008Arr[i], mrmC0de0008Arr[i]);
                }
            }
        }

        mrmUnk2 = ru.readInt(false);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("MRM unk2: %d (%x)", mrmUnk2, mrmUnk2);
        }

        for (i = 0; i < 8; i++) { // i = 12; i < 44; i += 4
            if (!buildFromParsed) {
                mrmF1Arr[i] = ru.readFloat(false);
            } else {
                wu.writeFloat(mrmF1Arr[i]);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("-->f1: %f", mrmF1Arr[i]);
            }
        }

        return true;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner Geometry Chunk.");
        }

        vertexNum = ru.readInt(false);
        vertexColorNum = ru.readInt(false);
        uvNum = ru.readInt(false);
        triangleChunkCount = ru.readInt(false);
        mrm = ru.readInt(false);
        flags = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Vertex count: %d", vertexNum);
            logF("Vertexcolor count: %d (must be same as vertexnum or 0)", vertexColorNum);
            logF("UV count: %d", uvNum);
            logF("Triangle Chunk count: %d", triangleChunkCount);
            logF("MRM: %d (%x)", mrm, mrm);
            logF("Flags: 0x%x", flags);
        }

        if ((flags & 0xc0de2002) == 0xc0de2002) {
            if (!sknParse(buildFromParsed)) {
                return false;
            }
        }
        if ((flags & 1) == 1) {
            if (!type1Parse(buildFromParsed)) {
                return false;
            }
        }

        int i, j, k;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("--VERTICES--");
        }

        Vector3 pos;

        if (vertexNum > 0) {
            vertices = new Vector3[vertexNum];
            verticesTempForCalc = new Vector3[vertexNum];
            verticesTempForCalc2 = new Vector3[vertexNum];
            normalsTemp = new Vector3[vertexNum];                        
            normalsNumTemp = new int[vertexNum];
        }

        for (i = 0; i < vertexNum; i++) {
            pos = new Vector3();
            if (!pos.readValues(ru)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Vector3 read failed.");
                }
                return false;
            }
            vertices[i] = pos;
            verticesTempForCalc[i] = new Vector3();
            verticesTempForCalc2[i] = new Vector3();
            normalsTemp[i] = new Vector3();
            
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(pos);
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("--MRM--");
        }
        if (mrm != 0) {
            if (!mrmParse(vertexNum, mrm, buildFromParsed)) {
                return false;
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("--VertexColor--");
        }
        if (vertexColorNum > vertexNum) {
            vertexColorNum = vertexNum;
        }
        if (vertexColorNum > 0) {
            vertexColors = new int[vertexColorNum];
            for (i = 0; i < vertexColorNum; i++) {
                vertexColors[i] = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF(" -->Vertexcolor: %x", vertexColors[i]);
                }
            }

        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("--UV coords--");
        }

        if (uvNum > 0) {
            uvs = new UV[uvNum];
        }

        for (i = 0; i < uvNum; i++) {
            UV uv = new UV();
            if (!uv.readValues(ru)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("UV read failed.");
                }
                return false;
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(uv);
            }
            uvs[i] = uv;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("--Geometry Triangle Chunks--");
        }

        geotc = new GeometryTriangleChunk[triangleChunkCount];

        Short us1;
        Integer u1, u2;

        if (triangleChunkCount > 0) {
            for (i = 0; i < triangleChunkCount; i++) {
                geotc[i] = new GeometryTriangleChunk();
                if (!geotc[i].readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("GeometryTriangleChunk read failed.");
                    }
                    return false;
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(geotc[i]);
                }
            }

            GeometryTriangleData geotd;

            for (i = 0; i < triangleChunkCount; i++) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("-->Triangle Chunk ID: %d", i);
                }
                for (j = 0; j < geotc[i].getTriangleNum(); j++) {
                    geotd = new GeometryTriangleData();
                    if (!geotd.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("GeometryTriangleData read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(geotd);
                    }
                    geotc[i].addGeometryTriangleData(geotd);
                }
                if (!geotc[i].getUnkNum1().equals(0)) {
                    u1 = ru.readInt(false);
                    u2 = ru.readInt(false);
                    geotc[i].getTriangleChunkUnkData1().setU1(u1);
                    geotc[i].getTriangleChunkUnkData1().setU2(u2);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF(" -->UNKNUM DATA: unk1 u1=%d (0x%x), u2=%d (0x%x)", u1, u1, u2, u2);
                    }
                }
                for (j = 0; j < geotc[i].getUnkNum2(); j++) {
                    us1 = ru.readShort(false);
                    geotc[i].addGeometryChunkUnkData2(us1);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF(" -->NUM2 shorts - id=%d us1=%d (0x%x)", j, us1, us1);
                    }
                }
            }
        }

        unkflags2 = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Unkflags2: %d (%x)", unkflags2, unkflags2);
        }

        if ((unkflags2 & 1) != 0) {
            int funk1, funknum, funkdata;
            Short vertexID, UVID;
            for (i = 0; i < triangleChunkCount; i++) {
                funk1 = ru.readInt(false);
                funknum = ru.readInt(false);
                GeometryWholeTriangleStripBlock geowtsb = new GeometryWholeTriangleStripBlock();
                geowtsb.setFunk1(funk1);
                geowtsb.setFunknum(funknum);
                geotc[i].setTriangleChunkUnkData3(geowtsb);

                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Triangle Strip Chunk: unkflags & 1 : id=%d, funk1=%d (0x%x), funknum=%d (0x%x)", i, funk1, funk1, funknum, funknum);
                }
                for (j = 0; j < funknum; j++) {
                    funkdata = ru.readInt(false);
                    geowtsb.getTriangleStripChunks()[j] = new GeometryTriangleStripChunk();
                    geowtsb.getTriangleStripChunks()[j].setFunkdata(funkdata);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("-->Triangle Chunk: funkdata: %d (0x%x)", funkdata, funkdata);
                    }

                    if (funkdata < 0) {
                        funkdata = funkdata & 0x7FFFFFFF;
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("-->(funkdata & 7FFFFFFF): %d", funkdata);
                        }
                    } else {
                        for (k = 0; k < funkdata; k++) {
//			funkdata1 = *(int *)data;
//			data += 4;
//			printf(" -->SMDATA: funkdata1(id=%d)=%d (0x%x)\n", k, funkdata1, funkdata1);
                        }
                    }
                    geowtsb.getTriangleStripChunks()[j].setGeoTFCElemNum(funkdata);
                    for (k = 0; k < funkdata; k++) {
                        vertexID = ru.readShort(false);
                        UVID = ru.readShort(false);
                        geowtsb.getTriangleStripChunks()[j].getTriangleStripElements()[k] = new GeometryTriangleStripElement(vertexID, UVID);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF(" -->Triangle Strip elem: vertexID(id: %d): %d (0x%x), UVID: %d (0x%x)", k, vertexID, vertexID, UVID, UVID);
                        }
                    }
                }
            }
        }

        spriteCount = ru.readInt(false);

        Float spf1, spf2;
        Integer spUnk1, spElemNum;
        Short spunks1, spunks2;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Sprite count: %d", spriteCount);
        }
        if (spriteCount > 0) {
            sprites = new GeometrySprite[spriteCount];
        }
        for (i = 0; i < spriteCount; i++) {
            spf1 = ru.readFloat(false);
            spf2 = ru.readFloat(false);
            spUnk1 = ru.readInt(false);
            spElemNum = ru.readInt(false);
            sprites[i] = new GeometrySprite(spf1, spf2, spUnk1, spElemNum);
            log(sprites[i]);
            for (j = 0; j < spElemNum; j++) {
                spunks1 = ru.readShort(false);
                spunks2 = ru.readShort(false);
                sprites[i].getGeoSpriteElem()[j] = new GeometrySpriteElem(spunks1, spunks2);
                log(sprites[i].getGeoSpriteElem()[j]);
            }
        }

        return true;
    }

}
