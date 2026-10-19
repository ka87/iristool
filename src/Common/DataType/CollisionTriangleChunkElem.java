/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public class CollisionTriangleChunkElem {

    private Short vertexID1;
    private Short vertexID2;
    private Short vertexID3;
    private Short proxmityID1;
    private Short proxmityID2;
    private Short proxmityID3;

    public CollisionTriangleChunkElem() {
    }

    public boolean readVertexIDValues(ReaderUtil ru) {

        vertexID1 = ru.readShort(false);
        vertexID2 = ru.readShort(false);
        vertexID3 = ru.readShort(false);

        return (vertexID1 != null && vertexID2 != null && vertexID3 != null);
    }

    public boolean readProximityIDValues(ReaderUtil ru) {

        proxmityID1 = ru.readShort(false);
        proxmityID2 = ru.readShort(false);
        proxmityID3 = ru.readShort(false);

        return (proxmityID1 != null && proxmityID2 != null && proxmityID3 != null);
    }

    public Short getVertexID1() {
        return vertexID1;
    }

    public Short getVertexID2() {
        return vertexID2;
    }

    public Short getVertexID3() {
        return vertexID3;
    }

    public Short getProxmityID1() {
        return proxmityID1;
    }

    public Short getProxmityID2() {
        return proxmityID2;
    }

    public Short getProxmityID3() {
        return proxmityID3;
    }

    public void writeVertexIDValues(WriterUtil wu) {
        wu.writeShort(vertexID1);
        wu.writeShort(vertexID2);
        wu.writeShort(vertexID3);
    }

    public void writeProximityIDValues(WriterUtil wu) {
        wu.writeShort(proxmityID1);
        wu.writeShort(proxmityID2);
        wu.writeShort(proxmityID3);
    }

    @Override
    public String toString() {
        return toString(true, true);
    }

    public String toString(boolean vertices, boolean proximities) {
        String s = "";
        if (vertices) {
            s += String.format("vertexID1: %d (%x), vertexID2: %d (%x), vertexID3: %d (%x)",
                    vertexID1, vertexID1, vertexID2, vertexID2, vertexID3, vertexID3);
        }
        if (proximities) {
            if (s.length() > 0) {
                s += ", ";
            }
            s += String.format("proximityID1: %d (%x), proximityID2: %d (%x), proximityID3: %d (%x)",
                    proxmityID1, proxmityID1, proxmityID2, proxmityID2, proxmityID3, proxmityID3);
        }

        return "CollisionTriangleChunkElem: " + s;
    }
}
