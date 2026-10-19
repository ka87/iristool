/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;
import Common.ReaderUtil;

/**
 *
 * @author andras
 */
public class GeometryTriangleData extends Logger {

    private Short vertex1ID;
    private Short vertex2ID;
    private Short vertex3ID;
    private Short uv1ID;
    private Short uv2ID;
    private Short uv3ID;
    private Integer unk1;
    private Integer unk2;

    public Short getVertex1ID() {
        return vertex1ID;
    }

    public void setVertex1ID(Short vertex1ID) {
        this.vertex1ID = vertex1ID;
    }

    public Short getVertex2ID() {
        return vertex2ID;
    }

    public void setVertex2ID(Short vertex2ID) {
        this.vertex2ID = vertex2ID;
    }

    public Short getVertex3ID() {
        return vertex3ID;
    }

    public void setVertex3ID(Short vertex3ID) {
        this.vertex3ID = vertex3ID;
    }

    public Short getUv1ID() {
        return uv1ID;
    }

    public void setUv1ID(Short uv1ID) {
        this.uv1ID = uv1ID;
    }

    public Short getUv2ID() {
        return uv2ID;
    }

    public void setUv2ID(Short uv2ID) {
        this.uv2ID = uv2ID;
    }

    public Short getUv3ID() {
        return uv3ID;
    }

    public void setUv3ID(Short uv3ID) {
        this.uv3ID = uv3ID;
    }

    public Integer getUnk1() {
        return unk1;
    }

    public void setUnk1(Integer unk1) {
        this.unk1 = unk1;
    }

    public Integer getUnk2() {
        return unk2;
    }

    public void setUnk2(Integer unk2) {
        this.unk2 = unk2;
    }

    public GeometryTriangleData(Short vertex1ID, Short vertex2ID, Short vertex3ID, Short uv1ID, Short uv2ID, Short uv3ID, Integer unk1, Integer unk2) {
        this.vertex1ID = vertex1ID;
        this.vertex2ID = vertex2ID;
        this.vertex3ID = vertex3ID;
        this.uv1ID = uv1ID;
        this.uv2ID = uv2ID;
        this.uv3ID = uv3ID;
        this.unk1 = unk1;
        this.unk2 = unk2;
    }

    public GeometryTriangleData() {
    }

    public boolean readValues(ReaderUtil ru) {
        this.vertex1ID = ru.readShort(false);
        this.vertex2ID = ru.readShort(false);
        this.vertex3ID = ru.readShort(false);
        this.uv1ID = ru.readShort(false);
        this.uv2ID = ru.readShort(false);
        this.uv3ID = ru.readShort(false);
        this.unk1 = ru.readInt(false);
        this.unk2 = ru.readInt(false);
              
        return (this.vertex1ID != null && this.vertex2ID != null && this.vertex3ID != null
                && this.uv1ID != null && this.uv2ID != null && this.uv3ID != null 
                && this.unk1 != null && this.unk2 != null);
    }

    @Override
    public String toString() {
        return "GeometryTriangleData: vertex1ID: " + vertex1ID + ", vertex2ID: " + vertex2ID + ", vertex3ID:" + vertex3ID + ", uv1ID: " + uv1ID +
                ", uv2ID: " + uv2ID + ", uv3ID: " + uv3ID + ", unk1: " + unk1 + ", unk2: " + unk2;
    }
}
