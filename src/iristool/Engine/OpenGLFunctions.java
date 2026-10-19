/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.BFWOWChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialGroup;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialMultiTexture;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialSingleTexture;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFTextureElem;
import BFlib.TextureDatabase;
import Common.DataType.GeometryTriangleChunk;
import Common.DataType.GeometryTriangleData;
import Common.DataType.MaterialMultiTextureSubElem;
import Common.DataType.Matrix4x4;
import Common.DataType.UV;
import Common.DataType.Vector3;
import Common.IrisToolDefines;
import Common.TextureTool;
import Common.TextureTransformUtil;
import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL.GL_INVALID_ENUM;
import static com.jogamp.opengl.GL.GL_INVALID_FRAMEBUFFER_OPERATION;
import static com.jogamp.opengl.GL.GL_INVALID_OPERATION;
import static com.jogamp.opengl.GL.GL_INVALID_VALUE;
import static com.jogamp.opengl.GL.GL_NO_ERROR;
import static com.jogamp.opengl.GL.GL_OUT_OF_MEMORY;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

/**
 *
 * @author andra
 */
public class OpenGLFunctions {

    private int[] objects = new int[Semantic.Object.SIZE];
    private FloatBuffer vertexBuffer1, vertexBuffer2;
    private IntBuffer indexBuffer1, indexBuffer2;
    private Integer maxIBOAndVBOBufferSize = 350000;
    private float[] vertexData = null;
    private int[] indexData = null;
    private int actBuffer = 0;

    private float[] texTransform;
    private float[] planarMatrix = new float[16];
    private float[] modelViewMtx = new float[16];
    private float[] viewMtx = new float[16];
    private float[] projectionMtx = new float[16];

    // fog params
    private int fogEnabled = 0;
    private int fogState = 0;
    private int fogColor1 = 0;
    private int fogColor2 = 0;
    private float fogStart = 0.0f;
    private float fogEnd = 1.0f;

    // Normal Calc vars
    private static float normalCalcVec1[] = new float[3];
    private static float normalCalcVec2[] = new float[3];
    private static float normalCalcVec3[] = new float[3];
    private static float normalCalcTmp1Vec3[] = new float[3];
    private static float normalCalcTmp2Vec3[] = new float[3];
    private static float normalCalcNormal[] = new float[3];
    private static float normalCalcNormalSum[] = new float[3];
    private static float normalCalcTmpNormal[] = new float[3];

    private int program, modelViewMatrixUL, texture0UL, texTransformMatrixUL,
            uvTypeUL, colorModeUL, solidVertexColorUL, colorBitMaskUL,
            alphaChannelMaskUL, alphaTestUL, alphaRefUL, addDummyLightingUL, projectionMatrixUL, viewMatrixUL,
            uvCustomDataUL, globalMatrixUL, planarMatrixUL, planarDistUL, useTextureUL,
            fogEnabledUL, fogColorUL, fogStartUL, fogEndUL;
    private final String SHADERS_ROOT = "/assets/shadersGL";

    private int[] textureGenArray = null;

    private boolean clipControlExtensionSupported = false;
    private boolean cullingEnabled = true;
    private boolean dummyRealTimeLightingEnabled = false;
    private boolean fogRenderingEnabled = true;

    private BFWOWChunk wow;
    private TextureDatabase td;
    private Dimension displaySize;

    public OpenGLFunctions() {
        vertexData = new float[maxIBOAndVBOBufferSize * 24]; // 3 position + 4x4 color[vertexpaint, normal, specular, final] + 2 UV (tex) + 3 normal
        indexData = new int[maxIBOAndVBOBufferSize];
    }

    // INIT FUNCTIONS
    private void initTexture(GL4 gl4, BFDataFileClass bfdfc, TextureDatabase td, Integer texturePackKey) {
        int i = 0;
        byte[] pd;

        if (td == null) {
            return;
        }
        td.resetTextureIterator();
        if (td.getTextureCount() > 0) {
            textureGenArray = new int[td.getTextureCount()];
            gl4.glGenTextures(td.getTextureCount(), textureGenArray, 0);
        }

        while (td.hasNext()) {
            pd = null;
            BFTextureElem bte = td.next();
            if (bte.getIgnoreFromDataParse()) {
                i++;
                continue;
            }

            if (!IrisToolDefines.isUseTexturesFromBin() && texturePackKey != null && bfdfc.getBFFile() != null) {
                String path = bfdfc.getBFFile().getAbsolutePath().substring(0, bfdfc.getBFFile().getAbsolutePath().lastIndexOf(File.separator));
                File f = new File(String.format("%s%stextures_%08x%s%08x_%s.png", path, File.separator,
                        texturePackKey, File.separator, bte.getKey(), ((bte.getPaletteKey() != null && bte.getPaletteKey() != -1) ? "P" : "T")));
                pd = TextureTool.loadImage(f);
                if (pd == null && !IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.format("Texture (0x%x) does not present in dir (%s), using from bin file.", bte.getKey(), f.getPath());
                }
            }
            if (pd == null && bte.getConvertedPixelData() == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("BUG: converted pixeldata is null (Type: " + bte.getTextureType() + "), Key: " + String.format("%x", bte.getKey()));
                }
                String lenString = ((bte.getPixelData() == null) ? "null" : "" + bte.getPixelData().length);
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println(bte.getWidth() + "," + bte.getWidth() + ": pixeldata is " + lenString + " bytes");
                }
                i++;
                continue;
            } else if (pd == null) {
                pd = bte.getConvertedPixelData();
            }
            Buffer pixelData = ByteBuffer.wrap(pd);

