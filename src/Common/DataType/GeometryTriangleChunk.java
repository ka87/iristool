/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.IrisToolDefines;
import Common.Logger;
import Common.ReaderUtil;

/**
 *
 * @author andras
 */
public class GeometryTriangleChunk extends Logger {

    private Integer triangleNum;
    private Integer materialNum;
    private Integer unkNum1;
    private Integer unkNum2;
    // ONLY FOR STORING GeometryTrieangleData
    private GeometryTriangleData geoTriangleData[];
    private int geoTriangleDataIdx = 0;
    private GeometryTriangleChunkUnkData1 triangleChunkUnkData1;
    private Short triangleChunkUnkData2[];
    private int geoTriangleChunkUnkData2Idx = 0;
    private GeometryWholeTriangleStripBlock triangleChunkUnkData3;
    // ONLY FOR IRISTOOL
    private boolean renderingEnabled = true;

    public boolean isRenderingEnabled() {
        return renderingEnabled;
    }

    public void setRenderingEnabled(boolean renderingEnabled) {
        this.renderingEnabled = renderingEnabled;
    }

    public void addGeometryTriangleData(GeometryTriangleData geotd) {
        if (geoTriangleData == null || geoTriangleDataIdx == geoTriangleData.length) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("GeometryTriangleData add failed.");
            }
            return;
        }
        geoTriangleData[geoTriangleDataIdx++] = geotd;
    }

    public void addGeometryChunkUnkData2(Short ud2) {
        if (triangleChunkUnkData2 == null || geoTriangleChunkUnkData2Idx == triangleChunkUnkData2.length) {
            if (!IrisToolDefines.PUBLIC_RELEASE) {
                log("triangleChunkUnkData2 add failed.");
            }
            return;
        }
        triangleChunkUnkData2[geoTriangleChunkUnkData2Idx++] = ud2;
    }

    public Short[] getTriangleChunkUnkData2() {
        return triangleChunkUnkData2;
    }

    public GeometryWholeTriangleStripBlock getTriangleChunkUnkData3() {
        return triangleChunkUnkData3;
    }

    public void setTriangleChunkUnkData3(GeometryWholeTriangleStripBlock triangleChunkUnkData3) {
        this.triangleChunkUnkData3 = triangleChunkUnkData3;
    }

    public GeometryTriangleData[] getGeoTriangleData() {
        return geoTriangleData;
    }

    public int getGeoTriangleDataCount() {
        return geoTriangleDataIdx;
    }

    public Integer getTriangleNum() {
        return triangleNum;
    }

    public void setTriangleNum(Integer triangleNum) {
        this.triangleNum = triangleNum;
        this.geoTriangleData = new GeometryTriangleData[triangleNum];
        this.geoTriangleDataIdx = 0;
    }

    public Integer getMaterialNum() {
        return materialNum;
    }

    public void setMaterialNum(Integer materialNum) {
        this.materialNum = materialNum;
    }

    public Integer getUnkNum1() {
        return unkNum1;
    }

    public void setUnkNum1(Integer unkNum1) {
        this.unkNum1 = unkNum1;
    }

    public Integer getUnkNum2() {
        return unkNum2;
    }

    public void setUnkNum2(Integer unkNum2) {
        this.unkNum2 = unkNum2;
        this.triangleChunkUnkData2 = new Short[unkNum2];
    }

    public GeometryTriangleChunkUnkData1 getTriangleChunkUnkData1() {
        return triangleChunkUnkData1;
    }

    public GeometryTriangleChunk(Integer triangleNum, Integer materialNum, Integer unkNum1, Integer unkNum2) {
        this.triangleNum = triangleNum;
        this.materialNum = materialNum;
        this.unkNum1 = unkNum1;
        this.unkNum2 = unkNum2;
        this.geoTriangleData = new GeometryTriangleData[triangleNum];
        this.triangleChunkUnkData1 = new GeometryTriangleChunkUnkData1();
        this.triangleChunkUnkData2 = new Short[unkNum2];
        this.geoTriangleDataIdx = 0;
        this.geoTriangleChunkUnkData2Idx = 0;
    }

    public GeometryTriangleChunk() {
    }

    public boolean readValues(ReaderUtil ru) {
        this.triangleNum = ru.readInt(false);
        this.materialNum = ru.readInt(false);
        this.unkNum1 = ru.readInt(false);
        this.unkNum2 = ru.readInt(false);

        //THIS IS ONLY FOR FUTURE STORING OF VALUES
        this.geoTriangleData = new GeometryTriangleData[triangleNum];
        this.geoTriangleDataIdx = 0;
        this.triangleChunkUnkData1 = new GeometryTriangleChunkUnkData1();
        this.triangleChunkUnkData2 = new Short[unkNum2];
        this.geoTriangleChunkUnkData2Idx = 0;

        return (this.triangleNum != null && this.materialNum != null
                && this.unkNum1 != null && this.unkNum2 != null);
    }

    @Override
    public String toString() {
        return "GeometryTriangleChunk: triangleNum: " + triangleNum + ", materialNum: " + materialNum + ", unkNum1:" + unkNum1 + ", unkNum2: " + unkNum2;
    }
}
