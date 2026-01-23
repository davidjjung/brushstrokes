package com.davigj.brushstrokes.core;

import com.davigj.brushstrokes.client.SelectionHandler;
import com.davigj.brushstrokes.core.registry.BSCreativePlacements;
import com.davigj.brushstrokes.core.registry.BSItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BrushStrokes.MOD_ID)
public class BrushStrokes {
    public static final String MOD_ID = "brushstrokes";
    public static SelectionHandler SELECTION_HANDLER;

    public BrushStrokes() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        MinecraftForge.EVENT_BUS.register(this);

        BSItems.ITEMS.register(bus);
        bus.addListener(BSCreativePlacements::set);

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::dataSetup);
        context.registerConfig(ModConfig.Type.COMMON, BSConfig.COMMON_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // If this is like, Not how it's done, then sue me. Or fix it. Please fix it.
            SELECTION_HANDLER = new SelectionHandler();
        });
    }

    private void dataSetup(GatherDataEvent event) {

    }
}