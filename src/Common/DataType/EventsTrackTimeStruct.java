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
public class EventsTrackTimeStruct {

    private float elapsedTime;
    private float totalTime;

    public EventsTrackTimeStruct(float elapsedTime, float totalTime) {
        this.elapsedTime = elapsedTime;
        this.totalTime = totalTime;
    }    
    
    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }
    
}
