/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import Common.DataType.ZListNode;
import Common.DataType.ZListNodeElem;
import java.util.ArrayList;

/**
 *
 * @author andra
 */
public class ZListSorter {

    private ZListNode[] zList;

    public ZListSorter() {
        this.zList = new ZListNode[0x4000];
    }

    public void clear() {
        for (int i = 0; i < this.zList.length; i++) {
            this.zList[i] = null;
        }
    }

    public ZListNode[] getNodes() {
        return zList;
    }

    public ZListNodeElem addNode(float z) {
        if (z < 0.0f) {
            z = 0.0f;
        }
        int dist = (Float.floatToIntBits(z) >> 17);

        if (dist >= 0x4000) {
            return null;
        }

        if (zList[dist] == null) {
            zList[dist] = new ZListNode();
        }

        ArrayList<ZListNodeElem> elems = zList[dist].getElems();
        if (elems == null) {
            System.out.println("addNode: BUG: zlist node elem is null");
            return null;
        }

        ZListNodeElem ne = new ZListNodeElem();
        elems.add(0, ne);

        return ne;
    }

}
