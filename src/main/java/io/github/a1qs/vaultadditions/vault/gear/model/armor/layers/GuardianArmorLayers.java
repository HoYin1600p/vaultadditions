package io.github.a1qs.vaultadditions.vault.gear.model.armor.layers;

import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class GuardianArmorLayers extends ArmorLayers {
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

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
		.texOffs(56, 9).addBox(-1.0F, -5.0F, -6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(52, 65).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -4.116F, 2.951F, 0.0F, -1.5708F, 0.3491F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(38, 65).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -4.116F, -3.049F, 0.0F, -1.5708F, 0.3491F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 86).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -10.116F, -0.049F, 0.0F, -1.5708F, -1.7453F));

		PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(64, 16).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -10.116F, -0.049F, 0.0F, -1.5708F, -0.5236F));

		PartDefinition cube_r5 = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(68, 30).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -4.116F, -3.049F, 0.0F, 1.5708F, -0.3491F));

		PartDefinition cube_r6 = head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(66, 65).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -4.116F, 2.951F, 0.0F, 1.5708F, -0.3491F));

		PartDefinition cube_r7 = head.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(64, 9).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -10.116F, -0.049F, 0.0F, 1.5708F, 0.5236F));

		PartDefinition cube_r8 = head.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(28, 86).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -10.116F, -0.049F, 0.0F, 1.5708F, 1.7453F));

		PartDefinition cube_r9 = head.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(52, 86).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.866F, 6.951F, 1.3963F, 0.0F, -3.1416F));

		PartDefinition cube_r10 = head.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(64, 23).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.866F, 6.951F, 2.618F, 0.0F, -3.1416F));

		PartDefinition cube_r11 = head.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 85).addBox(-1.0F, -1.0F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -12.366F, -8.549F, -1.7453F, 0.0F, 0.0F));

		PartDefinition cube_r12 = head.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(30, 72).addBox(-4.9668F, -1.0F, -5.5438F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1631F, 1.3844F, 5.4514F, -2.2415F, -0.445F, 2.6445F));

		PartDefinition cube_r13 = head.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(44, 72).addBox(3.1383F, -1.0844F, -5.3049F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1631F, 1.3844F, 5.4514F, -2.309F, 0.4404F, -2.7262F));

		PartDefinition cube_r14 = head.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 71).addBox(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.8369F, 1.3844F, -5.5486F, 0.8326F, -0.4404F, -0.4154F));

		PartDefinition cube_r15 = head.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(16, 70).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, 1.25F, -4.0F, 0.9001F, 0.445F, 0.4971F));

		PartDefinition cube_r16 = head.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(32, 15).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.75F, -6.75F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r17 = head.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(92, 53).addBox(-3.25F, 1.0F, -0.5F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(38, 22).addBox(-0.25F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(92, 58).addBox(-3.25F, -5.0F, -0.5F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.75F, -4.0F, -5.25F, 0.0F, -0.3927F, 0.0F));

		PartDefinition cube_r18 = head.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(32, 22).addBox(-1.75F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(92, 37).addBox(-1.75F, -5.0F, -0.5F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(92, 19).addBox(-1.75F, 1.0F, -0.5F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8725F, -4.0F, -5.25F, 0.0F, 0.3927F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
		.texOffs(62, 40).addBox(-3.0F, 6.0F, -6.0F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(92, 7).addBox(-2.0F, 4.0F, -7.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(92, 63).addBox(-2.0F, 10.0F, -5.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r19 = body.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(38, 56).addBox(-4.0F, -7.1398F, 1.6862F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(58, 56).addBox(-4.0F, -5.292F, -0.0792F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0268F, 4.3257F, -2.3562F, 0.0F, 3.1416F));

		PartDefinition cube_r20 = body.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(62, 47).addBox(-3.0F, -1.0268F, -1.3257F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(24, 93).addBox(-2.0F, 2.9732F, -0.3257F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0268F, 4.3257F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition cube_r21 = body.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(56, 0).addBox(-4.0F, -3.5F, -0.875F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(48, 31).addBox(-4.0F, -5.3478F, 0.8904F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.1969F, -5.3788F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r22 = body.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(0, 78).addBox(1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.261F, 4.9797F, 7.711F, -2.3389F, -0.1841F, 2.9543F));

		PartDefinition cube_r23 = body.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(78, 7).addBox(-3.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1813F, 4.7872F, 7.6578F, -2.3776F, 0.257F, -2.9254F));

		PartDefinition cube_r24 = body.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(76, 0).addBox(3.1383F, -1.0844F, -5.3049F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3369F, 7.1344F, 5.4514F, -2.309F, 0.4404F, -2.7262F));

		PartDefinition cube_r25 = body.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(14, 77).addBox(-4.9668F, -1.0F, -5.5438F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1631F, 7.1344F, 5.4514F, -2.2415F, -0.445F, 2.6445F));

		PartDefinition cube_r26 = body.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(72, 72).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.25F, 7.25F, -4.0F, 0.9001F, 0.445F, 0.4971F));

		PartDefinition cube_r27 = body.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(58, 72).addBox(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8369F, 7.3844F, -5.5486F, 0.8326F, -0.4404F, -0.4154F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(46, 40).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
		.texOffs(24, 25).addBox(-5.0F, -2.0F, -4.0F, 4.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition cube_r28 = right_arm.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(64, 88).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 1.884F, -0.049F, 0.0F, 1.5708F, 0.6109F));

		PartDefinition cube_r29 = right_arm.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(42, 79).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.25F, 1.25F, -2.0F, 0.9001F, 0.445F, 0.4971F));

		PartDefinition cube_r30 = right_arm.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(78, 54).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 1.884F, -0.049F, 0.0F, 1.5708F, -0.6109F));

		PartDefinition cube_r31 = right_arm.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(86, 70).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -4.116F, -0.049F, 0.0F, 1.5708F, 1.7453F));

		PartDefinition cube_r32 = right_arm.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(78, 14).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -4.116F, -0.049F, 0.0F, 1.5708F, 0.5236F));

		PartDefinition cube_r33 = right_arm.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(24, 40).addBox(-2.0F, -2.5F, -4.0F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 8.5F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition cube_r34 = right_arm.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(56, 79).addBox(3.1383F, -1.0844F, -5.3049F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1631F, 1.3844F, 4.4514F, -2.309F, 0.4404F, -2.7262F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0).addBox(1.0F, -2.0F, -4.0F, 4.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(48, 15).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition cube_r35 = left_arm.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(92, 13).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5869F, 2.3844F, -4.0486F, 0.8326F, -0.4404F, -0.4154F));

		PartDefinition cube_r36 = left_arm.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(0, 41).addBox(-2.0F, -2.5F, -4.0F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 8.5F, 0.0F, 3.1416F, 0.0F, -2.7489F));

		PartDefinition cube_r37 = left_arm.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(90, 0).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 1.884F, -0.049F, 0.0F, -1.5708F, -0.6109F));

		PartDefinition cube_r38 = left_arm.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(28, 79).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 1.884F, -0.049F, 0.0F, -1.5708F, 0.6109F));

		PartDefinition cube_r39 = left_arm.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(82, 86).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -4.116F, -0.049F, 0.0F, -1.5708F, -1.7453F));

		PartDefinition cube_r40 = left_arm.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(78, 21).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -4.116F, -0.049F, 0.0F, -1.5708F, -0.5236F));

		PartDefinition cube_r41 = left_arm.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(14, 84).addBox(-8.9668F, 2.0F, -9.5438F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.1631F, -0.6156F, 5.4514F, -2.2415F, -0.445F, 2.6445F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(22, 54).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition cube_r42 = right_leg.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(80, 46).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.1F, 7.884F, -0.049F, 0.0F, 1.5708F, 0.3491F));

		PartDefinition cube_r43 = right_leg.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(12, 91).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.1F, 7.884F, -0.049F, 0.0F, 1.5708F, 1.5708F));

		PartDefinition cube_r44 = right_leg.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(70, 79).addBox(-4.0F, -3.5F, -1.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 9.5F, -3.0F, 0.3054F, 0.0F, 0.0F));

		PartDefinition cube_r45 = right_leg.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(82, 28).addBox(0.0F, -3.5F, -1.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 9.5F, 3.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition cube_r46 = left_leg.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(82, 79).addBox(-1.0F, 0.049F, -1.317F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.1F, 7.884F, -0.049F, 0.0F, -1.5708F, -0.3491F));

		PartDefinition cube_r47 = left_leg.addOrReplaceChild("cube_r47", CubeListBuilder.create().texOffs(0, 92).addBox(-0.5F, -0.2471F, -3.8904F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.1F, 7.884F, -0.049F, 0.0F, -1.5708F, -1.5708F));

		PartDefinition cube_r48 = left_leg.addOrReplaceChild("cube_r48", CubeListBuilder.create().texOffs(80, 61).addBox(0.0F, -3.5F, -1.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, 9.5F, 3.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r49 = left_leg.addOrReplaceChild("cube_r49", CubeListBuilder.create().texOffs(80, 37).addBox(0.0F, -3.5F, -1.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, 9.5F, -3.0F, 0.3054F, 0.0F, 0.0F));

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

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -6.0F, 2.0F, 0.0F, 8.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(40, 11).addBox(-2.0F, -3.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 14.75F, 14.25F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 35).addBox(-2.0F, -3.0F, -1.0F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 15.0F, 7.25F, 0.0436F, 0.0F, 0.0F));

		PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(22, 0).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 1.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 11).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 27).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}
    }
}
