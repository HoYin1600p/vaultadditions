package io.github.a1qs.vaultadditions.mixins;

import iskallia.vault.dynamodel.registry.DynamicModelRegistries;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.init.ModItems;
import xyz.iwolfking.woldsvaults.models.Battlestaffs;
import xyz.iwolfking.woldsvaults.models.Tridents;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mixin(value = DynamicModelRegistries.class, remap = false)
public class MixinDynamicModelRegistries {
    @Inject(method = "getUniqueRegistries", at = @At("RETURN"), cancellable = true)
    public void addArmorModelRegistry(CallbackInfoReturnable<Set<DynamicModelRegistry<?>>> cir) {
        Set<DynamicModelRegistry<?>> registries = new HashSet<>(cir.getReturnValue());
        registries.add(ModDynamicModels.Armor.MODEL_REGISTRY);
        registries.add(Battlestaffs.REGISTRY);
        registries.add(Tridents.REGISTRY);
        cir.setReturnValue(Collections.unmodifiableSet(registries));
    }

    @Inject(method = "getUniqueItems", at = @At("RETURN"), cancellable = true)
    public void addWoldDynamicModelItems(CallbackInfoReturnable<Set<Item>> cir) {
        Set<Item> items = new HashSet<>(cir.getReturnValue());
        if (ModItems.BATTLESTAFF != null) {
            items.add(ModItems.BATTLESTAFF);
        }
        if (ModItems.TRIDENT != null) {
            items.add(ModItems.TRIDENT);
        }
        cir.setReturnValue(Collections.unmodifiableSet(items));
    }

    @Inject(method = "getAssociatedRegistry", at = @At("HEAD"), cancellable = true)
    public void getWoldAssociatedRegistry(Item item, CallbackInfoReturnable<Optional<DynamicModelRegistry<?>>> cir) {
        if (item == ModItems.BATTLESTAFF) {
            cir.setReturnValue(Optional.of(Battlestaffs.REGISTRY));
        } else if (item == ModItems.TRIDENT) {
            cir.setReturnValue(Optional.of(Tridents.REGISTRY));
        }
    }

    @Inject(method = "getAssociatedItem", at = @At("HEAD"), cancellable = true)
    public void getWoldAssociatedItem(DynamicModelRegistry<?> registry, CallbackInfoReturnable<Item> cir) {
        if (registry == Battlestaffs.REGISTRY && ModItems.BATTLESTAFF != null) {
            cir.setReturnValue(ModItems.BATTLESTAFF);
        } else if (registry == Tridents.REGISTRY && ModItems.TRIDENT != null) {
            cir.setReturnValue(ModItems.TRIDENT);
        }
    }
}
