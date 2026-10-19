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
public class ScriptingEngineCategoryFunctions {

    private static final int[] FUNCTION_ORDER = {
        0x1,
        0x2,
        0x3,
        0x4,
        0x5,
        0x6,
        0x7,
        0x8,
        0x14,
        0x15,
        0x16,
        0x17,
        0x18,
        0x19,
        0x1a,
        0x1f
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
