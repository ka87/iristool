/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFlib;

import Common.Convert;

/**
 *
 * @author andras
 */
public class BFFatTableElem {
    private Integer position;
    private Integer key;

    public BFFatTableElem() {
        
    }
    
    public BFFatTableElem(Integer position, Integer key) {
        this.position = position;
        this.key = key;
    }
    
    public Long getPosition() {
        return Convert.intToLongUnsigned(position);
    }

    public void setPosition(Integer position) {        
        this.position = position;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }
}
