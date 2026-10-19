/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class GAOInnerParserLight extends BFChunkParseClass implements BFChunkParseInterface {

    private Integer flags, color, gaokey;
    private Float start, end, densityMin, densityMax;
    private BFKeyListElem gaoChunkKLE;

    public GAOInnerParserLight() {
        super();
    }

    public Integer getColor() {
        return color;
    }

    public Integer getFlags() {
        return flags;
    }

    public Integer getGaokey() {
        return gaokey;
    }

    public Float getStart() {
        return start;
    }

    public Float getEnd() {
        return end;
    }

    public Float getDensityMin() {
        return densityMin;
    }

    public Float getDensityMax() {
        return densityMax;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public void setGaokey(Integer gaokey) {
        this.gaokey = gaokey;
    }

    public void setStart(Float start) {
        this.start = start;
    }

    public void setEnd(Float end) {
        this.end = end;
    }

    public void setDensityMin(Float densityMin) {
        this.densityMin = densityMin;
    }

    public void setDensityMax(Float densityMax) {
        this.densityMax = densityMax;
    }

    public BFKeyListElem getGaoChunkKLE() {
        return gaoChunkKLE;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing GAO Inner Light Chunk.");
        }

        if (!buildFromParsed) {
            flags = ru.readInt(false);
            color = ru.readInt(false);
            start = ru.readFloat(false);
            end = ru.readFloat(false);
            densityMin = ru.readFloat(false);
            densityMax = ru.readFloat(false);
            gaokey = ru.readInt(false);
        } else {
            wu.writeInt(flags);
            wu.writeInt(color);
            wu.writeFloat(start);
            wu.writeFloat(end);
            wu.writeFloat(densityMin);
            wu.writeFloat(densityMax);
            wu.writeInt(gaokey);
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("flags: %d (%x, %s)", flags, flags, getFlagsDescription());
            logF("color: %d (%x)", color, color);
            logF("start: %f, end: %f, densityMin: %f, densityMax: %f", start, end, densityMin, densityMax);
            logF("GAO Key: %x", gaokey);
        }

        if (gaokey != -1) {
            if (!buildFromParsed) {
                gaoChunkKLE = new BFKeyListElem(gaokey, BFGaoChunk.class);
            }
            bfdfc.addToKeyList(gaoChunkKLE);
        }

        return true;
    }

    public String getFlagsDescription() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder ext = new StringBuilder();
            switch (flags & 0x7) { // it has check for 0x1000, but it has same case for missing 0x1000
                case 0:
                    ext.append("Omni light");
                    break;
                case 1:
                    ext.append("Direct light");
                    break;
                case 2:
                    ext.append("Spot light");
                    break;
                case 3:
                    ext.append("Fog");
                    break; // also checks for 0x8
                case 4:
                    ext.append("Custom material draw on model (after original)"); // "start" var selects material from materialgroup
                    break;
                default:
                    ext.append("unknown light");
                    break;
            }
            if ((flags & 0x20) == 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("only alpha");
            }
            if ((flags & 0x40) == 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("no alpha");
            }
            if ((flags & 0x80) == 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("color add");
            } else {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("color substract");
            }
            // TODO: 100, 200 in should be lit check in game
            if ((flags & 0x1000) != 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("with specular");
            }
            if ((flags & 0x2000) == 0 && (flags & 0x8000) != 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("normalVec YXZ invert");
            } else if ((flags & 0x8000) == 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                if ((flags & 0x2000) != 0) {
                    ext.append("normalVec YZ invert");
                } else {
                    ext.append("normalVec X invert");
                }
            }
            if ((flags & 0x10000) != 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("light pos - center of gao");
            }
            if ((flags & 0x20000) == 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("light UP/DN related factor"); // see Militaire
            }
            if ((flags & 0x100000) != 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("omni - no specular");
            }
            if ((flags & 0x1000000) != 0) { // removes unkflags 0x100000
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("only object shadow (with case 4)");
            }
            if ((flags & 0x2000000) != 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("gao1 BV contains gao2 BV (with case 4)"); // otherwise overlaps
            }
            if ((flags & 0x40000000) != 0) {
                if (ext.length() > 0) {
                    ext.append(", ");
                }
                ext.append("light add disabled");
            }
            return ext.toString();
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        String ext = getFlagsDescription();
        return String.format("Light: flags: 0x%x"
                + (ext.length() > 0 ? " [" + ext + "]" : "")
                + ", color: %x, start: %f, end: %f, densityMin: %f, densityMax: %f, GAO Key: %x", flags, color, start, end, densityMin, densityMax, gaokey);
    }

}
