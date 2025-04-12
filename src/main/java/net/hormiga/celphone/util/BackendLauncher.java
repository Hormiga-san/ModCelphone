package net.hormiga.celphone.util;

import java.io.*;
import java.nio.file.*;

public class BackendLauncher {

    private static Process procesoBackend;

    public static void iniciarBackend() {
        try {
            Path destino = Paths.get("temp/backend.exe");

            // Solo copiar si no existe
            if (!Files.exists(destino)) {
                InputStream in = BackendLauncher.class.getClassLoader().getResourceAsStream("external/backend.exe");
                if (in == null) {
                    System.out.println("❌ No se encontró backend.exe en resources/external/");
                    return;
                }

                Files.createDirectories(destino.getParent());
                Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Copiado backend.exe a: " + destino.toAbsolutePath());
            }

            // Verifica si ya se está ejecutando (muy básico)
            if (procesoBackend != null && procesoBackend.isAlive()) {
                System.out.println("ℹ️ Backend ya está corriendo.");
                return;
            }

            // Ejecutar
            ProcessBuilder pb = new ProcessBuilder(destino.toAbsolutePath().toString());
            pb.redirectErrorStream(true);
            pb.inheritIO(); // muestra logs en consola
            procesoBackend = pb.start();

            System.out.println("🚀 Backend lanzado con éxito.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error al iniciar el backend.");
        }
    }

    public static void detenerBackend() {
        if (procesoBackend != null && procesoBackend.isAlive()) {
            procesoBackend.destroy();
            System.out.println("🛑 Backend detenido.");
        }
    }
}

