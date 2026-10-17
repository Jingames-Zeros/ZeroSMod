package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import noppes.npcs.client.gui.util.script.interpreter.ScriptDocument;
import noppes.npcs.client.gui.util.script.interpreter.field.FieldInfo;
import noppes.npcs.client.gui.util.script.interpreter.method.MethodInfo;
import noppes.npcs.client.gui.util.script.interpreter.type.TypeInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ScriptDocument.class, remap = false)
public abstract class MixinScriptDocumentHelperTypes {

  @Shadow private String text;
  @Shadow private void parseLocalVariables() {}
  @Shadow public abstract boolean isJavaScript();
  @Shadow public abstract boolean isExcluded(int offset);
  @Shadow public abstract List<MethodInfo> getMethods();
  @Shadow public abstract MethodInfo findContainingMethod(int offset);
  @Shadow public abstract TypeInfo resolveExpressionType(String expression, int offset);

  @Unique private final Map<String, Map<String, TypeInfo>> zerosmod$indexedProperties = new HashMap<String, Map<String, TypeInfo>>();
  @Unique private final Map<Integer, Map<String, String>> zerosmod$indexedAliases = new HashMap<Integer, Map<String, String>>();

  @Inject(method = "formatCodeText", at = @At("HEAD"), remap = false)
  private void zerosmod$clearInferredObjects(CallbackInfo ci) {
    zerosmod$indexedProperties.clear();
    zerosmod$indexedAliases.clear();
  }

  @Inject(
      method = "parseStructure",
      at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/gui/util/script/interpreter/ScriptDocument;parseGlobalFields()V"),
      remap = false)
  private void zerosmod$inferHelperParameterTypes(CallbackInfo ci) {
    if (!isJavaScript()) {
      return;
    }

    List<MethodInfo> methods = getMethods();
    for (int pass = 0; pass < methods.size(); pass++) {
      boolean changed = false;
      for (MethodInfo method : methods) {
        if (method.getParameterCount() != 1 || !hasUniqueName(method, methods)) {
          continue;
        }

        FieldInfo parameter = method.getParameters().get(0);
        if (!parameter.canInferType()) {
          continue;
        }

        TypeInfo type = inferArgumentType(method);
        TypeInfo current = parameter.getEffectiveType();
        if (type != null && (current == null || !type.getFullName().equals(current.getFullName()))) {
          parameter.setInferredType(type);
          changed = true;
        }
      }

      if (!changed) {
        break;
      }
      // Local variables can depend on the newly typed parameter, including array elements.
      parseLocalVariables();
    }

    zerosmod$collectIndexedObjects();
    if (!zerosmod$indexedProperties.isEmpty()) {
      parseLocalVariables();
    }
  }

  @Inject(
      method = "resolveExpressionType(Ljava/lang/String;I)Lnoppes/npcs/client/gui/util/script/interpreter/type/TypeInfo;",
      at = @At("RETURN"), cancellable = true, remap = false)
  private void zerosmod$resolveIndexedObjectProperty(
      String expression, int offset, CallbackInfoReturnable<TypeInfo> cir) {
    TypeInfo original = cir.getReturnValue();
    if (!isJavaScript() || (original != null && !"any".equals(original.getFullName()))) {
      return;
    }

    Matcher access = Pattern.compile(
        "^\\s*([A-Za-z_$][A-Za-z0-9_$]*)(?:\\s*\\[[^]]+\\])?\\s*\\.\\s*([A-Za-z_$][A-Za-z0-9_$]*)\\s*$")
        .matcher(expression);
    if (!access.matches()) {
      return;
    }

    String owner = access.group(1);
    MethodInfo method = findContainingMethod(offset);
    Map<String, String> aliases = zerosmod$indexedAliases.get(method == null ? -1 : method.getNameOffset());
    if (aliases != null && aliases.containsKey(owner)) {
      owner = aliases.get(owner);
    }

    Map<String, TypeInfo> properties = zerosmod$indexedProperties.get(owner);
    if (properties != null && properties.containsKey(access.group(2))) {
      cir.setReturnValue(properties.get(access.group(2)));
    }
  }

  private void zerosmod$collectIndexedObjects() {
    Pattern assignment = Pattern.compile(
        "\\b([A-Za-z_$][A-Za-z0-9_$]*)\\s*\\[[^]]+\\]\\s*=\\s*\\{([^{}]*)\\}");
    Pattern property = Pattern.compile(
        "\\b([A-Za-z_$][A-Za-z0-9_$]*)\\s*:\\s*"
            + "([A-Za-z_$][A-Za-z0-9_$]*(?:\\.[A-Za-z_$][A-Za-z0-9_$]*\\(\\))?)(?=\\s*(?:,|$))");
    Matcher objects = assignment.matcher(text);
    while (objects.find()) {
      if (isExcluded(objects.start())) {
        continue;
      }
      Map<String, TypeInfo> types = zerosmod$indexedProperties.get(objects.group(1));
      if (types == null) {
        types = new HashMap<String, TypeInfo>();
        zerosmod$indexedProperties.put(objects.group(1), types);
      }
      Matcher fields = property.matcher(objects.group(2));
      while (fields.find()) {
        int valueOffset = objects.start(2) + fields.start(2);
        TypeInfo valueType = resolveExpressionType(fields.group(2), valueOffset);
        if (valueType != null && !"any".equals(valueType.getFullName())) {
          types.put(fields.group(1), valueType);
        }
      }
    }

    Pattern alias = Pattern.compile(
        "\\b(?:var|let|const)\\s+([A-Za-z_$][A-Za-z0-9_$]*)\\s*=\\s*([A-Za-z_$][A-Za-z0-9_$]*)\\s*\\[[^]]+\\]");
    Matcher aliases = alias.matcher(text);
    while (aliases.find()) {
      if (isExcluded(aliases.start()) || !zerosmod$indexedProperties.containsKey(aliases.group(2))) {
        continue;
      }
      MethodInfo method = findContainingMethod(aliases.start());
      int scope = method == null ? -1 : method.getNameOffset();
      Map<String, String> names = zerosmod$indexedAliases.get(scope);
      if (names == null) {
        names = new HashMap<String, String>();
        zerosmod$indexedAliases.put(scope, names);
      }
      names.put(aliases.group(1), aliases.group(2));
    }
  }

  private boolean hasUniqueName(MethodInfo method, List<MethodInfo> methods) {
    for (MethodInfo other : methods) {
      if (other != method && other.getName().equals(method.getName())) {
        return false;
      }
    }
    return true;
  }

  private TypeInfo inferArgumentType(MethodInfo method) {
    Pattern call = Pattern.compile(
        "\\b" + Pattern.quote(method.getName()) + "\\s*\\(\\s*([A-Za-z_$][A-Za-z0-9_$]*)\\s*\\)");
    Matcher matcher = call.matcher(text);
    TypeInfo candidate = null;
    while (matcher.find()) {
      if (matcher.start() == method.getNameOffset() || isExcluded(matcher.start())) {
        continue;
      }

      TypeInfo argument = resolveExpressionType(matcher.group(1), matcher.start(1));
      if (argument == null || "any".equals(argument.getFullName())) {
        continue;
      }
      if (candidate != null && !candidate.getFullName().equals(argument.getFullName())) {
        return null;
      }
      candidate = argument;
    }
    return candidate;
  }
}
