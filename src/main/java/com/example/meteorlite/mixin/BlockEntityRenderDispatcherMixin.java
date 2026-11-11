package com.example.meteorlite.mixin;

import com.example.meteorlite.NoRender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$be(BlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumers, CallbackInfo ci) {
        if (!NoRender.on()) return;
        BlockEntityType<?> t = be.getType();
        if (NoRender.c().beaconBeams && t == BlockEntityType.BEACON) { ci.cancel(); return; }
        if (NoRender.c().enchantmentTableBook && t == BlockEntityType.ENCHANTING_TABLE) { ci.cancel(); return; }
        if (NoRender.hidesBlockEntity(Registries.BLOCK_ENTITY_TYPE.getId(t).toString())) ci.cancel();
    }
}
