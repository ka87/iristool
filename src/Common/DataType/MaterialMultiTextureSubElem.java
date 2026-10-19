/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Convert;
import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;
import Common.TextureTransformUtil;
import Common.WriterUtil;
import java.nio.ByteBuffer;

/**
 *
 * @author andras
 */
public class MaterialMultiTextureSubElem extends Logger {

    public static final byte MMTSE_ZFUNC_EQUAL = 1;
    public static final byte MMTSE_ZFUNC_LESSEQUAL = 0;
    public static final byte MMTSE_SSTA_WRAP = 1;
    public static final byte MMTSE_SSTA_CLAMP = 3;
    public static final byte MMTSE_SSTF_POINT = 1;
    public static final byte MMTSE_SSTF_LINEAR = 2;
    public static final byte MMTSE_AF_LESS = 2;
    public static final byte MMTSE_AF_GREATER = 5;

    //alphaMaskType400 bits: AAAAAABBHHMMUUUU : A = alphamask, B: boneID, HH: handleType, M: Matrix typeID, U: unknown flags (planar)
    private Short hasNext, alphaMaskType400;
    private Integer subTextureFlags, transform1, transform2, textureKey;
    private Float transform1F, transform2F;
    private byte colorSource, UVFuncNum;
    private byte zFunc = MMTSE_ZFUNC_LESSEQUAL;
    private boolean zWriteEnable = false;
    private byte samplerStateTypeAddressU = MMTSE_SSTA_WRAP;
    private byte samplerStateTypeAddressV = MMTSE_SSTA_WRAP;
    private byte samplerStateTypeMagAndMinFilter = MMTSE_SSTF_POINT;
    private byte samplerStateTypeMipFilter = MMTSE_SSTF_POINT;
    private byte colorWriteEnable = 0;
    private byte alphaFunc = MMTSE_AF_LESS; // ??, OK?
    private byte alphaRef = 0;
    private int alphaBlendMode = 0;

    public MaterialMultiTextureSubElem() {
    }

    public Short getHasNext() {
        return hasNext;
    }

    public void setHasNext(Short hasNext) {
        this.hasNext = hasNext;
    }

    public Short getAlphaMaskType400() {
        return alphaMaskType400;
    }

    public void setAlphaMaskType400(Short alphaMaskType400) {
        this.alphaMaskType400 = alphaMaskType400;
    }

    public Integer getSubTextureFlags() {
        return subTextureFlags;
    }

    public void setSubTextureFlags(Integer subTextureFlags) {
        this.subTextureFlags = subTextureFlags;
        handleSubTextureFlags();
    }

    public Integer getTransform1() {
        return transform1;
    }

    public void setTransform1(Integer transform1) {
        this.transform1 = transform1;
    }

    public Integer getTransform2() {
        return transform2;
    }

    public void setTransform2(Integer transform2) {
        this.transform2 = transform2;
    }

    public Integer getTextureKey() {
        return textureKey;
    }

    public void setTextureKey(Integer textureKey) {
        this.textureKey = textureKey;
    }

    public Float getTransform1F() {
        return transform1F;
    }

    public void setTransform1F(Float transform1F) {
        this.transform1F = transform1F;
    }

    public Float getTransform2F() {
        return transform2F;
    }

    public void setTransform2F(Float transform2F) {
        this.transform2F = transform2F;
    }

    public byte getColorSource() {
        return colorSource;
    }

    public byte getUVFuncNum() {
        return UVFuncNum;
    }

    private void handleSubTextureFlags() {
        this.colorSource = (byte) ((subTextureFlags >> 12) & 0xf);
        this.UVFuncNum = (byte) ((subTextureFlags >> 20) & 0xf); // implemented in TextureTransformUtil too
        int tmpFlags = subTextureFlags & 0xfff;
        zFunc = ((tmpFlags & 0x100) != 0 ? MMTSE_ZFUNC_EQUAL : MMTSE_ZFUNC_LESSEQUAL);
        zWriteEnable = (~(tmpFlags >> 9) & 1) == 1;
        samplerStateTypeAddressU = (~(2 * tmpFlags) & 2 | 1) == 3 ? MMTSE_SSTA_CLAMP : MMTSE_SSTA_WRAP;
        samplerStateTypeAddressV = (~tmpFlags & 2 | 1) == 3 ? MMTSE_SSTA_CLAMP : MMTSE_SSTA_WRAP;
        samplerStateTypeMagAndMinFilter = ((tmpFlags & 4) != 0) ? MMTSE_SSTF_LINEAR : MMTSE_SSTF_POINT;
        samplerStateTypeMipFilter = ((tmpFlags & 8) != 0) ? MMTSE_SSTF_LINEAR : MMTSE_SSTF_POINT;

        if ((tmpFlags & 0x20) == 0) {
            colorWriteEnable = 8;
        }
        if ((tmpFlags & 0x40) == 0) {
            colorWriteEnable |= 7;
        }

        if ((tmpFlags & 0x10) != 0) {
            alphaFunc = MMTSE_AF_LESS;
            if ((tmpFlags & 0x80) == 0) {
                alphaFunc = MMTSE_AF_GREATER;
            }
            alphaRef = (byte) ((subTextureFlags >> 22) & 0xfc);
        }

        alphaBlendMode = (subTextureFlags >> 16) & 0xf; // or fog
    }

