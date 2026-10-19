/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public class WaveFormatType52 extends Logger implements WaveFormatInterface {

    private boolean full = false;
    private Integer riff;
    private Integer chunkSize;
    private Integer wave;
    private Integer fmt;
    private Integer subChunkSize;
    private Short format;
    private Short numChannels;
    private Integer sampleRate;
    private Integer byteRate;
    private Short blockAlign;
    private Short bitsPerSample;
    private Short extraDataSize;
    private byte[] extraData;
    private Integer seekTag;
    private Integer seekTagLen;
    private byte[] seekData;
    private Integer data;
    private Integer dataSize;
    private byte[] waveData = null;
    private boolean parseModeHDPak = false;
    private int offset1;
    private int offset2;
    private int len2;
    private int offset3;
    private byte[] unk1Data = null;
    private byte[] unk2Data = null;

    public WaveFormatType52() {
    }

    @Override
    public float getSoundLengthInSecs() {
        if (dataSize == null || sampleRate == null || numChannels == null || bitsPerSample == null) {
            return 0.0f;
        }
        return dataSize / (sampleRate * numChannels * bitsPerSample / 8.0f);
    }

    @Override
    public int getHeaderSize() {
        int variableLen = 0;
        if (extraData != null) {
            variableLen += extraData.length;
        }
        if (seekData != null) {
            variableLen += seekData.length;
        }
        return 54 + variableLen; // see readValues() read sum
    }

    @Override
    public Integer getSampleRate() {
        return sampleRate;
    }

    @Override
    public Short getNumChannels() {
        return numChannels;
    }

    @Override
    public Short getBlockAlign() {
        return blockAlign;
    }

    @Override
    public Short getFormat() {
        return format;
    }

    @Override
    public void setData(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        waveData = new byte[data.length];
        System.arraycopy(data, 0, waveData, 0, data.length);
    }

    @Override
    public byte[] getData() {
        return waveData;
    }

    @Override
    public void setHeaderFirstPart(Integer riff, Integer chunkSize, Integer wave, Integer fmt, Integer subChunkSize,
            Integer offset1, Integer len2, Integer offset2) {
        this.riff = riff;
        this.chunkSize = chunkSize;
        this.wave = wave;
        this.fmt = fmt;
        this.subChunkSize = subChunkSize;
        this.offset1 = offset1;
        this.len2 = len2;
        this.offset2 = offset2;
        full = true;
    }

    private boolean readValuesOriginal(ReaderUtil ru, boolean full) {
        this.full = full;
        if (full) {
            riff = ru.readInt(false);
            chunkSize = ru.readInt(false);
            wave = ru.readInt(false);
            fmt = ru.readInt(false);
            subChunkSize = ru.readInt(false);
        }

        format = ru.readShort(false);
        numChannels = ru.readShort(false);
        sampleRate = ru.readInt(false);
        byteRate = ru.readInt(false);
        blockAlign = ru.readShort(false);
        bitsPerSample = ru.readShort(false);
        extraDataSize = ru.readShort(false);
        if (!extraDataSize.equals((short) 34)) {
            logF("Invalid extra data size: %x (%d).", extraDataSize, extraDataSize);
            return false;
        }
        extraData = ru.readByteArray(34, false);
        seekTag = ru.readInt(false);
        seekTagLen = ru.readInt(false);
        seekData = ru.readByteArray(seekTagLen, false);
        data = ru.readInt(false);
        dataSize = ru.readInt(false);
        if (full) {
            return (this.riff != null && this.chunkSize != null && this.wave != null && this.fmt != null && this.subChunkSize != null
                    && this.format != null && this.numChannels != null && this.sampleRate != null && this.byteRate != null && this.blockAlign != null
                    && this.bitsPerSample != null && this.extraDataSize != null && this.extraData != null && this.seekTag != null && this.seekTagLen != null
                    && this.seekData != null && this.data != null && this.dataSize != null);
        } else {
            return (this.format != null && this.numChannels != null && this.sampleRate != null && this.byteRate != null && this.blockAlign != null
                    && this.bitsPerSample != null && this.extraDataSize != null && this.extraData != null && this.seekTag != null && this.seekTagLen != null
                    && this.seekData != null && this.data != null && this.dataSize != null);
        }
    }

    private boolean readValuesHD(ReaderUtil ru, boolean full) {
        this.full = full;
        if (full) {
            chunkSize = ru.readInt(false);
            riff = ru.readInt(false);
            offset1 = ru.readInt(false);
            len2 = ru.readInt(false);
            fmt = ru.readInt(false);
            offset2 = ru.readInt(false);
            subChunkSize = ru.readInt(false);
            wave = 0;
        }

        data = ru.readInt(false);
        offset3 = ru.readInt(false);
        dataSize = ru.readInt(false);
        format = ru.readShort(false);
        numChannels = ru.readShort(false);
        sampleRate = ru.readInt(false);
        byteRate = ru.readInt(false);
        blockAlign = ru.readShort(false);
        bitsPerSample = ru.readShort(false);
        extraDataSize = ru.readShort(false);
        if (!extraDataSize.equals((short) 34)) {
            logF("Invalid extra data size: %x (%d).", extraDataSize, extraDataSize);
            return false;
        }
        unk1Data = ru.readByteArray(20, false);
        extraData = ru.readByteArray(34, false);
        if (full) {
            return (this.riff != null && this.chunkSize != null && this.wave != null && this.fmt != null && this.subChunkSize != null
                    && this.format != null && this.numChannels != null && this.sampleRate != null && this.byteRate != null && this.blockAlign != null
                    && this.bitsPerSample != null && this.extraDataSize != null && this.extraData != null && this.seekTag != null && this.seekTagLen != null
                    && this.seekData != null && this.data != null && this.dataSize != null);
        } else {
            return (this.format != null && this.numChannels != null && this.sampleRate != null && this.byteRate != null && this.blockAlign != null
                    && this.bitsPerSample != null && this.extraDataSize != null && this.extraData != null && this.seekTag != null && this.seekTagLen != null
                    && this.seekData != null && this.data != null && this.dataSize != null);
        }
    }

    @Override
    public boolean readValues(ReaderUtil ru, boolean full) {
        if (parseModeHDPak) {
            return readValuesHD(ru, full);
        } else {
            return readValuesOriginal(ru, full);
        }
    }

    @Override
    public String toString() {
        String s = "WaveFormatType52: ";
        if (full) {
            s += String.format("ChunkSize: %d (0x%x), subChunkSize: %d (0x%x),", chunkSize, chunkSize, subChunkSize, subChunkSize);
        }
        s += String.format(" format: %d (0x%x), numChannels: %d (0x%x), sampleRate: %d (0x%x), byteRate: %d (0x%x), blockAlign: %d (0x%x), bitsPerSample: %d (0x%x), extraDataSize: %x, dataSize: %d (0x%x)",
                format, format, numChannels, numChannels, sampleRate, sampleRate, byteRate, byteRate, blockAlign, blockAlign, bitsPerSample, bitsPerSample, extraDataSize, dataSize, dataSize);
        return s;
    }

    @Override
    public int getSize() {
        return dataSize;
    }

    @Override
    public byte[] getAssembledWavData() {
        WriterUtil wu = new WriterUtil();

        wu.writeInt(riff);
        wu.writeInt(chunkSize);
        wu.writeInt(wave);
        wu.writeInt(fmt);
        wu.writeInt(subChunkSize);
        wu.writeShort(format);
        wu.writeShort(numChannels);
        wu.writeInt(sampleRate);
        wu.writeInt(byteRate);
        wu.writeShort(blockAlign);
        wu.writeShort(bitsPerSample);
        wu.writeShort(extraDataSize);
        wu.writeByteArray(extraData);
        if (!parseModeHDPak) {
            wu.writeInt(seekTag);
            wu.writeInt(seekTagLen);
            wu.writeByteArray(seekData);
        }
        wu.writeInt(data);
        wu.writeInt(dataSize);
        wu.writeByteArray(waveData);

        return wu.getBytes();
    }

    @Override
    public void setParseModeHDPak(boolean b) {
        parseModeHDPak = b;
    }

    @Override
    public void readUnkData(ReaderUtil ru) {
        unk2Data = ru.readByteArray(54, false);
    }

    @Override
    public byte[] getWaveData() {
        return waveData;
    }

    @Override
    public byte[] getUnk1Data() {
        return unk1Data;
    }

    @Override
    public byte[] getUnk2Data() {
        return unk2Data;
    }
}
