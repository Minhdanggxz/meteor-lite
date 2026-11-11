package com.example.meteorlite.mixin;

import com.example.meteorlite.NoRender;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$totem(ItemStack stack, CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().totemAnimation && stack.isOf(Items.TOTEM_OF_UNDYING)) ci.cancel();
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$hurtcam(CallbackInfo ci) {
        if (NoRender.on() && NoRender.c().hurtCam) ci.cancel();
    }
}
