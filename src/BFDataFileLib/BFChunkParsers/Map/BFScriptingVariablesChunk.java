/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map;

import BFDataFileLib.BFChunkParseClass;
import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import Common.Convert;
import Common.DataType.OVATypeElem;
import Common.DataType.Vector3;
import Common.DataType.VariableTypeMessage;
import Common.DataType.VariableTypeText;
import Common.DataType.ScriptingSpecialPointerElem;
import Common.IrisToolDefines;
import Common.ReaderUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author andras
 */
public class BFScriptingVariablesChunk extends BFChunkParseClass implements BFChunkParseInterface {

    private static HashMap<Integer, Integer> variableSizeMap;
    private Integer[] instanceOFCKey = new Integer[5];
    private ArrayList<Integer> callbackOFCKeys = null;
    private ArrayList<OVATypeElem> typeElems = null;
    private ArrayList<String> variableNames = null;
    private byte[] variableStoragePool = null;
    private ArrayList<ScriptingSpecialPointerElem> variableDefaultsResolved = null;

    public Integer[] getInstanceOFCKey() {
        return instanceOFCKey;
    }

    public ArrayList<Integer> getCallbackOFCKeys() {
        return callbackOFCKeys;
    }

    public void clearDefaultValueResolutions(OVATypeElem te) {
        if (variableDefaultsResolved == null) {
            return;
        }

        ArrayList<ScriptingSpecialPointerElem> clearArr = new ArrayList<>();

        int pos = te.getMemoryOffset() + (4 * te.getArrayDimension());
        int endPos = pos + (getVariableSizeInBytesByType(te.getTypeOrig()) * te.getRawDataElemNumWithoutDimensionSizes());
        ScriptingSpecialPointerElem vk;
        Iterator it = variableDefaultsResolved.iterator();
        while (it.hasNext()) {
            vk = (ScriptingSpecialPointerElem) (it.next());
            if (vk.getPos() >= pos && vk.getPos() < endPos) {
                clearArr.add(vk);
            }
        }
        it = clearArr.iterator();
        while (it.hasNext()) {
            variableDefaultsResolved.remove((ScriptingSpecialPointerElem) (it.next()));
        }
        clearArr.clear();
    }

    public void addResolvedDefaultValueSimple(OVATypeElem te, int index, int elemID, int type, Object obj, int offset) {
        int i;
        if (te == null || variableDefaultsResolved == null) {
            return;
        }

        int oneVariableElemSize = getVariableSizeInBytesByType(te.getTypeOrig());
        int pos = te.getMemoryOffset() + (te.getArrayDimension() * 4) + (elemID * oneVariableElemSize);
        int endPos = pos + oneVariableElemSize;
        int currPos;

        for (i = 0; i < variableDefaultsResolved.size(); i++) {
            currPos = variableDefaultsResolved.get(i).getPos();
            if (currPos == pos) {
                variableDefaultsResolved.get(i).setResolved(obj);
                variableDefaultsResolved.get(i).setOffset(offset);
                variableDefaultsResolved.get(i).setType(type);
                return;
            } else if (currPos > pos && currPos < endPos) {
                System.out.println("BUG: addResolvedDefaultValueSimple: possible resolved space overwrite");
            }
        }
        variableDefaultsResolved.add(new ScriptingSpecialPointerElem(pos, type, obj, offset));
    }

    private Object getDefaultResolvedValueAtPos(int pos) {
        if (variableDefaultsResolved == null) {
            return null;
        }
        for (int i = 0; i < variableDefaultsResolved.size(); i++) {
            if (variableDefaultsResolved.get(i) != null && variableDefaultsResolved.get(i).getPos() == pos) {
                return variableDefaultsResolved.get(i).getResolved();
            }
        }
        return null;
    }

