package iristool.Engine;

import BFDataFileLib.BFChunkParsers.Map.BFCollisionInstanceChunk;
import BFDataFileLib.BFChunkParsers.Map.BFCollisionInstanceCollisionSetChunk;
import BFDataFileLib.BFChunkParsers.Map.BFCollisionMapGroupChunk;
import BFDataFileLib.BFChunkParsers.Map.BFCollisionObjectChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.CollisionInstanceElem;
import Common.DataType.CollisionTriangleChunk;
import Common.DataType.CollisionTriangleChunkElem;
import Common.DataType.GeometryTriangleChunk;
import Common.DataType.GeometryTriangleData;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.FloatUtil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andra
 */
public class DebugRenderFunctions {

    private static Vector3 boundingBoxMin = new Vector3();
    private static Vector3 boundingBoxMax = new Vector3();
    private static Matrix4x4 invGlobalForBoundingBox = new Matrix4x4();
    private static Vector3 tempVecForBoundingBox = new Vector3();

    public static void drawACylinder(Renderer r, GL4 gl4, Vector3 pos, float radius, float length, int slices, Matrix4x4 globalMatrix, int color, boolean filled) {
        float tmp[] = new float[16];
        float halfLength = length * 0.5f;
        float a = 0.0f;
        float step = (float) ((Math.PI * 2) / (float) slices);

        Vector3 poss1[] = new Vector3[(slices + 1) * 6];

        int idx = 0;

        // body
        for (int i = 0; i < slices + 1; ++i) {
            float x = (float) Math.cos(a) * radius;
            float y = (float) Math.sin(a) * radius;
            poss1[idx++] = new Vector3(x + pos.getX(), y + pos.getY(), -halfLength + pos.getZ());
            poss1[idx++] = new Vector3(x + pos.getX(), y + pos.getY(), halfLength + pos.getZ());

            a += step;
        }

        // top
        for (int i = 0; i < slices + 1; ++i) {
            float x = (float) Math.cos(a) * radius;
            float y = (float) Math.sin(a) * radius;
            poss1[idx++] = new Vector3(x + pos.getX(), y + pos.getY(), halfLength + pos.getZ());
            poss1[idx++] = new Vector3(0.0f + pos.getX(), 0.0f + pos.getY(), halfLength + pos.getZ());

            a += step;
        }

        // bottom
        for (int i = 0; i < slices + 1; ++i) {
            float x = (float) Math.cos(a) * radius;
            float y = (float) Math.sin(a) * radius;
            poss1[idx++] = new Vector3(x + pos.getX(), y + pos.getY(), -halfLength + pos.getZ());
            poss1[idx++] = new Vector3(0.0f + pos.getX(), 0.0f + pos.getY(), -halfLength + pos.getZ());

            a += step;
        }

        Vector3 poss[] = RenderUtils.convertTriangleStripToTringleList(poss1);

        int i, j;
        Vector3 normalsTmp[] = new Vector3[poss.length];
        for (i = 0; i < poss.length; i++) {
            normalsTmp[i] = new Vector3();
        }
        int vertexColors[] = new int[poss.length];
        for (i = 0; i < poss.length; i++) {
            vertexColors[i] = color;
        }
        int normalsNumTmp[] = new int[poss.length];
        for (i = 0; i < poss.length; i++) {
            normalsNumTmp[i] = 0;
        }
        GeometryTriangleChunk geotc[] = new GeometryTriangleChunk[1];
        geotc[0] = new GeometryTriangleChunk(poss.length / 3, 0, 0, 0);

        j = 0;
        GeometryTriangleData geotd;
        for (i = 0; i < poss.length / 3; i++) {
            geotd = new GeometryTriangleData((short) j++, (short) j++, (short) j++, (short) 0, (short) 0, (short) 0, 0, 0);
            geotc[0].addGeometryTriangleData(geotd);
        }

        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (!filled ? GL4.GL_LINE : GL4.GL_FILL)); // override global filled/wireframe default

