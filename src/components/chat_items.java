 
package components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.border.EmptyBorder;

public class chat_items extends javax.swing.JPanel {

    
    public chat_items() {
        initComponents();
        chat_item.setEditable(false);
        chat_item.setBackground(new Color(0,0,0,0));
        chat_item.setOpaque(false);
    }
    
    public void setText(String Text){
        chat_item.setText(Text);
    }
    
   
    
    public void setTime(String time){
        JLayeredPane layer = new JLayeredPane();
        layer.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
        layer.setBorder(new EmptyBorder(0,10,5,10));
        JLabel label1 = new JLabel(time);
        label1.setForeground(Color.white);
        layer.add(label1);
        add(layer);
    }
    
    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 17, 17);
        super.paintComponent(g);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chat_item = new swing.JIMSendTextPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        chat_item.setBackground(new java.awt.Color(204, 210, 253));
        chat_item.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(chat_item);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private swing.JIMSendTextPane chat_item;
    // End of variables declaration//GEN-END:variables
}
