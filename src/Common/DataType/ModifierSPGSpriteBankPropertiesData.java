/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;
import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public class ModifierSPGSpriteBankPropertiesData extends Logger {

    private Integer matID;
    private Float dimensionSize1Multiplier, randomFactorMultiplier, dimensionSize2Multiplier;

    public ModifierSPGSpriteBankPropertiesData() {
    }

    public Integer getMatID() {
        return matID;
    }

    public void setMatID(Integer matID) {
        this.matID = matID;
    }

    public Float getDimensionSize1Multiplier() {
        return dimensionSize1Multiplier;
    }

    public void setDimensionSize1Multiplier(Float dimensionSize1Multiplier) {
        this.dimensionSize1Multiplier = dimensionSize1Multiplier;
    }

    public Float getRandomFactorMultiplier() {
        return randomFactorMultiplier;
    }

    public void setRandomFactorMultiplier(Float randomFactorMultiplier) {
        this.randomFactorMultiplier = randomFactorMultiplier;
    }

    public Float getDimensionSize2Multiplier() {
        return dimensionSize2Multiplier;
    }

    public void setDimensionSize2Multiplier(Float dimensionSize2Multiplier) {
        this.dimensionSize2Multiplier = dimensionSize2Multiplier;
    }

    public boolean readValues(ReaderUtil ru) {
        matID = ru.readInt(false);
        dimensionSize1Multiplier = ru.readFloat(false);
        randomFactorMultiplier = ru.readFloat(false);
        dimensionSize2Multiplier = ru.readFloat(false);

        return (this.matID != null && this.dimensionSize1Multiplier != null && this.randomFactorMultiplier != null
                && this.dimensionSize2Multiplier != null);
    }

    public void writeValues(WriterUtil wu) {
        wu.writeInt(matID);
        wu.writeFloat(dimensionSize1Multiplier);
        wu.writeFloat(randomFactorMultiplier);
        wu.writeFloat(dimensionSize2Multiplier);
    }

    public ModifierSPGSpriteBankPropertiesData duplicate() {
        ModifierSPGSpriteBankPropertiesData ret = new ModifierSPGSpriteBankPropertiesData();
        
        ret.dimensionSize1Multiplier = dimensionSize1Multiplier;
        ret.dimensionSize2Multiplier = dimensionSize2Multiplier;
        ret.randomFactorMultiplier = randomFactorMultiplier;
        ret.matID = matID;
        
        return ret;
    }

    @Override
    public String toString() {
        return String.format("ModifierSPGSpriteBankPropertiesData: matID: %d, dimensionSize1Multiplier: %f, randomFactorMultiplier: %f, dimensionSize2Multiplier: %f",
                matID, dimensionSize1Multiplier, randomFactorMultiplier, dimensionSize2Multiplier);
    }
}