        // Matrices set
        System.arraycopy(globalMatrix.getAsFloatArray(true, true), 0, tmp, 0, tmp.length);
        FloatUtil.multMatrix(r.getRotationMatrix(), tmp, tmp);
        System.arraycopy(tmp, 0, r.getOglf().getModelViewMtx(), 0, r.getOglf().getModelViewMtx().length);
        FloatUtil.multMatrix(r.getOglf().getViewMtx(), r.getOglf().getModelViewMtx(), r.getOglf().getModelViewMtx());

        Matrix4x4 view = new Matrix4x4(r.getOglf().getViewMtx());
        Matrix4x4.fixMatrix(view);
        Matrix4x4 rot = new Matrix4x4(r.getRotationMatrix());
        Matrix4x4.fixMatrix(rot);
        Lights.setMatrices(globalMatrix, view, rot);
        // --

        r.getOglf().drawMultiMaterialTriangles(gl4, false, geotc,
                poss, null, normalsTmp, normalsNumTmp, vertexColors,
                null, 1, globalMatrix.getAsFloatArray(true, true), false, null, r.getElapsedTimeInSecs());
        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (r.isWireFrame() ? GL4.GL_LINE : GL4.GL_FILL)); // set back to default

    }

    public static void drawASphere(Renderer r, GL4 gl4, Vector3 pos, float radius, int divO, int divA, Matrix4x4 globalMatrix, int color, boolean filled) {
        float tmp[] = new float[16];
        double theta = 2 * Math.PI;
        double phi = Math.PI;
        double x, y, z;

        Vector3 poss1[] = new Vector3[divO * divA * 2];
        int idx = 0;

        for (double i = 0.0; i < phi; i += phi / divO) {
            for (double j = 0.0; j < theta; j += theta / divA) {
                x = radius * Math.cos(j) * Math.sin(i);
                y = radius * Math.sin(j) * Math.sin(i);
                z = radius * Math.cos(i);

                poss1[idx++] = new Vector3((float) x + pos.getX(), (float) y + pos.getY(), (float) z + pos.getZ());

                x = radius * Math.cos(j) * Math.sin(i + Math.PI / divO);
                y = radius * Math.sin(j) * Math.sin(i + Math.PI / divO);
                z = radius * Math.cos(i + Math.PI / divO);

                poss1[idx++] = new Vector3((float) x + pos.getX(), (float) y + pos.getY(), (float) z + pos.getZ());
            }
        }

        Vector3 poss[] = RenderUtils.convertTriangleStripToTringleList(poss1);

        int i, j;
        Vector3 normalsTmp[] = new Vector3[poss.length];
        for (i = 0; i < poss.length; i++) {
            normalsTmp[i] = new Vector3();
        }
        int vertexColors[] = new int[poss.length];
        for (i = 0; i < poss.length; i++) {
            vertexColors[i] = color;
        }
        int normalsNumTmp[] = new int[poss.length];
        for (i = 0; i < poss.length; i++) {
            normalsNumTmp[i] = 0;
        }
        GeometryTriangleChunk geotc[] = new GeometryTriangleChunk[1];
        geotc[0] = new GeometryTriangleChunk(poss.length / 3, 0, 0, 0);

        j = 0;
        GeometryTriangleData geotd;
        for (i = 0; i < poss.length / 3; i++) {
            geotd = new GeometryTriangleData((short) j++, (short) j++, (short) j++, (short) 0, (short) 0, (short) 0, 0, 0);
            geotc[0].addGeometryTriangleData(geotd);
        }

        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (!filled ? GL4.GL_LINE : GL4.GL_FILL)); // override global filled/wireframe default

        // Matrices set
        System.arraycopy(globalMatrix.getAsFloatArray(true, true), 0, tmp, 0, tmp.length);
        FloatUtil.multMatrix(r.getRotationMatrix(), tmp, tmp);
        System.arraycopy(tmp, 0, r.getOglf().getModelViewMtx(), 0, r.getOglf().getModelViewMtx().length);
        FloatUtil.multMatrix(r.getOglf().getViewMtx(), r.getOglf().getModelViewMtx(), r.getOglf().getModelViewMtx());

        Matrix4x4 view = new Matrix4x4(r.getOglf().getViewMtx());
        Matrix4x4.fixMatrix(view);
        Matrix4x4 rot = new Matrix4x4(r.getRotationMatrix());
        Matrix4x4.fixMatrix(rot);
        Lights.setMatrices(globalMatrix, view, rot);
        // --

        r.getOglf().drawMultiMaterialTriangles(gl4, false, geotc,
                poss, null, normalsTmp, normalsNumTmp, vertexColors,
                null, 1, globalMatrix.getAsFloatArray(true, true), false, null, r.getElapsedTimeInSecs());
        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (r.isWireFrame() ? GL4.GL_LINE : GL4.GL_FILL)); // set back to default
    }

    public static void drawAABBox(Renderer r, GL4 gl4, Vector3 pos1, Vector3 pos2, Matrix4x4 globalMatrix, int color, boolean filled) {
        Vector3 pos = new Vector3((pos1.getX() + pos2.getX()) / 2.0f, (pos1.getY() + pos2.getY()) / 2.0f, (pos1.getZ() + pos2.getZ()) / 2.0f);
        Vector3 lens = new Vector3();
        Vector3.substractTwoVectors(lens, pos2, pos1);

        drawABox(r, gl4, globalMatrix, pos, lens.getX(), lens.getY(), lens.getZ(), color, filled);
    }

    public static void drawACube(Renderer r, GL4 gl4, Matrix4x4 globalMatrix, int color, boolean filled) {
        drawABox(r, gl4, globalMatrix, new Vector3(0.0f, 0.0f, 0.0f), 1.0f, 1.0f, 1.0f, color, filled);
    }

    public static void drawABox(Renderer r, GL4 gl4, Matrix4x4 globalMatrix, Vector3 pos, float width, float height, float depth, int color, boolean filled) {
        float tmp[] = new float[16];
        float halfWidth = width / 2.0f;
        float halfHeight = height / 2.0f;
        float halfDepth = depth / 2.0f;
        Vector3 poss[] = new Vector3[]{
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), -halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), -halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), -halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), -halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), -halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), -halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), -halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(-halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ()),
            new Vector3(halfWidth + pos.getX(), halfHeight + pos.getY(), halfDepth + pos.getZ())
        };
        int i;
        Vector3 normalsTmp[] = new Vector3[poss.length];
        for (i = 0; i < poss.length; i++) {
            normalsTmp[i] = new Vector3();
        }
        int vertexColors[] = new int[poss.length];
        for (i = 0; i < poss.length; i++) {
            vertexColors[i] = color;
        }
        int normalsNumTmp[] = new int[poss.length];
        for (i = 0; i < poss.length; i++) {
            normalsNumTmp[i] = 0;
        }
        GeometryTriangleChunk geotc[] = new GeometryTriangleChunk[1];
        geotc[0] = new GeometryTriangleChunk(poss.length / 3, 0, 0, 0);
        int j = 0;
        GeometryTriangleData geotd;
        for (i = 0; i < poss.length / 3; i++) {
            geotd = new GeometryTriangleData((short) j++, (short) j++, (short) j++, (short) 0, (short) 0, (short) 0, 0, 0);
            geotc[0].addGeometryTriangleData(geotd);
        }

        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (!filled ? GL4.GL_LINE : GL4.GL_FILL)); // override global filled/wireframe default

        // Matrices set
        System.arraycopy(globalMatrix.getAsFloatArray(true, true), 0, tmp, 0, tmp.length);
        FloatUtil.multMatrix(r.getRotationMatrix(), tmp, tmp);
        System.arraycopy(tmp, 0, r.getOglf().getModelViewMtx(), 0, r.getOglf().getModelViewMtx().length);
        FloatUtil.multMatrix(r.getOglf().getViewMtx(), r.getOglf().getModelViewMtx(), r.getOglf().getModelViewMtx());

        Matrix4x4 view = new Matrix4x4(r.getOglf().getViewMtx());
        Matrix4x4.fixMatrix(view);
        Matrix4x4 rot = new Matrix4x4(r.getRotationMatrix());
        Matrix4x4.fixMatrix(rot);
        Lights.setMatrices(globalMatrix, view, rot);
        // --

        r.getOglf().drawMultiMaterialTriangles(gl4, false, geotc,
                poss, null, normalsTmp, normalsNumTmp, vertexColors,
                null, 1, globalMatrix.getAsFloatArray(true, true), false, null, r.getElapsedTimeInSecs());
        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (r.isWireFrame() ? GL4.GL_LINE : GL4.GL_FILL)); // set back to default
    }

    public static void drawCollisionMapGeometry(Renderer r, GL4 gl4, BFCollisionObjectChunk cob, Matrix4x4 globalMatrix, int color, boolean filled) {
        int i, j;
        Vector3 normalsTmp[];
        int vertexColors[];
        int normalsNumTmp[];
        GeometryTriangleChunk geotc[];
        CollisionTriangleChunk ctc;
        CollisionTriangleChunkElem ctceArr[];
        float tmp[] = new float[16];
        if (cob == null || cob.getType() != 0x5 || cob.getCollisionTringleChunkNum() == 0) {
            return;
        }

        Vector3 posIn[] = cob.getCollisionVertices();
        if (posIn == null) {
            return;
        }

        Vector3 poss[] = new Vector3[posIn.length];
        Vector3 scale = new Vector3(1.0f, 1.0f, 1.0f);

        // Ubisoft related vertices fix, I don't know why are they doing this thing. Probably speed optimalization.
        // Otherwise broken with gaos with scaled globalmatrix (eg: dec_00_03_tapisetage.gao in ff0045a9)        
        Matrix4x4 globalMatrixToSelect = globalMatrix;

        /* We are not removing scale now
        if (!r.getUseGAOPosition()) {
            // this needs to be done, because vertices are scaled with original matrix, 
            //and we are using here identity matrix
            globalMatrixToSelect = r.getGlobalMatrixOrig();
        }*/
        if ((globalMatrixToSelect.getFlags() & 0x8) != 0) {
            Matrix4x4.getScale(scale, globalMatrixToSelect);
        }

        for (i = 0; i < poss.length; i++) {
            poss[i] = new Vector3();
            poss[i].set(posIn[i]);
            poss[i].setX(poss[i].getX() / scale.getX());
            poss[i].setY(poss[i].getY() / scale.getY());
            poss[i].setZ(poss[i].getZ() / scale.getZ());
        }
        // end

        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (!filled ? GL4.GL_LINE : GL4.GL_FILL)); // override global filled/wireframe default

        // Matrices set
        System.arraycopy(globalMatrix.getAsFloatArray(true, true), 0, tmp, 0, tmp.length);
        FloatUtil.multMatrix(r.getRotationMatrix(), tmp, tmp);
        System.arraycopy(tmp, 0, r.getOglf().getModelViewMtx(), 0, r.getOglf().getModelViewMtx().length);
        FloatUtil.multMatrix(r.getOglf().getViewMtx(), r.getOglf().getModelViewMtx(), r.getOglf().getModelViewMtx());

        Matrix4x4 view = new Matrix4x4(r.getOglf().getViewMtx());
        Matrix4x4.fixMatrix(view);
        Matrix4x4 rot = new Matrix4x4(r.getRotationMatrix());
        Matrix4x4.fixMatrix(rot);
        Lights.setMatrices(globalMatrix, view, rot);
        // --

        geotc = new GeometryTriangleChunk[cob.getCollisionTringleChunkNum()];

        normalsTmp = new Vector3[poss.length];
        for (j = 0; j < poss.length; j++) {
            normalsTmp[j] = new Vector3();
        }
        vertexColors = new int[poss.length];
        for (j = 0; j < poss.length; j++) {
            vertexColors[j] = color;
        }
        normalsNumTmp = new int[poss.length];
        for (j = 0; j < poss.length; j++) {
            normalsNumTmp[j] = 0;
        }

        for (i = 0; i < cob.getCollisionTringleChunkNum(); i++) {

            if ((ctc = cob.getCtc()[i]) == null) {
                continue;
            }

            geotc[i] = new GeometryTriangleChunk((int) ctc.getTriangleElemNum(), 0, 0, 0);

            GeometryTriangleData geotd;
            ctceArr = ctc.getCtce();
            for (j = 0; j < ctc.getTriangleElemNum(); j++) {
                geotd = new GeometryTriangleData((short) ctceArr[j].getVertexID1(), (short) ctceArr[j].getVertexID2(), (short) ctceArr[j].getVertexID3(), (short) 0, (short) 0, (short) 0, 0, 0);
                geotc[i].addGeometryTriangleData(geotd);
            }

        }

        r.getOglf().drawMultiMaterialTriangles(gl4, false, geotc,
                poss, null, normalsTmp, normalsNumTmp, vertexColors,
                null, 1, globalMatrix.getAsFloatArray(true, true), false, null, r.getElapsedTimeInSecs());
        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (r.isWireFrame() ? GL4.GL_LINE : GL4.GL_FILL)); // set back to default
    }

    public static void drawCollisionMap(Renderer r, GL4 gl4, BFGaoChunk gao, Matrix4x4 globalMatrix, int color, boolean filled) {
        BFCollisionMapGroupChunk cmgc;
        BFKeyListElem tmpkle;
        BFCollisionObjectChunk cob;
        int i;
        if ((gao.getFlags() & 0x100) == 0) {
            return;
        }
        BFKeyListElem collMapkle = gao.getCollisionMapKLE();
        if (collMapkle == null || (cmgc = (BFCollisionMapGroupChunk) (collMapkle.getParseHandler())) == null
                || cmgc.getCollisionObjectChunksKLE() == null) {
            return;
        }

        for (i = 0; i < cmgc.getCollKeyNum(); i++) {
            tmpkle = cmgc.getCollisionObjectChunksKLE().get(i);
            if (tmpkle == null || (cob = (BFCollisionObjectChunk) (tmpkle.getParseHandler())) == null) {
                continue;
            }
            switch (cob.getType()) {
                case 0x1:
                    drawAABBox(r, gl4, cob.getPos1(), cob.getPos2(), globalMatrix, color, filled);
                    break;
                case 0x2:
                    drawASphere(r, gl4, cob.getPos1(), cob.getF1(), 20, 20, globalMatrix, color, filled);
                    break;
                case 0x3:
                    drawACylinder(r, gl4, cob.getPos1(), cob.getF1(), cob.getF2() * 2 /* halfLength * 2*/, 20, globalMatrix, color, filled);
                    break;
                case 0x5:
                    drawCollisionMapGeometry(r, gl4, cob, globalMatrix, color, filled);
                    break;
                default:
                    System.out.println("BIG: gao: " + gao.getGaoName() + " colmap type: " + cob.getType() + " unhandled.");
                    break;
            }
        }
    }

    public static void drawCollisionInstance(Renderer r, GL4 gl4, BFGaoChunk gao, Matrix4x4 globalMatrix, int color, boolean filled) {
        BFCollisionInstanceChunk cic;
        BFCollisionInstanceCollisionSetChunk cicsc;
        CollisionInstanceElem cie;
        Vector3 pos1 = new Vector3(), pos2 = new Vector3();
        float f1, f2;
        Matrix4x4 globalMatrixToSelect = globalMatrix;
        Vector3 scale = new Vector3(1.0f, 1.0f, 1.0f);
        float maxScale;

        int i, id;
        if ((gao.getFlags() & 0x600) == 0) {
            return;
        }
        BFKeyListElem collInstkle = gao.getCollisionInstanceChunkKLE();
        if (collInstkle == null || (cic = (BFCollisionInstanceChunk) (collInstkle.getParseHandler())) == null
                || cic.getCollisionSetChunkKLE() == null
                || (cicsc = (BFCollisionInstanceCollisionSetChunk) (cic.getCollisionSetChunkKLE().getParseHandler())) == null) {
            return;
        }

        Byte[] ids = cic.getCollisionInstanceOrderingIDs();

        if (ids == null) {
            return;
        }

        /* We are not removing scale now
        if (!r.getUseGAOPosition()) {
            // this needs to be done, because vertices are scaled with original matrix, 
            //and we are using here identity matrix
            globalMatrixToSelect = r.getGlobalMatrixOrig();
        }*/
        if ((globalMatrixToSelect.getFlags() & 0x8) != 0) {
            Matrix4x4.getScale(scale, globalMatrixToSelect);
        }

        for (i = 0; i < ids.length; i++) {
            id = ids[i];
            if ((cic.getCollElemsOrCollSetElemsBitmask() & (1 << i)) != 0) {
                if (cic.getCie().length <= id) {
                    System.out.println("BUG: Collision instance elem: " + id + " not found for GAO: " + gao.getGaoName());
                    continue;
                }
                cie = cic.getCie()[id];
            } else {
                if (cicsc.getCie().length <= id) {
                    System.out.println("BUG: Collision instance set elem: " + id + " not found for GAO: " + gao.getGaoName());
                    continue;
                }
                cie = cicsc.getCie()[id];
            }

            switch (cie.getType()) {
                case 0x1:
                    pos1.set(cie.getPos1());
                    pos2.set(cie.getPos2());
                    Vector3.divTwoVector(pos1, pos1, scale);
                    Vector3.divTwoVector(pos2, pos2, scale);
                    drawAABBox(r, gl4, pos1, pos2, globalMatrix, color, filled);
                    break;
                case 0x2:
                    pos1.set(cie.getPos1());
                    Vector3.divTwoVector(pos1, pos1, scale);
                    f1 = cie.getF1();
                    maxScale = scale.getX();
                    if (maxScale < scale.getY()) {
                        maxScale = scale.getY();
                    }
                    if (maxScale < scale.getZ()) {
                        maxScale = scale.getZ();
                    }
                    drawASphere(r, gl4, pos1, f1 * maxScale, 20, 20, globalMatrix, color, filled);
                    break;
                case 0x3:
                    pos1.set(cie.getPos1());
                    Vector3.divTwoVector(pos1, pos1, scale);
                    f1 = cie.getF1();
                    if (scale.getX() < scale.getY()) {
                        f1 *= scale.getY();
                    } else {
                        f1 *= scale.getX();
                    }
                    f2 = cie.getF2() * scale.getZ();

                    drawACylinder(r, gl4, pos1, f1, f2 * 2 /* halfLength * 2*/, 20, globalMatrix, color, filled);
                    break;
                default:
                    System.out.println("BUG: gao: " + gao.getGaoName() + " collision instance type: " + cie.getType() + " unhandled.");
                    break;
            }
        }
    }

    public static void drawBoundingBox(Renderer r, GL4 gl4, BFGaoChunk gao, boolean useGAOPosition, Matrix4x4 gaoGlobalMatrixBackup, int color) {
        RenderUtils.calculateGlobalMatrixFromGAO(gao, gao.getGlobalMatrix());
        // Bounding box is independent of scale, but we are counting in scale, when doing invertmatrix, but it won't be a problem, because
        // bounding box is getting same scale again from actual globalmatrix (we're using same scale from original globalmatrix 
        // in single gao view and world view too)
        Matrix4x4.invertMatrix(invGlobalForBoundingBox, useGAOPosition ? gao.getGlobalMatrix() : gaoGlobalMatrixBackup);
        if (BV.isSphere(gao.getBoundingBox2())) {
            tempVecForBoundingBox.set(gao.getBoundingBox2().getX(), 0.0f, 0.0f);
        } else {
            tempVecForBoundingBox.set(gao.getBoundingBox2());
        }
        Vector3.transformVector(boundingBoxMin, invGlobalForBoundingBox, gao.getBoundingBox1());
        Vector3.transformVector(boundingBoxMax, invGlobalForBoundingBox, tempVecForBoundingBox);
        if (BV.isSphere(gao.getBoundingBox2())) {
            DebugRenderFunctions.drawASphere(r, gl4, boundingBoxMin, boundingBoxMax.getX(), 20, 20, gao.getGlobalMatrix(), color, false);
        } else {
            DebugRenderFunctions.drawAABBox(r, gl4, boundingBoxMin, boundingBoxMax, gao.getGlobalMatrix(), color, false);
        }
    }
}
