package com.nobaritone.plugin;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class for analyzing player movement patterns to detect Baritone
 */
public class PlayerMovementData {
    private static final int MAX_MOVEMENT_HISTORY = 100;
    
    private final Queue<MovementEntry> movementHistory = new LinkedList<>();
    private int violationLevel = 0;
    private long lastViolationTime = 0;
    
    // Metrics for algorithm detection
    private int straightLineCounter = 0;
    private int exactAngleChanges = 0;
    private int perfectJumps = 0;
    private long lastDirectionChangeTime = 0;
    
    /**
     * Adds a movement entry to the player's history
     */
    public void addMovement(Location from, Location to) {
        // Only track movements in the same world
        if (from.getWorld() != to.getWorld()) {
            return;
        }
        
        // Create new movement entry
        MovementEntry entry = new MovementEntry(
            System.currentTimeMillis(),
            from.getX(), from.getY(), from.getZ(),
            to.getX(), to.getY(), to.getZ(),
            from.getYaw(), from.getPitch()
        );
        
        // Add to history
        movementHistory.add(entry);
        
        // Maintain history size
        if (movementHistory.size() > MAX_MOVEMENT_HISTORY) {
            movementHistory.poll();
        }
        
        // Analyze latest movement
        analyzeLatestMovement();
    }
    
    /**
     * Analyzes the latest movement for Baritone-like patterns
     */
    private void analyzeLatestMovement() {
        if (movementHistory.size() < 3) {
            return;
        }
        
        List<MovementEntry> entries = new ArrayList<>(movementHistory);
        
        // Get the 3 most recent movements
        MovementEntry current = entries.get(entries.size() - 1);
        MovementEntry previous = entries.get(entries.size() - 2);
        MovementEntry beforePrevious = entries.get(entries.size() - 3);
        
        // Check for straight-line movement
        if (isStraightLineMovement(beforePrevious, previous, current)) {
            straightLineCounter++;
        } else {
            straightLineCounter = Math.max(0, straightLineCounter - 1);
        }
        
        // Check for exact angle changes (45 or 90 degrees)
        if (isExactAngleChange(beforePrevious, previous, current)) {
            exactAngleChanges++;
            lastDirectionChangeTime = System.currentTimeMillis();
        }
        
        // Check for perfect jumps
        if (isPerfectJump(previous, current)) {
            perfectJumps++;
        }
    }
    
    /**
     * Checks if the player is moving in a straight line
     */
    private boolean isStraightLineMovement(MovementEntry a, MovementEntry b, MovementEntry c) {
        // Calculate direction vectors
        double dx1 = b.toX - a.toX;
        double dz1 = b.toZ - a.toZ;
        
        double dx2 = c.toX - b.toX;
        double dz2 = c.toZ - b.toZ;
        
        // Normalize vectors
        double length1 = Math.sqrt(dx1 * dx1 + dz1 * dz1);
        double length2 = Math.sqrt(dx2 * dx2 + dz2 * dz2);
        
        if (length1 < 0.01 || length2 < 0.01) {
            return false;
        }
        
        dx1 /= length1;
        dz1 /= length1;
        dx2 /= length2;
        dz2 /= length2;
        
        // Calculate dot product (cosine of angle)
        double dotProduct = dx1 * dx2 + dz1 * dz2;
        
        // If dot product is close to 1, vectors are pointing in almost the same direction
        return dotProduct > 0.98;
    }
    
    /**
     * Checks if the player made an exact angle change (like 45 or 90 degrees)
     */
    private boolean isExactAngleChange(MovementEntry a, MovementEntry b, MovementEntry c) {
        // Calculate direction vectors
        double dx1 = b.toX - a.toX;
        double dz1 = b.toZ - a.toZ;
        
        double dx2 = c.toX - b.toX;
        double dz2 = c.toZ - b.toZ;
        
        // Calculate lengths
        double length1 = Math.sqrt(dx1 * dx1 + dz1 * dz1);
        double length2 = Math.sqrt(dx2 * dx2 + dz2 * dz2);
        
        if (length1 < 0.01 || length2 < 0.01) {
            return false;
        }
        
        // Normalize vectors
        dx1 /= length1;
        dz1 /= length1;
        dx2 /= length2;
        dz2 /= length2;
        
        // Calculate dot product
        double dotProduct = dx1 * dx2 + dz1 * dz2;
        
        // Convert to angle in degrees
        double angleDegrees = Math.toDegrees(Math.acos(dotProduct));
        
        // Check if angle is close to 45 or 90 degrees
        return (Math.abs(angleDegrees - 45) < 2 || Math.abs(angleDegrees - 90) < 2);
    }
    
