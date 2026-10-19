/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;

/**
 *
 * @author andra
 */
public class GeometryTriangleStripElement extends Logger {

    private Short funkdata21;
    private Short funkdata22;

    public Short getFunkdata21() {
        return funkdata21;
    }

    public void setFunkdata21(Short funkdata21) {
        this.funkdata21 = funkdata21;
    }

    public Short getFunkdata22() {
        return funkdata22;
    }

    public void setFunkdata22(Short funkdata22) {
        this.funkdata22 = funkdata22;
    }

    public GeometryTriangleStripElement(Short funkdata21, Short funkdata22) {
        this.funkdata21 = funkdata21;
        this.funkdata22 = funkdata22;
    }
    
}
