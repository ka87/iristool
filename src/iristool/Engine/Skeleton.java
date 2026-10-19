/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFAnimationShapeChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFKeyListElem;
import Common.Convert;
import Common.DataType.BoneShapeElem;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import Common.IrisToolDefines;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author andra
 */
public class Skeleton {

    private static Vector3 transformedVertex = new Vector3();
    private static Matrix4x4 mtxDestTemp = new Matrix4x4();
    private static Matrix4x4 destMtx = new Matrix4x4();
    private static Matrix4x4 gaoMatrixInvert = new Matrix4x4();
    private static Vector3 currShapeMatrixScale = new Vector3();
    private static Vector3 currBoneLocalMtxScale = new Vector3();
    private static Matrix4x4 mainGaoMtxInvert = new Matrix4x4();
    private static Vector3 mainGaoGlobalScale = new Vector3();

    public static void fillBoneShapeMatricesToSize(ArrayList<Matrix4x4> shapeMatrices, int size) {
        if (shapeMatrices.size() > size) {
            return;
        }

        int numToAdd = (size - shapeMatrices.size());

        for (int i = 0; i < numToAdd; i++) {
            shapeMatrices.add(new Matrix4x4());
        }
    }

    public static Integer findShapeElemOffsetByBoneID(BFAnimationShapeChunk asc, int id) {
        int i;
        if (asc.getElemNum().equals(0)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println("Shape bone elem id not found.");
            }
            return null;
        }
        if (asc.getBoneIDArray() == null) {
            return null;
        }

        Byte[] boneIDArray = asc.getBoneIDArray();

