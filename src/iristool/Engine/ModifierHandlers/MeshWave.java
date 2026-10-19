/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine.ModifierHandlers;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierMeshWave;
import Common.DataType.Vector3;
import iristool.Engine.RenderUtils;

/**
 *
 * @author andra
 */
public class MeshWave {

    public static void handleMeshWave(BFModifierMeshWave mw, BFGaoChunk gao, Vector3 possTemp[], int possLen, float elapsedTime) {
        int flags;
        int v11;
        float v12;
        float v13;
        int v14;
        float v15;
        float v16;
        int v17;
        float v18;
        float v19;
        int v20;
        float v21;
        float v22;
        int v23;
        float v24;
        float v25;
        int v26;
        float v27;
        float v28;
        int v29;
        float v30;
        float v31;
        int v32;
        float v33;
        float v34;
        int v35;
        float v36;
        float v37;
        int v38;
        float v39;
        float v40;
        int v41;
        float v42;
        float v43;
        int v44;
        float v45;
        float v46;
        float v51;
        float v52;
        float v53;
        float v54;
        float v55;
        float v56;
        float v57;
        float v58;
        float v59;
        float v60;
        double v61;
        double v62;
        double v63;
        float v64;
        float v65;
        float v66;
        float v67;
        float v68;
        float v69;
        float v70;
        float v71;
        float v72;
        float v73;
        float v74;
        float v75;
        float sq;
        float v84;
        int j, k;

        flags = mw.getFlags();
        if ((flags & 0x10) != 0 && gao.getPrimaryVertexColorArr() != null) {
            int vertexColorNum = gao.getPrimaryVertexColorArr().length;
            k = 0; // itt azért nem 1 van hanem 0 mint a játékban, mert az RLI első elemee ott a ohssz, itt meg nem.
            if ((flags & 0x20) != 0) {
                if ((flags & 1) != 0) {
                    if ((flags & 8) != 0) {
                        if (possLen > 0) {
                            j = 0;
                            do {
                                v11 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                                v64 = (1.0f - v11 * 0.00390625f) * mw.getIntensity();
                                v12 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                                possTemp[j].setX(RenderUtils.getDecalByFloat(v12) * v64 + possTemp[j].getX());
                                v13 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                                possTemp[j].setX(RenderUtils.getDecalByFloat(v13) * v64 + possTemp[j].getX());
                                ++j;
                                ++k;
                            } while (j < possLen);
                        }
                    } else if (possLen > 0) {
                        j = 0;
                        do {
                            v14 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                            v65 = (1.0f - v14 * 0.00390625f) * mw.getIntensity();
                            v15 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                            possTemp[j].setY(RenderUtils.getDecalByFloat(v15) * v65 + possTemp[j].getY());
                            v16 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                            possTemp[j].setZ(RenderUtils.getDecalByFloat(v16) * v65 + possTemp[j].getZ());
                            ++j;
                            ++k;
                        } while (j < possLen);
                    }
                } else if ((flags & 2) != 0) {
                    if ((flags & 8) != 0) {
                        if (possLen > 0) {
                            j = 0;
                            v17 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                            do {
                                v66 = (1.0f - v17 * 0.00390625f) * mw.getIntensity();
                                v18 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                                possTemp[j].setY(RenderUtils.getDecalByFloat(v18) * v66 + possTemp[j].getY());
                                v19 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                                possTemp[j].setY(RenderUtils.getDecalByFloat(v19) * v66 + possTemp[j].getY());
                                ++j;
                                ++k;
                            } while (j < possLen);
                        }
                    } else if (possLen > 0) {
                        j = 0;
                        v20 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                        do {
                            v67 = (1.0f - v20 * 0.00390625f) * mw.getIntensity();
                            v21 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                            possTemp[j].setX(RenderUtils.getDecalByFloat(v21) * v67 + possTemp[j].getX());
                            v22 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                            possTemp[j].setZ(RenderUtils.getDecalByFloatOffsettedBy256(v22) * v67 + possTemp[j].getZ());
                            ++j;
                            ++k;
                        } while (j < possLen);
                    }
                } else if ((flags & 4) != 0) {
                    if ((flags & 8) != 0) {
                        if (possLen > 0) {
                            j = 0;
                            v23 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                            do {
                                v68 = (1.0f - v23 * 0.00390625f) * mw.getIntensity();
                                v24 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                                possTemp[j].setZ(RenderUtils.getDecalByFloat(v24) * v68 + possTemp[j].getZ());
                                v25 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                                possTemp[j].setZ(RenderUtils.getDecalByFloat(v25) * v68 + possTemp[j].getZ());
                                ++j;
                                ++k;
                            } while (j < possLen);
                        }
                    } else if (possLen > 0) {
                        j = 0;
                        v26 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                        do {
                            v69 = (1.0f - v26 * 0.00390625f) * mw.getIntensity();
                            v27 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                            possTemp[j].setX(RenderUtils.getDecalByFloat(v27) * v69 + possTemp[j].getX());
                            v28 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                            possTemp[j].setY(RenderUtils.getDecalByFloatOffsettedBy256(v28) * v69 + possTemp[j].getY());
                            ++j;
                            ++k;
                        } while (j < possLen);
                    }
                }
            } else if ((flags & 1) != 0) {
                if ((flags & 8) != 0) {
                    if (possLen > 0) {
                        j = 0;
                        v29 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                        do {
                            v70 = v29 * mw.getIntensity() * 0.00390625f;
                            v30 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                            possTemp[j].setX(RenderUtils.getDecalByFloat(v30) * v70 + possTemp[j].getX());
                            v31 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                            possTemp[j].setX(RenderUtils.getDecalByFloat(v31) * v70 + possTemp[j].getX());
                            ++j;
                            ++k;
                        } while (j < possLen);
                    }
                } else if (possLen > 0) {
                    j = 0;
                    v32 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                    do {
                        v71 = v32 * mw.getIntensity() * 0.00390625f;
                        v33 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                        possTemp[j].setY(RenderUtils.getDecalByFloat(v33) * v71 + possTemp[j].getY());
                        v34 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();

                        possTemp[j].setZ(RenderUtils.getDecalByFloatOffsettedBy256(v34) * v71 + possTemp[j].getZ());
                        ++j;
                        ++k;
                    } while (j < possLen);
                }
            } else if ((flags & 2) != 0) {
                if ((flags & 8) != 0) {
                    if (possLen > 0) {
                        j = 0;
                        v35 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                        do {
                            v72 = v35 * mw.getIntensity() * 0.00390625f;
                            v36 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                            possTemp[j].setY(RenderUtils.getDecalByFloat(v36) * v72 + possTemp[j].getY());
                            v37 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                            possTemp[j].setY(RenderUtils.getDecalByFloat(v37) * v72 + possTemp[j].getY());
                            ++j;
                            ++k;
                        } while (j < possLen);
                    }
                } else if (possLen > 0) {
                    j = 0;
                    v38 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                    do {
                        v73 = v38 * mw.getIntensity() * 0.00390625f;
                        v39 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                        possTemp[j].setX(RenderUtils.getDecalByFloat(v39) * v73 + possTemp[j].getX());
                        v40 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();

                        possTemp[j].setZ(RenderUtils.getDecalByFloatOffsettedBy256(v40) * v73 + possTemp[j].getZ());
                        ++j;
                        ++k;
                    } while (j < possLen);
                }
            } else if ((flags & 4) != 0) {
                if ((flags & 8) != 0) {
                    if (possLen > 0) {
                        j = 0;
                        v41 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                        do {
                            v74 = v41 * mw.getIntensity() * 0.00390625f;
                            v42 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                            possTemp[j].setZ(RenderUtils.getDecalByFloat(v42) * v74 + possTemp[j].getZ());
                            v43 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                            possTemp[j].setZ(RenderUtils.getDecalByFloat(v43) * v74 + possTemp[j].getZ());
                            ++j;
                            ++k;
                        } while (j < possLen);
                    }
                } else if (possLen > 0) {
                    j = 0;
                    v44 = ((k >= vertexColorNum) ? 0 : ((gao.getPrimaryVertexColorArr()[k] >> 24) & 0xff));
                    do {
                        v75 = v44 * mw.getIntensity() * 0.00390625f;
                        v45 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                        possTemp[j].setX(RenderUtils.getDecalByFloat(v45) * v75 + possTemp[j].getX());
                        v46 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                        possTemp[j].setY(RenderUtils.getDecalByFloatOffsettedBy256(v46) * v75 + possTemp[j].getY());
                        ++j;
                        ++k;
                    } while (j < possLen);
                }
            }
        } else if ((flags & 1) != 0) {
            if ((flags & 8) != 0) {
                for (j = 0; j < possLen; j++) {
                    v61 = (possTemp[j].getY() * mw.getSpeed() + mw.getOffset()) * 162.975 + RenderUtils.getUnknownMagic();
                    possTemp[j].setX(RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v61) & 0x3ff)] * mw.getIntensity() + possTemp[j].getX());
                    v62 = (possTemp[j].getZ() * mw.getSpeed() + mw.getOffset()) * 162.975 + RenderUtils.getUnknownMagic();
                    possTemp[j].setX(RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v62) & 0x3ff)] * mw.getIntensity() + possTemp[j].getX());
                }
            } else {
                for (j = 0; j < possLen; j++) {
                    v63 = (mw.getSpeed() * possTemp[j].getX() + mw.getOffset()) * 162.975 + RenderUtils.getUnknownMagic();
                    possTemp[j].setY(RenderUtils.getDecalTable()[(int) (Double.doubleToLongBits(v63) & 0x3ff)] * mw.getIntensity() + possTemp[j].getY());
                    v51 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                    possTemp[j].setZ(RenderUtils.getDecalByFloatOffsettedBy256(v51) * mw.getIntensity() + possTemp[j].getZ());
                }
            }
        } else if ((flags & 2) != 0) {
            if ((flags & 8) != 0) {
                for (j = 0; j < possLen; j++) {
                    v52 = mw.getSpeed() * possTemp[j].getX() + mw.getOffset();
                    possTemp[j].setY(RenderUtils.getDecalByFloat(v52) * mw.getIntensity() + possTemp[j].getY());
                    v53 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                    possTemp[j].setY(RenderUtils.getDecalByFloat(v53) * mw.getIntensity() + possTemp[j].getY());
                }
            } else {
                for (j = 0; j < possLen; j++) {
                    v54 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                    possTemp[j].setX(RenderUtils.getDecalByFloat(v54) * mw.getIntensity() + possTemp[j].getX());
                    v55 = possTemp[j].getY() * mw.getSpeed() + mw.getOffset();
                    possTemp[j].setZ(RenderUtils.getDecalByFloatOffsettedBy256(v55) * mw.getIntensity() + possTemp[j].getZ());
                }
            }
        } else if ((flags & 4) != 0) {
            if ((flags & 8) != 0) {
                v56 = RenderUtils.getDecalByFloat(mw.getOffset());
                sq = v56 * mw.getIntensity() + v56 * mw.getIntensity();
                for (j = 0; j < possLen; j++) {
                    v57 = possTemp[j].getY() * mw.getSpeed();
                    v84 = RenderUtils.getDecalByFloat(v57);
                    v58 = mw.getSpeed() * possTemp[j].getX();
                    possTemp[j].setZ((RenderUtils.getDecalByFloat(v58) + v84) * sq + possTemp[j].getZ());
                }
            } else {
                for (j = 0; j < possLen; j++) {
                    v59 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                    possTemp[j].setX(RenderUtils.getDecalByFloat(v59) * mw.getIntensity() + possTemp[j].getX());
                    v60 = possTemp[j].getZ() * mw.getSpeed() + mw.getOffset();
                    possTemp[j].setY(RenderUtils.getDecalByFloatOffsettedBy256(v60) * mw.getIntensity() + possTemp[j].getY());
                }
            }
        }
        mw.setOffset(elapsedTime * mw.getTimeMultiplier() * 10.0f + mw.getOffset());
    }
}
