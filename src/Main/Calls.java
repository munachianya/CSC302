/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

/**
 *
 * @author HP
 */
public class Calls extends javax.swing.JFrame {

    private javax.swing.Timer timer;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private TargetDataLine targetLine;
    private SourceDataLine sourceLine;
    private Thread captureThread;
    private Thread playbackThread;
    private String username;
    private String host;
    private int port;
    Client clientframe;

    private int elapsedTime = 0;

    /**
     * Creates new form Client2
     */
    public Calls() {
        initComponents();
        jLabel2.setText(Client.jLabel2.getText());
    }

    public void connect(String username, String host, int port) {
        try {
            AudioFormat captureFormat = new AudioFormat(8000.0f, 16, 1, true, true);
            DataLine.Info captureInfo = new DataLine.Info(TargetDataLine.class, captureFormat);
            targetLine = (TargetDataLine) AudioSystem.getLine(captureInfo);
            targetLine.open(captureFormat);
            targetLine.start();

            AudioFormat playbackFormat = new AudioFormat(8000.0f, 16, 1, true, true);
            DataLine.Info playbackInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
            sourceLine = (SourceDataLine) AudioSystem.getLine(playbackInfo);
            sourceLine.open(playbackFormat);
            sourceLine.start();

            socket = new Socket(host, port);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            outputStream.write(username.getBytes());

            captureThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (!Thread.currentThread().isInterrupted()) {
                        int bytesRead = targetLine.read(buffer, 0, buffer.length);
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            captureThread.start();

            playbackThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (!Thread.currentThread().isInterrupted()) {
                        int bytesRead = inputStream.read(buffer);
                        if (bytesRead == -1) {
                            break;
                        }
                        sourceLine.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            playbackThread.start();

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
        startTimer();
        jLabel5.setText("Connected");
        jLabel5.setForeground(new Color(0, 128, 0));

    }

    public void disconnect() {
        try {
            if (captureThread != null && captureThread.isAlive()) {
                captureThread.interrupt();
            }
            if (playbackThread != null && playbackThread.isAlive()) {
                playbackThread.interrupt();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (targetLine != null) {
                targetLine.close();
            }
            if (sourceLine != null) {
                sourceLine.close();
            }

            stopTimer();
            jLabel6.setText("00:00:00");
            jLabel5.setText("Disconnected");
            jLabel5.setForeground(Color.RED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatTime(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void startTimer() {
        elapsedTime = 0;
        jLabel6.setText(formatTime(elapsedTime));
        ActionListener timeListener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                elapsedTime++;
                jLabel6.setText(formatTime(elapsedTime));
            }
        };
        timer = new javax.swing.Timer(1000, timeListener);
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
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

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jButton1.setFont(new java.awt.Font("Krungthep", 0, 13)); // NOI18N
        jButton1.setForeground(new java.awt.Color(231, 143, 9));
        jButton1.setText("Start Call");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Krungthep", 0, 13)); // NOI18N
        jButton2.setForeground(new java.awt.Color(231, 143, 9));
        jButton2.setText("End Call");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Krungthep", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(237, 135, 34));
        jLabel4.setText("Status");

        jLabel6.setFont(new java.awt.Font("Krungthep", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(237, 135, 34));
        jLabel6.setText("00:00:00");

        jLabel5.setFont(new java.awt.Font("Krungthep", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(237, 135, 34));
        jLabel5.setText("Connected");

        jLabel7.setFont(new java.awt.Font("Krungthep", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(237, 135, 34));
        jLabel7.setText("Time Elapsed");

        jLabel2.setFont(new java.awt.Font("Krungthep", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(237, 135, 34));
        jLabel2.setText("jLabel2");

        jLabel1.setFont(new java.awt.Font("Krungthep", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(237, 135, 34));
        jLabel1.setText("Username");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(65, 65, 65)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addComponent(jLabel1))
                        .addGap(37, 37, 37)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(57, 57, 57)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        username = jLabel2.getText();
        host = "localhost";
        port = 3000;
        if (!username.isEmpty() && !host.isEmpty() && port != 0) {
            connect(username, host, port);
            jButton1.setEnabled(false);
            jButton2.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Provide all required fields");
        }
// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        disconnect();
        jButton1.setEnabled(true);
        jButton2.setEnabled(false);
// TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Calls.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Calls.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Calls.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Calls.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Calls().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
