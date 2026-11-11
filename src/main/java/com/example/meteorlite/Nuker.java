package com.example.meteorlite;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class Nuker {
    private static int timer = 0;
    private static final List<BlockPos> targets = new ArrayList<>();

    public static void tick(MinecraftClient mc) {
        Config.NukerCfg c = Config.I.nuker;
        targets.clear();
        if (!c.enabled || mc.player == null || mc.world == null || mc.interactionManager == null || mc.getNetworkHandler() == null) return;

        if (timer < c.delay) { timer++; return; }
        timer = 0;

        ClientPlayerEntity p = mc.player;
        ClientWorld w = mc.world;
        Vec3d eye = p.getEyePos();
        BlockPos pb = p.getBlockPos();
        Direction f = p.getHorizontalFacing();
        Direction rt = f.rotateYClockwise();

        int ext;
        switch (c.shape) {
            case SPHERE -> ext = (int) Math.ceil(c.range) + 1;
            case UNIFORM_CUBE -> ext = (int) Math.round(c.range);
            default -> ext = Math.max(Math.max(Math.max(c.up, c.down), Math.max(c.left, c.right)), Math.max(c.forward, c.back));
        }
        double r2 = c.range * c.range;

        List<BlockPos> list = new ArrayList<>();
        for (int dx = -ext; dx <= ext; dx++) {
            for (int dy = -ext; dy <= ext; dy++) {
                for (int dz = -ext; dz <= ext; dz++) {
                    BlockPos pos = pb.add(dx, dy, dz);
                    if (c.shape == Config.NukerShape.SPHERE) {
                        if (Vec3d.ofCenter(pos).squaredDistanceTo(eye) > r2) continue;
                    } else if (c.shape == Config.NukerShape.CUBE) {
                        int fwd = dx * f.getOffsetX() + dz * f.getOffsetZ();
                        int rgt = dx * rt.getOffsetX() + dz * rt.getOffsetZ();
                        if (fwd > c.forward || fwd < -c.back || rgt > c.right || rgt < -c.left || dy > c.up || dy < -c.down) continue;
                    }
                    BlockState s = w.getBlockState(pos);
                    if (!canBreak(p, w, pos, s)) continue;
                    if (c.mode == Config.NukerMode.FLATTEN && pos.getY() < p.getBlockY()) continue;
                    if (c.mode == Config.NukerMode.SMASH && s.getHardness(w, pos) != 0) continue;
                    String id = Registries.BLOCK.getId(s.getBlock()).toString();
                    if (c.listMode == Config.ListMode.WHITELIST) {
                        if (!contains(c.whitelist, id)) continue;
                    } else if (contains(c.blacklist, id)) continue;
                    list.add(pos);
                }
            }
        }
        if (list.isEmpty()) return;

        switch (c.sortMode) {
            case CLOSEST -> list.sort(Comparator.comparingDouble(b -> Vec3d.ofCenter(b).squaredDistanceTo(eye)));
            case FURTHEST -> list.sort(Comparator.comparingDouble((BlockPos b) -> Vec3d.ofCenter(b).squaredDistanceTo(eye)).reversed());
            case TOP_DOWN -> list.sort(Comparator.comparingInt((BlockPos b) -> b.getY()).reversed());
            default -> { }
        }

        int count = 0;
        for (BlockPos pos : list) {
            if (count >= c.maxBlocksPerTick) break;
            BlockState s = w.getBlockState(pos);
            boolean instant = p.isCreative() || s.getHardness(w, pos) == 0;
            Vec3d center = Vec3d.ofCenter(pos);
            Direction side = Direction.getFacing(eye.x - center.x, eye.y - center.y, eye.z - center.z);

            if (c.rotate) {
                double dx = center.x - eye.x, dy = center.y - eye.y, dz = center.z - eye.z;
                double h = Math.sqrt(dx * dx + dz * dz);
                float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
                float pitch = (float) -Math.toDegrees(Math.atan2(dy, h));
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, p.isOnGround(), p.horizontalCollision));
            }

            if (c.packetMine) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, side));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, side));
            } else {
                mc.interactionManager.updateBlockBreakingProgress(pos, side);
            }

            if (c.swingHand) p.swingHand(Hand.MAIN_HAND);
            else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

            targets.add(pos);
            if (!instant) break; // block không phá tức thì: chỉ xử lý 1 block mỗi tick
            count++;
        }
    }

    private static boolean canBreak(ClientPlayerEntity p, ClientWorld w, BlockPos pos, BlockState s) {
        if (s.isAir()) return false;
        if (!p.isCreative() && s.getHardness(w, pos) < 0) return false;
        return !s.getOutlineShape(w, pos).isEmpty();
    }

    private static boolean contains(List<String> l, String id) {
        for (String s : l) {
            String t = s.trim().toLowerCase(Locale.ROOT);
            if (t.isEmpty()) continue;
            if (!t.contains(":")) t = "minecraft:" + t;
            if (t.equals(id)) return true;
        }
        return false;
    }

    public static void render(Draw.Batch b, MinecraftClient mc) {
        Config.NukerCfg c = Config.I.nuker;
        if (!c.enabled || mc.player == null) return;

        if (c.enableRenderBounding && c.shape != Config.NukerShape.SPHERE) {
            BlockPos pb = mc.player.getBlockPos();
            Box box;
            if (c.shape == Config.NukerShape.UNIFORM_CUBE) {
                int r = (int) Math.round(c.range);
                box = new Box(pb.getX() - r, pb.getY() - r, pb.getZ() - r, pb.getX() + r + 1, pb.getY() + r + 1, pb.getZ() + r + 1);
            } else {
                Direction f = mc.player.getHorizontalFacing();
                Direction rt = f.rotateYClockwise();
                int ax = pb.getX() + f.getOffsetX() * c.forward + rt.getOffsetX() * c.right;
                int az = pb.getZ() + f.getOffsetZ() * c.forward + rt.getOffsetZ() * c.right;
                int bx = pb.getX() - f.getOffsetX() * c.back - rt.getOffsetX() * c.left;
                int bz = pb.getZ() - f.getOffsetZ() * c.back - rt.getOffsetZ() * c.left;
                box = new Box(Math.min(ax, bx), pb.getY() - c.down, Math.min(az, bz),
                        Math.max(ax, bx) + 1, pb.getY() + c.up + 1, Math.max(az, bz) + 1);
            }
            b.box(box, c.sideColor, c.lineColor, c.shapeModeBox);
        }

        if (c.enableRenderBreaking) {
            for (BlockPos pos : targets)
                b.box(new Box(pos).expand(0.002), c.sideColorBreak, c.lineColorBreak, c.shapeModeBreak);
        }
    }
}
