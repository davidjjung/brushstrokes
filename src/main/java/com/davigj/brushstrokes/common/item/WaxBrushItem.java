package com.davigj.brushstrokes.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public class WaxBrushItem extends Item {
    private static final String START_POS = "Pos";
    public static final int MAX_WAX_CONVERTS = 1096;

    public WaxBrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();

        if (player == null || level.isClientSide)
            return InteractionResult.SUCCESS;

        BlockPos clickedPos = ctx.getClickedPos();
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(START_POS)) {
            tag.putLong(START_POS, clickedPos.asLong());
            return InteractionResult.SUCCESS;
        }

        BlockPos start = BlockPos.of(tag.getLong(START_POS));
        tag.remove(START_POS);

        if (player.isCrouching()) {
            return InteractionResult.SUCCESS;
        }

        applyWax(level, player, stack, start, clickedPos);
        return InteractionResult.SUCCESS;
    }

    private void applyWax(Level level, Player player, ItemStack stack, BlockPos start, BlockPos end) {
        // TODO: maybe make an enum for all the results: boxTooBig, noneTransformed, someTransformedButThenBrushBorked, totalSuccess
        AABB box = new AABB(start, end);
        int volume = (int)((box.maxX - box.minX + 1) * (box.maxY - box.minY + 1) * (box.maxZ - box.minZ + 1));

        if (volume > MAX_WAX_CONVERTS) {
            // TODO: send a message that the box was too big, we couldn't do it, sorry
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
            stack.hurtAndBreak(transformed, player,
                    p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }

        // TODO: send to player a msg using the following. transformed == 0 ? boxTooBig : (broken ? someTransformedButThenBrushBorked : totalSuccess)
    }

    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return false;
    }

    public boolean canBeDepleted() {
        return true;
    }

    public static void onBroken(Player player) {
        // TODO: like, return a brush or smth? idk i just copied this from SuperGlueItem
        // TODO: that reminds me that using up this/glue brush should just return like. a brush. not just destroying items
    }
}
