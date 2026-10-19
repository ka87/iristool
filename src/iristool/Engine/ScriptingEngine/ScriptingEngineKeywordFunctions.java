/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ScriptingEngine;

/**
 *
 * @author andra
 */
public class ScriptingEngineKeywordFunctions {

    private static final int[] FUNCTION_ORDER = {
        0x800,
        0x801,
        0x802,
        0x803,
        0x804,
        0x805,
        0x806,
        0x807,
        0x808,
        0x809,
        0x834,
        0x835,
        0x836,
        0x837,
        0x838,
        0x839,
        0x83A,
        0x83B,
        0x898,
        0x899,
        0x89A,
        0x89B,
        0x89C,
        0x89D,
        0x89E,
        0x89F,
        0x8A0,
        0x8A1,
        0x8A2,
        0x8A3,
        0x8A4,
        0x8A5,
        0x8A6,
        0x8A7,
        0x8A8,
        0x8A9,
        0x8AA,
        0x8AB,
        0x8AC,
        0x8AD,
        0x8AE,
        0x8AF,
        0x8B0,
        0x8B1,
        0x8B2,
        0x8B3,
        0x8B4,
        0x8B5,
        0x8B6,
        0x8B7,
        0x8B8,
        0x8FC,
        0x8FD,
        0x8FE,
        0x900,
        0x901,
        0x902,
        0x903,
        0x960,
        0x961,
        0x962,
        0x963,
        0x964,
        0x965,
        0x966,
        0x967,
        0x968,
        0x969,
        0x96A,
        0x96B,
        0x96C,
        0x9C4,
        0x9F6,
        0x9F7
    };

    public static int getNumberOfFunctions() {
        return FUNCTION_ORDER.length;
    }

    public static int getMaxFunctionID() {
        if (FUNCTION_ORDER == null || FUNCTION_ORDER.length <= 0) {
            return 0;
        }
        int max = FUNCTION_ORDER[0];
        for (int i = 0; i < FUNCTION_ORDER.length; i++) {
            if (max < FUNCTION_ORDER[i]) {
                max = FUNCTION_ORDER[i];
            }
        }
        return max;
    }

    public static int[] getFunctionIDs() {
        return FUNCTION_ORDER;
    }
}
