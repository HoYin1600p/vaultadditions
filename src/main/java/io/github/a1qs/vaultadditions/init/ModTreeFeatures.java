package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.world.feature.StructureTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public final class ModTreeFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registry.FEATURE_REGISTRY, VaultAdditions.MOD_ID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, VaultAdditions.MOD_ID);

    public static final RegistryObject<StructureTreeFeature> CHROMATIC_TREE_FEATURE = registerFeature(
            "chromatic_tree", "chromatic", "chromatic_log", "chromatic_leaves",
            new BlockPos(4, 0, 6), new BlockPos(4, 0, 4), new BlockPos(5, 0, 5));
    public static final RegistryObject<StructureTreeFeature> IDONA_TREE_FEATURE = registerFeature(
            "idona_tree", "idona", "idona_log", "idona_leaves",
            new BlockPos(3, 0, 2), new BlockPos(5, 0, 4), new BlockPos(2, 0, 2));
    public static final RegistryObject<StructureTreeFeature> TENOS_TREE_FEATURE = registerFeature(
            "tenos_tree", "tenos", "tenos_log", "tenos_leaves",
            new BlockPos(5, 0, 3), new BlockPos(4, 0, 2), new BlockPos(4, 0, 3));
    public static final RegistryObject<StructureTreeFeature> VELARA_TREE_FEATURE = registerFeature(
            "velara_tree", "velara", "velara_log", "velara_leaves",
            new BlockPos(5, 0, 4), new BlockPos(5, 0, 4), new BlockPos(2, 0, 3));
    public static final RegistryObject<StructureTreeFeature> WENDARR_TREE_FEATURE = registerFeature(
            "wendarr_tree", "wendarr", "wendarr_log", "wendarr_leaves",
            new BlockPos(4, 0, 3), new BlockPos(3, 0, 3), new BlockPos(3, 0, 2));

    public static final RegistryObject<ConfiguredFeature<?, ?>> CHROMATIC_TREE = registerConfigured(
            "chromatic_tree", CHROMATIC_TREE_FEATURE);
    public static final RegistryObject<ConfiguredFeature<?, ?>> IDONA_TREE = registerConfigured(
            "idona_tree", IDONA_TREE_FEATURE);
    public static final RegistryObject<ConfiguredFeature<?, ?>> TENOS_TREE = registerConfigured(
            "tenos_tree", TENOS_TREE_FEATURE);
    public static final RegistryObject<ConfiguredFeature<?, ?>> VELARA_TREE = registerConfigured(
            "velara_tree", VELARA_TREE_FEATURE);
    public static final RegistryObject<ConfiguredFeature<?, ?>> WENDARR_TREE = registerConfigured(
            "wendarr_tree", WENDARR_TREE_FEATURE);

    private ModTreeFeatures() {
    }

    private static RegistryObject<StructureTreeFeature> registerFeature(
            String featureName, String treeName, String log, String leaves, BlockPos... anchors) {
        List<StructureTreeFeature.StructureVariant> variants = new ArrayList<>();
        for (int i = 0; i < anchors.length; i++) {
            variants.add(new StructureTreeFeature.StructureVariant(
                    new ResourceLocation("the_vault", "trees/" + treeName + "_treetype_0" + (i + 1)),
                    anchors[i]));
        }

        List<StructureTreeFeature.StructureVariant> immutableVariants = List.copyOf(variants);
        return FEATURES.register(featureName, () -> new StructureTreeFeature(
                immutableVariants, new ResourceLocation("the_vault", log),
                new ResourceLocation("the_vault", leaves)));
    }

    private static RegistryObject<ConfiguredFeature<?, ?>> registerConfigured(
            String name, RegistryObject<StructureTreeFeature> feature) {
        return CONFIGURED_FEATURES.register(name,
                () -> new ConfiguredFeature<>(feature.get(), NoneFeatureConfiguration.INSTANCE));
    }
}
