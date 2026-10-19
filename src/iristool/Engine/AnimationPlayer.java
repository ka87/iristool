/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParsers.Map.BFActionChunk;
import BFDataFileLib.BFChunkParsers.Map.BFActionListChunk;
import BFDataFileLib.BFChunkParsers.Map.BFEventsChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.EventTrackElem;
import BFDataFileLib.EventTrackFrameElem;
import Common.DataType.ActionsElemStruct;
import Common.DataType.ActionsElemTransitionStructInside1;
import Common.DataType.ActionsElemTransitionStructInside2;
import Common.DataType.AnimationPlayerDataContainer;
import Common.DataType.AnimationPlayerTrackData;
import Common.DataType.ArrayListWithSizeOverLoad;
import Common.DataType.EventTrackOption;
import Common.DataType.Quaternion;
import Common.DataType.EventsSearchLabelStruct;
import Common.DataType.EventsTrackTimeStruct;
import Common.DataType.Matrix4x4;
import Common.DataType.PlayTrackReturnTimeAndShouldCallChangeAction;
import Common.DataType.Vector3;
import Common.IrisToolDefines;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author andra
 */
public class AnimationPlayer {

    public final static int POSITIONUPDATE_ALL = 0;
    public final static int POSITIONUPDATE_NO_GLOBAL = 1;
    public final static int POSITIONUPDATE_NONE = 2;

    private BFGaoChunk _event_OwnerGAO;
    private BFGaoChunk _event_CurrentGAO;
    private BFEventsChunk _event_CurrentTrackList;
    private EventTrackElem _event_CurrentTrack;
    private EventTrackFrameElem _event_CurrentTrackFrameElem;
    private EventTrackOption _event_CurrentTrackOption;
    private Matrix4x4 _event_CurrentRefMatrix;
    private Boolean _eventsRunning = false;
    private Boolean _eventsisBeingUpdated = false;
    private Boolean _eventsNeedToReparse = false;
    private Boolean _eventsReferenceMatrixSetEnable = true;
    private AnimationPlayerTrackData _eventsCurrentData;
    private float _timeDiffBetweenLoops = 0.0f;
    private float _event_elapsedTime = 0.0f;
    private float _event_elapsedTimeInside = 0.0f;
    private Vector3 _event_positionBeforeAnimPlayer = new Vector3(0.0f, 0.0f, 0.0f);
    private boolean animDebugEnabled = false;
    private int animGaoPositionUpdateMode = AnimationPlayer.POSITIONUPDATE_NO_GLOBAL;

    public int getAnimGaoPositionUpdateMode() {
        return animGaoPositionUpdateMode;
    }

    public void setAnimGaoPositionUpdateMode(int animGaoPositionUpdateMode) {
        if (this.animGaoPositionUpdateMode > AnimationPlayer.POSITIONUPDATE_NONE || this.animGaoPositionUpdateMode < AnimationPlayer.POSITIONUPDATE_ALL) {
            return;
        }
        this.animGaoPositionUpdateMode = animGaoPositionUpdateMode;
    }

    public void printAnimationDebugMessage(Object o) {
        if (!isAnimDebugEnabled()) {
            return;
        }

        String className = getClass().getName();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName;

        methodName = stackTraceElements[2].getMethodName();

        String logText = (className + "::" + methodName + "(): ") + o + "\n";

        System.out.print(logText);
        logText = null;
    }

    public boolean isAnimDebugEnabled() {
        return animDebugEnabled;
    }

    public void setAnimDebugEnabled(boolean animDebugEnabled) {
        this.animDebugEnabled = animDebugEnabled;
    }

    public BFGaoChunk getEvent_CurrentGAO() {
        return _event_CurrentGAO;
    }

    public void setEvent_CurrentGAO(BFGaoChunk _event_CurrentGAO) {
        this._event_CurrentGAO = _event_CurrentGAO;
    }

    public EventTrackElem getEvent_CurrentTrack() {
        return _event_CurrentTrack;
    }

    public void setEvent_CurrentTrack(EventTrackElem _event_CurrentTrack) {
        this._event_CurrentTrack = _event_CurrentTrack;
    }

    public EventTrackOption getEvent_CurrentTrackOption() {
        return _event_CurrentTrackOption;
    }

    public void setEvent_CurrentTrackOption(EventTrackOption _event_CurrentTrackOption) {
        this._event_CurrentTrackOption = _event_CurrentTrackOption;
    }

    public Boolean getEventsRunning() {
        return _eventsRunning;
    }

    public void setEventsRunning(Boolean _eventsRunning) {
        this._eventsRunning = _eventsRunning;
    }

    public Matrix4x4 getEvent_CurrentRefMatrix() {
        return _event_CurrentRefMatrix;
    }

