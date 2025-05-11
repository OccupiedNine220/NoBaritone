package com.nobaritone.plugin;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Configuration manager for Baritone detection settings
 */
public class BaritoneDetectionConfig {
    private final NoBaritone plugin;
    
    // General settings
    private boolean enabled;
    private int notifyThreshold;
    private int kickThreshold;
    private int banThreshold;
    
    // Action settings
    private boolean notifyAdminsEnabled;
    private boolean warnPlayerEnabled;
    private boolean kickEnabled;
    private boolean banEnabled;
    private boolean logToFileEnabled;
    
    // Detection settings
    private double movementSimilarityThreshold;
    private int patternDetectionSampleSize;
    private double algorithmicPathDetectionWeight;
    private int minSamplesForDetection;
    private boolean excludeOPPlayers;
    private boolean excludeCreativePlayers;
    private boolean ignoreVerticalMovement;
    
    // Advanced detection settings
    private boolean checkBlockBreakPatterns;
    private boolean checkPathfindingPatterns;
    private int maxViolationPoints;
    private int violationDecayMinutes;
    private boolean enablePacketAnalysis;

    public BaritoneDetectionConfig(NoBaritone plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * Reloads configuration from config.yml
     */
    public void reload() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        
        // General settings
        enabled = config.getBoolean("detection.enabled", true);
        notifyThreshold = config.getInt("action.notification-threshold", 5);
        kickThreshold = config.getInt("action.kick-threshold", 15);
        banThreshold = config.getInt("action.ban-threshold", 30);
        
        // Action settings
        notifyAdminsEnabled = config.getBoolean("action.notify-admins", true);
        warnPlayerEnabled = config.getBoolean("action.warn-player", true);
        kickEnabled = config.getBoolean("action.kick-enabled", true);
        banEnabled = config.getBoolean("action.ban-enabled", false);
        logToFileEnabled = config.getBoolean("action.log-to-file", true);
        
        // Detection settings
        movementSimilarityThreshold = config.getDouble("detection.movement-similarity-threshold", 0.85);
        patternDetectionSampleSize = config.getInt("detection.pattern-sample-size", 20);
        algorithmicPathDetectionWeight = config.getDouble("detection.algorithmic-path-detection", 0.7);
        minSamplesForDetection = config.getInt("detection.min-samples", 10);
        excludeOPPlayers = config.getBoolean("detection.exclude-op-players", true);
        excludeCreativePlayers = config.getBoolean("detection.exclude-creative-mode", true);
        ignoreVerticalMovement = config.getBoolean("detection.ignore-vertical-movement", false);
        
        // Advanced detection settings
        checkBlockBreakPatterns = config.getBoolean("detection.advanced.check-block-break-patterns", true);
        checkPathfindingPatterns = config.getBoolean("detection.advanced.check-pathfinding", true);
        maxViolationPoints = config.getInt("detection.advanced.max-violation-points", 100);
        violationDecayMinutes = config.getInt("detection.advanced.violation-decay-minutes", 30);
        enablePacketAnalysis = config.getBoolean("detection.advanced.enable-packet-analysis", false);
    }

    // Getters
    public boolean isEnabled() {
        return enabled;
    }

    public int getNotifyThreshold() {
        return notifyThreshold;
    }

    public int getKickThreshold() {
        return kickThreshold;
    }
    
    public int getBanThreshold() {
        return banThreshold;
    }

    public boolean isNotifyAdminsEnabled() {
        return notifyAdminsEnabled;
    }

    public boolean isWarnPlayerEnabled() {
        return warnPlayerEnabled;
    }

    public boolean isKickEnabled() {
        return kickEnabled;
    }
    
    public boolean isBanEnabled() {
        return banEnabled;
    }
    
    public boolean isLogToFileEnabled() {
        return logToFileEnabled;
    }
    
    public double getMovementSimilarityThreshold() {
        return movementSimilarityThreshold;
    }
    
    public int getPatternDetectionSampleSize() {
        return patternDetectionSampleSize;
    }
    
    public double getAlgorithmicPathDetectionWeight() {
        return algorithmicPathDetectionWeight;
    }
    
    public int getMinSamplesForDetection() {
        return minSamplesForDetection;
    }
    
    public boolean shouldExcludeOPPlayers() {
        return excludeOPPlayers;
    }
    
    public boolean shouldExcludeCreativePlayers() {
        return excludeCreativePlayers;
    }
    
    public boolean shouldIgnoreVerticalMovement() {
        return ignoreVerticalMovement;
    }
    
    public boolean shouldCheckBlockBreakPatterns() {
        return checkBlockBreakPatterns;
    }
    
    public boolean shouldCheckPathfindingPatterns() {
        return checkPathfindingPatterns;
    }
    
    public int getMaxViolationPoints() {
        return maxViolationPoints;
    }
    
    public int getViolationDecayMinutes() {
        return violationDecayMinutes;
    }
    
    public boolean isPacketAnalysisEnabled() {
        return enablePacketAnalysis;
    }
} 