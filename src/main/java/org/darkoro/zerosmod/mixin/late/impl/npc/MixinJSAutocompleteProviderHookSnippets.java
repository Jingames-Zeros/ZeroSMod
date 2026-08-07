package org.darkoro.zerosmod.mixin.late.impl.npc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import noppes.npcs.api.handler.IHookDefinition;
import noppes.npcs.client.gui.util.script.autocomplete.AutocompleteItem;
import noppes.npcs.client.gui.util.script.autocomplete.AutocompleteProvider;
import noppes.npcs.client.gui.util.script.autocomplete.JSAutocompleteProvider;
import noppes.npcs.client.gui.util.script.autocomplete.JavaAutocompleteProvider;
import noppes.npcs.constants.ScriptContext;
import noppes.npcs.controllers.ScriptHookController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JSAutocompleteProvider.class, remap = false)
public abstract class MixinJSAutocompleteProviderHookSnippets extends JavaAutocompleteProvider {

  @Inject(method = "addLanguageUniqueSuggestions", at = @At("TAIL"), remap = false)
  private void zerosmod$addRegisteredHookSnippets(
      AutocompleteProvider.Context context,
      List<AutocompleteItem> items,
      CallbackInfo ci) {
    if (this.document == null || ScriptHookController.Instance == null || context.isMemberAccess) {
      return;
    }

    ScriptContext scriptContext = this.document.getScriptContext();
    if (scriptContext == null || scriptContext.hookContext == null || scriptContext.hookContext.isEmpty()) {
      return;
    }

    List<IHookDefinition> hooks = ScriptHookController.Instance.getAllHookDefinitions(scriptContext.hookContext);
    if (hooks == null || hooks.isEmpty()) {
      return;
    }

    Set<String> existing = new HashSet<String>();
    for (AutocompleteItem item : items) {
      existing.add(item.getSearchName());
    }

    for (IHookDefinition hook : hooks) {
      if (hook == null || hook.hookName() == null || hook.hookName().isEmpty() || existing.contains(hook.hookName())) {
        continue;
      }

      String paramName = "event";
      String[] paramNames = hook.paramNames();
      if (paramNames != null && paramNames.length > 0 && paramNames[0] != null && !paramNames[0].isEmpty()) {
        paramName = paramNames[0];
      }

      String signature = "function " + hook.hookName() + "(" + paramName + ")";
      items.add(
          new AutocompleteItem.Builder()
              .name(hook.hookName() + "(" + paramName + ")")
              .searchName(hook.hookName())
              .insertText(signature + " {\n    \n}")
              .kind(AutocompleteItem.Kind.SNIPPET)
              .typeLabel("hook")
              .signature(signature)
              .documentation("Registered " + scriptContext.id + " script hook.")
              .build());
      existing.add(hook.hookName());
    }
  }
}
