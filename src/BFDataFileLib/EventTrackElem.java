/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import Common.Logger;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class EventTrackElem extends Logger {

    private Short eventTrackFlags = null;
    private Short boneID = null;
    private Integer trackFrameNum = null;
    private Integer innerUnk1 = null;
    private Integer innerGaoKey = null;
    private BFKeyListElem innerGaoKLE = null;
    private String innerUnkS1 = null;
    private Byte innerUnkB1 = null;
    private Integer trackSize = null;
    private ArrayList<EventTrackFrameElem> eventTrackFrameElems = new ArrayList<>();

    public Integer getTrackSize() {
        return trackSize;
    }

    public void setTrackSize(Integer trackSize) {
        this.trackSize = trackSize;
    }

    public Integer getInnerUnk1() {
        return innerUnk1;
    }

    public void setInnerUnk1(Integer innerUnk1) {
        this.innerUnk1 = innerUnk1;
    }

    public Integer getInnerGaoKey() {
        return innerGaoKey;
    }

    public void setInnerGaoKey(Integer innerGaoKey) {
        this.innerGaoKey = innerGaoKey;
    }

    public BFKeyListElem getInnerGaoKLE() {
        return innerGaoKLE;
    }

    public void setInnerGaoKLE(BFKeyListElem innerGaoKLE) {
        this.innerGaoKLE = innerGaoKLE;
    }

    public String getInnerUnkS1() {
        return innerUnkS1;
    }

    public void setInnerUnkS1(String innerUnkS1) {
        this.innerUnkS1 = innerUnkS1;
    }

    public Byte getInnerUnkB1() {
        return innerUnkB1;
    }

    public void setInnerUnkB1(Byte innerUnkB1) {
        this.innerUnkB1 = innerUnkB1;
    }

    public Short getEventTrackFlags() {
        return eventTrackFlags;
    }

    public void setEventTrackFlags(Short eventTrackFlags) {
        this.eventTrackFlags = eventTrackFlags;
    }

    public Short getBoneId() {
        return boneID;
    }

    public void setBoneID(Short boneID) {
        this.boneID = boneID;
    }

    public Integer getTrackFrameNum() {
        return trackFrameNum;
    }

    public void setTrackFrameNum(Integer trackFrameNum) {
        this.trackFrameNum = trackFrameNum;
    }

    public ArrayList<EventTrackFrameElem> getEventTrackFrameElems() {
        return eventTrackFrameElems;
    }

    public void setEventTrackFrameElems(ArrayList<EventTrackFrameElem> eventTrackFrameElems) {
        this.eventTrackFrameElems = eventTrackFrameElems;
    }

    public EventTrackElem() {
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(Boolean shortMode) {
        StringBuilder sb = new StringBuilder();
        if (!shortMode) {
            sb.append("Event Track: ");
        }
        if (innerGaoKey != null && innerGaoKey != 0 && innerGaoKey != -1
                && innerGaoKLE != null && innerGaoKLE.getParseHandler() != null) {
            if (!shortMode) {
                sb.append("GAO: ");
            }
            sb.append(((BFGaoChunk) (innerGaoKLE.getParseHandler())).getGaoName());
            if (!shortMode) {
                sb.append(" -- ");
            } else {
                sb.append(" ");
            }
        }
        if (!shortMode && innerUnk1 != null) {
            sb.append("Inner UNK1: ");
            sb.append(innerUnk1);
            sb.append(" ");
        }
        if (!shortMode) {
            sb.append(String.format("Flags: %x, TrackSize: %d (%x), BoneID: %d (%x), ", eventTrackFlags, trackSize, trackSize, boneID, boneID));
            sb.append(String.format("trackFrameNum: %d", trackFrameNum));
        } else {
            sb.append(String.format("BID: %d, F: %x", boneID, eventTrackFlags));
        }
        return sb.toString();
    }

}
