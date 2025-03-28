package net.hormiga.celphone.item.custom;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

public class CelularItem extends Item {

    public CelularItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            net.minecraft.client.Minecraft.getInstance().setScreen(new net.hormiga.celphone.client.screen.MenuCelularScreen());
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
