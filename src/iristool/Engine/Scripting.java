/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFActionChunk;
import BFDataFileLib.BFChunkParsers.Map.BFActionListChunk;
import BFDataFileLib.BFChunkParsers.Map.BFEventsChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingFunctionCodeChunk;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingVariablesAndFunctionCodeKeyCollection;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingVariablesChunk;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.EventTrackElem;
import BFDataFileLib.EventTrackFrameElem;
import Common.Convert;
import Common.DataType.OFCProgramCodeElem;
import Common.DataType.OVATypeElem;
import Common.DataType.VariableTypeMessage;
import Common.DataType.VariableTypeText;
import Common.DataType.Vector3;
import Common.DataType.ScriptingSpecialPointerElem;
import iristool.Engine.ScriptingEngine.ScriptingEngineCategoryFunctions;
import iristool.Engine.ScriptingEngine.ScriptingEngineFieldFunctions;
import iristool.Engine.ScriptingEngine.ScriptingEngineFunctionFunctions;
import iristool.Engine.ScriptingEngine.ScriptingEngineKeywordFunctions;
import iristool.Engine.ScriptingEngine.ScriptingEngineTypeFunctions;
import java.util.ArrayList;

/**
 *
 * @author andra
 */
public class Scripting {

    private static final boolean SCRIPT_RESOLVE_DEBUG = false;
    private static int[] SCRIPT_FUNCTIONID_TO_OFFSET;

    public static void init() {
        int i;
        int tmp, maxID;
        int tmpFuncIDs[];

        maxID = ScriptingEngineCategoryFunctions.getMaxFunctionID();
        tmp = ScriptingEngineTypeFunctions.getMaxFunctionID();
        if (tmp > maxID) {
            maxID = tmp;
        }
        tmp = ScriptingEngineKeywordFunctions.getMaxFunctionID();
        if (tmp > maxID) {
            maxID = tmp;
        }
        tmp = ScriptingEngineFunctionFunctions.getMaxFunctionID();
        if (tmp > maxID) {
            maxID = tmp;
        }
        tmp = ScriptingEngineFieldFunctions.getMaxFunctionID();
        if (tmp > maxID) {
            maxID = tmp;
        }
        if (maxID > 0) {
            SCRIPT_FUNCTIONID_TO_OFFSET = new int[maxID + 1];

            tmpFuncIDs = ScriptingEngineCategoryFunctions.getFunctionIDs();
            for (i = 0; i < ScriptingEngineCategoryFunctions.getNumberOfFunctions(); i++) {
                SCRIPT_FUNCTIONID_TO_OFFSET[tmpFuncIDs[i]] = i;
            }

            tmpFuncIDs = ScriptingEngineTypeFunctions.getFunctionIDs();
            for (i = 0; i < ScriptingEngineTypeFunctions.getNumberOfFunctions(); i++) {
                SCRIPT_FUNCTIONID_TO_OFFSET[tmpFuncIDs[i]] = i;
            }

            tmpFuncIDs = ScriptingEngineKeywordFunctions.getFunctionIDs();
            for (i = 0; i < ScriptingEngineKeywordFunctions.getNumberOfFunctions(); i++) {
                SCRIPT_FUNCTIONID_TO_OFFSET[tmpFuncIDs[i]] = i;
            }

            tmpFuncIDs = ScriptingEngineFunctionFunctions.getFunctionIDs();
            for (i = 0; i < ScriptingEngineFunctionFunctions.getNumberOfFunctions(); i++) {
                SCRIPT_FUNCTIONID_TO_OFFSET[tmpFuncIDs[i]] = i;
            }

            tmpFuncIDs = ScriptingEngineFieldFunctions.getFunctionIDs();
            for (i = 0; i < ScriptingEngineFieldFunctions.getNumberOfFunctions(); i++) {
                SCRIPT_FUNCTIONID_TO_OFFSET[tmpFuncIDs[i]] = i;
            }
        } else {
            System.out.println("Scripting init: BUG: maxID is null.");
        }
    }

