/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFModifierBaseClass;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author andra
 */
public class Modifier {

    public static BFChunkParseInterface getModifierByType(ArrayList<BFChunkParseInterface> modList, Class objType, Integer whichOne) {
        int i = 0;
        for (BFChunkParseInterface mod : modList) {
            if (mod.getClass().equals(objType)) {
                if (i == (int) whichOne || whichOne == null) {
                    return mod;
                }
                i++;
            }
        }
        return null;
    }

    public static int countModifiersByType(ArrayList<BFChunkParseInterface> modList, Class objType) {
        int i = 0;
        for (BFChunkParseInterface mod : modList) {
            if (mod.getClass().equals(objType)) {
                i++;
            }
        }
        return i;
    }

    public static void updateModifiersForMultipartGaos(BFGaoChunk gao, BFGRPContainerChunk grpCont) {
        int i, j;
        if (gao == null || grpCont == null) {
            return;
        }

        BFGRPChunk grp = grpCont.getGRP();

        ArrayList<BFChunkParseInterface> mdfs = gao.getModifiersArray();
        if (mdfs != null) {
            for (i = 0; i < mdfs.size(); i++) {
                if (mdfs.get(i) instanceof BFModifierBaseClass) {
                    BFModifierBaseClass m = (BFModifierBaseClass) (mdfs.get(i));
                    m.setModifierGroup(grp);                    
                    // TODO: call modifier set functions
                }
            }
        }

        ArrayList<TreeMap<Integer, BFKeyListElem>> gArray = grp.getGrpGaoList();
        if (gArray != null) {
            for (i = 0; i < gArray.size(); i++) {
                BFKeyListElem kle = gArray.get(i).firstEntry().getValue();
                if (kle == null || kle.getParseHandler() == null) {
                    continue;
                }
                mdfs = ((BFGaoChunk) (kle.getParseHandler())).getModifiersArray();
                if (mdfs != null) {
                    for (j = 0; j < mdfs.size(); j++) {
                        if (mdfs.get(j) instanceof BFModifierBaseClass) {
                            BFModifierBaseClass m = (BFModifierBaseClass) (mdfs.get(j));
                            m.setModifierGroup(grp);
                            // TODO: call modifier set functions                            
                        }
                    }
                }
            }
        }
    }

    public static BFGRPChunk getGroupForModifierAndGao(BFGaoChunk gao, BFModifierBaseClass mod) {
        BFGRPChunk grp = mod.getModifierGroup();
        if (grp == null) {
            if (gao != null && gao.getGroupChunkKLE() != null && gao.getGroupChunkKLE().getParseHandler() != null) {
                BFGRPContainerChunk cont = (BFGRPContainerChunk) (gao.getGroupChunkKLE().getParseHandler());
                grp = cont.getGRP();
            }
        }
        return grp;
    }    
    
}
