/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.Engine;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFActionChunk;
import BFDataFileLib.BFChunkParsers.Map.BFActionListChunk;
import iristool.RendererControlsWindow;
import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.BFWOWChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserParticleGenerator;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGAOInverseKinematics;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGAOSpecialLookAt;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoMorphModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierLazyModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierMeshWave;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierObjectFakeLookAtCameraModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierParticleGenerator;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSnake;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSpriteGenModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierXMECModifier;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFlib.TextureDatabase;
import Common.DataType.GeometrySprite;
import Common.DataType.GeometryTriangleChunk;
import Common.DataType.GeometryTriangleData;
import Common.DataType.Matrix4x4;
import Common.DataType.Vector3;
import Common.DataType.UV;
import Common.DataType.ZListNode;
import Common.DataType.ZListNodeElem;
import Common.IrisToolDefines;
import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.Animator;
import iristool.GUI.GUIDefines;
import iristool.Engine.ModifierHandlers.InverseKinematics;
import iristool.Engine.ModifierHandlers.Lazy;
import iristool.Engine.ModifierHandlers.ObjectFakeLookAtCamera;
import iristool.Engine.ModifierHandlers.MeshWave;
import iristool.Engine.ModifierHandlers.Morph;
import iristool.Engine.ModifierHandlers.Snake;
import iristool.Engine.ModifierHandlers.SpecialLookAt;
import iristool.Engine.ModifierHandlers.SpriteGen;
import iristool.Engine.ModifierHandlers.XMEC;
import iristool.PositionDialog;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

class SpriteData {

    public int color;
    public float vec1X;
    public float vec1Y;
    public float vec1Z;
    public float vec2X;
    public float vec2Y;
    public float vec2Z;
    public float vec3X;
    public float vec3Y;
    public float vec3Z;
    public float vec4X;
    public float vec4Y;
    public float vec4Z;
}

/*
    NOTE for FF005EFB (ilot plaques gao) broken Lycoperdon fugiferus display
    Game uses function #260031B7 to hide shape ID (OFC) 0,2,3,4 to hide.
    (see des_i_rouge in Variables and V/F arrays)

    TODO: 
      - gl_TexCoord[0] = gl_MultiTexCoord0
 */
public class Renderer implements GLEventListener, KeyListener, MouseListener {

    private final int LRM_REALTIME_ON = 0;
    private final int LRM_REALTIME_AND_DUMMY_OFF = 1;
    private final int LRM_ONLY_DUMMY_LIGHT_ON = 2;

    private String windowNameExt = "";
    private Dimension windowSize = new Dimension(800, 600);
    public static GLWindow glWindow;
    public static Animator animator;
    private JDialog mainFrame;
    private RendererCallbacksInterface parent;
    private JDialog rendererControlsWindow;
    private PositionDialog positionDialog = null;

    private float xAxisRotation = GUIDefines.RENDERER_X_AXIS_PREROTATION;
    private float yAxisRotation = GUIDefines.RENDERER_Y_AXIS_PREROTATION;
    private float zAxisRotation = GUIDefines.RENDERER_Z_AXIS_PREROTATION;

    private float xAxisRotationBone = 0.0f;
    private float yAxisRotationBone = 0.0f;
    private float xTranslateBone = 0.0f;
    private float yTranslateBone = 0.0f;

    private float translateFactor = 0.5f;

    private boolean skinComputeEnabled = true;
    private boolean morphEnabled = true;
    private boolean ikEnabled = false;
    private boolean XMECEnabled = true;
    private boolean bonePositionerEnabled = false;
    private boolean isWireFrame = false;
    private boolean isAntiAliasingEnabled = true;
    private int invisibleElemsDebugMode = 0;
    private boolean zListSortEnabled = true;
    private int lightRenderingMode = LRM_REALTIME_ON;
    private boolean isCollMapDebugOn = false;
    private boolean isCollInstanceDebugOn = false;

    private float[] translationBone = new float[16];
    private float[] rotation = new float[16];
    private float[] rotationBone = new float[16];
    private float[] model = new float[16];
    private Matrix4x4 spriteViewMatrixTmp2 = new Matrix4x4();
    private Matrix4x4 spriteViewMatrixTmp3 = new Matrix4x4();

    private TextureDatabase td = null;
    private BFDataFileClass bfdfc = null;
    private BFWOWChunk bfwc = null;
    private Integer texturePackKey = null;

    private Integer prevX = null, prevY = null;
    private Integer clearColor = 0x555555;
    private ArrayList<BFGaoChunk> gaos;
    private ArrayList<Boolean> processedGaos;
    private ArrayList<BFGaoChunk> multiPartGaosToDisplay;
    private boolean gaoRenderingForcefully = false;
    private boolean useGAOPosition = true;
    private BFGaoChunk currentlyManipluatedMultipartGao = null;
    private BFGaoChunk currentlyManipluatedBoneGao = null;
    private Matrix4x4 boneManipulatorLocalBoneMatricesOrig[] = null;

    private Matrix4x4 backupMatrix = new Matrix4x4();
    private Matrix4x4 gaoGlobalMatrixBackup = new Matrix4x4();
    private Matrix4x4 gaoLocalMatrixBackup = null;
    private BFKeyListElem gaoPivotBackup = null;
    private Integer gaoPivotKeyBackup = null;
    private Matrix4x4 globalMatrixRenderTemp = new Matrix4x4();
    private AnimationPlayer animationPlayer;
    private BFGaoChunk aniGao;
    private BFGaoChunk selectedSingleGao;
    private boolean aniPlaying = false;
    private Long aniPlayingPrevTime = null;
    private Integer aniPlayingStartAction = null;
    private BFGaoChunk runApplyShapeOnThisGao = null;
    private ZListSorter zListPool[];
    private SpriteData spriteToDisplay = new SpriteData();
    private float finalViewMatrixForCamera[] = new float[16];
    private Matrix4x4 cameraWorldSpace = new Matrix4x4();

    private GLProfile glProfile = null;
    private String initError = null;

    private Camera cam;
    private OpenGLFunctions oglf;

    private long start, now, prev = 0;

    // for FPS display
    private float renderingLastTimeForFps = 0.0f;
    private long fpsCount = 0;

    private int renderingCounter = 0;

    public boolean isWireFrame() {
        return isWireFrame;
    }

    public int getRenderingCounter() {
        return renderingCounter;
    }

    public float[] getRotationMatrix() {
        return rotation;
    }

    public OpenGLFunctions getOglf() {
        return oglf;
    }

    public boolean iszListSortEnabled() {
        return zListSortEnabled;
    }

    public float getElapsedTimeInSecs() {
        now = System.currentTimeMillis();
        return (float) (now - start) / 1000;
    }

    public float getElapsedTimeInSecsPerLoop() {
        if (prev == 0) {
            prev = start;
        }
        now = System.currentTimeMillis();
        float f = (float) (now - prev) / 1000;
        prev = now;
        if (f < 0.0099999998f) {
            f = 0.0099999998f;
        }
        if (f > 0.26666668f) {
            f = 0.26666668f;
        }

        return f;
    }

    public long getElapsedTimeInMillis() {
        now = System.currentTimeMillis();
        return now - start;
    }

