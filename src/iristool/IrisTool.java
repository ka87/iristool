/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool;

import BFDataFileLib.BFChunkParsers.Map.BFEventsChunk;
import BFDataFileLib.BFDataFileClass;
import Common.Convert;
import Common.IrisToolDefines;
import Common.SettingsManager.SettingsManager;
import BFlib.BFClass;
import BFlib.BFFatTable;
import BFlib.LoadedSoundsClass;
import BFlib.SPEClass;
import BFlib.TextureDatabase;
import Common.DataType.BFFilenameElem;
import iristool.Engine.Scripting;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author andras
 */
public class IrisTool extends javax.swing.JFrame {

    /**
     * Creates new form IrisTool
     */
    private BFClass bfc = null;
    private SPEClass spe = null;
    private SettingsManager sm = null;
    private File bfFile = null;
    private File speFile = null;
    private String logTypeString = null;
    private Boolean decompressUponExtract;
    private Boolean saveTexturesUponParse;
    private int nextSearchOccurrenceNumber;

    private void setWindowTitle(String s) {
        this.setTitle("IrisTool v.: " + IrisToolDefines.getIrisToolVersion() + " " + s);
    }

    public IrisTool() {
        initComponents();
        setWindowTitle("Loading...");
        IrisToolDefines.setLoggingEnabled(false);
        IrisToolDefines.setLogFile(null);

        Scripting.init();

        tbmFTEList.addColumn("Key");
        tbmFTEList.addColumn("Filename");
        tbmFTEList.addColumn("Binarized ?");
        tbmFTEList.addColumn("Compressed ?");
        tbmFTEList.addColumn("Position");

        sm = new SettingsManager("IrisTool");

        String tmp = sm.getSettingValue("decompressonextract", "1");
        if (!"0".equals(tmp) && "1".equals(tmp)) {
            tmp = "1";
        }
        decompressUponExtract = (tmp.equals("1"));
        tmp = sm.getSettingValue("dumptextures", "0");
        if (!"0".equals(tmp) && "1".equals(tmp)) {
            tmp = "1";
        }
        saveTexturesUponParse = (tmp.equals("1"));

        tmp = sm.getSettingValue("consolespecificparse", "0");
        if (!"0".equals(tmp) && "1".equals(tmp)) {
            tmp = "1";
        }
        IrisToolDefines.setConsoleVersionTextureLoad(tmp.equals("1"));
        IrisToolDefines.setPreferPalettelessTextures(tmp.equals("0"));

        sm.addSetting("decompressonextract", (decompressUponExtract ? "1" : "0"));
        sm.addSetting("dumptextures", (saveTexturesUponParse ? "1" : "0"));
        sm.addSetting("consolespecificparse", (IrisToolDefines.isConsoleVersionTextureLoad() ? "1" : "0"));

        cbDecompress.setSelected(decompressUponExtract);
        cbSaveTextures.setSelected(saveTexturesUponParse);
        cbConsoleSpecificParse.setSelected(IrisToolDefines.isConsoleVersionTextureLoad());

        logTypeString = sm.getSettingValue("logType", "none");

        if (!logTypeString.equals("none") && !logTypeString.equals("console")
                && !logTypeString.equals("file")) {
            logTypeString = "none";
        }

        sm.addSetting("logType", logTypeString);

        if ("file".equals(logTypeString)) {
            rbLogToFile.setSelected(true);
        }
        if ("console".equals(logTypeString)) {
            rbLogToConsole.setSelected(true);
        }
        if ("none".equals(logTypeString)) {
            rbLogNone.setSelected(true);
        }

        sm.SaveSettings();

        if (!tryToOpen(true)) {
            System.exit(-1);
        }

        fillBinList();

        /*
        // Test for whole bf file read
        
        for (int i= 0; i < bfc.getFatTable().getElemCount(); i++) {
            System.out.println("i="+i);
            bfc.readLengthAndDataFromPos(bfc.getFatTable().getElemByIndex(i).getPosition());
        }*/
        IrisToolDefines.setLoggingEnabled(false);
        if (IrisToolDefines.PUBLIC_RELEASE) {
            rbLogToFile.setEnabled(false);
            rbLogToConsole.setEnabled(false);
            rbLogNone.setEnabled(false);
            cbSaveTextures.setEnabled(false);
        }
        
/*        BFEventsChunk ec = new BFEventsChunk();
        if (ec.parseHack()) {
            EventViewer ev = new EventViewer(null, true, ec.getEventTracks(), "test");
            ev.setVisible(true);
        }
  */      
    }

