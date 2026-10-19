/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;
import Common.WriterUtil;

/**
 *
 * @author andras
 */
public abstract class BFChunkParseClass extends Logger implements BFChunkParseInterface {

    protected BFDataFileClass bfdfc;
    protected Boolean insideBinFile;
    protected ReaderUtil ru;
    protected WriterUtil wu;
    protected Integer selfKey = null;
    protected Boolean hasBeenParsed = false;

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        this.insideBinFile = insideBinFile;
        this.bfdfc = bfdfc;
        this.ru = this.bfdfc.getReaderUtil();
        this.wu = this.bfdfc.getWriterUtil();
        if (buildFromParsed && !getHasBeenParsed()) {
            return false;
        }
        return true;
    }

    public Boolean getHasBeenParsed() {
        return hasBeenParsed;
    }

    public void setHasBeenParsed(Boolean hasBeenParsed) {
        this.hasBeenParsed = hasBeenParsed;
    }

    public Long initParse() {
        Long len;
        if (insideBinFile) {
            Integer iLen = ru.readInt(false);
            if (iLen == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    log("Length read fail.");
                }
                return null;
            }
            len = (long) iLen;
        } else {
            len = ru.getDataLength();
        }

        if (!IrisToolDefines.PUBLIC_RELEASE) {
            log("Chunk length: " + len);
        }
        return len;
    }

    @Override
    public void setSelfKey(Integer selfKey) {
        this.selfKey = selfKey;
    }

    @Override
    public Integer getSelfKey() {
        return selfKey;
    }

    /*public BFChunkParseClass() {
        if (this.bfdfc == null) {
            return;
        }
        this.ru = bfdfc.getReaderUtil();
    }*/
    @Override
    public BFChunkParseInterface duplicate() {
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            System.out.println("TODO: " + this.getClass().getName() + " duplicate() unimplemented.");
        }
        return null;
    }
}
