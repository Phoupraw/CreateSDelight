package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileEntity;
import com.simibubi.create.content.contraptions.relays.elementary.SimpleKineticTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import ph.mcmod.csd.game.InjectBracketedKineticTileEntity;
import ph.mcmod.csd.game.RoastingStorage;
@Mixin(BracketedKineticTileEntity.class)
public class MixinBracketedKineticTileEntity extends SimpleKineticTileEntity implements InjectBracketedKineticTileEntity {
    private final RoastingStorage roastingStorage = new RoastingStorage((BracketedKineticTileEntity) (Object) this);

    public MixinBracketedKineticTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull RoastingStorage getRoastingStorage() {
        return roastingStorage;
    }

    @Override
    protected void write(NbtCompound compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        InjectBracketedKineticTileEntity.writeRoastingNbt((BracketedKineticTileEntity) (Object) this, compound, clientPacket);
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        InjectBracketedKineticTileEntity.readRoastingNbt((BracketedKineticTileEntity) (Object) this, compound, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        InjectBracketedKineticTileEntity.tick((BracketedKineticTileEntity) (Object) this);
//        MixinDelegates.tickRoasting((BracketedKineticTileEntity) (Object) this);
    }
}
