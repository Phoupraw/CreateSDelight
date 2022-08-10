package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileEntity;
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileRenderer;
import com.simibubi.create.content.contraptions.relays.elementary.SimpleKineticTileEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import ph.mcmod.cs.MixinDelegates;
import ph.mcmod.cs.game.RoastingGrill;
import ph.mcmod.cs.game.RoastingStorage;
@Mixin(BracketedKineticTileEntity.class)
public class MixinBracketedKineticTileEntity extends SimpleKineticTileEntity implements RoastingGrill {
    private final RoastingStorage roastingStorage = new RoastingStorage(this);

    public MixinBracketedKineticTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull RoastingStorage getRoastingStorage() {
        return roastingStorage;
    }
    //    @Override
//    public BlockEntityType<? extends KineticTileEntity> getTileEntityType() {
//        return MyRegistries.MyBlockEntityTypes.SHAFT;
//    }
//
//    @Override
//    public Class<KineticTileEntity> getTileEntityClass() {
//        //noinspection unchecked
//        return (Class<KineticTileEntity>)(Object) ShaftBlockEntity.class;
//    }


    @Override
    protected void write(NbtCompound compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        MixinDelegates.writeRoastingNbt((BracketedKineticTileEntity) (Object) this, compound,clientPacket);
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        MixinDelegates.readRoastingNbt((BracketedKineticTileEntity) (Object) this, compound,clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        MixinDelegates.tickRoasting((BracketedKineticTileEntity) (Object) this);
    }
}