    private static Object getDefaultValueElemAtPos(OVATypeElem te, byte[] pool, int pos) {
        Object ret = null;
        ReaderUtil tmpRU = new ReaderUtil(pool);
        if (tmpRU.seek(pos)) {
            if (getVariableSizeInBytesByType(te.getTypeOrig()) == 4) {
                ret = tmpRU.readInt(false);
            } else {
                switch (te.getTypeOrig()) {
                    case 0x25:
                        ret = new Vector3();
                        ((Vector3) ret).readValues(tmpRU);
                        break;
                    case 0x29:
                        ret = new VariableTypeMessage();
                        ((VariableTypeMessage) ret).readValues(tmpRU);
                        break;
                    case 0x2c:
                        ret = new VariableTypeText();
                        ((VariableTypeText) ret).readValues(tmpRU);
                        break;
                    default:
                        System.out.format("getDefaultValueElemAtPos: BUG: Can not convert to type 0x%x.\n", te.getTypeOrig());
                        ret = null;
                        break;
                }
            }
        }
        tmpRU.close();
        return ret;
    }

    private int _getIntFromBytes(byte[] b, int i1, int i2) {
        int ret = Convert.hexArrayToInt(b, i1, i2);
        ret = Convert.intBigEndianToLittleEndian(ret);
        return ret;
    }

