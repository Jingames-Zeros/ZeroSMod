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

## Finisher camera

Scripts can play a cinematic camera for killmoves through the `ZSAPI` global:

```js
ZSAPI.playFinisherCamera(player, npc, 60, "orbit"); // or "shoulder", "side"; "intro"/"orbit_target" film only the npc
ZSAPI.playFinisherCamera(player, npc, 60, "side", "finisher_attack"); // also plays a CNPC+ animation on the player
ZSAPI.stopFinisherCamera(player);                    // end early
```

During the shot runtime (<= 1200 ticks), client hides HUD, hand and locks movement et all.
Shit gets restored once the shot ends - this can happen on Death, Respawn, Disconnect or Dim Change.

Testing without a script:
- `/zsmod finishercam <ticks> [preset] [player] [animation]`
- `/zsmod finishercam path [player] [animation]` (a sample keyframe path around the entity you look at)
- `/zsmod finishercam stop [player]`

### Camera paths

For shots the presets can't do, build a path from keyframes. The camera follows a smooth curve through them
and turns the short way between rotations:

```js
var path = ZSAPI.createCameraPath()
    .relativeTo(npc)                // offsets follow the npc: x right, y up from its feet, z in front of it
    .key(0, 0, 1.0, -6, 0, 10)      // tick, x, y, z, yaw, pitch (yaw 0 = the way the npc faces, + turns right)
    .key(40, 4, 2.0, -3, -30, 15)
    .keyLookAt(80, 1, 1.6, 2, npc)  // aim at an entity instead of a fixed rotation
    .ease("inOut");                 // linear (default), in, out, inOut
ZSAPI.playCameraPath(player, path, "FlashFlurryRush"); // animation optional, stopped like with presets
```

- The shot lasts until the last keyframe (at most 1200 ticks, 64 keyframes); a new keyframe at the same tick
  replaces the old one.
- Without `relativeTo`, positions and yaw are world coordinates, for set pieces in a fixed spot.
- Relative paths are kept out of walls like the presets; world-coordinate paths are played as written.
- `ZSAPI.stopFinisherCamera(player)` ends a path early too.

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