    public Renderer(RendererCallbacksInterface parent) {

        oglf = new OpenGLFunctions();
        cam = new Camera();

        try {
            glProfile = GLProfile.get(GLProfile.GL4);
        } catch (Exception e) {
            initError = e.getLocalizedMessage();
            return;
        }

        animationPlayer = new AnimationPlayer();
        zListPool = new ZListSorter[8];
        for (int i = 0; i < 8; i++) {
            zListPool[i] = new ZListSorter();
        }
        this.parent = parent;
        gaos = new ArrayList<>();
        processedGaos = new ArrayList<>();
        multiPartGaosToDisplay = new ArrayList<>();
        Lights.setRealTimeLightingEnabled(true);
    }

    public String getInitError() {
        return initError;
    }

    public void setClearColor(Integer clearColor) {
        this.clearColor = clearColor;
    }

    public Integer getTexturePackKey() {
        return texturePackKey;
    }

    public void setTexturePackKey(Integer texturePackKey) {
        this.texturePackKey = texturePackKey;
    }

    private void clearZLists() {
        for (int i = 0; i < 8; i++) {
            zListPool[i].clear();
        }
    }

    public void setRunApplyShapeOnThisGao(BFGaoChunk runApplyShapeOnThisGao) {
        this.runApplyShapeOnThisGao = runApplyShapeOnThisGao;
    }

    public BFGaoChunk getAnigao() {
        return aniGao;
    }

    public void setAniGao(BFGaoChunk aniGao) {
        this.aniGao = aniGao;
    }

    public BFGaoChunk getSelectedSingleGao() {
        return selectedSingleGao;
    }

    public void setAnimationDebug(boolean b) {
        if (animationPlayer == null) {
            return;
        }
        animationPlayer.setAnimDebugEnabled(b);
    }

    public void startAniPlaying(int idx) {
        aniPlayingStartAction = idx;
    }

    public boolean isAniPlaying() {
        return aniPlaying;
    }

    public void setAniPlaying(boolean aniPlaying) {
        this.aniPlaying = aniPlaying;
    }

    public void setAnimGaoPositionUpdateMode(int positionUpdateMode) {
        animationPlayer.setAnimGaoPositionUpdateMode(positionUpdateMode);
    }

    public int getAnimGaoPositionUpdateMode() {
        return animationPlayer.getAnimGaoPositionUpdateMode();
    }

    public void setPosition(Vector3 p) {
        cam.setPos(p.getX(), p.getY(), p.getZ());
    }

    public Boolean getUseGAOPosition() {
        return useGAOPosition;
    }

    public void setUseGAOPosition(Boolean useGAOPosition) {
        this.useGAOPosition = useGAOPosition;
    }

    public Boolean getGaoRenderingForcefully() {
        return gaoRenderingForcefully;
    }

    public void setGaoRenderingForcefully(Boolean gaoRenderingForcefully) {
        this.gaoRenderingForcefully = gaoRenderingForcefully;
    }

    public Boolean getSkinComputeEnabled() {
        return skinComputeEnabled;
    }

    public void setSkinComputeEnabled(Boolean skinComputeEnabled) {
        this.skinComputeEnabled = skinComputeEnabled;
    }

    public Boolean getBonePositionerEnabled() {
        return bonePositionerEnabled;
    }

    public void setBonePositionerEnabled(Boolean bonePositionerEnabled) {
        this.bonePositionerEnabled = bonePositionerEnabled;
    }

    public Boolean getMorphEnabled() {
        return morphEnabled;
    }

    public void setMorphEnabled(Boolean morphEnabled) {
        this.morphEnabled = morphEnabled;
    }

    public BFGaoChunk getCurrentlyManipluatedBoneGao() {
        return currentlyManipluatedBoneGao;
    }

    public boolean isDummyRealTimeLightingEnabled() {
        return lightRenderingMode == LRM_ONLY_DUMMY_LIGHT_ON;
    }

    public void setDummyRealTimeLightingEnabled(boolean dummyRealTimeLightingEnabled) {
        if (dummyRealTimeLightingEnabled) {
            lightRenderingMode = LRM_ONLY_DUMMY_LIGHT_ON;
        } else {
            lightRenderingMode = LRM_REALTIME_ON;
        }
    }

    public Boolean getIkEnabled() {
        return ikEnabled;
    }

    public void setIkEnabled(Boolean ikEnabled) {
        this.ikEnabled = ikEnabled;
    }

    private void setCurrentlyManipluatedMultipartGao(BFGaoChunk currentlyManipluatedMultipartGao) {
        this.currentlyManipluatedMultipartGao = null;
        ArrayList grpl = RenderUtils.getGRPLForGao(currentlyManipluatedMultipartGao);
        if (grpl == null) {
            return;
        }
        if (grpl.size() > 0) {
            boneManipulatorLocalBoneMatricesOrig = new Matrix4x4[grpl.size()];
        }

        Iterator it = grpl.iterator();
        int i = 0;
        while (it.hasNext()) {
            TreeMap<Integer, BFKeyListElem> tm = (TreeMap<Integer, BFKeyListElem>) it.next();
            BFGaoChunk boneGao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            if (boneGao.getLocalMatrix() != null) {
                boneManipulatorLocalBoneMatricesOrig[i] = Matrix4x4.copy(boneGao.getLocalMatrix());
            } else {
                boneManipulatorLocalBoneMatricesOrig[i] = null;
            }
            i++;
        }
        this.currentlyManipluatedMultipartGao = currentlyManipluatedMultipartGao;
    }

    public void setCurrentlyManipulatedBoneGao(BFGaoChunk currentlyManipluatedBoneGao) {
        this.currentlyManipluatedBoneGao = null;

        if (currentlyManipluatedMultipartGao == null || currentlyManipluatedBoneGao == null) {
            return;
        }

        this.currentlyManipluatedBoneGao = currentlyManipluatedBoneGao;
        xAxisRotationBone = 0.0f;
        yAxisRotationBone = 0.0f;
        xTranslateBone = 0.0f;
        yTranslateBone = 0.0f;
    }

    private void setWindowTitle(Float fps) {
        mainFrame.setTitle("IrisTool Model Renderer Window" + (windowNameExt != null && windowNameExt.equals("")
                ? "" : ": " + windowNameExt) + (fps != null ? String.format(" [%.2f fps]", fps) : ""));
    }

