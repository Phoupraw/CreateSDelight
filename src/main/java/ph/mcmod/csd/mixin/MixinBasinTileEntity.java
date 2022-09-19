package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.inventory.InvManipulationBehaviour;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ph.mcmod.csd.game.InjectBasinTileEntity;

import java.util.Iterator;
@Mixin(value = BasinTileEntity.class)
public abstract class MixinBasinTileEntity extends SmartTileEntity implements InjectBasinTileEntity {
    private double temperature = 25;
    private double animationTicks = 0;
    private final Object recipeKey = new Object();
    private @Nullable Double steepingDuration;
    private @Nullable Double youtiaoDuration;
    private Storage<ItemVariant> targetInv;
    private Transaction nested;

    public MixinBasinTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

    @NotNull
    @Override
    public Object getSteepingKey() {
        return recipeKey;
    }

    @Nullable
    @Override
    public Double getSteepingDuration() {
        return steepingDuration;
    }

    @Override
    public void setSteepingDuration(@Nullable Double steepingDuration) {
        this.steepingDuration = steepingDuration;
    }

    @Nullable
    @Override
    public Double getYoutiaoDuration() {
        return youtiaoDuration;
    }

    @Override
    public void setYoutiaoDuration(@Nullable Double youtiaoDuration) {
        this.youtiaoDuration = youtiaoDuration;
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void tick(CallbackInfo ci) {
        InjectBasinTileEntity.tick((BasinTileEntity) (Object) this);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/tileEntity/SmartTileEntity;tick()V", shift = At.Shift.AFTER))
    private void tickSteeping(CallbackInfo ci) {
        InjectBasinTileEntity.tickSteeping((BasinTileEntity) (Object) this);
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void write(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        compound.putDouble("temperature", getTemperature());
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(NbtCompound compound, boolean clientPacket, CallbackInfo ci) {
        setTemperature(compound.getDouble("temperature"));
    }

    @Inject(method = "tryClearingSpoutputOverflow", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;of(Lnet/minecraft/item/ItemStack;)Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureLocals(CallbackInfo ci, BlockState blockState, Direction direction, BlockEntity te, FilteringBehaviour filter, InvManipulationBehaviour inserter, Storage<ItemVariant> targetInv, Storage<FluidVariant> targetTank, boolean update, Transaction t, Iterator<ItemStack> iterator, ItemStack itemStack, Transaction nested) {
        this.targetInv = targetInv;
        this.nested = nested;
    }

    @Redirect(method = "tryClearingSpoutputOverflow", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/transfer/v1/storage/Storage;insert(Ljava/lang/Object;JLnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext;)J", ordinal = 0), remap = false)
    private long modifyOutput(Storage<ItemVariant> targetInv, Object itemVariant, long amount, TransactionContext nested) {
        return InjectBasinTileEntity.modifyOutput((BasinTileEntity) (Object) this, targetInv, (ItemVariant) itemVariant, amount, nested);
    }

}
