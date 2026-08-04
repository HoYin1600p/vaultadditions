package io.github.a1qs.vaultadditions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.github.a1qs.vaultadditions.VaultAdditions;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;

public final class AltarDoubleChestModel {
    public static final ResourceLocation TEXTURE =
            VaultAdditions.id("textures/entity/chest/altar_double_chest.png");

    private static final float PIXEL = 1.0F / 16.0F;
    private static final float SURFACE_OFFSET = PIXEL / 64.0F;
    private static final float TEXTURE_SIZE = 256.0F;

    private static final FaceUv BODY_FRONT = uv(0, 0, 32, 10);
    private static final FaceUv BODY_BACK = uv(0, 10, 32, 20);
    private static final FaceUv BODY_TOP = uv(0, 20, 32, 34);
    private static final FaceUv BODY_BOTTOM = uv(32, 20, 64, 34);
    private static final FaceUv BODY_LEFT = uv(64, 0, 78, 10);
    private static final FaceUv BODY_RIGHT = uv(78, 0, 92, 10);

    private static final FaceUv RIM_FRONT = uv(0, 34, 32, 38);
    private static final FaceUv RIM_BACK = uv(0, 38, 32, 42);
    private static final FaceUv RIM_TOP = uv(0, 42, 32, 58);
    private static final FaceUv RIM_BOTTOM = uv(32, 42, 64, 58);
    private static final FaceUv RIM_LEFT = uv(64, 34, 80, 38);
    private static final FaceUv RIM_RIGHT = uv(80, 34, 96, 38);

    private static final FaceUv LID_SHELL_FRONT = uv(0, 58, 32, 60);
    private static final FaceUv LID_PANEL_FRONT = uv(0, 60, 32, 65);
    private static final FaceUv LID_SHELL_BACK = uv(0, 65, 32, 67);
    private static final FaceUv LID_PANEL_BACK = uv(0, 67, 32, 72);
    private static final FaceUv LID_SHELL_SURFACE = uv(32, 72, 64, 88);
    private static final FaceUv BODY_INTERIOR = uv(0, 88, 32, 102);
    private static final FaceUv LID_PANEL_SURFACE = uv(32, 88, 64, 102);
    private static final FaceUv LID_SHELL_LEFT = uv(64, 58, 80, 60);
    private static final FaceUv LID_PANEL_LEFT = uv(64, 60, 78, 65);
    private static final FaceUv LID_SHELL_RIGHT = uv(80, 58, 96, 60);
    private static final FaceUv LID_PANEL_RIGHT = uv(78, 60, 92, 65);

    private static final BoxUvs BASE_BAND_LEFT = boxUvs(128, 0, 4, 6, 16);
    private static final BoxUvs BASE_BAND_RIGHT = boxUvs(176, 0, 4, 6, 16);
    private static final BoxUvs LID_BAND_LEFT = boxUvs(128, 24, 4, 4, 16);
    private static final BoxUvs LID_BAND_RIGHT = boxUvs(176, 24, 4, 4, 16);
    private static final BoxUvs BUCKLE = boxUvs(128, 48, 2, 4, 1);
    private static final BoxUvs ACCENT = boxUvs(144, 48, 4, 2, 4);
    private static final BoxUvs FOOT_LEFT_BACK = boxUvs(128, 60, 3, 3, 3);
    private static final BoxUvs FOOT_LEFT_FRONT = boxUvs(144, 60, 3, 3, 3);
    private static final BoxUvs FOOT_RIGHT_BACK = boxUvs(160, 60, 3, 3, 3);
    private static final BoxUvs FOOT_RIGHT_FRONT = boxUvs(176, 60, 3, 3, 3);
    private static final BoxUvs CAP_LEFT_BACK = boxUvs(128, 72, 3, 3, 3);
    private static final BoxUvs CAP_LEFT_FRONT = boxUvs(144, 72, 3, 3, 3);
    private static final BoxUvs CAP_RIGHT_BACK = boxUvs(160, 72, 3, 3, 3);
    private static final BoxUvs CAP_RIGHT_FRONT = boxUvs(176, 72, 3, 3, 3);
    private static final FaceUv HIDDEN = uv(255, 255, 256, 256);

    private AltarDoubleChestModel() {
    }

    public static void renderHalf(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            ChestType chestType,
            float lidAngle
    ) {
        if (chestType == ChestType.SINGLE) {
            return;
        }

        boolean leftHalf = chestType == ChestType.RIGHT;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderBase(poseStack, consumer, packedLight, packedOverlay, leftHalf);

        poseStack.pushPose();
        poseStack.translate(0.0D, 9.0D * PIXEL, 1.0D * PIXEL);
        poseStack.mulPose(Vector3f.XP.rotation(-lidAngle * ((float) Math.PI / 2.0F)));
        renderLid(poseStack, consumer, packedLight, packedOverlay, leftHalf);
        poseStack.popPose();
    }

