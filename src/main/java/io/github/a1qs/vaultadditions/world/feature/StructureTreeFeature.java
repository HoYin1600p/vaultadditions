package io.github.a1qs.vaultadditions.world.feature;

import io.github.a1qs.vaultadditions.VaultAdditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class StructureTreeFeature extends Feature<NoneFeatureConfiguration> {
    private final List<StructureVariant> structureVariants;
    private final ResourceLocation logId;
    private final ResourceLocation leavesId;
    private boolean invalidBlocksLogged;
    private boolean invalidStructureLogged;

    public StructureTreeFeature(List<StructureVariant> structureVariants,
                                ResourceLocation logId, ResourceLocation leavesId) {
        super(NoneFeatureConfiguration.CODEC);
        this.structureVariants = List.copyOf(structureVariants);
        this.logId = logId;
        this.leavesId = leavesId;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!(context.level() instanceof ServerLevel level)) {
            return false;
        }

        List<LoadedVariant> loadedVariants = new ArrayList<>();
        for (StructureVariant variant : structureVariants) {
            Optional<StructureTemplate> template = level.getStructureManager().get(variant.structureId);
            template.ifPresent(value -> loadedVariants.add(new LoadedVariant(variant, value)));
        }

        if (!loadedVariants.isEmpty()) {
            LoadedVariant selected = loadedVariants.get(context.random().nextInt(loadedVariants.size()));
            return placeStructure(level, context.origin(), context.random(), selected);
        }

        return placePlaceholder(level, context.origin(), context.random());
    }

    private boolean placeStructure(ServerLevel level, BlockPos saplingPos, Random random, LoadedVariant loaded) {
        StructureTemplate template = loaded.template;
        Vec3i size = template.getSize();
        if (size.getX() < 1 || size.getY() < 1 || size.getZ() < 1) {
            if (!invalidStructureLogged) {
                VaultAdditions.LOGGER.error("Tree structure {} is empty; leaving its sapling in place",
                        loaded.variant.structureId);
                invalidStructureLogged = true;
            }
            return false;
        }

        Rotation rotation = Rotation.getRandom(random);
        BlockPos pivot = loaded.variant.anchor;
        BlockPos placementOrigin = saplingPos.subtract(pivot);
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setRotationPivot(pivot)
                .setIgnoreEntities(true)
                .setKeepLiquids(false)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        BoundingBox bounds = template.getBoundingBox(settings, placementOrigin);
        BlockPos min = new BlockPos(bounds.minX(), bounds.minY(), bounds.minZ());
        BlockPos max = new BlockPos(bounds.maxX(), bounds.maxY(), bounds.maxZ());

        if (!level.hasChunksAt(min, max) || !hasRoom(level, min, max)) {
            return false;
        }

        return template.placeInWorld(level, placementOrigin, placementOrigin, settings, random, 3);
    }

    private boolean placePlaceholder(ServerLevel level, BlockPos origin, Random random) {
        Block log = ForgeRegistries.BLOCKS.getValue(logId);
        Block leaves = ForgeRegistries.BLOCKS.getValue(leavesId);
        if (log == null || leaves == null || log == Blocks.AIR || leaves == Blocks.AIR) {
            if (!invalidBlocksLogged) {
                VaultAdditions.LOGGER.error("Unable to grow placeholder tree: missing {} or {}", logId, leavesId);
                invalidBlocksLogged = true;
            }
            return false;
        }

        int trunkHeight = 5 + random.nextInt(2);
        BlockPos min = origin.offset(-2, 0, -2);
        BlockPos max = origin.offset(2, trunkHeight + 1, 2);
        if (!level.hasChunksAt(min, max) || !hasRoom(level, min, max)) {
            return false;
        }

        BlockState logState = log.defaultBlockState();
        BlockState leavesState = leaves.defaultBlockState();
        if (leavesState.hasProperty(BlockStateProperties.PERSISTENT)) {
            leavesState = leavesState.setValue(BlockStateProperties.PERSISTENT, false);
        }

        for (int y = 0; y < trunkHeight; y++) {
            level.setBlock(origin.above(y), logState, 19);
        }

        placeLeafLayer(level, origin.above(trunkHeight - 2), leavesState, 2, true);
        placeLeafLayer(level, origin.above(trunkHeight - 1), leavesState, 2, true);
        placeLeafLayer(level, origin.above(trunkHeight), leavesState, 1, false);
        placeLeafLayer(level, origin.above(trunkHeight + 1), leavesState, 1, true);
        return true;
    }

    private static void placeLeafLayer(ServerLevel level, BlockPos center, BlockState baseState,
                                       int radius, boolean trimCorners) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (trimCorners && Math.abs(x) == radius && Math.abs(z) == radius) {
                    continue;
                }

                BlockPos pos = center.offset(x, 0, z);
                if (!isReplaceable(level.getBlockState(pos))) {
                    continue;
                }

                BlockState leavesState = baseState;
                if (leavesState.hasProperty(BlockStateProperties.DISTANCE)) {
                    int distance = Math.min(7, Math.max(1, Math.abs(x) + Math.abs(z)));
                    leavesState = leavesState.setValue(BlockStateProperties.DISTANCE, distance);
                }
                level.setBlock(pos, leavesState, 19);
            }
        }
    }

    private static boolean hasRoom(ServerLevel level, BlockPos min, BlockPos max) {
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (!isReplaceable(level.getBlockState(pos))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isReplaceable(BlockState state) {
        return state.isAir() || state.is(BlockTags.LEAVES) || state.getMaterial().isReplaceable();
    }

    public static final class StructureVariant {
        private final ResourceLocation structureId;
        private final BlockPos anchor;

        public StructureVariant(ResourceLocation structureId, BlockPos anchor) {
            this.structureId = structureId;
            this.anchor = anchor;
        }
    }

    private static final class LoadedVariant {
        private final StructureVariant variant;
        private final StructureTemplate template;

        private LoadedVariant(StructureVariant variant, StructureTemplate template) {
            this.variant = variant;
            this.template = template;
        }
    }
}
