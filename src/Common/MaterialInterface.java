/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

/**
 *
 * @author andra
 */
public interface MaterialInterface {

    public Integer getFlags();

    public static String getFlagsMeaning(int flags) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            if ((flags & 0x80) != 0 && (flags & 0x40) == 0) {
                sb.append("Transparent, render in two-pass (opaque textures directly, transparent ones go to zlist sorted draw)");
            } else if ((flags & 0x40) != 0) {
                sb.append("Transparent, every texture should go to zlist sorted draw");
            }
            if ((flags & 0x10) != 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("apply vertex paint (precomputed lighting)");
            }
            if ((flags & 0x3) != 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("apply lights from light sources");
            }
            flags &= ~(0x3 | 0x10 | 0x40 | 0x80);
            if (flags != 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(String.format("Unknown Flags: %x", flags));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

}
