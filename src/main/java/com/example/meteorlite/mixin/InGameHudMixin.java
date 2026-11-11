package com.example.meteorlite.mixin;

import com.example.meteorlite.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$portal(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().portalOverlay) ci.cancel();
    }

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$spyglass(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().spyglassOverlay) ci.cancel();
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$vignette(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().vignette) ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$crosshair(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().crosshair) ci.cancel();
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$scoreboard(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().scoreboard) ci.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$heldName(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().heldItemName) ci.cancel();
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$overlay(DrawContext ctx, Identifier tex, float opacity, CallbackInfo ci) {
        if (!NoRender.on()) return;
        String path = tex.getPath();
        if (NoRender.c().pumpkinOverlay && path.contains("pumpkinblur")) ci.cancel();
        else if (NoRender.c().powderedSnowOverlay && path.contains("powder_snow_outline")) ci.cancel();
    }
}
