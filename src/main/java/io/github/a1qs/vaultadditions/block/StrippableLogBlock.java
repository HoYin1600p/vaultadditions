package io.github.a1qs.vaultadditions.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class StrippableLogBlock extends RotatedPillarBlock {
    private final Supplier<? extends Block> strippedBlock;

    public StrippableLogBlock(Properties properties, Supplier<? extends Block> strippedBlock) {
        super(properties);
        this.strippedBlock = strippedBlock;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction != ToolActions.AXE_STRIP) {
            return super.getToolModifiedState(state, context, toolAction, simulate);
        }

        return strippedBlock.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
    }
}
