package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import noppes.npcs.client.gui.util.script.autocomplete.AutocompleteItem;
import noppes.npcs.client.gui.util.script.autocomplete.AutocompleteProvider;
import noppes.npcs.client.gui.util.script.autocomplete.JSAutocompleteProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Removes identical suggestions collected through multiple CNPC JavaScript type paths. */
@Mixin(value = JSAutocompleteProvider.class, remap = false)
public abstract class MixinJSAutocompleteProviderDeduplication {

  @Inject(method = "addMemberSuggestions", at = @At("RETURN"), remap = false)
  private void zerosmod$removeDuplicateMemberSuggestions(
      AutocompleteProvider.Context context,
      List<AutocompleteItem> items,
      CallbackInfo ci) {
    Set<String> seen = new HashSet<String>();
    Iterator<AutocompleteItem> iterator = items.iterator();
    while (iterator.hasNext()) {
      AutocompleteItem item = iterator.next();
      String key = item.getKind()
          + "\u0000" + item.getName()
          + "\u0000" + item.getInsertText()
          + "\u0000" + item.getTypeLabel();
      if (!seen.add(key)) {
        iterator.remove();
      }
    }
  }
}
