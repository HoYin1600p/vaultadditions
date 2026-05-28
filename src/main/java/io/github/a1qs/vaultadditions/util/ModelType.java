package io.github.a1qs.vaultadditions.util;

import io.github.a1qs.vaultadditions.VaultAdditions;
import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.model.item.PlainItemModel;
import iskallia.vault.dynamodel.model.item.shield.ShieldModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xyz.iwolfking.woldsvaults.models.Battlestaffs;
import xyz.iwolfking.woldsvaults.models.Tridents;

public enum ModelType {
    SWORD(ModDynamicModels.Swords.REGISTRY, HandHeldModel::new),
    AXE(ModDynamicModels.Axes.REGISTRY, HandHeldModel::new),
    BATTLESTAFF(Battlestaffs.REGISTRY, HandHeldModel::new),
    TRIDENT(Tridents.REGISTRY, HandHeldModel::new),
    SHIELD(ModDynamicModels.Shields.REGISTRY, ShieldModel::new),
    FOCUS(ModDynamicModels.Focus.REGISTRY, PlainItemModel::new),
    WAND(ModDynamicModels.Wands.REGISTRY, PlainItemModel::new),
    MAGNETS(ModDynamicModels.Magnets.REGISTRY, PlainItemModel::new),
    WENDARR_IDOL("idol", ModDynamicModels.Idols.REGISTRY_WENDARR, PlainItemModel::new),
    IDONA_IDOL("idol", ModDynamicModels.Idols.REGISTRY_IDONA, PlainItemModel::new),
    VELARA_IDOL("idol", ModDynamicModels.Idols.REGISTRY_VELARA, PlainItemModel::new),
    TENOS_IDOL("idol", ModDynamicModels.Idols.REGISTRY_TENOS, PlainItemModel::new);

    private final String type;
    private final DynamicModelRegistry<?> registry;
    private final ModelFactory modelFactory;

    ModelType(DynamicModelRegistry<?> registry, ModelFactory modelFactory) {
        this.type = name().toLowerCase();
        this.registry = registry;
        this.modelFactory = modelFactory;
    }

    ModelType(String type, DynamicModelRegistry<?> registry, ModelFactory modelFactory) {
        this.type = type;
        this.registry = registry;
        this.modelFactory = modelFactory;
    }

    public DynamicModel<?> createModel(String id, String displayName) {
        return modelFactory.create(VaultMod.id("gear/" + type + "/" + id), displayName);
    }

    public DynamicModel<?> createModel(ResourceLocation id, String displayName) {
        return modelFactory.create(id, displayName);
    }

    public DynamicModel<?> createGeckoModel(String id, String displayName, String animationName, float transitionTicks) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return ClientModelFactories.createGeckoModel(this, id, displayName, animationName, transitionTicks);
        }
        return createModel(id, displayName);
    }

    public DynamicModelRegistry<?> getRegistry() {
        return registry;
    }

    String getType() {
        return type;
    }

    public void register(DynamicModel<?> model) {
        registry.register(forceCast(model));
    }

    public static ModelType getValue(String value) {
        if (value != null) {
            return switch (value) {
                case "sword", "swords" -> SWORD;
                case "axe", "axes" -> AXE;
                case "battlestaff", "battlestaffs" -> BATTLESTAFF;
                case "trident", "tridents" -> TRIDENT;
                case "shield", "shields" -> SHIELD;
                case "focus", "focuses" -> FOCUS;
                case "wand", "wands" -> WAND;
                case "magnet", "magnets" -> MAGNETS;
                case "wendarr", "wendarr_idol" -> WENDARR_IDOL;
                case "idona", "idona_idol" -> IDONA_IDOL;
                case "velara", "velara_idol" -> VELARA_IDOL;
                case "tenos", "tenos_idol" -> TENOS_IDOL;
                default -> null;
            };
        }
        return null;
    }

    @FunctionalInterface
    private interface ModelFactory {
        DynamicModel<?> create(ResourceLocation id, String displayName);
    }

    private static <C> C forceCast(Object obj) {
        try {
            return (C) obj;
        } catch (Exception e) {
            VaultAdditions.LOGGER.error("Failed to cast object " + obj + " to expected type", e);
            throw e;
        }
    }
}
