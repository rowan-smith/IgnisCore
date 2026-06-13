package dev.rono.igniscore.service;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the lifecycle and state of custom block instances.
 */
public class RuntimeBlockService {
    private final Map<UUID, RuntimeBlockInstance> activeInstances = new ConcurrentHashMap<>();
    private final Map<IgnisLocation, UUID> locationToUuid = new ConcurrentHashMap<>();

    public RuntimeBlockInstance createInstance(BlockDefinition definition, IgnisLocation location) {
        RuntimeBlockInstance instance = new RuntimeBlockInstance(UUID.randomUUID(), definition, location);
        activeInstances.put(instance.getUuid(), instance);
        locationToUuid.put(location, instance.getUuid());
        return instance;
    }

    public RuntimeBlockInstance getInstance(UUID uuid) {
        return activeInstances.get(uuid);
    }

    public RuntimeBlockInstance getInstanceAt(IgnisLocation location) {
        UUID uuid = locationToUuid.get(location);
        return uuid != null ? activeInstances.get(uuid) : null;
    }

    public void removeInstance(UUID uuid) {
        RuntimeBlockInstance instance = activeInstances.remove(uuid);
        if (instance != null) {
            locationToUuid.remove(instance.getLocation());
        }
    }

    public Collection<RuntimeBlockInstance> getActiveInstances() {
        return activeInstances.values();
    }
}
