package com.nobaritone.plugin;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API interface for NoBaritone, allowing other plugins to interact with the detection system
 */
public class NoBaritoneAPI {
    private final NoBaritone plugin;
    private final Map<UUID, Integer> customViolationLevels = new HashMap<>();
    private final Map<String, DetectionModule> customDetectionModules = new HashMap<>();
    
    public NoBaritoneAPI(NoBaritone plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Registers a custom detection module
     * 
     * @param name The name of the detection module
     * @param module The detection module instance
     * @return true if registered, false if a module with the same name already exists
     */
    public boolean registerDetectionModule(String name, DetectionModule module) {
        if (customDetectionModules.containsKey(name)) {
            return false;
        }
        
        customDetectionModules.put(name, module);
        plugin.getLogger().info("Registered custom detection module: " + name);
        return true;
    }
    
    /**
     * Unregisters a custom detection module
     * 
     * @param name The name of the detection module to unregister
     * @return true if unregistered, false if the module doesn't exist
     */
    public boolean unregisterDetectionModule(String name) {
        if (!customDetectionModules.containsKey(name)) {
            return false;
        }
        
        customDetectionModules.remove(name);
        plugin.getLogger().info("Unregistered custom detection module: " + name);
        return true;
    }
    
    /**
     * Gets all registered custom detection modules
     * 
     * @return Map of module names to module instances
     */
    public Map<String, DetectionModule> getDetectionModules() {
        return new HashMap<>(customDetectionModules);
    }
    
    /**
     * Adds a custom violation level for a player
     * 
     * @param player The player to add the violation for
     * @param amount The amount to add (can be negative to reduce violations)
     */
    public void addViolation(Player player, int amount) {
        UUID playerId = player.getUniqueId();
        int currentLevel = customViolationLevels.getOrDefault(playerId, 0);
        customViolationLevels.put(playerId, Math.max(0, currentLevel + amount));
        
        plugin.getLogger().fine("API: Added " + amount + " violation points to " + player.getName() + 
                ", now at " + customViolationLevels.get(playerId));
    }
    
    /**
     * Gets the current custom violation level for a player
     * 
     * @param player The player to check
     * @return The current custom violation level
     */
    public int getCustomViolationLevel(Player player) {
        return customViolationLevels.getOrDefault(player.getUniqueId(), 0);
    }
    
    /**
     * Resets the custom violation level for a player
     * 
     * @param player The player to reset
     */
    public void resetViolations(Player player) {
        customViolationLevels.remove(player.getUniqueId());
    }
    
    /**
     * Gets the baritone detection configuration
     * 
     * @return The plugin configuration
     */
    public BaritoneDetectionConfig getConfig() {
        return plugin.getBaritoneConfig();
    }
    
    /**
     * Checks if a player is considered to be using Baritone
     * 
     * @param player The player to check
     * @return true if the player is suspected of using Baritone
     */
    public boolean isUsingBaritone(Player player) {
        // Check if they have a high violation level
        return getCustomViolationLevel(player) >= plugin.getBaritoneConfig().getNotifyThreshold();
    }
    
    /**
     * Checks if a player should be excluded from Baritone detection
     * 
     * @param player The player to check
     * @return true if the player should be excluded
     */
    public boolean isExcluded(Player player) {
        BaritoneDetectionConfig config = plugin.getBaritoneConfig();
        
        // Check bypass permission
        if (player.hasPermission("nobaritone.bypass")) {
            return true;
        }
        
        // Check for OP exclusion 
        if (config.shouldExcludeOPPlayers() && player.isOp()) {
            return true;
        }
        
        // Check for creative mode exclusion
        if (config.shouldExcludeCreativePlayers() && 
            player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Interface for custom detection modules
     */
    public interface DetectionModule {
        /**
         * Process a player movement
         * 
         * @param player The player
         * @param from The starting location
         * @param to The destination location
         * @return A positive number if suspicious behavior is detected, 0 otherwise
         */
        float processMovement(Player player, org.bukkit.Location from, org.bukkit.Location to);
        
        /**
         * Gets the name of this detection module
         * 
         * @return The module name
         */
        String getName();
        
        /**
         * Gets the description of this detection module
         * 
         * @return The module description
         */
        String getDescription();
    }
} 