/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;
import Common.ReaderUtil;

/**
 *
 * @author andras
 */
public class VariableTypeMessage extends Logger {

    private Integer dataI1, dataI2;
    private Vector3 pos1 = new Vector3(), pos2 = new Vector3();
    private Float dataF1, dataF2;
    private Integer dataI3, dataI4, dataI5;

    public VariableTypeMessage() {
    }

    public VariableTypeMessage(Integer type41DataI1, Integer type41DataI2, Float type41DataF1, Float type41DataF2, Integer type41DataI3, Integer type41DataI4, Integer type41DataI5) {
        this.dataI1 = type41DataI1;
        this.dataI2 = type41DataI2;
        this.dataF1 = type41DataF1;
        this.dataF2 = type41DataF2;
        this.dataI3 = type41DataI3;
        this.dataI4 = type41DataI4;
        this.dataI5 = type41DataI5;
    }

    public Integer getDataI1() {
        return dataI1;
    }

    public void setDataI1(Integer dataI1) {
        this.dataI1 = dataI1;
    }

    public Integer getDataI2() {
        return dataI2;
    }

    public void setDataI2(Integer dataI2) {
        this.dataI2 = dataI2;
    }

    public Vector3 getPos1() {
        return pos1;
    }

    public void setPos1(Vector3 pos1) {
        this.pos1 = pos1;
    }

    public Vector3 getPos2() {
        return pos2;
    }

    public void setPos2(Vector3 pos2) {
        this.pos2 = pos2;
    }

    public Float getDataF1() {
        return dataF1;
    }

    public void setDataF1(Float dataF1) {
        this.dataF1 = dataF1;
    }

    public Float getDataF2() {
        return dataF2;
    }

    public void setDataF2(Float dataF2) {
        this.dataF2 = dataF2;
    }

    public Integer getDataI3() {
        return dataI3;
    }

    public void setDataI3(Integer dataI3) {
        this.dataI3 = dataI3;
    }

    public Integer getDataI4() {
        return dataI4;
    }

    public void setDataI4(Integer dataI4) {
        this.dataI4 = dataI4;
    }

    public Integer getDataI5() {
        return dataI5;
    }

    public void setDataI5(Integer dataI5) {
        this.dataI5 = dataI5;
    }

    public boolean readValues(ReaderUtil ru) {
        dataI1 = ru.readInt(false);
        dataI2 = ru.readInt(false);
        if (!pos1.readValues(ru) || !pos2.readValues(ru)) {
            log("Vector3 read fail.");
            return false;
        }
        dataF1 = ru.readFloat(false);
        dataF2 = ru.readFloat(false);
        dataI3 = ru.readInt(false);
        dataI4 = ru.readInt(false);
        dataI5 = ru.readInt(false);

        return (this.dataI1 != null && this.dataI2 != null && this.pos1 != null && this.pos2 != null
                && this.dataF1 != null && this.dataF2 != null && this.dataI3 != null && this.dataI4 != null
                && this.dataI5 != null);
    }

    public VariableTypeMessage duplicate() {
        VariableTypeMessage ret = new VariableTypeMessage();

        ret.dataF1 = dataF1;
        ret.dataF2 = dataF2;
        ret.dataI1 = dataI1;
        ret.dataI2 = dataI2;
        ret.dataI3 = dataI3;
        ret.dataI4 = dataI4;
        ret.dataI5 = dataI5;
        if (pos1 != null) {
            ret.pos1 = pos1.duplicate();
        }
        if (pos2 != null) {
            ret.pos2 = pos2.duplicate();
        }

        return ret;
    }

    @Override
    public String toString() {
        return String.format("VariableTypeMessage: dataI1: %d (%x), dataI2: %d (%x)", dataI1, dataI1, dataI2, dataI2) + "\n"
                + pos1 + "\n" + pos2 + "\n"
                + String.format("dataF1: %f, dataF2: %f\n", dataF1, dataF2)
                + String.format("dataI3: %d (%x), dataI4: %d (%x), dataI5: %d (%x)", dataI3, dataI3,
                        dataI4, dataI4, dataI5, dataI5);
    }

}
