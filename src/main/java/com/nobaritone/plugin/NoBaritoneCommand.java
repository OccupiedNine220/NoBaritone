package com.nobaritone.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoBaritoneCommand implements CommandExecutor, TabCompleter {
    private final NoBaritone plugin;

    public NoBaritoneCommand(NoBaritone plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!hasPermission(sender, "nobaritone.admin.reload")) return true;
                plugin.reloadNoBaritone();
                sender.sendMessage(ChatColor.GREEN + "NoBaritone configuration reloaded.");
                break;
                
            case "status":
                if (!hasPermission(sender, "nobaritone.admin.status")) return true;
                showStatus(sender);
                break;
                
            case "toggle":
                if (!hasPermission(sender, "nobaritone.admin.toggle")) return true;
                // Toggle enabled state - for testing or temporarily disabling
                // Would need to modify config and save
                sender.sendMessage(ChatColor.YELLOW + "This functionality is not implemented yet.");
                break;
                
            case "debug":
                if (!hasPermission(sender, "nobaritone.admin.debug")) return true;
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /nobaritone debug <player>");
                    return true;
                }
                // Show debug info for specific player
                sender.sendMessage(ChatColor.YELLOW + "Debug mode not implemented yet.");
                break;
                
            case "help":
            default:
                showHelp(sender);
                break;
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "==== NoBaritone Commands ====");
        sender.sendMessage(ChatColor.YELLOW + "/nobaritone reload" + ChatColor.WHITE + " - Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/nobaritone status" + ChatColor.WHITE + " - Show plugin status");
        sender.sendMessage(ChatColor.YELLOW + "/nobaritone toggle" + ChatColor.WHITE + " - Toggle plugin enabled state");
        sender.sendMessage(ChatColor.YELLOW + "/nobaritone debug <player>" + ChatColor.WHITE + " - Debug info for player");
        sender.sendMessage(ChatColor.YELLOW + "/nobaritone help" + ChatColor.WHITE + " - Show this help");
    }

    private void showStatus(CommandSender sender) {
        BaritoneDetectionConfig config = plugin.getBaritoneConfig();
        sender.sendMessage(ChatColor.GREEN + "==== NoBaritone Status ====");
        sender.sendMessage(ChatColor.YELLOW + "Status: " + ChatColor.WHITE + 
                (config.isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Notification threshold: " + ChatColor.WHITE + config.getNotifyThreshold());
        sender.sendMessage(ChatColor.YELLOW + "Kick threshold: " + ChatColor.WHITE + config.getKickThreshold());
        sender.sendMessage(ChatColor.YELLOW + "Ban threshold: " + ChatColor.WHITE + config.getBanThreshold());
        
        sender.sendMessage(ChatColor.YELLOW + "Kick enabled: " + ChatColor.WHITE + 
                (config.isKickEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        sender.sendMessage(ChatColor.YELLOW + "Ban enabled: " + ChatColor.WHITE + 
                (config.isBanEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
                
        // Show more advanced detection settings for users with higher permissions
        if (sender.hasPermission("nobaritone.admin.advanced")) {
            sender.sendMessage(ChatColor.GREEN + "==== Advanced Settings ====");
            sender.sendMessage(ChatColor.YELLOW + "Movement similarity threshold: " + ChatColor.WHITE + 
                    config.getMovementSimilarityThreshold());
            sender.sendMessage(ChatColor.YELLOW + "Pattern detection sample size: " + ChatColor.WHITE + 
                    config.getPatternDetectionSampleSize());
            sender.sendMessage(ChatColor.YELLOW + "Max violation points: " + ChatColor.WHITE + 
                    config.getMaxViolationPoints());
        }
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        }
        
        // No permission message
        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> commands = Stream.of("reload", "status", "toggle", "debug", "help")
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
            
            completions.addAll(commands);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            // Add online players for debug command
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
} 