/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;
import java.awt.Dimension;

/**
 *
 * @author andra
 */
public final class Camera {

    private float[] temp = new float[16];
    private float[] cameraRaw = new float[9];
    private float[] cameraPos = new float[3];
    private float[] cameraFront = new float[3];
    private float[] cameraUp = new float[3];
    private float[] temp2 = new float[3];
    private float[] temp3 = new float[3];
    private float[] temp4 = new float[3];

    private static float FOV = FloatUtil.HALF_PI;

    private float yaw;
    private float pitch;

    public Camera() {
        reset();
    }

    public void reset() {
        yaw = -90.0f;	// yaw is initialized to -90.0 degrees since a yaw of 
        // 0.0 results in a direction vector pointing to the 
        // right so we initially rotate a bit to the left.
        pitch = 0;
        cameraPos[0] = 0.0f;
        cameraPos[1] = 0.0f;
        cameraPos[2] = 10.0f;
        cameraFront[0] = 0.0f;
        cameraFront[1] = 0.0f;
        cameraFront[2] = -1.0f;
        cameraUp[0] = 0.0f;
        cameraUp[1] = -1.0f;
        cameraUp[2] = 0.0f;
    }

    public void setViewMatrix(float[] view) {
        cameraRaw[0] = cameraPos[0];
        cameraRaw[1] = cameraPos[1];
        cameraRaw[2] = cameraPos[2];

        cameraRaw[3] = cameraPos[0] + cameraFront[0];
        cameraRaw[4] = cameraPos[1] + cameraFront[1];
        cameraRaw[5] = cameraPos[2] + cameraFront[2];

        cameraRaw[6] = cameraUp[0];
        cameraRaw[7] = cameraUp[1];
        cameraRaw[8] = cameraUp[2];

        FloatUtil.makeLookAt(view, 0, cameraRaw, 0, cameraRaw, 3, cameraRaw, 6, temp); // eye, center, up
    }

    public void forward() {
        forward(0.5f);
    }

    public void forward(float factor) {
        cameraPos[0] += factor * cameraFront[0];
        cameraPos[1] += factor * cameraFront[1];
        cameraPos[2] += factor * cameraFront[2];
    }

    public void backward() {
        backward(0.5f);
    }

    public void backward(float factor) {
        cameraPos[0] -= factor * cameraFront[0];
        cameraPos[1] -= factor * cameraFront[1];
        cameraPos[2] -= factor * cameraFront[2];
    }

    public void strafeLeft() {
        strafeLeft(0.5f);
    }

    public void strafeLeft(float factor) {
        VectorUtil.crossVec3(temp2, cameraFront, cameraUp);
        VectorUtil.normalizeVec3(temp2, temp2);
        cameraPos[0] -= factor * temp2[0];
        cameraPos[1] -= factor * temp2[1];
        cameraPos[2] -= factor * temp2[2];
    }

    public void strafeRight() {
        strafeRight(0.5f);
    }

    public void strafeRight(float factor) {
        VectorUtil.crossVec3(temp2, cameraFront, cameraUp);
        VectorUtil.normalizeVec3(temp2, temp2);
        cameraPos[0] += factor * temp2[0];
        cameraPos[1] += factor * temp2[1];
        cameraPos[2] += factor * temp2[2];
    }

    public void strafeUp() {
        strafeUp(0.5f);
    }

    public void strafeUp(float factor) {
        VectorUtil.crossVec3(temp2, cameraFront, cameraUp);
        VectorUtil.normalizeVec3(temp2, temp2);
        VectorUtil.crossVec3(temp4, temp2, cameraFront);
        VectorUtil.normalizeVec3(temp4, temp4);
        cameraPos[0] += factor * temp4[0];
        cameraPos[1] += factor * temp4[1];
        cameraPos[2] += factor * temp4[2];
    }

    public void strafeDown() {
        strafeDown(0.5f);
    }

    public void strafeDown(float factor) {
        VectorUtil.crossVec3(temp2, cameraFront, cameraUp);
        VectorUtil.normalizeVec3(temp2, temp2);
        VectorUtil.crossVec3(temp4, temp2, cameraFront);
        VectorUtil.normalizeVec3(temp4, temp4);
        cameraPos[0] -= factor * temp4[0];
        cameraPos[1] -= factor * temp4[1];
        cameraPos[2] -= factor * temp4[2];
    }

    public void handleMouseWithLeftClick(float xDiff, float yDiff) {
        yaw += xDiff;
        pitch -= yDiff;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (pitch > 89.0f) {
            pitch = 89.0f;
        }

        if (pitch < -89.0f) {
            pitch = -89.0f;
        }

        temp3[0] = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        temp3[1] = (float) Math.sin(Math.toRadians(pitch));
        temp3[2] = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        VectorUtil.normalizeVec3(cameraFront, temp3);
    }

    public void handleMouseWithRightClick(float xDiff, float yDiff) {
        if (xDiff < 0) {
            strafeLeft(Math.abs(xDiff) * 0.5f);
        } else if (xDiff > 0) {
            strafeRight(Math.abs(xDiff) * 0.5f);
        }

        if (yDiff > 0) {
            strafeDown(Math.abs(yDiff) * 0.5f);
        } else if (yDiff < 0) {
            strafeUp(Math.abs(yDiff) * 0.5f);
        }
    }

    public void setPos(float x, float y, float z) {
        reset();
        cameraPos[0] = x;
        cameraPos[1] = y;
        cameraPos[2] = z;
    }

