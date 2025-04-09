package net.hormiga.celphone.audio;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.hormiga.celphone.media.CelphoneMediaPlayer;

public class VolumeSlider extends AbstractSliderButton {

    public VolumeSlider(int x, int y, int width, int height, double value) {
        super(x, y, width, height, Component.literal("Volumen: " + (int) (value * 100) + "%"), value);
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal("Volumen: " + (int) (this.value * 100) + "%"));
    }

    @Override
    protected void applyValue() {
        int volumen = (int) (this.value * 100);
        CelphoneMediaPlayer.setVolume(volumen);
    }
}

