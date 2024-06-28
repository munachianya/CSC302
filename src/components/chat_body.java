package components;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import net.miginfocom.swing.MigLayout;
import swing.ScrollBar;


public class chat_body extends javax.swing.JPanel  {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/chatapp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "plutos908";

    private String currentUser;

    public chat_body() {
        initComponents();
        init();
    }

    private void init() {
        panel.setLayout(new MigLayout("fillx"));
        smk.setVerticalScrollBar(new ScrollBar());
        smk.getVerticalScrollBar().setBackground(Color.white);
        
        
    }

    public void removeContent(){
        panel.removeAll();
        panel.repaint();
        panel.revalidate();        
    }
    

    private void addDateBanner(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = dateFormat.format(date);
        JLabel bannerLabel = new JLabel("----- " + formattedDate + " -----");
        panel.add(bannerLabel, "span, center, wrap");
    }

    public void left_text(String text, String time) {
        chat_left chat = new chat_left();
        chat.setText(text, time);
        panel.add(chat, "wrap, w al ::60%");
        panel.repaint();
        panel.revalidate();
    }

    public void right_text(String text, String time) {
        chat_right chats = new chat_right();
        chats.setText(text, time);
        panel.add(chats, "wrap,right, w al ::60%");
        panel.repaint();
        panel.revalidate();
    }

    public void addItemLeft(JPanel apanel) {
        panel.add(apanel, "wrap, w al ::60%");
        panel.repaint();
        panel.revalidate();
    }

    public void addItemRight(JPanel apanel) {
        panel.add(apanel,"wrap,right, w al ::60%");
        panel.repaint();
        panel.revalidate();
        scrollToBottom();
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }

    private void scrollToBottom() {
        JScrollBar verticalBar = smk.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        smk = new javax.swing.JScrollPane();
        panel = new javax.swing.JPanel();

        smk.setBorder(null);
        smk.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 590, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 436, Short.MAX_VALUE)
        );

        smk.setViewportView(panel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(smk)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(smk)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane smk;
    // End of variables declaration//GEN-END:variables
}
