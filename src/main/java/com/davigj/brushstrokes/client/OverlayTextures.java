package com.davigj.brushstrokes.client;

import com.davigj.brushstrokes.core.BrushStrokes;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.resources.ResourceLocation;

public enum OverlayTextures implements BindableTexture {
    DYE("dye.png"),
    WAX("wax.png");

    public static final String ASSET_PATH = "textures/special/";
    private final ResourceLocation location;

    OverlayTextures(String filename) {
        this.location = BrushStrokes.asResource(ASSET_PATH + filename);
    }

    @Override
    public ResourceLocation getLocation() {
        return this.location;
    }
}
