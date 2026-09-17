package com.lusterlib.client.render.screen.glitch1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

/** Pure geometry: a screen-clipped Voronoi tessellation and its shared seams. */
final class LusterFractureMesh {

    record Cell(float[] x, float[] y, float centerX, float centerY, float reflection,
                float offsetX, float offsetY, float rotation) {
    }

    /** One affine UV transform per cell; all of its triangles share this transform. */
    record Sampling(float centerX, float centerY, float offsetX, float offsetY,
                    float cosine, float sine, float zoom, float width, float height) {
        float u(float x, float y) {
            float dx = x - centerX - offsetX;
            float dy = y - centerY - offsetY;
            return (centerX + (cosine * dx + sine * dy) / zoom) / width;
        }

        float v(float x, float y) {
            float dx = x - centerX - offsetX;
            float dy = y - centerY - offsetY;
            return 1.0F - (centerY + (-sine * dx + cosine * dy) / zoom) / height;
        }
    }

    record Edge(float x1, float y1, float x2, float y2, float normalX, float normalY, float reflection) {
    }

    record Mesh(Cell[] cells, Edge[] edges) {
    }

    private record Point(double x, double y) {
    }

    private record EdgeKey(long x1, long y1, long x2, long y2) {
        static EdgeKey of(Point a, Point b) {
            long ax = Math.round(a.x * 1000.0D);
            long ay = Math.round(a.y * 1000.0D);
            long bx = Math.round(b.x * 1000.0D);
            long by = Math.round(b.y * 1000.0D);
            return ax < bx || ax == bx && ay < by
                    ? new EdgeKey(ax, ay, bx, by) : new EdgeKey(bx, by, ax, ay);
        }
    }

    private LusterFractureMesh() {
    }

    static Mesh create(int width, int height, long seed, int count) {
        SplittableRandom random = new SplittableRandom(seed);
        List<Point> sites = new ArrayList<>(count);
        double spacingSquared = width * (double) height / count * 0.14D;
        for (int index = 0; index < count; index++) {
            Point candidate = null;
            for (int attempt = 0; attempt < 80; attempt++) {
                candidate = new Point(random.nextDouble() * width, random.nextDouble() * height);
                boolean separated = true;
                for (Point site : sites) {
                    double dx = site.x - candidate.x;
                    double dy = site.y - candidate.y;
                    if (dx * dx + dy * dy < spacingSquared) {
                        separated = false;
                        break;
                    }
                }
                if (separated) {
                    break;
                }
            }
            sites.add(candidate);
        }

        List<Cell> cells = new ArrayList<>(count);
        Map<EdgeKey, Edge> edges = new LinkedHashMap<>();
        for (Point site : sites) {
            List<Point> polygon = new ArrayList<>(List.of(
                    new Point(0.0D, 0.0D), new Point(width, 0.0D),
                    new Point(width, height), new Point(0.0D, height)
            ));
            for (Point other : sites) {
                if (other == site) {
                    continue;
                }
                double nx = other.x - site.x;
                double ny = other.y - site.y;
                double limit = (other.x * other.x + other.y * other.y - site.x * site.x - site.y * site.y) * 0.5D;
                polygon = clip(polygon, nx, ny, limit);
                if (polygon.size() < 3) {
                    break;
                }
            }
            if (polygon.size() < 3) {
                continue;
            }

            float[] x = new float[polygon.size()];
            float[] y = new float[polygon.size()];
            float centerX = 0.0F;
            float centerY = 0.0F;
            for (int index = 0; index < polygon.size(); index++) {
                Point a = polygon.get(index);
                Point b = polygon.get((index + 1) % polygon.size());
                x[index] = (float) a.x;
                y[index] = (float) a.y;
                centerX += x[index] / polygon.size();
                centerY += y[index] / polygon.size();
                double dx = b.x - a.x;
                double dy = b.y - a.y;
                double length = Math.hypot(dx, dy);
                if (length < 0.0001D || onScreenBorder(a, b, width, height)) {
                    continue;
                }
                // Canonical endpoints keep the bevel's light direction independent of cell order.
                if (a.x > b.x || a.x == b.x && a.y > b.y) {
                    Point temporary = a;
                    a = b;
                    b = temporary;
                    dx = -dx;
                    dy = -dy;
                }
                float nx = (float) (-dy / length);
                float ny = (float) (dx / length);
                float reflection = 0.35F + 0.65F * Math.abs(nx * -0.6F + ny * -0.8F);
                edges.putIfAbsent(EdgeKey.of(a, b), new Edge(
                        (float) a.x, (float) a.y, (float) b.x, (float) b.y, nx, ny, reflection
                ));
            }
            float reflection = (float) random.nextDouble(0.35D, 1.0D);
            double direction = random.nextDouble() * Math.PI * 2.0D;
            float displacement = (float) random.nextDouble(0.45D, 1.0D);
            float rotation = (float) random.nextDouble(0.35D, 1.0D) * (random.nextBoolean() ? 1.0F : -1.0F);
            cells.add(new Cell(x, y, centerX, centerY, reflection,
                    (float) Math.cos(direction) * displacement, (float) Math.sin(direction) * displacement, rotation));
        }
        return new Mesh(cells.toArray(Cell[]::new), edges.values().toArray(Edge[]::new));
    }

