package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import ph.mcmod.csd.game.InjectFluidTankTileEntity;
@Mixin(FluidTankTileEntity.class)
public abstract class MixinFluidTankTileEntity extends SmartTileEntity implements InjectFluidTankTileEntity, ItemTransferable {
    private final SmartInventory inventory = InjectFluidTankTileEntity.newInventory((SyncedTileEntity) (Object) this);

    public MixinFluidTankTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @NotNull
    @Override
    public SmartInventory getInventory() {
        return inventory;
    }

    @Nullable
    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction face) {
        return getInventory();
    }
}
