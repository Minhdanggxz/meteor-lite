package com.example.meteorlite;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class Draw {
    public static final class Batch {
        private final List<double[]> quads = new ArrayList<>(); // 12 toạ độ + argb
        private final List<double[]> lines = new ArrayList<>(); // 6 toạ độ + argb

        public boolean isEmpty() { return quads.isEmpty() && lines.isEmpty(); }

        private void quad(int argb, double[] a, double[] b, double[] c, double[] d) {
            quads.add(new double[]{a[0], a[1], a[2], b[0], b[1], b[2], c[0], c[1], c[2], d[0], d[1], d[2], argb});
        }

        private void line(int argb, double[] a, double[] b) {
            lines.add(new double[]{a[0], a[1], a[2], b[0], b[1], b[2], argb});
        }

        private static double[][] corners(Box b) {
            return new double[][]{
                    {b.minX, b.minY, b.minZ}, {b.maxX, b.minY, b.minZ}, {b.maxX, b.minY, b.maxZ}, {b.minX, b.minY, b.maxZ},
                    {b.minX, b.maxY, b.minZ}, {b.maxX, b.maxY, b.minZ}, {b.maxX, b.maxY, b.maxZ}, {b.minX, b.maxY, b.maxZ}
            };
        }

        public void box(Box b, int sideColor, int lineColor, Config.ShapeMode mode) {
            double[][] c = corners(b);
            if (mode.sides()) {
                quad(sideColor, c[0], c[1], c[2], c[3]); // dưới
                quad(sideColor, c[4], c[5], c[6], c[7]); // trên
                quad(sideColor, c[0], c[1], c[5], c[4]); // bắc
                quad(sideColor, c[3], c[2], c[6], c[7]); // nam
                quad(sideColor, c[0], c[3], c[7], c[4]); // tây
                quad(sideColor, c[1], c[2], c[6], c[5]); // đông
            }
            if (mode.lines()) {
                int[][] e = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
                for (int[] i : e) line(lineColor, c[i[0]], c[i[1]]);
            }
        }

        /** Chỉ vẽ một mặt của box. */
        public void face(Box b, Direction d, int sideColor, int lineColor, Config.ShapeMode mode) {
            double[][] c = corners(b);
            int[] idx = switch (d) {
                case DOWN -> new int[]{0, 1, 2, 3};
                case UP -> new int[]{4, 5, 6, 7};
                case NORTH -> new int[]{0, 1, 5, 4};
                case SOUTH -> new int[]{3, 2, 6, 7};
                case WEST -> new int[]{0, 3, 7, 4};
                case EAST -> new int[]{1, 2, 6, 5};
            };
            double[] p0 = c[idx[0]], p1 = c[idx[1]], p2 = c[idx[2]], p3 = c[idx[3]];
            if (mode.sides()) quad(sideColor, p0, p1, p2, p3);
            if (mode.lines()) {
                line(lineColor, p0, p1);
                line(lineColor, p1, p2);
                line(lineColor, p2, p3);
                line(lineColor, p3, p0);
            }
        }

        public void flush(Matrix4f m, Vec3d cam) {
            if (isEmpty()) return;
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();

            if (!quads.isEmpty()) {
                BufferBuilder bb = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                for (double[] q : quads) {
                    int col = (int) q[12];
                    for (int i = 0; i < 4; i++)
                        bb.vertex(m, (float) (q[i * 3] - cam.x), (float) (q[i * 3 + 1] - cam.y), (float) (q[i * 3 + 2] - cam.z)).color(col);
                }
                BufferRenderer.drawWithGlobalProgram(bb.end());
            }
            if (!lines.isEmpty()) {
                RenderSystem.lineWidth(1f);
                BufferBuilder bb = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                for (double[] l : lines) {
                    int col = (int) l[6];
                    bb.vertex(m, (float) (l[0] - cam.x), (float) (l[1] - cam.y), (float) (l[2] - cam.z)).color(col);
                    bb.vertex(m, (float) (l[3] - cam.x), (float) (l[4] - cam.y), (float) (l[5] - cam.z)).color(col);
                }
                BufferRenderer.drawWithGlobalProgram(bb.end());
            }
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }
}
