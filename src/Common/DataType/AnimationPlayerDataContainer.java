/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import BFDataFileLib.BFChunkParsers.Map.BFEventsChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;

/**
 *
 * @author andra
 */
public class AnimationPlayerDataContainer { // eventsptr_orig

    private AnimationPlayerTrackData trackData = new AnimationPlayerTrackData();
    private Matrix4x4 flashMatrix;
    private int unkFlags;
    private int unkFlagsActid;
    private int flags;
    private int actionID;
    private int actionIDDataElemID;
    private int actionLength;
    private int startTime;

    public static void copy(AnimationPlayerDataContainer dest, AnimationPlayerDataContainer src) {
        dest.setTrackData(src.getTrackData());
        if (dest.getFlashMatrix() != null) // TODO: hack, implement flash matrix
        {
            dest.getFlashMatrix().set(src.getFlashMatrix());
        }
        dest.setUnkFlags(src.getUnkFlags());
        dest.setUnkFlagsActid(src.getUnkFlagsActid());
        dest.setFlags(src.getFlags());
        dest.setActionID(src.getActionID());
        dest.setActionIDDataElemID(src.getActionIDDataElemID());
        dest.setActionLength(src.getActionLength());
        dest.setStartTime(src.getStartTime());
    }

    public int getActionLength() {
        return actionLength;
    }

    public void setActionLength(int actionLength) {
        this.actionLength = actionLength;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getActionID() {
        return actionID;
    }

    public void setActionID(int actionID) {
        this.actionID = actionID;
    }

    public int getActionIDDataElemID() {
        return actionIDDataElemID;
    }

    public void setActionIDDataElemID(int actionIDDataElemID) {
        this.actionIDDataElemID = actionIDDataElemID;
    }

    public int getUnkFlagsActid() {
        return unkFlagsActid;
    }

    public void setUnkFlagsActid(int unkFlagsActid) {
        this.unkFlagsActid = unkFlagsActid;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getUnkFlags() {
        return unkFlags;
    }

    public void setUnkFlags(int unkFlags) {
        this.unkFlags = unkFlags;
    }

    public Matrix4x4 getFlashMatrix() {
        return flashMatrix;
    }

    public void setFlashMatrix(Matrix4x4 flashMatrix) {
        this.flashMatrix = flashMatrix;
    }

    public AnimationPlayerTrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(AnimationPlayerTrackData trackData) {
        this.trackData = trackData;
    }

    public boolean initAnimationPlayerDataContainer(BFEventsChunk eve, BFGaoChunk gaoToAttach) {
        int i = 0;
        EventTrackOption eto;
        if (eve == null || eve.getEventTracks() == null) { // check: no event track data data (eg in alpha sections model: action 72004df8, I think it is shared with Double H [it is in main bin 0xff0084df])
            System.out.println("BUG: events chunk or event track data is not found.");
            return false;
        }
        getTrackData().getTrackOptions().clear(); // init
        getTrackData().setEventTracks(eve); // init
        for (i = 0; i < eve.getEventTracks().size(); i++) {
            eve.getEventTracks().get(i).setEventTrackFlags((short) (eve.getEventTracks().get(i).getEventTrackFlags() | 0x3));
            eto = new EventTrackOption();
            getTrackData().getTrackOptions().add(eto);
            eto.setFlag((short) (eto.getFlag() | 0x1));
            if (i == 0) {
                if (eve.getEventTracks() != null && eve.getEventTracks().get(0) != null && eve.getEventTracks().get(0).getEventTrackFrameElems() != null
                        && eve.getEventTracks().get(0).getEventTrackFrameElems().get(0) != null) {
                    eve.getEventTracks().get(0).getEventTrackFrameElems().get(0).setTrackFrameFlags((short) (eve.getEventTracks().get(0).getEventTrackFrameElems().get(0).getTrackFrameFlags() | 0x4));
                }
            }
        }
        if (gaoToAttach != null) {
            gaoToAttach.setCurrentAttachedAnimationPlayerDataContainer(this);
        }
        return true;
    }
}
