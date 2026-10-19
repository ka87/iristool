/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserParticleGenerator;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFModifierBaseClass;
import Common.IrisToolDefines;

/**
 *
 * @author andras
 */
public class BFModifierParticleGenerator extends BFModifierBaseClass implements BFChunkParseInterface {

    private GAOInnerParserParticleGenerator pag;

    public BFModifierParticleGenerator() {
        super();
    }

    @Override
    public boolean parse(BFDataFileClass bfdfc, Boolean insideBinFile, Boolean buildFromParsed) {
        if (!super.parse(bfdfc, insideBinFile, buildFromParsed)) {
            return false;
        }
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            logF("Parsing Particle Generator Modifier.");
        }

        pag = new GAOInnerParserParticleGenerator();

        return pag.parsePAG(bfdfc, insideBinFile, buildFromParsed);
    }

    public GAOInnerParserParticleGenerator getPag() {
        return pag;
    }

    @Override
    public String toString() {
        return "BFModifierParticleGenerator: " + (pag != null ? pag.toString() : "");
    }

}
