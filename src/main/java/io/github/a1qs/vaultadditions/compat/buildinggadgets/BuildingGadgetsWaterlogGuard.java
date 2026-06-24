package io.github.a1qs.vaultadditions.compat.buildinggadgets;

import com.direwolf20.buildinggadgets.common.tainted.building.BlockData;
import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.ITileEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class BuildingGadgetsWaterlogGuard {

    private BuildingGadgetsWaterlogGuard() {
    }

    public static BlockState sanitizeState(BlockState state) {
        if (state == null) return null;
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            return state.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        return state;
    }

    public static boolean isWaterState(BlockState state) {
        if (state == null) return false;
        return state.is(Blocks.WATER) || state.getFluidState().is(FluidTags.WATER);
    }

    public static boolean isWaterData(BlockData data) {
        if (data == null) return false;
        return isWaterState(sanitizeState(data.getState()));
    }

    public static BlockData sanitizeData(BlockData data) {
        if (data == null) return null;

        BlockState original = data.getState();
        BlockState sanitized = sanitizeState(original);
        if (isWaterState(sanitized)) {
            return BlockData.AIR;
        }
        if (sanitized == original) {
            return data;
        }

        ITileEntityData tileData = data.getTileData();
        return new BlockData(sanitized, tileData);
    }
}
