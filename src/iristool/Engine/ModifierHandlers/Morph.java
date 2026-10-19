/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoMorphModifier;
import Common.DataType.ModifierMorphShapeFormStruct;
import Common.DataType.ModifierMorphShapeSettingsContainerStruct;
import Common.DataType.Vector3;
import Common.IrisToolDefines;
import java.util.ArrayList;

/**
 *
 * @author andra
 */
public class Morph {

    public static void handleMorph(BFModifierGeoMorphModifier morphMod, Vector3 vecDst[]) {
        ArrayList<ModifierMorphShapeSettingsContainerStruct> mssc;
        int unkInner2Num;
        ArrayList<Integer> shapeFormIDs;
        Integer unkInner2F2ToInt;
        float fl1Xfloat1;
        ModifierMorphShapeFormStruct msf;
        int i, j, k;
        float factor;
        ArrayList<Integer> idx2Bak;
        Vector3 ret = new Vector3();
        float fl1;
        float inv;

        if (morphMod.getShapeFormSettingsContainerNum() > 0) {
            if (morphMod.getRequestedStaticLODElemID() > 0) {
                mssc = morphMod.getMorphShapeSettingsContainerStructs();
                if (morphMod.getShapeFormSettingsContainerNum() > 0) {
                    for (i = 0; i < morphMod.getShapeFormSettingsContainerNum(); i++) {
                        unkInner2Num = mssc.get(i).getMorphShapeIDNum();
                        if (mssc.get(i).getMorphShapeIDNum() > 0) {
                            if (mssc.get(i).getUnkInner2F1() != 0.0f && mssc.get(i).getUnkInner2F2() != 0.0f) {
                                shapeFormIDs = mssc.get(i).getMorphShapeFormIDs();
                                idx2Bak = mssc.get(i).getMorphShapeFormIDs();
                                unkInner2F2ToInt = Math.round(mssc.get(i).getUnkInner2F2());
                                for (j = unkInner2F2ToInt; j < unkInner2Num; j++) {
                                    if (shapeFormIDs.get(j) < morphMod.getShapeFormNum()) {
                                        break;
                                    }
                                }
                                if (j < unkInner2Num) {
                                    --unkInner2F2ToInt;
                                    if (unkInner2F2ToInt >= 0) {
                                        do {
                                            if (shapeFormIDs.get(unkInner2F2ToInt) < morphMod.getShapeFormNum()) {
                                                break;
                                            }
                                            --unkInner2F2ToInt;
                                        } while (unkInner2F2ToInt >= 0);
                                    }
                                    fl1 = (mssc.get(i).getUnkInner2F2() - unkInner2F2ToInt - 1.0f) / (float) (j - unkInner2F2ToInt);
                                } else {
                                    fl1 = 1.0f;
                                    for (j = unkInner2Num - 1; j > 0; j++) {
                                        if (shapeFormIDs.get(j) < morphMod.getShapeFormNum()) {
                                            break;
                                        }
                                    }
                                    if (shapeFormIDs.get(j) >= morphMod.getShapeFormNum()) {
                                        continue;
                                    }
                                }
                                if (fl1 > 0.0f) {
                                    fl1Xfloat1 = fl1 * mssc.get(i).getUnkInner2F1();
                                    if (shapeFormIDs.get(j) == null || shapeFormIDs.get(j).equals(-1)) {
                                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                                            System.out.println("modifierMorphHandler: BUG: idx2 is not valid. (" + shapeFormIDs.get(j) + ")");
                                        }
                                        continue;
                                    }
                                    msf = morphMod.getMorphShapeForms().get(shapeFormIDs.get(j));
                                    if (fl1Xfloat1 == 1.0f) {
                                        for (k = 0; k < msf.getMorphVerticesNum(); k++) {
                                            Vector3.sumTwoVectorInPlace(vecDst[msf.getIndices().get(k)], msf.getVecs().get(k));
                                        }
                                    } else {
                                        for (k = 0; k < msf.getMorphVerticesNum(); k++) {
                                            Vector3.multiplyVectorWithScalar(ret, msf.getVecs().get(k), fl1Xfloat1);
                                            Vector3.sumTwoVectorInPlace(vecDst[msf.getIndices().get(k)], ret);
                                        }
                                        shapeFormIDs = idx2Bak;
                                    }
                                }
                                inv = 1.0f - fl1;
                                if (unkInner2F2ToInt >= 0 && inv > 0.0f) {
                                    factor = inv * mssc.get(i).getUnkInner2F1();
                                    if (shapeFormIDs.get(unkInner2F2ToInt) == null || shapeFormIDs.get(unkInner2F2ToInt).equals(-1)) {
                                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                                            System.out.println("modifierMorphHandler: BUG: idx2 is not valid. (" + shapeFormIDs.get(unkInner2F2ToInt) + ")");
                                        }
                                        continue;
                                    }
                                    msf = morphMod.getMorphShapeForms().get(shapeFormIDs.get(unkInner2F2ToInt));
                                    if (factor == 1.0) {
                                        for (k = 0; k < msf.getMorphVerticesNum(); k++) {
                                            Vector3 tmp = vecDst[msf.getIndices().get(k)];
                                            tmp.setX(msf.getVecs().get(k).getX() + vecDst[msf.getIndices().get(k)].getX());
                                            tmp.setY(msf.getVecs().get(k).getY() + vecDst[msf.getIndices().get(k)].getY());
                                            tmp.setZ(msf.getVecs().get(k).getZ() + vecDst[msf.getIndices().get(k)].getZ());
                                        }
                                    } else {
                                        for (k = 0; k < msf.getMorphVerticesNum(); k++) {
                                            Vector3 tmp = vecDst[msf.getIndices().get(k)];
                                            tmp.setX(msf.getVecs().get(k).getX() * factor + vecDst[msf.getIndices().get(k)].getX());
                                            tmp.setY(msf.getVecs().get(k).getY() * factor + vecDst[msf.getIndices().get(k)].getY());
                                            tmp.setZ(msf.getVecs().get(k).getZ() * factor + vecDst[msf.getIndices().get(k)].getZ());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
