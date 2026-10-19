/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.GUI;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFKeyListElem;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author andra
 */
public class GAOColorizedTreeCellRenderer extends DefaultTreeCellRenderer {

    private HashMap<Integer, Color> bitMaskToColorList = new HashMap();
    private HashMap<Integer, Color> gaoInnerTypeToColorList = new HashMap();

    public GAOColorizedTreeCellRenderer() {

    }

    public void addColorToBitmask(int bitmask, Color c) {
        bitMaskToColorList.put(bitmask, c);
    }

    public void addColorToGaoInnerType(int innerType, Color c) {
        gaoInnerTypeToColorList.put(innerType, c);
    }

    public void removeColors() {
        bitMaskToColorList.clear();
        gaoInnerTypeToColorList.clear();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        if (leaf && !sel && ((DefaultMutableTreeNode) value).getUserObject() instanceof BFGaoChunk) {
            BFGaoChunk gao = (BFGaoChunk) ((DefaultMutableTreeNode) value).getUserObject();
            Iterator it = bitMaskToColorList.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry) it.next();
                if (gao.getFlags() != null && (((int) gao.getFlags()) & ((int) pair.getKey())) != 0) {
                    setForeground((Color) pair.getValue());
                    break;
                }
            }
            it = gaoInnerTypeToColorList.entrySet().iterator();
            BFKeyListElem geoChunkKLE = gao.getGeoChunkKLE();
            Object obj;
            if (geoChunkKLE != null && (obj = geoChunkKLE.getParseHandler()) != null && obj instanceof BFGaoInnerChunkParser) {
                BFGaoInnerChunkParser gicp = (BFGaoInnerChunkParser) obj;
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it.next();
                    if (pair.getKey().equals(gicp.getGaoInnerType())) {
                        setForeground((Color) pair.getValue());
                        break;
                    }
                }
            }
        }
        return this;
    }
}
