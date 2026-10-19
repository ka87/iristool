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
public class ScriptingEngineFieldFunctions {

    private static final int[] FUNCTION_ORDER = {
        0x200,
        0x201,
        0x202,
        0x203,
        0x204,
        0x205,
        0x206,
        0x207,
        0x208,
        0x209,
        0x20A,
        0x20B,
        0x226,
        0x227,
        0x228,
        0x229,
        0x22A,
        0x22B,
        0x22C,
        0x22D,
        0x22E,
        0x22F,
        0x230,
        0x231,
        0x232,
        0x233,
        0x234
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