    /**
     * Checks if the player made a "perfect" jump (consistent height)
     */
    private boolean isPerfectJump(MovementEntry a, MovementEntry b) {
        // Check if player is moving upward
        boolean movingUp = b.toY > a.toY;
        
        if (!movingUp) {
            return false;
        }
        
        // Check if jump height is exactly the same as a standard jump
        double jumpHeight = b.toY - a.toY;
        
        // Standard jump height in Minecraft
        return Math.abs(jumpHeight - 0.42) < 0.01;
    }
    
    /**
     * Checks for Baritone-like movement patterns
     */
    public boolean checkForBaritonePatterns() {
        int suspiciousScore = 0;
        
        // Weight different factors
        if (straightLineCounter > 8) {
            suspiciousScore += 5;
        }
        
        if (exactAngleChanges > 3) {
            suspiciousScore += 3;
        }
        
        if (perfectJumps > 2) {
            suspiciousScore += 3;
        }
        
        // Check timing consistency (a very strong Baritone indicator)
        if (isConsistentTimingBetweenMoves()) {
            suspiciousScore += 10;
        }
        
        // Baritone often makes direction changes at consistent intervals
        if (isConsistentDirectionChangePattern()) {
            suspiciousScore += 5;
        }
        
        return suspiciousScore >= 10;
    }
    
    /**
     * Checks if there is consistent timing between movements (algorithmic indicator)
     */
    private boolean isConsistentTimingBetweenMoves() {
        if (movementHistory.size() < 10) {
            return false;
        }
        
        List<MovementEntry> entries = new ArrayList<>(movementHistory);
        List<Long> timeDifferences = new ArrayList<>();
        
        // Calculate time differences between consecutive moves
        for (int i = 1; i < entries.size(); i++) {
            timeDifferences.add(entries.get(i).timestamp - entries.get(i-1).timestamp);
        }
        
        // Count how many time differences are almost identical
        int consistentTimeCount = 0;
        for (int i = 1; i < timeDifferences.size(); i++) {
            long diff = Math.abs(timeDifferences.get(i) - timeDifferences.get(i-1));
            if (diff < 10) { // 10ms tolerance
                consistentTimeCount++;
            }
        }
        
        // If more than 70% of movements have consistent timing, flag as suspicious
        return (double) consistentTimeCount / timeDifferences.size() > 0.7;
    }
    
    /**
     * Checks if direction changes happen at consistent intervals (algorithmic indicator)
     */
    private boolean isConsistentDirectionChangePattern() {
        // Implementation would track timing of direction changes
        // For now, we use a simpler check
        return exactAngleChanges > 5;
    }
    
    /**
     * Gets the current violation level
     */
    public int getViolationLevel() {
        return violationLevel;
    }
    
    /**
     * Increments the violation level
     */
    public void incrementViolationLevel() {
        violationLevel++;
        lastViolationTime = System.currentTimeMillis();
    }
    
    /**
     * Resets the violation level
     */
    public void resetViolationLevel() {
        violationLevel = 0;
    }
    
    /**
     * Class representing a single movement entry
     */
    private static class MovementEntry {
        private final long timestamp;
        private final double fromX, fromY, fromZ;
        private final double toX, toY, toZ;
        private final float yaw, pitch;
        
        public MovementEntry(long timestamp, 
                             double fromX, double fromY, double fromZ,
                             double toX, double toY, double toZ,
                             float yaw, float pitch) {
            this.timestamp = timestamp;
            this.fromX = fromX;
            this.fromY = fromY;
            this.fromZ = fromZ;
            this.toX = toX;
            this.toY = toY;
            this.toZ = toZ;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
} 