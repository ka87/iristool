/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author andras
 */
public class TextureTool extends Logger {

    public static int getTextureBithDepthFromFormat(int i) {
        switch (i - 16) {
            case 0:
                return 32;
            case 16:
                return 24;
            case 32:
                return 16;
            case 33:
                return 16;
            case 48:
                return 8;
            case 64:
                return 4;
        }
        return 0;
    }

    public static int getChannelNumberFromPaletteSize(int paletteSize) {

        switch (paletteSize) {
            case 48:
            case 768:
                return 3;
            case 64:
            case 1024:
                return 4;
        }
        return 0;
    }

    public byte[] makePalettedImage(byte textureBytes[], byte paletteBytes[], int width, int height, int textureFormat, String file) {
        if (paletteBytes.length != 48
                && paletteBytes.length != 64
                && paletteBytes.length != 768
                && paletteBytes.length != 1024) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Invalid palette size. (%d)", paletteBytes.length);
            }
            return null;
        }

        byte textureOutData[] = new byte[width * height * 4];

        int i, j, k = 0;
        int cs;

        switch (textureFormat) {
            case 0x50: // halfbyte
                cs = TextureTool.getChannelNumberFromPaletteSize(paletteBytes.length);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Palette channel number: %d, palette size: %d bytes", cs, paletteBytes.length);
                }

                int halfByteOffset;
                for (i = 0; i < height; i++) {
                    halfByteOffset = 0;
                    for (j = 0; j < width / 2; j++) {

                        int paletteArrIndex, textureOutArrayIndex;

                        for (k = 4; k >= 0; k -= 4) {

                            if (((textureBytes[(i * (width / 2)) + j] >> k) & 0xf) > (paletteBytes.length / cs)) {
                                if (!IrisToolDefines.PUBLIC_RELEASE) {
                                    logF("texture is out of bounds of it's palette. (%x > %x)",
                                            ((textureBytes[(i * (width / 2)) + j] >> k) & 0xf), (paletteBytes.length / cs));
                                }
                                return null;
                            }

                            textureOutArrayIndex = (((width * (height - 1)) - (i * width)) + halfByteOffset + (1 - (k / 4))) * 4;

                            switch (cs) {
                                case 3:
                                    paletteArrIndex = (((textureBytes[(i * (width / 2)) + j] >> k) & 0xf) * 3);
                                    textureOutData[textureOutArrayIndex + 3] = (byte) 0xff;
                                    textureOutData[textureOutArrayIndex + 2] = paletteBytes[paletteArrIndex + 0];
                                    textureOutData[textureOutArrayIndex + 1] = paletteBytes[paletteArrIndex + 1];
                                    textureOutData[textureOutArrayIndex + 0] = paletteBytes[paletteArrIndex + 2];
                                    break;
                                case 4:
                                    paletteArrIndex = (((textureBytes[(i * (width / 2)) + j] >> k) & 0xf) * 4);
                                    textureOutData[textureOutArrayIndex + 3] = paletteBytes[paletteArrIndex + 3];
                                    textureOutData[textureOutArrayIndex + 2] = paletteBytes[paletteArrIndex + 0];
                                    textureOutData[textureOutArrayIndex + 1] = paletteBytes[paletteArrIndex + 1];
                                    textureOutData[textureOutArrayIndex + 0] = paletteBytes[paletteArrIndex + 2];
                                    break;
                            }
                        }
                        halfByteOffset += 2;
                    }
                }
                break;
            case 0x40: // byte
                cs = TextureTool.getChannelNumberFromPaletteSize(paletteBytes.length);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Palette channel number: %d, palette size: %d bytes", cs, paletteBytes.length);
                }

                for (i = 0; i < height; i++) {
                    for (j = 0; j < width; j++) {
                        if ((textureBytes[(i * width) + j] & 0xff) > (paletteBytes.length / cs)) {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                logF("texture is out of bounds of it's palette. (%x > %x)", textureBytes[(i * width) + j], (paletteBytes.length / cs));
                            }
                            return null;

                        }
                        int paletteArrIndex, textureOutArrayIndex;

                        textureOutArrayIndex = (((width * (height - 1)) - (i * width)) + j) * 4;

                        switch (cs) {
                            case 3:
                                paletteArrIndex = (textureBytes[(i * width) + j] & 0xff) * 3;
                                textureOutData[textureOutArrayIndex + 3] = (byte) 0xff;
                                textureOutData[textureOutArrayIndex + 2] = paletteBytes[paletteArrIndex + 0];
                                textureOutData[textureOutArrayIndex + 1] = paletteBytes[paletteArrIndex + 1];
                                textureOutData[textureOutArrayIndex + 0] = paletteBytes[paletteArrIndex + 2];
                                break;
                            case 4:
                                paletteArrIndex = (textureBytes[(i * width) + j] & 0xff) * 4;
                                textureOutData[textureOutArrayIndex + 3] = paletteBytes[paletteArrIndex + 3];
                                textureOutData[textureOutArrayIndex + 2] = paletteBytes[paletteArrIndex + 0];
                                textureOutData[textureOutArrayIndex + 1] = paletteBytes[paletteArrIndex + 1];
                                textureOutData[textureOutArrayIndex + 0] = paletteBytes[paletteArrIndex + 2];
                                break;
                        }
                    }
                }
                break;
            default:
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Unhandled type = %x", textureFormat);
                }
                return null;
        }

        if (file != null) {
            writeToPNGFile(textureOutData, width, height, file);
        }
        return textureOutData;
    }

    private static byte[] swap32BitBGRtoRGB(byte[] b) {
        if (b.length % 4 != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println("swap32BitBGRtoRGB: Invalid size");
            }
            return null;
        }
        byte tmp;
        for (int i = 0; i < b.length; i += 4) {
            tmp = b[i];
            b[i] = b[i + 2];
            b[i + 2] = tmp;
        }
        return b;
    }

    private byte[] flipUpDown(byte pixelData[], int width, int height) {
        byte tmp[] = new byte[width * 4];
        for (int i = 0; i < height / 2; i++) {
            System.arraycopy(pixelData, width * i * 4, tmp, 0, 4 * width);
            System.arraycopy(pixelData, width * (height - 1) * 4 - (width * i * 4), pixelData, width * i * 4, 4 * width);
            System.arraycopy(tmp, 0, pixelData, width * (height - 1) * 4 - (width * i * 4), 4 * width);
        }
        return pixelData;
    }

    private Boolean writeToPNGFile(byte bytes[], int width, int height, String file) {
        DataBuffer buffer = new DataBufferByte(bytes, bytes.length);

        //4 bytes per pixel: red, green, blue, alpha
        WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 4 * width, 4, new int[]{0, 1, 2, 3}, (Point) null);
        ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        BufferedImage image = new BufferedImage(cm, raster, true, null);

        try {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Writing png image. [%dx%d] (%s)", width, height, file);
            }
            ImageIO.write(image, "png", new File(file));
        } catch (Exception e) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Image creation error: " + e.getLocalizedMessage());
            }
            return false;
        }
        return true;
    }

    public byte[] makeTrueColorImage(byte bytes[], int width, int height, int bitDepth, String file) {
        if (bitDepth == 24) {
            bytes = convert24BitTo32Bit(bytes);
        } else if (bitDepth != 32) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println("makeTrueColorImage: bitDepth is not 32 or 24 bit: " + bitDepth);
            }
            return null;
        }
        bytes = TextureTool.swap32BitBGRtoRGB(bytes);
        bytes = flipUpDown(bytes, width, height);
        if (file != null) {
            writeToPNGFile(bytes, width, height, file);
        }

        return bytes;
    }

    // TODO: cleanup, should be ignore this step
    private static byte[] fixRGBByteOrder(byte[] b) {
        if (b.length % 4 != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println("fixRGBByteOrder: Invalid size");
            }
            return null;
        }
        byte tmp;
        for (int i = 0; i < b.length; i += 4) {
            tmp = b[i];
            b[i] = b[i + 3];
            b[i + 3] = tmp;
            tmp = b[i + 1];
            b[i + 1] = b[i + 2];
            b[i + 2] = tmp;
        }
        return b;
    }

    public static byte[] loadImage(File f) {
        if (!f.isFile()) {
            return null;
        }
        BufferedImage b;
        try {
            b = ImageIO.read(f);
        } catch (IOException ex) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println(ex.getLocalizedMessage());
            }
            return null;
        }
        byte[] ret = ((DataBufferByte) b.getData().getDataBuffer()).getData();
        ret = TextureTool.fixRGBByteOrder(ret);
        return ret;
    }

    public byte[] convert24BitTo32Bit(byte src[]) {
        if (src == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println("convert24BitTo32Bit: src is null");
            }
            return null;
        }
        if (src.length % 3 != 0) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                System.out.println("convert24BitTo32Bit: Invalid size");
            }
            return null;
        }

        int num = src.length / 3;

        byte dest[] = new byte[num * 4];
        int j = 0, k = 0;

        for (int i = 0; i < num; i++) {
            dest[k++] = src[j++];
            dest[k++] = src[j++];
            dest[k++] = src[j++];
            dest[k++] = (byte) 0xff;
        }
        return dest;
    }
}
