//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.darkoro.zerosmod.mixinloaders;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

@LateMixin
public class ZeroSModLateMixins implements ILateMixinLoader {
    public static final MixinEnvironment.Side side = MixinEnvironment.getCurrentEnvironment().getSide();

    public ZeroSModLateMixins() {
    }

    public String getMixinConfig() {
        return "mixins.zerosmod.late.json";
    }

    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList();
        if (side == Side.CLIENT) {
            mixins.add("DBCClientTickHandlerMixins");
            mixins.add("JRMCoreHCMixins");
        } else if (side == Side.SERVER) {
            mixins.add("JRMCoreHMixins");
            mixins.add("DBCUtilsMixins");
        }
        return mixins;
    }
}
