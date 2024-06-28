package Main;

import javax.sound.sampled.*;
import java.io.*;

public class AudioPlayer {
    private Clip clip;
    private boolean isPaused = false;
    private boolean isPlaying = false;
    private long clipTimePosition = 0;
    private long startTime = 0; // Track start time of current play

    /**
     * Load audio from a specified file path.
     *
     * @param filePath The path to the audio file.
     */
    public void loadAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            clip = (Clip) AudioSystem.getLine(info);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    isPlaying = false;
                    clip.setMicrosecondPosition(0); // Rewind to beginning after playback ends
                }
            });
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start playing the loaded audio.
     */
    public void play() {
        if (clip != null && !clip.isRunning()) {
            if (isPaused) {
                clip.setMicrosecondPosition(clipTimePosition);
                startTime = System.currentTimeMillis() - clipTimePosition / 1000; // Adjust startTime based on clipTimePosition
            } else {
                clip.start();
                startTime = System.currentTimeMillis();
                isPlaying = true;
            }
            isPaused = false;
        }
    }

    /**
     * Pause the currently playing audio.
     */
    public void pause() {
        if (clip != null && clip.isRunning()) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
            isPlaying = false;
        }
    }

    /**
     * Resume playback from the paused position.
     */
    public void resume() {
        if (clip != null && isPaused) {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
            long currentSystemTime = System.currentTimeMillis();
            startTime = currentSystemTime - (clipTimePosition / 1000); 
            isPlaying = true;
            isPaused = false;
        }
    }

    /**
     * Check if the audio is currently paused.
     *
     * @return True if audio is paused, false otherwise.
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Check if the audio is currently playing.
     *
     * @return True if audio is playing, false otherwise.
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Close the audio player and release resources.
     */
    public void close() {
        if (clip != null) {
            clip.close();
        }
    }
}
