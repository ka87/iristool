/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

import Common.Logger;

/**
 *
 * @author andras
 */
public class MaterialSpriteElem extends Logger {

    private Integer key = null;
    private Integer refkey = null;

    public MaterialSpriteElem(Integer key, Integer refkey) {
        this.key = key;
        this.refkey = refkey;
    }

    public Integer getKey() {
        return key;
    }

    public Integer getRefkey() {
        return refkey;
    }            
}
