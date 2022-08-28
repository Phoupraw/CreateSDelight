package ph.mcmod.cs.mixin;

import com.simibubi.create.foundation.networking.AllPackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.MyRegistries;
import ph.mcmod.kum.KtUtilKt;
@Mixin(value = AllPackets.class, remap = false)
public class MixinAllPackets {
    @Inject(method = "registerPackets", at = @At("RETURN"))
    private static void register(CallbackInfo ci) {
        KtUtilKt.loadClass(MyRegistries.MyPackets.INSTANCE);
    }
}
