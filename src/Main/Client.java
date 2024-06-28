package Main;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import components.voice_note_items;
import javax.swing.JPanel;

public class Client extends javax.swing.JFrame {

    String username, command, password, sname, oname, phone, email;
    String host;
    register registerform;
    Login loginform;
    int port;
    Socket socket;
    DataOutputStream dos;
    String SendTo = "";
    Thread t;
    String dbURL = "jdbc:mysql://localhost:3306/chatapp";
    String dbUsername = "root";
    String dbPassword = "plutos908";
    byte photo[];
    String key;
    Boolean attachmentOpen = false;
    String mydownloadfolder = "C:\\";
    Server server;
    voice_note_items voiceNote;
    MessageAlert alert;

    public Client() {
        initComponents();
        Notification r = new Notification();
        t = new Thread(r);
        t.start();
    }

    public interface RecordingListener {

        void onRecordingStopped(String fileName, String duration, String timestamp);
    }

    public void verifyLoginDetails(String username, String host, int port, String command, String password, Login loginform) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.command = command;
        this.password = password;
        this.loginform = loginform;
        connect();
    }

    public void connect() {
        System.out.println("Login");
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("CMD_LOGIN " + username + " " + password);
            System.out.println("Login details sent to sever");
            ClientThread clientThread = new ClientThread(socket, this, loginform);
            Thread thread = new Thread(clientThread);
            thread.start();
            getAllRegisteredUsers(username);
            key = "J>D)_[m3kx2Ouo57";
        } catch (IOException e) {

            JOptionPane.showMessageDialog(this,
                    "Unable to Connect to Server, please try again later.!",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);

        }
    }

    public void RegistrationDetails(String sname, String oname, String email, String phone, String username, String host, int port, String command, String password, byte photo, register registerform) {
        this.username = username;
        this.sname = sname;
        this.oname = oname;
        this.email = email;
        this.phone = phone;
        this.host = host;
        this.port = port;
        this.command = command;
        this.password = password;
        this.registerform = registerform;
        register();
    }

    public void register() {
        System.out.println("Registration");
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("CMD_REGISTER " + sname + " " + oname + " " + email + " " + phone + " " + username + " " + password);
            System.out.println("Registration  details sent to sever");
            ClientThread clientThread = new ClientThread(socket, this, registerform);
            Thread thread = new Thread(clientThread);
            thread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to Connect to Server, please try again later.!",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);

        }
    }

    public void appendNotification(String msg) {
        jLabel4.setText("");
        jLabel4.setText(msg);
    }

    public String getMyDownloadFolder() {
        return this.mydownloadfolder;
    }

    public void appendOnlineList(Vector list) {
        OnlineList(list);
    }

    private void OnlineList(Vector list) {
        list1.removeAll();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                Object e = i.next();
                String users = String.valueOf(e);
                list1.add(users);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, e);
            }

        }
    }

    public void setMyTitle(String s) {
        setTitle(s);
    }

    public String getMyHost() {
        return this.host;
    }

    public int getMyPort() {
        return this.port;
    }

    public String getMyUsername() {
        return this.username;
    }

    public void getAllRegisteredUsers(String user) {
        try {
            Connection conn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            String query = "SELECT username FROM login";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                if (!user.equals(username)) {
                    list2.add(username);
                }
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFolder() {
        jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int open = jFileChooser1.showDialog(this, "Select Folder");
        if (open == jFileChooser1.APPROVE_OPTION) {
            mydownloadfolder = jFileChooser1.getSelectedFile().toString() + "\\";
        } else {
            mydownloadfolder = "C:\\";
        }
    }

    private void getUserDetails(String username) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            PreparedStatement checkIfExists = con.prepareStatement("SELECT * FROM login WHERE username = ?");
            checkIfExists.setString(1, username);
            ResultSet rs = checkIfExists.executeQuery();
            if (rs.next()) {
                byte[] images = rs.getBytes(7);
                if (images != null) {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(images));
                    if (img != null) {
                        ImageIcon scaledIcon = new ImageIcon(makeRoundedCorner(img, jLabel1.getWidth(), jLabel1.getHeight(), jLabel1));
                        jLabel1.setIcon(scaledIcon);
                    }
                } else {
                    ImageIcon image = new ImageIcon(getClass().getResource("/resources/Profile.png"));
                    ImageIcon scaledDefaultImage = new ImageIcon(image.getImage().getScaledInstance(jLabel1.getWidth(), jLabel1.getHeight(), java.awt.Image.SCALE_SMOOTH));
                    jLabel1.setIcon(scaledDefaultImage);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error retrieving user details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BufferedImage makeRoundedCorner(BufferedImage image, int width, int height, JLabel label) {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Float(0, 0, width, height));
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();

        return output;
    }

    public void updateAttachment(boolean b) {
        this.attachmentOpen = b;
    }

    public void logout() {
        try {
            if (dos != null) {
                dos.writeUTF("CMD_LOGOUT " + username);
                dos.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exitProcedure() {
        logout();
        System.exit(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        list1 = new java.awt.List();
        jPanel4 = new javax.swing.JPanel();
        list2 = new java.awt.List();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        button1 = new swing.button();
        button2 = new swing.button();
        chat_body1 = new components.chat_body();
        button3 = new swing.button();
        button4 = new swing.button();
        button5 = new swing.button();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(80, 80, 167));

        jPanel2.setBackground(new java.awt.Color(0, 204, 204));
        jPanel2.setOpaque(false);

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 204, 204));
        jPanel5.setOpaque(false);

        list1.setBackground(new java.awt.Color(255, 255, 255));
        list1.setFont(new java.awt.Font("Marmelad", 0, 23)); // NOI18N
        list1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                list1MouseClicked(evt);
            }
        });
        list1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                list1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Online", jPanel5);

        jPanel4.setOpaque(false);

        list2.setBackground(new java.awt.Color(255, 255, 255));
        list2.setFont(new java.awt.Font("Marmelad", 0, 23)); // NOI18N
        list2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                list2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(list2, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(list2, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jTabbedPane1.addTab("Registered", jPanel4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 204));
        jPanel3.setOpaque(false);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Marmelad", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));

        button1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Call.png"))); // NOI18N

        button2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/video-camera.png"))); // NOI18N

        chat_body1.setBackground(new java.awt.Color(255, 255, 255));

        button3.setBorder(null);
        button3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Send.png"))); // NOI18N
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });

        button4.setBorder(null);
        button4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Voice.png"))); // NOI18N
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });

        button5.setBorder(null);
        button5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Paper Plus.png"))); // NOI18N
        button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 387, Short.MAX_VALUE)
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chat_body1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(chat_body1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Marmelad", 0, 17)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Notification");

        jLabel4.setFont(new java.awt.Font("Marmelad", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 51));
        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 301, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 33, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jMenuBar1.setBorder(null);

        jMenu1.setText("Settings");

        jMenuItem1.setText("Modify Profile");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Log Out");

        jMenuItem2.setText("logout");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

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
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void list1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_list1MouseClicked
        SendTo = list1.getSelectedItem();
        if (!SendTo.equals(jLabel2.getText())) {
            chat_body1.removeContent();
            jLabel2.setText(SendTo);
            getUserDetails(SendTo);

            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_GETMESSAGES " + SendTo + " " + username + " " + key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_list1MouseClicked


    private void list1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_list1ActionPerformed

    }//GEN-LAST:event_list1ActionPerformed

    private void list2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_list2MouseClicked
        SendTo = list2.getSelectedItem();
        if (!SendTo.equals(jLabel2.getText())) {
            chat_body1.removeContent();
            jLabel2.setText(SendTo);
            getUserDetails(SendTo);

            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_GETMESSAGES " + SendTo + " " + username + " " + key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_list2MouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        ModifyPage modify = new ModifyPage();
        modify.init(username);
        modify.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        if (jTextArea2.getText().equals("")) {
            
        } else {
            try {
                String to = jLabel2.getText();
                if (!to.equalsIgnoreCase("")) {
                    String message = username + " " + to + " " + key + " " + jTextArea2.getText();
                    dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF("CMD_CHAT " + message);
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String Timestamp = now.format(formatter);
                    chat_body1.right_text(jTextArea2.getText(), Timestamp);
                    alert = new MessageAlert();
                    alert.CallMsg();
                    jTextArea2.setText("");
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Select who you want to Chat With");

                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(rootPane, e);
            }
        }
    }//GEN-LAST:event_button3ActionPerformed

    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
//        CapturingAudio capturingAudio = new CapturingAudio();
//        capturingAudio.setRecordingListener(new RecordingListener() {
//            @Override
//            public void onRecordingStopped(String fileName, String duration, String timestamp) {
//              try{ 
//                //dos = new DataOutputStream(socket.getOutputStream());
//                //dos.writeUTF("CMD_CHAT " );
//                voice_note_items voiceNote = new voice_note_items(username, timestamp, duration, fileName);
//                chat_body1.addItemRight(voiceNote);
//              }catch(Exception e){
//                  
//              }
//            }
//        });
//        capturingAudio.setVisible(true);
//        capturingAudio.startingRecording();
    }//GEN-LAST:event_button4ActionPerformed

    private void button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button5ActionPerformed
        if (!attachmentOpen) {
            Attachment sendfile = new Attachment();
            if (sendfile.prepare(username, host, port, this, server, jLabel2.getText())) {
                sendfile.setLocationRelativeTo(null);
                sendfile.setVisible(true);
                attachmentOpen = true;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to establish File Sharing at this moment, please try again later.!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_button5ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Logout your Account.?");
        if (confirm == 0) {
            try {
                socket.close();
                setVisible(false);
                new Login().setVisible(true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    public void displayOptionPane(String text) {
        JOptionPane.showMessageDialog(rootPane, text);
    }

    public void appendRightText(String time, String text) {
        chat_body1.right_text(text, time);
    }

    public void appendLeftText(String time, String text) {
        chat_body1.left_text(text, time);
    }

    public void removeMessages() {
        chat_body1.removeAll();
    }

    public void appendRight(JPanel panel) {
        chat_body1.addItemRight(panel);
    }

    public void appendLeft(JPanel panel) {
        chat_body1.addItemLeft(panel);
    }

    //Notification Thread
    public class Notification implements Runnable {

        int count = 0;
        String friend = "";

        @Override
        public void run() {
            while (true) {
                count = list2.getItemCount();
                for (int i = 0; i < count; i++) {
                    friend = list2.getItem(i);
                    try {
                        dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeUTF("CMD_NOTIFICATION " + friend + " " + username);

                        Thread.sleep(1900);
                    } catch (Exception e) {
                    }

                }

            }

        }
    }

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
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Client client = new Client();
            client.setVisible(true);
            client.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    client.exitProcedure();
                }
            });
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private swing.button button1;
    private swing.button button2;
    private swing.button button3;
    private swing.button button4;
    private swing.button button5;
    private components.chat_body chat_body1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    public static javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea2;
    public static java.awt.List list1;
    private java.awt.List list2;
    // End of variables declaration//GEN-END:variables
}
