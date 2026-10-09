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
- `/zsmod finishercam stop [player]`
