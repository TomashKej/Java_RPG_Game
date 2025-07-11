/**
 * Audio playback utility for handling background music and sound effects.
 * Supports play, stop, mute/unmute, and status check.
 */
package tools;

/**
 * @author TomaszKaczmarek
 */

import javax.sound.sampled.*;
import java.net.URL;

public class Audio {
    private static Clip clip;
    private static boolean isEnabled = true;

    /**
     * Plays the audio from the given path if sound is enabled.
     * Loops the audio continuously.
     * @param path the path to the audio file
     */
    public static void play(String path) {
        if (!isEnabled) return;

        try {
            URL url = Audio.class.getResource(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the audio if it's currently playing.
     */
    public static void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Enables audio playback and resumes if previously loaded.
     */
    public static void enable() {
        isEnabled = true;
        if (clip != null) clip.start();
    }

    /**
     * Disables audio playback and stops the current sound.
     */
    public static void disable() {
        isEnabled = false;
        stop();
    }

    /**
     * Returns current audio status.
     * @return true if audio is enabled
     */
    public static boolean status() {
        return isEnabled;
    }
}
