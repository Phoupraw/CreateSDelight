package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import kotlin.jvm.internal.Ref;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ph.mcmod.cs.MixinDelegates;
import ph.mcmod.cs.game.InjectItemDrainTileEntity;
@Mixin(value = ItemDrainTileEntity.class, remap = false)
public abstract class MixinItemDrainTileEntity implements InjectItemDrainTileEntity {
    private int toastingStage;

    @Override
    public int getToastingStage() {
        return toastingStage;
    }

    @Override
    public void setToastingStage(int toastingStage) {
        this.toastingStage = toastingStage;
    }

    @Shadow
    TransportedItemStack heldItem;

    @Shadow
    protected int processingTicks;

    @Inject(method = "tick", at = @At("HEAD"))
    private void particle(CallbackInfo ci) {
//        MixinDelegates.particle((ItemDrainTileEntity) (Object) this, heldItem);
        InjectItemDrainTileEntity.particle((ItemDrainTileEntity) (Object) this, heldItem);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/processing/EmptyingByBasin;canItemBeEmptied(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean enableProcessing(World world, ItemStack stack) {
        return InjectItemDrainTileEntity.enableProcessing((ItemDrainTileEntity) (Object) this, world, stack, heldItem);
    }

    @Inject(method = "continueProcessing", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/processing/EmptyingByBasin;canItemBeEmptied(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"), cancellable = true)
    private void process(CallbackInfoReturnable<Boolean> cir) {
        /* Integer processingTicks = */
//        MixinDelegates.steam((ItemDrainTileEntity) (Object) this, heldItem, cir);
        var processingTicks = new Ref.IntRef();
        processingTicks.element = this.processingTicks;
        InjectItemDrainTileEntity.process((ItemDrainTileEntity) (Object) this, heldItem, cir, processingTicks);
        this.processingTicks = processingTicks.element;
    }
    @Inject(method = "setHeldItem", at = @At("RETURN"))
    private void modifyAngle(TransportedItemStack heldItem, Direction insertedFrom, CallbackInfo ci) {
        InjectItemDrainTileEntity.modifyAngle((ItemDrainTileEntity) (Object) this, heldItem,insertedFrom);
    }
    @Inject(method = "read", at = @At("HEAD"))
    private void read(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        setToastingStage(compound.getInt("toastingStage"));
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void write(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        compound.putInt("toastingStage", getToastingStage());
    }
}
