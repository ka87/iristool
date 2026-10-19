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
import BFDataFileLib.BFChunkParsers.Map.BFCollisionInstanceChunk;
import BFDataFileLib.BFChunkParsers.Map.BFCollisionInstanceCollisionSetChunk;
import BFDataFileLib.BFChunkParsers.Map.BFCollisionMapGroupChunk;
import BFDataFileLib.BFChunkParsers.Map.BFCollisionObjectChunk;
import BFDataFileLib.BFChunkParsers.Map.BFCollisionObjectGameMaterialChunk;
import BFDataFileLib.BFChunkParsers.Map.BFEventsChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGRPContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoChunk;
import BFDataFileLib.BFChunkParsers.Map.BFGaoInnerChunkParser;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingContainerChunk;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingFunctionCodeChunk;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingVariablesAndFunctionCodeKeyCollection;
import BFDataFileLib.BFChunkParsers.Map.BFScriptingVariablesChunk;
import BFDataFileLib.BFChunkParsers.Map.BFWOWChunk;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserCamera;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserGeometry;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserLight;
import BFDataFileLib.BFChunkParsers.Map.GAOInnerParsers.GAOInnerParserParticleGenerator;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGAOInverseKinematics;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGAOSpecialLookAt;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoColorModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierGeoTransformedVerticesSnapshotStore;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierLazyModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierMeshWave;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierParticleGenerator;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSnake;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierSpriteGenModifier;
import BFDataFileLib.BFChunkParsers.Map.GAOModifierParsers.BFModifierXMECModifier;
import BFDataFileLib.BFDataFileClass;
import BFDataFileLib.BFKeyListElem;
import BFDataFileLib.BFModifierBaseClass;
import BFlib.TextureDatabase;
import Common.Convert;
import Common.DataType.ActionsElemStruct;
import Common.DataType.ActionsElemTransitionStruct;
import Common.DataType.ActionsElemTransitionStructInside1;
import Common.DataType.ActionsElemTransitionStructInside2;
import Common.DataType.CollisionInstanceElem;
import Common.DataType.Matrix4x4;
import Common.DataType.OVATypeElem;
import Common.IrisToolDefines;
import iristool.GUI.GAOColorizedTreeCellRenderer;
import iristool.GUI.GUIDefines;
import iristool.Engine.RenderUtils;
import iristool.Engine.Renderer;
import iristool.Engine.RendererCallbacksInterface;
import iristool.Engine.Scripting;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author andras
 */
public class WOWBrowser extends javax.swing.JDialog implements RendererCallbacksInterface {

    /**
     * Creates new form WOWBrowser
     *
     * @param parent
     * @param modal
     */
    private DefaultMutableTreeNode rootElem = null;
    private BFWOWChunk bfwc;
    private BFDataFileClass bfdfc;
    private BFDataFileClass bfdfcTex;
    private TextureDatabase td;
    private boolean rendererOpen = false;
    private JPopupMenu scriptingMenu = null;
    private JPopupMenu scriptingDetailsMenu = null;
    private JPopupMenu gaoListMenu = null;
    private String prevSearchKey = "";
    private String prevSearchGaoName = "";

