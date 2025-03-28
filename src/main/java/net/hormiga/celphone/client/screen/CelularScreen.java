package net.hormiga.celphone.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.EditBox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class CelularScreen extends Screen {
    private boolean navegadorAbierto = false;

    private boolean cargando = true;
    private final int guiWidth = 200;
    private final int guiHeight = 220;


    public CelularScreen() {
        super(Component.literal(""));
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        // Campo de texto para buscar
        EditBox buscador = new EditBox(this.font, x + 20, y + 20, 160, 20, Component.literal("Buscar"));
        buscador.setMaxLength(100);
        this.addRenderableWidget(buscador);

        // Botón de búsqueda
        this.addRenderableWidget(Button.builder(Component.literal("Buscar"), (button) -> {
            String texto = buscador.getValue().trim();

            if (!texto.isEmpty()) {
                try {
                    File resultsFile = new File("results.txt");
                    if (resultsFile.exists()) {
                        resultsFile.delete();
                    }

                    FileWriter writer = new FileWriter("search.txt");
                    writer.write(texto);
                    writer.close();
                    System.out.println("📁 Búsqueda escrita: " + texto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).pos(x + 20, y + 50).size(160, 20).build());

        // Botón para cerrar
        this.addRenderableWidget(Button.builder(Component.literal("Salir"), (b) -> this.onClose())
                .pos(x + 20, y + 80).size(160, 20).build());

        // Botón "Iniciar sesión"
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
        }).pos(x + 20, y + 110).size(160, 20).build());
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        cargando = true;

        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        // Título
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        // Leer y mostrar resultados
        try {
            List<String> lineas = Files.readAllLines(Path.of("results.txt"));
            int baseX = (this.width - guiWidth) / 2 + 10;
            int baseY = (this.height - guiHeight) / 2 + 140;

            if (lineas.isEmpty()) {
                cargando = true;
            } else {
                cargando = false;
                for (int i = 0; i < Math.min(lineas.size(), 5); i++) {
                    String linea = lineas.get(i);
                    graphics.drawString(this.font, "- " + linea, baseX, baseY + i * 12, 0xAAAAAA);
                }
            }
        } catch (IOException e) {
            cargando = true;
        }
        if (cargando) {
            int x = (this.width - guiWidth) / 2 + 20;
            int y = (this.height - guiHeight) / 2 + 140;
            graphics.drawString(this.font, "🔄 Cargando resultados...", x, y, 0xCCCC00);
        }


    }
    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;
        graphics.fill(x, y, x + guiWidth, y + guiHeight, 0xFF1E1E1E); // gris oscuro
    }


    @Override
    public boolean isPauseScreen() {
        return false; // No pausa el juego
    }
}
