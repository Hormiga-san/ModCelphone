package net.hormiga.celphone.client.screen;

import com.google.gson.Gson;
import net.hormiga.celphone.audio.URLSoundPlayer;
import net.hormiga.celphone.data.Cancion;
import net.hormiga.celphone.util.YTDLPHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class ReproductorScreen extends Screen {
    private final String tituloCancion;
    private final Runnable onSalir;
    private List<Cancion> lista;
    private int indiceActual;
    private Cancion cancionSeleccionada;

    public ReproductorScreen(List<Cancion> lista, int indiceActual, Runnable onSalir) {
        super(Component.literal("Reproduciendo..."));
        this.lista = lista;
        this.indiceActual = indiceActual;
        this.onSalir = onSalir;
        this.tituloCancion = lista.get(indiceActual).titulo + " - " + lista.get(indiceActual).artista;
    }

    @Override
    protected void init() {
        this.cancionSeleccionada = lista.get(indiceActual);
        guardarPlayJson(cancionSeleccionada); // también guarda el JSON al abrir

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Leer play.json
        try {
            String json = java.nio.file.Files.readString(java.nio.file.Path.of("modcelphone/play.json"));
            Gson gson = new Gson();
            Cancion c = gson.fromJson(json, Cancion.class);

            System.out.println("🎧 Reproduciendo:");
            System.out.println("ID: " + c.id);
            System.out.println("Título: " + c.titulo);
            System.out.println("Artista: " + c.artista);
            System.out.println("URL: " + c.url);




        } catch (Exception e) {
            System.out.println("⚠️ No se pudo leer play.json: " + e.getMessage());
        }

        // Texto de la canción actual
       /* this.addRenderableWidget(Button.builder(Component.literal("▶ Reproducir"), (b) -> {
            Cancion actual = lista.get(indiceActual);
            ClientData.streamPlayer.play(actual.url);
            System.out.println("▶ Reproduciendo: " + actual.url);
        }).pos(centerX - 80, centerY - 50).size(160, 20).build());*/
        this.addRenderableWidget(Button.builder(Component.literal("▶ Reproducir"), (b) -> {
            System.out.println("🎧 Reproduciendo:");
            System.out.println("ID: " + cancionSeleccionada.id);
            System.out.println("Título: " + cancionSeleccionada.titulo);
            System.out.println("Artista: " + cancionSeleccionada.artista);
            System.out.println("URL: " + cancionSeleccionada.url);

            URLSoundPlayer.playFromUrl(cancionSeleccionada.url);
        }).pos(centerX - 80, centerY - 50).size(160, 20).build());


        this.addRenderableWidget(Button.builder(Component.literal("⏸ Pausar"), (b) -> {
            System.out.println("🔇 Pausar");
            // Lógica futura de pausa

        }).pos(centerX - 80, centerY - 20).size(160, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("🔉 Volumen -"), (b) -> {
            System.out.println("🔉 Volumen abajo");
        }).pos(centerX - 80, centerY + 10).size(75, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("🔊 Volumen +"), (b) -> {
            System.out.println("🔊 Volumen arriba");
        }).pos(centerX + 5, centerY + 10).size(75, 20).build());

        // ⏮ Botón anterior
        this.addRenderableWidget(Button.builder(Component.literal("⏮ Anterior"), (b) -> {
            if (indiceActual > 0) {
                this.minecraft.setScreen(new ReproductorScreen(lista, indiceActual - 1, onSalir));

            }
        }).pos(centerX - 80, centerY + 40).size(75, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("⏭ Siguiente"), (b) -> {
            if (indiceActual < lista.size() - 1) {
                this.minecraft.setScreen(new ReproductorScreen(lista, indiceActual + 1, onSalir));

            }
        }).pos(centerX + 5, centerY + 40).size(75, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("⬅ Volver"), (b) -> {
            this.onClose();
            if (onSalir != null) onSalir.run();
        }).pos(centerX - 80, centerY + 70).size(160, 20).build());
    }
    private void guardarPlayJson(Cancion cancion) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(cancion);

            File archivo = new File("modcelphone/play.json");
            archivo.getParentFile().mkdirs(); // asegura que la carpeta existe
            FileWriter fw = new FileWriter(archivo);

            fw.write(json);
            fw.close();
            System.out.println("✅ play.json guardado en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int y = this.height / 2 - 60;

        graphics.drawCenteredString(this.font, "🎵 Reproduciendo:", centerX, y, 0xFFFFFF);
        graphics.drawCenteredString(this.font, tituloCancion, centerX, y + 12, 0xAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

