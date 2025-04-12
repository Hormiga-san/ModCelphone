package net.hormiga.celphone.client.screen;

import com.google.gson.Gson;
import net.hormiga.celphone.data.Cancion;
import net.hormiga.celphone.util.YTDLPHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
    private final int maxVisibleResultados = 15;
    private List<Cancion> cancionesEnMemoria = new ArrayList<>();
    //private final int maxVisibleResultados = 12;
    public CelularScreen() {
        super(Component.literal(""));
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int x = (this.width - (guiWidth * 2 + 20)) / 2;
        int y = (this.height - guiHeight) / 2;

        int paddingX = x + 10;
        int currentY = y + 20;

        buscador = new EditBox(this.font, paddingX, currentY, 160, 20, Component.literal("Buscar"));
        buscador.setMaxLength(100);
        this.addRenderableWidget(buscador);
        currentY += 30;

        this.addRenderableWidget(Button.builder(Component.literal("🔍 Buscar canción"), (button) -> {
            String texto = buscador.getValue().trim();
            if (!texto.isEmpty()) {
                new Thread(() -> {
                    try {
                        java.net.URL url = new java.net.URL("http://127.0.0.1:8080/buscar");
                        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json; utf-8");
                        connection.setDoOutput(true);

                        String jsonInput = "{\"query\":\"" + texto + "\"}";
                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
                        }

                        int status = connection.getResponseCode();
                        if (status == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line.trim());
                            }

                            Gson gson = new Gson();
                            List<Cancion> lista = gson.fromJson(response.toString(), new TypeToken<List<Cancion>>() {}.getType());

                            if (lista != null && !lista.isEmpty()) {
                                cancionesEnMemoria = lista; // Solo se cargan los títulos, sin URLs válidas aún
                                mensajeError = "";
                                System.out.println("✅ Búsqueda cargada en memoria.");
                            } else {
                                mensajeError = "❌ No se encontraron resultados.";
                            }

                        } else {
                            mensajeError = "❌ Error del servidor: " + status;
                        }

                        connection.disconnect();
                    } catch (Exception e) {
                        mensajeError = "❌ Error al buscar: " + e.getMessage();
                        e.printStackTrace();
                    }
                }).start();
            } else {
                mensajeError = "❌ Ingresá un término.";
            }
        }).pos(paddingX, currentY).size(160, 20).build());


        currentY += 50;

        urlPlaylistBox = new EditBox(this.font, paddingX, currentY, 160, 20, Component.literal("URL Playlist"));
        urlPlaylistBox.setMaxLength(500);
        this.addRenderableWidget(urlPlaylistBox);
        currentY += 30;

        // Botón Cargar lista
        this.addRenderableWidget(Button.builder(Component.literal("📥 Cargar lista"), (button) -> {
            String url = urlPlaylistBox.getValue().trim();
            if (!url.isEmpty()) {
                new Thread(() -> {
                    try {
                        java.net.URL endpoint = new java.net.URL("http://127.0.0.1:8080/playlist");
                        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) endpoint.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json; utf-8");
                        connection.setDoOutput(true);

                        // Enviar URL al backend
                        String jsonInput = "{\"url\":\"" + url + "\"}";
                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
                        }

                        int status = connection.getResponseCode();
                        if (status == 200) {
                            // Leer respuesta del backend
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line.trim());
                            }

                            Gson gson = new Gson();
                            List<Cancion> lista = gson.fromJson(response.toString(), new TypeToken<List<Cancion>>() {}.getType());

                            if (lista != null && !lista.isEmpty()) {
                                cancionesEnMemoria = lista; // ✅ Solo guardamos, sin transformar
                                mensajeError = "";
                                System.out.println("✅ Playlist cargada en memoria. URLs aún no transformadas.");
                            } else {
                                mensajeError = "❌ La playlist está vacía.";
                            }
                        } else {
                            mensajeError = "❌ Error del servidor: " + status;
                        }

                        connection.disconnect();
                    } catch (Exception e) {
                        mensajeError = "❌ Error al cargar playlist: " + e.getMessage();
                        e.printStackTrace();
                    }
                }).start();
            } else {
                mensajeError = "❌ Ingresá una URL válida.";
            }
        }).pos(paddingX, currentY).size(160, 20).build());



        currentY += 30;

        // Botón "Reproduciendo..."
        this.addRenderableWidget(Button.builder(Component.literal("🎧 Reproduciendo..."), (b) -> {
            if (cancionesEnMemoria != null && !cancionesEnMemoria.isEmpty()) {
                this.minecraft.setScreen(new ReproductorScreen(
                        cancionesEnMemoria, 0, () -> this.minecraft.setScreen(this)
                ));
            } else {
                mensajeError = "⚠️ No hay canciones cargadas.";
            }
        }).pos(paddingX, currentY).size(160, 20).build());
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        int x = (this.width - (guiWidth * 2 + 20)) / 2;
        int y = (this.height - guiHeight) / 2;
        graphics.fill(x, y, x + guiWidth * 2 + 20, y + guiHeight, 0xFF1E1E1E);
        super.render(graphics, mouseX, mouseY, partialTicks);

        // ✅ Usar cancionesEnMemoria directamente
        List<Cancion> visibles = cancionesEnMemoria != null ? cancionesEnMemoria : new ArrayList<>();

        int baseX = x + guiWidth + 10;
        int baseY = y + 20;

        int start = Math.min(scrollOffset, Math.max(0, visibles.size() - maxVisibleResultados));
        int end = Math.min(visibles.size(), start + maxVisibleResultados);

        for (int i = start; i < end; i++) {
            Cancion c = visibles.get(i);
            String texto = c.getTitulo() + " - " + c.getArtista();
            if (texto.length() > 50) texto = texto.substring(0, 47) + "...";
            graphics.drawString(this.font, texto, baseX + 6, baseY + (i - start) * 14, 0xAAAAAA);
        }

        if (!mensajeError.isEmpty()) {
            graphics.drawString(this.font, mensajeError, x + 10, y + guiHeight - 15, 0xFF0000);
        }
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0 && scrollOffset > 0) scrollOffset--;
        else if (delta < 0 && scrollOffset < Math.max(0, resultados.size() - maxVisibleResultados)) scrollOffset++;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        int x = (this.width - (guiWidth * 2 + 20)) / 2;
        int y = (this.height - guiHeight) / 2;

        int baseX = x + guiWidth + 10;
        int baseY = y + 20;

        if (cancionesEnMemoria == null || cancionesEnMemoria.isEmpty()) return super.mouseClicked(mouseX, mouseY, button);

        int start = Math.min(scrollOffset, Math.max(0, cancionesEnMemoria.size() - maxVisibleResultados));
        int end = Math.min(cancionesEnMemoria.size(), start + maxVisibleResultados);

        for (int i = start; i < end; i++) {
            int yLinea = baseY + (i - start) * 14;

            if (mouseX >= baseX && mouseX <= baseX + guiWidth &&
                    mouseY >= yLinea && mouseY <= yLinea + 12) {

                System.out.println("🎶 Seleccionada: " + cancionesEnMemoria.get(i).getTitulo());

                // 🔁 Transformar la seleccionada y 2 vecinas (-2, -1, actual, +1, +2)
                List<Cancion> sublista = new ArrayList<>();
                for (int j = i - 2; j <= i + 2; j++) {
                    System.out.println("for"+ j);
                    if (j >= 0 && j < cancionesEnMemoria.size()) {
                        Cancion c = cancionesEnMemoria.get(j);
                        System.out.println("aaaaaaaaaaaaAAAAAAIF");
                        if (c.getUrl() == null || c.getUrl().isEmpty() || c.getUrl().contains("youtube.com")) {
                            System.out.println("🔍 URL vacía para: " + c.getTitulo());
                            sublista.add(c);

                        }
                    }
                }

                // ⚡ transforma directamente los objetos referenciados
                YTDLPHelper.transformarURLsEnParalelo(sublista);

                // ✅ Abrimos reproductor con la lista original
                this.minecraft.setScreen(new ReproductorScreen(
                        cancionesEnMemoria,
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
        return false;
    }
}
