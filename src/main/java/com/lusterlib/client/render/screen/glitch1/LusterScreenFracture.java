package com.lusterlib.client.render.screen.glitch1;

import com.lusterlib.api.render.screen.glitch1.LusterScreenFractureStyle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

import java.util.LinkedHashMap;
import java.util.Map;

/** Refracted scene fragments, glass facets and seams derived from the same cached mesh. */
public final class LusterScreenFracture {

    private static final LusterScreenFractureStyle DEFAULT_STYLE = new LusterScreenFractureStyle();
    private static final int GLOW_STEPS = 8;
    private static final Map<MeshKey, LusterFractureMesh.Mesh> MESH_CACHE = new LinkedHashMap<>(8, 0.75F, true);

    private record MeshKey(int width, int height, long seed, int count) {
    }

    private LusterScreenFracture() {
    }

    public static void draw(GuiGraphics graphics, float intensity, float elapsedSeconds) {
        draw(graphics, intensity, elapsedSeconds, 0.0F, DEFAULT_STYLE);
    }

    public static void draw(GuiGraphics graphics, float intensity, float elapsedSeconds, float durationSeconds) {
        draw(graphics, intensity, elapsedSeconds, durationSeconds, DEFAULT_STYLE);
    }

    public static void draw(GuiGraphics graphics, float intensity, float elapsedSeconds, LusterScreenFractureStyle style) {
        draw(graphics, intensity, elapsedSeconds, 0.0F, style);
    }

    public static void draw(
            GuiGraphics graphics, float intensity, float elapsedSeconds, float durationSeconds, LusterScreenFractureStyle style
    ) {
        if (!Float.isFinite(intensity) || !Float.isFinite(elapsedSeconds) || !Float.isFinite(durationSeconds)) {
            return;
        }
        float amount = Math.min(Math.max(intensity, 0.0F), 2.0F) * visibility(elapsedSeconds, durationSeconds);
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        if (amount <= 0.0F || width <= 0 || height <= 0) {
            return;
        }

        LusterFractureMesh.Mesh mesh = mesh(width, height, style);
        float antialias = 0.8F / (float) Math.max(1.0D, Minecraft.getInstance().getWindow().getGuiScale());
        float halfWidth = style.getCrackWidth() * 0.5F;
        float glowRadius = Math.min(style.getGlowRadius(), Math.min(width, height) * 0.12F);

        // Capture the completed scene/UI before drawing any glass surface decoration.
        graphics.flush();
        LusterFractureRefraction.draw(graphics, mesh, style, amount);
        Matrix4f matrix = graphics.pose().last().pose();
        VertexConsumer vertices = graphics.bufferSource().getBuffer(RenderType.gui());
        drawFacets(vertices, matrix, mesh, style, amount);
        if (glowRadius > 0.0F) {
            for (LusterFractureMesh.Edge edge : mesh.edges()) {
                drawGlow(vertices, matrix, edge, glowRadius, style.getGlowColor(), amount * edge.reflection() * 0.42F);
            }
        }
        for (LusterFractureMesh.Edge edge : mesh.edges()) {
            // Subpixel dark seam, with a narrow reflected bevel on the exact same edge.
            drawFineLine(vertices, matrix, edge, 0.0F, halfWidth, antialias, style.getCrackColor(), amount);
            float bevelOffset = halfWidth + antialias * 0.35F;
            drawFineLine(vertices, matrix, edge, bevelOffset, Math.max(0.08F, halfWidth * 0.55F),
                    antialias, style.getGlowColor(), amount * edge.reflection() * 0.70F);
        }
        graphics.flush();
    }

    /** 调用方在渲染线程结束播放或退出世界时可释放复用的屏幕副本与网格。 */
    public static void releaseResources() {
        LusterFractureRefraction.releaseResources();
        MESH_CACHE.clear();
    }

    private static LusterFractureMesh.Mesh mesh(int width, int height, LusterScreenFractureStyle style) {
        MeshKey key = new MeshKey(width, height, style.getSeed(), style.getFragmentCount());
        LusterFractureMesh.Mesh mesh = MESH_CACHE.get(key);
        if (mesh == null) {
            mesh = LusterFractureMesh.create(width, height, key.seed(), key.count());
            if (MESH_CACHE.size() >= 8) {
                MESH_CACHE.remove(MESH_CACHE.keySet().iterator().next());
            }
            MESH_CACHE.put(key, mesh);
        }
        return mesh;
    }

