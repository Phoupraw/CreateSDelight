package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import kotlin.jvm.internal.Ref;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ph.mcmod.csd.game.InjectItemDrainTileEntity;
@Mixin(value = ItemDrainTileEntity.class)
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

    @Shadow(remap = false)
    TransportedItemStack heldItem;

    @Shadow(remap = false)
    protected int processingTicks;

    @Shadow(remap = false)
    protected abstract float itemMovementPerTick();

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
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
        InjectItemDrainTileEntity.modifyAngle((ItemDrainTileEntity) (Object) this, heldItem, insertedFrom);
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        setToastingStage(compound.getInt("toastingStage"));
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void write(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        compound.putInt("toastingStage", getToastingStage());
    }

    @Inject(method = "tryInsertingFromSide", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/fluids/actors/ItemDrainTileEntity;setHeldItem(Lcom/simibubi/create/content/contraptions/relays/belt/transport/TransportedItemStack;Lnet/minecraft/util/math/Direction;)V", shift = At.Shift.AFTER))
    private void flapWhenInput(TransportedItemStack transportedStack, Direction side, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        InjectItemDrainTileEntity.flapWhenInput((ItemDrainTileEntity) (Object) this, heldItem, transportedStack, side, simulate);
    }

    @Inject(method = "tick", slice = @Slice(from = @At(value = "FIELD", target = "Lcom/simibubi/create/foundation/advancement/AllAdvancements;CHAINED_DRAIN:Lcom/simibubi/create/foundation/advancement/CreateAdvancement;")), at = {@At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/fluids/actors/ItemDrainTileEntity;notifyUpdate()V", ordinal = 0), @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/fluids/actors/ItemDrainTileEntity;notifyUpdate()V", ordinal = 1)}, locals = LocalCapture.CAPTURE_FAILHARD)
    private void flapWhenOutput(CallbackInfo ci, boolean onClient, Direction side) {
        InjectItemDrainTileEntity.flapWhenOutput((ItemDrainTileEntity) (Object) this, heldItem, side);
    }

    @Inject(method = "tick", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z")), at = {@At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/fluids/actors/ItemDrainTileEntity;notifyUpdate()V", ordinal = 0)}, locals = LocalCapture.CAPTURE_FAILHARD)
    private void flapWhenThrow(CallbackInfo ci, boolean onClient, Direction side) {
        InjectItemDrainTileEntity.flapWhenThrow((ItemDrainTileEntity) (Object) this, heldItem, side);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/tileEntity/TileEntityBehaviour;get(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lcom/simibubi/create/foundation/tileEntity/behaviour/BehaviourType;)Lcom/simibubi/create/foundation/tileEntity/TileEntityBehaviour;"))
    private @Nullable TileEntityBehaviour cancelOutput(BlockView world, BlockPos pos, BehaviourType<TileEntityBehaviour> type) {
        return InjectItemDrainTileEntity.cancelOutput((ItemDrainTileEntity) (Object) this, heldItem, world, pos, type);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;hasBlockSolidSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
    private boolean cancelThrow(BlockState blockState, BlockView world, BlockPos pos, Direction side) {
        return InjectItemDrainTileEntity.cancelThrow((ItemDrainTileEntity) (Object) this, heldItem, blockState, world, pos, side);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/fluids/actors/ItemDrainTileEntity;itemMovementPerTick()F", ordinal = 0))
    private float cancelMovement(ItemDrainTileEntity instance) {
        return InjectItemDrainTileEntity.cancelMovement((ItemDrainTileEntity) (Object) this, heldItem, itemMovementPerTick());
    }

    @Inject(method = "tryInsertingFromSide", at = @At("HEAD"), cancellable = true)
    private void cancelInput(TransportedItemStack transportedStack, Direction side, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        InjectItemDrainTileEntity.cancelInput((ItemDrainTileEntity) (Object) this, heldItem, transportedStack, side, simulate, cir);
    }
}
