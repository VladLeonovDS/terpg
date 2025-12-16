package org.terpg.workdir;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class SoundEffects {
    private SoundEffects() {
    }

    public static void blipMenu() {
        playTone(880, 70);
    }

    public static void hit() {
        playTone(220, 140);
    }

    public static void sparkle() {
        playTone(1320, 80);
    }

    public static void victory() {
        playTone(660, 120);
        playTone(880, 160);
    }

    private static void playTone(int hz, int millis) {
        float sampleRate = 44100f;
        int length = (int) (millis * sampleRate / 1000);
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; i++) {
            double angle = 2.0 * Math.PI * i * hz / sampleRate;
            buffer[i] = (byte) (Math.sin(angle) * 120);
        }
        AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, true);
        SourceDataLine line = null;
        try {
            line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
        } catch (LineUnavailableException ex) {
            // Без звука продолжаем игру, если устройство недоступно
        } finally {
            if (line != null) {
                line.stop();
                line.close();
            }
        }
    }
}
