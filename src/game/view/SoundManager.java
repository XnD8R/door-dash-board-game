package game.view;

import javax.sound.sampled.*;

public class SoundManager {

    public static void playWinSound() {
        new Thread(() -> {
            playTone(523, 150);
            playTone(659, 150);
            playTone(784, 150);
            playTone(1047, 400);
        }).start();
    }

    public static void playLoseSound() {
        new Thread(() -> {
            playTone(392, 150);
            playTone(349, 150);
            playTone(311, 150);
            playTone(261, 400);
        }).start();
    }

    public static void playRollSound() {
        new Thread(() -> playTone(440, 80)).start();
    }

    public static void playCardSound() {
        new Thread(() -> {
            playTone(587, 80);
            playTone(740, 120);
        }).start();
    }

    public static void playPowerupSound() {
        new Thread(() -> {
            playTone(880, 80);
            playTone(1047, 80);
            playTone(1319, 200);
        }).start();
    }

    private static void playTone(int frequency, int duration) {
        try {
            AudioFormat format = new AudioFormat(44100, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            byte[] buffer = new byte[44100 * duration / 1000];
            for (int i = 0; i < buffer.length; i++) {
                double angle = 2.0 * Math.PI * frequency * i / 44100;
               
                double fade = 1.0 - (double) i / buffer.length;
                buffer[i] = (byte) (Math.sin(angle) * 70 * fade);
            }

            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        } catch (Exception e) {
           
        }
    }
}
/*
 * ============================================================
 * AI USAGE DECLARATION - Milestone 3
 * ============================================================
 * AI was used to generate the entire SoundManager class.
 *
 * AI Generated:
 * - Programmatic tone generation using javax.sound.sampled
 * - Frequency values for win, lose, roll, card, powerup sounds
 * - Fade-out effect on tones to avoid audio clicking
 * - Threading to avoid blocking the JavaFX thread
 *
 * I Verified and Changed:
 * - Verified sounds played correctly without crashing the game
 * - Verified threading did not cause JavaFX thread conflicts
 * - Tested all sound triggers worked at correct game moments
 * ============================================================
 */