    public byte getzFunc() {
        return zFunc;
    }

    public boolean isZWriteEnable() {
        return zWriteEnable;
    }

    public byte getSamplerStateTypeAddressU() {
        return samplerStateTypeAddressU;
    }

    public byte getSamplerStateTypeAddressV() {
        return samplerStateTypeAddressV;
    }

    public byte getSamplerStateTypeMagAndMinFilter() {
        return samplerStateTypeMagAndMinFilter;
    }

    public byte getSamplerStateTypeMipFilter() {
        return samplerStateTypeMipFilter;
    }

    public byte getColorWriteEnable() {
        return colorWriteEnable;
    }

    public byte getAlphaFunc() {
        return alphaFunc;
    }

    public int getAlphaRefAsInt() {
        return Byte.toUnsignedInt(alphaRef);
    }

    public int getAlphaBlendMode() {
        return alphaBlendMode;
    }

    public MaterialMultiTextureSubElem(Short hasNext, Short alphaMaskType400, Integer subTextureFlags, Integer transform1, Integer transform2, Integer textureKey) {
        this.hasNext = hasNext;
        this.alphaMaskType400 = alphaMaskType400;
        this.subTextureFlags = subTextureFlags;
        handleSubTextureFlags();
        this.transform1 = transform1;
        this.transform2 = transform2;
        this.textureKey = textureKey;
        this.transform1F = ByteBuffer.wrap(Convert.intToHexArray(transform1)).getFloat();
        this.transform2F = ByteBuffer.wrap(Convert.intToHexArray(transform2)).getFloat();
    }

    public boolean readValues(ReaderUtil ru) {
        hasNext = ru.readShort(false);
        alphaMaskType400 = ru.readShort(false);
        subTextureFlags = ru.readInt(false);
        transform1 = ru.readInt(false);
        transform2 = ru.readInt(false);
        textureKey = ru.readInt(false);
        transform1F = ByteBuffer.wrap(Convert.intToHexArray(transform1)).getFloat();
        transform2F = ByteBuffer.wrap(Convert.intToHexArray(transform2)).getFloat();
        handleSubTextureFlags();
        return (this.hasNext != null && this.alphaMaskType400 != null && this.subTextureFlags != null
                && this.transform1 != null && this.transform2 != null && this.textureKey != null);
    }

    public void writeValues(WriterUtil wu) {
        wu.writeShort(hasNext);
        wu.writeShort(alphaMaskType400);
        wu.writeInt(subTextureFlags);
        wu.writeInt(transform1);
        wu.writeInt(transform2);
        wu.writeInt(textureKey);
    }