    public Integer getArrayFirstDimension(int index) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index || !typeElems.get(index).isArray()) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        return _getIntFromBytes(variableStoragePool, te.getMemoryOffset(), 4);
    }

    public Integer getArraySecondDimension(int index) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index || typeElems.get(index).getArrayDimension() != 2) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        return _getIntFromBytes(variableStoragePool, te.getMemoryOffset() + 4, 4);
    }

    public Integer getArrayThirdDimension(int index) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index || typeElems.get(index).getArrayDimension() != 3) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        return _getIntFromBytes(variableStoragePool, te.getMemoryOffset() + 4 + 4, 4);
    }

    public Object getDefaultValue(int index) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index || typeElems.get(index).isArray()) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        return getDefaultValueElemAtPos(te, variableStoragePool, te.getMemoryOffset());
    }

    public Object getDefaultValueResolved(int index) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index || typeElems.get(index).isArray()) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        return getDefaultResolvedValueAtPos(te.getMemoryOffset());
    }

    public Object getDefaultValueAtIndexSimple(int index, int elemID) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        int oneVariableElemSize = getVariableSizeInBytesByType(te.getTypeOrig());
        int pos = (te.getArrayDimension() * 4) + (elemID * oneVariableElemSize);
        // TODO: te.getRawDataLengthWithoutDimensionSizes() check
        if (te.getRawDataElemNumWithoutDimensionSizes() <= elemID) {
            System.out.println("getDefaultValueAtIndexSimple: BUG: elemID asked outside of array.");
        }
        return getDefaultValueElemAtPos(te, variableStoragePool, te.getMemoryOffset() + pos);
    }

    public Integer getDefaultArrayPoolPos(int index, int elemIndex) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index
                || !typeElems.get(index).isArray() || typeElems.get(index).getArrayDimension() != 1) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        int elemNum = _getIntFromBytes(variableStoragePool, te.getMemoryOffset(), 4);
        int oneVariableElemSize = getVariableSizeInBytesByType(te.getTypeOrig());

        if (elemNum <= elemIndex) {
            System.out.format("getDefaultArrayValue: BUG: Wrong index requested. (index: %d, elemIndex: %d [elemnum: %d])\n", index, elemIndex, elemNum);
            return null;
        }
        return te.getMemoryOffset()
                + 4 * typeElems.get(index).getArrayDimension()
                + oneVariableElemSize * elemIndex;
    }

    public Object getDefaultArrayValue(int index, int elemIndex) {
        OVATypeElem te = typeElems.get(index);

        Integer pos = getDefaultArrayPoolPos(index, elemIndex);
        if (pos == null) {
            return null;
        }
        return getDefaultValueElemAtPos(te, variableStoragePool, pos);
    }

    public Object getDefaultArrayValueResolved(int index, int elemIndex) {

        Integer pos = getDefaultArrayPoolPos(index, elemIndex);
        if (pos == null) {
            return null;
        }

        return getDefaultResolvedValueAtPos(pos);
    }

    public Integer getDefaultTwoDimensionalArrayPoolPos(int index, int elemIndex1, int elemIndex2) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index
                || !typeElems.get(index).isArray() || typeElems.get(index).getArrayDimension() != 2) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        int elemNum1 = _getIntFromBytes(variableStoragePool, te.getMemoryOffset(), 4);
        int elemNum2 = _getIntFromBytes(variableStoragePool, te.getMemoryOffset() + 4, 4);
        int oneVariableElemSize = getVariableSizeInBytesByType(te.getTypeOrig());

        if (elemNum1 <= elemIndex1 || elemNum2 <= elemIndex2) {
            System.out.format("getDefaultTwoDimensionalArrayValue: BUG: Wrong index requested. (index: %d, elemIndex1: %d [elemnum1: %d], elemIndex2: %d [elemnum2: %d])\n",
                    index, elemIndex1, elemNum1, elemIndex2, elemNum2);
            return null;
        }

        return te.getMemoryOffset()
                + ((elemNum2 * elemIndex1 + elemIndex2) * oneVariableElemSize)
                + (4 * typeElems.get(index).getArrayDimension());
    }

    public Object getDefaultTwoDimensionalArrayValue(int index, int elemIndex1, int elemIndex2) {
        OVATypeElem te = typeElems.get(index);

        Integer pos = getDefaultTwoDimensionalArrayPoolPos(index, elemIndex1, elemIndex2);
        if (pos == null) {
            return null;
        }
        return getDefaultValueElemAtPos(te, variableStoragePool, pos);
    }

    public Object getDefaultTwoDimensionalArrayValueResolved(int index, int elemIndex1, int elemIndex2) {

        Integer pos = getDefaultTwoDimensionalArrayPoolPos(index, elemIndex1, elemIndex2);
        if (pos == null) {
            return null;
        }

        return getDefaultResolvedValueAtPos(pos);
    }

    public Integer getDefaultThreeDimensionalArrayPoolPos(int index, int elemIndex1, int elemIndex2, int elemIndex3) {
        if (variableStoragePool == null || typeElems == null || typeElems.size() <= index
                || !typeElems.get(index).isArray() || typeElems.get(index).getArrayDimension() != 3) {
            return null;
        }
        OVATypeElem te = typeElems.get(index);
        int elemNum1 = _getIntFromBytes(variableStoragePool, te.getMemoryOffset(), 4);
        int elemNum2 = _getIntFromBytes(variableStoragePool, te.getMemoryOffset() + 4, 4);
        int elemNum3 = _getIntFromBytes(variableStoragePool, te.getMemoryOffset() + 4 + 4, 4);
        int oneVariableElemSize = getVariableSizeInBytesByType(te.getTypeOrig());

        if (elemNum1 <= elemIndex1 || elemNum2 <= elemIndex2 || elemNum3 <= elemIndex3) {
            System.out.format("getDefaultThreeDimensionalArrayValue: BUG: Wrong index requested. (index: %d, elemIndex1: %d [elemnum1: %d], elemIndex2: %d [elemnum2: %d], elemIndex3: %d [elemnum3: %d])\n",
                    index, elemIndex1, elemNum1, elemIndex2, elemNum2, elemIndex3, elemNum3);
            return null;
        }

        return te.getMemoryOffset()
                + (elemNum3 * elemNum2 * elemIndex1
                + elemNum3 * elemIndex2
                + elemIndex3) * oneVariableElemSize
                + (4 * typeElems.get(index).getArrayDimension());
    }

    public Object getDefaultThreeDimensionalArrayValue(int index, int elemIndex1, int elemIndex2, int elemIndex3) {

        OVATypeElem te = typeElems.get(index);

        Integer pos = getDefaultThreeDimensionalArrayPoolPos(index, elemIndex1, elemIndex2, elemIndex3);
        if (pos == null) {
            return null;
        }

        return getDefaultValueElemAtPos(te, variableStoragePool, pos);
    }

    public Object getDefaultThreeDimensionalArrayValueResolved(int index, int elemIndex1, int elemIndex2, int elemIndex3) {

        Integer pos = getDefaultThreeDimensionalArrayPoolPos(index, elemIndex1, elemIndex2, elemIndex3);
        if (pos == null) {
            return null;
        }

        return getDefaultResolvedValueAtPos(pos);
    }

    public byte[] getVariableStoragePool() {
        return variableStoragePool;
    }

    public ArrayList<OVATypeElem> getTypeElems() {
        return typeElems;
    }

    public ArrayList<String> getVariableNames() {
        return variableNames;
    }

    public ArrayList<ScriptingSpecialPointerElem> getVariableDefaultsResolved() {
        return variableDefaultsResolved;
    }

    public BFScriptingVariablesChunk() {
        super();
        variableSizeInit();
    }

    private static void variableSizeInit() {
        if (variableSizeMap == null) {
            variableSizeMap = new HashMap<>();

            variableSizeMap.put(0x20, 4); // bool
            variableSizeMap.put(0x21, 4); // int
            variableSizeMap.put(0x22, 4); // float
            variableSizeMap.put(0x25, 0x0C); // vector
            variableSizeMap.put(0x26, 4); // texget string ptr, or str_global (ai_codes) ptr
            variableSizeMap.put(0x27, 4); // ai track (ofc function ptr)
            variableSizeMap.put(0x28, 4); // resolved key
            variableSizeMap.put(0x29, 0x34); // message (msg)
            variableSizeMap.put(0x2A, 4);
            variableSizeMap.put(0x2B, 4); // ways ptr
            variableSizeMap.put(0x2C, 8); // texget groupkey and id
            variableSizeMap.put(0x2D, 4); // key (txt, or material ?)
            variableSizeMap.put(0x2E, 4); // color
            variableSizeMap.put(0x7C, 0x0C);
            variableSizeMap.put(0x7D, 0x0C);
            variableSizeMap.put(0x7E, 0x0C);
            variableSizeMap.put(0x7F, 0x0C);
            variableSizeMap.put(0x80, 0x0C);
            variableSizeMap.put(0x81, 0x0C);
            variableSizeMap.put(0x82, 0x0C);
            variableSizeMap.put(0x83, 0x0C);
            variableSizeMap.put(0x84, 4);
            variableSizeMap.put(0x85, 0x0C);
        }
    }

    public static Integer getVariableSizeInBytesByType(short type) {
        variableSizeInit();
        Integer i = variableSizeMap.get((int) type);
        if (i == null) {
            i = 0;
        }

        return i;
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed /*TODO*/) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Scripting variables.");
        }

        Long len = initParse();

        long startPos = ru.getCurrentPos();

        typeElems = new ArrayList<>();
        callbackOFCKeys = new ArrayList<>();
        variableNames = new ArrayList<>();
        variableDefaultsResolved = new ArrayList<>();

        int i, j, typePartNum;
        Integer variableDefaultArrayDimension[];
        Object[] variableDefault;

        if (len == null) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Length read fail.");
            }
            return false;
        }

        if (len > 0) {
            Integer typePartSize = ru.readInt(false);
            if (typePartSize == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Type part size read fail.");
                }
                return false;
            }

            if (typePartSize % 12 != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Type part size invalid: %x", typePartSize);
                }
                return false;
            }

            typePartNum = typePartSize / 12;

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Variable type num: " + typePartNum);
            }

            for (i = 0; i < typePartNum; i++) {
                OVATypeElem ote = new OVATypeElem();
                if (!ote.readValues(ru)) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("OVATypeElem read failed.");
                    }
                    return false;
                }
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("%d/%d %s", i + 1, typePartNum, ote);
                }
                typeElems.add(ote);
            }

            Integer variableNamesSize = ru.readInt(false);

            if (variableNamesSize == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Variable names size read fail.");
                }
                return false;
            }

            if (variableNamesSize % 30 != 0) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("variables name size invalid: %x", typePartSize);
                }
                return false;
            }

            int variableNamesNum = variableNamesSize / 30;

            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Variable names num: " + variableNamesNum);
            }

            if (variableNamesNum != typePartNum) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Variable names number (%d) != type parts number (%d), expect problems...", variableNamesNum, typePartNum);
                }
            }

            String varName;
            for (i = 0; i < typePartNum; i++) {
                varName = ru.readNString(30, false, true);
                if (varName == null) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Variable name read fail.");
                    }
                    return false;
                }
                variableNames.add(varName);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    logF("Variable (%d) name: %s", i + 1, varName);
                }
            }

            if (!insideBinFile) {
                Integer unkDescSize = ru.readInt(false);
                if (unkDescSize == null) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        log("Unkdescsize read fail");
                    }
                    return false;
                }

                ru.readInt(false);

                int unkdescnum = unkDescSize / 20;

                Integer unkdescint1, unkdescint2;
                ArrayList<Integer> unkDescUnk1 = new ArrayList<>();
                ArrayList<Integer> unkDescUnk2 = new ArrayList<>();

                for (i = 0; i < unkdescnum; i++) {
                    ru.readInt(false);
                    unkdescint1 = ru.readInt(false);
                    unkdescint2 = ru.readInt(false);
                    unkDescUnk1.add(unkdescint1);
                    unkDescUnk2.add(unkdescint2);
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("unkdesc (%d) unkdescint1=%x (%d), unkdescint2=%x (%d)",
                                i, unkdescint1, unkdescint1, unkdescint2, unkdescint2);
                    }
                    ru.readShort(false);
                    ru.readShort(false);
                    ru.readInt(false);
                }

                String s1, s2;

                for (i = 0; i < unkdescnum; i++) {
                    if (unkDescUnk1.get(i) != 0) {
                        s1 = ru.readStringToEnd(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdesc (%d) allowed values string: %s", i, s1);
                        }
                    }
                    if (unkDescUnk2.get(i) != 0) {
                        s2 = ru.readStringToEnd(false);
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("unkdesc (%d) description string: %s", i, s2);
                        }
                    }
                }
            }

            Integer defaultValueStorageSize = ru.readInt(false);
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                logF("Default value storage size: %d (%x)", defaultValueStorageSize, defaultValueStorageSize);
            }

            if (defaultValueStorageSize != 0) {

                /*
                We need to read the whole default values in one step. I think it is a mistake, what ubisoft employees have done.
                Game reads default values in the order of OVATypeElems parsing earlier with ignoring memoryOffset parameter. 
                This is completely broken, there is no use to loop through this values, because without using memoryoffset param,
                if you read a variable, there is a high chance to read another variable's data (if variables not in increasing offset order). 
                The game's behaviour doesn't harm, because it uses emoryOffset paramter later then. (eg resolving-, addressing vars)
                 */
                variableStoragePool = ru.readByteArray(defaultValueStorageSize, false);
                ReaderUtil defaultValuesRU = new ReaderUtil(variableStoragePool);

                for (i = 0; i < typePartNum; i++) {
                    defaultValuesRU.seek(typeElems.get(i).getMemoryOffset());
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("Reading default value block for %d (%s)", (i + 1), ((variableNames != null && variableNames.size() > i) ? variableNames.get(i) : "<BUG>"));
                    }
                    if (typeElems.get(i).getTypeNum() != 1) {
                        Integer nnum = (typeElems.get(i).getTypeNum() >> 30) & 3;

                        if (nnum > 0) {
                            variableDefaultArrayDimension = new Integer[nnum];
                        } else {
                            variableDefaultArrayDimension = null;
                        }

                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("Default array dimension sizes(value size >> 30 [nnum, max 3]): %d (%x)", nnum, nnum);
                        }
                        for (j = 0; j < nnum; j++) {
                            variableDefaultArrayDimension[j] = defaultValuesRU.readInt(false);
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                logF("Default dimension size (%d/%d): %d (%x)", j + 1, nnum, variableDefaultArrayDimension[j], variableDefaultArrayDimension[j]);
                            }
                        }
                    }

                    Integer variableDefaultNum = typeElems.get(i).getTypeNum() & 0x3FFFFFFF;

                    if (getVariableSizeInBytesByType(typeElems.get(i).getType()) == 4) {
                        if (variableDefaultNum > 0) {
                            variableDefault = new Integer[variableDefaultNum];
                        } else {
                            variableDefault = null;
                        }
                        for (j = 0; j < variableDefaultNum; j++) {
                            variableDefault[j] = defaultValuesRU.readInt(false);
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                logF("Default (%d/%d): %d (%x)", j + 1, variableDefaultNum, variableDefault[j], variableDefault[j]);
                            }
                        }
                    } else {
                        variableDefault = null;
                        switch (typeElems.get(i).getType()) {
                            case 0x25:
                                if (variableDefaultNum > 0) {
                                    variableDefault = new Vector3[variableDefaultNum];
                                } else {
                                    variableDefault = null;
                                }
                                for (j = 0; j < variableDefaultNum; j++) {
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        logF("Default (%d/%d):", j + 1, variableDefaultNum);
                                    }
                                    variableDefault[j] = new Vector3();
                                    if (!((Vector3) variableDefault[j]).readValues(defaultValuesRU)) {
                                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                                            log("Vector3 read fail.");
                                        }
                                        return false;
                                    }
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        log(variableDefault[j]);
                                    }
                                }
                                break;
                            case 0x29:
                                if (variableDefaultNum > 0) {
                                    variableDefault = new VariableTypeMessage[variableDefaultNum];
                                } else {
                                    variableDefault = null;
                                }
                                for (j = 0; j < variableDefaultNum; j++) {
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        logF("Default (%d/%d):", j + 1, variableDefaultNum);
                                    }
                                    variableDefault[j] = new VariableTypeMessage();
                                    if (!((VariableTypeMessage) variableDefault[j]).readValues(defaultValuesRU)) {
                                        log("VariableTypeMessage read fail.");
                                        return false;
                                    }
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        log(variableDefault[j]);
                                    }
                                }
                                break;
                            case 0x2c:
                                if (variableDefaultNum > 0) {
                                    variableDefault = new VariableTypeText[variableDefaultNum];
                                } else {
                                    variableDefault = null;
                                }
                                for (j = 0; j < variableDefaultNum; j++) {
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        logF("Default (%d/%d):", j + 1, variableDefaultNum);
                                    }
                                    variableDefault[j] = new VariableTypeText();
                                    if (!((VariableTypeText) variableDefault[j]).readValues(defaultValuesRU)) {
                                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                                            log("VariableTypeText read fail.");
                                        }
                                        return false;
                                    }
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        log(variableDefault[j]);
                                    }
                                }
                                break;
                        }
                    }
                }
                defaultValuesRU.close();
            }
            // AI instance (periodically [time based])
            for (i = 0; i < 5; i++) {
                instanceOFCKey[i] = ru.readInt(false);
                if (instanceOFCKey[i] != null && instanceOFCKey[i] != 0) {
                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                        logF("AI Instance OFC Key (%d) found: %x", i, instanceOFCKey[i]);
                    }
                    bfdfc.addToKeyList(new BFKeyListElem(instanceOFCKey[i], BFScriptingFunctionCodeChunk.class));
                }
            }
            // AI callbacks (trigger based: eg after geo render)
            if ((ru.getCurrentPos() - startPos) != len) {
                Integer callbackOFCNum, callbackOFCKey;
                callbackOFCNum = ru.readInt(false);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("AI callback OFC key num: " + callbackOFCNum);
                }
                for (i = 0; i < callbackOFCNum; i++) {
                    callbackOFCKey = ru.readInt(false);
                    callbackOFCKeys.add(callbackOFCKey);
                    if (callbackOFCKey != null && callbackOFCKey != 0) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            logF("AI callback OFC Key (%d) found: %x", i, callbackOFCKey);
                        }
                        bfdfc.addToKeyList(new BFKeyListElem(callbackOFCKey, BFScriptingFunctionCodeChunk.class));
                    }
                }
            }
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Current pos: %x (%d)", ru.getCurrentPos(), ru.getCurrentPos());
        }

        if (insideBinFile && (ru.getCurrentPos() - startPos) != len) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("Parsed length != length");
            }
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbCb = new StringBuilder();
        for (int i = 0; i < instanceOFCKey.length; i++) {
            if (instanceOFCKey[i] == null || instanceOFCKey[i] == 0) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(String.format("[id:%d] %X", i, instanceOFCKey[i]));
        }
        if (sb.length() > 0) {
            sb.insert(0, "Instance funcs: ");
        }
        for (int i = 0; i < callbackOFCKeys.size(); i++) {
            Integer key = callbackOFCKeys.get(i);
            if (key == null || key == 0) {
                continue;
            }
            if (sbCb.length() > 0) {
                sbCb.append(", ");
            }
            sbCb.append(String.format("[id:%d] %X", i, key));
        }
        if (sbCb.length() > 0) {
            sbCb.insert(0, "Callback funcs: ");
        }

        if (sbCb.length() > 0) {
            sb.append("; ").append(sbCb);
        }

        return "Variables: " + typeElems.size() + " elems" + (sb.length() > 0 ? " | " : "") + sb.toString();
    }

}
