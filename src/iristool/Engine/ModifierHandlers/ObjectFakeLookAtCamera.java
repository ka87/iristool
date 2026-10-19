/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierObjectFakeLookAtCameraModifier;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import iristool.Engine.RenderUtils;

/**
 *
 * @author andra
 */
public class ObjectFakeLookAtCamera {

    public static void handleObjectFakeLookAtCamera(BFModifierObjectFakeLookAtCameraModifier mod, BFGaoChunk gao, Vector3 worldMtxPos) {
        Matrix4x4 global;
        float xDiff;
        float yDiff;
        float zDiff;
        float len;

        global = gao.getGlobalMatrix();

        if (Vector3.isVectorsDifferenceWithinLimit(worldMtxPos, global.getPos(), 0.01f)) {
            return;
        }

        xDiff = global.getPos().getX() - worldMtxPos.getX();
        //yDiff = global.getPos().getY() - worldMtxPos.getY();
        yDiff = (global.getPos().getY() - worldMtxPos.getY());
        zDiff = global.getPos().getZ() - worldMtxPos.getZ();
        global.setM11((yDiff * global.getM33()) - (zDiff * global.getM32()));
        global.setM12((zDiff * global.getM31()) - (xDiff * global.getM33()));
        global.setM13((xDiff * global.getM32()) - (yDiff * global.getM31()));

        len = (float) Math.sqrt(global.getM11() * global.getM11() + global.getM12() * global.getM12() + global.getM13() * global.getM13());

        global.setM11((1.0f / len) * global.getM11());
        global.setM12((1.0f / len) * global.getM12());
        global.setM13((1.0f / len) * global.getM13());
        global.setM21((global.getM13() * global.getM32()) - (global.getM12() * global.getM33()));
        global.setM22((global.getM11() * global.getM33()) - (global.getM31() * global.getM13()));
        global.setM23((global.getM31() * global.getM12()) - (global.getM32() * global.getM11()));
        RenderUtils.calculateLocalMatrixFromGAOIfHasPivotReference(gao);
        //System.out.println(global);
    }
}
