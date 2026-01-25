package com.davigj.brushstrokes.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WaxBrushItem extends Item {
    public static final int MAX_WAX_CONVERTS = 1096;
    private static final String START_POS = "Pos";

    public WaxBrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();

        if (player == null) return InteractionResult.FAIL;

        BlockPos clickedPos = ctx.getClickedPos();
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(START_POS)) {
            tag.putLong(START_POS, clickedPos.asLong());

            if (level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.brushstrokes.selection_start"), true);
            }
            return InteractionResult.SUCCESS;
        }

        BlockPos start = BlockPos.of(tag.getLong(START_POS));

        tag.remove(START_POS);

        if (player.isCrouching()) {
            if (level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.brushstrokes.selection_cleared"), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide) {
            applyWax(level, player, stack, start, clickedPos, ctx.getHand());
        }

        return InteractionResult.SUCCESS;
    }

    private void applyWax(Level level, Player player, ItemStack stack, BlockPos start, BlockPos end, InteractionHand hand) {
        AABB box = new AABB(start, end);
        int volume = (int) ((box.maxX - box.minX + 1) * (box.maxY - box.minY + 1) * (box.maxZ - box.minZ + 1));

        if (volume > MAX_WAX_CONVERTS) {
            sendFeedback(player, WaxResult.TOO_BIG);
            return;
        }

        int transformed = 0;
        boolean broken = false;
        boolean creative = player.getAbilities().instabuild;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        outer:
        for (int x = (int) box.minX; x <= box.maxX; x++) {
            for (int y = (int) box.minY; y <= box.maxY; y++) {
                for (int z = (int) box.minZ; z <= box.maxZ; z++) {

                    cursor.set(x, y, z);
                    BlockState state = level.getBlockState(cursor);
                    Optional<BlockState> waxed = HoneycombItem.getWaxed(state);

                    if (waxed.isEmpty()) continue;
                    level.setBlock(cursor, waxed.get(), 11);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, cursor, GameEvent.Context.of(player, waxed.get()));
                    level.levelEvent(3003, cursor, 0);
                    transformed++;

                    if (!creative && transformed >= stack.getMaxDamage() - stack.getDamageValue()) {
                        broken = true;
                        break outer;
                    }
                }
            }
        }

        if (transformed > 0 && !creative) {
            stack.hurtAndBreak(transformed, player, p -> {
                p.broadcastBreakEvent(hand);
                p.setItemInHand(hand, new ItemStack(Items.BRUSH));
            });
        }

        WaxResult result;
        if (broken) {
            result = WaxResult.PARTIAL_BROKEN;
        } else if (transformed > 0) {
            result = WaxResult.SUCCESS;
        } else {
            result = WaxResult.NONE;
        }

        sendFeedback(player, result);
    }

    private void sendFeedback(Player player, WaxResult result) {
        player.displayClientMessage(Component.translatable(result.key), true);
    }

    public boolean canAttackBlock(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer) {
        return false;
    }

    public boolean canBeDepleted() {
        return true;
    }

    private enum WaxResult {
        TOO_BIG("message.brushstrokes.too_big"),
        NONE("message.brushstrokes.none"),
        PARTIAL_BROKEN("message.brushstrokes.partial_broken"),
        SUCCESS("message.brushstrokes.success");

        final String key;

        WaxResult(String key) {
            this.key = key;
        }
    }
}