package com.example.meteorlite.mixin;

import com.example.meteorlite.NoRender;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true, require = 0)
    private void meteorlite$shouldRender(Entity entity, Frustum frustum, double x, double y, double z,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (!NoRender.on()) return;
        if (NoRender.c().fallingBlocks && entity.getType() == EntityType.FALLING_BLOCK) {
            cir.setReturnValue(false);
            return;
        }
        if (NoRender.hidesEntity(Registries.ENTITY_TYPE.getId(entity.getType()).toString())) cir.setReturnValue(false);
    }
}