            gl4.glBindTexture(GL4.GL_TEXTURE_2D, textureGenArray[i++]);
            {
                gl4.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA,
                        bte.getWidth(), bte.getHeight(), 0,
                        GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, pixelData);
                gl4.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_BASE_LEVEL, 0);
                gl4.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAX_LEVEL, 0);
            }
            gl4.glBindTexture(GL4.GL_TEXTURE_2D, 0);
        }
    }

    private void initSampler(GL4 gl4) {
        gl4.glGenSamplers(1, objects, Semantic.Object.SAMPLER);
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT); // GL4.GL_CLAMP_TO_EDGE
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT); // GL4.GL_CLAMP_TO_EDGE
    }

    private void initVbo(GL4 gl4) {

        gl4.glGenBuffers(1, objects, Semantic.Object.VBO1);
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, objects[Semantic.Object.VBO1]);
        {
            vertexBuffer1 = GLBuffers.newDirectFloatBuffer(vertexData);
            gl4.glBufferData(GL4.GL_ARRAY_BUFFER, maxIBOAndVBOBufferSize * Float.BYTES, vertexBuffer1, GL4.GL_DYNAMIC_DRAW);
        }
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);

        gl4.glGenBuffers(1, objects, Semantic.Object.VBO2);
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, objects[Semantic.Object.VBO2]);
        {
            vertexBuffer2 = GLBuffers.newDirectFloatBuffer(vertexData);
            gl4.glBufferData(GL4.GL_ARRAY_BUFFER, maxIBOAndVBOBufferSize * Float.BYTES, vertexBuffer2, GL4.GL_DYNAMIC_DRAW);
        }
        gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        checkError(gl4, "initVbo");
    }

    private void initIbo(GL4 gl4) {
        for (int i = 0; i < maxIBOAndVBOBufferSize; i++) {
            indexData[i] = i;
        }

        gl4.glGenBuffers(1, objects, Semantic.Object.IBO1);
        gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, objects[Semantic.Object.IBO1]);
        {
            indexBuffer1 = GLBuffers.newDirectIntBuffer(indexData);
            indexBuffer1.rewind();
            indexBuffer1.put(indexData);
            indexBuffer1.rewind();
            gl4.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, maxIBOAndVBOBufferSize * Integer.BYTES, indexBuffer1, GL4.GL_DYNAMIC_DRAW);
        }
        gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl4.glGenBuffers(1, objects, Semantic.Object.IBO2);
        gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, objects[Semantic.Object.IBO2]);
        {
            indexBuffer2 = GLBuffers.newDirectIntBuffer(indexData);
            indexBuffer2.rewind();
            indexBuffer2.put(indexData);
            indexBuffer2.rewind();
            gl4.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, maxIBOAndVBOBufferSize * Integer.BYTES, indexBuffer2, GL4.GL_DYNAMIC_DRAW);
        }
        gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);

        checkError(gl4, "initIbo");
    }

    private void setVertexDeclarationForVao(GL4 gl4, int vaoID, int iboID, int vboID) {
        gl4.glBindVertexArray(objects[vaoID]);
        {
            gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, objects[iboID]);
            {
                gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, objects[vboID]);
                {
                    int stride = (3 + 3 + 4 + 4 + 4 + 4 + 2) * Float.BYTES;

                    gl4.glEnableVertexAttribArray(Semantic.Attr.POSITION);
                    gl4.glVertexAttribPointer(Semantic.Attr.POSITION, 3, GL4.GL_FLOAT,
                            false, stride, 0 * Float.BYTES);
                    gl4.glEnableVertexAttribArray(Semantic.Attr.NORMAL);
                    gl4.glVertexAttribPointer(Semantic.Attr.NORMAL, 4, GL4.GL_FLOAT,
                            true, stride, 3 * Float.BYTES);
                    gl4.glEnableVertexAttribArray(Semantic.Attr.COLORVERTEXPAINT);
                    gl4.glVertexAttribPointer(Semantic.Attr.COLORVERTEXPAINT, 4, GL4.GL_FLOAT,
                            true, stride, 6 * Float.BYTES);
                    gl4.glEnableVertexAttribArray(Semantic.Attr.COLORREALTIMENORMAL);
                    gl4.glVertexAttribPointer(Semantic.Attr.COLORREALTIMENORMAL, 4, GL4.GL_FLOAT,
                            true, stride, 10 * Float.BYTES);
                    gl4.glEnableVertexAttribArray(Semantic.Attr.COLORREALTIMESPECULAR);
                    gl4.glVertexAttribPointer(Semantic.Attr.COLORREALTIMESPECULAR, 4, GL4.GL_FLOAT,
                            true, stride, 14 * Float.BYTES);
                    gl4.glEnableVertexAttribArray(Semantic.Attr.COLORFINAL);
                    gl4.glVertexAttribPointer(Semantic.Attr.COLORFINAL, 4, GL4.GL_FLOAT,
                            true, stride, 18 * Float.BYTES);
                    gl4.glEnableVertexAttribArray(Semantic.Attr.TEXCOORD);
                    gl4.glVertexAttribPointer(Semantic.Attr.TEXCOORD, 2, GL4.GL_FLOAT,
                            true, stride, 22 * Float.BYTES);
                }
                gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
            }
        }
        gl4.glBindVertexArray(0);
    }

    private void initVao(GL4 gl4) {
        gl4.glGenVertexArrays(1, objects, Semantic.Object.VAO1);
        setVertexDeclarationForVao(gl4, Semantic.Object.VAO1, Semantic.Object.IBO1, Semantic.Object.VBO1);
        gl4.glGenVertexArrays(1, objects, Semantic.Object.VAO2);
        setVertexDeclarationForVao(gl4, Semantic.Object.VAO2, Semantic.Object.IBO2, Semantic.Object.VBO2);
        checkError(gl4, "initVao");
    }

    private void initProgram(GL4 gl4) {
        ShaderCode vertShader = ShaderCode.create(gl4, GL_VERTEX_SHADER, this.getClass(),
                SHADERS_ROOT, null, "vs", "glsl", null, true);
        ShaderCode fragShader = ShaderCode.create(gl4, GL_FRAGMENT_SHADER, this.getClass(),
                SHADERS_ROOT, null, "fs", "glsl", null, true);

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.add(vertShader);
        shaderProgram.add(fragShader);

        shaderProgram.init(gl4);

        program = shaderProgram.program();

        gl4.glBindAttribLocation(program, Semantic.Attr.POSITION, "position");
        gl4.glBindAttribLocation(program, Semantic.Attr.NORMAL, "normal");
        gl4.glBindAttribLocation(program, Semantic.Attr.COLORVERTEXPAINT, "colorVertexPaint");
        gl4.glBindAttribLocation(program, Semantic.Attr.COLORREALTIMENORMAL, "colorRealTimeNormal");
        gl4.glBindAttribLocation(program, Semantic.Attr.COLORREALTIMESPECULAR, "colorRealTimeSpecular");
        gl4.glBindAttribLocation(program, Semantic.Attr.COLORFINAL, "colorFinal");
        gl4.glBindFragDataLocation(program, Semantic.Frag.COLOR, "outputColor");
        gl4.glBindAttribLocation(program, Semantic.Attr.TEXCOORD, "texCoord");

        shaderProgram.link(gl4, System.out);
        projectionMatrixUL = gl4.glGetUniformLocation(program, "projectionMatrix");
        modelViewMatrixUL = gl4.glGetUniformLocation(program, "modelViewMatrix");
        viewMatrixUL = gl4.glGetUniformLocation(program, "viewMatrix");
        texTransformMatrixUL = gl4.glGetUniformLocation(program, "texTransformMatrix");
        uvTypeUL = gl4.glGetUniformLocation(program, "uvType");
        colorModeUL = gl4.glGetUniformLocation(program, "colorMode");
        solidVertexColorUL = gl4.glGetUniformLocation(program, "solidVertexColor");
        colorBitMaskUL = gl4.glGetUniformLocation(program, "colorBitMask");
        alphaChannelMaskUL = gl4.glGetUniformLocation(program, "alphaChannelMask");
        alphaTestUL = gl4.glGetUniformLocation(program, "alphaTest");
        alphaRefUL = gl4.glGetUniformLocation(program, "alphaRef");
        addDummyLightingUL = gl4.glGetUniformLocation(program, "addDummyLighting");
        uvCustomDataUL = gl4.glGetUniformLocation(program, "uvCustomData");
        globalMatrixUL = gl4.glGetUniformLocation(program, "globalMatrix");
        planarMatrixUL = gl4.glGetUniformLocation(program, "planarMatrix");
        planarDistUL = gl4.glGetUniformLocation(program, "planarDist");
        useTextureUL = gl4.glGetUniformLocation(program, "useTexture");
        fogEnabledUL = gl4.glGetUniformLocation(program, "fogEnabled");
        fogColorUL = gl4.glGetUniformLocation(program, "fogColor");
        fogStartUL = gl4.glGetUniformLocation(program, "fogStart");
        fogEndUL = gl4.glGetUniformLocation(program, "fogEnd");

        texture0UL = gl4.glGetUniformLocation(program, "texture0");
        gl4.glUseProgram(program);
        {
            gl4.glUniform1i(uvTypeUL, 0);
            gl4.glUniform1i(texture0UL, Semantic.Sampler.DIFFUSE);
        }
        gl4.glUseProgram(0);
        checkError(gl4, "initProgram");
    }

    public void init(GL4 gl4, BFDataFileClass bfdfc, BFWOWChunk wow, TextureDatabase td, Dimension displaySize, Integer texturePackKey) {

        this.displaySize = displaySize;
        this.wow = wow;
        this.td = td;
        initVbo(gl4);
        initIbo(gl4);
        initVao(gl4);
        initTexture(gl4, bfdfc, td, texturePackKey);
        initSampler(gl4);
        initProgram(gl4);
        gl4.glEnable(GL4.GL_DEPTH_TEST);
        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);
        // use Direct3D like Z coordinate mapping (helps with large terrain Z fighting)
        if (gl4.isExtensionAvailable("GL_ARB_clip_control")) {
            clipControlExtensionSupported = true;
            gl4.glClipControl(GL4.GL_LOWER_LEFT, GL4.GL_ZERO_TO_ONE);
        }
        // Alpha enable
        gl4.glEnable(GL4.GL_BLEND);
        gl4.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void destroy(GL4 gl4) {
        gl4.glDeleteProgram(program);
        gl4.glDeleteVertexArrays(1, objects, objects[Semantic.Object.VAO1]);
        gl4.glDeleteVertexArrays(1, objects, objects[Semantic.Object.VAO2]);
        gl4.glDeleteBuffers(1, objects, Semantic.Object.VBO1);
        gl4.glDeleteBuffers(1, objects, Semantic.Object.VBO2);
        gl4.glDeleteBuffers(1, objects, Semantic.Object.IBO1);
        gl4.glDeleteBuffers(1, objects, Semantic.Object.IBO2);
        try {
            if (indexBuffer1 != null) {
                BufferUtils.destroyDirectBuffer(indexBuffer1);
            }
            if (indexBuffer2 != null) {
                BufferUtils.destroyDirectBuffer(indexBuffer2);
            }
            if (vertexBuffer1 != null) {
                BufferUtils.destroyDirectBuffer(vertexBuffer1);
            }
            if (vertexBuffer2 != null) {
                BufferUtils.destroyDirectBuffer(vertexBuffer2);
            }
        } catch (Exception e) {
        }
    }

    // INIT FUNCTIONS END
    public void setFogParamsToRender(int enable, int color1, int color2, float start, float end) {
        fogEnabled = enable;
        if (fogEnabled != 0) {
            fogState = 1;
        } else {
            fogState = 0;
        }
        fogColor1 = color1;
        fogColor2 = color2;
        fogStart = start;
        fogEnd = end;
    }

    private void setCulling(GL4 gl4, Integer designFlags) {
        if (cullingEnabled && (designFlags & 0x800) != 0) {
            gl4.glEnable(GL4.GL_CULL_FACE);
            if ((designFlags & 0x2000) == 0) { // TODO: this is opposite in game
                gl4.glFrontFace(GL4.GL_CW);
            } else {
                gl4.glFrontFace(GL4.GL_CCW);
            }
        } else {
            gl4.glDisable(GL4.GL_CULL_FACE);
        }
    }

    private void setFogParams(GL4 gl4, MaterialMultiTextureSubElem mmtse, Integer designFlags) {
        boolean shouldBeFogEnabled = (fogRenderingEnabled && (designFlags & 0x4000) != 0 && fogState != 0);

        fogEnabled = shouldBeFogEnabled ? 1 : 0;
        gl4.glUniform1i(fogEnabledUL, fogEnabled);
        // Other fog params will be set in drawMultiMaterialTriangles

        switch (mmtse.getAlphaBlendMode()) {
            case 0:
            case 1:
            case 3:
            case 7:
                gl4.glUniform1i(fogColorUL, fogColor1);
                break;
            case 2:
            case 4:
                gl4.glUniform1i(fogColorUL, fogColor1);
                break;
            case 5:
            case 6:
                gl4.glUniform1i(fogColorUL, fogColor2);
                break;
            default:
                break;
        }
    }

    private void setMaterialMultiParams(GL4 gl4, MaterialMultiTextureSubElem mmtse, Integer designFlags) {
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_WRAP_S,
                (mmtse.getSamplerStateTypeAddressU() == MaterialMultiTextureSubElem.MMTSE_SSTA_WRAP ? GL4.GL_REPEAT : GL4.GL_CLAMP_TO_EDGE));
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_WRAP_T,
                (mmtse.getSamplerStateTypeAddressV() == MaterialMultiTextureSubElem.MMTSE_SSTA_WRAP ? GL4.GL_REPEAT : GL4.GL_CLAMP_TO_EDGE));
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_MAG_FILTER,
                (mmtse.getSamplerStateTypeMagAndMinFilter() == MaterialMultiTextureSubElem.MMTSE_SSTF_POINT ? GL4.GL_NEAREST : GL4.GL_LINEAR));
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_MIN_FILTER,
                (mmtse.getSamplerStateTypeMagAndMinFilter() == MaterialMultiTextureSubElem.MMTSE_SSTF_POINT ? GL4.GL_NEAREST : GL4.GL_LINEAR));
        if ((designFlags & 0x1000000) != 0) {
            gl4.glEnable(GL4.GL_DEPTH_TEST);
        } else {
            gl4.glDisable(GL4.GL_DEPTH_TEST);
        }
        setCulling(gl4, designFlags);

        switch (mmtse.getAlphaBlendMode()) {
            case 0:
            case 7:
                gl4.glDisable(GL4.GL_BLEND);
                gl4.glBlendFunc(GL4.GL_ONE, GL4.GL_ZERO);
                break;
            case 1:
                gl4.glEnable(GL4.GL_BLEND);
                if (((byte) (mmtse.getSubTextureFlags() & 0xff)) >= 0) { // subtextureflags & 0xfff és abból az alsó byte
                    gl4.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
                } else {
                    gl4.glBlendFunc(GL4.GL_ONE_MINUS_SRC_ALPHA, GL4.GL_SRC_ALPHA);
                }
                break;
            case 2:
                gl4.glEnable(GL4.GL_BLEND);
                if (((byte) (mmtse.getSubTextureFlags() & 0xff)) >= 0) { // subtextureflags & 0xfff és abból az alsó byte
                    gl4.glBlendFunc(GL4.GL_ONE, GL4.GL_ONE_MINUS_SRC_ALPHA);
                } else {
                    gl4.glBlendFunc(GL4.GL_ONE, GL4.GL_SRC_ALPHA);
                }
                break;
            case 3:
                gl4.glEnable(GL4.GL_BLEND);
                if (((byte) (mmtse.getSubTextureFlags() & 0xff)) >= 0) { // subtextureflags & 0xfff és abból az alsó byte
                    gl4.glBlendFunc(GL4.GL_DST_ALPHA, GL4.GL_ONE_MINUS_DST_ALPHA);
                } else {
                    gl4.glBlendFunc(GL4.GL_ONE_MINUS_DST_ALPHA, GL4.GL_DST_ALPHA);
                }
                break;
            case 4:
                gl4.glEnable(GL4.GL_BLEND);
                if (((byte) (mmtse.getSubTextureFlags() & 0xff)) >= 0) { // subtextureflags & 0xfff és abból az alsó byte
                    gl4.glBlendFunc(GL4.GL_DST_ALPHA, GL4.GL_ONE);
                } else {
                    gl4.glBlendFunc(GL4.GL_ONE_MINUS_DST_ALPHA, GL4.GL_ONE);
                }
                break;
            case 5:
                gl4.glEnable(GL4.GL_BLEND);
                gl4.glBlendFunc(GL4.GL_ONE, GL4.GL_ONE);
                break;
            case 6:
            case 8:
                gl4.glEnable(GL4.GL_BLEND);
                gl4.glBlendFunc(GL4.GL_ZERO, GL4.GL_ONE_MINUS_SRC_COLOR);
                break;
        }

        gl4.glDepthFunc(mmtse.getzFunc() == MaterialMultiTextureSubElem.MMTSE_ZFUNC_EQUAL ? GL4.GL_EQUAL : GL4.GL_LEQUAL);
        gl4.glDepthMask(mmtse.isZWriteEnable());
        byte cwe = mmtse.getColorWriteEnable();
        gl4.glColorMask((cwe & 1) != 0, (cwe & 2) != 0, (cwe & 4) != 0, (cwe & 8) != 0);
        if ((mmtse.getSubTextureFlags() & 0x10) != 0) {
            gl4.glUniform1i(alphaTestUL, mmtse.getAlphaFunc());
        } else {
            gl4.glUniform1i(alphaTestUL, -1);
        }
        gl4.glUniform1i(alphaRefUL, mmtse.getAlphaRefAsInt());
    }

    public static Vector3[] getVertexNormals(Vector3 poss[], Vector3 normalsTmp[], int normalsNumTmp[], GeometryTriangleChunk geotc[]) {
        GeometryTriangleData geotd[];
        int i, j;

        if (poss == null || poss.length <= 0) {
            return null;
        }

        for (i = 0; i < poss.length; i++) {
            normalsNumTmp[i] = 0;
            normalsTmp[i].setX(0.0f);
            normalsTmp[i].setY(0.0f);
            normalsTmp[i].setZ(0.0f);
        }

        for (i = 0; i < geotc.length; i++) {
            geotd = geotc[i].getGeoTriangleData();
            for (j = 0; j < geotd.length; j++) {
                normalCalcVec1[0] = poss[geotd[j].getVertex1ID()].getX();
                normalCalcVec1[1] = poss[geotd[j].getVertex1ID()].getY();
                normalCalcVec1[2] = poss[geotd[j].getVertex1ID()].getZ();
                normalCalcVec2[0] = poss[geotd[j].getVertex2ID()].getX();
                normalCalcVec2[1] = poss[geotd[j].getVertex2ID()].getY();
                normalCalcVec2[2] = poss[geotd[j].getVertex2ID()].getZ();
                normalCalcVec3[0] = poss[geotd[j].getVertex3ID()].getX();
                normalCalcVec3[1] = poss[geotd[j].getVertex3ID()].getY();
                normalCalcVec3[2] = poss[geotd[j].getVertex3ID()].getZ();
                VectorUtil.getNormalVec3(normalCalcNormal,
                        normalCalcVec1, normalCalcVec2, normalCalcVec3, normalCalcTmp1Vec3, normalCalcTmp2Vec3);
                normalsTmp[geotd[j].getVertex1ID()].setX(normalsTmp[geotd[j].getVertex1ID()].getX() + normalCalcNormal[0]);
                normalsTmp[geotd[j].getVertex1ID()].setY(normalsTmp[geotd[j].getVertex1ID()].getY() + normalCalcNormal[1]);
                normalsTmp[geotd[j].getVertex1ID()].setZ(normalsTmp[geotd[j].getVertex1ID()].getZ() + normalCalcNormal[2]);
                normalsTmp[geotd[j].getVertex2ID()].setX(normalsTmp[geotd[j].getVertex2ID()].getX() + normalCalcNormal[0]);
                normalsTmp[geotd[j].getVertex2ID()].setY(normalsTmp[geotd[j].getVertex2ID()].getY() + normalCalcNormal[1]);
                normalsTmp[geotd[j].getVertex2ID()].setZ(normalsTmp[geotd[j].getVertex2ID()].getZ() + normalCalcNormal[2]);
                normalsTmp[geotd[j].getVertex3ID()].setX(normalsTmp[geotd[j].getVertex3ID()].getX() + normalCalcNormal[0]);
                normalsTmp[geotd[j].getVertex3ID()].setY(normalsTmp[geotd[j].getVertex3ID()].getY() + normalCalcNormal[1]);
                normalsTmp[geotd[j].getVertex3ID()].setZ(normalsTmp[geotd[j].getVertex3ID()].getZ() + normalCalcNormal[2]);
                normalsNumTmp[geotd[j].getVertex1ID()]++;
                normalsNumTmp[geotd[j].getVertex2ID()]++;
                normalsNumTmp[geotd[j].getVertex3ID()]++;
            }
        }

        for (i = 0; i < poss.length; i++) {
            if (normalsNumTmp[i] > 0) {
                normalCalcNormalSum[0] = normalsTmp[i].getX();
                normalCalcNormalSum[1] = normalsTmp[i].getY();
                normalCalcNormalSum[2] = normalsTmp[i].getZ();
                VectorUtil.divVec3(normalCalcTmpNormal, normalCalcNormalSum, normalsNumTmp[i]);
            }
            normalsTmp[i].setX(normalCalcTmpNormal[0]);
            normalsTmp[i].setY(normalCalcTmpNormal[1]);
            normalsTmp[i].setZ(normalCalcTmpNormal[2]);
        }

        return normalsTmp;
    }

    private void setVAOAndIBOData(GL4 gl4, GeometryTriangleChunk[] geotc, Vector3 poss[], UV uvs[],
            Vector3 normals[], int vertexColors[]) {
        int offset = 0;
        int i, j;
        int triangleSum = 0;
        int vertexSum = 0;
        GeometryTriangleData geotd[];

        int currVertexPaintColor;
        int currNormalColor;
        int currSpecularColor;
        int currFinalColor;

        int colorVertexPaint[] = vertexColors;
        int colorNormal[] = vertexColors;
        int colorSpecular[] = vertexColors;
        int colorFinal[] = vertexColors;

        if (vertexColors == null) {
            colorVertexPaint = Lights.getVertexPaintColors();
            colorNormal = Lights.getCalculatedNormalColors();
            colorSpecular = Lights.getCalculatedSpecularColors();
            colorFinal = Lights.getCalculatedFinalColors();
        }

        for (i = 0; i < geotc.length; i++) {
            geotd = geotc[i].getGeoTriangleData();
            for (j = 0; j < geotd.length; j++) {
                // first vertex of the triangle
                vertexData[offset++] = poss[geotd[j].getVertex1ID()].getX();
                vertexData[offset++] = poss[geotd[j].getVertex1ID()].getY();
                vertexData[offset++] = poss[geotd[j].getVertex1ID()].getZ();
                if (colorVertexPaint != null && (colorVertexPaint.length - 1) >= geotd[j].getVertex1ID()) {
                    currVertexPaintColor = colorVertexPaint[geotd[j].getVertex1ID()];
                } else {
                    currVertexPaintColor = 0;
                }
                if (colorNormal != null && (colorNormal.length - 1) >= geotd[j].getVertex1ID()) {
                    currNormalColor = colorNormal[geotd[j].getVertex1ID()];
                } else {
                    currNormalColor = 0;
                }
                if (colorSpecular != null && (colorSpecular.length - 1) >= geotd[j].getVertex1ID()) {
                    currSpecularColor = colorSpecular[geotd[j].getVertex1ID()];
                } else {
                    currSpecularColor = 0;
                }
                if (colorFinal != null && (colorFinal.length - 1) >= geotd[j].getVertex1ID()) {
                    currFinalColor = colorFinal[geotd[j].getVertex1ID()];
                } else {
                    currFinalColor = 0;
                }
                vertexData[offset++] = normals[geotd[j].getVertex1ID()].getX(); // normal
                vertexData[offset++] = normals[geotd[j].getVertex1ID()].getY(); // normal
                vertexData[offset++] = normals[geotd[j].getVertex1ID()].getZ(); // normal
                vertexData[offset++] = ((currVertexPaintColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currVertexPaintColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currVertexPaintColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currVertexPaintColor >> 24) & 0xff) / 255.0f; // color  (b)
                vertexData[offset++] = ((currNormalColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currNormalColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currNormalColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currNormalColor >> 24) & 0xff) / 255.0f; // color  (b)
                vertexData[offset++] = ((currSpecularColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currSpecularColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currSpecularColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currSpecularColor >> 24) & 0xff) / 255.0f; // color  (b)
                vertexData[offset++] = ((currFinalColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currFinalColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currFinalColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currFinalColor >> 24) & 0xff) / 255.0f; // color  (b)                
                vertexData[offset++] = (uvs != null ? uvs[geotd[j].getUv1ID()].getU() : 0.0f); // tex
                vertexData[offset++] = (uvs != null ? uvs[geotd[j].getUv1ID()].getV() : 0.0f); // tex
                // second vertex of the triangle
                vertexData[offset++] = poss[geotd[j].getVertex2ID()].getX();
                vertexData[offset++] = poss[geotd[j].getVertex2ID()].getY();
                vertexData[offset++] = poss[geotd[j].getVertex2ID()].getZ();
                if (colorVertexPaint != null && (colorVertexPaint.length - 1) >= geotd[j].getVertex2ID()) {
                    currVertexPaintColor = colorVertexPaint[geotd[j].getVertex2ID()];
                } else {
                    currVertexPaintColor = 0;
                }
                if (colorNormal != null && (colorNormal.length - 1) >= geotd[j].getVertex2ID()) {
                    currNormalColor = colorNormal[geotd[j].getVertex2ID()];
                } else {
                    currNormalColor = 0;
                }
                if (colorSpecular != null && (colorSpecular.length - 1) >= geotd[j].getVertex2ID()) {
                    currSpecularColor = colorSpecular[geotd[j].getVertex2ID()];
                } else {
                    currSpecularColor = 0;
                }
                if (colorFinal != null && (colorFinal.length - 1) >= geotd[j].getVertex2ID()) {
                    currFinalColor = colorFinal[geotd[j].getVertex2ID()];
                } else {
                    currFinalColor = 0;
                }
                vertexData[offset++] = normals[geotd[j].getVertex2ID()].getX(); // normal
                vertexData[offset++] = normals[geotd[j].getVertex2ID()].getY(); // normal
                vertexData[offset++] = normals[geotd[j].getVertex2ID()].getZ(); // normal
                vertexData[offset++] = ((currVertexPaintColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currVertexPaintColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currVertexPaintColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currVertexPaintColor >> 24) & 0xff) / 255.0f; // color (b)
                vertexData[offset++] = ((currNormalColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currNormalColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currNormalColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currNormalColor >> 24) & 0xff) / 255.0f; // color  (b)
                vertexData[offset++] = ((currSpecularColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currSpecularColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currSpecularColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currSpecularColor >> 24) & 0xff) / 255.0f; // color  (b)
                vertexData[offset++] = ((currFinalColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currFinalColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currFinalColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currFinalColor >> 24) & 0xff) / 255.0f; // color  (b)                                
                vertexData[offset++] = (uvs != null ? uvs[geotd[j].getUv2ID()].getU() : 0.0f); // tex
                vertexData[offset++] = (uvs != null ? uvs[geotd[j].getUv2ID()].getV() : 0.0f); // tex
                // third vertex of the triangle
                vertexData[offset++] = poss[geotd[j].getVertex3ID()].getX();
                vertexData[offset++] = poss[geotd[j].getVertex3ID()].getY();
                vertexData[offset++] = poss[geotd[j].getVertex3ID()].getZ();
                if (colorVertexPaint != null && (colorVertexPaint.length - 1) >= geotd[j].getVertex3ID()) {
                    currVertexPaintColor = colorVertexPaint[geotd[j].getVertex3ID()];
                } else {
                    currVertexPaintColor = 0;
                }
                if (colorNormal != null && (colorNormal.length - 1) >= geotd[j].getVertex3ID()) {
                    currNormalColor = colorNormal[geotd[j].getVertex3ID()];
                } else {
                    currNormalColor = 0;
                }
                if (colorSpecular != null && (colorSpecular.length - 1) >= geotd[j].getVertex3ID()) {
                    currSpecularColor = colorSpecular[geotd[j].getVertex3ID()];
                } else {
                    currSpecularColor = 0;
                }
                if (colorFinal != null && (colorFinal.length - 1) >= geotd[j].getVertex3ID()) {
                    currFinalColor = colorFinal[geotd[j].getVertex3ID()];
                } else {
                    currFinalColor = 0;
                }
                vertexData[offset++] = normals[geotd[j].getVertex3ID()].getX(); // normal
                vertexData[offset++] = normals[geotd[j].getVertex3ID()].getY(); // normal
                vertexData[offset++] = normals[geotd[j].getVertex3ID()].getZ(); // normal
                vertexData[offset++] = ((currVertexPaintColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currVertexPaintColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currVertexPaintColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currVertexPaintColor >> 24) & 0xff) / 255.0f; // color (b)
                vertexData[offset++] = ((currNormalColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currNormalColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currNormalColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currNormalColor >> 24) & 0xff) / 255.0f; // color  (b)
                vertexData[offset++] = ((currSpecularColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currSpecularColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currSpecularColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currSpecularColor >> 24) & 0xff) / 255.0f; // color  (b)
                vertexData[offset++] = ((currFinalColor >> 0) & 0xff) / 255.0f; // color
                vertexData[offset++] = ((currFinalColor >> 8) & 0xff) / 255.0f; // color (r)
                vertexData[offset++] = ((currFinalColor >> 16) & 0xff) / 255.0f; // color (g)
                vertexData[offset++] = ((currFinalColor >> 24) & 0xff) / 255.0f; // color  (b)                                
                vertexData[offset++] = (uvs != null ? uvs[geotd[j].getUv3ID()].getU() : 0.0f); // tex
                vertexData[offset++] = (uvs != null ? uvs[geotd[j].getUv3ID()].getV() : 0.0f); // tex
            }
        }

        // Upload vertices to VBO
        if (actBuffer == 0) {
            vertexBuffer1.rewind();
            vertexBuffer1.put(vertexData, 0, offset);
            vertexBuffer1.rewind();
            gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, objects[Semantic.Object.VBO1]);
            {
                gl4.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, offset * Float.BYTES, vertexBuffer1);
            }
            gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        } else {
            vertexBuffer2.rewind();
            vertexBuffer2.put(vertexData, 0, offset);
            vertexBuffer2.rewind();
            gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, objects[Semantic.Object.VBO2]);
            {
                gl4.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, offset * Float.BYTES, vertexBuffer2);
            }
            gl4.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        }

        /* Not needed (optimization): index is static (not used as it should be), so it does not need updated here, index data is inited in initIBO()
        for (i = 0; i < geotc.length; i++) {
            triangleSum += geotc[i].getGeoTriangleDataCount();
        }

        vertexSum = triangleSum * 3; // a triangle contains 3 vertices

        for (i = 0; i < vertexSum; i++) {
            indexData[i] = i;
        }

        // Upload indices to IBO
        indexBuffer.rewind();
        indexBuffer.put(indexData);
        indexBuffer.rewind();
        gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, objects[Semantic.Object.IBO]);
        {
            gl4.glBufferSubData(GL4.GL_ELEMENT_ARRAY_BUFFER, 0, vertexSum * Integer.BYTES, indexBuffer);
        }
        gl4.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, 0);
         */
    }

    public void resetMaterialMultiParamsToDefault(GL4 gl4) {
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
        gl4.glSamplerParameteri(objects[Semantic.Object.SAMPLER], GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST_MIPMAP_LINEAR);
        gl4.glEnable(GL4.GL_DEPTH_TEST);
        gl4.glDisable(GL4.GL_CULL_FACE);
        gl4.glDisable(GL4.GL_BLEND);
        gl4.glBlendFunc(GL4.GL_ONE, GL4.GL_ZERO);
        gl4.glDepthFunc(GL4.GL_LEQUAL);
        gl4.glDepthMask(true);
        gl4.glColorMask(true, true, true, true);
        gl4.glUniform1i(alphaTestUL, -1);
        gl4.glUniform1i(alphaRefUL, 0);
    }

    public boolean checkError(GL gl, String title) {

        int error = gl.glGetError();
        if (error != GL_NO_ERROR) {
            String errorString;
            switch (error) {
                case GL_INVALID_ENUM:
                    errorString = "GL_INVALID_ENUM";
                    break;
                case GL_INVALID_VALUE:
                    errorString = "GL_INVALID_VALUE";
                    break;
                case GL_INVALID_OPERATION:
                    errorString = "GL_INVALID_OPERATION";
                    break;
                case GL_INVALID_FRAMEBUFFER_OPERATION:
                    errorString = "GL_INVALID_FRAMEBUFFER_OPERATION";
                    break;
                case GL_OUT_OF_MEMORY:
                    errorString = "GL_OUT_OF_MEMORY";
                    break;
                default:
                    errorString = "UNKNOWN";
                    break;
            }
            System.out.println("OpenGL Error(" + errorString + "): " + title);
            throw new Error();
        }
        return error == GL_NO_ERROR;
    }

    // DRAW FUNCTIONS
    private void drawElementWithTexture(GL4 gl4, int currTextureID, int triangleCnt, long renderedVerticesNum, boolean texturedRendering) {
        if (texturedRendering) {
            gl4.glBindTexture(GL4.GL_TEXTURE_2D, textureGenArray[currTextureID]);
            {
                gl4.glBindSampler(Semantic.Sampler.DIFFUSE, objects[Semantic.Object.SAMPLER]);
                {
                    gl4.glDrawElements(GL4.GL_TRIANGLES, triangleCnt * 3, GL4.GL_UNSIGNED_INT, renderedVerticesNum * 4); // 4 = GL_INT size (4 bytes)
                }
                gl4.glBindSampler(Semantic.Sampler.DIFFUSE, 0);
            }
        }
        gl4.glBindTexture(GL4.GL_TEXTURE_2D, 0);
        if (!texturedRendering) {
            gl4.glDrawElements(GL4.GL_TRIANGLES, triangleCnt * 3, GL4.GL_UNSIGNED_INT, renderedVerticesNum * 4); // 4 = GL_INT size (4 bytes)
        }
    }

    public void drawMultiMaterialTriangles(GL4 gl4, boolean spriteMode, GeometryTriangleChunk geotc[],
            Vector3 poss[], UV uvs[], Vector3 normalsTmp[], int normalsNumTmp[], int vertexColors[],
            BFGaoInnerChunkParser materialGroupIC,
            int materialFlagsUnion,
            float[] globalMatrix, boolean renderFromZlistSort, BFGaoChunk gao, float elapsedTime) {
        int unk1g;
        int i;
        int currTextureID;
        int uvFuncNum;

        Vector3 normals[] = getVertexNormals(poss, normalsTmp, normalsNumTmp, geotc);
        Lights.handleLights(wow, gao, materialFlagsUnion);
        setVAOAndIBOData(gl4, geotc, poss, uvs, normals, vertexColors);

        gl4.glUseProgram(program);
        {
            gl4.glBindVertexArray(objects[(actBuffer == 0 ? Semantic.Object.VAO1 : Semantic.Object.VAO2)]);
            {
                resetMaterialMultiParamsToDefault(gl4);
                gl4.glUniformMatrix4fv(modelViewMatrixUL, 1, false, modelViewMtx, 0);
                gl4.glUniformMatrix4fv(projectionMatrixUL, 1, false, projectionMtx, 0);
                gl4.glUniformMatrix4fv(viewMatrixUL, 1, false, viewMtx, 0);
                gl4.glUniformMatrix4fv(globalMatrixUL, 1, false, globalMatrix, 0);
                gl4.glUniform1f(planarDistUL, TextureTransformUtil.getDistanceForPlanar(displaySize.getWidth()));
                gl4.glActiveTexture(GL4.GL_TEXTURE0 + Semantic.Sampler.DIFFUSE);

                int renderedVerticesNum = 0;
                int triangleCnt;
                int colorBitMask;
                GAOInnerParserMaterialMultiTexture bfgicpMT;
                GAOInnerParserMaterialSingleTexture bfgicpST;
                Iterator mtsi;
                boolean multiMaterialsAlphaBlendingTypeHas3Or4;
                boolean textureFound = true;
                boolean texIsTransparent;
                // -- LIGHTING & FOG --
                gl4.glUniform1i(addDummyLightingUL, (dummyRealTimeLightingEnabled && ((materialFlagsUnion & 0x13) != 0) && gao != null && (gao.getDesignFlags1() & 0x8000000) != 0 ? 1 : -1)); // dummy realtime light switch                
                gl4.glUniform1f(fogStartUL, fogStart);
                gl4.glUniform1f(fogEndUL, fogEnd);
                gl4.glUniform1i(fogColorUL, fogColor1);
                // -- LIGHTING & FOG END --
                for (i = 0; i < geotc.length; i++) {
                    triangleCnt = geotc[i].getGeoTriangleDataCount();

                    if (!geotc[i].isRenderingEnabled()) {
                        renderedVerticesNum += triangleCnt * 3;
                        continue;
                    }

                    if (materialGroupIC != null && materialGroupIC.getGaoInnerType() != null
                            && materialGroupIC.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_MATERIAL_GROUP) {
                        GAOInnerParserMaterialGroup mmg = (GAOInnerParserMaterialGroup) materialGroupIC.getGaoInner();
                        BFKeyListElem mmgKLE[] = mmg.getMaterialGroupChunkArrayKLE();
                        if (mmgKLE != null) {
                            if (geotc[i].getMaterialNum() <= (mmgKLE.length - 1)) { // TODO: debug why this occurs, error handling
                                if (((BFGaoInnerChunkParser) (mmgKLE[geotc[i].getMaterialNum()]).getParseHandler()).getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_MATERIAL_MULTITEXTURE) {
                                    bfgicpMT = (GAOInnerParserMaterialMultiTexture) (((BFGaoInnerChunkParser) mmgKLE[geotc[i].getMaterialNum()].getParseHandler()).getGaoInner());
                                } else { // TODO: singletexture: ff00112d
                                    if (!IrisToolDefines.PUBLIC_RELEASE) {
                                        System.out.format("unhandled single texture: key: %x name: %s mat num: %d mat nr: %d\n", ((BFGaoInnerChunkParser) mmgKLE[geotc[i].getMaterialNum()].getParseHandler()).getGaoInner().getSelfKey(),
                                                (gao == null ? "null" : gao.getGaoName()), geotc[i].getMaterialNum(), i);
                                    }
                                    bfgicpMT = null;
                                    textureFound = false;
                                }
                            } else {
                                bfgicpMT = (GAOInnerParserMaterialMultiTexture) ((((BFGaoInnerChunkParser) (mmgKLE[mmgKLE.length - 1].getParseHandler())).getGaoInner())); // AFAIK: game does the same (see function at 453C30)
                            }
                        } else {
                            if (!IrisToolDefines.PUBLIC_RELEASE) {
                                System.out.format("unhandled single texture: name: %s mat num: %d mat nr: %d\n", (gao == null ? "null" : gao.getGaoName()), geotc[i].getMaterialNum(), i);
                            }
                            bfgicpMT = null;
                            textureFound = false;
                        }
                    } else if (materialGroupIC != null && materialGroupIC.getGaoInnerType() != null
                            && materialGroupIC.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_MATERIAL_MULTITEXTURE) {
                        bfgicpMT = (GAOInnerParserMaterialMultiTexture) (materialGroupIC.getGaoInner());
                    } else if (materialGroupIC != null && materialGroupIC.getGaoInnerType() != null
                            && materialGroupIC.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_MATERIAL_SINGLETEXTURE) {
                        if (!IrisToolDefines.PUBLIC_RELEASE) {
                            System.out.println("singletexture unhandled");
                        }
                        bfgicpST = (GAOInnerParserMaterialSingleTexture) materialGroupIC.getGaoInner();
                        bfgicpMT = null;
                        textureFound = false; // TODO: remove when single texture handling is done
                    } else {
                        bfgicpMT = null;
                        textureFound = false; // TODO: nem biztos hogy ide kéne !!!!!
                    }
                    if (bfgicpMT != null) {
                        /*
                         ZListSort & transparent material handle:
                         ----------------------------------------
                         if material transparent (but not all materials - see getGAOUsedMaterialsFlagsUnion) and normal draw; CONTINUE
                         if material not transparent and ZlistSort draw; CONTINUE
                         This renders multimaterial models (with non-transparent material) in two pass 
                         (1st pass: normal draw (opaque materials), 2nd pass: ZListSort draw ([semi]transparent materials))
                         In sprite mode, game does not check this. (Causes missing sprites, if we check !!)
                         */
                        if (!spriteMode) {
                            texIsTransparent = (bfgicpMT.getFlags() & 0xc0) != 0;
                            if (renderFromZlistSort && !texIsTransparent) {
                                renderedVerticesNum += triangleCnt * 3;
                                continue;
                            } else if (!renderFromZlistSort && texIsTransparent) {
                                renderedVerticesNum += triangleCnt * 3;
                                continue;
                            }
                        }
                        // <-- check end
                        multiMaterialsAlphaBlendingTypeHas3Or4 = RenderUtils.checkSubMatsAlphaBlendingTypeHas3Or4(bfgicpMT);
                        mtsi = bfgicpMT.getMtSubArr().iterator();
                        while (mtsi.hasNext()) {
                            colorBitMask = 0;
                            MaterialMultiTextureSubElem mmtse = (MaterialMultiTextureSubElem) mtsi.next();

                            if (td.hasKey(mmtse.getTextureKey())) {
                                currTextureID = td.getElemIndex(mmtse.getTextureKey());
                            } else {
                                currTextureID = 0;
                            }
                            if ((mmtse.getSubTextureFlags() & 0x800) != 0) { // if subtexture flags has 0x800, do not render
                                continue;
                            }

                            if (!multiMaterialsAlphaBlendingTypeHas3Or4) {
                                mmtse.setSubTextureFlags(mmtse.getSubTextureFlags() | 0x20);
                            }

                            if (gao != null) {
                                unk1g = gao.getDesignFlags1();
                            } else {
                                unk1g = ~(0x800); // all, but no culling
                            }

                            // set blending modes, samplerstate, depthfunc, etc
                            setMaterialMultiParams(gl4, mmtse, unk1g);
                            setFogParams(gl4, mmtse, unk1g);

                            if (!spriteMode && gao != null) {
                                // texture transform
                                TextureTransformUtil ttu = new TextureTransformUtil(mmtse, elapsedTime);
                                texTransform = ttu.getMatrixAsFloatArray();
                                TextureTransformUtil.selectPlanarMatrix(gao, mmtse, modelViewMtx, planarMatrix);
                            } else {
                                FloatUtil.transposeMatrix(Matrix4x4.getIdentityMatrix().getAsFloatArray(true, true), planarMatrix);
                                texTransform = Matrix4x4.getIdentityMatrix().getAsFloatArray(true, true);
                            }

                            gl4.glUniformMatrix4fv(planarMatrixUL, 1, false, planarMatrix, 0);
                            gl4.glUniformMatrix4fv(texTransformMatrixUL, 1, false, texTransform, 0);
                            gl4.glUniform1i(colorModeUL, mmtse.getColorSource());

                            if (!spriteMode) {
                                uvFuncNum = mmtse.getUVFuncNum();
                            } else {
                                uvFuncNum = 0; // if sprite, always use supplied UV-s
                            }

                            gl4.glUniform1i(uvTypeUL, uvFuncNum);

                            if (uvFuncNum == 2) {
                                gl4.glUniform1i(uvCustomDataUL, unk1g);
                            } else if (uvFuncNum == 6) {
                                gl4.glUniform1i(uvCustomDataUL, mmtse.getAlphaMaskType400());
                            } else {
                                gl4.glUniform1i(uvCustomDataUL, 0);
                            }
                            if (mmtse.getColorSource() == 4) {
                                gl4.glUniform1i(solidVertexColorUL, 0xffffff);
                            } else if (mmtse.getColorSource() == 2) {
                                gl4.glUniform1i(solidVertexColorUL, bfgicpMT.getVertexColor() & 0xffffff);
                            } else {
                                gl4.glUniform1i(solidVertexColorUL, 0);
                            }
                            if (mmtse.getColorSource() == 5) {
                                colorBitMask = 0xffffff;
                            }

                            if ((mmtse.getSubTextureFlags() & 0x400) != 0) {
                                colorBitMask = (colorBitMask | ((mmtse.getAlphaMaskType400() & 0xff00) << 16)) ^ 0xff000000;
                            }

                            gl4.glUniform1i(colorBitMaskUL, colorBitMask);
                            gl4.glUniform1i(alphaChannelMaskUL, (((mmtse.getSubTextureFlags() & 0x400) != 0) ? 0xff000000 : 0));
                            gl4.glUniform1i(useTextureUL, 1);
                            drawElementWithTexture(gl4, currTextureID, triangleCnt, renderedVerticesNum, textureFound);
                        }
                        renderedVerticesNum += triangleCnt * 3;
                    } else { // fallback if texture is not found
                        FloatUtil.transposeMatrix(Matrix4x4.getIdentityMatrix().getAsFloatArray(true, true), planarMatrix);
                        texTransform = Matrix4x4.getIdentityMatrix().getAsFloatArray(true, true);
                        gl4.glUniformMatrix4fv(planarMatrixUL, 1, false, planarMatrix, 0);
                        gl4.glUniformMatrix4fv(texTransformMatrixUL, 1, false, texTransform, 0);
                        gl4.glUniform1i(uvTypeUL, 0);
                        gl4.glUniform1i(colorModeUL, 0);
                        gl4.glUniform1i(uvCustomDataUL, 0);
                        gl4.glUniform1i(solidVertexColorUL, 0);
                        gl4.glUniform1i(colorBitMaskUL, 0);
                        gl4.glUniform1i(alphaChannelMaskUL, 0);
                        gl4.glUniform1i(useTextureUL, 0);
                        gl4.glUniform1i(fogEnabledUL, 0);
                        gl4.glUniform1i(fogColorUL, 0);
                        gl4.glUniform1f(fogStartUL, 0.0f);
                        gl4.glUniform1f(fogEndUL, 0.0f);
                        drawElementWithTexture(gl4, 0, triangleCnt, renderedVerticesNum, textureFound);
                        renderedVerticesNum += triangleCnt * 3;
                    }
                }
            }
            gl4.glBindVertexArray(0);
        }
        gl4.glUseProgram(0);
        checkError(gl4, "drawMultiMaterialTriangles (" + (gao != null ? gao.getGaoName() : "null") + ")");
        actBuffer = ++actBuffer % 2;
    }

    public boolean isDummyRealTimeLightingEnabled() {
        return dummyRealTimeLightingEnabled;
    }

    public void setDummyRealTimeLightingEnabled(boolean dummyRealTimeLightingEnabled) {
        this.dummyRealTimeLightingEnabled = dummyRealTimeLightingEnabled;
    }

    public boolean isFogRenderingEnabled() {
        return fogRenderingEnabled;
    }

    public void setFogRenderingEnabled(boolean fogRenderingEnabled) {
        this.fogRenderingEnabled = fogRenderingEnabled;
    }

    public boolean isCullingEnabled() {
        return cullingEnabled;
    }

    public void setCullingEnabled(boolean cullingEnabled) {
        this.cullingEnabled = cullingEnabled;
    }

    public boolean isClipControlExtensionSupported() {
        return clipControlExtensionSupported;
    }

    public float[] getModelViewMtx() {
        return modelViewMtx;
    }

    public float[] getViewMtx() {
        return viewMtx;
    }

    public float[] getProjectionMtx() {
        return projectionMtx;
    }
}
