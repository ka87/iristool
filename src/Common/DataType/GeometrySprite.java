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
public class GeometrySprite extends Logger {

    private Float spDimension1;
    private Float spDimension2;
    private Integer spMaterialID;
    private Integer spElemNum;
    private GeometrySpriteElem geoSpriteElem[];

    public Float getSpDimension1() {
        return spDimension1;
    }

    public void setSpDimension1(Float spDimension1) {
        this.spDimension1 = spDimension1;
    }

    public Float getSpDimension2() {
        return spDimension2;
    }

    public void setSpDimension2(Float spDimension2) {
        this.spDimension2 = spDimension2;
    }

    public Integer getSpMaterialID() {
        return spMaterialID;
    }

    public void setSpMaterialID(Integer spMaterialID) {
        this.spMaterialID = spMaterialID;
    }

    public Integer getSpElemNum() {
        return spElemNum;
    }

    public void setSpElemNum(Integer spElemNum) {
        if (this.geoSpriteElem.length < spElemNum) {
            System.out.println("GeometrySprite: elem size increase not supported.");
            return;
        }
        this.spElemNum = spElemNum;
    }

    public GeometrySpriteElem[] getGeoSpriteElem() {
        return geoSpriteElem;
    }

    public GeometrySprite(Float spDimension1, Float spDimension2, Integer spMaterialID, Integer spElemNum) {
        this.spDimension1 = spDimension1;
        this.spDimension2 = spDimension2;
        this.spMaterialID = spMaterialID;
        this.spElemNum = spElemNum;
        this.geoSpriteElem = new GeometrySpriteElem[spElemNum];
        for (int i = 0; i < spElemNum; i++) {
            this.geoSpriteElem[i] = new GeometrySpriteElem((short) 0, (short) 0);
        }
    }

    @Override
    public String toString() {
        return String.format("Sprite: spDimension1=%f spDimension2=%f spMaterialID=%d (0x%x) spElemNum=%d (0x%x)", spDimension1, spDimension2, spMaterialID, spMaterialID, spElemNum, spElemNum);
    }

}
