/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.DataType.ModifierSPGSpriteBankPropertiesData;
import Common.DataType.ModifierSPGBitFlagsData;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierSpriteGenModifier extends BFModifierBaseClass implements BFChunkParseInterface {

    private ModifierSPGBitFlagsData spgbfd[] = new ModifierSPGBitFlagsData[4];
    private ModifierSPGSpriteBankPropertiesData spgsbpd[] = new ModifierSPGSpriteBankPropertiesData[4];
    private Integer type;
    private Float dimensionSize1, dimensionSize2;
    private Float posMultiplicator, dividerVectorLimit;
    private Integer dividerMaxDecreaseLimit, dividerMaxDecreaseLimitOrig;
    private Integer materialEnabledBitmask;
    private Float tf3;
    private Float distMultiplicator;
    private Integer anotherFlags;
    private Float pos1, pos2;
    private Integer color;
    private Integer unk1, unk2;

    public BFModifierSpriteGenModifier() {
        super();
    }

    public ModifierSPGBitFlagsData[] getSpgbfd() {
        return spgbfd;
    }

    public ModifierSPGSpriteBankPropertiesData[] getSpgsbpd() {
        return spgsbpd;
    }

    public Integer getAnotherFlags() {
        return anotherFlags;
    }

    public Float getDimensionSize1() {
        return dimensionSize1;
    }

    public Float getDimensionSize2() {
        return dimensionSize2;
    }

    public Integer getDividerMaxDecreaseLimit() {
        return dividerMaxDecreaseLimit;
    }

    public void setDividerMaxDecreaseLimit(Integer dividerMaxDecreaseLimit) {
        this.dividerMaxDecreaseLimit = dividerMaxDecreaseLimit;
    }

    public Float getDistMultiplicator() {
        return distMultiplicator;
    }

    public Integer getColor() {
        return color;
    }

    public Float getPos1() {
        return pos1;
    }

    public Float getPos2() {
        return pos2;
    }

    public Float getDividerVectorLimit() {
        return dividerVectorLimit;
    }

    public Integer getMaterialEnabledBitmask() {
        return materialEnabledBitmask;
    }

    public Float getTf3() {
        return tf3;
    }

    public Float getPosMultiplicator() {
        return posMultiplicator;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Sprite Gen Modifier (SPG)");
        }

        if (!buildFromParsed) {
            unk1 = ru.readInt(false);
            type = ru.readInt(false);
            dimensionSize1 = ru.readFloat(false);
            dimensionSize2 = ru.readFloat(false);
        } else {
            wu.writeInt(unk1);
            wu.writeInt(type);
            wu.writeFloat(dimensionSize1);
            wu.writeFloat(dimensionSize2);
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Type: %d (%x)", type, type);
            logF("dimensionSize1: %f", dimensionSize1);
            logF("dimensionSize2: %f", dimensionSize2);
        }

        if (!buildFromParsed) {
            unk2 = ru.readInt(false);
        } else {
            wu.writeInt(unk2);
        }

        int i;

        if (type > 0) {
            if (!buildFromParsed) {
                posMultiplicator = ru.readFloat(false);
                dividerVectorLimit = ru.readFloat(false);
                dividerMaxDecreaseLimit = ru.readInt(false);
            } else {
                wu.writeFloat(posMultiplicator);
                wu.writeFloat(dividerVectorLimit);
                wu.writeInt(dividerMaxDecreaseLimit);
            }
            dividerMaxDecreaseLimitOrig = dividerMaxDecreaseLimit;
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("posMultiplicator: %f", posMultiplicator);
                logF("dividerVectorLimit: %f", dividerVectorLimit);
                logF("dividerMaxDecreaseLimit: %d (%x)", dividerMaxDecreaseLimit, dividerMaxDecreaseLimit);
            }
        }
        if (type > 1) {
            if (!buildFromParsed) {
                materialEnabledBitmask = ru.readInt(false);
            } else {
                wu.writeInt(materialEnabledBitmask);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("materialEnabledBitmask: %x", materialEnabledBitmask);
            }
        }
        if (type > 2) {
            if (!buildFromParsed) {
                tf3 = ru.readFloat(false);
            } else {
                wu.writeFloat(tf3);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("tf3: %f", tf3);
            }
        }
        if (type > 3) {
            for (i = 0; i < 4; i++) {
                if (!buildFromParsed) {
                    spgbfd[i] = new ModifierSPGBitFlagsData();
                    spgbfd[i].readValues(ru);
                } else {
                    spgbfd[i].writeValues(wu);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(spgbfd[i]);
                }
            }
        }
        if (type > 4) {
            for (i = 0; i < 4; i++) {
                if (!buildFromParsed) {
                    spgsbpd[i] = new ModifierSPGSpriteBankPropertiesData();
                    spgsbpd[i].readValues(ru);
                } else {
                    spgsbpd[i].writeValues(wu);
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log(spgsbpd[i]);
                }
            }
        }
        if (type > 5) {
            if (!buildFromParsed) {
                distMultiplicator = ru.readFloat(false);
            } else {
                wu.writeFloat(distMultiplicator);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("distMultiplicator: %f", distMultiplicator);
            }
        }
        if (type > 6) {
            if (!buildFromParsed) {
                anotherFlags = ru.readInt(false);
            } else {
                wu.writeInt(anotherFlags);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("anotherFlags: %x (%s)", anotherFlags, getAnotherFlagsMeaning(anotherFlags));
            }
        }
        if (type > 7) {
            if (!buildFromParsed) {
                pos1 = ru.readFloat(false);
                pos2 = ru.readFloat(false);
                color = ru.readInt(false);
            } else {
                wu.writeFloat(pos1);
                wu.writeFloat(pos2);
                wu.writeInt(color);
            }
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("pos1: %f", pos1);
                logF("pos2: %f", pos2);
                logF("color: %x", color);
            }
        }

        return true;
    }

    @Override
    public BFModifierSpriteGenModifier duplicate() {
        BFModifierSpriteGenModifier ret = new BFModifierSpriteGenModifier();
        
        ret.spgbfd = new ModifierSPGBitFlagsData[4];
        ret.spgbfd[0] = spgbfd[0].duplicate();
        ret.spgbfd[1] = spgbfd[1].duplicate();
        ret.spgbfd[2] = spgbfd[2].duplicate();
        ret.spgbfd[3] = spgbfd[3].duplicate();
        ret.spgsbpd = new ModifierSPGSpriteBankPropertiesData[4];
        ret.spgsbpd[0] = spgsbpd[0].duplicate();
        ret.spgsbpd[1] = spgsbpd[1].duplicate();
        ret.spgsbpd[2] = spgsbpd[2].duplicate();
        ret.spgsbpd[3] = spgsbpd[3].duplicate();
        ret.type = type;
        ret.dimensionSize1 = dimensionSize1;
        ret.dimensionSize2 = dimensionSize2;
        ret.posMultiplicator = posMultiplicator;
        ret.dividerVectorLimit = dividerVectorLimit;
        ret.dividerMaxDecreaseLimit = dividerMaxDecreaseLimit;
        ret.dividerMaxDecreaseLimitOrig = dividerMaxDecreaseLimitOrig;
        ret.materialEnabledBitmask = materialEnabledBitmask;
        ret.tf3 = tf3;
        ret.distMultiplicator = distMultiplicator;
        ret.anotherFlags = anotherFlags;
        ret.pos1 = pos1;
        ret.pos2 = pos2;
        ret.color = color;
        ret.unk1 = unk1;
        ret.unk2 = unk2;
        
        return ret;
    }

    @Override
    public String toString() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("SpriteGenModifier: unk1=%x, type=%d, dimensionSize1=%f, dimensionSize2=%f, unk2=%x",
                    unk1, type, dimensionSize1, dimensionSize2, unk2)).append(System.lineSeparator());
            if (type > 0) {
                sb.append(String.format("[>0] posMultiplicator=%f, dividerVectorLimit=%f, dividerMaxDecreaseLimit=%x", posMultiplicator, dividerVectorLimit, dividerMaxDecreaseLimit)).append(System.lineSeparator());
            }
            if (type > 1) {
                sb.append(String.format("[>1] materialEnabledBitmask: %x", materialEnabledBitmask)).append(System.lineSeparator());
            }
            if (type > 2) {
                sb.append(String.format("[>2] tf3: %f", tf3)).append(System.lineSeparator());
            }
            if (type > 3) {
                for (int i = 0; i < 4; i++) {
                    sb.append("[>3] ").append(spgbfd[i]).append(System.lineSeparator());
                }
            }
            if (type > 4) {
                for (int i = 0; i < 4; i++) {
                    sb.append("[>4] ").append(spgsbpd[i]).append(System.lineSeparator());
                }
            }
            if (type > 5) {
                sb.append(String.format("[>5] distMultiplicator: %f", distMultiplicator)).append(System.lineSeparator());
            }
            if (type > 6) {
                sb.append(String.format("[>6] anotherFlags: %x (%s)", anotherFlags, getAnotherFlagsMeaning(anotherFlags))).append(System.lineSeparator());
            }
            if (type > 7) {
                sb.append(String.format("[>7] pos1=%f, pos2=%f, color=%x", pos1, pos2, color)).append(System.lineSeparator());
            }
            return sb.toString();
        } else {
            return "SprinteGenModifier";
        }
    }

    public static String getAnotherFlagsMeaning(int flags) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            if ((flags & 0x1) != 0) {
                sb.append("0x1: send to zListSort");
            }
            if ((flags & 0x4) != 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("0x4: advanced color interpolation (unknown)");
            }
            if ((flags & 0x8) != 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("0x8: advanced color interpolation (unknown) factor clamped between 0.0 - 1.0");
            }
            if ((flags & 0x10) != 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("0x10: moving sprite");
            }
            int unk = flags & ~(0x10 | 0x8 | 0x4 | 0x1);
            if (unk != 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(String.format("unk flags: %x", unk));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

}
