package net.hormiga.celphone.item;

import net.hormiga.celphone.CelPhoneMain;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CellPhoneItem {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CelPhoneMain.MOD_ID);
    public static final RegistryObject<Item> CELULAR = ITEMS.register("celular",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLACA_ELECTRONICA = ITEMS.register("placa_electronica",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PEPITA_PLASTICO = ITEMS.register("pepita_plastico",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LINGOTE_PLASTICO = ITEMS.register("lingote_plastico",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLASTICO_BRUTO = ITEMS.register("plastico_bruto",
            () -> new Item(new Item.Properties()));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
