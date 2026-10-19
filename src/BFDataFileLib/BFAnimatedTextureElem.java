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
public class BFAnimatedTextureElem extends Logger {

    private int key;
    private int id;

    public int getKey() {
        return key;
    }
    
    public int getId() {
        return id;
    }

    public BFAnimatedTextureElem(int key, int id) {
        this.key = key;
        this.id = id;
    }       
}
