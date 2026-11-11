package com.example.meteorlite;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class NoRender {
    private static Set<String> particles = new HashSet<>();
    private static Set<String> entities = new HashSet<>();
    private static Set<String> blockEntities = new HashSet<>();

    public static boolean on() { return Config.I.noRender.enabled; }
    public static Config.NoRenderCfg c() { return Config.I.noRender; }

    public static void rebuild() {
        particles = norm(Config.I.noRender.particles);
        entities = norm(Config.I.noRender.entities);
        blockEntities = norm(Config.I.noRender.blockEntities);
    }

    private static Set<String> norm(java.util.List<String> l) {
        Set<String> s = new HashSet<>();
        if (l == null) return s;
        for (String x : l) {
            String t = x.trim().toLowerCase(Locale.ROOT);
            if (t.isEmpty()) continue;
            if (!t.contains(":")) t = "minecraft:" + t;
            s.add(t);
        }
        return s;
    }

    public static boolean hidesParticle(String id) { return on() && particles.contains(id); }
    public static boolean hidesEntity(String id) { return on() && entities.contains(id); }
    public static boolean hidesBlockEntity(String id) { return on() && blockEntities.contains(id); }
}
