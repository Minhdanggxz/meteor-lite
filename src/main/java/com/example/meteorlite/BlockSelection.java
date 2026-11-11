package com.example.meteorlite;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;

public class BlockSelection {
    public static void render(Draw.Batch b, MinecraftClient mc) {
        Config.SelectionCfg c = Config.I.blockSelection;
        if (!c.enabled || mc.world == null || mc.player == null) return;

        HitResult hit = mc.crosshairTarget;
        if (!(hit instanceof BlockHitResult bhr) || hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = bhr.getBlockPos();
        BlockState state = mc.world.getBlockState(pos);
        if (state.isAir()) return;

        VoxelShape shape = state.getOutlineShape(mc.world, pos, ShapeContext.of(mc.player));
        if (shape.isEmpty()) return;

        // ẩn khi camera nằm trong block (ví dụ Freecam / bị kẹt trong block)
        if (c.hideInsideAnimation && shape.getBoundingBox().offset(pos).contains(mc.gameRenderer.getCamera().getPos())) return;

        if (c.advanced) {
            List<Box> boxes = shape.getBoundingBoxes();
            for (Box bx : boxes) b.box(bx.offset(pos).expand(0.002), c.sideColor, c.lineColor, c.shapeMode);
        } else {
            Box bx = shape.getBoundingBox().offset(pos).expand(0.002);
            if (c.singleSide) b.face(bx, bhr.getSide(), c.sideColor, c.lineColor, c.shapeMode);
            else b.box(bx, c.sideColor, c.lineColor, c.shapeMode);
        }
    }
}
