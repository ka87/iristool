/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.DataType.ModifierMorphShapeFormStruct;
import Common.DataType.ModifierMorphShapeSettingsContainerStruct;
import Common.DataType.Vector3;
import Common.IrisToolDefines;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class BFModifierGeoMorphModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    private Integer unk1, unk2, requestedStaticLODElemID, shapeFormNum, shapeFormSettingsContainerNum;
    private ArrayList<ModifierMorphShapeFormStruct> morphShapeForms;
    private ArrayList<ModifierMorphShapeSettingsContainerStruct> morphShapeSettingsContainerStructs;

    @Override
    public BFModifierGeoMorphModifier duplicate() {
        BFModifierGeoMorphModifier tmp = new BFModifierGeoMorphModifier();
        tmp.setSelfKey(this.getSelfKey());
        tmp.setHandleFlags(this.getHandleFlags());
        tmp.setUnk1(this.getUnk1());
        tmp.setUnk2(this.getUnk2());
        tmp.setRequestedStaticLODElemID(this.getRequestedStaticLODElemID());

        if (this.getMorphShapeForms() != null) {
            ArrayList<ModifierMorphShapeFormStruct> tmpmsf = new ArrayList<>();
            this.getMorphShapeForms().forEach((ms1) -> {
                tmpmsf.add(ms1.duplicate());
            });
            tmp.setMorphShapeForms(tmpmsf);
        }
        if (this.getMorphShapeSettingsContainerStructs() != null) {
            ArrayList<ModifierMorphShapeSettingsContainerStruct> tmpmmsc = new ArrayList<>();
            this.getMorphShapeSettingsContainerStructs().forEach((ms2) -> {
                tmpmmsc.add(ms2.duplicate());
            });
            tmp.setMorphShapeSettingsContainerStructs(tmpmmsc);
        }
        tmp.setShapeFormNum(this.getShapeFormNum());
        tmp.setShapeFormSettingsContainerNum(this.getShapeFormSettingsContainerNum());

        return tmp;
    }

    public ArrayList<ModifierMorphShapeFormStruct> getMorphShapeForms() {
        return morphShapeForms;
    }

    public ArrayList<ModifierMorphShapeSettingsContainerStruct> getMorphShapeSettingsContainerStructs() {
        return morphShapeSettingsContainerStructs;
    }

    public Integer getUnk1() {
        return unk1;
    }

    public Integer getUnk2() {
        return unk2;
    }

    public Integer getRequestedStaticLODElemID() {
        return requestedStaticLODElemID;
    }

    public Integer getShapeFormNum() {
        return shapeFormNum;
    }

    public Integer getShapeFormSettingsContainerNum() {
        return shapeFormSettingsContainerNum;
    }

    public void setUnk1(Integer unk1) {
        this.unk1 = unk1;
    }

    public void setUnk2(Integer unk2) {
        this.unk2 = unk2;
    }

    public void setRequestedStaticLODElemID(Integer requestedStaticLODElemID) {
        this.requestedStaticLODElemID = requestedStaticLODElemID;
    }

    public void setShapeFormNum(Integer shapeFormNum) {
        this.shapeFormNum = shapeFormNum;
    }

    public void setShapeFormSettingsContainerNum(Integer shapeFormSettingsContainerNum) {
        this.shapeFormSettingsContainerNum = shapeFormSettingsContainerNum;
    }

    public void setMorphShapeForms(ArrayList<ModifierMorphShapeFormStruct> morphShapeForms) {
        this.morphShapeForms = morphShapeForms;
    }

    public void setMorphShapeSettingsContainerStructs(ArrayList<ModifierMorphShapeSettingsContainerStruct> morphShapeSettingsContainerStructs) {
        this.morphShapeSettingsContainerStructs = morphShapeSettingsContainerStructs;
    }

    public BFModifierGeoMorphModifier() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Geo Morph Modifier (Type 4)");
        }

        morphShapeForms = new ArrayList<>();
        morphShapeSettingsContainerStructs = new ArrayList<>();

        unk1 = ru.readInt(false);
        unk2 = ru.readInt(false);
        requestedStaticLODElemID = ru.readInt(false);
        shapeFormNum = ru.readInt(false);
        shapeFormSettingsContainerNum = ru.readInt(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("unk1: %d (%x)", unk1, unk1);
            logF("unk2: %d (%x)", unk2, unk2);
            logF("requestedStaticLODElemID: %d (%x)", requestedStaticLODElemID, requestedStaticLODElemID);
            logF("shapeFormNum: %d (%x)", shapeFormNum, shapeFormNum);
            logF("shapeFormSettingsContainerNum: %d (%x)", shapeFormSettingsContainerNum, shapeFormSettingsContainerNum);
        }

        int i, j;
        ModifierMorphShapeFormStruct msf;
        ModifierMorphShapeSettingsContainerStruct mssc;

        for (i = 0; i < shapeFormNum; i++) {
            msf = new ModifierMorphShapeFormStruct();
            Integer verticesNum = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("MorphShapeForm(%d) verticesNum: %d", i, verticesNum);
            }
            msf.setMorphVecticesNum(verticesNum);
            Integer index;

            for (j = 0; j < verticesNum; j++) {
                index = ru.readInt(false);
                msf.getIndices().add(index);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF(" [%d.] Index: %d (%x)", j, index, index);
                }
            }

            Vector3 pos;

            for (j = 0; j < verticesNum; j++) {
                pos = new Vector3();
                if (!pos.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read failed.");
                    }
                    return false;
                }
                msf.getVecs().add(pos);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(pos);
                }
            }
            morphShapeForms.add(msf);
        }

        Integer morphShapeIDNum;
        Float unkInner2F1, unkInner2F2;
        String unkInner2S1;

        for (i = 0; i < shapeFormSettingsContainerNum; i++) {
            mssc = new ModifierMorphShapeSettingsContainerStruct();

            morphShapeIDNum = ru.readInt(false);
            unkInner2F1 = ru.readFloat(false);
            unkInner2F2 = ru.readFloat(false);

            mssc.setMorphShapeIDNum(morphShapeIDNum);
            mssc.setUnkInner2F1(unkInner2F1);
            mssc.setUnkInner2F2(unkInner2F2);

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("morphShapeIDNum: %d (%x), unkInner2F1: %f, unkInner2F2: %f", morphShapeIDNum, morphShapeIDNum, unkInner2F1, unkInner2F2);
            }

            if (!insideBinFile) {
                unkInner2S1 = ru.readNString(64, false);
                mssc.setUnkInner2S1(unkInner2S1);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("unkInnerS1 (%d): %s", i, unkInner2S1);
                }
            }

            // Ezeket a Cutscene is módosítja például. pl 0x2047-es script függvény. Az unkInner2F2 hivatkozik ezekre az elemekre. Lásd: Modifier.java
            Integer morphShapeFormID;
            for (j = 0; j < morphShapeIDNum; j++) {
                morphShapeFormID = ru.readInt(false);
                mssc.getMorphShapeFormIDs().add(morphShapeFormID);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF(" [%d] morphShapeFormID: %d (%x)", j, morphShapeFormID, morphShapeFormID);
                }
            }
            morphShapeSettingsContainerStructs.add(mssc);
        }

        return true;
    }

}
