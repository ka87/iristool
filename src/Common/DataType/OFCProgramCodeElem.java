/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import BFDataFileLib.BFChunkParseInterface;
import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;
import iristool.Engine.Scripting;

/**
 *
 * @author andras
 */
public class OFCProgramCodeElem extends Logger {

    private Integer val;
    private Short funcID;
    private Byte flags, category; // flags & 0x4 => has been resolved, category: game uses this field to handle actual OFCProgramCodeElem

    private Integer valOrig;
    private Short funcIDOrig;
    private Byte flagsOrig, categoryOrig;

    // RUNTIME only
    private BFChunkParseInterface cpiResolved = null;

    public BFChunkParseInterface getCpiResolved() {
        return cpiResolved;
    }

    public Integer getValOrig() {
        return valOrig;
    }

    public Short getFuncIDOrig() {
        return funcIDOrig;
    }

    public Byte getFlagsOrig() {
        return flagsOrig;
    }

    public Byte getCategoryOrig() {
        return categoryOrig;
    }

    public void setCpiResolved(BFChunkParseInterface cpiResolved) {
        this.cpiResolved = cpiResolved;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public Short getFuncID() {
        return funcID;
    }

    public void setFuncID(Short funcID) {
        this.funcID = funcID;
    }

    public Byte getFlags() {
        return flags;
    }

    public void setFlags(Byte flags) {
        this.flags = flags;
    }

    public Byte getCategory() {
        return category;
    }

    public void setCategory(Byte category) {
        this.category = category;
    }

    public OFCProgramCodeElem(Integer val, Short funcID, Byte flags, Byte category) {
        this.val = val;
        this.funcID = funcID;
        this.flags = flags;
        this.category = category;

        this.valOrig = val;
        this.funcIDOrig = funcID;
        this.flagsOrig = flags;
        this.categoryOrig = category;
    }

    public OFCProgramCodeElem() {
    }

    public boolean readValues(ReaderUtil ru) {

        this.val = ru.readInt(false);
        this.funcID = ru.readShort(false);
        this.flags = ru.readByte(false);
        this.category = ru.readByte(false);

        this.valOrig = this.val;
        this.funcIDOrig = this.funcID;
        this.flagsOrig = this.flags;
        this.categoryOrig = this.category;

        return (this.val != null && this.funcID != null && this.flags != null && this.category != null);
    }

    @Override
    public String toString() {
        return String.format("OFCProgramCodeElem: val=%d (%x), funcID=%d (%x), flags=%x, Category=%d (%x)",
                valOrig, valOrig, funcIDOrig, funcIDOrig, flagsOrig, categoryOrig, categoryOrig);
    }

    public String getActualElemDescription() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            switch (categoryOrig) {
                case 0x1:
                    return String.format("call type function %d (%x, %s)", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig));
                case 0x2:
                    return String.format("call keyword function %d (%x, %s)", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig));
                case 0x3:
                    return String.format("call function %d (%x, %s)", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig));
                case 0x4:
                    return String.format("call field function %d (%x, %s)", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig));
                case 0x5:
                    return String.format("call function %d (%x, %s) with param offset val (%x), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), valOrig);
                case 0x6:
                    return String.format("call function %d (%x, %s) with param offset val (%x) ARRAY (id from stack), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), valOrig);
                case 0x7:
                    return String.format("call function %d (%x, %s) with param offset val (%x) OFFSET (from stack) ARRAY (id from stack), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), val);
                case 0x8:
                    return String.format("call function %d (%x, %s) with param offset val (%x) 2 OFFSETS (from stack) ARRAY (id from stack), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), val);
                case 0x14:
                    return String.format("call function %d (%x, %s) with param offset val (CURRINST:%x), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), valOrig);
                case 0x15:
                    return String.format("call function %d (%x, %s) with param offset val (CURRINST:%x) ARRAY (id from stack), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), valOrig);
                case 0x16:
                    return String.format("call function %d (%x, %s) with param offset val (CURRINST:%x) OFFSET (from stack) ARRAY (id from stack), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), valOrig);
                case 0x17:
                    return String.format("call function %d (%x, %s) with param offset val (CURRINST:%x) 2 OFFSETS (from stack) ARRAY (id from stack), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), valOrig);
                case 0x18:
                    return String.format("call function[hardcoded to categ funcid 0x2c] %d (%x, %s) with param offset val (%x), save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig), valOrig);
                case 0x19:
                    return String.format("call function %d (%x, %s) LOOP (loopnum from stack)", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig));
                case 0x1a:
                    return String.format("call function %d (%x, %s) with param curr event track options blendfactor, save result to stack", funcIDOrig, funcIDOrig, getFuncIDDescription(funcIDOrig));
                case 0x1f:
                    return "last elem of the script"; // get one elem from stack, does nothing
            }
            return "unknown";
        } else {
            return "";
        }
    }

    private String getFuncIDDescription(Short fID) {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            switch (fID) {
                case 0x20:
                    return "set val (int) [0x20]";
                case 0x21:
                    return "set val (int) [0x21]";
                case 0x22:
                    return "set val (int) [0x22]";
                case 0x25:
                    return "set val (vec3)";
                case 0x2c:
                    return "memcpy 8 bytes";
                case 0x28:
                    return "resolve key";
                case 0x806:
                    return "set ai current ultra";
                case 0x898:
                    return "copy stack elem (float/double) with cast";
                case 0x89b:
                    return "inc float/double";
                case 0x89c:
                    return "dec float/double";
                case 0x8a0:
                    return "negate float/double";
                case 0x8a2:
                    return "is first param lteq second";
                case 0x8a5:
                    return "is first param greather than second";
                case 0x8fc:
                    return String.format("unconditional goto [currElem]+[val=%d]", valOrig);
                case 0x900:
                    return String.format("if [stackCurrVal] is 0 goto [curr elem]+[val=%d]", valOrig);
                case 0x902:
                    return String.format("call function %d (%x, %s) with param 0, and goto [curr elem]+[val=%d]", 0x20, 0x20, getFuncIDDescription((short) 0x20), valOrig);
                case 0x1770:
                    return "create vec from floats [stack]";
                case 0x1b61:
                    return "joystick mask set";
                case 0x1f46:
                    return "load world by key from strack (bin)";
                case 0x1f78:
                    return "make msg from stack (add to curr gao)";
                case 0x1f7d:
                    return "make msg from stack (add to global gao, used for sounds)"; // GST_SOUND_MANAGER
                case 0x1fb1:
                    return "destroy duplicated gao or disable normal gao";
                case 0x1fb3:
                    return "gao control flags set";
                case 0x1fb4:
                    return "gao control flags get";
                case 0x1fc5:
                    return "set gao capa";
                case 0x1fd8:
                    return "set gao invisible";
                case 0x1fd9:
                    return "gao control flags object inactivate set";
                case 0x1fe4:
                    return "set or remove handleFlags 0x2000000 to modifier (enable/disable modifier)";
                case 0x2046:
                    return "set gao morph val (f1, f2)";
                case 0x2047:
                    return "set gao morph slot to shape";
                case 0x2916:
                    return "get save flag by id";
                case 0x2bc4:
                    return "set gao local bounding box min, and set do not compute BV from local flag";
                case 0x2bc5:
                    return "get gao local bounding box max";
                case 0x2ee2:
                    return "call sound func arr 28th elem with [stack curr val]";
                case 0x2ef6:
                    return "play ai sound (and maybe set pos) [sndid, pos]";
                case 0x32c9:
                    return "event set frequency";
                case 0x3391:
                    return "set gao action";
                case 0x3398:
                    return "gao action kit merge, no overwrite";
                case 0x339b:
                    return "set gao action with flags";
                case 0x339c:
                    return "set gao action with freq";
                case 0x3cf3:
                    return "aftereffect set on/off and unknown";
            }
            return "unknown";
        } else {
            return "";
        }
    }

    public String getActualElemExtraDescription() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            StringBuilder sb = new StringBuilder();
            if (categoryOrig.equals((byte) 0x1)) {
                if (funcIDOrig.equals((short) 0x26)) {
                    sb.append("val += <next elems's val>; ");
                } else {
                    if (Scripting.isValAReference(funcIDOrig)) {
                        if (!valOrig.equals(0)) {
                            if (funcIDOrig.equals((short) 0x28) && valOrig != 0 && (valOrig == 1 || valOrig == 2)) {
                                sb.append("[ref]val is main actor, should not be updated (funcID == 0x28 && val != 0 && val <= 2); ");
                            } else {
                                sb.append("[ref]val is a key, should be resolved; ");
                                if (cpiResolved != null) {
                                    sb.append(" Resolved to: " + cpiResolved);
                                }
                            }
                        } else {
                            sb.append("[ref]val = 0; ");
                        }
                    } else {
                        sb.append("val should not be updated (not a reference); ");
                    }
                }
            }
            if (categoryOrig.equals((byte) 0x18)) {
                sb.append("funcID should not be set to array offset; ");
            }
            if (sb.length() == 0) {
                sb.append("none");
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
