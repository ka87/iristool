/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.ReaderUtil;

/**
 *
 * @author andra
 */
public interface WaveFormatInterface {
 
    public float getSoundLengthInSecs();
    public int getHeaderSize();
    public Short getFormat();
    public boolean readValues(ReaderUtil ru, boolean full);
    public void setParseModeHDPak(boolean b);
    public int getSize();
    public void setData(byte[] data);
    public byte[] getData();
    public void setHeaderFirstPart(Integer riff, Integer chunkSize, Integer wave, Integer fmt, Integer subChunkSize, Integer offset1, Integer len2, Integer offset2);
    public Short getNumChannels();
    public Integer getSampleRate();
    public Short getBlockAlign();
    public byte[] getAssembledWavData();
    public void readUnkData(ReaderUtil ru);
    public byte[] getWaveData();
    public byte[] getUnk1Data();
    public byte[] getUnk2Data();
}
