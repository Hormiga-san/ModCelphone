package net.hormiga.celphone.util;

import com.google.gson.*;
import net.hormiga.celphone.data.Cancion;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class YTDLPHelper {

    public static File obtenerYtDlpEjecutable() throws IOException {
        Path destino = Paths.get("temp/yt-dlp.exe");

        if (!Files.exists(destino)) {
            InputStream in = YTDLPHelper.class.getClassLoader().getResourceAsStream("external/yt-dlp.exe");
            if (in == null) throw new FileNotFoundException("❌ yt-dlp.exe no se encontró en resources.");
            Files.createDirectories(destino.getParent());
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📄 yt-dlp ubicado en: " + destino.toAbsolutePath());

        }

        return destino.toFile();
    }

    public static void transformarURLsEnParalelo(List<Cancion> canciones) {
        if (canciones == null || canciones.isEmpty()) return;

        System.out.println("🧠 Transformando URLs para " + canciones.size() + " canciones...");

        canciones.parallelStream().forEach(c -> {
            if (c.getUrl() == null || c.getUrl().isEmpty()|| c.getUrl().contains("youtube.com")) {
                String urlConvertida = obtenerURLDirecta(c.getId()); // usa tu método existente
                if (urlConvertida != null && !urlConvertida.isEmpty()) {
                    c.setUrl(urlConvertida);
                    System.out.println("🔁 URL convertida: " + urlConvertida);
                } else {
                    System.out.println("⚠️ No se pudo convertir URL para: " + c.getTitulo());
                }
            }
        });
    }

    public static List<Cancion> buscarCancionesPorTexto(String query) {
        List<Cancion> canciones = new ArrayList<>();

        try {
            File ytDlp = obtenerYtDlpEjecutable();
            ProcessBuilder builder = new ProcessBuilder(
                    ytDlp.getAbsolutePath(), "-J", "ytsearch15:" + query
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();


            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder jsonBuilder = new StringBuilder();
                System.out.println("📦 JSON bruto devuelto por yt-dlp:");
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                Gson gson = new Gson();
                JsonObject root = gson.fromJson(jsonBuilder.toString(), JsonObject.class);
                if (!root.has("entries")) {
                    System.out.println("❌ No hay entradas 'entries' en el JSON. yt-dlp falló?");
                    return canciones;
                }
                JsonArray entries = root.getAsJsonArray("entries");

                for (JsonElement e : entries) {
                    JsonObject entry = e.getAsJsonObject();
                    String id = entry.get("id").getAsString();
                    String titulo = entry.get("title").getAsString();
                    String artista = entry.get("artista").getAsString(); // si querés, más adelante se puede mejorar

                    canciones.add(new Cancion(id, titulo, artista, ""));
                }
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error al buscar canciones: " + e.getMessage());
        }

        return canciones;
    }


   public static String obtenerURLDirecta(String videoId) {
        try {
            File ytDlp = obtenerYtDlpEjecutable();
            ProcessBuilder builder = new ProcessBuilder(
                ytDlp.getAbsolutePath(), "--no-playlist", "-f", "bestaudio", "--get-url",
                "https://www.youtube.com/watch?v=" + videoId
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String url = reader.readLine();
                if (url != null && url.startsWith("http")) {
                    return url.trim();
                }
            }

            process.waitFor();
        } catch (Exception e) {
            System.out.println("❌ Error al obtener URL con yt-dlp: " + e.getMessage());
        }

        return null;
    }



    public static List<String> obtenerURLsDirectas(List<String> videoIds) {
       List<String> urls = new ArrayList<>();

       try {
           File ytDlp = obtenerYtDlpEjecutable();

           List<String> command = new ArrayList<>();
           command.add(ytDlp.getAbsolutePath());
           command.add("--no-playlist");
           command.add("-f");
           command.add("bestaudio");
           command.add("--get-url");

           // Agrega todas las URLs de YouTube
           for (String id : videoIds) {
               command.add("https://www.youtube.com/watch?v=" + id);
           }

           ProcessBuilder builder = new ProcessBuilder(command);
           builder.redirectErrorStream(true);
           Process process = builder.start();

           try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
               String line;
               while ((line = reader.readLine()) != null) {
                   if (line.startsWith("http")) {
                       urls.add(line.trim());
                   }
               }
           }

           process.waitFor();
       } catch (Exception e) {
           System.out.println("❌ Error al obtener URLs con yt-dlp: " + e.getMessage());
       }

       return urls;
   }





}