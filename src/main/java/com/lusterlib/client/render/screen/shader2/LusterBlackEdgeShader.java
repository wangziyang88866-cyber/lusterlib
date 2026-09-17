package com.lusterlib.client.render.screen.shader2;

import com.lusterlib.LusterLib;
import com.lusterlib.api.render.screen.shader2.LusterBlackEdgeStyle;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.io.UncheckedIOException;

/** Captures the completed frame and replaces its colors with glowing monochrome contours. */
public final class LusterBlackEdgeShader {

    private static final ResourceLocation SHADER_LOCATION =
            ResourceLocation.fromNamespaceAndPath(LusterLib.MODID, "black_edge");

    private static ShaderInstance shader;
    private static TextureTarget snapshot;

    private LusterBlackEdgeShader() {
    }

    public static void register(RegisterShadersEvent event) {
        try {
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), SHADER_LOCATION, DefaultVertexFormat.POSITION_TEX),
                    loadedShader -> shader = loadedShader
            );
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to load LusterLib black-edge shader", exception);
        }
    }

    public static void draw(GuiGraphics graphics, float intensity) {
        draw(graphics, intensity, null);
    }

    public static void draw(GuiGraphics graphics, float intensity, LusterBlackEdgeStyle style) {
        if (!Float.isFinite(intensity)) {
            return;
        }
        float amount = Math.min(Math.max(intensity, 0.0F), 1.0F);
        ShaderInstance activeShader = shader;
        if (amount <= 0.0F || activeShader == null) {
            return;
        }

        RenderSystem.assertOnRenderThread();
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (main.width <= 0 || main.height <= 0 || graphics.guiWidth() <= 0 || graphics.guiHeight() <= 0) {
            return;
        }

        graphics.flush();
        ensureSnapshot(main);

        ShaderInstance previousShader = RenderSystem.getShader();
        int previousTexture = RenderSystem.getShaderTexture(0);
        float[] previousColor = RenderSystem.getShaderColor().clone();
        boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean depthWrite = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

        try {
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, main.frameBufferId);
            GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, snapshot.frameBufferId);
            GlStateManager._glBlitFrameBuffer(
                    0, 0, main.width, main.height,
                    0, 0, snapshot.width, snapshot.height,
                    GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST
            );
            main.bindWrite(true);

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableBlend();
            RenderSystem.disableCull();
            RenderSystem.setShader(() -> activeShader);
            RenderSystem.setShaderTexture(0, snapshot.getColorTextureId());
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            activeShader.setSampler("Sampler0", snapshot);
            activeShader.safeGetUniform("Intensity").set(amount);
            activeShader.safeGetUniform("InSize").set((float) main.width, (float) main.height);
            int edgeColor = style == null ? 0xFFFFFF : style.getEdgeColor();
            int interiorColor = style == null ? 0x000000 : style.getInteriorColor();
            setColorUniform(activeShader, "EdgeColor", edgeColor);
            setColorUniform(activeShader, "InteriorColor", interiorColor);

            drawFullscreenQuad(graphics);
        } finally {
            main.bindWrite(true);
            RenderSystem.setShaderTexture(0, previousTexture);
            RenderSystem.setShaderColor(previousColor[0], previousColor[1], previousColor[2], previousColor[3]);
            if (previousShader != null) {
                RenderSystem.setShader(() -> previousShader);
            }
            RenderSystem.depthMask(depthWrite);
            if (depth) RenderSystem.enableDepthTest(); else RenderSystem.disableDepthTest();
            if (blend) RenderSystem.enableBlend(); else RenderSystem.disableBlend();
            if (cull) RenderSystem.enableCull(); else RenderSystem.disableCull();
        }
    }

    public static void releaseResources() {
        RenderSystem.assertOnRenderThread();
        if (snapshot != null) {
            snapshot.destroyBuffers();
            snapshot = null;
        }
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    private static void ensureSnapshot(RenderTarget main) {
        if (snapshot == null) {
            snapshot = new TextureTarget(main.width, main.height, false, Minecraft.ON_OSX);
            snapshot.setFilterMode(GL11.GL_LINEAR);
        } else if (snapshot.width != main.width || snapshot.height != main.height) {
            snapshot.resize(main.width, main.height, Minecraft.ON_OSX);
            snapshot.setFilterMode(GL11.GL_LINEAR);
        }
    }

    private static void setColorUniform(ShaderInstance shader, String uniformName, int color) {
        shader.safeGetUniform(uniformName).set(
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F
        );
    }

    private static void drawFullscreenQuad(GuiGraphics graphics) {
        float width = graphics.guiWidth();
        float height = graphics.guiHeight();
        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder vertices = RenderSystem.renderThreadTesselator()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        vertices.addVertex(matrix, 0.0F, height, 0.0F).setUv(0.0F, 0.0F);
        vertices.addVertex(matrix, width, height, 0.0F).setUv(1.0F, 0.0F);
        vertices.addVertex(matrix, width, 0.0F, 0.0F).setUv(1.0F, 1.0F);
        vertices.addVertex(matrix, 0.0F, 0.0F, 0.0F).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(vertices.buildOrThrow());
    }
}
