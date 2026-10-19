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
public class PlayTrackReturnTimeAndShouldCallChangeAction {
    private Boolean changeActionCallNeeded;
    private Float time;

    public Boolean getChangeActionCallNeeded() {
        return changeActionCallNeeded;
    }

    public void setChangeActionCallNeeded(Boolean changeActionCallNeeded) {
        this.changeActionCallNeeded = changeActionCallNeeded;
    }

    public float getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    public PlayTrackReturnTimeAndShouldCallChangeAction(Boolean success, Float time) {
        this.changeActionCallNeeded = success;
        this.time = time;
    }        
}
