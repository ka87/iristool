/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib;

/**
 *
 * @author andras
 */
public interface BFChunkParseInterface {
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed);
    public Integer getSelfKey();
    public void setSelfKey(Integer selfKey);
    public BFChunkParseInterface duplicate();
}
