package com.example.meteorlite.mixin;

import com.example.meteorlite.NoRender;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private static void meteorlite$fire(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().fireOverlay) ci.cancel();
    }

    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private static void meteorlite$wall(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().inWallOverlay) ci.cancel();
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private static void meteorlite$liquid(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().liquidOverlay) ci.cancel();
    }
}
