package com.nobaritone.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event that is fired when a player violates the NoBaritone detection system
 */
public class NoBaritoneViolationEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private int violationLevel;
    private boolean cancelled = false;
    
    public NoBaritoneViolationEvent(Player player, int violationLevel) {
        this.player = player;
        this.violationLevel = violationLevel;
    }
    
    /**
     * Gets the player who violated the rules
     * 
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the violation level
     * 
     * @return The violation level
     */
    public int getViolationLevel() {
        return violationLevel;
    }
    
    /**
     * Sets the violation level
     * 
     * @param violationLevel The new violation level
     */
    public void setViolationLevel(int violationLevel) {
        this.violationLevel = violationLevel;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
} 