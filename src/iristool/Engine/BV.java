/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;

/**
 *
 * @author andra
 */
public class BV {

    public static boolean isAABBox(Vector3 bvMaxVec) {
        return Float.floatToRawIntBits(bvMaxVec.getZ()) != -1;
    }

    public static boolean isSphere(Vector3 bvMaxVec) {
        return Float.floatToRawIntBits(bvMaxVec.getZ()) == -1;
    }

    public static boolean checkIntersectionBetweenSphereAndAABB(Vector3 sphereCenter, float radius, Vector3 aabbMin, Vector3 aabbMax) {
        float dist;

        if (sphereCenter.getX() < aabbMax.getX()) {
            if (sphereCenter.getX() < aabbMin.getX() && aabbMin.getX() - sphereCenter.getX() > radius) {
                return false;
            }
        } else if (sphereCenter.getX() - aabbMax.getX() > radius) {
            return false;
        }
        if (sphereCenter.getY() < aabbMax.getY()) {
            if (sphereCenter.getY() < aabbMin.getY() && aabbMin.getY() - sphereCenter.getY() > radius) {
                return false;
            }
        } else if (sphereCenter.getY() - aabbMax.getY() > radius) {
            return false;
        }
        if (sphereCenter.getZ() < aabbMax.getZ()) {
            if (sphereCenter.getZ() >= aabbMin.getZ()) {
                return true;
            }
            dist = aabbMin.getZ() - sphereCenter.getZ();
        } else {
            dist = sphereCenter.getZ() - aabbMax.getZ();
        }
        return dist <= radius;
    }

    public static Vector3 getGaoBVCenterPosInWorldSpace(BFGaoChunk gao, Vector3 center) {
        if (gao.getBoundingBox1() == null || gao.getBoundingBox2() == null || gao.getGlobalMatrix() == null) {
            return null;
        }
        Vector3 minBV = gao.getBoundingBox1();
        Vector3 maxBV = gao.getBoundingBox2();
        Vector3 pos = gao.getGlobalMatrix().getPos();
        if (isAABBox(maxBV)) {
            center.setX((maxBV.getX() + minBV.getX()) / 2.0f);
            center.setY((maxBV.getY() + minBV.getY()) / 2.0f);
            center.setZ((maxBV.getZ() + minBV.getZ()) / 2.0f);
            center.setX(center.getX() + pos.getX());
            center.setY(center.getY() + pos.getY());
            center.setZ(center.getZ() + pos.getZ());
        } else {
            Matrix4x4 mtx = gao.getGlobalMatrix();
            Vector3.transformVertex(center, mtx, minBV);
        }
        return center;
    }
}
