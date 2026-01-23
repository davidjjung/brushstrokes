package com.davigj.brushstrokes.core.other;

import com.davigj.brushstrokes.core.BrushStrokes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class BSClientEvents {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        // TODO: client tick selection box using SuperGlueSelectionHandler as a base but like. How. Can't access Outliner class bc it doesn't even seem to be a part of the mod ??
        if (isGameActive()) {
            if (event.phase == TickEvent.Phase.END) {
                BrushStrokes.SELECTION_HANDLER.tick();
            }
        }
    }

    protected static boolean isGameActive() {
        return Minecraft.getInstance().level != null && Minecraft.getInstance().player != null;
    }
}