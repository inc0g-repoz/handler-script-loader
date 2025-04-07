[icon]: https://raw.githubusercontent.com/inc0g-repoz/low-tier-script-engine/refs/heads/main/src/assets/icon.png
[LIX4J]: https://github.com/inc0g-repoz/lix4j
[Spigot-API]: https://hub.spigotmc.org/nexus/service/rest/repository/browse/snapshots/org/spigotmc/spigot-api/

# ![icon] Handler Script Loader
A loader for event handlers from [LIX4J] scripts. Allows developers to manage [LIX4J] scripts and use them for handling events on Spigot servers.

### Commands
In fact, you only need one command: `/handler-script-loader reload` (or an `/hsl reload` shortcut). Requires a `handlerscriptloader.reload` permission to be used.

### Compatibility
Only tested on 1.20.4, but should work on any versions of Spigot and Paper servers unless they are using a deprecated API.

### Dependencies
The dependencies required for building are listed below:
- [Spigot-API] – can be grabbed from the libraries bundle of your server (provided);
- [LTSE] – should be cloned from the respective repository (compile).
