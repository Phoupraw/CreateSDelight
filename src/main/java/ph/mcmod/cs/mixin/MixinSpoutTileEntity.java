package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.fluids.actors.SpoutTileEntity;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.game.InjectSpoutTileEntity;
@Mixin(SpoutTileEntity.class)
public class MixinSpoutTileEntity {
    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void tickOil(CallbackInfo ci) {
       InjectSpoutTileEntity.tickOil((SpoutTileEntity) (Object) this);
    }
    @Inject(method = "spawnProcessingParticles", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;getCenterOf(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/Vec3d;"), cancellable = true)
    private void shouldProcessingParticles(FluidStack fluid, CallbackInfo ci) {
        if (InjectSpoutTileEntity.isNotOil((SpoutTileEntity) (Object) this)) {
            ci.cancel();
        }
    }
    @Inject(method = "spawnSplash", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;getCenterOf(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/Vec3d;"), cancellable = true)
    private void shouldSplashParticles(FluidStack fluid, CallbackInfo ci) {
        if (InjectSpoutTileEntity.isNotOil((SpoutTileEntity) (Object) this)) {
            ci.cancel();
        }
    }

}
