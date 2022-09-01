package ph.mcmod.cs.mixin;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour;
import com.simibubi.create.content.contraptions.fluids.actors.SpoutTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.game.InjectSpoutTileEntity;
@Mixin(value = SpoutTileEntity.class,remap = false)
public abstract class MixinSpoutTileEntity extends SmartTileEntity implements InjectSpoutTileEntity {
    private boolean shouldParticle;

    public MixinSpoutTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean getShouldParticle() {
        return shouldParticle;
    }

    @Override
    public void setShouldParticle(boolean shouldParticle) {
        this.shouldParticle = shouldParticle;
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void tickOil(CallbackInfo ci) {
        InjectSpoutTileEntity.tickOil((SpoutTileEntity) (Object) this);
    }

    @Inject(method = "spawnProcessingParticles", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;getCenterOf(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/Vec3d;"), cancellable = true)
    private void shouldProcessingParticles(FluidStack fluid, CallbackInfo ci) {
        if (!getShouldParticle()) {
            ci.cancel();
        }
    }

    @Inject(method = "spawnSplash", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;getCenterOf(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/Vec3d;"), cancellable = true)
    private void shouldSplashParticles(FluidStack fluid, CallbackInfo ci) {
        if (!getShouldParticle()) {
            ci.cancel();
        }
    }
    @Inject(method = "read", at = @At("HEAD"))
    private void read(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        InjectSpoutTileEntity.read((SpoutTileEntity)(Object)this,compound,clientPacket);
    }
    @Inject(method = "write", at = @At("HEAD"))
    private void write(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        InjectSpoutTileEntity.write((SpoutTileEntity)(Object)this,compound,clientPacket);
    }
    @Inject(method = "lambda$tick$0",at = @At(value = "FIELD",opcode = Opcodes.PUTFIELD,target = "Lcom/simibubi/create/content/contraptions/fluids/actors/SpoutTileEntity;customProcess:Lcom/simibubi/create/api/behaviour/BlockSpoutingBehaviour;",shift = At.Shift.AFTER))
    private void afterSetCustomProcess(FluidStack currentFluidInTank, BlockSpoutingBehaviour behaviour, CallbackInfo ci) {
        InjectSpoutTileEntity.onSetCustomProcess((SpoutTileEntity)(Object)this);
    }
}
