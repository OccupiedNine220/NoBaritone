# NoBaritone

Advanced anti-Baritone plugin for Minecraft servers. Detects and prevents usage of automated pathfinding mods like Baritone.

## Features

- **Advanced Detection**: Uses multiple techniques to identify Baritone and similar mods
- **Flexible Configuration**: Customize detection parameters, thresholds, and actions
- **Multiple Actions**: Notify admins, warn players, kick, or ban based on violation levels
- **Multi-Language Support**: Easily add custom translations through language files
- **Permissions System**: Fine-grained permissions for all features
- **Performance Friendly**: Minimal impact on server performance

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
```

## Languages

NoBaritone supports multiple languages. The default is English, but you can choose another language in the config:

```yaml
language: "ru_RU"  # Options: en_US, ru_RU, etc.
```

You can add your own language file by creating a new file in the `plugins/NoBaritone/lang/` directory.

## Building from Source

```bash
git clone https://github.com/nobaritone/NoBaritone.git
cd NoBaritone
mvn clean package
```

## License

This project is licensed under the GPL-3.0 license - see the LICENSE file for details. 