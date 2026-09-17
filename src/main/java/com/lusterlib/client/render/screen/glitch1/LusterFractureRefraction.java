package com.lusterlib.client.render.screen.glitch1;

import com.lusterlib.api.render.screen.glitch1.LusterScreenFractureStyle;
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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/** Reuses the tested screen-copy path with the fracture mesh as its destination mask. */
final class LusterFractureRefraction {

    private static TextureTarget snapshot;

    private LusterFractureRefraction() {
    }

    static void draw(GuiGraphics graphics, LusterFractureMesh.Mesh mesh, LusterScreenFractureStyle style, float amount) {
        if (style.getRefractionOffset() == 0.0F && style.getFragmentRotation() == 0.0F
                || GameRenderer.getPositionTexShader() == null) {
            return;
        }
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (main.width <= 0 || main.height <= 0) {
            return;
        }

        ShaderInstance previousShader = RenderSystem.getShader();
        int previousTexture = RenderSystem.getShaderTexture(0);
        float[] previousColor = RenderSystem.getShaderColor().clone();
        boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean depthWrite = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        try {
            if (snapshot == null) {
                snapshot = new TextureTarget(main.width, main.height, false, Minecraft.ON_OSX);
                snapshot.setFilterMode(GL11.GL_LINEAR);
            } else if (snapshot.width != main.width || snapshot.height != main.height) {
                snapshot.resize(main.width, main.height, Minecraft.ON_OSX);
                snapshot.setFilterMode(GL11.GL_LINEAR);
            }
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, main.frameBufferId);
            GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, snapshot.frameBufferId);
            GlStateManager._glBlitFrameBuffer(0, 0, main.width, main.height,
                    0, 0, snapshot.width, snapshot.height, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
            main.bindWrite(true);

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableBlend();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, snapshot.getColorTextureId());
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            Matrix4f matrix = graphics.pose().last().pose();
            BufferBuilder vertices = RenderSystem.renderThreadTesselator()
                    .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            for (LusterFractureMesh.Cell cell : mesh.cells()) {
                LusterFractureMesh.Sampling sampling = LusterFractureMesh.sampling(cell, graphics.guiWidth(), graphics.guiHeight(),
                        style.getRefractionOffset() * amount, style.getFragmentRotation() * amount);
                for (int index = 0; index < cell.x().length; index++) {
                    int next = (index + 1) % cell.x().length;
                    // The exact same fan vertices and winding as the glass facet overlay.
                    vertex(vertices, matrix, cell.centerX(), cell.centerY(), sampling);
                    vertex(vertices, matrix, cell.x()[next], cell.y()[next], sampling);
                    vertex(vertices, matrix, cell.x()[index], cell.y()[index], sampling);
                    vertex(vertices, matrix, cell.x()[index], cell.y()[index], sampling);
                }
            }
            BufferUploader.drawWithShader(vertices.buildOrThrow());
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

    private static void vertex(BufferBuilder vertices, Matrix4f matrix, float x, float y, LusterFractureMesh.Sampling sampling) {
        vertices.addVertex(matrix, x, y, 0.0F).setUv(sampling.u(x, y), sampling.v(x, y));
    }

    static void releaseResources() {
        RenderSystem.assertOnRenderThread();
        if (snapshot != null) {
            snapshot.destroyBuffers();
            snapshot = null;
            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        }
    }
}
