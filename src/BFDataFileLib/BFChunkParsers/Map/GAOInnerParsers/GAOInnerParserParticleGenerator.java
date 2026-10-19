/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFDataFileClass;
import Common.DataType.ParticleElem;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class GAOInnerParserParticleGenerator extends BFChunkParseClass implements BFChunkParseInterface {

    private int maxPAGVertNum = 0;
    private Integer type;
    private int flags = 0, flagsBackup = 0;
    private byte particleType = 0;
    private byte particleType2 = 0;
    private float timeDividerMain = 0.0f, timeDividerMainBackup = 0.0f;
    private float elapsedTimeRemained = 0.0f;
    private float particleType2Case6GlobalMtxMultiplier = 0.0f;
    private float particleType1RandMultiplier = 0.0f;
    private float globalMtxMultiplier = 0.0f;
    private float partType5GlobalMtxRndMultiplier = 0.0f;
    private float particleTypeCase2SecMinOffset = 0.0f;
    private float particleTypeCase2SecMaxOffset = 0.0f;
    private float particleTypeCase2FirstCentralizedOffsetMin = 0.0f;
    private float particleTypeCase2FirstCentralizedOffsetMax = 0.0f;
    private float vecMultMin = 0.0f;
    private float vecMultMax = 0.0f;
    private float vecMult2WOunkFlags4Min = 0.0f;
    private float vecMult2WOunkFlags4Max = 0.0f;
    private float randMinVal1 = 0.0f;
    private float randMaxVal1 = 0.0f;
    private float randMinVal2 = 0.0f;
    private float randMaxVal2 = 0.0f;
    private float globalMinMultiplier = 0.0f;
    private float globalMaxMultiplier = 0.0f;
    private float twoShortsMultiplierIfUnkFlagsHas20 = 0.0f;
    private Vector3 vec = null;
    private float unkFlagsHas2000Multiplier = 0.0f;
    private float size2Min = 0.0f;
    private float negateOffsetMultiplierForUnkflags400 = 0.0f;
    private float size2Max = 0.0f;
    private float negateOffsetMultiplierForUnkflags800 = 0.0f;
    private float rand1MaxMultiplier = 0.0f;
    private float rand1MinMultiplier = 0.0f;
    private float randAdditionalMinMultiplier = 0.0f;
    private float randAdditionalMaxMultiplier = 0.0f;
    private float decalXMultipilier = 0.0f;
    private float decalYMultipilier = 0.0f;
    private float disableUnkFlagsCase400IfThisSet = 0.0f;
    private int color = 0;
    
    // to be set by script
    private BFGaoChunk gaoForVertices = null;
    private BFGaoChunk gaoForGlobalMatrix = null;

    // unused and !insidebinfile
    private Byte b0_nb1;
    private Float timeDividerMainUNUSED;
    private Integer i0_nb1, i0_nb2, i0_nb3, i0_nb4, i0_nb5, i0_nb6;

    // runtime
    private int actPAGVertNum = 0;
    private float elapsedtimeRelated = 0;
    private ParticleElem PAGElemData[];
    private int renderingCounter = 0;

    public GAOInnerParserParticleGenerator() {
        super();
    }

    public Boolean parsePAG(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false; // set readerutil and other objects
        }

        if (!buildFromParsed) {
            maxPAGVertNum = ru.readInt(false);
            type = ru.readInt(false);
        } else {
            wu.writeInt(maxPAGVertNum);
            wu.writeInt(type);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("maxPAGVertNum: %d, type: %d (%x)", maxPAGVertNum, type, type);
        }

        switch (type) {
            case 0:
                if (!buildFromParsed) {
                    flags = ru.readByte(false);
                    particleType = ru.readByte(false);
                    particleType2 = ru.readByte(false);
                } else {
                    wu.writeByte((byte) flags);
                    wu.writeByte(particleType);
                    wu.writeByte(particleType2);
                }
                if (!insideBinFile) {
                    if (!buildFromParsed) {
                        b0_nb1 = ru.readByte(false);
                    } else {
                        wu.writeByte(b0_nb1);
                    }
                }
                if (!buildFromParsed) {
                    particleType1RandMultiplier = ru.readFloat(false);
                    globalMtxMultiplier = ru.readFloat(false);
                    partType5GlobalMtxRndMultiplier = ru.readFloat(false);
                    timeDividerMainUNUSED = ru.readFloat(false);
                    elapsedTimeRemained = ru.readFloat(false);
                } else {
                    wu.writeFloat(particleType1RandMultiplier);
                    wu.writeFloat(globalMtxMultiplier);
                    wu.writeFloat(partType5GlobalMtxRndMultiplier);
                    wu.writeFloat(timeDividerMainUNUSED);
                    wu.writeFloat(elapsedTimeRemained);
                }

                if (!insideBinFile) {
                    if (!buildFromParsed) {
                        i0_nb1 = ru.readInt(false);
                        i0_nb2 = ru.readInt(false);
                        i0_nb3 = ru.readInt(false);
                        i0_nb4 = ru.readInt(false);
                        i0_nb5 = ru.readInt(false);
                        i0_nb6 = ru.readInt(false);
                    } else {
                        wu.writeInt(i0_nb1);
                        wu.writeInt(i0_nb2);
                        wu.writeInt(i0_nb3);
                        wu.writeInt(i0_nb4);
                        wu.writeInt(i0_nb5);
                        wu.writeInt(i0_nb6);
                    }
                }

                if (!buildFromParsed) {
                    particleTypeCase2SecMinOffset = ru.readFloat(false);
                    particleTypeCase2SecMaxOffset = ru.readFloat(false);
                    particleTypeCase2FirstCentralizedOffsetMin = ru.readFloat(false);
                    particleTypeCase2FirstCentralizedOffsetMax = ru.readFloat(false);
                    vecMultMax = ru.readFloat(false);
                    vecMult2WOunkFlags4Max = ru.readFloat(false);
                    randMinVal1 = ru.readFloat(false);
                    randMaxVal1 = ru.readFloat(false);
                } else {
                    wu.writeFloat(particleTypeCase2SecMinOffset);
                    wu.writeFloat(particleTypeCase2SecMaxOffset);
                    wu.writeFloat(particleTypeCase2FirstCentralizedOffsetMin);
                    wu.writeFloat(particleTypeCase2FirstCentralizedOffsetMax);
                    wu.writeFloat(vecMultMax);
                    wu.writeFloat(vecMult2WOunkFlags4Max);
                    wu.writeFloat(randMinVal1);
                    wu.writeFloat(randMaxVal1);
                }
                vecMultMin = vecMultMax;
                vecMult2WOunkFlags4Min = vecMult2WOunkFlags4Max;

                if (!buildFromParsed) {
                    vec = new Vector3(0.0f, 0.0f, 0.0f);
                    if (!vec.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Vector3 read failed.");
                        }
                        return false;
                    }
                } else {
                    vec.writeValues(wu);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(vec);
                }
                break;
            case 9:
                if (!buildFromParsed) {
                    flags = ru.readShort(false);
                    particleType = ru.readByte(false);
                    particleType2 = ru.readByte(false);
                    particleType2Case6GlobalMtxMultiplier = ru.readFloat(false);
                    particleType1RandMultiplier = ru.readFloat(false);
                    globalMtxMultiplier = ru.readFloat(false);
                    partType5GlobalMtxRndMultiplier = ru.readFloat(false);
                    timeDividerMain = ru.readFloat(false);
                    particleTypeCase2SecMinOffset = ru.readFloat(false);
                    particleTypeCase2SecMaxOffset = ru.readFloat(false);
                    particleTypeCase2FirstCentralizedOffsetMin = ru.readFloat(false);
                    particleTypeCase2FirstCentralizedOffsetMax = ru.readFloat(false);
                    vecMultMin = ru.readFloat(false);
                    vecMultMax = ru.readFloat(false);
                    vecMult2WOunkFlags4Min = ru.readFloat(false);
                    vecMult2WOunkFlags4Max = ru.readFloat(false);
                    randMinVal1 = ru.readFloat(false);
                    randMaxVal1 = ru.readFloat(false);
                    randMinVal2 = ru.readFloat(false);
                    randMaxVal2 = ru.readFloat(false);
                    globalMinMultiplier = ru.readFloat(false);
                    globalMaxMultiplier = ru.readFloat(false);
                    vec = new Vector3(0.0f, 0.0f, 0.0f);
                    if (!vec.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Vector3 read failed.");
                        }
                        return false;
                    }
                    unkFlagsHas2000Multiplier = ru.readFloat(false);
                    twoShortsMultiplierIfUnkFlagsHas20 = ru.readFloat(false);
                    color = ru.readInt(false);
                    size2Min = ru.readFloat(false);
                    negateOffsetMultiplierForUnkflags400 = ru.readFloat(false);
                    size2Max = ru.readFloat(false);
                    negateOffsetMultiplierForUnkflags800 = ru.readFloat(false);
                    rand1MaxMultiplier = ru.readFloat(false);
                    rand1MinMultiplier = ru.readFloat(false);
                    randAdditionalMinMultiplier = ru.readFloat(false);
                    randAdditionalMaxMultiplier = ru.readFloat(false);
                    decalXMultipilier = ru.readFloat(false);
                    decalYMultipilier = ru.readFloat(false);
                } else {
                    wu.writeShort((short) flags);
                    wu.writeByte(particleType);
                    wu.writeByte(particleType2);
                    wu.writeFloat(particleType2Case6GlobalMtxMultiplier);
                    wu.writeFloat(particleType1RandMultiplier);
                    wu.writeFloat(globalMtxMultiplier);
                    wu.writeFloat(partType5GlobalMtxRndMultiplier);
                    wu.writeFloat(timeDividerMain);
                    wu.writeFloat(particleTypeCase2SecMinOffset);
                    wu.writeFloat(particleTypeCase2SecMaxOffset);
                    wu.writeFloat(particleTypeCase2FirstCentralizedOffsetMin);
                    wu.writeFloat(particleTypeCase2FirstCentralizedOffsetMax);
                    wu.writeFloat(vecMultMin);
                    wu.writeFloat(vecMultMax);
                    wu.writeFloat(vecMult2WOunkFlags4Min);
                    wu.writeFloat(vecMult2WOunkFlags4Max);
                    wu.writeFloat(randMinVal1);
                    wu.writeFloat(randMaxVal1);
                    wu.writeFloat(randMinVal2);
                    wu.writeFloat(randMaxVal2);
                    wu.writeFloat(globalMinMultiplier);
                    wu.writeFloat(globalMaxMultiplier);
                    wu.writeFloat(unkFlagsHas2000Multiplier);
                    wu.writeFloat(twoShortsMultiplierIfUnkFlagsHas20);
                    wu.writeInt(color);
                    vec.writeValues(wu);
                    wu.writeFloat(size2Min);
                    wu.writeFloat(negateOffsetMultiplierForUnkflags400);
                    wu.writeFloat(size2Max);
                    wu.writeFloat(negateOffsetMultiplierForUnkflags800);
                    wu.writeFloat(rand1MaxMultiplier);
                    wu.writeFloat(rand1MinMultiplier);
                    wu.writeFloat(randAdditionalMinMultiplier);
                    wu.writeFloat(randAdditionalMaxMultiplier);
                    wu.writeFloat(decalXMultipilier);
                    wu.writeFloat(decalYMultipilier);
                }
                timeDividerMainUNUSED = timeDividerMain;
                break;
            case 11:
                if (!buildFromParsed) {
                    flags = ru.readInt(false);
                    particleType = ru.readByte(false);
                    particleType2 = ru.readByte(false);
                } else {
                    wu.writeInt(flags);
                    wu.writeByte(particleType);
                    wu.writeByte(particleType2);
                }
                if (!buildFromParsed) {
                    particleType2Case6GlobalMtxMultiplier = ru.readFloat(false);
                    particleType1RandMultiplier = ru.readFloat(false);
                    globalMtxMultiplier = ru.readFloat(false);
                    partType5GlobalMtxRndMultiplier = ru.readFloat(false);
                    timeDividerMain = ru.readFloat(false);
                    particleTypeCase2SecMinOffset = ru.readFloat(false);
                    particleTypeCase2SecMaxOffset = ru.readFloat(false);
                    particleTypeCase2FirstCentralizedOffsetMin = ru.readFloat(false);
                    particleTypeCase2FirstCentralizedOffsetMax = ru.readFloat(false);
                    vecMultMin = ru.readFloat(false);
                    vecMultMax = ru.readFloat(false);
                    vecMult2WOunkFlags4Min = ru.readFloat(false);
                    vecMult2WOunkFlags4Max = ru.readFloat(false);
                    randMinVal1 = ru.readFloat(false);
                    randMaxVal1 = ru.readFloat(false);
                    randMinVal2 = ru.readFloat(false);
                    randMaxVal2 = ru.readFloat(false);
                    globalMinMultiplier = ru.readFloat(false);
                    globalMaxMultiplier = ru.readFloat(false);
                } else {
                    wu.writeFloat(particleType2Case6GlobalMtxMultiplier);
                    wu.writeFloat(particleType1RandMultiplier);
                    wu.writeFloat(globalMtxMultiplier);
                    wu.writeFloat(partType5GlobalMtxRndMultiplier);
                    wu.writeFloat(timeDividerMain);
                    wu.writeFloat(particleTypeCase2SecMinOffset);
                    wu.writeFloat(particleTypeCase2SecMaxOffset);
                    wu.writeFloat(particleTypeCase2FirstCentralizedOffsetMin);
                    wu.writeFloat(particleTypeCase2FirstCentralizedOffsetMax);
                    wu.writeFloat(vecMultMin);
                    wu.writeFloat(vecMultMax);
                    wu.writeFloat(vecMult2WOunkFlags4Min);
                    wu.writeFloat(vecMult2WOunkFlags4Max);
                    wu.writeFloat(randMinVal1);
                    wu.writeFloat(randMaxVal1);
                    wu.writeFloat(randMinVal2);
                    wu.writeFloat(randMaxVal2);
                    wu.writeFloat(globalMinMultiplier);
                    wu.writeFloat(globalMaxMultiplier);
                }
                // common part for several types
                if (!buildFromParsed) {
                    vec = new Vector3();
                    if (!vec.readValues(ru)) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            log("Vector3 read failed.");
                        }
                        return false;
                    }
                } else {
                    vec.writeValues(wu);
                }

                if (!buildFromParsed) {
                    unkFlagsHas2000Multiplier = ru.readFloat(false);
                    twoShortsMultiplierIfUnkFlagsHas20 = ru.readFloat(false);
                } else {
                    wu.writeFloat(unkFlagsHas2000Multiplier);
                    wu.writeFloat(twoShortsMultiplierIfUnkFlagsHas20);
                }

                if (!buildFromParsed) {
                    color = ru.readInt(false);
                } else {
                    wu.writeInt(color);
                }

                if (!buildFromParsed) {
                    size2Min = ru.readFloat(false);
                    negateOffsetMultiplierForUnkflags400 = ru.readFloat(false);
                    size2Max = ru.readFloat(false);
                    negateOffsetMultiplierForUnkflags800 = ru.readFloat(false);
                    rand1MaxMultiplier = ru.readFloat(false);
                    rand1MinMultiplier = ru.readFloat(false);
                    randAdditionalMinMultiplier = ru.readFloat(false);
                    randAdditionalMaxMultiplier = ru.readFloat(false);
                    decalXMultipilier = ru.readFloat(false);
                    decalYMultipilier = ru.readFloat(false);
                    disableUnkFlagsCase400IfThisSet = ru.readFloat(false);
                } else {
                    wu.writeFloat(size2Min);
                    wu.writeFloat(negateOffsetMultiplierForUnkflags400);
                    wu.writeFloat(size2Max);
                    wu.writeFloat(negateOffsetMultiplierForUnkflags800);
                    wu.writeFloat(rand1MaxMultiplier);
                    wu.writeFloat(rand1MinMultiplier);
                    wu.writeFloat(randAdditionalMinMultiplier);
                    wu.writeFloat(randAdditionalMaxMultiplier);
                    wu.writeFloat(decalXMultipilier);
                    wu.writeFloat(decalYMultipilier);
                    wu.writeFloat(disableUnkFlagsCase400IfThisSet);
                }

                timeDividerMainUNUSED = timeDividerMain;

                break;
            default:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Unhandled type: " + type);
                }
                return false;
            //break;
        }

        flagsBackup = flags;
        timeDividerMainBackup = timeDividerMain;

        PAGElemData = new ParticleElem[maxPAGVertNum];

        for (int i = 0; i < maxPAGVertNum; i++) {
            PAGElemData[i] = new ParticleElem();
        }

        // log variables
        logF("flags: %x (%d)", flags, flags);
        logF("particleType: %x (%d)", particleType, particleType);
        logF("particleType2: %x (%d)", particleType2, particleType2);
        logF("timeDividerMain: %f", timeDividerMain);
        logF("timeDividerMainUNUSED: %f", timeDividerMainUNUSED);
        logF("elapsedTimeRemained: %f", elapsedTimeRemained);
        logF("particleType2Case6GlobalMtxMultiplier: %f", particleType2Case6GlobalMtxMultiplier);
        logF("particleType1RandMultiplier: %f", particleType1RandMultiplier);
        logF("globalMtxMultiplier: %f", globalMtxMultiplier);
        logF("partType5GlobalMtxRndMultiplier: %f", partType5GlobalMtxRndMultiplier);
        logF("particleTypeCase2SecMinOffset: %f", particleTypeCase2SecMinOffset);
        logF("particleTypeCase2SecMaxOffset: %f", particleTypeCase2SecMaxOffset);
        logF("particleTypeCase2FirstCentralizedOffsetMin: %f", particleTypeCase2FirstCentralizedOffsetMin);
        logF("particleTypeCase2FirstCentralizedOffsetMax: %f", particleTypeCase2FirstCentralizedOffsetMax);
        logF("vecMultMin: %f", vecMultMin);
        logF("vecMultMax: %f", vecMultMax);
        logF("vecMult2WOunkFlags4Min: %f", vecMult2WOunkFlags4Min);
        logF("vecMult2WOunkFlags4Max: %f", vecMult2WOunkFlags4Max);
        logF("randMinVal1: %f", randMinVal1);
        logF("randMaxVal1: %f", randMaxVal1);
        logF("randMinVal2: %f", randMinVal2);
        logF("randMaxVal2: %f", randMaxVal2);
        logF("globalMinMultiplier: %f", globalMinMultiplier);
        logF("globalMaxMultiplier: %f", globalMaxMultiplier);
        logF("twoShortsMultiplierIfUnkFlagsHas20: %f", twoShortsMultiplierIfUnkFlagsHas20);
        log("vec: " + vec);
        logF("unkFlagsHas2000Multiplier: %f", unkFlagsHas2000Multiplier);
        logF("size2Min: %f", size2Min);
        logF("negateOffsetMultiplierForUnkflags400: %f", negateOffsetMultiplierForUnkflags400);
        logF("size2Max: %f", size2Max);
        logF("negateOffsetMultiplierForUnkflags800: %f", negateOffsetMultiplierForUnkflags800);
        logF("rand1MaxMultiplier: %f", rand1MaxMultiplier);
        logF("rand1MinMultiplier: %f", rand1MinMultiplier);
        logF("randAdditionalMinMultiplier: %f", randAdditionalMinMultiplier);
        logF("randAdditionalMaxMultiplier: %f", randAdditionalMaxMultiplier);
        logF("decalXMultipilier: %f", decalXMultipilier);
        logF("decalYMultipilier: %f", decalYMultipilier);
        logF("disableUnkFlagsCase400IfThisSet: %f", disableUnkFlagsCase400IfThisSet);
        logF("color: %x", color);

        if (!insideBinFile) {
            logF("b0_nb1: %x (%d)", b0_nb1, b0_nb1);
            logF("i0_nb1: %x (%d)", i0_nb1, i0_nb1);
            logF("i0_nb2: %x (%d)", i0_nb2, i0_nb2);
            logF("i0_nb3: %x (%d)", i0_nb3, i0_nb3);
            logF("i0_nb4: %x (%d)", i0_nb4, i0_nb4);
            logF("i0_nb5: %x (%d)", i0_nb5, i0_nb5);
            logF("i0_nb6: %x (%d)", i0_nb6, i0_nb6);
        }

        return true;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner Particle Generator Chunk.");
        }

        return parsePAG(bfdfc, insideBinFile, buildFromParsed);
    }

    public int getMaxPAGVertNum() {
        return maxPAGVertNum;
    }

    public void setMaxPAGVertNum(int maxPAGVertNum) {
        this.maxPAGVertNum = maxPAGVertNum;
    }

    public int getActPAGVertNum() {
        return actPAGVertNum;
    }

    public void setActPAGVertNum(int actPAGVertNum) {
        this.actPAGVertNum = actPAGVertNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public byte getParticleType() {
        return particleType;
    }

    public void setParticleType(byte particleType) {
        this.particleType = particleType;
    }

    public byte getParticleType2() {
        return particleType2;
    }

    public void setParticleType2(byte particleType2) {
        this.particleType2 = particleType2;
    }

    public float getTimeDividerMain() {
        return timeDividerMain;
    }

    public void setTimeDividerMain(float timeDividerMain) {
        this.timeDividerMain = timeDividerMain;
    }

    public float getElapsedTimeRemained() {
        return elapsedTimeRemained;
    }

    public void setElapsedTimeRemained(float elapsedTimeRemained) {
        this.elapsedTimeRemained = elapsedTimeRemained;
    }

    public float getParticleType2Case6GlobalMtxMultiplier() {
        return particleType2Case6GlobalMtxMultiplier;
    }

    public void setParticleType2Case6GlobalMtxMultiplier(float particleType2Case6GlobalMtxMultiplier) {
        this.particleType2Case6GlobalMtxMultiplier = particleType2Case6GlobalMtxMultiplier;
    }

    public float getParticleType1RandMultiplier() {
        return particleType1RandMultiplier;
    }

    public void setParticleType1RandMultiplier(float particleType1RandMultiplier) {
        this.particleType1RandMultiplier = particleType1RandMultiplier;
    }

    public float getGlobalMtxMultiplier() {
        return globalMtxMultiplier;
    }

    public void setGlobalMtxMultiplier(float globalMtxMultiplier) {
        this.globalMtxMultiplier = globalMtxMultiplier;
    }

    public float getPartType5GlobalMtxRndMultiplier() {
        return partType5GlobalMtxRndMultiplier;
    }

    public void setPartType5GlobalMtxRndMultiplier(float partType5GlobalMtxRndMultiplier) {
        this.partType5GlobalMtxRndMultiplier = partType5GlobalMtxRndMultiplier;
    }

    public float getParticleTypeCase2SecMinOffset() {
        return particleTypeCase2SecMinOffset;
    }

    public void setParticleTypeCase2SecMinOffset(float particleTypeCase2SecMinOffset) {
        this.particleTypeCase2SecMinOffset = particleTypeCase2SecMinOffset;
    }

    public float getParticleTypeCase2SecMaxOffset() {
        return particleTypeCase2SecMaxOffset;
    }

    public void setParticleTypeCase2SecMaxOffset(float particleTypeCase2SecMaxOffset) {
        this.particleTypeCase2SecMaxOffset = particleTypeCase2SecMaxOffset;
    }

    public float getParticleTypeCase2FirstCentralizedOffsetMin() {
        return particleTypeCase2FirstCentralizedOffsetMin;
    }

    public void setParticleTypeCase2FirstCentralizedOffsetMin(float particleTypeCase2FirstCentralizedOffsetMin) {
        this.particleTypeCase2FirstCentralizedOffsetMin = particleTypeCase2FirstCentralizedOffsetMin;
    }

    public float getParticleTypeCase2FirstCentralizedOffsetMax() {
        return particleTypeCase2FirstCentralizedOffsetMax;
    }

    public void setParticleTypeCase2FirstCentralizedOffsetMax(float particleTypeCase2FirstCentralizedOffsetMax) {
        this.particleTypeCase2FirstCentralizedOffsetMax = particleTypeCase2FirstCentralizedOffsetMax;
    }

    public float getVecMultMin() {
        return vecMultMin;
    }

    public void setVecMultMin(float vecMultMin) {
        this.vecMultMin = vecMultMin;
    }

    public float getVecMultMax() {
        return vecMultMax;
    }

    public void setVecMultMax(float vecMultMax) {
        this.vecMultMax = vecMultMax;
    }

    public float getVecMult2WOunkFlags4Min() {
        return vecMult2WOunkFlags4Min;
    }

    public void setVecMult2WOunkFlags4Min(float vecMult2WOunkFlags4Min) {
        this.vecMult2WOunkFlags4Min = vecMult2WOunkFlags4Min;
    }

    public float getVecMult2WOunkFlags4Max() {
        return vecMult2WOunkFlags4Max;
    }

    public void setVecMult2WOunkFlags4Max(float vecMult2WOunkFlags4Max) {
        this.vecMult2WOunkFlags4Max = vecMult2WOunkFlags4Max;
    }

    public float getRandMinVal1() {
        return randMinVal1;
    }

    public void setRandMinVal1(float randMinVal1) {
        this.randMinVal1 = randMinVal1;
    }

    public float getRandMaxVal1() {
        return randMaxVal1;
    }

    public void setRandMaxVal1(float randMaxVal1) {
        this.randMaxVal1 = randMaxVal1;
    }

    public float getRandMinVal2() {
        return randMinVal2;
    }

    public void setRandMinVal2(float randMinVal2) {
        this.randMinVal2 = randMinVal2;
    }

    public float getRandMaxVal2() {
        return randMaxVal2;
    }

    public void setRandMaxVal2(float randMaxVal2) {
        this.randMaxVal2 = randMaxVal2;
    }

    public float getGlobalMinMultiplier() {
        return globalMinMultiplier;
    }

    public void setGlobalMinMultiplier(float globalMinMultiplier) {
        this.globalMinMultiplier = globalMinMultiplier;
    }

    public float getGlobalMaxMultiplier() {
        return globalMaxMultiplier;
    }

    public void setGlobalMaxMultiplier(float globalMaxMultiplier) {
        this.globalMaxMultiplier = globalMaxMultiplier;
    }

    public float getTwoShortsMultiplierIfUnkFlagsHas20() {
        return twoShortsMultiplierIfUnkFlagsHas20;
    }

    public void setTwoShortsMultiplierIfUnkFlagsHas20(float twoShortsMultiplierIfUnkFlagsHas20) {
        this.twoShortsMultiplierIfUnkFlagsHas20 = twoShortsMultiplierIfUnkFlagsHas20;
    }

    public Vector3 getVec() {
        return vec;
    }

    public void setVec(Vector3 vec) {
        this.vec = vec;
    }

    public float getUnkFlagsHas2000Multiplier() {
        return unkFlagsHas2000Multiplier;
    }

    public void setUnkFlagsHas2000Multiplier(float unkFlagsHas2000Multiplier) {
        this.unkFlagsHas2000Multiplier = unkFlagsHas2000Multiplier;
    }

    public float getSize2Min() {
        return size2Min;
    }

    public void setSize2Min(float size2Min) {
        this.size2Min = size2Min;
    }

    public float getNegateOffsetMultiplierForUnkflags400() {
        return negateOffsetMultiplierForUnkflags400;
    }

    public void setNegateOffsetMultiplierForUnkflags400(float negateOffsetMultiplierForUnkflags400) {
        this.negateOffsetMultiplierForUnkflags400 = negateOffsetMultiplierForUnkflags400;
    }

    public float getSize2Max() {
        return size2Max;
    }

    public void setSize2Max(float size2Max) {
        this.size2Max = size2Max;
    }

    public float getNegateOffsetMultiplierForUnkflags800() {
        return negateOffsetMultiplierForUnkflags800;
    }

    public void setNegateOffsetMultiplierForUnkflags800(float negateOffsetMultiplierForUnkflags800) {
        this.negateOffsetMultiplierForUnkflags800 = negateOffsetMultiplierForUnkflags800;
    }

    public float getRand1MaxMultiplier() {
        return rand1MaxMultiplier;
    }

    public void setRand1MaxMultiplier(float rand1MaxMultiplier) {
        this.rand1MaxMultiplier = rand1MaxMultiplier;
    }

    public float getRand1MinMultiplier() {
        return rand1MinMultiplier;
    }

    public void setRand1MinMultiplier(float rand1MinMultiplier) {
        this.rand1MinMultiplier = rand1MinMultiplier;
    }

    public float getRandAdditionalMinMultiplier() {
        return randAdditionalMinMultiplier;
    }

    public void setRandAdditionalMinMultiplier(float randAdditionalMinMultiplier) {
        this.randAdditionalMinMultiplier = randAdditionalMinMultiplier;
    }

    public float getRandAdditionalMaxMultiplier() {
        return randAdditionalMaxMultiplier;
    }

    public void setRandAdditionalMaxMultiplier(float randAdditionalMaxMultiplier) {
        this.randAdditionalMaxMultiplier = randAdditionalMaxMultiplier;
    }

    public float getDecalXMultipilier() {
        return decalXMultipilier;
    }

    public void setDecalXMultipilier(float decalXMultipilier) {
        this.decalXMultipilier = decalXMultipilier;
    }

    public float getDecalYMultipilier() {
        return decalYMultipilier;
    }

    public void setDecalYMultipilier(float decalYMultipilier) {
        this.decalYMultipilier = decalYMultipilier;
    }

    public float getDisableUnkFlagsCase400IfThisSet() {
        return disableUnkFlagsCase400IfThisSet;
    }

    public void setDisableUnkFlagsCase400IfThisSet(float disableUnkFlagsCase400IfThisSet) {
        this.disableUnkFlagsCase400IfThisSet = disableUnkFlagsCase400IfThisSet;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Float getTimeDividerMainUNUSED() {
        return timeDividerMainUNUSED;
    }

    public void setTimeDividerMainUNUSED(Float timeDividerMainUNUSED) {
        this.timeDividerMainUNUSED = timeDividerMainUNUSED;
    }

    public float getElapsedtimeRelated() {
        return elapsedtimeRelated;
    }

    public void setElapsedtimeRelated(float elapsedtimeRelated) {
        this.elapsedtimeRelated = elapsedtimeRelated;
    }

    public ParticleElem[] getPAGElemData() {
        return PAGElemData;
    }

    public int getRenderingCounter() {
        return renderingCounter;
    }

    public void setRenderingCounter(int renderingCounter) {
        this.renderingCounter = renderingCounter;
    }

    public BFGaoChunk getGaoForVertices() {
        return gaoForVertices;
    }

    public void setGaoForVertices(BFGaoChunk gaoForVertices) {
        this.gaoForVertices = gaoForVertices;
    }

    public BFGaoChunk getGaoForGlobalMatrix() {
        return gaoForGlobalMatrix;
    }

    public void setGaoForGlobalMatrix(BFGaoChunk gaoForGlobalMatrix) {
        this.gaoForGlobalMatrix = gaoForGlobalMatrix;
    }

    
    
    public void reset() {
        flags = flagsBackup;
        timeDividerMain = timeDividerMainBackup;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            sb.append("GAOInnerParserParticleGenerator:").append(System.lineSeparator());
            sb.append(String.format("maxPAGVertNum: %d, type: %d (%x)", maxPAGVertNum, type, type)).append(System.lineSeparator());
            sb.append(String.format("flags: %x (%d)", flags, flags)).append(System.lineSeparator());
            sb.append(String.format("particleType: %x (%d)", particleType, particleType)).append(System.lineSeparator());
            sb.append(String.format("particleType2: %x (%d)", particleType2, particleType2)).append(System.lineSeparator());
            sb.append(String.format("timeDividerMain: %f", timeDividerMain)).append(System.lineSeparator());
            sb.append(String.format("timeDividerMainUNUSED: %f", timeDividerMainUNUSED)).append(System.lineSeparator());
            sb.append(String.format("elapsedTimeRemained: %f", elapsedTimeRemained)).append(System.lineSeparator());
            sb.append(String.format("particleType2Case6GlobalMtxMultiplier: %f", particleType2Case6GlobalMtxMultiplier)).append(System.lineSeparator());
            sb.append(String.format("particleType1RandMultiplier: %f", particleType1RandMultiplier)).append(System.lineSeparator());
            sb.append(String.format("globalMtxMultiplier: %f", globalMtxMultiplier)).append(System.lineSeparator());
            sb.append(String.format("partType5GlobalMtxRndMultiplier: %f", partType5GlobalMtxRndMultiplier)).append(System.lineSeparator());
            sb.append(String.format("particleTypeCase2SecMinOffset: %f", particleTypeCase2SecMinOffset)).append(System.lineSeparator());
            sb.append(String.format("particleTypeCase2SecMaxOffset: %f", particleTypeCase2SecMaxOffset)).append(System.lineSeparator());
            sb.append(String.format("particleTypeCase2FirstCentralizedOffsetMin: %f", particleTypeCase2FirstCentralizedOffsetMin)).append(System.lineSeparator());
            sb.append(String.format("particleTypeCase2FirstCentralizedOffsetMax: %f", particleTypeCase2FirstCentralizedOffsetMax)).append(System.lineSeparator());
            sb.append(String.format("vecMultMin: %f", vecMultMin)).append(System.lineSeparator());
            sb.append(String.format("vecMultMax: %f", vecMultMax)).append(System.lineSeparator());
            sb.append(String.format("vecMult2WOunkFlags4Min: %f", vecMult2WOunkFlags4Min)).append(System.lineSeparator());
            sb.append(String.format("vecMult2WOunkFlags4Max: %f", vecMult2WOunkFlags4Max)).append(System.lineSeparator());
            sb.append(String.format("randMinVal1: %f", randMinVal1)).append(System.lineSeparator());
            sb.append(String.format("randMaxVal1: %f", randMaxVal1)).append(System.lineSeparator());
            sb.append(String.format("randMinVal2: %f", randMinVal2)).append(System.lineSeparator());
            sb.append(String.format("randMaxVal2: %f", randMaxVal2)).append(System.lineSeparator());
            sb.append(String.format("globalMinMultiplier: %f", globalMinMultiplier)).append(System.lineSeparator());
            sb.append(String.format("globalMaxMultiplier: %f", globalMaxMultiplier)).append(System.lineSeparator());
            sb.append(String.format("twoShortsMultiplierIfUnkFlagsHas20: %f", twoShortsMultiplierIfUnkFlagsHas20)).append(System.lineSeparator());
            sb.append("vec: ").append(vec).append(System.lineSeparator());
            sb.append(String.format("unkFlagsHas2000Multiplier: %f", unkFlagsHas2000Multiplier)).append(System.lineSeparator());
            sb.append(String.format("size2Min: %f", size2Min)).append(System.lineSeparator());
            sb.append(String.format("negateOffsetMultiplierForUnkflags400: %f", negateOffsetMultiplierForUnkflags400)).append(System.lineSeparator());
            sb.append(String.format("size2Max: %f", size2Max)).append(System.lineSeparator());
            sb.append(String.format("negateOffsetMultiplierForUnkflags800: %f", negateOffsetMultiplierForUnkflags800)).append(System.lineSeparator());
            sb.append(String.format("rand1MaxMultiplier: %f", rand1MaxMultiplier)).append(System.lineSeparator());
            sb.append(String.format("rand1MinMultiplier: %f", rand1MinMultiplier)).append(System.lineSeparator());
            sb.append(String.format("randAdditionalMinMultiplier: %f", randAdditionalMinMultiplier)).append(System.lineSeparator());
            sb.append(String.format("randAdditionalMaxMultiplier: %f", randAdditionalMaxMultiplier)).append(System.lineSeparator());
            sb.append(String.format("decalXMultipilier: %f", decalXMultipilier)).append(System.lineSeparator());
            sb.append(String.format("decalYMultipilier: %f", decalYMultipilier)).append(System.lineSeparator());
            sb.append(String.format("disableUnkFlagsCase400IfThisSet: %f", disableUnkFlagsCase400IfThisSet)).append(System.lineSeparator());
            sb.append(String.format("color: %x", color)).append(System.lineSeparator());

            if (!insideBinFile) {
                sb.append(String.format("b0_nb1: %x (%d)", b0_nb1, b0_nb1)).append(System.lineSeparator());
                sb.append(String.format("i0_nb1: %x (%d)", i0_nb1, i0_nb1)).append(System.lineSeparator());
                sb.append(String.format("i0_nb2: %x (%d)", i0_nb2, i0_nb2)).append(System.lineSeparator());
                sb.append(String.format("i0_nb3: %x (%d)", i0_nb3, i0_nb3)).append(System.lineSeparator());
                sb.append(String.format("i0_nb4: %x (%d)", i0_nb4, i0_nb4)).append(System.lineSeparator());
                sb.append(String.format("i0_nb5: %x (%d)", i0_nb5, i0_nb5)).append(System.lineSeparator());
                sb.append(String.format("i0_nb6: %x (%d)", i0_nb6, i0_nb6)).append(System.lineSeparator());
            }
            return sb.toString();
        } else {
            return "GAOInnerParserParticleGenerator";
        }
    }

}