    private static void drawFacets(
            VertexConsumer vertices, Matrix4f matrix, LusterFractureMesh.Mesh mesh, LusterScreenFractureStyle style, float amount
    ) {
        float paneOpacity = style.getPaneOpacity() * amount;
        if (paneOpacity <= 0.0F) {
            return;
        }
        for (LusterFractureMesh.Cell cell : mesh.cells()) {
            float radius = 1.0F;
            for (int index = 0; index < cell.x().length; index++) {
                radius = Math.max(radius, (float) Math.hypot(cell.x()[index] - cell.centerX(), cell.y()[index] - cell.centerY()));
            }
            float centerAlpha = paneOpacity * cell.reflection() * 0.13F;
            for (int index = 0; index < cell.x().length; index++) {
                int next = (index + 1) % cell.x().length;
                float alpha1 = facetAlpha(cell, index, radius, paneOpacity);
                float alpha2 = facetAlpha(cell, next, radius, paneOpacity);
                // Reverse the screen-space polygon winding to match the vanilla GUI quad winding.
                vertex(vertices, matrix, cell.centerX(), cell.centerY(), style.getGlowColor(), centerAlpha);
                vertex(vertices, matrix, cell.x()[next], cell.y()[next], style.getGlowColor(), alpha2);
                vertex(vertices, matrix, cell.x()[index], cell.y()[index], style.getGlowColor(), alpha1);
                vertex(vertices, matrix, cell.x()[index], cell.y()[index], style.getGlowColor(), alpha1);
            }
        }
    }

    private static float facetAlpha(LusterFractureMesh.Cell cell, int index, float radius, float opacity) {
        float facing = ((cell.centerX() - cell.x()[index]) * 0.6F + (cell.centerY() - cell.y()[index]) * 0.8F) / radius;
        return opacity * cell.reflection() * (0.20F + Math.max(0.0F, facing) * 0.80F);
    }

    private static void drawGlow(
            VertexConsumer vertices, Matrix4f matrix, LusterFractureMesh.Edge edge, float radius, int color, float opacity
    ) {
        // Vertex interpolation joins these samples continuously; no integer-pixel fill or step edges.
        for (int index = 0; index < GLOW_STEPS; index++) {
            float t1 = index / (float) GLOW_STEPS;
            float t2 = (index + 1.0F) / GLOW_STEPS;
            float alpha1 = opacity * glowFalloff(t1);
            float alpha2 = opacity * glowFalloff(t2);
            ribbon(vertices, matrix, edge, radius * t1, radius * t2, color, alpha1, alpha2);
            ribbon(vertices, matrix, edge, -radius * t2, -radius * t1, color, alpha2, alpha1);
        }
    }

    private static float glowFalloff(float distance) {
        float value = 1.0F - distance * distance;
        return value * value * value;
    }

    private static void drawFineLine(
            VertexConsumer vertices, Matrix4f matrix, LusterFractureMesh.Edge edge,
            float offset, float halfWidth, float antialias, int color, float opacity
    ) {
        float low = offset - halfWidth;
        float high = offset + halfWidth;
        ribbon(vertices, matrix, edge, low - antialias, low, color, 0.0F, opacity);
        ribbon(vertices, matrix, edge, low, high, color, opacity, opacity);
        ribbon(vertices, matrix, edge, high, high + antialias, color, opacity, 0.0F);
    }

    private static void ribbon(
            VertexConsumer vertices, Matrix4f matrix, LusterFractureMesh.Edge edge,
            float low, float high, int color, float lowAlpha, float highAlpha
    ) {
        float lowX = edge.normalX() * low;
        float lowY = edge.normalY() * low;
        float highX = edge.normalX() * high;
        float highY = edge.normalY() * high;
        vertex(vertices, matrix, edge.x1() + lowX, edge.y1() + lowY, color, lowAlpha);
        vertex(vertices, matrix, edge.x1() + highX, edge.y1() + highY, color, highAlpha);
        vertex(vertices, matrix, edge.x2() + highX, edge.y2() + highY, color, highAlpha);
        vertex(vertices, matrix, edge.x2() + lowX, edge.y2() + lowY, color, lowAlpha);
    }

    private static void vertex(VertexConsumer vertices, Matrix4f matrix, float x, float y, int color, float opacity) {
        int alpha = Math.round((color >>> 24) * Math.max(0.0F, Math.min(1.0F, opacity)));
        vertices.addVertex(matrix, x, y, 0.0F).setColor((color & 0x00FFFFFF) | (alpha << 24));
    }

    private static float visibility(float elapsedSeconds, float durationSeconds) {
        if (durationSeconds <= 0.0F) {
            return 1.0F;
        }
        if (elapsedSeconds < 0.0F || elapsedSeconds >= durationSeconds) {
            return 0.0F;
        }
        float fadeStart = durationSeconds * 0.70F;
        if (elapsedSeconds <= fadeStart) {
            return 1.0F;
        }
        float progress = (elapsedSeconds - fadeStart) / (durationSeconds - fadeStart);
        return 1.0F - progress * progress * (3.0F - 2.0F * progress);
    }
}
