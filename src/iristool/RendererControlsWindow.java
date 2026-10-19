/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool;

import BFDataFileLib.BFChunkParseInterface;
import BFDataFileLib.BFChunkParsers.Map.BFActionChunk;
import BFDataFileLib.BFChunkParsers.Map.BFActionListChunk;
import BFDataFileLib.BFChunkParsers.Map.BFAnimationShapeChunk;
import BFDataFileLib.BFChunkParsers.Map.BFEventsChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.BFWOWChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserMaterialGroup;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserParticleGenerator;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoMorphModifier;
import BFDataFileLib.BFKeyListElem;
import Common.DataType.ActionsElemStruct;
import Common.DataType.GeometrySprite;
import Common.DataType.GeometryTriangleChunk;
import Common.DataType.ModifierMorphShapeFormStruct;
import Common.DataType.ModifierMorphShapeSettingsContainerStruct;
import iristool.Engine.AnimationPlayer;
import iristool.Engine.Modifier;
import iristool.Engine.RenderUtils;
import iristool.Engine.Renderer;
import iristool.Engine.Skeleton;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author andra
 */
public final class RendererControlsWindow extends javax.swing.JDialog {

    private BFGaoChunk gao;
    private Renderer r;
    private BFWOWChunk bfwc;
    private JDialog parent;
    private JPopupMenu actionMenu;
    private boolean singlePartGao = false;
    private static int BS_HIDE = 0;
    private static int BS_SHOW = 1;
    private static int BS_SET = 2;

    private BFGaoChunk getSelectedBoneGao() {
        if (gao == null || (gao.getFlags() & 0x2) == 0) {
            return null;
        }
        int i = -1;
        int selectedElemIdx = lBones.getSelectedIndex();
        if (selectedElemIdx == -1) {
            return null;
        }
        Iterator it = RenderUtils.getGRPLForGao(gao).iterator();
        while (it.hasNext()) {
            i++;
            TreeMap<Integer, BFKeyListElem> tm = (TreeMap<Integer, BFKeyListElem>) it.next();
            BFGaoChunk boneGao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            if (i == selectedElemIdx) {
                return boneGao;
            }
        }
        return null;
    }

    private BFActionChunk getActionFromGao(BFGaoChunk gao, int actionID) {
        if (gao != null) {
            if ((gao.getFlags() & 0x2) == 0) {
                return null;
            }
            if (gao.getActionListChunkKLE() != null && gao.getActionListChunkKLE().getParseHandler() != null) {
                BFActionListChunk aclc = (BFActionListChunk) (gao.getActionListChunkKLE().getParseHandler());
                if (aclc.getActionsKLE() == null || aclc.getActionsKLE().size() <= actionID) {
                    return null;
                }
                BFKeyListElem ackle = aclc.getActionsKLE().get(actionID);
                if (ackle == null || ackle.getParseHandler() == null) {
                    return null;
                }
                return (BFActionChunk) (ackle.getParseHandler());
            }
        }
        return null;
    }

    private BFEventsChunk getActionElemFromAction(BFActionChunk ac, int actionElemID) {
        if (ac.getEventsChunksKLE() == null || ac.getEventsChunksKLE().size() <= actionElemID) {
            return null;
        }
        BFKeyListElem evkle = ac.getEventsChunksKLE().get(actionElemID);
        if (evkle == null || evkle.getParseHandler() == null) {
            return null;
        }
        return (BFEventsChunk) (evkle.getParseHandler());
    }

    public void populateActionList(BFGaoChunk gao) {
        lmActions.clear();
        if (gao != null) {
            if ((gao.getFlags() & 0x2) == 0) {
                return;
            }
            System.out.println("kle: "+gao.getActionListChunkKLE());
            if (gao.getActionListChunkKLE() != null && gao.getActionListChunkKLE().getParseHandler() != null) {
                BFActionListChunk aclc = (BFActionListChunk) (gao.getActionListChunkKLE().getParseHandler());
                if (aclc.getActionsKLE() != null) {
                    Iterator it = aclc.getActionsKLE().iterator();
                    String s;
                    while (it.hasNext()) {
                        BFKeyListElem bke = (BFKeyListElem) (it.next());
                        if (bke == null) {
                            s = "0";
                        } else {
                            BFActionChunk ac = (BFActionChunk) bke.getParseHandler();
                            s = (ac != null ? String.format("Action Key: %x, Loop start elem: %d", ac.getSelfKey(), ac.getLoopStartElem()) : "0");
                        }
                        lmActions.addElement(s);
                    }
                }
            }
        }
    }

