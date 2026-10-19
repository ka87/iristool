/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool;

import BFDataFileLib.EventTrackElem;
import BFDataFileLib.EventTrackFrameElem;
import Common.DataType.ArrayListWithSizeOverLoad;
import Common.DataType.OFCProgramCodeElem;
import Common.IrisToolDefines;
import iristool.GUI.CustomColorButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author andra
 */
public class EventViewer extends javax.swing.JDialog {

    private final static int ONE_MILLISEC_IN_PIXEL = 5;
    private final static int TRACK_HEIGHT_IN_PIXEL = 25;
    private final static int GRPBTN_HEIGHT_IN_PIXEL = 15;

    private final ArrayListWithSizeOverLoad<EventTrackElem> etearr;
    private final JPanel container = new JPanel();
    private final JPanel timeLineHeader = new JPanel();
    private final JScrollPane spTimeLineHeader = new JScrollPane(timeLineHeader, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JPanel trackList = new JPanel();
    private final JScrollPane spTrackList = new JScrollPane(trackList, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private final JPanel trackLine = new JPanel();
    private final JScrollPane spTrackLine = new JScrollPane(trackLine);
    private CustomColorButton[] trackListButtons;
    private ArrayList[] trackLineButtons;

    private void resizeComponent() {
        if (container.getWidth() <= 0 || container.getHeight() <= 0) {
            return;
        }
        spTimeLineHeader.setSize(container.getWidth(), 30);
        spTrackList.setSize(100, container.getHeight() - 30);
        spTrackLine.setSize(container.getWidth() - 100, container.getHeight() - 30);
        spTimeLineHeader.invalidate();
        spTrackList.invalidate();
        spTrackLine.invalidate();
        container.invalidate();
    }

    private void resizeInnerArea(int timeLineWidth, int trackListHeight) {
        timeLineHeader.setPreferredSize(new Dimension(timeLineWidth, 30));
        trackList.setPreferredSize(new Dimension(100, trackListHeight));
        trackLine.setPreferredSize(new Dimension(timeLineWidth, trackListHeight));
    }

    private StringBuilder _dumpProgramCodeFromETFE(EventTrackFrameElem etfe, StringBuilder sb) {
        if (etfe != null && sb != null
                && etfe.getProgramCodeElemNum() != null && etfe.getProgramCode() != null) {
            ArrayList<OFCProgramCodeElem> ofcarr = etfe.getProgramCode();
            Iterator it = ofcarr.iterator();
            OFCProgramCodeElem pce;
            while (it.hasNext()) {
                pce = (OFCProgramCodeElem) (it.next());
                sb.append(pce).append("\n");
                if (!IrisToolDefines.PUBLIC_RELEASE) {
                    sb.append("  Details: ").append(pce.getActualElemDescription()).append("\n");
                    sb.append("  Extra details: ").append(pce.getActualElemExtraDescription()).append("\n");
                }
            }
        } else {
            sb.append("Not a programcode frame.");
        }
        return sb;
    }

    private CustomColorButton createButton(String text, String tooltip, int posX, int posY, int btnSizeWidth,
            int btnSizeInHeight, Color c, Object userObject) {
        if (c == null) {
            c = Color.LIGHT_GRAY;
        }
        CustomColorButton btn = new CustomColorButton(c, Color.black);
        btn.setText(text, true);
        btn.setToolTipText(tooltip, true);
        btn.setFont(new Font("Arial", 0, 8));
        btn.setLocation(posX, posY);
        btn.setSize(btnSizeWidth, btnSizeInHeight);
        btn.setPreferredSize(new Dimension(btnSizeWidth, btnSizeInHeight));
        btn.setUserObject(userObject);
        btn.addActionListener((ActionEvent evt) -> {
            CustomColorButton src = (CustomColorButton) evt.getSource();
            JTextArea textArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(textArea);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            scrollPane.setPreferredSize(new Dimension(500, 500));
            if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
                StringBuilder sb = new StringBuilder();
                sb.append("Programcode details:\n");
                if (src.getUserObject() != null) {
                    if (src.getUserObject() instanceof EventTrackFrameElem) {
                        EventTrackFrameElem etfe = (EventTrackFrameElem) src.getUserObject();
                        _dumpProgramCodeFromETFE(etfe, sb);
                    } else if (src.getUserObject() instanceof ArrayList) {
                        ArrayList grpccb = (ArrayList) src.getUserObject();
                        Iterator it = grpccb.iterator();
                        while (it.hasNext()) {
                            Object obj = it.next();
                            if (obj instanceof CustomColorButton) {
                                CustomColorButton innerccb = (CustomColorButton) obj;
                                if (innerccb.getUserObject() != null && innerccb.getUserObject() instanceof EventTrackFrameElem) {
                                    EventTrackFrameElem inneretfe = (EventTrackFrameElem) innerccb.getUserObject();
                                    sb.append("--- Next programcode group elem ---\n");
                                    _dumpProgramCodeFromETFE(inneretfe, sb);
                                }
                            }
                        }
                    }
                }
                textArea.setText(sb.toString());
            } else {
                textArea.setText(src.getToolTipTextCustom());
            }
            JOptionPane.showMessageDialog(null, scrollPane, "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        );
        return btn;
    }

    public EventViewer(java.awt.Dialog parent, boolean modal, ArrayListWithSizeOverLoad<EventTrackElem> etearr, String gaoName) {
        super(parent, modal);
        initComponents();

        setTitle("Event viewer (Track count: " + (etearr != null ? etearr.size() : "null")
                + ")" + (gaoName != null ? ": " + gaoName : ""));
        this.etearr = etearr;
        if (!etearr.isEmpty()) {
            trackListButtons = new CustomColorButton[etearr.size()];
            trackLineButtons = new ArrayList[etearr.size()];
        }

        container.setLayout(null);
        timeLineHeader.setLayout(null);
        JScrollBar sbTimeLineHeaderH = spTimeLineHeader.getHorizontalScrollBar();
        spTimeLineHeader.setLocation(100, 0);
        container.add(spTimeLineHeader);
        trackList.setLayout(null);
        JScrollBar sbTrackListV = spTrackList.getVerticalScrollBar();
        spTrackList.setLocation(0, 30);
        container.add(spTrackList);
        trackLine.setLayout(null);
        JScrollBar sbTrackLineH = spTrackLine.getHorizontalScrollBar();
        JScrollBar sbTrackLineV = spTrackLine.getVerticalScrollBar();
        sbTrackLineH.setModel(sbTimeLineHeaderH.getModel());
        sbTrackLineV.setModel(sbTrackListV.getModel());
        spTrackLine.setLocation(100, 30);
        container.add(spTrackLine);
        JButton btnHelp = new JButton("?");
        btnHelp.setLocation(0, 0);
        btnHelp.setSize(40, 30);
        btnHelp.setPreferredSize(new Dimension(40, 30));
        btnHelp.addActionListener((ActionEvent ae) -> {
            JOptionPane.showMessageDialog(EventViewer.this,
                    "<html><b>GREEN</b> colored track frames contain programcode, <br/>"
                    + "<b>ORANGE</b> ones contain nothing (used for idle), <br>"
                    + "<b>RED</b> ones are Goto Nodes (current track, all track), <br>"
                    + "<b>BLUE</b> ones are Goto destionation labels.<br>"
                    + "<b>PINK</b> ones have been grouped together (because of 0 ms keyframe duration),"
                    + "<br> and contain programcode (probably).<br><br>"
                    + "If you click on track frames, you can see detailed information about specific keyframe.<br>"
                    + "If you are using <b>CTRL</b> key with <b>click</b>, programcode will be fully displayed (raw).</html>",
                    "Help", JOptionPane.INFORMATION_MESSAGE);
        });
        container.add(btnHelp);

        resizeInnerArea(1000, 1000);

        ComponentListener resizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponent();
            }
        };
        container.addComponentListener(resizeListener);

        getContentPane().add(container);
        pack();

        // calc max area size
        Iterator it1 = etearr.iterator();
        int maxWidth = 1;
        int oneTrackWidth = 0;
        while (it1.hasNext()) {
            EventTrackElem ete1 = (EventTrackElem) it1.next();
            ArrayList<EventTrackFrameElem> etfe1arr = ete1.getEventTrackFrameElems();
            if (etfe1arr != null) {
                Iterator it3 = etfe1arr.iterator();
                while (it3.hasNext()) {
                    EventTrackFrameElem etfe2 = (EventTrackFrameElem) it3.next();
                    if (etfe2 != null) {
                        oneTrackWidth += (etfe2.getTrackFrameLen() * EventViewer.ONE_MILLISEC_IN_PIXEL);
                    }
                }
            }
            if (oneTrackWidth > maxWidth) {
                maxWidth = oneTrackWidth;
            }
            oneTrackWidth = 0;
        }
        // end

        resizeInnerArea(maxWidth + 200, (etearr.size() * TRACK_HEIGHT_IN_PIXEL) + 20);

        Iterator it = etearr.iterator();
        int posY = 0, posX = 0, i = 0;
        ArrayList<CustomColorButton> zeroSizeFrames = new ArrayList<>();
        while (it.hasNext()) {
            zeroSizeFrames.clear();
            EventTrackElem ete = (EventTrackElem) it.next();
            CustomColorButton btn = createButton(ete.toString(true), ete.toString(), 0, posY, 100, TRACK_HEIGHT_IN_PIXEL, ((ete.getEventTrackFlags() & 0x8000) != 0 ? null : Color.MAGENTA), null);
            trackList.add(btn);
            trackListButtons[i] = btn;
            trackLineButtons[i] = new ArrayList<>();
            trackList.invalidate();
            ArrayList<EventTrackFrameElem> etfearr = ete.getEventTrackFrameElems();
            if (etfearr != null) {
                Iterator it2 = etfearr.iterator();
                while (it2.hasNext()) {
                    EventTrackFrameElem etfe = (EventTrackFrameElem) it2.next();
                    if (etfe != null) {
                        Color c = null;
                        String text = "";
                        if (etfe.getProgramCodeElemNum() != null) {
                            c = Color.GREEN;
                        } else if (etfe.getTrackFrameTypeFlags() == null || etfe.getTrackFrameTypeFlags() == 0) {
                            c = Color.ORANGE;
                            text = "Delay";
                        }
                        if (etfe.isAllTrackGotoNode()) {
                            c = Color.RED;
                            if (!text.equals("")) {
                                text += ", ";
                            }
                            text += "All Track Goto Node [id: " + etfe.getGotoLabelID() + "]";
                        }
                        if (etfe.isCurrentTrackGotoNode()) {
                            c = Color.RED;
                            if (!text.equals("")) {
                                text += ", ";
                            }
                            text += "Current Track Goto Node [id: " + etfe.getGotoLabelID() + "]";
                        }
                        if (etfe.isGotoLabel()) {
                            c = Color.BLUE;
                            if (!text.equals("")) {
                                text += ", ";
                            }
                            text += "Goto label [id: " + etfe.getGotoLabelID() + "]";
                        }
                        CustomColorButton btn2 = createButton(text, etfe.getFullInfoText(), posX, posY,
                                (etfe.getTrackFrameLen() <= 0 ? 10 : etfe.getTrackFrameLen() * EventViewer.ONE_MILLISEC_IN_PIXEL),
                                TRACK_HEIGHT_IN_PIXEL, c, etfe);
                        if (etfe.getTrackFrameLen() == 0) {
                            zeroSizeFrames.add(btn2);
                        }
                        if (zeroSizeFrames.size() > 0 && (etfe.getTrackFrameLen() != 0 || !it2.hasNext())) {
                            trackLineButtons[i].add(zeroSizeFrames);
                            StringBuilder sb = new StringBuilder();
                            sb.append("These zero length elements were grouped into this elem.\nDetails:\n");
                            Iterator itZero = zeroSizeFrames.iterator();
                            while (itZero.hasNext()) {
                                CustomColorButton ccb = (CustomColorButton) itZero.next();
                                sb.append(ccb.getToolTipTextCustom());
                                sb.append("---\n");
                            }
                            ArrayList<CustomColorButton> zeroSizeFramesBak = (ArrayList<CustomColorButton>) zeroSizeFrames.clone();
                            zeroSizeFrames.clear();
                            CustomColorButton grpBtn = createButton("", sb.toString(), posX, posY, 30, GRPBTN_HEIGHT_IN_PIXEL, Color.PINK, zeroSizeFramesBak);
                            trackLine.add(grpBtn);
                        }
                        if (etfe.getTrackFrameLen() != 0) {
                            trackLine.add(btn2);
                            trackLineButtons[i].add(btn2);
                        }
                        posX += (etfe.getTrackFrameLen() * EventViewer.ONE_MILLISEC_IN_PIXEL);
                    }
                }
                trackLine.invalidate();
                posX = 0;
            }
            posY += TRACK_HEIGHT_IN_PIXEL;
            i++;
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Event viewer");
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
