# Zero S Mod
This mod is the client "catch all" mod for a multitude of features for ZS:
- Custom GUI handling for mods like SPC and Tourney
- Keybinds for various mods we use
- Custom Biomes (for worldedit biome visuals NOT biome generation)
- Custom Blocks/Items (including a creative tab for these)

## Race stat editor

Operators (permission level 2) can open `/zsmod racestats` in game. Select a race and
class, edit its twelve stat multipliers, and choose **Save to config**. Install the
matching ZeroSMod version on both the server and clients.

The editor updates only the selected class's **Stat Multiplier from Attribute**
values in the server's `config/jingames/dbc/races/<race>/main.cfg`. It preserves
other settings and formatting, and rejects saves if the file has changed since it
was loaded. Use **Reload / discard** to fetch the current saved values and discard
unsaved edits. Values must be finite numbers between -100000 and 100000.

**Restart the server to apply saved changes.** The editor does not change live DBC
stats and is independent of `/zsmod reload`. The displayed Saiyan entry edits
`half-saiyan/main.cfg`; full Saiyan is excluded.

## Development client

Run the **ZeroSMod Dev** configuration in IntelliJ, or start it from PowerShell:

```powershell
.\gradlew.bat runClient
```

This launches the deobfuscated Forge client with ZeroSMod and the development
dependencies configured by the Gradle plugin, including DBC/JRMCore, JBRA,
CustomNPC+, and the DBC Addon. The development game directory is `run/`.
Gradle downloads and prepares the Minecraft/Forge runtime the first time it is
launched.

## License

Zero S Mod is source-available under the
[PolyForm Strict License 1.0.0](LICENSE.md), not an open-source license. You may
use the official releases for noncommercial purposes, but not modify, redistribute
or reuse the code. Contributions are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md).
