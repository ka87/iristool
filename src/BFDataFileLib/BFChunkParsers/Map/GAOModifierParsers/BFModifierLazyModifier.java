/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierLazyModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierLazyModifier() { // eg. PeyJ's ears physics
        super();
    }

    private Float zAxisMultiplier, positionFactorMultiplier;
    private Vector3 limiterVecMin;
    private Vector3 limiterVecMax;
    private Integer type, flags;
    private Float lazyness;
    private Integer unk1;

    // runtime 
    private Matrix4x4 mainGaoMtxCache = new Matrix4x4();
    private Matrix4x4 mainGaoMtx = new Matrix4x4();
    float positionFactorX, positionFactorY, positionFactorZ;
    private Float zAxisMultiplierBakLocal, positionFactorMultiplierBakLocal, lazynessBakLocal;
    private Vector3 limiterVecMinBakLocal = new Vector3();
    private Vector3 limiterVecMaxBakLocal = new Vector3();
    private Integer flagsBakLocal;

    public Vector3 getLimiterVecMinBakLocal() {
        return limiterVecMinBakLocal;
    }

    public Vector3 getLimiterVecMaxBakLocal() {
        return limiterVecMaxBakLocal;
    }

    public Float getZAxisMultiplierBakLocal() {
        return zAxisMultiplierBakLocal;
    }

    public void setZAxisMultiplierBakLocal(Float zAxisMultiplierBakLocal) {
        this.zAxisMultiplierBakLocal = zAxisMultiplierBakLocal;
    }

    public Float getPositionFactorMultiplierBakLocal() {
        return positionFactorMultiplierBakLocal;
    }

    public void setPositionFactorMultiplierBakLocal(Float positionFactorMultiplierBakLocal) {
        this.positionFactorMultiplierBakLocal = positionFactorMultiplierBakLocal;
    }

    public Float getLazynessBakLocal() {
        return lazynessBakLocal;
    }

    public void setLazynessBakLocal(Float lazynessBakLocal) {
        this.lazynessBakLocal = lazynessBakLocal;
    }

    public Integer getFlagsBakLocal() {
        return flagsBakLocal;
    }

    public void setFlagsBakLocal(Integer flagsBakLocal) {
        this.flagsBakLocal = flagsBakLocal;
    }

    public Matrix4x4 getMainGaoMtxCache() {
        return mainGaoMtxCache;
    }

    public Matrix4x4 getMainGaoMtx() {
        return mainGaoMtx;
    }

    public Float getLazyness() {
        return lazyness;
    }

    public void setLazyness(Float lazyness) {
        this.lazyness = lazyness;
    }

    public float getPositionFactorX() {
        return positionFactorX;
    }

    public float getPositionFactorY() {
        return positionFactorY;
    }

    public float getPositionFactorZ() {
        return positionFactorZ;
    }

    public void setPositionFactorX(float positionFactorX) {
        this.positionFactorX = positionFactorX;
    }

    public void setPositionFactorY(float positionFactorY) {
        this.positionFactorY = positionFactorY;
    }

    public void setPositionFactorZ(float positionFactorZ) {
        this.positionFactorZ = positionFactorZ;
    }

    public Float getZAxisMultiplier() {
        return zAxisMultiplier;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public Float getPositionFactorMultiplier() {
        return positionFactorMultiplier;
    }

    public Vector3 getLimiterVecMin() {
        return limiterVecMin;
    }

    public Vector3 getLimiterVecMax() {
        return limiterVecMax;
    }

    public void setZAxisMultiplier(Float zAxisMultiplier) {
        this.zAxisMultiplier = zAxisMultiplier;
    }

    public void setPositionFactorMultiplier(Float positionFactorMultiplier) {
        this.positionFactorMultiplier = positionFactorMultiplier;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Lazy Modifier.");
        }

        if (!insideBinFile) {
            if (!buildFromParsed) {
                unk1 = ru.readInt(false);
            } else {
                wu.writeInt(unk1);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk1 (!insidebinfile): %d (%x)", unk1, unk1);
            }
        }

        if (!buildFromParsed) {
            type = ru.readInt(false);
            lazyness = ru.readFloat(false);
            flags = ru.readInt(false);
        } else {
            wu.writeInt(type);
            wu.writeFloat(lazyness);
            wu.writeInt(flags);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Type: %d (%x)", type, type);
            logF("lazyness: %f", lazyness);
            logF("flags: %x", flags);
        }

        if (type != 0) {
            if (!buildFromParsed) {
                limiterVecMin = new Vector3();
                if (!limiterVecMin.readValues(ru)) {
                    log("Vector3 read failed.");
                    return false;
                }
                limiterVecMax = new Vector3();
                if (!limiterVecMax.readValues(ru)) {
                    log("Vector3 read failed.");
                    return false;
                }
            } else {
                limiterVecMin.writeValues(wu);
                limiterVecMax.writeValues(wu);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("limiterVecMin: %s", limiterVecMin);
                logF("limiterVecMax: %s", limiterVecMax);
            }
            if (type > 1) {
                if (!buildFromParsed) {
                    zAxisMultiplier = ru.readFloat(false);
                } else {
                    wu.writeFloat(zAxisMultiplier);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("zAxisMultiplier: %f", zAxisMultiplier);
                }
            }
            if (type > 2) {
                if (!buildFromParsed) {
                    positionFactorMultiplier = ru.readFloat(false);
                } else {
                    wu.writeFloat(positionFactorMultiplier);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("positionFactorMultiplier: %f", positionFactorMultiplier);
                }
            }
        }

        return true;
    }

    @Override
    public BFModifierLazyModifier duplicate() {
        BFModifierLazyModifier tmp = new BFModifierLazyModifier();
        tmp.setSelfKey(this.getSelfKey());
        tmp.setHandleFlags(this.getHandleFlags());

        if (limiterVecMin != null) {
            tmp.limiterVecMin = new Vector3();
            tmp.limiterVecMin.set(this.limiterVecMin);
        }
        if (limiterVecMax != null) {
            tmp.limiterVecMax = new Vector3();
            tmp.limiterVecMax.set(this.limiterVecMax);
        }

        tmp.zAxisMultiplier = this.zAxisMultiplier;
        tmp.positionFactorMultiplier = this.positionFactorMultiplier;
        tmp.type = this.type;
        tmp.flags = this.flags;
        tmp.lazyness = this.lazyness;
        tmp.unk1 = this.unk1;

        return tmp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            sb.append("LazyModifier: ");
            sb.append(String.format("Type: %d (%x), ", type, type));
            sb.append(String.format("lazyness: %f, ", lazyness));
            sb.append(String.format("flags: %x, ", flags));
            sb.append(String.format("zAxisMultiplier: %f, ", zAxisMultiplier));
            sb.append(String.format("positionFactorMultiplier: %f, ", positionFactorMultiplier));
            sb.append(String.format("limiterVecMin: %s, ", limiterVecMin));
            sb.append(String.format("limiterVecMax: %s", limiterVecMax));
        } else {
            sb.append("LazyModifier");
        }

        return sb.toString();
    }

}
