package com.davigj.brushstrokes.core.events;

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