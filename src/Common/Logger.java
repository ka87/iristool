/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.nio.charset.Charset;

/**
 *
 * @author andras
 */
public abstract class Logger {

    private int stackPlus = 0;

    public void logF(String format, Object... args) {
        stackPlus = 1;
        log(false, String.format(format, args));
    }

    public void log(Object o) {
        stackPlus = 1;
        log(false, o);
    }
    
    public void log(boolean simple, Object o) {
        if (!IrisToolDefines.isLoggingEnabled()) return;
        
        String className = getClass().getName();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName;

        methodName = stackTraceElements[2 + stackPlus].getMethodName();
        stackPlus = 0;
        
        String logText = (simple ? "" : (className + "::" + methodName + "(): ")) + o + "\n";

        if (IrisToolDefines.hasLogFile()) {
            try {
                IrisToolDefines.getLogBos().write(logText.getBytes(Charset.forName("UTF-8")));
            } catch (Exception e) {
            }
        } else {
            System.out.print(logText);
        }
        logText = null;
    }

    public void simpleLog(Object o) {
        stackPlus = 1;
        log(true, o);
    }
    
    public void simpleLogF(String format, Object... args) {
        stackPlus = 1;
        log(true, String.format(format, args));
    }
    
}
