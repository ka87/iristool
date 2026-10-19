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
public class GeometryType1Data extends Logger {

    private Integer type1UnknownNum;
    private Vector3 pos1;
    private Vector3 pos2;
    private GeometryType1InnerData innerArr[];

    public GeometryType1Data(Integer type1UnknownNum, Vector3 pos1, Vector3 pos2) {
        this.type1UnknownNum = type1UnknownNum;
        this.pos1 = pos1;
        this.pos2 = pos2;
        innerArr = new GeometryType1InnerData[type1UnknownNum];
    }

    public Integer getType1UnknownNum() {
        return type1UnknownNum;
    }

    public void setType1UnknownNum(Integer type1UnknownNum) {
        this.type1UnknownNum = type1UnknownNum;
        innerArr = new GeometryType1InnerData[type1UnknownNum];
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

    public GeometryType1InnerData[] getInnerArr() {
        return innerArr;
    }        
}