    public static Matrix4x4 camInvert(Matrix4x4 dest, Matrix4x4 src) {
        if (dest == null) {
            dest = new Matrix4x4();
        }
        dest.setM14(0.0f);
        dest.setM24(0.0f);
        dest.setM34(0.0f);
        Matrix4x4.transposeM33(dest, src);
        dest.getPos().setX(-src.getPos().getX());
        dest.getPos().setY(-src.getPos().getY());
        dest.getPos().setZ(-src.getPos().getZ());
        Vector3.transformVector(dest.getPos(), dest, dest.getPos());
        dest.setFlags(0x6);
        dest.setW(1.0f);
        return dest;
    }

    public static float getHalfWidthDivWithTangentOfHalfFOV(int width) {
        return (width / 2.0f) / (float) Math.tan(FOV / 2.0f);
    }

    public static float getHalfHeightDivWithTangentOfHalfFOV(int width, int height) {
        return (height / 2.0f) / (float) Math.tan((FOV / 2.0f) * (height / width));
    }

    public static int getCameraDistanceFromGao(BFGaoChunk gao, Matrix4x4 cameraWorldSpaceMatrix, int width, boolean dbg) {
        float distSQ;
        Vector3 centerWorldSpace = new Vector3();
        float cameraAndGaoDistanceSQ;

        float halfWidthDivWithTangentOfHalfFOV = getHalfWidthDivWithTangentOfHalfFOV(width);
        float widthRelated = 1.0f / width * 255.0f;

        BV.getGaoBVCenterPosInWorldSpace(gao, centerWorldSpace);
        if (dbg) {
            System.out.println("getCameraDistanceFromGao: BV center: " + centerWorldSpace);
            System.out.println("getCameraDistanceFromGao: pos: " + gao.getGlobalMatrix().getPos());
            System.out.println("getCameraDistanceFromGao: CamPos: " + cameraWorldSpaceMatrix.getPos());
        }
        cameraAndGaoDistanceSQ = (centerWorldSpace.getZ() - cameraWorldSpaceMatrix.getPos().getZ())
                * (centerWorldSpace.getZ() - cameraWorldSpaceMatrix.getPos().getZ())
                + (centerWorldSpace.getY() - cameraWorldSpaceMatrix.getPos().getY())
                * (centerWorldSpace.getY() - cameraWorldSpaceMatrix.getPos().getY())
                + (centerWorldSpace.getX() - cameraWorldSpaceMatrix.getPos().getX())
                * (centerWorldSpace.getX() - cameraWorldSpaceMatrix.getPos().getX());
        if (dbg) {
            System.out.println("getCameraDistanceFromGao: cameraAndGaoDistanceSQ: " + cameraAndGaoDistanceSQ);
        }
        if (BV.isSphere(gao.getBoundingBox2())) {
            distSQ = (gao.getBoundingBox2().getX() * gao.getBoundingBox2().getX())
                    * (halfWidthDivWithTangentOfHalfFOV * halfWidthDivWithTangentOfHalfFOV)
                    / cameraAndGaoDistanceSQ;
        } else {
            distSQ = ((gao.getBoundingBox2().getZ() - gao.getBoundingBox1().getZ()) * (gao.getBoundingBox2().getZ() - gao.getBoundingBox1().getZ())
                    + (gao.getBoundingBox2().getY() - gao.getBoundingBox1().getY()) * (gao.getBoundingBox2().getY() - gao.getBoundingBox1().getY())
                    + (gao.getBoundingBox2().getX() - gao.getBoundingBox1().getX()) * (gao.getBoundingBox2().getX() - gao.getBoundingBox1().getX()))
                    * (halfWidthDivWithTangentOfHalfFOV * halfWidthDivWithTangentOfHalfFOV
                    / cameraAndGaoDistanceSQ);
        }
        if (dbg) {
            System.out.println("getCameraDistanceFromGao: distsq: " + distSQ);
        }
        double ret = RenderUtils.getRootForFloat(distSQ) * widthRelated;
        if (dbg) {
            System.out.println("getCameraDistanceFromGao: distf: " + ret);
        }

        if (ret >= 255.0f) {
            ret = 255.0f;
        }
        int retB = (int) ret;
        if (retB <= 0) {
            retB = 1;
        }
        return retB;
    }

    public static void Cam2dTo3dCamera2(Vector3 dest, Vector3 src, int width, int height) {

        // TODO: maybe broken !!!!
        float halfWidthDivWithTangentOfHalfFOV = getHalfWidthDivWithTangentOfHalfFOV(width);
        float halfHeightDivWithTangentOfHalfFOV = getHalfHeightDivWithTangentOfHalfFOV(width, height);

        dest.setX((width + src.getX() - (width / 2.0f))
                * (src.getZ() / halfWidthDivWithTangentOfHalfFOV));
        dest.setY(((height / 2.0f) - width - src.getY())
                * (src.getZ() / halfHeightDivWithTangentOfHalfFOV));
        dest.setZ(src.getZ());

        /*
    dest.setX((src.getX() - (dim.width/2.0f)) / halfWidthDivWithTangentOfHalfFOV);
    dest.setY(((dim.height/2.0f) - src.getY()) / halfHeightDivWithTangentOfHalfFOV);
    dest.setZ(src.getZ());
         */
    }

    public static float getFOV() {
        return FOV;
    }
}
