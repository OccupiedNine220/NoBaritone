# NoBaritone

Advanced anti-Baritone plugin for Minecraft servers. Detects and prevents usage of automated pathfinding mods like Baritone.

## Features

- **Advanced Detection**: Uses multiple techniques to identify Baritone and similar mods
- **Flexible Configuration**: Customize detection parameters, thresholds, and actions
- **Multiple Actions**: Notify admins, warn players, kick, or ban based on violation levels
- **Multi-Language Support**: Easily add custom translations through language files
- **Permissions System**: Fine-grained permissions for all features
- **Performance Friendly**: Minimal impact on server performance
- **API for Developers**: Integrate with other plugins and add custom detection methods
- **Legacy Support**: Special version available for older Minecraft servers (1.8-1.12)
- **ProtocolLib Enhancement**: Optional deeper packet analysis when ProtocolLib is available

## Versions

- **Standard Version**: For modern Minecraft servers (1.13+) 
- **Legacy Version**: For older Minecraft servers (1.8-1.12)

## Installation

1. Download the latest JAR from the [Releases page](https://github.com/nobaritone/NoBaritone/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Edit the configuration in `plugins/NoBaritone/config.yml` if needed

## Commands

- `/nobaritone reload` - Reload configuration
- `/nobaritone status` - Show plugin status
- `/nobaritone toggle` - Toggle plugin enabled state
- `/nobaritone debug <player>` - Debug info for player
- `/nobaritone help` - Show help

## Permissions

- `nobaritone.admin.use` - Use the base /nobaritone command
- `nobaritone.admin.reload` - Reload the plugin configuration
- `nobaritone.admin.status` - Check the plugin status
- `nobaritone.admin.toggle` - Toggle the plugin on/off
- `nobaritone.admin.debug` - Use debug commands
- `nobaritone.admin.advanced` - View advanced settings
- `nobaritone.notify` - Receive notifications about suspicious players
- `nobaritone.bypass` - Bypass NoBaritone detection

## Configuration

The plugin is highly configurable. Check the `config.yml` file for all options.

### Detection Settings

```yaml
detection:
  enabled: true
  movement-similarity-threshold: 0.85
  pattern-sample-size: 20
  algorithmic-path-detection: 0.7
  min-samples: 10
  exclude-op-players: true
  exclude-creative-mode: true
  
  advanced:
    check-block-break-patterns: true
    check-pathfinding: true
    max-violation-points: 100
    violation-decay-minutes: 30
    enable-packet-analysis: false
```

### Action Settings

```yaml
action:
  notification-threshold: 5
  kick-threshold: 15
  ban-threshold: 30
  notify-admins: true
  warn-player: true
  kick-enabled: true
  ban-enabled: false
  log-to-file: true
```

## Languages

NoBaritone supports multiple languages. The default is English, but you can choose another language in the config:

```yaml
language: "ru_RU"  # Options: en_US, ru_RU, etc.
```

You can add your own language file by creating a new file in the `plugins/NoBaritone/lang/` directory.

## Developer API

NoBaritone provides an API for other plugins to interact with the detection system.

```java
// Get the NoBaritone API
NoBaritoneAPI api = ((NoBaritone) Bukkit.getPluginManager().getPlugin("NoBaritone")).getAPI();

// Register a custom detection module
api.registerDetectionModule("MyDetector", new CustomDetector());

// Add custom violations to a player
api.addViolation(player, 5);

// Check if a player is suspected of using Baritone
boolean isSuspicious = api.isUsingBaritone(player);
```

## Custom Detection Modules

You can create custom detection modules to extend NoBaritone's capabilities:

```java
public class CustomDetector implements NoBaritoneAPI.DetectionModule {
    @Override
    public float processMovement(Player player, Location from, Location to) {
        // Your detection logic here
        return suspiciousScore; // Return > 0 if suspicious
    }

    @Override
    public String getName() {
        return "Custom Detector";
    }

    @Override
    public String getDescription() {
        return "Detects custom Baritone patterns";
    }
}
```

## Events

NoBaritone exposes events that other plugins can listen to:

```java
@EventHandler
public void onBaritoneViolation(NoBaritoneViolationEvent event) {
    // Access the player
    Player player = event.getPlayer();
    
    // Access the violation level
    int level = event.getViolationLevel();
    
    // Modify the violation level
    event.setViolationLevel(level + 5);
    
    // Cancel the violation (prevent punishment)
    event.setCancelled(true);
}
```

## Building from Source

```bash
git clone https://github.com/nobaritone/NoBaritone.git
cd NoBaritone
mvn clean package
```

To build the Legacy version for Minecraft 1.8-1.12:

```bash
git checkout legacy-support
mvn clean package
```

## License

This project is licensed under the GPL-3.0 license - see the LICENSE file for details. 