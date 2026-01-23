package com.davigj.brushstrokes.core.registry;

import com.davigj.brushstrokes.common.item.WaxBrushItem;
import com.davigj.brushstrokes.core.BrushStrokes;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;


public class BSItems {
    public static final DeferredRegister<Item> ITEMS;
    public static final RegistryObject<Item> WAX_BRUSH;

    public BSItems() {
    }

    public static RegistryObject<Item> register(String name, Supplier<Item> supplier) {
        RegistryObject<Item> item = ITEMS.register(name, supplier);
        return item;
    }

    static {
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BrushStrokes.MOD_ID);
        WAX_BRUSH = register("wax_brush", () -> {
            return new WaxBrushItem(new Item.Properties().stacksTo(1).durability(200));
        });
    }

}
