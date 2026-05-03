package io.github.a1qs.vaultadditions.mixins;

import com.mojang.logging.LogUtils;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

public class VaultAdditionsMixinPlugin extends RestrictiveMixinConfigPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    protected void onRestrictionCheckFailed(String mixinClassName, String reason) {
        LOGGER.debug("Skipping mixin {}: {}", mixinClassName, reason);
    }
}
