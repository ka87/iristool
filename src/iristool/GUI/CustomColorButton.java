/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iristool.GUI;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;

/**
 *
 * @author andra
 */
public class CustomColorButton extends JButton implements ActionListener, MouseListener {

    private boolean hovered = false;
    private boolean clicked = false;

    private Color normalColor = null;
    private Color lightColor = null;
    private Color darkColor = null;
    private Object userObject = null;
    private String text = "";
    private String toolTipText = "";

    public CustomColorButton(Color backGroundColor, Color fontColor) {
        setForeground(fontColor);

        this.normalColor = backGroundColor;
        this.lightColor = backGroundColor.brighter();
        this.darkColor = backGroundColor.darker();

        addActionListener(this);
        addMouseListener(this);
        setContentAreaFilled(false);
    }

    public void setText(String s, Boolean toHtml) {
        text = s;
        if (toHtml) {
            s = "<html>" + s.replaceAll("\\n", "<br>") + "</html>";
        }
        super.setText(s);
    }

    @Override
    public String getText() {
        return text;
    }

    public void setToolTipText(String s, Boolean toHtml) {
        toolTipText = s;
        if (toHtml) {
            s = "<html>" + s.replaceAll("\\n", "<br>") + "</html>";
        }
        super.setToolTipText(s);
    }

    public String getToolTipTextCustom() {
        return toolTipText;
    }
    
    public Object getUserObject() {
        return userObject;
    }

    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gp = null;

        if (clicked) {
            gp = new GradientPaint(0, 0, darkColor, 0, getHeight(), darkColor.darker());
        } else if (hovered) {
            gp = new GradientPaint(0, 0, lightColor, 0, getHeight(), lightColor.darker());
        } else {
            gp = new GradientPaint(0, 0, normalColor, 0, getHeight(), normalColor.darker());
        }

        g2d.setPaint(gp);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);

        g2d.setColor(darkColor.darker().darker());
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);

        super.paintComponent(g);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        hovered = true;
        clicked = false;

        repaint();
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        hovered = false;
        clicked = false;

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        hovered = true;
        clicked = true;

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        hovered = true;
        clicked = false;

        repaint();
    }
}
