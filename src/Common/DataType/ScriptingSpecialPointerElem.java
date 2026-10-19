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
public class ScriptingSpecialPointerElem { // This can be used for storing for resolved objects, and passing to various scripting functions too

    public static final int TYPE_SCRIPTING = 0;
    public static final int TYPE_VARIABLE = 1; // use only for passing to scripting functions (eg. type)

    private int pos;
    private int type;
    private Object resolved;
    private byte[] varPtr;
    private int offset;

    public ScriptingSpecialPointerElem(int pos, int type, Object resolved, int offset) {
        this.pos = pos;
        this.type = type;
        this.resolved = resolved;
        this.offset = offset;
    }
    
    public ScriptingSpecialPointerElem(byte varPtr[], int offset) {
        this.pos = 0;
        this.type = TYPE_VARIABLE;
        this.resolved = null;
        this.offset = offset;
    }

    public int getPos() {
        return pos;
    }

    public int getType() {
        return type;
    }

    public Object getResolved() {
        return resolved;
    }

    public int getOffset() {
        return offset;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setResolved(Object resolved) {
        this.resolved = resolved;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte[] getVarPtr() {
        return varPtr;
    }

    public void setVarPtr(byte[] varPtr) {
        this.varPtr = varPtr;
    }
    
}