    public static void resolveUnkGaoStorage(BFDataFileClass bfdc, BFGaoChunk gao) {
        BFChunkParseInterface cpi;
        if (gao == null || bfdc == null || bfdc.getParsedObjectDatabaseClass() == null) {
            return;
        }
        if (gao.getU0x40key1() != 0) {
            cpi = bfdc.getParsedObjectDatabaseClass().getObject(gao.getU0x40key1());
            if (cpi instanceof BFGaoChunk) {
                gao.setU0x40Gao1((BFGaoChunk) cpi);
            }
        }
        if (gao.getU0x40key2() != 0) {
            cpi = bfdc.getParsedObjectDatabaseClass().getObject(gao.getU0x40key2());
            if (cpi instanceof BFGaoChunk) {
                gao.setU0x40Gao2((BFGaoChunk) cpi);
            }
        }
        if (gao.getU0x40key3() != 0) {
            cpi = bfdc.getParsedObjectDatabaseClass().getObject(gao.getU0x40key3());
            if (cpi instanceof BFGaoChunk) {
                gao.setU0x40Gao3((BFGaoChunk) cpi);
            }
        }
        if (gao.getU0x40key4() != 0) {
            cpi = bfdc.getParsedObjectDatabaseClass().getObject(gao.getU0x40key4());
            if (cpi instanceof BFGaoChunk) {
                gao.setU0x40Gao4((BFGaoChunk) cpi);
            }
        }
    }

    public static void scriptingResolveFunctions(BFDataFileClass bfdc, BFScriptingVariablesAndFunctionCodeKeyCollection kk) {
        int i, j;
        BFScriptingFunctionCodeChunk ofc;
        OFCProgramCodeElem elem;
        ArrayList<BFKeyListElem> kles = kk.getScriptingKLEs();
        for (i = 0; i < kles.size(); i++) {
            if (kles.get(i) == null || kles.get(i).getParseHandler() == null
                    || !(kles.get(i).getParseHandler() instanceof BFScriptingFunctionCodeChunk)) {
                continue;
            }
            ofc = (BFScriptingFunctionCodeChunk) (kles.get(i).getParseHandler());

            if (ofc.getOfcpes() == null) { // no scripting
                return;
            }

            for (j = 0; j < ofc.getProgramCodeElemNum(); j++) {
                elem = ofc.getOfcpes()[j];
                if (elem == null || (elem.getFlags() & 0x4) != 0) {
                    continue;
                }
                elem.setFlags((byte) (elem.getFlags() | 0x4));
                if (elem.getCategory() == (byte) 0x1f) {
                    break; // end of script
                }
                if (elem.getCategory() == (byte) 0x1) {
                    if (elem.getFuncID() == (byte) 0x26) {
                        elem.setVal(elem.getVal() + Convert.hexArrayToInt(ofc.getDesc()));
                    } else {
                        scriptingResolveFunctionValue(bfdc, elem);
                    }
                }
                // resolve ids to array offsets to speed up
                if (elem.getCategory() != (byte) 0x18) { // do not relocate funcid in this case
                    elem.setFuncID((short) SCRIPT_FUNCTIONID_TO_OFFSET[elem.getFuncID()]);
                }
                elem.setCategory((byte) SCRIPT_FUNCTIONID_TO_OFFSET[elem.getCategory()]);
            }
        }
    }

    public static void scriptingResolveFunctionsInEventTrackFrame(BFDataFileClass bfdc, EventTrackElem ete) {
        int i, j;
        EventTrackFrameElem etfe;
        OFCProgramCodeElem elem;
        if (ete == null || ete.getEventTrackFrameElems() == null) {
            return;
        }
        for (i = 0; i < ete.getEventTrackFrameElems().size(); i++) {
            etfe = ete.getEventTrackFrameElems().get(i);
            if (etfe == null || etfe.getProgramCode() == null) {
                continue;
            }
            for (j = 0; j < etfe.getProgramCode().size(); j++) {

                elem = etfe.getProgramCode().get(j);
                if (elem == null) {
                    continue;
                }
                if (elem.getCategory() == (byte) 0x1f) {
                    break; // end of script
                }
                if (elem.getCategory() == (byte) 0x1) {
                    scriptingResolveFunctionValue(bfdc, elem);
                }
                // resolve ids to array offsets to speed up
                if (elem.getCategory() != (byte) 0x18) { // do not relocate funcid in this case
                    elem.setFuncID((short) SCRIPT_FUNCTIONID_TO_OFFSET[elem.getFuncID()]);
                }
                elem.setCategory((byte) SCRIPT_FUNCTIONID_TO_OFFSET[elem.getCategory()]);
            }
        }
    }