    private File[] getXboxFiles(File bf) {
        ArrayList<File> files = new ArrayList<>();
        if (bf == null || !bf.getAbsolutePath().toLowerCase().endsWith(".bf0")) {
            return null;
        }
        files.add(bf);

        String s = bf.getAbsolutePath();
        s = s.substring(0, s.length() - 1);

        File f;

        for (int i = 1; i < 9; i++) {
            f = new File(s + i);
            if (!f.isFile() || !f.canRead()) {
                break;
            }
            files.add(f);
        }

        return files.toArray(new File[files.size()]);
    }

    public final boolean tryToOpen(boolean openSaved) {
        boolean ok = false;
        boolean nowSelected;

        String fileName = sm.getSettingValue("bf");
        String speFileName = sm.getSettingValue("spe");

        if (!openSaved) {
            fileName = null;
            speFileName = null;
        }

        do {
            nowSelected = false;
            if (fileName == null || speFileName == null) {
                if (!selectFiles()) {
                    bfc = null;
                    spe = null;
                    return false;
                }
                nowSelected = true;
            } else {
                bfFile = new File(fileName);
                speFile = new File(speFileName);
            }
            if (bfFile.getName().toLowerCase().endsWith("bf0")) {
                // XBOX splitted format
                bfc = new BFClass(getXboxFiles(bfFile));
            } else {
                bfc = new BFClass(bfFile);
            }
            if (!bfc.isOpened()) {
                JOptionPane.showMessageDialog(this, "BF file open failed.", "Error", JOptionPane.ERROR_MESSAGE);
                sm.removeSetting("bf");
                sm.removeSetting("spe");
                sm.setSettingValue("lastUsedElem", "-1");
                sm.SaveSettings();
                fileName = null;
                speFileName = null;
                continue;
            }

            BFFatTable bft = bfc.getFatTable();

            if (bft == null) {
                JOptionPane.showMessageDialog(this, "BF file fat table read failed.", "Error", JOptionPane.ERROR_MESSAGE);
                sm.removeSetting("bf");
                sm.removeSetting("spe");
                sm.setSettingValue("lastUsedElem", "-1");
                sm.SaveSettings();
                fileName = null;
                speFileName = null;
                continue;
            }

            spe = new SPEClass(speFile);
            if (!spe.isParsed()) {
                JOptionPane.showMessageDialog(this, "SPF file parse failed.", "Error", JOptionPane.ERROR_MESSAGE);
                sm.removeSetting("bf");
                sm.removeSetting("spe");
                sm.setSettingValue("lastUsedElem", "-1");
                sm.SaveSettings();
                fileName = null;
                speFileName = null;
                continue;
            }
            ok = true;

        } while (!ok);

        if (nowSelected) {
            sm.setSettingValue("lastUsedElem", "-1");
        }
        sm.addSetting("bf", bfFile.getAbsolutePath());
        sm.addSetting("spe", speFile.getAbsolutePath());
        sm.SaveSettings();

        setWindowTitle("(" + bfFile.getAbsolutePath() + ", BFID: 0x" + String.format("%x)", bfc.getBfUnkBFID()));

        return true;
    }