    public void setEvent_CurrentRefMatrix(Matrix4x4 _event_CurrentRefMatrix) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("ref matrix set");
        }
        this._event_CurrentRefMatrix = _event_CurrentRefMatrix;
    }

    public BFGaoChunk getEvent_OwnerGAO() {
        return _event_OwnerGAO;
    }

    public void setEvent_OwnerGAO(BFGaoChunk _event_OwnerGAO) {
        this._event_OwnerGAO = _event_OwnerGAO;
    }

    public BFEventsChunk getEvent_CurrentTrackList() {
        return _event_CurrentTrackList;
    }

    public void setEvent_CurrentTrackList(BFEventsChunk trackList) {
        this._event_CurrentTrackList = trackList;
    }

    public Boolean getEventsReferenceMatrixSetEnable() {
        return _eventsReferenceMatrixSetEnable;
    }

    public void setEventsReferenceMatrixSetEnable(Boolean _eventsReferenceMatrixSetEnable) {
        this._eventsReferenceMatrixSetEnable = _eventsReferenceMatrixSetEnable;
    }

    public float getTimeDiffBetweenLoops() {
        return _timeDiffBetweenLoops;
    }

    public void setTimeDiffBetweenLoops(float _timeDiffBetweenLoops) {
        this._timeDiffBetweenLoops = _timeDiffBetweenLoops;
    }

    public Boolean getEventsisBeingUpdated() {
        return _eventsisBeingUpdated;
    }

    public void setEventsisBeingUpdated(Boolean _eventsisBeingUpdated) {
        this._eventsisBeingUpdated = _eventsisBeingUpdated;
    }

    public EventTrackFrameElem getEvent_CurrentTrackFrameElem() {
        return _event_CurrentTrackFrameElem;
    }

    public void setEvent_CurrentTrackFrameElem(EventTrackFrameElem _event_CurrentTrackFrameElem) {
        this._event_CurrentTrackFrameElem = _event_CurrentTrackFrameElem;
    }

    public float getEvent_elapsedTime() {
        return _event_elapsedTime;
    }

    public void setEvent_elapsedTime(float _event_elapsedTime) {
        this._event_elapsedTime = _event_elapsedTime;
    }

    public float getEvent_elapsedTimeInside() {
        return _event_elapsedTimeInside;
    }

    public void setEvent_elapsedTimeInside(float _event_elapsedTimeInside) {
        this._event_elapsedTimeInside = _event_elapsedTimeInside;
    }

    public Boolean getEventsNeedToReparse() {
        return _eventsNeedToReparse;
    }

    public void setEventsNeedToReparse(Boolean _eventsNeedToReparse) {
        this._eventsNeedToReparse = _eventsNeedToReparse;
    }

    public AnimationPlayerTrackData getEventsCurrentData() {
        return _eventsCurrentData;
    }

    public void setEventsCurrentData(AnimationPlayerTrackData _eventsCurrentData) {
        this._eventsCurrentData = _eventsCurrentData;
    }

    public Vector3 getEvent_positionBeforeAnimPlayer() {
        return _event_positionBeforeAnimPlayer;
    }

    public void setEvent_positionBeforeAnimPlayer(Vector3 _event_positionBeforeAnimPlayer) {
        this._event_positionBeforeAnimPlayer = _event_positionBeforeAnimPlayer;
    }

    private boolean isTrackUsesFlash(EventTrackElem currTrackElem, BFGaoChunk currentGao, BFGaoChunk ownerGao, BFEventsChunk currentTrackList) {
        BFEventsChunk currTrackList;
        BFGaoChunk oGao;
        BFGaoChunk currGao;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (currentGao != null) {
            currGao = currentGao;
        } else {
            currGao = getEvent_CurrentGAO();
        }
        if (ownerGao != null) {
            oGao = ownerGao;
        } else {
            oGao = getEvent_OwnerGAO();
        }
        if (currentTrackList != null) {
            currTrackList = currentTrackList;
        } else {
            currTrackList = getEvent_CurrentTrackList();
        }
        EventTrackElem ete;
        for (int i = 0; i < currTrackList.getEventTracks().size(); i++) {
            ete = currTrackList.getEventTracks().get(i);
            if (ete == currTrackElem) {
                break;
            }
            if ((((ete.getInnerGaoKLE() != null && ete.getInnerGaoKLE().getParseHandler() != null) || currGao == oGao))
                    && ((ete.getInnerGaoKLE() == null || ete.getInnerGaoKLE().getParseHandler() == null)
                    || (ete.getInnerGaoKLE() != null && ete.getInnerGaoKLE().getParseHandler() != null && ete.getInnerGaoKLE().getParseHandler() == currGao))
                    && ete.getEventTrackFrameElems().size() > 0 && (ete.getEventTrackFrameElems().get(0).getTrackFrameFlags() & 0x4) != 0) {
                return true;
            }
        }
        return false;
    }

    private void interpolationKeyPlayStandardHandlePosition(EventTrackFrameElem etfe) {
        ArrayList<TreeMap<Integer, BFKeyListElem>> currentGaoGroup;
        Matrix4x4 currentGaoMultipliedWithRefMatrix = new Matrix4x4();
        Matrix4x4 currentGaoInvMatrix = new Matrix4x4();
        Vector3 nextVecMultipliedWithFactor = new Vector3();
        Vector3 currVecMultipliedWithInvFactor = new Vector3();
        float invFactor;
        float factor;
        Vector3 nextVec;
        Vector3 currVec;
        boolean isReferenceEqualsCurrentGao;
        BFGaoChunk referenceGao;
        Matrix4x4 currentMatrix;
        BFGaoChunk currentBoneGao;
        boolean hasBoneID;
        float elapsedTime;
        EventTrackFrameElem nextElem;
        int nextElemIdx;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        nextElemIdx = getEvent_CurrentTrackOption().getActElemNum() + 1;
        if (nextElemIdx == getEvent_CurrentTrack().getTrackFrameNum()) {
            nextElemIdx = 0;
        }
        nextElem = getEvent_CurrentTrack().getEventTrackFrameElems().get(nextElemIdx);
        elapsedTime = getEvent_CurrentTrackOption().getActElemTimeElapsed();
        hasBoneID = !getEvent_CurrentTrack().getBoneId().equals((short) -1);
        if (hasBoneID) {
            currentGaoGroup = RenderUtils.getGRPLForGao(getEvent_CurrentGAO());
            if (getEvent_CurrentTrack().getBoneId() >= currentGaoGroup.size()) {
                return;
            }
            currentBoneGao = (BFGaoChunk) (((BFKeyListElem) (currentGaoGroup.get(getEvent_CurrentTrack().getBoneId()).firstEntry().getValue())).getParseHandler());
            if ((currentBoneGao.getVisualFlags() & 0x80000000) != 0) {
                return;
            }
            if ((getEvent_CurrentGAO().getAnimationPlayerUnknownComputedFlags() & 0x8) != 0 && (currentBoneGao.getAnimationControlFlags() & 0x20) != 0) {
                return;
            }
            if (currentBoneGao.getAnimationControlFlags() != null && (currentBoneGao.getAnimationControlFlags() & 0x200) != 0) {
                return;
            }
        } else {
            currentBoneGao = getEvent_CurrentGAO();
        }
        if ((currentBoneGao.getFlags() & 0x400000) != 0) {
            currentMatrix = currentBoneGao.getLocalMatrix();
            if (currentBoneGao != null) {
                referenceGao = (BFGaoChunk) (currentBoneGao.getReferencePointGAOChunkKLE().getParseHandler());
            } else {
                referenceGao = null;
            }
            isReferenceEqualsCurrentGao = referenceGao == getEvent_CurrentGAO();
        } else {
            currentMatrix = currentBoneGao.getGlobalMatrix();
            isReferenceEqualsCurrentGao = false;
        }
        currVec = etfe.getPart3case1vec();
        if (etfe.getTrackFrameLen() > 0) {
            nextVec = nextElem.getPart3case1vec();
            factor = elapsedTime / (etfe.getTrackFrameLen() * 1.0f / 60.0f);
            invFactor = 1.0f - factor;
            currVecMultipliedWithInvFactor.setX(currVec.getX() * invFactor);
            currVecMultipliedWithInvFactor.setY(currVec.getY() * invFactor);
            currVecMultipliedWithInvFactor.setZ(currVec.getZ() * invFactor);
            nextVecMultipliedWithFactor.setX(nextVec.getX() * factor);
            nextVecMultipliedWithFactor.setY(nextVec.getY() * factor);
            nextVecMultipliedWithFactor.setZ(nextVec.getZ() * factor);
            Vector3.sumTwoVector(currVecMultipliedWithInvFactor, currVecMultipliedWithInvFactor, nextVecMultipliedWithFactor);
        } else {
            currVecMultipliedWithInvFactor.setX(currVec.getX());
            currVecMultipliedWithInvFactor.setY(currVec.getY());
            currVecMultipliedWithInvFactor.setZ(currVec.getZ());
        }
        if (hasBoneID) {
            if (isReferenceEqualsCurrentGao && getEvent_CurrentRefMatrix() != null) {
                if ((getEvent_CurrentGAO().getFlags() & 0x400000) != 0) {
                    Matrix4x4.invertMatrix(currentGaoInvMatrix, getEvent_CurrentGAO().getLocalMatrix());
                } else {
                    Matrix4x4.invertMatrix(currentGaoInvMatrix, getEvent_CurrentGAO().getGlobalMatrix());
                }
                Matrix4x4.matrixMultiplyWithNormalized(currentGaoMultipliedWithRefMatrix, getEvent_CurrentRefMatrix(), currentGaoInvMatrix);
                Vector3.transformVertex(currVecMultipliedWithInvFactor, currentGaoMultipliedWithRefMatrix, currVecMultipliedWithInvFactor);
                //  TODO: unknown add if (getEvent_CurrentGAO().getFlags() & 0x2) != 0)
            }
            currentMatrix.getPos().setX(currVecMultipliedWithInvFactor.getX());
            currentMatrix.getPos().setY(currVecMultipliedWithInvFactor.getY());
            currentMatrix.getPos().setZ(currVecMultipliedWithInvFactor.getZ());
            currentMatrix.setFlags(currentMatrix.getFlags() | 0x2);
        } else {
            Vector3.transformVertex(currVecMultipliedWithInvFactor, getEvent_CurrentRefMatrix(), currVecMultipliedWithInvFactor);

            currentMatrix.getPos().setX(currVecMultipliedWithInvFactor.getX());
            currentMatrix.getPos().setY(currVecMultipliedWithInvFactor.getY());
            currentMatrix.getPos().setZ(currVecMultipliedWithInvFactor.getZ());
            currentMatrix.setFlags(currentMatrix.getFlags() | 0x2);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("pos:");
            printAnimationDebugMessage(currentMatrix);
        }
    }

    private void interpolationKeyPlayStandardHandleTransformation(ArrayList<EventTrackFrameElem> etfea, int currElemIdx) {
        ArrayList<TreeMap<Integer, BFKeyListElem>> currentGaoGroup;
        Matrix4x4 currentInvMatrixMultipliedWithRefMatrix = new Matrix4x4();
        Matrix4x4 currentInvMatrix = new Matrix4x4();
        Matrix4x4 tmpDestMtx = new Matrix4x4();
        Matrix4x4 interpolatedMatrix;
        boolean isReferenceEqualsCurrentGao;
        BFGaoChunk referenceGao;
        Matrix4x4 currentMatrix;
        BFGaoChunk currentBoneGao;
        boolean hasBoneId;
        float elapsedTime;
        int nextElemIdx;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        nextElemIdx = getEvent_CurrentTrackOption().getActElemNum() + 1;
        if (nextElemIdx == getEvent_CurrentTrack().getTrackFrameNum()) {
            nextElemIdx = 0;
        }
        elapsedTime = getEvent_CurrentTrackOption().getActElemTimeElapsed();
        hasBoneId = !getEvent_CurrentTrack().getBoneId().equals((short) -1);
        if (hasBoneId) {
            currentGaoGroup = RenderUtils.getGRPLForGao(getEvent_CurrentGAO());
            if (getEvent_CurrentTrack().getBoneId() >= currentGaoGroup.size()) {
                return;
            }
            currentBoneGao = (BFGaoChunk) (((BFKeyListElem) (currentGaoGroup.get(getEvent_CurrentTrack().getBoneId()).firstEntry().getValue())).getParseHandler());
            if ((currentBoneGao.getVisualFlags() & 0x80000000) != 0) {
                return;
            }
            if ((getEvent_CurrentGAO().getAnimationPlayerUnknownComputedFlags() & 0x8) != 0 && (currentBoneGao.getAnimationControlFlags() & 0x20) != 0) {
                return;
            }
            if (currentBoneGao.getAnimationControlFlags() != null && (currentBoneGao.getAnimationControlFlags() & 0x400) != 0) {
                return;
            }
        } else {
            currentBoneGao = getEvent_CurrentGAO();
        }
        if ((currentBoneGao.getFlags() & 0x400000) != 0) {
            currentMatrix = currentBoneGao.getLocalMatrix();
            if (currentBoneGao.getLocalMatrix() != null) {
                referenceGao = (BFGaoChunk) (currentBoneGao.getReferencePointGAOChunkKLE().getParseHandler());
            } else {
                referenceGao = null;
            }
            isReferenceEqualsCurrentGao = referenceGao.equals(getEvent_CurrentGAO());
        } else {
            currentMatrix = currentBoneGao.getGlobalMatrix();
            isReferenceEqualsCurrentGao = false;
        }

        interpolatedMatrix = interpolationKeyPlayComputeInterpolatedMatrixStandard(etfea, currElemIdx, nextElemIdx, elapsedTime);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("interpoled mtx:");
            printAnimationDebugMessage(interpolatedMatrix);
        }
        if (hasBoneId) {
            if (isReferenceEqualsCurrentGao && getEvent_CurrentRefMatrix() != null) {
                if ((getEvent_CurrentGAO().getFlags() & 0x400000) != 0) {
                    Matrix4x4.invertMatrix(currentInvMatrix, getEvent_CurrentGAO().getLocalMatrix());
                } else {
                    Matrix4x4.invertMatrix(currentInvMatrix, getEvent_CurrentGAO().getGlobalMatrix());
                }
                Matrix4x4.matrixMultiplyWithNormalized(currentInvMatrixMultipliedWithRefMatrix, getEvent_CurrentRefMatrix(), currentInvMatrix);
                Matrix4x4.mult33Matrix(tmpDestMtx, interpolatedMatrix, currentInvMatrixMultipliedWithRefMatrix, true);
            } else {
                tmpDestMtx.set33(interpolatedMatrix);
            }
            currentMatrix.set33(tmpDestMtx);
        } else {
            Matrix4x4.mult33Matrix(tmpDestMtx, interpolatedMatrix, getEvent_CurrentRefMatrix(), true);
            currentMatrix.set33(tmpDestMtx);
        }
        currentMatrix.setFlags(currentMatrix.getFlags() | 0x4);
    }

    private void interpolationKeyPlayCopyMatrix(Matrix4x4 destMtx, ArrayList<EventTrackFrameElem> etfea, int index, boolean useNextElem) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        Quaternion tmpQuat = new Quaternion();
        int flags = (etfea.get(index).getTrackFrameTypeFlags() & 0x14);

        if (flags != 0) {
            if ((flags & 0x4) != 0) {
                if (useNextElem) {
                    destMtx.set(etfea.get(index + 1).getPart3case0matrix());
                } else {
                    destMtx.set(etfea.get(index).getPart3case0matrix());
                }
                if ((etfea.get(index).getTrackFrameFlags() & 0x20) != 0) {
                    Matrix4x4.convertMatrixToQuaternion(tmpQuat, destMtx);
                    tmpQuat.setF2(-tmpQuat.getF2());
                    tmpQuat.setF3(-tmpQuat.getF3());
                    Quaternion.convertQuaternionToMatrix(destMtx, tmpQuat);
                }
            } else if ((flags & 0x10) != 0) {
                if (useNextElem) {
                    tmpQuat = (etfea.get(index + 1).getPart3case0quaternion() == null
                            ? etfea.get(index + 1).getUncompressedQuaternion() : etfea.get(index + 1).getPart3case0quaternion());
                } else {
                    tmpQuat = (etfea.get(index).getPart3case0quaternion() == null
                            ? etfea.get(index).getUncompressedQuaternion() : etfea.get(index).getPart3case0quaternion());
                }
                if ((etfea.get(index).getTrackFrameFlags() & 0x20) != 0) {
                    tmpQuat.setF2(-tmpQuat.getF2());
                    tmpQuat.setF3(-tmpQuat.getF3());
                }
                Quaternion.convertQuaternionToMatrix(destMtx, tmpQuat);
                if ((etfea.get(index).getTrackFrameFlags() & 0x20) != 0) {
                    tmpQuat.setF2(-tmpQuat.getF2());
                    tmpQuat.setF3(-tmpQuat.getF3());
                }
                Matrix4x4.fixMatrix(destMtx);
            }
        }
    }

    private Matrix4x4 interpolationKeyPlayComputeInterpolatedMatrixStandard(ArrayList<EventTrackFrameElem> etfea, int currElemIdx, int nextElemIdx, float trackFrameElemElapsedTime) {
        Matrix4x4 result;
        Quaternion q1;
        Quaternion q2;
        float interpFactor;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        EventTrackFrameElem currElem = etfea.get(currElemIdx);
        EventTrackFrameElem nextElem = etfea.get(nextElemIdx);

        if (currElem.getTrackFrameLen() > 0) {
            if (currElem.getTrackFrameLen() * 1.0f / 60.0f > trackFrameElemElapsedTime) {
                q1 = currElem.getPart3case0quaternion() == null ? currElem.getUncompressedQuaternion() : currElem.getPart3case0quaternion();
                q2 = nextElem.getPart3case0quaternion() == null ? nextElem.getUncompressedQuaternion() : nextElem.getPart3case0quaternion();
                interpFactor = trackFrameElemElapsedTime / (currElem.getTrackFrameLen() * 1.0f / 60.0f);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    printAnimationDebugMessage("BLEND (" + currElemIdx + ", " + nextElemIdx + ")");
                    printAnimationDebugMessage(q1);
                    printAnimationDebugMessage(q2);
                    printAnimationDebugMessage("Factor: " + interpFactor);
                }
                result = Quaternion.quaternionBlendAndConvertToMatrix(null, q1, q2, interpFactor, true);
            } else {

                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    printAnimationDebugMessage("NORMAL");
                }

                result = Quaternion.convertQuaternionToMatrix(null, (nextElem.getPart3case0quaternion() == null ? nextElem.getUncompressedQuaternion() : nextElem.getPart3case0quaternion()));
                result.setFlags(result.getFlags() | 0x4);
            }
        } else {
            result = Quaternion.convertQuaternionToMatrix(null, (currElem.getPart3case0quaternion() == null ? currElem.getUncompressedQuaternion() : currElem.getPart3case0quaternion()));
            result.setFlags(result.getFlags() | 0x4);
        }
        return result;
    }

    private void interpolationKeyPlayComputeInterpolatedMatrixAdvanced(Matrix4x4 destMtx, ArrayList<EventTrackFrameElem> etfea, int currElemIdx, int nextElemIdx,
            float trackFrameElemElapsedTime) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        short currElemType = etfea.get(currElemIdx).getTrackFrameTypeFlags();
        short nextElemType = etfea.get(nextElemIdx).getTrackFrameTypeFlags();
        float blendValue;
        Quaternion tmpQuat = new Quaternion();
        Quaternion firstQuat = new Quaternion();
        Quaternion secondQuat = new Quaternion();

        if (currElemType != -1) {
            if ((currElemType & 0x14) != 0) {
                if (etfea.get(currElemIdx).getTrackFrameLen() != 0
                        && nextElemType != -1
                        && ((nextElemType & 0x14) != 0)
                        && (etfea.get(currElemIdx).getTrackFrameFlags() & 0x800) == 0) {
                    if (etfea.get(currElemIdx).getTrackFrameLen() * 1.0f / 60.0f > trackFrameElemElapsedTime) {
                        if ((currElemType & 0x4) != 0) {
                            Matrix4x4.convertMatrixToQuaternion(firstQuat, etfea.get(currElemIdx).getPart3case0matrix());
                        } else {
                            firstQuat = (etfea.get(currElemIdx).getPart3case0quaternion() == null
                                    ? etfea.get(currElemIdx).getUncompressedQuaternion() : etfea.get(currElemIdx).getPart3case0quaternion());
                        }
                        if ((etfea.get(currElemIdx).getTrackFrameFlags() & 0x20) != 0) {
                            tmpQuat.setF1(firstQuat.getF1());
                            tmpQuat.setF2(-firstQuat.getF2());
                            tmpQuat.setF3(-firstQuat.getF3());
                            tmpQuat.setF4(firstQuat.getF4());
                            firstQuat = tmpQuat;
                        }
                        if ((currElemType & 0x40) != 0) {
                            if ((currElemType & 0x4) != 0) {
                                Matrix4x4.convertMatrixToQuaternion(secondQuat, etfea.get(currElemIdx + 1).getPart3case0matrix());
                            } else {
                                secondQuat = (etfea.get(currElemIdx + 1).getPart3case0matrix() == null
                                        ? etfea.get(currElemIdx + 1).getUncompressedQuaternion() : etfea.get(currElemIdx + 1).getPart3case0quaternion());
                            }
                        } else if ((nextElemType & 0x4) != 0) {
                            Matrix4x4.convertMatrixToQuaternion(secondQuat, etfea.get(nextElemIdx).getPart3case0matrix());
                        } else {
                            secondQuat = (etfea.get(nextElemIdx).getPart3case0quaternion() == null
                                    ? etfea.get(nextElemIdx).getUncompressedQuaternion() : etfea.get(nextElemIdx).getPart3case0quaternion());
                        }
                        if ((etfea.get(nextElemIdx).getTrackFrameFlags() & 0x20) != 0) {
                            tmpQuat.setF1(secondQuat.getF1());
                            tmpQuat.setF2(-secondQuat.getF2());
                            tmpQuat.setF3(-secondQuat.getF3());
                            tmpQuat.setF4(secondQuat.getF4());
                            secondQuat = tmpQuat;
                        }
                        blendValue = etfea.get(currElemIdx).getTrackFrameLen() / (etfea.get(currElemIdx).getTrackFrameLen() * 1.0f / 60.0f);
                        Quaternion.quaternionBlendAndConvertToMatrix(destMtx, firstQuat, secondQuat, blendValue, true);
                    } else if ((currElemType & 0x40) != 0) {
                        interpolationKeyPlayCopyMatrix(destMtx, etfea, currElemIdx, true);
                    } else {
                        interpolationKeyPlayCopyMatrix(destMtx, etfea, nextElemIdx, false);
                    }
                } else {
                    interpolationKeyPlayCopyMatrix(destMtx, etfea, currElemIdx, false);
                }
            }
        }
    }

    private float interpolationKeyInterpolateTimeCustomTimeFactors(EventTrackFrameElem etfe, float time) {
        float timeSum;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        ArrayList<Integer> elems = etfe.getTimedEventFrameElems();
        int i = 0;
        timeSum = 0.0f;
        while (i < (elems.size() - 1) && elems.get(i + 2) <= time) {
            timeSum = (Float.intBitsToFloat(elems.get(i + 2)) - Float.intBitsToFloat(elems.get(i))) * Float.intBitsToFloat(elems.get(i + 1)) + timeSum;// +2 = next time, +1 = factor, +0 = time
            i += 2;
        }
        timeSum = (time - Float.intBitsToFloat(elems.get(i))) * Float.intBitsToFloat(elems.get(i + 1)) + timeSum;

        return timeSum;
    }

    private void interpolationKeyPlayAdvanced(ArrayList<EventTrackFrameElem> etfea, int currElemIdx) {
        Matrix4x4 currGaoMtx;
        Matrix4x4 refMtx;
        Matrix4x4 tmpRefMtx2 = new Matrix4x4();
        Matrix4x4 refMtxInv = new Matrix4x4();
        Matrix4x4 mtxCalculated = new Matrix4x4();
        Matrix4x4 interpolatedMtxRet = new Matrix4x4();
        Matrix4x4 multWithRefMtx = new Matrix4x4();
        Matrix4x4 currGaoMtxInv = new Matrix4x4();
        float ratio;
        Vector3 vecMultRatiox1_1 = new Vector3();
        float ratiox1_1;
        Vector3 vecMultRatiox1_2 = new Vector3();
        float ratiox1_2;
        float ratiox2;
        Vector3 vecMultRatiox1_1_1 = new Vector3();
        float ratiox_1_1_1;
        Vector3 vecMultRatiox3_1 = new Vector3();
        float ratiox3_1;
        Vector3 vcMultRatiox2_1 = new Vector3();
        Vector3 vecMultRatio = new Vector3();
        float ratiox2_1;
        float ratiox3;
        float trackElemElapsedTimeFullTimeRatio;
        Vector3 positionCalculated = new Vector3();
        Vector3 positionVecNext;
        Vector3 tmpPositionVecNext = new Vector3();
        Vector3 positionVec = new Vector3();
        BFGaoChunk refGao;
        float actualElemElapsedTime;
        short nextTrackFrameElemTypeFlag;
        EventTrackFrameElem nextTrackFrameElem;
        int nextElemIndex;
        boolean isCurrentRefGaoEqCurrentGao;
        BFGaoChunk currentReferencePointGao;
        Matrix4x4 destCurrentMatrix;
        boolean shouldSetReferenceToCurrGao;
        boolean isGaoHasReferencePoint;
        BFGaoChunk currentBoneGao;
        boolean hasTrackBoneIdSet;
        short currTrackElemTrackFrameTypeFlags;
        ArrayList<TreeMap<Integer, BFKeyListElem>> grpl;
        EventTrackFrameElem currTrackElem = getEvent_CurrentTrack().getEventTrackFrameElems().get(currElemIdx);

        currTrackElemTrackFrameTypeFlags = currTrackElem.getTrackFrameTypeFlags();
        if (currTrackElemTrackFrameTypeFlags == -1) {
            return;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        hasTrackBoneIdSet = !getEvent_CurrentTrack().getBoneId().equals((short) -1);
        if (hasTrackBoneIdSet) {
            if ((grpl = RenderUtils.getGRPLForGao(getEvent_CurrentGAO())) == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    printAnimationDebugMessage("No bone gao group found.");
                }
                return;
            }
            if (getEvent_CurrentTrack().getBoneId() >= grpl.size()) {
                return;
            }
            currentBoneGao = (BFGaoChunk) (((BFKeyListElem) (grpl.get(getEvent_CurrentTrack().getBoneId()).firstEntry().getValue())).getParseHandler());
            if (!getEventsRunning() && (currentBoneGao.getVisualFlags() & 0x80000000) != 0) {
                return;
            }
        } else {
            currentBoneGao = getEvent_CurrentGAO();
        }
        isGaoHasReferencePoint = (currentBoneGao.getFlags() & 0x400000) != 0;
        shouldSetReferenceToCurrGao = (currTrackElemTrackFrameTypeFlags & 0x20) != 0;
        if (isGaoHasReferencePoint) {
            destCurrentMatrix = currentBoneGao.getLocalMatrix();
            if (currentBoneGao != null && destCurrentMatrix != null) {
                currentReferencePointGao = (BFGaoChunk) (currentBoneGao.getReferencePointGAOChunkKLE().getParseHandler());
            } else {
                currentReferencePointGao = null;
            }
            isCurrentRefGaoEqCurrentGao = getEvent_CurrentGAO() == currentReferencePointGao;
        } else {
            destCurrentMatrix = currentBoneGao.getGlobalMatrix();
            isCurrentRefGaoEqCurrentGao = false;
        }
        if (destCurrentMatrix == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                printAnimationDebugMessage("destCurrentMatrix is null.");
            }
            return;
        }
        nextElemIndex = getEvent_CurrentTrackOption().getActElemNum() + 1;
        if (nextElemIndex == getEvent_CurrentTrack().getTrackFrameNum()) {
            nextElemIndex = 0;
        }
        nextTrackFrameElem = getEvent_CurrentTrack().getEventTrackFrameElems().get(nextElemIndex);
        if ((nextTrackFrameElem.getTrackFrameFlags() & 0x7C0) != 0) {
            if (((nextTrackFrameElem.getTrackFrameFlags()) & 0x7C0) != 0x80) {
                return;
            }
            nextTrackFrameElemTypeFlag = nextTrackFrameElem.getTrackFrameTypeFlags();
            if (nextTrackFrameElemTypeFlag == -1) {
                return;
            }
        } else {
            nextTrackFrameElemTypeFlag = 0;
        }
        if ((currTrackElemTrackFrameTypeFlags & 0x8) != 0) {
            actualElemElapsedTime = interpolationKeyInterpolateTimeCustomTimeFactors(currTrackElem, getEvent_CurrentTrackOption().getActElemTimeElapsed());
        } else {
            actualElemElapsedTime = getEvent_CurrentTrackOption().getActElemTimeElapsed();
        }
        if (hasTrackBoneIdSet
                && isGaoHasReferencePoint
                && (shouldSetReferenceToCurrGao && ((BFGaoChunk) (currentBoneGao.getReferencePointGAOChunkKLE().getParseHandler())) != getEvent_CurrentGAO())) /*|| !shouldSetReferenceToCurrGao && refgao != refgao2 */ {
            if (shouldSetReferenceToCurrGao) {
                refGao = getEvent_CurrentGAO();
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    printAnimationDebugMessage("TODO: unsupported: refgao2 needed to set refgao1"); // TODO: can't go here, se commented out part of line in if
                }
                refGao = null; // TODO: refgao2
            }
            if (refGao != null) {
                BFKeyListElem kle = new BFKeyListElem(refGao.getSelfKey(), BFGaoChunk.class, false);
                kle.setParseHandler(refGao);
                currentBoneGao.setReferencePointGAOChunkKLE(kle);
                currentBoneGao.setReferencePointGAOKey(refGao.getSelfKey());
            }
            RenderUtils.computeObjectGlobalMatrixWithReferencesGlobalMatrix(currentBoneGao);
        }
        if ((currTrackElemTrackFrameTypeFlags & 3) != 0) {
            positionVec.setX(currTrackElem.getPart3case1vec().getX());
            positionVec.setY(currTrackElem.getPart3case1vec().getY());
            positionVec.setZ(currTrackElem.getPart3case1vec().getZ());
            if ((getEvent_CurrentTrack().getEventTrackFrameElems().get(currElemIdx).getTrackFrameFlags() & 0x20) != 0) {
                positionVec.setX(-currTrackElem.getPart3case1vec().getX());
                positionVec.setY(currTrackElem.getPart3case1vec().getY());
                positionVec.setZ(currTrackElem.getPart3case1vec().getZ());
            }
            if ((nextTrackFrameElemTypeFlag & 3) != 0 && currTrackElem.getTrackFrameLen() != 0 && !((currTrackElem.getTrackFrameFlags() & 0x800) != 0)) {
                if ((currTrackElemTrackFrameTypeFlags & 0x40) != 0) {
                    positionVecNext = getEvent_CurrentTrack().getEventTrackFrameElems().get(currElemIdx + 1).getPart3case1vec(); // TODO: itt a case2 és a case3 vec is kellene
                } else {
                    positionVecNext = nextTrackFrameElem.getPart3case1vec();
                }
                if ((nextTrackFrameElem.getTrackFrameFlags() & 0x20) != 0) {
                    tmpPositionVecNext.setX(-positionVecNext.getX());
                    tmpPositionVecNext.setY(positionVecNext.getY());
                    tmpPositionVecNext.setZ(positionVecNext.getZ());
                    positionVecNext = tmpPositionVecNext;
                }
                if (currTrackElem.getTrackFrameLen() * 1.0f / 60.0f > actualElemElapsedTime) {
                    trackElemElapsedTimeFullTimeRatio = actualElemElapsedTime / (currTrackElem.getTrackFrameLen() * 1.0f / 60.0f);
                    switch (currTrackElemTrackFrameTypeFlags & 0x3) {
                        case 0x3:
                            ratiox3 = (1.0f - trackElemElapsedTimeFullTimeRatio) * ((1.0f - trackElemElapsedTimeFullTimeRatio) * (1.0f - trackElemElapsedTimeFullTimeRatio));
                            positionCalculated.setX(positionVec.getX() * ratiox3);
                            positionCalculated.setY(positionVec.getY() * ratiox3);
                            positionCalculated.setZ(positionVec.getZ() * ratiox3);
                            ratiox2_1 = (1.0f - trackElemElapsedTimeFullTimeRatio) * ((1.0f - trackElemElapsedTimeFullTimeRatio) * (3.0f * trackElemElapsedTimeFullTimeRatio));
                            vcMultRatiox2_1.setX(currTrackElem.getPart3case2vec().getX() * ratiox2_1);
                            vcMultRatiox2_1.setY(currTrackElem.getPart3case2vec().getY() * ratiox2_1);
                            vcMultRatiox2_1.setZ(currTrackElem.getPart3case2vec().getZ() * ratiox2_1);
                            Vector3.sumTwoVector(positionCalculated, positionCalculated, vcMultRatiox2_1);
                            ratiox3_1 = (1.0f - trackElemElapsedTimeFullTimeRatio) * (3.0f * trackElemElapsedTimeFullTimeRatio * trackElemElapsedTimeFullTimeRatio);
                            vecMultRatiox3_1.setX(ratiox3_1 * currTrackElem.getPart3case3vec().getX());
                            vecMultRatiox3_1.setY(currTrackElem.getPart3case3vec().getY() * ratiox3_1);
                            vecMultRatiox3_1.setZ(currTrackElem.getPart3case3vec().getZ() * ratiox3_1);
                            Vector3.sumTwoVector(positionCalculated, positionCalculated, vecMultRatiox3_1);
                            ratiox_1_1_1 = trackElemElapsedTimeFullTimeRatio * trackElemElapsedTimeFullTimeRatio * trackElemElapsedTimeFullTimeRatio;
                            vecMultRatiox1_1_1.setX(positionVecNext.getX() * ratiox_1_1_1);
                            vecMultRatiox1_1_1.setY(positionVecNext.getY() * ratiox_1_1_1);
                            vecMultRatiox1_1_1.setZ(positionVecNext.getZ() * ratiox_1_1_1);
                            Vector3.sumTwoVector(positionCalculated, positionCalculated, vecMultRatiox1_1_1);
                            break;
                        case 0x2:
                            ratiox2 = (1.0f - trackElemElapsedTimeFullTimeRatio) * (1.0f - trackElemElapsedTimeFullTimeRatio);
                            positionCalculated.setX(positionVec.getX() * ratiox2);
                            positionCalculated.setY(positionVec.getY() * ratiox2);
                            positionCalculated.setZ(positionVec.getZ() * ratiox2);
                            ratiox1_2 = (1.0f - trackElemElapsedTimeFullTimeRatio) * (2.0f * trackElemElapsedTimeFullTimeRatio);
                            vecMultRatiox1_2.setX(currTrackElem.getPart3case2vec().getX() * ratiox1_2);
                            vecMultRatiox1_2.setY(currTrackElem.getPart3case2vec().getY() * ratiox1_2);
                            vecMultRatiox1_2.setZ(currTrackElem.getPart3case2vec().getZ() * ratiox1_2);
                            Vector3.sumTwoVector(positionCalculated, positionCalculated, vecMultRatiox1_2);
                            ratiox1_1 = trackElemElapsedTimeFullTimeRatio * trackElemElapsedTimeFullTimeRatio;
                            vecMultRatiox1_1.setX(positionVecNext.getX() * ratiox1_1);
                            vecMultRatiox1_1.setY(positionVecNext.getY() * ratiox1_1);
                            vecMultRatiox1_1.setZ(positionVecNext.getZ() * ratiox1_1);
                            Vector3.sumTwoVector(positionCalculated, positionCalculated, vecMultRatiox1_1);
                            break;
                        default:
                            ratio = 1.0f - trackElemElapsedTimeFullTimeRatio;
                            positionCalculated.setX(positionVec.getX() * ratio);
                            positionCalculated.setY(positionVec.getY() * ratio);
                            positionCalculated.setZ(positionVec.getZ() * ratio);
                            vecMultRatio.setX(positionVecNext.getX() * trackElemElapsedTimeFullTimeRatio);
                            vecMultRatio.setY(positionVecNext.getY() * trackElemElapsedTimeFullTimeRatio);
                            vecMultRatio.setZ(positionVecNext.getZ() * trackElemElapsedTimeFullTimeRatio);
                            Vector3.sumTwoVector(positionCalculated, positionCalculated, vecMultRatio);
                            break;
                    }
                } else {
                    positionCalculated.setX(positionVecNext.getX());
                    positionCalculated.setY(positionVecNext.getY());
                    positionCalculated.setZ(positionVecNext.getZ());
                }
            } else {
                positionCalculated.setX(positionVec.getX());
                positionCalculated.setY(positionVec.getY());
                positionCalculated.setZ(positionVec.getZ());
            }
            if (hasTrackBoneIdSet) {
                if (currentBoneGao.getAnimationControlFlags() != null && (currentBoneGao.getAnimationControlFlags() & 0x200) != 0) {
                    return;
                }
                if (isCurrentRefGaoEqCurrentGao && getEvent_CurrentRefMatrix() != null) {
                    currGaoMtx = ((getEvent_CurrentGAO().getFlags() & 0x400000) != 0) ? getEvent_CurrentGAO().getLocalMatrix() : getEvent_CurrentGAO().getGlobalMatrix();
                    Matrix4x4.invertMatrix(currGaoMtxInv, currGaoMtx);
                    Matrix4x4.matrixMultiplyWithNormalized(multWithRefMtx, getEvent_CurrentRefMatrix(), currGaoMtxInv);
                    Vector3.transformVertex(positionCalculated, multWithRefMtx, positionCalculated);
                    //  TODO: unknown add if (getEvent_CurrentGAO().getFlags() & 0x2) != 0)
                }
                if (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_ALL
                        || (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_NO_GLOBAL && !getEvent_CurrentTrack().getBoneId().equals((short) -1))) {
                    destCurrentMatrix.getPos().setX(positionCalculated.getX());
                    destCurrentMatrix.getPos().setY(positionCalculated.getY());
                    destCurrentMatrix.getPos().setZ(positionCalculated.getZ());
                    destCurrentMatrix.setFlags(destCurrentMatrix.getFlags() | 0x2);
                }
            } else {
                if (getEvent_CurrentRefMatrix() != null && isTrackUsesFlash(getEvent_CurrentTrack(), null, null, null)) {
                    Vector3.transformVertex(positionCalculated, getEvent_CurrentRefMatrix(), positionCalculated);
                }
                if (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_ALL
                        || (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_NO_GLOBAL && !getEvent_CurrentTrack().getBoneId().equals((short) -1))) {
                    destCurrentMatrix.getPos().setX(positionCalculated.getX());
                    destCurrentMatrix.getPos().setY(positionCalculated.getY());
                    destCurrentMatrix.getPos().setZ(positionCalculated.getZ());
                    destCurrentMatrix.setFlags(destCurrentMatrix.getFlags() | 0x2);
                }

                // TODO: innen ki lett hagyva a collision
            }
        }
        if ((currTrackElemTrackFrameTypeFlags & 0x14) != 0) {
            interpolationKeyPlayComputeInterpolatedMatrixAdvanced(interpolatedMtxRet, etfea, currElemIdx, nextElemIndex, actualElemElapsedTime);
            if (hasTrackBoneIdSet) {
                if (currentBoneGao.getAnimationControlFlags() != null && (currentBoneGao.getAnimationControlFlags() & 0x400) != 0) {
                    return;
                }
                if (isCurrentRefGaoEqCurrentGao && getEvent_CurrentRefMatrix() != null) {
                    if ((getEvent_CurrentGAO().getFlags() & 0x400000) != 0) {
                        refMtx = getEvent_CurrentGAO().getLocalMatrix();
                    } else {
                        refMtx = getEvent_CurrentGAO().getGlobalMatrix();
                    }
                    Matrix4x4.invertMatrix(refMtxInv, refMtx);
                    Matrix4x4.matrixMultiplyWithNormalized(tmpRefMtx2, getEvent_CurrentRefMatrix(), refMtxInv);
                    Matrix4x4.mult33Matrix(mtxCalculated, interpolatedMtxRet, tmpRefMtx2, true);
                } else {
                    mtxCalculated.set33(interpolatedMtxRet);
                }
                destCurrentMatrix.set33(mtxCalculated);
            } else {
                if (getEvent_CurrentRefMatrix() != null && isTrackUsesFlash(getEvent_CurrentTrack(), null, null, null)) {
                    Matrix4x4.mult33Matrix(mtxCalculated, interpolatedMtxRet, getEvent_CurrentRefMatrix(), true);
                } else {
                    mtxCalculated.set33(interpolatedMtxRet);
                }
                destCurrentMatrix.set33(mtxCalculated);
            }
            destCurrentMatrix.setFlags(destCurrentMatrix.getFlags() | 0x4);
        }
        if (hasTrackBoneIdSet && shouldSetReferenceToCurrGao && (currTrackElemTrackFrameTypeFlags & 0x3) != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                printAnimationDebugMessage("TODO: hasTrackBoneIdSet && shouldSetReferenceToCurrGao && (currTrackElemTrackFrameTypeFlags & 3) != 0 unsupported.");
            }
        }
    }

    private void eventsResetEventTracksHasFlag1(AnimationPlayerTrackData aptd) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (aptd == null || aptd.getEventTracks() == null || aptd.getEventTracks().getEventTracks() == null || aptd.getTrackOptions() == null) {
            return;
        }

        ArrayList<EventTrackElem> eteal = aptd.getEventTracks().getEventTracks();
        ArrayList<EventTrackOption> etoal = aptd.getTrackOptions();
        EventTrackOption eto;

        for (int i = 0; i < eteal.size(); i++) {
            eto = etoal.get(i);
            if ((eto.getFlag() & 0x1) != 0) {
                eto.setFlag((short) (eto.getFlag() & ~0x4));
                eto.setActElemNum((short) 0);
                eto.setActElemTimeElapsed(0.0f);
            }
        }
    }

    private void eventsReferenceMatrixSet() {
        Matrix4x4 mtx;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (getEventsReferenceMatrixSetEnable() && getEvent_CurrentTrackList() != null && getEvent_CurrentGAO() != null) {
            if ((getEvent_CurrentGAO().getFlags() & 0x400000) != 0) {
                mtx = getEvent_CurrentGAO().getLocalMatrix();
            } else {
                mtx = getEvent_CurrentGAO().getGlobalMatrix();
            }
            if (getEvent_CurrentRefMatrix() != null) {
                getEvent_CurrentRefMatrix().set(mtx);
            } else {
                BFGaoChunk.setFlashMatrix(getEvent_CurrentGAO(), mtx);
            }
        }
    }

    private EventsSearchLabelStruct eventsSearchLabel(ArrayList<EventTrackFrameElem> etfea, int flag) {
        float tmpSumTime;
        EventTrackFrameElem etfe;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        tmpSumTime = 0.0f;
        for (int i = 0; i < etfea.size(); i++) {
            etfe = etfea.get(i);
            if ((etfe.getTrackFrameFlags() & 0xF000) == flag
                    && (etfe.getTrackFrameFlags() & 0x7C0) != 0x200
                    && (etfe.getTrackFrameFlags() & 0x7C0) != 0x400) {
                return new EventsSearchLabelStruct(tmpSumTime, i);
            }
            tmpSumTime = etfe.getTrackFrameLen() * 1.0f / 60.0f + tmpSumTime;
        }
        return null;
    }

    private EventsSearchLabelStruct eventsSearchTime(ArrayList<EventTrackFrameElem> etfea, float time) {
        int tmpIdx;
        float actTime;
        float trackFrameLen;
        int i;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        actTime = time;
        for (i = 0;; ++i) {
            if (etfea == null || i >= etfea.size()) {
                if (etfea != null) {
                    time = etfea.get(etfea.size() - 1).getTrackFrameLen() * 1.0f / 60.0f;
                    tmpIdx = etfea.size() - 1;
                } else {
                    time = 0;
                    tmpIdx = 0;
                }
                return new EventsSearchLabelStruct(time, tmpIdx);
            }
            trackFrameLen = etfea.get(i).getTrackFrameLen() * 1.0f / 60.0f;
            if (trackFrameLen >= actTime) {
                break;
            }
            actTime = actTime - etfea.get(i).getTrackFrameLen() * 1.0f / 60.0f;
        }
        if (trackFrameLen - actTime >= (1.0f / 60.0f)) {
            time = actTime;
        } else {
            time = 0;
            ++i;
        }
        return new EventsSearchLabelStruct(time, i);
    }

    private int eventsGotoCurrentEventAllEventTrackToTime(float time) {
        int result = 0;
        EventsSearchLabelStruct esls;
        EventTrackOption eto;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        for (int i = 0; i < getEvent_CurrentTrackList().getEventTracks().size(); i++) {
            esls = eventsSearchTime(getEvent_CurrentTrackList().getEventTracks().get(i).getEventTrackFrameElems(), time);
            if (esls.getElemIndex() != -1) {
                if (getEvent_OwnerGAO() == null || getEvent_OwnerGAO().getCurrentAttachedAnimationPlayerDataContainer() == null
                        || getEvent_OwnerGAO().getCurrentAttachedAnimationPlayerDataContainer().getTrackData().getTrackOptions() == null
                        || getEvent_OwnerGAO().getCurrentAttachedAnimationPlayerDataContainer().getTrackData().getTrackOptions().size() <= i) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        printAnimationDebugMessage("BUG: eventsGotoCurrentEventAllEventTrackToTime: getTrackOptions(id=" + i + ") not found.");
                    }
                    return 0;
                }

                eto = getEvent_OwnerGAO().getCurrentAttachedAnimationPlayerDataContainer().getTrackData().getTrackOptions().get(i);
                eto.setFlag((short) (eto.getFlag() & ~0x4));
                eto.setActElemNum((short) esls.getElemIndex());
                eto.setActElemTimeElapsed(esls.getSumTime());
            }
            result = i + 1;
        }
        return result;
    }

    private EventsTrackTimeStruct eventsGetTrackTime(AnimationPlayerTrackData aptd, int trackID) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        EventsTrackTimeStruct etts = new EventsTrackTimeStruct(0.0f, 0.0f);

        if (aptd == null || aptd.getEventTracks() == null || aptd.getEventTracks().getEventTracks() == null
                || aptd.getEventTracks().getEventTracks().size() <= trackID) {
            return etts;
        }
        EventTrackElem trackElems = aptd.getEventTracks().getEventTracks().get(trackID);
        ArrayList<EventTrackFrameElem> tfes = trackElems.getEventTrackFrameElems();
        if (tfes == null) {
            return etts;
        }

        int i;
        for (i = 0; i < aptd.getTrackOptions().get(trackID).getActElemNum(); i++) {
            etts.setElapsedTime(tfes.get(i).getTrackFrameLen() * 1.0f / 60.0f + etts.getElapsedTime());
        }
        etts.setTotalTime(etts.getElapsedTime());
        for (; i < tfes.size(); i++) {
            etts.setTotalTime(tfes.get(i).getTrackFrameLen() * 1.0f / 60.0f + etts.getTotalTime());
        }

        etts.setElapsedTime(etts.getElapsedTime() + aptd.getTrackOptions().get(trackID).getActElemTimeElapsed());

        return etts;
    }

    private void eventsSetTracksTime(AnimationPlayerTrackData aptd, float time) {
        float actTime;
        int sumTime;
        int sumtimeCurr;
        EventTrackOption trackFrameOption;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (aptd.getEventTracks() == null) {
            return;
        }

        ArrayListWithSizeOverLoad<EventTrackElem> eventTracks = aptd.getEventTracks().getEventTracks();
        EventTrackElem ete;

        sumTime = (int) (time * 60.0f);
        int i = 0, j = 0, k = 0;
        for (trackFrameOption = aptd.getTrackOptions().get(i);; i++) {
            if (j >= eventTracks.size()) {
                break;
            }
            sumtimeCurr = sumTime;

            trackFrameOption.setActElemNum((short) 0);
            ete = eventTracks.get(j);
            for (k = 0; k < ete.getEventTrackFrameElems().size(); k++) {
                if (sumtimeCurr < ete.getEventTrackFrameElems().get(k).getTrackFrameLen()) {
                    actTime = time - sumTime / 60.0f;
                    trackFrameOption.setActElemTimeElapsed(sumtimeCurr / 60.0f + actTime);
                    trackFrameOption.setFlag((short) (trackFrameOption.getFlag() | 0x4));
                    break;
                }
                sumtimeCurr -= ete.getEventTrackFrameElems().get(k).getTrackFrameLen();
                trackFrameOption.setActElemTimeElapsed(0.0f);
                trackFrameOption.setFlag((short) (trackFrameOption.getFlag() | 0x4));

                trackFrameOption.setActElemNum((short) (trackFrameOption.getActElemNum() + 1));
            }
            if (trackFrameOption.getActElemNum() == ete.getEventTrackFrameElems().size()) {
                trackFrameOption.setActElemNum((short) (trackFrameOption.getActElemNum() - 1));
            }
            j++;
        }
    }

    private PlayTrackReturnTimeAndShouldCallChangeAction eventsRunOneEventTrackAdvanced(EventTrackElem trackArrayElem, EventTrackOption trackOption, boolean shouldResetFinishedTrack, float timeDiff) {
        float tfl;
        float trackFrameLen;
        float timeDiffToRet;
        EventTrackFrameElem trackCurrentElem;
        EventsSearchLabelStruct esls;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (trackArrayElem == null || trackArrayElem.getEventTrackFrameElems() == null) {
            return new PlayTrackReturnTimeAndShouldCallChangeAction(false, timeDiff);
        }

        if ((trackArrayElem.getEventTrackFlags() & 0x8) != 0) {
            trackOption.setMagicBoxBlendFactor(0xffffffff);
        }
        if (shouldResetFinishedTrack) {
            if (getTimeDiffBetweenLoops() != 0.0f
                    && trackOption.getActElemNum() == trackArrayElem.getEventTrackFrameElems().size() - 1) {
                tfl = trackArrayElem.getEventTrackFrameElems().get(trackOption.getActElemNum()).getTrackFrameLen();
                if (tfl * 1.0f / 60.0f == trackOption.getActElemTimeElapsed()) {
                    if ((trackArrayElem.getEventTrackFlags() & 0x2) != 0) {
                        if (trackArrayElem.getBoneId().equals((short) -1)) {
                            eventsResetEventTracksHasFlag1(getEventsCurrentData());
                        } else {
                            trackOption.setActElemNum((short) 0);
                            trackOption.setFlag((short) (trackOption.getFlag() & ~0x4));
                        }
                    }
                }
            }
        }
        timeDiffToRet = getTimeDiffBetweenLoops();
        while (true) {
            while (true) {
                trackCurrentElem = trackArrayElem.getEventTrackFrameElems().get(trackOption.getActElemNum());
                setEvent_CurrentTrackFrameElem(trackCurrentElem);
                setEvent_elapsedTime(getTimeDiffBetweenLoops() - timeDiffToRet);
                if (trackCurrentElem.getTrackFrameLen() * 1.0f / 60.0f >= timeDiffToRet + trackOption.getActElemTimeElapsed()) {
                    setEvent_elapsedTimeInside(timeDiffToRet);
                    trackOption.setActElemTimeElapsed(timeDiffToRet
                            + trackOption.getActElemTimeElapsed());
                    timeDiffToRet = 0.0f;
                } else {
                    trackFrameLen = trackCurrentElem.getTrackFrameLen();
                    setEvent_elapsedTimeInside(trackFrameLen * 1.0f / 60.0f - trackOption.getActElemTimeElapsed());
                    timeDiffToRet = timeDiffToRet - getEvent_elapsedTimeInside();
                    trackOption.setActElemTimeElapsed(trackCurrentElem.getTrackFrameLen() * 1.0f / 60.0f);
                }
                if ((trackCurrentElem.getTrackFrameFlags() & 0x4) != 0 && (trackOption.getFlag() & 0x4) == 0 // ha van flash, csak egyszer fut le, utána ráteszi a 0x4-et lejjebb
                        && (getEvent_CurrentTrackList().getFlags() & 0x1) == 0 && getTimeDiffBetweenLoops() >= 0.0f) {
                    eventsReferenceMatrixSet();
                }
                if ((trackCurrentElem.getTrackFrameFlags() & 0x1) == 0 || (trackOption.getFlag() & 0x4) == 0) {
                    switch (trackCurrentElem.getTrackFrameFlags() & 0x7C0) {
                        case 0x40:
                            if (!getEventsisBeingUpdated()) {
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    printAnimationDebugMessage("TODO: AI function is not supported.");
                                }
                            }
                            break;
                        case 0x80:
                            if ((timeDiffToRet == 0.0f
                                    || trackOption.getActElemNum() == trackArrayElem.getEventTrackFrameElems().size() - 1)) {
                                interpolationKeyPlayAdvanced(trackArrayElem.getEventTrackFrameElems(), trackOption.getActElemNum());
                            }
                            break;
                        case 0x200:
                            if (!getEventsisBeingUpdated()) {
                                esls = eventsSearchLabel(trackArrayElem.getEventTrackFrameElems(), trackCurrentElem.getTrackFrameFlags() & 0xF000);
                                if (esls.getElemIndex() != -1) {

                                    trackOption.setActElemTimeElapsed(0);
                                    trackOption.setActElemNum((short) esls.getElemIndex());
                                }
                            }
                            break;
                        case 0x400:
                            if (!getEventsisBeingUpdated()) {
                                esls = eventsSearchLabel(trackArrayElem.getEventTrackFrameElems(), trackCurrentElem.getTrackFrameFlags() & 0xF000);
                                if (esls.getElemIndex() != -1) {
                                    eventsGotoCurrentEventAllEventTrackToTime(esls.getSumTime());
                                    setEventsNeedToReparse(true);
                                    return new PlayTrackReturnTimeAndShouldCallChangeAction(false, timeDiff);
                                }
                                break;
                            }
                    }
                }
                trackOption.setFlag((short) (trackOption.getFlag() | 0x4));
                if (timeDiffToRet == 0.0f) {
                    return new PlayTrackReturnTimeAndShouldCallChangeAction(false, timeDiffToRet);
                }
                trackOption.setActElemTimeElapsed(0);
                if (trackOption.getActElemNum() == trackArrayElem.getEventTrackFrameElems().size() - 1) {
                    break;
                }
                trackOption.setActElemNum((short) (trackOption.getActElemNum() + 1));
                trackOption.setFlag((short) (trackOption.getFlag() & ~0x4));
            }
            if ((trackArrayElem.getEventTrackFlags() & 0x2) != 0) {
                trackOption.setActElemNum((short) 0);
                trackOption.setFlag((short) (trackOption.getFlag() & ~0x4));
                // TODO: this is same
                if (getEvent_CurrentGAO() == getEvent_OwnerGAO() && (getEvent_CurrentGAO().getFlags() & 0x2) != 0
                        && shouldResetFinishedTrack && (trackCurrentElem.getTrackFrameFlags() & 0x7C0) == 0x80) {
                    trackOption.setActElemTimeElapsed(trackCurrentElem.getTrackFrameLen() * 1.0f / 60.0f);
                    trackOption.setActElemNum((short) (trackArrayElem.getEventTrackFrameElems().size() - 1));
                    return new PlayTrackReturnTimeAndShouldCallChangeAction(true, timeDiffToRet);
                }
                continue;
            }
            if ((trackArrayElem.getEventTrackFlags() & 0x4) != 0) {
                break;
            }
            // TODO: with this
            if (getEvent_CurrentGAO() == getEvent_OwnerGAO() && (getEvent_CurrentGAO().getFlags() & 0x2) != 0
                    && shouldResetFinishedTrack && (trackCurrentElem.getTrackFrameFlags() & 0x7C0) == 0x80) {
                trackOption.setActElemTimeElapsed(trackCurrentElem.getTrackFrameLen() * 1.0f / 60.0f);
                trackOption.setActElemNum((short) (trackArrayElem.getEventTrackFrameElems().size() - 1));
                return new PlayTrackReturnTimeAndShouldCallChangeAction(true, timeDiffToRet);
            }
        }
        trackOption.setActElemTimeElapsed(trackCurrentElem.getTrackFrameLen() * 1.0f / 60.0f);
        trackOption.setFlag((short) (trackOption.getFlag() & ~0x1));
        return new PlayTrackReturnTimeAndShouldCallChangeAction(true, timeDiffToRet);
    }

    private PlayTrackReturnTimeAndShouldCallChangeAction eventsRunOneEventTrackStandard(EventTrackElem trackArrayElem, EventTrackOption trackOption, int trackID, int flag, float time) {
        float tmpTimeDiff;
        float timeDiff;
        EventTrackFrameElem currElem;
        float elemTime;
        int currElemIdx;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (trackOption.getActElemNum() == trackArrayElem.getEventTrackFrameElems().size() - 1) {
            if (trackArrayElem.getBoneId().equals((short) -1)) {
                eventsResetEventTracksHasFlag1(getEventsCurrentData());
            } else {
                trackOption.setFlag((short) (trackOption.getFlag() & ~0x4));
                trackOption.setActElemNum((short) 0);
                trackOption.setActElemTimeElapsed(0.0f);
            }
        }
        tmpTimeDiff = getTimeDiffBetweenLoops();
        while (true) {
            currElemIdx = trackOption.getActElemNum();
            currElem = trackArrayElem.getEventTrackFrameElems().get(currElemIdx);
            setEvent_CurrentTrackFrameElem(currElem);
            elemTime = currElem.getTrackFrameLen() * 1.0f / 60.0f;
            if ((currElem.getTrackFrameFlags() & 0x4) != 0 && (trackOption.getFlag() & 4) == 0) {
                eventsReferenceMatrixSet();
            }
            if (elemTime - 0.0000099999997f >= tmpTimeDiff + trackOption.getActElemTimeElapsed()) {
                break;
            }
            timeDiff = tmpTimeDiff - (elemTime - trackOption.getActElemTimeElapsed());
            if (timeDiff <= 0.0f) {
                tmpTimeDiff = 0.0f;
            } else {
                tmpTimeDiff = timeDiff;
            }
            if (trackOption.getActElemNum() == trackArrayElem.getEventTrackFrameElems().size() - 1) {
                // TODO: same as ->
                if ((currElem.getTrackFrameTypeFlags() & 0x14) != 0) {
                    interpolationKeyPlayStandardHandleTransformation(trackArrayElem.getEventTrackFrameElems(), currElemIdx);
                } else {
                    if (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_ALL
                            || (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_NO_GLOBAL && !trackArrayElem.getBoneId().equals((short) -1))) {
                        interpolationKeyPlayStandardHandlePosition(currElem);
                    }
                }
                trackOption.setActElemNum((short) (trackArrayElem.getEventTrackFrameElems().size() - 1));
                trackOption.setActElemTimeElapsed(0.0f);
                trackOption.setFlag((short) (trackOption.getFlag() & ~0x4));
                return new PlayTrackReturnTimeAndShouldCallChangeAction(true, tmpTimeDiff);
            }

            trackOption.setActElemTimeElapsed(0.0f);
            trackOption.setActElemNum((short) (trackOption.getActElemNum() + 1));
            trackOption.setFlag((short) (trackOption.getFlag() & ~0x4));
        }
        trackOption.setActElemTimeElapsed(tmpTimeDiff + trackOption.getActElemTimeElapsed());
        tmpTimeDiff = 0.0f;
        if (trackID > 1 /*&& 0.0f == 0.0f*/ && (flag & 2) != 0 && (flag & 0x10) != 0) { // TODO: debug this
            trackOption.setFlag((short) (trackOption.getFlag() | 0x4));
            return new PlayTrackReturnTimeAndShouldCallChangeAction(false, time);
        }
        if ((currElem.getTrackFrameTypeFlags() & 0x14) != 0) {
            interpolationKeyPlayStandardHandleTransformation(trackArrayElem.getEventTrackFrameElems(), currElemIdx);
        } else {
            if (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_ALL
                    || (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_NO_GLOBAL && !trackArrayElem.getBoneId().equals((short) -1))) {
                interpolationKeyPlayStandardHandlePosition(currElem);
            }
        }
        trackOption.setFlag((short) (trackOption.getFlag() | 0x4));
        if (0.0f == 0.0f) { // TODO: debug this
            return new PlayTrackReturnTimeAndShouldCallChangeAction(false, time);
        }
        // TODO: as this -->
        if ((currElem.getTrackFrameTypeFlags() & 0x14) != 0) {
            interpolationKeyPlayStandardHandleTransformation(trackArrayElem.getEventTrackFrameElems(), currElemIdx);
        } else {
            if (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_ALL
                    || (animGaoPositionUpdateMode == AnimationPlayer.POSITIONUPDATE_NO_GLOBAL && !trackArrayElem.getBoneId().equals((short) -1))) {
                interpolationKeyPlayStandardHandlePosition(currElem);
            }
        }
        trackOption.setActElemNum((short) (trackArrayElem.getEventTrackFrameElems().size() - 1));
        trackOption.setActElemTimeElapsed(0.0f);
        trackOption.setFlag((short) (trackOption.getFlag() & ~0x4));
        return new PlayTrackReturnTimeAndShouldCallChangeAction(true, tmpTimeDiff);
    }

    private PlayTrackReturnTimeAndShouldCallChangeAction eventsRunAllEventTracks(AnimationPlayerTrackData aptd, float timeDiff) {
        boolean ret;
        boolean eventsIsNotRunningAndHasGroup;
        Matrix4x4 gaoMtx;
        ArrayListWithSizeOverLoad<EventTrackElem> trackList;
        Matrix4x4 currRefMtx;
        int uf;
        ArrayList<EventTrackOption> trackOptions;
        float timeDiffBak;
        boolean runOneTrackRetSuccess;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        uf = 0;
        if (aptd != null) {
            if (aptd.getEventTracks() == null) {
                return new PlayTrackReturnTimeAndShouldCallChangeAction(true, timeDiff);
            }
            trackList = aptd.getEventTracks().getEventTracks();
            if (trackList.size() > 0) {
                trackOptions = aptd.getTrackOptions();
                if (trackOptions != null) {
                    runOneTrackRetSuccess = false;
                    timeDiffBak = 0;
                    setEvent_CurrentGAO(getEvent_OwnerGAO());
                    eventsIsNotRunningAndHasGroup = !getEventsRunning() && getEvent_CurrentGAO().getBonesGroupChunkInstanceKLE() != null && getEvent_CurrentGAO().getBonesGroupChunkInstanceKLE().getParseHandler() != null;
                    if (eventsIsNotRunningAndHasGroup) {
                        uf = computeAnimationPlayerUnknownComputedFlags(getEvent_CurrentGAO(), false);
                    }
                    for (int i = 0; i < trackList.getCustomSize(); ++i) {
                        if (eventsIsNotRunningAndHasGroup && i > 1
                                // TODO: another boolean (!bool &&) is ignored here, we need to find out what it does.
                                && ((getEvent_CurrentGAO().getVisualFlags() & 4) != 0 || (getEvent_CurrentGAO().getVisualFlags() & 2) == 0)
                                && trackList.get(i) != null && trackList.get(i).getEventTrackFrameElems() != null
                                && trackList.get(i).getEventTrackFrameElems().size() > 0
                                && (trackList.get(i).getEventTrackFrameElems().get(0).getTrackFrameFlags() & 0x7C0) == 0x80) {
                            if (getEvent_CurrentGAO().getCurrentAttachedAnimationPlayerDataContainer() != null) {
                                getEvent_CurrentGAO().setAnimationPlayerFlag(0);
                            }
                        } else if ((trackList.get(i).getEventTrackFlags() & 0x8000) == 0
                                || (uf & 4) == 0 || trackList.get(i).getBoneId().equals((short) -1)
                                || trackList.get(i).getBoneId().equals((short) 0) || (trackList.get(i).getEventTrackFrameElems().get(0).getTrackFrameTypeFlags() & 3) == 0) {
                            currRefMtx = getEvent_CurrentRefMatrix();
                            if ((trackOptions.get(i).getFlag() & 1) != 0 && (trackOptions.get(i).getFlag() & 2) == 0) {
                                setEvent_CurrentTrack(trackList.get(i));
                                setEvent_CurrentTrackOption(trackOptions.get(i));
                                if (getEvent_CurrentTrack().getInnerGaoKLE() != null && getEvent_CurrentTrack().getInnerGaoKLE().getParseHandler() != null) {
                                    setEvent_CurrentGAO((BFGaoChunk) (getEvent_CurrentTrack().getInnerGaoKLE().getParseHandler()));
                                    if ((getEvent_CurrentGAO().getFlags() & 0x4000000) != 0) {
                                        if ((getEvent_CurrentGAO().getFlags() & 0x10000) != 0) {
                                            gaoMtx = getEvent_CurrentGAO().getGlobalMatrixThird();
                                        } else {
                                            gaoMtx = getEvent_CurrentGAO().getGlobalMatrixSecond();
                                        }
                                    } else {
                                        gaoMtx = null;
                                    }
                                    setEvent_CurrentRefMatrix(gaoMtx); // gaoMtx = flash matrix
                                } else {
                                    setEvent_CurrentGAO(getEvent_OwnerGAO());
                                }
                                setEventsNeedToReparse(false);
                                PlayTrackReturnTimeAndShouldCallChangeAction ptras = eventsRunOneEventTrackAdvanced(trackList.get(i), trackOptions.get(i), true, timeDiff);
                                timeDiff = ptras.getTime();
                                if (ptras.getChangeActionCallNeeded()) {
                                    runOneTrackRetSuccess = true;
                                    if (i == 0) {
                                        timeDiffBak = timeDiff;
                                    }
                                }
                                if (getEventsNeedToReparse()) {
                                    i = -1;
                                }
                                if (getEventsRunning()) {
                                    if ((getEvent_CurrentGAO().getFlags() & 0x4000000) != 0) {
                                        setEventsRunning(false);
                                        updateFlash(getEvent_CurrentGAO(), 0x3);
                                        setEventsRunning(true);
                                    }
                                    if ((getEvent_CurrentGAO().getFlags() & 4) != 0) {
                                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                                            printAnimationDebugMessage("physics not supported.");
                                        }
                                    }
                                }
                            }
                            setEvent_CurrentRefMatrix(currRefMtx);
                        }
                    }
                    timeDiff = timeDiffBak;
                    ret = runOneTrackRetSuccess;
                } else {
                    ret = true;
                }
            } else {
                ret = true;
            }
        } else {
            ret = true;
        }
        return new PlayTrackReturnTimeAndShouldCallChangeAction(ret, timeDiff);
    }

    private PlayTrackReturnTimeAndShouldCallChangeAction _eventsRunAllAdvancedTracksFromAllTracks(BFEventsChunk trackList, ArrayList<EventTrackOption> trackOptions,
            boolean changeActionNeeded, float timeDiff, float timeBackup) {
        int j;
        BFGaoChunk gao;
        Matrix4x4 flashMatrix;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        for (j = trackList.getEventTracks().size() - 1; j != 0 && (trackList.getEventTracks().get(j).getEventTrackFlags() & 0x8000) == 0; --j) {
            setEvent_CurrentTrack(trackList.getEventTracks().get(j));
            setEvent_CurrentTrackOption(trackOptions.get(j));
            if (getEvent_CurrentTrack().getInnerGaoKLE() != null && getEvent_CurrentTrack().getInnerGaoKLE().getParseHandler() != null) {
                gao = (BFGaoChunk) (getEvent_CurrentTrack().getInnerGaoKLE().getParseHandler());
            } else {
                gao = getEvent_OwnerGAO();
            }
            setEvent_CurrentGAO(gao);
            if ((gao.getFlags() & 0x4000000) != 0) {
                if ((getEvent_CurrentGAO().getFlags() & 0x10000) != 0) {
                    flashMatrix = getEvent_CurrentGAO().getGlobalMatrixThird();
                } else {
                    flashMatrix = getEvent_CurrentGAO().getGlobalMatrixSecond();
                }
            } else {
                flashMatrix = null;
            }
            setEvent_CurrentRefMatrix(flashMatrix);
            eventsRunOneEventTrackAdvanced(trackList.getEventTracks().get(j), trackOptions.get(j), true, timeDiff);
        }
        timeDiff = timeBackup;
        return new PlayTrackReturnTimeAndShouldCallChangeAction(changeActionNeeded, timeDiff);
    }

    private PlayTrackReturnTimeAndShouldCallChangeAction eventsRunAllEventTracksStandard(AnimationPlayerTrackData aptd, float timeDiff) {
        Matrix4x4 currFlashMatrix;
        boolean changeActionNeeded;
        boolean doNotRender;
        float timeBackup;
        boolean hasBoneId;
        BFEventsChunk trackList;
        Matrix4x4 currentRefMatrix;
        short boneId;
        int cameraFlags;
        ArrayList<EventTrackOption> TrackOptions;
        ArrayList<TreeMap<Integer, BFKeyListElem>> bonesGroup;
        BFGaoChunk currBoneGao;
        boolean flagHas0x8000;
        PlayTrackReturnTimeAndShouldCallChangeAction ptrtas;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (aptd == null) {
            return new PlayTrackReturnTimeAndShouldCallChangeAction(false, timeDiff);
        }
        trackList = aptd.getEventTracks();
        TrackOptions = aptd.getTrackOptions();
        if (trackList == null || TrackOptions == null) {
            return new PlayTrackReturnTimeAndShouldCallChangeAction(false, timeDiff);
        }
        setEvent_CurrentTrackList(trackList);

        currentRefMatrix = getEvent_CurrentRefMatrix();
        setEvent_CurrentGAO(getEvent_OwnerGAO());
        bonesGroup = RenderUtils.getGRPLForGao(getEvent_OwnerGAO());
        doNotRender = ((getEvent_CurrentGAO().getVisualFlags() & 0x4) != 0 // TODO: another boolean (!bool &&) is ignored here, we need to find out what it does.
                || (getEvent_CurrentGAO().getVisualFlags() & 0x2) == 0);
        //TODO: handle do not render gao flag -> doNotRender = true;
        cameraFlags = computeAnimationPlayerUnknownComputedFlags(getEvent_OwnerGAO(), true);
        timeBackup = 0;
        changeActionNeeded = false;
        for (int i = 0;; ++i) {
            if (i >= trackList.getEventTracks().getCustomSize()) {
                timeDiff = timeBackup;
                return new PlayTrackReturnTimeAndShouldCallChangeAction(changeActionNeeded, timeDiff);
            }
            setEvent_CurrentRefMatrix(currentRefMatrix);
            setEvent_CurrentTrack(trackList.getEventTracks().get(i));
            setEvent_CurrentTrackOption(TrackOptions.get(i));
            if (getEvent_CurrentTrack().getInnerGaoKLE() != null && getEvent_CurrentTrack().getInnerGaoKLE().getParseHandler() != null) {
                setEvent_CurrentGAO((BFGaoChunk) (getEvent_CurrentTrack().getInnerGaoKLE().getParseHandler()));
                if ((getEvent_CurrentGAO().getFlags() & 0x4000000) != 0) {
                    if ((getEvent_CurrentGAO().getFlags() & 0x10000) != 0) {
                        currFlashMatrix = getEvent_CurrentGAO().getGlobalMatrixThird();
                    } else {
                        currFlashMatrix = getEvent_CurrentGAO().getGlobalMatrixSecond();
                    }
                } else {
                    currFlashMatrix = null;
                }
                setEvent_CurrentRefMatrix(currFlashMatrix);
            } else {
                setEvent_CurrentGAO(getEvent_OwnerGAO());
            }
            hasBoneId = getEvent_CurrentTrack().getBoneId().equals((short) -1);
            if ((getEvent_CurrentTrackOption().getFlag() & 0x1) == 0) {
                continue;
            }
            if ((getEvent_CurrentTrackOption().getFlag() & 0x2) != 0) {
                continue;
            }
            flagHas0x8000 = (getEvent_CurrentTrack().getEventTrackFlags() & 0x8000) != 0;
            if (flagHas0x8000) {
                if ((cameraFlags & 0x4) != 0
                        && !getEvent_CurrentTrack().getBoneId().equals((short) -1) && !getEvent_CurrentTrack().getBoneId().equals((short) 0)
                        && (getEvent_CurrentTrack().getEventTrackFrameElems().get(0).getTrackFrameTypeFlags() & 0x3) != 0) {
                    continue;
                }
            }
            if (doNotRender && (cameraFlags & 0x80) != 0 && !hasBoneId) {
                break;
            }
            if ((cameraFlags & 1) != 0 && !hasBoneId) {
                return _eventsRunAllAdvancedTracksFromAllTracks(trackList, TrackOptions, changeActionNeeded, timeDiff, timeBackup);
            }
            if (flagHas0x8000 && (cameraFlags & 0x8) != 0) {
                boneId = getEvent_CurrentTrack().getBoneId();
                if (!hasBoneId && bonesGroup.size() > boneId) {
                    currBoneGao = (BFGaoChunk) (((BFKeyListElem) (bonesGroup.get(boneId).firstEntry().getValue())).getParseHandler());
                    if (currBoneGao != null) {
                        if (currBoneGao.getAnimationControlFlags() != null && (currBoneGao.getAnimationControlFlags() & 0x20) != 0) {
                            continue;
                        }
                    }
                }
            }
            if (!flagHas0x8000 || (getEvent_CurrentTrack().getInnerGaoKLE() != null && getEvent_CurrentTrack().getInnerGaoKLE().getParseHandler() != null)) {
                ptrtas = eventsRunOneEventTrackAdvanced(trackList.getEventTracks().get(i), TrackOptions.get(i), true, timeDiff);
            } else {
                ptrtas = eventsRunOneEventTrackStandard(getEvent_CurrentTrack(), getEvent_CurrentTrackOption(), i, cameraFlags, timeDiff);
            }
            timeDiff = ptrtas.getTime();
            if (i == 0) {
                changeActionNeeded = ptrtas.getChangeActionCallNeeded();
            }
            if (i == 0 && ptrtas.getChangeActionCallNeeded()) {
                timeBackup = timeDiff;
            }
        }
        // only in case of doNotRender gets here
        getEvent_OwnerGAO().setAnimationPlayerFlag(0);
        return _eventsRunAllAdvancedTracksFromAllTracks(trackList, TrackOptions, changeActionNeeded, timeDiff, timeBackup);
    }

    private void recomputeRefMatrix(BFGaoChunk gao, AnimationPlayerDataContainer apdc) {
        Matrix4x4 mtx;
        float timeDiffBackup;
        Matrix4x4 matrixBackup = new Matrix4x4();
        Matrix4x4 localMatrixBackup2 = new Matrix4x4();
        Matrix4x4 localMatrixBackup = new Matrix4x4();
        Matrix4x4 localMatrixBackup2Inv = new Matrix4x4();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (gao == null) {
            return;
        }

        if ((gao.getFlags() & 0x400000) != 0) {
            localMatrixBackup.set(gao.getLocalMatrix());
            matrixBackup = localMatrixBackup;
        } else {
            localMatrixBackup2.set(gao.getGlobalMatrix());
            matrixBackup.set(gao.getGlobalMatrix());
            // gao's globalmatrix set not needed here
        }
        setEvent_OwnerGAO(gao);
        setEventsCurrentData(apdc.getTrackData());
        setEvent_CurrentTrackList(getEventsCurrentData().getEventTracks());
        setEvent_CurrentRefMatrix(Matrix4x4.getIdentityMatrix());
        setEventsRunning(false);
        if ((gao.getFlags() & 0x400000) != 0) {
            mtx = gao.getLocalMatrix();
        } else {
            mtx = gao.getGlobalMatrix();
        }
        mtx.setM14(0.0f);
        mtx.setM24(0.0f);
        mtx.setM34(0.0f);
        mtx.setFlags(mtx.getFlags() & ~0x8);

        if (getEvent_CurrentTrackList() != null) {
            getEvent_CurrentTrackList().getEventTracks().setCustomSize((getEvent_CurrentTrackList().getEventTracks().get(1).getBoneId().equals((short) -1) ? 1 : 0) + 1);
            timeDiffBackup = getTimeDiffBetweenLoops();
            setTimeDiffBetweenLoops(0.0f);
            setEventsReferenceMatrixSetEnable(false);
            eventsRunAllEventTracks(apdc.getTrackData(), 0.0f);
            setEventsReferenceMatrixSetEnable(true);
            setTimeDiffBetweenLoops(timeDiffBackup);
            getEvent_CurrentTrackList().getEventTracks().setCustomSize(null);
        }
        if ((gao.getFlags() & 0x400000) != 0) {
            localMatrixBackup2.set(gao.getLocalMatrix());

            gao.getLocalMatrix().set(localMatrixBackup);
        } else {
            gao.getGlobalMatrix().set(matrixBackup);
        }
        Matrix4x4.invertMatrix(localMatrixBackup2Inv, localMatrixBackup2);

        if (apdc == gao.getCurrentAttachedAnimationPlayerDataContainer()) {
            if ((gao.getFlags() & 0x4000000) != 0) {
                if ((gao.getFlags() & 0x10000) != 0) {
                    Matrix4x4.matrixMultiplyWithNormalized(gao.getGlobalMatrixThird(), localMatrixBackup2Inv, matrixBackup);
                } else {
                    Matrix4x4.matrixMultiplyWithNormalized(gao.getGlobalMatrixSecond(), localMatrixBackup2Inv, matrixBackup);
                }
            } else {
                Matrix4x4.matrixMultiplyWithNormalized(null, localMatrixBackup2Inv, matrixBackup); // ???
            }
        } else {
            Matrix4x4.matrixMultiplyWithNormalized(apdc.getFlashMatrix(), localMatrixBackup2Inv, matrixBackup);
        }
    }

    private void updateFlash(BFGaoChunk gao, int flag) {
        AnimationPlayerDataContainer apdc;
        Matrix4x4 crm;
        BFEventsChunk ctl;
        BFGaoChunk ogao;
        AnimationPlayerTrackData cdata;
        EventTrackElem ct;
        EventTrackOption cto;
        EventTrackFrameElem ctfe;
        boolean eventsRunning;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (gao != null && (gao.getFlags() & 0x2) != 0 && (gao.getFlags() & 0x4000000) != 0 && gao.getCurrentAttachedAnimationPlayerDataContainer() != null) {

            apdc = gao.getCurrentAttachedAnimationPlayerDataContainer();
            ct = getEvent_CurrentTrack();
            cto = getEvent_CurrentTrackOption();
            crm = getEvent_CurrentRefMatrix();
            ctfe = getEvent_CurrentTrackFrameElem();
            ctl = getEvent_CurrentTrackList();
            ogao = getEvent_OwnerGAO();
            eventsRunning = getEventsRunning();
            cdata = getEventsCurrentData();
            if ((flag & 0x1) != 0) {
                recomputeRefMatrix(gao, apdc);
            }
            if (gaoAnimationIsTransitioning(gao) && (flag & 0x2) != 0) {
                recomputeRefMatrix(gao, gao.getCurrentAttachedAnimationPlayerDataContainerForTransition());
            }
            setEventsCurrentData(cdata);
            setEvent_OwnerGAO(ogao);
            setEvent_CurrentTrackList(ctl);
            setEvent_CurrentTrack(ct);
            setEvent_CurrentRefMatrix(crm);
            setEvent_CurrentTrackOption(cto);
            setEvent_CurrentTrackFrameElem(ctfe);
            setEventsRunning(eventsRunning);
        }
    }

    private ActionsElemTransitionStructInside2 eventsFindActionTransition(BFActionListChunk actl, int currentActionID, int actionElemID, int nextActionID) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (currentActionID == -1) {
            return null;
        }
        ArrayList<BFKeyListElem> kles = actl.getActionsKLE();
        if (kles == null || kles.size() <= currentActionID || kles.get(currentActionID) == null || kles.get(currentActionID).getParseHandler() == null) {
            return null;
        }
        BFActionChunk act = (BFActionChunk) (kles.get(currentActionID).getParseHandler());
        if (act.getAesList() == null || act.getAesList().size() <= actionElemID || act.getAesList().get(actionElemID) == null) {
            return null;
        }
        ActionsElemStruct aes = act.getAesList().get(actionElemID);
        if (!aes.hasTransition() || aes.getATS() == null || aes.getATS().getATSi1Array() == null
                || aes.getATS().getATSi2Array() == null) {
            return null;
        }
        ArrayList<ActionsElemTransitionStructInside1> aetsi1a = aes.getATS().getATSi1Array();
        ArrayList<ActionsElemTransitionStructInside2> aetsi2a = aes.getATS().getATSi2Array();
        Integer tmpActID;
        for (int i = 0; i < aetsi1a.size(); i++) {
            if (aetsi1a.get(i) == null || (tmpActID = aetsi1a.get(i).getActionID()) == null) {
                continue;
            }
            if (tmpActID.equals(nextActionID) && aetsi2a.size() > i) {
                return aetsi2a.get(i);
            }
        }
        return null;
    }

    private void eventsGetElapsedTimeAndAdjust(BFGaoChunk gao) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (gao == null || gao.getCurrentAttachedAnimationPlayerDataContainer() == null) {
            return;
        }
        EventsTrackTimeStruct etts = eventsGetTrackTime(gao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), 0);
        eventsSetTracksTime(gao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), etts.getElapsedTime());
        if (gao.getAnimationPlayerSlots() != 1) {
            gao.setAnimationPlayerSlots(1);
        }
    }

    // TODO: placeholder, but does exactly what game uses of it
    private int computeAnimationPlayerUnknownComputedFlags(BFGaoChunk gao, boolean b) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        char ucf = (char) (gao.getAnimationPlayerUnknownComputedFlags() & 0xEF);

        if (gao.getAnimationControlFlags() != null && (gao.getAnimationControlFlags() & 0x80) != 0) {
            gao.setAnimationPlayerUnknownComputedFlags(gao.getAnimationPlayerUnknownComputedFlags() | 0x4);
        }
        if (ucf > (char) (gao.getAnimationPlayerUnknownComputedFlags() & 0xEF)) {
            eventsGetElapsedTimeAndAdjust(gao);
        }
        return gao.getAnimationPlayerUnknownComputedFlags();
    }

    private boolean gaoAnimationIsTransitioning(BFGaoChunk gao) {
        return (gao.getFlags() & 0x2) != 0 && (gao.getAnimationPlayerSlots() & 0x2) != 0;
    }

    private int updateFrequency(AnimationPlayerDataContainer apdc, Vector3 vec1, Vector3 vec2) {
        float vecsq;
        float vecLen;
        int ret;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        vecsq = vec1.getX() * vec1.getX()
                + vec1.getY() * vec1.getY()
                + vec1.getZ() * vec1.getZ();
        if (vecsq == 0.0f) {
            ret = 1;
            apdc.setUnkFlags((apdc.getUnkFlags() & ~0xff0000) | ret << 16);
        } else {
            vecLen = (float) Math.sqrt((vec2.getX() * vec2.getX()
                    + vec2.getY() * vec2.getY()
                    + vec2.getZ() * vec2.getZ()) / vecsq);
            if (vecLen >= 0.0f) {
                ret = (int) vecLen;
                if (vecLen > 255.0f) {
                    ret = (int) 255.0f;
                }
                apdc.setUnkFlags((apdc.getUnkFlags() & ~0xff0000) | ret << 16);
            } else {
                ret = 0;
                apdc.setUnkFlags((apdc.getUnkFlags() & ~0xff0000) | ret << 16);
            }
        }
        return ret;
    }

    private void updateFlashAfterMagicBoxTranslation(BFGaoChunk gao, Vector3 vec, int flagType) {
        Matrix4x4 flashMatrix;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if (gao != null) {
            if ((gao.getFlags() & 0x2) != 0) {
                if ((flagType & 0x1) != 0 && (gao.getFlags() & 0x4000000) != 0) {
                    if ((gao.getFlags() & 0x4000000) != 0) {
                        if ((gao.getFlags() & 0x10000) == 0) {
                            flashMatrix = gao.getGlobalMatrixSecond();
                        } else {
                            flashMatrix = gao.getGlobalMatrixThird();
                        }
                    } else {
                        flashMatrix = null;
                    }
                    Vector3.sumTwoVectorInPlace(flashMatrix.getPos(), vec);
                }
                if ((flagType & 0x2) != 0) {
                    if (gaoAnimationIsTransitioning(gao)) {
                        Vector3.sumTwoVectorInPlace(gao.getCurrentAttachedAnimationPlayerDataContainerForTransition().getFlashMatrix().getPos(), vec);
                        Vector3 tmp = new Vector3(gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().getM21(), // TODO: megnézni, hogy nem getM14-től kell-e
                                gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().getM22(),
                                gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().getM23());
                        Vector3.sumTwoVectorInPlace(tmp, vec);
                        gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().setM21(tmp.getX());
                        gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().setM22(tmp.getY());
                        gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().setM23(tmp.getZ());
                        Vector3.sumTwoVectorInPlace(gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().getPos(), vec);
                    }
                }
            }
        }
    }

    private void playAnimation(BFGaoChunk gao, BFGaoChunk gaoAnim, AnimationPlayerDataContainer apdc) {
        AnimationPlayerTrackData tracks;
        Matrix4x4 flashMtx;
        short actionID;
        int time;
        float timeDiffCalcdTmp;
        int shouldActionInit;
        float timeDiffCalcd;
        float timeDiffRet;
        int freq;
        Matrix4x4 globalMatrixBak = new Matrix4x4();
        boolean useRunAllTracksAnother;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        setEvent_OwnerGAO(gao);

        tracks = apdc.getTrackData();
        setEventsCurrentData(tracks);
        setEvent_CurrentTrackList(tracks.getEventTracks());
        if ((gao.getFlags() & 0x4000000) != 0) {
            if ((gao.getFlags() & 0x10000) == 0) {
                flashMtx = gao.getGlobalMatrixSecond();
            } else {
                flashMtx = gao.getGlobalMatrixThird();
            }
        } else {
            flashMtx = null;
        }
        setEvent_CurrentRefMatrix(flashMtx);

        if (getTimeDiffBetweenLoops() > 0.0f && !getEventsRunning() && gao.getBonesGroupChunkInstanceKLE() != null && gao.getBonesGroupChunkInstanceKLE().getParseHandler() != null) {
            useRunAllTracksAnother = true;
        } else {
            useRunAllTracksAnother = false;
        }

        timeDiffCalcd = getTimeDiffBetweenLoops();
        timeDiffRet = getTimeDiffBetweenLoops();
        while (true) {
            freq = (apdc.getUnkFlags() >> 16) & 0xff;
            setTimeDiffBetweenLoops(freq * timeDiffCalcd * (1 / 60.0f));
            if (gaoAnim.getAnimationPlayerActionShouldInit() != 0) {
                setTimeDiffBetweenLoops(gaoAnim.getAnimationPlayerElapsedTimeSetInSetAction());
            }
            if ((gao.getFlags() & 0x4000000) != 0) {
                if ((gao.getFlags() & 0x10000) == 0) {
                    flashMtx = gao.getGlobalMatrixSecond();
                } else {
                    flashMtx = gao.getGlobalMatrixThird();
                }
            } else {
                flashMtx = null;
            }
            setEvent_CurrentRefMatrix(flashMtx);

            if (((~(apdc.getUnkFlags() >> 7)) & 1) != 0) {
                PlayTrackReturnTimeAndShouldCallChangeAction ptrts = null;
                if (useRunAllTracksAnother) {
                    ptrts = eventsRunAllEventTracksStandard(apdc.getTrackData(), timeDiffCalcd);
                    timeDiffCalcd = ptrts.getTime();
                } else {
                    ptrts = eventsRunAllEventTracks(apdc.getTrackData(), timeDiffCalcd);
                    timeDiffCalcd = ptrts.getTime();
                }
                if (ptrts != null && ptrts.getChangeActionCallNeeded()) {
                    freq = (apdc.getUnkFlags() >> 16) & 0xff;
                    timeDiffCalcd = timeDiffCalcd * 60.0f / (float) freq;
                    changeGaoActionElements(gao, gao);
                    if ((gao.getFlags() & 0x2) != 0) {
                        if ((gao.getAnimationPlayerSlots() & 0x2) != 0 && timeDiffCalcd != 0.0) {
                            gaoAnim.getCurrentAttachedAnimationPlayerDataContainerForTransition().getTrackData().getTrackOptions().get(1).setMagicBoxBlendFactor(timeDiffRet);
                            gaoAnim.getCurrentAttachedAnimationPlayerDataContainerForTransition().setFlags(gaoAnim.getCurrentAttachedAnimationPlayerDataContainerForTransition().getFlags() | 0x1);
                        }
                    }
                }
            }
            shouldActionInit = gaoAnim.getAnimationPlayerActionShouldInit();
            if (gaoAnim.getAnimationPlayerActionShouldInit() != 0) {
                actionID = (short) gaoAnim.getAnimationPlayerProbablyActionIDAnother();
                gaoAnim.setAnimationPlayerActionShouldInit(0);
                gaoAnim.setAnimationPlayerProbablyActionIDAnother(-1);

                globalMatrixBak.set(gao.getGlobalMatrix());
                setGaoAction(gao, actionID, false);
                time = gaoAnim.getAnimationPlayerTime();
                if (time > 0) {
                    eventsSetTracksTime(gaoAnim.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), time * 0.016666668f);
                }
                if (shouldActionInit != -1) {
                    gaoAnim.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags((gaoAnim.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() & ~0xff0000) | shouldActionInit << 16);
                }
                gao.getGlobalMatrix().set(globalMatrixBak);
                updateFlash(gao, 0x3);
                timeDiffCalcdTmp = timeDiffRet - gaoAnim.getAnimationPlayerElapsedTimeSetInSetAction();
                gaoAnim.setAnimationPlayerElapsedTimeSetInSetAction(0.0f);
                timeDiffCalcd = timeDiffCalcdTmp;
            }
            setEventsCurrentData(apdc.getTrackData());
            setEvent_CurrentTrackList(getEventsCurrentData().getEventTracks());
            if ((gao.getFlags() & 0x4000000) != 0) {
                flashMtx = (gao.getFlags() & 0x10000) != 0 ? gao.getGlobalMatrixThird() : gao.getGlobalMatrixSecond();
            } else {
                flashMtx = null;
            }
            setEvent_CurrentRefMatrix(flashMtx);
            if (timeDiffCalcd == 0.0f) {
                break;
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                printAnimationDebugMessage(String.format("unkFlags: %x", apdc.getUnkFlags()));
            }
            if (((apdc.getUnkFlags() & 0xff) & 0x80) != 0) {
                setTimeDiffBetweenLoops(timeDiffRet);
                return;
            }
        }
        setTimeDiffBetweenLoops(timeDiffRet);
    }

    private float computeMagicBoxPositionForBlendAnim(BFGaoChunk gao, BFActionListChunk actionList, BFGaoChunk apGao, AnimationPlayerDataContainer apdc) {
        float timeDiff;
        BFActionChunk currAction;
        int actionDataElemID;
        int nextActionIDDataElem;
        int nextAction;
        float blendFactor;
        AnimationPlayerTrackData trackData;
        int unkFlagsActid;
        int flags;
        Matrix4x4 mtx;
        float timeDiffRet;
        float timeDiffBak;
        boolean useRunAllTracksAnother;
        int trackSize;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        timeDiffRet = getTimeDiffBetweenLoops();
        timeDiffBak = getTimeDiffBetweenLoops();
        timeDiff = getTimeDiffBetweenLoops();
        if (getTimeDiffBetweenLoops() > 0.0f && !getEventsRunning() && gao.getBonesGroupChunkInstanceKLE() != null && gao.getBonesGroupChunkInstanceKLE().getParseHandler() != null) {
            useRunAllTracksAnother = true;
        } else {
            useRunAllTracksAnother = false;
        }
        if ((apdc.getFlags() & 0x6) != 0) {
            interpolateMagicBoxPositionAfterEndOfAnimation(gao, apGao, apdc, timeDiffRet);
            setTimeDiffBetweenLoops(timeDiffBak);
            return timeDiff;
        }
        setEvent_OwnerGAO(gao);
        setEvent_CurrentGAO(gao);
        setEventsCurrentData(apdc.getTrackData());
        setEvent_CurrentTrackList(getEventsCurrentData().getEventTracks());
        setEvent_CurrentRefMatrix(apdc.getFlashMatrix());
        trackSize = getEvent_CurrentTrackList().getEventTracks().size();
        getEvent_CurrentTrackList().getEventTracks().setCustomSize((getEvent_CurrentTrackList().getEventTracks().get(1).getBoneId().equals((short) -1) ? 1 : 0) + 1);
        while (true) {
            while (true) {
                timeDiff = ((apdc.getUnkFlags() >> 16) & 0xff) * timeDiffRet * 0.016666668f;
                setTimeDiffBetweenLoops(timeDiff);
                if (useRunAllTracksAnother) {
                    PlayTrackReturnTimeAndShouldCallChangeAction ptrtscca = eventsRunAllEventTracksStandard(apdc.getTrackData(), timeDiffRet);
                    timeDiffRet = ptrtscca.getTime();
                    if (ptrtscca.getChangeActionCallNeeded()) {
                        break;
                    }
                } else {
                    PlayTrackReturnTimeAndShouldCallChangeAction ptrtscca = eventsRunAllEventTracks(apdc.getTrackData(), timeDiffRet);
                    timeDiffRet = ptrtscca.getTime();
                    if (ptrtscca.getChangeActionCallNeeded()) {
                        break;
                    }
                }
                timeDiff = timeDiffRet;
                if (timeDiffRet == 0.0f) {
                    getEvent_CurrentTrackList().getEventTracks().setCustomSize(trackSize);
                    setTimeDiffBetweenLoops(timeDiffBak);
                    return timeDiff;
                }
            }
            currAction = (BFActionChunk) (actionList.getActionsKLE().get(apdc.getActionID()).getParseHandler());
            actionDataElemID = apdc.getActionIDDataElemID();
            if (actionDataElemID != -1) {
                nextActionIDDataElem = actionDataElemID + 1;
                apdc.setActionIDDataElemID(nextActionIDDataElem);
                if (nextActionIDDataElem >= currAction.getAesList().size()) {
                    nextAction = ((BFActionChunk) (actionList.getActionsKLE().get(apdc.getActionID() + 1).getParseHandler())).getAesList().size(); // TODO: átnézni
                    apdc.setActionIDDataElemID(nextAction);
                    if (nextAction != -1 && nextAction >= currAction.getAesList().size()) {
                        apdc.setActionIDDataElemID(currAction.getAesList().size() - 1);
                    }
                }
            }
            if (apdc.getActionIDDataElemID() == -1) {
                break;
            }
            getEvent_CurrentTrackList().getEventTracks().setCustomSize(trackSize);
            blendFactor = apdc.getTrackData().getTrackOptions().get(0).getMagicBoxBlendFactor();
            if (!apdc.initAnimationPlayerDataContainer((BFEventsChunk) (currAction.getEventsChunksKLE().get(apdc.getActionIDDataElemID()).getParseHandler()), apGao)) {
                apGao.resetAnimationPlayerSpecificVars();
                return timeDiff;
            }
            apdc.getTrackData().getTrackOptions().get(0).setMagicBoxBlendFactor(blendFactor);
            apdc.getTrackData().getTrackOptions().get(1).setMagicBoxBlendFactor(blendFactor);
            trackData = apdc.getTrackData();
            apdc.setFlags(apdc.getFlags() | 0x1);
            setEventsCurrentData(trackData);
            setEvent_CurrentTrackList(trackData.getEventTracks());
            trackSize = getEvent_CurrentTrackList().getEventTracks().size();
            getEvent_CurrentTrackList().getEventTracks().setCustomSize((getEvent_CurrentTrackList().getEventTracks().get(1).getBoneId().equals((short) -1) ? 1 : 0) + 1);
            if (timeDiffRet == 0.0) {
                apdc.getFlashMatrix().set(gao.getGlobalMatrix());
                getEvent_CurrentTrackList().getEventTracks().setCustomSize(trackSize);
                setTimeDiffBetweenLoops(timeDiffBak);
                return timeDiff;
            }
        }
        unkFlagsActid = apdc.getUnkFlagsActid();
        flags = apdc.getFlags();
        apdc.setUnkFlags(apdc.getUnkFlags() | 0x80);
        apdc.setUnkFlagsActid(unkFlagsActid | 0x4);
        apdc.setFlags(flags | 0x2);
        if ((gao.getFlags() & 0x400000) != 0) {
            mtx = gao.getLocalMatrix();
        } else {
            mtx = gao.getGlobalMatrix();
        }

        setEvent_positionBeforeAnimPlayer(mtx.getPos());
        interpolateMagicBoxPositionAfterEndOfAnimation(gao, apGao, apdc, timeDiffRet);
        getEvent_CurrentTrackList().getEventTracks().setCustomSize(trackSize);
        setTimeDiffBetweenLoops(timeDiffBak);
        return timeDiff;
    }

    private void getEndSpeedOfMagicBox(AnimationPlayerDataContainer a1, Vector3 retVec) {
        BFEventsChunk eventsChunk;
        ArrayListWithSizeOverLoad<EventTrackElem> eventTracks;
        Vector3 currVec;
        int currTrackFramID;
        EventTrackFrameElem currTrackFrameElem;
        Vector3 prevVec;
        float ratio;
        int i, j;
        int trackNum;
        Short typeFlags;
        EventTrackFrameElem etfe;
        boolean ignoreThis;
        int trackFrameFlags;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        retVec.setX(0.0f);
        retVec.setY(0.0f);
        retVec.setZ(0.0f);

        eventsChunk = a1.getTrackData().getEventTracks();
        setEvent_CurrentTrackList(eventsChunk);

        eventTracks = eventsChunk.getEventTracks();
        i = 0;
        j = 0;
        if (a1.getTrackData().getEventTracks().getEventTracks().size() > 0) {
            do {
                ++j;
                setEvent_CurrentTrack(eventTracks.get(i));
                if (!eventTracks.get(i).getBoneId().equals((short) -1)) {
                    break;
                }
                currTrackFramID = eventTracks.get(i).getEventTrackFrameElems().size();
                currTrackFrameElem = eventTracks.get(i).getEventTrackFrameElems().get(currTrackFramID - 1);
                if (eventTracks.get(i).getEventTrackFrameElems().size() > 0) {
                    while (true) {
                        trackFrameFlags = currTrackFrameElem.getTrackFrameFlags() & 0x7C0;
                        --currTrackFramID;
                        if (trackFrameFlags == 0x80) {
                            ignoreThis = false;
                            break;
                        }
                        if (trackFrameFlags == 0) {
                            currTrackFrameElem = eventTracks.get(i).getEventTrackFrameElems().get(currTrackFramID - 1);
                            if (currTrackFramID != 0) {
                                continue;
                            }
                        }
                        ignoreThis = true;
                        break;
                    }
                    typeFlags = currTrackFrameElem.getTrackFrameTypeFlags();
                    if (!ignoreThis && (typeFlags & 0x3) != 0) {
                        if (currTrackFramID == eventTracks.get(i).getEventTrackFrameElems().size() - 1) {
                            etfe = eventTracks.get(i).getEventTrackFrameElems().get(eventTracks.get(i).getEventTrackFrameElems().indexOf(currTrackFrameElem) - 1);
                            prevVec = etfe.getPart3case1vec();
                            if (prevVec != null) {
                                currVec = currTrackFrameElem.getPart3case1vec();
                                retVec.setX(currVec.getX() - prevVec.getX());
                                retVec.setY(currVec.getY() - prevVec.getY());
                                retVec.setZ(currVec.getZ() - prevVec.getZ());

                                ratio = (((a1.getUnkFlags() >> 16)) & 0xff) / (float) (etfe.getTrackFrameLen());
                                retVec.setX(ratio * retVec.getX());
                                retVec.setX(ratio * retVec.getY());
                                retVec.setX(ratio * retVec.getZ());
                            }
                        }
                        setEvent_CurrentTrack(null);
                        return;
                    }
                }
                trackNum = a1.getTrackData().getEventTracks().getEventTracks().size();
                ++i;
            } while (j < trackNum);
        }
        setEvent_CurrentTrack(null);
    }

    private void interpolateMagicBoxPositionAfterEndOfAnimation(BFGaoChunk gao, BFGaoChunk apGao, AnimationPlayerDataContainer apdc, float ratio) {
        Matrix4x4 flashMtx;
        Matrix4x4 localMatrix;
        BFGaoChunk referenceGao;
        Vector3 globalMtxPosVec;
        Vector3 destVec = new Vector3();
        Vector3 vecTransformed = new Vector3();
        Matrix4x4 refGlobaMtxInv = new Matrix4x4();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        flashMtx = apGao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix();
        if ((apdc.getFlags() & 0x2) != 0) {
            getEndSpeedOfMagicBox(apdc, destVec);
            Vector3.transformVector(destVec, apdc.getFlashMatrix(), destVec);
            flashMtx.setM11(destVec.getX());
            flashMtx.setM12(destVec.getY());
            flashMtx.setM13(destVec.getZ());
            apdc.setFlags((apdc.getFlags() - 2) | 0x4);
        }
        if ((apdc.getFlags() & 0x4) != 0) {
            destVec.setX(0.0f);
            destVec.setY(0.0f);
            destVec.setZ(0.0f);

            if (ratio != 0.0) {
                destVec.setX(flashMtx.getM11() * ratio);
                destVec.setY(flashMtx.getM12() * ratio);
                destVec.setZ(flashMtx.getM13() * ratio);
            }
            if ((gao.getFlags() & 0x400000) != 0) {

                localMatrix = gao.getLocalMatrix();
                if (localMatrix != null) {
                    referenceGao = (BFGaoChunk) (gao.getReferencePointGAOChunkKLE().getParseHandler());
                } else {
                    referenceGao = null;
                }
                Matrix4x4.invertMatrix(refGlobaMtxInv, referenceGao.getGlobalMatrix());
                Vector3.transformVector(vecTransformed, refGlobaMtxInv, destVec);
                localMatrix.getPos().setX(getEvent_positionBeforeAnimPlayer().getX() + vecTransformed.getX());
                localMatrix.getPos().setY(getEvent_positionBeforeAnimPlayer().getY() + vecTransformed.getY());
                localMatrix.getPos().setZ(getEvent_positionBeforeAnimPlayer().getZ() + vecTransformed.getZ());
                updateFlashAfterMagicBoxTranslation(gao, vecTransformed, 0x2);
            } else {
                globalMtxPosVec = gao.getGlobalMatrix().getPos();
                globalMtxPosVec.setX(getEvent_positionBeforeAnimPlayer().getX() + destVec.getX());
                globalMtxPosVec.setY(getEvent_positionBeforeAnimPlayer().getY() + destVec.getY());
                globalMtxPosVec.setZ(getEvent_positionBeforeAnimPlayer().getZ() + destVec.getZ());
                updateFlashAfterMagicBoxTranslation(gao, destVec, 0x2);
            }
        }
    }

    private void blendMagicBox(BFGaoChunk gao, BFActionListChunk actionList, BFGaoChunk apGao, AnimationPlayerDataContainer apdc) {
        int gaoFlags;
        Matrix4x4 posMtx;
        float timeDiffBak;
        Vector3 vec = new Vector3();
        Matrix4x4 posMtxBak2 = new Matrix4x4();
        Matrix4x4 posMtxBak1 = new Matrix4x4();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        gaoFlags = gao.getFlags();
        if ((gao.getFlags() & 0x400000) != 0) {
            posMtx = gao.getLocalMatrix();
        } else {
            posMtx = gao.getGlobalMatrix();
        }
        posMtxBak1.set(posMtx);
        posMtxBak2.set(posMtxBak1);

        if ((apdc.getUnkFlagsActid() & 0x8) != 0 || (apdc.getUnkFlags() & 0x6) != 0) {
            vec.setX(posMtxBak1.getPos().getX() - getEvent_positionBeforeAnimPlayer().getX());
            vec.setY(posMtxBak1.getPos().getY() - getEvent_positionBeforeAnimPlayer().getY());
            vec.setZ(posMtxBak1.getPos().getZ() - getEvent_positionBeforeAnimPlayer().getZ());

            if ((gaoFlags & 0x2) != 0) {
                if (gaoAnimationIsTransitioning(gao)) {
                    Vector3.sumTwoVectorInPlace(gao.getCurrentAttachedAnimationPlayerDataContainerForTransition().getFlashMatrix().getPos(), vec);
                    Vector3 tmp = new Vector3(gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().getM21(), gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().getM22(), gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().getM23()); // TODO: átnézni, lehet M14 kell
                    Vector3.sumTwoVectorInPlace(tmp, vec);
                    gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().setM21(tmp.getX());
                    gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().setM22(tmp.getY());
                    gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().setM23(tmp.getZ());
                    Vector3.sumTwoVectorInPlace(gao.getCurrentAttachedAnimationPlayerDataContainer().getFlashMatrix().getPos(), vec);
                }
            }
        } else {
            timeDiffBak = getTimeDiffBetweenLoops();
            computeMagicBoxPositionForBlendAnim(gao, actionList, apGao, apdc);
            Vector3.blendVector(posMtxBak2.getPos(), posMtx.getPos(), posMtxBak1.getPos(), apdc.getTrackData().getTrackOptions().get(0).getMagicBoxBlendFactor());
            vec.setX(posMtxBak2.getPos().getX() - posMtxBak1.getPos().getX());
            vec.setY(posMtxBak2.getPos().getY() - posMtxBak1.getPos().getY());
            vec.setZ(posMtxBak2.getPos().getZ() - posMtxBak1.getPos().getZ());
            updateFlashAfterMagicBoxTranslation(gao, vec, 0x1);
            vec.setX(posMtxBak2.getPos().getX() - posMtx.getPos().getX());
            vec.setY(posMtxBak2.getPos().getY() - posMtx.getPos().getY());
            vec.setZ(posMtxBak2.getPos().getZ() - posMtx.getPos().getZ());
            updateFlashAfterMagicBoxTranslation(gao, vec, 0x2);
            posMtx.set(posMtxBak2);
            setTimeDiffBetweenLoops(timeDiffBak);
        }
    }

    private void _setGaoActionInside(BFActionListChunk actionList, int actionID, BFGaoChunk animationGao, ActionsElemTransitionStructInside2 transition,
            float elapsedTime, float totalTimeGet) {
        BFActionChunk currentAction;
        BFEventsChunk eventToSet;
        AnimationPlayerDataContainer currApdt;
        short currentActionElemFlags;
        float elapsedTimeCalcd;
        int time;
        float timeret;
        BFGaoChunk ownerGao;
        AnimationPlayerTrackData eventsCurrentData;
        BFEventsChunk currentTrackList;
        Matrix4x4 currentRefMatrix;
        float totalTime;
        float elapsedTimeToSet;
        float timeDiff;
        EventTrackElem currentTrack;
        EventTrackFrameElem currentTrackFrameElem;
        EventTrackOption currentTrackOption;
        int freq;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        currentAction = (BFActionChunk) (actionList.getActionsKLE().get(actionID).getParseHandler());
        animationGao.setAnimationPlayerCurrentAction(currentAction);
        animationGao.setAnimationPlayerProbablyActionID(actionID);
        if (currentAction != null && currentAction.getAesList().size() != 0) {
            eventToSet = (BFEventsChunk) (currentAction.getEventsChunksKLE().get(0).getParseHandler());
            if (animationGao.getCurrentAttachedAnimationPlayerDataContainer() == null) {
                animationGao.setCurrentAttachedAnimationPlayerDataContainer(new AnimationPlayerDataContainer());
            }
            currApdt = animationGao.getCurrentAttachedAnimationPlayerDataContainer();
            if (currApdt.getTrackData().getEventTracks() == null || currApdt.getTrackData().getEventTracks() != eventToSet) {
                if (!animationGao.getCurrentAttachedAnimationPlayerDataContainer().initAnimationPlayerDataContainer(eventToSet, animationGao)) {
                    animationGao.resetAnimationPlayerSpecificVars();
                    return;
                }
            }
            if (currentAction.getAnimationChunksKLE().size() > 0 && currentAction.getAnimationChunksKLE().get(0) != null && currentAction.getAnimationChunksKLE().get(0).getParseHandler() != null) {
                //TODO: change gao shape to currAction Shape
            } else {
                //TODO: change gao shape to default
            }
            animationGao.setAnimationPlayerSlots(animationGao.getAnimationPlayerSlots() | 0x1);
            animationGao.setAnimationPlayerProbablyActionIDElemID(0);
            animationGao.setAnimationPlayerActionLoopCounter(0);
            currentActionElemFlags = currentAction.getAesList().get(0).getFlags();
            if ((animationGao.getFlags() & 4) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    printAnimationDebugMessage("physics not supported.");
                }
            }
            animationGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags(animationGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() & 0xf0);
            animationGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags(animationGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() & 0x7f);
            animationGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags(animationGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() | (currentActionElemFlags & 0xf));

            freq = 60 * currentAction.getAesList().get(0).getFrequency();
            animationGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags((animationGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() & 0xff00ffff) | ((freq >> 6) << 16));
            if (transition != null) {
                if ((transition.getFlags() & 3) != 0) {
                    EventsTrackTimeStruct etts = eventsGetTrackTime(animationGao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), 0);
                    totalTime = etts.getTotalTime();

                    if ((transition.getFlags() & 1) != 0) {
                        elapsedTimeCalcd = totalTime / totalTimeGet * elapsedTime;
                    } else {
                        elapsedTimeCalcd = (1.0f - elapsedTime / totalTimeGet) * totalTime;
                    }
                    elapsedTimeToSet = elapsedTimeCalcd;
                    eventsSetTracksTime(animationGao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), elapsedTimeToSet);
                    updateFlash(animationGao, 0x1);
                }
                if (animationGao.getAnimationPlayerProbablyActionIDAnother() == animationGao.getAnimationPlayerProbablyActionID()) {
                    animationGao.setAnimationPlayerCurrentTransition(null);
                    animationGao.setAnimationPlayerProbablyActionIDAnother(-1);
                }
            }
            if (false /*TODO: ofcs is called ? */) {
                time = animationGao.getAnimationPlayerTime();
                if (time > 0) {
                    timeret = time * 0.016666668f;
                    eventsSetTracksTime(animationGao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), timeret);
                }
            }
            ownerGao = getEvent_OwnerGAO();
            eventsCurrentData = getEventsCurrentData();
            currentTrackList = getEvent_CurrentTrackList();
            currentRefMatrix = getEvent_CurrentRefMatrix();
            timeDiff = getTimeDiffBetweenLoops();
            currentTrack = getEvent_CurrentTrack();
            setTimeDiffBetweenLoops(0.0f);
            currentTrackFrameElem = getEvent_CurrentTrackFrameElem();
            currentTrackOption = getEvent_CurrentTrackOption();
            if (animationGao.getAnimationControlFlags() != null && (animationGao.getAnimationControlFlags() & 0x1) == 0) {
                animationMainHandler(animationGao);
            }
            setEventsCurrentData(eventsCurrentData);
            setEvent_CurrentRefMatrix(currentRefMatrix);
            setEvent_OwnerGAO(ownerGao);
            setEvent_CurrentTrack(currentTrack);
            setEvent_CurrentTrackList(currentTrackList);
            setEvent_CurrentTrackOption(currentTrackOption);
            setEvent_CurrentTrackFrameElem(currentTrackFrameElem);
            setTimeDiffBetweenLoops(timeDiff);
        } else {
            animationGao.setAnimationPlayerSlots(0);
        }
    }

    public void setGaoAction(BFGaoChunk gao, int actionID, boolean a4) {
        BFGaoChunk aniGao;
        int actionID2;
        AnimationPlayerDataContainer currApdc;
        ActionsElemTransitionStructInside2 currActionData;
        ActionsElemTransitionStructInside2 transition;
        ActionsElemTransitionStructInside2 ttransition;
        AnimationPlayerDataContainer currTransitionApdc;
        ActionsElemTransitionStructInside2 tttransition;
        BFActionListChunk aniGaoActions;
        float elapsedTime = 0.0f;
        float totalTime = 0.0f;
        boolean currActionDataWasnotNull;
        int currTansitionApdcUnkFlags;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("Gao: " + gao.getGaoName() + ", actionID: " + actionID);
        }

        aniGao = gao;
        actionID2 = actionID;
        if (actionID >= 0) {
            if (!a4 && actionID == aniGao.getAnimationPlayerProbablyActionID()) {
                currApdc = aniGao.getCurrentAttachedAnimationPlayerDataContainer();
                if (currApdc != null) {
                    if (((~(currApdc.getUnkFlags() >> 7)) & 0x1) != 0) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            printAnimationDebugMessage("ret 1");
                        }
                        return;
                    }
                }
            }
        } else {
            actionID2 = actionID & 0x7FFFFFFF;
            actionID &= 0x7FFFFFFF;
        }
        if (getEventsRunning()) {
            aniGao.setAnimationPlayerActionShouldInit(-1);
            gao.setAnimationPlayerElapsedTimeSetInSetAction(getEvent_elapsedTime());
            gao.setAnimationPlayerProbablyActionID(actionID2);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                printAnimationDebugMessage("events is already running, returning.");
            }
            return;
        }
        aniGaoActions = (BFActionListChunk) (aniGao.getActionListChunkKLE().getParseHandler());
        if (actionID2 >= 0) {
            if (actionID2 < aniGaoActions.getActionsKLE().size()
                    && (actionID2 == 0 || aniGaoActions.getActionsKLE().get(actionID2) != null)) {
                currActionData = null;
                currActionDataWasnotNull = false;
                if (aniGao.getAnimationPlayerCurrentTransition() != null) {
                    currActionData = aniGao.getAnimationPlayerCurrentTransition();
                    currActionDataWasnotNull = true;
                    if (aniGao.getAnimationPlayerProbablyActionIDAnother() == actionID2) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            printAnimationDebugMessage("ret 2");
                        }
                        return;
                    }
                    aniGao.setAnimationPlayerCurrentTransition(null);
                }
                aniGao.setAnimationPlayerSlots(aniGao.getAnimationPlayerSlots() & ~0x2);

                if (aniGao.getAnimationPlayerProbablyActionID() == -1) {
                    tttransition = null;
                    ttransition = tttransition;
                    _setGaoActionInside(aniGaoActions, actionID2, aniGao, ttransition, elapsedTime, totalTime);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        printAnimationDebugMessage("ret 3");
                    }
                    return;
                }
                transition = eventsFindActionTransition(aniGaoActions, aniGao.getAnimationPlayerProbablyActionID(), aniGao.getAnimationPlayerProbablyActionIDElemID(), actionID2);

                ttransition = transition;
                tttransition = transition;
                boolean ignoreThis = false;
                if (transition != null) {
                    if (aniGao.getAnimationPlayerProbablyActionID() != actionID2 || (transition.getFlags() & 0x10) == 0) {
                        ignoreThis = true;
                    } else {
                        tttransition = null;
                        ttransition = null;
                    }
                }

                if (!ignoreThis) {
                    if (!currActionDataWasnotNull || currActionData != null && ((currActionData.getFlags() & 0x40) != 0
                            || actionID2 != aniGao.getAnimationPlayerProbablyActionIDAnother()
                            && aniGao.getAnimationPlayerProbablyActionIDAnother() != -1)) {
                        tttransition = eventsFindActionTransition(aniGaoActions, actionID2, 0, actionID2);

                        if (tttransition == null || (tttransition.getFlags() & 0x10) == 0 || (aniGao.getAnimationPlayerProbablyActionID() == actionID2 && (tttransition.getFlags() & 0x40) == 0)) {
                            tttransition = null;
                            ttransition = tttransition;
                        } else {
                            ttransition = tttransition;
                        }
                    }
                    if (ttransition == null) {
                        _setGaoActionInside(aniGaoActions, actionID2, aniGao, ttransition, elapsedTime, totalTime);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            printAnimationDebugMessage("ret 4");
                        }
                        return;
                    }
                }
                if ((ttransition.getFlags() & 3) != 0) {
                    EventsTrackTimeStruct etts = eventsGetTrackTime(aniGao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), 0);
                    elapsedTime = etts.getElapsedTime();
                    totalTime = etts.getTotalTime();
                }
                if (ttransition.getLength() != 0 && aniGao.getCurrentAttachedAnimationPlayerDataContainer() != null) {
                    if ((aniGao.getAnimationPlayerSlots() & 0x2) == 0
                            && aniGao.getCurrentAttachedAnimationPlayerDataContainerForTransition() == null) {
                        aniGao.setCurrentAttachedAnimationPlayerDataContainerForTransition(new AnimationPlayerDataContainer());
                    }
                    currTransitionApdc = aniGao.getCurrentAttachedAnimationPlayerDataContainerForTransition();
                    currApdc = aniGao.getCurrentAttachedAnimationPlayerDataContainer();
                    aniGao.setAnimationPlayerSlots(aniGao.getAnimationPlayerSlots() | 0x2);
                    AnimationPlayerDataContainer.copy(currTransitionApdc, currApdc);
                    aniGao.getCurrentAttachedAnimationPlayerDataContainer().setTrackData(currTransitionApdc.getTrackData());
                    aniGao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData().setEventTracks(null);
                    if ((gao.getFlags() & 0x4000000) != 0) {
                        // TODO: fixme!
                        if (currTransitionApdc.getFlashMatrix() == null) {
                            currTransitionApdc.setFlashMatrix(new Matrix4x4());
                        }
                        if ((gao.getFlags() & 0x10000) == 0) {
                            currTransitionApdc.getFlashMatrix().set(gao.getGlobalMatrixSecond());
                        } else {
                            currTransitionApdc.getFlashMatrix().set(gao.getGlobalMatrixThird());
                        }
                    }

                    currTransitionApdc.setActionLength(tttransition.getLength());
                    currTransitionApdc.setActionID(aniGao.getAnimationPlayerProbablyActionID());
                    currTransitionApdc.setActionIDDataElemID(aniGao.getAnimationPlayerProbablyActionIDElemID());
                    currTansitionApdcUnkFlags = currTransitionApdc.getUnkFlags();
                    currTransitionApdc.setUnkFlags(tttransition.getFlags());
                    currTransitionApdc.setStartTime(0);
                    currTransitionApdc.setFlags(0);
                    if ((~(currTansitionApdcUnkFlags >> 7) & 1) == 0
                            || (currTransitionApdc.getTrackData().getTrackOptions().get(0).getActElemNum() == currTransitionApdc.getTrackData().getEventTracks().getEventTracks().size() - 1)
                            && currTransitionApdc.getTrackData().getEventTracks().getEventTracks().get(0).getEventTrackFrameElems().get(currTransitionApdc.getTrackData().getTrackOptions().get(0).getActElemNum()).getTrackFrameLen() * 0.016666668f == currTransitionApdc.getTrackData().getTrackOptions().get(0).getActElemTimeElapsed()) {
                        currTransitionApdc.setUnkFlagsActid(currTransitionApdc.getUnkFlagsActid() | 0x4);
                        currTransitionApdc.setFlags(0x2);
                    }
                    ttransition = tttransition;
                    actionID2 = actionID;
                }
                aniGao.setAnimationPlayerProbablyActionIDAnother(actionID2);
                aniGao.setAnimationPlayerCurrentTransition(tttransition);
                actionID2 = ttransition.getActionID();
                _setGaoActionInside(aniGaoActions, actionID2, aniGao, ttransition, elapsedTime, totalTime);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    printAnimationDebugMessage("ret 5");
                }
                return;
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                printAnimationDebugMessage("ret i 1");
            }
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("ret i 2");
        }
    }

    private void changeGaoActionElements(BFGaoChunk gao, BFGaoChunk apGao) {
        BFActionChunk action;
        int actionElemID;
        int actidDataElemHplus1;
        int actionElemNum;
        int currActionElemID;
        int actionID;
        AnimationPlayerDataContainer apdc;
        int actionElemFlag;
        int freq;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        action = apGao.getAnimationPlayerCurrentAction();
        if (action == null) {
            return;
        }
        actionElemID = apGao.getAnimationPlayerProbablyActionIDElemID();
        actidDataElemHplus1 = apGao.getAnimationPlayerActionLoopCounter() + 1;
        apGao.setAnimationPlayerActionLoopCounter(actidDataElemHplus1);
        if (actidDataElemHplus1 < action.getAesList().get(actionElemID).getLoopNumAsInt()) {
            return;
        }
        apGao.setAnimationPlayerActionLoopCounter(0);

        actionElemNum = action.getAesList().size();
        currActionElemID = actionElemID + 1;
        if (actionElemID + 1 >= actionElemNum) {
            if (apGao.getAnimationPlayerCurrentTransition() != null) {
                actionID = apGao.getAnimationPlayerProbablyActionIDAnother();
                apGao.setAnimationPlayerProbablyActionIDAnother(-1);
                setGaoAction(gao, actionID, false);
                return;
            }
            currActionElemID = action.getLoopStartElem();
            if (currActionElemID == -1) {
                apGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags(apGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() | 0x80);
                return;
            }
            if (currActionElemID >= actionElemNum) {
                currActionElemID = actionElemNum - 1;
            }
        }
        if (currActionElemID == -1 || action.getAesList() == null || action.getAesList().size() == 0) {
            apGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags(apGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() | 0x80);
            return;
        }
        apdc = apGao.getCurrentAttachedAnimationPlayerDataContainer();
        apGao.setAnimationPlayerProbablyActionIDElemID(currActionElemID);
        if (!apdc.initAnimationPlayerDataContainer((BFEventsChunk) (action.getEventsChunksKLE().get(currActionElemID).getParseHandler()), apGao)) {
            apGao.resetAnimationPlayerSpecificVars();
            return;
        }
        //TODO: change gao shape
        apGao.setAnimationPlayerActionLoopCounter(0);
        actionElemFlag = action.getAesList().get(currActionElemID).getFlags();
        if ((gao.getFlags() & 4) != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                printAnimationDebugMessage("physics not supported.");
            }
        }
        apGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags(apGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() & 0xf0);
        apGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags(apGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() & 0x7f);
        apGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags(apGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() | (actionElemFlag & 0xf));

        freq = 60 * action.getAesList().get(currActionElemID).getFrequency();
        apGao.getCurrentAttachedAnimationPlayerDataContainer().setUnkFlags((apGao.getCurrentAttachedAnimationPlayerDataContainer().getUnkFlags() & 0xff00ffff) | ((freq >> 6) << 16));
    }

    private void playGameObjectAnimation(BFGaoChunk gao, BFActionListChunk actionList, BFGaoChunk apGao, float time, Vector3 physicsVec) {
        Vector3 mtxPos;
        float mtxPosX;
        float mtxPosY;
        float mtxPosZ;
        AnimationPlayerDataContainer eventsptr;
        AnimationPlayerDataContainer apdcTransition;
        float transitionActionLen;
        float transitionTime;
        boolean hasLocalMtx;
        Matrix4x4 refMtx;
        Vector3 refMtxPos;
        Matrix4x4 refMtx2;
        Matrix4x4 mtx;
        float interpFactor;
        float timeDiffBak;
        Vector3 ret = new Vector3();
        Vector3 vec = new Vector3();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("");
        }

        if ((gao.getFlags() & 0x400000) != 0) {
            mtxPos = gao.getLocalMatrix().getPos();
        } else {
            mtxPos = gao.getGlobalMatrix().getPos();
        }
        mtxPosX = mtxPos.getX();
        mtxPosY = mtxPos.getY();
        mtxPosZ = mtxPos.getZ();
        getEvent_positionBeforeAnimPlayer().setX(mtxPosX);
        getEvent_positionBeforeAnimPlayer().setY(mtxPosY);
        getEvent_positionBeforeAnimPlayer().setZ(mtxPosZ);
        timeDiffBak = getTimeDiffBetweenLoops();
        setTimeDiffBetweenLoops(time);
        eventsptr = apGao.getCurrentAttachedAnimationPlayerDataContainer();
        vec.setX(mtxPosX);
        vec.setY(mtxPosY);
        vec.setZ(mtxPosZ);
        if (eventsptr != null) {
            if ((~(eventsptr.getUnkFlags() >> 7) & 1) != 0 && physicsVec != null && (eventsptr.getUnkFlags() & 0xF) == 2) {
                Vector3.multiplyVectorWithScalar(ret, physicsVec, time);
                updateFrequency(eventsptr, ret, physicsVec);
            }
            if ((~(eventsptr.getUnkFlags() >> 7) & 1) != 0 || apGao.getAnimationPlayerActionShouldInit() != 0) {
                playAnimation(gao, apGao, eventsptr);
            }
            if ((gao.getFlags() & 0x2) != 0) {

                if ((gao.getAnimationPlayerSlots() & 0x2) != 0) {
                    apdcTransition = apGao.getCurrentAttachedAnimationPlayerDataContainerForTransition();
                    if ((apdcTransition.getFlags() & 0x1) != 0) {
                        setTimeDiffBetweenLoops(apdcTransition.getTrackData().getTrackOptions().get(1).getMagicBoxBlendFactor());
                    }
                    transitionActionLen = (apdcTransition.getActionLength() + 1) * 0.016666668f;
                    transitionTime = apdcTransition.getStartTime() * 0.00006510417f + getTimeDiffBetweenLoops();
                    if (transitionTime >= transitionActionLen) {
                        gao.setAnimationPlayerSlots(gao.getAnimationPlayerSlots() & ~0x2);
                    } else {
                        apdcTransition.getTrackData().getTrackOptions().get(0).setMagicBoxBlendFactor(transitionTime / transitionActionLen);
                        apdcTransition.setStartTime((int) (transitionTime * 15360.0f));
                        if (physicsVec != null && (apdcTransition.getUnkFlags() & 0xF) == 2) {
                            Vector3.multiplyVectorWithScalar(ret, physicsVec, getTimeDiffBetweenLoops());
                            updateFrequency(apdcTransition, ret, physicsVec);
                        }
                        blendMagicBox(gao, actionList, apGao, apdcTransition);
                    }
                    mtxPosZ = vec.getZ();
                }
            }
            if ((gao.getFlags() & 0x400000) != 0) {
                RenderUtils.computeObjectGlobalMatrixWithReferencesGlobalMatrix(gao);
            }
            hasLocalMtx = (gao.getFlags() & 0x400000) != 0;
            if (hasLocalMtx) {
                refMtx = gao.getLocalMatrix();
            } else {
                refMtx = gao.getGlobalMatrix();
            }
            refMtxPos = refMtx.getPos();
            ret.setX(refMtxPos.getX() - vec.getX());
            ret.setY(refMtxPos.getY() - vec.getY());
            ret.setY(refMtxPos.getZ() - vec.getZ());

            if ((eventsptr.getUnkFlags() & 0xF) == 6) {
                if (hasLocalMtx) {
                    refMtx2 = gao.getLocalMatrix();
                } else {
                    refMtx2 = gao.getGlobalMatrix();
                }
                refMtx2.getPos().setX(mtxPosX);
                refMtx2.getPos().setY(mtxPosY);
                refMtx2.getPos().setZ(mtxPosZ);
                vec.setX(-ret.getX());
                vec.setY(-ret.getY());
                vec.setZ(-ret.getZ());
                updateFlashAfterMagicBoxTranslation(gao, vec, 0x3);
            }
            if ((eventsptr.getUnkFlags() & 0xF) == 0 && physicsVec != null && time != 0.0f) {
                if ((gao.getFlags() & 0x400000) != 0) {
                    RenderUtils.computeObjectGlobalMatrixWithReferencesGlobalMatrix(gao);
                }
                if ((gao.getFlags() & 0x400000) != 0) {
                    mtx = gao.getLocalMatrix();
                } else {
                    mtx = gao.getGlobalMatrix();
                }
                mtx.getPos().setX(mtxPosX);
                mtx.getPos().setY(mtxPosY);
                mtx.getPos().setZ(mtxPosZ);
                vec.setX(-ret.getX());
                vec.setY(-ret.getY());
                vec.setZ(-ret.getZ());
                updateFlashAfterMagicBoxTranslation(gao, vec, 0x3);
                if ((gao.getFlags() & 0x400000) != 0) {
                    vec.setX(ret.getX());
                    vec.setY(ret.getY());
                    vec.setZ(ret.getZ());
                    Vector3.transformVector(ret, ((BFGaoChunk) (gao.getReferencePointGAOChunkKLE().getParseHandler())).getGlobalMatrix(), vec);
                }
                interpFactor = 1.0f / time;
                Vector3.multiplyVectorWithScalar(physicsVec, ret, interpFactor);

                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    printAnimationDebugMessage("physics not supported.");
                }
            }
            setTimeDiffBetweenLoops(timeDiffBak);
        }
    }

    public void animationMainHandler(BFGaoChunk gao) {
        Vector3 phys;

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            printAnimationDebugMessage("gao: " + gao.getGaoName());
        }

        if ((gao.getFlags() & 0x4) != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                printAnimationDebugMessage("physics not supported.");
            }
            phys = null;
        } else {
            phys = null;
        }

        if ((gao.getVisualFlags() & 2) != 0 && (gao.getVisualFlags() & 4) == 0) {
            if (gao.getCurrentAttachedAnimationPlayerDataContainer() != null) {
                if (gao.getAnimationPlayerFlag() == 0) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        printAnimationDebugMessage("Slots set!");
                    }
                    EventsTrackTimeStruct ets = eventsGetTrackTime(gao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), 0);
                    eventsSetTracksTime(gao.getCurrentAttachedAnimationPlayerDataContainer().getTrackData(), ets.getElapsedTime());

                    if (gao.getAnimationPlayerSlots() != 1) {
                        gao.setAnimationPlayerSlots(1);
                    }
                    gao.setAnimationPlayerFlag(1);
                }
            }
        }
        playGameObjectAnimation(gao, (BFActionListChunk) (gao.getActionListChunkKLE().getParseHandler()), gao, getTimeDiffBetweenLoops(), phys);
    }

    public static void resolveEventTracks(BFDataFileClass bfdc, BFEventsChunk ec) {
        int i, j;
        EventTrackElem ete;

        if (ec == null || ec.getEventTracks() == null) {
            return;
        }

        if ((ec.getFlags() & 0x2) == 0) {
            ec.setFlags((short) (ec.getFlags() | 0x2)); // set resolved
            for (i = 0; i < ec.getEventTracks().size(); i++) {
                if ((ete = ec.getEventTracks().get(i)) == null) {
                    continue;
                }
                if ((ete.getEventTrackFlags() & 0x10) != 0) {
                    for (j = 0; j < ete.getTrackFrameNum(); j++) {
                        if (ete.getEventTrackFrameElems() == null || ete.getEventTrackFrameElems().get(j) == null) {
                            continue;
                        }
                        if ((ete.getEventTrackFrameElems().get(j).getTrackFrameFlags() & 0x20) != 0) {
                            System.out.format("TODO: TrackElemLinkToDataOffset (trackFrameFlags has 0x20): %d (%x) unimplemented.\n", ete.getEventTrackFrameElems().get(j).getTrackElemLinkToDataOffset(), ete.getEventTrackFrameElems().get(j).getTrackElemLinkToDataOffset());
                            // TODO: int should be casted to two shorts
                        } else if ((ete.getEventTrackFrameElems().get(j).getTrackFrameFlags() & 0x7C0) == 0x40)// OFC
                        {
                            Scripting.scriptingResolveFunctionsInEventTrackFrame(bfdc, ete);
                        }
                    }
                }
            }
        }
    }

}
