/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierMeshWave extends BFModifierBaseClass implements BFChunkParseInterface {

    public BFModifierMeshWave() {
        super();
    }

    private Integer unk1;
    private Integer flags;
    private Float offset, intensity, speed, timeMultiplier, offsetBackup;
    private Integer unk3;

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Geo Modifier Mesh Wave (Type 1)");
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
            flags = ru.readInt(false);
            offset = ru.readFloat(false);
            intensity = ru.readFloat(false);
            speed = ru.readFloat(false);
            timeMultiplier = ru.readFloat(false);
            offsetBackup = offset;
            offset = 0.0f;
        } else {
            wu.writeInt(flags);
            wu.writeFloat(offsetBackup);
            wu.writeFloat(intensity);
            wu.writeFloat(speed);
            wu.writeFloat(timeMultiplier);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("flags: %x", flags);
            logF("offset: %f", offset);
            logF("intensity: %f", intensity);
            logF("speed: %f", speed);
            logF("timeMultiplier: %f", timeMultiplier);
        }

        if (!insideBinFile) {
            if (!buildFromParsed) {
                unk3 = ru.readInt(false);
            } else {
                wu.writeInt(unk3);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("unk3 (!insidebinfile): %d (%x)", unk3, unk3);
            }
        }

        return true;
    }

    public Integer getUnk1() {
        return unk1;
    }

    public void setUnk1(Integer unk1) {
        this.unk1 = unk1;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public Float getOffset() {
        return offset;
    }

    public void setOffset(Float offset) {
        this.offset = offset;
    }

    public Float getIntensity() {
        return intensity;
    }

    public void setIntensity(Float intensity) {
        this.intensity = intensity;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getTimeMultiplier() {
        return timeMultiplier;
    }

    public void setTimeMultiplier(Float timeMultiplier) {
        this.timeMultiplier = timeMultiplier;
    }

    public Integer getUnk3() {
        return unk3;
    }

    public void setUnk3(Integer unk3) {
        this.unk3 = unk3;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("MeshWaveModifier: flags: 0x%x, offset: %f, intensity: %f, speed: %f, timeMultiplier: %f",
                    flags, offset, intensity, speed, timeMultiplier)).append(System.lineSeparator());
            sb.append(String.format("unk1: %d (%x), unk3: %d (%x)",
                    unk1, unk1, unk3, unk3)).append(System.lineSeparator());
            return sb.toString();
        } else {
            return "MeshWaveModifierModifier";
        }
    }

    @Override
    public BFModifierMeshWave duplicate() {
        BFModifierMeshWave ret = new BFModifierMeshWave();

        ret.unk1 = unk1;
        ret.flags = flags;
        ret.offset = offset;
        ret.intensity = intensity;
        ret.speed = speed;
        ret.timeMultiplier = timeMultiplier;
        ret.unk3 = unk3;

        return ret;
    }

}
