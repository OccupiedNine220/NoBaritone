package com.nobaritone.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Language manager for handling multilingual messages
 */
public class LanguageManager {
    private final NoBaritone plugin;
    private final Map<String, String> messages = new HashMap<>();
    private final Pattern placeholderPattern = Pattern.compile("\\{(\\d+)\\}");
    private File langFile;
    private String language;

    public LanguageManager(NoBaritone plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    /**
     * Loads the language file based on configuration
     */
    public void loadLanguage() {
        language = plugin.getConfig().getString("language", "en_US");
        
        // Clear existing messages
        messages.clear();
        
        // Load default language file first as fallback
        loadDefaultLanguage();
        
        // Then load custom language file if it exists
        loadCustomLanguage();
    }
    
    /**
     * Loads the default language file from plugin resources
     */
    private void loadDefaultLanguage() {
        try (InputStreamReader reader = new InputStreamReader(
                plugin.getResource("lang/en_US.yml"), StandardCharsets.UTF_8)) {
            
            YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(reader);
            
            for (String key : defaultMessages.getKeys(true)) {
                if (!defaultMessages.isConfigurationSection(key)) {
                    messages.put(key, defaultMessages.getString(key));
                }
            }
            
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load default language file: " + e.getMessage());
        }
    }
    
    /**
     * Loads a custom language file from the plugin directory
     */
    private void loadCustomLanguage() {
        // Skip if using default language
        if (language.equals("en_US")) {
            return;
        }
        
        // Create language directory if it doesn't exist
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        
        // Create the language file if it doesn't exist
        langFile = new File(langDir, language + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + language + ".yml", false);
        }
        
        // If the file exists now, load it
        if (langFile.exists()) {
            FileConfiguration customMessages = YamlConfiguration.loadConfiguration(langFile);
            
            for (String key : customMessages.getKeys(true)) {
                if (!customMessages.isConfigurationSection(key)) {
                    messages.put(key, customMessages.getString(key));
                }
            }
        } else {
            plugin.getLogger().warning("Language file " + language + ".yml not found. Using default language.");
        }
    }
    
    /**
     * Gets a message with the given key
     */
    public String getMessage(String key) {
        String message = messages.get(key);
        return message != null ? message : "Missing message: " + key;
    }
    
    /**
     * Gets a message with the given key and replaces placeholders with arguments
     */
    public String getMessage(String key, String... args) {
        String message = getMessage(key);
        
        Matcher matcher = placeholderPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            String replacement = (index < args.length) ? args[index] : "";
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    
    /**
     * Reloads the language files
     */
    public void reload() {
        loadLanguage();
    }
} 