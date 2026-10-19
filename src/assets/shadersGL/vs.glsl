#version 400

in vec3 position;
in vec3 normal;
in vec4 colorVertexPaint;
in vec4 colorRealTimeNormal;
in vec4 colorRealTimeSpecular;
in vec4 colorFinal;
in vec2 texCoord;

uniform mat4 texTransformMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform int uvType;
uniform int uvCustomData;
uniform mat4 globalMatrix;
uniform mat4 planarMatrix;
uniform float planarDist;
uniform int fogEnabled;
uniform float fogStart;
uniform float fogEnd;

out vec4 interpolatedColorVertexPaint;
out vec4 interpolatedColorRealTimeNormal;
out vec4 interpolatedColorRealTimeSpecular;
out vec4 interpolatedColorFinal;
out vec2 interpolatedTexCoord;
out float fogFactor;

vec2 getPlanarUVFromPlanarMatrix(int mtxType, mat4 mtx) {
    float u, v;
    switch (mtxType) {
        case 0:
            u = (((position.y * mtx[1][1]) + (position.x * mtx[1][0])) + (position.z * mtx[1][2])) + mtx[1][3];
            v = (((position.y * mtx[2][1]) + (position.x * mtx[2][0])) + (position.z * mtx[2][2])) + mtx[2][3];
            return vec2(u, v);
        case 1:
            u = (((position.y * mtx[0][1]) + (position.x * mtx[0][0])) + (position.z * mtx[0][2])) + mtx[0][3];
            v = (((position.y * mtx[2][1]) + (position.x * mtx[2][0])) + (position.z * mtx[2][2])) + mtx[2][3];
            return vec2(u, v);
        case 2:
            u = (((position.y * mtx[0][1]) + (position.x * mtx[0][0])) + (position.z * mtx[0][2])) + mtx[0][3];
            v = (((position.y * mtx[1][1]) + (position.x * mtx[1][0])) + (position.z * mtx[1][2])) + mtx[1][3];
            return vec2(u, v);
        case 3:
            mat3 transpMtx = transpose(mat3(mtx));
            v = (((mtx[1][3] * -0.57735) + (mtx[0][3] * -0.57735)) + (mtx[2][3] * 0.57735)) + 
             ((((((transpMtx[1][1] * -0.57735) + (transpMtx[1][0] * -0.57735)) + (transpMtx[1][2] * 0.57735)) * position.y) + 
               ((((transpMtx[0][1] * -0.57735) + (transpMtx[0][0] * -0.57735)) + (transpMtx[0][2] * 0.57735)) * position.x)) + 
               ((((transpMtx[2][1] * -0.57735) + (transpMtx[2][0] * -0.57735)) + (transpMtx[2][2] * 0.57735)) * position.z));
            u = (((mtx[1][3] * -0.70711) + (mtx[0][3] * 0.70711)) + (mtx[2][3] * 0.0)) + 
             ((((((transpMtx[1][1] * -0.70711) + (transpMtx[1][0] * 0.70711)) + (transpMtx[1][2] * 0.0)) * position.y) + 
               ((((transpMtx[0][1] * -0.70711) + (transpMtx[0][0] * 0.70711)) + (transpMtx[0][2] * 0.0)) * position.x)) + 
               ((((transpMtx[2][1] * -0.70711) + (transpMtx[2][0] * 0.70711)) + (transpMtx[2][2] * 0.0)) * position.z));
            return vec2(u, v);
    }
}

vec2 getUVForPlanar() {
    int planarType = (uvCustomData & 0xff) >> 6;
    int mtxType = (uvCustomData >> 4) & 3;
    mat4 tmpForDistPlanar;

    if (planarType == 0) {
        switch (mtxType) {
            case 0:
                return vec2(position.y, position.z);
            case 1: 
                return vec2(position.x, position.z);
            case 2: 
                return vec2(position.x, position.y);
            case 3:
                return vec2(((position.y * -0.70711) + (position.x * 0.70711)), ((position.y * -0.57735) + (position.x * -0.57735) + (position.z * 0.57735)));
        }
    } else if (planarType == 2) {
        tmpForDistPlanar = planarMatrix * planarDist;
        return getPlanarUVFromPlanarMatrix(mtxType, tmpForDistPlanar);
    } else {
        return getPlanarUVFromPlanarMatrix(mtxType, planarMatrix);
    }
}

vec2 getUVForDiffuse() {
    vec2 ret;
    vec4 positionViewSpace = normalize(modelViewMatrix * vec4(position, 1));
    vec4 local = modelViewMatrix * vec4(0, 0, 0, 0);
    local = normalize(local - positionViewSpace);

    vec3 normalViewSpace = normalize(modelViewMatrix * vec4(normal, 1)).xyz;
    ret.x = colorRealTimeNormal.r + colorRealTimeNormal.g + colorRealTimeNormal.b;
    ret.y = 1.0 - (dot(normalViewSpace, local.xyz));
    ret.y = clamp(ret.y, 0.1, 0.9);

    return ret;
}

float fogFactorLinear(const float dist, const float start, const float end) {
    return 1.0 - clamp((end - dist) / (end - start), 0.0, 1.0);
}

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1);
    interpolatedColorVertexPaint = colorVertexPaint;
    interpolatedColorRealTimeNormal = colorRealTimeNormal;
    interpolatedColorRealTimeSpecular = colorRealTimeSpecular;
    interpolatedColorFinal = colorFinal;

    fogFactor = 0.0;

    if (fogEnabled != 0) {
        float dist = length(vec3(modelViewMatrix * vec4(position, 1)));
        fogFactor = fogFactorLinear(dist, fogStart, fogEnd);
    }

    if (uvType == 2) {        
        mat4 ttmp = modelViewMatrix;
        // remove position from modelview matrix to prevent moving texture on vertex translation
        ttmp[3][0] = 0.0;
        ttmp[3][1] = 0.0;
        ttmp[3][2] = 0.0;
        ttmp[3][3] = 1.0;
        ttmp = ttmp * 0.5;
        if ((uvCustomData & 0x8000) != 0) {
            ttmp[0][0] = -ttmp[0][0];
            ttmp[1][0] = -ttmp[1][0];   
        }
        interpolatedTexCoord = (texTransformMatrix * ttmp * vec4(normal.x, normal.y, normal.z, 1.0)).xy;
        interpolatedTexCoord = 0.5 - interpolatedTexCoord;
    } else if (uvType == 3) {
        interpolatedTexCoord = (texTransformMatrix * vec4(getUVForDiffuse(), 1.0, 1.0)).xy;
    } else if (uvType == 6) {
        interpolatedTexCoord = (texTransformMatrix * vec4(getUVForPlanar(), 1.0, 1.0)).xy;
    } else {
        interpolatedTexCoord = (texTransformMatrix * vec4(texCoord, 1.0, 1.0)).xy;
    }
    interpolatedTexCoord.y = 1.0 - interpolatedTexCoord.y; // OpenGL texture coord V fix
}