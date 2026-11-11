package com.example.meteorlite;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MeteorLite implements ClientModInitializer {
    private static KeyBinding kNuker, kNoRender, kSelection, kGui;

    @Override
    public void onInitializeClient() {
        Config.load();

        kNuker = key("key.meteorlite.nuker", GLFW.GLFW_KEY_N);
        kNoRender = key("key.meteorlite.norender", GLFW.GLFW_KEY_B);
        kSelection = key("key.meteorlite.blockselection", GLFW.GLFW_KEY_V);
        kGui = key("key.meteorlite.settings", GLFW.GLFW_KEY_RIGHT_SHIFT);

        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            while (kNuker.wasPressed()) {
                Config.I.nuker.enabled = !Config.I.nuker.enabled;
                msg(mc, "Nuker", Config.I.nuker.enabled);
            }
            while (kNoRender.wasPressed()) {
                Config.I.noRender.enabled = !Config.I.noRender.enabled;
                msg(mc, "NoRender", Config.I.noRender.enabled);
            }
            while (kSelection.wasPressed()) {
                Config.I.blockSelection.enabled = !Config.I.blockSelection.enabled;
                msg(mc, "Block Selection", Config.I.blockSelection.enabled);
            }
            while (kGui.wasPressed()) mc.setScreen(ConfigUi.create(mc.currentScreen));
            Nuker.tick(mc);
        });

        // tắt viền block mặc định khi Block Selection bật
        WorldRenderEvents.BLOCK_OUTLINE.register((ctx, outline) -> !Config.I.blockSelection.enabled);
        WorldRenderEvents.LAST.register(MeteorLite::render);
    }

    private static KeyBinding key(String id, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(id, InputUtil.Type.KEYSYM, code, "category.meteorlite"));
    }

    private static void msg(MinecraftClient mc, String name, boolean on) {
        Config.save();
        if (mc.player != null)
            mc.player.sendMessage(Text.literal("§b" + name + " " + (on ? "§aON" : "§cOFF")), true);
    }

    private static void render(WorldRenderContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        Draw.Batch b = new Draw.Batch();
        Nuker.render(b, mc);
        BlockSelection.render(b, mc);
        if (b.isEmpty()) return;
        MatrixStack ms = ctx.matrixStack();
        if (ms == null) {
            ms = new MatrixStack();
            ms.multiplyPositionMatrix(ctx.positionMatrix());
        }
        RenderSystem.enableDepthTest();
        b.flush(ms.peek().getPositionMatrix(), ctx.camera().getPos());
    }
}
