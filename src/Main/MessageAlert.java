package Main;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author New
 */
public class MessageAlert {

    String filename = null;
    InputStream music;
    File audiofile;
    AudioInputStream song;
    Clip clip;

    void CallMsg() {
        try {
            audiofile = new File("/Users/munachianya/Downloads/ExamProject_2.0/src/resources/public.wav"); 
            music = new FileInputStream(audiofile);
            song = AudioSystem.getAudioInputStream(audiofile);
            clip = AudioSystem.getClip();
            clip.open(song);
            clip.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    void FileMsg() {
        try {
            audiofile = new File("/Users/munachianya/Downloads/ExamProject_2.0/src/resources/sendFile.wav"); 

            music = new FileInputStream(audiofile);
            song = AudioSystem.getAudioInputStream(audiofile);
            clip = AudioSystem.getClip();
            clip.open(song);
            clip.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
       
    void PrivateMsg() {
        try {
            audiofile = new File("/Users/munachianya/Downloads/ExamProject_2.0/src/resources/private.wav"); 

            music = new FileInputStream(audiofile);
            song = AudioSystem.getAudioInputStream(audiofile);
            clip = AudioSystem.getClip();
            clip.open(song);
            clip.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

}
