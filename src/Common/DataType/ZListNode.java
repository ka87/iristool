/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import java.util.ArrayList;

/**
 *
 * @author andra
 */
public class ZListNode {
    private ArrayList<ZListNodeElem> elems;

    public ZListNode() {
        elems = new ArrayList<>();
    }

    public ArrayList<ZListNodeElem> getElems() {
        return elems;
    }    
}