    public static BFChunkParseInterface scriptingResolveValueReference(BFDataFileClass bfdc, int funcID, int val) {

        if (val == 0) {
            return null;
        }

        if (funcID == (byte) 0x28 && val != 0 && (val == 1 || val == 2)) {
            return null;
        }

        BFChunkParseInterface cpi = bfdc.getParsedObjectDatabaseClass().getObject(val);
        if (cpi == null) {
            System.out.format("scriptingResolveValueReference: BUG: Can not resolve %x key\n", val);
        } else if (cpi instanceof BFGaoChunk && SCRIPT_RESOLVE_DEBUG) {
            System.out.format("scriptingResolveValueReference: Resolving %x key to gao: %s\n", val, ((BFGaoChunk) cpi).getGaoName());
        } else if (SCRIPT_RESOLVE_DEBUG) {
            System.out.format("scriptingResolveValueReference: Resolving %x key to type: %s\n", val, cpi.getClass().getCanonicalName());
        }
        return cpi;
    }

    public static boolean isValAReference(int funcID) {
        switch (funcID) {
            case 0x27:
            case 0x28:
            case 0x2a:
            case 0x2b:
                return true;
        }
        return false;
    }

    public static void scriptingResolveFunctionValue(BFDataFileClass bfdc, OFCProgramCodeElem elem) {
        if (isValAReference(elem.getFuncID())) {
            BFChunkParseInterface cpi = scriptingResolveValueReference(bfdc, elem.getFuncID(), elem.getVal());
            if (cpi != null) {
                elem.setCpiResolved(cpi);
            }
        }
    }

    public static void scriptingCopyVariableResolutions(ArrayList<ScriptingSpecialPointerElem> src, OVATypeElem srcte,
            ArrayList<ScriptingSpecialPointerElem> dst, OVATypeElem dstte) {
        int i;
        ScriptingSpecialPointerElem vk;
        if (src == null || srcte == null || dst == null || dstte == null) {
            return;
        }
        int pos = srcte.getMemoryOffset() + (4 * srcte.getArrayDimension());
        int endPos = pos + (BFScriptingVariablesChunk.getVariableSizeInBytesByType(srcte.getTypeOrig()) * srcte.getRawDataElemNumWithoutDimensionSizes());
        int dstPos = dstte.getMemoryOffset() + (4 * dstte.getArrayDimension());
        int relativePos;
        for (i = 0; i < src.size(); i++) {
            vk = src.get(i);
            if (vk.getPos() >= pos && vk.getPos() < endPos) {
                relativePos = vk.getPos() - pos;
                dst.add(new ScriptingSpecialPointerElem(dstPos + relativePos, ScriptingSpecialPointerElem.TYPE_SCRIPTING, vk.getResolved(), 0));
            }
        }
    }

    public static void scriptingResolveVariables(BFDataFileClass bfdc, BFScriptingVariablesChunk ova) {
        int i, j;
        OVATypeElem te;
        Object obj;
        BFChunkParseInterface cpi;

        if (ova == null || ova.getTypeElems() == null) {
            return;
        }

        for (i = 0; i < ova.getTypeElems().size(); i++) {
            te = ova.getTypeElems().get(i);
            if (te == null) {
                continue;
            }
            if (ova.getTypeElems() == null || ova.getTypeElems().size() <= i) {
                System.out.println("scriptingResolveVariables: BUG: invalid values.");
                continue;
            }
            if (isValAReference(te.getType())) {
                if (SCRIPT_RESOLVE_DEBUG) {
                    System.out.println("Resolving: " + ova.getVariableNames().get(i));
                }
                for (j = 0; j < ova.getTypeElems().get(i).getRawDataElemNumWithoutDimensionSizes(); j++) {
                    obj = ova.getDefaultValueAtIndexSimple(i, j);
                    if (!(obj instanceof Integer)) {
                        System.out.println("scriptingResolveVariables: BUG: can not resolve different than integer.");
                        continue;
                    }
                    cpi = scriptingResolveValueReference(bfdc, te.getType(), (int) obj);
                    if (cpi != null) {
                        ova.addResolvedDefaultValueSimple(te, i, j, ScriptingSpecialPointerElem.TYPE_SCRIPTING, cpi, 0);
                    }
                }
                // resolve ids to array offsets to speed up
                te.setType((short) SCRIPT_FUNCTIONID_TO_OFFSET[te.getType()]);
            }
        }
    }

