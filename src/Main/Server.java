package Main;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;
import javax.net.ssl.SSLServerSocket;
import javax.swing.JOptionPane;

public class Server extends javax.swing.JFrame {

    Thread t;
    ServerThread serverThread;

    public Vector socketList = new Vector();
    public Vector clientList = new Vector();
    public Vector clientFileSharingUsername = new Vector();
    public Vector clientFileSharingSocket = new Vector();
    SSLServerSocket server;

    public Server() {
        initComponents();
    }

    public void appendMessage(String msg) {
        jTextArea1.append(msg + "\n");
    }

    public void setSocketList(Socket socket) {
        try {
            socketList.add(socket);
        } catch (Exception e) {

        }
    }

    public void setClientList(String client) {
        try {
            clientList.add(client);

        } catch (Exception e) {

        }
    }

    public Socket getClientList(String client) {
        Socket tsoc = null;
        for (int x = 0; x < clientList.size(); x++) {
            if (clientList.get(x).equals(client)) {
                tsoc = (Socket) socketList.get(x);
                break;
            }
        }
        return tsoc;
    }

    public void removeFromTheList(String client) {
        try {
            for (int x = 0; x < clientList.size(); x++) {
                if (clientList.elementAt(x).equals(client)) {
                    clientList.removeElementAt(x);
                    socketList.removeElementAt(x);
                    appendMessage("Removed " + client);
                    
                    break;
                }
            }
        } catch (Exception e) {

        }
    }
    
    public void setClientFileSharingUsername(String user) {
        try {
            clientFileSharingUsername.add(user);
            System.out.println("Added username to file sharing: " + user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClientFileSharingSocket(Socket soc) {
        try {
            clientFileSharingSocket.add(soc);
            System.out.println("Added socket to file sharing for user: " + soc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Socket getClientFileSharingSocket(String username) {
        Socket tsoc = null;
        System.out.println("Looking for username: " + username);
        for (int x = 0; x < clientFileSharingUsername.size(); x++) {
            System.out.println("Checking: " + clientFileSharingUsername.elementAt(x));
            if (clientFileSharingUsername.elementAt(x).equals(username)) {
                tsoc = (Socket) clientFileSharingSocket.elementAt(x);
                System.out.println("Socket found for username: " + username);
                break;
            }
        }
        if (tsoc == null) {
            System.out.println("Socket not found for username: " + username);
        }
        return tsoc;
    }
    
    public void removeClientFileSharing(String username) {
        for (int x = 0; x < clientFileSharingUsername.size(); x++) {
            if (clientFileSharingUsername.elementAt(x).equals(username)) {
                try {
                    Socket rSock = getClientFileSharingSocket(username);
                    if (rSock != null) {
                        rSock.close();
                    }
                    clientFileSharingUsername.removeElementAt(x);
                    clientFileSharingSocket.removeElementAt(x);
                    appendMessage("Removed " + username);
                } catch (IOException e) {
                    System.err.println(e);

                }
                break;
            }
        }
    }
    
    public void broadcastMessage(String username, Socket userSocket) {
    for (int i = 0; i < socketList.size(); i++) {
        try {
            Socket clientSocket = (Socket) socketList.get(i);
            // Skip the user's socket
            if (clientSocket.equals(userSocket)) {
                continue;
            }
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            dos.writeUTF("CMD_USERONLINE " + username);
            dos.flush();
        } catch (IOException e) {
            appendMessage("Error broadcasting message: " + e.getMessage());
        }
    }
}
    public void broadcastOfflineMessage(String username) {
        for (int i = 0; i < socketList.size(); i++) {
            try {
                Socket clientSocket = (Socket) socketList.get(i);
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                dos.writeUTF("CMD_USEROFFLINE " + username);
                dos.flush();
            } catch (IOException e) {
                appendMessage("Error broadcasting offline message: " + e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(80, 80, 167));

        jButton2.setFont(new java.awt.Font("Hoefler Text", 0, 15)); // NOI18N
        jButton2.setText("Stop Server");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Hoefler Text", 0, 15)); // NOI18N
        jButton1.setText("Start Server");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setFont(new java.awt.Font("Krungthep", 0, 15)); // NOI18N
        jTextField1.setText("1234");

        jLabel1.setFont(new java.awt.Font("Krungthep", 0, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Port:");

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(255, 255, 255));
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(15, 15, 15)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        int confirm = JOptionPane.showConfirmDialog(null, "Close Server.?");
        if (confirm == 0) {
            serverThread.stop();
            jButton1.setEnabled(true);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int port = Integer.parseInt(jTextField1.getText());
        serverThread = new ServerThread(port, this);
        t = new Thread(serverThread);
        t.start();

        new Thread(new OnlineListThread(this)).start();

        jButton1.setEnabled(false);
        jButton2.setEnabled(true);
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
