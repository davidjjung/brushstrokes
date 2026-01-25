package com.davigj.brushstrokes.client;

import com.davigj.brushstrokes.common.item.WaxBrushItem;
import com.davigj.brushstrokes.core.registry.BSItems;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class SelectionHandler {
    public SelectionHandler() {
    }

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = mc.level;

        if (player == null || level == null) return;

        ItemStack heldItem = player.getMainHandItem();
        boolean isWax = heldItem.is(BSItems.WAX_BRUSH.get());

        if (!isWax) {
            heldItem = player.getOffhandItem();
            isWax = heldItem.is(BSItems.WAX_BRUSH.get());

            if (!isWax) return;
        }

        CompoundTag tag = heldItem.getTag();
        if (tag != null && tag.contains("Pos")) {
            BlockPos startPos = BlockPos.of(tag.getLong("Pos"));

            HitResult hitResult = mc.hitResult;
            if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }
            BlockPos currentPos = ((BlockHitResult) hitResult).getBlockPos();

            AABB box = new AABB(startPos, currentPos).expandTowards(1, 1, 1);

            int xLen = (int) (box.maxX - box.minX);
            int yLen = (int) (box.maxY - box.minY);
            int zLen = (int) (box.maxZ - box.minZ);
            int volume = xLen * yLen * zLen;

            boolean tooBig = volume > WaxBrushItem.MAX_WAX_CONVERTS;

            int color;
            String msgKey;

            if (tooBig) {
                color = 0xFF5555;
                msgKey = "message.brushstrokes.too_big";
            } else {
                color = 0xFFD700;
                msgKey = "message.brushstrokes.selection_size";
            }

            Outliner.getInstance().showAABB("wax_brush_selection", box)
                    .colored(color)
                    .withFaceTextures(OverlayTextures.WAX, OverlayTextures.WAX)
                    .lineWidth(1 / 16f)
                    .disableLineNormals();

            MutableComponent msg;
            if (tooBig) {
                msg = Component.translatable(msgKey).withStyle(ChatFormatting.RED);
            } else {
                msg = Component.translatable(msgKey, volume).withStyle(style -> style.withColor(color));
            }
            player.displayClientMessage(msg, true);
        }
    }
}