    public static void scriptingResolveModel(BFDataFileClass bfdc, BFScriptingVariablesAndFunctionCodeKeyCollection kk) {
        int i;
        BFScriptingVariablesChunk ova;
        if (kk == null || kk.isModelResolved() || kk.getScriptingKLEs() == null) {
            return;
        }
        scriptingResolveFunctions(bfdc, kk);
        ArrayList<BFKeyListElem> kles = kk.getScriptingKLEs();
        // TODO: use getVariablesChunk()
        for (i = 0; i < kles.size(); i++) {
            if (kles.get(i) == null || kles.get(i).getParseHandler() == null
                    || !(kles.get(i).getParseHandler() instanceof BFScriptingVariablesChunk)) {
                continue;
            }
            ova = (BFScriptingVariablesChunk) (kles.get(i).getParseHandler());
            scriptingResolveVariables(bfdc, ova);
        }
    }

    public static void scriptingCombineDefaultValuesWithOverrides(BFDataFileClass bfdc, BFScriptingContainerChunk scc) {
        BFScriptingVariablesChunk ovaOverrides, ovaModel;
        int i, j;
        if (scc.getVariableOverridesChunkKLE() == null || scc.getVariableOverridesChunkKLE().getParseHandler() == null
                || !(scc.getVariableOverridesChunkKLE().getParseHandler() instanceof BFScriptingVariablesChunk)) {
            return;
        }
        ovaOverrides = (BFScriptingVariablesChunk) (scc.getVariableOverridesChunkKLE().getParseHandler());
        ovaModel = scc.getVariablesChunk();
        if (ovaModel == null) {
            return;
        }

        scriptingResolveVariables(bfdc, ovaOverrides);

        if (ovaOverrides.getVariableNames() == null) {
            return;
        }

        int dimension, dimension2;
        int size;
        for (i = 0; i < ovaModel.getVariableNames().size(); i++) {
            for (j = 0; j < ovaOverrides.getVariableNames().size(); j++) {
                if (ovaModel.getVariableNames().get(i).equals(ovaOverrides.getVariableNames().get(j))) {

                    if (ovaModel.getTypeElems().get(i).getTypeNum().equals(ovaOverrides.getTypeElems().get(j).getTypeNum())) { // size and dimeansion equal
                        if ((dimension = ovaOverrides.getTypeElems().get(j).getArrayDimension()) != 0) {
                            // copy dimension data
                            System.arraycopy(ovaOverrides.getVariableStoragePool(), ovaOverrides.getTypeElems().get(j).getMemoryOffset(),
                                    ovaModel.getVariableStoragePool(), ovaModel.getTypeElems().get(i).getMemoryOffset(),
                                    4 * dimension);
                        }
                        System.arraycopy(ovaOverrides.getVariableStoragePool(), ovaOverrides.getTypeElems().get(j).getMemoryOffset() + (4 * dimension),
                                ovaModel.getVariableStoragePool(), ovaModel.getTypeElems().get(i).getMemoryOffset() + (4 * dimension),
                                ovaOverrides.getTypeElems().get(j).getRawDataElemNumWithoutDimensionSizes()
                                * BFScriptingVariablesChunk.getVariableSizeInBytesByType(ovaOverrides.getTypeElems().get(j).getTypeOrig()));
                        ovaModel.clearDefaultValueResolutions(ovaModel.getTypeElems().get(i));
                        scriptingCopyVariableResolutions(ovaOverrides.getVariableDefaultsResolved(), ovaOverrides.getTypeElems().get(j),
                                ovaModel.getVariableDefaultsResolved(), ovaModel.getTypeElems().get(i));
                        if (SCRIPT_RESOLVE_DEBUG) {
                            System.out.println("simple copy: " + ovaOverrides.getVariableNames().get(j));
                        }
                    } else {
                        size = ovaOverrides.getTypeElems().get(j).getRawDataElemNumWithoutDimensionSizes();
                        if (size > ovaModel.getTypeElems().get(i).getRawDataElemNumWithoutDimensionSizes()) {
                            size = ovaModel.getTypeElems().get(i).getRawDataElemNumWithoutDimensionSizes();
                        }
                        dimension2 = ovaModel.getTypeElems().get(i).getArrayDimension();
                        System.arraycopy(ovaOverrides.getVariableStoragePool(), ovaOverrides.getTypeElems().get(j).getMemoryOffset() + (4 * dimension2),
                                ovaModel.getVariableStoragePool(), ovaModel.getTypeElems().get(i).getMemoryOffset() + (4 * dimension2),
                                size * BFScriptingVariablesChunk.getVariableSizeInBytesByType(ovaOverrides.getTypeElems().get(j).getTypeOrig()));
                        ovaModel.clearDefaultValueResolutions(ovaModel.getTypeElems().get(i));
                        scriptingCopyVariableResolutions(ovaOverrides.getVariableDefaultsResolved(), ovaOverrides.getTypeElems().get(j),
                                ovaModel.getVariableDefaultsResolved(), ovaModel.getTypeElems().get(i));
                        if (SCRIPT_RESOLVE_DEBUG) {
                            System.out.println("dim keep & copy: " + ovaOverrides.getVariableNames().get(j));
                        }
                    }
                }
            }
        }

    }

