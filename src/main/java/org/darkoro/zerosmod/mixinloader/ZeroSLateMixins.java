package org.darkoro.zerosmod.mixinloader;

import cpw.mods.fml.common.FMLCommonHandler;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public final class ZeroSLateMixins implements ILateMixinLoader {

  @Override public String getMixinConfig() {
    return "mixins.zerosmod.late.json";
  }

  @Override public List<String> getMixins(Set<String> loadedMods) {
    List<String> mixins = new ArrayList<String>();
    if (loadedMods.contains("jinryuujrmcore") && loadedMods.contains("jinryuudragonblockc")) {
      mixins.add("dbc.MixinEntityEnergyAtt");
      if (FMLCommonHandler.instance().getSide().isClient()) {
        mixins.add("dbc.client.MixinRenderEnergyAttackKi");
      }
      if (loadedMods.contains("customnpcs")) {
        mixins.add("dbc.MixinEntityInstantTransmission");
        mixins.add("npc.MixinScriptEntityKi");
        mixins.add("npc.MixinScriptEntityInstantTransmission");
        mixins.add("npc.MixinScriptLivingBaseKi");
        mixins.add("npc.MixinScriptPlayerInstantTransmission");
        if (FMLCommonHandler.instance().getSide().isClient()) {
          mixins.add("npc.MixinJSAutocompleteProviderHookSnippets");
        }
      }
    }
    return mixins;
  }
}
