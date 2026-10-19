/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

import Common.IrisToolDefines;
import Common.Logger;
import java.util.ArrayList;

/**
 *
 * @author andras
 */
public class BFTextureElem extends Logger {

    private int key = -1;
    private Integer paletteKey = null;
    private Integer textureType = null;
    private Integer textureFormat = null;
    private Integer type7Data[] = new Integer[12];
    private Integer type7DataIndex = 0;
    private Integer selectedType7DataTextureKey = null;
    private ArrayList<BFAnimatedTextureElem> animatedTextureArr = null;
    private int arIteratorIdx = -1;
    private Integer width = 0;
    private Integer height = 0;
    private Boolean ignoreFromDataParse = false;
    private byte pixelData[] = null;
    private Integer fontdescKey = null;
    private byte convertedPixelData[] = null;
    private BFTextureElem consoleSpecificLinkedBTE = null;
    private Integer trueColorBitDepth = null;

    public BFTextureElem(int key) {
        this.key = key;
        for (int i = 0; i < 12; i++) {
            type7Data[i] = -1;
        }
    }

    public BFTextureElem getConsoleSpecificLinkedBTE() {
        return consoleSpecificLinkedBTE;
    }

    public void setConsoleSpecificLinkedBTE(BFTextureElem consoleSpecificLinkedBTE) {
        this.consoleSpecificLinkedBTE = consoleSpecificLinkedBTE;
    }

    public Integer getPaletteKey() {
        return paletteKey;
    }

    public void setPaletteKey(Integer paletteKey) {
        this.paletteKey = paletteKey;
    }

    public int getKey() {
        return key;
    }

    public Integer getTextureType() {
        return textureType;
    }

    public void setTextureType(Integer textureType) {
        this.textureType = textureType;
    }

    public Integer getTextureFormat() {
        return textureFormat;
    }

    public void setTextureFormat(Integer textureFormat) {
        this.textureFormat = textureFormat;
    }

    public void addToType7Data(Integer key) {
        if (type7DataIndex > (type7Data.length - 1)) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Failed to add key: %x, overflow.", key);
            }
            return;
        }
        type7Data[type7DataIndex++] = key;
    }

    public Integer getType7ElemKey(int index) {
        return type7Data[index];
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getIgnoreFromDataParse() {
        return ignoreFromDataParse;
    }

    public void setIgnoreFromDataParse(Boolean ignoreFromDataParse) {
        this.ignoreFromDataParse = ignoreFromDataParse;
    }

    public Integer getSelectedType7DataTextureKey() {
        return selectedType7DataTextureKey;
    }

    public void setSelectedType7DataTextureKey(Integer selectedType7DataKey) {
        this.selectedType7DataTextureKey = selectedType7DataKey;
    }

    public byte[] getPixelData() {
        return pixelData;
    }

    public void setPixelData(byte[] pixelData) {
        this.pixelData = pixelData;
    }

    public Integer getFontdescKey() {
        return fontdescKey;
    }

    public void setFontdescKey(Integer fontdescKey) {
        this.fontdescKey = fontdescKey;
    }

    public byte[] getConvertedPixelData() {
        return convertedPixelData;
    }

    public void setConvertedPixelData(byte[] convertedPixelData) {
        this.convertedPixelData = convertedPixelData;
    }

    public void addAnimatedTextureElem(BFAnimatedTextureElem bate) {
        if (animatedTextureArr == null) {
            animatedTextureArr = new ArrayList<>();
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Adding Animated Texture Elem (key: %x) to Texture Type 9 (key: %x)", bate.getKey(), getKey());
        }
        animatedTextureArr.add(bate);
    }

    public Boolean hasNextAnimatedTexture() {
        if (animatedTextureArr == null) {
            return false;
        }
        if ((arIteratorIdx + 1) > (animatedTextureArr.size() - 1)) {
            return false;
        }
        return true;
    }

    public BFAnimatedTextureElem nextAnimatedTexture() {
        if (animatedTextureArr == null) {
            return null;
        }
        if (!hasNextAnimatedTexture()) {
            return null;
        } else {
            arIteratorIdx++;
            return animatedTextureArr.get(arIteratorIdx);
        }
    }

    public void resetAnimatedTextureIterator() {
        arIteratorIdx = -1;
    }

    public Integer getTrueColorBitDepth() {
        return trueColorBitDepth;
    }

    public void setTrueColorBitDepth(Integer trueColorBitDepth) {
        this.trueColorBitDepth = trueColorBitDepth;
    }

    public void set(BFTextureElem bte, boolean safeForLinking) {
        if (bte.animatedTextureArr != null) {
            animatedTextureArr = new ArrayList<>();
            for (BFAnimatedTextureElem aniTex : bte.animatedTextureArr) {
                animatedTextureArr.add(aniTex);
            }
        }
        this.arIteratorIdx = bte.arIteratorIdx;
        this.consoleSpecificLinkedBTE = bte.consoleSpecificLinkedBTE;
        this.convertedPixelData = bte.convertedPixelData;
        this.fontdescKey = bte.fontdescKey;
        this.height = bte.height;
        this.ignoreFromDataParse = bte.ignoreFromDataParse;
        if (!safeForLinking) {
            this.key = bte.key;
            this.paletteKey = bte.paletteKey;
            this.selectedType7DataTextureKey = bte.selectedType7DataTextureKey;
            this.pixelData = bte.pixelData;
        }
        this.textureFormat = bte.textureFormat;
        this.textureType = bte.textureType;
        if (bte.type7Data != null) {
            this.type7Data = new Integer[bte.type7Data.length];
            System.arraycopy(bte.type7Data, 0, this.type7Data, 0, this.type7Data.length);
        }
        this.type7DataIndex = bte.type7DataIndex;
        this.width = bte.width;
        this.trueColorBitDepth = bte.trueColorBitDepth;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            return "Key: " + String.format("0x%x", key) + ",TextureType: " + textureType + ", TextureFormat: " + textureFormat + ", " + width + "x" + height;
        } else {
            return "TextureElem";
        }
    }
}