    public static Object copyVariable(Object src) {
        if (src instanceof Integer) {
            return (int) src;
        } else if (src instanceof Vector3) {
            return ((Vector3) (src)).duplicate();
        } else if (src instanceof VariableTypeMessage) {
            return ((VariableTypeMessage) (src)).duplicate();
        } else if (src instanceof VariableTypeText) {
            return ((VariableTypeText) (src)).duplicate();
        } else {
            System.out.println("copyVariable: BUG: I do not know, how to copy this: " + src.getClass().getCanonicalName());
            return null;
        }
    }

    public static void scriptingMakeInstanceVariablesFromModelVariables(BFDataFileClass bfdc, BFScriptingContainerChunk scc) {
        BFScriptingVariablesChunk ova;
        int i;
        if (scc.getInstanceVariableDefaultsDefault() == null) {
            scc.setInstanceVariableDefaults(null);
            scc.setInstanceVariableDefaultsResolved(null);
            scc.setInstanceVariableDefaultsDefault(null);
            scc.setInstanceVariableDefaultsResolvedDefault(null);
            if (scc.getFunctionsAndVariablesChunkKLE() == null || scc.getFunctionsAndVariablesChunkKLE().getParseHandler() == null) {
                return;
            }
            if ((ova = scc.getVariablesChunk()) == null) {
                return;
            }

            scc.setInstanceVariableDefaultsResolved(new ArrayList<>());
            scc.setInstanceVariableDefaultsResolvedDefault(new ArrayList<>());

            // copy variables
            byte[] vd = ova.getVariableStoragePool();
            if (vd != null && vd.length != 0) {
                byte[] newVD = new byte[vd.length];
                System.arraycopy(vd, 0, newVD, 0, vd.length);
                scc.setInstanceVariableDefaults(newVD);
            }
            ScriptingSpecialPointerElem kp;
            if (ova.getVariableDefaultsResolved() != null) {
                for (i = 0; i < ova.getVariableDefaultsResolved().size(); i++) {
                    kp = ova.getVariableDefaultsResolved().get(i);
                    scc.getInstanceVariableDefaultsResolvedDefault().add(new ScriptingSpecialPointerElem(kp.getPos(), ScriptingSpecialPointerElem.TYPE_SCRIPTING, kp.getResolved(), 0));
                }
            }
            // copy end
            if (scc.getVariableOverridesChunkKLE() != null && scc.getVariableOverridesChunkKLE().getParseHandler() != null) {
                scriptingCombineDefaultValuesWithOverrides(bfdc, scc);
            }
        }
    }

