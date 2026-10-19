/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import BFDataFileLib.BFChunkParsers.Map.BFEventsChunk;
import java.util.ArrayList;

/**
 *
 * @author andra
 */
public class AnimationPlayerTrackData {
        private ArrayList<EventTrackOption> trackOptions = new ArrayList<>();
    private BFEventsChunk eventTracks = null;
     public ArrayList<EventTrackOption> getTrackOptions() {
        return trackOptions;
    }

    public void setTrackOptions(ArrayList<EventTrackOption> trackOptions) {
        this.trackOptions = trackOptions;
    }

    public BFEventsChunk getEventTracks() {
        return eventTracks;
    }

    public void setEventTracks(BFEventsChunk eventTracks) {
        this.eventTracks = eventTracks;
    }
    
}
