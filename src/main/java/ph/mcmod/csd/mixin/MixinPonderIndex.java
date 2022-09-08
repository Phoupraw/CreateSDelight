package ph.mcmod.csd.mixin;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.content.PonderIndex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.csd.game.InjectPonderIndex;
@Mixin(value = PonderIndex.class,remap = false)
public class MixinPonderIndex {
    @Shadow @Final
    static PonderRegistrationHelper HELPER;

    @Inject(method = "register",at = @At("TAIL"))
    private static void register(CallbackInfo ci){
        InjectPonderIndex.register(HELPER);
    }
}
