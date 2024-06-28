package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.sql.Statement;

public class SocketThread implements Runnable {

    Socket socket;
    Server serverform;
    DataInputStream dis;
    StringTokenizer st;
    dbconnection con;
    PreparedStatement ps;
    ResultSet rs;
    String client, oname, sname, username, email, phone, password;
    String encryptedMsg;
    String decryptedMsg;
    final int BUFFER_SIZE = 100;

    public SocketThread(Socket socket, Server serverform) {
        this.serverform = serverform;
        this.socket = socket;

        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createConnection(String receiver, String sender, String filename, int MessageID) {
        try {
            Socket s = serverform.getClientList(receiver);
            if (s != null) {
                DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                String format = "CMD_FILE_XD " + sender + " " + receiver + " " + filename + " " + MessageID;
                dosS.writeUTF(format);
            } else {
                serverform.appendMessage("Client was not found '" + receiver + "'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_SENDFILEERROR Client '" + receiver + "' was not found in the list, make sure it is on the online list.!");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String data = dis.readUTF();
                st = new StringTokenizer(data);
                String CMD = st.nextToken();
                serverform.appendMessage(data + " connected \n");
                /**
                 * Check COMMAND *
                 */
                switch (CMD) {
                    case "CMD_LOGIN":
                        /**
                         * CMD_LOGIN [Username] [PASSWORD]*
                         */
                        username = st.nextToken();
                        password = st.nextToken();
                        client = username;
                        try {
                            DataOutputStream dos;
                            con = new dbconnection();
                            ps = con.psStatement("Select * from login where username=? and password=? ");
                            ps.setString(1, username);
                            ps.setString(2, password);
                            rs = ps.executeQuery();

                            if (rs.next()) {

                                serverform.appendMessage(username + " connected \n");

                                dos = new DataOutputStream(socket.getOutputStream());
                                dos.writeUTF("CMD_LOGINCONFIRMED " + username + " " + socket);
                                dos.flush();
                                serverform.setClientList(username);
                                serverform.setSocketList(socket);
                                serverform.appendMessage("" + username + " logged in successfully");
                                serverform.broadcastMessage(username, socket);
                            } else {
                                dos = new DataOutputStream(socket.getOutputStream());
                                dos.writeUTF("CMD_LOGINNOTCONFIRMED " + username);
                                dos.flush();
                            }
                        } catch (Exception e) {
                            serverform.appendMessage("Error when trying to log in" + username + e);
                        }

                        break;
                    case "CMD_LOGOUT":
                        username = st.nextToken();
                        serverform.broadcastOfflineMessage(username);
                        break;
                    case "CMD_REGISTER":
                        /**
                         * CMD_REGISTER [SNAME] [ONAME] [EMAIL] [PHONE]
                         * [USERNAME] [PASSWORD]*
                         */
                        DataOutputStream dos = null;
                        try {
                            sname = st.nextToken();
                            oname = st.nextToken();
                            email = st.nextToken();
                            phone = st.nextToken();
                            username = st.nextToken();
                            password = st.nextToken();
                            client = username;

                            con = new dbconnection();
                            PreparedStatement ps = con.psStatement("insert into login values (?,?,?,?,?,?) ");
                            ps.setString(1, sname);
                            ps.setString(2, oname);
                            ps.setString(3, email);
                            ps.setString(4, phone);
                            ps.setString(5, username);
                            ps.setString(6, password);
                            int rs = ps.executeUpdate();
                            serverform.appendMessage(username + " connected \n");

                            dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF("CMD_REGISTERCONFIRMED " + username);
                            dos.flush();
                            serverform.appendMessage("" + username + " REGISTERD  successfully");

                        } catch (Exception e) {
                            dos.writeUTF("CMD_REGISTRATIONNOTCONFIRMED " + username);
                            serverform.appendMessage("Error when trying to register" + username + e);
                        }

                        break;

                    case "CMD_CHAT":
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String Timestamp = now.format(formatter);
                        String from = st.nextToken();
                        String sendTo = st.nextToken();
                        String key = st.nextToken();
                        StringBuilder msgBuilder = new StringBuilder();
                        while (st.hasMoreTokens()) {
                            msgBuilder.append(" ").append(st.nextToken());
                        }
                        String msg = msgBuilder.toString().trim();
                        try {
                            byte[] encryptedText = Encryption.encrypt(msg, key);
                            encryptedMsg = Encryption.bytesToHex(encryptedText);
                            decryptedMsg = Encryption.decrypt(encryptedText, key);
                            System.out.println(encryptedMsg);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(serverform, "Error encrypting message: " + e.getMessage());
                        }

                        if (encryptedMsg != null) {
                            try {
                                con = new dbconnection();
                                PreparedStatement ps = con.psStatement(
                                        "INSERT INTO Messages (sender, receiver, message, messageType, timeStamp, status) VALUES (?, ?, ?, ?, ?, ?)"
                                );
                                ps.setString(1, from);
                                ps.setString(2, sendTo);
                                ps.setString(3, encryptedMsg);
                                ps.setString(4, "text");
                                ps.setString(5, Timestamp);
                                ps.setString(6, "Not Read");
                                int rs = ps.executeUpdate();

                                if (rs > 0) {
                                    System.out.println("this happened for " + from);
                                    Socket tsoc = serverform.getClientList(sendTo);
                                    try {
                                        if (tsoc != null) {
                                            dos = new DataOutputStream(tsoc.getOutputStream());
                                            String content = from + " " + Timestamp + " " + decryptedMsg;
                                            // Send the message to the recipient
                                            dos.writeUTF("CMD_CHAT " + content);
                                            dos.flush();
                                            // After successfully sending the message, update the message status to "Read"
                                            PreparedStatement pst = con.psStatement(
                                                    "UPDATE Messages SET status=? WHERE sender=? AND receiver=? AND timestamp=?"
                                            );
                                            pst.setString(1, "Read");
                                            pst.setString(2, from);
                                            pst.setString(3, sendTo);
                                            pst.setString(4, Timestamp);
                                            pst.executeUpdate();
                                        } else {
                                            // Log the scenario where the recipient is not online
                                            serverform.appendMessage("Recipient " + sendTo + " is not online. Message stored in the database.");
                                        }
                                    } catch (IOException | SQLException e) {
                                        serverform.appendMessage("Error sending message to " + sendTo + ": " + e.getMessage());
                                    }
                                }
                            } catch (SQLException e) {
                                JOptionPane.showMessageDialog(serverform, "Error storing message in database: " + e.getMessage());
                            }
                        }

                        serverform.appendMessage("Message: From " + from + " To " + sendTo + " : " + msg);

                        break;

                    case "CMD_GETMESSAGES":
                        /**
                         * CMD_GETMESSAGES [from] [user] [key]
                         */
                        String with = st.nextToken();
                        String me = st.nextToken();
                        key = st.nextToken();
                        Socket mysocket = serverform.getClientList(me);

                        try {
                            dos = new DataOutputStream(mysocket.getOutputStream());
                            con = new dbconnection();
                            // Select messages between the two users ordered by timestamp
                            ps = con.psStatement(
                                    "SELECT id, sender, receiver, message, messageType, timeStamp FROM Messages "
                                    + "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) "
                                    + "ORDER BY timestamp"
                            );
                            ps.setString(1, me);
                            ps.setString(2, with);
                            ps.setString(3, with);
                            ps.setString(4, me);
                            rs = ps.executeQuery();

                            while (rs.next()) {
                                int messageId = rs.getInt("id");
                                String sender = rs.getString("sender");
                                String message = rs.getString("message");
                                String messageType = rs.getString("messageType");
                                String time = rs.getString("timeStamp");

                                boolean sentByMe = sender.equalsIgnoreCase(me);
                                if (messageType.equals("text")) {
                                    // Decrypt text message
                                    byte[] decryptedMsg = Encryption.hexToBytes(message);
                                    String decryptedText = Encryption.decrypt(decryptedMsg, key);
                                    if (sentByMe) {
                                        //text messages i sent 
                                        dos.writeUTF("CMD_FETCHMYMSG " + time + " " + decryptedText);
                                    } else {
                                        //text messages i recieved
                                        dos.writeUTF("CMD_FETCHMSG " + time + " " + decryptedText);
                                    }
                                } else if (messageType.equals("file")) {
                                    // Fetch file details from the Files table
                                    PreparedStatement filePs = con.psStatement("SELECT fileName, fileExtension FROM Files WHERE messageID = ?");
                                    filePs.setInt(1, messageId);
                                    ResultSet fileRs = filePs.executeQuery();
                                    if (fileRs.next()) {
                                        String fileName = fileRs.getString("fileName");
                                        String fileExtension = fileRs.getString("fileExtension");
                                        String fname = fileName + "." + fileExtension;
                                        if (sentByMe) {
                                            dos.writeUTF("CMD_FETCHMYFILE " + time + " " + fname + " " + messageId);
                                        } else {
                                            dos.writeUTF("CMD_FETCHFILE " + time + " " + fname + " " + messageId);
                                        }
                                    }
                                } else if (messageType.equals("voicenote")) {
                                    // Fetch voice note details from the VoiceNotes table
                                    PreparedStatement voiceNotePs = con.psStatement("SELECT Duration FROM VoiceNotes WHERE messageID = ?");
                                    voiceNotePs.setInt(1, messageId);
                                    ResultSet voiceNoteRs = voiceNotePs.executeQuery();
                                    if (voiceNoteRs.next()) {
                                        String Duration = voiceNoteRs.getString("Duration");
                                        if (sentByMe) {
                                            dos.writeUTF("CMD_FETCHMYVOICENOTE " + time + " " + Duration);
                                        } else {
                                            dos.writeUTF("CMD_FETCHVOICENOTE " + time + " " + Duration);
                                        }
                                    }
                                }
                            }

                            // Update message status from Not Read to Read
                            ps = con.psStatement(
                                    "UPDATE Messages SET status = ? WHERE sender = ? AND receiver = ? AND status = ?"
                            );
                            ps.setString(1, "Read");
                            ps.setString(2, with);
                            ps.setString(3, me);
                            ps.setString(4, "Not Read");
                            ps.executeUpdate();

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(serverform, e);
                        }

                        serverform.appendMessage("Retrieve messages between " + with + " and " + me);

                        break;

                    case "CMD_SHARINGSOCKET":
                        serverform.appendMessage("CMD_SHARINGSOCKET : Client stablish a socket connection for file sharing...");
                        String file_sharing_username = st.nextToken();
                        serverform.setClientFileSharingUsername(file_sharing_username);
                        serverform.setClientFileSharingSocket(socket);
                        serverform.appendMessage("CMD_SHARINGSOCKET : Username: " + file_sharing_username);

                        break;

                    case "CMD_SENDFILE":
                        serverform.appendMessage("CMD_SENDFILE : Client sending a file...");
                        /*
                        Format: CMD_SENDFILE [Filename] [Size] [Recipient] [Sender]  from: Sender Format
                        Format: CMD_SENDFILE [Filename] [Size] [Sender] to Receiver Format
                         */
                        String file_name = st.nextToken();
                        String filesize = st.nextToken();
                        String sendto = st.nextToken();
                        String Sender = st.nextToken();
                        serverform.appendMessage("CMD_SENDFILE : From: " + Sender);
                        serverform.appendMessage("CMD_SENDFILE : To: " + sendto);
                        serverform.appendMessage("CMD_SENDFILE : preparing connections..");
                        Socket cSock = serverform.getClientFileSharingSocket(sendto);

                        if (cSock != null) {
                            try {

                                DataOutputStream cDos = new DataOutputStream(cSock.getOutputStream());
                                cDos.writeUTF("CMD_SENDFILE " + file_name + " " + filesize + " " + Sender + " " + sendto);
                                InputStream input = socket.getInputStream();
                                OutputStream sendFile = cSock.getOutputStream();
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int cnt;
                                while ((cnt = input.read(buffer)) > 0) {
                                    sendFile.write(buffer, 0, cnt);
                                }
                                sendFile.flush();
                                sendFile.close();
                                serverform.removeClientFileSharing(sendto);
                                serverform.removeClientFileSharing(Sender);

                            } catch (IOException e) {
                                System.err.println(e.getMessage());
                            }
                        } else {
                            /*   FORMAT: CMD_SENDFILEERROR  */
                            serverform.removeClientFileSharing(Sender);
                            serverform.appendMessage("CMD_SENDFILE : Client '" + sendto + "' was not found.!");
                            dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + sendto + "' was not found, File Sharing will exit.");
                        }
                        break;

                    case "CMD_SENDFILERESPONSE":
                        /*
                        Format: CMD_SENDFILERESPONSE [username] [Message]
                         */
                        String receiver = st.nextToken();
                        String rMsg = "";
                        serverform.appendMessage("[CMD_SENDFILERESPONSE]: username: " + receiver);
                        while (st.hasMoreTokens()) {
                            rMsg = rMsg + " " + st.nextToken();
                        }
                        try {
                            Socket rSock = (Socket) serverform.getClientFileSharingSocket(receiver);
                            DataOutputStream rDos = new DataOutputStream(rSock.getOutputStream());
                            rDos.writeUTF("CMD_SENDFILERESPONSE" + " " + receiver + " " + rMsg);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                        break;

                    case "CMD_SEND_FILE_XD":
                      try {
                        int MessageID = 0;
                        String send_filepath = "";
                        String send_sender = st.nextToken();
                        String send_receiver = st.nextToken();
                        String send_filename = st.nextToken();
                        String fname = st.nextToken();
                        String extension = st.nextToken();
                        if (st.hasMoreTokens()) {
                            send_filepath = st.nextToken();
                        }
                        while (st.hasMoreTokens()) {
                            send_filepath = send_filepath + " " + st.nextToken();
                        }
                        send_filepath = send_filepath.trim();
                        System.out.println("File path: '" + send_filepath + "'");
                        Path path = Paths.get(send_filepath);
                        if (!Files.exists(path)) {
                            throw new IOException("File not found: " + send_filepath);
                        }
                        dos = new DataOutputStream(socket.getOutputStream());
                        byte[] fileBytes = Files.readAllBytes(path);
                        System.out.println("it gets to the top of the send file xd");
                        con = new dbconnection();
                        PreparedStatement ps = con.psStatement("INSERT INTO Messages (sender, receiver, message, messageType, timeStamp, status) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, send_sender);
                        ps.setString(2, send_receiver);
                        ps.setString(3, send_filename);
                        ps.setString(4, "file");
                        ps.setString(5, "Not Read");
                        ps.executeUpdate();

                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            System.out.println("it gmade it past the upload to messages table");
                            MessageID = rs.getInt(1);
                            PreparedStatement st = con.psStatement("INSERT INTO Files (messageID, file, fileName, fileExtension, timeStamp) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)");
                            st.setInt(1, MessageID);
                            st.setBytes(2, fileBytes);
                            st.setString(3, fname);
                            st.setString(4, extension);
                            st.executeUpdate();
                        }
                        LocalDateTime Time = LocalDateTime.now();
                        DateTimeFormatter formatt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String DateTime = Time.format(formatt);
                        dos.writeUTF("CMD_FILESENT" + MessageID + fname + DateTime);
                        
                        serverform.appendMessage("CMD_SEND_FILE_XD Host: " + send_sender);

                        Socket s = serverform.getClientList(send_receiver);
                        System.out.println("socket is : " + s);
                        if (s != null) {
                            // Receiver is online
                            DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                            String format = "CMD_FILE_XD " + send_sender + " " + send_receiver + " " + send_filename + " " + MessageID;
                            dosS.writeUTF(format);
                        } else {
                            // Receiver is offline
                            System.out.println("User is offline");
                            serverform.appendMessage("Client was not found '" + send_receiver + "'");
                            // 
                            //dos.writeUTF("CMD_SENDFILEERROR Client '" + send_receiver + "' was not found in the list, make sure it is on the online list.!");
                        }
                        
                    } catch (Exception e) {
                        System.err.println("Error in CMD_SEND_FILE_XD: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                    case "CMD_SEND_FILE_ERROR":
                        // Format:  CMD_SEND_FILE_ERROR [receiver] [Message]
                        String eReceiver = st.nextToken();
                        String eMsg = "";
                        while (st.hasMoreTokens()) {
                            eMsg = eMsg + " " + st.nextToken();
                        }
                        try {

                            Socket eSock = serverform.getClientFileSharingSocket(eReceiver);
                            DataOutputStream eDos = new DataOutputStream(eSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ERROR [Message]
                            eDos.writeUTF("CMD_RECEIVE_FILE_ERROR " + eMsg);
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                        break;

                    case "CMD_SEND_FILE_ACCEPT": // Format:  CMD_SEND_FILE_ACCEPT [receiver] [Message]
                        String aReceiver = st.nextToken();
                        String aMsg = "";
                        while (st.hasMoreTokens()) {
                            aMsg = aMsg + " " + st.nextToken();
                        }
                        try {
                            /*  Send Error to the File Sharing host  */
                            Socket aSock = serverform.getClientFileSharingSocket(aReceiver); // get the file sharing host socket for connection
                            DataOutputStream aDos = new DataOutputStream(aSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ACCEPT [Message]
                            aDos.writeUTF("CMD_RECEIVE_FILE_ACCEPT " + aMsg);
                        } catch (IOException e) {
                            serverform.appendMessage("[CMD_RECEIVE_FILE_ERROR]: " + e.getMessage());
                        }
                        break;
                    case "CMD_NOTIFICATION":

                        /**
                         * CMD_GETMESSAGES [from] [users]*
                         */
                        String friend = st.nextToken();
                        String myusername = st.nextToken();
                        Socket Mysocket = serverform.getClientList(myusername);
                        int count = 0;

                        try {
                            dos = new DataOutputStream(Mysocket.getOutputStream());
                            con = new dbconnection();

                            ps = con.psStatement("SELECT count(*) FROM messages where sender=? and receiver=? and status=?");
                            ps.setString(1, friend);
                            ps.setString(2, myusername);
                            ps.setString(3, "Not Read");
                            rs = ps.executeQuery();
                            while (rs.next()) {
                                count = rs.getInt(1);
                            }

                            dos.writeUTF("CMD_NOTIFICATION You have " + count + " Unread message(s) from " + friend);

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(serverform, e);
                        }

                        serverform.appendMessage("Notification for " + friend + " and " + myusername);

                        break;

                    default:
                        serverform.appendMessage("Unknown Command " + CMD);
                        break;
                }
            }
        } catch (IOException e) {

            serverform.removeFromTheList(client);
            serverform.appendMessage("Connection closed..!");
        }
    }

}
