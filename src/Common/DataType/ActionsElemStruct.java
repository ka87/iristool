/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public class ActionsElemStruct {

    // transitionDataArrMemOffset: theoretically original game mem address of transitiondata corresponding of selected action, we can find out from current actionid'th elem and actionid-to-set'th elem
    private Integer eventsKey, transitionDataArrMemOffset, animationShapeKey;
    private Byte loopNum, b2, frequency, b4, b5;
    private Short flags;
    private ActionsElemTransitionStruct ats;
    private Byte frequencyCalculated;

    public ActionsElemStruct(Integer eventskey, Integer transitionDataArrMemOffset, Integer animationShapeKey, Byte loopNum, Byte b2, Byte frequency, Byte b4, Byte b5) {
        this.eventsKey = eventskey;
        this.transitionDataArrMemOffset = transitionDataArrMemOffset;
        this.animationShapeKey = animationShapeKey;
        this.loopNum = loopNum;
        this.b2 = b2;
        this.frequency = frequency;
        _setFrequencyCalculated();
        this.b4 = b4;
        this.b5 = b5;
    }

    public ActionsElemStruct() {
    }

    public ActionsElemTransitionStruct getATS() {
        return ats;
    }

    public void setATS(ActionsElemTransitionStruct ats) {
        this.ats = ats;
    }

    public Integer getEventsKey() {
        return eventsKey;
    }

    public void setEventsKey(Integer eventskey) {
        this.eventsKey = eventskey;
    }

    public Integer getTransitionDataArrMemOffset() {
        return transitionDataArrMemOffset;
    }

    public void setTransitionDataArrMemOffset(Integer unk1) {
        this.transitionDataArrMemOffset = unk1;
    }

    public boolean hasTransition() {
        return !this.transitionDataArrMemOffset.equals(0);
    }

    public Integer getAnimationShapeKey() {
        return animationShapeKey;
    }

    public void setAnimationShapeKey(Integer animationShapeKey) {
        this.animationShapeKey = animationShapeKey;
    }

    public Byte getLoopNum() {
        return loopNum;
    }

    public Integer getLoopNumAsInt() {
        return Byte.toUnsignedInt(loopNum);
    }
    
    public void setLoopNum(Byte loopNum) {
        this.loopNum = loopNum;
    }

    public Byte getB2() {
        return b2;
    }

    public void setB2(Byte b2) {
        this.b2 = b2;
    }

    public Byte getFrequency() {
        return frequency;
    }

    public void setFrequency(Byte frequency) {
        this.frequency = frequency;
        _setFrequencyCalculated();
    }

    public Byte getB4() {
        return b4;
    }

    public void setB4(Byte b4) {
        this.b4 = b4;
    }

    public Byte getB5() {
        return b5;
    }

    public void setB5(Byte b5) {
        this.b5 = b5;
    }

    public Short getFlags() {
        return flags;
    }

    public void setFlags(Short flags) {
        this.flags = flags;
    }

    public boolean readValues(ReaderUtil ru) {
        eventsKey = ru.readInt(false);
        transitionDataArrMemOffset = ru.readInt(false);
        animationShapeKey = ru.readInt(false);
        loopNum = ru.readByte(false);
        b2 = ru.readByte(false);
        frequency = ru.readByte(false);
        _setFrequencyCalculated();
        b4 = ru.readByte(false);
        flags = ru.readShort(false);
        b5 = ru.readByte(false);

        return (eventsKey != null && transitionDataArrMemOffset != null && animationShapeKey != null
                && loopNum != null && b2 != null && frequency != null && b4 != null
                && flags != null && b5 != null);
    }

    public void writeValues(WriterUtil wu) {
        wu.writeInt(eventsKey);
        wu.writeInt(transitionDataArrMemOffset);
        wu.writeInt(animationShapeKey);
        wu.writeByte(loopNum);
        wu.writeByte(b2);
        wu.writeByte(frequency);
        wu.writeByte(b4);
        wu.writeShort(flags);
        wu.writeByte(b5);
    }

    private void _setFrequencyCalculated() {
        this.frequencyCalculated = (byte)((frequency * 60) >> 6); // TODO: check if needed: in game adds int64's (4th byte & 0x3f) to this
    }
    
    @Override
    public String toString() {
        return String.format("Action: eventsKey=%x, animationShapeKey=%x, %s, transitionDataArrMemOffset=%x, loopNum=%d, b2=%d(%x), freq=%d(%x), freq calcd=%d(%x), b4=%d(%x) flags=%x, b5=%d(%x)",
                eventsKey, animationShapeKey, (hasTransition() ? "Has transition" : "No transition"), transitionDataArrMemOffset, Byte.toUnsignedInt(loopNum), b2, b2, frequency, frequency, frequencyCalculated, frequencyCalculated, b4, b4, flags, b5, b5);
    }
}
