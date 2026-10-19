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
public class GeometryType1InnerData extends Logger {

     private Short type1Unk2;
     private Short type1Unk3Num;
     private Short type1Unk4Arr[];

    public GeometryType1InnerData(Short type1Unk2, Short type1Unk3Num) {
        this.type1Unk2 = type1Unk2;
        this.type1Unk3Num = type1Unk3Num;
        type1Unk4Arr = new Short[type1Unk3Num];
    }

    public Short getType1Unk2() {
        return type1Unk2;
    }

    public void setType1Unk2(Short type1Unk2) {
        this.type1Unk2 = type1Unk2;
    }

    public Short getType1Unk3Num() {
        return type1Unk3Num;
    }

    public void setType1Unk3Num(Short type1Unk3Num) {
        this.type1Unk3Num = type1Unk3Num;
        type1Unk4Arr = new Short[type1Unk3Num];
    }

    public Short[] getType1Unk4Arr() {
        return type1Unk4Arr;
    }    
}
