package ph.mcmod.csd.mixin;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.csd.MyRegistries;
@Mixin(value = FarmersDelightMod.class)
public class MixinFarmersDelightMod {
    @Inject(method = "onInitialize", at = @At("RETURN"),remap = false)
    private void afterFDInit(CallbackInfo ci) {
        MyRegistries.afterFDInit();
    }
}