    public String getColorSourceMeaning(int id) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            switch (id) {
                case 0:
                    return "calc light and shadow";
                case 1:
                    return "calc light";
                case 2:
                    return "constant color";
                case 3:
                    return "vertexcolor wo light and shadow";
                case 4:
                    return "full color";
                case 5:
                    return "inverse vertexcolor wo light and shadow";
                case 6:
                    return "calc light and shadow 2X";
            }
            return "unknown";
        } else {
            return "";
        }
    }

    public String getUVFuncMeaning(int id, int planarFlags) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            switch (id) {
                case 0:
                    return "UV";
                case 1:
                    return "UV v2";
                case 2:
                    return "chrome";
                case 3:
                    return "diffuse";
                case 4:
                    return "phong";
                case 5:
                    return "calls [UV] if no UVdata";
                case 6:
                    return "planar: " + getPlanarUVFuncOptionsMeaning(planarFlags);
            }
            return "unknown";
        } else {
            return "";
        }
    }

    public String getPlanarUVFuncOptionsMeaning(int flags) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            int planarType = (flags & 0xff) >> 6;
            int planarCoords = ((flags >> 4) & 3);
            int boneID = (((byte) (flags >> 8)) & 0x7);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("planarType: %d%s, ", planarType, (planarType == 3 ? (", BoneID: " + boneID) : "")));
            switch (planarCoords) {
                case 0:
                    sb.append("planarCoordsType: 0 (y, z coords)");
                    break;
                case 1:
                    sb.append("planarCoordsType: 1 (x, z coords)");
                    break;
                case 2:
                    sb.append("planarCoordsType: 2 (x, y coords)");
                    break;
                case 3:
                    sb.append("planarCoordsType: 3 (x, y, z coords rate as UV)");
                    break;
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public String getColorWriteEnableMeaning(int flag) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            String s = "";
            if ((flag & 0x1) != 0) {
                s = "r ";
            }
            if ((flag & 0x2) != 0) {
                s += "g ";
            }
            if ((flag & 0x4) != 0) {
                s += "b ";
            }
            if ((flag & 0x8) != 0) {
                s += "a ";
            }
            return s;
        } else {
            return "";
        }
    }

    public String getAlphaBlendModeMeaning(int abm, int stf) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            switch (abm) {
                case 0:
                    return "alphablend disabled [0]";
                case 7:
                    return "alphablend disabled [7]";
                case 1:
                    return (((byte) (stf & 0xff)) >= 0) ? "src: src alpha | dst: inv src alpha" : "src: inv src alpha | dst: src alpha";
                case 2:
                    return (((byte) (stf & 0xff)) >= 0) ? "src: one | dst: inv src alpha" : "src: one | dst: src alpha";
                case 3:
                    return (((byte) (stf & 0xff)) >= 0) ? "src: dst alpha | dst: inv dst alpha" : "src: inv dst alpha | dst: dst alpha";
                case 4:
                    return (((byte) (stf & 0xff)) >= 0) ? "src: dst alpha | dst: one" : "src: inv dst alpha | dst: one";
                case 5:
                    return "src: one | dst: one";
                case 6:
                    return "src: zero | dst: inv src color [6]";
                case 8:
                    return "src: zero | dst: inv src color [8]";
                default:
                    return "unknown";
            }
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        TextureTransformUtil ttu = new TextureTransformUtil(this, 0);
        return System.lineSeparator() + String.format("Has next? : %d (%x)", hasNext, hasNext) + System.lineSeparator()
                + String.format("Subtexture Flags: %x, ColorSource: %d (%s), UVFuncNum: %d (%s)", subTextureFlags, //0xCxxxxxxx => moving texture, & 0x400 => has alpha mask
                        colorSource, getColorSourceMeaning(colorSource), UVFuncNum, getUVFuncMeaning(UVFuncNum, alphaMaskType400)) + System.lineSeparator()
                + String.format("Should we add alpha to color?: %s", (subTextureFlags & 0x400) != 0 ? "yes" : "no") + System.lineSeparator()
                + String.format("alphaMaskType400: %x", alphaMaskType400) + System.lineSeparator()
                + "zFunc: " + (zFunc == MMTSE_ZFUNC_LESSEQUAL ? "less or equal" : "equal") + System.lineSeparator()
                + "zWriteEnable: " + (zWriteEnable ? "enabled" : "disabled") + System.lineSeparator()
                + "samplerStateTypeAddressU: " + (samplerStateTypeAddressU == MMTSE_SSTA_WRAP ? "wrap" : "clamp") + System.lineSeparator()
                + "samplerStateTypeAddressV: " + (samplerStateTypeAddressV == MMTSE_SSTA_WRAP ? "wrap" : "clamp") + System.lineSeparator()
                + "samplerStateTypeMagAndMinFilter: " + (samplerStateTypeMagAndMinFilter == MMTSE_SSTF_POINT ? "point" : "linear") + System.lineSeparator()
                + "samplerStateTypeMipFilter: " + (samplerStateTypeMipFilter == MMTSE_SSTF_POINT ? "point" : "linear") + System.lineSeparator()
                + String.format("colorWriteEnable: %x (%s)", colorWriteEnable, getColorWriteEnableMeaning(colorWriteEnable)) + System.lineSeparator()
                + ((subTextureFlags & 0x10) != 0 ? ("alphaFunc: " + (alphaFunc == MMTSE_AF_LESS ? "less" : "greater") + System.lineSeparator()
                        + String.format("alphaRef: %x (%d)", alphaRef, Byte.toUnsignedInt(alphaRef))) : "no alphatest") + System.lineSeparator()
                + String.format("alphaBlendMode: %d (%s)", alphaBlendMode, getAlphaBlendModeMeaning(alphaBlendMode, subTextureFlags)) + System.lineSeparator()
                + String.format("Transform1: %x (%f)", transform1, transform1F) + System.lineSeparator()
                + String.format("Transform2: %x (%f)", transform2, transform2F) + System.lineSeparator()
                + String.format("Texture key: %x", textureKey, textureKey) + System.lineSeparator()
                + "Texture transformation matrix example: " + ttu.getMatrixAsObject().toString();
    }
}
