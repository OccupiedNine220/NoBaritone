package com.nobaritone.plugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.GameMode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Main plugin class for NoBaritone anti-Baritone protection
 */
public class NoBaritone extends JavaPlugin implements Listener {

    private static final String PREFIX = ChatColor.RED + "[NoBaritone] " + ChatColor.RESET;
    private final Map<UUID, PlayerMovementData> playerData = new HashMap<>();
    private BaritoneDetectionConfig config;
    private LanguageManager languageManager;
    private boolean protocolLibAvailable = false;
    private PacketAnalyzer packetAnalyzer;
    private NoBaritoneAPI api;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Load configuration
        config = new BaritoneDetectionConfig(this);
        
        // Initialize language manager and load messages
        languageManager = new LanguageManager(this);
        
        // Initialize API
        api = new NoBaritoneAPI(this);
        
        // Setup ProtocolLib if available
        setupProtocolLib();
        
        // Register event handlers
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register commands
        getCommand("nobaritone").setExecutor(new NoBaritoneCommand(this));
        
        getLogger().info(languageManager.getMessage("plugin.enabled"));
    }

    @Override
    public void onDisable() {
        // Clean up packet listeners if ProtocolLib is used
        if (protocolLibAvailable && packetAnalyzer != null) {
            packetAnalyzer.unregisterListeners();
        }
        
        getLogger().info(languageManager.getMessage("plugin.disabled"));
    }
    
    /**
     * Sets up ProtocolLib integration if available
     */
    private void setupProtocolLib() {
        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            protocolLibAvailable = true;
            packetAnalyzer = new PacketAnalyzer(this);
            packetAnalyzer.registerListeners();
            getLogger().info("ProtocolLib found! Enhanced Baritone detection enabled.");
        } else {
            getLogger().info("ProtocolLib not found. Using basic detection methods.");
        }
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
        
        // Check bypass permission
        if (player.hasPermission("nobaritone.bypass")) {
            return;
        }
        
        // Check for OP exclusion
        if (config.shouldExcludeOPPlayers() && player.isOp()) {
            return;
        }
        
        // Skip creative mode players if configured
        if (config.shouldExcludeCreativePlayers() && 
            player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        
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
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!config.isEnabled() || !config.shouldCheckBlockBreakPatterns()) {
            return;
        }
        
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // Skip bypassed players
        if (player.hasPermission("nobaritone.bypass")) {
            return;
        }
        
        // Get player data
        PlayerMovementData data = playerData.get(playerId);
        if (data == null) {
            return;
        }
        
        // Check for mining patterns associated with Baritone
        if (data.analyzeMiningPattern(event.getBlock())) {
            getLogger().log(Level.INFO, "Detected possible Baritone mine pattern for player {0}", 
                    player.getName());
            
            // Increase violation level but with lower weight
            data.incrementViolationLevel(0.5f);
        }
    }
    
    /**
     * Checks for Baritone in the player's client
     */
    private void checkForBaritoneClient(Player player) {
        // Implementation for packet-based detection can go here
        // For example, sending a specific packet and checking response
        
        // With ProtocolLib, more advanced checking is done via the PacketAnalyzer class
        if (protocolLibAvailable) {
            getLogger().fine("ProtocolLib checking client data for " + player.getName());
        }
    }
    
    /**
     * Handles suspicious player movement
     */
    private void handleSuspiciousMovement(Player player, PlayerMovementData data) {
        // Increment violation counter
        data.incrementViolationLevel();
        
        // Take actions based on violation level
        int violationLevel = data.getViolationLevel();
        
        // Allow plugins to modify the violation level or cancel actions via API
        NoBaritoneViolationEvent event = new NoBaritoneViolationEvent(player, violationLevel);
        getServer().getPluginManager().callEvent(event);
        
        if (event.isCancelled()) {
            return;
        }
        
        // Use the possibly modified violation level
        violationLevel = event.getViolationLevel();
        
        if (violationLevel >= config.getBanThreshold() && config.isBanEnabled()) {
            // Ban player
            String banCommand = "ban " + player.getName() + " " + languageManager.getMessage("action.ban");
            getServer().dispatchCommand(getServer().getConsoleSender(), banCommand);
            getLogger().info(languageManager.getMessage("log.player_banned", 
                    player.getName(), String.valueOf(violationLevel)));
            return;
        }
        
        if (violationLevel >= config.getKickThreshold() && config.isKickEnabled()) {
            // Kick player
            player.kickPlayer(PREFIX + languageManager.getMessage("action.kick"));
            data.resetViolationLevel();
            getLogger().info(languageManager.getMessage("log.player_kicked", 
                    player.getName(), String.valueOf(violationLevel)));
            return;
        }
        
        if (violationLevel >= config.getNotifyThreshold()) {
            // Notify admins
            if (config.isNotifyAdminsEnabled()) {
                notifyAdmins(languageManager.getMessage("admin.notification", 
                        player.getName(), String.valueOf(violationLevel)));
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
    
    /**
     * Checks if ProtocolLib is available for advanced detection
     */
    public boolean isProtocolLibAvailable() {
        return protocolLibAvailable;
    }
    
    /**
     * Gets the NoBaritone API interface
     */
    public NoBaritoneAPI getAPI() {
        return api;
    }
} 