    private void setWindowParams() {
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        glCapabilities.setSampleBuffers(true);
        glWindow = GLWindow.create(glCapabilities);
        glWindow.addGLEventListener(this);
        glWindow.addKeyListener(this);
        glWindow.addMouseListener(this);
        glWindow.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
        final NewtCanvasAWT lNewtCanvasAWT = new NewtCanvasAWT(glWindow);
        mainFrame = new JDialog((JDialog) parent, false);
        setWindowTitle(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setSize(windowSize.getWidth(), windowSize.getHeight());
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(lNewtCanvasAWT, BorderLayout.CENTER);
        mainFrame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent ce) {
                rendererControlsWindowPositionUpdate();
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
                rendererControlsWindowPositionUpdate();
            }

            @Override
            public void componentShown(ComponentEvent ce) {

            }

            @Override
            public void componentHidden(ComponentEvent ce) {

            }
        });
    }

    public void setWindowNameExt(String windowName) {
        this.windowNameExt = windowName;
    }

    public void setTextureDatabase(TextureDatabase td) {
        this.td = td;
    }

    public void setBfdfc(BFDataFileClass bfdfc) {
        this.bfdfc = bfdfc;
    }

    public void setBfwc(BFWOWChunk bfwc) {
        this.bfwc = bfwc;
    }

    public void restoreBoneGaoLocalMatrix(BFGaoChunk bgao) {
        if (currentlyManipluatedMultipartGao == null
                || currentlyManipluatedBoneGao == null || currentlyManipluatedBoneGao.getLocalMatrix() == null) {
            return;
        }

        Integer idx = RenderUtils.getBoneGaoIndexFromMultipartGao(currentlyManipluatedMultipartGao, bgao);
        if (idx == null || boneManipulatorLocalBoneMatricesOrig[idx] == null) {
            return;
        }
        if (bgao == null) {
            return;
        }
        if (bgao.getLocalMatrix() == null || boneManipulatorLocalBoneMatricesOrig[idx] == null) {
            return;
        }
        bgao.getLocalMatrix().set(boneManipulatorLocalBoneMatricesOrig[idx]);
    }

    public void restoreAllLocalMatrices() {
        if (currentlyManipluatedMultipartGao == null) {
            return;
        }
        Iterator it = RenderUtils.getGRPLForGao(currentlyManipluatedMultipartGao).iterator();
        int i = -1;
        while (it.hasNext()) {
            i++;
            TreeMap<Integer, BFKeyListElem> tm = (TreeMap<Integer, BFKeyListElem>) it.next();
            BFGaoChunk boneGao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            if (boneGao != null && boneGao.getLocalMatrix() != null && boneManipulatorLocalBoneMatricesOrig != null && boneManipulatorLocalBoneMatricesOrig[i] != null) {
                boneGao.getLocalMatrix().set(boneManipulatorLocalBoneMatricesOrig[i]);
            }
        }
    }

    private void updateLocalBoneMatrix() {
        if (currentlyManipluatedMultipartGao == null
                || currentlyManipluatedBoneGao == null || currentlyManipluatedBoneGao.getLocalMatrix() == null) {
            return;
        }
        float[] dest = new float[16];

        Integer idx = RenderUtils.getBoneGaoIndexFromMultipartGao(currentlyManipluatedMultipartGao, currentlyManipluatedBoneGao);
        if (idx == null || boneManipulatorLocalBoneMatricesOrig[idx] == null) {
            return;
        }

        Matrix4x4 local = boneManipulatorLocalBoneMatricesOrig[idx];
        FloatUtil.makeRotationEuler(rotationBone, 0, xAxisRotationBone, yAxisRotationBone, 0.0f);
        FloatUtil.makeTranslation(translationBone, true, xTranslateBone, yTranslateBone, 0.0f);

        System.arraycopy(local.getAsFloatArray(true, true), 0, dest, 0, dest.length);

        FloatUtil.multMatrix(rotationBone, dest, dest);
        FloatUtil.multMatrix(translationBone, dest, dest);
        currentlyManipluatedBoneGao.getLocalMatrix().set(new Matrix4x4(dest[0], dest[1], dest[2], dest[3], dest[4], dest[5],
                dest[6], dest[7], dest[8], dest[9], dest[10], dest[11], dest[12], dest[13],
                dest[14], dest[15], local.getFlags()));
    }

    private void rendererControlsWindowPositionUpdate() {
        if (rendererControlsWindow != null) {
            Point newLoc = new Point();
            newLoc.setLocation(mainFrame.getX() + mainFrame.getWidth(), mainFrame.getY());
            rendererControlsWindow.setLocation(newLoc);
        }
    }

    public void renderAGao(BFGaoChunk gao) {
        if (initError != null) {
            return;
        }
        start = System.currentTimeMillis();
        SpriteGen.init(this);
        gaos.add(gao);
        processedGaos.add(false);

        // save original position, set current matrix to identity matrix, and copy scale from original matrix
        // to keep scale information and ensure modifiers and collision to work
        if (!useGAOPosition) {
            gaoGlobalMatrixBackup.set(gao.getGlobalMatrix());
            Vector3 scale = new Vector3();
            Matrix4x4.getScale(scale, gao.getGlobalMatrix());
            gao.getGlobalMatrix().set(Matrix4x4.getIdentityMatrix());
            Matrix4x4.setScale(gao.getGlobalMatrix(), scale);
            if ((gao.getFlags() & 0x10000) != 0 || (gao.getFlags() & 0x4000000) != 0) {
                gao.getGlobalMatrixSecond().set(Matrix4x4.getIdentityMatrix());
                Matrix4x4.setScale(gao.getGlobalMatrixSecond(), scale);
            }
            if ((gao.getFlags() & 0x10000) != 0 && (gao.getFlags() & 0x4000000) != 0) {
                gao.getGlobalMatrixThird().set(Matrix4x4.getIdentityMatrix());
                Matrix4x4.setScale(gao.getGlobalMatrixThird(), scale);
            }
            // remove reference, if it has
            if ((gao.getFlags() & 0x400000) != 0 && gao.getReferencePointGAOChunkKLE() != null) {
                gaoPivotBackup = gao.getReferencePointGAOChunkKLE();
                gao.setReferencePointGAOChunkKLE(null);
                gaoPivotKeyBackup = gao.getReferencePointGAOKey();
                gao.setReferencePointGAOKey(-1);
                gaoLocalMatrixBackup = gao.getLocalMatrix();
                gao.setLocalMatrix(null);
                gao.setFlags(gao.getFlags() & ~0x400000);
            }
        }
        Skeleton.applyShapeForGao(gao);
        setWindowParams();
        positionDialog = new PositionDialog(mainFrame, false, this);
        mainFrame.setVisible(true);

        selectedSingleGao = gao;

        if (gao.getActionListChunkKLE() != null && gao.getActionListChunkKLE().getParseHandler() != null) {
            BFActionListChunk al = (BFActionListChunk) (gao.getActionListChunkKLE().getParseHandler());
            if (al.getActionsKLE() != null && al.getActionsKLE().size() > 0
                    && al.getActionsKLE().get(0) != null && al.getActionsKLE().get(0).getParseHandler() != null) {
                BFActionChunk ac = (BFActionChunk) (al.getActionsKLE().get(0).getParseHandler());
                //TODO: animationPlayer.setGaoAction(gao, 0, true);
                aniGao = gao;
            }
        }

        animator = new Animator(glWindow);
        animator.start();
        if (bonePositionerEnabled) {
            if (RenderUtils.getGRPLForGao(gao) != null) {
                setCurrentlyManipluatedMultipartGao(gao);
            }
            rendererControlsWindow = new RendererControlsWindow(mainFrame, bfwc, gao, this, false);
            rendererControlsWindowPositionUpdate();
            rendererControlsWindow.setVisible(true);
        }
    }

    public void addGao(BFGaoChunk gao) {
        gaos.add(gao);
        Skeleton.applyShapeForGao(gao);
        processedGaos.add(false);
    }

    public ArrayList<BFGaoChunk> getGaos() {
        return gaos;
    }

    public void renderAllGao() {
        if (initError != null) {
            return;
        }
        start = System.currentTimeMillis();
        SpriteGen.init(this);
        Scripting.resolveWowScripting(bfdfc, gaos);
        setWindowParams();

        positionDialog = new PositionDialog(mainFrame, false, this);
        mainFrame.setVisible(true);

        animator = new Animator(glWindow);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl4 = drawable.getGL().getGL4();
        oglf.init(gl4, bfdfc, bfwc, td, windowSize, texturePackKey);
    }

    private void resetProcessedGaosForLoopDetection() {
        for (int i = 0; i < processedGaos.size(); i++) {
            processedGaos.set(i, false);
        }
    }

    public void addGaoToZList(BFGaoChunk gao, Matrix4x4 globalMatrix) {
        Byte zSorterPoolID = gao.getZListBufferNum(true);
        if (zSorterPoolID != null) {
            ZListNodeElem ne = zListPool[zSorterPoolID].addNode(1.0f / oglf.getModelViewMtx()[14]);
            if (ne != null) {
                ne.setGao(gao);
                ne.setMtx(globalMatrix);
            }
        }
    }

    private void drawOneGAO(GL4 gl4, BFGaoChunk gao, Matrix4x4 globalMatrix, boolean renderFromZlistSort) {
        int i;
        BFGaoInnerChunkParser geoICToRender = RenderUtils.getGeometryICTFromLODGAO(gao);
        if (geoICToRender == null || geoICToRender.getGaoInner() == null
                || geoICToRender.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_STRINGSTRUCT // TODO
                || geoICToRender.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_CAMERA // TODO
                || geoICToRender.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_ZERO_UNKNOWN // TODO
                ) {
            return;
        }

        if (!gaoRenderingForcefully && !gao.shouldItRender()) {
            return;
        }

        Matrix4x4 view = new Matrix4x4(oglf.getViewMtx());
        Matrix4x4.fixMatrix(view);
        Matrix4x4 rot = new Matrix4x4(rotation);
        Matrix4x4.fixMatrix(rot);

        if ((gao.getVisualFlags() & 0x800000) != 0) {
            globalMatrix = RenderUtils.getLookAtCameraMatrixForGlobalMatrix(globalMatrix, view, rot);
        }

        // NOTE: view, rotation array set done in display() method
        // -- Object position compute --
        System.arraycopy(globalMatrix.getAsFloatArray(true, true), 0, model, 0, model.length);

        // add prerotation to model
        FloatUtil.multMatrix(rotation, model, model);
        // make modelview matrix
        System.arraycopy(model, 0, oglf.getModelViewMtx(), 0, oglf.getModelViewMtx().length);
        FloatUtil.multMatrix(oglf.getViewMtx(), oglf.getModelViewMtx(), oglf.getModelViewMtx());
        // -- Object position compute end --

        // calculate gao distance from Camera
        int dist = Camera.getCameraDistanceFromGao(gao, cameraWorldSpace, windowSize.getWidth(), false);

        BFGaoInnerChunkParser materialGroupIC = RenderUtils.getMaterialICFromGAO(gao);

        if (geoICToRender.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR) {
            GAOInnerParserParticleGenerator pagIN = ((GAOInnerParserParticleGenerator) (geoICToRender.getGaoInner()));
            ParticleGenerator.handlePAG(gl4, gao, pagIN, oglf.getViewMtx(), rotation, renderFromZlistSort, this);
            return;
        }

        // -- ONLY FOR GEOMETRY AFTER THIS LINE --
        if (((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getGeometryTriangleChunks() == null) {
            return;
        }

        GeometryTriangleChunk geotc[] = ((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getGeometryTriangleChunks();
        int materialFlagsUnion = RenderUtils.getGAOUsedMaterialsFlagsUnion(geotc, materialGroupIC);

        if (zListSortEnabled && (materialFlagsUnion & 0xc0) != 0 && !renderFromZlistSort) {
            addGaoToZList(gao, globalMatrix);
            if ((materialFlagsUnion & 0x40) != 0) {
                return;
            }
        }

        Vector3 poss[] = ((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getVertices();
        if (poss == null) {
            return;
        }
        Vector3 possTemp[] = ((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getVerticesTempForCalc();
        Vector3 possSkinning[] = ((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getVerticesTempForCalc2();
        if (possTemp == null || possSkinning == null) {
            return;
        }

        for (i = 0; i < poss.length; i++) {
            possTemp[i].setX(poss[i].getX());
            possTemp[i].setY(poss[i].getY());
            possTemp[i].setZ(poss[i].getZ());
            possSkinning[i].setX(0.0f);
            possSkinning[i].setY(0.0f);
            possSkinning[i].setZ(0.0f);
        }

        // Modifier handle (only those supposed to call before draw)
        if (gao.getModifiersArray() != null) {
            ArrayList<BFChunkParseInterface> modArr = gao.getModifiersArray();
            for (BFChunkParseInterface modIF : modArr) {
                if (modIF == null) {
                    continue;
                }
                if (morphEnabled && modIF instanceof BFModifierGeoMorphModifier) {
                    Morph.handleMorph((BFModifierGeoMorphModifier) modIF, possTemp);
                }
                if (modIF instanceof BFModifierParticleGenerator) {
                    BFModifierParticleGenerator pag = (BFModifierParticleGenerator) modIF;
                    if (pag.getPag() != null) {
                        ParticleGenerator.handlePAG(gl4, gao, pag.getPag(), oglf.getViewMtx(), rotation, renderFromZlistSort, this);
                    }
                }
                if (modIF instanceof BFModifierMeshWave) {
                    BFModifierMeshWave mw = (BFModifierMeshWave) modIF;
                    MeshWave.handleMeshWave(mw, gao, possTemp, poss.length, getElapsedTimeInSecsPerLoop());
                }
            }
        }

        if (skinComputeEnabled && ((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getSkinBoneIDs() != null
                && (gao.getFlags() & 0x200000) != 0) {
            Skeleton.skinCompute(gao, possTemp, possSkinning);
            possTemp = possSkinning;
        }

        UV uvs[] = ((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getUVs(); // UV's can be null
        Vector3 normalsTmp[] = ((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getNormalsTemp();
        int normalsNumTmp[] = ((GAOInnerParserGeometry) (geoICToRender.getGaoInner())).getNormalsNumTemp();

        Lights.setMatrices(globalMatrix, view, rot);
        oglf.drawMultiMaterialTriangles(gl4, false, geotc, possTemp, uvs, normalsTmp,
                normalsNumTmp, null, materialGroupIC,
                materialFlagsUnion, globalMatrix.getAsFloatArray(true, true), renderFromZlistSort, gao, getElapsedTimeInSecs());

        // Modifier handle (only those supposed to call after draw)
        if (gao.getModifiersArray() != null) {
            ArrayList<BFChunkParseInterface> modArr = gao.getModifiersArray();
            for (BFChunkParseInterface modIF : modArr) {
                if (modIF == null) {
                    continue;
                }
                if (modIF instanceof BFModifierSpriteGenModifier) {
                    SpriteGen.handleSpriteGen(this, gl4, bfwc, gao, possTemp, globalMatrix, oglf.getViewMtx(), rotation, getElapsedTimeInSecs(), (BFModifierSpriteGenModifier) modIF, dist, renderFromZlistSort);
                }
            }
        }
    }

    private void drawMultiPartGAO(GL4 gl4, BFGaoChunk gao) {
        TreeMap<Integer, BFKeyListElem> tm;
        BFGaoChunk boneGao;
        Matrix4x4 currBoneMatrix;

        if ((gao.getFlags() & 0x2) == 0) {
            return;
        }

        BFKeyListElem groupKLE = gao.getBonesGroupChunkInstanceKLE();
        if (groupKLE == null || groupKLE.getParseHandler() == null
                || !(groupKLE.getParseHandler() instanceof BFGRPContainerChunk)) {
            return;
        }

        BFGRPContainerChunk grpc = (BFGRPContainerChunk) groupKLE.getParseHandler();
        if (grpc == null) {
            return;
        }
        BFGRPChunk grp = grpc.getGRP();
        if (grp == null) {
            return;
        }

        ArrayList<TreeMap<Integer, BFKeyListElem>> grpl = grp.getGrpGaoList();
        if (grpl == null) {
            return;
        }

        int i;

        if (aniPlayingStartAction != null && animationPlayer != null && aniGao != null) {
            // HACK, transition broken: to ignore transition
            aniGao.resetAnimationPlayerSpecificVars();
            animationPlayer.setGaoAction(aniGao, aniPlayingStartAction, true);
            aniPlayingPrevTime = null;
            aniPlaying = true;
            aniPlayingStartAction = null;
        }

        if (aniPlaying && animationPlayer != null && aniGao != null) {
            if (aniPlayingPrevTime == null) {
                aniPlayingPrevTime = System.currentTimeMillis();
            }
            long currTime = System.currentTimeMillis();
            float diff = (currTime - aniPlayingPrevTime);
            aniPlayingPrevTime = currTime;
            animationPlayer.setTimeDiffBetweenLoops(diff / 1000.0f);
            animationPlayer.animationMainHandler(aniGao);
        }

        multiPartGaosToDisplay.clear();

        for (i = 0; i < grpl.size(); i++) {
            tm = grpl.get(i);
            boneGao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            currBoneMatrix = Skeleton.getShapeMatrixFromGaoByKey(gao, boneGao.getSelfKey());
            if (currBoneMatrix == null) {
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    System.out.println("BUG: currBoneMatrix is null for boneGao.");
                }
                continue;
            }

            // set unbound multipart gao parts to multipart gao container
            // game set this in gao init (at offset: 405EC0) -->
            if ((boneGao.getFlags() & 0x400000) == 0) {
                boneGao.setFlags(boneGao.getFlags() | 0x400000);
                if (i == 0 && (boneGao.getFlags() & 0x10000) == 0) {
                    boneGao.setFlags(boneGao.getFlags() | 0x10000);
                    boneGao.getGlobalMatrixSecond().set(Matrix4x4.getIdentityMatrix());
                }
                boneGao.setLocalMatrix(boneGao.getGlobalMatrixSecond());
                BFKeyListElem kle = new BFKeyListElem(gao.getSelfKey(), BFGaoChunk.class, false);
                kle.setParseHandler(gao);
                boneGao.setReferencePointGAOChunkKLE(kle);
                boneGao.setReferencePointGAOKey(gao.getSelfKey());
            }
            // <-- until this

            if (boneGao.getModifiersArray() != null) {
                ArrayList<BFChunkParseInterface> modArr = boneGao.getModifiersArray();
                for (BFChunkParseInterface modIF : modArr) {
                    if (modIF == null) {
                        continue;
                    }

                    // do not compute IK, if only one gao gets rendered
                    if (ikEnabled && modIF instanceof BFModifierGAOInverseKinematics) {
                        InverseKinematics.handleInverseKinematics((BFModifierGAOInverseKinematics) modIF, boneGao);
                    }
                    if (modIF instanceof BFModifierSnake) {
                        Snake.handleSnake((BFModifierSnake) modIF, boneGao, this);
                    }
                    if (modIF instanceof BFModifierLazyModifier) {
                        Lazy.handleLazy((BFModifierLazyModifier) modIF, boneGao, this);
                    }
                    if (XMECEnabled && modIF instanceof BFModifierXMECModifier) {
                        XMEC.handleXMEC((BFModifierXMECModifier) modIF, boneGao);
                    }
                }
            }

            Skeleton.computeBoneMatrixForAnimation(boneGao, gao, currBoneMatrix, true);

            if ((boneGao.getVisualFlags() & 0x20000000) == 0 && (boneGao.getVisualFlags() & 0x200000) == 0) {
                multiPartGaosToDisplay.add(boneGao);
            }
        }
        // Global matrix calc (works differently in original game, TODO: should be centralized)
        RenderUtils.calculateGlobalMatrixFromGAO(gao, globalMatrixRenderTemp);
        gao.getGlobalMatrix().set(globalMatrixRenderTemp);

        for (i = multiPartGaosToDisplay.size() - 1; i >= 0; --i) {
            boneGao = multiPartGaosToDisplay.get(i);
            if (boneGao.getBoneShapes() != null && boneGao.getBoneShapes().size() > 1) {
                backupMatrix.set(boneGao.getGlobalMatrix());
                boneGao.getGlobalMatrix().set(gao.getGlobalMatrix());
                globalMatrixRenderTemp.set(boneGao.getGlobalMatrix());
                boneGao.getGlobalMatrix().set(backupMatrix);
            } else {
                globalMatrixRenderTemp.set(boneGao.getGlobalMatrix());
            }
            drawOneGAO(gl4, boneGao, globalMatrixRenderTemp, false);
        }
    }

    public void drawSprite(GL4 gl4, BFGaoChunk gao, Vector3 poss[], int colors[], Matrix4x4 globalMatrix, Matrix4x4 viewMatrix, GeometrySprite sprites,
            boolean isMovingSprites, float elapsedTime, int offset) {
        float multiplier;
        Vector3 selectedVertex;
        long betweenSpritesMoveOffsetID;
        double betweenSpritesMoveOffset;
        long spriteMovingTimeOffsetID;
        float spriteFullMovingOffset;
        double spriteMovingTimeOffset;
        float cntF;
        int i = 0, j = 0, k = 0;

        BFGaoInnerChunkParser materialGroupIC = RenderUtils.getMaterialICFromGAO(gao);
        Vector3 spriteVertices[] = new Vector3[sprites.getSpElemNum() * 6]; // one sprite contains 2 triangles (=6 vertices)
        UV spriteUVs[] = new UV[sprites.getSpElemNum() * 6];
        int spriteColors[] = new int[sprites.getSpElemNum() * 6];

        // TODO: REALLY NASTY IMPLEMENTATION, should be simplified
        Matrix4x4 rot = new Matrix4x4(rotation);
        Matrix4x4.fixMatrix(rot);
        Matrix4x4.matrixMultiplyWithNormalized(spriteViewMatrixTmp2, globalMatrix, rot);
        Matrix4x4.matrixMultiplyWithNormalized(spriteViewMatrixTmp3, spriteViewMatrixTmp2, viewMatrix);
        spriteViewMatrixTmp2.set(spriteViewMatrixTmp3);

        float x1 = -(spriteViewMatrixTmp2.getM11() * sprites.getSpDimension1() * sprites.getSpDimension2());
        float y1 = -(spriteViewMatrixTmp2.getM21() * sprites.getSpDimension1() * sprites.getSpDimension2());
        float z1 = -(spriteViewMatrixTmp2.getM31() * sprites.getSpDimension1() * sprites.getSpDimension2());
        float x2 = spriteViewMatrixTmp2.getM12() * sprites.getSpDimension1();
        float y2 = spriteViewMatrixTmp2.getM22() * sprites.getSpDimension1();
        float z2 = spriteViewMatrixTmp2.getM32() * sprites.getSpDimension1();
        //TODO: set fill mode & cull mode
        cntF = 0.0f;
        if (sprites.getSpElemNum() > 0) {
            float v1x = -x1 - x2;
            float v1y = -y1 - y2;
            float v2x = x1 - x2;
            float v2y = y1 - y2;
            float v3x = x2 + x1;
            float v3y = y2 + y1;
            float v4x = x2 - x1;
            float v4y = y2 - y1;
            float v1z, v2z, v3z, v4z;
            do {
                cntF = cntF + 1.0f;
                spriteToDisplay.color = colors[sprites.getGeoSpriteElem()[i].getVerticesID1() + offset];
                multiplier = sprites.getGeoSpriteElem()[i].getMultiplier();
                selectedVertex = poss[sprites.getGeoSpriteElem()[i].getVerticesID1() + offset];
                spriteToDisplay.vec1X = v1x * multiplier + selectedVertex.getX();
                spriteToDisplay.vec1Y = v1y * multiplier + selectedVertex.getY();
                v1z = -z1 - z2;
                spriteToDisplay.vec1Z = v1z * multiplier + selectedVertex.getZ();
                spriteToDisplay.vec2X = v2x * multiplier + selectedVertex.getX();
                spriteToDisplay.vec2Y = v2y * multiplier + selectedVertex.getY();
                v2z = z1 - z2;
                spriteToDisplay.vec2Z = v2z * multiplier + selectedVertex.getZ();
                spriteToDisplay.vec3X = v3x * multiplier + selectedVertex.getX();
                spriteToDisplay.vec3Y = v3y * multiplier + selectedVertex.getY();
                v3z = z2 + z1;
                spriteToDisplay.vec3Z = v3z * multiplier + selectedVertex.getZ();
                spriteToDisplay.vec4X = v4x * multiplier + selectedVertex.getX();
                spriteToDisplay.vec4Y = v4y * multiplier + selectedVertex.getY();
                v4z = z2 - z1;
                spriteToDisplay.vec4Z = v4z * multiplier + selectedVertex.getZ();
                if (isMovingSprites) {
                    betweenSpritesMoveOffset = (cntF * 162.975) + RenderUtils.getUnknownMagic(); // 1024/2Pi
                    betweenSpritesMoveOffsetID = Double.doubleToLongBits(betweenSpritesMoveOffset);
                    betweenSpritesMoveOffsetID = betweenSpritesMoveOffsetID & 0x3ff;
                    betweenSpritesMoveOffset = RenderUtils.getDecalTable()[(int) betweenSpritesMoveOffsetID];
                    spriteMovingTimeOffset = 162.975f /* 1024/2Pi*/ * betweenSpritesMoveOffset * elapsedTime * 2.5f + RenderUtils.getUnknownMagic();
                    spriteMovingTimeOffsetID = Double.doubleToLongBits(spriteMovingTimeOffset);
                    spriteMovingTimeOffsetID = spriteMovingTimeOffsetID & 0x3ff;
                    spriteFullMovingOffset = (float) (betweenSpritesMoveOffset * RenderUtils.getDecalTable()[(int) spriteMovingTimeOffsetID] * 0.2f);
                    /* This was originally in game.
                    spriteToDisplay.vec1X = spriteToDisplay.vec1X + spriteFullMovingOffset;
                    spriteToDisplay.vec2X = spriteToDisplay.vec2X + spriteFullMovingOffset;
                     */
                    spriteToDisplay.vec3X = spriteToDisplay.vec3X + spriteFullMovingOffset;
                    spriteToDisplay.vec4X = spriteToDisplay.vec4X + spriteFullMovingOffset;
                }
                // make sprite triangles
                // 1.0f - x  for V param is for OpenGL - D3D difference
                spriteColors[j] = spriteToDisplay.color;
                spriteVertices[j] = new Vector3(spriteToDisplay.vec1X, spriteToDisplay.vec1Y, spriteToDisplay.vec1Z);
                spriteUVs[j++] = new UV(1.0f, 1.0f - 1.0f);
                spriteColors[j] = spriteToDisplay.color;
                spriteVertices[j] = new Vector3(spriteToDisplay.vec2X, spriteToDisplay.vec2Y, spriteToDisplay.vec2Z);
                spriteUVs[j++] = new UV(0.0f, 1.0f - 1.0f);
                spriteColors[j] = spriteToDisplay.color;
                spriteVertices[j] = new Vector3(spriteToDisplay.vec3X, spriteToDisplay.vec3Y, spriteToDisplay.vec3Z);
                spriteUVs[j++] = new UV(0.0f, 1.0f - 0.0f);
                spriteColors[j] = spriteToDisplay.color;
                spriteVertices[j] = new Vector3(spriteToDisplay.vec1X, spriteToDisplay.vec1Y, spriteToDisplay.vec1Z);
                spriteUVs[j++] = new UV(1.0f, 1.0f - 1.0f);
                spriteColors[j] = spriteToDisplay.color;
                spriteVertices[j] = new Vector3(spriteToDisplay.vec3X, spriteToDisplay.vec3Y, spriteToDisplay.vec3Z);
                spriteUVs[j++] = new UV(0.0f, 1.0f - 0.0f);
                spriteColors[j] = spriteToDisplay.color;
                spriteVertices[j] = new Vector3(spriteToDisplay.vec4X, spriteToDisplay.vec4Y, spriteToDisplay.vec4Z);
                spriteUVs[j++] = new UV(1.0f, 1.0f - 0.0f);
            } while (++i < sprites.getSpElemNum());
        }

        Vector3 normalsTmp[] = new Vector3[spriteVertices.length];
        int normalsNumTmp[] = new int[spriteVertices.length];
        for (i = 0; i < spriteVertices.length; i++) {
            normalsTmp[i] = new Vector3();
            normalsNumTmp[i] = 0;
        }
        GeometryTriangleChunk geotc[] = new GeometryTriangleChunk[1];
        geotc[0] = new GeometryTriangleChunk(spriteVertices.length / 3, sprites.getSpMaterialID(), 0, 0);
        int materialFlagsUnion = RenderUtils.getGAOUsedMaterialsFlagsUnion(geotc, materialGroupIC);
        GeometryTriangleData geotd;
        j = 0;
        k = 0;
        for (i = 0; i < spriteVertices.length / 3; i++) {
            geotd = new GeometryTriangleData((short) j++, (short) j++, (short) j++, (short) k++, (short) k++, (short) k++, 0, 0);
            geotc[0].addGeometryTriangleData(geotd);
        }

        Lights.setMatrices(globalMatrix, viewMatrix, rot);
        oglf.drawMultiMaterialTriangles(gl4, true, geotc,
                spriteVertices, spriteUVs, normalsTmp, normalsNumTmp, spriteColors,
                materialGroupIC, materialFlagsUnion, globalMatrix.getAsFloatArray(true, true), false, null, getElapsedTimeInSecs());
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        // for FPS display
        fpsCount++;
        if (fpsCount < 0) {
            fpsCount = 0;
        }

        if (renderingLastTimeForFps == 0.0f || (getElapsedTimeInSecs() - renderingLastTimeForFps) >= 1) {
            float fps = fpsCount / (getElapsedTimeInSecs() - renderingLastTimeForFps);
            renderingLastTimeForFps = getElapsedTimeInSecs();
            setWindowTitle(fps);
            fpsCount = 0;
        }
        // for FPS display end

        renderingCounter++;
        if (renderingCounter < 0) {
            renderingCounter = 0;
        }

        if (runApplyShapeOnThisGao != null) {
            Skeleton.applyShapeForGao(runApplyShapeOnThisGao);
            runApplyShapeOnThisGao = null;
        }

        GL4 gl4 = drawable.getGL().getGL4();
        // antialiasing
        if (isAntiAliasingEnabled && gl4.isExtensionAvailable("GL_ARB_multisample")) {
            gl4.glEnable(GL4.GL_MULTISAMPLE);
        } else {
            gl4.glDisable(GL4.GL_MULTISAMPLE);
        }
        gl4.glPolygonMode(GL4.GL_FRONT_AND_BACK, (isWireFrame ? GL4.GL_LINE : GL4.GL_FILL));
        gl4.glDepthMask(true);
        gl4.glClearColor(((((clearColor) >> 16) & 0xff) / 255.0f), ((((clearColor) >> 8) & 0xff) / 255.0f), (((clearColor) & 0xff) / 255.0f), 1f);
        gl4.glClearDepthf(1f);
        gl4.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        //set projection matrix
        FloatUtil.makePerspective(oglf.getProjectionMtx(), 0, true, Camera.getFOV(),
                windowSize.getWidth() / (float) windowSize.getHeight(), 0.1f, oglf.isClipControlExtensionSupported() ? 50000.0f : 5500.0f);

        // set view matrix
        cam.setViewMatrix(oglf.getViewMtx());

        // set model pre rotation
        FloatUtil.makeRotationEuler(rotation, 0, xAxisRotation, yAxisRotation, zAxisRotation);

        // compute camera world space for ObjectLookAtCamera modifier
        // -- compute real view matrix, TODO: REALLY NASTY IMPLEMENTATION, should be simplified
        FloatUtil.multMatrix(oglf.getViewMtx(), rotation, finalViewMatrixForCamera);
        cameraWorldSpace.set(finalViewMatrixForCamera, false);
        cameraWorldSpace = Camera.camInvert(null, cameraWorldSpace);

        switch (lightRenderingMode) {
            default:
            case LRM_REALTIME_ON:
                oglf.setDummyRealTimeLightingEnabled(false);
                Lights.setRealTimeLightingEnabled(true);
                break;
            case LRM_REALTIME_AND_DUMMY_OFF:
                oglf.setDummyRealTimeLightingEnabled(false);
                Lights.setRealTimeLightingEnabled(false);
                break;
            case LRM_ONLY_DUMMY_LIGHT_ON:
                oglf.setDummyRealTimeLightingEnabled(true);
                Lights.setRealTimeLightingEnabled(false);
                break;
        }

        Lights.addLightsFromGaoList(gaos, oglf);

        for (BFGaoChunk gao : gaos) {
            if (gao == null) {
                continue;
            }
            /* Black isle cutscene trigger bounding box display test
            if (gao.getSelfKey().equals(0xbf000975)) {
                RenderUtils.calculateGlobalMatrixFromGAO(gao, gao.getGlobalMatrix());
                DebugRenderFunctions.drawAABBox(this, gl4, gao.getBoundingBox1(), gao.getBoundingBox2(), gao.getGlobalMatrix(), 0xff663399, false);
            }
             */

            if (gao.isBoundingBoxDebugEnabled()) {
                DebugRenderFunctions.drawBoundingBox(this, gl4, gao, useGAOPosition, gaoGlobalMatrixBackup, GUIDefines.COLOR_BOUNDINGBOX_RENDER);
            }

            if (!gaoRenderingForcefully && (gao.getFlags() & 0x1) != 0) { // child elem               
                continue;
            }
            if (gaoRenderingForcefully && selectedSingleGao == null && (gao.getFlags() & 0x1) != 0) { // In world wiew don't want to render child elems. (they get rendered via container gaos)
                continue;
            }
            if (!gaoRenderingForcefully && ((gao.getVisualFlags() & 0x20000000) != 0
                    || (gao.getVisualFlags() & 0x200000) != 0)) { // invisible
                continue;
            }

            if (gao.getModifiersArray() != null) {
                ArrayList<BFChunkParseInterface> modArr = gao.getModifiersArray();
                for (BFChunkParseInterface modIF : modArr) {
                    if (modIF == null) {
                        continue;
                    }

                    if (modIF instanceof BFModifierObjectFakeLookAtCameraModifier) {
                        ObjectFakeLookAtCamera.handleObjectFakeLookAtCamera((BFModifierObjectFakeLookAtCameraModifier) modIF, gao, cameraWorldSpace.getPos());
                    }

                    if (modIF instanceof BFModifierGAOSpecialLookAt) {
                        SpecialLookAt.handleSpecialLookAt((BFModifierGAOSpecialLookAt) modIF, gao, cameraWorldSpace, this, windowSize.getWidth(), windowSize.getHeight());
                    }

                    // do not compute IK, if only one gao gets rendered
                    if (ikEnabled && modIF instanceof BFModifierGAOInverseKinematics) {
                        InverseKinematics.handleInverseKinematics((BFModifierGAOInverseKinematics) modIF, gao);
                    }
                    if (modIF instanceof BFModifierSnake) {
                        Snake.handleSnake((BFModifierSnake) modIF, gao, this);
                    }
                    if (modIF instanceof BFModifierLazyModifier) {
                        Lazy.handleLazy((BFModifierLazyModifier) modIF, gao, this);
                    }
                    if (XMECEnabled && modIF instanceof BFModifierXMECModifier) {
                        XMEC.handleXMEC((BFModifierXMECModifier) modIF, gao);
                    }
                }
            }

            if ((gao.getFlags() & 0x4000) != 0) {
                // Global matrix calc (works differently in original game, TODO: should be centralized)
                RenderUtils.calculateGlobalMatrixFromGAO(gao, gao.getGlobalMatrix());
                drawOneGAO(gl4, gao, gao.getGlobalMatrix(), false);
            } else if ((gao.getFlags() & 0x2) != 0) {
                drawMultiPartGAO(gl4, gao);
            }

            if (isCollMapDebugOn && (gao.getFlags() & 0x100) != 0) {
                RenderUtils.calculateGlobalMatrixFromGAO(gao, gao.getGlobalMatrix());
                DebugRenderFunctions.drawCollisionMap(this, gl4, gao, gao.getGlobalMatrix(), GUIDefines.COLOR_COLMAP_RENDER, false);
            }
            if (isCollInstanceDebugOn && (gao.getFlags() & 0x600) != 0) {
                RenderUtils.calculateGlobalMatrixFromGAO(gao, gao.getGlobalMatrix());
                DebugRenderFunctions.drawCollisionInstance(this, gl4, gao, gao.getGlobalMatrix(), GUIDefines.COLOR_COLINSTANCE_RENDER, false);
            }

            if (invisibleElemsDebugMode != 0) {
                if ((gao.getFlags() & 0x10) != 0) { // Light source boxes render
                    RenderUtils.calculateGlobalMatrixFromGAO(gao, gao.getGlobalMatrix());
                    DebugRenderFunctions.drawACube(this, gl4, gao.getGlobalMatrix(), GUIDefines.COLOR_LIGHTSOURCE_RENDER, invisibleElemsDebugMode == 1);
                }

                if (gao.getGeoChunkKLE() != null && gao.getGeoChunkKLE().getParseHandler() != null
                        && ((BFGaoInnerChunkParser) (gao.getGeoChunkKLE().getParseHandler())).
                                getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_CAMERA)) { // camera boxes render
                    RenderUtils.calculateGlobalMatrixFromGAO(gao, gao.getGlobalMatrix());
                    DebugRenderFunctions.drawACube(this, gl4, gao.getGlobalMatrix(), GUIDefines.COLOR_CAMERA_RENDER, invisibleElemsDebugMode == 1);
                }
            }
        }
        zListDrawAllPools(gl4);
        Lazy.variablesSetAfterRenderingCall();
    }

    private void zListDrawAllPools(GL4 gl4) {
        for (int i = 7; i >= 0; i--) {
            if (zListPool == null || zListPool[i] == null) {
                continue;
            }
            ZListNode[] nodes = zListPool[i].getNodes();
            if (nodes == null) {
                continue;
            }
            for (int j = 0; j < nodes.length; j++) {
                if (nodes[j] == null) {
                    continue;
                }
                ArrayList<ZListNodeElem> elems = nodes[j].getElems();
                if (elems == null) {
                    continue;
                }
                for (int k = 0; k < elems.size(); k++) {
                    ZListNodeElem ne = elems.get(k);
                    if (ne == null || ne.getGao() == null || ne.getMtx() == null) {
                        continue;
                    }
                    drawOneGAO(gl4, ne.getGao(), ne.getMtx(), true);
                }
            }
        }
        clearZLists();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL4 gl4 = drawable.getGL().getGL4();
        gl4.glViewport(x, y, width, height);
        windowSize.setWidth(width);
        windowSize.setHeight(height);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Renderer.glWindow.destroy();
            mainFrame.dispose();
            if (rendererControlsWindow != null) {
                rendererControlsWindow.dispose();
            }
        }
        if (currentlyManipluatedBoneGao == null) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                cam.forward();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                cam.backward();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                cam.strafeRight();
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                cam.strafeLeft();
            }
            if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                cam.strafeUp();
            }
            if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                cam.strafeDown();
            }
            if (e.getKeyCode() == KeyEvent.VK_R) {
                cam.reset();
                xAxisRotation = GUIDefines.RENDERER_X_AXIS_PREROTATION;
                yAxisRotation = GUIDefines.RENDERER_Y_AXIS_PREROTATION;
                zAxisRotation = GUIDefines.RENDERER_Z_AXIS_PREROTATION;
            }
            if (e.getKeyCode() == KeyEvent.VK_P) {
//                RenderUtils._dumpmtx(view);
                positionDialog.setGaoList(gaos);
                positionDialog.setVisible(true);
            }
            if (e.getKeyCode() == KeyEvent.VK_M) {
                setMorphEnabled(!getMorphEnabled());
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            isWireFrame = !isWireFrame;
        }
        if (e.getKeyCode() == KeyEvent.VK_L) {
            lightRenderingMode = ++lightRenderingMode % 3;
        }
        if (e.getKeyCode() == KeyEvent.VK_C) {
            oglf.setCullingEnabled(!oglf.isCullingEnabled());
        }
        if (e.getKeyCode() == KeyEvent.VK_F) {
            oglf.setFogRenderingEnabled(!oglf.isFogRenderingEnabled());
        }
        if (e.getKeyCode() == KeyEvent.VK_Y) { // Z in hungarian keyb
            zListSortEnabled = !zListSortEnabled;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            isAntiAliasingEnabled = !isAntiAliasingEnabled;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            invisibleElemsDebugMode = (++invisibleElemsDebugMode) % 3;
        }
        if (e.getKeyCode() == KeyEvent.VK_B) {
            isCollMapDebugOn = !isCollMapDebugOn;
        }
        if (e.getKeyCode() == KeyEvent.VK_N) {
            isCollInstanceDebugOn = !isCollInstanceDebugOn;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (selectedSingleGao == null) { // do not let to change it in single gao mode
                gaoRenderingForcefully = !gaoRenderingForcefully;
            }
        }

        /*
        // Test crane in factory boss level
        //BFGaoChunk gm = getGaoByKey(0x10000999);
        // Test XMEC cable on hangar level 
        // BFGaoChunk gm = getGaoByKey(0xa9004069);
        
        if (e.getKeyCode() == KeyEvent.VK_1) {
            gm.getGlobalMatrix().getPos().setX(gm.getGlobalMatrix().getPos().getX() + 0.5f);
        }
        if (e.getKeyCode() == KeyEvent.VK_2) {
            gm.getGlobalMatrix().getPos().setX(gm.getGlobalMatrix().getPos().getX() - 0.5f);
        }
        if (e.getKeyCode() == KeyEvent.VK_3) {
            gm.getGlobalMatrix().getPos().setY(gm.getGlobalMatrix().getPos().getY() + 0.5f);
        }
        if (e.getKeyCode() == KeyEvent.VK_4) {
            gm.getGlobalMatrix().getPos().setY(gm.getGlobalMatrix().getPos().getY() - 0.5f);
        }
        if (e.getKeyCode() == KeyEvent.VK_5) {
            gm.getGlobalMatrix().getPos().setZ(gm.getGlobalMatrix().getPos().getZ() + 0.5f);
        }
        if (e.getKeyCode() == KeyEvent.VK_6) {
            gm.getGlobalMatrix().getPos().setZ(gm.getGlobalMatrix().getPos().getZ() - 0.5f);
        }
         */
    }

    private BFGaoChunk getGaoByKey(int key) {
        BFGaoChunk g;
        Iterator i = gaos.iterator();
        while (i.hasNext()) {
            g = (BFGaoChunk) (i.next());
            if (g.getSelfKey().equals(key)) {
                return g;
            }
        }
        return null;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {
        prevX = null;
        prevY = null;
    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseMoved(MouseEvent me) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0
                || (e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0
                || (e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {
            if (prevX == null) {
                prevX = e.getX();
                prevY = e.getY();
            }
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                if (currentlyManipluatedBoneGao == null) {
                    cam.handleMouseWithLeftClick(((prevX - e.getX()) * translateFactor), ((prevY - e.getY()) * translateFactor));
                } else {
                    yAxisRotationBone -= (prevX - e.getX()) * 0.01f;
                    xAxisRotationBone -= (prevY - e.getY()) * 0.01f;
                    updateLocalBoneMatrix();
                }
            } else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                if (currentlyManipluatedBoneGao == null) {
                    cam.handleMouseWithRightClick(((prevX - e.getX()) * translateFactor), ((prevY - e.getY()) * translateFactor));
                } else {
                    xTranslateBone -= (prevX - e.getX()) * translateFactor;
                    yTranslateBone += (prevY - e.getY()) * translateFactor;
                    updateLocalBoneMatrix();
                }
            } else if ((e.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) {
                if (selectedSingleGao != null) {
                    yAxisRotation += (prevX - e.getX()) * 0.01f;
                    xAxisRotation -= (prevY - e.getY()) * 0.01f;
                }
            }
            prevX = e.getX();
            prevY = e.getY();
        }
    }

    @Override
    public void mouseWheelMoved(MouseEvent me) {
        if (currentlyManipluatedBoneGao == null) {
            float f[] = me.getRotation();
            if (f[1] > 0) {
                cam.backward();
            } else {
                cam.forward();
            }
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        if (currentlyManipluatedBoneGao != null) {
            restoreAllLocalMatrices();
        }
        // restore gao's original global matrix, if we are not using gao original position
        if (!useGAOPosition && gaoGlobalMatrixBackup != null) {
            gaos.get(0).getGlobalMatrix().set(gaoGlobalMatrixBackup);
            if ((gaos.get(0).getFlags() & 0x10000) != 0 || (gaos.get(0).getFlags() & 0x4000000) != 0) {
                gaos.get(0).getGlobalMatrixSecond().set(gaoGlobalMatrixBackup);
            }
            if ((gaos.get(0).getFlags() & 0x10000) != 0 && (gaos.get(0).getFlags() & 0x4000000) != 0) {
                gaos.get(0).getGlobalMatrixThird().set(gaoGlobalMatrixBackup);
            }
            // put back reference, if it has
            if (gaoPivotBackup != null) {
                gaos.get(0).setReferencePointGAOChunkKLE(gaoPivotBackup);
                gaos.get(0).setReferencePointGAOKey(gaoPivotKeyBackup);
                if (gaoLocalMatrixBackup != null) {
                    gaos.get(0).setLocalMatrix(gaoLocalMatrixBackup);
                }
                gaos.get(0).setFlags(gaos.get(0).getFlags() | 0x400000);
                gaoPivotBackup = null;
            }
        }
        currentlyManipluatedMultipartGao = null;
        currentlyManipluatedBoneGao = null;
        GL4 gl4 = drawable.getGL().getGL4();
        Renderer.animator.stop();
        oglf.destroy(gl4);
        parent.closeCallback();
    }

}
