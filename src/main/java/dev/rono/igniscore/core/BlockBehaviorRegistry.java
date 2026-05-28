package dev.rono.igniscore.core;

import dev.rono.igniscore.strategies.*;

import java.util.HashMap;
import java.util.Map;

public class BlockBehaviorRegistry {
    private static final Map<String, BlockBehaviorStrategy> strategies = new HashMap<>();

    public static void register(String type, BlockBehaviorStrategy strategy) {
        strategies.put(type.toLowerCase(), strategy);
    }

    public static BlockBehaviorStrategy get(String type) {
        return strategies.getOrDefault(type.toLowerCase(), strategies.get("default"));
    }

    public static void init() {
        register("default", new DefaultExplosionStrategy());
        register("nuclear", new NuclearStrategy());
        register("entity", new EntityStrategy());
        register("phantom", new PhantomStrategy());
        register("erupting", new EruptingStrategy());
        register("mimic", new MimicStrategy());
        register("tunneling", new TunnelingStrategy());
        register("wormhole", new WormholeStrategy());
        register("structure", new StructureStrategy());
        register("effect", new EffectStrategy());
    }
}
