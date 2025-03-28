package net.hormiga.celphone.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MenuCelularScreen extends Screen {
    private final int guiWidth = 200;
    private final int guiHeight = 120;

    public MenuCelularScreen() {
        super(Component.literal("Celular"));
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;

        // Botón YouTube Music
        this.addRenderableWidget(Button.builder(Component.literal("YouTube Music"), (b) -> {
            this.minecraft.setScreen(new CelularScreen());
        }).pos(x + 20, y + 40).size(160, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;
        graphics.fill(x, y, x + guiWidth, y + guiHeight, 0xFF1E1E1E);
        graphics.drawCenteredString(this.font, "Menú del Celular", this.width / 2, y + 15, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