    /**
     * Creates new form BonePositioner
     *
     * @param parent
     * @param bfwc
     * @param gao
     * @param r
     * @param modal
     */
    public RendererControlsWindow(JDialog parent, BFWOWChunk bfwc, BFGaoChunk gao, Renderer r, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.gao = gao;
        this.r = r;
        this.bfwc = bfwc;
        initComponents();
        setPositionUpdateModeValues(true);

        // ACTION CONTEXT MENU
        actionMenu = new JPopupMenu("actionmenu");
        JMenuItem gotoID = new JMenuItem("Goto ID");
        actionMenu.add(gotoID);
        gotoID.addActionListener((ActionEvent ae) -> {
            String s = JOptionPane.showInputDialog(RendererControlsWindow.this, "Please type ID in decimal format (range: 0-" + (lmActions.size() - 1) + "):", "Goto ID", JOptionPane.PLAIN_MESSAGE);
            if (s == null || s.trim().isEmpty() || lActions.getSelectedIndex() == -1 || lmActions.isEmpty()) {
                return;
            }
            try {
                int id = Integer.parseInt(s);
                if (id >= lmActions.size() || id < 0) {
                    JOptionPane.showMessageDialog(RendererControlsWindow.this, "Invalid ID.", "Goto ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                lActions.setSelectedIndex(id);
                lActions.ensureIndexIsVisible(id);
            } catch (NumberFormatException ne) {
            }
        });
        // ACTION CONTEXT MENU END

        setEnableGeometryMorphControls(false);
        setBoneManipulatorAndActionPlayerControls(false);
        tpControls.setSelectedComponent(pActionPlayer);
        singlePartGao = true;
        if (gao != null) {
            if ((gao.getFlags() & 0x2) == 0) {
                jLabel5.setVisible(false); // hide unnecessary hint
                setGeometryMorphElems(gao);
                setGeometryTriangleChunksAndSpritesForMaterialInfo(gao);
                setEnableParticleControls(false);
                setParticleSettingsByGao(gao);
                return;
            }

            ArrayList grpl = RenderUtils.getGRPLForGao(gao);

            if (grpl != null) {
                Iterator it = grpl.iterator();
                while (it.hasNext()) {
                    TreeMap<Integer, BFKeyListElem> tm = (TreeMap<Integer, BFKeyListElem>) it.next();
                    BFGaoChunk boneGao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
                    lmBones.addElement(boneGao.getGaoName());
                }
                populateActionList(gao);
                setBoneManipulatorAndActionPlayerControls(true);
                singlePartGao = false;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btgPUM = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        btResetBoneToDefault = new javax.swing.JButton();
        btSetBoneToManipulate = new javax.swing.JButton();
        btManipluteCamera = new javax.swing.JButton();
        btResetAllBonesToDefault = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lmBones = new DefaultListModel<String>();
        lBones = new javax.swing.JList<String>();
        tpControls = new javax.swing.JTabbedPane();
        pBoneShape = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        lbmBoneShape = new DefaultListModel<String>();
        lbBoneShape = new javax.swing.JList<>();
        jLabel6 = new javax.swing.JLabel();
        btBoneShapeSet = new javax.swing.JButton();
        btBoneShapeSet1 = new javax.swing.JButton();
        btBoneShapeMakeVisible = new javax.swing.JButton();
        pMaterialInfo = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        tpMaterialInfo = new javax.swing.JTabbedPane();
        pMaterialInfoTC = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        lbmTriangleChunk = new DefaultListModel<String>();
        lbTriangleChunk = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        taMaterialInfo = new javax.swing.JTextArea();
        cbTriangleChunkRenderingEnabled = new javax.swing.JCheckBox();
        pMaterialInfoSP = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        lbmSprites = new DefaultListModel<String>();
        lbSprites = new javax.swing.JList<>();
        jLabel10 = new javax.swing.JLabel();
        pActionPlayer = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lmActions = new DefaultListModel<String>();
        lActions = new javax.swing.JList<String>();
        btPlayStop = new javax.swing.JButton();
        btEventViewer = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btAttachActionKit = new javax.swing.JButton();
        cbAniDebug = new javax.swing.JCheckBox();
        pPositionUpdatemode = new javax.swing.JPanel();
        rbPUMAll = new javax.swing.JRadioButton();
        rbPUMNoGlobal = new javax.swing.JRadioButton();
        rbPUMNone = new javax.swing.JRadioButton();
        btResetGlobalPos = new javax.swing.JButton();
        btTrkOpenDir = new javax.swing.JButton();
        pGeometryMorph = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lbmGMGroup = new DefaultListModel<String>();
        lbGMGroup = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        lbmGMGroupElems = new DefaultListModel<String>();
        lbGMGroupElems = new javax.swing.JList<>();
        btSetElem = new javax.swing.JButton();
        btUnsetElem = new javax.swing.JButton();
        tfUnkF1 = new javax.swing.JTextField();
        tfUnkF2 = new javax.swing.JTextField();
        btGMSet = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbmModifierID = new DefaultComboBoxModel();
        cbModifierID = new javax.swing.JComboBox<>();
        lbAffectedVerticesNum = new javax.swing.JLabel();
        pParticles = new javax.swing.JPanel();
        tfParticlesFreq = new javax.swing.JTextField();
        lbFreq = new javax.swing.JLabel();
        cbParticlesEnabled = new javax.swing.JCheckBox();
        btPartFreqSet = new javax.swing.JButton();
        btPartResetToDefaults = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Renderer Controls");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Bone Selector");

        btResetBoneToDefault.setText("Reset bone to default value");
        btResetBoneToDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btResetBoneToDefaultActionPerformed(evt);
            }
        });

        btSetBoneToManipulate.setText("Select bone to manipulate");
        btSetBoneToManipulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetBoneToManipulateActionPerformed(evt);
            }
        });

        btManipluteCamera.setText("Camera manipulation");
        btManipluteCamera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btManipluteCameraActionPerformed(evt);
            }
        });

        btResetAllBonesToDefault.setText("Reset all bones to default values");
        btResetAllBonesToDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btResetAllBonesToDefaultActionPerformed(evt);
            }
        });

        lBones.setModel(lmBones);
        lBones.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lBones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lBonesMouseClicked(evt);
            }
        });
        lBones.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lBonesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lBones);

        lbBoneShape.setModel(lbmBoneShape);
        jScrollPane5.setViewportView(lbBoneShape);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Bone Shape");

        btBoneShapeSet.setText("Set");
        btBoneShapeSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBoneShapeSetActionPerformed(evt);
            }
        });

        btBoneShapeSet1.setText("Hide");
        btBoneShapeSet1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBoneShapeSet1ActionPerformed(evt);
            }
        });

        btBoneShapeMakeVisible.setText("Make visible (shape #0)");
        btBoneShapeMakeVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBoneShapeMakeVisibleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pBoneShapeLayout = new javax.swing.GroupLayout(pBoneShape);
        pBoneShape.setLayout(pBoneShapeLayout);
        pBoneShapeLayout.setHorizontalGroup(
            pBoneShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pBoneShapeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pBoneShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                    .addGroup(pBoneShapeLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pBoneShapeLayout.createSequentialGroup()
                        .addComponent(btBoneShapeSet, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btBoneShapeMakeVisible)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btBoneShapeSet1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pBoneShapeLayout.setVerticalGroup(
            pBoneShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pBoneShapeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pBoneShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btBoneShapeSet, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btBoneShapeSet1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btBoneShapeMakeVisible, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        tpControls.addTab("Bone Shape", pBoneShape);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Material Info");

        jLabel9.setText("Triangle Chunk:");

        lbTriangleChunk.setModel(lbmTriangleChunk);
        lbTriangleChunk.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lbTriangleChunkValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(lbTriangleChunk);

        jLabel8.setText("Material details:");

        taMaterialInfo.setEditable(false);
        taMaterialInfo.setColumns(20);
        taMaterialInfo.setLineWrap(true);
        taMaterialInfo.setRows(5);
        jScrollPane7.setViewportView(taMaterialInfo);

        cbTriangleChunkRenderingEnabled.setText("Selected triangle chunk rendering enabled");
        cbTriangleChunkRenderingEnabled.setEnabled(false);
        cbTriangleChunkRenderingEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTriangleChunkRenderingEnabledActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pMaterialInfoTCLayout = new javax.swing.GroupLayout(pMaterialInfoTC);
        pMaterialInfoTC.setLayout(pMaterialInfoTCLayout);
        pMaterialInfoTCLayout.setHorizontalGroup(
            pMaterialInfoTCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMaterialInfoTCLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pMaterialInfoTCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                    .addComponent(jScrollPane7)
                    .addGroup(pMaterialInfoTCLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pMaterialInfoTCLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbTriangleChunkRenderingEnabled)))
                .addContainerGap())
        );
        pMaterialInfoTCLayout.setVerticalGroup(
            pMaterialInfoTCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMaterialInfoTCLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pMaterialInfoTCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cbTriangleChunkRenderingEnabled))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addContainerGap())
        );

        tpMaterialInfo.addTab("TriangleChunks", pMaterialInfoTC);

        lbSprites.setModel(lbmSprites);
        jScrollPane8.setViewportView(lbSprites);

        jLabel10.setText("<html>As far as I know, this is ununsed in game. SpriteCount is 0 everywhere.<br/>SpriteGenModifier creates sprites dynamically.</html>");

        javax.swing.GroupLayout pMaterialInfoSPLayout = new javax.swing.GroupLayout(pMaterialInfoSP);
        pMaterialInfoSP.setLayout(pMaterialInfoSPLayout);
        pMaterialInfoSPLayout.setHorizontalGroup(
            pMaterialInfoSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMaterialInfoSPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pMaterialInfoSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addGroup(pMaterialInfoSPLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pMaterialInfoSPLayout.setVerticalGroup(
            pMaterialInfoSPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMaterialInfoSPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tpMaterialInfo.addTab("Sprites", pMaterialInfoSP);

        javax.swing.GroupLayout pMaterialInfoLayout = new javax.swing.GroupLayout(pMaterialInfo);
        pMaterialInfo.setLayout(pMaterialInfoLayout);
        pMaterialInfoLayout.setHorizontalGroup(
            pMaterialInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMaterialInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pMaterialInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpMaterialInfo)
                    .addGroup(pMaterialInfoLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pMaterialInfoLayout.setVerticalGroup(
            pMaterialInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMaterialInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tpMaterialInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        tpControls.addTab("Material Info", pMaterialInfo);

        lActions.setModel(lmActions);
        lActions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lActions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lActionsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lActions);

        btPlayStop.setText("Play");
        btPlayStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPlayStopActionPerformed(evt);
            }
        });

        btEventViewer.setText("EV");
        btEventViewer.setToolTipText("Display selected action tracks in event viewer");
        btEventViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEventViewerActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Action Player");

        btAttachActionKit.setText("Attach Action Kit");
        btAttachActionKit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAttachActionKitActionPerformed(evt);
            }
        });

        cbAniDebug.setText("Animation Player debug (console)");

        pPositionUpdatemode.setBorder(javax.swing.BorderFactory.createTitledBorder("Position update mode"));

        btgPUM.add(rbPUMAll);
        rbPUMAll.setSelected(true);
        rbPUMAll.setText("All");
        rbPUMAll.setToolTipText("For example: When a model walks, it will walk out of camera's vision eventually.");
        rbPUMAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPUMAllActionPerformed(evt);
            }
        });

        btgPUM.add(rbPUMNoGlobal);
        rbPUMNoGlobal.setText("Excluding global pos");
        rbPUMNoGlobal.setToolTipText("For example: When a model walks, it's position will reset to default when one animation loop finished.");
        rbPUMNoGlobal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPUMNoGlobalActionPerformed(evt);
            }
        });

        btgPUM.add(rbPUMNone);
        rbPUMNone.setText("None");
        rbPUMNone.setToolTipText("For example: When a model walks, it will walk in one place.");
        rbPUMNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPUMNoneActionPerformed(evt);
            }
        });

        btResetGlobalPos.setText("Reset Global Pos");
        btResetGlobalPos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btResetGlobalPosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pPositionUpdatemodeLayout = new javax.swing.GroupLayout(pPositionUpdatemode);
        pPositionUpdatemode.setLayout(pPositionUpdatemodeLayout);
        pPositionUpdatemodeLayout.setHorizontalGroup(
            pPositionUpdatemodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPositionUpdatemodeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pPositionUpdatemodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btResetGlobalPos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pPositionUpdatemodeLayout.createSequentialGroup()
                        .addGroup(pPositionUpdatemodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbPUMAll)
                            .addComponent(rbPUMNoGlobal)
                            .addComponent(rbPUMNone))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pPositionUpdatemodeLayout.setVerticalGroup(
            pPositionUpdatemodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPositionUpdatemodeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbPUMAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbPUMNoGlobal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbPUMNone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(btResetGlobalPos)
                .addContainerGap())
        );

        btTrkOpenDir.setText("Open TRL file");
        btTrkOpenDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btTrkOpenDirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pActionPlayerLayout = new javax.swing.GroupLayout(pActionPlayer);
        pActionPlayer.setLayout(pActionPlayerLayout);
        pActionPlayerLayout.setHorizontalGroup(
            pActionPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pActionPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pActionPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pActionPlayerLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbAniDebug, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btAttachActionKit, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pActionPlayerLayout.createSequentialGroup()
                        .addComponent(btPlayStop, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btEventViewer, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pActionPlayerLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pActionPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pActionPlayerLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pPositionUpdatemode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(pActionPlayerLayout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(btTrkOpenDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        pActionPlayerLayout.setVerticalGroup(
            pActionPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pActionPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pActionPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbAniDebug)
                    .addComponent(btAttachActionKit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pActionPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                    .addGroup(pActionPlayerLayout.createSequentialGroup()
                        .addComponent(btTrkOpenDir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pPositionUpdatemode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pActionPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btEventViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btPlayStop, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        tpControls.addTab("Action Player", pActionPlayer);

        pGeometryMorph.setEnabled(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Geometry Morph");

        lbGMGroup.setModel(lbmGMGroup);
        lbGMGroup.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lbGMGroupValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(lbGMGroup);

        lbGMGroupElems.setModel(lbmGMGroupElems);
        lbGMGroupElems.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lbGMGroupElemsValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(lbGMGroupElems);

        btSetElem.setText("Set elem");
        btSetElem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetElemActionPerformed(evt);
            }
        });

        btUnsetElem.setText("Unset Elem");
        btUnsetElem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUnsetElemActionPerformed(evt);
            }
        });

        tfUnkF1.setText("1.0");

        tfUnkF2.setText("1.0");

        btGMSet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btGMSet.setText("Set");
        btGMSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGMSetActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("F1 | F2");

        jLabel5.setText("Note: Select bone above at first. ");

        cbModifierID.setModel(cbmModifierID);
        cbModifierID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbModifierIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pGeometryMorphLayout = new javax.swing.GroupLayout(pGeometryMorph);
        pGeometryMorph.setLayout(pGeometryMorphLayout);
        pGeometryMorphLayout.setHorizontalGroup(
            pGeometryMorphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGeometryMorphLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pGeometryMorphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(pGeometryMorphLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbModifierID, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addGroup(pGeometryMorphLayout.createSequentialGroup()
                        .addGroup(pGeometryMorphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbAffectedVerticesNum, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pGeometryMorphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btSetElem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btUnsetElem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pGeometryMorphLayout.createSequentialGroup()
                                .addComponent(tfUnkF1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfUnkF2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1)
                            .addComponent(btGMSet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pGeometryMorphLayout.setVerticalGroup(
            pGeometryMorphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGeometryMorphLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pGeometryMorphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(cbModifierID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pGeometryMorphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pGeometryMorphLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbAffectedVerticesNum, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pGeometryMorphLayout.createSequentialGroup()
                        .addComponent(btSetElem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btUnsetElem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pGeometryMorphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfUnkF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfUnkF2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btGMSet, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tpControls.addTab("Geometry Morph", pGeometryMorph);

        lbFreq.setText("Frequency:");

        cbParticlesEnabled.setText("Enabled");
        cbParticlesEnabled.setToolTipText("Add flags 0x40, remove 0x80");
        cbParticlesEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbParticlesEnabledActionPerformed(evt);
            }
        });

        btPartFreqSet.setText("Set");
        btPartFreqSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPartFreqSetActionPerformed(evt);
            }
        });

        btPartResetToDefaults.setText("Reset values to defaults");
        btPartResetToDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPartResetToDefaultsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pParticlesLayout = new javax.swing.GroupLayout(pParticles);
        pParticles.setLayout(pParticlesLayout);
        pParticlesLayout.setHorizontalGroup(
            pParticlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pParticlesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pParticlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pParticlesLayout.createSequentialGroup()
                        .addComponent(cbParticlesEnabled)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pParticlesLayout.createSequentialGroup()
                        .addComponent(lbFreq)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfParticlesFreq, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btPartFreqSet)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
                        .addComponent(btPartResetToDefaults)))
                .addContainerGap())
        );
        pParticlesLayout.setVerticalGroup(
            pParticlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pParticlesLayout.createSequentialGroup()
                .addComponent(cbParticlesEnabled)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pParticlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFreq)
                    .addComponent(tfParticlesFreq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPartFreqSet)
                    .addComponent(btPartResetToDefaults))
                .addContainerGap(273, Short.MAX_VALUE))
        );

        tpControls.addTab("Particles", pParticles);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tpControls)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btResetAllBonesToDefault, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btSetBoneToManipulate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                            .addComponent(btResetBoneToDefault, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btManipluteCamera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(192, 192, 192)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btSetBoneToManipulate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btResetBoneToDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btResetAllBonesToDefault))
                    .addComponent(btManipluteCamera, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tpControls, javax.swing.GroupLayout.PREFERRED_SIZE, 349, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(49, 49, 49)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(467, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btManipluteCameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btManipluteCameraActionPerformed
        r.setCurrentlyManipulatedBoneGao(null);
    }//GEN-LAST:event_btManipluteCameraActionPerformed

    private void btSetBoneToManipulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetBoneToManipulateActionPerformed
        BFGaoChunk bgao = getSelectedBoneGao();
        if (bgao == null) {
            return;
        }
        r.setCurrentlyManipulatedBoneGao(bgao);
    }//GEN-LAST:event_btSetBoneToManipulateActionPerformed

    private void btResetBoneToDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btResetBoneToDefaultActionPerformed
        r.restoreBoneGaoLocalMatrix(getSelectedBoneGao());
    }//GEN-LAST:event_btResetBoneToDefaultActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        r.restoreAllLocalMatrices();
        r.setCurrentlyManipulatedBoneGao(null);
    }//GEN-LAST:event_formWindowClosing

    private void btResetAllBonesToDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btResetAllBonesToDefaultActionPerformed
        r.restoreAllLocalMatrices();
    }//GEN-LAST:event_btResetAllBonesToDefaultActionPerformed

    private void setPositionUpdateModeValues(boolean enabled) {
        rbPUMAll.setEnabled(enabled);
        rbPUMNoGlobal.setEnabled(enabled);
        rbPUMNone.setEnabled(enabled);
        btResetGlobalPos.setEnabled(enabled);
        int apm = r.getAnimGaoPositionUpdateMode();
        switch (apm) {
            case AnimationPlayer.POSITIONUPDATE_ALL:
                rbPUMAll.setSelected(true);
                break;
            case AnimationPlayer.POSITIONUPDATE_NO_GLOBAL:
                rbPUMNoGlobal.setSelected(true);
                break;
            case AnimationPlayer.POSITIONUPDATE_NONE:
                rbPUMNone.setSelected(true);
                break;
        }
    }

    private void btPlayStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPlayStopActionPerformed
        if (r.isAniPlaying()) {
            btPlayStop.setText("Play");
            r.setAniPlaying(false);
            r.restoreAllLocalMatrices();
            setPositionUpdateModeValues(true);
            return;
        }
        if (lActions.getSelectedIndex() != -1) {
            btPlayStop.setText("Stop");
            r.setAnimationDebug(cbAniDebug.isSelected());
            r.startAniPlaying(lActions.getSelectedIndex());
            setPositionUpdateModeValues(false);
        }
    }//GEN-LAST:event_btPlayStopActionPerformed

    private void btAttachActionKitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAttachActionKitActionPerformed
        ActionKitGaoSelectorWindow aksw = new ActionKitGaoSelectorWindow(RendererControlsWindow.this, true, bfwc, r);
        aksw.setVisible(true);
    }//GEN-LAST:event_btAttachActionKitActionPerformed

    private void btEventViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEventViewerActionPerformed
        if (lActions.getSelectedIndex() != -1) {
            BFActionChunk ac = getActionFromGao(gao, lActions.getSelectedIndex());
            if (ac == null || ac.getEventsChunksKLE() == null) {
                return;
            }
            String ret = JOptionPane.showInputDialog(this, "Please input, which elem should we display. (Valid range: 0-" + (ac.getEventsChunksKLE().size() - 1) + ")");
            if (ret == null) {
                return;
            }
            int num;
            try {
                num = Integer.parseInt(ret);
                if (num < 0 || num >= ac.getEventsChunksKLE().size()) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                return;
            }

            BFEventsChunk ec = getActionElemFromAction(ac, num);
            if (ec == null) {
                return;
            }

            EventViewer ev = new EventViewer(this, true, ec.getEventTracks(), gao.getGaoName());
            ev.setVisible(true);
        }
    }//GEN-LAST:event_btEventViewerActionPerformed

    private void setEnableGeometryMorphControls(boolean b) {
        cbModifierID.setEnabled(b);
        lbGMGroup.setEnabled(b);
        lbGMGroupElems.setEnabled(b);
        btSetElem.setEnabled(b);
        btUnsetElem.setEnabled(b);
        tfUnkF1.setEnabled(b);
        tfUnkF2.setEnabled(b);
        btGMSet.setEnabled(b);
    }

    private void setBoneManipulatorAndActionPlayerControls(boolean b) {
        lBones.setEnabled(b);
        btSetBoneToManipulate.setEnabled(b);
        btResetBoneToDefault.setEnabled(b);
        btResetAllBonesToDefault.setEnabled(b);
        btManipluteCamera.setEnabled(b);
        cbAniDebug.setEnabled(b);
        btAttachActionKit.setEnabled(b);
        lActions.setEnabled(b);
        btPlayStop.setEnabled(b);
        btEventViewer.setEnabled(b);
    }

    private void clearGeometryMorphControls() {
        lbAffectedVerticesNum.setText("");
        cbmModifierID.removeAllElements();
        lbmGMGroup.removeAllElements();
        lbmGMGroupElems.removeAllElements();
        tfUnkF1.setText("1.0");
        tfUnkF2.setText("1.0");
        setEnableGeometryMorphControls(false);
    }

    private void setGeometryMorphModifierControls(ArrayList<BFChunkParseInterface> modList, int id) {
        lbmGMGroup.removeAllElements();
        BFModifierGeoMorphModifier mod = (BFModifierGeoMorphModifier) (Modifier.getModifierByType(modList, BFModifierGeoMorphModifier.class, id));
        if (mod == null) {
            return;
        }
        int i = 0;
        for (ModifierMorphShapeSettingsContainerStruct mms2 : mod.getMorphShapeSettingsContainerStructs()) {
            lbmGMGroup.addElement("#" + i++);
        }
        lbGMGroupElems.clearSelection();
        lbmGMGroupElems.removeAllElements();
        setEnableGeometryMorphControls(true);
    }

    private void setGeometryMorphElems(BFGaoChunk gao) {
        clearGeometryMorphControls();
        if (gao == null || gao.getModifiersArray() == null) {
            return;
        }
        int cnt = Modifier.countModifiersByType(gao.getModifiersArray(), BFModifierGeoMorphModifier.class);
        if (cnt == 0) {
            return;
        }
        for (int i = 0; i < cnt; i++) {
            cbmModifierID.addElement("Modifier #" + i);
        }
        setGeometryMorphModifierControls(gao.getModifiersArray(), 0);
    }

    private void getSetBoneShape(BFGaoChunk bgao, boolean set, int mode) {
        if (bgao.getGroupChunkKLE() != null && bgao.getGroupChunkKLE().getParseHandler() != null) {
            BFGRPContainerChunk grpcc = (BFGRPContainerChunk) (bgao.getGroupChunkKLE().getParseHandler());
            BFGRPChunk grpc = grpcc.getGRP();
            if (grpc == null) {
                return;
            }

            ArrayList<TreeMap<Integer, BFKeyListElem>> grpelems = grpc.getGrpGaoList();
            if (grpelems == null) {
                return;
            }

            if (gao.getAnimationShapeChunkInstanceSpecificKLE() != null && gao.getAnimationShapeChunkInstanceSpecificKLE().getParseHandler() != null) {
                BFAnimationShapeChunk asc = (BFAnimationShapeChunk) (gao.getAnimationShapeChunkInstanceSpecificKLE().getParseHandler());
                if (gao.getBonesGroupChunkInstanceKLE() == null || gao.getBonesGroupChunkInstanceKLE().getParseHandler() == null) {
                    return;
                }

                BFGRPContainerChunk multipartGrpc = (BFGRPContainerChunk) (gao.getBonesGroupChunkInstanceKLE().getParseHandler());
                if (multipartGrpc.getGRP() == null || multipartGrpc.getGRP().getGrpGaoList() == null) {
                    return;
                }
                ArrayList<TreeMap<Integer, BFKeyListElem>> multipartGrpElems = multipartGrpc.getGRP().getGrpGaoList();

                for (int i = 0; i < multipartGrpElems.size(); i++) {
                    if (((BFKeyListElem) (multipartGrpElems.get(i).firstEntry().getValue())).getParseHandler() == bgao) {
                        Integer id = Skeleton.findShapeElemOffsetByBoneID(asc, i);
                        if (id != null && asc.getGroupElemIDArray() != null && asc.getGroupElemIDArray().length > id) {
                            if (!set) {
                                if (asc.getGroupElemIDArray()[id] >= 0) {
                                    lbBoneShape.setSelectedIndex(asc.getGroupElemIDArray()[id]);
                                }
                            } else {
                                if (mode == BS_HIDE) {
                                    asc.getGroupElemIDArray()[id] = (byte) -3;
                                } else if (mode == BS_SHOW) {
                                    asc.getGroupElemIDArray()[id] = (byte) -2;
                                } else {
                                    if (lbBoneShape.getSelectedIndex() != -1 && lbBoneShape.getSelectedIndex() < grpelems.size()) {
                                        asc.getGroupElemIDArray()[id] = (byte) (lbBoneShape.getSelectedIndex());
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private GeometryTriangleChunk[] getGTCFromGao(BFGaoChunk bgao) {
        if (bgao != null && bgao.getGeoChunkKLE() != null && bgao.getGeoChunkKLE().getParseHandler() != null) {

            BFGaoInnerChunkParser gicp = RenderUtils.getGeometryICTFromLODGAO(bgao);

            if (gicp.getGaoInnerType() != BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY) {
                return null;
            }

            GAOInnerParserGeometry geo = (GAOInnerParserGeometry) (gicp.getGaoInner());
            if (geo != null && geo.getGeometryTriangleChunks() != null) {
                return geo.getGeometryTriangleChunks();
            }
        }
        return null;
    }

    private GeometrySprite[] getGSFromGao(BFGaoChunk bgao) {
        if (bgao != null && bgao.getGeoChunkKLE() != null && bgao.getGeoChunkKLE().getParseHandler() != null) {

            BFGaoInnerChunkParser gicp = RenderUtils.getGeometryICTFromLODGAO(bgao);

            if (gicp.getGaoInnerType() != BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY) {
                return null;
            }

            GAOInnerParserGeometry geo = (GAOInnerParserGeometry) (gicp.getGaoInner());
            if (geo != null && geo.getGeometrySprites() != null) {
                return geo.getGeometrySprites();
            }
        }
        return null;
    }

    private void setGeometryTriangleChunksAndSpritesForMaterialInfo(BFGaoChunk gao) {
        GeometryTriangleChunk geoTC[];
        GeometrySprite geoSP[];
        int i = 0;
        if ((geoTC = getGTCFromGao(gao)) != null) {
            for (GeometryTriangleChunk gtc : geoTC) {
                lbmTriangleChunk.addElement("#" + ++i + " " + gtc.toString());
            }
        }
        if ((geoSP = getGSFromGao(gao)) != null) {
            for (GeometrySprite gse : geoSP) {
                lbmSprites.addElement("#" + ++i + " " + gse.toString());
            }
        }
    }

    private void setEnableParticleControls(boolean b) {
        if (!b) {
            cbParticlesEnabled.setSelected(false);
            tfParticlesFreq.setText("0.0");
        }
        cbParticlesEnabled.setEnabled(b);
        tfParticlesFreq.setEnabled(b);
    }

    private void setParticleSettingsByGao(BFGaoChunk bgao) {
        if (bgao != null && bgao.getGeoChunkKLE() != null && bgao.getGeoChunkKLE() != null && bgao.getGeoChunkKLE().getParseHandler() != null) {
            BFGaoInnerChunkParser icp = (BFGaoInnerChunkParser) (bgao.getGeoChunkKLE().getParseHandler());
            if (icp.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR) {
                setEnableParticleControls(true);
                GAOInnerParserParticleGenerator pag = (GAOInnerParserParticleGenerator) (icp.getGaoInner());
                tfParticlesFreq.setText("" + pag.getTimeDividerMain());
                cbParticlesEnabled.setSelected(((pag.getFlags() & 0x40) != 0) && ((pag.getFlags() & 0x80) == 0));
            }
        }
    }

    private void lBonesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lBonesValueChanged
        BFGaoChunk bgao = getSelectedBoneGao();
        setGeometryMorphElems(bgao);
        lbmBoneShape.removeAllElements();
        lbmTriangleChunk.removeAllElements();
        lbmSprites.removeAllElements();

        setEnableParticleControls(false);
        setParticleSettingsByGao(bgao);

        setGeometryTriangleChunksAndSpritesForMaterialInfo(bgao);

        if (bgao.getGroupChunkKLE() != null && bgao.getGroupChunkKLE().getParseHandler() != null) {
            BFGRPContainerChunk grpcc = (BFGRPContainerChunk) (bgao.getGroupChunkKLE().getParseHandler());
            BFGRPChunk grpc = grpcc.getGRP();
            if (grpc == null) {
                return;
            }

            ArrayList<TreeMap<Integer, BFKeyListElem>> grpelems = grpc.getGrpGaoList();
            if (grpelems == null) {
                return;
            }

            for (TreeMap<Integer, BFKeyListElem> tm : grpelems) {
                if (tm.firstEntry().getValue() == null) {
                    lbmBoneShape.addElement("null");
                } else {
                    lbmBoneShape.addElement(((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler().toString());
                }
            }
            getSetBoneShape(bgao, false, BS_SET);
        }
    }//GEN-LAST:event_lBonesValueChanged

    private void cbModifierIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbModifierIDActionPerformed

        if (singlePartGao == true && gao.getModifiersArray() != null) {
            setGeometryMorphModifierControls(gao.getModifiersArray(), cbModifierID.getSelectedIndex());
            return;
        }

        if (getSelectedBoneGao() != null && cbModifierID.getSelectedIndex() != -1) {
            setGeometryMorphModifierControls(getSelectedBoneGao().getModifiersArray(), cbModifierID.getSelectedIndex());
        }
    }//GEN-LAST:event_cbModifierIDActionPerformed

    private void btSetElemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetElemActionPerformed
        if (cbModifierID.getSelectedIndex() == -1 || lbGMGroup.getSelectedIndex() == -1 || lbGMGroupElems.getSelectedIndex() == -1) {
            return;
        }

        if (!singlePartGao && getSelectedBoneGao() == null) {
            return;
        }

        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }

        BFModifierGeoMorphModifier mod = (BFModifierGeoMorphModifier) (Modifier.getModifierByType(selGao.getModifiersArray(), BFModifierGeoMorphModifier.class, cbModifierID.getSelectedIndex()));
        if (mod == null) {
            return;
        }
        if (mod.getMorphShapeSettingsContainerStructs() == null || mod.getMorphShapeSettingsContainerStructs().size() <= lbGMGroup.getSelectedIndex() || mod.getMorphShapeForms() == null || mod.getMorphShapeForms().size() <= lbGMGroup.getSelectedIndex()) {
            return;
        }
        ModifierMorphShapeSettingsContainerStruct mms2 = mod.getMorphShapeSettingsContainerStructs().get(lbGMGroup.getSelectedIndex());
        ModifierMorphShapeFormStruct mms1 = mod.getMorphShapeForms().get(lbGMGroup.getSelectedIndex());
        if (mms2.getMorphShapeFormIDs().get(lbGMGroupElems.getSelectedIndex()) != null && mms2.getMorphShapeFormIDs().get(lbGMGroupElems.getSelectedIndex()).equals(-1)) {
            String ret = JOptionPane.showInputDialog(parent, "Please select morph shape to add! (Valid range: 0-" + (mod.getMorphShapeForms().size() - 1) + ")", "Set morph shape", JOptionPane.PLAIN_MESSAGE);
            int id = -1;
            try {
                if (ret == null || ret.trim().equals("")) {
                    return;
                }
                id = Integer.parseInt(ret);
            } catch (Exception e) {
                return;
            }

            if (mms1 == null) {
                return;
            }

            if (id < 0 || id >= mod.getMorphShapeForms().size()) {
                return;
            }

            mms2.getMorphShapeFormIDs().set(lbGMGroupElems.getSelectedIndex(), id);
            lbmGMGroupElems.setElementAt("" + id, lbGMGroupElems.getSelectedIndex());
            lbGMGroupElemsValueChanged(null);
        }
    }//GEN-LAST:event_btSetElemActionPerformed

    private void btUnsetElemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUnsetElemActionPerformed
        if (cbModifierID.getSelectedIndex() == -1 || lbGMGroup.getSelectedIndex() == -1 || lbGMGroupElems.getSelectedIndex() == -1) {
            return;
        }

        if (!singlePartGao && getSelectedBoneGao() == null) {
            return;
        }

        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }

        BFModifierGeoMorphModifier mod = (BFModifierGeoMorphModifier) (Modifier.getModifierByType(selGao.getModifiersArray(), BFModifierGeoMorphModifier.class, cbModifierID.getSelectedIndex()));
        if (mod == null) {
            return;
        }
        if (mod.getMorphShapeSettingsContainerStructs() == null || mod.getMorphShapeSettingsContainerStructs().size() <= lbGMGroup.getSelectedIndex()) {
            return;
        }
        ModifierMorphShapeSettingsContainerStruct mms2 = mod.getMorphShapeSettingsContainerStructs().get(lbGMGroup.getSelectedIndex());
        if (mms2.getMorphShapeFormIDs().get(lbGMGroupElems.getSelectedIndex()) != null && !(mms2.getMorphShapeFormIDs().get(lbGMGroupElems.getSelectedIndex()).equals(-1))) {
            if (JOptionPane.showConfirmDialog(parent, "Are you sure?", "Question", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                mms2.getMorphShapeFormIDs().set(lbGMGroupElems.getSelectedIndex(), -1);
                lbmGMGroupElems.setElementAt("-1", lbGMGroupElems.getSelectedIndex());
                lbGMGroupElemsValueChanged(null);
            }
        }

    }//GEN-LAST:event_btUnsetElemActionPerformed

    private void btGMSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGMSetActionPerformed
        if (cbModifierID.getSelectedIndex() == -1 || lbGMGroup.getSelectedIndex() == -1) {
            return;
        }

        if (!singlePartGao && getSelectedBoneGao() == null) {
            return;
        }

        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }

        BFModifierGeoMorphModifier mod = (BFModifierGeoMorphModifier) (Modifier.getModifierByType(selGao.getModifiersArray(), BFModifierGeoMorphModifier.class, cbModifierID.getSelectedIndex()));
        if (mod == null) {
            return;
        }
        if (mod.getMorphShapeSettingsContainerStructs() == null || mod.getMorphShapeSettingsContainerStructs().size() <= lbGMGroup.getSelectedIndex()) {
            return;
        }
        ModifierMorphShapeSettingsContainerStruct mms2 = mod.getMorphShapeSettingsContainerStructs().get(lbGMGroup.getSelectedIndex());
        try {
            Float unkf1 = Float.parseFloat(tfUnkF1.getText());
            Float unkf2 = Float.parseFloat(tfUnkF2.getText());
            mms2.setUnkInner2F1(unkf1);
            mms2.setUnkInner2F2(unkf2);
        } catch (Exception e) {
            tfUnkF1.setText("" + mms2.getUnkInner2F1());
            tfUnkF2.setText("" + mms2.getUnkInner2F2());
        }
    }//GEN-LAST:event_btGMSetActionPerformed

    private void lbGMGroupValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lbGMGroupValueChanged
        if (cbModifierID.getSelectedIndex() == -1 || lbGMGroup.getSelectedIndex() == -1) {
            return;
        }

        if (!singlePartGao && getSelectedBoneGao() == null) {
            return;
        }

        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }

        lbmGMGroupElems.clear();
        BFModifierGeoMorphModifier mod = (BFModifierGeoMorphModifier) (Modifier.getModifierByType(selGao.getModifiersArray(), BFModifierGeoMorphModifier.class, cbModifierID.getSelectedIndex()));
        if (mod == null) {
            return;
        }
        if (mod.getMorphShapeSettingsContainerStructs() == null || mod.getMorphShapeSettingsContainerStructs().size() <= lbGMGroup.getSelectedIndex()) {
            return;
        }
        ModifierMorphShapeSettingsContainerStruct mms2 = mod.getMorphShapeSettingsContainerStructs().get(lbGMGroup.getSelectedIndex());
        if (mms2.getMorphShapeFormIDs() == null) {
            return;
        }
        for (Integer i : mms2.getMorphShapeFormIDs()) {
            lbmGMGroupElems.addElement("" + i);
        }

        tfUnkF1.setText("" + mms2.getUnkInner2F1());
        tfUnkF2.setText("" + mms2.getUnkInner2F2());
    }//GEN-LAST:event_lbGMGroupValueChanged

    private void lbGMGroupElemsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lbGMGroupElemsValueChanged
        lbAffectedVerticesNum.setText("");
        if (cbModifierID.getSelectedIndex() == -1 || lbGMGroup.getSelectedIndex() == -1 || lbGMGroupElems.getSelectedIndex() == -1) {
            return;
        }
        if (!singlePartGao && getSelectedBoneGao() == null) {
            return;
        }

        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }

        BFModifierGeoMorphModifier mod = (BFModifierGeoMorphModifier) (Modifier.getModifierByType(selGao.getModifiersArray(), BFModifierGeoMorphModifier.class, cbModifierID.getSelectedIndex()));
        if (mod == null) {
            return;
        }
        if (mod.getMorphShapeSettingsContainerStructs() == null || mod.getMorphShapeSettingsContainerStructs().size() <= lbGMGroup.getSelectedIndex() || mod.getMorphShapeForms() == null || mod.getMorphShapeForms().size() <= lbGMGroup.getSelectedIndex()) {
            return;
        }
        Integer id = mod.getMorphShapeSettingsContainerStructs().get(lbGMGroup.getSelectedIndex()).getMorphShapeFormIDs().get(lbGMGroupElems.getSelectedIndex());
        if (id.equals(-1)) {
            return;
        }
        ModifierMorphShapeFormStruct mms1 = mod.getMorphShapeForms().get(id);
        lbAffectedVerticesNum.setText("Affected vertices: " + mms1.getMorphVerticesNum());
    }//GEN-LAST:event_lbGMGroupElemsValueChanged

    private void btBoneShapeSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBoneShapeSetActionPerformed
        if (getSelectedBoneGao() == null) {
            return;
        }
        getSetBoneShape(getSelectedBoneGao(), true, BS_SET);
        r.setRunApplyShapeOnThisGao(gao);
    }//GEN-LAST:event_btBoneShapeSetActionPerformed

    private void btBoneShapeSet1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBoneShapeSet1ActionPerformed
        if (getSelectedBoneGao() == null) {
            return;
        }
        getSetBoneShape(getSelectedBoneGao(), true, BS_HIDE);
        r.setRunApplyShapeOnThisGao(gao);
    }//GEN-LAST:event_btBoneShapeSet1ActionPerformed

    private void lbTriangleChunkValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lbTriangleChunkValueChanged
        taMaterialInfo.setText("");
        cbTriangleChunkRenderingEnabled.setEnabled(false);
        cbTriangleChunkRenderingEnabled.setSelected(false);
        if (!singlePartGao && getSelectedBoneGao() == null) {
            return;
        }

        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }

        if (selGao == null || lbTriangleChunk.getSelectedIndex() == -1
                || selGao.getMaterialGroupChunkKLE() == null || selGao.getMaterialGroupChunkKLE().getParseHandler() == null) {
            return;
        }

        BFGaoInnerChunkParser icp = (BFGaoInnerChunkParser) (selGao.getMaterialGroupChunkKLE().getParseHandler());
        if (icp.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_MATERIAL_GROUP) {
            GeometryTriangleChunk geoTC[] = getGTCFromGao(selGao);
            if (geoTC != null && geoTC.length > lbTriangleChunk.getSelectedIndex() && geoTC[lbTriangleChunk.getSelectedIndex()] != null) {
                cbTriangleChunkRenderingEnabled.setEnabled(true);
                cbTriangleChunkRenderingEnabled.setSelected(geoTC[lbTriangleChunk.getSelectedIndex()].isRenderingEnabled());
                Integer matID = geoTC[lbTriangleChunk.getSelectedIndex()].getMaterialNum();
                if (icp.getGaoInner() != null) {
                    GAOInnerParserMaterialGroup mmg = (GAOInnerParserMaterialGroup) (icp.getGaoInner());
                    if (mmg.getMaterialGroupChunkArrayKLE() != null) {
                        BFKeyListElem kle;
                        if (mmg.getMaterialGroupChunkArrayKLE().length > matID) {
                            kle = mmg.getMaterialGroupChunkArrayKLE()[matID];
                            taMaterialInfo.setText("Normal material ID");
                        } else {
                            kle = mmg.getMaterialGroupChunkArrayKLE()[mmg.getMaterialGroupChunkArrayKLE().length - 1];
                            taMaterialInfo.setText("Overflowed material ID");
                        }
                        if (kle != null && kle.getParseHandler() != null) {
                            BFGaoInnerChunkParser subicp = (BFGaoInnerChunkParser) (kle.getParseHandler());
                            if (subicp.getGaoInner() != null) {
                                taMaterialInfo.setText(taMaterialInfo.getText() + System.lineSeparator() + subicp.getGaoInner().toString());
                            }
                        }
                    }
                }
            }
        } else if (icp.getGaoInner() != null) {
            taMaterialInfo.setText(icp.getGaoInner().toString());
        }


    }//GEN-LAST:event_lbTriangleChunkValueChanged

    private void btBoneShapeMakeVisibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBoneShapeMakeVisibleActionPerformed
        if (getSelectedBoneGao() == null) {
            return;
        }
        getSetBoneShape(getSelectedBoneGao(), true, BS_SHOW);
        r.setRunApplyShapeOnThisGao(gao);
    }//GEN-LAST:event_btBoneShapeMakeVisibleActionPerformed

    private void cbTriangleChunkRenderingEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTriangleChunkRenderingEnabledActionPerformed
        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }
        GeometryTriangleChunk geoTC[] = getGTCFromGao(selGao);
        if (geoTC != null && geoTC.length > lbTriangleChunk.getSelectedIndex() && geoTC[lbTriangleChunk.getSelectedIndex()] != null) {
            geoTC[lbTriangleChunk.getSelectedIndex()].setRenderingEnabled(cbTriangleChunkRenderingEnabled.isSelected());
        }
    }//GEN-LAST:event_cbTriangleChunkRenderingEnabledActionPerformed

    private void lBonesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lBonesMouseClicked
        if (evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            BFGaoChunk bgao = getSelectedBoneGao();

            if (bgao == null) {
                return;
            }

            StringBuilder sb = RenderUtils.getMatricesDescription(bgao, null);
            JTextArea textArea = new JTextArea(sb.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            scrollPane.setPreferredSize(new Dimension(500, 200));
            JOptionPane.showMessageDialog(null, scrollPane, "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_lBonesMouseClicked

    private void cbParticlesEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbParticlesEnabledActionPerformed
        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }
        if (selGao != null && selGao.getGeoChunkKLE() != null && selGao.getGeoChunkKLE() != null && selGao.getGeoChunkKLE().getParseHandler() != null) {
            BFGaoInnerChunkParser icp = (BFGaoInnerChunkParser) (selGao.getGeoChunkKLE().getParseHandler());
            if (icp.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR) {
                GAOInnerParserParticleGenerator pag = (GAOInnerParserParticleGenerator) (icp.getGaoInner());
                int flags = pag.getFlags();
                if (cbParticlesEnabled.isSelected()) {
                    flags |= 0x40;
                    flags &= ~0x80;
                } else {
                    flags &= ~0x40;
                    flags |= 0x80;
                }
                pag.setFlags(flags);
            }
        }
    }//GEN-LAST:event_cbParticlesEnabledActionPerformed

    private void btPartFreqSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPartFreqSetActionPerformed
        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }
        if (selGao != null && selGao.getGeoChunkKLE() != null && selGao.getGeoChunkKLE() != null && selGao.getGeoChunkKLE().getParseHandler() != null) {
            BFGaoInnerChunkParser icp = (BFGaoInnerChunkParser) (selGao.getGeoChunkKLE().getParseHandler());
            if (icp.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR) {
                GAOInnerParserParticleGenerator pag = (GAOInnerParserParticleGenerator) (icp.getGaoInner());
                try {
                    float f = Float.parseFloat(tfParticlesFreq.getText());
                    pag.setTimeDividerMain(f);
                } catch (Exception e) {
                    tfParticlesFreq.setText("" + pag.getTimeDividerMain());
                }
            }
        }
    }//GEN-LAST:event_btPartFreqSetActionPerformed

    private void btPartResetToDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPartResetToDefaultsActionPerformed
        BFGaoChunk selGao;
        if (singlePartGao) {
            selGao = gao;
        } else {
            selGao = getSelectedBoneGao();
        }
        if (selGao != null && selGao.getGeoChunkKLE() != null && selGao.getGeoChunkKLE() != null && selGao.getGeoChunkKLE().getParseHandler() != null) {
            BFGaoInnerChunkParser icp = (BFGaoInnerChunkParser) (selGao.getGeoChunkKLE().getParseHandler());
            if (icp.getGaoInnerType() == BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR) {
                GAOInnerParserParticleGenerator pag = (GAOInnerParserParticleGenerator) (icp.getGaoInner());
                pag.reset();
                setParticleSettingsByGao(selGao);
            }
        }
    }//GEN-LAST:event_btPartResetToDefaultsActionPerformed

    private void lActionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lActionsMouseClicked
        if (lActions.getSelectedIndex() == -1 || lmActions.isEmpty()) {
            return;
        }
        if (evt.getModifiers() == MouseEvent.BUTTON3_MASK) {
            int x = evt.getX();
            int y = evt.getY();
            actionMenu.show(lActions, x, y);
            actionMenu.repaint();
        } else {
            actionMenu.setVisible(false);
        }
    }//GEN-LAST:event_lActionsMouseClicked

    private void rbPUMAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPUMAllActionPerformed
        r.setAnimGaoPositionUpdateMode(AnimationPlayer.POSITIONUPDATE_ALL);
    }//GEN-LAST:event_rbPUMAllActionPerformed

    private void rbPUMNoGlobalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPUMNoGlobalActionPerformed
        r.setAnimGaoPositionUpdateMode(AnimationPlayer.POSITIONUPDATE_NO_GLOBAL);
    }//GEN-LAST:event_rbPUMNoGlobalActionPerformed

    private void rbPUMNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPUMNoneActionPerformed
        r.setAnimGaoPositionUpdateMode(AnimationPlayer.POSITIONUPDATE_NONE);
    }//GEN-LAST:event_rbPUMNoneActionPerformed

    private void btResetGlobalPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btResetGlobalPosActionPerformed
        if (gao != null) {
            // update globalmatrix
            if (gao.getGlobalMatrix() != null) {
                gao.getGlobalMatrix().getPos().set(0.0f, 0.0f, 0.0f);
            }
            // update flashmatrix
            if ((gao.getFlags() & 0x4000000) != 0) {
                if ((gao.getFlags() & 0x10000) != 0) {
                    if (gao.getGlobalMatrixThird() != null) {
                        gao.getGlobalMatrixThird().getPos().set(0.0f, 0.0f, 0.0f);
                    }
                } else {
                    if (gao.getGlobalMatrixSecond() != null) {
                        gao.getGlobalMatrixSecond().getPos().set(0.0f, 0.0f, 0.0f);
                    }
                }
            }
        }
    }//GEN-LAST:event_btResetGlobalPosActionPerformed

    private void btTrkOpenDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btTrkOpenDirActionPerformed
        File f = IrisTool.showBestFileChooserForPlatform(this, "Open TRL file"); 
        if (f == null) return;
        BFEventsChunk ec = new BFEventsChunk();
        if (!ec.parseHack(f)) {           
            return;
        }
        BFActionListChunk al = new BFActionListChunk();
        BFKeyListElem kle = new BFKeyListElem(1234, BFActionChunk.class, false);
        BFKeyListElem kle2 = new BFKeyListElem(1234, BFEventsChunk.class, false);
        al.getActionsKLE().add(kle);
        al.getActionKeys().add(1234);
        BFKeyListElem kleac = new BFKeyListElem(1234, BFActionListChunk.class, false);
        kleac.setParseHandler(al);
        BFActionChunk ac = new BFActionChunk();
        ActionsElemStruct aes = new ActionsElemStruct(1234, 0, 0xffffffff, (byte)1, (byte)0, (byte)64, (byte)0, (byte)0);
        aes.setFlags((short)0);
        ac.getAesList().add(aes);
        ac.getEventsChunksKLE().add(kle2);
        ac.getEventsChunksKLE().get(0).setParseHandler(ec);
        kle.setParseHandler(ac);
        r.getSelectedSingleGao().setActionListChunkKLE(kleac);
        r.setAniGao(r.getSelectedSingleGao());
        populateActionList(r.getAnigao());
    }//GEN-LAST:event_btTrkOpenDirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAttachActionKit;
    private javax.swing.JButton btBoneShapeMakeVisible;
    private javax.swing.JButton btBoneShapeSet;
    private javax.swing.JButton btBoneShapeSet1;
    private javax.swing.JButton btEventViewer;
    private javax.swing.JButton btGMSet;
    private javax.swing.JButton btManipluteCamera;
    private javax.swing.JButton btPartFreqSet;
    private javax.swing.JButton btPartResetToDefaults;
    private javax.swing.JButton btPlayStop;
    private javax.swing.JButton btResetAllBonesToDefault;
    private javax.swing.JButton btResetBoneToDefault;
    private javax.swing.JButton btResetGlobalPos;
    private javax.swing.JButton btSetBoneToManipulate;
    private javax.swing.JButton btSetElem;
    private javax.swing.JButton btTrkOpenDir;
    private javax.swing.JButton btUnsetElem;
    private javax.swing.ButtonGroup btgPUM;
    private javax.swing.JCheckBox cbAniDebug;
    private javax.swing.JComboBox<String> cbModifierID;
    private DefaultComboBoxModel cbmModifierID;
    private javax.swing.JCheckBox cbParticlesEnabled;
    private javax.swing.JCheckBox cbTriangleChunkRenderingEnabled;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator1;
    private DefaultListModel<String> lmActions;
    private javax.swing.JList<String> lActions;
    private DefaultListModel<String> lmBones;
    private javax.swing.JList<String> lBones;
    private javax.swing.JLabel lbAffectedVerticesNum;
    private DefaultListModel<String> lbmBoneShape;
    private javax.swing.JList<String> lbBoneShape;
    private javax.swing.JLabel lbFreq;
    private javax.swing.JList<String> lbGMGroup;
    private DefaultListModel<String> lbmGMGroup;
    private DefaultListModel<String> lbmGMGroupElems;
    private javax.swing.JList<String> lbGMGroupElems;
    private javax.swing.JList<String> lbSprites;
    private DefaultListModel<String> lbmSprites;
    private DefaultListModel<String> lbmTriangleChunk;
    private javax.swing.JList<String> lbTriangleChunk;
    private javax.swing.JPanel pActionPlayer;
    private javax.swing.JPanel pBoneShape;
    private javax.swing.JPanel pGeometryMorph;
    private javax.swing.JPanel pMaterialInfo;
    private javax.swing.JPanel pMaterialInfoSP;
    private javax.swing.JPanel pMaterialInfoTC;
    private javax.swing.JPanel pParticles;
    private javax.swing.JPanel pPositionUpdatemode;
    private javax.swing.JRadioButton rbPUMAll;
    private javax.swing.JRadioButton rbPUMNoGlobal;
    private javax.swing.JRadioButton rbPUMNone;
    private javax.swing.JTextArea taMaterialInfo;
    private javax.swing.JTextField tfParticlesFreq;
    private javax.swing.JTextField tfUnkF1;
    private javax.swing.JTextField tfUnkF2;
    private javax.swing.JTabbedPane tpControls;
    private javax.swing.JTabbedPane tpMaterialInfo;
    // End of variables declaration//GEN-END:variables
}
