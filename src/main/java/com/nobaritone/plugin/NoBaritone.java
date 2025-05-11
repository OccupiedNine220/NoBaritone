package com.nobaritone.plugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoBaritone extends JavaPlugin implements Listener {

    private static final String PREFIX = ChatColor.RED + "[NoBaritone] " + ChatColor.RESET;
    private final Map<UUID, PlayerMovementData> playerData = new HashMap<>();
    private BaritoneDetectionConfig config;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Load configuration
        config = new BaritoneDetectionConfig(this);
        
        // Initialize language manager and load messages
        languageManager = new LanguageManager(this);
        
        // Register event handlers
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register commands
        getCommand("nobaritone").setExecutor(new NoBaritoneCommand(this));
        
        getLogger().info(languageManager.getMessage("plugin.enabled"));
    }

    @Override
    public void onDisable() {
        getLogger().info(languageManager.getMessage("plugin.disabled"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check for Baritone client
        checkForBaritoneClient(player);
        
        // Initialize player tracking data
        playerData.put(player.getUniqueId(), new PlayerMovementData());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!config.isEnabled()) return;
        
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // Get player movement data
        PlayerMovementData data = playerData.get(playerId);
        if (data == null) {
            data = new PlayerMovementData();
            playerData.put(playerId, data);
        }
        
        // Analyze player movement for Baritone patterns
        data.addMovement(event.getFrom(), event.getTo());
        
        // Check for suspicious movement
        if (data.checkForBaritonePatterns()) {
            handleSuspiciousMovement(player, data);
        }
    }
    
    /**
     * Checks for Baritone in the player's client
     */
    private void checkForBaritoneClient(Player player) {
        // Implementation for packet-based detection can go here
        // For example, sending a specific packet and checking response
    }
    
    /**
     * Handles suspicious player movement
     */
    private void handleSuspiciousMovement(Player player, PlayerMovementData data) {
        // Increment violation counter
        data.incrementViolationLevel();
        
        // Take actions based on violation level
        int violationLevel = data.getViolationLevel();
        
        if (violationLevel >= config.getKickThreshold()) {
            // Kick player
            if (config.isKickEnabled()) {
                player.kickPlayer(PREFIX + languageManager.getMessage("action.kick"));
                data.resetViolationLevel();
                getLogger().info(languageManager.getMessage("log.player_kicked", player.getName(), String.valueOf(violationLevel)));
            }
        } else if (violationLevel >= config.getNotifyThreshold()) {
            // Notify admins
            if (config.isNotifyAdminsEnabled()) {
                notifyAdmins(languageManager.getMessage("admin.notification", player.getName(), String.valueOf(violationLevel)));
            }
            
            // Warn player
            if (config.isWarnPlayerEnabled()) {
                player.sendMessage(PREFIX + languageManager.getMessage("player.warning"));
            }
        }
    }
    
    /**
     * Sends notifications to admins
     */
    private void notifyAdmins(String message) {
        String notifyMessage = PREFIX + message;
        
        // Log to console
        getLogger().warning(message);
        
        // Notify online admins
        getServer().getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("nobaritone.notify"))
            .forEach(p -> p.sendMessage(notifyMessage));
    }
    
    /**
     * Reloads the plugin configuration
     */
    public void reloadNoBaritone() {
        reloadConfig();
        config.reload();
        languageManager.reload();
    }
    
    /**
     * Gets the plugin's baritone configuration
     */
    public BaritoneDetectionConfig getBaritoneConfig() {
        return config;
    }
    
    /**
     * Gets the language manager
     */
    public LanguageManager getLanguageManager() {
        return languageManager;
    }
} 