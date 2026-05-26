package io.github.a1qs.vaultadditions.vault.gear.gecko.item;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.a1qs.vaultadditions.util.ModelUtil;
import io.github.a1qs.vaultadditions.vault.gear.gecko.VaultGeckoModel;
import io.github.a1qs.vaultadditions.vault.gear.gecko.VaultGeckoModelProvider;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VaultGeckoItemRenderer<T extends Item & VaultGearItem & IAnimatable> extends GeoItemRenderer<T> {
    private static final Gson GSON = new Gson();
    private static final String CULL_FACES_PROPERTY = "vaultadditions:cull_faces";
    private static final Map<ResourceLocation, Boolean> CULL_FACES_BY_MODEL = new HashMap<>();

    private final IItemRenderProperties defaultProperties;

    public VaultGeckoItemRenderer(IItemRenderProperties defaultProperties) {
        super(new VaultGeckoModelProvider<>());
        this.defaultProperties = defaultProperties != null ? defaultProperties : IItemRenderProperties.DUMMY;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        DynamicModel<?> dynamicModel = ModelUtil.getDynamicModel(stack);
        if (!(dynamicModel instanceof VaultGeckoModel)) {
            defaultProperties.getItemStackRenderer().renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            return;
        }
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void render(T animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack stack) {
        ((VaultGeckoModelProvider<?>) getGeoModelProvider()).using(stack);
        super.render(animatable, poseStack, bufferSource, packedLight, stack);
    }

    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        return ((VaultGeckoModelProvider<?>) getGeoModelProvider()).getTextureLocation(this.currentItemStack);
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        DynamicModel<?> dynamicModel = ModelUtil.getDynamicModel(this.currentItemStack);
        return usesFaceCulling(dynamicModel)
                ? RenderType.entityTranslucentCull(texture)
                : RenderType.entityTranslucent(texture);
    }

    private static boolean usesFaceCulling(DynamicModel<?> dynamicModel) {
        if (dynamicModel == null) {
            return false;
        }

        return CULL_FACES_BY_MODEL.computeIfAbsent(dynamicModel.getId(), VaultGeckoItemRenderer::loadFaceCulling);
    }

    private static boolean loadFaceCulling(ResourceLocation modelId) {
        ResourceLocation modelJson = DynamicModel.appendToId(DynamicModel.prependToId("models/item/", modelId), ".json");
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        if (!manager.hasResource(modelJson)) {
            return false;
        }

        try {
            Resource resource = manager.getResource(modelJson);
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                return json != null && json.has(CULL_FACES_PROPERTY) && json.get(CULL_FACES_PROPERTY).getAsBoolean();
            }
        } catch (Exception ignored) {
            return false;
        }
    }
}
