package net.hormiga.celphone.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.hormiga.celphone.data.Cancion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CelularScreen extends Screen {
    private final int guiWidth = 290;
    private final int guiHeight = 320;

    private EditBox buscador;
    private EditBox urlPlaylistBox;
    private String mensajeError = "";
    private List<Cancion> resultados = new ArrayList<>();
    private int scrollOffset = 0;
    private final int maxVisibleResultados = 12;
    private boolean cargando = true;


    public CelularScreen() {
        super(Component.literal(""));
    }

    @Override
    protected void init() {
        this.clearWidgets();

        // Alineado al centro total, como en el render
        int x = (this.width - (guiWidth * 2 + 20)) / 2;
        int y = (this.height - guiHeight) / 2;

        int paddingX = x + 10;
        int currentY = y + 20;

        // Campo de búsqueda
        buscador = new EditBox(this.font, paddingX, currentY, 160, 20, Component.literal("Buscar"));
        buscador.setMaxLength(100);
        this.addRenderableWidget(buscador);
        currentY += 30;

        // Botón Buscar
        this.addRenderableWidget(Button.builder(Component.literal("Buscar"), (button) -> {
            String texto = buscador.getValue().trim();

            if (!texto.isEmpty()) {
                try {
                    String url = "https://36cb-186-107-115-89.ngrok-free.app/buscar";
                    String resultsUrl = "https://36cb-186-107-115-89.ngrok-free.app/results.json";
                    String jsonInput = "{\"query\":\"" + texto + "\"}";

                    java.net.URL urlObj = new java.net.URL(url);
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) urlObj.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setDoOutput(true);

                    try (java.io.OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInput.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int status = connection.getResponseCode();
                    if (status == 200) {
                        String destino = "modcelphone/results.json";
                        File carpeta = new File("modcelphone");
                        if (!carpeta.exists()) carpeta.mkdirs();

                        boolean descargado = false;
                        for (int i = 0; i < 5; i++) {
                            try {
                                net.hormiga.celphone.util.FileDownloader.descargarArchivo(resultsUrl, destino);
                                descargado = true;
                                break;
                            } catch (IOException ex) {
                                Thread.sleep(1000);
                            }
                        }

                        mensajeError = descargado ? "" : "❌ No se pudo descargar results.json.";
                    } else {
                        mensajeError = "❌ Error del servidor: " + status;
                    }

                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    mensajeError = "❌ " + e.getClass().getSimpleName() + ": " + e.getMessage();
                }
            }
        }).pos(paddingX, currentY).size(160, 20).build());
        currentY += 50;

        // Campo URL de playlist
        urlPlaylistBox = new EditBox(this.font, paddingX, currentY, 160, 20, Component.literal("URL Playlist"));
        urlPlaylistBox.setMaxLength(500);
        this.addRenderableWidget(urlPlaylistBox);
        currentY += 30;

        // Botón Cargar lista
        this.addRenderableWidget(Button.builder(Component.literal("Cargar lista"), (button) -> {
            String url = urlPlaylistBox.getValue().trim();

            if (!url.isEmpty()) {
                new Thread(() -> {
                    try {
                        String endpoint = "https://36cb-186-107-115-89.ngrok-free.app/playlist";
                        String jsonInput = "{\"url\":\"" + url + "\"}";

                        java.net.URL urlObj = new java.net.URL(endpoint);
                        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) urlObj.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json; utf-8");
                        connection.setDoOutput(true);

                        try (java.io.OutputStream os = connection.getOutputStream()) {
                            byte[] input = jsonInput.getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }

                        int status = connection.getResponseCode();
                        System.out.println("🌐 Respuesta del servidor: " + status);

                        if (status == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line.trim());
                            }

                            File carpeta = new File("modcelphone");
                            if (!carpeta.exists()) carpeta.mkdirs();

                            try (FileWriter fw = new FileWriter("modcelphone/results.json")) {
                                fw.write(response.toString());
                            }

                            mensajeError = "";
                            System.out.println("✅ Playlist guardada en results.json");
                        } else {
                            mensajeError = "❌ Error del servidor: " + status;
                            System.out.println("❌ Código de error: " + status);
                        }

                        connection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mensajeError = "❌ " + e.getClass().getSimpleName() + ": " + e.getMessage();
                    }
                }).start();
            }
        }).pos(paddingX, currentY).size(160, 20).build());

        currentY += 50;
        // Botón iniciar sesión
        this.addRenderableWidget(Button.builder(Component.literal("Iniciar sesión"), (b) -> {
            try {
                File dir = new File("run");
                if (!dir.exists()) dir.mkdirs();
                FileWriter writer = new FileWriter("run/login.txt");
                writer.write("login");
                writer.close();
                System.out.println("🔐 Solicitud de inicio de sesión enviada.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).pos(paddingX, currentY).size(160, 20).build());
        currentY += 30;
    }

    private int resultadoX = 0;
    private int resultadoY = 0;

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        cargando = true;
        this.renderBackground(graphics);
        // Dibujar fondo grande (celular extendido)
        int x = (this.width - (guiWidth * 2 + 20)) / 2; // centro total
        int y = (this.height - guiHeight) / 2;
        graphics.fill(x, y, x + guiWidth * 2 + 20, y + guiHeight, 0xFF1E1E1E); // fondo extendido
        super.render(graphics, mouseX, mouseY, partialTicks);
        // Título centrado sobre todo
        graphics.drawCenteredString(this.font, this.title, this.width / 2, y - 10, 0xFFFFFF);

        // Cargar resultados
        try {
            String json = Files.readString(Path.of("modcelphone/results.json"));
            Gson gson = new Gson();
            resultados = gson.fromJson(json, new TypeToken<List<Cancion>>(){}.getType());

            if (resultados.isEmpty()) {
                cargando = true;
            } else {
                cargando = false;

                int baseX = x + guiWidth + 10; // justo a la derecha del área de botones
                int baseY = y + 20;

                int start = Math.min(scrollOffset, Math.max(0, resultados.size() - maxVisibleResultados));
                int end = Math.min(resultados.size(), start + maxVisibleResultados);

                for (int i = start; i < end; i++) {
                    Cancion c = resultados.get(i);
                    String textoCompleto = c.titulo + " - " + c.artista;
                    String texto = textoCompleto.length() > 50 ? textoCompleto.substring(0, 47) + "..." : textoCompleto;

                    graphics.drawString(this.font, texto, baseX + 6, baseY + (i - start) * 14, 0xAAAAAA);
                }
            }

        } catch (Exception e) {
            resultados = new ArrayList<>();
            cargando = true;
        }

        if (cargando) {
            graphics.drawString(this.font, "🔄 Cargando resultados...", x + 10, y + guiHeight - 30, 0xCCCC00);
        }

        if (!mensajeError.isEmpty()) {
            graphics.drawString(this.font, mensajeError, x + 10, y + guiHeight - 15, 0xFF0000);
        }
    }


    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0 && scrollOffset > 0) {
            scrollOffset--;
        } else if (delta < 0 && scrollOffset < Math.max(0, resultados.size() - maxVisibleResultados)) {
            scrollOffset++;
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Coordenadas del fondo extendido
        int x = (this.width - (guiWidth * 2 + 20)) / 2;
        int y = (this.height - guiHeight) / 2;

        // Coordenadas de los resultados
        int baseX = x + guiWidth + 10;
        int baseY = y + 20;

        int start = Math.min(scrollOffset, Math.max(0, resultados.size() - maxVisibleResultados));
        int end = Math.min(resultados.size(), start + maxVisibleResultados);

        for (int i = start; i < end; i++) {
            int yLinea = baseY + (i - start) * 14;

            if (mouseX >= baseX && mouseX <= baseX + guiWidth &&
                    mouseY >= yLinea && mouseY <= yLinea + 12) {

                Cancion seleccionada = resultados.get(i);
                System.out.println("🎶 Seleccionada: " + seleccionada.titulo);

                // Guardar en JSON
                try {
                    Gson gson = new Gson();
                    String json = gson.toJson(seleccionada);
                    FileWriter fw = new FileWriter("modcelphone/play.json");
                    fw.write(json);
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Abrir pantalla de reproducción
                this.minecraft.setScreen(new ReproductorScreen(
                        resultados,
                        i,
                        () -> this.minecraft.setScreen(this)
                ));

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean isPauseScreen() {
        return false; // No pausa el juego
    }
}
