package net.hormiga.celphone.client.screen;

import com.google.gson.Gson;
import net.hormiga.celphone.audio.VolumeSlider;
import net.hormiga.celphone.data.Cancion;
import net.hormiga.celphone.media.CelphoneMediaPlayer;
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
        this.tituloCancion = lista.get(indiceActual).getTitulo() + " - " + lista.get(indiceActual).getArtista();
    }

    @Override
    protected void init() {
        this.cancionSeleccionada = lista.get(indiceActual);
        guardarPlayJson(cancionSeleccionada); // también guarda el JSON al abrir

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.literal("▶ Reproducir"), (b) -> {
            if (cancionSeleccionada != null) {
                System.out.println("🎧 Reproduciendo desde botón:");
                System.out.println("ID: " + cancionSeleccionada.getId());
                System.out.println("Título: " + cancionSeleccionada.getTitulo());
                System.out.println("Artista: " + cancionSeleccionada.getArtista());
                System.out.println("URL: " + cancionSeleccionada.getUrl());

                CelphoneMediaPlayer.stop();
                CelphoneMediaPlayer.play(cancionSeleccionada.getUrl());
            }
        }).pos(centerX - 80, centerY - 60).size(160, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("⏸ Pausar"), (b) -> {
            CelphoneMediaPlayer.pause();
            System.out.println("⏸ Pausado");
        }).pos(centerX - 80, centerY - 35).size(160, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("▶ Reanudar"), (b) -> {
            CelphoneMediaPlayer.resume();
            System.out.println("▶ Reanudado");
        }).pos(centerX - 80, centerY - 10).size(160, 20).build());

        this.addRenderableWidget(new VolumeSlider(centerX - 80, centerY + 15, 160, 20, 1.0));

        this.addRenderableWidget(Button.builder(Component.literal("⏮ Anterior"), (b) -> {
            if (indiceActual > 0) {
                this.minecraft.setScreen(new ReproductorScreen(lista, indiceActual - 1, onSalir));
            }
        }).pos(centerX - 80, centerY + 45).size(75, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("⏭ Siguiente"), (b) -> {
            if (indiceActual < lista.size() - 1) {
                this.minecraft.setScreen(new ReproductorScreen(lista, indiceActual + 1, onSalir));
            }
        }).pos(centerX + 5, centerY + 45).size(75, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("⬅ Volver"), (b) -> {
            this.onClose();
            if (onSalir != null) onSalir.run();
        }).pos(centerX - 80, centerY + 75).size(160, 20).build());
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

