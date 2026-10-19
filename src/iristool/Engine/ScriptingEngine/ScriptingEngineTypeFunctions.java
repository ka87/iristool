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
public class ScriptingEngineTypeFunctions {

    private static final int[] FUNCTION_ORDER = {
        0x20,
        0x21,
        0x22,
        0x25,
        0x26,
        0x27,
        0x28,
        0x29,
        0x2A,
        0x2B,
        0x2C,
        0x2D,
        0x2E,
        0x7C,
        0x7D,
        0x7E,
        0x7F,
        0x80,
        0x81,
        0x82,
        0x83,
        0x84,
        0x85
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
