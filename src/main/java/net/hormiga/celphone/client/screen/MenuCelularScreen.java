package net.hormiga.celphone.client.screen;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.hormiga.celphone.data.Cancion;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class MenuCelularScreen extends Screen {

    public MenuCelularScreen() {
        super(Component.literal("Menú del Celular"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.literal("📱 YouTube Music"), (b) -> {
            this.minecraft.setScreen(new CelularScreen());
        }).pos(centerX - 80, centerY - 20).size(160, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("🎧 Reproduciendo..."), (b) -> {
            try {
                File jsonFile = new File("modcelphone/results.json");
                if (jsonFile.exists() && jsonFile.length() > 0) {
                    Gson gson = new Gson();
                    List<Cancion> lista = gson.fromJson(new FileReader(jsonFile), new TypeToken<List<Cancion>>() {}.getType());

                    if (lista != null && !lista.isEmpty()) {
                        this.minecraft.setScreen(new ReproductorScreen(lista, 0, () -> this.minecraft.setScreen(this)));
                    } else {
                        System.out.println("⚠️ Lista vacía o mal formada.");
                    }
                } else {
                    System.out.println("⚠️ No hay resultados aún.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("❌ Error al abrir reproductor: " + e.getMessage());
            }
        }).pos(centerX - 80, centerY + 10).size(160, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("⬅ Salir"), (b) -> {
            this.onClose();
        }).pos(centerX - 80, centerY + 40).size(160, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, "📱 Celular", this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
