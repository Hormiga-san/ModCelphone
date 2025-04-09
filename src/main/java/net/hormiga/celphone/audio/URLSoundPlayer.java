package net.hormiga.celphone.audio;

import net.hormiga.celphone.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import java.io.File;
import java.io.IOException;

public class URLSoundPlayer {

    public static void playFromUrl(String videoUrl) {
        try {
            System.out.println("⬇ Descargando audio desde: " + videoUrl);

            // Ejecutar yt-dlp para descargar temp.webm
            ProcessBuilder ytDlp = new ProcessBuilder(
                    "yt-dlp",
                    "-f", "251",
                    "-o", "temp.webm",
                    videoUrl
            );
            ytDlp.inheritIO(); // Muestra logs en consola
            Process procesoDescarga = ytDlp.start();
            procesoDescarga.waitFor();

            File tempWebm = new File("temp.webm");
            if (!tempWebm.exists()) {
                System.out.println("❌ temp.webm no se creó.");
                return;
            }

            // Convertir a OGG con ffmpeg
            File oggFile = new File("resources/assets/celphone/sounds/custom.ogg");
            oggFile.getParentFile().mkdirs();

            ProcessBuilder ffmpeg = new ProcessBuilder(
                    "ffmpeg", "-y",
                    "-i", tempWebm.getAbsolutePath(),
                    "-vn",
                    "-acodec", "libvorbis",
                    oggFile.getAbsolutePath()
            );
            ffmpeg.inheritIO();
            Process procesoConvertir = ffmpeg.start();
            procesoConvertir.waitFor();

            System.out.println("✅ Archivo convertido a .ogg: " + oggFile.getAbsolutePath());

            // Reproducir usando SoundEvent registrado
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forMusic(ModSounds.CUSTOM.get())
            );

        } catch (IOException | InterruptedException e) {
            System.out.println("❌ Error al reproducir desde URL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
