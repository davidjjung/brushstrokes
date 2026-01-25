package com.davigj.brushstrokes.core;

import com.davigj.brushstrokes.client.SelectionHandler;
import com.davigj.brushstrokes.core.registry.BSCreativePlacements;
import com.davigj.brushstrokes.core.registry.BSItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
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

        bus.addListener(this::addResourcePack);
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
            SELECTION_HANDLER = new SelectionHandler();
        });
    }

    private void dataSetup(GatherDataEvent event) {

    }

    private void addResourcePack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            var resourcePath = ModList.get().getModFileById(MOD_ID).getFile().findResource("overrides");

            var packId = "brushstrokes_overrides";
            var packTitle = Component.literal("Brush Strokes Overrides");

            var packInfo = Pack.readMetaAndCreate(
                    packId,
                    packTitle,
                    true,
                    (id) -> new PathPackResources(id, resourcePath, true),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN
            );

            if (packInfo != null) {
                event.addRepositorySource((consumer) -> consumer.accept(packInfo));
            }
        }
    }
}