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
public class EventsSearchLabelStruct {
    private float sumTime;
    private int elemIndex;

    public float getSumTime() {
        return sumTime;
    }

    public void setSumTime(float sumTime) {
        this.sumTime = sumTime;
    }

    public int getElemIndex() {
        return elemIndex;
    }

    public void setElemIndex(int elemIndex) {
        this.elemIndex = elemIndex;
    }

    public EventsSearchLabelStruct(float sumTime, int elemIndex) {
        this.sumTime = sumTime;
        this.elemIndex = elemIndex;
    }    
}