    private static void renderBase(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            boolean leftHalf
    ) {
        float bodyX0 = leftHalf ? PIXEL : 0.0F;
        float bodyX1 = leftHalf ? 1.0F : 15.0F * PIXEL;
        int textureX = leftHalf ? 0 : 16;

        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                bodyX0,
                0.0F,
                PIXEL,
                bodyX1,
                10.0F * PIXEL,
                15.0F * PIXEL,
                halfUv(BODY_FRONT, textureX),
                halfUv(BODY_BACK, textureX),
                leftHalf ? BODY_LEFT : HIDDEN,
                leftHalf ? HIDDEN : BODY_RIGHT,
                HIDDEN,
                halfUv(BODY_BOTTOM, textureX)
        );

        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                0.0F,
                6.0F * PIXEL,
                0.0F,
                1.0F,
                10.0F * PIXEL,
                1.0F,
                halfUv(RIM_FRONT, textureX),
                halfUv(RIM_BACK, textureX),
                leftHalf ? RIM_LEFT : HIDDEN,
                leftHalf ? HIDDEN : RIM_RIGHT,
                halfUv(RIM_TOP, textureX),
                halfUv(RIM_BOTTOM, textureX)
        );

        float bandX0 = leftHalf ? 3.0F * PIXEL : 9.0F * PIXEL;
        BoxUvs bandUvs = leftHalf ? BASE_BAND_LEFT : BASE_BAND_RIGHT;
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                bandX0,
                0.0F,
                -SURFACE_OFFSET,
                bandX0 + 4.0F * PIXEL,
                6.0F * PIXEL,
                1.0F + SURFACE_OFFSET,
                bandUvs.front,
                bandUvs.back,
                bandUvs.left,
                bandUvs.right,
                HIDDEN,
                bandUvs.bottom
        );

        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                bodyX0,
                10.0F * PIXEL,
                PIXEL,
                bodyX1,
                10.0F * PIXEL + SURFACE_OFFSET,
                15.0F * PIXEL,
                HIDDEN,
                HIDDEN,
                HIDDEN,
                HIDDEN,
                halfUv(BODY_INTERIOR, textureX),
                HIDDEN
        );

        float footX0 = leftHalf ? 0.75F * PIXEL : 12.25F * PIXEL;
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                footX0,
                -0.25F * PIXEL,
                0.75F * PIXEL,
                footX0 + 3.0F * PIXEL,
                2.75F * PIXEL,
                3.75F * PIXEL,
                leftHalf ? FOOT_LEFT_BACK : FOOT_RIGHT_BACK
        );
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                footX0,
                -0.25F * PIXEL,
                12.25F * PIXEL,
                footX0 + 3.0F * PIXEL,
                2.75F * PIXEL,
                15.25F * PIXEL,
                leftHalf ? FOOT_LEFT_FRONT : FOOT_RIGHT_FRONT
        );
    }

    private static void renderLid(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            boolean leftHalf
    ) {
        int textureX = leftHalf ? 0 : 16;
        float shellX0 = leftHalf ? SURFACE_OFFSET : 0.0F;
        float shellX1 = leftHalf ? 1.0F : 1.0F - SURFACE_OFFSET;

        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                shellX0,
                0.0F,
                -PIXEL + SURFACE_OFFSET,
                shellX1,
                2.0F * PIXEL,
                15.0F * PIXEL - SURFACE_OFFSET,
                halfUv(LID_SHELL_FRONT, textureX),
                halfUv(LID_SHELL_BACK, textureX),
                leftHalf ? LID_SHELL_LEFT : HIDDEN,
                leftHalf ? HIDDEN : LID_SHELL_RIGHT,
                halfUv(LID_SHELL_SURFACE, textureX),
                halfUv(LID_SHELL_SURFACE, textureX)
        );

        float innerX0 = leftHalf ? PIXEL : 0.0F;
        float innerX1 = leftHalf ? 1.0F : 15.0F * PIXEL;
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                innerX0,
                0.0F,
                0.0F,
                innerX1,
                5.0F * PIXEL,
                14.0F * PIXEL,
                halfUv(LID_PANEL_FRONT, textureX),
                halfUv(LID_PANEL_BACK, textureX),
                leftHalf ? LID_PANEL_LEFT : HIDDEN,
                leftHalf ? HIDDEN : LID_PANEL_RIGHT,
                halfUv(LID_PANEL_SURFACE, textureX),
                HIDDEN
        );

        float bandX0 = leftHalf ? 3.0F * PIXEL : 9.0F * PIXEL;
        BoxUvs bandUvs = leftHalf ? LID_BAND_LEFT : LID_BAND_RIGHT;
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                bandX0,
                2.0F * PIXEL,
                -PIXEL - SURFACE_OFFSET,
                bandX0 + 4.0F * PIXEL,
                6.0F * PIXEL,
                15.0F * PIXEL + SURFACE_OFFSET,
                bandUvs.front,
                bandUvs.back,
                bandUvs.left,
                bandUvs.right,
                bandUvs.top,
                HIDDEN
        );

        if (leftHalf) {
            renderBox(
                    poseStack,
                    consumer,
                    packedLight,
                    packedOverlay,
                    15.0F * PIXEL,
                    -2.0F * PIXEL,
                    15.0F * PIXEL - SURFACE_OFFSET,
                    17.0F * PIXEL,
                    2.0F * PIXEL,
                    1.0F,
                    BUCKLE
            );

            renderBox(
                    poseStack,
                    consumer,
                    packedLight,
                    packedOverlay,
                    14.0F * PIXEL,
                    5.0F * PIXEL - SURFACE_OFFSET,
                    5.0F * PIXEL,
                    18.0F * PIXEL,
                    7.0F * PIXEL,
                    9.0F * PIXEL,
                    ACCENT
            );
        }

        float capX0 = leftHalf ? 0.75F * PIXEL : 12.25F * PIXEL;
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                capX0,
                2.25F * PIXEL,
                -0.25F * PIXEL,
                capX0 + 3.0F * PIXEL,
                5.25F * PIXEL,
                2.75F * PIXEL,
                leftHalf ? CAP_LEFT_BACK : CAP_RIGHT_BACK
        );
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                capX0,
                2.25F * PIXEL,
                11.25F * PIXEL,
                capX0 + 3.0F * PIXEL,
                5.25F * PIXEL,
                14.25F * PIXEL,
                leftHalf ? CAP_LEFT_FRONT : CAP_RIGHT_FRONT
        );
    }

    private static void renderBox(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            FaceUv allFaces
    ) {
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                x0,
                y0,
                z0,
                x1,
                y1,
                z1,
                allFaces,
                allFaces,
                allFaces,
                allFaces,
                allFaces,
                allFaces
        );
    }

    private static void renderBox(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            BoxUvs uvs
    ) {
        renderBox(
                poseStack,
                consumer,
                packedLight,
                packedOverlay,
                x0,
                y0,
                z0,
                x1,
                y1,
                z1,
                uvs.front,
                uvs.back,
                uvs.left,
                uvs.right,
                uvs.top,
                uvs.bottom
        );
    }

    private static void renderBox(
            PoseStack poseStack,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            FaceUv front,
            FaceUv back,
            FaceUv left,
            FaceUv right,
            FaceUv top,
            FaceUv bottom
    ) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        quad(consumer, matrix, normal, packedLight, packedOverlay, front,
                x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, 0, 0, 1);
        quad(consumer, matrix, normal, packedLight, packedOverlay, back,
                x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0, 0, 0, -1);
        quad(consumer, matrix, normal, packedLight, packedOverlay, left,
                x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, -1, 0, 0);
        quad(consumer, matrix, normal, packedLight, packedOverlay, right,
                x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1, 1, 0, 0);
        quad(consumer, matrix, normal, packedLight, packedOverlay, top,
                x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, 0, 1, 0);
        quad(consumer, matrix, normal, packedLight, packedOverlay, bottom,
                x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, 0, -1, 0);
    }

    private static void quad(
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normal,
            int packedLight,
            int packedOverlay,
            FaceUv uv,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2,
            float x3,
            float y3,
            float z3,
            float normalX,
            float normalY,
            float normalZ
    ) {
        vertex(consumer, matrix, normal, packedLight, packedOverlay, x0, y0, z0, uv.u0, uv.v1, normalX, normalY, normalZ);
        vertex(consumer, matrix, normal, packedLight, packedOverlay, x1, y1, z1, uv.u1, uv.v1, normalX, normalY, normalZ);
        vertex(consumer, matrix, normal, packedLight, packedOverlay, x2, y2, z2, uv.u1, uv.v0, normalX, normalY, normalZ);
        vertex(consumer, matrix, normal, packedLight, packedOverlay, x3, y3, z3, uv.u0, uv.v0, normalX, normalY, normalZ);
    }

    private static void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normal,
            int packedLight,
            int packedOverlay,
            float x,
            float y,
            float z,
            float u,
            float v,
            float normalX,
            float normalY,
            float normalZ
    ) {
        consumer.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();
    }

    private static FaceUv uv(int u0, int v0, int u1, int v1) {
        return new FaceUv(u0 / TEXTURE_SIZE, v0 / TEXTURE_SIZE, u1 / TEXTURE_SIZE, v1 / TEXTURE_SIZE);
    }

    private static FaceUv halfUv(FaceUv full, int pixelOffset) {
        float uOffset = pixelOffset / TEXTURE_SIZE;
        return new FaceUv(full.u0 + uOffset, full.v0, full.u0 + uOffset + 16.0F / TEXTURE_SIZE, full.v1);
    }

    private static BoxUvs boxUvs(int u, int v, int width, int height, int depth) {
        return new BoxUvs(
                uv(u + depth * 2 + width, v + depth, u + depth * 2 + width * 2, v + depth + height),
                uv(u + depth, v + depth, u + depth + width, v + depth + height),
                uv(u, v + depth, u + depth, v + depth + height),
                uv(u + depth + width, v + depth, u + depth * 2 + width, v + depth + height),
                uv(u + depth, v, u + depth + width, v + depth),
                uv(u + depth + width, v, u + depth + width * 2, v + depth)
        );
    }

    private record FaceUv(float u0, float v0, float u1, float v1) {
    }

    private record BoxUvs(
            FaceUv front,
            FaceUv back,
            FaceUv left,
            FaceUv right,
            FaceUv top,
            FaceUv bottom
    ) {
    }
}
