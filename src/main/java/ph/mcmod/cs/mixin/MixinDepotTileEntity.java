package ph.mcmod.cs.mixin;

import com.simibubi.create.content.logistics.block.depot.DepotTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.game.InjectDepotTileEntity;

import java.util.List;
@Mixin(DepotTileEntity.class)
public abstract class MixinDepotTileEntity extends SmartTileEntity implements InjectDepotTileEntity, FluidTransferable {
    private SmartFluidTankBehaviour tank;
    private double temperature;

    public MixinDepotTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @NotNull
    @Override
    public SmartFluidTankBehaviour getTank() {
        return tank;
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }


    @Nullable
    @Override
    public Storage<FluidVariant> getFluidStorage(@Nullable Direction face) {
        return InjectDepotTileEntity.getFluidStorage((DepotTileEntity) (Object) this, face);
    }

    @Override
    public void tick() {
        super.tick();
        InjectDepotTileEntity.tick((DepotTileEntity) (Object) this);
    }

    @Inject(method = "addBehaviours", at = @At("RETURN"))
    private void addBehaviours(List<TileEntityBehaviour> behaviours, CallbackInfo ci) {
        tank = InjectDepotTileEntity.newTank((DepotTileEntity) (Object) this);
        behaviours.add(tank);//TODO 这句看上去没用？
//        InjectDepotTileEntity.addBehaviours((DepotTileEntity) (Object) this, behaviours);
    }


}
