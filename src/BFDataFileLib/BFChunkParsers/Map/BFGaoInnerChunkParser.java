/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserCamera;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserLight;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialGroup;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialMultiTexture;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialSingleTexture;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserParticleGenerator;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserStaticLOD;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserStringStruct;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserZeroTypeUnknown;
import BFDataFileLib.BFDataFileClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFGaoInnerChunkParser extends BFChunkParseClass implements BFChunkParseInterface {

    public static final int BF_GAO_TYPE_ZERO_UNKNOWN = 0;
    public static final int BF_GAO_TYPE_GEOMETRY = 1;
    public static final int BF_GAO_TYPE_LIGHT = 2;
    public static final int BF_GAO_TYPE_MATERIAL_SINGLETEXTURE = 3;
    public static final int BF_GAO_TYPE_MATERIAL_GROUP = 4;
    public static final int BF_GAO_TYPE_MATERIAL_MULTITEXTURE = 5;
    public static final int BF_GAO_TYPE_CAMERA = 6;
    public static final int BF_GAO_TYPE_STATICLOD = 8;
    public static final int BF_GAO_TYPE_STRINGSTRUCT = 10;
    public static final int BF_GAO_TYPE_PARTICLEGENERATOR = 11;

    private Long len = 0L;
    private Integer gaoInnerType = null;
    private BFChunkParseInterface gaoInner = null;

    public BFGaoInnerChunkParser() {
        super();
    }

    public Integer getGaoInnerType() {
        return gaoInnerType;
    }

    public BFChunkParseInterface getGaoInner() {
        return gaoInner;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing GAO Inner Chunk.");
        }

        long startPos = 0L;

        if (!buildFromParsed) {
            len = initParse();
            startPos = ru.getCurrentPos();

            if (len == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Length read failed.");
                }
                return false;
            }
        } else {
            wu.startBlockWithLength();
        }

        if (!buildFromParsed) {
            gaoInnerType = ru.readInt(false);
        } else {
            wu.writeInt(gaoInnerType);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("GAO Inner Type: %d", gaoInnerType);
        }

        Boolean ret = false;

        switch (gaoInnerType) {
            case BF_GAO_TYPE_ZERO_UNKNOWN:
                gaoInner = new GAOInnerParserZeroTypeUnknown();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_GEOMETRY:
                gaoInner = new GAOInnerParserGeometry();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_LIGHT:
                gaoInner = new GAOInnerParserLight();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_MATERIAL_SINGLETEXTURE:
                gaoInner = new GAOInnerParserMaterialSingleTexture();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_MATERIAL_GROUP:
                gaoInner = new GAOInnerParserMaterialGroup();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_MATERIAL_MULTITEXTURE:
                gaoInner = new GAOInnerParserMaterialMultiTexture();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_CAMERA:
                gaoInner = new GAOInnerParserCamera();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_STATICLOD:
                gaoInner = new GAOInnerParserStaticLOD();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_STRINGSTRUCT:
                gaoInner = new GAOInnerParserStringStruct();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case BF_GAO_TYPE_PARTICLEGENERATOR:
                gaoInner = new GAOInnerParserParticleGenerator();
                ret = gaoInner.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            default:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Unhandled GAO Inner Type. Parsing stopped at pos %d (%x)", ru.getCurrentPos(), ru.getCurrentPos());
                }
                return false;
        }

        if (!buildFromParsed && insideBinFile && len != (ru.getCurrentPos() - startPos)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != length");
            }
            ret = false;
        }

        if (buildFromParsed) {
            wu.finishBlockWithLength();
        }

        return ret;
    }
}
