package net.hormiga.celphone.registry;

import net.hormiga.celphone.CelPhoneMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = CelPhoneMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CelPhoneMain.MOD_ID);

    public static final RegistryObject<SoundEvent> CUSTOM =
            SOUND_EVENTS.register("custom",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CelPhoneMain.MOD_ID, "custom")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
