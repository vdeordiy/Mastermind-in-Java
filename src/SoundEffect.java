import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundEffect implements LineListener {
    private static SoundEffect instance;
    private Clip clip;
    private AudioInputStream audioInputStream;
    private boolean playing = false;

    public SoundEffect() {
    }

    public static synchronized SoundEffect getInstance() {
        // Singleton pattern
        if (instance == null) {
            instance = new SoundEffect();
        }

        return instance;
    }

    @Override
    public void update(LineEvent lineEvent) {
        if (lineEvent.getType() == LineEvent.Type.STOP) {
            // Close resources
            if (clip != null) {
                clip.close();
            }
            if (audioInputStream != null) {
                try {
                    audioInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            playing = false;
        } else if (lineEvent.getType() == LineEvent.Type.START) {
            playing = true;
        }
    }

    public void playSound(String filename) {
        // Debounce, one sound at a time
        if (playing) {
            return;
        }

        audioInputStream = null;
        clip = null;

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);

            audioInputStream = AudioSystem.getAudioInputStream(inputStream);

            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);

            clip = (Clip) AudioSystem.getLine(info);

            clip.addLineListener(this);

            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}