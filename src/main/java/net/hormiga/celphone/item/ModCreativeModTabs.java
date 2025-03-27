package net.hormiga.celphone.item;

import net.hormiga.celphone.CelPhoneMain;
import net.hormiga.celphone.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CRETIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CelPhoneMain.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CRETIVE_MODE_TABS.register("tutorial_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(CellPhoneItem.CELULAR.get()))
                    .title(Component.translatable("creativetab.tutorial_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(CellPhoneItem.CELULAR.get());
                        pOutput.accept(CellPhoneItem.PLACA_ELECTRONICA.get());
                        pOutput.accept(Items.DIAMOND);
                        pOutput.accept(ModBlocks.PLASTIC_BLOCK.get());
                        pOutput.accept(ModBlocks.RAW_PLASTIC_BLOCK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CRETIVE_MODE_TABS.register(eventBus);
    }
}
