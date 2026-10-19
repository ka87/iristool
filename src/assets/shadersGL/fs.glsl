#version 400

uniform int colorMode;
uniform int solidVertexColor;
uniform int colorBitMask;
uniform int alphaChannelMask;
uniform sampler2D texture0;

uniform int alphaTest;
uniform int alphaRef;
uniform int addDummyLighting;
uniform int useTexture;
//uniform int shouldAddAmbient;
//uniform int ambientToAdd;
uniform int fogEnabled;
uniform int fogColor;

in vec4 interpolatedColorVertexPaint;
in vec4 interpolatedColorRealTimeNormal;
in vec4 interpolatedColorRealTimeSpecular;
in vec4 interpolatedColorFinal;
in vec2 interpolatedTexCoord;
in float fogFactor;

out vec4 outputColor;

vec4 solidVertexColorV;
vec4 interpolatedColorTmp;
int solidVertexColorTmp;
vec4 texColor;

int vec4ColorToInt(vec4 v) {
    ivec4 bytes = ivec4(v * 255.0);
    return (bytes.a << 24) | (bytes.b << 16) | (bytes.g << 8) | (bytes.r);
}

vec4 intColorToVec4(int i) {
    vec4 r;
        r.r = (i & 0xff) / 255.0;
        r.g = ((i >> 8) & 0xff) / 255.0;
        r.b = ((i >> 16) & 0xff) / 255.0;
        r.a = ((i >> 24) & 0xff) / 255.0;
    return r;
}

void main() {  
    int colorTmp;
    if (colorMode == 0 || colorMode == 6) {
        colorTmp = vec4ColorToInt(interpolatedColorFinal);
    }
    if (colorMode == 1) {
        colorTmp = vec4ColorToInt(interpolatedColorRealTimeSpecular);
    }
    if (colorMode == 3 || colorMode == 5) {
        colorTmp = vec4ColorToInt(interpolatedColorVertexPaint);
    }
/*
    if ((colorMode == 0 || colorMode == 6) && shouldAddAmbient == 1) {
        colorTmp = vec4ColorToInt(clamp((intColorToVec4(ambientToAdd) + interpolatedColor), 0.0, 1.0));
    } else {
        colorTmp = vec4ColorToInt(interpolatedColor);
    }
*/
    interpolatedColorTmp = intColorToVec4(colorBitMask ^ (alphaChannelMask | colorTmp));    

    if (useTexture != 0) {
        texColor = texture(texture0, interpolatedTexCoord);
    } else {
        texColor = intColorToVec4(0xffffffff);
    }

    if (colorMode == 3 || colorMode == 5) { // 3 (only vertexpaint) and 5 (inverse vertexpaint)
        outputColor = texColor * interpolatedColorTmp;    
    } else if (colorMode == 2 || colorMode == 4) { // 2 (constant light) and 4 (full light)
        solidVertexColorTmp = colorBitMask ^ (alphaChannelMask | solidVertexColor);
        solidVertexColorV = intColorToVec4(solidVertexColorTmp);
        outputColor = texColor * solidVertexColorV;
    }else if (colorMode == 1) { // only realtime light
        outputColor = texColor;
        if (addDummyLighting != -1) outputColor = outputColor + vec4(0.6,0.6,0.6,0.0); // add dummy realtime lighting
    } else { // 6 (diffuse2x) and 0 (diffuse) should go here 
        if (addDummyLighting != -1) interpolatedColorTmp = interpolatedColorTmp + vec4(0.6,0.6,0.6,0.0);  // add dummy realtime lighting
        if (colorMode == 6) { // diffuse2x
            interpolatedColorTmp.rgb *= 2.0;
        }
        outputColor = texColor * (interpolatedColorTmp);
    }

    if (alphaTest != -1) {
        if (alphaTest == 2) { // less
            if (outputColor.a >= (alphaRef / 255.0)) {
                discard;
            }
        }
        if (alphaTest == 5) { // greater
            if (outputColor.a <= (alphaRef / 255.0)) {
                discard;
            }
        }
    }

    if (fogEnabled != 0) {
        vec4 fogColorV = intColorToVec4(fogColor);
        fogColorV.a = outputColor.a;
        outputColor = mix(outputColor, fogColorV, fogFactor);
    }
    outputColor = clamp(outputColor, 0.0, 1.0);
}