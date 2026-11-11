package com.example.meteorlite.mixin;

import com.example.meteorlite.NoRender;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$bossbar(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().bossBar) ci.cancel();
    }
}
