package com.example.meteorlite.mixin;

import com.example.meteorlite.NoRender;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$rain(float delta, CallbackInfoReturnable<Float> cir) {
        Object self = this;
        if (self instanceof ClientWorld && NoRender.on() && NoRender.c().weather) cir.setReturnValue(0f);
    }

    @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$thunder(float delta, CallbackInfoReturnable<Float> cir) {
        Object self = this;
        if (self instanceof ClientWorld && NoRender.on() && NoRender.c().weather) cir.setReturnValue(0f);
    }
}