    public WOWBrowser(java.awt.Frame parent, boolean modal, BFDataFileClass bfdfc, BFDataFileClass bfdfcTex) {
        super(parent, modal);
        this.bfdfc = bfdfc;
        this.bfdfcTex = bfdfcTex;
        this.bfwc = bfdfc.getParsedWOWMerged();
        this.td = bfdfc.getTextureDatabase();
        rootElem = new DefaultMutableTreeNode("WOW Name: " + bfwc.getWowName() + String.format("  [Clear color: #%x, Ambient Color 1: #%x, Ambient Color 2: #%x]",
                bfwc.getClearColor(), bfwc.getAmbientColor1(), bfwc.getAmbientColor2()));
        this.setTitle("WOW Browser: " + bfwc.getWowName());
        Iterator it = bfwc.getGaoList().getGaos().iterator();
        TreeMap<Integer, BFKeyListElem> tm;
        BFGaoChunk gao;
        DefaultMutableTreeNode gaoElem;

        GAOColorizedTreeCellRenderer cr = new GAOColorizedTreeCellRenderer();
        cr.addColorToBitmask(0x2, GUIDefines.COLOR_GAO_HAS_ANIMATION);
        cr.addColorToGaoInnerType(BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY, GUIDefines.COLOR_GAO_HAS_GEOMETRY);
        cr.addColorToGaoInnerType(BFGaoInnerChunkParser.BF_GAO_TYPE_STATICLOD, GUIDefines.COLOR_GAO_HAS_GEOMETRY);
        cr.addColorToGaoInnerType(BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR, GUIDefines.COLOR_GAO_HAS_PAG);
        cr.addColorToBitmask(0x2000000, GUIDefines.COLOR_GAO_HAS_EVENTS);

        while (it.hasNext()) {
            tm = (TreeMap<Integer, BFKeyListElem>) it.next();
            gao = (BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler());
            gaoElem = new DefaultMutableTreeNode(gao);
            rootElem.add(gaoElem);
        }
        initComponents();
        if (IrisToolDefines.PUBLIC_RELEASE) {
            btViewCollision.setEnabled(false);
        }
        trGaoList.setCellRenderer(cr);
        trGaoList.setToolTipText("<html>Game renders only <font color=\""
                + String.format("#%06X", (0xFFFFFF & GUIDefines.COLOR_GAO_HAS_GEOMETRY.getRGB()))
                + "\">geometry</font> and <font color=\""
                + String.format("#%06X", (0xFFFFFF & GUIDefines.COLOR_GAO_HAS_ANIMATION.getRGB()))
                + "\">animation</font> contained GAOs.<br>"
                + "<strong>Other Colors:</strong><br>"
                + "<font color=\"" + String.format("#%06X", (0xFFFFFF & GUIDefines.COLOR_GAO_HAS_EVENTS.darker().getRGB()))
                + "\">- Event GAOs</font><br>"
                + "<font color=\"" + String.format("#%06X", (0xFFFFFF & GUIDefines.COLOR_GAO_HAS_PAG.getRGB()))
                + "\">- Particle Generator GAOs</font><br><br>"
                + "Use <b>CTRL + click</b> for GAO flags bit details, and "
                + "use <b>ALT + click</b> for GAO visual flags bit details.</html>");
        trGaoList.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // -- SCRIPTING MENU THINGS --
        scriptingMenu = new JPopupMenu("scriptingMenu");
        JMenuItem gotoFunctionKey = new JMenuItem("Goto Function Key");
        scriptingMenu.add(gotoFunctionKey);
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            JMenuItem generateCStruct = new JMenuItem("Generate C compatible struct from variables");
            scriptingMenu.add(generateCStruct);
            generateCStruct.addActionListener((ActionEvent ae) -> {

                class SortByOffset implements Comparator<OVATypeElem> {

                    @Override
                    public int compare(OVATypeElem a, OVATypeElem b) {
                        return a.getMemoryOffset() - b.getMemoryOffset();
                    }
                }

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                Object obj = node.getUserObject();
                if (!(obj instanceof BFGaoChunk)) {
                    return;
                }
                BFGaoChunk currGao = (BFGaoChunk) obj;

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < lmScriptingList.getSize(); i++) {
                    if (lmScriptingList.get(i) instanceof BFScriptingVariablesChunk) {
                        sb.append("struct tScriptStruct_").append(String.format("%x", currGao.getScriptingContainerKey())).append("{\n");

                        ArrayList<OVATypeElem> al = ((BFScriptingVariablesChunk) (lmScriptingList.get(i))).getTypeElems();
                        ArrayList<String> alNames = ((BFScriptingVariablesChunk) (lmScriptingList.get(i))).getVariableNames();
                        if (al == null) {
                            return;
                        }
                        ArrayList<OVATypeElem> alCpy = (ArrayList<OVATypeElem>) (al.clone());
                        alCpy.sort(new SortByOffset());
                        String name;
                        String type = "int";
                        for (int j = 0; j < alCpy.size(); j++) {
                            Integer idx = al.indexOf(alCpy.get(j));
                            if (idx == null || alNames == null || alNames.size() <= idx) {
                                name = "unk_" + alCpy.get(j).getMemoryOffset();
                            } else {
                                name = alNames.get(idx);
                            }
                            switch (alCpy.get(j).getType()) {
                                case 0x22:
                                    type = "float";
                                    break;
                                case 0x25:
                                    type = "Vec3";
                                    break;
                                case 0x29:
                                    type = "tMessage";
                                    break;
                                case 0x2c:
                                    type = "tTextGet";
                                    break;
                                default:
                                    type = "int";
                                    break;
                            }
                            for (int k = 0; k < alCpy.get(j).getArrayDimension(); k++) {
                                sb.append("   int __").append(name).append("_Dimension").append(k + 1).append(";  // ").append(alCpy.get(j).getMemoryOffset() + (k * 4)).append("\n");
                            }
                            sb.append("   ").append(type).append(" ").append(name);
                            if (alCpy.get(j).getRawDataElemNumWithoutDimensionSizes() > 1) {
                                sb.append("[").append(alCpy.get(j).getRawDataElemNumWithoutDimensionSizes()).append("]");
                            }
                            sb.append(";  // ").append((alCpy.get(j).getArrayDimension() > 0 ? (alCpy.get(j).getArrayDimension() * 4) : 0) + alCpy.get(j).getMemoryOffset()).append("\n");
                        }
                        sb.append("}");
                        //System.out.println(sb.toString());
                        JTextArea textArea = new JTextArea(sb.toString());
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        textArea.setEditable(false);
                        scrollPane.setPreferredSize(new Dimension(500, 200));
                        JOptionPane.showMessageDialog(null, scrollPane, "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            });
        }
        gotoFunctionKey.addActionListener((ActionEvent ae) -> {
            String s = JOptionPane.showInputDialog(WOWBrowser.this, "Please type Function Key to search:", "Goto Function Key", JOptionPane.PLAIN_MESSAGE);
            if (s == null || s.trim().isEmpty()) {
                return;
            }
            try {
                Integer key = (int) (Long.parseLong(s.trim(), 16));
                for (int i = 0; i < lmScriptingList.size(); i++) {
                    if (lmScriptingList.getElementAt(i) instanceof BFScriptingFunctionCodeChunk
                            && ((BFScriptingFunctionCodeChunk) (lmScriptingList.getElementAt(i))).getSelfKey().equals(key)) {
                        lScriptingList.setSelectedIndex(i);
                        lScriptingList.ensureIndexIsVisible(i);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(WOWBrowser.this, "Key not found.", "Goto Function Key", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ne) {
            }
        });

        // -- SCRIPTING DETAILS MENU THINGS --
        scriptingDetailsMenu = new JPopupMenu("scriptingDetailsMenu");
        JMenuItem gotoOffset = new JMenuItem("Goto Offset");
        JMenuItem copyToClipboard = new JMenuItem("Copy selected item name to clipboard");
        scriptingDetailsMenu.add(gotoOffset);
        scriptingDetailsMenu.add(copyToClipboard);
        gotoOffset.addActionListener((ActionEvent ae) -> {
            String s = JOptionPane.showInputDialog(WOWBrowser.this, "Please type Offset in decimal format:", "Goto Offset", JOptionPane.PLAIN_MESSAGE);
            if (s == null || s.trim().isEmpty()) {
                return;
            }
            try {
                Integer offset = Integer.parseInt(s);
                if (lmScriptingList.get(lScriptingList.getSelectedIndex()) instanceof BFScriptingVariablesChunk) {
                    BFScriptingVariablesChunk vals = (BFScriptingVariablesChunk) lmScriptingList.get(lScriptingList.getSelectedIndex());
                    for (int i = 0; i < vals.getTypeElems().size(); i++) {
                        if (vals.getTypeElems().get(i).getMemoryOffset() != null && vals.getTypeElems().get(i).getMemoryOffset().equals(offset)) {
                            lScriptingDetails.setSelectedIndex(i);
                            lScriptingDetails.ensureIndexIsVisible(i);
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(WOWBrowser.this, "Offset not found.", "Goto Offset", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ne) {
            }
        });

        // -- GAO LIST MENU THINGS --
        gaoListMenu = new JPopupMenu("gaoListMenu");
        JMenuItem searchGaoByKey = new JMenuItem("Search GAO by Key");
        JMenuItem searchGaoByName = new JMenuItem("Search GAO by Name");
        gaoListMenu.add(searchGaoByKey);
        gaoListMenu.add(searchGaoByName);
        searchGaoByKey.addActionListener((ActionEvent ae) -> {
            Object o = JOptionPane.showInputDialog(WOWBrowser.this, "Please type GAO Key to search:", "Search GAO by Key", JOptionPane.PLAIN_MESSAGE, null, null, prevSearchKey);
            if (o == null || ((String) o).trim().isEmpty()) {
                prevSearchKey = "";
                return;
            }
            prevSearchKey = ((String) o);
            try {
                BFGaoChunk currGao;
                TreePath tp;
                Integer key = (int) (Long.parseLong(((String) o).trim(), 16));
                DefaultMutableTreeNode tn = (DefaultMutableTreeNode) (trGaoList.getModel().getRoot());
                for (int i = 0; i < tn.getChildCount(); i++) {
                    DefaultMutableTreeNode stn = (DefaultMutableTreeNode) (tn.getChildAt(i));
                    if (stn != null && stn.getUserObject() != null && stn.getUserObject() instanceof BFGaoChunk) {
                        currGao = (BFGaoChunk) (stn.getUserObject());
                        if (currGao.getSelfKey() != null && currGao.getSelfKey().equals(key)) {
                            tp = new TreePath(stn.getPath());
                            trGaoList.setSelectionPath(tp);
                            trGaoList.scrollPathToVisible(tp);
                            return;
                        }
                    }
                }
                JOptionPane.showMessageDialog(WOWBrowser.this, "Key not found.", "Search GAO by Key", JOptionPane.ERROR_MESSAGE);

            } catch (NumberFormatException ne) {
                JOptionPane.showMessageDialog(WOWBrowser.this, "Invalid Key format (not hex).", "Search GAO by Key", JOptionPane.ERROR_MESSAGE);
            }
        });

        copyToClipboard.addActionListener((ActionEvent ae) -> {
            if (lScriptingList.getSelectedIndex() == -1 || lScriptingDetails.getSelectedIndex() == -1) {
                return;
            }
            if (lmScriptingList.get(lScriptingList.getSelectedIndex()) instanceof BFScriptingVariablesChunk) {
                BFScriptingVariablesChunk vals = (BFScriptingVariablesChunk) lmScriptingList.get(lScriptingList.getSelectedIndex());
                StringSelection stringSelection = new StringSelection(vals.getVariableNames().get(lScriptingDetails.getSelectedIndex()));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });

        searchGaoByName.addActionListener((ActionEvent ae) -> {
            Object o = JOptionPane.showInputDialog(WOWBrowser.this, "Please type GAO name to search (Case sensitive):", "Search GAO by Name", JOptionPane.PLAIN_MESSAGE, null, null, prevSearchGaoName);
            if (o == null || ((String) o).trim().isEmpty()) {
                prevSearchGaoName = "";
                return;
            }
            prevSearchGaoName = ((String) o);
            BFGaoChunk currGao;
            TreePath tp;
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) (trGaoList.getModel().getRoot());
            for (int i = 0; i < tn.getChildCount(); i++) {
                DefaultMutableTreeNode stn = (DefaultMutableTreeNode) (tn.getChildAt(i));
                if (stn != null && stn.getUserObject() != null && stn.getUserObject() instanceof BFGaoChunk) {
                    currGao = (BFGaoChunk) (stn.getUserObject());
                    if (currGao.getSelfKey() != null && currGao.getGaoName().contains(((String) o))) {
                        tp = new TreePath(stn.getPath());
                        trGaoList.setSelectionPath(tp);
                        trGaoList.scrollPathToVisible(tp);
                        return;
                    }
                }
            }
            JOptionPane.showMessageDialog(WOWBrowser.this, "GAO not found.", "Search GAO by Key", JOptionPane.ERROR_MESSAGE);
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btgScriptingGet = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        trGaoList = new javax.swing.JTree(rootElem);
        lTitle = new javax.swing.JLabel();
        btClose = new javax.swing.JButton();
        pScriptingRadioButtonContainer = new javax.swing.JPanel();
        btScriptingVFArrays = new javax.swing.JRadioButton();
        btScriptingVariableOverrides = new javax.swing.JRadioButton();
        pScripting = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lmScriptingList = new DefaultListModel();
        lScriptingList = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        lmScriptingDetails = new DefaultListModel();
        lScriptingDetails = new javax.swing.JList<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        lmActionList = new DefaultListModel();
        lActionList = new javax.swing.JList<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        lmActionElemList = new DefaultListModel();
        lActionElemList = new javax.swing.JList<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        taGaoPositionInfo = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        taGaoBoundingBoxInfo = new javax.swing.JTextArea();
        sourceWOWList = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        taInfo = new javax.swing.JTextArea();
        lbGaoOtherFlags = new javax.swing.JLabel();
        tfGaoKeyAndName = new javax.swing.JTextField();
        pButtonContainer = new javax.swing.JPanel();
        btRenderGeometry = new javax.swing.JButton();
        btViewBonesInfoOrShapeMatrices = new javax.swing.JButton();
        btViewGroup = new javax.swing.JButton();
        btViewActionOrEventGAOGroupMembers = new javax.swing.JButton();
        btEvent = new javax.swing.JButton();
        btViewAnimationShapeData = new javax.swing.JButton();
        btRenderWorld = new javax.swing.JButton();
        btModifierView = new javax.swing.JButton();
        btViewCollision = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        trGaoList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                trGaoListMouseClicked(evt);
            }
        });
        trGaoList.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                trGaoListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(trGaoList);

        lTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lTitle.setText("IrisTool WOW Browser");

        btClose.setText("Close");
        btClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCloseActionPerformed(evt);
            }
        });

        pScriptingRadioButtonContainer.setBorder(javax.swing.BorderFactory.createTitledBorder("Show Scripting "));

        btgScriptingGet.add(btScriptingVFArrays);
        btScriptingVFArrays.setSelected(true);
        btScriptingVFArrays.setText("Variables/Functions");
        btScriptingVFArrays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btScriptingVFArraysActionPerformed(evt);
            }
        });

        btgScriptingGet.add(btScriptingVariableOverrides);
        btScriptingVariableOverrides.setText("Variable overrides");
        btScriptingVariableOverrides.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btScriptingVariableOverridesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pScriptingRadioButtonContainerLayout = new javax.swing.GroupLayout(pScriptingRadioButtonContainer);
        pScriptingRadioButtonContainer.setLayout(pScriptingRadioButtonContainerLayout);
        pScriptingRadioButtonContainerLayout.setHorizontalGroup(
            pScriptingRadioButtonContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pScriptingRadioButtonContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pScriptingRadioButtonContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btScriptingVFArrays)
                    .addComponent(btScriptingVariableOverrides))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pScriptingRadioButtonContainerLayout.setVerticalGroup(
            pScriptingRadioButtonContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pScriptingRadioButtonContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btScriptingVFArrays)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btScriptingVariableOverrides)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        pScripting.setLayout(new java.awt.GridLayout(3, 3));

        lScriptingList.setModel(lmScriptingList
        );
        lScriptingList.setToolTipText("Scripting variables summary / stripting function keys");
        lScriptingList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lScriptingListMouseClicked(evt);
            }
        });
        lScriptingList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lScriptingListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(lScriptingList);

        pScripting.add(jScrollPane3);

        lScriptingDetails.setModel(lmScriptingDetails);
        lScriptingDetails.setToolTipText("Scripting variable names");
        lScriptingDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lScriptingDetailsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lScriptingDetails);

        pScripting.add(jScrollPane2);

        lActionList.setModel(lmActionList
        );
        lActionList.setToolTipText("Action list");
        lActionList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lActionListValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(lActionList);

        pScripting.add(jScrollPane5);

        lActionElemList.setModel(lmActionElemList
        );
        lActionElemList.setToolTipText("<html>Action elem list<br>\n<b>CTRL + click</b>: shows animation shape data<br>\n<b>ALT + click</b>: shows Action transition data\n</html>");
        lActionElemList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lActionElemListMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(lActionElemList);

        pScripting.add(jScrollPane6);

        taGaoPositionInfo.setColumns(20);
        taGaoPositionInfo.setRows(5);
        taGaoPositionInfo.setToolTipText("Position information");
        taGaoPositionInfo.setEnabled(false);
        jScrollPane7.setViewportView(taGaoPositionInfo);

        pScripting.add(jScrollPane7);

        taGaoBoundingBoxInfo.setColumns(20);
        taGaoBoundingBoxInfo.setRows(5);
        taGaoBoundingBoxInfo.setToolTipText("Bounding box information");
        taGaoBoundingBoxInfo.setEnabled(false);
        jScrollPane8.setViewportView(taGaoBoundingBoxInfo);

        pScripting.add(jScrollPane8);

        sourceWOWList.setText("Source WOW list");
        sourceWOWList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceWOWListActionPerformed(evt);
            }
        });

        taInfo.setEditable(false);
        taInfo.setColumns(20);
        taInfo.setLineWrap(true);
        taInfo.setRows(5);
        jScrollPane4.setViewportView(taInfo);

        tfGaoKeyAndName.setEditable(false);

        pButtonContainer.setLayout(new java.awt.GridLayout(4, 3));

        btRenderGeometry.setText("Render geometry");
        btRenderGeometry.setToolTipText("Using with CTRL key, it shows skin (SKN) information");
        btRenderGeometry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRenderGeometryActionPerformed(evt);
            }
        });
        pButtonContainer.add(btRenderGeometry);

        btViewBonesInfoOrShapeMatrices.setText("View bones info/shape matrices");
        btViewBonesInfoOrShapeMatrices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btViewBonesInfoOrShapeMatricesActionPerformed(evt);
            }
        });
        pButtonContainer.add(btViewBonesInfoOrShapeMatrices);

        btViewGroup.setText("View Group members");
        btViewGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btViewGroupActionPerformed(evt);
            }
        });
        pButtonContainer.add(btViewGroup);

        btViewActionOrEventGAOGroupMembers.setText("View Action/Event GAO members");
        btViewActionOrEventGAOGroupMembers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btViewActionOrEventGAOGroupMembersActionPerformed(evt);
            }
        });
        pButtonContainer.add(btViewActionOrEventGAOGroupMembers);

        btEvent.setText("View Event");
        btEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEventActionPerformed(evt);
            }
        });
        pButtonContainer.add(btEvent);

        btViewAnimationShapeData.setText("View animation shape data ");
        btViewAnimationShapeData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btViewAnimationShapeDataActionPerformed(evt);
            }
        });
        pButtonContainer.add(btViewAnimationShapeData);

        btRenderWorld.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btRenderWorld.setText("Render World");
        btRenderWorld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRenderWorldActionPerformed(evt);
            }
        });
        pButtonContainer.add(btRenderWorld);

        btModifierView.setText("View modifier summary");
        btModifierView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btModifierViewActionPerformed(evt);
            }
        });
        pButtonContainer.add(btModifierView);

        btViewCollision.setText("View Collision Info");
        btViewCollision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btViewCollisionActionPerformed(evt);
            }
        });
        pButtonContainer.add(btViewCollision);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sourceWOWList))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                            .addComponent(tfGaoKeyAndName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pButtonContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(pScriptingRadioButtonContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(73, 73, 73))
                                    .addComponent(btClose, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addComponent(pScripting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbGaoOtherFlags, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lTitle)
                    .addComponent(sourceWOWList))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(pScripting, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lbGaoOtherFlags, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(tfGaoKeyAndName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pButtonContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(89, 89, 89)
                            .addComponent(btClose))
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pScriptingRadioButtonContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btCloseActionPerformed

    private Integer getTexturePackKey() {
        if (bfdfcTex == null) {
            return null;
        }
        return bfdfcTex.getKey();
    }

    private void showAnimationShapeInfo(BFAnimationShapeChunk asc) {
        if (asc == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Animation Shape information\n\n");
        int i;
        Byte boneIDs[] = asc.getBoneIDArray();
        Byte ids[] = asc.getIDForOFCArray();
        Byte groupElemIDs[] = asc.getGroupElemIDArray();
        String groupElementSpecialMeaningDesc;
        for (i = 0; i < asc.getElemNum(); i++) {
            if (groupElemIDs[i].equals((byte) -3)) {
                groupElementSpecialMeaningDesc = "(This GAO must be hidden)";
            } else if (groupElemIDs[i].equals((byte) -2)) {
                groupElementSpecialMeaningDesc = "(This GAO must be show)";
            } else {
                groupElementSpecialMeaningDesc = "";
            }
            sb.append(String.format("boneID: %d (%x), ID: %d (%x), groupElementID"
                    + ": %d (%x) %s\n", boneIDs[i], boneIDs[i], ids[i], ids[i], groupElemIDs[i], groupElemIDs[i], groupElementSpecialMeaningDesc));
        }
        JTextArea textArea = new JTextArea(sb.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(null, scrollPane, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSKNInfo(GAOInnerParserGeometry geo) {
        if (geo == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Skin (SKN) information\n\n");
        sb.append("Unk1: ").append(geo.getUnk1()).append("\n");
        int i;
        if (geo.getSkinBoneIDs() != null) {
            Short skinBoneIDs[] = geo.getSkinBoneIDs();
            Matrix4x4 mtxArray[] = geo.getSkinMatrices();
            ArrayList<Integer> sknWeightArr[] = geo.getSknWeightArrayPacked();
            for (i = 0; i < skinBoneIDs.length; i++) {
                sb.append(i).append(". ").append("MultiPart gao group elem id (skinBoneID): ").append(skinBoneIDs[i]).append("\nSKN Weight Array elem num: ").append(sknWeightArr[i].size()).append("\n");
                sb.append(mtxArray[i]).append("\n");
            }
        }
        JTextArea textArea = new JTextArea(sb.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(null, scrollPane, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void btRenderGeometryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRenderGeometryActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }
        if (rendererOpen) {
            return;
        }
        rendererOpen = true;
        BFGaoChunk gao = (BFGaoChunk) obj;
        if ((gao.getFlags() & 0x4000) != 0) {
            BFGaoInnerChunkParser geoICToRender = RenderUtils.getGeometryICTFromLODGAO(gao);

            if (geoICToRender != null && geoICToRender.getGaoInnerType() != null
                    && geoICToRender.getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_GEOMETRY)) {
                // CTRL shows SKN info
                if ((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
                    showSKNInfo((GAOInnerParserGeometry) geoICToRender.getGaoInner());
                    rendererOpen = false;
                    return;
                }
            } else if (geoICToRender != null && geoICToRender.getGaoInnerType() != null
                    && geoICToRender.getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR)) {
            } else {
                rendererOpen = false;
                return;
            }
        } else if ((gao.getFlags() & 0x2) == 0) {
            rendererOpen = false;
            return;
        }
        Renderer r = new Renderer(this);
        if (r.getInitError() != null) {
            JOptionPane.showMessageDialog(this, "<html><b>Renderer initialization error (mostly caused by missing support of OpenGL 4):</b><br/>" + r.getInitError(), "Error", JOptionPane.ERROR_MESSAGE);
            rendererOpen = false;
            return;
        }
        r.setBfdfc(bfdfc);
        r.setBfwc(bfwc);
        r.setWindowNameExt(gao.getGaoName());
        r.setTextureDatabase(td);
        r.setTexturePackKey(getTexturePackKey());
        r.setGaoRenderingForcefully(true);
        r.setUseGAOPosition(false);
        r.setSkinComputeEnabled((gao.getFlags() & 0x2) != 0); // skin compute is only enabled for multipart gao containers in single gao mode
        r.setBonePositionerEnabled(true);
        r.setDummyRealTimeLightingEnabled(true);
        r.setIkEnabled(true);
        r.renderAGao(gao);
    }//GEN-LAST:event_btRenderGeometryActionPerformed

    private void btViewGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btViewGroupActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }
        BFKeyListElem grpKLE = ((BFGaoChunk) obj).getGroupChunkKLE();
        if (grpKLE == null || grpKLE.getParseHandler() == null) {
            return;
        }
        Object grpGaoCont = grpKLE.getParseHandler();
        if (!(grpGaoCont instanceof BFGRPContainerChunk)) {
            return;
        }
        BFGRPChunk grpGao = ((BFGRPContainerChunk) grpGaoCont).getGRP();
        GRPBrowser gb = new GRPBrowser(this, true, bfdfc, grpGao);
        gb.setVisible(true);
    }//GEN-LAST:event_btViewGroupActionPerformed

    private void btViewActionOrEventGAOGroupMembersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btViewActionOrEventGAOGroupMembersActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }
        BFKeyListElem grpKLE = ((BFGaoChunk) obj).getBonesGroupChunkInstanceKLE();
        if (grpKLE == null || grpKLE.getParseHandler() == null) {
            return;
        }
        Object grpGaoCont = grpKLE.getParseHandler();
        if (!(grpGaoCont instanceof BFGRPContainerChunk)) {
            return;
        }
        BFGRPChunk grpGao = ((BFGRPContainerChunk) grpGaoCont).getGRP();
        GRPBrowser gb = new GRPBrowser(this, true, bfdfc, grpGao);
        gb.setVisible(true);
    }//GEN-LAST:event_btViewActionOrEventGAOGroupMembersActionPerformed

    private void lScriptingListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lScriptingListValueChanged
        lmScriptingDetails.clear();
        if (lScriptingList.getSelectedIndex() == -1) {
            return;
        }
        if (lmScriptingList.get(lScriptingList.getSelectedIndex()) instanceof BFScriptingVariablesChunk) {
            BFScriptingVariablesChunk vals = (BFScriptingVariablesChunk) lmScriptingList.get(lScriptingList.getSelectedIndex());
            int i;
            for (i = 0; i < vals.getTypeElems().size(); i++) {
                lmScriptingDetails.addElement(vals.getVariableNames().get(i) + ", Offset: " + String.format("0x%x (%d)", vals.getTypeElems().get(i).getMemoryOffset(), vals.getTypeElems().get(i).getMemoryOffset())
                        + String.format(", Type: 0x%x", vals.getTypeElems().get(i).getTypeOrig()));
            }

        }

    }//GEN-LAST:event_lScriptingListValueChanged

    private void btScriptingVFArraysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btScriptingVFArraysActionPerformed
        trGaoListValueChanged(null);
    }//GEN-LAST:event_btScriptingVFArraysActionPerformed

    private void btScriptingVariableOverridesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btScriptingVariableOverridesActionPerformed
        trGaoListValueChanged(null);
    }//GEN-LAST:event_btScriptingVariableOverridesActionPerformed

    private void sourceWOWListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceWOWListActionPerformed
        ArrayList<BFKeyListElem> al = bfdfc.getParsedWOWsKLE();
        if (al == null) {
            return;
        }

        int i;
        StringBuilder sb = new StringBuilder();
        sb.append("This merged WOW has been created from these WOWs:\n\n");
        for (i = 0; i < al.size(); i++) {
            sb.append(((BFWOWChunk) (al.get(i).getParseHandler())).getWowNameOriginal());
            sb.append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Information", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_sourceWOWListActionPerformed

    private void btModifierViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btModifierViewActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }

        BFGaoChunk gao = (BFGaoChunk) obj;

        if (gao.getHasMDF() != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("This GAO contains these modifiers:\n\n");
            ArrayList<BFChunkParseInterface> al = gao.getModifiersArray();
            Iterator it = al.iterator();
            while (it.hasNext()) {
                BFChunkParseInterface mdf = (BFChunkParseInterface) it.next();
                sb.append("--").append(System.lineSeparator());
                sb.append("Name: ").append(mdf.getClass().getName()).append(System.lineSeparator());
                sb.append(String.format("HandleFlags: %x", ((BFModifierBaseClass) mdf).getHandleFlags())).append(System.lineSeparator());
                if (mdf instanceof BFModifierSpriteGenModifier
                        || mdf instanceof BFModifierGAOInverseKinematics
                        || mdf instanceof BFModifierGAOSpecialLookAt
                        || mdf instanceof BFModifierMeshWave
                        || mdf instanceof BFModifierParticleGenerator
                        || mdf instanceof BFModifierGeoColorModifier
                        || mdf instanceof BFModifierGeoTransformedVerticesSnapshotStore
                        || mdf instanceof BFModifierSnake
                        || mdf instanceof BFModifierLazyModifier
                        || mdf instanceof BFModifierXMECModifier) { // TODO: remove this check, when every modifier will have overrided toString()
                    sb.append(mdf);
                }
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btModifierViewActionPerformed

    private void btEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEventActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }

        BFGaoChunk gao = (BFGaoChunk) obj;

        if (gao.getUnk5eventsChunkKLE() != null && gao.getUnk5eventsChunkKLE().getParseHandler() != null
                && gao.getUnk5eventsChunkKLE().getParseHandler() instanceof BFEventsChunk) {
            BFEventsChunk evc = (BFEventsChunk) gao.getUnk5eventsChunkKLE().getParseHandler();
            if (evc.getEventTracks() != null) {
                EventViewer ev = new EventViewer(this, true, evc.getEventTracks(), gao.getGaoName());
                ev.setVisible(true);
            }
        } else if (gao.getEventsChunkKLE() != null && gao.getEventsChunkKLE().getParseHandler() != null // Events [without actions]
                && (gao.getActionsKey().equals(0) || gao.getActionsKey().equals(-1))
                && gao.getEventsChunkKLE().getParseHandler() instanceof BFEventsChunk) {
            BFEventsChunk evc = (BFEventsChunk) gao.getEventsChunkKLE().getParseHandler();
            if (evc.getEventTracks() != null) {
                EventViewer ev = new EventViewer(this, true, evc.getEventTracks(), gao.getGaoName());
                ev.setVisible(true);
            }
        }

    }//GEN-LAST:event_btEventActionPerformed

    private void lActionListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lActionListValueChanged
        // TODO: no code reuse, should cleanup: trGaoListMouseClicked
        lmActionElemList.clear();
        if (lActionList.getSelectedIndex() == -1) {
            return;
        }
        int sel = lActionList.getSelectedIndex();

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }

        BFGaoChunk gao = (BFGaoChunk) obj;
        if (gao.getActionsKey() != null && gao.getActionsKey() != 0
                && gao.getActionsKey() != -1 && gao.getActionListChunkKLE() != null
                && gao.getActionListChunkKLE().getParseHandler() != null
                && gao.getActionListChunkKLE().getParseHandler() instanceof BFActionListChunk) {
            BFActionListChunk alc = (BFActionListChunk) gao.getActionListChunkKLE().getParseHandler();
            if (alc.getActionKeys() != null && alc.getActionsKLE() != null) {
                ArrayList<Integer> actionKeyList = alc.getActionKeys();
                ArrayList<BFKeyListElem> actionListKLE = alc.getActionsKLE();
                Integer key = actionKeyList.get(sel);
                if (key == 1 || key == 0) {
                    return;
                }
                BFKeyListElem kle = actionListKLE.get(sel);
                if (kle == null || kle.getParseHandler() == null
                        || !(kle.getParseHandler() instanceof BFActionChunk)) {
                    return;
                }
                BFActionChunk ac = (BFActionChunk) kle.getParseHandler();
                ArrayList<ActionsElemStruct> acs = ac.getAesList();
                if (acs == null) {
                    return;
                }
                Iterator it = acs.iterator();
                while (it.hasNext()) {
                    lmActionElemList.addElement(it.next());
                }
            }
        }
    }//GEN-LAST:event_lActionListValueChanged

    private void lActionElemListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lActionElemListMouseClicked
        if ((evt.getModifiers() & MouseEvent.CTRL_MASK) != 0 && !evt.isConsumed()) {
            evt.consume();
            if (lActionElemList.getSelectedIndex() != -1
                    && lmActionElemList.getElementAt(lActionElemList.getSelectedIndex()) != null
                    && lmActionElemList.getElementAt(lActionElemList.getSelectedIndex()) instanceof ActionsElemStruct) {
                ActionsElemStruct aus = (ActionsElemStruct) lmActionElemList.getElementAt(lActionElemList.getSelectedIndex());
                if (aus.getAnimationShapeKey() == null || aus.getAnimationShapeKey() == -1 || aus.getAnimationShapeKey() == 0) {
                    return;
                }
                Object obj = bfdfc.getParsedObjectDatabaseClass().getObject(aus.getAnimationShapeKey());
                if (obj != null || obj instanceof BFAnimationShapeChunk) {
                    showAnimationShapeInfo((BFAnimationShapeChunk) obj);
                }
            }
            return;
        }
        if ((evt.getModifiers() & MouseEvent.ALT_MASK) != 0 && !evt.isConsumed()) {
            evt.consume();
            if (lActionElemList.getSelectedIndex() != -1
                    && lmActionElemList.getElementAt(lActionElemList.getSelectedIndex()) != null
                    && lmActionElemList.getElementAt(lActionElemList.getSelectedIndex()) instanceof ActionsElemStruct) {
                ActionsElemStruct aus = (ActionsElemStruct) lmActionElemList.getElementAt(lActionElemList.getSelectedIndex());
                if (!(aus.getTransitionDataArrMemOffset().equals(0))) {
                    ActionsElemTransitionStruct ats = aus.getATS();
                    if (ats != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("ActionsElemTransitionStruct count: ").append(ats.getCount()).append(", unk2: ").append(ats.getUnk2()).append("\n---\n");
                        ArrayList<ActionsElemTransitionStructInside1> atsi1 = ats.getATSi1Array();
                        if (atsi1 != null) {
                            for (ActionsElemTransitionStructInside1 atsi1e : atsi1) {
                                if (atsi1e == null) {
                                    continue;
                                }
                                sb.append("actionID (to find): ").append(atsi1e.getActionID()).append("\n");
                            }
                        }
                        ArrayList<ActionsElemTransitionStructInside2> atsi2 = ats.getATSi2Array();
                        if (atsi2 != null) {
                            for (ActionsElemTransitionStructInside2 atsi2e : atsi2) {
                                if (atsi2e == null) {
                                    continue;
                                }
                                sb.append("actionID (to play tansition): ").append(atsi2e.getActionID()).append(", Flags: ").
                                        append(String.format("%d (%x)", atsi2e.getFlags(), atsi2e.getFlags())).append(", Length: ").
                                        append(String.format("%d (%x)", atsi2e.getLength(), atsi2e.getLength())).append("\n");
                            }
                        }
                        JOptionPane.showMessageDialog(this, sb, "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
            return;
        }
        if (evt.getClickCount() == 2 && !evt.isConsumed()) {
            evt.consume();
            if (lActionElemList.getSelectedIndex() != -1
                    && lmActionElemList.getElementAt(lActionElemList.getSelectedIndex()) != null
                    && lmActionElemList.getElementAt(lActionElemList.getSelectedIndex()) instanceof ActionsElemStruct) {
                ActionsElemStruct aus = (ActionsElemStruct) lmActionElemList.getElementAt(lActionElemList.getSelectedIndex());
                if (aus.getEventsKey() == null || aus.getEventsKey() == -1 || aus.getEventsKey() == 0) {
                    return;
                }
                Object obj = bfdfc.getParsedObjectDatabaseClass().getObject(aus.getEventsKey());
                if (obj != null || obj instanceof BFEventsChunk) {

                    BFEventsChunk evc = (BFEventsChunk) obj;
                    if (evc.getEventTracks() != null) {
                        EventViewer ev = new EventViewer(this, true, evc.getEventTracks(), null);
                        ev.setVisible(true);
                    }
                }
            }
        }
    }//GEN-LAST:event_lActionElemListMouseClicked

    private String _getAniCtrlFlagsMeaning(BFGaoChunk gao) {
        int i = 0;
        short flags = gao.getAnimationControlFlags();
        StringBuffer sb = new StringBuffer();
        while (flags != 0 && i < 32) {
            if ((flags & (1 << i)) != 0) {
                sb.append(String.format("[%xh: %s]", (1 << i),
                        gao.getGaoAnimationControlFlagBitMeaning((1 << i)))
                );
                flags -= (1 << i);
            }
            i++;
        }
        return sb.toString();
    }

    private GAOInnerParserLight getLightFromGao(BFGaoChunk gao) {
        Object obj1;
        if (gao.getLightKey() != null && !gao.getLightKey().equals(-1) && bfdfc.getParsedObjectDatabaseClass().hasKey(gao.getLightKey())
                && (obj1 = bfdfc.getParsedObjectDatabaseClass().getObject(gao.getLightKey())) instanceof BFGaoInnerChunkParser) {
            if (((BFGaoInnerChunkParser) obj1).getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_LIGHT)) {
                return (GAOInnerParserLight) ((BFGaoInnerChunkParser) obj1).getGaoInner();
            }
        }
        return null;
    }

    private void trGaoListValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_trGaoListValueChanged
        lmScriptingList.clear();
        lmScriptingDetails.clear();
        lmActionList.clear();
        lmActionElemList.clear();
        // INFO label display
        taInfo.setBackground(Color.WHITE);
        taInfo.setText("");
        // Position Info
        taGaoPositionInfo.setText("");
        // Bounding box info
        taGaoBoundingBoxInfo.setText("");
        // gao other + designFlags1 and designFlags2
        lbGaoOtherFlags.setText("");
        lbGaoOtherFlags.setToolTipText("");
        // gao key and name
        tfGaoKeyAndName.setText("");
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }
        BFGaoChunk gao = (BFGaoChunk) obj;
        if (gao.getScriptingContainerKey() != 0 && gao.getScriptingContainerKey() != -1) {
            Object scriptingGaoCont = bfdfc.getParsedObjectDatabaseClass().getObject(gao.getScriptingContainerKey());
            if (scriptingGaoCont == null || !(scriptingGaoCont instanceof BFScriptingContainerChunk)) {
                return;
            }
            BFScriptingContainerChunk scriptingGaoCont1 = (BFScriptingContainerChunk) scriptingGaoCont;
            if (btScriptingVFArrays.isSelected()) {
                if (scriptingGaoCont1.getFunctionsAndVariablesKey() != 0 && scriptingGaoCont1.getFunctionsAndVariablesKey() != -1) {
                    Object scriptingGaoVFArrays = bfdfc.getParsedObjectDatabaseClass().getObject(scriptingGaoCont1.getFunctionsAndVariablesKey());
                    if (scriptingGaoVFArrays == null || !(scriptingGaoVFArrays instanceof BFScriptingVariablesAndFunctionCodeKeyCollection)) {
                        return;
                    }
                    BFScriptingVariablesAndFunctionCodeKeyCollection scriptingGaoVFArrays1 = (BFScriptingVariablesAndFunctionCodeKeyCollection) scriptingGaoVFArrays;
                    ArrayList<Integer> al = scriptingGaoVFArrays1.getScriptingKeys();
                    int i;
                    for (i = 0; i < al.size(); i++) {
                        if (al.get(i) == -1 || al.get(i) == 0) {
                            continue;
                        }
                        Object o = bfdfc.getParsedObjectDatabaseClass().getObject(al.get(i));
                        if (o != null) {
                            lmScriptingList.addElement(o);
                        }
                    }
                }
            } else {
                if (scriptingGaoCont1.getVariableOverridesKey() != 0 && scriptingGaoCont1.getVariableOverridesKey() != -1) {
                    Object o = bfdfc.getParsedObjectDatabaseClass().getObject(scriptingGaoCont1.getVariableOverridesKey());
                    if (o != null) {
                        lmScriptingList.addElement(o);
                    }
                }
            }
        }
        if (gao.getActionsKey() != null && gao.getActionsKey() != 0
                && gao.getActionsKey() != -1 && gao.getActionListChunkKLE() != null
                && gao.getActionListChunkKLE().getParseHandler() != null
                && gao.getActionListChunkKLE().getParseHandler() instanceof BFActionListChunk) {
            BFActionListChunk alc = (BFActionListChunk) gao.getActionListChunkKLE().getParseHandler();
            if (alc.getActionKeys() != null && alc.getActionsKLE() != null) {
                ArrayList<Integer> actionKeyList = alc.getActionKeys();
                ArrayList<BFKeyListElem> actionListKLE = alc.getActionsKLE();
                Iterator it = actionKeyList.iterator();
                int i = 0;
                BFActionChunk ac;
                String extra;
                while (it.hasNext()) {
                    if (actionListKLE != null && actionListKLE.get(i) != null && (ac = (BFActionChunk) (actionListKLE.get(i).getParseHandler())) != null) {
                        extra = ", Loop start elem: " + ac.getLoopStartElem();
                    } else {
                        extra = "";
                    }
                    lmActionList.addElement(String.format("Action Key: %x%s", (Integer) (it.next()), extra));
                    i++;
                }
            }
        }

        // INFO label display
        Object obj1;
        GAOInnerParserLight ipl = getLightFromGao(gao);
        if (ipl != null) {
            taInfo.setText("Lighting:" + System.lineSeparator() + ipl.toString());
            taInfo.setBackground(new Color(Convert.BGRToRGB(ipl.getColor())));
        }
        if (gao.getGeoKey() != null && !gao.getGeoKey().equals(-1) && bfdfc.getParsedObjectDatabaseClass().hasKey(gao.getGeoKey())
                && (obj1 = bfdfc.getParsedObjectDatabaseClass().getObject(gao.getGeoKey())) instanceof BFGaoInnerChunkParser) {
            if (((BFGaoInnerChunkParser) obj1).getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_CAMERA)) {
                GAOInnerParserCamera gipl = (GAOInnerParserCamera) ((BFGaoInnerChunkParser) obj1).getGaoInner();
                taInfo.setText("Camera:" + System.lineSeparator() + taInfo.getText() + System.lineSeparator() + gipl.toString());
            }
            if (((BFGaoInnerChunkParser) obj1).getGaoInnerType().equals(BFGaoInnerChunkParser.BF_GAO_TYPE_PARTICLEGENERATOR)) {
                GAOInnerParserParticleGenerator gipl = (GAOInnerParserParticleGenerator) ((BFGaoInnerChunkParser) obj1).getGaoInner();
                taInfo.setText("ParticleGen:" + System.lineSeparator() + taInfo.getText() + System.lineSeparator() + gipl.toString());
            }
        }

        StringBuilder positionSB = new StringBuilder();

        // Position Info
        RenderUtils.getMatricesDescription(gao, positionSB);
        taGaoPositionInfo.setText(positionSB.toString());

        StringBuilder boundingBoxSB = new StringBuilder();

        // Bounding box info
        boundingBoxSB.append("Bounding Box\n");
        if (gao.getBoundingBox1() != null) {
            boundingBoxSB.append(gao.getBoundingBox1()).append("\n");
        }
        if (gao.getBoundingBox2() != null) {
            boundingBoxSB.append(gao.getBoundingBox2()).append("\n");
        }
        if (gao.getFlags() != null && (gao.getFlags() & 0x80000) != 0) {
            if (gao.getBoundingBox3() != null) {
                boundingBoxSB.append(gao.getBoundingBox3()).append("\n");
            }
            if (gao.getBoundingBox4() != null) {
                boundingBoxSB.append(gao.getBoundingBox4()).append("\n");
            }
        }
        if (gao.getBoundingBox2() != null) {
            if (gao.getBoundingBox2().getZ().equals(-1)) {
                boundingBoxSB.append("This is a Spherical Bounding Box.\n");
            } else {
                boundingBoxSB.append("This is a Axis Aligned Bounding Box.\n");
            }
        }
        taGaoBoundingBoxInfo.setText(boundingBoxSB.toString());

        String tmp = "";

        // gao other + designFlags and unk2g
        if ((gao.getFlags() & 0x2000) != 0) {
            tmp = (String.format("unk5f1: %f, renderMinReqDistSq: %f, Cap: %d (%x), unk5b1: %d (%x), unk5b2: %x (%x), aniCtrlFlags: %x %s",
                    gao.getUnk5f1(), gao.getRenderMinReqDistSq(), gao.getCapacity(), gao.getCapacity(), gao.getUnk5b1(),
                    gao.getUnk5b1(), gao.getUnk5b2(), gao.getUnk5b2(), gao.getAnimationControlFlags(), _getAniCtrlFlagsMeaning(gao)));
        }

        if (gao.getDesignFlags1() != null) {
            if (tmp.length() != 0) {
                tmp += ", ";
            }
            tmp += String.format("DesignFlags1: %x [%x][%s], DesignFlags2: %x [%d, ZListId: %d]", gao.getDesignFlags1(), (gao.getDesignFlags1() >> 8) & 0xff,
                    ((gao.getDesignFlags1() & 0x100000) != 0 ? "S+T" : "T"), gao.getDesignFlags2(), gao.getZListBufferNum(false), gao.getZListBufferNum(true)); // S = Sprite, T = triangle
        }
        if (tmp.length() != 0) {
            lbGaoOtherFlags.setText(tmp);
            lbGaoOtherFlags.setToolTipText(tmp);
        }

        // gao key and name
        tfGaoKeyAndName.setText(gao.getGaoName() + ", Key (hex): " + String.format("%x", gao.getSelfKey()));
    }//GEN-LAST:event_trGaoListValueChanged

    private void btViewBonesInfoOrShapeMatricesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btViewBonesInfoOrShapeMatricesActionPerformed
        // skeleton related (bones)
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }
        BFGaoChunk gao = (BFGaoChunk) obj;

        if ((gao.getFlags() & 0x200000) == 0 || (gao.getBoneShapes() == null && gao.getBoneShapeMatrices() == null)) {
            return;
        }

        BonesInfoOrShapeMatricesBrowser biosm = new BonesInfoOrShapeMatricesBrowser(this, true, bfdfc, gao);
        biosm.setVisible(true);
    }//GEN-LAST:event_btViewBonesInfoOrShapeMatricesActionPerformed

    private void btViewAnimationShapeDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btViewAnimationShapeDataActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object obj = node.getUserObject();
        if (!(obj instanceof BFGaoChunk)) {
            return;
        }
        BFGaoChunk gao = (BFGaoChunk) obj;

        if ((gao.getAnimationShapeChunkInstanceSpecificKLE() != null && gao.getAnimationShapeChunkInstanceSpecificKLE().getParseHandler() != null
                && gao.getAnimationShapeChunkInstanceSpecificKLE().getParseHandler() instanceof BFAnimationShapeChunk)) {
            showAnimationShapeInfo((BFAnimationShapeChunk) gao.getAnimationShapeChunkInstanceSpecificKLE().getParseHandler());
        }
    }//GEN-LAST:event_btViewAnimationShapeDataActionPerformed

    private void trGaoListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_trGaoListMouseClicked

        if (evt.getModifiers() == MouseEvent.BUTTON3_MASK) {
            int x = evt.getX();
            int y = evt.getY();
            gaoListMenu.show(trGaoList, x, y);
            gaoListMenu.repaint();
            return;
        } else {
            gaoListMenu.setVisible(false);
        }

        if ((evt.getModifiers() & (MouseEvent.CTRL_MASK | MouseEvent.ALT_MASK)) != 0) {
            int i = 0;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }
            Object obj = node.getUserObject();
            if (!(obj instanceof BFGaoChunk)) {
                return;
            }
            BFGaoChunk gao = (BFGaoChunk) obj;

            if (gao.getFlags() == null) {
                return;
            }

            StringBuilder sb = new StringBuilder();

            int flags = (evt.getModifiers() & (MouseEvent.CTRL_MASK)) != 0 ? gao.getFlags() : gao.getVisualFlags();

            while (flags != 0 && i < 32) {
                if ((flags & (1 << i)) != 0) {
                    sb.append(String.format("Has: %8xh, %s", (1 << i),
                            ((evt.getModifiers() & (MouseEvent.CTRL_MASK)) != 0
                            ? gao.getGaoFlagBitMeaning((1 << i))
                            : gao.getGaoVisualFlagBitMeaning((1 << i))))
                    ).append("\n");
                    flags -= (1 << i);
                }
                i++;
            }

            JOptionPane.showMessageDialog(this, sb.toString(), "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_trGaoListMouseClicked

    private void btRenderWorldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRenderWorldActionPerformed
        TreeMap<Integer, BFKeyListElem> tm;
        if (rendererOpen) {
            return;
        }
        rendererOpen = true;
        Renderer r = new Renderer(this);
        if (r.getInitError() != null) {
            JOptionPane.showMessageDialog(this, "<html><b>Renderer initialization error (mostly caused by missing support of OpenGL 4):</b><br/>" + r.getInitError(), "Error", JOptionPane.ERROR_MESSAGE);
            rendererOpen = false;
            return;
        }
        r.setBfdfc(bfdfc);
        r.setBfwc(bfwc);
        r.setTextureDatabase(td);
        r.setTexturePackKey(getTexturePackKey());
        r.setGaoRenderingForcefully(false);
        r.setUseGAOPosition(true); // This is default.
        r.setSkinComputeEnabled(true);
        r.setBonePositionerEnabled(false);
        r.setIkEnabled(true);
        r.setWindowNameExt("World (" + bfwc.getWowName() + ")");
        if (bfwc != null) {
            r.setClearColor(bfwc.getClearColor());
        }
        r.setDummyRealTimeLightingEnabled(false);
        Iterator it = bfwc.getGaoList().getGaos().iterator();
        while (it.hasNext()) {
            tm = (TreeMap<Integer, BFKeyListElem>) it.next();
            r.addGao((BFGaoChunk) (((BFKeyListElem) (tm.firstEntry().getValue())).getParseHandler()));
        }
        r.renderAllGao();
    }//GEN-LAST:event_btRenderWorldActionPerformed

    private void lScriptingDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lScriptingDetailsMouseClicked
        if (lScriptingList.getSelectedIndex() == -1 || lmScriptingDetails.isEmpty()) {
            return;
        }
        if (evt.getModifiers() == MouseEvent.BUTTON3_MASK) {
            int x = evt.getX();
            int y = evt.getY();
            scriptingDetailsMenu.show(lScriptingDetails, x, y);
            scriptingDetailsMenu.repaint();
        } else {
            scriptingDetailsMenu.setVisible(false);
        }

        if (evt.getModifiers() == MouseEvent.BUTTON1_MASK && evt.getClickCount() == 2) {
            if (lScriptingDetails.getSelectedIndex() == -1) {
                return;
            }
            if (!(lmScriptingList.get(lScriptingList.getSelectedIndex()) instanceof BFScriptingVariablesChunk)) {
                return;
            }
            BFScriptingVariablesChunk vals = (BFScriptingVariablesChunk) lmScriptingList.get(lScriptingList.getSelectedIndex());
            showScriptingDefaultValue(vals, lScriptingDetails.getSelectedIndex());
        }
    }//GEN-LAST:event_lScriptingDetailsMouseClicked

    private void showScriptingDefaultValue(BFScriptingVariablesChunk vals, int index) {
        int i, j, k;
        if (vals == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        if (vals.getTypeElems() != null && vals.getTypeElems().size() > index) {
            sb.append("Scripting default value for ").append(vals.getVariableNames().get(index)).
                    append(String.format("\nType: 0x%x (Resolved to array id: %d)\n\n", vals.getTypeElems().get(index).getTypeOrig(), vals.getTypeElems().get(index).getType()));
            OVATypeElem te = vals.getTypeElems().get(index);
            Object obj;
            if (!te.isArray()) {
                obj = vals.getDefaultValue(index);
                if (obj instanceof Integer) {
                    sb.append(String.format("int : %d (0x%x)\n", obj, obj));
                } else {
                    sb.append(obj).append("\n");
                }
                if (Scripting.isValAReference(te.getTypeOrig())) {
                    sb.append("Resolved to: ").append(vals.getDefaultValueResolved(index)).append("\n");
                }
            } else {
                switch (te.getArrayDimension()) {
                    case 0x3:
                        for (i = 0; i < vals.getArrayFirstDimension(index); i++) {
                            for (j = 0; j < vals.getArraySecondDimension(index); j++) {
                                for (k = 0; k < vals.getArrayThirdDimension(index); k++) {
                                    obj = vals.getDefaultThreeDimensionalArrayValue(index, i, j, k);
                                    if (obj instanceof Integer) {
                                        sb.append(String.format("[%d, %d, %d] : %d (0x%x)\n", i, j, k, obj, obj));
                                    } else {
                                        sb.append(String.format("[%d, %d, %d] : %s\n", i, j, k, obj));
                                    }
                                    if (Scripting.isValAReference(te.getTypeOrig())) {
                                        sb.append("Resolved to: ").append(vals.getDefaultThreeDimensionalArrayValueResolved(index, i, j, k)).append("\n");
                                    }
                                }
                            }
                        }
                        break;
                    case 0x2:
                        for (i = 0; i < vals.getArrayFirstDimension(index); i++) {
                            for (j = 0; j < vals.getArraySecondDimension(index); j++) {
                                obj = vals.getDefaultTwoDimensionalArrayValue(index, i, j);
                                if (obj instanceof Integer) {
                                    sb.append(String.format("[%d, %d] : %d (0x%x)\n", i, j, obj, obj));
                                } else {
                                    sb.append(String.format("[%d, %d] : %s\n", i, j, obj));
                                }
                                if (Scripting.isValAReference(te.getTypeOrig())) {
                                    sb.append("Resolved to: ").append(vals.getDefaultTwoDimensionalArrayValueResolved(index, i, j)).append("\n");
                                }
                            }
                        }
                        break;
                    case 0x1:
                        for (i = 0; i < vals.getArrayFirstDimension(index); i++) {
                            obj = vals.getDefaultArrayValue(index, i);
                            if (obj instanceof Integer) {
                                sb.append(String.format("[%d] : %d (0x%x)\n", i, obj, obj));
                            } else {
                                sb.append(String.format("[%d] : %s\n", i, obj));
                            }
                            if (Scripting.isValAReference(te.getTypeOrig())) {
                                sb.append("Resolved to: ").append(vals.getDefaultArrayValueResolved(index, i)).append("\n");
                            }
                        }
                        break;
                }
            }
        } else {
            sb.append("BUG: invalid index supplied.");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(null, scrollPane, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void lScriptingListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lScriptingListMouseClicked
        if (lmScriptingList.isEmpty()) {
            return;
        }
        if (evt.getModifiers() == MouseEvent.BUTTON3_MASK) {
            int x = evt.getX();
            int y = evt.getY();
            scriptingMenu.show(lScriptingList, x, y);
            scriptingMenu.repaint();
        } else {
            scriptingMenu.setVisible(false);
        }
    }//GEN-LAST:event_lScriptingListMouseClicked

    private void btViewCollisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btViewCollisionActionPerformed
        if (!IrisToolDefines.PUBLIC_RELEASE) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) trGaoList.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }
            Object obj = node.getUserObject();
            if (!(obj instanceof BFGaoChunk)) {
                return;
            }
            BFGaoChunk gao = (BFGaoChunk) obj;

            if ((gao.getFlags() & 0x700) == 0) {
                return;
            }

            StringBuilder sb = new StringBuilder();

            BFKeyListElem kle, kle2;
            int i, j;
            BFCollisionObjectChunk coc;

            if ((gao.getFlags() & 0x100) != 0) {
                sb.append("Collision map").append(System.lineSeparator());
                sb.append("-------------").append(System.lineSeparator());
                kle = gao.getCollisionMapKLE();
                if (kle != null && kle.getParseHandler() != null) {
                    BFCollisionMapGroupChunk cmgc = (BFCollisionMapGroupChunk) (kle.getParseHandler());
                    sb.append(cmgc).append(System.lineSeparator());
                    if (cmgc.getCollisionObjectChunksKLE() != null) {
                        for (i = 0; i < cmgc.getCollKeyNum(); i++) {
                            kle2 = cmgc.getCollisionObjectChunksKLE().get(i);
                            coc = (BFCollisionObjectChunk) kle2.getParseHandler();
                            if (coc != null) {
                                sb.append(coc).append(System.lineSeparator());
                                if (coc.getGameMaterialKLE() != null && coc.getGameMaterialKLE().getParseHandler() != null) {
                                    BFCollisionObjectGameMaterialChunk gmc = (BFCollisionObjectGameMaterialChunk) coc.getGameMaterialKLE().getParseHandler();
                                    sb.append(gmc).append(System.lineSeparator());
                                }
                                switch (coc.getType()) {
                                    case 1:
                                        sb.append(" Pos1: ").append(coc.getPos1()).append(System.lineSeparator());
                                        sb.append(" Pos2: ").append(coc.getPos2()).append(System.lineSeparator());
                                        break;
                                    case 2:
                                        sb.append(" Pos1: ").append(coc.getPos1()).append(System.lineSeparator());
                                        sb.append(" F1: ").append(coc.getF1()).append(System.lineSeparator());
                                        break;
                                    case 3:
                                        sb.append(" Pos1: ").append(coc.getPos1()).append(System.lineSeparator());
                                        sb.append(" F1: ").append(coc.getF1()).append(System.lineSeparator());
                                        sb.append(" F2: ").append(coc.getF2()).append(System.lineSeparator());
                                        break;
                                    case 5:
                                        sb.append(String.format(" verticesNum: %d, proximityVerticesNum: %d, triangleChunkNum: %d",
                                                coc.getCollisionVerticesNum(), coc.getProximityVerticesNum(), coc.getCollisionTringleChunkNum()));
                                        sb.append(System.lineSeparator());
                                        if ((coc.getFlags() & 0x80) != 0) {
                                            sb.append(" Has proximity indices").append(System.lineSeparator());
                                        }
                                        sb.append(" TriangleChunks: ").append(System.lineSeparator());
                                        for (i = 0; i < coc.getCollisionTringleChunkNum(); i++) {
                                            sb.append(String.format("  TriangleChunk: %d, triangleElemNum: %d, unkb1: %x (%d), unkb2: %x (%d) gmatID: %x (%d)", i,
                                                    coc.getCtc()[i].getTriangleElemNum(),
                                                    coc.getCtc()[i].getUnkb1(), coc.getCtc()[i].getUnkb1(),
                                                    coc.getCtc()[i].getUnkb2(), coc.getCtc()[i].getUnkb2(),
                                                    coc.getCtc()[i].getGmatID(), coc.getCtc()[i].getGmatID()
                                            )).append(System.lineSeparator());
                                        }
                                        if ((coc.getFlags() & 0x88) == 0x88) {
                                            sb.append(String.format(" Has collision grouped into AABoxes (makes precedence), boxesNum: %d", coc.getBoxesNum())).append(System.lineSeparator());
                                            for (i = 0; i < coc.getBoxesNum(); i++) {
                                                sb.append("  Box: ").append(i).append(System.lineSeparator());
                                                sb.append("   AABoxVec1: ").append(coc.getCollsisionBoxes()[i].getAaBoxVec1()).append(System.lineSeparator());
                                                sb.append("   AABoxVec2: ").append(coc.getCollsisionBoxes()[i].getAaBoxVec2()).append(System.lineSeparator());
                                                sb.append("   TriangleChunks: ").append(System.lineSeparator());
                                                // see comment in BFCollisionObjectChunk to how to assemble geometry from this
                                                for (j = 0; j < coc.getCollsisionBoxes()[i].getTriangleChunkNum(); j++) {
                                                    sb.append(String.format("    TriangleChunk: %d, reference triangleChunkID: %d, triangleIDElemNum: %d", j,
                                                            coc.getCollsisionBoxes()[i].getCollisionMapBoxTriangleChunks()[j].getTriangleChunkID(),
                                                            coc.getCollsisionBoxes()[i].getCollisionMapBoxTriangleChunks()[j].getTriangleIDElemNum())).append(System.lineSeparator());
                                                }
                                            }
                                        }
                                        break;
                                }
                            }
                            sb.append(" -").append(System.lineSeparator());
                        }
                    }
                }
            }
            if ((gao.getFlags() & 0x600) != 0) {
                sb.append("Collision instance").append(System.lineSeparator());
                sb.append("------------------").append(System.lineSeparator());
                kle = gao.getCollisionInstanceChunkKLE();
                CollisionInstanceElem cie[];
                if (kle != null && kle.getParseHandler() != null) {
                    BFCollisionInstanceChunk cic = (BFCollisionInstanceChunk) kle.getParseHandler();
                    sb.append(cic).append(System.lineSeparator());
                    kle2 = cic.getCollisionSetChunkKLE();
                    if (kle2 != null && kle2.getParseHandler() != null) {
                        sb.append("-- Collision set --").append(System.lineSeparator());
                        BFCollisionInstanceCollisionSetChunk cicsc = (BFCollisionInstanceCollisionSetChunk) kle2.getParseHandler();
                        sb.append(" ").append(cicsc).append(System.lineSeparator());
                        for (i = 0; i < cicsc.getCollisionInstanceOrderingIDsUsedForActivationCheck().length; i++) {
                            sb.append(String.format(" collisionInstanceOrderingIDsUsedForActivationCheck[%d]: %d (0x%x)", i, cicsc.getCollisionInstanceOrderingIDsUsedForActivationCheck()[i], cicsc.getCollisionInstanceOrderingIDsUsedForActivationCheck()[i])).append(System.lineSeparator());
                        }
                        cie = cicsc.getCie();
                        if (cie != null) {
                            for (i = 0; i < cie.length; i++) {
                                sb.append(" ").append(cie[i]).append(System.lineSeparator());
                                switch (cie[i].getType()) {
                                    case 1:
                                        sb.append(" Pos1: ").append(cie[i].getPos1()).append(System.lineSeparator());
                                        sb.append(" Pos2: ").append(cie[i].getPos2()).append(System.lineSeparator());
                                        break;
                                    case 2:
                                        sb.append(" Pos1: ").append(cie[i].getPos1()).append(System.lineSeparator());
                                        sb.append(" F1: ").append(cie[i].getF1()).append(System.lineSeparator());
                                        break;
                                    case 3:
                                        sb.append(" Pos1: ").append(cie[i].getPos1()).append(System.lineSeparator());
                                        sb.append(" F1: ").append(cie[i].getF1()).append(System.lineSeparator());
                                        sb.append(" F2: ").append(cie[i].getF2()).append(System.lineSeparator());
                                        break;
                                }
                                sb.append(" -").append(System.lineSeparator());
                            }
                        }
                    }
                    sb.append("-- Embedded collision set --").append(System.lineSeparator());
                    if (cic.getLen() != 4) {
                        if (cic.getCollisionInstanceOrderingIDs() != null) {
                            for (i = 0; i < cic.getCollisionInstanceOrderingIDs().length; i++) {
                                sb.append(String.format(" collisionInstanceOrderingIDs[%d]: %d (0x%x)", i, cic.getCollisionInstanceOrderingIDs()[i], cic.getCollisionInstanceOrderingIDs()[i])).append(System.lineSeparator());
                            }
                        }
                        cie = cic.getCie();
                        if (cie != null) {
                            for (i = 0; i < cie.length; i++) {
                                sb.append(" ").append(cie[i]).append(System.lineSeparator());
                                switch (cie[i].getType()) {
                                    case 1:
                                        sb.append(" Pos1: ").append(cie[i].getPos1()).append(System.lineSeparator());
                                        sb.append(" Pos2: ").append(cie[i].getPos2()).append(System.lineSeparator());
                                        break;
                                    case 2:
                                        sb.append(" Pos1: ").append(cie[i].getPos1()).append(System.lineSeparator());
                                        sb.append(" F1: ").append(cie[i].getF1()).append(System.lineSeparator());
                                        break;
                                    case 3:
                                        sb.append(" Pos1: ").append(cie[i].getPos1()).append(System.lineSeparator());
                                        sb.append(" F1: ").append(cie[i].getF1()).append(System.lineSeparator());
                                        sb.append(" F2: ").append(cie[i].getF2()).append(System.lineSeparator());
                                        break;
                                }
                                sb.append(" -").append(System.lineSeparator());
                            }
                        }
                    }
                }
            }

            JTextArea textArea = new JTextArea(sb.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            scrollPane.setPreferredSize(new Dimension(500, 500));
            JOptionPane.showMessageDialog(null, scrollPane, "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btViewCollisionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClose;
    private javax.swing.JButton btEvent;
    private javax.swing.JButton btModifierView;
    private javax.swing.JButton btRenderGeometry;
    private javax.swing.JButton btRenderWorld;
    private javax.swing.JRadioButton btScriptingVFArrays;
    private javax.swing.JRadioButton btScriptingVariableOverrides;
    private javax.swing.JButton btViewActionOrEventGAOGroupMembers;
    private javax.swing.JButton btViewAnimationShapeData;
    private javax.swing.JButton btViewBonesInfoOrShapeMatrices;
    private javax.swing.JButton btViewCollision;
    private javax.swing.JButton btViewGroup;
    private javax.swing.ButtonGroup btgScriptingGet;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private DefaultListModel lmActionElemList;
    private javax.swing.JList<String> lActionElemList;
    private DefaultListModel lmActionList;
    private javax.swing.JList<String> lActionList;
    private DefaultListModel lmScriptingDetails;
    private javax.swing.JList<String> lScriptingDetails;
    private DefaultListModel lmScriptingList;
    private javax.swing.JList<String> lScriptingList;
    private javax.swing.JLabel lTitle;
    private javax.swing.JLabel lbGaoOtherFlags;
    private javax.swing.JPanel pButtonContainer;
    private javax.swing.JPanel pScripting;
    private javax.swing.JPanel pScriptingRadioButtonContainer;
    private javax.swing.JButton sourceWOWList;
    private javax.swing.JTextArea taGaoBoundingBoxInfo;
    private javax.swing.JTextArea taGaoPositionInfo;
    private javax.swing.JTextArea taInfo;
    private javax.swing.JTextField tfGaoKeyAndName;
    private javax.swing.JTree trGaoList;
    // End of variables declaration//GEN-END:variables

    @Override
    public void closeCallback() {
        rendererOpen = false;
    }
}
