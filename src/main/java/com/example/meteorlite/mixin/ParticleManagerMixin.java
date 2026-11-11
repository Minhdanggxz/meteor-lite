package com.example.meteorlite.mixin;

import com.example.meteorlite.NoRender;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$breakParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().blockBreakParticles) ci.cancel();
    }

    @Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$breakingParticles(BlockPos pos, Direction side, CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().blockBreakParticles) ci.cancel();
    }

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$particle(ParticleEffect effect, double x, double y, double z, double vx, double vy, double vz,
                                     CallbackInfoReturnable<Particle> cir) {
        if (NoRender.hidesParticle(Registries.PARTICLE_TYPE.getId(effect.getType()).toString())) cir.setReturnValue(null);
    }
}
