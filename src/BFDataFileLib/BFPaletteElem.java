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
public class BFPaletteElem extends Logger {

    private int key = -1;   
    private byte paletteData[] = null;

    public BFPaletteElem(int key) {
        this.key = key;       
    }

    public int getKey() {
        return key;
    }

    public byte[] getPaletteData() {
        return paletteData;
    }

    public void setPaletteData(byte[] paletteData) {
        this.paletteData = paletteData;
    }    
}
