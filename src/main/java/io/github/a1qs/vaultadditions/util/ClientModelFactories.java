package io.github.a1qs.vaultadditions.util;

import io.github.a1qs.vaultadditions.vault.gear.gecko.item.GeckoHandHeldModel;
import io.github.a1qs.vaultadditions.vault.gear.gecko.item.GeckoPlainModel;
import io.github.a1qs.vaultadditions.vault.gear.gecko.item.GeckoShieldModel;
import iskallia.vault.dynamodel.DynamicModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
final class ClientModelFactories {
    private ClientModelFactories() {
    }

    static DynamicModel<?> createGeckoModel(ModelType modelType, String id, String displayName, String animationName, float transitionTicks) {
        String type = modelType.getType();
        return switch (modelType) {
            case SHIELD -> new GeckoShieldModel(id, type, displayName, animationName, transitionTicks);
            case FOCUS, WAND, MAGNETS, WENDARR_IDOL, IDONA_IDOL, VELARA_IDOL, TENOS_IDOL ->
                    new GeckoPlainModel(id, type, displayName, animationName, transitionTicks);
            default -> new GeckoHandHeldModel(id, type, displayName, animationName, transitionTicks);
        };
    }
}
