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
public class GeometryTriangleStripChunk extends Logger {

    private Integer funkdata;
    private GeometryTriangleStripElement stripElements[];

    public Integer getFunkdata() {
        return funkdata;
    }

    public void setFunkdata(Integer funkdata) {
        this.funkdata = funkdata;
    }

    public GeometryTriangleStripElement[] getTriangleStripElements() {
        return stripElements;
    }

    public void setGeoTFCElemNum(int len) {
        this.stripElements = new GeometryTriangleStripElement[len];
    }

}
