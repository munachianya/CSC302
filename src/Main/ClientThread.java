package Main;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import components.chat_body;
import components.files_items;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;

public class ClientThread implements Runnable {

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    Client clientFrame;
    StringTokenizer token;
    Login loginform;
    register registerform;
    chat_body chat;
    MessageAlert alert;
    files_items files;

    public ClientThread(Socket socket, Client ClientFrame, Login loginform) {
        this.clientFrame = ClientFrame;
        this.loginform = loginform;
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {

        }
    }

    public ClientThread(Socket socket, Client ClientFrame, register registerform) {
        this.clientFrame = ClientFrame;
        this.registerform = registerform;
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {

        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String data = dis.readUTF();
                token = new StringTokenizer(data);
                /**
                 * Get Message CMD *
                 */
                String CMD = token.nextToken();

                switch (CMD) {
                    case "CMD_USERONLINE":
                        String newUser = token.nextToken();
                        clientFrame.displayOptionPane(newUser + " is online now");
                        break;
                    case "CMD_USEROFFLINE":
                        String User = token.nextToken();
                        clientFrame.displayOptionPane(User + " has gone offline");
                        break;
                    case "CMD_LOGINCONFIRMED":
                        String username = token.nextToken();
                        String socket = token.nextToken();
                        clientFrame.setVisible(true);
                        clientFrame.username = username;
                        clientFrame.socket = this.socket;
                        clientFrame.setTitle(username);
                        loginform.dispose();
                        break;
                    case "CMD_LOGINNOTCONFIRMED":
                        JOptionPane.showMessageDialog(loginform, "Wrong Login Details");
                        break;
                    case "CMD_REGISTERCONFIRMED":
                        JOptionPane.showMessageDialog(registerform, "Registration Successful");
                        break;

                    case "CMD_REGISTRATIONNOTCONFIRMED":
                        JOptionPane.showMessageDialog(registerform, "Issue with Registration");
                        break;

                    case "CMD_ONLINE":
                        Vector online = new Vector();
                        while (token.hasMoreTokens()) {
                            String list = token.nextToken();

                            if (!list.equalsIgnoreCase(clientFrame.username)) {

                                online.add(list);

                            }
                        }
                        System.out.println(online);

                        clientFrame.appendOnlineList(online);
                        break;

                    case "CMD_CHAT":

                        String msgs = "";
                        String from = token.nextToken();
                        String date = token.nextToken();
                        String time = token.nextToken();
                        while (token.hasMoreTokens()) {
                            msgs = msgs + " " + token.nextToken();
                        }

                        String active = clientFrame.jLabel2.getText();
                        if (active.equalsIgnoreCase(from)) {
                            alert = new MessageAlert();
                            alert.CallMsg();
                            clientFrame.appendLeftText(date + " " + time, msgs);
                        }
                        break;

                    case "CMD_FETCHMYMSG":

                        String msg = "";
                        date = token.nextToken();
                        time = token.nextToken();
                        while (token.hasMoreTokens()) {
                            msg = msg + " " + token.nextToken();
                        }
                        // System.out.println("msg:" + msg + "time: " + time);
                        clientFrame.appendRightText(date + " " + time, msg);

                        break;
                    case "CMD_FETCHMSG":

                        msg = "";
                        date = token.nextToken();
                        time = token.nextToken();
                        while (token.hasMoreTokens()) {
                            msg = msg + " " + token.nextToken();
                        }
                        // System.out.println("msg:" + msg + "time: " + time);
                        clientFrame.appendLeftText(date + " " + time, msg);

                        break;

                    case "CMD_NOTIFICATION":

                        msg = "";

                        while (token.hasMoreTokens()) {
                            msg = msg + " " + token.nextToken();
                        }
                        clientFrame.appendNotification(msg + "\n");

                        break;
                    case "CMD_FETCHMYFILE":
                        date = token.nextToken();
                        time = token.nextToken();
                        String filename = token.nextToken();
                        String MessageID = token.nextToken();
                        files = new files_items(filename, MessageID, date + time);
                        clientFrame.appendRight(files);
                        break;
                    case "CMD_FETCHFILE":
                        date = token.nextToken();
                        time = token.nextToken();
                        filename = token.nextToken();
                        MessageID = token.nextToken();
                        files = new files_items(filename, MessageID, date + time);
                        clientFrame.appendLeft(files);
                        break;
                    case "CMD_FETCHMYVOICENOTE":
                        date = token.nextToken();
                        time = token.nextToken();
                        String Duration = token.nextToken();
                        clientFrame.appendLeftText(date + " " + time, Duration);
                        break;
                    case "CMD_FETCHVOICENOTE":
                        date = token.nextToken();
                        time = token.nextToken();
                        Duration = token.nextToken();
                        clientFrame.appendLeftText(date + " " + time, Duration);
                        break;
                    case "CMD_FILE_XD":  // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                        String sender = token.nextToken();
                        String receiver = token.nextToken();
                        String fname = token.nextToken();
                        MessageID = token.nextToken();
                        int confirm = JOptionPane.showConfirmDialog(clientFrame,
                                "From: " + sender + "\nFilename: " + fname + "\nWould you like to Accept.?");
                        alert = new MessageAlert();
                        alert.FileMsg();
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        time = now.format(formatter);
                        if (confirm == 0) {

                            clientFrame.openFolder();
                            try {
                                files = new files_items(fname, MessageID, time);
                                clientFrame.appendLeft(files);
                                dos = new DataOutputStream(this.socket.getOutputStream());
                                // Format:  CMD_SEND_FILE_ACCEPT [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ACCEPT " + sender + " accepted";
                                dos.writeUTF(format);

                                /*  this will create a filesharing socket to handle incoming file
                                and this socket will automatically closed when it's done.  */
                                Socket fSoc = new Socket(clientFrame.getMyHost(), clientFrame.getMyPort());
                                DataOutputStream fdos = new DataOutputStream(fSoc.getOutputStream());
                                System.out.println("Receiver CMD_SHARINGSOCKET " + clientFrame.getMyUsername());
                                fdos.writeUTF("CMD_SHARINGSOCKET " + clientFrame.getMyUsername());
                                /*  Run Thread for this   */
                                new Thread(new ReceivingFileThread(fSoc, clientFrame)).start();
                                
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: " + e.getMessage());
                            }
                        } else { // client rejected the request, then send back result to sender
                            try {
                                dos = new DataOutputStream(this.socket.getOutputStream());
                                // Format:  CMD_SEND_FILE_ERROR [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ERROR " + sender + " Client rejected your request or connection was lost.!";
                                dos.writeUTF(format);
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: " + e.getMessage());
                            }
                        }
                        break;
                    case "CMD_FILESENT":
                        MessageID = token.nextToken();
                        fname = token.nextToken();
                        time = token.nextToken();
                        files = new files_items(fname, MessageID, time);
                        clientFrame.appendRight(files);
                        break;
                    default:

                        break;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
