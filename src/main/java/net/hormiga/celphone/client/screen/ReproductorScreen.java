package net.hormiga.celphone.client.screen;

import com.google.gson.Gson;
import net.hormiga.celphone.audio.VolumeSlider;
import net.hormiga.celphone.data.Cancion;
import net.hormiga.celphone.media.CelphoneMediaPlayer;
import net.hormiga.celphone.util.YTDLPHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class ReproductorScreen extends Screen {
    private String tituloCancion;
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
        Cancion base = lista.get(indiceActual);

        // Si no tiene URL, generarla al inicio
        if (base.getUrl() == null || base.getUrl().isEmpty()) {
            String nuevaUrl = YTDLPHelper.obtenerURLDirecta(base.getId());
            if (nuevaUrl != null && !nuevaUrl.isEmpty()) {
                base.setUrl(nuevaUrl);
                lista.set(indiceActual, base); // actualiza el array
            }
        }

        this.cancionSeleccionada = base;
        // Reproducir
        // Solo reproducir si no está sonando ya
        if (!CelphoneMediaPlayer.isPlayingUrl(cancionSeleccionada.getUrl()) && !CelphoneMediaPlayer.isPaused()) {
            //CelphoneMediaPlayer.stop();
            CelphoneMediaPlayer.play(cancionSeleccionada.getUrl());
        }


        //verificaion de url vacia en este caso array

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        centerY += 50;

        this.addRenderableWidget(Button.builder(Component.literal("▶ Reproducir"), (b) -> {
            CelphoneMediaPlayer.stop();
            CelphoneMediaPlayer.play(cancionSeleccionada.getUrl());
        }).pos(centerX - 80, centerY - 60).size(160, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("⏸ Pausar"), (b) -> {
            CelphoneMediaPlayer.pause();
        }).pos(centerX - 80, centerY - 35).size(160, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("▶ Reanudar"), (b) -> {
            CelphoneMediaPlayer.resume();
        }).pos(centerX - 80, centerY - 10).size(160, 20).build());

        this.addRenderableWidget(new VolumeSlider(centerX - 80, centerY + 15, 160, 20, 1.0));

        this.addRenderableWidget(Button.builder(Component.literal("⏭ Siguiente"), (b) -> {
            if (indiceActual < lista.size() - 1) {
                int nuevoIndice = indiceActual + 1;
                Cancion siguiente = lista.get(nuevoIndice);

                // Si la canción siguiente aún no tiene URL, se la generamos antes de reproducir
                if (siguiente.getUrl() == null || siguiente.getUrl().isEmpty()|| siguiente.getUrl().contains("youtube.com")) {
                    String url = YTDLPHelper.obtenerURLDirecta(siguiente.getId());
                    if (url != null && !url.isEmpty()) {
                        siguiente.setUrl(url);
                    }
                }

                // ⚡ En segundo plano: transformar +2 y +3 si existen
                new Thread(() -> {
                    List<Cancion> extra = new ArrayList<>();
                    for (int i = nuevoIndice + 1; i <= nuevoIndice + 2; i++) {
                        if (i < lista.size()) {
                            Cancion c = lista.get(i);
                            if (c.getUrl() == null || c.getUrl().isEmpty()|| c.getUrl().contains("youtube.com")) {
                                extra.add(c);
                            }
                        }
                    }
                    YTDLPHelper.transformarURLsEnParalelo(extra);
                }).start();

                // Avanzar a la nueva pantalla
                //this.minecraft.setScreen(new ReproductorScreen(lista, nuevoIndice, onSalir));
                this.indiceActual = nuevoIndice;
                this.cancionSeleccionada = siguiente;
                this.tituloCancion = siguiente.getTitulo() + " - " + siguiente.getArtista();

                CelphoneMediaPlayer.stop();
                CelphoneMediaPlayer.play(siguiente.getUrl());
            }
        }).pos(centerX + 5, centerY + 45).size(75, 20).build());



        this.addRenderableWidget(Button.builder(Component.literal("⏮ Anterior"), (b) -> {
            if (indiceActual > 0) {
                int nuevoIndice = indiceActual - 1;
                Cancion anterior = lista.get(nuevoIndice);

                // Si la canción anterior aún no tiene URL, se la generamos antes de reproducir
                if (anterior.getUrl() == null || anterior.getUrl().isEmpty()|| anterior.getUrl().contains("youtube.com")) {
                    String url = YTDLPHelper.obtenerURLDirecta(anterior.getId());
                    if (url != null && !url.isEmpty()) {
                        anterior.setUrl(url);
                    }
                }

                // ⚡ En segundo plano: transformar -2 y -3 si existen
                new Thread(() -> {
                    List<Cancion> extra = new ArrayList<>();
                    for (int i = nuevoIndice - 2; i < nuevoIndice; i++) {
                        if (i >= 0) {
                            Cancion c = lista.get(i);
                            if (c.getUrl() == null || c.getUrl().isEmpty()|| c.getUrl().contains("youtube.com")) {
                                extra.add(c);
                            }
                        }
                    }
                    YTDLPHelper.transformarURLsEnParalelo(extra);
                }).start();

                // Volver a la pantalla con la anterior canción
                //this.minecraft.setScreen(new ReproductorScreen(lista, nuevoIndice, onSalir));
                // this.minecraft.setScreen(new ReproductorScreen(lista, nuevoIndice, onSalir));
                this.indiceActual = nuevoIndice;
                this.cancionSeleccionada = anterior;
                this.tituloCancion = anterior.getTitulo() + " - " + anterior.getArtista();

                CelphoneMediaPlayer.stop();
                CelphoneMediaPlayer.play(anterior.getUrl());

            }
        }).pos(centerX - 80, centerY + 45).size(75, 20).build());



        this.addRenderableWidget(Button.builder(Component.literal("⬅ Volver"), (b) -> {
            this.onClose();
            if (onSalir != null) onSalir.run();
        }).pos(centerX - 80, centerY + 75).size(160, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int y = this.height / 2 - 60;

        graphics.drawCenteredString(this.font, "🎵 Reproduciendo:", centerX, y, 0xFFFFFF);
        graphics.drawCenteredString(this.font, tituloCancion, centerX, y + 12, 0xAAAAAA);
        long actual = CelphoneMediaPlayer.getTime();
        long total = CelphoneMediaPlayer.getLength();

        if (total > 0) {
            int barX = this.width / 2 - 80;
            int barY = this.height / 2 - 27;
            int barWidth = 160;
            int barHeight = 6;

            float progress = (float) actual / total;
            int filledWidth = (int) (progress * barWidth);

            graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF444444);
            graphics.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF22AAFF);

            graphics.drawString(this.font, formatTime(actual), barX, barY - 10, 0xAAAAAA);
            graphics.drawString(this.font, formatTime(total), barX + barWidth - 30, barY - 10, 0xAAAAAA);
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int barX = this.width / 2 - 80;
        int barY = this.height / 2 - 27;
        int barWidth = 160;
        int barHeight = 6;

        if (mouseX >= barX && mouseX <= barX + barWidth &&
                mouseY >= barY && mouseY <= barY + barHeight) {

            long total = CelphoneMediaPlayer.getLength();
            float porcentaje = (float) (mouseX - barX) / barWidth;
            long nuevoTiempo = (long) (porcentaje * total);

            CelphoneMediaPlayer.seek(nuevoTiempo);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        long actual = CelphoneMediaPlayer.getTime();
        long total = CelphoneMediaPlayer.getLength();

        if (total > 0 && actual >= total - 1000) { // margen de 1 segundo para evitar bugs por precisión
            if (indiceActual < lista.size() - 1) {
                int nuevoIndice = indiceActual + 1;
                cambiarCancion(nuevoIndice);
            }
        }
    }

    private void reproducirCancionActual() {
        Cancion actual = lista.get(indiceActual);

        if (actual.getUrl() == null || actual.getUrl().isEmpty() || actual.getUrl().contains("youtube.com")) {
            String nuevaUrl = YTDLPHelper.obtenerURLDirecta(actual.getId());
            if (nuevaUrl != null && !nuevaUrl.isEmpty()) {
                actual.setUrl(nuevaUrl);
                lista.set(indiceActual, actual);
            }
        }

        this.cancionSeleccionada = actual;
        this.tituloCancion = actual.getTitulo() + " - " + actual.getArtista();

        CelphoneMediaPlayer.stop();
        CelphoneMediaPlayer.play(actual.getUrl());
    }


    public void cambiarCancion(int nuevoIndice) {
        if (nuevoIndice != this.indiceActual) {
            this.indiceActual = nuevoIndice;
            // ⚠️ Agregá aquí lo necesario para reiniciar la reproducción correctamente
            reproducirCancionActual(); // o el método que uses internamente
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}

