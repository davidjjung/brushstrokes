package com.davigj.brushstrokes.core.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class BSCreativePlacements {
    public static void set(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(BSItems.WAX_BRUSH);
        }
    }
}
