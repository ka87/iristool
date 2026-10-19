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
public class GeometryWholeTriangleStripBlock extends Logger {

   private Integer funk1;
   private Integer funknum;
   private GeometryTriangleStripChunk stripChunks[];

    public Integer getFunk1() {
        return funk1;
    }

    public void setFunk1(Integer funk1) {
        this.funk1 = funk1;
    }

    public Integer getFunknum() {
        return funknum;
    }

    public GeometryTriangleStripChunk[] getTriangleStripChunks() {
        return stripChunks;
    }

    public void setFunknum(Integer funknum) {
        this.funknum = funknum;
        this.stripChunks = new GeometryTriangleStripChunk[funknum];
    }          
}