        public static File showBestFileChooserForPlatform(Component parent, String title) {
        if (System.getProperty("os.name").contains("Windows")) {
            java.awt.FileDialog fd = new java.awt.FileDialog((java.awt.Frame) null, title);
            fd.setVisible(true);
            if (fd.getFile() == null) {
                return null;
            }
            return new File(fd.getDirectory() + fd.getFile());
        } else {
            JFileChooser jfc = new JFileChooser();
            jfc.setDialogTitle(title);
            if (jfc.showOpenDialog(parent) == JFileChooser.CANCEL_OPTION) {
                return null;
            }
            return jfc.getSelectedFile();
        }
    }
    
    public boolean selectFiles() {
        File f = IrisTool.showBestFileChooserForPlatform(this, "Choose BF (sally_clean.bf/sally_clean.bf0/sally.bf/SALLY_PC_POLISH.bf) file");
        if (f == null) {
            return false;
        }
        bfFile = f;

        f = IrisTool.showBestFileChooserForPlatform(this, "Choose SPE (jade.spe) file");
        if (f == null) {
            return false;
        }
        speFile = f;

        return true;
    }

    private void fillBinList() {
        tbmFTEList.setRowCount(0);
        int startKey = bfc.getStartKey();
        BFFatTable bft = bfc.getFatTable();
        String lastUsedElem = sm.getSettingValue("lastUsedElem", "-1");

        int i, key;
        boolean binarized;
        String fatTableFileName;
        BFFilenameElem fne;

        for (i = 0; i < bft.getElemCount(); i++) {
            key = bft.getElemByIndex(i).getKey();
            binarized = BFDataFileClass.isBinarizedDataKey(key);
            if (IrisToolDefines.isReadFileNameListAndDirectoryList()) {
                if ((fne = bft.getFileNameElemByIndex(i)) != null && fne != null) {
                    fatTableFileName = fne.getFileName();
                } else {
                    fatTableFileName = "[BUG: filename not found]";
                }
            } else {
                fatTableFileName = "[Filename read disabled]";
            }
            tbmFTEList.addRow(new Object[]{Convert.bytesToHex(Convert.intToHexArray(key)) + ((startKey == key && startKey != -1) ? " (startkey)" : ""),
                fatTableFileName,
                binarized + (binarized ? ", " + BFDataFileClass.describeBinarizedDataKeyType(key) : ""),
                BFDataFileClass.isDataCompressedByKey(key),
                Convert.bytesToHex(Convert.intToHexArray(bft.getElemByIndex(i).getPosition().intValue()))});
        }

        int lastUsedElemIdx = -1;
        try {
            lastUsedElemIdx = Integer.parseInt(lastUsedElem);
        } catch (NumberFormatException e) {
        }

        if (lastUsedElemIdx != -1) {
            tbFTEList.setRowSelectionInterval(lastUsedElemIdx, lastUsedElemIdx);
            scrollToVisible(tbFTEList, lastUsedElemIdx, 0);
            tbFTEListMouseClicked(null);
        }

        tbSearch.requestFocus();
        nextSearchOccurrenceNumber = 0;
    }

