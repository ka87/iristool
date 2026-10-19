/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.EventTrackElem;
import BFDataFileLib.EventTrackFrameElem;
import Common.DataType.ArrayListWithSizeOverLoad;
import Common.DataType.Quaternion;
import Common.DataType.EventsCompressedQuaternion;
import Common.DataType.EventsCompressedVector;
import Common.DataType.Matrix4x4;
import Common.DataType.OFCProgramCodeElem;
import Common.DataType.Vector3;
import Common.IrisToolDefines;
import Common.ReaderUtil;
import java.io.File;

/**
 *
 * @author andras
 */
public class BFEventsChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private Short trackNum, flags, flagsOrig;
    private ArrayListWithSizeOverLoad<EventTrackElem> eventTracks;

    public BFEventsChunk() {
        super();
        eventTracks = new ArrayListWithSizeOverLoad<>();
    }

    public ArrayListWithSizeOverLoad<EventTrackElem> getEventTracks() {
        return eventTracks;
    }

    public Short getFlags() {
        return flags;
    }

    public void setFlags(Short flags) {
        this.flags = flags;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Events Chunk.");
        }

        Long len = initParse();
        eventTracks.clear();

        long startpos = ru.getCurrentPos();

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Length is " + len + " bytes.");
        }

        trackNum = ru.readShort(false);
        flags = ru.readShort(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Track num: %d (%x)", trackNum, trackNum);
            logF("Flags: %d (%x)", flags, flags);
        }

        flagsOrig = flags;
        flags = (short) (flags & ~0x2); // remove resolved state
        // TODO: what is 0x8000, should it be removed?

        int i;

        short eventTrackFlagsToParser;

        for (i = 0; i < trackNum; i++) {
            EventTrackElem ete = new EventTrackElem();
            eventTracks.add(ete);

            ete.setEventTrackFlags(ru.readShort(false));
            ete.setBoneID(ru.readShort(false));
            ete.setTrackSize(ru.readInt(false));

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("TrackFlags[%d]: %d (%x), unks1: %d (%x), unkTrackNum: %d (%x)", i, ete.getEventTrackFlags(), ete.getEventTrackFlags(),
                        ete.getBoneId(), ete.getBoneId(), ete.getTrackSize(), ete.getTrackSize());
            }

            eventTrackFlagsToParser = (short) ((ete.getEventTrackFlags() & 0xFF00) & ~0x4000);
            eventTrackFlagsToParser &= ~0x8000;

            if ((ete.getEventTrackFlags() & 0x8000) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("trackFlagsToParser is %x", eventTrackFlagsToParser);
                }
                if (eventTrackFlagsToParser == 0) {
                    ete.setInnerUnk1(ru.readInt(false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("innerUnk1[%d] (if innerFlags & 0x8000): %d (%x)", i, ete.getInnerUnk1(), ete.getInnerUnk1());
                    }
                }
            } else {
                ete.setInnerGaoKey(ru.readInt(false));
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Inner GAO Key: %x", ete.getInnerGaoKey());
                }
                if (ete.getInnerGaoKey() != 0) {
                    ete.setInnerGaoKLE(new BFKeyListElem(ete.getInnerGaoKey(), BFGaoChunk.class));
                    bfdfc.addToKeyList(ete.getInnerGaoKLE());
                }
                if (!insideBinFile) {
                    ete.setInnerUnkS1(ru.readNString(15, false));
                    ete.setInnerUnkB1(ru.readByte(false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("[%d] innerUnkS1: %s, innerUnkB1: %d (%x)", i, ete.getInnerUnkS1(), ete.getInnerUnkB1(), ete.getInnerUnkB1());
                    }
                }
            }
            if (!parseEventsTrack(ete, eventTrackFlagsToParser)) {
                return false;
            }
        }

        if ((ru.getCurrentPos() - startpos) != len) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Parsed length != length: currentpos: %x", ru.getCurrentPos());
            }
            return false;
        }

        return true;
    }

    public boolean parseHack(File f) {

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Events Chunk. (Hack)");
        }
        insideBinFile = false;
        ru = new ReaderUtil(f);

        Long len = initParse();
        eventTracks.clear();

        long startpos = ru.getCurrentPos();

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read failed.");
            }
            try {
                ru.close();
            } catch (Exception e) {
            }
            return false;
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Length is " + len + " bytes.");
        }

        trackNum = ru.readShort(false);
        flags = ru.readShort(false);

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Track num: %d (%x)", trackNum, trackNum);
            logF("Flags: %d (%x)", flags, flags);
        }

        flagsOrig = flags;
        flags = (short) (flags & ~0x2); // remove resolved state
        // TODO: what is 0x8000, should it be removed?

        int i;

        short eventTrackFlagsToParser;

        for (i = 0; i < trackNum; i++) {
            EventTrackElem ete = new EventTrackElem();
            eventTracks.add(ete);

            ete.setEventTrackFlags(ru.readShort(false));
            ete.setBoneID(ru.readShort(false));
            ete.setTrackSize(ru.readInt(false));

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("TrackFlags[%d]: %d (%x), unks1: %d (%x), unkTrackNum: %d (%x)", i, ete.getEventTrackFlags(), ete.getEventTrackFlags(),
                        ete.getBoneId(), ete.getBoneId(), ete.getTrackSize(), ete.getTrackSize());
            }

            eventTrackFlagsToParser = (short) ((ete.getEventTrackFlags() & 0xFF00) & ~0x4000);
            eventTrackFlagsToParser &= ~0x8000;

            if ((ete.getEventTrackFlags() & 0x8000) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("trackFlagsToParser is %x", eventTrackFlagsToParser);
                }
                if (eventTrackFlagsToParser == 0) {
                    ete.setInnerUnk1(ru.readInt(false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("innerUnk1[%d] (if innerFlags & 0x8000): %d (%x)", i, ete.getInnerUnk1(), ete.getInnerUnk1());
                    }
                }
            } else {
                ete.setInnerGaoKey(ru.readInt(false));
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Inner GAO Key: %x", ete.getInnerGaoKey());
                }
                if (ete.getInnerGaoKey() != 0) {
                    ete.setInnerGaoKLE(new BFKeyListElem(ete.getInnerGaoKey(), BFGaoChunk.class));
                    //bfdfc.addToKeyList(ete.getInnerGaoKLE());
                }
                if (!insideBinFile) {
                    ete.setInnerUnkS1(ru.readNString(15, false));
                    ete.setInnerUnkB1(ru.readByte(false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("[%d] innerUnkS1: %s, innerUnkB1: %d (%x)", i, ete.getInnerUnkS1(), ete.getInnerUnkB1(), ete.getInnerUnkB1());
                    }
                }
            }
            if (!parseEventsTrack(ete, eventTrackFlagsToParser)) {
                try {
                    ru.close();
                } catch (Exception e) {
                }
                return false;
            }
        }

        if ((ru.getCurrentPos() - startpos) != len) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Parsed length != length: currentpos: %x", ru.getCurrentPos());
            }
            try {
                ru.close();
            } catch (Exception e) {
            }
            return false;
        }

        try {
            ru.close();
        } catch (Exception e) {
        }
        return true;
    }

    private Boolean parseEventsTrack(EventTrackElem ete, short trackFlags) {
        Short shortTrackFrameFlags;
        Byte trackFrameLenByte;
        Short trackFrameLenShort;
        Integer trackFrameNum = ru.readInt(false);

        ete.setTrackFrameNum(trackFrameNum);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("trackFrameNum: %d (%x)", trackFrameNum, trackFrameNum);
        }
        int i;

        short trackFlagsToTrackFrameParser;

        Short trackFrameFlagsSArray[] = new Short[trackFrameNum];

        for (i = 0; i < trackFrameNum; i++) {
            EventTrackFrameElem etfe = new EventTrackFrameElem();
            ete.getEventTrackFrameElems().add(etfe);

            if ((trackFlags & 0x200) != 0) {
                etfe.setTrackFrameLenByte(trackFrameLenByte = ru.readByte(false));
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("[%d] trackFrameLenByte: %d (%x)", i, trackFrameLenByte, trackFrameLenByte);
                }
            } else {
                etfe.setTrackFrameLenShort(trackFrameLenShort = ru.readShort(false));
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("[%d] trackFrameLenShort: %d (%x)", i, trackFrameLenShort, trackFrameLenShort);
                }
            }
            if ((trackFlags & 0x800) != 0) {
                if (i != 0) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Flags has 0x800, and i greather than 0, ignoring Short (int16) read.");
                    }
                    trackFrameFlagsSArray[i] = (short) (trackFrameFlagsSArray[0] & ~0x4);
                    etfe.setTrackFrameFlags(trackFrameFlagsSArray[i]);
                } else {
                    etfe.setTrackFrameFlags(shortTrackFrameFlags = ru.readShort(false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("[%d] trackFrameFlags (short) (flags has 0x800): %d (%x)", i, shortTrackFrameFlags, shortTrackFrameFlags);
                    }
                    trackFrameFlagsSArray[i] = shortTrackFrameFlags;
                }
            } else {
                etfe.setTrackFrameFlags(shortTrackFrameFlags = ru.readShort(false));
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("[%d] trackFrameFlags (short): %d (%x)", i, shortTrackFrameFlags, shortTrackFrameFlags);
                }
                trackFrameFlagsSArray[i] = shortTrackFrameFlags;
            }

            trackFrameFlagsSArray[i] = (short) (trackFrameFlagsSArray[i] & ~0x2); // remove "resolved" flag

            if ((trackFrameFlagsSArray[i] & 0x7C0) == 0x40) {
                if (!parseEventsOFCProgramCode(etfe)) {
                    return false;
                }
            } else if ((trackFrameFlagsSArray[i] & 0x7C0) == 0x80) {
                if ((trackFrameFlagsSArray[i] & 0x20) != 0) { // Not needed to implement, because this case not found in parser logs
                    etfe.setTrackElemLinkToDataOffset(ru.readInt(false));
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("TrackElemLinkToDataOffset (trackFrameFlags has 0x20): %d (%x)", etfe.getTrackElemLinkToDataOffset(), etfe.getTrackElemLinkToDataOffset());
                    }
                } else {
                    if (i > 0) {
                        trackFlagsToTrackFrameParser = trackFlags;
                    } else {
                        trackFlagsToTrackFrameParser = 0;
                    }
                    if (i == 0 && (trackFlags & 0x2000) != 0) {
                        trackFlagsToTrackFrameParser |= 0x100;
                    }
                    if (!parseEventsTrackFrameNonProgramCode(etfe, trackFlagsToTrackFrameParser)) {
                        return false;
                    }
                }
            }

        }
        return true;
    }

    private Boolean parseEventsOFCProgramCode(EventTrackFrameElem etfe) {
        etfe.setProgramCodeElemNum(ru.readInt(false));
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Number: " + etfe.getProgramCodeElemNum());
        }
        int i;
        OFCProgramCodeElem ofcpce;
        for (i = 0; i < etfe.getProgramCodeElemNum(); i++) {
            ofcpce = new OFCProgramCodeElem();
            if (!ofcpce.readValues(ru)) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("OFCProgramCodeElem read failed.");
                }
                return false;
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log(ofcpce);
            }
            etfe.getProgramCode().add(ofcpce);
        }
        return true;
    }

    private Short numCalculatedSaved = 0;
    private Short trackFrameTypeFlagsSaved = 0;

    private Boolean parseEventsTrackFrameNonProgramCode(EventTrackFrameElem etfe, int trackFlagsToParser) {
        Short numToCalc;
        long startPos = ru.getCurrentPos();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("TrackFlagsToParser received: %x", trackFlagsToParser);
        }

        Short numCalculated = 0, trackFrameInterpolationKeyTypeFlags = 0;

        if ((trackFlagsToParser & 0x2000) != 0) {
            numCalculated = (short) (numCalculatedSaved / 2);
        } else {
            numCalculated = ru.readShort(false);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("NumCalculated: %d (%x)", numCalculated, numCalculated);
        }

        if ((trackFlagsToParser & 0x1000) != 0) {
            if ((trackFlagsToParser & 0x2000) != 0) {
                trackFrameInterpolationKeyTypeFlags = trackFrameTypeFlagsSaved;
                etfe.setTrackFrameTypeFlags(trackFrameInterpolationKeyTypeFlags);
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("trackFlagsToParser: (& 0x2000 == FALSE): todo");
                }
                return false;
            }
        } else {
            trackFrameInterpolationKeyTypeFlags = ru.readShort(false);
            etfe.setTrackFrameTypeFlags(trackFrameInterpolationKeyTypeFlags);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("TrackFrameTypeFlags: %x", trackFrameInterpolationKeyTypeFlags);
        }

        if ((trackFrameInterpolationKeyTypeFlags & 0x780) != 0) {
            if ((trackFrameInterpolationKeyTypeFlags & 0x80) != 0) {
                EventsCompressedQuaternion ecq = new EventsCompressedQuaternion();
                if (!ecq.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("EventsCompressedQuaternion read failed.");
                    }
                    return false;
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(ecq);
                }
                etfe.setPart1ECQ(ecq);
            } else {
                EventsCompressedVector ecv = new EventsCompressedVector();
                if (!ecv.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("EventsCompressedVector read failed.");
                    }
                    return false;
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(ecv);
                }
                etfe.setPart1ECV(ecv);
                // TODO: handle case: 0x100, 0x200, 0x300
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("uncompress: TODO: Difference from previous quaternion not supported!"); // game maybe broken here (null pointer reference ?)
                }
            }
            if ((trackFrameInterpolationKeyTypeFlags & 0x80) != 0) { // remove, when todo above implemented (this filters out else case above)
                etfe.setUncompressedQuaternion(Quaternion.uncompressQuaternion(etfe.getPart1ECQ(), null));
            }
            numToCalc = 20;
            if ((trackFlagsToParser & 0x2100) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("trackFlagsToParser has 0x2100");
                }
                // itt számolgat: 2 * 20 | 1 = 0x29
                numToCalc = (short) ((2 * numToCalc) | 1);
                etfe.setNumCalculated(numToCalc);
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("trackFlagsToParser does not has 0x2100");
                }
                etfe.setNumCalculated(numToCalc);
            }

            numCalculatedSaved = numToCalc;
            trackFrameTypeFlagsSaved = trackFrameInterpolationKeyTypeFlags;

            return true;
        }
        if ((trackFrameInterpolationKeyTypeFlags & 0xF800) != 0) {
            EventsCompressedVector ecv;
            EventsCompressedQuaternion ecq;
            switch (trackFrameInterpolationKeyTypeFlags & 0xF800) {
                case 0x4000:
                    ecv = new EventsCompressedVector();
                    if (!ecv.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("EventsCompressedVector read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(ecv);
                    }
                    etfe.setPart2ECVCase4000(ecv);
                    etfe.setUncompressedVector(Vector3.uncompressVectorYXZ(ecv, null));
                    break;
                case 0x8000:
                    ecv = new EventsCompressedVector();
                    if (!ecv.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("EventsCompressedVector read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(ecv);
                    }
                    etfe.setPart2ECVCase8000(ecv);
                    etfe.setUncompressedVector(Vector3.uncompressVectorYZX(ecv, null));
                    break;
                case 0x2000:
                    ecv = new EventsCompressedVector();
                    if (!ecv.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("EventsCompressedVector read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(ecv);
                    }
                    etfe.setPart2ECVCase2000(ecv);
                    etfe.setUncompressedVector(Vector3.uncompressVectorXYZ(ecv, null));
                    break;
                case 0x1000:
                    ecq = new EventsCompressedQuaternion();
                    if (!ecq.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("EventsCompressedQuaternion read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(ecq);
                    }
                    etfe.setPart2ECQCase1000(ecq);
                    etfe.setUncompressedVector(Vector3.uncompressVectorXYZFromCompressedQuaternion(ecq, null));  // unnecessary, game zeroes destination vec's components
                    break;
                case 0x800:
                    ecq = new EventsCompressedQuaternion();
                    if (!ecq.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("EventsCompressedQuaternion read failed.");
                        }
                        return false;
                    }
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log(ecq);
                    }
                    etfe.setPart2ECQCase800(ecq);
                    etfe.setUncompressedVector(Vector3.uncompressVectorXYZFromCompressedQuaternionNoAddToPrevValue(ecq, null));
                    break;
            }
            numToCalc = 16;
            if ((trackFlagsToParser & 0x2100) != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("trackFlagsToParser has 0x2100");
                }
                numToCalc = (short) ((2 * numToCalc) | 1);
                etfe.setNumCalculated(numToCalc);
            } else {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("trackFlagsToParser does not has 0x2100");
                }
                etfe.setNumCalculated(numToCalc);
            }
            numCalculatedSaved = numToCalc;
            trackFrameTypeFlagsSaved = trackFrameInterpolationKeyTypeFlags;
        }
        if ((trackFrameInterpolationKeyTypeFlags & 0x17) == 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("trackFrameInterpolationKeyTypeFlags does not has 0x17, returning.");
            }
            return true;
        }

        numToCalc = numCalculated;
        if ((trackFlagsToParser & 0x2100) != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("trackFlagsToParser has 0x2100");
            }
            numToCalc = (short) ((2 * numToCalc) | 1);
            etfe.setNumCalculated(numToCalc);
        } else {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("trackFlagsToParser does not has 0x2100");
            }
            etfe.setNumCalculated(numToCalc);
        }

        numCalculatedSaved = numToCalc;
        trackFrameTypeFlagsSaved = trackFrameInterpolationKeyTypeFlags;

        if ((trackFlagsToParser & 0x2000) != 0) {
            numToCalc = (short) (numToCalc - 2);
        }

        if ((trackFlagsToParser & 0x1000) != 0) {
            numToCalc = (short) (numToCalc - 2);
        }

        Matrix4x4 mtx;
        Quaternion eqfs;
        Vector3 pos;

        switch (trackFrameInterpolationKeyTypeFlags & 0x3) {
            case 0:
                switch (trackFrameInterpolationKeyTypeFlags & 0x14) {
                    case 4:
                        mtx = new Matrix4x4();
                        if (!mtx.readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("Matrix4x4 read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(mtx);
                        }
                        etfe.setPart3case0matrix(mtx);
                        break;
                    case 16:
                        eqfs = new Quaternion();
                        if (!eqfs.readValues(ru)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                log("EventsQuaternion read failed.");
                            }
                            return false;
                        }
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log(eqfs);
                        }
                        etfe.setPart3case0quaternion(eqfs);
                        break;
                    default:
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("trackFrameTypeFlags & 0x14 invalid.");
                        }
                        break;
                }
                break;
            case 3:
                pos = new Vector3();
                if (!pos.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read failed.");
                    }
                    return false;
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(pos);
                }
                etfe.setPart3case3vec(pos);
            case 2:
                pos = new Vector3();
                if (!pos.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read failed.");
                    }
                    return false;
                }
                etfe.setPart3case2vec(pos);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(pos);
                }
            case 1:
                pos = new Vector3();
                if (!pos.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Vector3 read failed.");
                    }
                    return false;
                }
                etfe.setPart3case1vec(pos);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(pos);
                }
                break;
        }

        if ((trackFrameInterpolationKeyTypeFlags & 0x8) != 0 && (ru.getCurrentPos() - startPos) < numToCalc) {
            etfe.setTimedEventFrameNum(ru.readInt(false));
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Event keyframe number: %d (%x)", etfe.getTimedEventFrameNum(), etfe.getTimedEventFrameNum());
            }
            int i;
            int eventKeyFrameRelatedInt;
            etfe.getTimedEventFrameElems().add(0); // time zero, gme stores here size, and function interpolate_time sets to zero. I set here zero, because arrayList stores size elready
            for (i = 0; i < (2 * etfe.getTimedEventFrameNum()) - 1; i++) {
                eventKeyFrameRelatedInt = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("[%d] eventKeyFrameRelatedInt: %x %d %f", i, eventKeyFrameRelatedInt, eventKeyFrameRelatedInt, Float.intBitsToFloat(eventKeyFrameRelatedInt));
                }
                etfe.getTimedEventFrameElems().add(eventKeyFrameRelatedInt);
                // 2-esével, ez alapján mozog az event, 
                // egyik valamilyen időpont, a másik meg arra a pozícióra mutató szorzó
                // amire az időpont alapján be kell, hogy álljon
            }
        }

        return true;
    }
}
