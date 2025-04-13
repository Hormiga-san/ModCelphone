package net.hormiga.celphone.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
