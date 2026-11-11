package com.example.meteorlite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public enum ShapeMode {
        LINES("Lines"), SIDES("Sides"), BOTH("Both");
        private final String n;
        ShapeMode(String n) { this.n = n; }
        @Override public String toString() { return n; }
        public boolean lines() { return this != SIDES; }
        public boolean sides() { return this != LINES; }
    }
    public enum NukerShape {
        SPHERE("Sphere"), UNIFORM_CUBE("UniformCube"), CUBE("Cube");
        private final String n;
        NukerShape(String n) { this.n = n; }
        @Override public String toString() { return n; }
    }
    public enum NukerMode {
        ALL("All"), FLATTEN("Flatten"), SMASH("Smash");
        private final String n;
        NukerMode(String n) { this.n = n; }
        @Override public String toString() { return n; }
    }
    public enum SortMode {
        NONE("None"), CLOSEST("Closest"), FURTHEST("Furthest"), TOP_DOWN("TopDown");
        private final String n;
        SortMode(String n) { this.n = n; }
        @Override public String toString() { return n; }
    }
    public enum ListMode {
        WHITELIST("Whitelist"), BLACKLIST("Blacklist");
        private final String n;
        ListMode(String n) { this.n = n; }
        @Override public String toString() { return n; }
    }

    public static int argb(int a, int r, int g, int b) { return (a << 24) | (r << 16) | (g << 8) | b; }

    // ---------------- Nuker ----------------
    public static class NukerCfg {
        public boolean enabled = false;
        public NukerShape shape = NukerShape.SPHERE;
        public NukerMode mode = NukerMode.ALL;
        public double range = 4.5;
        public int up = 1, down = 1, left = 1, right = 1, forward = 1, back = 1;
        public int delay = 0;
        public int maxBlocksPerTick = 1;
        public SortMode sortMode = SortMode.CLOSEST;
        public boolean swingHand = true;
        public boolean packetMine = false;
        public boolean rotate = true;
        public ListMode listMode = ListMode.BLACKLIST;
        public List<String> whitelist = new ArrayList<>();
        public List<String> blacklist = new ArrayList<>();
        // render
        public boolean enableRenderBounding = true;
        public ShapeMode shapeModeBox = ShapeMode.LINES;
        public int sideColor = argb(0, 255, 255, 255);
        public int lineColor = argb(255, 255, 255, 255);
        public boolean enableRenderBreaking = true;
        public ShapeMode shapeModeBreak = ShapeMode.BOTH;
        public int sideColorBreak = argb(80, 255, 0, 0);
        public int lineColorBreak = argb(255, 255, 0, 0);
    }

    // ---------------- NoRender ----------------
    public static class NoRenderCfg {
        public boolean enabled = false;
        // overlay / HUD
        public boolean portalOverlay = true;
        public boolean spyglassOverlay = true;
        public boolean pumpkinOverlay = true;
        public boolean powderedSnowOverlay = true;
        public boolean fireOverlay = true;
        public boolean liquidOverlay = true;
        public boolean inWallOverlay = true;
        public boolean vignette = true;
        public boolean crosshair = false;
        public boolean bossBar = true;
        public boolean scoreboard = true;
        public boolean heldItemName = false;
        public boolean totemAnimation = true;
        public boolean hurtCam = false;
        // world
        public boolean weather = true;
        public boolean blockBreakParticles = true;
        public boolean beaconBeams = true;
        public boolean enchantmentTableBook = true;
        public boolean fallingBlocks = true;
        public List<String> particles = new ArrayList<>();
        public List<String> entities = new ArrayList<>();
        public List<String> blockEntities = new ArrayList<>();
    }

    // ---------------- Block Selection ----------------
    public static class SelectionCfg {
        public boolean enabled = false;
        public boolean advanced = false;
        public boolean singleSide = false;
        public ShapeMode shapeMode = ShapeMode.LINES;
        public int sideColor = argb(50, 255, 255, 255);
        public int lineColor = argb(255, 255, 255, 255);
        public boolean hideInsideAnimation = true;
    }

    public NukerCfg nuker = new NukerCfg();
    public NoRenderCfg noRender = new NoRenderCfg();
    public SelectionCfg blockSelection = new SelectionCfg();

    public static Config I = new Config();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("meteorlite.json"); }

    public static void load() {
        try {
            Path f = file();
            if (Files.exists(f)) {
                Config c = GSON.fromJson(Files.readString(f), Config.class);
                if (c != null) {
                    if (c.nuker == null) c.nuker = new NukerCfg();
                    if (c.noRender == null) c.noRender = new NoRenderCfg();
                    if (c.blockSelection == null) c.blockSelection = new SelectionCfg();
                    I = c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        NoRender.rebuild();
    }

    public static void save() {
        try {
            Files.writeString(file(), GSON.toJson(I));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