    public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport) table.getParent();
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        viewport.scrollRectToVisible(rect);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logBtnGrp = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbmFTEList = new DefaultTableModel();
        tbFTEList = new javax.swing.JTable(){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        btExtract = new javax.swing.JButton();
        cbDecompress = new javax.swing.JCheckBox();
        btParse = new javax.swing.JButton();
        btExit = new javax.swing.JButton();
        lbUsage = new javax.swing.JLabel();
        lbApplicationTitle = new javax.swing.JLabel();
        rbLogToFile = new javax.swing.JRadioButton();
        rbLogToConsole = new javax.swing.JRadioButton();
        rbLogNone = new javax.swing.JRadioButton();
        cbSaveTextures = new javax.swing.JCheckBox();
        tbSearch = new javax.swing.JTextField();
        lbSearch = new javax.swing.JLabel();
        btAbout = new javax.swing.JButton();
        btOpen = new javax.swing.JButton();
        cbConsoleSpecificParse = new javax.swing.JCheckBox();
        lbHint = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IrisTool");

        tbFTEList.setModel(tbmFTEList);
        tbFTEList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbFTEList.getTableHeader().setReorderingAllowed(false);
        tbFTEList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbFTEListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbFTEList);
        tbFTEList.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        btExtract.setText("Extract");
        btExtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExtractActionPerformed(evt);
            }
        });

        cbDecompress.setSelected(true);
        cbDecompress.setText("Decompress compressed files upon extract");
        cbDecompress.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbDecompressStateChanged(evt);
            }
        });
        cbDecompress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDecompressActionPerformed(evt);
            }
        });

        btParse.setText("Parse");
        btParse.setEnabled(false);
        btParse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btParseActionPerformed(evt);
            }
        });

        btExit.setText("Exit");
        btExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExitActionPerformed(evt);
            }
        });

        lbUsage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbUsage.setText("Select file to parse. All file types are supported.");

        lbApplicationTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbApplicationTitle.setText("IrisTool");

        logBtnGrp.add(rbLogToFile);
        rbLogToFile.setText("Log to file");
        rbLogToFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rbLogToFileMouseClicked(evt);
            }
        });

        logBtnGrp.add(rbLogToConsole);
        rbLogToConsole.setText("Log to console output");
        rbLogToConsole.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rbLogToConsoleMouseClicked(evt);
            }
        });

        logBtnGrp.add(rbLogNone);
        rbLogNone.setSelected(true);
        rbLogNone.setText("No logging");
        rbLogNone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rbLogNoneMouseClicked(evt);
            }
        });

        cbSaveTextures.setText("Save textures into textures_<key> directory upon parse");
        cbSaveTextures.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbSaveTexturesStateChanged(evt);
            }
        });

        tbSearch.setToolTipText("Use [ Enter ] to find next occurrences");
        tbSearch.setName("tbSearch"); // NOI18N
        tbSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbSearchKeyReleased(evt);
            }
        });

        lbSearch.setText("Search:");

        btAbout.setText("?");
        btAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAboutActionPerformed(evt);
            }
        });

        btOpen.setText("Open another bf & spe");
        btOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOpenActionPerformed(evt);
            }
        });

        cbConsoleSpecificParse.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cbConsoleSpecificParse.setText("Original Game console specific parse (use only with PS2/GC/XBOX files)");
        cbConsoleSpecificParse.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbConsoleSpecificParseStateChanged(evt);
            }
        });

        lbHint.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lbHint.setText("(Hint: Do not use with HD files. If you encounter parse errors, try this to change first.)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lbHint)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbConsoleSpecificParse)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(rbLogToFile)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rbLogToConsole)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rbLogNone))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cbDecompress)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbSaveTextures)))
                                .addGap(0, 307, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btExtract)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btParse)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btExit))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbUsage)
                                    .addComponent(lbApplicationTitle))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(lbSearch)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(btOpen)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btAbout)))))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbApplicationTitle)
                    .addComponent(btAbout)
                    .addComponent(btOpen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbUsage)
                    .addComponent(tbSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbConsoleSpecificParse)
                        .addGap(1, 1, 1)
                        .addComponent(lbHint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbSaveTextures)
                            .addComponent(cbDecompress))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbLogToFile)
                            .addComponent(rbLogToConsole)
                            .addComponent(rbLogNone))
                        .addGap(51, 51, 51))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btExit)
                            .addComponent(btParse)
                            .addComponent(btExtract))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbFTEListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbFTEListMouseClicked
        int sel = tbFTEList.getSelectedRow();
        if (sel == -1) {
            return;
        }
        Integer key = bfc.getFatTable().getElemByIndex(sel).getKey();
        Integer type = BFDataFileClass.getBinarizedDataKeyType(key);
        if (!bfc.getStartKey().equals(key) && (type == BFDataFileClass.BF_D_UNKNOWN || type == BFDataFileClass.BF_D_NOT_BINARIZED)) {
            btParse.setEnabled(false);
        } else {
            btParse.setEnabled(true);
        }
    }//GEN-LAST:event_tbFTEListMouseClicked

    private void btExtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExtractActionPerformed
        int sel = tbFTEList.getSelectedRow();
        if (sel == -1) {
            return;
        }

        sm.addSetting("lastUsedElem", "" + sel);
        sm.SaveSettings();

        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            byte[] data = BFClass.extractFatTableElem(bfc, sel, cbDecompress.isSelected());
            String ext = (cbDecompress.isSelected() ? ".decompressed.bin" : ".bin");
            if (data == null) {
                JOptionPane.showMessageDialog(this, "Extraction error.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String fileName = Convert.bytesToHex(Convert.intToHexArray(bfc.getFatTable().getElemByIndex(sel).getKey()));

                FileOutputStream fos = new FileOutputStream(new File(jfc.getSelectedFile().getAbsolutePath()
                        + File.separator + fileName + ext));
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Extraction error. Exception: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Extraction succeeded.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btExtractActionPerformed

    private void btParseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btParseActionPerformed
        int sel = tbFTEList.getSelectedRow();
        if (sel == -1) {
            return;
        }

        Boolean parseSucceeded = true;

        sm.addSetting("lastUsedElem", "" + sel);
        sm.SaveSettings();

        IrisToolDefines.setLoggingEnabled(!"none".equals(logTypeString));

        if ("file".equals(logTypeString)) {
            String lfs;
            if (!IrisToolDefines.setLogFile(new File(lfs = bfFile.getAbsolutePath().substring(0,
                    bfFile.getAbsolutePath().lastIndexOf(File.separator))
                    + File.separator
                    + String.format("%x", bfc.getFatTable().getElemByIndex(sel).getKey())
                    + ".bin.log"))) {
                System.out.println("LogFile open error.");
            }
            System.out.println("logFile: " + lfs);
        }

        LoadedSoundsClass lsc = new LoadedSoundsClass(); // Create new LSC in every parse.
        TextureDatabase td = new TextureDatabase(); // Create new TextureDatabase in every parse.

        BFDataFileClass bfdfc = new BFDataFileClass(bfc.getFatTable().getElemByIndex(sel).getKey(), bfc, spe, lsc, td);
        BFDataFileClass bfdfcTex = null;

        try {
            if (!bfdfc.parse()) {
                parseSucceeded = false;
                throw new Exception("see log for details");
            }

            if (BFDataFileClass.getBinarizedDataKeyType(bfc.getFatTable().getElemByIndex(sel).getKey()) == BFDataFileClass.BF_D_MAP_MODEL_SCRIPT_RELATED) {
                Integer texturePackKey = bfc.getFatTable().getElemByIndex(sel).getKey() | 0xff800000;
                bfdfcTex = new BFDataFileClass(texturePackKey, bfc, spe, null, td);
                bfdfcTex.setTextureSaveDir(cbSaveTextures.isSelected() ? (bfFile.getAbsolutePath().substring(0, bfFile.getAbsolutePath().lastIndexOf(File.separator))
                        + File.separator + String.format("textures_%08x", texturePackKey)) : null);
                if (!bfdfcTex.isDataFileOpened()) {
                    // ???
                } else if (!bfdfcTex.parse()) {
                    throw new Exception("see log for details");
                }
            }
        } catch (Exception e) {
            parseSucceeded = false;
            JOptionPane.showMessageDialog(this, "Parse failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        if ("file".equals(logTypeString)) {
            IrisToolDefines.closeLogFile();
        }

        if (parseSucceeded) {
            if (BFDataFileClass.getBinarizedDataKeyType(bfc.getFatTable().getElemByIndex(sel).getKey()) == BFDataFileClass.BF_D_MAP_MODEL_SCRIPT_RELATED
                    && bfdfc.getParsedWOWMerged() != null) {

                WOWBrowser wb = new WOWBrowser(this, true, bfdfc, bfdfcTex);
                wb.setVisible(true);
            }
            if (BFDataFileClass.getBinarizedDataKeyType(bfc.getFatTable().getElemByIndex(sel).getKey()) == BFDataFileClass.BF_D_SOUNDBANK_PACK) {
                SoundBrowser sb = new SoundBrowser(this, true, bfc, bfdfc, BFDataFileClass.BF_D_SOUNDBANK_PACK);
                sb.setVisible(true);
            }
            if (BFDataFileClass.getBinarizedDataKeyType(bfc.getFatTable().getElemByIndex(sel).getKey()) == BFDataFileClass.BF_D_SOUNDBANK_HEADER_COLLECTION) {
                SoundBrowser sb = new SoundBrowser(this, true, bfc, bfdfc, BFDataFileClass.BF_D_SOUNDBANK_HEADER_COLLECTION);
                sb.setVisible(true);
            }
            if (BFDataFileClass.getBinarizedDataKeyType(bfc.getFatTable().getElemByIndex(sel).getKey()) == BFDataFileClass.BF_D_TEXTS
                    || (bfc.getFatTable().getElemByIndex(sel).getKey() != null && bfc.getStartKey() != null
                    && bfc.getFatTable().getElemByIndex(sel).getKey().equals(bfc.getStartKey()))) {
                JOptionPane.showMessageDialog(this, "Parse succeeded.", "Done", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btParseActionPerformed

    private void btExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btExitActionPerformed

    private void setLoggingTypeFromRadioButtons() {
        if (rbLogToFile.isSelected()) {
            logTypeString = "file";
        }
        if (rbLogToConsole.isSelected()) {
            logTypeString = "console";
        }
        if (rbLogNone.isSelected()) {
            logTypeString = "none";
        }
        sm.addSetting("logType", logTypeString);
        sm.SaveSettings();
    }

    private void rbLogToFileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rbLogToFileMouseClicked
        setLoggingTypeFromRadioButtons();
    }//GEN-LAST:event_rbLogToFileMouseClicked

    private void rbLogToConsoleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rbLogToConsoleMouseClicked
        setLoggingTypeFromRadioButtons();
    }//GEN-LAST:event_rbLogToConsoleMouseClicked

    private void rbLogNoneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rbLogNoneMouseClicked
        setLoggingTypeFromRadioButtons();
    }//GEN-LAST:event_rbLogNoneMouseClicked

    private void cbDecompressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDecompressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbDecompressActionPerformed

    private void cbDecompressStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbDecompressStateChanged
        sm.addSetting("decompressonextract", (cbDecompress.isSelected() ? "1" : "0"));
        sm.addSetting("dumptextures", (cbSaveTextures.isSelected() ? "1" : "0"));
        sm.SaveSettings();
    }//GEN-LAST:event_cbDecompressStateChanged

    private void cbSaveTexturesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbSaveTexturesStateChanged
        cbDecompressStateChanged(evt);
    }//GEN-LAST:event_cbSaveTexturesStateChanged

    private void tbSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbSearchKeyReleased

        int occurrenceFoundNum = 0;

        // if typing, reset occurrence counter
        if (evt.getKeyCode() != KeyEvent.VK_ENTER) {
            nextSearchOccurrenceNumber = 0;
        }

        String s = tbSearch.getText().trim().toUpperCase();
        if (!s.equals("")) {
            int i;
            for (i = 0; i < tbmFTEList.getRowCount(); i++) {
                if (((String) (tbmFTEList.getValueAt(i, 0))).toUpperCase().indexOf(s) == 0
                        || (IrisToolDefines.isReadFileNameListAndDirectoryList() && ((String) (tbmFTEList.getValueAt(i, 1))).toUpperCase().contains(s))) {
                    occurrenceFoundNum++;
                    if (occurrenceFoundNum > nextSearchOccurrenceNumber) {
                        // if enter hit search for nex occurrence next time, else set to second (1), because we want to search next
                        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                            nextSearchOccurrenceNumber++;
                        } else {
                            nextSearchOccurrenceNumber = 1;
                        }
                        tbFTEList.setRowSelectionInterval(i, i);
                        scrollToVisible(tbFTEList, i, 0);
                        tbFTEListMouseClicked(null);
                        tbSearch.setBackground(Color.green);
                        break;
                    }
                }
            }
            if (i >= tbmFTEList.getRowCount()) {
                tbSearch.setBackground(Color.red);
                // if something is not found or reached all of occurences, clear occurrence counter, to start from beginning
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    nextSearchOccurrenceNumber = 0;
                }
            }
        } else {
            tbFTEList.clearSelection();
            tbSearch.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbSearchKeyReleased

    private void btAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAboutActionPerformed
        JOptionPane.showMessageDialog(this, "<html><b>IrisTool</b> v.:" + IrisToolDefines.getIrisToolVersion() + " (Java: " + System.getProperty("java.version") + ")<br/>&copy; 2015-2022 Andras Kovacs (kandras87@gmail.com)<br/><br/>"
                + "You can use this tool to open Beyond Good & Evil's data file (.bf), and view it's textures, levels,<br/>play sounds & music (only PC version), etc. It can play model animations too.<br/>"
                + "All versions of the game (original: PC/GC/PS2/XBOX, HD: PS3/XBOX360) are supported (two PC demos and full games were tested).<br/><br/>"
                + "Limitations:<br/>"
                + "- only these GAO modifier are supported: FakeLookAtCamera, InverseKinematics, Lazy, MeshWave, Morph, Snake, SpriteGen, XMEC, SpecialLookAt<br/>"
                + "- no scripting and cutscene play<br/>"
                + "- no collision/physics (only can display collision objects)<br/>"
                + "- no procedural water<br/><br/>"
                + "Uses: <br/>"
                + "- MiniLZO Java port (Mahadevan Gorti Surya Srinivasa, sgorti@gmail.com)<br/>"
                + "- Jogamp for OpenGL (https://jogamp.org/)<br/>"
                + "- HexLib (http://hexedit-lib.sourceforge.net/)<br/>"
                + "- Microsoft ADPCM Decoder (Ethan Lee, http://www.flibitijibibo.com/)<br/><br/>"
                + "Feel free to use this tool, and if you have any questions, use kandras87@gmail.com email address.</html>", "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btAboutActionPerformed

    private void btOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOpenActionPerformed
        if (!tryToOpen(false)) {
            if (!tryToOpen(true)) {
                System.exit(-1);
            }
        }
        fillBinList();
    }//GEN-LAST:event_btOpenActionPerformed

    private void cbConsoleSpecificParseStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbConsoleSpecificParseStateChanged
        IrisToolDefines.setConsoleVersionTextureLoad(cbConsoleSpecificParse.isSelected());
        IrisToolDefines.setPreferPalettelessTextures(!cbConsoleSpecificParse.isSelected());
        sm.addSetting("consolespecificparse", (IrisToolDefines.isConsoleVersionTextureLoad() ? "1" : "0"));
        sm.SaveSettings();
    }//GEN-LAST:event_cbConsoleSpecificParseStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // LAF
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ee) {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException eee) {
                }
            }
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new IrisTool().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAbout;
    private javax.swing.JButton btExit;
    private javax.swing.JButton btExtract;
    private javax.swing.JButton btOpen;
    private javax.swing.JButton btParse;
    private javax.swing.JCheckBox cbConsoleSpecificParse;
    private javax.swing.JCheckBox cbDecompress;
    private javax.swing.JCheckBox cbSaveTextures;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbApplicationTitle;
    private javax.swing.JLabel lbHint;
    private javax.swing.JLabel lbSearch;
    private javax.swing.JLabel lbUsage;
    private javax.swing.ButtonGroup logBtnGrp;
    private javax.swing.JRadioButton rbLogNone;
    private javax.swing.JRadioButton rbLogToConsole;
    private javax.swing.JRadioButton rbLogToFile;
    private javax.swing.JTable tbFTEList;
    private DefaultTableModel tbmFTEList;
    private javax.swing.JTextField tbSearch;
    // End of variables declaration//GEN-END:variables

}
