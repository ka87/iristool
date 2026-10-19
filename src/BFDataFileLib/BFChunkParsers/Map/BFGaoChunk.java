/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierPhotoModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGAOInverseKinematics;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGAOSpecialLookAt;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoMorphModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoSnap;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierMeshWave;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoTransformedVerticesSnapshotStore;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoColorModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierLazyModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierParticleGenerator;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierProtexModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierROTRModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierShadowModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSoundFXModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSoundLoadingModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSoundModifierGEN;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSpriteGenModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSnake;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierObjectFakeLookAtCameraModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierXMECModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierXMENModifier;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFModifierBaseClass;
import Common.DataType.ActionsElemTransitionStructInside2;
import Common.DataType.AnimationPlayerDataContainer;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import Common.DataType.BoneShapeElem;
import Common.IrisToolDefines;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class BFGaoChunk extends BFChunkParseClass implements BFChunkParseInterface {

    public BFGaoChunk() {
        super();
        resetAnimationPlayerSpecificVars();
    }

    private Integer flags, flagsOrig;
    private Integer eventsKey, animationShapeKey, bonesGroupKey, actionsKey, actionsKeyOrig;
    private BFKeyListElem animationShapeChunkKLE, animationShapeChunkInstanceSpecificKLE;
    private BFKeyListElem bonesGroupChunkKLE, bonesGroupChunkInstanceKLE;
    private BFKeyListElem eventsChunkKLE;
    private Integer groupKey = 0;
    private Integer referencePointGAOKey = -1; // eljen a konzisztencia (by Ubisoft)
    private BFKeyListElem referencePointGAOChunkKLE, referencePointGAOChunkOrigKLE;
    private Integer lightKey = -1;
    private int scriptingContainerKey = -1;
    private String gaoName = "";
    private Integer geoKey = null, geoKeyOrig = null;
    private BFKeyListElem geoChunkKLE, geoChunkOrigKLE;
    private Integer materialGroupKey = null, materialGroupKeyOrig = null;
    private BFKeyListElem materialGroupChunkKLE, materialGroupChunkOrigKLE;
    private int primaryVertexColorArr[] = null, primaryVertexColorArrOrig[] = null;
    private ArrayList<Integer> modifierHandleFlagsArray;
    private ArrayList<Integer> modifierTypesArray;
    private ArrayList<BFChunkParseInterface> modifiersArray, modifiersArrayOrig;
    private Long len = 0L;
    private String signature = "";
    private byte unk1, unk2, camDistance, unk4, unk5, processFlags, processFlagsRuntime;
    private Integer visualFlags;
    private int unk7;
    private Matrix4x4 globalMatrix, globalMatrixOrig, globalMatrixSecond, globalMatrixThird;
    private Vector3 boundingBox1, boundingBox2, boundingBox3, boundingBox4;
    private Integer designFlags1, designFlags1Orig, designFlags2, primaryVertexColorNum, primaryVertexColorNumOrig; // unk1g's 3rd byte is used in skn draw
    private Matrix4x4 localMatrix, localMatrixOrig;
    private ArrayList<BoneShapeElem> boneShapes, boneShapesOrig;
    private ArrayList<Matrix4x4> boneShapeMatrices, boneShapeMatricesOrig;
    private int subGaoOrMtxPartsNum;
    private int hasMDF;
    private float unk5f1, renderMinReqDistSq;
    private short capacity;
    private byte unk5b1, unk5b2;
    private Short animationControlFlags = null;
    private int unk5eventskey;
    private int unk5sbkey;
    private int unk5waykey;
    private int collisionInstanceKey;
    private int collisionMapKey;
    private int u0x40unkint0, u0x40unkint1, u0x40unkint2, u0x40unkint3;
    private float u0x40unkf1, u0x40unkf2;
    private int u0x40key1, u0x40key2, u0x40key3, u0x40key4, u0x40unkint1_5, u0x40unkint1_6, u0x40unkint1_7,
            u0x40unkint1_8, u0x40unkint1_9, u0x40unkint1_10, u0x40unkint1_11;
    private Vector3 u0x40pos1, u0x40pos2;
    private BFKeyListElem actionListChunkKLE, actionListChunkOrigKLE;
    private BFKeyListElem groupChunkKLE;
    private BFKeyListElem scriptingContainerChunkKLE;
    private BFKeyListElem unk5eventsChunkKLE;
    private BFKeyListElem unk5sbChunkKLE;
    private BFKeyListElem unk5wayChunkKLE;
    private BFKeyListElem lightGAOChunkKLE;
    private BFKeyListElem collisionInstanceChunkKLE;
    private BFKeyListElem collisionMapKLE;
    // RUNTIME ONLY
    private AnimationPlayerDataContainer currentAttachedAnimationPlayerDataContainer = null;
    private AnimationPlayerDataContainer currentAttachedAnimationPlayerDataContainerForTransition = null;
    private int animationPlayerFlag; // byte 1 = 1-> one track plays, 2-> transition plays
    private int animationPlayerSlots;
    private int animationPlayerTime;
    private int animationPlayerUnknownComputedFlags;
    private int animationPlayerActionShouldInit;
    private float animationPlayerElapsedTimeSetInSetAction;
    private int animationPlayerProbablyActionID;
    private int animationPlayerProbablyActionIDAnother;
    private int animationPlayerProbablyActionIDElemID;
    private int animationPlayerActionLoopCounter;
    private BFActionChunk animationPlayerCurrentAction;
    private ActionsElemTransitionStructInside2 animationPlayerCurrentTransition;
    private boolean boundingBoxDebugEnabled = false;
    private BFGaoChunk u0x40Gao1, u0x40Gao2, u0x40Gao3, u0x40Gao4;

    // TODO: currently this is for workarounding broken transition
    public final void resetAnimationPlayerSpecificVars() {
        currentAttachedAnimationPlayerDataContainer = null;
        currentAttachedAnimationPlayerDataContainerForTransition = null;
        animationPlayerFlag = 0; // byte 1 = 1-> one track plays, 2-> transition plays
        animationPlayerSlots = 0;
        animationPlayerTime = 0;
        animationPlayerUnknownComputedFlags = 0;
        animationPlayerActionShouldInit = 0;
        animationPlayerElapsedTimeSetInSetAction = 0.0f;
        animationPlayerProbablyActionID = -1;
        animationPlayerProbablyActionIDAnother = -1;
        animationPlayerProbablyActionIDElemID = 0;
        animationPlayerActionLoopCounter = 0;
        animationPlayerCurrentAction = null;
        animationPlayerCurrentTransition = null;
    }

    public int getU0x40key1() {
        return u0x40key1;
    }

    public int getU0x40key2() {
        return u0x40key2;
    }

    public int getU0x40key3() {
        return u0x40key3;
    }

    public int getU0x40key4() {
        return u0x40key4;
    }

    public BFGaoChunk getU0x40Gao1() {
        return u0x40Gao1;
    }

    public void setU0x40Gao1(BFGaoChunk u0x40Gao1) {
        this.u0x40Gao1 = u0x40Gao1;
    }

    public BFGaoChunk getU0x40Gao2() {
        return u0x40Gao2;
    }

    public void setU0x40Gao2(BFGaoChunk u0x40Gao2) {
        this.u0x40Gao2 = u0x40Gao2;
    }

    public BFGaoChunk getU0x40Gao3() {
        return u0x40Gao3;
    }

    public void setU0x40Gao3(BFGaoChunk u0x40Gao3) {
        this.u0x40Gao3 = u0x40Gao3;
    }

    public BFGaoChunk getU0x40Gao4() {
        return u0x40Gao4;
    }

    public void setU0x40Gao4(BFGaoChunk u0x40Gao4) {
        this.u0x40Gao4 = u0x40Gao4;
    }
   
    public boolean isBoundingBoxDebugEnabled() {
        return boundingBoxDebugEnabled;
    }

    public void setBoundingBoxDebugEnabled(boolean boundingBoxDebugEnabled) {
        this.boundingBoxDebugEnabled = boundingBoxDebugEnabled;
    }

    public float getUnk5f1() {
        return unk5f1;
    }

    public float getRenderMinReqDistSq() {
        return renderMinReqDistSq;
    }

    public short getCapacity() {
        return capacity;
    }

    public byte getUnk5b1() {
        return unk5b1;
    }

    public byte getUnk5b2() {
        return unk5b2;
    }

    public Short getAnimationControlFlags() {
        return animationControlFlags;
    }

    public ArrayList<BoneShapeElem> getBoneShapes() {
        return boneShapes;
    }

    public ArrayList<Matrix4x4> getBoneShapeMatrices() {
        return boneShapeMatrices;
    }

    public void setBoneShapes(ArrayList<BoneShapeElem> boneShapes) {
        this.boneShapes = boneShapes;
    }

    public void setBoneShapeMatrices(ArrayList<Matrix4x4> boneShapeMatrices) {
        this.boneShapeMatrices = boneShapeMatrices;
    }

    public Integer getDesignFlags1() {
        return designFlags1;
    }

    public Integer getDesignFlags2() {
        return designFlags2;
    }

    public boolean shouldItRender() {
        if (designFlags1 == null) {
            return false;
        }
        return (designFlags1 & 0x8) != 0;
    }

    /**
     * Get zlist buffer ID for this Gao
     *
     * @param shouldCalc
     * @return zlistID
     */
    public Byte getZListBufferNum(boolean shouldCalc) {
        if (designFlags2 == null) {
            return null;
        }
        byte b = (byte) (designFlags2 & 0xff);
        if (b == -1) {
            b = 0;
        } else if (b > 10) {
            b = (byte) (b - 16);
        }
        /* If shouldCalc == true:
            - (ret > 4) : render occurs before water draw
            - (ret = 4) : render occurs just after water draw
            - (ret < 4) : render occurs after water draw        
         */
        if (shouldCalc) {
            b += 4;
            if (b > 7) {
                return 7;
            }
        }
        return b;
    }

    public void setDesignFlags1(Integer designFlags1) {
        this.designFlags1 = designFlags1;
    }

    public void setDesignFlags2(Integer designFlags2) {
        this.designFlags2 = designFlags2;
    }

    public byte getProcessFlagsRuntime() {
        return processFlagsRuntime;
    }

    public void setProcessFlagsRuntime(byte processFlagsRuntime) {
        this.processFlagsRuntime = processFlagsRuntime;
    }

    public Vector3 getBoundingBox1() {
        return boundingBox1;
    }

    public Vector3 getBoundingBox2() {
        return boundingBox2;
    }

    public Vector3 getBoundingBox3() {
        return boundingBox3;
    }

    public Vector3 getBoundingBox4() {
        return boundingBox4;
    }

    public Matrix4x4 getGlobalMatrix() {
        return globalMatrix;
    }

    public Matrix4x4 getGlobalMatrixSecond() {
        return globalMatrixSecond;
    }

    public Matrix4x4 getGlobalMatrixThird() {
        return globalMatrixThird;
    }

    public Matrix4x4 getLocalMatrix() {
        return localMatrix;
    }

    public Matrix4x4 getGlobalMatrixInitial() {
        if ((flags & 0x10000) != 0) {
            return globalMatrixSecond;
        } else {
            return globalMatrix;
        }
    }

    public void setLocalMatrix(Matrix4x4 localMatrix) {
        this.localMatrix = localMatrix;
    }

    public BFKeyListElem getReferencePointGAOChunkKLE() {
        return referencePointGAOChunkKLE;
    }

    public void setReferencePointGAOChunkKLE(BFKeyListElem referencePointGAOChunkKLE) {
        this.referencePointGAOChunkKLE = referencePointGAOChunkKLE;
    }

    public BFKeyListElem getActionListChunkKLE() {
        return actionListChunkKLE;
    }

    public void setActionListChunkKLE(BFKeyListElem actionListChunkKLE) {
        this.actionListChunkKLE = actionListChunkKLE;
    }

    public BFKeyListElem getUnk5eventsChunkKLE() {
        return unk5eventsChunkKLE;
    }

    public int getHasMDF() {
        return hasMDF;
    }

    public ArrayList<BFChunkParseInterface> getModifiersArray() {
        return modifiersArray;
    }

    public void setModifiersArray(ArrayList<BFChunkParseInterface> modifiersArray) {
        this.modifiersArray = modifiersArray;
    }

    public BFKeyListElem getAnimationShapeChunkKLE() {
        return animationShapeChunkKLE;
    }

    public BFKeyListElem getAnimationShapeChunkInstanceSpecificKLE() {
        return animationShapeChunkInstanceSpecificKLE;
    }

    public void backupAnimationShapeChunkKLE() {
        if (animationShapeChunkInstanceSpecificKLE == null && animationShapeChunkKLE != null) {
            BFKeyListElem tmpKLE = new BFKeyListElem(animationShapeChunkKLE.getKey(), BFAnimationShapeChunk.class,
                    false);
            if (animationShapeChunkKLE.getParseHandler() != null) {
                BFChunkParseInterface cpi = ((BFAnimationShapeChunk) (animationShapeChunkKLE.getParseHandler())).duplicate();
                tmpKLE.setParseHandler(cpi);
                animationShapeChunkInstanceSpecificKLE = tmpKLE;
            }
        }
    }

    public void setAnimationShapeChunkKLE(BFKeyListElem animationShapeChunkKLE) {
        this.animationShapeChunkKLE = animationShapeChunkKLE;
    }

    public BFKeyListElem getBonesGroupChunkKLE() {
        return bonesGroupChunkKLE;
    }

    public void setBonesGroupChunkKLE(BFKeyListElem bonesGroupChunkKLE) {
        this.bonesGroupChunkKLE = bonesGroupChunkKLE;
    }

    public BFKeyListElem getBonesGroupChunkInstanceKLE() {
        return bonesGroupChunkInstanceKLE;
    }

    public void setBonesGroupChunkInstanceKLE(BFKeyListElem bonesGroupChunkInstanceKLE) {
        this.bonesGroupChunkInstanceKLE = bonesGroupChunkInstanceKLE;
    }

    public BFKeyListElem getEventsChunkKLE() {
        return eventsChunkKLE;
    }

    public void setEventsChunkKLE(BFKeyListElem eventsChunkKLE) {
        this.eventsChunkKLE = eventsChunkKLE;
    }

    public Integer getLightKey() {
        return lightKey;
    }

    public int[] getPrimaryVertexColorArr() {
        return primaryVertexColorArr;
    }

    public void setPrimaryVertexColorArr(int[] primaryVertexColorArr) {
        if (primaryVertexColorArr == null || primaryVertexColorArr.length == 0) {
            this.primaryVertexColorArr = null;
            this.primaryVertexColorNum = 0;
            return;
        }

        this.primaryVertexColorArr = new int[primaryVertexColorArr.length];
        primaryVertexColorNum = primaryVertexColorArr.length;
        System.arraycopy(primaryVertexColorArr, 0, this.primaryVertexColorArr, 0, primaryVertexColorArr.length);
    }

    public Integer getMaterialGroupKey() {
        return materialGroupKey;
    }

    public void setMaterialGroupKey(Integer materialGroupKey) {
        this.materialGroupKey = materialGroupKey;
    }

    public BFKeyListElem getMaterialGroupChunkKLE() {
        return materialGroupChunkKLE;
    }

    public void setMaterialGroupChunkKLE(BFKeyListElem materialGroupChunkKLE) {
        this.materialGroupChunkKLE = materialGroupChunkKLE;
    }

    public String getGaoName() {
        if (gaoName != null) {
            return gaoName.trim();
        }
        return gaoName;
    }

    public Integer getReferencePointGAOKey() {
        return referencePointGAOKey;
    }

    public void setReferencePointGAOKey(Integer referencePointGAOKey) { // pivot point
        this.referencePointGAOKey = referencePointGAOKey;
    }

    public Integer getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(Integer groupKey) {
        this.groupKey = groupKey;
    }

    public BFKeyListElem getGroupChunkKLE() {
        return groupChunkKLE;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public int getVisualFlags() {
        return visualFlags;
    }

    public void setVisualFlags(int anotherFlags) {
        this.visualFlags = anotherFlags;
    }

    public Integer getEventsKey() {
        return eventsKey;
    }

    public Integer getAnimationKey() {
        return animationShapeKey;
    }

    public Integer getBonesGroupKey() {
        return bonesGroupKey;
    }

    public int getScriptingContainerKey() {
        return scriptingContainerKey;
    }

    public Integer getActionsKey() {
        if (actionsKey == null) {
            return null;
        }
        if (actionsKey == -1) {
            return 0; // Game does almost the same, except it will only set key,
            // if actions parsing completed. (key is 0, because of memset 0)
        }
        return actionsKey;
    }

    public BFKeyListElem getScriptingContainerChunkKLE() {
        return scriptingContainerChunkKLE;
    }
        
    public void setActionsKey(Integer actionsKey) {
        this.actionsKey = actionsKey;
    }

    public Integer getGeoKey() {
        return geoKey;
    }

    public void setGeoKey(Integer geoKey) {
        this.geoKey = geoKey;
    }

    public BFKeyListElem getGeoChunkKLE() {
        return geoChunkKLE;
    }

    public void setGeoChunkKLE(BFKeyListElem geoChunkKLE) {
        this.geoChunkKLE = geoChunkKLE;
    }

    public AnimationPlayerDataContainer getCurrentAttachedAnimationPlayerDataContainer() {
        return currentAttachedAnimationPlayerDataContainer;
    }

    public void setCurrentAttachedAnimationPlayerDataContainer(AnimationPlayerDataContainer currentAttachedAnimationPlayerDataContainer) {
        this.currentAttachedAnimationPlayerDataContainer = currentAttachedAnimationPlayerDataContainer;
    }

    public AnimationPlayerDataContainer getCurrentAttachedAnimationPlayerDataContainerForTransition() {
        return currentAttachedAnimationPlayerDataContainerForTransition;
    }

    public void setCurrentAttachedAnimationPlayerDataContainerForTransition(AnimationPlayerDataContainer currentAttachedAnimationPlayerDataContainerForTransition) {
        this.currentAttachedAnimationPlayerDataContainerForTransition = currentAttachedAnimationPlayerDataContainerForTransition;
    }

    public int getAnimationPlayerFlag() {
        return animationPlayerFlag;
    }

    public void setAnimationPlayerFlag(int animationPlayerFlag) {
        this.animationPlayerFlag = animationPlayerFlag;
    }

    public int getAnimationPlayerSlots() {
        return animationPlayerSlots;
    }

    public void setAnimationPlayerSlots(int animationPlayerSlots) {
        this.animationPlayerSlots = animationPlayerSlots;
    }

    public int getAnimationPlayerTime() {
        return animationPlayerTime;
    }

    public void setAnimationPlayerTime(int animationPlayerTime) {
        this.animationPlayerTime = animationPlayerTime;
    }

    public int getAnimationPlayerUnknownComputedFlags() {
        return animationPlayerUnknownComputedFlags;
    }

    public void setAnimationPlayerUnknownComputedFlags(int animationPlayerUnknownComputedFlags) {
        this.animationPlayerUnknownComputedFlags = animationPlayerUnknownComputedFlags;
    }

    public int getAnimationPlayerActionShouldInit() {
        return animationPlayerActionShouldInit;
    }

    public void setAnimationPlayerActionShouldInit(int animationPlayerActionShouldInit) {
        this.animationPlayerActionShouldInit = animationPlayerActionShouldInit;
    }

    public float getAnimationPlayerElapsedTimeSetInSetAction() {
        return animationPlayerElapsedTimeSetInSetAction;
    }

    public void setAnimationPlayerElapsedTimeSetInSetAction(float animationPlayerElapsedTimeSetInSetAction) {
        this.animationPlayerElapsedTimeSetInSetAction = animationPlayerElapsedTimeSetInSetAction;
    }

    public int getAnimationPlayerProbablyActionID() {
        return animationPlayerProbablyActionID;
    }

    public void setAnimationPlayerProbablyActionID(int animationPlayerProbablyActionID) {
        this.animationPlayerProbablyActionID = animationPlayerProbablyActionID;
    }

    public int getAnimationPlayerProbablyActionIDAnother() {
        return animationPlayerProbablyActionIDAnother;
    }

    public void setAnimationPlayerProbablyActionIDAnother(int animationPlayerProbablyActionIDAnother) {
        this.animationPlayerProbablyActionIDAnother = animationPlayerProbablyActionIDAnother;
    }

    public int getAnimationPlayerProbablyActionIDElemID() {
        return animationPlayerProbablyActionIDElemID;
    }

    public void setAnimationPlayerProbablyActionIDElemID(int animationPlayerProbablyActionIDElemID) {
        this.animationPlayerProbablyActionIDElemID = animationPlayerProbablyActionIDElemID;
    }

    public int getAnimationPlayerActionLoopCounter() {
        return animationPlayerActionLoopCounter;
    }

    public void setAnimationPlayerActionLoopCounter(int animationPlayerActionLoopCounter) {
        this.animationPlayerActionLoopCounter = animationPlayerActionLoopCounter;
    }

    public BFActionChunk getAnimationPlayerCurrentAction() {
        return animationPlayerCurrentAction;
    }

    public void setAnimationPlayerCurrentAction(BFActionChunk animationPlayerCurrentAction) {
        this.animationPlayerCurrentAction = animationPlayerCurrentAction;
    }

    public ActionsElemTransitionStructInside2 getAnimationPlayerCurrentTransition() {
        return animationPlayerCurrentTransition;
    }

    public void setAnimationPlayerCurrentTransition(ActionsElemTransitionStructInside2 animationPlayerCurrentTransition) {
        this.animationPlayerCurrentTransition = animationPlayerCurrentTransition;
    }

    public BFKeyListElem getLightGAOChunkKLE() {
        return lightGAOChunkKLE;
    }

    public int getCollisionInstanceKey() {
        return collisionInstanceKey;
    }

    public int getCollisionMapKey() {
        return collisionMapKey;
    }

    public BFKeyListElem getCollisionInstanceChunkKLE() {
        return collisionInstanceChunkKLE;
    }

    public BFKeyListElem getCollisionMapKLE() {
        return collisionMapKLE;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing GAO.");
        }

        int i;

        if (!buildFromParsed) {
            len = initParse();

            if (len == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Length read failed.");
                }
                return false;
            }

            long startPos = ru.getCurrentPos();

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("GAO length: " + len + " bytes.");
            }
        } else {
            wu.startBlockWithLength();
        }

        if (!buildFromParsed) {
            signature = ru.readNString(4, false);
        } else {
            wu.writeNString(signature, 4);
        }

        if (!".gao".equals(signature)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("GAO signature must be .gao. We got " + signature);
            }
            return false;
        }

        if (!buildFromParsed) {
            unk7 = ru.readInt(false); // game does not uses it
            flags = ru.readInt(false);
            flagsOrig = flags;
            visualFlags = ru.readInt(false);
        } else {
            wu.writeInt(unk7);
            wu.writeInt(flags);
            wu.writeInt(visualFlags);
        }

        if (!buildFromParsed) {
            unk1 = ru.readByte(false);
            unk2 = ru.readByte(false);
            camDistance = ru.readByte(false);
            unk4 = ru.readByte(false);
            unk5 = ru.readByte(false);
            processFlags = ru.readByte(false);
        } else {
            wu.writeByte(unk1);
            wu.writeByte(unk2);
            wu.writeByte(camDistance);
            wu.writeByte(unk4);
            wu.writeByte(unk5);
            wu.writeByte(processFlags);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Flags: %x", flags);
            logF("Visual flags: %x", visualFlags);

            logF("Unk1: %x", (int) unk1);
            logF("Unk2: %x", (int) unk2);
            logF("Cam distance (maybe): %x", (int) camDistance);
            logF("Unk4: %x", (int) unk4);
            logF("Unk5: %x", (int) unk5);
            logF("ProcessFlags: %x", (int) processFlags); // & 0x80 != 0 means its global matrix has been calculated
        }
        processFlagsRuntime = (byte) (processFlags & 0xC
                | ((processFlags & 0xC) >> 2) & 1
                | ((processFlags & 0xC
                | ((processFlags & 0xC) >> 2) & 1) >> 2) & 2);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("ProcessFlags (as game filters out): %x", (int) processFlagsRuntime);
        }
        if (!buildFromParsed) {
            globalMatrix = new Matrix4x4();
            if (!globalMatrix.readValues(ru)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("BFGaoChunk::parse(): Matrix4x4 read fail.");
                }
                return false;
            }
            globalMatrixOrig = Matrix4x4.copy(globalMatrix);
            //fix matrix: ubisoft fix
            if ((globalMatrix.getFlags() & 0x8) == 0) {
                globalMatrix.setW(1.0f);
            }
            // --
            globalMatrixSecond = new Matrix4x4();
            // Hack: game not set 0x4000000 here.
            if ((flags & 0x10000) != 0 || (flags & 0x4000000) != 0) {
                globalMatrixSecond.set(globalMatrix);
            }
            globalMatrixThird = new Matrix4x4();
            // Hack: game not set this here.
            if ((flags & 0x10000) != 0 && (flags & 0x4000000) != 0) {
                globalMatrixThird.set(globalMatrix);
            }
        } else {
            globalMatrix.writeValues(wu);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log(globalMatrix);
        }

        if ((flags & 0x80000) != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("GAO has size72 Bounding Box!");
            }
            if (!buildFromParsed) {
                boundingBox1 = new Vector3();
                if (!boundingBox1.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read fail.");
                    }
                    return false;
                }
            } else {
                boundingBox1.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(boundingBox1);
            }
            if (!buildFromParsed) {
                boundingBox2 = new Vector3();
                if (!boundingBox2.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read fail.");
                    }
                    return false;
                }
            } else {
                boundingBox2.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(boundingBox2);
            }
            if (!buildFromParsed) {
                boundingBox3 = new Vector3();
                if (!boundingBox3.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read fail.");
                    }
                    return false;
                }
            } else {
                boundingBox3.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(boundingBox3);
            }
            if (!buildFromParsed) {
                boundingBox4 = new Vector3();
                if (!boundingBox4.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read fail.");
                    }
                    return false;
                }
            } else {
                boundingBox4.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(boundingBox4);
            }
        } else {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("GAO has size48!");
            }
            if (!buildFromParsed) {
                boundingBox1 = new Vector3();
                if (!boundingBox1.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read fail.");
                    }
                    return false;
                }
            } else {
                boundingBox1.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(boundingBox1);
            }
            if (!buildFromParsed) {
                boundingBox2 = new Vector3();
                if (!boundingBox2.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read fail.");
                    }
                    return false;
                }
            } else {
                boundingBox2.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(boundingBox2);
            }
        }

        if ((flags & 0x1000) != 0) {
            if ((flags & 0x4000) != 0) {
                if (!buildFromParsed) {
                    geoKey = ru.readInt(false);
                    geoKeyOrig = geoKey;
                    materialGroupKey = ru.readInt(false);
                    materialGroupKeyOrig = materialGroupKey;
                } else {
                    wu.writeInt(geoKey);
                    wu.writeInt(materialGroupKey);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Geometry/Camera key: %x", geoKey);
                    logF("Material Group key: %x", materialGroupKey);
                }

                if (geoKey != -1) {
                    if (!buildFromParsed) {
                        geoChunkKLE = new BFKeyListElem(geoKey, BFGaoInnerChunkParser.class);
                        geoChunkOrigKLE = geoChunkKLE;
                    }
                    bfdfc.addToKeyList(geoChunkKLE);

                }
                if (materialGroupKey != -1) {
                    if (!buildFromParsed) {
                        materialGroupChunkKLE = new BFKeyListElem(materialGroupKey, BFGaoInnerChunkParser.class);
                        materialGroupChunkOrigKLE = materialGroupChunkKLE;
                    }
                    bfdfc.addToKeyList(materialGroupChunkKLE);
                }
                if (!buildFromParsed) {
                    designFlags1 = ru.readInt(false);
                    designFlags1Orig = designFlags1;
                    designFlags2 = ru.readInt(false);
                } else {
                    wu.writeInt(designFlags1);
                    wu.writeInt(designFlags2);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("DesignFlags1: %x (%d)", designFlags1, designFlags1);
                    logF("DesignFlags2: %x (%d), ZlistID: %d, Calculated ZListBufferId (used in game): %d", designFlags2, designFlags2, getZListBufferNum(false), getZListBufferNum(true));
                }
                if (!buildFromParsed) {
                    primaryVertexColorNum = ru.readInt(false);
                    primaryVertexColorNumOrig = primaryVertexColorNum;
                } else {
                    wu.writeInt(primaryVertexColorNum);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Forced calculated vertexcolor num: " + primaryVertexColorNum);
                }
                if (!buildFromParsed && primaryVertexColorNum > 0) {
                    primaryVertexColorArr = new int[primaryVertexColorNum];
                    primaryVertexColorArrOrig = new int[primaryVertexColorNum];
                }
                for (i = 0; i < primaryVertexColorNum; i++) {
                    if (!buildFromParsed) {
                        primaryVertexColorArr[i] = ru.readInt(false); // unk3data
                        primaryVertexColorArrOrig[i] = primaryVertexColorArr[i];
                    } else {
                        wu.writeInt(primaryVertexColorArr[i]);
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF(" --> Forced calculated vertexcolor [%d]: %x (%d)", i, primaryVertexColorArr[i], primaryVertexColorArr[i]);
                    }
                }
            }
            if ((flags & 0x400000) != 0) {
                if (!buildFromParsed) {
                    referencePointGAOKey = ru.readInt(false);
                } else {
                    wu.writeInt(referencePointGAOKey);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Pivot point (referencePointGAOKey): %x", referencePointGAOKey);
                }

                if (referencePointGAOKey != -1) {
                    if (!buildFromParsed) {
                        referencePointGAOChunkKLE = new BFKeyListElem(referencePointGAOKey, BFGaoChunk.class);
                        referencePointGAOChunkOrigKLE = referencePointGAOChunkKLE;
                    }
                    bfdfc.addToKeyList(referencePointGAOChunkKLE);
                    bfdfc.addToKeyList(referencePointGAOChunkKLE); // a jatek ket reference pointot tárol
                }
                if (!buildFromParsed) {
                    localMatrix = new Matrix4x4();
                    if (!localMatrix.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("localMatrix Matrix4x4 read fail.");
                        }
                        return false;
                    }
                    localMatrixOrig = Matrix4x4.copy(localMatrix);
                } else {
                    localMatrix.writeValues(wu);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(localMatrix);
                }
                // ubisoft fix, fixes jade's pupilles to position correctly
                if ((localMatrix.getFlags() & 0x8) == 0) {
                    localMatrix.setW(1.0f);
                }
            }
            if ((flags & 0x2) != 0) {
                if (!buildFromParsed) {
                    eventsKey = ru.readInt(false);
                    animationShapeKey = ru.readInt(false);
                    bonesGroupKey = ru.readInt(false);
                    actionsKey = ru.readInt(false);
                    actionsKeyOrig = actionsKey;
                } else {
                    wu.writeInt(eventsKey);
                    wu.writeInt(animationShapeKey);
                    wu.writeInt(bonesGroupKey);
                    wu.writeInt(actionsKey);

                }
                if (actionsKey != -1) {
                    if (!buildFromParsed) {
                        actionListChunkKLE = new BFKeyListElem(actionsKey, BFActionListChunk.class);
                        actionListChunkOrigKLE = actionListChunkKLE;
                    }
                    bfdfc.addToKeyList(actionListChunkKLE);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("ActionsKey=0x%x [eventsKey=0x%x, animationShapeKey=0x%x, subGaoBonesGroupKey=0x%x]", actionsKey, eventsKey, animationShapeKey, bonesGroupKey);
                }
            }
            if ((flags & 0x200000) != 0) {
                if (!buildFromParsed) {
                    subGaoOrMtxPartsNum = ru.readInt(false);
                    boneShapes = new ArrayList<>();
                    boneShapesOrig = new ArrayList<>();
                    boneShapeMatrices = new ArrayList<>();
                    boneShapeMatricesOrig = new ArrayList<>();
                } else {
                    wu.writeInt(subGaoOrMtxPartsNum);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Multipart Gao subgaos/animation shape matrices number: " + subGaoOrMtxPartsNum);
                }
                if ((flags & 0x1000000) != 0) {
                    for (i = 0; i < subGaoOrMtxPartsNum; i++) {
                        int boneGaoKey, boneID;
                        BoneShapeElem bse;
                        BoneShapeElem bseOrig = null;
                        if (!buildFromParsed) {
                            boneGaoKey = ru.readInt(false);
                            bse = new BoneShapeElem();
                            bseOrig = new BoneShapeElem();
                            bse.setBoneGaoKey(boneGaoKey);
                            bseOrig.setBoneGaoKey(boneGaoKey);
                        } else {
                            bse = boneShapes.get(i);
                            boneGaoKey = bse.getBoneGaoKey();
                            wu.writeInt(boneGaoKey);
                        }
                        BFKeyListElem boneGaoChunkKLE;

                        if (boneGaoKey != -1) {
                            if (!buildFromParsed) {
                                boneGaoChunkKLE = new BFKeyListElem(boneGaoKey, BFGaoChunk.class);
                                bse.setBoneGaoChunkKLE(boneGaoChunkKLE);
                                if (bseOrig != null) {
                                    bseOrig.setBoneGaoChunkKLE(boneGaoChunkKLE);
                                }
                            } else {
                                boneGaoChunkKLE = bse.getBoneGaoChunkKLE();
                            }
                            bfdfc.addToKeyList(boneGaoChunkKLE);
                        } else {
                            bse.setBoneGaoChunkKLE(null);
                            if (bseOrig != null) {
                                bseOrig.setBoneGaoChunkKLE(null);
                            }
                        }
                        if (!buildFromParsed) {
                            boneID = ru.readInt(false);
                            bse.setBoneID(boneID);
                            boneShapes.add(bse);
                            if (bseOrig != null) {
                                bseOrig.setBoneID(boneID);
                                boneShapesOrig.add(bse);
                            }
                        } else {
                            boneID = bse.getBoneID();
                            wu.writeInt(boneID);
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF(" -->(boneGao num=%d) boneGaoKey=0x%x, boneId=%d (%x)", i, boneGaoKey, boneID, boneID);
                        }
                    }
                } else {
                    Matrix4x4 animationShapeMtx;
                    for (i = 0; i < subGaoOrMtxPartsNum; i++) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF(" -->(Animation shape matrix num=%d)", i);
                        }
                        if (!buildFromParsed) {
                            animationShapeMtx = new Matrix4x4();
                            if (!animationShapeMtx.readValues(ru)) {
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    log("Animation shape matrix Matrix4x4 read fail.");
                                }
                                return false;
                            }
                            boneShapeMatrices.add(animationShapeMtx);
                            boneShapeMatricesOrig.add(animationShapeMtx);
                        } else {
                            animationShapeMtx = boneShapeMatrices.get(i);
                            animationShapeMtx.writeValues(wu);
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(animationShapeMtx);
                        }
                    }
                }
            }
        }

        if ((flags & 0x2000) != 0) {
            if (!buildFromParsed) {
                groupKey = ru.readInt(false);
            } else {
                wu.writeInt(groupKey);

            }

            if ((flags & 0x800000) != 0 && groupKey != 0) {
                if (!buildFromParsed) {
                    groupChunkKLE = new BFKeyListElem(groupKey, BFGRPContainerChunk.class);
                }
                bfdfc.addToKeyList(groupChunkKLE);
            }

            if (!buildFromParsed) {
                hasMDF = ru.readInt(false);
            } else {
                wu.writeInt(hasMDF);
            }

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("GroupKey: %x", groupKey);
                logF("Has gao Modifier? %s", (hasMDF == 0x1 ? "yes" : "no"));
            }

            if (!buildFromParsed) {
                unk5f1 = ru.readFloat(false);
                renderMinReqDistSq = ru.readFloat(false); // game resets this to 0
                capacity = ru.readShort(false);
                unk5b1 = ru.readByte(false);
                unk5b2 = ru.readByte(false);
                animationControlFlags = ru.readShort(false);
            } else {
                wu.writeFloat(unk5f1);
                wu.writeFloat(renderMinReqDistSq);
                wu.writeShort(capacity);
                wu.writeByte(unk5b1);
                wu.writeByte(unk5b2);
                wu.writeShort(animationControlFlags);
            }

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk5f1=%f, renderMinReqDistSq=%f, capacity=%d (%x), unk5b1=%d (%x), unk5b2=%d (%x), animationControlFlags=%x", unk5f1, renderMinReqDistSq, capacity, capacity, (int) unk5b1, (int) unk5b1, (int) unk5b2, (int) unk5b2, animationControlFlags);
            }

            if ((flags & 0x20) != 0) {
                if (!buildFromParsed) {
                    scriptingContainerKey = ru.readInt(false);
                } else {
                    wu.writeInt(scriptingContainerKey);

                }
                if (scriptingContainerKey != -1) {
                    if (!buildFromParsed) {
                        scriptingContainerChunkKLE = new BFKeyListElem(scriptingContainerKey, BFScriptingContainerChunk.class);
                    }
                    bfdfc.addToKeyList(scriptingContainerChunkKLE);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Scripting Container Key: %x", scriptingContainerKey);
                }
            }

            if ((flags & 0x2000000) != 0) {
                if (!buildFromParsed) {
                    unk5eventskey = ru.readInt(false);
                } else {
                    wu.writeInt(unk5eventskey);

                }
                if (unk5eventskey != -1) {
                    if (!buildFromParsed) {
                        unk5eventsChunkKLE = new BFKeyListElem(unk5eventskey, BFEventsChunk.class);
                    }
                    bfdfc.addToKeyList(unk5eventsChunkKLE);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Events Key: %x", unk5eventskey);
                }
            }

            if ((flags & 0x8000000) != 0) {
                if (!buildFromParsed) {
                    unk5sbkey = ru.readInt(false);
                } else {
                    wu.writeInt(unk5sbkey);

                }
                if (unk5sbkey != -1) {
                    if (!buildFromParsed) {
                        unk5sbChunkKLE = new BFKeyListElem(unk5sbkey, BFSoundBankChunk.class);
                    }
                    bfdfc.addToKeyList(unk5sbChunkKLE);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Sound Bank Key: %x", unk5sbkey);
                }
            }

            if ((flags & 0x40000) != 0) {
                if (!buildFromParsed) {
                    unk5waykey = ru.readInt(false);
                } else {
                    wu.writeInt(unk5waykey);

                }
                if (unk5waykey != -1) {
                    if (!buildFromParsed) {
                        unk5wayChunkKLE = new BFKeyListElem(unk5waykey, BFWAYGroupInGAOChunk.class);
                    }
                    bfdfc.addToKeyList(unk5wayChunkKLE);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("GAO WAYS Key: %x", unk5waykey);
                }
            }

            if ((flags & 0x10) != 0) {
                if (!buildFromParsed) {
                    lightKey = ru.readInt(false);
                } else {
                    wu.writeInt(lightKey);

                }
                if (lightKey != -1) {
                    if (!buildFromParsed) {
                        lightGAOChunkKLE = new BFKeyListElem(lightKey, BFGaoInnerChunkParser.class);
                    }
                    bfdfc.addToKeyList(lightGAOChunkKLE);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Light Key: %x", lightKey);
                }
            }

            if ((flags & 0x40) != 0) {
                if (!parse0x40Storage(buildFromParsed)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("0x40 Storage parsing failed.");
                    }
                    return false;
                }
            }

            if (hasMDF != 0) {
                if (!parseModifierRecursive(true, true, 0, buildFromParsed)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Modifier parsing failed.");
                    }
                    return false;
                }
            }

        }

        if ((flags & 0x600) != 0) {
            if (!buildFromParsed) {
                collisionInstanceKey = ru.readInt(false);
            } else {
                wu.writeInt(collisionInstanceKey);

            }
            if (collisionInstanceKey != -1) {
                if (!buildFromParsed) {
                    collisionInstanceChunkKLE = new BFKeyListElem(collisionInstanceKey, BFCollisionInstanceChunk.class);
                }
                bfdfc.addToKeyList(collisionInstanceChunkKLE);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Collision Instance Key: %x", collisionInstanceKey);
            }
        }

        if ((flags & 0x100) != 0) {
            if (!buildFromParsed) {
                collisionMapKey = ru.readInt(false);
            } else {
                wu.writeInt(collisionMapKey);

            }
            if (collisionMapKey != -1) {
                if (!buildFromParsed) {
                    collisionMapKLE = new BFKeyListElem(collisionMapKey, BFCollisionMapGroupChunk.class);
                }
                bfdfc.addToKeyList(collisionMapKLE);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Collision Map Key: %x", collisionMapKey);
            }
        }

        if (!buildFromParsed) {
            int gaoNameSize = ru.readInt(false);
            gaoName = ru.readNString(gaoNameSize, false, false);
        } else {
            wu.writeInt(gaoName.length());
            wu.writeNString(gaoName, gaoName.length());
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("GAO name: " + gaoName);
        }

        if (buildFromParsed) {
            wu.finishBlockWithLength();
        }

        return true;
    }

    // used for storage several keys, floats, ints (referenced from compiled scripting)
    private boolean parse0x40Storage(Boolean buildFromParsed) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Parsing 0x40 Storage:");
        }

        if (!buildFromParsed) {
            u0x40unkint0 = ru.readInt(false);
            u0x40unkint1 = ru.readInt(false);
            u0x40unkint2 = ru.readInt(false);
            u0x40unkint3 = ru.readInt(false);
            u0x40unkf1 = ru.readFloat(false);
            u0x40unkf2 = ru.readFloat(false);
        } else {
            wu.writeInt(u0x40unkint0);
            wu.writeInt(u0x40unkint1);
            wu.writeInt(u0x40unkint2);
            wu.writeInt(u0x40unkint3);
            wu.writeFloat(u0x40unkf1);
            wu.writeFloat(u0x40unkf2);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("u0x40unkint0=%d (%x)", u0x40unkint0, u0x40unkint0);
            logF("u0x40unkint1=%d (%x)", u0x40unkint1, u0x40unkint1);
            logF("u0x40unkint2=%d (%x)", u0x40unkint2, u0x40unkint2);
            logF("u0x40unkint3=%d (%x)", u0x40unkint3, u0x40unkint3);
            logF("u0x40unkf1=%f", u0x40unkf1);
            logF("u0x40unkf2=%f", u0x40unkf2);
        }

        if (!buildFromParsed) {
            u0x40pos1 = new Vector3();
            u0x40pos2 = new Vector3();

            if (!u0x40pos1.readValues(ru)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("parse0x40Inner: 1st Vector3 read failed.");
                }
                return false;
            }
            log(u0x40pos1);

            if (!u0x40pos2.readValues(ru)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("parse0x40Inner: 2nd Vector3 read failed.");
                }
                return false;
            }
            log(u0x40pos2);
        } else {
            u0x40pos1.writeValues(wu);
            u0x40pos2.writeValues(wu);
        }

        if (!buildFromParsed) {
            // see @ addr 40fb00
            u0x40key1 = ru.readInt(false);
            u0x40key2 = ru.readInt(false);
            u0x40key3 = ru.readInt(false);
            u0x40key4 = ru.readInt(false);
            u0x40unkint1_5 = ru.readInt(false);
            u0x40unkint1_6 = ru.readInt(false);
            u0x40unkint1_7 = ru.readInt(false);
            u0x40unkint1_8 = ru.readInt(false);
            u0x40unkint1_9 = ru.readInt(false);
            u0x40unkint1_10 = ru.readInt(false);
            u0x40unkint1_11 = ru.readInt(false);
        } else {
            wu.writeInt(u0x40key1);
            wu.writeInt(u0x40key2);
            wu.writeInt(u0x40key3);
            wu.writeInt(u0x40key4);
            wu.writeInt(u0x40unkint1_5);
            wu.writeInt(u0x40unkint1_6);
            wu.writeInt(u0x40unkint1_7);
            wu.writeInt(u0x40unkint1_8);
            wu.writeInt(u0x40unkint1_9);
            wu.writeInt(u0x40unkint1_10);
            wu.writeInt(u0x40unkint1_11);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("u0x40key1: %x (%d)", u0x40key1, u0x40key1);
            logF("u0x40key2: %x (%d)", u0x40key2, u0x40key2);
            logF("u0x40key3: %x (%d)", u0x40key3, u0x40key3);
            logF("u0x40key4: %x (%d)", u0x40key4, u0x40key4);
            logF("u0x40unkint1_5: %d (%x)", u0x40unkint1_5, u0x40unkint1_5);
            logF("u0x40unkint1_6: %d (%x)", u0x40unkint1_6, u0x40unkint1_6);
            logF("u0x40unkint1_7: %d (%x)", u0x40unkint1_7, u0x40unkint1_7);
            logF("u0x40unkint1_8: %d (%x)", u0x40unkint1_8, u0x40unkint1_8);
            logF("u0x40unkint1_9: %d (%x)", u0x40unkint1_9, u0x40unkint1_9);
            logF("u0x40unkint1_10: %d (%x)", u0x40unkint1_10, u0x40unkint1_10);
            logF("u0x40unkint1_11: %d (%x)", u0x40unkint1_11, u0x40unkint1_11);
        }

        return true;
    }

    private boolean parseModifierRecursive(boolean doRecursion, Boolean firstCall, int recursionLevel, Boolean buildFromParsed) {
        Integer type, handleFlags, recursion;

        /*
        A modifiereknek két csoportja van, első, ami az adott gao renderelése előtt fut le, és egy másik, ami utána. 
        Van olyan, ami mindkét esetben csinál valamit.
        Modifier 0: mindkettő
        Modifier 1: előtte
        Modifier 2: mindkettő
        Modifier 3: előtte (ik)
        Modifier 4: előtte (morph)
        Modifier 5: előtte
        Modifier 6: sehol (sdw)
        Modifier 7: előtte
        Modifier 8: előtte
        Modifier 9: előtte
        Modifier 10: előtte (xmec)
        Modifier 11: utána (spg)
        Modifier 12: mindkettő
        Modifier 13: előtte
        Modifier 14: előtte (snake)
        Modifier 15: előtte
        Modifier 16: utána
        Modifier 17: sehol
        Modifier 18: előtte
        Modifier 19: előtte
        Modifier 20: sehol
        Modifier 21: mindkettő
        Modifier 22: előtte
        Modifier 23: mindkettő
        Modifier 24: előtte        
         */
        // Modifier block format: [type][data][type][data]...[-1]
        if (!buildFromParsed) {
            if (firstCall) {
                modifierTypesArray = new ArrayList<>();
                modifierHandleFlagsArray = new ArrayList<>();
                modifiersArray = new ArrayList<>();
                modifiersArrayOrig = new ArrayList<>();
            }
            type = ru.readInt(false);
            handleFlags = ru.readInt(false);
            modifierTypesArray.add(type);
            modifierHandleFlagsArray.add(flags);
        } else {
            if (recursionLevel >= modifierTypesArray.size()) {
                return true;
            }
            type = modifierTypesArray.get(recursionLevel);
            handleFlags = modifierHandleFlagsArray.get(recursionLevel);
            wu.writeInt(type);
            wu.writeInt(handleFlags);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Modifier type: %d, HandleFlags: %d (%x)", type, handleFlags, handleFlags);
        }

        boolean ret = true;
        BFChunkParseInterface mdf = null;

        switch (type) {
            case 0:
                if (!buildFromParsed) {
                    mdf = new BFModifierGeoSnap();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 1:
                if (!buildFromParsed) {
                    mdf = new BFModifierMeshWave();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 3:
                if (!buildFromParsed) {
                    mdf = new BFModifierGAOInverseKinematics();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 4:
                if (!buildFromParsed) {
                    mdf = new BFModifierGeoMorphModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 5:
                if (!buildFromParsed) {
                    mdf = new BFModifierObjectFakeLookAtCameraModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 6:
                if (!buildFromParsed) {
                    mdf = new BFModifierShadowModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 7:
                if (!buildFromParsed) {
                    mdf = new BFModifierGAOSpecialLookAt();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 8:
                if (!buildFromParsed) {
                    mdf = new BFModifierSoundModifierGEN();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 9:
                if (!buildFromParsed) {
                    mdf = new BFModifierXMENModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 10:
                if (!buildFromParsed) {
                    mdf = new BFModifierXMECModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 11:
                if (!buildFromParsed) {
                    mdf = new BFModifierSpriteGenModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            /*case 12: MeshAxisOffsetter*/
            case 13:
                if (!buildFromParsed) {
                    mdf = new BFModifierROTRModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 14:
                if (!buildFromParsed) {
                    mdf = new BFModifierSnake();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 15:
                if (!buildFromParsed) {
                    mdf = new BFModifierSoundFXModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 16:
                if (!buildFromParsed) {
                    mdf = new BFModifierProtexModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 18:
                if (!buildFromParsed) {
                    mdf = new BFModifierParticleGenerator();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 19:
                if (!buildFromParsed) {
                    mdf = new BFModifierSoundLoadingModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 20:
                if (!buildFromParsed) {
                    mdf = new BFModifierPhotoModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 21:
                if (!buildFromParsed) {
                    mdf = new BFModifierGeoTransformedVerticesSnapshotStore();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            /*case 22: MeshTransform2DToAxis*/
            case 23:
                if (!buildFromParsed) {
                    mdf = new BFModifierGeoColorModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            case 24:
                if (!buildFromParsed) {
                    mdf = new BFModifierLazyModifier();
                    modifiersArray.add(mdf);
                } else {
                    mdf = modifiersArray.get(recursionLevel);
                }
                ret = mdf.parse(bfdfc, insideBinFile, buildFromParsed);
                break;
            default:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Unhandled modifier type: " + type);
                }
                ret = false;
        }
        modifiersArrayOrig.add(mdf);

        if (!ret) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Modifier type: " + type + " parse failed.");
            }
            return false;
        } else if (mdf != null) {
            ((BFModifierBaseClass) mdf).setHandleFlags(handleFlags);
        }

        if (!buildFromParsed) {
            recursion = ru.readInt(true); // not increment read pos, because it can be type or -1. 
        } else {
            recursion = modifierTypesArray.get(recursionLevel + 1); // in begin of function there is a check for elem num.
        }

        if (recursion != -1 && doRecursion) {
            ret = parseModifierRecursive(true, false, recursionLevel + 1, buildFromParsed);
        }

        if (firstCall) {
            if (!buildFromParsed) {
                ru.readInt(false); // if we reach -1, we should increment read ptr, because read modifiers recursively finished
            } else {
                wu.writeInt(-1);
            }
        }
        return ret;
    }

    public static void setFlashMatrix(BFGaoChunk gao, Matrix4x4 src) {
        Matrix4x4 flashMatrixDest;

        if ((gao.getFlags() & 0x4000000) != 0) {
            if ((gao.getFlags() & 0x10000) != 0) {
                flashMatrixDest = gao.getGlobalMatrixThird();
            } else {
                flashMatrixDest = gao.getGlobalMatrixSecond();
            }
            flashMatrixDest.set(src);
        } else {
            gao.setFlags(gao.getFlags() | 0x4000000);
            if ((gao.getFlags() & 0x10000) != 0) {
                flashMatrixDest = gao.getGlobalMatrixThird();
            } else {
                flashMatrixDest = gao.getGlobalMatrixSecond();
            }
            flashMatrixDest.set(src);
        }
    }

    public static void restoreGaoInitialPosition(BFGaoChunk gao) {
        BFGaoChunk refGao;

        if ((gao.getFlags() & 0x400000) != 0 && gao.getLocalMatrix() != null
                && gao.getReferencePointGAOChunkKLE() != null
                && (refGao = (BFGaoChunk) (gao.getReferencePointGAOChunkKLE().getParseHandler())) != null) {
            if ((gao.getFlags() & 0x10000) != 0) {
                gao.getLocalMatrix().set(gao.getGlobalMatrixSecond());
            }
            Matrix4x4.matrixMultiplyWithNormalized(gao.getGlobalMatrix(), gao.getLocalMatrix(), refGao.getGlobalMatrix());
        } else if ((gao.getFlags() & 0x10000) != 0) {
            gao.getGlobalMatrix().set(gao.getGlobalMatrixSecond());
        }
        if ((gao.getFlags() & 0x2) != 0) {
            if ((gao.getFlags() & 0x400000) != 0) {
                BFGaoChunk.setFlashMatrix(gao, gao.getLocalMatrix());
            } else {
                BFGaoChunk.setFlashMatrix(gao, gao.getGlobalMatrix());
            }
        }
    }

    @Override
    public String toString() {
        if (gaoName == null) {
            return "<name not read>";
        }
        String ext = "";
        Object obj;
        if (geoChunkKLE != null && (obj = geoChunkKLE.getParseHandler()) != null && obj instanceof BFGaoInnerChunkParser) {
            if (((BFGaoInnerChunkParser) obj).getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY)) {
                ext += "Geometry";
            }
            if (((BFGaoInnerChunkParser) obj).getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_STATICLOD)) {
                ext += "Geometry, LOD";
            }
            if (((BFGaoInnerChunkParser) obj).getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_CAMERA)) {
                ext += "Camera";
            }
            if (((BFGaoInnerChunkParser) obj).getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR)) {
                ext += "ParticleGen";
            }
            if (((BFGaoInnerChunkParser) obj).getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_STRINGSTRUCT)) {
                ext += "StringDraw";
            }
        }
        if (this.groupKey != null && !this.groupKey.equals(-1) && !this.groupKey.equals(0)) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Group";
        }
        if ((this.flags & 0x2) != 0 && bonesGroupKey != 0 && bonesGroupKey != -1) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Bones Group";
        }

        if ((this.flags & 0x200000) != 0) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            if ((this.flags & 0x1000000) != 0) {
                ext += "Bone GAOs";
            } else {
                ext += "Animation Shape matrices";
            }
        }

        if ((this.flags & 0x2) != 0 && actionsKey != 0 && actionsKey != -1) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Actions";
        }

        if ((this.flags & 0x2) != 0 && animationShapeKey != 0 && animationShapeKey != -1) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Animation";
        }

        // temp
        if ((this.flags & 0x2) != 0 && (actionsKey == 0 || actionsKey == -1)
                && eventsKey != 0 && eventsKey != -1) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Events [without Actions]";
        }

        if ((this.flags & 0x4) != 0) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Physics enabled";
        }

        if ((this.flags & 0x10) != 0 && lightKey != -1) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Light";
        }

        if ((this.flags & 0x20) != 0) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Scripting";
        }

        if ((this.flags & 0x2000000) != 0) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Events";
        }

        if ((this.flags & 0x700) != 0) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Collision ";
            if ((this.flags & 0x100) != 0) {
                ext += "Map";
                if ((this.flags & 0x600) != 0) {
                    ext += "+";
                }
            }
            if ((this.flags & 0x600) != 0) {
                ext += "Inst";
            }
        }

        if (hasMDF != 0) {
            if (!ext.isEmpty()) {
                ext += ", ";
            }
            ext += "Has modifier";
        }
        Integer flagsSafe = (this.flags == null ? 0 : this.flags);
        Integer visualFlagsSafe = (this.visualFlags == null ? 0 : this.visualFlags);
        return gaoName + (ext.equals("") ? "" : " (" + ext + ")") + String.format(" [Flags: %x] [VisFlags: %x]%s",
                flagsSafe, visualFlagsSafe, (!shouldItRender() && designFlags1 != null ? "[Invisible]" : ""));
    }

    public String getGaoFlagBitMeaning(int num) {
        /*
        double bit = Math.log((double) num) / Math.log(2);
        double test = Math.pow(2, bit);
        if (num != test) {
            return "<invalid>";
        }
         */
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            if ((num & 0x1) != 0) {
                return "child elem (causes not to render it directly)"; // skeletal, bone elems: game filters out, when make gaos visible
            }
            if ((num & 0x2) != 0) {
                return "multipart container gao (eg. actor)";
            }
            if ((num & 0x4) != 0) {
                return "physics enabled";
            }
            if ((num & 0x8) != 0) {
                return "hierarchy recalc call skip";
            }
            if ((num & 0x10) != 0) {
                return "has lighting";
            }
            if ((num & 0x20) != 0) {
                return "has scripting";
            }
            if ((num & 0x40) != 0) {
                return "has 0x40 storage";
            }
            if ((num & 0x100) != 0) {
                return "has collision map (+ goes to intersection)";
            }
            if ((num & 0x200) != 0) {
                return "has collision instance (0x200)";
            }
            if ((num & 0x400) != 0) {
                return "has collision instance (0x400)";
            }
            if ((num & 0x1000) != 0) {
                return "visual gao (can contain geo/bones/vertexcolors/actions/etc)";
            }
            if ((num & 0x2000) != 0) {
                return "has action/event group";
            }
            if ((num & 0x4000) != 0) {
                return "has geo/cam and/or material";
            }
            if ((num & 0x8000) != 0) {
                return "supports message (MSG) send/recv";
            }
            if ((num & 0x10000) != 0) {
                return "+1 global mtx storage (storing initial pos)";
            }
            if ((num & 0x20000) != 0) {
                return "duplicated game object"; // we should not see this type in wowbrowser
            }
            if ((num & 0x40000) != 0) {
                return "has ways";
            }
            if ((num & 0x80000) != 0) {
                return "has local bounding box (size 72) too";
            }
            if ((num & 0x200000) != 0) {
                return "has skin (skn)";
            }
            if ((num & 0x400000) != 0) {
                return "has localmatrix (and GAO pivot refpoint optionally)";
            }
            if ((num & 0x800000) != 0) {
                return "has group";
            }
            if ((num & 0x1000000) != 0) {
                return "skin has gao linked/ids too, not only the matrix";
            }
            if ((num & 0x2000000) != 0) {
                return "has events";
            }
            if ((num & 0x4000000) != 0) {
                return "+1 global mtx storage (flash matrix) and calc";
            }
            if ((num & 0x8000000) != 0) {
                return "has soundbank";
            }
            return "unknown";
        } else {
            return "";
        }
    }

    public String getGaoVisualFlagBitMeaning(int num) {
        /*
        double bit = Math.log((double) num) / Math.log(2);
        double test = Math.pow(2, bit);
        if (num != test) {
            return "<invalid>";
        }
         */
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            if ((num & 0x1) != 0) {
                return "gao is in gaotable (selected from all gao list table)"; // see TAM (40bf70)
            }
            if ((num & 0x2) != 0) {
                return "ignore visibility check";
            }
            if ((num & 0x4) != 0) {
                return "invisible (game checks this directly before render)";
            }
            if ((num & 0x20) != 0) {
                return "collision detection list already allocated";
            }
            if ((num & 0x80) != 0) {
                return "snp sorting enabled, and has been added to intersection";
            }
            if ((num & 0x100000) != 0) {
                return "has collision detection list";
            }
            if ((num & 0x200000) != 0) {
                return "make object invisible (control flag)";
            }
            if ((num & 0x400000) != 0) {
                return "deactivate object (control flag)";
            }
            if ((num & 0x800000) != 0) {
                return "make object look at camera";
            }
            if ((num & 0x2000000) != 0) {
                return "do not compute global BV from local BV (use global)";
            }
            if ((num & 0x10000000) != 0) {
                return "always render (ignores culling, distance checking)";
            }
            if ((num & 0x20000000) != 0) {
                return "make object invisible permanently (stays after reset)";
            }
            if ((num & 0x40000000) != 0) {
                return "deactivate object permanently (stays after reset)";
            }
            if ((num & 0x80000000) != 0) {
                return "no interpolationKeyPlay without Events running";
            }

            return "unknown";
        } else {
            return "";
        }
    }

    public String getGaoAnimationControlFlagBitMeaning(int num) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            if ((num & 0x1) != 0) {
                return "disable animation"; // ignores ani onecall
            }
            if ((num & 0x200) != 0) {
                return "ani no pos write";
            }
            if ((num & 0x400) != 0) {
                return "ani no transf write";
            }
            return "unknown";
        } else {
            return "";
        }
    }

    @Override
    public BFGaoChunk duplicate() {
        BFGaoChunk ret = new BFGaoChunk();

        ret.setSelfKey(selfKey);
        ret.flags = flags | 0x20000;
        ret.flagsOrig = flagsOrig | 0x20000;
        ret.eventsKey = eventsKey;
        ret.animationShapeKey = animationShapeKey;
        ret.bonesGroupKey = bonesGroupKey;
        ret.actionsKey = actionsKey;
        ret.actionsKeyOrig = actionsKeyOrig;
        if (animationShapeChunkKLE != null) {
            ret.animationShapeChunkKLE = animationShapeChunkKLE.duplicate();
        }
        if (animationShapeChunkInstanceSpecificKLE != null) {
            ret.animationShapeChunkInstanceSpecificKLE = animationShapeChunkInstanceSpecificKLE.duplicate();
        }
        if (bonesGroupChunkKLE != null) {
            ret.bonesGroupChunkKLE = bonesGroupChunkKLE.duplicate();
        }
        if (eventsChunkKLE != null) {
            ret.eventsChunkKLE = eventsChunkKLE.duplicate();
        }
        ret.groupKey = groupKey;
        ret.referencePointGAOKey = referencePointGAOKey;
        if (referencePointGAOChunkKLE != null) {
            ret.referencePointGAOChunkKLE = referencePointGAOChunkKLE.duplicate();
        }
        if (referencePointGAOChunkOrigKLE != null) {
            ret.referencePointGAOChunkOrigKLE = referencePointGAOChunkOrigKLE.duplicate();
        }
        ret.lightKey = lightKey;
        ret.scriptingContainerKey = scriptingContainerKey;
        ret.gaoName = gaoName;
        ret.geoKey = geoKey;
        ret.geoKeyOrig = geoKeyOrig;
        if (geoChunkKLE != null) {
            ret.geoChunkKLE = geoChunkKLE.duplicate();
        }
        if (geoChunkOrigKLE != null) {
            ret.geoChunkOrigKLE = geoChunkOrigKLE.duplicate();
        }
        ret.materialGroupKey = materialGroupKey;
        ret.materialGroupKeyOrig = materialGroupKeyOrig;
        if (materialGroupChunkKLE != null) {
            ret.materialGroupChunkKLE = materialGroupChunkKLE.duplicate();
        }
        if (materialGroupChunkOrigKLE != null) {
            ret.materialGroupChunkOrigKLE = materialGroupChunkOrigKLE.duplicate();
        }
        if (primaryVertexColorArr != null) {
            ret.primaryVertexColorArr = new int[primaryVertexColorArr.length];
            System.arraycopy(primaryVertexColorArr, 0, ret.primaryVertexColorArr, 0, primaryVertexColorArr.length);
        }
        if (primaryVertexColorArrOrig != null) {
            ret.primaryVertexColorArrOrig = new int[primaryVertexColorArrOrig.length];
            System.arraycopy(primaryVertexColorArrOrig, 0, ret.primaryVertexColorArrOrig, 0, primaryVertexColorArrOrig.length);
        }

        if (modifierHandleFlagsArray != null) {
            ret.modifierHandleFlagsArray = new ArrayList<>(modifierHandleFlagsArray);
        }
        if (modifierTypesArray != null) {
            ret.modifierTypesArray = new ArrayList<>(modifierTypesArray);
        }
        if (modifiersArray != null) {
            BFChunkParseInterface cpi, cpiDuplicated;
            ret.modifiersArray = new ArrayList<>();
            for (int i = 0; i < modifiersArray.size(); i++) {
                cpi = modifiersArray.get(i);
                cpiDuplicated = cpi.duplicate();
                if (cpiDuplicated == null) {
                    cpiDuplicated = cpi; // TODO: should be removed, when all modifiers will be implemented 
                }
                ret.modifiersArray.add(cpiDuplicated);
            }
        }
        if (modifiersArrayOrig != null) {
            ret.modifiersArrayOrig = new ArrayList<>(modifiersArrayOrig);
        }
        ret.len = len;
        ret.signature = signature;
        ret.unk1 = unk1;
        ret.unk2 = unk2;
        ret.camDistance = camDistance;
        ret.unk4 = unk4;
        ret.unk5 = unk5;
        ret.processFlags = processFlags;
        ret.processFlagsRuntime = processFlags; // we want to use processFalgs here not processFlagsRuntime
        ret.visualFlags = visualFlags;
        ret.unk7 = unk7;
        if (globalMatrix != null) {
            ret.globalMatrix = new Matrix4x4();
            ret.globalMatrix.set(globalMatrix);
        }
        if (globalMatrixOrig != null) {
            ret.globalMatrixOrig = new Matrix4x4();
            ret.globalMatrixOrig.set(globalMatrixOrig);
        }
        if (globalMatrixSecond != null) {
            ret.globalMatrixSecond = new Matrix4x4();
            ret.globalMatrixSecond.set(globalMatrixSecond);
        }
        if (globalMatrixThird != null) {
            ret.globalMatrixThird = new Matrix4x4();
            ret.globalMatrixThird.set(globalMatrixThird);
        }

        if (boundingBox1 != null) {
            ret.boundingBox1 = new Vector3();
            ret.boundingBox1.set(boundingBox1);
        }
        if (boundingBox2 != null) {
            ret.boundingBox2 = new Vector3();
            ret.boundingBox2.set(boundingBox2);
        }
        if (boundingBox3 != null) {
            ret.boundingBox3 = new Vector3();
            ret.boundingBox3.set(boundingBox3);
        }
        if (boundingBox4 != null) {
            ret.boundingBox4 = new Vector3();
            ret.boundingBox4.set(boundingBox4);
        }

        ret.designFlags1 = designFlags1;
        ret.designFlags1Orig = designFlags1Orig;
        ret.designFlags2 = designFlags2;
        ret.primaryVertexColorNum = primaryVertexColorNum;
        ret.primaryVertexColorNumOrig = primaryVertexColorNumOrig;
        if (localMatrix != null) {
            ret.localMatrix = new Matrix4x4();
            ret.localMatrix.set(localMatrix);
        }
        if (localMatrixOrig != null) {
            ret.localMatrixOrig = new Matrix4x4();
            ret.localMatrixOrig.set(localMatrixOrig);
        }
        if (boneShapes != null) {
            ret.boneShapes = new ArrayList<>();
            BoneShapeElem bse;
            for (int i = 0; i < boneShapes.size(); i++) {
                bse = boneShapes.get(i).duplicate();
                ret.boneShapes.add(bse);
            }
        }
        if (boneShapesOrig != null) {
            ret.boneShapesOrig = new ArrayList<>();
            BoneShapeElem bse;
            for (int i = 0; i < boneShapesOrig.size(); i++) {
                bse = boneShapesOrig.get(i).duplicate();
                ret.boneShapesOrig.add(bse);
            }
        }
        if (boneShapeMatrices != null) {
            ret.boneShapeMatrices = new ArrayList<>();
            Matrix4x4 m;
            for (int i = 0; i < boneShapeMatrices.size(); i++) {
                m = new Matrix4x4();
                m.set(boneShapeMatrices.get(i));
                ret.boneShapeMatrices.add(m);
            }
        }
        if (boneShapeMatricesOrig != null) {
            ret.boneShapeMatricesOrig = new ArrayList<>();
            Matrix4x4 m;
            for (int i = 0; i < boneShapeMatricesOrig.size(); i++) {
                m = new Matrix4x4();
                m.set(boneShapeMatricesOrig.get(i));
                ret.boneShapeMatricesOrig.add(m);
            }
        }
        ret.subGaoOrMtxPartsNum = subGaoOrMtxPartsNum;
        ret.hasMDF = hasMDF;
        ret.unk5f1 = unk5f1;
        ret.renderMinReqDistSq = renderMinReqDistSq;
        ret.capacity = capacity;
        ret.unk5b1 = unk5b1;
        ret.unk5b2 = unk5b2;
        ret.animationControlFlags = animationControlFlags;
        ret.unk5eventskey = unk5eventskey;
        ret.unk5sbkey = unk5sbkey;
        ret.unk5waykey = unk5waykey;
        ret.collisionInstanceKey = collisionInstanceKey;
        ret.collisionMapKey = collisionMapKey;
        ret.u0x40unkint0 = u0x40unkint0;
        ret.u0x40unkint1 = u0x40unkint1;
        ret.u0x40unkint2 = u0x40unkint2;
        ret.u0x40unkint3 = u0x40unkint3;
        ret.u0x40unkf1 = u0x40unkf1;
        ret.u0x40unkf2 = u0x40unkf2;
        ret.u0x40key1 = u0x40key1;
        ret.u0x40key2 = u0x40key2;
        ret.u0x40key3 = u0x40key3;
        ret.u0x40key4 = u0x40key4;
        ret.u0x40unkint1_5 = u0x40unkint1_5;
        ret.u0x40unkint1_6 = u0x40unkint1_6;
        ret.u0x40unkint1_7 = u0x40unkint1_7;
        ret.u0x40unkint1_8 = u0x40unkint1_8;
        ret.u0x40unkint1_9 = u0x40unkint1_9;
        ret.u0x40unkint1_10 = u0x40unkint1_10;
        ret.u0x40unkint1_11 = u0x40unkint1_11;
        if (u0x40pos1 != null) {
            ret.u0x40pos1 = new Vector3();
            ret.u0x40pos1.set(u0x40pos1);
        }
        if (u0x40pos2 != null) {
            ret.u0x40pos2 = new Vector3();
            ret.u0x40pos2.set(u0x40pos2);
        }
        if (actionListChunkKLE != null) {
            ret.actionListChunkKLE = actionListChunkKLE.duplicate();
        }
        if (actionListChunkOrigKLE != null) {
            ret.actionListChunkOrigKLE = actionListChunkOrigKLE.duplicate();
        }
        if (groupChunkKLE != null) {
            ret.groupChunkKLE = groupChunkKLE.duplicate();
        }
        if (scriptingContainerChunkKLE != null) {
            ret.scriptingContainerChunkKLE = scriptingContainerChunkKLE.duplicate();
        }
        if (unk5eventsChunkKLE != null) {
            ret.unk5eventsChunkKLE = unk5eventsChunkKLE.duplicate();
        }
        if (unk5sbChunkKLE != null) {
            ret.unk5sbChunkKLE = unk5sbChunkKLE.duplicate();
        }
        if (unk5wayChunkKLE != null) {
            ret.unk5wayChunkKLE = unk5wayChunkKLE.duplicate();
        }
        if (lightGAOChunkKLE != null) {
            ret.lightGAOChunkKLE = lightGAOChunkKLE.duplicate();
        }
        if (collisionInstanceChunkKLE != null) {
            ret.collisionInstanceChunkKLE = collisionInstanceChunkKLE.duplicate();
        }
        if (collisionMapKLE != null) {
            ret.collisionMapKLE = collisionMapKLE.duplicate();
        }

        return ret;
    }
}
