/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @author andras
 */
public class IrisToolDefines {

    public static final boolean PUBLIC_RELEASE = false;
    private static int languageID = 0;
    private static int mainBINFileID = 0xff0084df; // TODO: ezt dinamikusan (de ehhez kell OFC parser)
    private static int textureType7ReadOrder[] = {3, 0, -1, -1}; // ezt használja a PC-s verzió a 7-es textúrablokk olvasási sorrendjének megállapításához
    private static Boolean preferPalettelessTextures = false;
    private static File logFile = null;
    private static FileOutputStream logFos = null;
    private static BufferedOutputStream logBos = null;
    private static boolean loggingEnabled = false;
    private static boolean readFileNameListAndDirectoryList = true;
    private static boolean useTexturesFromBin = true;
    private static boolean consoleVersionTextureLoad = true;
    private static boolean hdVersionQuirk = false;
    private static final String VERSION = "0.26.3";

    public static final String getIrisToolVersion() {
        return VERSION;
    }

    public static boolean isUseTexturesFromBin() {
        return useTexturesFromBin;
    }

    public static void setUseTexturesFromBin(boolean useTexturesFromBin) {
        IrisToolDefines.useTexturesFromBin = useTexturesFromBin;
    }
   
    public static boolean isReadFileNameListAndDirectoryList() {
        return readFileNameListAndDirectoryList;
    }

    public static void setReadFileNameListAndDirectoryList(boolean readFileNameListAndDirectoryList) {
        IrisToolDefines.readFileNameListAndDirectoryList = readFileNameListAndDirectoryList;
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public static void setLoggingEnabled(boolean loggingEnabled) {
        IrisToolDefines.loggingEnabled = loggingEnabled;
    }

    public static BufferedOutputStream getLogBos() {
        return logBos;
    }

    public static File getLogFile() {
        return logFile;
    }

    public static Boolean setLogFile(File logFile) {
        IrisToolDefines.logFile = logFile;
        Boolean ret = true;
        try {
            logFos = new FileOutputStream(logFile);
            logBos = new BufferedOutputStream(logFos);
        } catch (Exception e) {
            if (logFos != null) {
                try {
                    logFos.close();
                } catch (Exception ee) {
                }
            }
            logFos = null;
            logBos = null;
            IrisToolDefines.logFile = null;
            ret = false;
        }
        return ret;
    }

    public static Boolean hasLogFile() {
        return logBos != null;
    }

    public static void closeLogFile() {
        if (logBos != null) {
            try {
                logBos.close();
            } catch (Exception ee) {
            }
        }
        logBos = null;
        if (logFos != null) {
            try {
                logFos.close();
            } catch (Exception ee) {
            }
        }
        logFos = null;
    }

    public static int getMainBINFileID() {
        return mainBINFileID;
    }

    public static void setMainBINFileID(int mainBINFileID) {
        IrisToolDefines.mainBINFileID = mainBINFileID;
    }

    public static int getLanguageID() {
        return languageID;
    }

    public static void setLanguageID(int languageID) {
        IrisToolDefines.languageID = languageID;
    }

    public static int[] getTextureType7ReadOrder() {
        return textureType7ReadOrder;
    }

    public static Boolean getPreferPalettelessTextures() {
        return preferPalettelessTextures;
    }

    public static boolean isConsoleVersionTextureLoad() {
        return consoleVersionTextureLoad;
    }

    public static void setConsoleVersionTextureLoad(boolean consoleVersionTextureLoad) {
        IrisToolDefines.consoleVersionTextureLoad = consoleVersionTextureLoad;
    }       

    public static void setPreferPalettelessTextures(Boolean preferPalettelessTextures) {
        IrisToolDefines.preferPalettelessTextures = preferPalettelessTextures;
    }        
  
    public static boolean isHdVersionQuirk() {
        return hdVersionQuirk;
    }

    public static void setHdVersionQuirk(boolean hdVersionQuirk) {
        IrisToolDefines.hdVersionQuirk = hdVersionQuirk;
    }
    
}