    public static void resolveGaoScriptingInstance(BFDataFileClass bfdc, BFScriptingContainerChunk scc) {
        BFScriptingVariablesAndFunctionCodeKeyCollection kk;
        if (scc == null || scc.getFunctionsAndVariablesChunkKLE() == null
                || (kk = (BFScriptingVariablesAndFunctionCodeKeyCollection) (scc.getFunctionsAndVariablesChunkKLE().getParseHandler())) == null) {
            return;
        }
        scriptingResolveModel(bfdc, kk);
        scriptingMakeInstanceVariablesFromModelVariables(bfdc, scc);
    }

    public static void initScriptingInstanceVariables(BFScriptingContainerChunk scc) {

    }

    public static void resolveGaoScripting(BFDataFileClass bfdc, BFGaoChunk gao) {
        int i, j;
        BFScriptingContainerChunk scc;
        BFActionChunk ac;
        if ((gao.getFlags() & 0x20) != 0) {
            if (gao.getScriptingContainerChunkKLE() == null
                    || (scc = (BFScriptingContainerChunk) (gao.getScriptingContainerChunkKLE().getParseHandler())) == null) {
                return;
            }
            resolveGaoScriptingInstance(bfdc, scc);
            initScriptingInstanceVariables(scc);
        }
        if ((gao.getFlags() & 0x2000000) != 0) {
            // TODO: maybe track otions init ?
            if (gao.getUnk5eventsChunkKLE() != null && gao.getUnk5eventsChunkKLE().getParseHandler() != null) {
                if (SCRIPT_RESOLVE_DEBUG) {
                    System.out.println("Unk5Events resolve: " + gao.getGaoName());
                }
                AnimationPlayer.resolveEventTracks(bfdc, (BFEventsChunk) (gao.getUnk5eventsChunkKLE().getParseHandler()));
            }
        }
        if ((gao.getFlags() & 0x2) != 0) {
            if (gao.getActionListChunkKLE() != null && gao.getActionListChunkKLE().getParseHandler() != null) {
                if (SCRIPT_RESOLVE_DEBUG) {
                    System.out.println("Actions resolve: " + gao.getGaoName());
                }
                BFActionListChunk alc = (BFActionListChunk) (gao.getActionListChunkKLE().getParseHandler());
                if (alc.getActionsKLE() != null) {
                    for (i = 0; i < alc.getActionsKLE().size(); i++) {
                        if (alc.getActionsKLE().get(i) == null || alc.getActionsKLE().get(i).getParseHandler() == null) {
                            continue;
                        }
                        ac = (BFActionChunk) (alc.getActionsKLE().get(i).getParseHandler());
                        if (ac.getEventsChunksKLE() == null) {
                            continue;
                        }
                        for (j = 0; j < ac.getEventsChunksKLE().size(); j++) {
                            if (ac.getEventsChunksKLE().get(j) == null || ac.getEventsChunksKLE().get(j).getParseHandler() == null) {
                                continue;
                            }
                            AnimationPlayer.resolveEventTracks(bfdc, (BFEventsChunk) (ac.getEventsChunksKLE().get(j).getParseHandler()));
                        }
                    }
                }
            } else {
                // TODO: is it needed? It was done in WOLParser after events load too.
                if (SCRIPT_RESOLVE_DEBUG) {
                    System.out.println("Events without actions resolve: " + gao.getGaoName());
                }
                if (gao.getEventsChunkKLE() != null && gao.getEventsChunkKLE().getParseHandler() != null) {
                    AnimationPlayer.resolveEventTracks(bfdc, (BFEventsChunk) (gao.getEventsChunkKLE().getParseHandler()));
                }
            }

        }
    }

    public static void resolveWowScripting(BFDataFileClass bfdc, ArrayList<BFGaoChunk> gaos) {
        int i;
        BFGaoChunk gao;
        if (gaos == null || bfdc == null || bfdc.getParsedObjectDatabaseClass() == null) {
            return;
        }

        for (i = 0; i < gaos.size(); i++) {
            gao = gaos.get(i);
            if (gao == null) {
                continue;
            }
            resolveGaoScripting(bfdc, gao);
            resolveUnkGaoStorage(bfdc, gao);
        }
    }
}
