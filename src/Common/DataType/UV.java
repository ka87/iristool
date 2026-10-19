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
public class UV extends Logger {

    private Float u;
    private Float v;

    public Float getU() {
        return u;
    }

    public void setU(Float u) {
        this.u = u;
    }

    public Float getV() {
        return v;
    }

    public void setV(Float v) {
        this.v = v;
    }

    public UV(Float u, Float v) {
        this.u = u;
        this.v = v;
    }

    public UV() {
    }

    public boolean readValues(ReaderUtil ru) {
        this.u = ru.readFloat(false);
        this.v = ru.readFloat(false);

        return (this.u != null && this.v != null);
    }

    @Override
    public String toString() {
        return "UV: U:" + u + ", V:" + v;
    }
}
