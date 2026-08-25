package io.github.a1qs.vaultadditions.block.grower;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Random;

public class StructureTreeGrower extends AbstractTreeGrower {
    private final RegistryObject<ConfiguredFeature<?, ?>> configuredFeature;

    public StructureTreeGrower(RegistryObject<ConfiguredFeature<?, ?>> configuredFeature) {
        this.configuredFeature = configuredFeature;
    }

    @Override
    @Nullable
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random random, boolean hasFlowers) {
        return configuredFeature.getHolder().orElse(null);
    }
}
