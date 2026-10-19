/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

/**
 *
 * @author andra
 */
public class EventTrackOption {

    private float actElemTimeElapsed;
    private float magicBoxBlendFactor;
    private short actElemNum;
    private short flag; // & 1 - running, & 2 - paused

    public float getActElemTimeElapsed() {
        return actElemTimeElapsed;
    }

    public void setActElemTimeElapsed(float actElemTimeElapsed) {
        this.actElemTimeElapsed = actElemTimeElapsed;
    }

    public float getMagicBoxBlendFactor() {
        return magicBoxBlendFactor;
    }

    public void setMagicBoxBlendFactor(float magicBoxBlendFactor) {
        this.magicBoxBlendFactor = magicBoxBlendFactor;
    }

    public short getActElemNum() {
        return actElemNum;
    }

    public void setActElemNum(short actElemNum) {
        this.actElemNum = actElemNum;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

}
