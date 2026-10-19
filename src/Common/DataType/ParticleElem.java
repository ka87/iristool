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
public class ParticleElem {

    private int particleState; // 1 spawning, 2 dying, 0 living
    private float posX;
    private float posY;
    private float posZ;
    private float vertPlusOffsetX;
    private float vertPlusOffsetY;
    private float vertPlusOffsetZ;
    private float invElapsedTimeDownCount;
    private float elapsedTimeMinDownCount;
    private float elapsedTimeMinUpCount;
    private float particleSize1;
    private float particleSize2;
    private int unkCounter;

    public int getParticleState() {
        return particleState;
    }

    public void setParticleState(int particleState) {
        this.particleState = particleState;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosZ() {
        return posZ;
    }

    public void setPosZ(float posZ) {
        this.posZ = posZ;
    }

    public float getVertPlusOffsetX() {
        return vertPlusOffsetX;
    }

    public void setVertPlusOffsetX(float vertPlusOffsetX) {
        this.vertPlusOffsetX = vertPlusOffsetX;
    }

    public float getVertPlusOffsetY() {
        return vertPlusOffsetY;
    }

    public void setVertPlusOffsetY(float vertPlusOffsetY) {
        this.vertPlusOffsetY = vertPlusOffsetY;
    }

    public float getVertPlusOffsetZ() {
        return vertPlusOffsetZ;
    }

    public void setVertPlusOffsetZ(float vertPlusOffsetZ) {
        this.vertPlusOffsetZ = vertPlusOffsetZ;
    }

    public float getInvElapsedTimeDownCount() {
        return invElapsedTimeDownCount;
    }

    public void setInvElapsedTimeDownCount(float invElapsedTimeDownCount) {
        this.invElapsedTimeDownCount = invElapsedTimeDownCount;
    }

    public float getElapsedTimeMinDownCount() {
        return elapsedTimeMinDownCount;
    }

    public void setElapsedTimeMinDownCount(float elapsedTimeMinDownCount) {
        this.elapsedTimeMinDownCount = elapsedTimeMinDownCount;
    }

    public float getElapsedTimeMinUpCount() {
        return elapsedTimeMinUpCount;
    }

    public void setElapsedTimeMinUpCount(float elapsedTimeMinUpCount) {
        this.elapsedTimeMinUpCount = elapsedTimeMinUpCount;
    }

    public float getParticleSize1() {
        return particleSize1;
    }

    public void setParticleSize1(float particleSize1) {
        this.particleSize1 = particleSize1;
    }

    public float getParticleSize2() {
        return particleSize2;
    }

    public void setDecalOffsetForCheck(float decalOffsetForCheck) {
        this.particleSize2 = decalOffsetForCheck;
    }

    public int getUnkCounter() {
        return unkCounter;
    }

    public void setUnkCounter(int unkCounter) {
        this.unkCounter = unkCounter;
    }

    public void set(ParticleElem pe) {
        particleState = pe.particleState;
        posX = pe.posX;
        posY = pe.posY;
        posZ = pe.posZ;
        vertPlusOffsetX = pe.vertPlusOffsetX;
        vertPlusOffsetY = pe.vertPlusOffsetY;
        vertPlusOffsetZ = pe.vertPlusOffsetZ;
        invElapsedTimeDownCount = pe.invElapsedTimeDownCount;
        elapsedTimeMinDownCount = pe.elapsedTimeMinDownCount;
        elapsedTimeMinUpCount = pe.elapsedTimeMinUpCount;
        particleSize1 = pe.particleSize1;
        particleSize2 = pe.particleSize2;
        unkCounter = pe.unkCounter;
    }

    @Override
    public String toString() {
        return String.format("ParticleElem: particleState: %d, posX: %f, posY: %f, posZ: %f, vertPlusOffsetX: %f , vertPlusOffsetY: %f, vertPlusOffsetZ: %f, invElapsedTimeDownCount: %f, elapsedTimeMinDownCount: %f, elapsedTimeMinUpCount: %f, particleSize1: %f, particleSize2: %f unkCounter: %d (%x)",
                particleState, posX, posY, posZ, vertPlusOffsetX, vertPlusOffsetY, vertPlusOffsetZ, invElapsedTimeDownCount, elapsedTimeMinDownCount, elapsedTimeMinUpCount, particleSize1, particleSize2, unkCounter, unkCounter);
    }

}
