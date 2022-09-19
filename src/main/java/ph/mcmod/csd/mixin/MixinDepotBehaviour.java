package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.csd.game.InjectDepotBehaviour;
@Mixin(value = DepotBehaviour.class)
public abstract class MixinDepotBehaviour extends TileEntityBehaviour implements InjectDepotBehaviour {
    private long startTime = -1;
    private int flippingCountdown = -1;
    @Shadow(remap = false)
    TransportedItemStack heldItem;

    public MixinDepotBehaviour(SmartTileEntity te) {
        super(te);
    }

    @Nullable
    @Override
    public TransportedItemStack getHeldItem() {
        return heldItem;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public int getFlippingCountdown() {
        return flippingCountdown;
    }

    @Override
    public void setFlippingCountdown(int flippingCountdown) {
        this.flippingCountdown = flippingCountdown;
//        tileEntity.notifyUpdate();
    }

@Inject(method = "setHeldItem", at = @At("RETURN"), remap = false)
private void center(TransportedItemStack heldItem, CallbackInfo ci) {
    InjectDepotBehaviour.center((DepotBehaviour) (Object) this, heldItem);
}

    @Inject(method = "write", at = @At("HEAD"))
    private void write(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
//        compound.putLong("startTime",getStartTime());
        compound.putInt("flippingCountdown", getFlippingCountdown());
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
//        setStartTime(compound.getLong("startTime"));
        setFlippingCountdown(compound.getInt("flippingCountdown"));
    }
}
