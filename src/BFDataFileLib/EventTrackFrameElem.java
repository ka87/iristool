/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

import Common.DataType.Quaternion;
import Common.DataType.EventsCompressedQuaternion;
import Common.DataType.EventsCompressedVector;
import Common.DataType.Matrix4x4;
import Common.DataType.OFCProgramCodeElem;
import Common.DataType.Vector3;
import Common.IrisToolDefines;
import Common.Logger;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class EventTrackFrameElem extends Logger {

    private Integer trackFrameNum = null;
    private Byte trackFrameLenByte = null;
    private Short trackFrameLenShort = null;
    private Short trackFrameFlags = null;
    private Integer trackElemLinkToDataOffset = null;
    private Integer programCodeElemNum = null;
    private ArrayList<OFCProgramCodeElem> programCode = new ArrayList<>();
    private Short numCalculated = null;
    private Short trackFrameTypeFlags = null;
    private EventsCompressedQuaternion part1ECQ = null;
    private EventsCompressedVector part1ECV = null;
    private EventsCompressedVector part2ECVCase4000 = null;
    private EventsCompressedVector part2ECVCase8000 = null;
    private EventsCompressedVector part2ECVCase2000 = null;
    private EventsCompressedQuaternion part2ECQCase1000 = null;
    private EventsCompressedQuaternion part2ECQCase800 = null;
    private Matrix4x4 part3case0matrix = null;
    private Quaternion part3case0quaternion = null;
    private Vector3 part3case3vec = null;
    private Vector3 part3case2vec = null;
    private Vector3 part3case1vec = null;
    private Integer timedEventFrameNum = null;
    private ArrayList<Integer> timedEventFrameElems = new ArrayList<>();
    private Vector3 uncompressedVector = null;
    private Quaternion uncompressedQuaternion = null;

    public static final int ETF_TYPE_TRACK_FRAME_FLAGS = 0;
    public static final int ETF_TYPE_TRACK_FRAME_INTERPOLATION_KEY_TYPE_FLAGS = 1;

    public boolean isCurrentTrackGotoNode() {
        return trackFrameFlags != null && (trackFrameFlags & 0x200) != 0;
    }

    public boolean isAllTrackGotoNode() {
        return trackFrameFlags != null && (trackFrameFlags & 0x400) != 0;
    }

    public boolean isGotoLabel() {
        return trackFrameFlags != null && (trackFrameFlags & 0xF000) != 0
                && (trackFrameFlags & 0x400) == 0
                && (trackFrameFlags & 0x200) == 0;
    }

    /**
     * Can be used for querying GotoNode and Destination Label ID
     *
     * @return label id
     */
    public Byte getGotoLabelID() {
        if (trackFrameFlags == null) {
            return null;
        }
        return (byte) ((trackFrameFlags & 0xF000) >> 12);
    }

    public EventsCompressedQuaternion getPart1ECQ() {
        return part1ECQ;
    }

    public void setPart1ECQ(EventsCompressedQuaternion part1ECQ) {
        this.part1ECQ = part1ECQ;
    }

    public EventsCompressedVector getPart1Eus2() {
        return part1ECV;
    }

    public void setPart1ECV(EventsCompressedVector part1ECV) {
        this.part1ECV = part1ECV;
    }

    public EventsCompressedVector getPart2ECVCase4000() {
        return part2ECVCase4000;
    }

    public void setPart2ECVCase4000(EventsCompressedVector part2ECVCase4000) {
        this.part2ECVCase4000 = part2ECVCase4000;
    }

    public EventsCompressedVector getPart2ECVCase8000() {
        return part2ECVCase8000;
    }

    public void setPart2ECVCase8000(EventsCompressedVector part2ECVCase8000) {
        this.part2ECVCase8000 = part2ECVCase8000;
    }

    public EventsCompressedVector getPart2ECVCase2000() {
        return part2ECVCase2000;
    }

    public void setPart2ECVCase2000(EventsCompressedVector part2ECVCase2000) {
        this.part2ECVCase2000 = part2ECVCase2000;
    }

    public EventsCompressedQuaternion getPart2ECQCase1000() {
        return part2ECQCase1000;
    }

    public void setPart2ECQCase1000(EventsCompressedQuaternion part2ECQCase1000) {
        this.part2ECQCase1000 = part2ECQCase1000;
    }

    public EventsCompressedQuaternion getPart2ECQCase800() {
        return part2ECQCase800;
    }

    public void setPart2ECQCase800(EventsCompressedQuaternion part2ECQCase800) {
        this.part2ECQCase800 = part2ECQCase800;
    }

    public Matrix4x4 getPart3case0matrix() {
        return part3case0matrix;
    }

    public void setPart3case0matrix(Matrix4x4 part3case0matrix) {
        this.part3case0matrix = part3case0matrix;
    }

    public Quaternion getPart3case0quaternion() {
        return part3case0quaternion;
    }

    public void setPart3case0quaternion(Quaternion part3case0quaternion) {
        this.part3case0quaternion = part3case0quaternion;
    }

    public Vector3 getPart3case3vec() {
        return part3case3vec;
    }

    public void setPart3case3vec(Vector3 part3case3vec) {
        this.part3case3vec = part3case3vec;
    }

    public Vector3 getPart3case2vec() {
        return part3case2vec;
    }

    public void setPart3case2vec(Vector3 part3case2vec) {
        this.part3case2vec = part3case2vec;
    }

    public Vector3 getPart3case1vec() {
        return part3case1vec;
    }

    public void setPart3case1vec(Vector3 part3case1vec) {
        this.part3case1vec = part3case1vec;
    }

    public Integer getTimedEventFrameNum() {
        return timedEventFrameNum;
    }

    public void setTimedEventFrameNum(Integer timedEventFrameNum) {
        this.timedEventFrameNum = timedEventFrameNum;
    }

    public ArrayList<Integer> getTimedEventFrameElems() {
        return timedEventFrameElems;
    }

    public void setTimedEventFrameElems(ArrayList<Integer> timedEventFrameElems) {
        this.timedEventFrameElems = timedEventFrameElems;
    }

    public Short getTrackFrameTypeFlags() {
        return trackFrameTypeFlags;
    }

    public void setTrackFrameTypeFlags(Short trackFrameTypeFlags) {
        this.trackFrameTypeFlags = trackFrameTypeFlags;
    }

    public Short getNumCalculated() {
        return numCalculated;
    }

    public void setNumCalculated(Short numCalculated) {
        this.numCalculated = numCalculated;
    }

    public Integer getProgramCodeElemNum() {
        return programCodeElemNum;
    }

    public void setProgramCodeElemNum(Integer programCodeElemNum) {
        this.programCodeElemNum = programCodeElemNum;
    }

    public ArrayList<OFCProgramCodeElem> getProgramCode() {
        return programCode;
    }

    public void setProgramCode(ArrayList<OFCProgramCodeElem> programCode) {
        this.programCode = programCode;
    }

    public Integer getTrackFrameNum() {
        return trackFrameNum;
    }

    public void setTrackFrameNum(Integer trackFrameNum) {
        this.trackFrameNum = trackFrameNum;
    }

    public Byte getTrackFrameLenByte() {
        return trackFrameLenByte;
    }

    public Integer getTrackFrameLenByteAsInt() {
        return Byte.toUnsignedInt(trackFrameLenByte);
    }

    public void setTrackFrameLenByte(Byte unkTrackFrameByte) {
        this.trackFrameLenByte = unkTrackFrameByte;
    }

    public Short getTrackFrameLenShort() {
        return trackFrameLenShort;
    }

    public Integer getTrackFrameLenShortAsInt() {
        return Short.toUnsignedInt(trackFrameLenShort);
    }

    public void setTrackFrameLenShort(Short unkTrackFrameShort) {
        this.trackFrameLenShort = unkTrackFrameShort;
    }

    public Short getTrackFrameFlags() {
        return trackFrameFlags;
    }

    public void setTrackFrameFlags(Short trackFrameFlags) {
        this.trackFrameFlags = trackFrameFlags;
    }

    public Integer getTrackElemLinkToDataOffset() {
        return trackElemLinkToDataOffset;
    }

    public void setTrackElemLinkToDataOffset(Integer trackElemLinkToDataOffset) {
        this.trackElemLinkToDataOffset = trackElemLinkToDataOffset;
    }

    public int getTrackFrameLen() {
        if (trackFrameLenByte != null) {
            return Byte.toUnsignedInt(trackFrameLenByte);
        }
        if (trackFrameLenShort != null) {
            return Short.toUnsignedInt(trackFrameLenShort);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("BUG! trackFrameLenByte & trackFrameLenShort is null.");
        }
        return 0;
    }

    public Vector3 getUncompressedVector() {
        return uncompressedVector;
    }

    public void setUncompressedVector(Vector3 uncompressedVector) {
        this.uncompressedVector = uncompressedVector;
    }

    public Quaternion getUncompressedQuaternion() {
        return uncompressedQuaternion;
    }

    public void setUncompressedQuaternion(Quaternion uncompressedQuaternion) {
        this.uncompressedQuaternion = uncompressedQuaternion;
    }

    public String getFullInfoText() {
        StringBuilder sb = new StringBuilder();
        if (trackFrameNum != null) {
            sb.append(String.format("trackFrameNum: %d\n", trackFrameNum));
        };
        if (trackFrameLenByte != null) {
            sb.append(String.format("trackFrameLen: %d (stored as byte)\n", Byte.toUnsignedInt(trackFrameLenByte)));
        }
        if (trackFrameLenShort != null) {
            sb.append(String.format("trackFrameLen: %d (stored as short)\n", Short.toUnsignedInt(trackFrameLenShort)));
        }
        if (trackFrameFlags != null) {
            sb.append(String.format("shortTrackFrameFlags: 0x%x %s\n", trackFrameFlags, dumpEventFlags(EventTrackFrameElem.ETF_TYPE_TRACK_FRAME_FLAGS, Short.toUnsignedInt(trackFrameFlags), 12))); // 12 = only dump fist 12 bit, because last for bit used for label jump 
        }
        if (trackElemLinkToDataOffset != null) {
            sb.append(String.format("trackElemLinkToDataOffset: 0x%x (%d)\n", trackElemLinkToDataOffset, trackElemLinkToDataOffset));
        }
        if (programCodeElemNum != null) {
            sb.append("Has programcode\n");
        }
        if (numCalculated != null) {
            sb.append(String.format("numCalculated: 0x%x (%d)\n", numCalculated, numCalculated));
        }
        if (trackFrameTypeFlags != null) {
            sb.append(String.format("trackFrameTypeFlags: 0x%x %s\n", trackFrameTypeFlags, dumpEventFlags(EventTrackFrameElem.ETF_TYPE_TRACK_FRAME_INTERPOLATION_KEY_TYPE_FLAGS, Short.toUnsignedInt(trackFrameTypeFlags))));
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) { // do not display compressed quats/vecs in public release mode
            if (part1ECQ != null) {
                sb.append("part1ECQ: ");
                sb.append(part1ECQ);
                sb.append("\n");
            }
            if (part1ECV != null) {
                sb.append("part1ECV: ");
                sb.append(part1ECV);
                sb.append("\n");
            }
            if (part2ECVCase4000 != null) {
                sb.append("part2ECVCase4000: ");
                sb.append(part2ECVCase4000);
                sb.append("\n");
            }
            if (part2ECVCase8000 != null) {
                sb.append("part2ECVCase8000: ");
                sb.append(part2ECVCase8000);
                sb.append("\n");
            }
            if (part2ECVCase2000 != null) {
                sb.append("part2ECVCase2000: ");
                sb.append(part2ECVCase2000);
                sb.append("\n");
            }
            if (part2ECQCase1000 != null) {
                sb.append("part2ECQCase1000: ");
                sb.append(part2ECQCase1000);
                sb.append("\n");
            }
            if (part2ECQCase800 != null) {
                sb.append("part2ECQCase800: ");
                sb.append(part2ECQCase800);
                sb.append("\n");
            }
        }
        if (part3case0matrix != null) {
            sb.append("part3case0matrix: ");
            sb.append(part3case0matrix);
            sb.append("\n");
        }
        if (part3case0quaternion != null) {
            sb.append("part3case0quaternion: ");
            sb.append(part3case0quaternion);
            sb.append("\n");
        }
        if (part3case3vec != null) {
            sb.append("part3case3vec: ");
            sb.append(part3case3vec);
            sb.append("\n");
        }
        if (part3case2vec != null) {
            sb.append("part3case2vec: ");
            sb.append(part3case2vec);
            sb.append("\n");
        }
        if (part3case1vec != null) {
            sb.append("part3case1vec: ");
            sb.append(part3case1vec);
            sb.append("\n");
        }

        if (uncompressedVector != null) {
            sb.append("Uncompressed Vector: ");
            sb.append(uncompressedVector);
            sb.append("\n");
        }
        if (uncompressedQuaternion != null) {
            sb.append("Uncompressed Quaternion: ");
            sb.append(uncompressedQuaternion);
            sb.append("\n");
        }

        if (timedEventFrameNum != null) {
            sb.append("Has timed frames\n");
        }

        if (isAllTrackGotoNode()) {
            sb.append("is an all track goto node, id: ").append(getGotoLabelID()).append("\n");
        }

        if (isCurrentTrackGotoNode()) {
            sb.append("is a current track goto node, id: ").append(getGotoLabelID()).append("\n");
        }

        if (isGotoLabel()) {
            sb.append("is a goto label, id: ").append(getGotoLabelID()).append("\n");
        }

        return sb.toString();
    }

    public String getEventTrackFrameFlagMeaning(int num) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            if ((num & 0x4) != 0) {
                return "use flash";
            }
            if ((num & 0x8) != 0) {
                return "ai call ret zero check";
            }
            if ((num & 0x10) != 0) {
                return "ai call ret !zero check";
            }
            if ((num & 0x20) != 0) {
                return "link to another EventTrackFrame's quat/negate q1 q2 X";
            }
            if ((num & 0x40) != 0) {
                return "scripting frame";
            }
            if ((num & 0x80) != 0) {
                return "interpolation key frame";
            }
            if ((num & 0x200) != 0) {
                return "this track jump node";
            }
            if ((num & 0x400) != 0) {
                return "all track jump node";
            }
            if ((num & 0x800) != 0) {
                return "no matrix/quat/vec calc";
            }
            return "unknown";
        } else {
            return "";
        }
    }

    public String getEventTrackFrameInterpolationKeyTypeFlagMeaning(int num) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            if ((num & 0x1) != 0) {
                return "vec bit #1";
            }
            if ((num & 0x2) != 0) {
                return "vec bit #2";
            }
            if ((num & 0x4) != 0) {
                return "matrix";
            }
            if ((num & 0x8) != 0) {
                return "timed frames";
            }
            if ((num & 0x10) != 0) {
                return "quaternion";
            }
            if ((num & 0x20) != 0) {
                return "set reference to currgao";
            }
            if ((num & 0x40) != 0) {
                return "set blending to next elem";
            }
            if ((num & 0x80) != 0) {
                return "compressed quaternion";
            }
            if ((num & 0x100) != 0) {
                return "calc from prev cq 1";
            }
            if ((num & 0x200) != 0) {
                return "calc from prev cq 2";
            }
            if ((num & 0x400) != 0) {
                return "calc from prev cq 3";
            }
            if ((num & 0x800) != 0) {
                return "compressed vec XYZq no add";
            }
            if ((num & 0x1000) != 0) {
                return "compressed vec XYZq";
            }
            if ((num & 0x2000) != 0) {
                return "compressed vec XYZ";
            }
            if ((num & 0x4000) != 0) {
                return "compressed vec YXZ";
            }
            if ((num & 0x8000) != 0) {
                return "compressed vec YZX";
            }
            return "unknown";
        } else {
            return "";
        }
    }

    public String dumpEventFlags(int type, int flags) {
        return dumpEventFlags(type, flags, 32);
    }

    public String dumpEventFlags(int type, int flags, int bitLimit) {
        StringBuilder sb = new StringBuilder();

        if (bitLimit < 0) {
            return sb.toString();
        }

        if (bitLimit > 32) {
            bitLimit = 32;
        }

        int i = 0;
        String s;

        while (flags != 0 && i < bitLimit) {
            if ((flags & (1 << i)) != 0) {
                if (type == EventTrackFrameElem.ETF_TYPE_TRACK_FRAME_FLAGS) {
                    s = getEventTrackFrameFlagMeaning(1 << i);
                } else {
                    s = getEventTrackFrameInterpolationKeyTypeFlagMeaning(1 << i);
                }
                sb.append(String.format("[%x, %s]", (1 << i), s));
                flags -= (1 << i);
            }
            i++;
        }
        return sb.toString();
    }

    public EventTrackFrameElem() {
    }

}
