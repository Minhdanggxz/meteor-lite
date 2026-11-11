package com.example.meteorlite;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigUi {
    private static ConfigEntryBuilder E;

    public static Screen create(Screen parent) {
        ConfigBuilder b = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Meteor Lite"))
                .setSavingRunnable(() -> { Config.save(); NoRender.rebuild(); });
        E = b.entryBuilder();

        // ---------- Nuker ----------
        Config.NukerCfg n = Config.I.nuker;
        ConfigCategory c = b.getOrCreateCategory(Text.literal("Nuker"));
        bool(c, "active", n.enabled, false, v -> n.enabled = v);
        en(c, "shape", Config.NukerShape.class, n.shape, Config.NukerShape.SPHERE, v -> n.shape = v);
        en(c, "mode", Config.NukerMode.class, n.mode, Config.NukerMode.ALL, v -> n.mode = v);
        dbl(c, "range", n.range, 4.5, v -> n.range = v);
        integer(c, "up", n.up, 1, v -> n.up = v);
        integer(c, "down", n.down, 1, v -> n.down = v);
        integer(c, "left", n.left, 1, v -> n.left = v);
        integer(c, "right", n.right, 1, v -> n.right = v);
        integer(c, "forward", n.forward, 1, v -> n.forward = v);
        integer(c, "back", n.back, 1, v -> n.back = v);
        integer(c, "delay", n.delay, 0, v -> n.delay = v);
        integer(c, "max-blocks-per-tick", n.maxBlocksPerTick, 1, v -> n.maxBlocksPerTick = v);
        en(c, "sort-mode", Config.SortMode.class, n.sortMode, Config.SortMode.CLOSEST, v -> n.sortMode = v);
        bool(c, "swing-hand", n.swingHand, true, v -> n.swingHand = v);
        bool(c, "packet-mine", n.packetMine, false, v -> n.packetMine = v);
        bool(c, "rotate", n.rotate, true, v -> n.rotate = v);
        en(c, "list-mode", Config.ListMode.class, n.listMode, Config.ListMode.BLACKLIST, v -> n.listMode = v);
        list(c, "whitelist (vd: minecraft:stone)", n.whitelist, v -> n.whitelist = v);
        list(c, "blacklist", n.blacklist, v -> n.blacklist = v);
        bool(c, "bounding-box", n.enableRenderBounding, true, v -> n.enableRenderBounding = v);
        en(c, "shape-mode (box)", Config.ShapeMode.class, n.shapeModeBox, Config.ShapeMode.LINES, v -> n.shapeModeBox = v);
        color(c, "side-color (box)", n.sideColor, Config.argb(0, 255, 255, 255), v -> n.sideColor = v);
        color(c, "line-color (box)", n.lineColor, Config.argb(255, 255, 255, 255), v -> n.lineColor = v);
        bool(c, "broken-blocks", n.enableRenderBreaking, true, v -> n.enableRenderBreaking = v);
        en(c, "shape-mode (broken)", Config.ShapeMode.class, n.shapeModeBreak, Config.ShapeMode.BOTH, v -> n.shapeModeBreak = v);
        color(c, "side-color (broken)", n.sideColorBreak, Config.argb(80, 255, 0, 0), v -> n.sideColorBreak = v);
        color(c, "line-color (broken)", n.lineColorBreak, Config.argb(255, 255, 0, 0), v -> n.lineColorBreak = v);

        // ---------- NoRender ----------
        Config.NoRenderCfg r = Config.I.noRender;
        c = b.getOrCreateCategory(Text.literal("NoRender"));
        bool(c, "active", r.enabled, false, v -> r.enabled = v);
        bool(c, "portal-overlay", r.portalOverlay, true, v -> r.portalOverlay = v);
        bool(c, "spyglass-overlay", r.spyglassOverlay, true, v -> r.spyglassOverlay = v);
        bool(c, "pumpkin-overlay", r.pumpkinOverlay, true, v -> r.pumpkinOverlay = v);
        bool(c, "powdered-snow-overlay", r.powderedSnowOverlay, true, v -> r.powderedSnowOverlay = v);
        bool(c, "fire-overlay", r.fireOverlay, true, v -> r.fireOverlay = v);
        bool(c, "liquid-overlay", r.liquidOverlay, true, v -> r.liquidOverlay = v);
        bool(c, "in-wall-overlay", r.inWallOverlay, true, v -> r.inWallOverlay = v);
        bool(c, "vignette", r.vignette, true, v -> r.vignette = v);
        bool(c, "crosshair", r.crosshair, false, v -> r.crosshair = v);
        bool(c, "boss-bar", r.bossBar, true, v -> r.bossBar = v);
        bool(c, "scoreboard", r.scoreboard, true, v -> r.scoreboard = v);
        bool(c, "held-item-name", r.heldItemName, false, v -> r.heldItemName = v);
        bool(c, "totem-animation", r.totemAnimation, true, v -> r.totemAnimation = v);
        bool(c, "hurt-cam", r.hurtCam, false, v -> r.hurtCam = v);
        bool(c, "weather", r.weather, true, v -> r.weather = v);
        bool(c, "block-break-particles", r.blockBreakParticles, true, v -> r.blockBreakParticles = v);
        bool(c, "beacon-beams", r.beaconBeams, true, v -> r.beaconBeams = v);
        bool(c, "enchantment-table-book", r.enchantmentTableBook, true, v -> r.enchantmentTableBook = v);
        bool(c, "falling-blocks", r.fallingBlocks, true, v -> r.fallingBlocks = v);
        list(c, "particles (vd: minecraft:flame)", r.particles, v -> r.particles = v);
        list(c, "entities (vd: minecraft:item)", r.entities, v -> r.entities = v);
        list(c, "block-entities (vd: minecraft:chest)", r.blockEntities, v -> r.blockEntities = v);

        // ---------- Block Selection ----------
        Config.SelectionCfg s = Config.I.blockSelection;
        c = b.getOrCreateCategory(Text.literal("Block Selection"));
        bool(c, "active", s.enabled, false, v -> s.enabled = v);
        bool(c, "advanced", s.advanced, false, v -> s.advanced = v);
        bool(c, "single-side", s.singleSide, false, v -> s.singleSide = v);
        en(c, "shape-mode", Config.ShapeMode.class, s.shapeMode, Config.ShapeMode.LINES, v -> s.shapeMode = v);
        color(c, "side-color", s.sideColor, Config.argb(50, 255, 255, 255), v -> s.sideColor = v);
        color(c, "line-color", s.lineColor, Config.argb(255, 255, 255, 255), v -> s.lineColor = v);
        bool(c, "hide-when-inside-animation", s.hideInsideAnimation, true, v -> s.hideInsideAnimation = v);

        return b.build();
    }

    private static void bool(ConfigCategory c, String name, boolean val, boolean def, Consumer<Boolean> set) {
        c.addEntry(E.startBooleanToggle(Text.literal(name), val).setDefaultValue(def).setSaveConsumer(set).build());
    }

    private static void dbl(ConfigCategory c, String name, double val, double def, Consumer<Double> set) {
        c.addEntry(E.startDoubleField(Text.literal(name), val).setDefaultValue(def).setMin(0).setSaveConsumer(set).build());
    }

    private static void integer(ConfigCategory c, String name, int val, int def, Consumer<Integer> set) {
        c.addEntry(E.startIntField(Text.literal(name), val).setDefaultValue(def).setMin(0).setSaveConsumer(set).build());
    }

    private static <T extends Enum<?>> void en(ConfigCategory c, String name, Class<T> cls, T val, T def, Consumer<T> set) {
        c.addEntry(E.startEnumSelector(Text.literal(name), cls, val).setDefaultValue(def).setSaveConsumer(set).build());
    }

    private static void color(ConfigCategory c, String name, int val, int def, Consumer<Integer> set) {
        c.addEntry(E.startAlphaColorField(Text.literal(name), val).setDefaultValue(def).setSaveConsumer(set).build());
    }

    private static void list(ConfigCategory c, String name, List<String> val, Consumer<List<String>> set) {
        c.addEntry(E.startStrList(Text.literal(name), new ArrayList<>(val)).setDefaultValue(new ArrayList<String>()).setSaveConsumer(set).build());
    }
}
