package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.MixinDelegates;
import ph.mcmod.cs.game.FTemperature;
@Mixin(value = BasinTileEntity.class,remap = false)
public abstract class MixinBasinTileEntity extends SmartTileEntity implements FTemperature {
    private double temperature=25;
    private double animationTicks=0;
    public MixinBasinTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickBoil(CallbackInfo ci) {
        MixinDelegates.tickBoil((BasinTileEntity) (Object) this);
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void write(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        compound.putDouble("temperature", getTemperature());
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        setTemperature(compound.getDouble("temperature"));
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public double getAnimationTicks() {
        return animationTicks;
    }

    @Override
    public void setAnimationTicks(double animationTicks) {
        this.animationTicks = animationTicks;
    }
}