        for (i = 0; i < asc.getElemNum(); i++) {
            if (((byte) (boneIDArray[i])) == id) {
                return i;
            }
        }
        return null;
    }

    public static void applyShapeForGao(BFGaoChunk gao) {

        if ((gao.getFlags() & 0x2) == 0) {
            return;
        }
        BFKeyListElem groupKLE = gao.getBonesGroupChunkInstanceKLE();
        if (groupKLE == null || groupKLE.getParseHandler() == null
                || !(groupKLE.getParseHandler() instanceof BFGRPContainerChunk)) {
            return;
        }

        BFGRPContainerChunk grpc = (BFGRPContainerChunk) groupKLE.getParseHandler();
        if (grpc == null) {
            return;
        }
        BFGRPChunk grp = grpc.getGRP();
        if (grp == null) {
            return;
        }

        ArrayList<TreeMap<Integer, BFKeyListElem>> grpl = grp.getGrpGaoList();
        if (grpl == null) {
            return;
        }

        BFKeyListElem asckle = gao.getAnimationShapeChunkInstanceSpecificKLE();
        if (asckle == null || asckle.getParseHandler() == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println("Has no AnimationShapeChunkKLE.");
            }
            return;
        }
        if (!(asckle.getParseHandler() instanceof BFAnimationShapeChunk)) {
            return;
        }
        BFAnimationShapeChunk asc = (BFAnimationShapeChunk) asckle.getParseHandler();
        Iterator it = grpl.iterator();
        int i = -1;
        while (it.hasNext()) {
            i++;
            TreeMap<Integer, BFKeyListElem> tm = (TreeMap<Integer, BFKeyListElem>) it.next();
            BFGaoChunk boneGao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            Integer boneElemShapeOffset = findShapeElemOffsetByBoneID(asc, i);
            if (boneElemShapeOffset == null) {
                continue;
            }
            if (asc.getGroupElemIDArray()[boneElemShapeOffset].equals((byte) -3)) {
                boneGao.setVisualFlags(boneGao.getVisualFlags() | 0x20200000); // set to invisible
            } else if (asc.getGroupElemIDArray()[boneElemShapeOffset].equals((byte) -2)) {
                boneGao.setVisualFlags(boneGao.getVisualFlags() & ~0x20200000); // set to visible
            } else if (boneGao.getGroupKey().equals(0)
                    || boneGao.getGroupChunkKLE() == null
                    || boneGao.getGroupChunkKLE().getParseHandler() == null
                    || !(boneGao.getGroupChunkKLE().getParseHandler() instanceof BFGRPContainerChunk)) { // bonegao contain geometry, because it has no unk5grp
                continue;
            } else if ((boneGao.getFlags() & 0x800000) != 0) {
                BFGRPContainerChunk unk5GRPC = (BFGRPContainerChunk) boneGao.getGroupChunkKLE().getParseHandler();
                BFGRPChunk unk5GRP = unk5GRPC.getGRP();
                if (unk5GRP == null) {
                    continue;
                }
                ArrayList<TreeMap<Integer, BFKeyListElem>> unk5grpl = unk5GRP.getGrpGaoList();
                if (unk5grpl == null) {
                    continue;
                }
                if (unk5grpl.size() <= asc.getGroupElemIDArray()[boneElemShapeOffset]) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        System.out.println("Group elem id is bigger than array size.");
                    }
                }
                TreeMap<Integer, BFKeyListElem> tm2 = (TreeMap<Integer, BFKeyListElem>) unk5grpl.get(asc.getGroupElemIDArray()[boneElemShapeOffset]);
                BFGaoChunk selectedGeometryGao = (BFGaoChunk) (((BFKeyListElem) (tm2.firstEntry().getValue())).getParseHandler());
                if (selectedGeometryGao == null) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        System.out.println("Selected geometry gao is null");
                    }
                    continue;
                }
                boneGao.setPrimaryVertexColorArr(selectedGeometryGao.getPrimaryVertexColorArr());
                boneGao.setGeoKey(selectedGeometryGao.getGeoKey());
                boneGao.setGeoChunkKLE(selectedGeometryGao.getGeoChunkKLE());
                boneGao.setMaterialGroupKey(selectedGeometryGao.getMaterialGroupKey());
                boneGao.setMaterialGroupChunkKLE(selectedGeometryGao.getMaterialGroupChunkKLE());
                boneGao.setDesignFlags1(selectedGeometryGao.getDesignFlags1());

                // Copy BoneShapes to [boneGao], set [boneGao] transformation matrix containing gao to [gao]
                // and set animation shape matrix reference to [gao] boneid'th elem reference. 
                if (selectedGeometryGao.getBoneShapes() != null && selectedGeometryGao.getBoneShapes().size() > 0) {
                    ArrayList<BoneShapeElem> dstBoneShapes = new ArrayList<>();
                    for (BoneShapeElem bse : selectedGeometryGao.getBoneShapes()) {
                        BoneShapeElem dstbse = new BoneShapeElem();
                        BFKeyListElem boneKLE = new BFKeyListElem(gao.getSelfKey(), BFGaoChunk.class, false);
                        boneKLE.setParseHandler(gao);
                        dstbse.setBoneID(bse.getBoneID());
                        dstbse.setBoneGaoChunkKLE(boneKLE);
                        dstbse.setBoneGaoKey(gao.getSelfKey());
                        int boneID = bse.getBoneID();
                        if (boneID != -1) {
                            if (gao.getBoneShapeMatrices().size() <= bse.getBoneID()) {
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    System.out.format("BUG: %s: shape matrices num (%d) < than bone ID (%d), adjusting size.\n",
                                            gao.getGaoName(), gao.getBoneShapeMatrices().size(), bse.getBoneID());
                                }
                                fillBoneShapeMatricesToSize(gao.getBoneShapeMatrices(), boneID + 1);
                            }
                            dstbse.setAnimationShapeMtx(gao.getBoneShapeMatrices().get(boneID));
                        }
                        dstBoneShapes.add(dstbse);
                    }
                    boneGao.setBoneShapes(dstBoneShapes);
                    boneGao.setFlags(boneGao.getFlags() | 0x1200000);
                } else {
                    if (boneGao.getBoneShapes() != null) {
                        boneGao.setBoneShapes(null);
                        boneGao.setBoneShapeMatrices(null);
                        boneGao.setFlags(boneGao.getFlags() & ~0x1200000);
                    }
                }

                if (boneGao.getModifiersArray() != null) {
                    boneGao.setModifiersArray(null);
                }
                if (selectedGeometryGao.getModifiersArray() != null) {
                    ArrayList<BFChunkParseInterface> tmpModArr = new ArrayList<>();
                    BFChunkParseInterface cpiDuplicated;
                    for (BFChunkParseInterface tmpcpi : selectedGeometryGao.getModifiersArray()) {
                        cpiDuplicated = tmpcpi.duplicate();
                        if (cpiDuplicated == null) {
                            cpiDuplicated = tmpcpi; // TODO: should be removed, when all modifiers will be implemented 
                        }
                        tmpModArr.add(cpiDuplicated);
                    }
                    boneGao.setModifiersArray(tmpModArr);
                    Modifier.updateModifiersForMultipartGaos(boneGao, grpc);
                }
                boneGao.setFlags(boneGao.getFlags() | 0x4000);
                boneGao.setVisualFlags(boneGao.getVisualFlags() & ~0x20200000); // set to visible
            }
        }

    }

    public static void skinApplyMirroringIfSet(BFGaoChunk gao, GAOInnerParserGeometry geo, int currID, Matrix4x4 destMtx, Matrix4x4 mtxDestTemp) {
        if ((byte) ((gao.getDesignFlags1() >> 8) & 0xff) >= 0) {
            geo.getSkinMatrices()[currID - 1].setM11(-geo.getSkinMatrices()[currID - 1].getM11());
            geo.getSkinMatrices()[currID - 1].setM21(-geo.getSkinMatrices()[currID - 1].getM21());
            geo.getSkinMatrices()[currID - 1].setM31(-geo.getSkinMatrices()[currID - 1].getM31());
            geo.getSkinMatrices()[currID - 1].getPos().setX(-geo.getSkinMatrices()[currID - 1].getPos().getX());
            Matrix4x4.matrixMultiplyWithNormalized(destMtx, geo.getSkinMatrices()[currID - 1], mtxDestTemp);
            geo.getSkinMatrices()[currID - 1].setM11(-geo.getSkinMatrices()[currID - 1].getM11());
            geo.getSkinMatrices()[currID - 1].setM21(-geo.getSkinMatrices()[currID - 1].getM21());
            geo.getSkinMatrices()[currID - 1].setM31(-geo.getSkinMatrices()[currID - 1].getM31());
            geo.getSkinMatrices()[currID - 1].getPos().setX(-geo.getSkinMatrices()[currID - 1].getPos().getX());
        } else {
            Matrix4x4.matrixMultiplyWithNormalized(destMtx, geo.getSkinMatrices()[currID - 1], mtxDestTemp);
        }
    }

    public static void skinApplyWeight(GAOInnerParserGeometry geo, int currID, Vector3 dstVertices[], Matrix4x4 destMtx, Vector3 srcVertices[]) {
        int i;
        ArrayList<Integer> weightsPacked = geo.getSknWeightArrayPacked()[currID - 1];
        for (i = 0; i < weightsPacked.size(); i++) {
            int id = weightsPacked.get(i) & 0xffff;
            Vector3.transformVertex(transformedVertex, destMtx, srcVertices[id]);
            dstVertices[id].setX(transformedVertex.getX() * ByteBuffer.wrap(Convert.intToHexArray(weightsPacked.get(i))).getFloat() + dstVertices[id].getX());
            dstVertices[id].setY(transformedVertex.getY() * ByteBuffer.wrap(Convert.intToHexArray(weightsPacked.get(i))).getFloat() + dstVertices[id].getY());
            dstVertices[id].setZ(transformedVertex.getZ() * ByteBuffer.wrap(Convert.intToHexArray(weightsPacked.get(i))).getFloat() + dstVertices[id].getZ());
        }
    }

    public static boolean skinCompute(BFGaoChunk gao, Vector3 srcVertices[], Vector3 dstVertices[]) {
        boolean shapeBoneMtxSet;
        
        if ((gao.getFlags() & 0x200000) != 0) {
            BFGaoInnerChunkParser ic;
            if ((ic = RenderUtils.getGeometryICTFromLODGAO(gao)) == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("BUG: Gao has no skin: " + gao.getGaoName());
                }
                return false;
            }
            GAOInnerParserGeometry geo = (GAOInnerParserGeometry) (ic.getGaoInner());
            if (geo.getSkinMatrices() == null || geo.getSkinBoneIDs() == null || geo.getSknWeightArrayPacked() == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("BUG: sknMtxArr, sknBoneIDArray or sknWeightArrayPacked is null: " + gao.getGaoName());
                }
                return false;
            }
            int currID = geo.getSkinMatrices().length;
            while (currID > 0) {
                shapeBoneMtxSet = false;
                if (geo.getSkinBoneIDs().length <= (currID - 1)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        System.out.println("BoneID bigger than sknBoneIDArray size. (" + geo.getSkinBoneIDs().length + " >= " + (currID - 1) + ")");
                    }
                    currID--;
                    continue;
                } else {
                    if (geo.getSknWeightArrayPacked()[(currID - 1)].size() <= 0) {
                        currID--;
                        continue;
                    }
                    int boneIDOffset = geo.getSkinBoneIDs()[(currID - 1)];
                    if (gao.getBoneShapes() != null && gao.getBoneShapes().size() > boneIDOffset) {
                        if ((gao.getFlags() & 0x1000000) != 0) {
                            Integer shapeBoneID = gao.getBoneShapes().get(boneIDOffset).getBoneID();
                            BFKeyListElem shapeBoneGaoKLE = gao.getBoneShapes().get(boneIDOffset).getBoneGaoChunkKLE();
                            if (gao.getBoneShapes().get(boneIDOffset).getAnimationShapeMtx() != null) {
                                mtxDestTemp.set(gao.getBoneShapes().get(boneIDOffset).getAnimationShapeMtx());
                                shapeBoneMtxSet = true;
                            }
                            if (shapeBoneID.equals(-1)
                                    || (shapeBoneGaoKLE != null
                                    && shapeBoneGaoKLE.getParseHandler() != null
                                    && (shapeBoneGaoKLE.getParseHandler() instanceof BFGaoChunk)
                                    && (((BFGaoChunk) (shapeBoneGaoKLE.getParseHandler())).getFlags() & 0x1000000) != 0)) {
                                if (shapeBoneGaoKLE != null
                                        && shapeBoneGaoKLE.getParseHandler() != null) {
                                    Matrix4x4.invertMatrix(gaoMatrixInvert, gao.getGlobalMatrix());
                                    Matrix4x4.matrixMultiplyWithNormalized(mtxDestTemp,
                                            ((BFGaoChunk) (shapeBoneGaoKLE.getParseHandler())).getGlobalMatrix(), gaoMatrixInvert);
                                } else {
                                    mtxDestTemp.set(Matrix4x4.getIdentityMatrix());
                                }
                                shapeBoneMtxSet = true;
//                                TODO: this is maybe pointless                                
//                                skinApplyMirroringIfSet(gao, geo, currID, destMtx, mtxDestTemp);
//                                skinApplyWeight(geo, currID, dstVertices, destMtx, srcVertices);
                            }
                        } else {
                            if (gao.getBoneShapeMatrices() != null
                                    && gao.getBoneShapeMatrices().size() > boneIDOffset
                                    && gao.getBoneShapeMatrices().get(boneIDOffset) != null) {
                                mtxDestTemp.set(gao.getBoneShapeMatrices().get(boneIDOffset));
                                shapeBoneMtxSet = true;
                            }
                        }
                        if (!shapeBoneMtxSet) {
                            destMtx.set(Matrix4x4.getIdentityMatrix());
                            skinApplyWeight(geo, currID, dstVertices, destMtx, srcVertices);
                        } else {
                            skinApplyMirroringIfSet(gao, geo, currID, destMtx, mtxDestTemp);
                            skinApplyWeight(geo, currID, dstVertices, destMtx, srcVertices);
                        }
                    } else {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            System.out.println("Bone not found for id offset: " + boneIDOffset + ", gao: " + gao.getGaoName());
                        }
                        destMtx.set(Matrix4x4.getIdentityMatrix());
                        skinApplyWeight(geo, currID, dstVertices, destMtx, srcVertices);
                    }
                    currID--;
                }
            }
            return true;
        }
        return false;
    }

    public static Integer getShapeElemOffsetFromGaoByKey(BFGaoChunk gao, int key) {
        if (gao.getBonesGroupChunkInstanceKLE() == null || gao.getBonesGroupChunkInstanceKLE().getParseHandler() == null
                || !(gao.getBonesGroupChunkInstanceKLE().getParseHandler() instanceof BFGRPContainerChunk)) {
            return null;
        }
        BFGRPContainerChunk grpc = (BFGRPContainerChunk) gao.getBonesGroupChunkInstanceKLE().getParseHandler();
        BFGRPChunk grp = grpc.getGRP();
        if (grp == null) {
            return null;
        }
        return grp.getElemOffsetByKey(key);
    }

    public static Matrix4x4 getShapeMatrixFromGaoByKey(BFGaoChunk gao, int key) {
        Integer offset = getShapeElemOffsetFromGaoByKey(gao, key);
        if (offset == null) {
            return null;
        }
        ArrayList<Matrix4x4> matrices = gao.getBoneShapeMatrices();
        if (matrices == null || matrices.size() <= offset) {
            return null;
        }
        return matrices.get(offset);
    }

    public static void computeBoneMatrixForAnimation(BFGaoChunk currentBone, BFGaoChunk mainGao, Matrix4x4 destMtx, boolean setGlobalMtx) {
        BFGaoChunk referencePointGao;
        Matrix4x4 mainGaoGlobalMtx = mainGao.getGlobalMatrix();
        Matrix4x4.getScale(mainGaoGlobalScale, mainGaoGlobalMtx);
        if (currentBone != null && currentBone.getLocalMatrix() != null && currentBone.getReferencePointGAOChunkKLE() != null
                && currentBone.getReferencePointGAOChunkKLE().getParseHandler() != null
                && currentBone.getReferencePointGAOChunkKLE().getParseHandler() instanceof BFGaoChunk) {
            referencePointGao = (BFGaoChunk) (currentBone.getReferencePointGAOChunkKLE().getParseHandler());
        } else {
            referencePointGao = null;
        }
        if (referencePointGao == mainGao) {
            destMtx.set(currentBone.getLocalMatrix());
        } else if (currentBone != null && currentBone.getLocalMatrix() != null && currentBone.getReferencePointGAOChunkKLE() != null
                && currentBone.getReferencePointGAOChunkKLE().getParseHandler() != null
                && currentBone.getReferencePointGAOChunkKLE().getParseHandler() instanceof BFGaoChunk
                && (currentBone.getFlags() & 0x400000) != 0) {
            Matrix4x4 currShapeMatrix = getShapeMatrixFromGaoByKey(mainGao, ((BFGaoChunk) (currentBone.getReferencePointGAOChunkKLE().getParseHandler())).getSelfKey());
            if (currShapeMatrix == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("BUG: currShapeMatrix for refgao (" + ((BFGaoChunk) (currentBone.getReferencePointGAOChunkKLE().getParseHandler())).getGaoName() + ") not found.");
                }
                return;
            }
            Matrix4x4.getScale(currShapeMatrixScale, currShapeMatrix);
            Matrix4x4.getScale(currBoneLocalMtxScale, currentBone.getLocalMatrix());
            currShapeMatrixScale.setX(currShapeMatrixScale.getX() * currBoneLocalMtxScale.getX());
            currShapeMatrixScale.setY(currShapeMatrixScale.getY() * currBoneLocalMtxScale.getY());
            currShapeMatrixScale.setZ(currShapeMatrixScale.getZ() * currBoneLocalMtxScale.getZ());
            Matrix4x4.matrixMultiplyWithNormalized(destMtx, currentBone.getLocalMatrix(), currShapeMatrix);
            Matrix4x4.orthoNormalize(destMtx);
            Matrix4x4.setScale(destMtx, currShapeMatrixScale);
        } else {
            Matrix4x4.invertMatrix(mainGaoMtxInvert, mainGao.getGlobalMatrix());
            Matrix4x4.matrixMultiplyWithNormalized(destMtx, mainGaoMtxInvert, currentBone.getGlobalMatrix());
        }
        if (setGlobalMtx) {
            // todo check for signbit (!8000000000)
            Matrix4x4.matrixMultiplyWithNormalized(currentBone.getGlobalMatrix(), destMtx, mainGaoGlobalMtx);
            if ((currentBone.getGlobalMatrix().getFlags() & 0x8) != 0) {
                Matrix4x4.orthoNormalize(currentBone.getGlobalMatrix());
            }
        }
    }
}
