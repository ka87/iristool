/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGAOSpecialLookAt;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.Matrix4x4;
import iristool.Engine.Renderer;
import Common.DataType.Vector3;
import iristool.Engine.Camera;
import iristool.Engine.Modifier;
import iristool.Engine.RenderUtils;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author andra
 */
public class SpecialLookAt {

    public static void handleSpecialLookAt(BFModifierGAOSpecialLookAt sla, BFGaoChunk currentGao, Matrix4x4 cameraInWorldSpace, Renderer r, int width, int height) {
        Matrix4x4 currentGlobalMatrix;
        Vector3 currGaoGlobalPos;
        float ratio;
        float ratio2;
        float len;
        BFGaoInnerChunkParser ict;
        Vector3 twoGaoPosDistance = new Vector3();
        Vector3 zCoord = new Vector3();
        float zCoordAndLEndDist;
        BFGaoChunk selectedGao = null;
        Vector3 tmpVec = new Vector3();

        currGaoGlobalPos = currentGao.getGlobalMatrix().getPos();
        currentGlobalMatrix = currentGao.getGlobalMatrix();
        if (sla.getGaoID() != -1) {
            BFGRPChunk grp = Modifier.getGroupForModifierAndGao(currentGao, sla);
            if (grp == null) {
                return;
            }
            ArrayList<TreeMap<Integer, BFKeyListElem>> grpa = grp.getGrpGaoList();
            if (grpa != null && grpa.size() <= sla.getGaoID()) {
                BFKeyListElem kle = grpa.get(sla.getGaoID()).firstEntry().getValue();
                if (kle != null && kle.getParseHandler() != null) {
                    selectedGao = (BFGaoChunk) (kle.getParseHandler());
                }
            }
        } else if (sla.getGaoKLE() != null) {
            selectedGao = (BFGaoChunk) (sla.getGaoKLE().getParseHandler());
        }

        switch (sla.getType()) {
            case 0:
                currGaoGlobalPos.set(cameraInWorldSpace.getPos());
                currGaoGlobalPos.setX((cameraInWorldSpace.getM31() * sla.getzCoord()) + currGaoGlobalPos.getX());
                currGaoGlobalPos.setY((cameraInWorldSpace.getM32() * sla.getzCoord()) + currGaoGlobalPos.getY());
                currGaoGlobalPos.setZ((cameraInWorldSpace.getM33() * sla.getzCoord()) + currGaoGlobalPos.getZ());
                currentGlobalMatrix.setM11(cameraInWorldSpace.getM11());
                currentGlobalMatrix.setM12(cameraInWorldSpace.getM12());
                currentGlobalMatrix.setM13(cameraInWorldSpace.getM13());
                currentGlobalMatrix.setM21(-cameraInWorldSpace.getM21());
                currentGlobalMatrix.setM22(-cameraInWorldSpace.getM22());
                currentGlobalMatrix.setM23(-cameraInWorldSpace.getM23());
                currentGlobalMatrix.setM31(-cameraInWorldSpace.getM31());
                currentGlobalMatrix.setM32(-cameraInWorldSpace.getM32());
                currentGlobalMatrix.setM33(-cameraInWorldSpace.getM33());

                zCoord.set(0.0f, 0.0f, sla.getzCoord());
                Camera.Cam2dTo3dCamera2(twoGaoPosDistance, zCoord, width, height);

                currentGlobalMatrix.setFlags(currentGlobalMatrix.getFlags() | 0x8);
                currentGlobalMatrix.setM14(twoGaoPosDistance.getX() / currentGao.getBoundingBox1().getX());
                currentGlobalMatrix.setM24(1.0f);
                currentGlobalMatrix.setM34(-(twoGaoPosDistance.getY() / currentGao.getBoundingBox1().getZ()));
                break;
            case 0x1: // works (see espace level)
                if (selectedGao != null) {
                    twoGaoPosDistance.setX(cameraInWorldSpace.getPos().getX() - selectedGao.getGlobalMatrix().getPos().getX());
                    twoGaoPosDistance.setY(cameraInWorldSpace.getPos().getY() - selectedGao.getGlobalMatrix().getPos().getY());
                    twoGaoPosDistance.setZ(cameraInWorldSpace.getPos().getZ() - selectedGao.getGlobalMatrix().getPos().getZ());
                    ratio = sla.getzCoord() * sla.getzCoord() / (twoGaoPosDistance.getZ() * twoGaoPosDistance.getZ() + twoGaoPosDistance.getY() * twoGaoPosDistance.getY() + twoGaoPosDistance.getX() * twoGaoPosDistance.getX());
                    Vector3.vectorMultiplyWithScalarAndSum(currGaoGlobalPos, selectedGao.getGlobalMatrix().getPos(), twoGaoPosDistance, ratio);
                    Vector3.substractTwoVectors(tmpVec, currGaoGlobalPos, selectedGao.getGlobalMatrix().getPos());
                    len = Vector3.getVectorLength(tmpVec);
                    zCoordAndLEndDist = RenderUtils.getRootForFloat(sla.getzCoord() * sla.getzCoord() - len * len);
                    Vector3.getVectorNormalized(tmpVec, twoGaoPosDistance);
                    currentGlobalMatrix.setM21(tmpVec.getX());
                    currentGlobalMatrix.setM22(tmpVec.getY());
                    currentGlobalMatrix.setM23(tmpVec.getZ());
                    currentGlobalMatrix.setM21(-currentGlobalMatrix.getM21());
                    currentGlobalMatrix.setM22(-currentGlobalMatrix.getM22());
                    currentGlobalMatrix.setM23(-currentGlobalMatrix.getM23());
                    Vector3.crossProduct(tmpVec, new Vector3(cameraInWorldSpace.getM11(), cameraInWorldSpace.getM12(), cameraInWorldSpace.getM13()), twoGaoPosDistance);
                    Vector3.divEqualVectorInPlace(tmpVec);
                    currentGlobalMatrix.setM31(tmpVec.getX());
                    currentGlobalMatrix.setM32(tmpVec.getY());
                    currentGlobalMatrix.setM33(tmpVec.getZ());
                    Vector3.crossProduct(tmpVec, new Vector3(currentGlobalMatrix.getM21(), currentGlobalMatrix.getM22(), currentGlobalMatrix.getM23()), new Vector3(currentGlobalMatrix.getM31(), currentGlobalMatrix.getM32(), currentGlobalMatrix.getM33()));
                    currentGlobalMatrix.setM11(tmpVec.getX());
                    currentGlobalMatrix.setM12(tmpVec.getY());
                    currentGlobalMatrix.setM13(tmpVec.getZ());
                    ict = RenderUtils.getGeometryICTFromLODGAO(currentGao);
                    if (ict != null) {
                        BFChunkParseInterface cpi = ict.getGaoInner();
                        if (cpi != null && cpi instanceof GAOInnerParserGeometry) {
                            GAOInnerParserGeometry geo = (GAOInnerParserGeometry) cpi;
                            ratio2 = (zCoordAndLEndDist + sla.getuF2()) / zCoordAndLEndDist * 100.0f;
                            float diff = 0.0f;
                            for (int i = 0; i < geo.getVertices().length; i += 2) {
                                geo.getVertices()[i].setX((float) Math.sin(diff) * ratio2);
                                geo.getVertices()[i].setY(-sla.getuF3());
                                geo.getVertices()[i].setZ((float) Math.cos(diff) * ratio2);
                                diff = diff + (12.566371f / geo.getVertices().length);
                            }
                        }
                    }

                    currentGlobalMatrix.setFlags(currentGlobalMatrix.getFlags() | 0x8);
                    currentGlobalMatrix.setM14(zCoordAndLEndDist * 0.0099999998f);
                    currentGlobalMatrix.setM24(1.0f);
                    currentGlobalMatrix.setM34(zCoordAndLEndDist * 0.0099999998f);
                }
                break;
            case 0x2:
                if (selectedGao != null) {
                    twoGaoPosDistance.setX(currentGlobalMatrix.getPos().getX() - selectedGao.getGlobalMatrix().getPos().getX());
                    twoGaoPosDistance.setY(currentGlobalMatrix.getPos().getY() - selectedGao.getGlobalMatrix().getPos().getY());
                    twoGaoPosDistance.setZ(currentGlobalMatrix.getPos().getZ() - selectedGao.getGlobalMatrix().getPos().getZ());
                    Vector3.getVectorNormalized(tmpVec, twoGaoPosDistance);
                    currentGlobalMatrix.setM21(tmpVec.getX());
                    currentGlobalMatrix.setM22(tmpVec.getY());
                    currentGlobalMatrix.setM23(tmpVec.getZ());
                    Vector3.crossProduct(tmpVec, new Vector3(cameraInWorldSpace.getM11(), cameraInWorldSpace.getM12(), cameraInWorldSpace.getM13()),
                            new Vector3(currentGlobalMatrix.getM21(), currentGlobalMatrix.getM22(), currentGlobalMatrix.getM23()));
                    Vector3.divEqualVectorInPlace(tmpVec);
                    currentGlobalMatrix.setM31(tmpVec.getX());
                    currentGlobalMatrix.setM32(tmpVec.getY());
                    currentGlobalMatrix.setM33(tmpVec.getZ());
                    Vector3.crossProduct(tmpVec, new Vector3(currentGlobalMatrix.getM21(), currentGlobalMatrix.getM22(), currentGlobalMatrix.getM23()),
                            new Vector3(currentGlobalMatrix.getM31(), currentGlobalMatrix.getM32(), currentGlobalMatrix.getM33()));
                    currentGlobalMatrix.setM11(tmpVec.getX());
                    currentGlobalMatrix.setM12(tmpVec.getY());
                    currentGlobalMatrix.setM13(tmpVec.getZ());

                    RenderUtils.calculateLocalMatrixFromGAOIfHasPivotReference(currentGao);
                }
                break;
        }

    }
}
