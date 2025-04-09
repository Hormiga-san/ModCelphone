package net.hormiga.celphone.util;

import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class FileDownloader {

    public static void descargarArchivo(String urlStr, String destino) throws IOException {
        URL url = new URL(urlStr);
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(destino), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
