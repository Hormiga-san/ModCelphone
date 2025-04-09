package net.hormiga.celphone.util;

import java.io.*;
import java.nio.file.Files;

public class YTDLPHelper {
    public static String obtenerUrlDeAudio(String videoUrl) {
        try {
            // Ruta completa al ejecutable
            File tempExe = new File("run/modcelphone/yt-dlp.exe");

            if (!tempExe.exists()) {
                System.out.println("📥 yt-dlp.exe no encontrado, intentando copiar...");
                try (InputStream in = YTDLPHelper.class.getResourceAsStream("/external/yt-dlp.exe")) {
                    if (in == null) {
                        System.out.println("❌ yt-dlp.exe no se encontró dentro del JAR.");
                        return null;
                    }
                    Files.copy(in, tempExe.toPath());
                    System.out.println("✅ yt-dlp.exe copiado a " + tempExe.getAbsolutePath());
                }
            }

            System.out.println("▶️ Ejecutando yt-dlp para: " + videoUrl);
            ProcessBuilder pb = new ProcessBuilder(tempExe.getAbsolutePath(), "-f", "bestaudio", "--get-url", videoUrl);

            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String url = reader.readLine();
            process.waitFor();

            if (url == null || url.trim().isEmpty()) {
                System.out.println("❌ yt-dlp no devolvió una URL");
                return null;
            }

            System.out.println("🔗 URL obtenida: " + url);
            return url;

        } catch (Exception e) {
            System.out.println("❌ Error al ejecutar yt-dlp: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

