package Main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceivingFileThread implements Runnable {

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    Client ClientFrame;
    StringTokenizer token;
    final int BUFFER_SIZE = 100;

    public ReceivingFileThread(Socket soc, Client clientframe) {
        this.socket = soc;
        this.ClientFrame = clientframe;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String data = dis.readUTF();
                token = new StringTokenizer(data);
                String CMD = token.nextToken();

                switch (CMD) {

                    case "CMD_SENDFILE":
                        String receiver,
                         sender = null;
                        try {
                            String filename = token.nextToken();
                            int filesize = Integer.parseInt(token.nextToken());
                            sender = token.nextToken();
                            receiver = token.nextToken();
                            ClientFrame.setMyTitle("Downloading File....");
                            String path = ClientFrame.getMyDownloadFolder() + filename;
                            FileOutputStream fos = new FileOutputStream(path);
                            InputStream input = socket.getInputStream();
                            ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(ClientFrame,
                                    "Downloading file please wait...", input);
                            BufferedInputStream bis = new BufferedInputStream(pmis);
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int count, percent = 0;
                            while ((count = bis.read(buffer)) != -1) {
                                percent = percent + count;
                                int p = (percent / filesize);
                                ClientFrame.setMyTitle("Downloading File  " + p + "%");
                                fos.write(buffer, 0, count);
                            }
                            fos.flush();
                            fos.close();
                            ClientFrame.setTitle(sender);
                            JOptionPane.showMessageDialog(null, "File has been downloaded to \n'" + path + "'");

                        } catch (IOException e) {
                            DataOutputStream eDos = new DataOutputStream(socket.getOutputStream());
                            eDos.writeUTF("CMD_SENDFILERESPONSE " + sender + " Connection was lost, please try again later.!");

                            System.out.println(e.getMessage());
                        }
                        break;

                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
