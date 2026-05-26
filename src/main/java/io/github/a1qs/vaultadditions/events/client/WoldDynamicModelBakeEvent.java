package io.github.a1qs.vaultadditions.events.client;

import io.github.a1qs.vaultadditions.VaultAdditions;
import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WoldDynamicModelBakeEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void wrapWoldGearModels(ModelBakeEvent event) {
        wrap(event, new ModelResourceLocation(VaultMod.id("battlestaff"), "inventory"));
        wrap(event, new ModelResourceLocation(VaultMod.id("trident"), "inventory"));
    }

    private static void wrap(ModelBakeEvent event, ModelResourceLocation location) {
        Map<ResourceLocation, BakedModel> models = event.getModelRegistry();
        BakedModel model = models.get(location);
        if (model == null || model instanceof DynamicBakedModel) {
            return;
        }

        models.put(location, new DynamicBakedModel(model, event.getModelLoader()));
    }
}
