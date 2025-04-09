package net.hormiga.celphone.media;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.player.base.MediaPlayer;

import java.net.URL;

public class CelphoneMediaPlayer {
    private static MediaPlayerFactory factory;
    private static MediaPlayer player;
    private static String currentUrl = "";

    public static void play(String urlString) {
        try {
            URL url = new URL(urlString); // convierte String → URL
            System.out.println("[Celphone] Reproduciendo desde: " + url);

            factory = new MediaPlayerFactory();
            player = factory.mediaPlayers().newMediaPlayer();

            player.media().start(url); // ✅ método que acepta URL
            currentUrl = urlString;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ URL inválida: " + urlString);
        }
    }
    public static void setVolume(int volume) {
        if (player != null) {
            player.audio().setVolume(volume);
            System.out.println("🔊 Volumen ajustado a: " + volume + "%");
        }
    }

    public static void pause() {
        if (player != null && player.status().isPlaying()) {
            player.controls().pause();
        }
    }

    public static void resume() {
        if (player != null && !player.status().isPlaying()) {
            player.controls().play();
        }
    }

    public static void stop() {
        if (player != null) {
            player.controls().stop();
            player.release();
            player = null;
        }
        if (factory != null) {
            factory.release();
            factory = null;
        }
        currentUrl = "";
    }

    public static boolean isPlaying() {
        return player != null && player.status().isPlaying();
    }
}

