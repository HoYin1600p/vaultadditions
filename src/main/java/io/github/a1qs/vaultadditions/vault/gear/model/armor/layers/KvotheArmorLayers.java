package io.github.a1qs.vaultadditions.vault.gear.model.armor.layers;

import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class KvotheArmorLayers extends ArmorLayers {
    @Override
    public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS ? LeggingsLayer::createBodyLayer : MainLayer::createBodyLayer;
    }

    @Override
    public VaultArmorLayerSupplier<? extends BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS ? LeggingsLayer::new : MainLayer::new;
    }

    @OnlyIn(Dist.CLIENT)
    public static class MainLayer extends ArmorLayers.MainLayer {
        public MainLayer(ArmorPieceModel definition, ModelPart root) {
            super(definition, root);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = createBaseLayer();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition head = partdefinition.addOrReplaceChild("head",
                    CubeListBuilder.create()
                            .texOffs(0, 12).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.51F))
                            .texOffs(50, 0).addBox(-4.0F, -12.0F, -4.0F, 3.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                            .texOffs(54, 10).addBox(1.0F, -12.0F, -4.0F, 3.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                            .texOffs(0, 0).addBox(-7.0F, -6.0F, -4.0F, 14.0F, 1.0F, 11.0F, new CubeDeformation(0.0F)),
                    PartPose.offset(0.0F, 0.0F, 0.0F));
            head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(16, 56).addBox(0.25F, -4.0F, -3.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.3F, -10.0F, 2.0F, -0.4363F, 0.0F, 0.0F));
            head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 12).addBox(-0.5F, -1.5F, -4.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -5.75F, 0.0F, 0.0F, 0.0F, 0.7854F));
            head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(24, 29).addBox(-1.5F, -0.5F, -4.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -5.75F, 0.0F, 0.0F, 0.0F, 0.7854F));
            head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(36, 24).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -5.4389F, -4.959F, 0.1872F, -0.1841F, 0.7681F));
            head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(56, 40).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -5.4389F, -4.959F, 0.1872F, 0.1841F, -0.7681F));
            head.addOrReplaceChild("cube_r6", CubeListBuilder.create()
                    .texOffs(28, 56).addBox(-5.0F, -1.0F, -8.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 24).addBox(-7.0F, -1.0F, -7.0F, 14.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(0.0F, -6.0F, -1.0F, 0.2618F, 0.0F, 0.0F));
            head.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(54, 20).addBox(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0913F, 7.4619F, 0.7854F, 0.0F, 0.0F));

            partdefinition.addOrReplaceChild("body",
                    CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.52F)),
                    PartPose.offset(0.0F, 0.0F, 0.0F));

            partdefinition.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(24, 40).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));

            partdefinition.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(40, 40).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
                    PartPose.offset(5.0F, 2.0F, 0.0F));

            partdefinition.addOrReplaceChild("right_leg",
                    CubeListBuilder.create().texOffs(0, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
                    PartPose.offset(-1.9F, 12.0F, 0.0F));

            partdefinition.addOrReplaceChild("left_leg",
                    CubeListBuilder.create().texOffs(48, 24).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
                    PartPose.offset(1.9F, 12.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 128, 128);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LeggingsLayer extends ArmorLayers.LeggingsLayer {
        public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
            super(definition, root);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = createBaseLayer();
            PartDefinition partdefinition = meshdefinition.getRoot();

            partdefinition.addOrReplaceChild("body",
                    CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
                    PartPose.offset(0.0F, 0.0F, 0.0F));

            partdefinition.addOrReplaceChild("right_leg",
                    CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.52F)),
                    PartPose.offset(-1.9F, 12.0F, 0.0F));

            partdefinition.addOrReplaceChild("left_leg",
                    CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.52F)),
                    PartPose.offset(1.9F, 12.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 32, 32);
        }
    }
}
