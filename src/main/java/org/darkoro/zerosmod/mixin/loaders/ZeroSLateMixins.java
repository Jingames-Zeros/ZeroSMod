package org.darkoro.zerosmod.mixin.loaders;

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
      mixins.add("dbc.JRMCoreHMixins");
      mixins.add("dbc.JRMCoreMajinHairDataMixin");
      if (FMLCommonHandler.instance().getSide().isClient()) {
        mixins.add("dbc.client.JRMCoreHairEditorMixin");
        mixins.add("dbc.client.JRMCoreMajinHairColorMixin");
        mixins.add("dbc.client.JRMCoreHairEditorAccess");
        mixins.add("dbc.client.JRMCoreHairPreviewStateMixin");
        mixins.add("dbc.client.ModelBipedDBCHairHighlightMixin");
        mixins.add("dbc.client.MixinRenderEnergyAttackKi");
        mixins.add("dbc.client.DBCClientTickHandlerMixins");
        mixins.add("dbc.JRMCoreHCMixins");
      }
      if (loadedMods.contains("customnpcs")) {
        mixins.add("dbc.MixinEntityInstantTransmission");
        mixins.add("npc.AttributeItemUtilMixins");
        mixins.add("npc.AttributeAttackUtilMixins");
        mixins.add("npc.DBCUtilsMixins");
        mixins.add("npc.MixinScriptEntityKi");
        mixins.add("npc.MixinScriptEntityInstantTransmission");
        mixins.add("npc.MixinScriptLivingBaseKi");
        mixins.add("npc.MixinScriptPlayerInstantTransmission");
        if (FMLCommonHandler.instance().getSide().isClient()) {
          mixins.add("npc.MixinJSAutocompleteProviderHookSnippets");
          if (loadedMods.contains("npcdbc")) {
            mixins.add("npc.client.DBCHairStrandColorMixin");
            mixins.add("npc.client.GuiModelDBCHairTrailerMixin");
            mixins.add("npc.client.MixinTransformControllerTransformationEvents");
            mixins.add("npc.client.SubGuiFormHairTrailerMixin");
          }
        }
        if (loadedMods.contains("npcdbc")) {
          mixins.add("npc.MixinScriptDBCAddonAbsorption");
          mixins.add("npc.MixinScriptDBCAddonHeat");
          mixins.add("npc.MixinScriptPlayerTransformation");
          mixins.add("npc.MixinDBCDataStatsAbsorption");
        }
      }
    }
    return mixins;
  }
}