    static Sampling sampling(Cell cell, int width, int height, float displacement, float rotationDegrees) {
        float offsetX = cell.offsetX() * displacement;
        float offsetY = cell.offsetY() * displacement;
        float angle = (float) Math.toRadians(cell.rotation() * rotationDegrees);
        float cosine = (float) Math.cos(angle);
        float sine = (float) Math.sin(angle);
        float zoom = 1.0F;
        // Overscan only as much as this cell needs. UVs stay within the source image,
        // without moving the destination polygon or stretching clamped screen borders.
        for (int index = 0; index < cell.x().length; index++) {
            float dx = cell.x()[index] - cell.centerX() - offsetX;
            float dy = cell.y()[index] - cell.centerY() - offsetY;
            float rx = cosine * dx + sine * dy;
            float ry = -sine * dx + cosine * dy;
            float xSpace = rx < 0.0F ? cell.centerX() : width - cell.centerX();
            float ySpace = ry < 0.0F ? cell.centerY() : height - cell.centerY();
            zoom = Math.max(zoom, Math.abs(rx) / Math.max(0.0001F, xSpace));
            zoom = Math.max(zoom, Math.abs(ry) / Math.max(0.0001F, ySpace));
        }
        return new Sampling(cell.centerX(), cell.centerY(), offsetX, offsetY, cosine, sine, zoom, width, height);
    }

    private static List<Point> clip(List<Point> polygon, double nx, double ny, double limit) {
        List<Point> output = new ArrayList<>(polygon.size() + 1);
        if (polygon.isEmpty()) {
            return output;
        }
        Point previous = polygon.getLast();
        double previousDistance = previous.x * nx + previous.y * ny - limit;
        for (Point current : polygon) {
            double distance = current.x * nx + current.y * ny - limit;
            boolean inside = distance <= 0.0D;
            boolean previouslyInside = previousDistance <= 0.0D;
            if (inside != previouslyInside) {
                double fraction = previousDistance / (previousDistance - distance);
                output.add(new Point(
                        previous.x + (current.x - previous.x) * fraction,
                        previous.y + (current.y - previous.y) * fraction
                ));
            }
            if (inside) {
                output.add(current);
            }
            previous = current;
            previousDistance = distance;
        }
        return output;
    }

    private static boolean onScreenBorder(Point a, Point b, int width, int height) {
        double epsilon = 0.00001D;
        return Math.abs(a.x) < epsilon && Math.abs(b.x) < epsilon
                || Math.abs(a.y) < epsilon && Math.abs(b.y) < epsilon
                || Math.abs(a.x - width) < epsilon && Math.abs(b.x - width) < epsilon
                || Math.abs(a.y - height) < epsilon && Math.abs(b.y - height) < epsilon;
    }
}
